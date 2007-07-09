/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/HSMNodePropertiesPane.java,v 1.3 2007-07-09 20:15:55 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.specification.hsm;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;

import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import  gov.nih.nci.caadapter.hl7.datatype.Cardinality;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeParserUtil;
//import gov.nih.nci.caadapter.hl7.mif.CMETRef;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
//import hl7OrgV3.mif.List;
//import gov.nih.nci.caadapter.hl7.mif.v1.CMETUtil;

import javax.swing.JTextField;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

/**
 * This class defines the layout and some of data handling of the properties pane resided in HSMPanel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2007-07-09 20:15:55 $
 */
public class HSMNodePropertiesPane extends JPanel implements ActionListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: HSMNodePropertiesPane.java,v $";
	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/HSMNodePropertiesPane.java,v 1.3 2007-07-09 20:15:55 wangeug Exp $";

	private static final String APPLY_BUTTON_COMMAND_NAME = "Apply";
	private static final String APPLY_BUTTON_COMMAND_MNEMONIC = "A";
	private static final String RESET_BUTTON_COMMAND_NAME = "Reset";
	private static final String RESET_BUTTON_COMMAND_MNEMONIC = "R";

	private static final String COMBO_BOX_DEFAULT_BLANK_CHOICE = "";

	private Color defaultEditableFieldBackgroundColor = null;

	private JTextField elementNameField;
	private JTextField elementTypeField;
	private JTextField elementParentField;
	private JTextField cardinalityField;
	private JTextField mandatoryField;
	private JTextField conformanceField;
	private JTextField rimSourceField;
	private JTextField abstractField;
	private JComboBox dataTypeField;
	private JTextField hl7DefaultValueField;
	private JTextField hl7DomainField;
	private JTextField codingStrengthField;
	private JTextField cmetField;
	private JTextField userDefaultValueField;

	//could be either HL7V3Meta, CloneMeta, CloneAttributeMeta, CloneDatatypeFieldMeta, etc.
//	private MetaObject hl7V3Meta;
	private DatatypeBaseObject seletedBaseObject;
	private HSMPanel parentPanel;

	/**
	 * Creates a new <code>JPanel</code> with a double buffer
	 * and a flow layout.
	 */
	public HSMNodePropertiesPane(HSMPanel parent)
	{
		this.parentPanel = parent;
		initialize();
	}

	private void initialize()
	{

		this.setLayout(new BorderLayout());
		this.add(getCenterComponent(), BorderLayout.CENTER);
		this.add(getSouthComponent(), BorderLayout.SOUTH);
	}

	private JComponent getCenterComponent()
	{
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(BorderFactory.createTitledBorder("HL7 v3 Specification Properties"));
		JPanel centerPanel = new JPanel(new GridBagLayout());
		Insets insets = new Insets(5, 5, 5, 5);
		int posY = 0;
		JLabel elementNameLabel = new JLabel("Element Name:");
		centerPanel.add(elementNameLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		elementNameField = new JTextField();
		Dimension fieldDimension = new Dimension(elementNameLabel.getPreferredSize().width, elementNameField.getPreferredSize().height);
		//first time set the editable field background color
		defaultEditableFieldBackgroundColor = elementNameField.getBackground();
		elementNameField.setEditable(false);
		elementNameField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		elementNameField.setPreferredSize(fieldDimension);//titleLabel.getPreferredSize());
		centerPanel.add(elementNameField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		posY++;
		JLabel elementTypeLabel = new JLabel("Element Type:");
		centerPanel.add(elementTypeLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		elementTypeField = new JTextField();
		elementTypeField.setEditable(false);
		elementTypeField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		centerPanel.add(elementTypeField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		posY++;
		JLabel elementParentLabel = new JLabel("Element Parent:");
		centerPanel.add(elementParentLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		elementParentField = new JTextField();
		elementParentField.setEditable(false);
		elementParentField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		centerPanel.add(elementParentField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		posY++;
		JLabel cardinalityLabel = new JLabel("Cardinality:");
		centerPanel.add(cardinalityLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		cardinalityField = new JTextField();
		cardinalityField.setEditable(false);
		cardinalityField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		centerPanel.add(cardinalityField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		posY++;
		JLabel mandatoryLabel = new JLabel("Mandatory:");
		centerPanel.add(mandatoryLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		mandatoryField = new JTextField();
		mandatoryField.setEditable(false);
		mandatoryField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		centerPanel.add(mandatoryField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		posY++;
		JLabel conformanceLabel = new JLabel("Conformance:");
		centerPanel.add(conformanceLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		conformanceField = new JTextField();
		conformanceField.setEditable(false);
		conformanceField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		centerPanel.add(conformanceField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		posY++;
		JLabel rimSourceLabel = new JLabel("RIM Source:");
		centerPanel.add(rimSourceLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		rimSourceField = new JTextField();
		rimSourceField.setEditable(false);
		rimSourceField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		centerPanel.add(rimSourceField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		posY++;
		JLabel abstractLabel = new JLabel("Abstract:");
		centerPanel.add(abstractLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		abstractField = new JTextField();
		abstractField.setEditable(false);
		abstractField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		centerPanel.add(abstractField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		posY++;
		JLabel dataTypeLabel = new JLabel("Data Type:");
		centerPanel.add(dataTypeLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		dataTypeField = new JComboBox();
		dataTypeField.setPreferredSize(new Dimension(fieldDimension.width + 4, fieldDimension.height + 2));//titleLabel.getPreferredSize());
		centerPanel.add(dataTypeField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		posY++;
		JLabel hl7DefaultValueLabel = new JLabel("HL7 Default Value:");
		centerPanel.add(hl7DefaultValueLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		hl7DefaultValueField = new JTextField();
		hl7DefaultValueField.setEditable(false);
		hl7DefaultValueField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		centerPanel.add(hl7DefaultValueField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		posY++;
		JLabel hl7DomainLabel = new JLabel("HL7 Domain:");
		centerPanel.add(hl7DomainLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		hl7DomainField = new JTextField();
		hl7DomainField.setEditable(false);
		hl7DomainField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		centerPanel.add(hl7DomainField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		posY++;
		JLabel codingStrengthLabel = new JLabel("Coding Strength:");
		centerPanel.add(codingStrengthLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		codingStrengthField = new JTextField();
		codingStrengthField.setEditable(false);
		codingStrengthField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		centerPanel.add(codingStrengthField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		posY++;
		JLabel cmetLabel = new JLabel("CMET:");
		centerPanel.add(cmetLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		cmetField = new JTextField();
		cmetField.setEditable(false);
		cmetField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		centerPanel.add(cmetField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		posY++;
		JLabel userDefaultValueLabel = new JLabel("User-defined Default Value:");
		centerPanel.add(userDefaultValueLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		userDefaultValueField = new JTextField();
		centerPanel.add(userDefaultValueField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		posY++;

		scrollPane.getViewport().setView(centerPanel);
		return scrollPane;
	}

	private JComponent getSouthComponent()
	{
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		JButton applyButton = new JButton(APPLY_BUTTON_COMMAND_NAME);
		applyButton.setMnemonic(APPLY_BUTTON_COMMAND_MNEMONIC.charAt(0));
		applyButton.addActionListener(this);
		JButton resetButton = new JButton(RESET_BUTTON_COMMAND_NAME);
		resetButton.setMnemonic(RESET_BUTTON_COMMAND_MNEMONIC.charAt(0));
		resetButton.addActionListener(this);

		buttonPanel.add(applyButton);
		buttonPanel.add(resetButton);
		return buttonPanel;
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		if (APPLY_BUTTON_COMMAND_NAME.equals(command))
		{
			//Log.logInfo(this, "To Implement Apply Command...");
			applyUserChanges();
		}
		else if (RESET_BUTTON_COMMAND_NAME.equals(command))
		{
			//Log.logInfo(this, "To Implement Reset Command...");
			reloadData();
		}
	}

	public DatatypeBaseObject getDatatypeObject(boolean fromUI)
	{
		if (fromUI)
		{//synchronize from UI.
			if (seletedBaseObject instanceof MIFAttribute)
			{
				//set selected concrete class
				MIFAttribute mifAttr=(MIFAttribute)seletedBaseObject;
				mifAttr.setConcreteDatatype(DatatypeParserUtil.getDatatype((String)dataTypeField.getSelectedItem()));
			}
			else if (seletedBaseObject instanceof Attribute)
			{
				//set default value
				Attribute updtdDatatypeAttr=(Attribute)seletedBaseObject;
				updtdDatatypeAttr.setDefaultValue(userDefaultValueField.getText());
			}
		}
		return seletedBaseObject;
	}


	/**
	 * Called by outsiders to trigger change check
	 *
	 * @param treeNode
	 */
	public boolean setDisplayData(DefaultMutableTreeNode treeNode)
	{
		if (isDataChanged())
		{
			int userChoice = JOptionPane.showConfirmDialog(parentPanel, "This HL7 v3 specification has been changed in this properties panel. Would you like to apply the changes?", "Question", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (userChoice == JOptionPane.YES_OPTION)
			{
				applyUserChanges();
			}
			else if (userChoice == JOptionPane.CANCEL_OPTION)
			{//stay where user is at, abort the attemption to move to different node.
				return false;
			}
		}
		setDisplayData(treeNode, true);
		return true;
	}

	/**
	 * Interim between the public setDisplayData() and the setHl7V3Meta() so as to check if treeNode is null and its user object is MetaObject.
	 * @param treeNode
	 * @param refresh
	 */
	private void setDisplayData(DefaultMutableTreeNode treeNode, boolean refresh)
	{
		if (treeNode == null)
		{//no need to update
			return;
		}
		
		Object userObj = treeNode.getUserObject();
		if (!(userObj instanceof DatatypeBaseObject))
		{
			Log.logWarning(this,"Invalid data type being selectd:"+userObj.getClass().getName());
			return;
		}
		
		DatatypeBaseObject userDatatypeObj=(DatatypeBaseObject)userObj;
		if (refresh || !GeneralUtilities.areEqual(this.seletedBaseObject, userDatatypeObj))
		{
			this.seletedBaseObject = userDatatypeObj;
			clearAndEditableFields(false);
			elementNameField.setText(seletedBaseObject.getXmlPath());//.getName());
			//set parent name form xmlPath
			String parentXmlPath=userDatatypeObj.getParentXmlPath();//xmlPath.substring(0, xmlPath.lastIndexOf("."));
			if (parentXmlPath!=null&&parentXmlPath.length()>0)
			{ 	
				if (parentXmlPath.lastIndexOf(".")>-1)
					elementParentField.setText(parentXmlPath.substring(parentXmlPath.lastIndexOf(".")+1));
				else
					elementParentField.setText(parentXmlPath);
			}
			else
				elementParentField.setText("null");
			
			if (userDatatypeObj instanceof Attribute )
			{
				//userDefaultValue  is editable
				Attribute dtAttr=(Attribute)userDatatypeObj;
				elementTypeField.setText("Data Type Field");
				cardinalityField.setText(new Cardinality(dtAttr.getMin(), dtAttr.getMax()).toString());
				if (dtAttr.isOptional())
					mandatoryField.setText("N");
				else
					mandatoryField.setText("Y");
				if (DatatypeParserUtil.isAbstractDatatypeWithName(dtAttr.getType()))
				{
					dataTypeField.setEditable(true);
					abstractField.setText("Y"); 
				}
				else 
					abstractField.setText("N"); 
				dataTypeField.addItem(dtAttr.getType());
				setEditableField(userDefaultValueField, true);
				userDefaultValueField.setText(dtAttr.getDefaultValue());
			}
			else if (userDatatypeObj instanceof MIFAttribute )
			{
				// dataTypeField is editable if the MIFAttribute is Abstract
				MIFAttribute mifAttr=(MIFAttribute)userDatatypeObj;
				elementTypeField.setText("Attribute");
				cardinalityField.setText(new Cardinality(mifAttr.getMinimumMultiplicity(), mifAttr.getMaximumMultiplicity()).toString());
				if (mifAttr.isMandatory())
					mandatoryField.setText("Y");
				else
					mandatoryField.setText("N");
				conformanceField.setText(mifAttr.getConformance());
				rimSourceField.setText(mifAttr.getDefaultFrom()); //not set
				Datatype mifAttrDataType=mifAttr.getDatatype();
				//the pre-defined type is always the first elemnt
				if (mifAttrDataType!=null&&mifAttrDataType.isAbstract())
				{
					Datatype subClass=mifAttr.getConcreteDatatype();
					dataTypeField.setEditable(true);
					abstractField.setText("Y"); 
					List<String> subClassList=DatatypeParserUtil.findSubclassListWithTypeName(mifAttr.getType());
					if (subClassList!=null)
					{
						dataTypeField.addItem(COMBO_BOX_DEFAULT_BLANK_CHOICE);
						for(String subName:subClassList)
						{
							if (!subName.equals(mifAttr.getType()))
								dataTypeField.addItem(subName);
							if(subClass!=null&&subName.equals(subClass.getName()))
								dataTypeField.setSelectedItem(subName);
						}
					}
					else
						dataTypeField.addItem("No subclass is found");
				}
				else
				{
					abstractField.setText("N");
					dataTypeField.addItem(mifAttr.getType());
				}
				hl7DefaultValueField.setText(mifAttr.getFixedValue());
				hl7DomainField.setText(mifAttr.getDomainName());
				codingStrengthField.setText(mifAttr.getCodingStrength());
				cmetField.setText("");//not set
				userDefaultValueField.setText(mifAttr.getDefaultValue());
			}
			else if (userDatatypeObj instanceof MIFClass )
			{
				MIFClass mifClass=(MIFClass)userDatatypeObj;
				elementTypeField.setText("Clone");
				//set 1..1 cardinality to root node
				if (mifClass.getParentXmlPath()==null)
				{
					cardinalityField.setText(new Cardinality(1, 1).toString());
					mandatoryField.setText("Y");
				}
				else
				{
					//here is a MIFClass selected for a ChoiceAssociation
					//find the cardinality from parent association
					DefaultMutableTreeNode parentNode =(DefaultMutableTreeNode)treeNode.getParent();
					MIFAssociation parentMifAssc=(MIFAssociation)parentNode.getUserObject();
					cardinalityField.setText(new Cardinality(parentMifAssc.getMaximumMultiplicity(), parentMifAssc.getMaximumMultiplicity()).toString());
					if (parentMifAssc.isMandatory())
						mandatoryField.setText("Y");
					else
						mandatoryField.setText("N");
					conformanceField.setText(parentMifAssc.getConformance());
						
				}
				dataTypeField.addItem(mifClass.getName());
				if (mifClass.isReference())//.getReferenceName()!=null)
				{
					cmetField.setText(mifClass.getName());
					hl7DomainField.setText(mifClass.getName());
				}
//				conformanceField.setText(mifClass.getConformance());
//				rimSourceField.setText(mifAttr.getDefaultFrom()); //not set
//				hl7DefaultValueField.setText(mifAttr.getFixedValue());
//				codingStrengthField.setText(mifAttr.getCodingStrength());
//				userDefaultValueField.setText(mifAttr.getDefaultValue());
			}
			else if (userDatatypeObj instanceof MIFAssociation  )
			{
				MIFAssociation  mifAssc=(MIFAssociation)userDatatypeObj;
				elementTypeField.setText("Clone");
				cardinalityField.setText(new Cardinality(mifAssc.getMinimumMultiplicity(), mifAssc.getMaximumMultiplicity()).toString());
				if (mifAssc.isMandatory())
					mandatoryField.setText("Y");
				else
					mandatoryField.setText("N");
				conformanceField.setText(mifAssc.getConformance());
				
//				abstractField.setText(mifAssc.getMifClass().isDynamic()); //not set
				MIFClass asscClass=mifAssc.getMifClass();
				rimSourceField.setText(asscClass.getReferenceName()); 
				if(asscClass.getChoices().isEmpty())
					dataTypeField.addItem(asscClass.getName());

				if (asscClass.isReference())//.getReferenceName()!=null)
				{
					cmetField.setText(asscClass.getName());//.getReferenceName());
					hl7DomainField.setText(asscClass.getName());
				}
				hl7DefaultValueField.setText("");
				codingStrengthField.setText("");
				userDefaultValueField.setText("");
			}
			else
				Log.logWarning(this,"Invalid data type being selectd:"+userDatatypeObj.getClass().getName());
		}
	}
	

	private void clearAndEditableFields(boolean editableValue)
	{
		//clear fields
		elementNameField.setText("");
		elementTypeField.setText("");
		elementParentField.setText("");
		cardinalityField.setText("");
		mandatoryField.setText("");
		conformanceField.setText("");
		rimSourceField.setText("");
		abstractField.setText("");
		dataTypeField.removeAllItems();
		hl7DefaultValueField.setText("");
		hl7DomainField.setText("");
		codingStrengthField.setText("");
		cmetField.setText("");
		userDefaultValueField.setText("");

		//alter the editability display status
		setEditableField(dataTypeField,  editableValue);
		setEditableField(userDefaultValueField,  editableValue);
	}

	private void setEditableField(JComponent component, boolean editableValue)
	{
		if(component instanceof JTextComponent)
		{
			((JTextComponent) component).setEditable(editableValue);
		}
		else if(component instanceof JComboBox)
		{
			((JComboBox) component).setEditable(editableValue);
		}
		if(editableValue)
		{
			component.setBackground(defaultEditableFieldBackgroundColor);
		}
		else
		{
			component.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		}
	}


	public boolean isDataChanged()
	{
		if (this.seletedBaseObject == null)
		{
			return false;
		}
		boolean result = false;
			
		if (seletedBaseObject instanceof MIFClass)
		{
			result=false;
		}
		else if (seletedBaseObject instanceof MIFAssociation)
		{
			result=false;
//			MIFAssociation mifClass=(MIFAssociation)seletedBaseObject;
			
		}
		else if (seletedBaseObject instanceof MIFAttribute)
		{
			//check the selected concreted class
			MIFAttribute mifAttr=(MIFAttribute)seletedBaseObject;
			Datatype mifAttrDataType=mifAttr.getDatatype();
			//the pre-defined type is always the first elemnt
			if (mifAttrDataType!=null&&mifAttrDataType.isAbstract())
			{
				//compare the selected concrete class
				Datatype subClass=mifAttr.getConcreteDatatype();
				if (subClass==null)
					result=!GeneralUtilities.areEqual(subClass, dataTypeField.getSelectedItem(), true);
				else 
					result = !GeneralUtilities.areEqual(subClass.getName(), dataTypeField.getSelectedItem(), true);
			}
		}
		else if (seletedBaseObject instanceof Attribute)
		{
			//check the default value
			Attribute mifDatatypeAttr=(Attribute)seletedBaseObject;
			result=!GeneralUtilities.areEqual(mifDatatypeAttr.getDefaultValue(), userDefaultValueField.getText(), true);
		}

		return result;
	}

	/**
	 * Following handle some button actions
	 */
	private void applyUserChanges()
	{
		DatatypeBaseObject userDatatypeObj= this.getDatatypeObject(true);//getHl7V3Meta(true);
		parentPanel.getController().updateCurrentNodeWithUserObject(userDatatypeObj);
		//explicitly redisplay the property pane, because user could continue to work on it
		parentPanel.setPropertiesPaneVisible(true);		 
	}

	
	/**
	 * Reload the data.
	 */
	public void reloadData()
	{
		Object targetNode = getDatatypeObject(false);
		
		 TreePath treePath=parentPanel.getTree().getSelectionPath();
		DefaultMutableTreeNode slctdNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
		this.setDisplayData(slctdNode,true);
		//.setDatatypeBaseObject((DatatypeBaseObject)targetNode, true);
	}
}

