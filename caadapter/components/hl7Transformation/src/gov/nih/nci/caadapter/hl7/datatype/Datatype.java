/**
 * <!-- LICENSE_TEXT_START -->
  * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.datatype;

/**
 * The class defines HL7 Datatypes.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.5 $
 *          date        $Date: 2007-06-28 13:50:25 $
 */

import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.Enumeration;

public class Datatype extends DatatypeBaseObject implements Serializable, Cloneable {
	static final long serialVersionUID = 2L;

	private Hashtable attributes = new Hashtable();
	private HashSet predefinedValues = new HashSet();
	private String name;
	private boolean simple = false;
	private boolean isAbstract =false;
	private String parents;
	private boolean optionChosen = false;
	private String parentXmlPath;
	private boolean enabled = true;
 
	public String getNodeXmlName() {
		return this.getName();
	}
	
	public String getParentXmlPath() {
		return parentXmlPath;
	}
	public void setParentXmlPath(String parentXmlPath) {
		this.parentXmlPath = parentXmlPath;
	}
	/**
	 * This method will add an attribute object to a given datatype object.
	 * 
	 * @param attributeName is the name of attribute
	 * @param attr is the Attribute object associates with the name of that attribute
	 */

	public void addAttribute(String attributeName, Attribute attr) {
		attributes.put(attributeName, attr);
	}
	/**
	 * @return attributes of a datatype
	 */
	public Hashtable getAttributes() {
		return attributes;
	}
	
	/**
	 * addPredefinedValue method will add a predefined value to the given datatype.
	 * 
	 * @param value is a predefined value of the datatype
	 */
	public void addPredefinedValue(String value) {
		predefinedValues.add(value);
	}
	
	public void setAttributes(Hashtable<String,Attribute> newAttrs)
	{
		attributes=newAttrs;
	}
	
	public HashSet getPredefinedValues() {
		return predefinedValues;
	}
	
	public void setPredefinedValue(HashSet<String> newValues)
	{
		predefinedValues=newValues;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the parents
	 */
	public String getParents() {
		return parents;
	}
	/**
	 * @param parents the parents to set
	 */
	public void setParents(String parents) {
		this.parents = parents;
	}
	/**
	 * @return the simple
	 */
	public boolean isSimple() {
		return simple;
	}
	/**
	 * @param simple the simple to set
	 */
	public void setSimple(boolean simple) {
		this.simple = simple;
	}
	
	public void print(int level) {
	   for(int i=0;i<level;i++) {
		   System.out.print("   ");
	   }
 	   System.out.println("Datatype Name: " + this.getName() + "    Parent Type Name: " +  this.getParents());
	   for(int i=0;i<level;i++) {
		   System.out.print("   ");
	   }
	   System.out.println("         type: " + ((this.isSimple()) ? "Simple" : "Complex"));
	   Vector a = new Vector(this.getAttributes().keySet());
	   Collections.sort(a);
	   Iterator attriIt = a.iterator();
	   while (attriIt.hasNext()) {
		   String attributeName = (String)attriIt.next();
		   Attribute attr = (Attribute)this.getAttributes().get(attributeName);
		   if (attr.isValid()) {
			   for(int i=0;i<level;i++) {
				   System.out.print("   ");
			   }
			   System.out.format("%-30s,%s","    attribute: " + attr.getName(), "type = " + attr.getType() + "\n");
		   }
	   }
		
	}
	/**
	 * @return the isAbstract
	 */
	public boolean isAbstract() {
		return isAbstract;
	}
	/**
	 * @param isAbstract the isAbstract to set
	 */
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	
	public Object clone()
	{
		 try {
			 Datatype clonnedObj = (Datatype)super.clone();
			 //clone Attribute
			 Hashtable  attrHash=getAttributes();
			 Hashtable <String,Attribute> clonnedAttrHash=new Hashtable<String,Attribute>();
			 Enumeration eleEnum=attrHash.elements();
			 while (eleEnum.hasMoreElements())
			 {
				 Attribute oneAttr=(Attribute)eleEnum.nextElement();
				 clonnedAttrHash.put(oneAttr.getName(), (Attribute) oneAttr.clone());
			 }
			 clonnedObj.setAttributes(clonnedAttrHash);
			
			 //	clone PredeinedValue
			 HashSet valueSet=getPredefinedValues();
			 HashSet clonnedValueHash=new HashSet();
			 for (Object oneValue:valueSet)
			 {
				 clonnedValueHash.add(oneValue);
			 }
			 clonnedObj.setPredefinedValue(clonnedValueHash);
             return clonnedObj;
         }
         catch (CloneNotSupportedException e) {
             throw new InternalError(e.toString());
         }

	}
	@Override
	public boolean isOptionChosen() {
		// TODO Auto-generated method stub
		return  optionChosen ;
	}
	@Override
	public void setOptionChosen(boolean option) {
		// TODO Auto-generated method stub
		optionChosen=option;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return enabled;
	}

	@Override
	public void setEnabled(boolean enable) {
		// TODO Auto-generated method stub
		enabled=enable;
	}
}
