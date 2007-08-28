package gov.nih.nci.caadapter.hl7.mif;

import gov.nih.nci.caadapter.common.util.CaadapterUtil;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;

public class MIFUtil {
	
	public static boolean isTreatedAsSimpleType(String typeName)
	{
		if (typeName.trim().equals("CS"))
			return true;
		return false;
	}
	
	public static boolean containAssociation(MIFAssociation assc)
	{
		MIFClass mifClass=assc.getMifClass();
		if (mifClass.getAssociations().isEmpty())
			return false;
		TreeSet mifAsscs=mifClass.getSortedAssociations();//.getAssociations();
		Iterator mifAsscIt=mifAsscs.iterator();
		while(mifAsscIt.hasNext())
		{
			MIFAssociation mifAssc=(MIFAssociation)mifAsscIt.next();
			if (!mifAssc.getMifClass().getChoices().isEmpty())
				return true;
		}
		
		return false;
	}
	
	public static List<MIFAssociation> findAddableAssociation(MIFClass mifClass)
	{
		List<MIFAssociation> rtnList=new ArrayList<MIFAssociation>();
		TreeSet mifAsscs=mifClass.getSortedAssociations();//.getAssociations();
		Iterator mifAsscIt=mifAsscs.iterator();
		while(mifAsscIt.hasNext())
		{
			MIFAssociation mifAssc=(MIFAssociation)mifAsscIt.next();
			if (mifAssc.getMinimumMultiplicity()==0&&!mifAssc.isOptionChosen())
				rtnList.add(mifAssc);
		}
		return rtnList;
	}
	
	public static List<MIFAssociation> findRemovableAssociation(MIFClass mifClass)
	{
		List<MIFAssociation> rtnList=new ArrayList<MIFAssociation>();
		TreeSet mifAsscs=mifClass.getSortedAssociations();//.getAssociations();
		Iterator mifAsscIt=mifAsscs.iterator();
		while(mifAsscIt.hasNext())
		{
			MIFAssociation mifAssc=(MIFAssociation)mifAsscIt.next();
			if (mifAssc.getMinimumMultiplicity()==0&&mifAssc.isOptionChosen())
				rtnList.add(mifAssc);
		}
		return rtnList;
	}
	public static List<MIFAssociation> findMIFAssociationWithName(MIFClass mifClass, String asscName)
	{
		List<MIFAssociation> rtnList=new ArrayList<MIFAssociation>();
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
}
