/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/HSMNodePropertiesPane.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $
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
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.hl7.clone.meta.CloneAttributeMeta;
import gov.nih.nci.caadapter.hl7.clone.meta.CloneDatatypeFieldMeta;
import gov.nih.nci.caadapter.hl7.clone.meta.CloneMeta;

import org.hl7.meta.Cardinality;
import org.hl7.meta.Datatype;
import org.hl7.meta.UnknownDatatypeException;
import org.hl7.meta.impl.DatatypeMetadataFactoryDatatypes;
import org.hl7.meta.impl.DatatypeMetadataFactoryImpl;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class defines the layout and some of data handling of the properties pane resided in HSMPanel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:18:15 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/HSMNodePropertiesPane.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $";

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
	private MetaObject hl7V3Meta;
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
			resetToPreviousValue();
		}
	}

	public MetaObject getHl7V3Meta(boolean fromUI)
	{
		if (fromUI)
		{//synchronize from UI.
			return getHl7V3MetaFromUI();
		}
		return hl7V3Meta;
	}

	private MetaObject getHl7V3MetaFromUI()
	{
		MetaObject meta = null;
		if (hl7V3Meta instanceof CloneMeta)
		{
			meta = getCloneMetaFromUI();
		}
		else if (hl7V3Meta instanceof CloneAttributeMeta)
		{
			meta = getCloneAttributeMetaFromUI();
		}
		else if (hl7V3Meta instanceof CloneDatatypeFieldMeta)
		{
			meta = getCloneDatatypeFieldMetaFromUI();
		}
		return meta;
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
		if (userObj instanceof MetaObject)
		{
			setHl7V3Meta((MetaObject) userObj, refresh);
		}
	}

	private void setHl7V3Meta(MetaObject hl7V3Meta, boolean refresh)
	{
		if (refresh || !GeneralUtilities.areEqual(this.hl7V3Meta, hl7V3Meta))
		{
			this.hl7V3Meta = hl7V3Meta;
			clearAndEditableFields(false);
			if (this.hl7V3Meta instanceof CloneMeta)
			{
				CloneMeta cloneMeta = (CloneMeta) this.hl7V3Meta;
				setCloneMetaToUI(cloneMeta);
			}
			else if (this.hl7V3Meta instanceof CloneAttributeMeta)
			{
				CloneAttributeMeta cloneAttributeMeta = (CloneAttributeMeta) this.hl7V3Meta;
				setCloneAttributeMetaToUI(cloneAttributeMeta);
			}
			else if (this.hl7V3Meta instanceof CloneDatatypeFieldMeta)
			{
				CloneDatatypeFieldMeta cloneDatatypeFieldMeta = (CloneDatatypeFieldMeta) this.hl7V3Meta;
				setCloneDatatypeFieldMetaToUI(cloneDatatypeFieldMeta);
			}
			else
			{
				System.err.println("What is this type? '" + (this.hl7V3Meta == null ? "null" : this.hl7V3Meta.getClass().getName()));
			}
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

	/**
	 * Called by getHl7V3MetaFromUI().
	 *
	 * @return the CloneMeta
	 */
	private CloneMeta getCloneMetaFromUI()
	{
		CloneMeta hl7V3MetaLocal = (CloneMeta) hl7V3Meta;
		//todo: add more if anything becomes editable
		return hl7V3MetaLocal;
	}

	/**
	 * Called by setHl7V3Meta
	 *
	 * @param cloneMeta
	 */
	private void setCloneMetaToUI(CloneMeta cloneMeta)
	{
		elementNameField.setText(cloneMeta.getName());
		elementTypeField.setText(cloneMeta.getType());
		CloneMeta parentClone = cloneMeta.getParentMeta();
		elementParentField.setText((parentClone == null) ? "" : parentClone.getName());
		hl7DomainField.setText(cloneMeta.getReferenceCloneName());//GeneralUtilities.getClassName(cloneMeta.getClass(), false));
		cmetField.setText(cloneMeta.getCmetID());
		Object tempObj = cloneMeta.getCardinality();
		cardinalityField.setText((tempObj==null? "" : tempObj.toString()));
		rimSourceField.setText(cloneMeta.getRimSource());
	}

	/**
	 * Called by getHl7V3MetaFromUI().
	 *
	 * @return the CloneAttributeMeta
	 */
	private CloneAttributeMeta getCloneAttributeMetaFromUI()
	{
		CloneAttributeMeta hl7V3MetaLocal = (CloneAttributeMeta) hl7V3Meta;
		//todo: add more if anything becomes editable
		if(hl7V3MetaLocal.isAbstract())
		{
			Object userChoice = dataTypeField.getSelectedItem();
			if((userChoice instanceof String) && GeneralUtilities.areEqual(userChoice, COMBO_BOX_DEFAULT_BLANK_CHOICE))
			{//implies user does not select any or just choose back the abstract value.
				//todo: question: to set to null or empty string
				hl7V3MetaLocal.setSubClass("");
			}
			else if(userChoice instanceof Datatype)
			{
				Datatype userChoiceType = (Datatype) userChoice;
				hl7V3MetaLocal.setSubClass(userChoiceType.getFullName());
			}
		}
		return hl7V3MetaLocal;
	}

	/**
	 * Called by setHl7V3MetaToUI()
	 *
	 * @param cloneAttributeMeta
	 */
	private void setCloneAttributeMetaToUI(CloneAttributeMeta cloneAttributeMeta)
	{
		elementNameField.setText(cloneAttributeMeta.getName());
		elementTypeField.setText(cloneAttributeMeta.getType());
		MetaObject parentClone = cloneAttributeMeta.getParentMeta();
		elementParentField.setText((parentClone == null) ? "" : parentClone.getName());
		Object tempObj = cloneAttributeMeta.getCardinality();
		cardinalityField.setText(GeneralUtilities.getStringValue(tempObj, false));
		tempObj = cloneAttributeMeta.getConformance();
		conformanceField.setText(GeneralUtilities.getStringValue(tempObj, false));
		rimSourceField.setText(cloneAttributeMeta.getRimSource());
		tempObj = cloneAttributeMeta.getCodingStrength();
		codingStrengthField.setText(GeneralUtilities.getStringValue(tempObj, false));
		hl7DefaultValueField.setText(cloneAttributeMeta.getHL7DefaultValue());
		hl7DomainField.setText(cloneAttributeMeta.getDomainName());//GeneralUtilities.getClassName(cloneAttributeMeta.getClass(), false));

		abstractField.setText(cloneAttributeMeta.isAbstract() ? "Y" : "N");
		try
		{
			if (cloneAttributeMeta.isAbstract())
			{
				String abstractClass = cloneAttributeMeta.getDatatype();
//				Log.logInfo(this, "Abstract Class is '" + abstractClass + "'");
				if (abstractClass != null)
				{
					setEditableField(dataTypeField, true);
					Datatype dataType = DatatypeMetadataFactoryImpl.instance().create(abstractClass);
					Log.logDebug(this, "Abs data type is '" + dataType + "'");
					if (dataType != null)
					{
						java.util.List<org.hl7.meta.Datatype> list = DatatypeMetadataFactoryDatatypes.instance().ABSTRACT_DATATYPES_MAP.get(dataType);
						Log.logDebug(this, "SUB class data type is '" + list + "'");
						int size = list == null ? 0 : list.size();
						dataTypeField.addItem(COMBO_BOX_DEFAULT_BLANK_CHOICE);
						for (int i = 0; i < size; i++)
						{
							dataTypeField.addItem(list.get(i));
						}
						String subClass = cloneAttributeMeta.getSubClass();
						if (subClass == null || subClass.length() == 0)
						{
							dataTypeField.setSelectedIndex(0);
						}
						else
						{
							Datatype subDataType = DatatypeMetadataFactoryImpl.instance().create(subClass);
							if (subDataType != null)
							{
								dataTypeField.setSelectedItem(subDataType);
							}
							else
							{
								System.err.println("Cannot create a sub-data type for '" + subClass + "'");
							}
						}
					}
					else
					{
						System.err.println("DataType is null for '" + abstractClass + "'");
					}
				}
			}//end of if(isAbstract()
			else
			{//concrete sub-class
//				Log.logInfo(this, "Clone Attribute is a concrete class.");
				String subClass = cloneAttributeMeta.getDatatype();//getSubClass();
				if (subClass == null || subClass.length() == 0)
				{
//					System.err.println("SubClass is null. What about Abstract Class? '" + cloneAttributeMeta.getDatatype() + "'");
					dataTypeField.addItem("");
				}
				else
				{
					Datatype subDataType = DatatypeMetadataFactoryImpl.instance().create(subClass);
					if (subDataType != null)
					{
						dataTypeField.addItem(subDataType);
						dataTypeField.setSelectedItem(subDataType);
					}
					else
					{
						System.err.println("DataType is null for '" + subClass + "'");
					}
				}
			}
		}//end of try
		catch (UnknownDatatypeException e)
		{
			Log.logException(this, e);
		}
	}

	/**
	 * Called by getHl7V3MetaFromUI().
	 *
	 * @return the CloneDatatypeFieldMeta
	 */
	private CloneDatatypeFieldMeta getCloneDatatypeFieldMetaFromUI()
	{
		CloneDatatypeFieldMeta hl7V3MetaLocal = (CloneDatatypeFieldMeta) hl7V3Meta;
		//todo: add more if anything becomes editable
		hl7V3MetaLocal.setUserDefaultValue(userDefaultValueField.getText());
		return hl7V3MetaLocal;
	}

	/**
	 * Called by setHl7V3MetaToUI()
	 *
	 * @param cloneDatatypeFieldMeta
	 */
	private void setCloneDatatypeFieldMetaToUI(CloneDatatypeFieldMeta cloneDatatypeFieldMeta)
	{
		elementNameField.setText(cloneDatatypeFieldMeta.getName());
		elementTypeField.setText(cloneDatatypeFieldMeta.getType());
		MetaObject parentClone = cloneDatatypeFieldMeta.getParentMeta();
		elementParentField.setText((parentClone == null) ? "" : parentClone.getName());
		hl7DefaultValueField.setText(cloneDatatypeFieldMeta.getHL7DefaultValue());
		setEditableField(userDefaultValueField, true);
		userDefaultValueField.setText(cloneDatatypeFieldMeta.getUserDefaultValue());
		Cardinality cardinality = cloneDatatypeFieldMeta.getCardinality();
		cardinalityField.setText((cardinality == null) ? "" : cardinality.toString());
	}

	public boolean isDataChanged()
	{
		if (this.hl7V3Meta == null)
		{
			return false;
		}
		boolean result = false;
		if (hl7V3Meta instanceof CloneMeta)
		{
			CloneMeta local = (CloneMeta) hl7V3Meta;
			result = !GeneralUtilities.areEqual(local.getCmetID(), cmetField.getText(), true);
		}
		else if (hl7V3Meta instanceof CloneAttributeMeta)
		{
			CloneAttributeMeta local = (CloneAttributeMeta) hl7V3Meta;
			if (local.isAbstract())
			{
				String subClass = local.getSubClass();
				Object selectedItem = dataTypeField.getSelectedItem();
				String selectedToString = (selectedItem == null) ? "" : selectedItem.toString();
				if (subClass == null || subClass.length() == 0)
				{
					result = !GeneralUtilities.areEqual("", selectedToString);
				}
				else
				{//validate if the sub-data type exists before comparing if it equals to the string from user selection.
					Datatype subDataType = null;
					try
					{
						subDataType = DatatypeMetadataFactoryImpl.instance().create(subClass);
					}
					catch (Exception e)
					{
						System.err.println("e:" + e.getMessage());
					}
					if (subDataType != null)
					{
						result = !GeneralUtilities.areEqual(subClass, selectedToString, true);
					}
					else
					{
						System.err.println("Cannot create a sub-data type for '" + subClass + "'");
						result = true;
					}
				}// end of else matched to if (subClass == null || subClass.length() == 0)
			}
		}
		else if (hl7V3Meta instanceof CloneDatatypeFieldMeta)
		{
			CloneDatatypeFieldMeta local = (CloneDatatypeFieldMeta) hl7V3Meta;
			result = !GeneralUtilities.areEqual(local.getUserDefaultValue(), userDefaultValueField.getText(), true);
		}
		return result;
	}

	/**
	 * Following handle some button actions
	 */
	private void applyUserChanges()
	{
		MetaObject newMetaObject = getHl7V3Meta(true);
		parentPanel.getController().updateCurrentNodeWithUserObject(newMetaObject);
		//explicitly redisplay the property pane, because user could continue to work on it
		parentPanel.setPropertiesPaneVisible(true);
	}

	private void resetToPreviousValue()
	{
		MetaObject targetNode = getHl7V3Meta(false);
		setHl7V3Meta(targetNode, true);
	}

	/**
	 * Reload the data.
	 */
	public void reloadData()
	{
		resetToPreviousValue();
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.37  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.36  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.35  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.34  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.33  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.32  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.31  2005/12/01 18:50:32  jiangsc
 * HISTORY      : Fix to Defect #207.
 * HISTORY      :
 * HISTORY      : Revision 1.30  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.29  2005/11/21 19:53:12  jiangsc
 * HISTORY      : Minor enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.28  2005/11/07 22:09:09  chene
 * HISTORY      : Add datatype field cardinality support
 * HISTORY      :
 * HISTORY      : Revision 1.27  2005/11/07 20:13:57  chene
 * HISTORY      : Rename sub_datatypes_map to abstract_datatypes_map
 * HISTORY      :
 * HISTORY      : Revision 1.26  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.25  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.24  2005/10/18 14:51:46  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.23  2005/10/17 22:32:00  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.22  2005/10/13 17:32:56  jiangsc
 * HISTORY      : Updated text for elementType field.
 * HISTORY      :
 * HISTORY      : Revision 1.21  2005/10/07 18:40:16  jiangsc
 * HISTORY      : Enhanced the Look and Feel of Validation and Properties.
 * HISTORY      :
 * HISTORY      : Revision 1.20  2005/10/05 20:15:03  jiangsc
 * HISTORY      : Enhanced data loading.
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/10/05 20:13:53  jiangsc
 * HISTORY      : Enhanced data loading.
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/09/30 21:17:33  jiangsc
 * HISTORY      : Minor update - corrected wording
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/09/29 16:07:04  jiangsc
 * HISTORY      : Added code to populate field values.
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/09/22 16:30:20  jiangsc
 * HISTORY      : Consolidation of labeling.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/08/31 15:03:25  jiangsc
 * HISTORY      : Fixed some UI medium defects. Thanks to Dan's test.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/08/30 20:48:16  jiangsc
 * HISTORY      : minor update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/08/24 22:28:36  jiangsc
 * HISTORY      : Enhanced JGraph implementation;
 * HISTORY      : Save point of CSV and HSM navigation update;
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/08/08 17:43:48  jiangsc
 * HISTORY      : Enhanced the support of Abstract Datatype.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/08/05 20:35:50  jiangsc
 * HISTORY      : 0)Implemented field sequencing on CSVPanel but needs further rework;
 * HISTORY      : 1)Removed (Yes/No) for questions;
 * HISTORY      : 2)Removed double-checking after Save-As;
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/08/03 19:11:01  jiangsc
 * HISTORY      : Some cosmetic update and make HSMPanel able to save the same content to different file.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/08/03 16:59:06  chene
 * HISTORY      : Add datatype feature at CloneMetaAttribute
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/03 16:56:16  jiangsc
 * HISTORY      : Further consolidation of context sensitive management.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/03 14:39:10  jiangsc
 * HISTORY      : Further consolidation of context sensitive management.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/02 22:32:09  jiangsc
 * HISTORY      : Minor update
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/02 22:28:54  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/02 15:18:30  chene
 * HISTORY      : Add HL7 default value and user default value
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/29 22:00:00  jiangsc
 * HISTORY      : Enhanced HSMPanel
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/28 18:18:42  jiangsc
 * HISTORY      : Can Open HSM Panel
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/27 13:57:46  jiangsc
 * HISTORY      : Added the first round of HSMPanel.
 * HISTORY      :
 */
