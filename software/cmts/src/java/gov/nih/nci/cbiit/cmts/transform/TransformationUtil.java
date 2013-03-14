/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.transform;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;

public class TransformationUtil {
//	public static String XML_VERSION="<?xml version=\"1.0\"";
	public static String XML_ENCODING_ISO_8859_1=" encoding=\"ISO-8859-1\"";
	public static String XML_ENCODING_UTF_8     =" encoding=\"UTF-8\"";
	public static String XML_ENCODING_UTF_16    =" encoding=\"UTF-16\"";
	protected static String CDA_STYLE_TARGET="xml-stylesheet";
	protected static String CDA_STYLE_TYPE="text/xsl";
	protected static String CDA_STYLE_REF="CDA.xsl";
	protected static String CDA_STYLE_NAMESPACE="urn:hl7-org:v3";
	
	public static String formatXqueryResult(String sIn, boolean presentable)
	{
		if (sIn==null||sIn.length()==0)
			return "Invalid data:"+sIn;
		StringBuffer rtnSb=new StringBuffer();
		StringReader sr=new StringReader(sIn);
		LineNumberReader lR=new LineNumberReader(sr);
		String line;
		try {
			while ((line=lR.readLine())!=null)
			{
				if (line.trim().length()>0)
					rtnSb.append(line+"\n");
			}
			lR.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			return formatXmlDocument(rtnSb.toString(), presentable);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	private static String formatXmlDocument(String xmlData, boolean processable) throws Exception {
	    SAXBuilder builder = new SAXBuilder();
	   	StringReader stReader=new StringReader(xmlData);
	   	InputSource inSrc =new InputSource(stReader);
	    Document document = builder.build(inSrc);
	    if (processable)
	    {
	    	HashMap<String, String> styleMap=new HashMap<String, String>();
		    styleMap.put("type", CDA_STYLE_TYPE);
		    styleMap.put("href", CDA_STYLE_REF);
		    ProcessingInstruction styleIns=new ProcessingInstruction(CDA_STYLE_TARGET, styleMap);
		    document.addContent(0,styleIns);
		    resetNamespace(document.getRootElement(), Namespace.getNamespace(CDA_STYLE_NAMESPACE));
	    }    
	    XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
	    StringWriter writer=new StringWriter();
	    xmlOutputter.output(document,writer);
	    writer.close();
	    return writer.toString();
	  }
	
	private static void resetNamespace(Element elmnt, Namespace ns)
	{
		elmnt.setNamespace(ns);
		for (Object child:elmnt.getChildren())
			resetNamespace((Element)child,ns);
	}

}
