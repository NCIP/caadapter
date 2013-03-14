/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.common.tree;

import javax.swing.JTree;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;
import java.awt.Color;

import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFUtil;
import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;

/**
 * This class defines a customized implementation of tree cell renderer to provide
 * additional look and feel for tree cells in HSMPanel.
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.15 $
 *          date        $Date: 2009-02-12 20:34:52 $
 */
public class MIFTreeCellRenderer extends DefaultTreeCellRenderer
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: MIFTreeCellRenderer.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/tree/MIFTreeCellRenderer.java,v 1.15 2009-02-12 20:34:52 wangeug Exp $";

	private static final Color DISABLED_CHOICE_BACK_GROUND_COLOR = new Color(100, 100, 100);

	private static final ImageIcon CLONE_IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("CloneNode.gif"));
	private static final ImageIcon CLONE_ATTRIBUTE_IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("CloneAttributeNode.gif"));
	private static final ImageIcon CLONE_DATATYPE_FIELD_IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("CloneDatatypeFieldNode.gif"));
	/**
	 * Configures the renderer based on the passed in components.
	 * The value is set from messaging the tree with
	 * <code>convertValueToText</code>, which ultimately invokes
	 * <code>toString</code> on <code>value</code>.
	 * The foreground color is set based on the selection and the icon
	 * is set based on on leaf and expanded.
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		Component rtnComp=super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if(value instanceof DefaultMutableTreeNode)
		{
			Object userObj = ((DefaultMutableTreeNode)value).getUserObject();
			if (userObj instanceof DatatypeBaseObject)
			{
				DatatypeBaseObject nodeBase=(DatatypeBaseObject)userObj;
				this.setEnabled(nodeBase.isEnabled());
				if (!nodeBase.isEnabled())
					setBackground(DISABLED_CHOICE_BACK_GROUND_COLOR);
			}

			if(userObj instanceof MIFClass)
			{
				MIFClass nodeMIFC=(MIFClass)userObj;
				String mifCViewName=nodeMIFC.getNodeXmlName();
				if (nodeMIFC.getMessageType()!=null)
					mifCViewName=nodeMIFC.getMessageType()+":"+mifCViewName;
				String mifClassCopyyear=nodeMIFC.getCopyrightYears();
				if (mifClassCopyyear!=null&&!mifClassCopyyear.equals(""))
					mifCViewName=mifCViewName+" (copyrightYears: "+mifClassCopyyear+" )";
				setText(mifCViewName);
				setIcon(CLONE_IMAGE_ICON);
			}
			else if(userObj instanceof MIFAttribute)
			{
				MIFAttribute mifAttr=(MIFAttribute)userObj;
				setText(setViewNameForMIFAttribute(mifAttr, (DefaultMutableTreeNode)value));
				setIcon(CLONE_ATTRIBUTE_IMAGE_ICON);
			}
			else if(userObj instanceof MIFAssociation)
			{
				MIFAssociation mifAssc=(MIFAssociation)userObj;
				setText(setViewNameForAssociation(mifAssc, (DefaultMutableTreeNode)value));
				setIcon(CLONE_IMAGE_ICON);
			}
			else if(userObj instanceof Attribute)
			{
				DefaultMutableTreeNode crntNode=(DefaultMutableTreeNode)value;
				setText(this.setViewNameForDatatypeAttribute((Attribute)userObj, (DefaultMutableTreeNode)value));
				if(crntNode.getChildCount()>0)
					setIcon(CLONE_ATTRIBUTE_IMAGE_ICON);
				else
					setIcon(CLONE_DATATYPE_FIELD_IMAGE_ICON);
			}
		}
		return rtnComp;
	}

	/**
	 * Set display name for a MIFAttribute tree node
	 * @param mifAttr
	 * @param currentNode
	 * @return
	 */
	private String setViewNameForDatatypeAttribute(Attribute dtAttr, DefaultMutableTreeNode currentNode)
	{
		String treeCellText=dtAttr.getName();
		//set any type
		Datatype dtDatatype=dtAttr.getReferenceDatatype();
		if (dtDatatype==null)
			return treeCellText;
		if (dtDatatype.isAbstract()||!dtDatatype.getName().equalsIgnoreCase(dtAttr.getType()))
		{
			treeCellText=treeCellText +"  [Abstract - "+dtAttr.getType();
			if (!dtDatatype.getName().equalsIgnoreCase(dtAttr.getType()))
			{
				treeCellText=treeCellText +":"+dtDatatype.getName();
			}
			treeCellText=treeCellText +"]";
		}
		//set cardinality
		if (dtAttr.getMax()==1)
			return treeCellText;
		else
		{
			if (dtAttr.getMultiplicityIndex()>0)
				treeCellText=treeCellText+"  ["+(dtAttr.getMultiplicityIndex()+1)+"]";
			else
			{
				//count the number of duplicate Attribute with the same parent
				DefaultMutableTreeNode parentNode=	(DefaultMutableTreeNode)currentNode.getParent();
				Object parentObj=parentNode.getUserObject();
				Datatype parentDt=null;
				if (parentObj instanceof Attribute)
					parentDt=((Attribute)parentObj).getReferenceDatatype();
				else if  (parentObj instanceof MIFAttribute)
				{
					MIFAttribute parentMifAttr=(MIFAttribute)parentObj;
					parentDt=parentMifAttr.getConcreteDatatype();
					if (parentDt==null)
						parentDt=parentMifAttr.getDatatype();
					int attrCnt=0;
					if (parentDt!=null)
						attrCnt=MIFUtil.findDatatypeAttributeWithName(parentDt, dtAttr.getName()).size();
					if (attrCnt>1)
						treeCellText=treeCellText+"  [1]";
					else
						treeCellText=treeCellText+"  [Multiple]";
				}

			}
		}

		return treeCellText;
	}


	/**
	 * Set display name for a MIFAttribute tree node
	 * @param mifAttr
	 * @param currentNode
	 * @return
	 */
	private String setViewNameForMIFAttribute(MIFAttribute mifAttr, DefaultMutableTreeNode currentNode)
	{
		String treeCellText=mifAttr.getName();
		//set any type
		Datatype mifDatatype=mifAttr.getDatatype();
		if (mifDatatype!=null&&mifDatatype.isAbstract())
		{
			treeCellText=treeCellText +"  [Abstract - "+mifAttr.getType();
			if (mifAttr.getConcreteDatatype()!=null)
			{
				treeCellText=treeCellText +":"+mifAttr.getConcreteDatatype().getName();
			}
			treeCellText=treeCellText +"]";
		}
		if (mifAttr.getMaximumMultiplicity()!=1&&mifAttr.getMultiplicityIndex()==0)
		{
			Object parentObj=((DefaultMutableTreeNode)currentNode.getParent()).getUserObject();
			MIFClass parentMIFClass=null;
			int attrMultiplicity=0;
			if (parentObj instanceof MIFClass)
			{
				parentMIFClass=(MIFClass)parentObj;
				attrMultiplicity=parentMIFClass.getMaxAttributeMultiplicityWithName(mifAttr.getName());
			}
			else if (parentObj instanceof MIFAssociation )
			{
				MIFAssociation mifAssc=(MIFAssociation)parentObj;
				parentMIFClass=mifAssc.getMifClass();
				attrMultiplicity=parentMIFClass.getMaxAttributeMultiplicityWithName(mifAttr.getName());
				//if (attrMultiplicity==1), this MIFAttribute belong to MIFAssociation
				if (mifAssc.isChoiceSelected()
						&&attrMultiplicity!=1)
				{
					MIFClass choiceClass=mifAssc.findChoiceSelectedMifClass();
					attrMultiplicity=choiceClass.getMaxAttributeMultiplicityWithName(mifAttr.getName());
				}
			}
			if (attrMultiplicity==1)
				treeCellText=treeCellText+"  [Multiple]";
			else
				treeCellText=treeCellText +"  [1]";
		}
		else if(mifAttr.getMaximumMultiplicity()!=1)
			treeCellText=treeCellText+"  ["+(mifAttr.getMultiplicityIndex()+1) +"]";

		return treeCellText;
	}

	/**
	 * Set display name for a MIFAssociation tree node
	 * @param mifAssc
	 * @param currentNode
	 * @return
	 */
	private String setViewNameForAssociation(MIFAssociation mifAssc, DefaultMutableTreeNode currentNode)
	{
		String viewName=mifAssc.getName();
		boolean hasChoice =MIFUtil.containChoiceAssociation(mifAssc);
		boolean multipleRequired=true;
		//check if "multiple" is required
		if (mifAssc.getMaximumMultiplicity()==1&&!hasChoice)
			multipleRequired=false;

		//find the existing multiplicity of current association
		boolean hasMoreThanOne=false;
		if (mifAssc.getMultiplicityIndex()>0)
			hasMoreThanOne=true;
		else
		{
			Object parentObj=((DefaultMutableTreeNode)currentNode.getParent()).getUserObject();
			int parentAsscCnt=0;
			if (parentObj instanceof MIFClass)
			{
				MIFClass parentMIFClass=(MIFClass)parentObj;
				parentAsscCnt=parentMIFClass.getMaxAssociationMultiplicityWithName(mifAssc.getName());

			}
			else if (parentObj instanceof MIFAssociation )
			{
				MIFAssociation parentMifAssc=(MIFAssociation)parentObj;
				MIFClass parentMIFClass=parentMifAssc.getMifClass();
				parentAsscCnt=parentMIFClass.getMaxAssociationMultiplicityWithName(mifAssc.getName());
				if (parentAsscCnt==0&&parentMifAssc.isChoiceSelected())
				{
					//the association may belong to a choosenItem
					MIFClass parentChosenMIFClass=parentMifAssc.findChoiceSelectedMifClass();
					parentAsscCnt=parentChosenMIFClass.getMaxAssociationMultiplicityWithName(mifAssc.getName());
				}
			}
			if (parentAsscCnt>1)
				hasMoreThanOne=true;
		}

		//set view text
		String viewIndex="";
		String viewIndexEnd="]";
		if (mifAssc.getMultiplicityIndex()==0)
		{
			if(multipleRequired)
			{
				viewIndex="  [Multiple";
				if (hasMoreThanOne)
					viewIndex="  [1";
			}
		}
		else
		{
			viewIndex="  ["+(mifAssc.getMultiplicityIndex()+1);
		}
		if (hasChoice)
			viewIndexEnd="--Alert:For Choice]";
		if (!viewIndex.equals(""))
			viewIndex=viewIndex+viewIndexEnd;

		MIFClass asscMIFClass=mifAssc.getMifClass();
		if(asscMIFClass.getSortedChoices().size()>0)
		{
			if (mifAssc.isChoiceSelected())
			{
				//show the selected choice
				viewName=mifAssc.getNodeXmlName();
				viewIndex= viewIndex+"  [Selected Choice -- for "+mifAssc.getName()+"]";
			}
			else
				viewIndex=viewIndex+ "  [Choice - Unselected]";
		}
		String showText=viewName+viewIndex;
		if (asscMIFClass.isReference())
		{
				showText=showText+" [Reference - " +asscMIFClass.getName()+ "]";
		}
		if (mifAssc.isOptionForced())
			showText=showText+" [Force XML]";
		return showText;
	}
}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.14  2009/01/16 15:20:06  wangeug
 * HISTORY      : 	display MIFclass.copyrightYears on tree node
 * HISTORY      :
 * HISTORY      : Revision 1.13  2008/09/24 18:00:29  phadkes
 * HISTORY      : Changes for code standards
 * HISTORY      :
*/
