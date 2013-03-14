/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.common.nodeloader;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeParserUtil;
import gov.nih.nci.caadapter.hl7.mif.CMETRef;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFIndex;
import gov.nih.nci.caadapter.hl7.mif.MIFIndexParser;
import gov.nih.nci.caadapter.hl7.mif.MIFUtil;
import gov.nih.nci.caadapter.hl7.mif.NormativeVersionUtil;
import gov.nih.nci.caadapter.hl7.mif.XmlToMIFImporter;
import gov.nih.nci.caadapter.hl7.mif.v1.CMETUtil;
import gov.nih.nci.caadapter.hl7.mif.v1.MIFParserUtil;
import gov.nih.nci.caadapter.ui.common.tree.DefaultHSMTreeMutableTreeNode;
import gov.nih.nci.caadapter.ui.common.tree.DefaultTargetTreeNode;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * This class defines a basic node loader implementation focusing on how to traverse HSM
 * meta data tree to convert them to a Java UI tree structure, whose nodes are either
 * DefaultMutableTreeNode or its various descendants that may individual requirement.
 *
 * Therefore, it is highly recommended to individual panel developers to sub-class this loader
 * class, with main purpose of providing customized DefaultMutableTreeNode descendant implementation,
 * while leaving the algorithm of traversing HSM meta data tree defined here intact.
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.52 $
 *          date        $Date: 2009-03-13 14:57:08 $
 */
public class NewHSMBasicNodeLoader extends DefaultNodeLoader
{
	/**
	 * Based on the given object type, this function will convert the meta-data tree to a TreeNode-based tree structure, whose root is the returned TreeNode.
	 * @param o the meta-data object
	 * @return the root node representing the TreeNode structure mapping the given meta-data tree.
	 * @throws NodeLoader.MetaDataloadException
	 */
	private boolean isMappingTree=false;
	private boolean nullFlavorEnabled=false;
	private boolean complexTypeEnabled=false;
	private boolean treeEditable=false;
	private boolean newSpecificationFlag=false;
	private MIFClass rootMIFClass=null;
	public NewHSMBasicNodeLoader(boolean editableFlag)
	{
		this (editableFlag,false);
	}

	public NewHSMBasicNodeLoader(boolean editableFlag, boolean preferenceFlag)
	{
		treeEditable=editableFlag;
		newSpecificationFlag=preferenceFlag;
		String nullFlavorSetting=CaadapterUtil.readPrefParams(Config.CAADAPTER_COMPONENT_HL7_SPECFICATION_NULLFLAVOR_ENABLED);
		if (nullFlavorSetting!=null&&nullFlavorSetting.equalsIgnoreCase("true"))
			nullFlavorEnabled=true;
		String complexTypeSetting=CaadapterUtil.readPrefParams(Config.CAADAPTER_COMPONENT_HL7_SPECFICATION_COMPLEXTYPE_ENABLED);
		if (complexTypeSetting!=null&&complexTypeSetting.equalsIgnoreCase("true"))
			complexTypeEnabled=true;
	}
	public TreeNode loadMappingTargetData(Object o)
	{
		isMappingTree=true;
		TreeNode realRoot=loadData(o);
		DefaultTargetTreeNode node = new DefaultTargetTreeNode("Target Tree", true);
		node.add((MutableTreeNode) realRoot);
		return node;
	}

	public TreeNode loadData(Object o)
	{
		DefaultMutableTreeNode root = null;
		if(o instanceof MIFClass)
		{
			rootMIFClass = (MIFClass) o;
		}
		else if (o instanceof File)
		{
			System.out.println("NewHSMBasicNodeLoader.loadData()..with file:"+((File)o).getAbsolutePath());
			File file=(File)o;
			XmlToMIFImporter mifImport=new XmlToMIFImporter();
			rootMIFClass=mifImport.importMifFromXml(file);

		}
		if (rootMIFClass!=null)
		{
			String currentMIFVersion=rootMIFClass.getCopyrightYears();
			MIFIndex currentMIFIndex=NormativeVersionUtil.loadMIFIndex(currentMIFVersion);
			NormativeVersionUtil.setCurrentMIFIndex(currentMIFIndex);
			root = buildObjectNode(rootMIFClass, null);
		}
		else
			Log.logError(this, "Unable to intialized tree root with object:"+o.toString());
		return root;
	}

	public DefaultMutableTreeNode buildObjectNode(Object objToLoad, MIFClass treeNodeRootMIF)
	{
		if (treeNodeRootMIF!=null)
			rootMIFClass=treeNodeRootMIF;

		DefaultMutableTreeNode rtnNode=null;
		if (objToLoad instanceof MIFClass)
			rtnNode=buildMIFClassNode((MIFClass)objToLoad, null);
		else if (objToLoad instanceof MIFAttribute)
			rtnNode=buildMIFAttributeNode((MIFAttribute)objToLoad);
		else if (objToLoad instanceof MIFAssociation)
			rtnNode= buildMIFAssociationNode((MIFAssociation)objToLoad);
		else if (objToLoad instanceof Attribute)
			rtnNode= buildDatatypeAttributeNode((Attribute)objToLoad);
		else
			rtnNode= new DefaultMutableTreeNode(objToLoad);
		//only set the node xmlPath if the tree is editable
		if (treeEditable)
			setXmlpathForUserObject(rtnNode);
		return rtnNode;
	}

	private DefaultMutableTreeNode buildMIFClassNode(MIFClass mifClass, MIFAssociation mifAssociation)
	{
		DefaultMutableTreeNode rtnNode =constructTreeNodeBasedOnTreeType(mifClass,true);

		Hashtable<String, String> traversalnames = new Hashtable<String, String>();
		if (mifAssociation!=null)
			traversalnames=mifAssociation.getParticipantTraversalNames();
		resolveClassAndTraversalName(mifClass, traversalnames);

		for (MIFClass choiceToNode:mifClass.getSortedChoices())
		{
			if (choiceToNode.isChoiceSelected())
			{
				//choiceClassNode
				DefaultMutableTreeNode choicClassNode=buildMIFClassNode(choiceToNode, null);
				int choiceChildrenCnt=choicClassNode.getChildCount();
				for (int childCnt=0;childCnt<choiceChildrenCnt;childCnt++)
				{
					rtnNode.add((DefaultMutableTreeNode)choicClassNode.getChildAt(0));
				}
				break;//there is only node can be selected
			}
		}

		//process non-CMETReference class
		if (!mifClass.getSortedAttributes().isEmpty())
		{
			for (Object attr:mifClass.getSortedAttributes())
				rtnNode.add(buildMIFAttributeNode((MIFAttribute)attr));
		}

		if (!mifClass.getSortedAssociations().isEmpty())
		{
			for (Object assc:mifClass.getSortedAssociations())
			{
				MIFAssociation mifAssc=(MIFAssociation)assc;
				//only display the mandatory association
				if (mifAssc.getMinimumMultiplicity()==0&&!mifAssc.isOptionChosen())
					continue;
				rtnNode.add(buildMIFAssociationNode(mifAssc));
			}
		}
		return rtnNode;
	}

	private void resolveClassAndTraversalName(MIFClass mifClass, Hashtable<String, String> traversalnames)
	{
		if (!treeEditable)
			return;

		HashSet <MIFClass>resolvedChoices=new HashSet<MIFClass>();
		for (MIFClass choiceClass:mifClass.getChoices())
		{
			//load the choice and resolve internal reference
			if (!choiceClass.getReferenceName().equals(""))
			{
				MIFClass referencedMifClass =loadReferenceMIFClass(choiceClass.getReferenceName(), null);
				if (referencedMifClass != null)
				{
					//there is not "traversalName" issue to load the CMET class for a choice item
					referencedMifClass.setChoiceSelected(choiceClass.isChoiceSelected());
					String refTraversal=traversalnames.get(choiceClass.getReferenceName());
					if (refTraversal!=null)
						referencedMifClass.setTraversalName(refTraversal);
					else
						referencedMifClass.setTraversalName(choiceClass.getTraversalName());

					referencedMifClass.setSortKey(choiceClass.getSortKey());
					resolvedChoices.add(referencedMifClass);
				}
				else
				{
					if (choiceClass.getTraversalName()==null
							||choiceClass.getTraversalName().equalsIgnoreCase(""))
						choiceClass.setTraversalName(traversalnames.get(choiceClass.getName()));

					resolvedChoices.add(choiceClass);
					Log.logError(this, "Not Found..:"+choiceClass.getReferenceName());
				}
			}
			else
			{
				if (choiceClass.getTraversalName()==null
						||choiceClass.getTraversalName().equalsIgnoreCase(""))
				{
					choiceClass.setTraversalName(traversalnames.get(choiceClass.getName()));
					Log.logError(this, "choice class ...:"+ choiceClass.getName()+"...set traversalName..:"+choiceClass.getTraversalName());
				}
				resolvedChoices.add(choiceClass);
			}
			if (choiceClass.isAbstractDefined())
			{
//				for (MIFClass childChoice:choiceClass.getChoices())
					resolveClassAndTraversalName(choiceClass, traversalnames);
			}
		}
		mifClass.setChoice(resolvedChoices);
	}

	private MIFClass loadReferenceMIFClass(String refClassName, Hashtable asscRefTraversalNames)
	{
		Log.logInfo(this,"Loading class reference..className:"+refClassName +"..rootClass:"+rootMIFClass.getName());
		MIFClass rtnMif=null;
		if (refClassName.equals(rootMIFClass.getName()))
		{
			//resolve reference with root MIFClass
			//05-20-2008, this section may never be visited.
				try {
					Log.logInfo(this, "Root class message type:"+rootMIFClass.getMessageType());
					String refClassMIFFileName=MIFIndexParser.loadMIFIndex().findMIFFileName(rootMIFClass.getMessageType());
					Log.logInfo(this, "Resolving root class referenece:"+refClassName);
					rtnMif=(MIFClass)MIFParserUtil.getMIFClass(refClassMIFFileName).clone();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		else
		{
			MIFClass refMif=MIFUtil.findLocalRefenceClass(rootMIFClass, refClassName);
			/*
			 * A local classReference may point to
			 * 1. a ClassDefinition section-- a locally defined class without referenceName
			 * 2. a CommonModelReference section -- the locally defined class with "referenceName", @see CMETRefParser
			 *
			 */
			if (refMif!=null&&refMif.getReferenceName().equals(""))
				rtnMif=(MIFClass)refMif.clone(); //find the referenced class and it is not a CMET
			else
			{
				//loading the referenced MIF class from CMET
				CMETRef cmetRef = CMETUtil.getCMET(refClassName);
				if (cmetRef != null)
				{
					String cmetMifName=cmetRef.getFilename() + ".mif";
					Log.logInfo(this, "load commonModelElementRef:"+cmetMifName);
					rtnMif = (MIFClass)MIFParserUtil.getMIFClass(cmetMifName).clone();
				}
				else
				{
					Log.logError(this, "Not Found..:"+refClassName);
				}
			}
		}
//		set traversal name with
		if (rtnMif!=null&&asscRefTraversalNames!=null)
		{
			if(rtnMif.getSortedChoices()==null
					||rtnMif.getSortedChoices().isEmpty())
				Log.logInfo(this, "No choice classes needs to set..:"+rtnMif);
			else
			{
				for(MIFClass refChoice:rtnMif.getSortedChoices())
				{
					refChoice.setTraversalName((String)asscRefTraversalNames.get(refChoice.getName()));
				}
			}
		}
		return rtnMif;
	}
	private DefaultMutableTreeNode buildMIFAssociationNode(MIFAssociation mifAssc)
	{
		MIFClass asscMifClass=mifAssc.getMifClass();
		DefaultMutableTreeNode childNode=null;
		if (treeEditable)
		{
			//load reference class
			if(!asscMifClass.getReferenceName().equals(""))
			{//load CMetReference
				String asscMIFClassRefName=asscMifClass.getReferenceName();
				MIFClass referedMifClass=loadReferenceMIFClass(asscMIFClassRefName,mifAssc.getParticipantTraversalNames());
				if(referedMifClass!=null)
					mifAssc.setMifClass(referedMifClass);

			}
		}
		childNode=buildMIFClassNode(mifAssc.getMifClass(), mifAssc);
		childNode.setUserObject(mifAssc);
		return childNode;
	}


	private DefaultMutableTreeNode buildMIFAttributeNode(MIFAttribute mifAttribute)
	{
		DefaultMutableTreeNode rtnNode=constructTreeNodeBasedOnTreeType(mifAttribute,true);
		mifAttribute.setEnabled(true);
		if (MIFUtil.isTreatedAsSimpleType(mifAttribute.getType())
				&&mifAttribute.isStrutural())
		{
			rtnNode.setAllowsChildren(false);
			return rtnNode;
		}
		TreeSet dtAttrs=null;
			if (mifAttribute.getDatatype()==null)
			{
				//load from DataType spec for the new HL7 specification
				String mifAttrType=mifAttribute.getType();
				if (mifAttrType.equals("GTS"))
				{
					String msg=mifAttribute.getNodeXmlName()+" -- load type:GTS from type:IVL_TS";
					Log.logWarning(this, msg);
					mifAttrType="IVL_TS";

				}
				Datatype dType=mifAttribute.getDatatype();
				if (dType==null)
				{
					dType=(Datatype)DatatypeParserUtil.getDatatype(mifAttrType).clone();
					mifAttribute.setEnabled(true);
				}
				mifAttribute.setDatatype(dType);
				enableDatatypeAttributesComplextype(dType, newSpecificationFlag);
				dtAttrs=MIFUtil.sortDatatypeAttribute(dType);
				if (mifAttrType.equals("GTS"))
					mifAttribute.getDatatype().setSimple(false);
			}
			else
			{
				mifAttribute.setEnabled(true);
				enableDatatypeAttributesComplextype(mifAttribute.getDatatype(), newSpecificationFlag);
				dtAttrs=MIFUtil.sortDatatypeAttribute(mifAttribute.getDatatype());
			}

			String dtTypeName=mifAttribute.getDatatype().getName();
			if (mifAttribute.getDatatype().isAbstract())
			{
				Datatype concretDt=mifAttribute.getConcreteDatatype();
				if (concretDt!=null)
				{
					enableDatatypeAttributesComplextype(concretDt, newSpecificationFlag);
					if (concretDt.getName().equals("GTS"))
						concretDt.setSimple(false);
					dtAttrs=MIFUtil.sortDatatypeAttribute(concretDt);//concretDt.getAttributes();
					dtTypeName=concretDt.getName();
				}
			}

			Iterator dtAttrIt=dtAttrs.iterator();
	    	while (dtAttrIt.hasNext())
	    	{
	    		Attribute childAttr = (Attribute)dtAttrIt.next();
				if (!childAttr.isProhibited()&&childAttr.isValid())
				{
					//and only the the chosen Datatype field for "AD" attributes
					if (childAttr.isOptionChosen()
							||!dtTypeName.equals("AD"))
					{
						DefaultMutableTreeNode childNode=buildDatatypeAttributeNode(childAttr);
						rtnNode.add(childNode);
					}
				}
			}

		return rtnNode;
	}

	private DefaultMutableTreeNode buildDatatypeAttributeNode(Attribute dtAttr)
	{
		DefaultMutableTreeNode rtnNode=constructTreeNodeBasedOnTreeType(dtAttr,false); //new DefaultMutableTreeNode(dtAttr);
		enableNullFlavorAttribute(dtAttr);
		if (dtAttr.isSimple())
			return rtnNode;

		if(dtAttr.isEnabled())
		{
			rtnNode=constructTreeNodeBasedOnTreeType(dtAttr,true);
			if (dtAttr.getReferenceDatatype()==null)
			{
				Datatype attrDataType=(Datatype)DatatypeParserUtil.getDatatype(dtAttr.getType()).clone();
				enableDatatypeAttributesComplextype(attrDataType,false);
				dtAttr.setReferenceDatatype(attrDataType);
			}
//			Hashtable childAttrHash=dtAttr.getReferenceDatatype().getAttributes();
			TreeSet dtAttrSet=MIFUtil.sortDatatypeAttribute(dtAttr.getReferenceDatatype());
			Iterator dtAttrIt=dtAttrSet.iterator();
	    	while (dtAttrIt.hasNext())
	    	{
	    		Attribute childAttr = (Attribute)dtAttrIt.next();
				if (!childAttr.isProhibited()&&childAttr.isValid())
				{
					DefaultMutableTreeNode childNode=buildDatatypeAttributeNode(childAttr);
					rtnNode.add(childNode);
				}
			}
//			Enumeration childAttrsEnum=childAttrHash.elements();
//			while (childAttrsEnum.hasMoreElements())
//			{
//				Attribute childAttr=(Attribute)childAttrsEnum.nextElement();
//				if (!childAttr.isProhibited()&&childAttr.isValid())
//				{
//					DefaultMutableTreeNode childNode=buildDatatypeAttributeNode(childAttr);
//					rtnNode.add(childNode);
//				}
//			}
		}
		return rtnNode;
	}
	/**
	 * Check the Datatype associated with each attribute and enable their complexType
	 * @param dtType
	 * @param usePreference
	 */
	private void enableDatatypeAttributesComplextype(Datatype dtType,boolean usePreference)
	{
		//enable all the data type element as loading for the first time
		Enumeration childAttrsEnum=dtType.getAttributes().elements();
		while (childAttrsEnum.hasMoreElements())
		{
			Attribute childAttr=(Attribute)childAttrsEnum.nextElement();
			if (childAttr.getReferenceDatatype()!=null)
				continue;
			childAttr.setEnabled(true);
			childAttr.setSimple(true);
			Datatype childDataType=null;
			if (childAttr.getType()!=null&&!childAttr.getType().equals(""))
				childDataType=(Datatype)DatatypeParserUtil.getDatatype(childAttr.getType()).clone();

			if (childDataType!=null&&!childDataType.isSimple())
			{
				//only check the datatype of an attribute
				childAttr.setEnabled(false);
				//use user's preference to enable complexType
				if (usePreference)
					childAttr.setEnabled(complexTypeEnabled);
				//force to enable selected attribute for name and address
				if (childAttr.isOptionChosen()||CaadapterUtil.getMandatorySelectedAttributes().contains(childDataType.getName()))
				{	childAttr.setEnabled(true);
					childAttr.setOptionChosen(true);
				}

				if (!childAttr.isEnabled())
				{
					//enable all complex Abstract datatype
					if (childDataType.isAbstract())
						childAttr.setEnabled(true);
					else if (!childDataType.getName().equals(childAttr.getType()))
						childAttr.setEnabled(true);
				}
				childAttr.setSimple(false);
			}
		}

		if (MIFUtil.isInlineTextRequired(dtType.getName())
				&&dtType.getAttributes().get("inlineText")==null)//(CaadapterUtil.getInlineTextAttributes().contains(dtType.getName()))
		{ //the "inlineText" should be added only in the first loading....
			Attribute inlineText=new Attribute();
			inlineText.setName("inlineText");
			inlineText.setMax(1);
			inlineText.setMin(1);
			inlineText.setSimple(true);
			inlineText.setAttribute(true);
			//always being selected/mandatory
			inlineText.setOptionChosen(true);
			dtType.addAttribute(inlineText.getName(),inlineText);
			inlineText.setSortKey(dtType.getAttributes().keySet().size());
		}

	}

	private DefaultMutableTreeNode constructTreeNodeBasedOnTreeType(Object userObject, boolean allowsChildren)
	{
		DefaultMutableTreeNode node=null;
		if (isMappingTree)
			node = new DefaultTargetTreeNode(userObject, allowsChildren);
		else
			node=new DefaultHSMTreeMutableTreeNode(userObject,allowsChildren, rootMIFClass);
		return node;
	}

	/**
	 * Recursively find each node's absoulte path, and index its user object
	 * @param node
	 */
	private void setXmlpathForUserObject(DefaultMutableTreeNode node)
	{

		if (node==null)
		{
			System.out.println("NewHSMBasicNodeLoader.setXmlpathForUserObject()..:"+node);
			return;
		}
		DefaultMutableTreeNode parentNode=(DefaultMutableTreeNode)node.getParent();
		Object userObj=node.getUserObject();

		if (parentNode!=null)
		{
			Object parentObj=parentNode.getUserObject();
			if (parentObj instanceof DatatypeBaseObject)
			{
				DatatypeBaseObject dtParentObj=(DatatypeBaseObject)parentObj;
				if (userObj instanceof DatatypeBaseObject )
					((DatatypeBaseObject)userObj).setParentXmlPath(dtParentObj.getXmlPath());
				else
					System.out
							.println("NewHSMBasicNodeLoader.setXmlpathForUserObject()...not set field:"+userObj);
			}
			else
				((DatatypeBaseObject)userObj).setParentXmlPath("");
		}

		if (node.getChildCount()>0)
		{
			Enumeration childEnum=node.children();
			while (childEnum.hasMoreElements())
			{
				setXmlpathForUserObject((DefaultMutableTreeNode)childEnum.nextElement());
			}
		}
	}

	private void enableNullFlavorAttribute(Attribute dtAttr)
	{
		if (!treeEditable)
			return;

		//enable the "nullFlavor" attribute for HL7 specification panel
		if (!dtAttr.getName().equals("nullFlavor"))
			return;
		//use global preference for new specification or user's request
		if (newSpecificationFlag)
			dtAttr.setEnabled(nullFlavorEnabled);
	}

}
/**
 *HISTORY 	:$Log: not supported by cvs2svn $
 *HISTORY 	:Revision 1.51  2009/03/12 15:02:12  wangeug
 *HISTORY 	:support multiple HL& normatives
 *HISTORY 	:
 *HISTORY 	:Revision 1.50  2009/02/12 20:37:42  wangeug
 *HISTORY 	:use sortedChoice() to include all choiceItems from sub-list
 *HISTORY 	:
 *HISTORY 	:Revision 1.49  2008/12/23 17:37:12  wangeug
 *HISTORY 	:Process MIFClass with isAbstract=true:clean code
 *HISTORY 	:
 *HISTORY 	:Revision 1.48  2008/12/23 14:36:50  wangeug
 *HISTORY 	:use MIFClass.getChoice() to replace MIFClass.getSortedChoice()
 *HISTORY 	:
 *HISTORY 	:Revision 1.47  2008/12/18 19:11:40  wangeug
 *HISTORY 	:Return all item to be selected. If an item is a list of other MIFClass, all these children are promoted to top level as choice item
 *HISTORY 	:
 *HISTORY 	:Revision 1.46  2008/12/18 17:15:29  wangeug
 *HISTORY 	:MIF Parsing: A item of a choice is a list of other MIFClass.
 *HISTORY 	:
 *HISTORY 	:Revision 1.45  2008/12/18 14:20:30  wangeug
 *HISTORY 	:correct logging method:use logInfor rather than logError
 *HISTORY 	:
 *HISTORY 	:Revision 1.44  2008/10/15 20:59:58  wangeug
 *HISTORY 	:fix bug: only add "inlineText" attribute if not exist
 *HISTORY 	:
 *HISTORY 	:Revision 1.43  2008/09/23 15:19:50  wangeug
 *HISTORY 	:caAdapter 4.2 alpha release
 *HISTORY 	:
 *Revision 1.42  2008/09/23 15:12:14  wangeug
 *caAdapter 4.2 alpha release
 *
 *
 */