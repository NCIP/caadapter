/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.mif;

import gov.nih.nci.caadapter.hl7.datatype.DatatypeParserUtil;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import gov.nih.nci.caadapter.hl7.mif.v1.CMETUtil;
import gov.nih.nci.caadapter.hl7.mif.v1.MIFParserUtil;

import java.io.Serializable;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Collections;
import java.util.Iterator;

/**
 * The class defines a MIF Class.
 * 
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0 revision $Revision: 1.2 $ date $Date: 2007-05-25 15:05:19 $
 */

 public class MIFClass extends DatatypeBaseObject implements Serializable, Comparable <MIFClass> {
		static final long serialVersionUID = 6L;
	 private HashSet<MIFAttribute> attributes = new HashSet<MIFAttribute>();
	 private HashSet<MIFAssociation> associations = new HashSet<MIFAssociation>();
	 private HashSet<MIFClass> choices = new HashSet<MIFClass>();
	 private String referenceName ="";
	 private String name;
	 private boolean abstractClass;
	 private boolean isReference = false;
	 private boolean isDynamic = false;
	 private String sortKey;
	 
	 /**
	  * This method will add an attribute object to a given MIF object.
	  * 
	  * @param attributeName is the name of attribute
	  * @param attr is the MIFAttribute object associates with the name of that attribute
	  */

	 public void addAttribute(MIFAttribute attr) {
			attributes.add(attr);
	 }
	 /**
	  * @return attributes of a MIFClass
	  */
	 public HashSet<MIFAttribute> getAttributes() {
	 	return attributes;
	 }
	 
	 public TreeSet<MIFAttribute> getSortedAttributes ()
	 {
		TreeSet<MIFAttribute> rtnSet =new TreeSet<MIFAttribute>(); 
		Iterator it=getAttributes().iterator();
		while(it.hasNext())
			rtnSet.add((MIFAttribute)(it.next()));
		return rtnSet;
	 }

	 /**
	  * This method will add an association object to a given MIF object.
	  * 
	  * @param associationName is the name of association
	  * @param association is the MIFClass object associates with the name of that association
	  */

	 public void addAssociation(MIFAssociation association) {
		 associations.add(association);
	 }
	 /**
	  * @return associations of a MIFClass
	  */
	 public HashSet<MIFAssociation> getAssociations() {
	 	return associations;
	 }
	 
	 public TreeSet<MIFAssociation> getSortedAssociations ()
	 {
		TreeSet<MIFAssociation> rtnSet =new TreeSet<MIFAssociation>(); 
		Iterator it=getAssociations().iterator();
		while(it.hasNext())
			rtnSet.add((MIFAssociation)(it.next()));
		return rtnSet;
	 }
	 /**
	  * This method will add an choice object to a given MIF object.
	  * 
	  * @param choiceName is the name of choice
	  * @param choice is the MIFClass object associates with the name of that choiceName
	  */

	 public void addChoice(MIFClass choice) {
		 choices.add(choice);
	 }
	 /**
	  * @return choices of a MIFClass
	  */
	 public HashSet<MIFClass> getChoices() {
	 	return choices;
	 }

	 public TreeSet<MIFClass> getSortedChoices ()
	 {
		TreeSet<MIFClass> rtnSet =new TreeSet<MIFClass>(); 
		Iterator it=getChoices().iterator();
		while(it.hasNext())
			rtnSet.add((MIFClass)(it.next()));
		return rtnSet;
	 }
	 /**
	  * This method will set the reference name to a CMET.
	  * 
	  * @param referenceName is the name of a CMET reference
	  */

	 public void setReferenceName(String referenceName) {
		 this.referenceName = referenceName;
	 }
	 /**
	  * @return true is this contains a reference
	  */
	 public boolean isReference(){
		 return isReference;
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
	 * @return the sortKey
	 */
	public String getSortKey() {
		return sortKey;
	}
	/**
	 * @param sortKey the sortKey to set
	 */
	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}
	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(HashSet<MIFAttribute> attributes) {
		this.attributes = attributes;
	}
	/*
	 * 
	 */
	public void printMIFClass(int level, HashSet visitedMIFClass) {
		if (visitedMIFClass != null) {
			if (visitedMIFClass.contains(getName())) {
				return;
			}
		}
		visitedMIFClass.add(getName());
		if (isDynamic()) {
			for(int i=0;i<level;i++) {
				System.out.print("   ");
			}
			System.out.println("The Class: " + this.getName() + " will be determined at run time");
			return;
		}
		if (!getReferenceName().equals("")) {
			for(int i=0;i<level;i++) {
				System.out.print("   ");
			}
			if (!isReference()) { // is actually a CMET reference
				System.out.println("Reference MIF Class: " + this.referenceName);
			}
			else {
				System.out.println("Self-Reference MIF Class: " + this.referenceName);
			}
			
			CMETRef cmetRef = CMETUtil.getCMET(this.referenceName);
			for(int i=0;i<level;i++) {
				System.out.print("   ");
			}
			if (cmetRef == null) System.out.println("Not Found");
			else {
				System.out.println(cmetRef.getName() + "'s class name is : " + cmetRef.getClassName() +  "   Filename is:" + cmetRef.getFilename());
				MIFClass mifClass = MIFParserUtil.getMIFClass(cmetRef.getFilename() + ".mif");
				mifClass.printMIFClass(level+1, visitedMIFClass);
			}
		}
		else {
			for(int i=0;i<level;i++) {
				System.out.print("   ");
			}
			System.out.println("MIF Class: " + this.name);
		}
		printAttribute(level);
		printAssociation(level, visitedMIFClass);
		printChoice(level, visitedMIFClass);
		visitedMIFClass.remove(getName());
	}
	public void printAttribute(int level) {
		if (this.getAttributes().size()==0) return;
		for(MIFAttribute mifAttribute:this.getSortedAttributes()){ //.getAttributes()) {
			for(int i=0;i<level;i++) {
				System.out.print("   ");
			}
			System.out.println("Attribute name:" + mifAttribute.getName() + "  type:" + mifAttribute.getType());
			DatatypeParserUtil.getDatatype(mifAttribute.getType()).print(level);
			
		}
	}
	public void printAssociation(int level, HashSet visitedMIFClass) {
//		if (this.getAssociations().size()==0) return;
//		for(MIFAssociation mifAssociation:this.getAssociations()) {
		if (this.getSortedAssociations().size()==0) return;
		for(MIFAssociation mifAssociation:this.getSortedAssociations()) {

		for(int i=0;i<level;i++) {
				System.out.print("   ");
			}
			System.out.println("Association name:" + mifAssociation.getName());
			mifAssociation.getMifClass().printMIFClass(level+1, visitedMIFClass);
		}
	}
	public void printChoice(int level, HashSet visitedMIFClass) {
		if (this.getChoices().size()==0) return;
		int choiceIndex = 0;
		for(MIFClass mifChoice:this.getChoices()) {
			for(int i=0;i<level;i++) {
				System.out.print("   ");
			}
			System.out.println("Choice " + (choiceIndex++) +":" );
			mifChoice.printMIFClass(level+1, visitedMIFClass);
		}
	}
	/**
	 * @return the isDynamic
	 */
	public boolean isDynamic() {
		return isDynamic;
	}
	/**
	 * @param isDynamic the isDynamic to set
	 */
	public void setDynamic(boolean isDynamic) {
		this.isDynamic = isDynamic;
	}
	/**
	 * @param isReference the isReference to set
	 */
	public void setReference(boolean isReference) {
		this.isReference = isReference;
	}
	/**
	 * @return the referenceName
	 */
	public String getReferenceName() {
		return referenceName;
	}
	public int compareTo(MIFClass mifClass) {
		// TODO Auto-generated method stub
		if (this.getSortKey()==null||mifClass.getSortKey()==null)
			return (this.getName().compareToIgnoreCase(mifClass.getName()));
		else
			return (this.getSortKey().compareToIgnoreCase(mifClass.getSortKey()));
	}
 }