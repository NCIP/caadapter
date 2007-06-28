package gov.nih.nci.caadapter.hl7.mif;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
	
	public static boolean isChoiceAssociation(MIFAssociation assc)
	{
		MIFClass mifClass=assc.getMifClass();
		if (mifClass.getChoices().isEmpty())
			return false;
		else
			return true;
	}
	
	public static List<MIFAssociation> findAddableAssociation(MIFClass mifClass)
	{
		List<MIFAssociation> rtnList=new ArrayList<MIFAssociation>();
		HashSet mifAsscs=mifClass.getAssociations();
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
		HashSet mifAsscs=mifClass.getAssociations();
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
	
	public static void reloadTreeNode(DefaultMutableTreeNode oldNode,DefaultMutableTreeNode newNode, JTree tree )
	{
		oldNode.removeAllChildren();
		for (int i=0;i<newNode.getChildCount();i++)
			oldNode.add((DefaultMutableTreeNode)newNode.getChildAt(i));
		((DefaultTreeModel) tree.getModel()).nodeStructureChanged(oldNode);
	}

//	public static void setAssociationOptionStatus(List<MIFAssociation> asscList, MIFClass targetClass, boolean newStatus)
//	{
//		List<MIFAssociation> targetAsscList=null;
//		if (newStatus)
//			targetAsscList=findAddableAssociation(targetClass);
//		else
//			targetAsscList=findRemovableAssociation(targetClass);
//		for(MIFAssociation assc:asscList)
//		{
//			for (MIFAssociation targetAssc:targetAsscList)
//			{
//				if(targetAssc.getName().equals(assc.getName()))
//					System.out.println("MIFUtil.setAssociationOptionStatus()..targetAssc:"+targetAssc +"..newStatus:"+newStatus+"...targetClass:"+targetClass);
//					targetAssc.setOptionChosen(newStatus);
//			}
//		}
//	}

}
