/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.specification.hsm;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;

import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeParserUtil;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFCardinality;
import gov.nih.nci.caadapter.hl7.mif.MIFUtil;

import gov.nih.nci.caadapter.hl7.v2v3.tools.DefaultDataProcessor;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * This class defines the layout and some of data handling of the properties pane resided in HSMPanel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.25 $
 *          date        $Date: 2009-04-21 17:16:58 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/HSMNodePropertiesPane.java,v 1.25 2009-04-21 17:16:58 altturbo Exp $";

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
	private JTextField abstractField;
	private JComboBox dataTypeField;
	private JTextField hl7DefaultValueField;
	private JTextField hl7DomainField;
	private JTextField codingStrengthField;
	private JTextField cmetField;
	private JTextField userDefaultValueField;

//&umkis    private JTextArea annotationField;
//&umkis	private JTextArea commentField;

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

//&umkis        // For annotation - don't modify these remarks
//&umkis        posY++;
//&umkis        JLabel elementAnnotationLabel = new JLabel("Annotation:");
//&umkis        centerPanel.add(elementAnnotationLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0,
//&umkis                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));

//&umkis        annotationField = new JTextArea();
//&umkis        annotationField.setLineWrap(true);
//&umkis        annotationField.setRows(3);
//&umkis        annotationField.setWrapStyleWord(true);
//&umkis        annotationField.setEditable(true);
//&umkis        annotationField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
//&umkis        centerPanel.add(annotationField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0,
//&umkis                GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

//&umkis        // For comment - don't modify these remarks
//&umkis        posY++;
//&umkis        JLabel elementCommentsLabel = new JLabel("Comment:");
//&umkis        centerPanel.add(elementCommentsLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0,
//&umkis                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
//&umkis        commentField = new JTextArea();
//&umkis        commentField.setLineWrap(true);
//&umkis        commentField.setRows(3);
//&umkis        commentField.setWrapStyleWord(true);
//&umkis        commentField.setEditable(true);
//&umkis        //commentField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
//&umkis        centerPanel.add(commentField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0,
//&umkis                GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

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
//		JLabel rimSourceLabel = new JLabel("RIM Source:");
//		centerPanel.add(rimSourceLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0,
//				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
//		rimSourceField = new JTextField();
//		rimSourceField.setEditable(false);
//		rimSourceField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
//		centerPanel.add(rimSourceField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0,
//				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
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
				if (mifAttr.getDatatype()!=null&&mifAttr.getDatatype().isAbstract())
				{
					Datatype clonedDt=(Datatype)DatatypeParserUtil.getDatatype((String)dataTypeField.getSelectedItem()).clone();
					mifAttr.setConcreteDatatype(clonedDt);
//					mifAttr.setConcreteDatatype(DatatypeParserUtil.getDatatype((String)dataTypeField.getSelectedItem()));
				}
				if (userDefaultValueField.isEditable())
					mifAttr.setDefaultValue(userDefaultValueField.getText());
			}
			else if (seletedBaseObject instanceof Attribute)
			{
				//set default value
				Attribute updtdDatatypeAttr=(Attribute)seletedBaseObject;
				updtdDatatypeAttr.setDefaultValue(userDefaultValueField.getText());
				//update the concrete datatype if it is an abstract
				Datatype dtType=updtdDatatypeAttr.getReferenceDatatype();
				String slctdTypeName=(String)dataTypeField.getSelectedItem();
				if (dtType!=null&&!slctdTypeName.equalsIgnoreCase(""))
				{
					if (dtType.isAbstract()
							||(!dtType.getName().equals(slctdTypeName)))
						updtdDatatypeAttr.setReferenceDatatype((Datatype)DatatypeParserUtil.getDatatype(slctdTypeName).clone());
				}
			}
		}

//&umkis        if(commentField.getText()!=null) seletedBaseObject.setComment(commentField.getText());

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
		Component parentCom=this.getParent();
		if (parentCom instanceof JTabbedPane)
		{
			JTabbedPane parentTab=(JTabbedPane)parentCom;
			parentTab.setSelectedComponent(this);
		}
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
			elementNameField.setText(seletedBaseObject.getName());
			//set parent name form xmlPath
			String parentXmlPath=userDatatypeObj.getParentXmlPath();//xmlPath.substring(0, xmlPath.lastIndexOf("."));
			elementParentField.setText(parentXmlPath);

//&umkis            annotationField.setText(userDatatypeObj.getAnnotation());
//&umkis			annotationField.setEditable(true);
//&umkis			commentField.setText(userDatatypeObj.getComment());
//&umkis			commentField.setEditable(true);

            if (userDatatypeObj instanceof Attribute )
			{
				//userDefaultValue  is editable
				Attribute dtAttr=(Attribute)userDatatypeObj;
				elementTypeField.setText("Data Type Field");
				cardinalityField.setText(new MIFCardinality(dtAttr.getMin(), dtAttr.getMax()).toString());
				if (dtAttr.isOptional())
					mandatoryField.setText("N");
				else
					mandatoryField.setText("Y");
				//conformance is not present
				if (dtAttr.getType()!=null&&DatatypeParserUtil.isAbstractDatatypeWithName(dtAttr.getType()))
				{
					abstractField.setText("Y");
					Datatype dtRefClass=dtAttr.getReferenceDatatype();
//					if (dtAttr.isEnabled())
//					{
						dataTypeField.setEditable(true);
						List<String> subClassList=DatatypeParserUtil.findSubclassListWithTypeName(dtAttr.getType());
						if (subClassList!=null)
						{
							dataTypeField.addItem(COMBO_BOX_DEFAULT_BLANK_CHOICE);
							for(String subName:subClassList)
							{
								if (!subName.equals(dtAttr.getType()))
									dataTypeField.addItem(subName);
								if(dtRefClass!=null&&subName.equals(dtRefClass.getName()))
									dataTypeField.setSelectedItem(subName);
							}
						}
						else
							dataTypeField.addItem("No subclass is found");
//					}
				}
				else
					abstractField.setText("N");
				dataTypeField.addItem(dtAttr.getType());
				if (dtAttr.getReferenceDatatype()==null)
					setEditableField(userDefaultValueField, dtAttr.isEnabled());
				else if (dtAttr.getReferenceDatatype().isSimple())
					setEditableField(userDefaultValueField, dtAttr.isEnabled());
				//HL7 default is not present
				//HL7 Domain is not present
				//code strength is not present
				//cmet is not present
				userDefaultValueField.setText(dtAttr.getDefaultValue());
			}
			else if (userDatatypeObj instanceof MIFAttribute )
			{
				// dataTypeField is editable if the MIFAttribute is Abstract
				MIFAttribute mifAttr=(MIFAttribute)userDatatypeObj;
				elementTypeField.setText("Attribute");
				cardinalityField.setText(new MIFCardinality(mifAttr.getMinimumMultiplicity(), mifAttr.getMaximumMultiplicity()).toString());
				if (mifAttr.isMandatory())
					mandatoryField.setText("Y");
				else
					mandatoryField.setText("N");
				conformanceField.setText(mifAttr.getConformance());

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

				//use fixedValue as default value if available
				hl7DefaultValueField.setText(mifAttr.findHL7DefaultValueProperty());
				hl7DomainField.setText(mifAttr.findDomainNameOidProperty());//.getDomainName());
				codingStrengthField.setText(mifAttr.getCodingStrength());
				userDefaultValueField.setText(mifAttr.getDefaultValue());

				if (MIFUtil.isEditableMIFAttributeDefault(mifAttr))
					setEditableField(userDefaultValueField,true);
			}
			else if (userDatatypeObj instanceof MIFClass )
			{
				MIFClass mifClass=(MIFClass)userDatatypeObj;
				elementTypeField.setText("Clone");
				//set 1..1 cardinality to root node
				if (mifClass.getParentXmlPath()==null
						||mifClass.getParentXmlPath().equals(""))
				{
					cardinalityField.setText(new MIFCardinality(1, 1).toString());
					mandatoryField.setText("Y");
				}
				else
				{
					//here is a MIFClass selected for a ChoiceAssociation
					//find the cardinality from parent association
					DefaultMutableTreeNode parentNode =(DefaultMutableTreeNode)treeNode.getParent();
					MIFAssociation parentMifAssc=(MIFAssociation)parentNode.getUserObject();
					cardinalityField.setText(new MIFCardinality(parentMifAssc.getMaximumMultiplicity(), parentMifAssc.getMaximumMultiplicity()).toString());
					if (parentMifAssc.isMandatory())
						mandatoryField.setText("Y");
					else
						mandatoryField.setText("N");
					conformanceField.setText(parentMifAssc.getConformance());

				}
				//Abstract is not present
//				dataTypeField.addItem(mifClass.getName());
//				dataTypeField.setEditable(false);
				if (mifClass.isReference())//.getReferenceName()!=null)
				{
					cmetField.setText(mifClass.getName());
//					hl7DomainField.setText(mifClass.getName());
				}
//				hl7DefaultValueField.setText(mifAttr.getFixedValue());
//				codingStrengthField.setText(mifAttr.getCodingStrength());
//				userDefaultValueField.setText(mifAttr.getDefaultValue());
			}
			else if (userDatatypeObj instanceof MIFAssociation  )
			{
				MIFAssociation  mifAssc=(MIFAssociation)userDatatypeObj;
				elementTypeField.setText("Clone");
				cardinalityField.setText(new MIFCardinality(mifAssc.getMinimumMultiplicity(), mifAssc.getMaximumMultiplicity()).toString());
				if (mifAssc.isMandatory())
					mandatoryField.setText("Y");
				else
					mandatoryField.setText("N");
				conformanceField.setText(mifAssc.getConformance());

//				abstractField.setText(mifAssc.getMifClass().isDynamic()); //Abstract is not presentt
				MIFClass asscClass=mifAssc.getMifClass();
//				if(asscClass.getChoices().isEmpty())
//					dataTypeField.addItem(asscClass.getName());

				if (asscClass.isReference())//.getReferenceName()!=null)
				{
					cmetField.setText(asscClass.getName());//.getReferenceName());
//					hl7DomainField.setText(asscClass.getName());
				}
//				hl7DefaultValueField.setText("");
//				codingStrengthField.setText("");
//				userDefaultValueField.setText("");
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

//&umkis        annotationField.setText("");
//&umkis		commentField.setText("");

        cardinalityField.setText("");
		mandatoryField.setText("");
		conformanceField.setText("");
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

//&umkis        result=!GeneralUtilities.areEqual(this.seletedBaseObject.getComment(), commentField.getText().trim(), true);
//&umkis        if (result) return true;

        if (seletedBaseObject instanceof MIFClass)
		{
			result=false;
		}
		else if (seletedBaseObject instanceof MIFAssociation)
		{
			result=false;
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
			else if (userDefaultValueField.isEditable())
			{
				//check the default value
				result=!GeneralUtilities.areEqual(mifAttr.getDefaultValue(), userDefaultValueField.getText().trim(), true);
			}
		}
		else if (seletedBaseObject instanceof Attribute)
		{
			//check the default value
			Attribute mifDatatypeAttr=(Attribute)seletedBaseObject;
			result=!GeneralUtilities.areEqual(mifDatatypeAttr.getDefaultValue(), userDefaultValueField.getText(), true);
			Datatype attrDataType=mifDatatypeAttr.getReferenceDatatype();
			String slectdDt=(String)dataTypeField.getSelectedItem();
			if (slectdDt==null||slectdDt.equalsIgnoreCase(""))
				return result;

			if (attrDataType!=null&&attrDataType.isAbstract())
			{
				//compare the selected concrete class
				result = !GeneralUtilities.areEqual(attrDataType.getName(), dataTypeField.getSelectedItem(), true);
			}
		}

		return result;
	}

	/**
	 * Following handle some button actions
	 */
	private void applyUserChanges()
	{
//&umkis        validateDefaultData();
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
//		Object targetNode = getDatatypeObject(false);
		TreePath treePath=parentPanel.getTree().getSelectionPath();
		DefaultMutableTreeNode slctdNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
		this.setDisplayData(slctdNode,true);
	}


//&umkis    /*   inserted by umkis 04/21/2009
//&umkis     *   validate new default value
//&umkis     */
//&umkis    private void validateDefaultData()
//&umkis    {
//&umkis        if (!(seletedBaseObject instanceof Attribute)) return;

//&umkis        Attribute mifDatatypeAttr=(Attribute)seletedBaseObject;
//&umkis        if (GeneralUtilities.areEqual(mifDatatypeAttr.getDefaultValue(), userDefaultValueField.getText(), true)) return;

//&umkis        String newDefaultData = userDefaultValueField.getText();
//&umkis        if (newDefaultData == null) return;
//&umkis        newDefaultData = newDefaultData.trim();
//&umkis        if (newDefaultData.equals("")) return;
//&umkis        DefaultDataProcessor defProc = new DefaultDataProcessor();
//&umkis        if (mifDatatypeAttr.getName().equalsIgnoreCase("nullFlavor"))
//&umkis        {
//&umkis            if (!defProc.isValidNullFlavorValue(newDefaultData))
//&umkis            {
//&umkis                JOptionPane.showMessageDialog(this, "Invalid Null Flavor value : "+newDefaultData, "Invalid Null Flavor value", JOptionPane.ERROR_MESSAGE);
//&umkis                userDefaultValueField.setText(mifDatatypeAttr.getDefaultValue());
//&umkis            }
//&umkis            return;
//&umkis        }
//&umkis        if (!newDefaultData.startsWith(defProc.getDefaultTagSymbol())) return;
//&umkis        String res = "";
//&umkis        try
//&umkis        {
//&umkis            res = defProc.processDefaultValueTag(newDefaultData, "50");
//&umkis            if ((res == null)||(res.trim().equals(""))) throw new ApplicationException("Null Result default Tag : " + newDefaultData);
//&umkis        }
//&umkis        catch(ApplicationException ae)
//&umkis        {
//&umkis            JOptionPane.showMessageDialog(this, ae.getMessage(), "Invalid Functional Default Tag", JOptionPane.ERROR_MESSAGE);
//&umkis            res = "";
//&umkis        }
//&umkis        if (!res.equals("")) return;
//&umkis        userDefaultValueField.setText(mifDatatypeAttr.getDefaultValue());
//&umkis    }

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.24  2009/04/03 15:51:35  altturbo
 * HISTORY      : minor change - add remarks
 * HISTORY      :
 * HISTORY      : Revision 1.23  2009/04/02 20:35:25  altturbo
 * HISTORY      : add comment and annotation items but deactivated
 * HISTORY      :
 * HISTORY      : Revision 1.22  2008/09/29 20:14:14  wangeug
 * HISTORY      : enforce code standard: license file, file description, changing history
 * HISTORY      :
 *
 * **/
