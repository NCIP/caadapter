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
import gov.nih.nci.cbiit.cmts.core.FunctionData;
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

	public String buildStyleSheetString()
	{
        XQueryBuilder xqueryBuilder=new XQueryBuilder(mapping);
	    String xmlResult=xqueryBuilder.getXQuery(false);
        String xslt = setupTransformXQueryToXSLT(xmlResult);
        return xslt;
    }
    private String setupTransformXQueryToXSLT(String text)
    {
        if (text == null) return "";
        String tx = text.trim();
        if (!tx.toLowerCase().startsWith("declare ")) return null;

        try
        {
            int idx1 = tx.toLowerCase().indexOf("document");
            //String buf = tx.substring(0, idx1).trim() + "\r\n";
            tx = tx.substring(idx1 + ("document").length() + 1, tx.lastIndexOf("}")).trim();

            String cx = setupTransformXQueryToXSLT(tx, 0);
            //return buf + cx;
            return "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n" +
                    "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\r\n" +
                    "   <xsl:template match=\"/\">\r\n" +
                    cx +
                    "   </xsl:template>\r\n" +
                    "</xsl:stylesheet>";
        }
        catch(Exception ee)
        {
            System.out.println("Error:" + ee.getMessage());
            return text;
        }
    }
    private String setupTransformXQueryToXSLT(String text, int level)
    {
        String unitSpace = "   ";
        String space = unitSpace;
        for (int i=0;i<level;i++) space = space + unitSpace;
        level++;

        String buf = "";

        boolean blockSkip = false;
        boolean elementPresent = false;
        while(true)
        {
            text = text.trim();
            //System.out.println("Level("+level+"): " + text);

            //if ((text.toLowerCase().startsWith("document")) ||
            //    (text.toLowerCase().startsWith("element "))
            if (text.toLowerCase().startsWith("element "))
            {
                int idx = text.indexOf("{");
                String eleName = text.substring(("element").length(), idx).trim();
                if (text.toLowerCase().startsWith("element <choice>")) blockSkip = true;
                else
                {
                    buf = buf + space + "<" + eleName + ">\r\n";// + space + "{" + "\r\n";
                }
                text = text.substring(idx).trim();
                idx = getBlockIndex(text);
                String sub = setupTransformXQueryToXSLT(text.substring(1, idx), level);
                text = text.substring(idx + 1).trim();
                String tail = "</" + eleName + ">";
                if (text.startsWith(","))
                {
                    text = text.substring(1).trim();
                    tail = tail + "";
                }
                if (blockSkip)
                {
                    blockSkip = false;
                    tail = "";
                    if (text.startsWith("\"\"")) text = text.substring(2).trim();
                }
                elementPresent = true;
                buf = buf + sub + space + tail + "\r\n";
            }
            else if (text.toLowerCase().startsWith("attribute "))
            {
                int idx = text.indexOf("{");
                buf = buf + space + "<xsl:attribute name=\"" + text.substring(("attribute").length(), idx).trim() + "\">\r\n";
                text = text.substring(idx).trim();
                idx = getBlockIndex(text);
                String sub = text.substring(1, idx);
                String tSub = transformContentToXSLT(sub, space, unitSpace);

                text = text.substring(idx + 1).trim();
                String tail = space + "</xsl:attribute>";
                if (text.startsWith(","))
                {
                    text = text.substring(1).trim();
                    //tail = "";
                }
                buf = buf + tSub + tail + "\r\n";
            }
            else if (text.toLowerCase().startsWith("for "))
            {
                String returnStr = "return";
                int idx = text.toLowerCase().indexOf(returnStr);
                String forLine = text.substring(0, idx);
                text = text.substring(idx + returnStr.length()).trim();
                forLine = forLine.substring(forLine.indexOf("in") + 2).trim();
                int idx1 = forLine.indexOf("$");
                int idx2 = forLine.indexOf("/");
                if ((idx1 >= 0)&&(idx2 > 0)&&(idx1 < idx2))
                {
                    forLine = forLine.substring(idx2 + 1);
                }
                String forXSLT = "<xsl:for-each select=\""+forLine+"\">";
                String forTail = "</xsl:for-each>";

                String sub2 = "";
                if (text.toLowerCase().startsWith("element "))
                {
                    idx = getBlockIndex(text);
                    String eleLine = text.substring(0, idx + 1);
                    sub2 = setupTransformXQueryToXSLT(eleLine, level);
                    text = text.substring(idx+1).trim();
                    elementPresent = true;
                }
                else
                {
                    System.out.println("CCCCC Weired Text : " + text);
                    sub2 = "#### Error ####";
                }

                if (text.startsWith(","))
                {
                    text = text.substring(1).trim();
                }
                buf = buf + space + forXSLT + "\r\n" + sub2 + space + forTail + "\r\n";

                //text = text.substring(idx + returnStr.length()).trim();
                //buf = buf + space + text.substring(0, idx + 6) + "\r\n";
                //text = text.substring(idx + 6).trim();
            }
            else if ((text.toLowerCase().startsWith("$item"))&&(text.toLowerCase().indexOf("/text()") > 0))
            {
                int idx = text.toLowerCase().indexOf("/text()");
                text = text.substring(idx + ("/text()").length()).trim();
                if (!elementPresent) buf = buf + space + "<xsl:value-of select=\".\"/>\r\n";
            }
            else if (text.startsWith("\"\""))
            {
                text = text.substring(2).trim();
            }
            else if (text.startsWith("\""))
            {
                text = text.substring(1).trim();
                int idx = text.indexOf("\"");
                if (idx > 0)
                {
                    String str = text.substring(0, idx);
                    text = text.substring(idx+1).trim();
                    buf = buf + space + "<xsl:text>"+str+"</xsl:text>\r\n";
                }
            }
            else if (text.toLowerCase().startsWith("string(\""))
            {
                text = text.substring(("string(\"").length()).trim();
                int idx = text.indexOf("\"");
                if (idx > 0)
                {
                    String str = text.substring(0, idx);
                    text = text.substring(idx+1).trim();
                    while (text.startsWith(")")) text = text.substring(1).trim();
                    buf = buf + space + "<xsl:value-of select=\"string(&quot;" + str + "&quot;)\" />\r\n";
                }
            }
            else if (text.indexOf("(") >= 0)
            {
                int idx1 = text.indexOf("(");
                int idx2 = getBlockIndex(text, "(", ")");
                if ((idx2 < 0)||(idx1 > idx2))
                {
                    System.out.println("CCCCC Something weired Xquery line (3) : " + text);
                    break;
                }
                String functionL = text.substring(0, idx2 + 1);
                text = text.substring(idx2 + 1);

                String str = transformFunctionItem(functionL);
                if (str == null)
                {
                    System.out.println("CCCCC Something weired Xquery line (4) : " + text + ", functionL=" + functionL);
                    break;
                }

                buf = buf + space + "<xsl:value-of select=\""+str+"\" />\r\n";
            }
            else if (text.equals(""))
            {
                break;
            }
            else
            {
                System.out.println("CCCCC Something weired Xquery line : " + text);
                break;
            }
        }
        return buf;
    }
    private String transformContentToXSLT(String sub, String space, String unitSpace)
    {
        String tSub = null;
        sub = sub.trim();
        //System.out.println("CCCCC 110 sub=" + sub);
        if ((sub.toLowerCase().startsWith("data(doc($"))||
            (sub.toLowerCase().startsWith("data($item"))||
            (sub.toLowerCase().startsWith("$item_"))||
            (sub.toLowerCase().startsWith("doc($")))
        {
            int idx = sub.indexOf("/");
            if (idx > 0)
            {
                String con = sub.substring(idx + 1).trim();
                //System.out.println("CCCCC 111 con=" + con);
                while (con.endsWith(")")) con = con.substring(0, con.length()-1).trim();
                tSub = space + unitSpace + "<xsl:value-of select=\"" + con + "\"/>\r\n";
                //System.out.println("CCCCC 112 tSub=" + tSub);
            }
        }
        else if (sub.startsWith("\""))
        {
            String sub2 = sub.substring(sub.indexOf("\"")+1);
            String con = sub2.substring(0, sub2.indexOf("\""));
            tSub = space + unitSpace + "<xsl:text>"+con+"</xsl:text>\r\n";
        }
        else if (sub.toLowerCase().startsWith("string(\""))
        {
            String sub2 = sub.substring(sub.indexOf("\"")+1);
            String con = sub2.substring(0, sub2.indexOf("\""));
            tSub = space + unitSpace + "<xsl:value-of select=\"string(&quot;" + con + "&quot;)\" />\r\n";
        }
        if (tSub != null) return tSub;

        int idx = sub.indexOf("(");
        if (idx <= 0) return null;

        //tSub = space + unitSpace + "<xsl:value-of select=\""+transformContentToXSLT_item(sub)+"\" />\r\n";

        tSub = space + unitSpace + "<xsl:value-of select=\""+transformFunctionItem(sub)+"\" />\r\n";


        return tSub;
    }
    private String transformFunctionItem(String sub)
    {
        sub = sub.trim();
        int idx1 = sub.indexOf("(");
        int idx2 = getBlockIndex(sub, "(", ")");

        if ((idx2 < 0)||(idx1 > idx2))
        {
            //System.out.println("CCCC 1 text=" + sub + ", idx1=" + idx1 + ", idx2=" + idx2);
            return null;
        }

        String inner = sub.substring(idx1 + 1, idx2) + "\t";
        String funcName = sub.substring(0, idx1);
        String transformedInner = "";
        if (!inner.trim().equals(""))
        {
            int open = 0;
            String s = "";

            for(int i=0;i<inner.length();i++)
            {
                String achar = inner.substring(i, i + 1);
                if (achar.equals("(")) open++;
                if (achar.equals(")"))
                {
                    open--;
                }
                s = s + achar;
                if (open != 0) continue;

                String delimit = null;
                if (achar.equals(",")) delimit = ",";
                if (achar.equals("\t")) delimit = "";
                if (s.endsWith("mod")) delimit = "mod";
                if (delimit != null)
                {
                    s = s.substring(0, s.length()-delimit.length());
                    String str = "";
                    //System.out.println("CCCC 22 text=" + s + ", idx1=" + idx1 + ", idx2=" + idx2);

                    if (s.indexOf("(") >= 0)
                    {
                        if ((s.toLowerCase().startsWith("data(doc($"))||
                            (s.toLowerCase().startsWith("data($item"))||
                            (s.toLowerCase().startsWith("doc($docn"))||
                            (s.toLowerCase().startsWith("$item")))
                        {
                            int idx3 = s.indexOf("/");
                            if (idx3 > 0)
                            {
                                String con = s.substring(idx3 + 1).trim();
                                while (con.endsWith(")")) con = con.substring(0, con.length()-1).trim();
                                str = con;
                            }
                            else str = null;
                            //str = transformContentToXSLT_item(s);
                        }
                        //if (s.startsWith("doc($docN")) str = transformContentToXSLT_item(s);
                        else if (s.startsWith("string("))
                        {
                            String sub2 = s.substring(s.indexOf("\"")+1);
                            String con = sub2.substring(0, sub2.indexOf("\""));
                            str = "string(&quot;" + con + "&quot;)";
                            //str = transformContentToXSLT_item(s);
                        }
                        else
                        {
                            //System.out.println("CCCC 39 text=" + s + ", idx1=" + idx1 + ", idx2=" + idx2);

                            str = transformFunctionItem(s);
                        }
                    }
                    else str = transformContentToXSLT_item(s);
                    if ((str == null)||(str.trim().equals("")))
                    {
                        //System.out.println("CCCC 3 text=" + s + ", idx1=" + idx1 + ", idx2=" + idx2);
                        return null;
                    }
                    //else System.out.println("CCCC 223 text=" + str);


                    transformedInner = transformedInner + str + " " + delimit + " ";
                    s = "";
                    str = "";
                }
            }
        }
        String funcName2 = funcName;
        if (funcName.equals("doc")) funcName2 = "document";
        else if (funcName.equals("replace")) funcName2 = "translate";

        return funcName2 + "(" + transformedInner + ")";
    }
    private String transformContentToXSLT_item(String sub)
    {
        String tSub = null;
        if ((sub.toLowerCase().startsWith("data(doc($"))||
            (sub.toLowerCase().startsWith("data($item"))||
            (sub.toLowerCase().startsWith("doc($docn"))||
            (sub.toLowerCase().startsWith("$item")))
        {
            int idx = sub.indexOf("/");
            if (idx > 0)
            {
                String con = sub.substring(idx + 1).trim();
                while (con.endsWith(")")) con = con.substring(0, con.length()-1).trim();
                tSub = con;
            }
            else return null;
        }
        //if (sub.toLowerCase().startsWith("data(doc($docname)/"))
        //{
        //    String con = sub.substring(("data(doc($docname)/").length());
        //    if (con.endsWith(")")) con = con.substring(0, con.length()-1);
        //    tSub = con;
        //}
        else if ((sub.startsWith("\""))||(sub.toLowerCase().startsWith("string(\"")))
        {
            String sub2 = sub.substring(sub.indexOf("\"")+1);
            String con = sub2.substring(0, sub2.indexOf("\""));
            tSub = "string(&quot;" + con + "&quot;)";

        }
        else if (sub.toLowerCase().startsWith("string(\""))
        {
            String sub2 = sub.substring(sub.indexOf("\"")+1);
            String con = sub2.substring(0, sub2.indexOf("\""));
            tSub = "string(&quot;" + con + "&quot;)";
        }
        if (tSub != null) return tSub;

        int idx = sub.indexOf("(");
        if (idx < 0) return null;
        int idx2 = getBlockIndex(sub, "(", ")");
        if (idx2 < 0) return null;
        String functionTitle = sub.substring(0,idx);
        String core = sub.substring(idx + 1, idx2) + ",";

        String bufLine = functionTitle + "(";
        String buf = "";
        for(int i=0;i<core.length();i++)
        {
            String achar = core.substring(i, i + 1);
            if (achar.equals(","))
            {
                buf = buf.trim();
                if (!buf.equals(""))
                {
                   bufLine = bufLine + transformContentToXSLT_item(buf) + ",";
                }
                buf = "";
            }
            else buf = buf + achar;
        }

        if (bufLine.endsWith(",")) bufLine = bufLine.substring(0, bufLine.length()-1);
        return bufLine + ")";
    }
    private int getBlockIndex(String text)
    {
        return getBlockIndex(text, "{", "}");
    }
    private int getBlockIndex(String text, String openB, String closeB)
    {
        int open = 0;

        for(int i=0;i<text.length();i++)
        {
            String achar = text.substring(i, i + 1);
            if (achar.equals(openB)) open++;
            if (achar.equals(closeB))
            {
                open--;
                if (open == 0) return i;
            }
        }
        return -1;
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
        String nameE = elementMeta.getName();
        if (nameE.endsWith("]"))
        {
            int idx = nameE.indexOf("[");
            if(idx < 0) nameE = nameE.substring(0, (nameE.length() - 1));
            else nameE = nameE.substring(0, idx);
        }
        ElementMeta tempMeta = null;
        
        while (nameE.equals("<choice>"))
        {
            if (elementMeta.getChildElement().size() == 0) break;

            for (ElementMeta cMeta:elementMeta.getChildElement())
            {
                System.out.println("   CCCX <choice> child meta name="  + cMeta.getName() + ", isIsChosen()=" + cMeta.isIsChosen());
                if (cMeta.isIsChosen())
                {
                    tempMeta = cMeta;
                    nameE = tempMeta.getName();
                    //break;
                }
            }
            if (tempMeta == null) break;
        }
        if (nameE.equals("<choice>"))
        {
            for(ElementMeta e:elementMeta.getChildElement()) processTargetElement(e,childElementRef, parentTemplate);
            return;
        }
        if (tempMeta != null) elementMeta = tempMeta;
        Element tgtDataElement= new Element(nameE);
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
			LinkType fLink = metaToFunctionLinks.get(targetElementXpath);
			if (fLink!=null)
			{
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

    /**
	 * Set values for the attributes of an element
	 * Case I: The attribute is mapped from a source item
	 * Case II.1: The attribute is mapped to the out put port of a function without input port
	 * Case II.2: The attribute is mapped to the out put port of a function with one or more input ports
	 * Case III :use fixed value first -- the highest precedence
	 * Case IV : use default value
	 * Case V: Ignore this attribute
	 * @param elementMeta - Target Element meta
	 * @param mappedSourceNodeId - The previously mapped source node path, which is associated with the variable on varStack
	 */
	private void encodeAttribute(Element targetData, ElementMeta targetMeta,
			String targetElementMetaXpath)
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
				//I.1 is default
				String selectExp=".";
				if (tgtMappingSrc.indexOf("@")>-1)
					//case I.1
					selectExp=tgtMappingSrc.substring(tgtMappingSrc.indexOf("@"));
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
				addAttributeTemplate(targetData, attrMeta.getName(), attrMeta.getFixedValue(), null);
			else
				//case V
				continue;				
			
		}
	}
		
	/**
	 * Set content of an attribute template
	 * Case I: set literal data as inline text
	 * Case II: Set xpath expression for runtime data
	 * @param element
	 * @param attributeTemplate
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
}
