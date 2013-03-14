/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.mif;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.PropertiesResult;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeParserUtil;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import gov.nih.nci.caadapter.hl7.mif.v1.CMETUtil;
import gov.nih.nci.caadapter.hl7.mif.v1.MIFParserUtil;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeSet;
import java.util.Iterator;

/**
 * The class defines a MIF Class.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v4.0 revision $Revision: 1.28 $ date $Date: 2009-04-02 20:34:40 $
 */

 public class MIFClass extends DatatypeBaseObject implements Serializable, Comparable <MIFClass>, Cloneable {
		static final long serialVersionUID = 6L;
		private Hashtable<String, String> packageLocation = new Hashtable<String, String> ();
	 private HashSet<MIFAttribute> attributes = new HashSet<MIFAttribute>();
	 private HashSet<MIFAssociation> associations = new HashSet<MIFAssociation>();
	 private HashSet<MIFClass> choices = new HashSet<MIFClass>();
	 private String referenceName ="";
	 private String name;
	 private String copyrightYears;
	 private boolean isReference = false;
	 private boolean isDynamic = false;
	 private String sortKey;
	 private boolean optionChosen = false;
	 private boolean choiceSelected =false;//make this variable serializable
	 private boolean abstractDefined=false;
	 private String parentXmlPath;
	 private boolean mapped;
 	 private List<String> csvSegments;
 	 private String csvSegment;
 	 private String messageType;
 	private String traversalName;

//&umkis    private String annotation;
//&umkis	private String comment;
     /**
	  * This method will add an attribute object to a given MIF object.
	  *
	  *
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
	/**
	 * @param newAttributes the attributes to set
	 */
	public void setAttributes(HashSet<MIFAttribute> newAttributes) {
		attributes=newAttributes;
	}

	 public void removeAttribute(MIFAttribute attrToRemove)
	 {
			Iterator it=getAttributes().iterator();
			MIFAttribute mifFound=null;
			while(it.hasNext())
			{
				 MIFAttribute nxtAttr=(MIFAttribute)it.next();
				 if (nxtAttr.getName().equals(attrToRemove.getName()))
				 {
					 if (nxtAttr.getMultiplicityIndex()==attrToRemove.getMultiplicityIndex())
						 mifFound=nxtAttr;
					 else
					 {
					 	if (nxtAttr.getMultiplicityIndex()>attrToRemove.getMultiplicityIndex())
						 	nxtAttr.setMultiplicityIndex(nxtAttr.getMultiplicityIndex()-1);
					 }
				 }
			}
			if (mifFound!=null)
				 attributes.remove(mifFound);

	 }

	 public TreeSet<MIFAttribute> getSortedAttributes ()
	 {
		TreeSet<MIFAttribute> rtnSet =new TreeSet<MIFAttribute>();
		Iterator it=getAttributes().iterator();
		while(it.hasNext())
			rtnSet.add((MIFAttribute)(it.next()));
		return rtnSet;
	 }

	 public int getMaxAttributeMultiplicityWithName(String attrName)
	 {
		 int rtnCount=0;
		 for(MIFAttribute attr:attributes)
			 if (attr.getName().equals(attrName))
				 rtnCount++;
		 return rtnCount;
	 }
	 /**
	  * This method will add an association object to a given MIF object.
	  *
	  *
	  * @param association is the MIFClass object associates with the name of that association
	  */

	 public void addAssociation(MIFAssociation association) {
		 associations.add(association);
	 }

	 public void setAssociation(HashSet<MIFAssociation> newAsscs)
	 {
		 associations=newAsscs;
	 }

	 public void removeAssociation(MIFAssociation asscToRemove)
	 {
			Iterator it=getAssociations().iterator();
			MIFAssociation asscFound=null;
			while(it.hasNext())
			{
				MIFAssociation nxtAssc=(MIFAssociation)it.next();
				 if (nxtAssc.getName().equals(asscToRemove.getName()))
				 {
					 if (nxtAssc.getMultiplicityIndex()==asscToRemove.getMultiplicityIndex())
					 	asscFound=nxtAssc;
					 else if (nxtAssc.getMultiplicityIndex()>asscToRemove.getMultiplicityIndex())
						 nxtAssc.setMultiplicityIndex(nxtAssc.getMultiplicityIndex()-1);
				 }
			}

			if (asscFound!=null)
				associations.remove(asscFound);
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

	 public int getMaxAssociationMultiplicityWithName(String asscName)
	 {
		 int rtnCount=0;
		 for(MIFAssociation attr:associations)
			 if (attr.getName().equals(asscName))
				 rtnCount++;
		 return rtnCount;
	 }

	 /**
	  * This method will add an choice object to a given MIF object.
	  *
	  *
	  * @param choice is the MIFClass object associates with the name of that choiceName
	  */

	 public void addChoice(MIFClass choice) {
		 choices.add(choice);
	 }

	 public void setChoice(HashSet<MIFClass> newChoices)
	 {
		 choices=newChoices;
	 }
	 /**
	  * @return choices of a MIFClass
	  */
	 public HashSet<MIFClass> getChoices() {
	 	return choices;
	 }
/**
 * Return all item to be selected.
 * If an item is a list of other MIFClass, all these children are promoted
 * to top level as choice item
 * @return A list of choice items
 */
	 public TreeSet<MIFClass> getSortedChoices ()
	 {
		TreeSet<MIFClass> rtnSet =new TreeSet<MIFClass>();

		for (MIFClass choiceItem:getChoices())
		{
//			rtnSet.add((MIFClass)(choiceItem));//it.next()));
			//add the content of a choiceItem if it is a list of other MIFClass
			if (choiceItem.isAbstractDefined())
			{
				for (MIFAssociation asbtractAssc:choiceItem.getAssociations())
				{
					//the parent Association MIFClass is abstract, push all its association down to its children
					for (MIFClass childChoice:choiceItem.getChoices())
					{
						if (!childChoice.getAssociations().contains(asbtractAssc))
							childChoice.addAssociation(asbtractAssc);
						rtnSet.add(childChoice);
					}
				}
			}
			else
				rtnSet.add((MIFClass)(choiceItem));

		}
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
	 * @return the copyrightYears
	 */
	public String getCopyrightYears() {
		return copyrightYears;
	}
	/**
	 * @param copyrightYears the copyrightYears to set
	 */
	public void setCopyrightYears(String copyrightYears) {
		this.copyrightYears = copyrightYears;
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
//		printAttribute(level);
		printAssociation(level, visitedMIFClass);
//		printChoice(level, visitedMIFClass);
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
	 * @return the abstractDefined
	 */
	public boolean isAbstractDefined() {
		return abstractDefined;
	}
	/**
	 * @param abstractDefined the abstractDefined to set
	 */
	public void setAbstractDefined(boolean abstractDefined) {
		this.abstractDefined = abstractDefined;
	}
	/**
	 * @return the referenceName
	 */
	public String getReferenceName() {
		return referenceName;
	}
	public int compareTo(MIFClass mifClass) {
		// TODO Auto-generated method stub
		String myCompareKey=getSortKey();
		if (myCompareKey==null||myCompareKey.equals(""));
			myCompareKey=getName();

		String otherCompareKey=mifClass.getSortKey();
		if (otherCompareKey==null||otherCompareKey.equals(""));
			otherCompareKey=mifClass.getName();
		return (myCompareKey.compareToIgnoreCase(otherCompareKey));
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
	public boolean isChoiceSelected() {
		return choiceSelected;
	}

	public void setChoiceSelected(boolean choiceSelected) {
		this.choiceSelected = choiceSelected;
	}
	public Object clone()
	{
		try {
			 MIFClass clonnedObj = (MIFClass)super.clone();
			 //clone MIFAttribute
			 HashSet  attrHash=getAttributes();
			 HashSet <MIFAttribute> attrClonnedHash=new HashSet<MIFAttribute>();
			 Iterator attrIt=attrHash.iterator();
			 while (attrIt.hasNext())
			 {
				 MIFAttribute oneAttr=(MIFAttribute)attrIt.next();
				 attrClonnedHash.add((MIFAttribute)oneAttr.clone());
			 }
			 clonnedObj.setAttributes(attrClonnedHash);

			 //clone MIFAssociation
			 HashSet  asscHash=getAssociations();
			 HashSet <MIFAssociation> asscClonnedHash=new HashSet<MIFAssociation>();
			 Iterator asscIt=asscHash.iterator();
			 while (asscIt.hasNext())
			 {
				 MIFAssociation oneAssc=(MIFAssociation)asscIt.next();
				 //clone the abstractAssociation only if the MIFClass is
				 //abstract since the concrete MIFClass carries only a
				 //reference of the abstract MIFAssociation define by the
				 //abstract MIFClass
				 if (oneAssc.isAbstractDefined()==this.isAbstractDefined())
				 {
					 MIFAssociation clonedAssc=(MIFAssociation)oneAssc.clone();
					 asscClonnedHash.add(clonedAssc);
				 }
			 }
			 clonnedObj.setAssociation(asscClonnedHash);

//			clone choice MIFClass
			 HashSet  choiceHash=this.getChoices();
			 HashSet <MIFClass> choiceClonnedHash=new HashSet<MIFClass>();
			 Iterator choiceIt=choiceHash.iterator();
			 while (choiceIt.hasNext())
			 {
				 MIFClass oneChoice=(MIFClass)choiceIt.next();
				 MIFClass clonedChoice=(MIFClass)oneChoice.clone();
				 choiceClonnedHash.add(clonedChoice);
			 }
//			 if (clonnedObj.isAbstractDefined())
//			 {
//				for (MIFAssociation asbtractAssc:clonnedObj.getAssociations())
//				{
// 					//the cloned MIFClass is abstract, push all its association down to its children
// 					for (MIFClass childChoice:clonnedObj.getChoices())
// 					{
//							childChoice.addAssociation(asbtractAssc);
//					}
//				}
//			 }
			 clonnedObj.setChoice(choiceClonnedHash);
			 clonnedObj.setChoiceSelected(false);
//			 clonnedObj.setOptionChosen(false);
             return clonnedObj;
         }
         catch (CloneNotSupportedException e) {
             throw new InternalError(e.toString());
         }

	}
	public String getNodeXmlName() {
		if (traversalName!=null&&!traversalName.equals(""))
			return traversalName;

		return this.getName();
	}

	public String getParentXmlPath() {
		return parentXmlPath;
	}
	public void setParentXmlPath(String parentXmlPath) {
		this.parentXmlPath = parentXmlPath;
	}
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public void setEnabled(boolean enable) {
		// TODO Auto-generated method stub

	}
	/**
	 * @return the csvSegment
	 */
	public String getCsvSegment() {
		return csvSegment;
	}
	/**
	 * @param csvSegment the csvSegment to set
	 */
	public void setCsvSegment(String csvSegment) {
		this.csvSegment = csvSegment;
	}
	/**
	 * @return the csvSegments
	 */
	public List<String> getCsvSegments() {
		return csvSegments;
	}
	/**
	 * @param csvSegments the csvSegments to set
	 */
	public void setCsvSegments(List<String> csvSegments) {
		this.csvSegments = csvSegments;
	}
	/**
	 * @return the mapped
	 */
	public boolean isMapped() {
		return mapped;
	}
	/**
	 * @param mapped the mapped to set
	 */
	public void setMapped(boolean mapped) {
		this.mapped = mapped;
	}

	public PropertiesResult getPropertyDescriptors() throws Exception {
		Class beanClass = this.getClass();

		PropertyDescriptor _name = new PropertyDescriptor("Name", beanClass, "getName", null);
		PropertyDescriptor _parentPath = new PropertyDescriptor("Parent", beanClass, "getParentXmlPath", null);
		PropertyDescriptor _class = new PropertyDescriptor("Type", beanClass, "findTypeProperty", null);
		List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>();
		propList.add(_name);
		propList.add(_parentPath);
		propList.add(_class);
		propList.add(new PropertyDescriptor("isReference", beanClass, "isReference", null));
		propList.add(new PropertyDescriptor("Cardinality", beanClass, "findCardinality", null));
		PropertiesResult result = new PropertiesResult();
		result.addPropertyDescriptors(this, propList);
		return result;
	}

	public String getTitle() {
		return "MIF Clone Properties";
	}


	public String findCardinality() {

		return Config.CARDINALITY_ONE_TO_ONE;
	}

	public String findTypeProperty() {
		return "Clone";
	}
	public String getMessageType() {
 		return messageType;
	}
	public void setMessageType(String msgType) {
		this.messageType = msgType;
	}
	public String getTraversalName() {
		return traversalName;
	}
	public void setTraversalName(String traversalName) {
		this.traversalName = traversalName;
	}
	public String toString()
	{
		return getNodeXmlName();
	}
	public Hashtable<String, String> getPackageLocation() {
		return packageLocation;
	}
	public void setPackageLocation(Hashtable<String, String> packageLocation) {
		this.packageLocation = packageLocation;
	}

//&umkis    public String getAnnotation() {
//&umkis		return annotation;
//&umkis	}

//&umkis	public void setAnnotation(String annotation) {
//&umkis		this.annotation = annotation;
//&umkis	}

//&umkis	public String getComment() {
//&umkis		return comment;
//&umkis	}
//&umkis	public void setComment(String comment) {
//&umkis		this.comment = comment;
//&umkis    }
 }
 /**
  * HISTORY :$Log: not supported by cvs2svn $
  * HISTORY :Revision 1.27  2009/02/12 19:48:55  wangeug
  * HISTORY :use sortedChoice() to include all choiceItems from sub-list
  * HISTORY :
  * HISTORY :Revision 1.26  2009/02/09 21:42:45  wangeug
  * HISTORY :correct errors in "nullFlavor" setting: set value with "nullFlavor" attribute only if a NULLFLAVOR constant being found with the value of a "coreAttribute"
  * HISTORY :
  * HISTORY :Revision 1.25  2009/01/16 15:11:16  wangeug
  * HISTORY :add new attribute:copyrightYears
  * HISTORY :
  * HISTORY :Revision 1.24  2008/12/30 15:03:11  wangeug
  * HISTORY :Process MIFClass with isAbstract=true:create new property abstractDefined
  * HISTORY :
  * HISTORY :Revision 1.23  2008/12/23 14:35:03  wangeug
  * HISTORY :Process MIFClass with isAbstract=true
  * HISTORY :
  * HISTORY :Revision 1.22  2008/12/18 17:20:42  wangeug
  * HISTORY :Return all item to be selected. If an item is a list of other MIFClass, all these children are promoted to top level as choice item
  * HISTORY :
  * HISTORY :Revision 1.21  2008/12/11 17:05:25  wangeug
  * HISTORY :MIF Parsing: A item of a choice is a list of other MIFClass.
  * HISTORY :
  * HISTORY :Revision 1.20  2008/09/29 15:44:40  wangeug
  * HISTORY :enforce code standard: license file, file description, changing history
  * HISTORY :
  */