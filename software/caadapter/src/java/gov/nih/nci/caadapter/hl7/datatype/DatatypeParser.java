/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.datatype;

import gov.nih.nci.caadapter.hl7.mif.MIFUtil;
import gov.nih.nci.caadapter.hl7.mif.NormativeVersionUtil;
import gov.nih.nci.caadapter.common.util.FileUtil;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


/**
 * The class load HL7 datatypes into Datatype object.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.22 $
 *          date        $Date: 2009-04-06 15:06:09 $
 */

public class DatatypeParser {
    private String prefix; //prefix for each element
    private Hashtable<String, Datatype> datatypes = new Hashtable<String, Datatype>();
    private final int COMPLETE = 1;
    private final int NONCOMPLETE = 0;
    private Hashtable<String, List<String>> datatypeSubclass=new Hashtable<String, List<String>>();
    private boolean dataLoaded=false;
    public Hashtable<String, Datatype> getDatatypes()
    {
    	return datatypes;
    }
	//
	//	private HashSet getEnums(String datatypeString) {
	//		HashSet enums = new HashSet();
	//		Datatype datatype = (Datatype)datatypes.get(datatypeString);
	//		if (datatype == null) return enums;
	//		return datatype.getPredefinedValues();
	//	}
	//
		public void loadDatatypes() {
			if(!dataLoaded)
			{
				loadDatatypeRawdata();
				dataLoaded=true;
			}
		}
	public List<String> findSubclassList(String typeName)
	{
		return datatypeSubclass.get(typeName);
	}
	/**
	 * Load datatype data from original HL7 schema data
	 */
	private void loadDatatypeRawdata()
	{
		System.out.println("DatatypeParser.loadDatatypeRawdata()");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
	        Document vocDoc = null;
			Document baseDoc = null;
			Document allDoc = null;
			String schemaFilePath=NormativeVersionUtil.getCurrentMIFIndex().getSchemaPath();
	        File schemaFile = new File(schemaFilePath);
	        String coreSchemaPrefix="coreschemas";
	        if(!schemaFile.exists())
	        {
	        	//webStart deployment, the schemas.zip file INVISIBLE
	        	System.err.println("Webstart deployment: " + schemaFilePath);
	        	String specHome=NormativeVersionUtil.getCurrentMIFIndex().findSpecificationHome();
	        	String coreXsdPath=specHome+"/"+coreSchemaPrefix;
	        	String nameVoc=coreXsdPath+"/voc.xsd";

	            URL urlVoc=FileUtil.retrieveResourceURL(nameVoc);
	        	String nameDatatypeBase=coreXsdPath+"/datatypes-base.xsd";
	        	URL urlDtBase=FileUtil.retrieveResourceURL(nameDatatypeBase);
	        	String nameDatatype=coreXsdPath+"/datatypes.xsd";
	        	URL urlDt=FileUtil.retrieveResourceURL(nameDatatype);

	        	vocDoc = db.parse(urlVoc.openStream());
	            baseDoc = db.parse(urlDtBase.openStream());
	            allDoc = db.parse(urlDt.openStream());

	        }
	        else
	    	{
	        	//standalone deployment, the schemas.zip file is VISIBLE
	    		ZipFile xsdZipFile=new ZipFile(schemaFile);
	    		if (xsdZipFile!=null)
	    		{
                    ZipEntry vocEntry=xsdZipFile.getEntry(coreSchemaPrefix+"/voc.xsd");
                    if (vocEntry==null) vocEntry=xsdZipFile.getEntry("schemas/" + coreSchemaPrefix+"/voc.xsd");
                    ZipEntry baseEntry=xsdZipFile.getEntry(coreSchemaPrefix+"/datatypes-base.xsd");
                    if (baseEntry==null) baseEntry=xsdZipFile.getEntry("schemas/" + coreSchemaPrefix+"/datatypes-base.xsd");
                    ZipEntry allEntry=xsdZipFile.getEntry(coreSchemaPrefix+"/datatypes.xsd");
                    if (allEntry==null) allEntry=xsdZipFile.getEntry("schemas/" + coreSchemaPrefix+"/datatypes.xsd");

	        		vocDoc = db.parse(xsdZipFile.getInputStream(vocEntry));
	            	baseDoc= db.parse(xsdZipFile.getInputStream(baseEntry));
	            	allDoc = db.parse(xsdZipFile.getInputStream(allEntry));
	        	}
	    		else
	    			System.out.println("DatatypeParser.loadDatatypeRawdata()...failed load datatype type");
	    	}

			handleGTS();
			parse(vocDoc);
			parse(baseDoc);
			parse(allDoc);
			populateDatatypes();
			populateSubclasses();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private boolean parse(Node node)
	{
        if (node == null) {
            return true;
        }
        short type = node.getNodeType();
        switch (type) {
        	case Node.DOCUMENT_NODE: {
                Document document = (Document)node;
                String schemaString = document.getDocumentElement().getNodeName();
                if (!schemaString.endsWith("schema")) return false;
                if (schemaString.equals("schema")) prefix = "";
                else if (schemaString.endsWith(":schema")) {
                	prefix = schemaString.substring(0,schemaString.lastIndexOf(":schema")+1);
                }
                parse(document.getDocumentElement());
                break;
            }
            case Node.ELEMENT_NODE: {
            	if (node.getNodeName().equals(prefix + "schema")) {
                    Node child = node.getFirstChild();
                    while (child != null) {
                        parse(child);
                        child = child.getNextSibling();
                    }
            	}
            	if (node.getNodeName().equals(prefix + "simpleType")) {
            		Datatype datatype = SimpleTypeParser.parseSimple(node,prefix);
            		datatypes.put(datatype.getName(), datatype);
            	}
            	if (node.getNodeName().equals(prefix + "complexType")) {
            		Datatype datatype = ComplexTypeParser.parseComplex(node, prefix);
            		datatypes.put(datatype.getName(), datatype);
            	}
            	break;
            }
        }
        return true;
	}
//	public void populateUnion(Datatype datatype) {
//		if (datatype.getUnions()!= null)
//		{
////			System.out.println("Unions for:"+datatype.getName());
//			String unions = datatype.getUnions();
//			StringTokenizer st = new StringTokenizer(unions);
//			while (st.hasMoreTokens()) {
//			   String union = st.nextToken();
//			   Datatype uDT = ((Datatype)datatypes.get(union));
//			   if (uDT!=null)
//			   {
//
//				   if (uDT.getPatterns().size()>0) {
//					   for(String p:uDT.getPatterns()) {
//						   datatype.addPattern(p);
//					   }
//				   }
//
//				   for (String enumValue:(HashSet<String>)getEnums(union))
//				   {
//					   datatype.addPredefinedValue(enumValue);
//				   }
//			   }
//			}
//		}
//
//	}
	private void populateDatatypes()
    {
		Hashtable datatypes_check = new Hashtable();
		Stack<String> processingStack = new Stack();

		Iterator datatypeCheckIt = datatypes.keySet().iterator();
		while (datatypeCheckIt.hasNext())
        {
			String datatypeString = (String)datatypeCheckIt.next();
			Datatype datatype = (Datatype)datatypes.get(datatypeString);
			if (datatype.isSimple())
            {
				//Process patterns

//				System.out.println("Unions for:"+datatype.getName()+ " -- unions"+ datatype.getUnions());
				if (datatype.getUnions()!= null)
				{
//					System.out.println("Unions for:"+datatype.getName());
					datatypes_check.put(datatypeString, NONCOMPLETE);
				}
				else
                {
					datatypes_check.put(datatypeString, COMPLETE);
				}

			}
			else
            {
				if (datatypeString.equals("ANY"))
                {
					datatypes_check.put(datatypeString, COMPLETE);
				}
				else
                {
					datatypes_check.put(datatypeString, NONCOMPLETE);
				}
			}
		}

		Iterator datatypeIt = datatypes.keySet().iterator();
		while (datatypeIt.hasNext())
        {
			String datatypeString = (String)datatypeIt.next();
			Datatype datatype = (Datatype)datatypes.get(datatypeString);
			if ((Integer)datatypes_check.get(datatypeString) == COMPLETE) continue;

			processingStack.push(datatypeString);

			while (processingStack.size() > 0)
            {
				boolean restart = false;
				String currentDatatypeString = processingStack.pop();
				Datatype currentDatatype = (Datatype)datatypes.get(currentDatatypeString);
				if (currentDatatype.isSimple())
				{
					String unions = currentDatatype.getUnions();
					StringTokenizer st = new StringTokenizer(unions);
					while (st.hasMoreTokens())
                    {
					   String union = st.nextToken();
					   Datatype uDT = ((Datatype)datatypes.get(union));
					   if (uDT!=null)
					   {
						   if ((Integer)datatypes_check.get(union) == NONCOMPLETE)
						   {
								processingStack.push(currentDatatypeString);
								processingStack.push(union);
								restart = true;
								break;
						   }
						   if (uDT.getPatterns().size()>0)
                           {
							   for(String p:uDT.getPatterns())
                               {
								   currentDatatype.addPattern(p);
							   }
						   }

						   for (String enumValue:(HashSet<String>)uDT.getPredefinedValues())
						   {
							   currentDatatype.addPredefinedValue(enumValue);
						   }
					   }
					}
					if (restart)
					{
						restart=false;
						continue;
					}
					else {
						datatypes_check.put(currentDatatypeString,COMPLETE);
//						System.out.println(((Datatype)datatypes.get("ActClassDocument")).getPredefinedValues()+currentDatatypeString);

					}
				}
				else
				{
					String parentDatatypeString = currentDatatype.getParents();
//					System.out.println("current datatype = " + currentDatatypeString + "   Parent datatypd = "+ parentDatatypeString);

                    if (parentDatatypeString == null) parentDatatypeString = "ANY";
                    // This line was inserted by umkis for protecting null pointer exception when meet 'StrucDoc.Text' data type of CDA.

                    if ((Integer)datatypes_check.get(parentDatatypeString) == NONCOMPLETE)
                    {
						processingStack.push(currentDatatypeString);
						processingStack.push(parentDatatypeString);
						continue;
					}

					Datatype parentDatatype = (Datatype)datatypes.get(parentDatatypeString);

//					Hashtable pAttributes = parentDatatype.getAttributes();
					Hashtable attributes = currentDatatype.getAttributes();
					//retrieve the Attributes of parent datatype and sort them in decending order
					TreeSet pDtAttrSet=MIFUtil.sortDatatypeAttribute(parentDatatype);
			    	Iterator pAttrIt=pDtAttrSet.iterator();
			    	ArrayList <Attribute> pAttrList=new  ArrayList<Attribute>();
			    	while (pAttrIt.hasNext()) {
							Attribute pAttribute = (Attribute)pAttrIt.next();
							pAttrList.add(pAttribute);
			    	}
			    	Comparator decendingOrder = Collections.reverseOrder();
			    	Collections.sort(pAttrList, decendingOrder);
					//Process attributes
			    	for (Attribute onePAttr:pAttrList)
			    	{
						if (attributes.get(onePAttr.getName()) == null) {
							MIFUtil.addDatatypeAttributeOnTop(currentDatatype, (Attribute)onePAttr.clone());
						}
			    	}
					datatypes_check.put(currentDatatypeString,COMPLETE);
				}
			}
		}
	}

	private void populateSubclasses()
	{
		Enumeration dtKeys=datatypes.keys();
		while ( dtKeys.hasMoreElements())
		{
			String dtOneKey=(String)dtKeys.nextElement();
			Datatype dtOneElement=(Datatype)datatypes.get(dtOneKey);
			addDatatypeToSubclassHash(dtOneElement);
		}
	}
	private void addDatatypeToSubclassHash(Datatype dType)
	{
//		System.out.println("DatatypeParser.addDatatypeToSubclassHash()..dataType:"+dType.getName()+"...getParents():"+dType.getParents());
		String parentName=dType.getParents();
		if (parentName==null)
			return;
		if (dType.isAbstract())
			return;

		Datatype parentDatatype=(Datatype)datatypes.get(parentName);
		while (parentDatatype!=null)
		{
			if (parentDatatype.isAbstract())
			{
				List <String>list = datatypeSubclass.get(parentDatatype.getName());
				//add one AbstractSubclassPair
				if (list==null)
				{
					list=new ArrayList <String>();
					datatypeSubclass.put(parentDatatype.getName(), list);
				}
				if (!list.contains(dType.getName()))
					list.add(dType.getName());
			}
			parentName=parentDatatype.getParents();
			if (parentName==null)
				break;
			parentDatatype=(Datatype)datatypes.get(parentName);
		}

	}

	private void handleGTS() {
		Datatype datatype = new Datatype();
		datatype.setName("GTS");
		datatype.setParents("ANY");
		datatype.setSimple(false);
		datatype.setAbstract(true);
		datatypes.put("GTS", datatype);
	}

	public static void main(String argv[]) throws Exception{

/*  		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
  		DocumentBuilder db = dbf.newDocumentBuilder();

		DOMImplementation domImpl = db.getDOMImplementation();

		Document vocDoc = db.parse("T:/YeWu/Edition2006/processable/coreschemas/voc.xsd");
		Document baseDoc = db.parse("T:/YeWu/Edition2006/processable/coreschemas/datatypes-base.xsd");
		Document allDoc = db.parse("T:/YeWu/Edition2006/processable/coreschemas/datatypes.xsd");

		DatatypeParser datatypeParser = new DatatypeParser();
		datatypeParser.handleGTS();
		datatypeParser.parse(vocDoc);
		datatypeParser.parse(baseDoc);
		datatypeParser.parse(allDoc);
		SimpleTypeParser.printMeta();
		ComplexTypeParser.printMeta();
		datatypeParser.populateDatatypes();
		//		datatypeParser.printDatatypes(true, false);
		datatypeParser.printDatatypes(true, false);

		System.out.println(((Datatype)datatypeParser.getDatatypes().get("ActClass")).getPredefinedValues());
	*/
//		datatypeParser.saveDatatypes("C:/temp/serializedMIF/resource/datatypes");
//		datatypeParser.saveDatatypes("C:/temp/datatypes");

		DatatypeParser datatypeParser1 = new DatatypeParser();
		datatypeParser1.loadDatatypes();
		System.out.println(((Datatype)datatypeParser1.getDatatypes().get("ActClass")).getPredefinedValues());
//		datatypeParser1.printDatatypes(true, false);

    }
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.21  2009/03/25 14:01:30  wangeug
 * HISTORY :load HL7 artifacts with new procedure
 * HISTORY :
 * HISTORY :Revision 1.20  2009/03/18 15:49:54  wangeug
 * HISTORY :enable wesstart to support multiple normatives
 * HISTORY :
 * HISTORY :Revision 1.19  2009/03/13 14:50:39  wangeug
 * HISTORY :support multiple HL& normatives
 * HISTORY :
 * HISTORY :Revision 1.18  2009/02/25 15:56:50  wangeug
 * HISTORY :enable webstart
 * HISTORY :
 * HISTORY :Revision 1.17  2008/12/12 22:03:43  umkis
 * HISTORY :use FileUtil.getV3XsdFilePath() at the loadDatatypeRawdata()
 * HISTORY :
 * HISTORY :Revision 1.16  2008/09/29 15:48:56  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */