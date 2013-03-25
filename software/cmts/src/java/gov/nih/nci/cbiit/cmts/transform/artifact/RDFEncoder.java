/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.transform.artifact;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;
public class RDFEncoder   {
	private Document xmlDoc;
	private static Namespace rdfNS=Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	/**
	 * Constructor with XML string
	 * @param xmlSource String of source XML
	 */
	public RDFEncoder (String xmlSource)
	{
		StringReader stringReader=new StringReader(xmlSource);
		InputSource xmlIn =new InputSource(stringReader);
		SAXBuilder builder = new SAXBuilder();
		try {
			xmlDoc=builder.build(xmlIn);
			formatElement(xmlDoc.getRootElement());
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void formatElement(Element element)
	{
		for (int i=element.getChildren().size();i>0;i--) 
		{
			Element castChild=(Element)element.getChildren().get(i-1);
			boolean isRdfResource=resetAbout(castChild);
				
			if (castChild.getAttributes().size()>0)
			{
				if (!element.isRootElement()&&isRdfResource)
				{	
					element.removeContent(castChild);
					element.setAttribute("resource", castChild.getAttributeValue("about", rdfNS));
					element.getAttribute("resource").setNamespace(rdfNS);
					if (!duplicateResource(castChild))
					xmlDoc.getRootElement().addContent(castChild);
				}
			}
			formatElement(castChild);
		}
	}
	
	private boolean resetAbout(Element element)
	{
		if (element.getAttribute("about")==null)
			return false;
		String rdfAbout=element.getAttributeValue("about")+element.getName()+"&id="+element.getAttributeValue("id");
		element.setAttribute("about", rdfAbout);
		element.getAttribute("about").setNamespace(rdfNS);
		xmlDoc.getRootElement().addNamespaceDeclaration(rdfNS);
		return true;
	}
	
	/**
	 * Check if the same resource has already existed
	 * @param element
	 * @return
	 */
	private boolean duplicateResource(Element element)
	{
		for (Object child:xmlDoc.getRootElement().getChildren(element.getName(), element.getNamespace()))
		{
			Element existing= (Element)child;
			if (hasSameStructureAndInfor(element, existing))
				return true;
		}
		return false;
	}
	
	/**
	 * Determine if two Elements have the same content
	 * @param element
	 * @param toCompare
	 * @return
	 */
	private boolean hasSameStructureAndInfor(Element element, Element toCompare)
	{
		if(toCompare.getAttributes().size()!=element.getAttributes().size())
			return false;
		if (toCompare.getChildren().size()!=element.getChildren().size())
			return false;
		
		//compare attribute
		for(Object attrObj:element.getAttributes())
		{
			Attribute attr=(Attribute)attrObj;
			if (toCompare.getAttributeValue(attr.getName(), attr.getNamespace())==null)
				return false;
			else if (!toCompare.getAttributeValue(attr.getName(), attr.getNamespace()).equals(attr.getValue()))
				return false;
		}
		return true;
	}
	/**
	 * Export formated XML document
	 * @return xmlString formated  XML document
	 */
	public String getFormatedRDF()
	{
		StringWriter stringWriter=new StringWriter();
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
	    try {
			outputter.output(xmlDoc, stringWriter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stringWriter.toString();
	}
}
