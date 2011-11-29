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
import gov.nih.nci.cbiit.cmts.core.FunctionType;
import gov.nih.nci.cbiit.cmts.core.LinkType;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.function.FunctionException;
import gov.nih.nci.cbiit.cmts.function.FunctionInvoker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * This class generates XQuery from Mapping
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.11 $
 * @date       $Date: 2009-11-18 16:44:52 $
 *
 */
public class XQueryBuilder {
	protected Mapping mapping;
	protected StringBuffer sbQuery;
	protected Map<String, LinkType> links;
	protected Map<String, LinkType> metaToFunctionLinks;
	protected Map<String, Map<String, LinkType>> allToFunctionLinks;
	
	protected Map<String, FunctionType>functions;
	protected Stack<String> xpathStack;
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
    public String getXQuery()
    {
        return getXQuery(true);
    }
    public String getXQuery(boolean indent)
    {
		List<Component> l = mapping.getComponents().getComponent();
		Component tgt = null;
		Component src=null;
		for (Component c:l) {
			if (c.getType().equals(ComponentType.TARGET)) 
				tgt = c;
			else if (c.getType().equals(ComponentType.SOURCE))
				src=c;
			
		}
		xpathStack = new Stack<String>();
		varStack = new Stack<String>();
		sbQuery = new StringBuffer();
		ElementMeta srcRootMeta=src.getRootElement();
		if (srcRootMeta!=null&&srcRootMeta.getNameSpace()!=null)
			sbQuery.append("declare default element namespace \""+srcRootMeta.getNameSpace() +"\";" + sep);
		sbQuery.append("declare variable $docName as xs:string external;" + sep +"document{");
		varStack.push("doc($docName)");
		System.out.println("XQueryBuilder.getXQuery()...:"+tgt.getRootElement().getNameSpace());
		processTargetElement(tgt.getRootElement(),null);
		sbQuery.append("}");

        String xqry = sbQuery.toString();
        //System.out.println("XQueryBuilder.getXQuery(). A XQuery..:"+xqry);
        if (!indent) return xqry;
        String xqry2 = setupXQueryStructuredIndenation(xqry);
        //System.out.println("XQueryBuilder.getXQuery(). B XQuery..:"+xqry2);
        return xqry2;
	}
    private String setupXQueryStructuredIndenation(String text)
        {
            if (text == null) return "";
            String tx = text.trim();
            if (!tx.toLowerCase().startsWith("declare ")) return text;

            try
            {
                int idx1 = tx.toLowerCase().indexOf("document");
                String buf = tx.substring(0, idx1).trim() + "\r\n";
                tx = tx.substring(idx1).trim();
                String cx = setupXQueryStructuredIndenation(tx, 0);
                return buf + cx;
            }
            catch(Exception ee)
            {
                System.out.println("Error:" + ee.getMessage());
                return text;
            }
        }
        private String setupXQueryStructuredIndenation(String text, int level)
        {
            String space = "";
            for (int i=0;i<level;i++) space = space + "   ";
            level++;

            String buf = "";

            boolean blockSkip = false;
            while(true)
            {
                text = text.trim();
                //System.out.println("Level("+level+"): " + text);

                if ((text.toLowerCase().startsWith("document")) ||
                    (text.toLowerCase().startsWith("element ")))
                {
                    int idx = text.indexOf("{");
                    if (text.toLowerCase().startsWith("element <choice>")) blockSkip = true;
                    else
                    {
                        buf = buf + space + text.substring(0, idx).trim() + "\r\n" + space + "{" + "\r\n";
                    }
                    text = text.substring(idx).trim();
                    idx = getBlockIndex(text);
                    String sub = setupXQueryStructuredIndenation(text.substring(1, idx), level);
                    text = text.substring(idx + 1).trim();
                    String tail = "}";
                    if (text.startsWith(","))
                    {
                        text = text.substring(1).trim();
                        tail = tail + ",";
                    }
                    if (blockSkip)
                    {
                        blockSkip = false;
                        tail = "";
                        if (text.startsWith("\"\"")) text = text.substring(2).trim();
                    }
                    buf = buf + sub + space + tail + "\r\n";
                }
                else if (text.toLowerCase().startsWith("attribute "))
                {
                    int idx = text.indexOf("{");
                    buf = buf + space + text.substring(0, idx).trim();
                    text = text.substring(idx).trim();
                    idx = getBlockIndex(text);
                    String sub = text.substring(0, idx+1);
                    text = text.substring(idx + 1).trim();
                    String tail = "";
                    if (text.startsWith(","))
                    {
                        text = text.substring(1).trim();
                        tail = ",";
                    }
                    buf = buf + sub + tail + "\r\n";
                }
                else if (text.toLowerCase().startsWith("for "))
                {
                    int idx = text.toLowerCase().indexOf("return");
                    buf = buf + space + text.substring(0, idx + 6) + "\r\n";
                    text = text.substring(idx + 6).trim();
                }
                else if (text.indexOf("{") < 0)
                {
                    buf = buf + space + text + "\r\n";
                    break;
                }
            }
            return buf;
        }

        private int getBlockIndex(String text)
        {
            int open = 0;

            for(int i=0;i<text.length();i++)
            {
                String achar = text.substring(i, i + 1);
                if (achar.equals("{")) open++;
                if (achar.equals("}"))
                {
                    open--;
                    if (open == 0) return i;
                }
            }
            return -1;
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
	 * Case V: create an empty element if it is mandatory
	 * Case VI: Others, empty element with/without default inline text
	 * @param tgt
	 * @param parentMappedXPath
	 */
	private boolean processTargetElement(ElementMeta tgt,String parentMappedXPath) {
		xpathStack.push(tgt.getName());
		boolean elementCreated=false;
		String inlineText="\"\"";
		String elementXpath=QueryBuilderUtil.buildXPath(xpathStack);
		LinkType link = links.get(elementXpath);
        boolean findUnder = false;
        if(link==null)
        {
            Set<String> set = links.keySet();
            Object[] objs = set.toArray();

            for(Object obj:objs)
            {
                String str = (String) obj;
                if (str.startsWith(elementXpath))
                {
                    findUnder = true;
                    break;
                }
            }
        }
		if(link!=null) 		
		{ 	//Case I:
			//create loop
			String tgtMappingSrc=link.getSource().getId();
			String localpath =QueryBuilderUtil.retrieveRelativePath(parentMappedXPath, tgtMappingSrc);
			String var = "$item_temp"+String.valueOf(varStack.size());
			sbQuery.append("for "+var+" in " +varStack.peek() );			
			sbQuery.append(localpath);			
			varStack.push(var);
			
			sbQuery.append(" return ");	
			//set online text
			if (localpath.contains("@"))
				inlineText="data("+var+")";
			else
				inlineText=var+"/text()";
			encodeElement(tgt, tgtMappingSrc,inlineText,true);
			varStack.pop();
			elementCreated=true;
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
					//set online text
					inlineText=QueryBuilderUtil.generateXpathExpressionForFunctionWithoutInput(inputFunction);//createQueryForFunctionNonInput(functionType);
					encodeElement(tgt, parentMappedXPath,inlineText,true);
					elementCreated=true;
				}
				else
				{
					//Case II.2
					//set inline text
					//a loop will be create to invoke data manipulation function
					inlineText=createQueryForFunctionWithInput(fLink,  parentMappedXPath);
					encodeElement(tgt, parentMappedXPath,inlineText,true);
					elementCreated=true;
				}
			}
			else if (hasMappedDescenant(tgt)||hasAnyMappedAttribute(tgt)!=null)
			{		
				//Case III && IV:
				//The current element is not mapped, but its attribute or descendant may be mapped
				//set online text
				if (tgt.getDefaultValue()!=null)
					inlineText=tgt.getDefaultValue();
				encodeElement(tgt, parentMappedXPath,inlineText,true);
				elementCreated=true;
			}
			else if (tgt.getMinOccurs().intValue()>0)
			{
				//Case V: create an empty element only if it is mandatory
				//set online text
				if (tgt.getDefaultValue()!=null)
					inlineText=tgt.getDefaultValue();
				encodeElement(tgt, parentMappedXPath,inlineText,false);
				elementCreated=true;
			}
            else if (findUnder)
            {
                //Case VI: create an element if a link of which id starts with this finding key
                //set online text
                if (tgt.getDefaultValue()!=null)
                    inlineText=tgt.getDefaultValue();
                encodeElement(tgt, parentMappedXPath,inlineText,false);
                elementCreated=true;
            }
		}
		xpathStack.pop();
		return elementCreated;
	}
	
	private void encodeElement(ElementMeta elementMeta, String referencePath,  String inlineText, boolean childrenRequired)
	{
		//process clone
		String elementTagName=elementMeta.getName();
		if (elementMeta.getMultiplicityIndex()!=null
			&&elementMeta.getMultiplicityIndex().intValue()>0)
			elementTagName=elementTagName.substring(0,elementTagName.indexOf("[") );
		
			
		sbQuery.append(" element "+ elementTagName + "{");
		//add attributes
		encodeAttribute(elementMeta, referencePath);	

		//process  children elements if required
		if (childrenRequired)
		{
			for(ElementMeta e:elementMeta.getChildElement()) 
			{
				//procee choice
				if(e.getName().startsWith("<choice>"))//.equals("<choice>"))
				{
					//do not create xml element, but "<choice>" is part of xmlpath
					xpathStack.push(e.getName());
					for (ElementMeta choiceChild:e.getChildElement())
					{
						if (!choiceChild.isIsChosen())
							continue;
						//create xml element for the chosen child
						if (processTargetElement(choiceChild, referencePath))
							sbQuery.append(",");
					}
					xpathStack.pop();
				}
				else if (processTargetElement(e, referencePath))
					sbQuery.append(",");
			}
		}
		// add inline text, close the element tag
		sbQuery.append(inlineText).append("}");
	}
	/**
	 * Set values for the attributes of an element
	 * Case I: The attribute is mapped from a source item
	 * Case II.1: The attribute is mapped to the out put port of a function without input port
	 * Case II.2: The attribute is mapped to the out put port of a function with one or more input ports
	 * Case III :use fixed value -- first precedent value
	 * Case IV : use default value
	 * Case V: Ignore this attribute
	 * @param elementMeta - Target Element meta
	 * @param mappedSourceNodeId - The previously mapped source node path, which is associated with the variable on varStack
	 */
	private void encodeAttribute(ElementMeta elementMeta, String mappedSourceNodeId)
	{
		for(AttributeMeta a:elementMeta.getAttrData())
		{
			String elementXpath=QueryBuilderUtil.buildXPath(xpathStack);
			String attrValue="";
			if(a.getFixedValue()!=null) 
			{ 
				//Case III -- the first priority
				attrValue="\""+a.getFixedValue()+"\"";
				sbQuery.append("attribute "+a.getName() +"{"+attrValue+"},");
				continue;
			}
			if(links.get(elementXpath+"/@"+a.getName())!=null) 
			{
				//Case I
				LinkType l = links.get(elementXpath+"/@"+a.getName());
				String attrMappingSrc = l.getSource().getId();
				attrValue="data("+varStack.peek();
				String localpath = null;
				localpath =QueryBuilderUtil.retrieveRelativePath(mappedSourceNodeId, attrMappingSrc);			
				attrValue=attrValue+localpath;
				attrValue=attrValue+")";
			}
			else if(metaToFunctionLinks.get(elementXpath+"/@"+a.getName())!=null) 
			{	
				//Case II:
				LinkType fLink=metaToFunctionLinks.get(elementXpath+"/@"+a.getName());
				String functionId=fLink.getTarget().getComponentid();
				FunctionType inputFunction=functions.get(functionId);
				if (inputFunction.getData().size()==1)
				{
					//Case II.1: The linked function dose not have input port
					FunctionType functionType=functions.get(fLink.getTarget().getComponentid());
					attrValue=attrValue+QueryBuilderUtil.generateXpathExpressionForFunctionWithoutInput(functionType);//createQueryForFunctionNonInput(functionType);
				}
				else
				{
					//Case II.2: The linked function has one or more input ports
					attrValue=createQueryForFunctionWithInput(fLink,  mappedSourceNodeId);
				}
			}

			else if(a.getDefaultValue()!=null) {//Case IV
				attrValue="\""+a.getDefaultValue()+"\"";
			}
			else //Case V
				continue;
			sbQuery.append("attribute "+a.getName() +"{"+attrValue+"},");
		}
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
		if (linkToFunction!=null)
		{
			for(FunctionData fData:functionType.getData())
			{
				//only consider input ports
				if (!fData.isInput())
					continue;
				LinkType inputLink=linkToFunction.get(fData.getName());		
				if (inputLink==null)
					continue;
				
				if (inputLink.getSource().getComponentid().length()==1)
				{
					String linkSrId=inputLink.getSource().getId();
					String parameterPath=var;
					String localpath =QueryBuilderUtil.retrieveRelativePath(elementMapingSourceId, linkSrId);
					parameterPath=parameterPath+localpath;
					parameterMap.put(fData.getName(), parameterPath );
				}
				else
				{ 
					//a function is mapped to this input port, 
					FunctionType inputFunction=functions.get(inputLink.getSource().getComponentid());
					String inputSrcValue=QueryBuilderUtil.generateXpathExpressionForFunctionWithoutInput(inputFunction);//createQueryForFunctionNonInput(functionType);
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
		return "";
	}
	
	/**
	 * Check if any descendant is mapped
	 * @param tgtElement
	 * @return
	 */
		protected boolean hasMappedDescenant(ElementMeta tgtElement){
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
		protected String hasAnyMappedAttribute(ElementMeta tgtElement)
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

