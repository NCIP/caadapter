/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/CSVSegmentMetadataPropertyPane.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.specification.csv;

import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVSegmentMetaImpl;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
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
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:18:15 $
 */
public class CSVSegmentMetadataPropertyPane extends JPanel
{
	public static final String SEGMENT_NAME_LABEL = "Segment Name:";
	public static final String PARENT_SEGMENT_NAME_LABEL = "Parent Segment Name:";
	private JLabel segmentNameLabel;
	private JTextField segmentNameTextField;
	private JLabel parentSegmentNameLabel;
	private JTextField parentSegmentNameTextField;

	private CSVFieldOrderReshufflePane fieldOrderPane;

	//private CSVPanel parentController;
	private JPanel parentController;
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
	public CSVSegmentMetadataPropertyPane(JPanel parentController)
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

		Dimension segmentSize = segmentNameLabel.getPreferredSize();
		Dimension parentSegmentSize = parentSegmentNameLabel.getPreferredSize();
//		Log.logInfo(this, "segmentSize: " + segmentSize);
//		Log.logInfo(this, "parentSegmentSize: " + parentSegmentSize);
//		Log.logInfo(this, "segmentSize: " + segmentNameLabel.getPreferredSize());
//		Log.logInfo(this, "parentSegmentSize: " + parentSegmentNameLabel.getPreferredSize());
//		Log.logInfo(this, "segmentSize: " + segmentNameLabel.getMaximumSize());
//		Log.logInfo(this, "parentSegmentSize: " + parentSegmentNameLabel.getMaximumSize());
//		Log.logInfo(this, "segmentSize: " + segmentNameLabel.getMinimumSize());
//		Log.logInfo(this, "parentSegmentSize: " + parentSegmentNameLabel.getMinimumSize());

		int textFieldWidth = Math.max(segmentSize.width, parentSegmentSize.width) + 6;
		int textFieldHeight = Math.max(segmentSize.height, parentSegmentSize.height) + 6;

		segmentNameTextField.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));
		parentSegmentNameTextField.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));//segmentNameTextField.getPreferredSize());

		Insets insets = new Insets(5, 5, 5, 5);

		northPanel.add(segmentNameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		northPanel.add(segmentNameTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		northPanel.add(parentSegmentNameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		northPanel.add(parentSegmentNameTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		this.add(northPanel, BorderLayout.NORTH);

		fieldOrderPane = new CSVFieldOrderReshufflePane();
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
			if(fieldOrderPane.isDataChanged())
			{
				userData.setFields(fieldOrderPane.getCSVFieldMetaList(true));
				if(treeNode instanceof DefaultSCMTreeMutableTreeNode)
				{
					((DefaultSCMTreeMutableTreeNode)treeNode).resortChildren(new DefaultSCMTreeMutableTreeNodeComparator());
				}
//				DefaultMutableTreeNode newTreeNode = null;
//				try
//				{
//					newTreeNode = (DefaultMutableTreeNode) parentController.getNodeLoader().loadData(userData);
//				}
//				catch(Throwable e)
//				{
//					Log.logException(getClass(), e);
//				}
//				if(newTreeNode!=null)
//				{
//					treeNode.removeAllChildren();
//				}
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
		result = result || fieldOrderPane.isDataChanged();
		return result;
	}

	/**
	 * Answers if field meta's order has been changed.
	 * @return if field meta's order has been changed.
	 */
	public boolean isFieldOrderChanged()
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
//				ValidationMessageDialog.displayValidationResults(parentController.getRootContainer(), validatorResults);
				ValidationMessageDialog.displayValidationResults(parentController.getRootPane(), validatorResults);
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

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
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
