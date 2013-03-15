/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.tree;

import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFUtil;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmps.core.ElementMeta;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Color;
import java.awt.Component;

/**
 * The class defines the default tree cell renderer for mapping panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.16 $
 *          date        $Date: 2009-01-16 15:20:06 $
 */
public class DefaultMappingTreeCellRender extends DefaultTreeCellRenderer //extends JPanel implements TreeCellRenderer
{
	private static final Color NONDRAG_COLOR = new Color(140, 140, 175);
	private static final Color MANYTOONE_COLOR = Color.pink;
	private static final Color DISABLED_CHOICE_BACK_GROUND_COLOR = new Color(100, 100, 100);
//	private static ImageIcon pseudoRootIcon = new ImageIcon(DefaultSettings.getImage("pseudo_root.gif"));
	private static ImageIcon disableItemIcon = new ImageIcon(DefaultSettings.getImage("blue.png"));
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		if (!selected)
		{
			setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			if (node.getUserObject() instanceof AssociationMetadata) {
				AssociationMetadata assoMeta = (AssociationMetadata)node.getUserObject();
				if (!assoMeta.getNavigability())
					setBackgroundNonSelectionColor(NONDRAG_COLOR);
				if (assoMeta.getNavigability()&&!assoMeta.isBidirectional()&&assoMeta.isManyToOne())
					setBackgroundNonSelectionColor(MANYTOONE_COLOR);
			}
		}
		Component returnValue = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object usrObj=node.getUserObject();

		if (usrObj instanceof ElementMeta)
		{
			ElementMeta elmntMeta=(ElementMeta)usrObj;
			String cellText=elmntMeta.getName().replace(".", "_");
	    	if (cellText.indexOf(":")>-1)
	    		cellText=cellText.substring(cellText.lastIndexOf(":")+1); // remove the leading XML namespace value
	    	if (elmntMeta.getMinOccurs()!= elmntMeta.getMaxOccurs())
	    	{
		    	cellText=cellText+" ["+elmntMeta.getMinOccurs()+" ... ";
		    	if (elmntMeta.getMaxOccurs()!=null&&elmntMeta.getMaxOccurs().intValue()==-1)
		    		cellText=cellText+"*]";
		    	else
		    		cellText=cellText+elmntMeta.getMaxOccurs()+"]";
	    	}
	    	setText(cellText);
		}
		else if (usrObj instanceof DatatypeBaseObject)
		{
			DatatypeBaseObject nodeBase=(DatatypeBaseObject)usrObj;
			returnValue.setEnabled(nodeBase.isEnabled());
			if (!nodeBase.isEnabled())
			{
				setIcon(disableItemIcon);
				returnValue.setBackground(DISABLED_CHOICE_BACK_GROUND_COLOR);
			}
			String treeCellText=nodeBase.getName();
			if (nodeBase instanceof Attribute)
			{
				Attribute dtAttr=(Attribute)nodeBase;
				if (dtAttr.getMultiplicityIndex()>0)
					treeCellText=treeCellText+"  ["+(dtAttr.getMultiplicityIndex()+1) +"]";
				else if(dtAttr.getReferenceDatatype()!=null&&!MIFUtil.isTreatedAsSimpleType(dtAttr.getType()))
				{
					DefaultMutableTreeNode currentNode=(DefaultMutableTreeNode)value;
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

					}
					int attrCnt=0;
					if (parentDt!=null)
						attrCnt=MIFUtil.findDatatypeAttributeWithName(parentDt, dtAttr.getName()).size();
					if (attrCnt>1)
						treeCellText=treeCellText+"  [1]";
				}
			}
			else if(nodeBase instanceof MIFClass)
			{
				String cpYears=((MIFClass)nodeBase).getCopyrightYears();
				if (cpYears!=null&&!cpYears.equals(""))
				treeCellText=treeCellText+" ( copyrightYears: "+cpYears+" )";
			}
			else if(nodeBase instanceof MIFAttribute)
			{
				MIFAttribute mifAttr=(MIFAttribute)nodeBase;
				if(mifAttr.getMultiplicityIndex()>0)
					treeCellText=treeCellText+"  ["+(mifAttr.getMultiplicityIndex()+1) +"]";
				else
				{
					if (mifAttr.getMaximumMultiplicity()!=1)
					{
						Object parentObj=((DefaultMutableTreeNode)((DefaultMutableTreeNode)value).getParent()).getUserObject();
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
							if (mifAssc.isChoiceSelected()
									&&attrMultiplicity!=1)
							{
								MIFClass choiceClass=mifAssc.findChoiceSelectedMifClass();
								attrMultiplicity=choiceClass.getMaxAttributeMultiplicityWithName(mifAttr.getName());
							}
						}

						if (attrMultiplicity!=1)
							treeCellText=treeCellText +"  [1]";
					}

				}
			}
			else if(nodeBase instanceof MIFAssociation)
			{
				MIFAssociation mifAssc=(MIFAssociation)nodeBase;
				if (mifAssc.isChoiceSelected())
					treeCellText=mifAssc.getNodeXmlName();
				if (mifAssc.getMultiplicityIndex()>0)
				{
					treeCellText=treeCellText+ "  ["+(mifAssc.getMultiplicityIndex()+1) +"]";
					if (mifAssc.getMaximumMultiplicity()==1)
						treeCellText=treeCellText+"  [Alert: For Choice]";
				}
				else
				{
					Object parentObj=((DefaultMutableTreeNode)((DefaultMutableTreeNode)value).getParent()).getUserObject();
					MIFClass parentMIFClass=null;
					int asscMultiplicity=0;//parentMIFClass.getMaxAssociationMultiplicityWithName(mifAssc.getName());
					if (parentObj instanceof MIFClass)
					{
						parentMIFClass=(MIFClass)parentObj;
						asscMultiplicity=parentMIFClass.getMaxAssociationMultiplicityWithName(mifAssc.getName());
					}
					else if (parentObj instanceof MIFAssociation )
					{
						MIFAssociation parentMifAssc=(MIFAssociation)parentObj;
						parentMIFClass=parentMifAssc.getMifClass();//.getReferencedMifClass();
						asscMultiplicity=parentMIFClass.getMaxAssociationMultiplicityWithName(mifAssc.getName());
						if (asscMultiplicity==1&&parentMifAssc.isChoiceSelected())
						{
							//this association may belong to a chosen MIFClass
							MIFClass asscChosenClass=parentMifAssc.findChoiceSelectedMifClass();
							if (asscChosenClass!=null)
								asscMultiplicity=asscChosenClass.getMaxAssociationMultiplicityWithName(mifAssc.getName());
						}
					}
					if (asscMultiplicity!=1)
					{
						treeCellText=treeCellText+"  [1]";
						if (mifAssc.getMaximumMultiplicity()==1)
							treeCellText=treeCellText+"  [Alert: For Choice]";
					}
				}
			}
			setText(treeCellText);
		}
		return returnValue;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.15  2008/11/03 21:38:46  wangeug
 * HISTORY      : set xmlPath name of V2Meta element: replacing "." with "_"
 * HISTORY      :
 * HISTORY      : Revision 1.14  2008/10/07 15:16:55  wangeug
 * HISTORY      : clean code/remove unnecessary file for 4.2 release
 * HISTORY      :
 * HISTORY      : Revision 1.13  2008/09/24 18:00:29  phadkes
 * HISTORY      : Changes for code standards
 * HISTORY      :
*/
