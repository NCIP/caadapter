package gov.nih.nci.cbiit.cmts.transform.artifact;

import java.util.List;
import java.util.Stack;

import org.jdom.Element;

import gov.nih.nci.cbiit.cmts.core.Component;
import gov.nih.nci.cbiit.cmts.core.ComponentType;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.core.LinkType;
import gov.nih.nci.cbiit.cmts.core.Mapping;
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
		processTargetElement(tgt.getRootElement(), null, rootTemplate);
		return stylesheet;
	}
	
	private void processTargetElement(ElementMeta elementMeta,String parentMappedXPath,
			XSLTTemplate parentTemplate)
	{
		xpathStack.push(elementMeta.getName());
		boolean elementCreated=false;
		String inlineText="\"\"";
		String elementXpath=QueryBuilderUtil.buildXPath(xpathStack);
		LinkType link = links.get(elementXpath);
		
		Element tgtDataElement= new Element(elementMeta.getName());
        if(link==null)
        {
    		parentTemplate.addContent(tgtDataElement);
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
				XSLTApplyTemplates attrApply=new XSLTApplyTemplates();
				//remove the first "/"
				attrApply.setSelect(tgtMappingSrc.substring(1));	
				tgtDataElement.addContent(attrApply);
				parentTemplate.addContent(tgtDataElement);
			}
			else
			{
				LinkType fLink = metaToFunctionLinks.get(elementXpath);
				if (fLink==null)
				{
					//case II
					XSLTElement forEach=new XSLTElement("for-each");
					forEach.setAttribute("select",tgtMappingSrc);
					XSLTApplyTemplates elementApply=new XSLTApplyTemplates();
					//remove the first "/"
					elementApply.setSelect(".");	
					tgtDataElement.addContent(elementApply);
					forEach.addContent(tgtDataElement);
				}
			}
        }
		if (hasMappedDescenant(elementMeta)||hasAnyMappedAttribute(elementMeta)!=null)
		{
			for(ElementMeta e:elementMeta.getChildElement()) 
			{
				XSLTCallTemplate callTemplate=new XSLTCallTemplate();
				String tmpName=varName+varCount++;
				
				callTemplate.setCalledTemplate(tmpName);
				tgtDataElement.addContent(callTemplate);
				XSLTTemplate calledTemplate =new XSLTTemplate();
				calledTemplate.setAttribute("name", tmpName);
				stylesheet.addTempate(calledTemplate);
				processTargetElement(e,null, calledTemplate);
			}
		}
		xpathStack.pop();
	}
}
