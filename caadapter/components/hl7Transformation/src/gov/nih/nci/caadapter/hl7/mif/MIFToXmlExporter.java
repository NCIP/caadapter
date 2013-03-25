/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.hl7.mif;

import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.TreeSet;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import gov.nih.nci.caadapter.common.Log;
/**
 * Export a MIF/HL7 specification to an xml file
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: phadkes $
 * @version $Revision: 1.5 $
 * @date $Date: 2008-06-09 19:53:50 $
 * @since caAdapter v4.0
 */
public class MIFToXmlExporter {

	public static ArrayList<String> methodToBeIngored=new ArrayList<String>();
	private Document mifDoc;

	/**
	 * Add the methods to be ingnored
	 */
	 static {

		 methodToBeIngored.add("toString");
         methodToBeIngored.add("getNodeXmlName");
         methodToBeIngored.add("getXmlPath");
         methodToBeIngored.add("findCardinality");
         methodToBeIngored.add("findTypeProperty");

         methodToBeIngored.add("getMaxAssociationMultiplicityWithName");
         methodToBeIngored.add("getMaxAttributeMultiplicityWithName");
         methodToBeIngored.add("findIsAbstract");
         methodToBeIngored.add("findIsMultiple");
         methodToBeIngored.add("findIsRerence");

	}
	/**
	 * Constructor with a MIFClass object for exporting
	 * @param tbExported
	 */
	public MIFToXmlExporter (MIFClass tbExported) throws Exception
	{
		mifDoc=new Document();
		Element rootElm=buildMIFClassElement (tbExported);
		rootElm.setName(tbExported.getName());
		mifDoc.setRootElement(rootElm);
	}

	/**
	 * Exoport the given MIFClass object into a XML file
	 * @param fileName -- name of the exported XML file
	 */
	public void exportToFile(String fileName) throws Exception
	{
		XMLOutputter outputter =new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		if (fileName!=null&&!fileName.equals(""))
		{
			File outFile=new File(fileName);
			if (outFile==null)
				outFile=new File(System.getProperty("user.dir")+File.separator+fileName);
			try {
				FileWriter fw = new FileWriter(outFile);
				outputter.output(mifDoc, fw);
				fw.close();
			} catch (IOException e) {
				throw new Exception(e.getMessage());
			}
		}
		else
		{
			try {
				outputter.output(mifDoc, System.out);
			} catch (java.io.IOException e) {
				throw new Exception(e.getMessage());
			}
		}
	}
	/**
	 * Build an XML element with a MIFClass object
	 * @param tbBuilt
	 * @return xmlElement
	 */
	private Element buildMIFClassElement(MIFClass tbBuilt) throws Exception
	{
		if (tbBuilt==null)
			return new Element("No_MIFClass_being_defined");
		Element rtnElm=new Element("class");
		setPrimaryAttributes(tbBuilt, rtnElm);
		//set packageLocation
		Hashtable<String, String> packageLocation=tbBuilt.getPackageLocation();
		if (packageLocation!=null)
		{
			Element packageLocationElm=new Element("packageLocation");
			Enumeration pKeys=packageLocation.keys();
			while ( pKeys.hasMoreElements())
			{
				String packageAttrName=(String)pKeys.nextElement();
				String packageAttrValue=(String)packageLocation.get(packageAttrName);
				if (packageAttrName.indexOf("xmlns")>-1)
				{
					packageLocationElm.setNamespace(Namespace.getNamespace("mif",packageAttrValue));

				}
				else
					packageLocationElm.setAttribute(packageAttrName,packageAttrValue);
			System.out.println("MIFToXmlExporter.buildMIFClassElement()..add attribute:"+ packageAttrName+"="+packageLocation.get(packageAttrName));
			}

			rtnElm.addContent(packageLocationElm);
		}
		//add MIFAttibute
		TreeSet<MIFAttribute> attrSet=tbBuilt.getSortedAttributes();
		for(MIFAttribute mifAttr:attrSet)
		{
			rtnElm.addContent(buildMIFAttribute(mifAttr));
		}
		//add MIFAssociation
		TreeSet<MIFAssociation> asscSet=tbBuilt.getSortedAssociations();
		for(MIFAssociation assc:asscSet)
		{
			Element asscElm=buildAssociationElement(assc);
			MIFClass asscMifClass=assc.getMifClass();
			if (asscMifClass!=null)
			{
				asscElm.addContent(buildMIFClassElement(asscMifClass));
			}
			rtnElm.addContent(asscElm);
		}
//		add Choice
		TreeSet<MIFClass> choiceSet=tbBuilt.getSortedChoices();
		Element choiceElm=new Element("choice");
		for(MIFClass choice:choiceSet)
		{
			choiceElm.addContent(buildMIFClassElement(choice));
		}

		//add the choic elemnt only if necessary
		if (choiceElm.getChildren().size()>0)
			rtnElm.addContent(choiceElm);
		return rtnElm;
	}

	/**
	 * Build an XML element with a MIFAssociation object
	 * @param tbBuilt
	 * @return xmlElement
	 */
	private Element buildAssociationElement(MIFAssociation tbBuilt) throws Exception
	{
		if (tbBuilt==null)
			return new Element("No_MIFAssociation_being_defined");
		Element rtnElm=new Element("association");
		setPrimaryAttributes(tbBuilt, rtnElm);
		if (tbBuilt.getParticipantTraversalNames()!=null)
		{
			Hashtable traversalNameHash=tbBuilt.getParticipantTraversalNames();
			Enumeration nameKeys=traversalNameHash.keys();
			while (nameKeys.hasMoreElements())
			{
				Element traversalEl=new Element("participantClassSpecialization");
				String nameKey=(String)nameKeys.nextElement();
				String nameValue=(String)traversalNameHash.get(nameKey);
				addAttibuteToElement(traversalEl, "className",nameKey);
				addAttibuteToElement(traversalEl, "traversalName",nameValue);
				rtnElm.addContent(traversalEl);
			}
		}
		return rtnElm;
	}

	/**
	 * Build an XML element with a MIFAttribute object
	 * @param tbBuilt
	 * @return xmlElement
	 */
	private Element buildMIFAttribute(MIFAttribute tbBuilt)throws Exception
	{
		Element rtnElm=new Element("attribute");
		setPrimaryAttributes(tbBuilt, rtnElm);
		Datatype dt=tbBuilt.getDatatype();
		Element dtElm=null;
		if (dt!=null)
		{
			dtElm=buildDatatype(dt);
			rtnElm.addContent(dtElm);
			//there is a concrete data type for
			//the MIFAttribute with Abstract type
			if (tbBuilt.getConcreteDatatype()!=null)
			{
				dtElm.addContent(buildDatatype(tbBuilt.getConcreteDatatype()));
			}
		}
		else
			Log.logInfo(this,"Datatype is not defined for MIFAttribute:"+tbBuilt.getXmlPath());

		return rtnElm;
	}
	/**
	 * Build an XML element with a Datatype object
	 * @param tbBuilt
	 * @return xmlElement
	 */
	private Element buildDatatype(Datatype tbBuilt) throws Exception
	{
		Element rtnElm=new Element("type");
		setPrimaryAttributes(tbBuilt, rtnElm);
		//set predefined value
		HashSet prfValues=tbBuilt.getPredefinedValues();
		for (Object value:prfValues)
		{
			Element prfValueElm=new Element("predefinedValue");
			prfValueElm.addContent(value.toString());
			rtnElm.addContent(prfValueElm);
		}

		Enumeration attrEnum= tbBuilt.getAttributes().keys();
		while (attrEnum.hasMoreElements())
		{
			   String attributeName = (String)attrEnum.nextElement();
			   Attribute attr = (Attribute)tbBuilt.getAttributes().get(attributeName);
			   rtnElm.addContent(buildDatatypeAttribute(attr));
		}
		return rtnElm;
	}

	private Element buildDatatypeAttribute(Attribute tbBuilt) throws Exception
	{
		Element rtnElm=new Element("dataField");
		setPrimaryAttributes(tbBuilt, rtnElm);
		//set referenced datatype

		Datatype refDatatype=tbBuilt.getReferenceDatatype();
		if (refDatatype!=null)
			rtnElm.addContent(buildDatatype(refDatatype));
		return rtnElm;
	}

/**
 * Parser the properties of an object and set them as the attributes
 * the target XML element using java.reflection
 * @param o
 * @param elmt
 */
	private void setPrimaryAttributes(Object o, Element elmt)
	{
	    try {
            Class c = o.getClass();
            Method m[] = c.getDeclaredMethods();
            for (int i = 0; i < m.length; i++)
            {
            	String mthdName=m[i].getName();
            	String mthRtnType=m[i].getReturnType().getSimpleName();
            	String xmlAttrName="";
            	Method invlkMthd=null;
            	if (methodToBeIngored.contains(mthdName))
            		continue;
            	//build attributeName
            	if (mthRtnType.equals("String")||mthRtnType.equals("int"))
            	{   //ognore toString() method
            		xmlAttrName=mthdName.substring(3,4).toLowerCase()+ mthdName.substring(4); //remove "get"
            	}
            	else if (mthRtnType.equals("boolean"))
            	{
            		xmlAttrName=mthdName;
            	}

            	if (xmlAttrName.equals(""))
            		continue;

	            try{
	            	invlkMthd=c.getMethod(mthdName,new Class[0]);
	            	Object valueObj=invlkMthd.invoke(o,new Object[0]);
	            	addAttibuteToElement(elmt,xmlAttrName,valueObj);
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
	/**
	 * Add an attribute to the target XML elment
	 * @param elm
	 * @param attrName
	 * @param attrValue
	 * @throws Exception
	 */
	private void addAttibuteToElement(Element elm, String attrName, Object attrValue) throws Exception
	{
		if (attrValue==null)
			return;
		if (attrValue instanceof Boolean)
		{
			Boolean booleanObj=(Boolean)attrValue;
			if (booleanObj.booleanValue())
				elm.setAttribute(attrName, attrValue.toString());
		}
		else
			elm.setAttribute(attrName, attrValue.toString());
	}
}
