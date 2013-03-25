/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.hl7.datatype;

import gov.nih.nci.caadapter.hl7.mif.MIFUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


/**
 * The class load HL7 datatypes into Datatype object.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.14 $
 *          date        $Date: 2008-06-09 19:53:50 $
 */

public class DatatypeParser {
    private String prefix; //prefix for each element
    private Hashtable datatypes = new Hashtable();
    private final int COMPLETE = 1;
    private final int NONCOMPLETE = 0;
    private Hashtable<String, List<String>> datatypeSubclass=new Hashtable<String, List<String>>();
    public Hashtable getDatatypes() {
    	return datatypes;
    }
	public boolean parse(Node node) {

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
	public void populateUnion(Datatype datatype) {
		if (datatype.getUnions()!= null)
		{
//			System.out.println("Unions for:"+datatype.getName());
			String unions = datatype.getUnions();
			StringTokenizer st = new StringTokenizer(unions);
			while (st.hasMoreTokens()) {
			   String union = st.nextToken();
			   Datatype uDT = ((Datatype)datatypes.get(union));
			   if (uDT!=null)
			   {

				   if (uDT.getPatterns().size()>0) {
					   for(String p:uDT.getPatterns()) {
						   datatype.addPattern(p);
					   }
				   }

				   for (String enumValue:(HashSet<String>)getEnums(union))
				   {
					   datatype.addPredefinedValue(enumValue);
				   }
			   }
			}
		}

	}
	public void populateDatatypes()
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
						System.out.println(((Datatype)datatypes.get("ActClassDocument")).getPredefinedValues()+currentDatatypeString);

					}
				}
				else
				{
					String parentDatatypeString = currentDatatype.getParents();
					System.out.println("current datatype = " + currentDatatypeString + "   Parent datatypd = "+ parentDatatypeString);

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
//					while (pAttrIt.hasNext()) {
//						Attribute pAttribute = (Attribute)pAttrIt.next();
//						if (attributes.get(pAttribute.getName()) == null) {
////							currentDatatype.addAttribute(pAttribute.getName(), pAttribute);
//							MIFUtil.addDatatypeAttributeOnTop(currentDatatype, (Attribute)pAttribute.clone());
//						}
//					}

					datatypes_check.put(currentDatatypeString,COMPLETE);
				}
			}
		}
	}

	public void printDatatypes(boolean isSimple, boolean isComplex) {
		System.out.println("Size"+datatypes.size());
	    Vector v = new Vector(datatypes.keySet());
	    Collections.sort(v);
	    Iterator it = v.iterator();
	    while (it.hasNext()) {
	       String datatypeName =  (String)it.next();
	       Datatype datatype = (Datatype)datatypes.get(datatypeName);
	       if ((datatype.isSimple() && isSimple) || (!datatype.isSimple() && isComplex)) {
	    	   System.out.println("\nDatatype Name: " + datatypeName + "    Parent Type Name: " + datatype.getParents() + " Abstract?" + datatype.isAbstract());
	    	   System.out.println("         type: " + ((datatype.isSimple()) ? "Simple" : "Complex"));
	    	   Vector a = new Vector(datatype.getAttributes().keySet());
	    	   Collections.sort(a);
	    	   Iterator attriIt = a.iterator();
	    	   while (attriIt.hasNext()) {
	    		   String attributeName = (String)attriIt.next();
	    		   Attribute attr = (Attribute)datatype.getAttributes().get(attributeName);
	    		   if (attr.isValid())
	    			   System.out.format("%-30s,%s","    attribute: " + attr.getName(), "type = " + attr.getType() + "\n");
	    	   }
	    	   for(String p:datatype.getPatterns()) {
		    	   System.out.println("      Pattern: " + p);
	    	   }

	    	   for(String p:(HashSet<String>)datatype.getPredefinedValues()) {
		    	   System.out.println("      PredefinedValue: '" + p + "'");
	    	   }
	       }
	    }

	}

	private HashSet getEnums(String datatypeString) {
		HashSet enums = new HashSet();
		Datatype datatype = (Datatype)datatypes.get(datatypeString);
		if (datatype == null) return enums;
		return datatype.getPredefinedValues();
	}
	/*
	 * This method will serialize the datatype objects into a file.
	 *
	 * @param filenNme the name of the file to save all the info
	 *
	 */

	public void saveDatatypes(String fileName) throws Exception {
		OutputStream os = new FileOutputStream(fileName);
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(datatypes);
		oos.close();
		os.close();
	}

	public void loadDatatypes() {
		try {
			InputStream is = this.getClass().getResourceAsStream("/datatypes");
			ObjectInputStream ois = new ObjectInputStream(is);
			datatypes = (Hashtable)ois.readObject();
			populateSubclasses();
			ois.close();
			is.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//  		DocumentBuilder db;
//		try {
//			db = dbf.newDocumentBuilder();
//			InputStream isVoc =getClass().getResourceAsStream("/schemas/coreschemas/voc.xsd");
//			InputStream isDatatypeBase =getClass().getResourceAsStream("/schemas/coreschemas/datatypes-base.xsd");
//			InputStream isDatatype =getClass().getResourceAsStream("/schemas/coreschemas/datatypes.xsd");
//
//			Document vocDoc = db.parse(isVoc);
//			Document baseDoc = db.parse(isDatatypeBase);
//			Document allDoc = db.parse(isDatatype);
//			handleGTS();
//			parse(vocDoc);
//			parse(baseDoc);
//			parse(allDoc);
//			populateDatatypes();
//			populateSubclasses();
//
//		} catch (ParserConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public List<String> findSubclassList(String typeName)
	{
		return datatypeSubclass.get(typeName);
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

	public void handleGTS() {
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
