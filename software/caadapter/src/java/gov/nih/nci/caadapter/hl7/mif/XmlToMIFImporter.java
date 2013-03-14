/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.hl7.mif;

import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

/**
 *Import a MIF/HL7 specification from an xml file
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.10 $
 * @date $Date: 2008-09-29 15:44:40 $
 * @since caAdapter v4.0
 */

public class XmlToMIFImporter {
	private SAXBuilder builder;
	public XmlToMIFImporter()
	{
		builder = new SAXBuilder(false);
	}

	public MIFClass importMifFromXml(File f)
	{
		MIFClass rtnMif=null;
		Document document;
		try {
			document = builder.build(new File(f.getAbsolutePath()));
			Element root = document.getRootElement();
			rtnMif=parserMIFClass(root);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rtnMif;
	}

	private MIFClass parserMIFClass(Element elm)
	{
		MIFClass rtnMif=new MIFClass();
		setPrimaryAttributes(rtnMif, elm);
		//set package location
		Element packageLoc=null;
		Namespace hl7Namespace=Namespace.getNamespace("mif","urn:hl7-org:v3/mif");
		packageLoc=elm.getChild("packageLocation",hl7Namespace);

		if (packageLoc!=null &&!packageLoc.getAttributes().isEmpty())
		{
			Hashtable<String, String> packageLocation=new Hashtable<String, String>();
			if (packageLoc.getNamespacePrefix()!=null
					&&!packageLoc.getNamespacePrefix().equals(""))
				packageLocation.put("xmlns:"+packageLoc.getNamespacePrefix(),packageLoc.getNamespaceURI());
			List packageAttrs=packageLoc.getAttributes();
			for(Object packAttr:packageAttrs)
			{
				org.jdom.Attribute jdomAttr=(org.jdom.Attribute)packAttr;
				packageLocation.put(jdomAttr.getName(), jdomAttr.getValue());
			}
			rtnMif.setPackageLocation(packageLocation);
		}
		List attrList=elm.getChildren("attribute");
		if (!attrList.isEmpty())
		{
			for(Object oneElmnt: attrList)
			{
				MIFAttribute childAttr=parserMIFAttribute((Element)oneElmnt);
				rtnMif.addAttribute(childAttr);
			}
		}
		List asscList=elm.getChildren("association");
		if (!asscList.isEmpty())
		{
			for(Object oneElmnt: asscList)
			{
				MIFAssociation childAssc=parserMIFAssociation((Element)oneElmnt);
				rtnMif.addAssociation(childAssc);
			}
		}
		Element choiceElm=elm.getChild("choice");
		if (choiceElm!=null)
		{
			List choiceList=choiceElm.getChildren("class");
			if (!choiceList.isEmpty())
			{
				for(Object oneElmnt: choiceList)
				{
					MIFClass childChoiceClass=parserMIFClass((Element)oneElmnt);
					rtnMif.addChoice(childChoiceClass);
				}
			}
		}
		return rtnMif;
	}

	private MIFAssociation parserMIFAssociation(Element elm)
	{
		MIFAssociation rtnMif=new MIFAssociation();
		setPrimaryAttributes(rtnMif, elm);
		Element mifClassElm=elm.getChild("class");
		if (mifClassElm!=null)
			rtnMif.setMifClass(parserMIFClass(mifClassElm));
		List participantList=elm.getChildren("participantClassSpecialization");
		if (!participantList.isEmpty())
		{
			Hashtable<String, String> participantHash=new Hashtable<String, String> ();
			for (Object partEl:participantList)
			{
				participantHash.put(
						((Element)partEl).getAttribute("className").getValue(),
						((Element)partEl).getAttribute("className").getValue()
				);

			}
			rtnMif.setParticipantTraversalNames(participantHash);
		}
		return rtnMif;
	}

	private MIFAttribute parserMIFAttribute(Element elm)
	{
		MIFAttribute rtnMif=new MIFAttribute();
		setPrimaryAttributes(rtnMif, elm);
		Element typeElm=elm.getChild("type");
		if (typeElm!=null)
		{
			rtnMif.setDatatype(parserDatatype(typeElm));
			//there is an element "type" within "type"
			//if the MIFAttribute has datatyp of Abstract
			Element concreteTypeElm=typeElm.getChild("type");
			if (concreteTypeElm!=null)
				rtnMif.setConcreteDatatype(parserDatatype(concreteTypeElm));
		}
		return rtnMif;
	}
	private Datatype parserDatatype(Element elm)
	{
		Datatype rtnDt=new Datatype();
		setPrimaryAttributes(rtnDt, elm);
		List attrList=elm.getChildren("dataField");
		if (!attrList.isEmpty())
		{
			for(Object oneElmnt:attrList)
			{
				Attribute childAttr=parserDatatypeAttribute((Element)oneElmnt);
//				rtnDt.addAttribute(childAttr.getName(),childAttr);
//				rtnDt.addAttribute(childAttr.getNodeXmlName(),childAttr);
				rtnDt.getAttributes().put(childAttr.getNodeXmlName(),childAttr);
			}
		}
		return rtnDt;
	}

	private Attribute parserDatatypeAttribute(Element elm)
	{
		Attribute rtnDtAttr=new Attribute();
		setPrimaryAttributes(rtnDtAttr, elm);
		Element typeElm=elm.getChild("type");
		if (typeElm!=null)
		{
			rtnDtAttr.setReferenceDatatype(parserDatatype(typeElm));
			rtnDtAttr.setSimple(false);
		}
		return rtnDtAttr;
	}

	private void setPrimaryAttributes(Object o, Element elmt)
	{
		Class[] paramTypes = new Class[1];
	    try {
            Class c = o.getClass();
            Method m[] = c.getDeclaredMethods();
            for (int i = 0; i < m.length; i++)
            {
            	String mthdName=m[i].getName();
            	//not exported, no value to import
            	if (MIFToXmlExporter.methodToBeIngored.contains(mthdName))
            		continue;

            	String mthRtnType=m[i].getReturnType().getSimpleName();
            	String invlkMthdName="";
            	String xmlAttrName="";
            	Method invlkMthd=null;
            	if (mthRtnType.equals("String"))
            	{
            		invlkMthdName="s"+mthdName.substring(1);//replace "getXX" as "setXX"
            		paramTypes[0] = String.class;
            		xmlAttrName=mthdName.substring(3,4).toLowerCase()+ mthdName.substring(4); //remove "get"

            	}
            	else if (mthRtnType.equals("boolean"))
            	{
            		invlkMthdName="set"+mthdName.substring(2);//replace "isXX" as "setXX"
            		paramTypes[0] = boolean.class;///String.class;
            		xmlAttrName=mthdName;
            	}
            	else if (mthRtnType.equals("int"))
            	{
            		invlkMthdName="set"+mthdName.substring(3);//replace "getXX" as "setXX"
            		paramTypes[0] = int.class;///String.class;
            		xmlAttrName=mthdName.substring(3,4).toLowerCase()+ mthdName.substring(4); //remove "get"
              	}
            	if (xmlAttrName.equals(""))
            		continue;

            		org.jdom.Attribute attrElmt=elmt.getAttribute(xmlAttrName);
            		String xmlAttrValue="";
            		if (attrElmt!=null)
	            	{
            			xmlAttrValue=attrElmt.getValue();
	            	}
    	            try{
    	            	invlkMthd=c.getMethod(invlkMthdName,paramTypes );

    	            	if (mthRtnType.equals("String"))
    	            	{
    	            		invlkMthd.invoke(o,xmlAttrValue);
    	            	}
    	            	else if (mthRtnType.equals("boolean"))
    	            	{
    	            		//if the elment is not found or value is not "true"
    	            		//use "false" as default
    	            		boolean bvToSet=false;
     	            		if (xmlAttrValue.equalsIgnoreCase("true"))
    	            			bvToSet=true;

    	            		invlkMthd.invoke(o,bvToSet);
     	            	}
    	            	else if (mthRtnType.equals("int"))
    	            	{
    	            		invlkMthd.invoke(o,Integer.valueOf(xmlAttrValue));
    	            	}
    	            }
                	catch (NoSuchMethodException nm)
                	{
//		                		nm.printStackTrace();
                	}
            	}
         }
         catch (Throwable e) {
            System.err.println(e);
         }

	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 */