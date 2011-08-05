package gov.nih.nci.cbiit.cmts.transform.artifact;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.jdom.Element;

import gov.nih.nci.cbiit.cmts.core.AttributeMeta;
import gov.nih.nci.cbiit.cmts.core.Component;
import gov.nih.nci.cbiit.cmts.core.ComponentType;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.core.FunctionType;
import gov.nih.nci.cbiit.cmts.core.LinkType;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.function.FunctionException;
import gov.nih.nci.cbiit.cmts.function.FunctionInvoker;
import gov.nih.nci.cbiit.cmts.transform.QueryBuilderUtil;
import gov.nih.nci.cbiit.cmts.transform.XQueryBuilder;

public class StylesheetBuilder extends XQueryBuilder {

	private XSLTStylesheet stylesheet;
	private String varName="_template_";
	private int varCount=0;
	public StylesheetBuilder(Mapping m) {
		super(m);
		// TODO Auto-generated constructor stub
	}

	public XSLTStylesheet buildStyleSheet()
	{
		stylesheet =new XSLTStylesheet();
		
		XSLTTemplate rootTemplate=new XSLTTemplate();
		rootTemplate.setMatch("/");
		
		List<Component> l = mapping.getComponents().getComponent();
		Component tgt = null;
		Component src=null;
		for (Component c:l) {
			if (c.getType().equals(ComponentType.TARGET)) 
				tgt = c;
			else if (c.getType().equals(ComponentType.SOURCE))
				src=c;
			
		}

		stylesheet.addTempate(rootTemplate);
		xpathStack = new Stack<String>();
		processTargetElement(tgt.getRootElement(), "", rootTemplate);
		return stylesheet;
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
	 * @param elementMeta
	 * @param parentMappedXPath
	 * @param parentTemplate
	 */
	private void processTargetElement(ElementMeta elementMeta,String parentMappedXPath,
			XSLTTemplate parentTemplate)
	{
		xpathStack.push(elementMeta.getName());
		String targetElementXpath=QueryBuilderUtil.buildXPath(xpathStack);
		LinkType link = links.get(targetElementXpath);
		String childElementRef=parentMappedXPath;
		Element tgtDataElement= new Element(elementMeta.getName());
		//case III: The element is not mapped, but its attribute is mapped
		encodeAttribute(tgtDataElement,elementMeta, targetElementXpath);
        if(link!=null)
        {
			String tgtMappingSrc=link.getSource().getId();
			if (tgtMappingSrc.indexOf("@")>-1)
			{
				//case I.1: link source is an attribute 
				XSLTElement valueElment=new XSLTElement("value-of");
				String selectExp=tgtMappingSrc.substring(parentMappedXPath.length()+1);
				valueElment.setAttribute("select", selectExp);
				tgtDataElement.addContent(valueElment);
				parentTemplate.addContent(tgtDataElement);
			}
			else
			{
				childElementRef=tgtMappingSrc;
 				//case I.2: link source is an element
				XSLTElement forEach=new XSLTElement("for-each");
				forEach.setAttribute("select",tgtMappingSrc.substring(parentMappedXPath.length()+1));
				if (!hasMappedDescenant(elementMeta))
				{
					//apply content if this target element is leaf
					XSLTApplyTemplates elementApply=new XSLTApplyTemplates();
					elementApply.setSelect(".");	
					tgtDataElement.addContent(elementApply);
				}
				forEach.addContent(tgtDataElement);
				parentTemplate.addContent(forEach);
			}
        }
        else
        {
    		parentTemplate.addContent(tgtDataElement);
			LinkType fLink = metaToFunctionLinks.get(targetElementXpath);
			if (fLink!=null)
			{
				//Case II: The element is mapped to function output port
				String functionId=fLink.getTarget().getComponentid();
				FunctionType inputFunction=functions.get(functionId);
				if (inputFunction.getData().size()==1)
				{
					//Case II.1: The function does not have input port
					XSLTElement outElement=createXsltForFunctionNonInput(inputFunction);
					tgtDataElement.addContent(outElement);
				}
				else
				{
					//Case II.2: The function has one or more input ports
					//a loop will be create to invoke data manipulation function
//					inlineText=createQueryForFunctionWithInput(fLink,  parentMappedXPath);
//					encodeElement(tgt, parentMappedXPath,inlineText,true);
//					elementCreated=true;
				}
			}
        }
		if (hasMappedDescenant(elementMeta))
		{
			//case IV: Neither the element nor its attribute is mapped. But its descendant
			//is mapped
			for(ElementMeta e:elementMeta.getChildElement()) 
			{
				XSLTCallTemplate callTemplate=new XSLTCallTemplate();
//				String tmpName=targetElementXpath.replace("/", "_")+"_"+e.getName();//varName+varCount++;
				String tmpName=varName+varCount++;
				callTemplate.setCalledTemplate(tmpName);
				tgtDataElement.addContent(callTemplate);
				XSLTTemplate calledTemplate =new XSLTTemplate();
				calledTemplate.setAttribute("name", tmpName);
				stylesheet.addTempate(calledTemplate);
				processTargetElement(e,childElementRef, calledTemplate);
			}
		}
		xpathStack.pop();
	}
	
	private void encodeAttribute(Element targetData, ElementMeta targetMeta,
			String targetElementMetaXpath)
	{
		for (AttributeMeta attrMeta:targetMeta.getAttrData())
		{
			String targetAttributePath=targetElementMetaXpath+"/@"+attrMeta.getName();
			LinkType link = links.get(targetAttributePath);
			XSLTElement xsltAttr=new XSLTElement("attribute");
			xsltAttr.setAttribute("name", attrMeta.getName());
			if (link==null)
			{
				String targetAttrValue="";
				if (attrMeta.getFixedValue()!=null)
					targetAttrValue=attrMeta.getFixedValue();
				else if (attrMeta.getDefaultValue()!=null)
					targetAttrValue=attrMeta.getDefaultValue();
				if (!targetAttrValue.equals(""))
				{
					XSLTElement xlstText=new XSLTElement("text");
					xlstText.setText(targetAttrValue);
					xsltAttr.addContent(xlstText);
					targetData.addContent(xsltAttr);
				}					
			}
			else
			{
	        	/**
	        	 * Case I: link source is an attribute 
	        	 * case II: link source is an element
	        	 * case III: link source is the output of a data processing function
	        	 */
				String tgtMappingSrc=link.getSource().getId();
				if (tgtMappingSrc.indexOf("@")>-1)
				{
					//case I
					XSLTElement valueElment=new XSLTElement("value-of");
					String selectExp=tgtMappingSrc.substring(tgtMappingSrc.indexOf("@"));
					valueElment.setAttribute("select", selectExp);
					xsltAttr.addContent(valueElment);
					targetData.addContent(xsltAttr);
				}
				else
				{
					LinkType fLink = metaToFunctionLinks.get(targetAttributePath);
					if (fLink==null)
					{
						//case II
						XSLTElement valueElment=new XSLTElement("value-of");
						valueElment.setAttribute("select", ".");
						xsltAttr.addContent(valueElment);
						targetData.addContent(xsltAttr);
					}
					else
					{
						//case III
					}
				}
			}
		}
	}
	
	private XSLTElement createXsltForFunctionNonInput(FunctionType functionType)
	{
		if (functionType.getData().size()!=1)
			return null;// "invalid function:"+functionType.getGroup()+":"+functionType.getName()+":"+functionType.getMethod();
		Map<String,String> parameterMap=new HashMap<String,String>();
		Object argList[]=new Object[]{functionType, parameterMap};
		
		try {
			XSLTElement rtnElement=new XSLTElement("value-of");
			String xqueryString=(String)FunctionInvoker.invokeFunctionMethod(functionType.getClazz(), functionType.getMethod(), argList);
			rtnElement.setAttribute("select", xqueryString);
			return rtnElement ;
		} catch (FunctionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
