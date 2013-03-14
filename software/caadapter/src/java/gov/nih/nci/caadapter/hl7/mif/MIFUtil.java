/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.mif;

/**
 * The class defines a Utility class processing MIF information.
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.18 $
 *          date        $Date: 2009-02-12 19:49:37 $
 */

import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;


public class MIFUtil {

	private static ArrayList <String> treatedSimpleType=new ArrayList<String>();

	static {
		treatedSimpleType.add("CS");
		treatedSimpleType.add("cs");
		treatedSimpleType.add("bl");
		treatedSimpleType.add("BL");
	}
	/**
	 * Determine if the MIFAttribute is allowed to edit if default value
	 * @param mifAttr
	 * @return
	 */
	public static boolean isEditableMIFAttributeDefault(MIFAttribute mifAttr)
	{

		Datatype mifDt=mifAttr.getDatatype();
		if (mifAttr.getConcreteDatatype()!=null)
			mifDt=mifAttr.getConcreteDatatype();
		//not editable if complex datatype
		if (mifDt==null)
		{
			//verify if it is treated for SimpleType with typeName
			if (!MIFUtil.isTreatedAsSimpleType(mifAttr.getType()))
				return false;
		}
		else if (!mifDt.isSimple())
			return false;

		//verify with FixValue
		String fixedText=mifAttr.getFixedValue();
		if (fixedText==null)
			return true;

		if(fixedText.equalsIgnoreCase(""))
			return true;

		//check if domainName is INSTNACE+KIND
		String domainName = mifAttr.getDomainName();
        if (domainName == null) domainName = "";
        else domainName = domainName.toUpperCase();
		System.out.println("MIFUtil.isEditableMIFAttributeDefault()...domainName:"+domainName);
		if ((domainName.indexOf("INSTANCE")>-1)
			&&(domainName.indexOf("KIND")>-1))
			return true;

		return false;

	}
	public static boolean isTreatedAsSimpleType(String typeName)
	{
		if (treatedSimpleType.contains(typeName.trim()))
			return true;
		return false;
	}

	public static boolean containChoiceAssociation(MIFAssociation assc)
	{
		MIFClass mifClass=assc.getMifClass();
		if (mifClass.getAssociations().isEmpty())
			return false;
		TreeSet mifAsscs=mifClass.getSortedAssociations();//.getAssociations();
		Iterator mifAsscIt=mifAsscs.iterator();
		while(mifAsscIt.hasNext())
		{
			MIFAssociation mifAssc=(MIFAssociation)mifAsscIt.next();
			if (!mifAssc.getMifClass().getSortedChoices().isEmpty())
				return true;
		}

		return false;
	}

	public static List<MIFAssociation> findAddableAssociation(MIFClass mifClass)
	{
		List<MIFAssociation> rtnList=new ArrayList<MIFAssociation>();
		for (MIFAssociation mifAssc:mifClass.getSortedAssociations())
		{
			if (mifAssc.getMinimumMultiplicity()==0&&!mifAssc.isOptionChosen())
				rtnList.add(mifAssc);
		}

		for(MIFClass choicClass:mifClass.getSortedChoices())
		{
			if (choicClass.isChoiceSelected())
			{
				for(MIFAssociation choiceAssc:choicClass.getSortedAssociations())
				{
					if (choiceAssc.getMinimumMultiplicity()==0&&!choiceAssc.isOptionChosen())
						rtnList.add(choiceAssc);
				}
				break; //one and only one chosen MIFClass
			}
		}

//		TreeSet mifAsscs=mifClass.getSortedAssociations();//.getAssociations();
//		Iterator mifAsscIt=mifAsscs.iterator();
//		while(mifAsscIt.hasNext())
//		{
//			MIFAssociation mifAssc=(MIFAssociation)mifAsscIt.next();
//			if (mifAssc.getMinimumMultiplicity()==0&&!mifAssc.isOptionChosen())
//				rtnList.add(mifAssc);
//		}
//		for(MIFClass choicClass:mifClass.getSortedChoices())
//		{
//			if (choicClass.isChoiceSelected())
//			{
//				for(MIFAssociation choiceAssc:choicClass.getSortedAssociations())
//				{
//					if (choiceAssc.getMinimumMultiplicity()==0&&!choiceAssc.isOptionChosen())
//						rtnList.add(choiceAssc);
//				}
//				break; //only and only one chosen MIFClass
//			}
//		}
		return rtnList;
	}

	public static List<MIFAssociation> findRemovableAssociation(MIFClass mifClass)
	{
		List<MIFAssociation> rtnList=new ArrayList<MIFAssociation>();
		if (mifClass==null)
			return rtnList;

		for (MIFAssociation mifAssc:mifClass.getSortedAssociations())
		{
			if (mifAssc.getMinimumMultiplicity()==0&&mifAssc.isOptionChosen())
				rtnList.add(mifAssc);
		}

		for(MIFClass choicClass:mifClass.getSortedChoices())
		{
			if (choicClass.isChoiceSelected())
			{
				for(MIFAssociation choiceAssc:choicClass.getSortedAssociations())
				{
					if (choiceAssc.getMinimumMultiplicity()==0&&choiceAssc.isOptionChosen())
						rtnList.add(choiceAssc);
				}
				break; //only and only one chosen MIFClass
			}
		}
		return rtnList;
	}
	public static List<MIFAssociation> findMIFAssociationWithName(MIFClass mifClass, String asscName)
	{
		List<MIFAssociation> rtnList=new ArrayList<MIFAssociation>();
		if (mifClass==null)
			return rtnList;
		HashSet mifAsscs=mifClass.getAssociations();
		Iterator mifAttrIt=mifAsscs.iterator();
		while(mifAttrIt.hasNext())
		{
			MIFAssociation mifAttr=(MIFAssociation)mifAttrIt.next();
			if (mifAttr.getName().equals(asscName))
				rtnList.add(mifAttr);
		}
		return rtnList;
	}
	public static int getMaximumAssociationMultiplicityIndexWithName(MIFClass mifClass, String asscName)
	{
		int rtnNum=0;
		List mifAsscs=findMIFAssociationWithName(mifClass,asscName);
		Iterator mifAsscsIt=mifAsscs.iterator();
		while(mifAsscsIt.hasNext())
		{
			MIFAssociation mifAssc=(MIFAssociation)mifAsscsIt.next();
			if (mifAssc.getMultiplicityIndex()>rtnNum)
				rtnNum=mifAssc.getMultiplicityIndex();
		}
		return rtnNum;
	}

	public static List<MIFAttribute> findMIFAttributeWithName(MIFClass mifClass, String attrName)
	{
		List<MIFAttribute> rtnList=new ArrayList<MIFAttribute>();
		HashSet mifAttrs=mifClass.getAttributes();
		Iterator mifAttrIt=mifAttrs.iterator();
		while(mifAttrIt.hasNext())
		{
			MIFAttribute mifAttr=(MIFAttribute)mifAttrIt.next();
			if (mifAttr.getName().equals(attrName))
				rtnList.add(mifAttr);
		}
		return rtnList;
	}
	public static TreeSet sortDatatypeAttribute(Datatype dt)
	{
		TreeSet<Attribute> rtnSet=new TreeSet<Attribute>();

		Hashtable childAttrHash=dt.getAttributes();
		Enumeration keyEnums=childAttrHash.keys();
		while (keyEnums.hasMoreElements())
		{
			String attrName=(String)keyEnums.nextElement();
			Attribute childAttr=(Attribute)childAttrHash.get(attrName);
			rtnSet.add(childAttr);
		}
		return rtnSet;
	}

	public static void addDatatypeAttributeOnTop(Datatype dt, Attribute attr)
	{


		Hashtable childAttrHash=dt.getAttributes();
		Enumeration keyEnums=childAttrHash.keys();
		while (keyEnums.hasMoreElements())
		{
			String attrName=(String)keyEnums.nextElement();
			Attribute childAttr=(Attribute)childAttrHash.get(attrName);
			childAttr.setSortKey(childAttr.getSortKey()+1);
		}
		attr.setSortKey(0);
		dt.getAttributes().put(attr.getName(),attr);

	}

	public static List<Attribute> findDatatypeAttributeWithName(Datatype mifDatatype, String attrName)
	{
		List<Attribute> rtnList=new ArrayList<Attribute>();
		Hashtable attrHash=mifDatatype.getAttributes();
		Enumeration enumAttrKeys=mifDatatype.getAttributes().keys();
		while (enumAttrKeys.hasMoreElements())
		{
			String nameKey=(String)enumAttrKeys.nextElement();
			if (nameKey.startsWith(attrName))
				rtnList.add((Attribute)attrHash.get(nameKey));
		}
		return rtnList;
	}
	public static int getMaximumMIFAttributeMultiplicityIndexWithName(MIFClass mifClass, String attrName)
	{
		int rtnNum=0;
		List mifAsscs=findMIFAttributeWithName(mifClass,attrName);
		Iterator mifAttrIt=mifAsscs.iterator();
		while(mifAttrIt.hasNext())
		{
			MIFAttribute mifAttr=(MIFAttribute)mifAttrIt.next();
			if (mifAttr.getMultiplicityIndex()>rtnNum)
				rtnNum=mifAttr.getMultiplicityIndex();
		}
		return rtnNum;
	}
	/**
	 * Remove one Attribute from a Datatype and reset multiplicityIndex for the other
	 * Attributes with the same name
	 * @param dt
	 * @param xmlName
	 */
	public static void removeDatatypeAttributeWithXmlName(Datatype dt, String xmlName)
	{
		Attribute attr=(Attribute)dt.getAttributes().get(xmlName);
		if(attr==null)
			return;
		int indxRmv=attr.getMultiplicityIndex();
		//	remove the attribute from Datatype
		dt.getAttributes().remove(xmlName);
		List<Attribute> allAttr=findDatatypeAttributeWithName(dt, attr.getName());
		if (allAttr.size()>1)
		{
			//reset index for all attribute
			for (Attribute oneAttr:allAttr)
			{
				if (oneAttr.getMultiplicityIndex()>indxRmv)
					oneAttr.setMultiplicityIndex(oneAttr.getMultiplicityIndex()-1);
			}
		}

	}
	/**
	 * Count the Attribute with the same name
	 * @param Datatype parent Datatype object carrying the target Attribute
	 * @param attrName
	 * @return
	 */
	public static int getMaximumAttributeMultiplicityIndexWithName(Datatype mifDatatype, String attrName)
	{
		int rtnNum=0;
		List mifAsscs=findDatatypeAttributeWithName(mifDatatype,attrName);
		Iterator mifAttrIt=mifAsscs.iterator();
		while(mifAttrIt.hasNext())
		{
			Attribute dtAttr=(Attribute)mifAttrIt.next();
			if (dtAttr.getMultiplicityIndex()>rtnNum)
				rtnNum=dtAttr.getMultiplicityIndex();
		}
		return rtnNum;
	}
	/**
	 * Check if "inlineText" attribute is required
	 */
	public static boolean isInlineTextRequired(String datatypeName)
	{
		boolean rtnValue=false;
		if (CaadapterUtil.getInlineTextAttributes().contains(datatypeName))
			rtnValue=true;
		else if (datatypeName.indexOf(".")>-1)
		{
//			System.out.println("MIFUtil.isInlineTextRequired()..verify datatype:"+datatypeName);
			String dtNamePrefix=datatypeName.substring(0,datatypeName.indexOf("."));
			String dtSuperName=dtNamePrefix+".*";
//			System.out.println("MIFUtil.isInlineTextRequired()..verify datatype superName:"+dtSuperName);
			if (CaadapterUtil.getInlineTextAttributes().contains(dtSuperName))
				rtnValue=true;
		}
		return rtnValue;
	}

	/**
	 * Recursively check if a class have been defined by the class or its child classes
	 */
	public static MIFClass findLocalRefenceClass(MIFClass mifClass, String toLook)
	{
		if (mifClass.getName().equalsIgnoreCase(toLook))
			return mifClass;
		MIFClass rtnMif=null;
		TreeSet<MIFAssociation> mifAsscs=mifClass.getSortedAssociations();
		if (mifAsscs!=null
			&&!mifAsscs.isEmpty())
		{
			for(MIFAssociation assc:mifAsscs)
			{
				 MIFClass asscMifClass=assc.getMifClass();
				 if (asscMifClass.getName().equalsIgnoreCase(toLook))
					 return asscMifClass;
				 else
				 {
					 rtnMif =findLocalRefenceClass(asscMifClass, toLook);
					 if (rtnMif!=null)
						 return rtnMif;
				 }

			}
		}
		//process choice class
//		if(mifClass.getSortedChoices()!=null)
//		{
			for (MIFClass choiceClass:mifClass.getChoices())
			{
				if (choiceClass.getName().equalsIgnoreCase(toLook))
					 return choiceClass;
				else if (choiceClass.isAbstractDefined())
				{
					for(MIFClass concreteChild:choiceClass.getChoices())
						if (concreteChild.getName().equalsIgnoreCase(toLook))
							return concreteChild;
				}
				else
				 {
					 rtnMif =findLocalRefenceClass(choiceClass, toLook);
					 if (rtnMif!=null)
						 return rtnMif;
				 }

			}
//		}
		return null;
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.17  2009/01/08 21:53:02  umkis
 * HISTORY :Protect from NullPointerException at Line 74 mifAttr.getDomainName();
 * HISTORY :
 * HISTORY :Revision 1.16  2008/12/23 14:35:53  wangeug
 * HISTORY :Process MIFClass with isAbstract=true
 * HISTORY :
 * HISTORY :Revision 1.15  2008/09/29 15:44:41  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */