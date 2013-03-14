/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.specification.csv;

import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVFieldMetaImpl;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.validation.CSVMetaValidator;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.message.ValidationMessageDialog;
import gov.nih.nci.caadapter.ui.common.nodeloader.SCMTreeNodeLoader;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

/**
 * This class defines the layout of field metadata properties display and accepts user's inputs.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:54:06 $
 */
public class CSVFieldMetadataPropertyPane extends JPanel
{
	public static final String SEGMENT_NAME_LABEL = "Segment Name:";
	public static final String FIELD_NAME_LABEL = "Field Name:";
	public static final String FIELD_NUMBER_LABEL = "Field Sequence Number:";
	private JLabel segmentNameLabel;
	private JTextField segmentNameTextField;
	private JLabel fieldNameLabel;
	private JTextField fieldNameTextField;
	private JLabel fieldNumberLabel;
	private JComboBox fieldNumberField;

//	private CSVPanel parentContainer;
	private JComponent parentContainer;
	/**
	 * we trust tree node but not the user object since tree node will hold
	 * the substree structure while individual user object will not always
	 * persist the segment/field structure.
	 */
	private DefaultMutableTreeNode treeNode;

	//maximal field number starts from 0 instead of 1
	private int minimalFieldNumber = Config.DEFAULT_FIELD_COLUMN_START_NUMBER;
	private int maximalFieldNumber = minimalFieldNumber;

	/**
	 */
	public CSVFieldMetadataPropertyPane(JPanel parentController)
	{
		this.parentContainer = parentController;
		initialize();
		constructFieldNumberRange();
	}

	public int getMinimalFieldNumber()
	{
		return minimalFieldNumber;
	}

	public void setMinimalFieldNumber(int minimalFieldNumber)
	{
		if (this.minimalFieldNumber != minimalFieldNumber)
		{
			this.minimalFieldNumber = minimalFieldNumber;
			constructFieldNumberRange();
		}
	}

	public int getMaximalFieldNumber()
	{
		return maximalFieldNumber;
	}

	public void setMaximalFieldNumber(int maximalFieldNumber)
	{
		if(this.maximalFieldNumber != maximalFieldNumber)
		{
			this.maximalFieldNumber = maximalFieldNumber;
			constructFieldNumberRange();
		}
	}

	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		segmentNameTextField.setEnabled(enabled);
		fieldNameTextField.setEnabled(enabled);
		fieldNumberField.setEnabled(enabled);
	}

	/**
	 * @param enabled
	 * @param fieldName either SEGMENT_NAME_LABEL or FIELD_NAME_LABEL or FIELD_NUMBER_LABEL
	 */
	public void setEditable(boolean enabled, String fieldName)
	{
		if (SEGMENT_NAME_LABEL.equals(fieldName))
		{
			segmentNameTextField.setEnabled(enabled);
		}
		else if (FIELD_NAME_LABEL.equals(fieldName))
		{
			fieldNameTextField.setEnabled(enabled);
		}
		else if (FIELD_NUMBER_LABEL.equals(fieldName))
		{
			fieldNumberField.setEnabled(enabled);
		}
		else
		{
			//throw new IllegalArgumentException("I don't understand this field name '" + fieldName + "'. Please check!");
            throw new IllegalArgumentException("This field name '" + fieldName + "' is not registered word for parsing .");
        }
	}

	public void setEditable(boolean editable)
	{
		setEnabled(editable);
	}

	private void constructFieldNumberRange()
	{
		fieldNumberField.removeAllItems();
		for(int i= minimalFieldNumber; i<maximalFieldNumber; i++)
		{
			fieldNumberField.addItem(new Integer(i));
		}
	}

	private void initialize()
	{
		this.setLayout(new FlowLayout(FlowLayout.LEADING));

		JPanel centerPanel = new JPanel(new GridBagLayout());

		segmentNameLabel = new JLabel(SEGMENT_NAME_LABEL);
		segmentNameTextField = new JTextField();
		fieldNameLabel = new JLabel(FIELD_NAME_LABEL);
		fieldNameTextField = new JTextField();
		fieldNumberLabel = new JLabel(FIELD_NUMBER_LABEL);
		fieldNumberField = new JComboBox();

		Dimension segmentSize = segmentNameLabel.getPreferredSize();
		Dimension fieldNameSize = fieldNameLabel.getPreferredSize();
		Dimension fieldNumberSize = fieldNumberLabel.getPreferredSize();

		int textFieldWidth = Math.max(segmentSize.width, Math.max(fieldNameSize.width, fieldNumberSize.width)) + 106;
		int textFieldHeight = Math.max(segmentSize.height, Math.max(fieldNameSize.height, fieldNumberSize.height)) + 6;

		segmentNameTextField.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));
		fieldNameTextField.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));//segmentNameTextField.getPreferredSize());
		fieldNumberField.setPreferredSize(new Dimension(textFieldWidth + 2, textFieldHeight + 2));

		Insets insets = new Insets(5, 5, 5, 5);
		centerPanel.add(segmentNameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		centerPanel.add(segmentNameTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		centerPanel.add(fieldNameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		centerPanel.add(fieldNameTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		centerPanel.add(fieldNumberLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		centerPanel.add(fieldNumberField, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		this.add(centerPanel);
	}

	public String getSegmentName()
	{
		return segmentNameTextField.getText();
	}

	public void setSegmentName(String newValue)
	{
		segmentNameTextField.setText(newValue);
	}

	public String getFieldName()
	{
		return fieldNameTextField.getText();
	}

	public void setFieldName(String newValue)
	{
		fieldNameTextField.setText(newValue);
	}

	public int getFieldNumber()
	{
		return ((Integer)fieldNumberField.getSelectedItem()).intValue();
	}

	public void setFieldNumber(int newValue)
	{
		Integer newValueInt = new Integer(newValue);
		setFieldNumber(newValueInt);
	}

	public void setFieldNumber(String newValue)
	{
		Integer newValueInt = new Integer(newValue);
		setFieldNumber(newValueInt);
	}

	public void setFieldNumber(Integer newValue)
	{
		fieldNumberField.setSelectedItem(newValue);
	}
	public void setDisplayData(DefaultMutableTreeNode treeNode, boolean refresh)
	{
//		if(treeNode==null)
//		{
//			parentContainer.setPropertiesPaneVisible(false);
//		}
		if (refresh || !GeneralUtilities.areEqual(this.treeNode, treeNode))
		{
			this.treeNode = treeNode;
			CSVFieldMeta data = (CSVFieldMeta) this.treeNode.getUserObject();
			minimalFieldNumber = getFieldMetaMinimalFieldNumber((DefaultMutableTreeNode) treeNode.getParent());
			//call this setter in order to trigger the constructFieldNumberRange().
			setMaximalFieldNumber(Config.DEFAULT_FIELD_COLUMN_START_NUMBER + getFieldMetaCountInChildUserObject((DefaultMutableTreeNode)treeNode.getParent()));
			setFieldName(data.getName());
			setFieldNumber(data.getColumn());
			if (data.getSegment() != null) {
				String segName = data.getSegmentName();
				setSegmentName(segName == null ? "" : segName);
			}
			else {
				setSegmentName("");
			}
		}
	}

	public DefaultMutableTreeNode getDisplayData(boolean withUserInputFromUI)
	{
		if(!withUserInputFromUI)
		{
			return this.treeNode;
		}

		if (this.treeNode == null)
		{
			CSVFieldMeta data = new CSVFieldMetaImpl(getFieldNumber(), getFieldName(), null);
			SCMTreeNodeLoader nodeLoader = new SCMTreeNodeLoader();
			this.treeNode=nodeLoader.constructTreeNode(data, false);
//			this.treeNode = parentContainer.getDefaultTreeNode(data, false);
		}
		else
		{
			//may not be used since parent segment may be read-only
			CSVFieldMeta data = (CSVFieldMeta) this.treeNode.getUserObject();
			data.setName(getValidatedUserInputOnName());
//			data.setColumn(getFieldNumber());
		}
		return this.treeNode;
	}

	private int getFieldMetaMinimalFieldNumber(DefaultMutableTreeNode treeNode)
	{
		int size = treeNode==null ? 0 : treeNode.getChildCount();
		int result = (size==0) ? 0 : Integer.MAX_VALUE;
		for(int i=0; i<size; i++)
		{
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) treeNode.getChildAt(i);
			Object userObject = childNode.getUserObject();
			if(userObject instanceof CSVFieldMeta)
			{
				CSVFieldMeta meta = (CSVFieldMeta) userObject;
				int metaFieldNum = meta.getColumn();
				if(metaFieldNum<result)
				{
					result = metaFieldNum;
				}
			}
		}
		return result;
	}

	private int getFieldMetaCountInChildUserObject(DefaultMutableTreeNode treeNode)
	{
		int size = treeNode==null ? 0 : treeNode.getChildCount();
		int count = 0;
		for(int i=0; i<size; i++)
		{
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) treeNode.getChildAt(i);
			Object userObject = childNode.getUserObject();
			if(userObject instanceof CSVFieldMeta)
			{
				count++;
			}
		}
		return count;
	}

	public boolean isDataChanged()
	{
		if (this.treeNode == null)
		{
			return false;
		}
		CSVFieldMeta data = (CSVFieldMeta) this.treeNode.getUserObject();
		boolean result = false;//getFieldNumber()!=data.getColumn();
		result = result || !GeneralUtilities.areEqual(getFieldName(), data.getName());
		return result;
	}

	private String getValidatedUserInputOnName()
	{
		String resultValue = getFieldName();
		CSVFieldMeta data = (CSVFieldMeta) treeNode.getUserObject();
		String currentValue = data.getName();
		do
		{
			data.setName(resultValue);
			ValidatorResults validatorResults = new ValidatorResults();
			validatorResults.addValidatorResults(CSVMetaValidator.validateFieldMetaName(data));
			//clean up after validation purpose
			data.setName(currentValue);

			if (validatorResults.getAllMessages().size() == 0)
			{
				break;
			}
			else
			{
//				ValidationMessageDialog.displayValidationResults(parentContainer.getRootContainer(), validatorResults);
				ValidationMessageDialog.displayValidationResults(parentContainer.getRootPane(), validatorResults);
				String cosmeticName = (DefaultSettings.getClassNameWithoutPackage(data.getClass()).toLowerCase().indexOf("segment") != -1) ? "Segment" : "Field";
				resultValue = (String) JOptionPane.showInputDialog(parentContainer, "Edit a " + cosmeticName + " name", "Edit", JOptionPane.INFORMATION_MESSAGE, null, null, resultValue);
				if (GeneralUtilities.isBlank(resultValue))
				{//user may cancelled the choice
					resultValue = currentValue;
				}
				setFieldName(resultValue);
			}
		}
		while (true);
		return resultValue;
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.22  2006/10/19 21:00:20  wuye
 * HISTORY      : Fixed the lose focus bug when delete an segment or attribute from scs tree
 * HISTORY      :
 * HISTORY      : Revision 1.21  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.20  2006/07/24 17:16:47  jiangsc
 * HISTORY      : field size change
 * HISTORY      :
 * HISTORY      : Revision 1.19  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/11/01 23:09:31  jiangsc
 * HISTORY      : UI Enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/10/18 15:57:51  giordanm
 * HISTORY      : update config-dist + fix a spelling error
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/10/18 13:35:26  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/10/17 22:59:08  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/08/23 19:57:00  jiangsc
 * HISTORY      : Name change
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/09 22:53:03  jiangsc
 * HISTORY      : Save Point
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/04 21:39:37  jiangsc
 * HISTORY      : Updated to dynamically figure out the minimal field number value on the fly.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/03 22:07:54  jiangsc
 * HISTORY      : Save Point.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/07/29 21:59:36  jiangsc
 * HISTORY      : Enhanced.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/22 20:53:11  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
