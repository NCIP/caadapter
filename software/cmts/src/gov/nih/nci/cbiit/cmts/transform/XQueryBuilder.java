/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.transform;

import gov.nih.nci.cbiit.cmts.core.AttributeMeta;
import gov.nih.nci.cbiit.cmts.core.Component;
import gov.nih.nci.cbiit.cmts.core.ComponentType;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.core.FunctionData;
import gov.nih.nci.cbiit.cmts.core.FunctionDef;
import gov.nih.nci.cbiit.cmts.core.FunctionType;
import gov.nih.nci.cbiit.cmts.core.LinkType;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.function.FunctionException;
import gov.nih.nci.cbiit.cmts.function.FunctionInvoker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * This class generates XQuery from Mapping
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMPS v1.0
 * @version    $Revision: 1.11 $
 * @date       $Date: 2009-11-18 16:44:52 $
 *
 */
public class XQueryBuilder {
	private Mapping mapping;
	private StringBuffer sbQuery;
	private Map<String, LinkType> links;
	private Map<String, LinkType> metaToFunctionLinks;
	private Map<String, Map<String, LinkType>> allToFunctionLinks;
	
	private Map<String, FunctionType>functions;
	private Stack<String> xpathStack;
	private Stack<String> varStack;

	final static String sep = System.getProperty("line.separator");

	/**
	 * constructor that takes Mapping object
	 * @param m - Mapping object
	 */
	public XQueryBuilder(Mapping m){
		this.mapping = m;
		loadLinks();
		loadFunctions();
	}

	private void loadFunctions() {
		functions = new HashMap<String, FunctionType>();
		if(mapping.getLinks()==null) {
			return;
		}
		for (Component oneComp : mapping.getComponents().getComponent()) {
			FunctionType function = oneComp.getFunction();
			if (function != null) {
				functions.put(oneComp.getId(), function);
			}
		}
	}
	private void loadLinks() {
		this.links = new HashMap<String, LinkType>();
		this.metaToFunctionLinks = new HashMap<String, LinkType>();
		allToFunctionLinks = new HashMap<String, Map<String,LinkType>>();
		if(mapping.getLinks()==null) {
			return;
		}
		List<LinkType> links = mapping.getLinks().getLink();
		for (LinkType l:links) {
			if (l.getTarget().getComponentid().length()==1)
				this.links.put(l.getTarget().getId(), l);
			else 
			{
				if (l.getSource().getComponentid().length()==1)
					//metaToFuncionLink
					metaToFunctionLinks.put(l.getSource().getId(), l);
				Map<String, LinkType> linkToOneFunction=(Map<String,LinkType>)allToFunctionLinks.get(l.getTarget().getComponentid());
				if (linkToOneFunction==null)
					linkToOneFunction=new HashMap<String, LinkType>();
 
				linkToOneFunction.put(l.getTarget().getId(), l);
				allToFunctionLinks.put(l.getTarget().getComponentid(), linkToOneFunction);
			}
		}
	}

	/**
	 * generate XQuery
	 * @return XQuery String
	 */
	public String getXQuery() {
		List<Component> l = mapping.getComponents().getComponent();
		Component tgt = null;
		for (Component c:l) {
			if (c.getType().equals(ComponentType.TARGET)) {
				tgt = c;
				break;
			}
		}
		xpathStack = new Stack<String>();
		varStack = new Stack<String>();
		sbQuery = new StringBuffer();
		sbQuery.append("declare variable $docName as xs:string external;" + sep +"document{");
		varStack.push("doc($docName)");
		processTargetElement(tgt.getRootElement(),null);
		sbQuery.append("}");
		return sbQuery.toString();
	}
	
	/**
	 * Process a target element:
	 * Case I: The element is mapped to a source node
	 * Case II: The element is mapped to function output port
	 * Case II.1: The function does not have input port
	 * Case II.2: The function has one or more input ports
	 * Case III: The element is not mapped, but its attribute is mapped
	 * Case IV: Neither the element nor its attribute is mapped. But its descendant
	 * is mapped
	 * Case V: Others, empty element with/without default inline text
	 * @param tgt
	 * @param parentMappedXPath
	 */
	private void processTargetElement(ElementMeta tgt,String parentMappedXPath) {
		xpathStack.push(tgt.getName());
		String elementXpath=QueryBuilderUtil.buildXPath(xpathStack);
		String elementStartTag=" element "+ tgt.getName() + "{";
		String inlineText="\"\"";
		LinkType link = links.get(elementXpath);

		if(link!=null) 		
		{ 	//Case I:
			//create loop
			String tgtMappingSrc=null;
			tgtMappingSrc = link.getSource().getId();
			String var = "$item_temp"+String.valueOf(varStack.size());
			sbQuery.append("for "+var+" in " +varStack.peek() );
			String localpath = tgtMappingSrc;
			if (parentMappedXPath!=null&&tgtMappingSrc!=null)
				localpath =QueryBuilderUtil.retrieveRelativePath(parentMappedXPath, tgtMappingSrc);// getRelativePath(srcIdStack.peek(), srcId);
			sbQuery.append(localpath);
			
			varStack.push(var);
			
			//build the target element
			sbQuery.append(" return ").append(elementStartTag);//  element "+tgt.getName() +"{");
			encodeAttribute(tgt, tgtMappingSrc);
	
			for(ElementMeta e:tgt.getChildElement()) 
			{
				processTargetElement(e, tgtMappingSrc);
				sbQuery.append(",");
			}
			
			//set online text
			inlineText=var+"/text()";
			varStack.pop();
		}
		else
		{
			LinkType fLink = metaToFunctionLinks.get(elementXpath);
			if (fLink!=null)
			{
				//Case II:
				String functionId=fLink.getTarget().getComponentid();
				FunctionType inputFunction=functions.get(functionId);
				if (inputFunction.getData().size()==1)
				{
					//Case II.1
					sbQuery.append(elementStartTag);//" element "+tgt.getName() + " {");
					//Case III:
					encodeAttribute(tgt, parentMappedXPath);
					//Case IV:
					for(ElementMeta e:tgt.getChildElement()) 
					{
						processTargetElement(e, parentMappedXPath);
						sbQuery.append(",");
					}
				
					//set online text
					inlineText="data(";
					inlineText=inlineText+createQueryForFunctionNonInput(fLink);//, parentMappedXPath);
					inlineText=inlineText+")";
				}
				else
				{
					//Case II.2
					//Create a loop to invoke data manipulation function
//					Map<String, LinkType> linkToFunction=allToFunctionLinks.get(link.getTarget().getComponentid());
					Map<String,String> parameterMap=new HashMap<String,String>();
					createQueryForFunctionWithInput(fLink,  parentMappedXPath);
				}
			}
			else
			{
				//create empty element 
				sbQuery.append(elementStartTag);//" element "+tgt.getName() + " {");
				//Case III:
				encodeAttribute(tgt, parentMappedXPath);
				
				if (hasMappedDescenant(tgt))
				{
					//Case IV:
					for(ElementMeta e:tgt.getChildElement()) 
					{
						processTargetElement(e, parentMappedXPath);
						sbQuery.append(",");
					}
				}
				
				//Case V
				//set online text
				if (tgt.getDefaultValue()!=null)
					inlineText=tgt.getDefaultValue();
			}
		}
		// add inline text, close the element tag
		sbQuery.append(inlineText).append("}");
		xpathStack.pop();
	}
	
	/**
	 * Set values for the attributes of an element
	 * @param elementMeta - Target Element meta
	 * @param mappedSourceNodeId - The previously mapped source node path, which is associated with the variable on varStack
	 */
	private void encodeAttribute(ElementMeta elementMeta, String mappedSourceNodeId)
	{
		for(AttributeMeta a:elementMeta.getAttrData())
		{
			String elementXpath=QueryBuilderUtil.buildXPath(xpathStack);
			String attrValue="";
			if(links.get(elementXpath+"/@"+a.getName())!=null) 
			{
				LinkType l = links.get(elementXpath+"/@"+a.getName());
				String attrMappingSrc = l.getSource().getId();
				String var = varStack.peek();
				String localpath = null;
				if(mappedSourceNodeId!=null)
					localpath =QueryBuilderUtil.retrieveRelativePath(mappedSourceNodeId, attrMappingSrc);			
				attrValue="data("+var;
				attrValue=attrValue+localpath;
				attrValue=attrValue+")";
			}
			else if(metaToFunctionLinks.get(elementXpath+"/@"+a.getName())!=null) 
			{	
				attrValue="data(";
				attrValue=attrValue+createQueryForFunctionNonInput(metaToFunctionLinks.get(elementXpath+"/@"+a.getName()));//, mappedSourceNodeId);
				attrValue=attrValue+")";
			}
			else if(a.getFixedValue()!=null) { //use fixed value first
				attrValue="\""+a.getFixedValue()+"\"";
			} 
			else if(a.getDefaultValue()!=null) {//use default value
				attrValue="\""+a.getDefaultValue()+"\"";
			}
			if (attrValue!=null&&!attrValue.isEmpty())
				sbQuery.append("attribute "+a.getName() +"{"+attrValue+"},");
		}
	}
	private String createQueryForFunctionNonInput(LinkType link)
	{
		FunctionType functionType=functions.get(link.getTarget().getComponentid());
		if (functionType.getData().size()!=1)
			return "invalid function:"+functionType.getGroup()+":"+functionType.getName()+":"+functionType.getMethod();
		Map<String,String> parameterMap=new HashMap<String,String>();
		Object argList[]=new Object[]{functionType, parameterMap};
		
		try {
			String xqueryString=(String)FunctionInvoker.invokeFunctionMethod(functionType.getClazz(), functionType.getMethod(), argList);
			return xqueryString ;
		} catch (FunctionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String createQueryForFunctionWithInput(LinkType link, String elementMapingSourceId)
	{
		FunctionType functionType=functions.get(link.getTarget().getComponentid());
		//If there is only port in the functionType,
		if (functionType.getData().size()==1)
			return "invalid function:"+functionType.getGroup()+":"+functionType.getName()+":"+functionType.getMethod();

		String var = varStack.peek();
		Map<String, LinkType> linkToFunction=allToFunctionLinks.get(link.getTarget().getComponentid());
		Map<String,String> parameterMap=new HashMap<String,String>();
		//process all port related this functionType
		for(FunctionData fData:functionType.getData())
		{
			//only consider input ports
			if (!fData.isInput())
				continue;
 
			if ((linkToFunction!=null)&(linkToFunction.get(fData.getName())!=null))
			{
				LinkType inputLink=linkToFunction.get(fData.getName());
				if (inputLink.getSource().getComponentid().length()==1)
				{
					String linkSrId=linkToFunction.get(fData.getName()).getSource().getId();
					System.out
							.println("XQueryBuilder.createQueryForFunctionWithInput()..function input link: port:"+fData.getName()+"="+linkSrId);
					String parameterPath="$"+var;
					String localpath = null;
					if(elementMapingSourceId!=null)
						localpath =QueryBuilderUtil.retrieveRelativePath(elementMapingSourceId, linkSrId);
					if (localpath!=null)
						parameterPath=parameterPath+localpath;
					parameterMap.put(fData.getName(), parameterPath );
				}
				else
				{ 
					//a function is mapped to this input port, 
					FunctionType inputFunction=functions.get(inputLink.getSource().getComponentid());
					String inputSrcValue="";
					Object inputFunctionArgList[]=new Object[]{inputFunction, new HashMap<String,String>()};
					try {
						inputSrcValue=(String)FunctionInvoker.invokeFunctionMethod(inputFunction.getClazz(), inputFunction.getMethod(), inputFunctionArgList);
 
					} catch (FunctionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					parameterMap.put(fData.getName(), inputSrcValue);
				}
			}
 
		}
		Object argList[]=new Object[]{functionType, parameterMap};
		
		try {
			String xqueryString=(String)FunctionInvoker.invokeFunctionMethod(functionType.getClazz(), functionType.getMethod(), argList);
			return xqueryString ;
		} catch (FunctionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Check if any descendant is mapped
	 * @param tgtElement
	 * @return
	 */
		private boolean hasMappedDescenant(ElementMeta tgtElement){
			boolean rtnValue=false;
			for (ElementMeta childMeta:tgtElement.getChildElement())
			{
				xpathStack.push(childMeta.getName());
				String childXPath=QueryBuilderUtil.buildXPath(xpathStack);		
				if (links.get(childXPath)!=null)
					rtnValue=true;
				else if (metaToFunctionLinks.get(childXPath)!=null)
					rtnValue=true;
				else if ( hasAnyMappedAttribute(childMeta)!=null)
					rtnValue=true;
				else if (hasMappedDescenant(childMeta))
					rtnValue=true;

				xpathStack.pop();
				if (rtnValue)
					break;
			}
			return rtnValue;
		}
		/**
		 * Check if any attribute is mapped
		 * @param tgtElement
		 * @return
		 */
		private String hasAnyMappedAttribute(ElementMeta tgtElement)
		{
			String elementXpath=QueryBuilderUtil.buildXPath(xpathStack);
			String rtnValue=null;
			for(AttributeMeta a:tgtElement.getAttrData()) 
			{
				if(links.get(elementXpath+"/@"+a.getName())!=null)
				{
					LinkType l = links.get(elementXpath+"/@"+a.getName());
					String attrMappingSrc = l.getSource().getId();
					return attrMappingSrc;
				}
				else if(metaToFunctionLinks.get(elementXpath+"/@"+a.getName())!=null)
				{
					LinkType l = metaToFunctionLinks.get(elementXpath+"/@"+a.getName());
					String attrMappingSrc = l.getSource().getId();
					return attrMappingSrc;
				}
			}
			return rtnValue;
		}
		
//	/**
//	 * Process a target element:
//	 * Case I: The element is mapped
//	 * Case II: The element is not mapped, but its attribute is mapped
//	 * Case III: Neither the element nor its attribute is mapped. But its descendant
//	 * is mapped
//	 * @param tgt
//	 * @param parentMappedXPath
//	 */
//	private void processTargetElement_saved(ElementMeta tgt,String parentMappedXPath) {
//		xpathStack.push(tgt.getName());
//		String elementXpath=QueryBuilderUtil.buildXPath(xpathStack);
//		String tgtMappingSrc=null;
//		LinkType link = links.get(elementXpath);
//
//		if(link!=null) 
//		{
//			tgtMappingSrc = link.getSource().getId();
//		}
//		else if (metaToFunctionLinks.get(elementXpath)!=null)
//		{
////			sbQuery.append("<"+tgt.getName()+">");
////			pullInlineText(tgt, parentMappedXPath);
////			sbQuery.append("</"+tgt.getName()+">");
////			xpathStack.pop();
////			return;
//		}
//		else if (hasAnyMappedAttribute(tgt)!=null)
//		{
//			tgtMappingSrc=hasAnyMappedAttribute(tgt);
//		}
//		else if (hasMappedDescenant(tgt))
//		{
//			sbQuery.append("<"+tgt.getName()+">");
//			//child elements
//			for(ElementMeta e:tgt.getChildElement()) 
//				processTargetElement(e, tgtMappingSrc);
//			sbQuery.append("</"+tgt.getName()+">");
//			
//			xpathStack.pop();
//			return;
//		}
//		else
//		{
//			//neither the element and its attribute 
//			//nor any of its descendant is mapped,
//			//ignore this element
//			sbQuery.append("<"+tgt.getName()+"/>");
//			xpathStack.pop();
//			return;
//			
//		}
//		
//		//create loop to pull source data
//		String var = "item_temp"+String.valueOf(varStack.size());
//		if (xpathStack.size()>1) 
//			sbQuery.append("{");
//
//		sbQuery.append("for $"+var+" in ");
//		String localpath = null;
//		if (parentMappedXPath!=null)
//			localpath =QueryBuilderUtil.retrieveRelativePath(parentMappedXPath, tgtMappingSrc);// getRelativePath(srcIdStack.peek(), srcId);
//
//		if(localpath == null) {
//			sbQuery.append("doc($docName)");
//			sbQuery.append(tgtMappingSrc);
//		}else {
//			sbQuery.append("$"+varStack.peek()).append(localpath);
//		}
//		sbQuery.append(" return ");
//		varMap.put(tgtMappingSrc, var);
//		varStack.push(var);
//		
//		//start tag and attributes
//		sbQuery.append("<"+tgt.getName());
//		processAttributes(tgt, tgtMappingSrc);
//		sbQuery.append(">");
//		
//		pullInlineText(tgt, null);//, tgtMappingSrc);
//		
//		//child elements
//		for(ElementMeta e:tgt.getChildElement()) 
//			processTargetElement(e, tgtMappingSrc);
//		
//		//end tag
//		sbQuery.append("</"+tgt.getName()+">");	
//		//close loop
//		if(xpathStack.size()>1) 
//			sbQuery.append("}");
//		sbQuery.append(sep);
//		
//		//clear temporary variable stack
//		varStack.pop();
//		xpathStack.pop();
//	}
	
//	private void pullInlineTextFromFunction(ElementMeta tgtMeta, String parentMappedPath)
//	{
//		String elementXpath=QueryBuilderUtil.buildXPath(xpathStack);
//		LinkType link = metaToFunctionLinks.get(elementXpath);
//		if (link==null)
//			return;
//		sbQuery.append("{data(");
//		sbQuery.append(createQueryForFunctionLink(link, parentMappedPath));
//		sbQuery.append(")}");
//	}
	

//	/**
//	 * Pull online text
//	 * Case I: The source is an attribute
//	 * Case II: The source is an element, but the target is a leave element
//	 * Case III: Both the source and target are an element, but the target does have any child element mapped
//	 * @param tgtMeta
//	 */
//	private void pullInlineText(ElementMeta tgtMeta, String parentMappingPath)
//	{
//		String elementXpath=QueryBuilderUtil.buildXPath(xpathStack);
//		
//		LinkType link = links.get(elementXpath);
//		if (link==null)
//		{
//			pullInlineTextFromFunction(tgtMeta, parentMappingPath);			
//			return ;
//		}
//
// 		String srcTextId = link.getSource().getId();
// 		boolean inlineTextRequred=false;
// 		if (srcTextId.indexOf("@")>-1)
// 			inlineTextRequred=true;
// 		else if (tgtMeta.getChildElement().isEmpty())
// 			inlineTextRequred=true;
// 		else if (!hasMappedDescenant(tgtMeta))
// 			inlineTextRequred=true;
// 
////		if(links.get(elementXpath+"#inlinetext")!=null) 
////		{ //map inline text for mixed node
////			srcTextId = links.get(elementXpath+"#inlinetext").getSource().getId();
////		}
// 
// 		if (!inlineTextRequred)
// 			return;
// 		
//		String srcDynamicPath=null;
//		if (srcTextId.indexOf("@")>-1)
//		{
//			String mappingSrcElementId=srcTextId.substring(0, srcTextId.lastIndexOf("/"));
//			String mappingSrcElementVar=varMap.get(mappingSrcElementId);
//			String srcAttrName=srcTextId.substring(srcTextId.indexOf("@"));
//			if (mappingSrcElementVar!=null)
//				srcDynamicPath=mappingSrcElementVar+"/"+srcAttrName;
//		}
//		else
//		{
//				srcDynamicPath=varStack.peek();
//		}
//		
//		if (srcDynamicPath!=null)
//			sbQuery.append("{data($").append(srcDynamicPath).append(")}");
//		else //use the absolute path of the attribute/element in case its parent element are not mapped
//			sbQuery.append("{data(doc($docName)").append(srcTextId+")}");				
//	}

	
//	/**
//	 * Set attribute value
//	 * @param tgt
//	 * @param elementMapingSourceId
//	 */
//	private void processAttributes(ElementMeta tgt, String elementMapingSourceId) {
//		String elementXpath=QueryBuilderUtil.buildXPath(xpathStack);
//		for(AttributeMeta a:tgt.getAttrData()) {
//			if(links.get(elementXpath+"/@"+a.getName())!=null) 
//			{
//				LinkType l = links.get(elementXpath+"/@"+a.getName());
//				String attrMappingSrc = l.getSource().getId();
//				String var = varStack.peek();
//				String localpath = null;
//				if(elementMapingSourceId!=null)
//					localpath =QueryBuilderUtil.retrieveRelativePath(elementMapingSourceId, attrMappingSrc);
//				//xquery data function return the value of an element or attribute
//				sbQuery.append(" ").append(a.getName()).append("=\"{data(");
//				sbQuery.append("$").append(var).append(localpath);
//				sbQuery.append(")}\"");
//			}
//			else if(metaToFunctionLinks.get(elementXpath+"/@"+a.getName())!=null) {
//				sbQuery.append(" ").append(a.getName()).append("=\"{data{");
//				sbQuery.append(createQueryForFunctionLink(metaToFunctionLinks.get(elementXpath+"/@"+a.getName()), elementMapingSourceId));
//				sbQuery.append(")}\"");
//			}
//			else if(a.getFixedValue()!=null) { //use fixed value first
//				sbQuery.append("\""+a.getFixedValue()+"\"");
//			} 
//			else if(a.getDefaultValue()!=null) {//use default value
//				sbQuery.append(" ").append(a.getName()).append("=");
//				sbQuery.append("\""+a.getDefaultValue()+"\"");
//			}
//		}
//	}
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.10  2009/11/13 20:49:51  wangeug
 * HISTORY: set element text from an attribute of source element
 * HISTORY:
 * HISTORY: Revision 1.9  2008/12/10 15:43:02  linc
 * HISTORY: Fixed component id generator and delete link.
 * HISTORY:
 * HISTORY: Revision 1.8  2008/11/04 21:19:34  linc
 * HISTORY: core mapping and transform demo.
 * HISTORY:
 * HISTORY: Revision 1.7  2008/10/22 19:01:17  linc
 * HISTORY: Add comment of public methods.
 * HISTORY:
 * HISTORY: Revision 1.6  2008/10/21 20:49:08  linc
 * HISTORY: Tested with HL7 v3 schema
 * HISTORY:
 * HISTORY: Revision 1.5  2008/10/21 15:59:57  linc
 * HISTORY: updated.
 * HISTORY:
 * HISTORY: Revision 1.4  2008/10/21 15:56:55  linc
 * HISTORY: updated
 * HISTORY:
 * HISTORY: Revision 1.3  2008/10/20 20:46:15  linc
 * HISTORY: updated.
 * HISTORY:
 * HISTORY: Revision 1.2  2008/10/01 18:59:13  linc
 * HISTORY: updated.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/09/30 17:30:41  linc
 * HISTORY: updated.
 * HISTORY:
 */

