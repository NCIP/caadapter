/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/tree/DefaultMappingTreeCellRender.java,v 1.10 2007-12-13 21:08:43 wangeug Exp $
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
 *          revision    $Revision: 1.10 $
 *          date        $Date: 2007-12-13 21:08:43 $
 */
public class DefaultMappingTreeCellRender extends DefaultTreeCellRenderer //extends JPanel implements TreeCellRenderer
{
	private static final Color NONDRAG_COLOR = new Color(140, 140, 175);
	private static final Color MANYTOONE_COLOR = Color.pink;
	private static final Color DISABLED_CHOICE_BACK_GROUND_COLOR = new Color(100, 100, 100);
	private static ImageIcon pseudoRootIcon = new ImageIcon(DefaultSettings.getImage("pseudo_root.gif"));
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
		if (value instanceof PseudoRootTreeNode)
		{
			setIcon(pseudoRootIcon);
		}
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		if (node.getUserObject() instanceof DatatypeBaseObject)
		{
			DatatypeBaseObject nodeBase=(DatatypeBaseObject)node.getUserObject();
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
//					count the number of duplicate Attribute with the same parent
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
//					else
//						treeCellText=treeCellText+"  [Multiple]";

				}
			}
			else if(nodeBase instanceof MIFClass)
			{
				setText(((MIFClass)nodeBase).getMessageType()+":"+((MIFClass)nodeBase).getNodeXmlName());
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



