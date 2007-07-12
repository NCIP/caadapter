/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/tree/MIFTreeCellRenderer.java,v 1.3 2007-07-12 16:08:15 wangeug Exp $
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
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2007-07-12 16:08:15 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/tree/MIFTreeCellRenderer.java,v 1.3 2007-07-12 16:08:15 wangeug Exp $";

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
				setIcon(CLONE_IMAGE_ICON);	
			}
			else if(userObj instanceof MIFAttribute)
			{
				MIFAttribute mifAttr=(MIFAttribute)userObj;
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
					Object parentObj=((DefaultMutableTreeNode)((DefaultMutableTreeNode)value).getParent()).getUserObject();
					MIFClass parentMIFClass=null;
					if (parentObj instanceof MIFClass)
						parentMIFClass=(MIFClass)parentObj;
					else if (parentObj instanceof MIFAssociation )
					{
						MIFAssociation mifAssc=(MIFAssociation)parentObj;
						parentMIFClass=mifAssc.getMifClass();
					}
					int attrMultiplicity=parentMIFClass.getMaxAttributeMultiplicityWithName(mifAttr.getName());
					
					if (attrMultiplicity==1)
						treeCellText=treeCellText+"  [Multiple]";
					else
						treeCellText=treeCellText +"  [1]";
				}
				else if(mifAttr.getMaximumMultiplicity()!=1)
					treeCellText=treeCellText+"  ["+(mifAttr.getMultiplicityIndex()+1) +"]";					
				
				setText(treeCellText);
				setIcon(CLONE_ATTRIBUTE_IMAGE_ICON);
			}
			else if(userObj instanceof MIFAssociation)
			{
				MIFAssociation mifAssc=(MIFAssociation)userObj;
				String showText=mifAssc.getName(); //.toString();
				
				if (mifAssc.getMaximumMultiplicity()!=1&&mifAssc.getMultiplicityIndex()==0)
				{
					Object parentObj=((DefaultMutableTreeNode)((DefaultMutableTreeNode)value).getParent()).getUserObject();
					MIFClass parentMIFClass=null;
					if (parentObj instanceof MIFClass)
						parentMIFClass=(MIFClass)parentObj;
					else if (parentObj instanceof MIFAssociation )
					{
						MIFAssociation parentMifAssc=(MIFAssociation)parentObj;
						parentMIFClass=parentMifAssc.getMifClass();//.getReferencedMifClass();
					}
					
					int asscMultiplicity=parentMIFClass.getMaxAssociationMultiplicityWithName(mifAssc.getName());
					if (asscMultiplicity==1)
						showText=showText+"  [Multiple]";
					else
						showText=showText+"  [1]";
				}
				else if (mifAssc.getMaximumMultiplicity()!=1)
					showText=showText+ "  ["+(mifAssc.getMultiplicityIndex()+1) +"]";
				
				MIFClass asscMIFClass=mifAssc.getMifClass();
				if(asscMIFClass.getChoices().size()>0)
				{
					if (mifAssc.isChoiceSelected())
						showText= showText+"  [Selected Choice(s)]";
					else
						showText=showText+ "  [Choice - Unselected]";
				}
				
				if (asscMIFClass.isReference())//.getReferenceName().equals(""))
				{
						showText=showText+" [Reference - " +asscMIFClass.getName()+ "]";
				}
				if (mifAssc.isOptionForced())
					showText=showText+" [Force XML]";
				setText(showText);			
				setIcon(CLONE_IMAGE_ICON);	
			}
			else if(userObj instanceof Attribute)
			{
				setIcon(CLONE_DATATYPE_FIELD_IMAGE_ICON);	
			}
		}		
		return rtnComp;
	}
}
