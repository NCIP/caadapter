/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.transform.artifact;

import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;

import org.jdom.Element;

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
import gov.nih.nci.cbiit.cmts.transform.QueryBuilderUtil;
import gov.nih.nci.cbiit.cmts.transform.XQueryBuilder;
import gov.nih.nci.cbiit.cmts.mapping.MappingFactory;

import javax.xml.bind.JAXBException;

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

        ElementMeta srcRootMeta=src.getRootElement();
        if (srcRootMeta!=null&&srcRootMeta.getNameSpace()!=null)
            stylesheet.getRootElement().setAttribute("xpath-default-namespace", srcRootMeta.getNameSpace());
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
        String targetElementXpath = QueryBuilderUtil.buildXPath(xpathStack);

        LinkType link = links.get(targetElementXpath);
        LinkType fLink = metaToFunctionLinks.get(targetElementXpath);

        String childElementRef=parentMappedXPath;
        String nameE = elementMeta.getName();
        if (nameE.endsWith("]"))
        {
            int idx = nameE.indexOf("[");
            if(idx < 0) nameE = nameE.substring(0, (nameE.length() - 1));
            else nameE = nameE.substring(0, idx);
        }

        boolean hasMapped = false;

        hasMapped = encodeAttributeCheck(elementMeta, targetElementXpath);
        if(link!=null) hasMapped = true;
        if (fLink!=null) hasMapped = true;

        boolean hasMappedDesc = hasMappedDescenant(elementMeta);

        // If neither this node nor descendent is mapped return.
        if ((!hasMapped)&&(!hasMappedDesc))
        {
            xpathStack.pop();
            return;
        }

        // If this node is a <choice> element, bypass to child nodes.
        if ((nameE.equalsIgnoreCase("<choice>"))||(nameE.toLowerCase().startsWith("<choice>")))
        {
            for(ElementMeta e:elementMeta.getChildElement())
            {
                processTargetElement(e, parentMappedXPath, parentTemplate);
            }

            xpathStack.pop();
            return;
        }

        Element tgtDataElement= new Element(nameE);

        encodeAttribute(tgtDataElement,elementMeta, targetElementXpath, parentMappedXPath);

        if(link!=null)
        {
            hasMapped = true;
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
                String localpath =QueryBuilderUtil.retrieveRelativePath(parentMappedXPath, tgtMappingSrc);
                //case I.2: link source is an element
                XSLTElement forEach=new XSLTElement("for-each");
//				forEach.setAttribute("select",tgtMappingSrc.substring(parentMappedXPath.length()+1));
                //remove the leading "/" -- .substring(1)
                forEach.setAttribute("select",localpath.substring(1));
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
            //LinkType fLink = metaToFunctionLinks.get(targetElementXpath);
            if (fLink!=null)
            {
                hasMapped = true;

                //Case II: The element is mapped to function output port
                String functionId=fLink.getTarget().getComponentid();
                FunctionType inputFunction=functions.get(functionId);
                if (inputFunction.getData().size()==1)
                {
                    //Case II.1: The function does not have input port
                    XSLTElement outElement=new XSLTElement("value-of");
                    String xpathString=(String)QueryBuilderUtil.generateXpathExpressionForFunctionWithoutInput(inputFunction);
                    outElement.setAttribute("select", xpathString);
                    tgtDataElement.addContent(outElement);
                }
                else
                {
                    //Case II.2: The function has one or more input ports
                    //?a loop will be create to invoke data manipulation function
                    String xpathString=createXpathExpressionForFunctionWithInput(fLink, parentMappedXPath);
                    XSLTElement outElement=new XSLTElement("value-of");
                    outElement.setAttribute("select", xpathString);
                    tgtDataElement.addContent(outElement);
                }
            }
        }

        if (hasMappedDesc)
        {
            //case IV: Neither the element nor its attribute is mapped. But its descendant
            //is mapped
            XSLTCallTemplate callTemplate=new XSLTCallTemplate();
//			String tmpName=targetElementXpath.replace("/", "_")+"_"+e.getName();//varName+varCount++;
            String tmpName=varName+varCount++;
            callTemplate.setCalledTemplate(tmpName);
            tgtDataElement.addContent(callTemplate);
            XSLTTemplate calledTemplate =new XSLTTemplate();
            calledTemplate.setAttribute("name", tmpName);
            stylesheet.addTempate(calledTemplate);

            for(ElementMeta e:elementMeta.getChildElement())
            {
                processTargetElement(e,childElementRef, calledTemplate);
            }

        }
        xpathStack.pop();
    }
    /**
     * Set values for the attributes of an element
     * Case I: The attribute is mapped from a source item
     * Case II.1: The attribute is mapped to the out put port of a function without input port
     * Case II.2: The attribute is mapped to the out put port of a function with one or more input ports
     * Case III :use fixed value first -- the highest precedence
     * Case IV : use default value
     * Case V: Ignore this attribute
     * @param targetData - Target Element data or template
     * @param targetMeta - Target Element meta
     * @param targetElementMetaXpath - The xml path of target element
     * @param matchedAncestor - The previously mapped source node path, which is associated with the "select" attribute of last "for-each" loop
     */
    private void encodeAttribute(Element targetData, ElementMeta targetMeta,
                                 String targetElementMetaXpath, String matchedAncestor)
    {



            for (AttributeMeta attrMeta:targetMeta.getAttrData())
            {

                if (attrMeta.getFixedValue()!=null)
                {
                    //case III
                    addAttributeTemplate(targetData, attrMeta.getName(), attrMeta.getFixedValue(), null);
                    continue;
                }
                String targetAttributePath=targetElementMetaXpath+"/@"+attrMeta.getName();
                LinkType link = links.get(targetAttributePath);

                if (link!=null)
                {
                    /**
                     * Case I: The attribute is mapped from a source item
                     * Case I.1: link source is an element
                     * case I.2: link source is an attribute
                     */
                    String tgtMappingSrc=link.getSource().getId();
                    String localPath =QueryBuilderUtil.retrieveRelativePath(matchedAncestor, tgtMappingSrc);

                    //I.1 is default
                    String selectExp=localPath.substring(1);// ".";
    //				if (tgtMappingSrc.indexOf("@")>-1)
    //					//case I.1
    //					selectExp=tgtMappingSrc.substring(tgtMappingSrc.indexOf("@"));
    //				else
    //					selectExp=tgtMappingSrc;
                    addAttributeTemplate(targetData,  attrMeta.getName(), null, selectExp);

                }
                else if(metaToFunctionLinks.get(targetAttributePath)!=null)
                {
                    //case II
                    LinkType fLink = metaToFunctionLinks.get(targetAttributePath);
                    String functionId=fLink.getTarget().getComponentid();
                    FunctionType inputFunction=functions.get(functionId);
                    String attrValueExpression=".";
                    if (inputFunction.getData().size()==1)
                    {
                        //Case II.1: The linked function dose not have input port
                        FunctionType functionType=functions.get(fLink.getTarget().getComponentid());
                        attrValueExpression=(String)QueryBuilderUtil.generateXpathExpressionForFunctionWithoutInput(functionType);
                    }
                    else
                    {
                        //Case II.2: The linked function has one or more input ports
                        attrValueExpression=createXpathExpressionForFunctionWithInput(fLink,  "");
                    }
                    addAttributeTemplate(targetData, attrMeta.getName(), null, attrValueExpression);

                }
                else if (attrMeta.getDefaultValue()!=null)
                    //case IV
                    addAttributeTemplate(targetData, attrMeta.getName(), attrMeta.getDefaultValue(), null);
                else
                    //case V
                    continue;

            }

    }
    private boolean encodeAttributeCheck(ElementMeta targetMeta, String targetElementMetaXpath)
    {
        boolean hasMapped = false;
        for (AttributeMeta attrMeta:targetMeta.getAttrData())
        {

            String targetAttributePath=targetElementMetaXpath+"/@"+attrMeta.getName();
            LinkType link = links.get(targetAttributePath);

            if (link!=null)
            {
                hasMapped = true;
            }
            else if(metaToFunctionLinks.get(targetAttributePath)!=null)
            {
                hasMapped = true;
            }
        }
        return hasMapped;
    }

    /**
     * Set content of an attribute template
     * Case I: set literal data as inline text
     * Case II: Set xpath expression for runtime data
     * @param element
     * @param attributeName
     * @param inlineText
     * @param selectExpression
     */
    private void addAttributeTemplate(Element element, String attributeName,
                                      String inlineText, String selectExpression)
    {
        if (inlineText!=null)
        {
            XSLTElement attributeTemplate=new XSLTElement("attribute");
            attributeTemplate.setAttribute("name", attributeName);
            XSLTElement xlstStatement=new XSLTElement("text");
            xlstStatement.setText(inlineText);
            attributeTemplate.addContent(xlstStatement);
            element.addContent(attributeTemplate);
        }
        else if (selectExpression!=null)
        {
            XSLTElement attributeTemplate=new XSLTElement("attribute");
            attributeTemplate.setAttribute("name", attributeName);
            XSLTElement xlstStatement=new XSLTElement("value-of");
            xlstStatement.setAttribute("select", selectExpression);
            attributeTemplate.addContent(xlstStatement);
            element.addContent(attributeTemplate);
        }
        else
            return;
    }
    private String createXpathExpressionForFunctionWithInput(LinkType link, String elementMapingSourceId)
    {
        FunctionType functionType=functions.get(link.getTarget().getComponentid());
        //If there is only port in the functionType,
        if (functionType.getData().size()==1)
            return "invalid function:"+functionType.getGroup()+":"+functionType.getName()+":"+functionType.getMethod();

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
                    String localpath =QueryBuilderUtil.retrieveRelativePath(elementMapingSourceId, linkSrId);
                    //remove the leading '/' in the relative path
                    //XQuery builder use absolute path of source node: "variable value" + "relative path"
                    //XSLT builder only  need the relative path from the current template
                    parameterMap.put(fData.getName(), localpath.substring(1));
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

    public static void main(String[] args)
    {
        if (args.length != 2)
        {
            System.out.println("Usage: {Command_Line} mapping_file output_file");
            return;
        }

        String mappingFile=args[0];
        Mapping map = null;
        try
        {
            map= MappingFactory.loadMapping(new File(mappingFile));
        }
        catch(JAXBException je)
        {
            System.out.println("JAXBException : " + je.getMessage());
            return;
        }

        StylesheetBuilder transformer = new StylesheetBuilder(map);

        StringWriter writer = new StringWriter();
        XSLTStylesheet xsltSheet=transformer.buildStyleSheet();
		try
        {
            xsltSheet.writeOut(writer);
        }
        catch(Exception ie)
        {
            System.out.println("XSLT Generating Error : " + ie.getMessage());
            return;
        }
        String xmlResult=writer.toString();

        FileWriter fw = null;
        try
        {
            fw = new FileWriter(args[1]);
            fw.write(xmlResult);
            fw.close();
        }
        catch(Exception ie)
        {
            System.out.println("XSLT File Writing Error(" + args[1] + ") : " + ie.getMessage());
            return;
        }

        System.out.println("XSLT Transformation has completed successfully ! : " + args[1]);
    }
}
