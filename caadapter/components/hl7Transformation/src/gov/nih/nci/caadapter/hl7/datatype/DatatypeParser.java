/**
 * <!-- LICENSE_TEXT_START -->
  * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.datatype;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

//import dom.ParserWrapper;

/**
 * The class load HL7 datatypes into Datatype object.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2007-07-03 18:25:22 $
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

	public void populateDatatypes() {
		Hashtable datatypes_check = new Hashtable();
		Stack<String> processingStack = new Stack(); 
		
		Iterator datatypeCheckIt = datatypes.keySet().iterator();
		while (datatypeCheckIt.hasNext()) {
			String datatypeString = (String)datatypeCheckIt.next();
			Datatype datattype = (Datatype)datatypes.get(datatypeString);
			if (datattype.isSimple())datatypes_check.put(datatypeString, COMPLETE);
			else {
				if (datatypeString.equals("ANY")) {
					datatypes_check.put(datatypeString, COMPLETE);
				}
				else {
					datatypes_check.put(datatypeString, NONCOMPLETE);
				}
			}
		}
		
		Iterator datatypeIt = datatypes.keySet().iterator();
		while (datatypeIt.hasNext()) {
			String datatypeString = (String)datatypeIt.next();
			Datatype datatype = (Datatype)datatypes.get(datatypeString);
			if ((Integer)datatypes_check.get(datatypeString) == COMPLETE) continue;
			
			processingStack.push(datatypeString);
			
			while (processingStack.size() > 0) {
				String currentDatatypeString = processingStack.pop();
				Datatype currentDatatype = (Datatype)datatypes.get(currentDatatypeString);
				String parentDatatypeString = currentDatatype.getParents();
				System.out.println("current datatype = " + currentDatatypeString + "   Parent datatypd = "+ parentDatatypeString);
				if ((Integer)datatypes_check.get(parentDatatypeString) == NONCOMPLETE) {
					processingStack.push(currentDatatypeString);
					processingStack.push(parentDatatypeString);
					continue;
				}
				
				Datatype parentDatatype = (Datatype)datatypes.get(parentDatatypeString);
				
				Hashtable pAttributes = parentDatatype.getAttributes();
				Hashtable attributes = currentDatatype.getAttributes();
				
				Iterator pAttrIt = pAttributes.keySet().iterator();
				while (pAttrIt.hasNext()) {
					Attribute pAttribute = (Attribute)pAttributes.get(pAttrIt.next());
					if (attributes.get(pAttribute.getName()) == null) {
						currentDatatype.addAttribute(pAttribute.getName(), pAttribute);
					}
				}
				
				datatypes_check.put(currentDatatypeString,COMPLETE);
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
	       }
	    }

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
    
  		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
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
		datatypeParser.printDatatypes(false, true);
		datatypeParser.saveDatatypes("c:/temp/datatypes");
		
/*		DatatypeParser datatypeParser = new DatatypeParser();
		datatypeParser.loadDatatypes();
		datatypeParser.printDatatypes(false, true);
		*/
    }
}
