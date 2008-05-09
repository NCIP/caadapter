/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/nodeloader/NewHSMBasicNodeLoader.java,v 1.34 2008-05-09 16:33:41 umkis Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 *
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 *
 *
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 *
 *
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear.
 *
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software.
 *
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick.
 *
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
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
import gov.nih.nci.caadapter.hl7.mif.MIFIndexParser;
import gov.nih.nci.caadapter.hl7.mif.MIFUtil;
import gov.nih.nci.caadapter.hl7.mif.XmlToMIFImporter;
import gov.nih.nci.caadapter.hl7.mif.v1.CMETUtil;
import gov.nih.nci.caadapter.hl7.mif.v1.MIFParserUtil;
import gov.nih.nci.caadapter.ui.common.preferences.PreferenceManager;
//import gov.nih.nci.caadapter.ui.common.tree.DefaultMappableTreeNode;
import gov.nih.nci.caadapter.ui.common.tree.DefaultHSMTreeMutableTreeNode;
import gov.nih.nci.caadapter.ui.common.tree.DefaultTargetTreeNode;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

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
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.34 $
 *          date        $Date: 2008-05-09 16:33:41 $
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
//		DefaultMappableTreeNode node = new DefaultMappableTreeNode("Target Tree", true);
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
			FileInputStream fis;
			try {
				File file=(File)o;
				if (file.getName().endsWith(".xml"))
				{
					XmlToMIFImporter mifImport=new XmlToMIFImporter();
					rootMIFClass=mifImport.importMifFromXml(file);
				}
				else
				{
				fis = new FileInputStream ((File)o);
				ObjectInputStream ois = new ObjectInputStream(fis);
				rootMIFClass = (MIFClass)ois.readObject();
	    		ois.close();
	    		fis.close();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				Log.logException(this, e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.logException(this, e);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				Log.logException(this, e);
			}

		}
		if (rootMIFClass!=null)
			root = buildObjectNode(rootMIFClass, null);
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
		if(!mifClass.getSortedChoices().isEmpty())
		{
			HashSet <MIFClass>resolvedChoices=new HashSet<MIFClass>();
			if (treeEditable)
			{
				for (MIFClass choiceClass:mifClass.getSortedChoices())
				{
					//load the choice and resolve internal reference
					if (!choiceClass.getReferenceName().equals(""))
					{
						MIFClass referencedMifClass =this.loadReferenceMIFClass(choiceClass.getReferenceName(), null);
						if (referencedMifClass != null)
						{
							//there is not "traversalName" issue to load the CMET class for a choice item
							referencedMifClass.setChoiceSelected(choiceClass.isChoiceSelected());
							if (mifAssociation == null)
							{
								referencedMifClass.setTraversalName(choiceClass.getTraversalName());
							}
							else {
								Hashtable<String, String> traversalnames = mifAssociation.getParticipantTraversalNames();
								if (traversalnames.get(choiceClass.getReferenceName())!= null)
								{
									referencedMifClass.setTraversalName(traversalnames.get(choiceClass.getReferenceName()));
								}
								else
								{
									referencedMifClass.setTraversalName(choiceClass.getTraversalName());
								}
							}
							referencedMifClass.setSortKey(choiceClass.getSortKey());
							resolvedChoices.add(referencedMifClass);
						}
						else
						{
							resolvedChoices.add(choiceClass);
							Log.logError(this, "Not Found..:"+choiceClass.getReferenceName());
						}
					}
					else
						resolvedChoices.add(choiceClass);
				}
				mifClass.setChoice(resolvedChoices);
			}
		}
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
                //System.out.println("QQQQQ 5 mifAssc : " + mifAssc.getName());
                rtnNode.add(buildMIFAssociationNode(mifAssc));    //================================
			}
		}
		return rtnNode;
	}

	private MIFClass loadReferenceMIFClass(String refClassName, Hashtable asscRefTraversalNames)
	{
		MIFClass rtnMif=null;
		if (refClassName.equals(rootMIFClass.getName()))
		{
			//resolve reference with root MIFClass

				try {
					String refClassMIFFileName=MIFIndexParser.loadMIFInfos().findMIFFileName(rootMIFClass.getMessageType());
					Log.logInfo(this, "Resolving referenece:"+refClassName);
					rtnMif=(MIFClass)MIFParserUtil.getMIFClass(refClassMIFFileName).clone();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				rtnMif.setReferenceLevel(1);
		}
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
//		set traversal name with
		if (rtnMif!=null&&asscRefTraversalNames!=null)
		{
			if(rtnMif.getChoices()==null
					||rtnMif.getChoices().isEmpty())
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
			String asscMIFClassRefName=asscMifClass.getReferenceName();
			if(!asscMIFClassRefName.equals(""))
			{
				MIFClass referedMifClass=loadReferenceMIFClass(asscMIFClassRefName,mifAssc.getParticipantTraversalNames());
				if(referedMifClass!=null)
					mifAssc.setMifClass(referedMifClass);

			}
		}
        MIFClass mifClass = mifAssc.getMifClass();
        //System.out.println("QQQQQ 6 mifClass : "+mifClass.getName()+ ", mifAssc : " + mifAssc.getName());


        childNode=buildMIFClassNode(mifClass, mifAssc); //========================
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
			if (mifAttribute.getDatatype()==null)
			{
				//load from DataType spec for the new HL7 specification

                String mifAttrType=mifAttribute.getType();

                //System.out.println("QQQQ1 : " + mifAttrType);

                if (mifAttrType.equals("GTS"))
				{
					String msg=mifAttribute.getNodeXmlName()+" -- load type:GTS from type:IVL_TS";
					Log.logWarning(this, msg);
					mifAttrType="IVL_TS";

				}
				Datatype dType=mifAttribute.getDatatype();
				if (dType==null)
				{
                    if (DatatypeParserUtil.getDatatype(mifAttrType) == null) dType=(Datatype)DatatypeParserUtil.getDatatype("ST").clone();//System.out.println("QQQQ2 : " + mifAttrType);

                    else dType=(Datatype)DatatypeParserUtil.getDatatype(mifAttrType).clone();
					mifAttribute.setEnabled(true);
				}
				mifAttribute.setDatatype(dType);
				enableDatatypeAttributesComplextype(dType, newSpecificationFlag);
				if (mifAttrType.equals("GTS"))
					mifAttribute.getDatatype().setSimple(false);
			}
			else
				mifAttribute.setEnabled(true);

			Hashtable dtAttrs=mifAttribute.getDatatype().getAttributes();
			String dtTypeName=mifAttribute.getDatatype().getName();
			if (mifAttribute.getDatatype().isAbstract())
			{
				Datatype concretDt=mifAttribute.getConcreteDatatype();
				if (concretDt!=null)
				{
					enableDatatypeAttributesComplextype(concretDt, newSpecificationFlag);
					if (concretDt.getName().equals("GTS"))
						concretDt.setSimple(false);
					dtAttrs=concretDt.getAttributes();
					dtTypeName=concretDt.getName();
				}
			}

			ArrayList<String> keyList=new ArrayList<String>();
 			Enumeration keyEnums=dtAttrs.keys();
			while (keyEnums.hasMoreElements())
				keyList.add((String)keyEnums.nextElement());
			Collections.sort(keyList);
			for(String attrName:keyList)
			{
				Attribute childAttr=(Attribute)dtAttrs.get(attrName);
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
//			Enumeration childAttrsEnum=dtAttrs.elements();
//
//			while (childAttrsEnum.hasMoreElements())
//			{
//				Attribute childAttr=(Attribute)childAttrsEnum.nextElement();
//				if (!childAttr.isProhibited()&&childAttr.isValid())
//				{
//					//and only the the chosen Datatype field for "AD" attributes
//					if (childAttr.isOptionChosen()
//							||!dtTypeName.equals("AD"))
//					{
//						DefaultMutableTreeNode childNode=buildDatatypeAttributeNode(childAttr);
//						rtnNode.add(childNode);
//					}
//				}
//			}
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
			Hashtable childAttrHash=dtAttr.getReferenceDatatype().getAttributes();
			ArrayList<String> keyList=new ArrayList<String>();
			Enumeration keyEnums=childAttrHash.keys();
			while (keyEnums.hasMoreElements())
				keyList.add((String)keyEnums.nextElement());
			Collections.sort(keyList);
			for(String attrName:keyList)
			{
				Attribute childAttr=(Attribute)childAttrHash.get(attrName);
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
        //dtType.getAttributes().
        while (childAttrsEnum.hasMoreElements())
		{
			Attribute childAttr=(Attribute)childAttrsEnum.nextElement();
            //System.out.println("QQQQ4 childAttr.getType()" + childAttr.getType());
            if (childAttr.getReferenceDatatype()!=null)
				continue;
			childAttr.setEnabled(true);
			childAttr.setSimple(true);
            //System.out.println("QQQQ5 childAttr.getType()" + childAttr.getType());
            String typ = childAttr.getType();
            if (typ.startsWith("xs:")) typ = "st";
            Datatype childDataType=(Datatype)DatatypeParserUtil.getDatatype(typ).clone();

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

		if (MIFUtil.isInlineTextRequired(dtType.getName()))//(CaadapterUtil.getInlineTextAttributes().contains(dtType.getName()))
		{
			Attribute inlineText=new Attribute();
			inlineText.setName("inlineText");
			inlineText.setMax(1);
			inlineText.setMin(1);
			inlineText.setSimple(true);
			//always being selected/mandatory
			inlineText.setOptionChosen(true);
			dtType.addAttribute(inlineText.getName(),inlineText);
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