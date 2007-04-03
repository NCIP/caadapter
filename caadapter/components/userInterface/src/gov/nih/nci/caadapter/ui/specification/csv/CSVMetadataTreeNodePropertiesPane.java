/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/CSVMetadataTreeNodePropertiesPane.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $
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

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.ui.specification.csv.actions.DeleteTreeNodeAction;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class defines the property pane place holder that will display either
 * the segment property panel or the field property panel upon user selection.
 * Further this panel will provide navigation support through buttons.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:18:15 $
 */
public class CSVMetadataTreeNodePropertiesPane extends JPanel implements ActionListener
{
	private static final String PROPERTIES_NAME_ON_TITLE = "Properties";
	private static final String DEFAULT_PROPERTIES_TITLE = Config.COMMON_METADATA_DISPLAY_NAME + " " + PROPERTIES_NAME_ON_TITLE;
	public static final String PREVIOUS_COMMAND_NAME = "Previous";
	public static final String PREVIOUS_COMMAND_MNEMONIC = "P";
	public static final String NEXT_COMMAND_NAME = "Next";
	public static final String NEXT_COMMAND_MNEMONIC = "N";
	public static final String APPLY_COMMAND_NAME = "Apply";
	public static final String APPLY_COMMAND_MNEMONIC = "A";
	public static final String RESET_COMMAND_NAME = "Reset";
	public static final String RESET_COMMAND_MNEMONIC = "R";

	public static final int SEGMENT_PROPERTY_MODE = 1;
	public static final int FIELD_PROPERTY_MODE = 2;
	
	private JScrollPane centerScrollPane;
    private CSVSegmentMetadataPropertyPane segmentPane;
	//under field mode, we have
	private CSVFieldMetadataPropertyPane fieldPane;

	//default value
	private int visibleMode = SEGMENT_PROPERTY_MODE;
    private CSVPanel parentController;

	public CSVMetadataTreeNodePropertiesPane(CSVPanel parentController)
	{
		this.parentController = parentController;
		initialize();
	}

	public int getVisibleMode()
	{
		return visibleMode;
	}

	public void setVisibleMode(int visibleMode)
	{
		if(this.visibleMode!=visibleMode)
		{
			this.visibleMode = visibleMode;
			constructCenterPane();
		}
	}

	private void initialize()
	{
        this.setLayout(new BorderLayout());
        this.add(constructCenterPane(), BorderLayout.CENTER);
		this.add(constructButtonPanel(), BorderLayout.SOUTH);
	}

	private JScrollPane constructCenterPane()
	{
		JPanel centerPanel=null;
		if(centerScrollPane==null)
		{
			centerScrollPane = new JScrollPane();
			centerScrollPane.setBorder(BorderFactory.createTitledBorder(DEFAULT_PROPERTIES_TITLE));
			segmentPane = new CSVSegmentMetadataPropertyPane(parentController);
			segmentPane.setEditable(false, CSVSegmentMetadataPropertyPane.PARENT_SEGMENT_NAME_LABEL);

			fieldPane = new CSVFieldMetadataPropertyPane(parentController);
			fieldPane.setEditable(false, CSVFieldMetadataPropertyPane.SEGMENT_NAME_LABEL);
			fieldPane.setEditable(false, CSVFieldMetadataPropertyPane.FIELD_NUMBER_LABEL);
		}

		if (isVisibleModeContains(SEGMENT_PROPERTY_MODE))
		{
			centerPanel = segmentPane;
            segmentPane.setMoveUpAndDownButtonsDisabled();
//			segmentPane.addFocusListener(parentController.getDefaultNavigationAdapter());
		}
		else if (isVisibleModeContains(FIELD_PROPERTY_MODE))
		{
			centerPanel = fieldPane;
//			segmentPane.removeFocusListener(parentController.getDefaultNavigationAdapter());
		}
		centerScrollPane.getViewport().setView(centerPanel);

		return centerScrollPane;
	}

	private JPanel constructButtonPanel()
	{

		JPanel buttonPanel = new JPanel(new BorderLayout());
		JPanel westPanel = new JPanel(new GridLayout(1, 2, 5, 5));
		JButton previousButton = new JButton(PREVIOUS_COMMAND_NAME);
		previousButton.setMnemonic(PREVIOUS_COMMAND_MNEMONIC.charAt(0));
		previousButton.addActionListener(this);
		JButton nextButton = new JButton(NEXT_COMMAND_NAME);
		nextButton.setMnemonic(NEXT_COMMAND_MNEMONIC.charAt(0));
		nextButton.addActionListener(this);

		JPanel westOutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		westOutPanel.add(westPanel);
		buttonPanel.add(westOutPanel, BorderLayout.WEST);

		JPanel eastPanel = new JPanel(new GridLayout(1, 3, 5, 5));
		JButton applyButton = new JButton(APPLY_COMMAND_NAME);
		applyButton.setMnemonic(APPLY_COMMAND_MNEMONIC.charAt(0));
		applyButton.addActionListener(this);
		JButton  resetButton = new JButton(RESET_COMMAND_NAME);
		resetButton.setMnemonic(RESET_COMMAND_MNEMONIC.charAt(0));
		resetButton.addActionListener(this);
		DeleteTreeNodeAction deleteAction = new DeleteTreeNodeAction(parentController, true);
		JButton  deleteButton = new JButton(deleteAction);
		eastPanel.add(applyButton);
		eastPanel.add(resetButton);
		eastPanel.add(deleteButton);
		JPanel eastOutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		eastOutPanel.add(eastPanel);
		buttonPanel.add(eastOutPanel, BorderLayout.EAST);

		return buttonPanel;
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		if(PREVIOUS_COMMAND_NAME.equals(command))
		{

		}
		else if(NEXT_COMMAND_NAME.equals(command))
		{

		}
		else if(APPLY_COMMAND_NAME.equals(command))
		{
			applyUserChanges();
		}
		else if(RESET_COMMAND_NAME.equals(command))
		{
			resetToPreviousValue();
            if (isVisibleModeContains(SEGMENT_PROPERTY_MODE)) segmentPane.setMoveUpAndDownButtonsDisabled();
            // inserted by umkis 11/07/05 for No selected data on segment property pane
        }
		else
		{
			Log.logInfo(this, "I have no idea what you did here with command '" + command + "'.");
		}
	}

	/**
	 * Called by outsiders to trigger change check
	 * @param treeNode
	 */
	public boolean setDisplayData(DefaultMutableTreeNode treeNode)
	{
        if(isDataChanged())
		{
			int userChoice = JOptionPane.showConfirmDialog(parentController, "This CSV specification has been changed. Would you like to save it before moving to another one?", "Question", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (userChoice == JOptionPane.YES_OPTION)
			{
				applyUserChanges();
			}
			else if(userChoice == JOptionPane.CANCEL_OPTION)
			{//stay where user is at, abort the attemption to move to different node.
				return false;
			}
		}
		setDisplayData(treeNode, true);
		return true;
	}

	private void setDisplayData(DefaultMutableTreeNode treeNode, boolean refresh)
	{
		if(treeNode==null)
		{
//			Log.logInfo(this, "TreeNode is null.");
			parentController.setPropertiesPaneVisible(false);
			return;
		}
		Object data = treeNode.getUserObject();
        if(data instanceof CSVSegmentMeta)
		{
			setVisibleMode(SEGMENT_PROPERTY_MODE);
			segmentPane.setDisplayData(treeNode, refresh);
			centerScrollPane.setBorder(BorderFactory.createTitledBorder("Segment" + " " + PROPERTIES_NAME_ON_TITLE));
            segmentPane.setMoveUpAndDownButtonsDisabled();  // inserted by umkis 11/07/05 for No selected data on segment property pane
        }
		else if(data instanceof CSVFieldMeta)
		{
			setVisibleMode(FIELD_PROPERTY_MODE);
			fieldPane.setDisplayData(treeNode, refresh);
			centerScrollPane.setBorder(BorderFactory.createTitledBorder("Field" + " " + PROPERTIES_NAME_ON_TITLE));
		}
		else
		{
			centerScrollPane.setBorder(BorderFactory.createTitledBorder(DEFAULT_PROPERTIES_TITLE));
		}
	}

	public DefaultMutableTreeNode getDisplayData(boolean withUserInputFromUI)
	{
		if(isVisibleModeContains(SEGMENT_PROPERTY_MODE))
		{
            return segmentPane.getDisplayData(withUserInputFromUI);
		}
		else if(isVisibleModeContains(FIELD_PROPERTY_MODE))
		{
			return fieldPane.getDisplayData(withUserInputFromUI);
		}
		else
		{
			throw new IllegalStateException("I don't know where I am at now.");
		}
	}

	public boolean isDataChanged()
	{
		if(parentController!=null)
		{
			if(!parentController.isPropertiesPaneVisible())
			{//if property pane is not visible, no need to report whether data is changed or not.
				return false;
			}
		}
		if (isVisibleModeContains(SEGMENT_PROPERTY_MODE))
		{
			return segmentPane.isDataChanged();
		}
		else if (isVisibleModeContains(FIELD_PROPERTY_MODE))
		{
			return fieldPane.isDataChanged();
		}
		else
		{
			throw new IllegalStateException("I don't know where I am at now.");
		}
	}

	private boolean isVisibleModeContains(int mode)
	{
		return (this.visibleMode & mode)==mode;
	}

	/***
	 * Following handle some button actions
	 */
    private void applyUserChanges()
	{
		boolean segmentDataFieldOrderChanged = false;
		if(isVisibleModeContains(SEGMENT_PROPERTY_MODE) && segmentPane != null)
		{
			segmentDataFieldOrderChanged = segmentPane.isFieldOrderChanged();
		}
		DefaultMutableTreeNode targetNode = getDisplayData(true);
		TreeModel treeModel = parentController.getTree().getModel();
		if ((treeModel instanceof DefaultTreeModel) && (targetNode!=null))
		{
			DefaultTreeModel defaultTreeModel = (DefaultTreeModel) treeModel;
			if(segmentDataFieldOrderChanged)
			{
			   defaultTreeModel.nodeStructureChanged(targetNode);
			}
			else
			{
				defaultTreeModel.nodeChanged(targetNode);
			}
		}
		//should not call navitation adapter's setDataChanged(false) because ApplyUserChanges will synchronize the changes from property to the tree structure
		//so from tree structure perspective, it is changed, and the overall tree needs to be persisted.
//		parentController.getDefaultNavigationAdapter().setDataChanged(false);
	}

	private void resetToPreviousValue()
	{
		DefaultMutableTreeNode targetNode = getDisplayData(false);
		setDisplayData(targetNode, true);
//		parentController.getDefaultNavigationAdapter().setDataChanged(false);
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
 * HISTORY      : Revision 1.17  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/11/07 19:07:46  umkis
 * HISTORY      : for setting enable or disable button according to selected or non-selected data on segment property pane
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/11/01 23:09:31  jiangsc
 * HISTORY      : UI Enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/10/18 14:52:42  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/10/17 23:03:24  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/14 21:02:38  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/07 20:09:03  jiangsc
 * HISTORY      : Enhanced the Look and Feel of Validation and Properties.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/04 20:49:14  jiangsc
 * HISTORY      : UI Enhancement to fix data inconsistency between tree and properties panel.
 * HISTORY      :
 */
