/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.specification.csv;

import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;
import gov.nih.nci.caadapter.common.Cardinality;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVSegmentMetaImpl;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.validation.CSVMetaValidator;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.message.ValidationMessageDialog;
import gov.nih.nci.caadapter.ui.common.nodeloader.SCMTreeNodeLoader;
import gov.nih.nci.caadapter.ui.common.tree.DefaultSCMTreeMutableTreeNode;
import gov.nih.nci.caadapter.ui.common.tree.DefaultSCMTreeMutableTreeNodeComparator;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.Enumeration;

/**
 * This class defines the layout of segment metadata property pane.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.7 $
 *          date        $Date: 2008-06-09 19:54:07 $
 */
public class CSVSegmentMetadataPropertyPane extends JPanel
{
	public static final String SEGMENT_NAME_LABEL = "Segment Name:";
	public static final String PARENT_SEGMENT_NAME_LABEL = "Parent Segment Name:";
    public static final String CARDINALITY_LABEL = "Cardinality:";
    private JLabel segmentNameLabel;
	private JTextField segmentNameTextField;
	private JLabel parentSegmentNameLabel;
	private JTextField parentSegmentNameTextField;
    private JLabel cardinalityLabel;
	private JComboBox cardinalityField;

    private CSVFieldOrderReshufflePane fieldOrderPane;

	private CSVPanel parentController;
	/**
	 * we trust tree node but not the user object since tree node will hold
	 * the substree structure while individual user object will not always
	 * persist the segment/field structure.
	 */
	private DefaultMutableTreeNode treeNode;

	/**
	 * Creates a new <code>JPanel</code> with a double buffer
	 * and a flow layout.
	 */
	public CSVSegmentMetadataPropertyPane(CSVPanel parentController)
	{
		this.parentController = parentController;
		initialize();
	}

	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		segmentNameTextField.setEnabled(enabled);
		parentSegmentNameTextField.setEnabled(enabled);
	}

	/**
	 *
	 * @param enabled
	 * @param fieldName either SEGMENT_NAME_LABEL or PARENT_SEGMENT_NAME_LABEL
	 */
	public void setEditable(boolean enabled, String fieldName)
	{
		if(SEGMENT_NAME_LABEL.equals(fieldName))
		{
			segmentNameTextField.setEnabled(enabled);
		}
		else if(PARENT_SEGMENT_NAME_LABEL.equals(fieldName))
		{
			parentSegmentNameTextField.setEnabled(enabled);
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

	private void initialize()
	{
		this.setLayout(new BorderLayout());//new FlowLayout(FlowLayout.LEADING));

		JPanel northPanel = new JPanel(new GridBagLayout());

		segmentNameLabel = new JLabel(SEGMENT_NAME_LABEL);
		segmentNameTextField = new JTextField();
		parentSegmentNameLabel = new JLabel(PARENT_SEGMENT_NAME_LABEL);
		parentSegmentNameTextField = new JTextField();
        cardinalityLabel = new JLabel(CARDINALITY_LABEL);
		cardinalityField = new JComboBox();
		//add all choicable value

        // CSVSegmentMeta data = (CSVSegmentMeta) this.treeNode.getUserObject();
        cardinalityField.addItem(new Cardinality(CardinalityType.VALUE_0));
		cardinalityField.addItem(new Cardinality(CardinalityType.VALUE_1));
		cardinalityField.addItem(new Cardinality(CardinalityType.VALUE_2));
		cardinalityField.addItem(new Cardinality(CardinalityType.VALUE_3));
//        cardinalityField.setEditable(false);

        Dimension segmentSize = segmentNameLabel.getPreferredSize();
		Dimension parentSegmentSize = parentSegmentNameLabel.getPreferredSize();
        Dimension cardinalitySize = cardinalityLabel.getPreferredSize();

		int textFieldWidth = Math.max(segmentSize.width, Math.max(cardinalitySize.width, parentSegmentSize.width)) + 6;
		int textFieldHeight = Math.max(segmentSize.height, Math.max(cardinalitySize.height, parentSegmentSize.height)) + 6;

		segmentNameTextField.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));
		parentSegmentNameTextField.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));//segmentNameTextField.getPreferredSize());
        cardinalityField.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));//segmentNameTextField.getPreferredSize());

		Insets insets = new Insets(5, 5, 5, 5);

		northPanel.add(segmentNameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		northPanel.add(segmentNameTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		northPanel.add(parentSegmentNameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		northPanel.add(parentSegmentNameTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        northPanel.add(cardinalityLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		northPanel.add(cardinalityField, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

        this.add(northPanel, BorderLayout.NORTH);

		fieldOrderPane = new CSVFieldOrderReshufflePane(parentController);
		this.add(fieldOrderPane, BorderLayout.CENTER);
	}

    // Following 2 methods are inserted by umkis 11/07/05
    // for setting enable or disable button according to selected or non-selected data on segment property pane
    public void setMoveUpAndDownButtonsEnabled()
    {
       fieldOrderPane.setButtonsEnabled();
    }
    public void setMoveUpAndDownButtonsDisabled()
    {
       fieldOrderPane.setButtonsDisabled();
    }

    protected String getSegmentName()
	{
		return segmentNameTextField.getText();
	}

	protected void setSegmentName(String newValue)
	{
		segmentNameTextField.setText(newValue);
	}

	protected String getParentSegmentName()
	{
		return parentSegmentNameTextField.getText();
	}

	protected void setParentSegmentName(String newValue)
	{
		parentSegmentNameTextField.setText(newValue);
	}

    protected String getCardinality()
	{
		return cardinalityField.getSelectedItem().toString();//.getText();
	}

	protected void setCardinality(String newValue)
	{
        boolean cTag = false;
        for (int carIndex=0;carIndex<cardinalityField.getItemCount();carIndex++)
		{
			if (cardinalityField.getItemAt(carIndex).toString().equals(newValue))
            {
                cardinalityField.setSelectedIndex(carIndex);
                cTag = true;
            }
        }
        if (cTag) return;
        int idx = cardinalityField.getItemAt(0).toString().indexOf(Config.SUFFIX_OF_CHOICE_CARDINALITY);
        cardinalityField.removeAllItems();
        if (idx < 0)
        {
            cardinalityField.addItem(new Cardinality(CardinalityType.VALUE_4));
		    cardinalityField.addItem(new Cardinality(CardinalityType.VALUE_5));
		    cardinalityField.addItem(new Cardinality(CardinalityType.VALUE_6));
		    cardinalityField.addItem(new Cardinality(CardinalityType.VALUE_7));
        }
        else
        {
            cardinalityField.addItem(new Cardinality(CardinalityType.VALUE_0));
		    cardinalityField.addItem(new Cardinality(CardinalityType.VALUE_1));
		    cardinalityField.addItem(new Cardinality(CardinalityType.VALUE_2));
		    cardinalityField.addItem(new Cardinality(CardinalityType.VALUE_3));
        }

        cTag = false;
        for (int carIndex=0;carIndex<cardinalityField.getItemCount();carIndex++)
		{
			if (cardinalityField.getItemAt(carIndex).toString().equals(newValue))
            {
                cardinalityField.setSelectedIndex(carIndex);
                cTag = true;
            }
        }
        if (!cTag) JOptionPane.showMessageDialog(this, "Invalid cardinality type : " + newValue, "Invalid Cardinality", JOptionPane.ERROR_MESSAGE);
    }

    public void setDisplayData(DefaultMutableTreeNode treeNode, boolean refresh)
	{
		if(refresh || !GeneralUtilities.areEqual(this.treeNode, treeNode))
		{
			this.treeNode = treeNode;
			if (treeNode == null)
			{//do not refresh
				return;
			}

			CSVSegmentMeta data = (CSVSegmentMeta) treeNode.getUserObject();
			setSegmentName(data.getName());
            if (data.isChoiceMemberSegment())
            {
                setCardinality(data.getParent().getCardinalityWithString());
            }
            else setCardinality(data.getCardinalityType().toString());
            CSVSegmentMeta parent = data.getParent();
			setParentSegmentName(parent==null? "" : parent.getName());
            /**
			 * NOTE: instead of obtaining field list directly from metadata,
			 * parse the whole children list of the given treeNode, so as to find out the most up-to-date field list.
			 * This is because every tree CRUD (Add/delete, etc) will only happen on treeNode level and is not propogated
			 * to the user object level.
			 */
//			fieldOrderPane.setCSVFieldMetaList(data.getFields());
			java.util.List<CSVFieldMeta> fieldList = retrieveFieldMetaList(treeNode);
			fieldOrderPane.setCSVFieldMetaList(fieldList);
		}
	}

	public DefaultMutableTreeNode getDisplayData(boolean withUserInputFromUI)
	{
		if (!withUserInputFromUI)
		{
			return this.treeNode;
		}

		if(this.treeNode==null)
		{
//			CSVSegmentMeta parent = new CSVSegmentMetaImpl(getParentSegmentName(), null);
//			this.treeNode = new CSVSegmentMetaImpl(getSegmentName(), parent);
			CSVSegmentMeta data = new CSVSegmentMetaImpl(getSegmentName(), null);
			SCMTreeNodeLoader nodeLoader = new SCMTreeNodeLoader();
			this.treeNode=nodeLoader.constructTreeNode(data, true);
//			this.treeNode = parentController.getDefaultTreeNode(data, true);
		}
		else
		{
			//may not be used since parent segment may be read-only
//			CSVSegmentMeta parent = this.treeNode.getParent();
//			if(parent!=null)
//			{
//				parent.setName(getParentSegmentName());
//			}
			CSVSegmentMetaImpl userData = (CSVSegmentMetaImpl) this.treeNode.getUserObject();
			userData.setName(getValidatedUserInputOnName());
			userData.setCardinalityWithString(cardinalityField.getSelectedItem().toString());
			if(fieldOrderPane.isDataChanged())
			{
				userData.setFields(fieldOrderPane.getCSVFieldMetaList(true));
				if(treeNode instanceof DefaultSCMTreeMutableTreeNode)
				{
					((DefaultSCMTreeMutableTreeNode)treeNode).resortChildren(new DefaultSCMTreeMutableTreeNodeComparator());
				}
			}
		}
		return this.treeNode;
	}

	public boolean isDataChanged()
	{
		if(this.treeNode==null)
		{
			return false;
		}
		CSVSegmentMeta data = (CSVSegmentMeta) this.treeNode.getUserObject();

        boolean result = !GeneralUtilities.areEqual(getSegmentName(), data.getName());
        if (!result)
                result = !GeneralUtilities.areEqual(this.cardinalityField.getSelectedItem().toString(), data.getCardinalityType().toString());

        result = result || fieldOrderPane.isDataChanged();

        return result;
	}

	/**
	 * Answers if field meta's order has been changed.
	 * @return if field meta's order has been changed.
	 */
	boolean isFieldOrderChanged()
	{
		if(fieldOrderPane!=null && fieldOrderPane.isDataChanged())
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	void setFieldOrderChange(boolean value)
	{
		if(fieldOrderPane!=null)
		{
			fieldOrderPane.setDataChanged(value);
		}
	}

	private java.util.List<CSVFieldMeta> retrieveFieldMetaList(DefaultMutableTreeNode treeNode)
	{
		java.util.List<CSVFieldMeta> resultList = new java.util.ArrayList<CSVFieldMeta>();
		Enumeration enumeration = treeNode.children();
		while(enumeration.hasMoreElements())
		{
			Object obj = enumeration.nextElement();
			if(obj instanceof DefaultMutableTreeNode)
			{
				Object userObj = ((DefaultMutableTreeNode)obj).getUserObject();
				if(userObj instanceof CSVFieldMeta)
				{
					resultList.add((CSVFieldMeta)userObj);
				}
			}
		}
		return resultList;
	}

	private String getValidatedUserInputOnName()
	{
		String resultValue = getSegmentName();
		CSVSegmentMeta data = (CSVSegmentMeta) treeNode.getUserObject();
		String currentValue = data.getName();
		do
		{
			data.setName(resultValue);
			ValidatorResults validatorResults = new ValidatorResults();
			validatorResults.addValidatorResults(CSVMetaValidator.validateSegmentMetaName(data));
			//clean up after validation purpose
			data.setName(currentValue);

			if (validatorResults.getAllMessages().size() == 0)
			{
				break;
			}
			else
			{
				ValidationMessageDialog.displayValidationResults(parentController.getRootContainer(), validatorResults);
				String cosmeticName = (DefaultSettings.getClassNameWithoutPackage(data.getClass()).toLowerCase().indexOf("segment") != -1) ? "Segment" : "Field";
				resultValue = (String) JOptionPane.showInputDialog(parentController, "Edit a " + cosmeticName + " name", "Edit",
						JOptionPane.INFORMATION_MESSAGE, null, null, resultValue);
//				EditTreeNodeAction editAction = new EditTreeNodeAction(parentController);
//				resultValue = editAction.getValidatedUserInput(data);
				if (GeneralUtilities.isBlank(resultValue))
				{//user may cancelled the choice
					resultValue = currentValue;
				}
				setSegmentName(resultValue);
			}
		}
		while(true);
		return resultValue;
	}

    public CSVFieldOrderReshufflePane getTableFields(){
                   return fieldOrderPane;
    }

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.6  2007/11/05 16:53:26  jayannah
 * HISTORY      : Changes to handle the delete button operations
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/10/23 14:35:49  umkis
 * HISTORY      : Fixing Error#36
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/10/13 03:07:00  jayannah
 * HISTORY      : Changes to enable delete action from the properties pane and refresh the tree as well as the property pane, And show a confirmation window for the delete
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/10/05 17:50:08  wangeug
 * HISTORY      : fixbug item 44 of list on 10-05-2007
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/12 15:48:46  umkis
 * HISTORY      : csv cardinality
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/06/13 18:12:12  jiangsc
 * HISTORY      : Upgraded to catch Throwable instead of Exception.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/11/07 19:07:40  umkis
 * HISTORY      : for setting enable or disable button according to selected or non-selected data on segment property pane
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/11/01 23:09:31  jiangsc
 * HISTORY      : UI Enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/10/18 13:35:26  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/04 20:49:13  jiangsc
 * HISTORY      : UI Enhancement to fix data inconsistency between tree and properties panel.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/19 18:54:37  jiangsc
 * HISTORY      : Added reshuffle functionality.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/03 22:07:55  jiangsc
 * HISTORY      : Save Point.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/22 20:53:12  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
