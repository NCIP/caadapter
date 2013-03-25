/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.mapping.jgraph.actions;

import gov.nih.nci.caadapter.common.SDKMetaData;
import gov.nih.nci.caadapter.ui.common.jgraph.MappingViewCommonComponent;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.caadapter.ui.mapping.jgraph.MiddlePanelJGraphController;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * This class defines the action to delete selected graphic cells.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.8 $
 *          date        $Date: 2008-12-12 15:52:33 $
 */
public class GraphDeleteAction extends DefaultAbstractJgraphAction
{
	private static final String COMMAND_NAME = "Delete";
	private static final Character COMMAND_MNEMONIC = new Character('D');	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public GraphDeleteAction(MiddlePanelJGraphController controller)
	{
		this(null, controller);
	}

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public GraphDeleteAction(MappingMiddlePanel middlePanel, MiddlePanelJGraphController controller)
	{
		this(COMMAND_NAME, middlePanel, controller);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public GraphDeleteAction(String name, MappingMiddlePanel middlePanel, MiddlePanelJGraphController controller)
	{
		this(name, null, middlePanel, controller);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public GraphDeleteAction(String name, Icon icon, MappingMiddlePanel middlePanel, MiddlePanelJGraphController controller)
	{
		super(name, icon, middlePanel, controller);
		setMnemonic(COMMAND_MNEMONIC);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e)
	{
//		Log.logInfo(this, "GraphDeleteAction's actionPerformed() is called.");
		JGraph graph = getController().getGraph();
		if (!graph.isSelectionEmpty())
		{
			int userChoice = JOptionPane.showConfirmDialog(getMiddlePanel(),
				"Are you sure you want to delete?", "Question",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (userChoice == JOptionPane.YES_OPTION)
			{
				Object[] cells = graph.getSelectionCells();
				DefaultGraphCell graphCell=(DefaultGraphCell)cells[0];
				boolean hasMappedColumn=false;
				//check if the mapped Table has any mapped column
				//the following block only apply to MMS mapping
				//not allow to delete a table mapping if column is mapped
				if (graphCell instanceof DefaultEdge)
				{
					MappingViewCommonComponent viewC = (MappingViewCommonComponent) graphCell.getUserObject();
					DefaultMutableTreeNode tableTreeNode=(DefaultMutableTreeNode) viewC.getTargetNode();//.getSourceNode();
					for(int i=0;i<tableTreeNode.getChildCount(); i++)
					{
						DefaultMutableTreeNode childNode=(DefaultMutableTreeNode)tableTreeNode.getChildAt(i);
						if (childNode.getUserObject() instanceof SDKMetaData)
						{
							SDKMetaData columnMeta=(SDKMetaData)childNode.getUserObject();
							if (columnMeta.isMapped())
							{
								hasMappedColumn=true;
								JOptionPane.showMessageDialog(getMiddlePanel(), "Unable to delete, this mapping has children mapping !", "Children Are Mapped", JOptionPane.WARNING_MESSAGE);
								break;
							}
						}
					}
				}
				if (!hasMappedColumn)
					getController().handleDelete();
			}
		}
		else
		{
			JOptionPane.showMessageDialog(getMiddlePanel(), "No graph is currently selected.", "No selection", JOptionPane.WARNING_MESSAGE);
		}
		setSuccessfullyPerformed(true);
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return getMiddlePanel();
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.6  2008/10/21 15:36:40  wangeug
 * HISTORY      : fix bug: allow user delete a function box
 * HISTORY      :
 * HISTORY      : Revision 1.5  2008/09/29 20:36:23  wangeug
 * HISTORY      : enforce code standard: license file, file description, changing history
 * HISTORY      :
 * HISTORY      : Revision 1.4  2008/06/19 17:22:50  wangeug
 * HISTORY      : verify if any child note being mapped before delete a parent tree node
 * HISTORY      :
 * HISTORY      : Revision 1.3  2008/06/09 19:54:06  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2008/06/04 18:08:06  wangeug
 * HISTORY      : handle delete a table-object dependency mappingl
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:57  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/19 18:51:12  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/18 14:51:59  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/09/29 16:34:57  jiangsc
 * HISTORY      : Rename classes
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/05 20:35:52  jiangsc
 * HISTORY      : 0)Implemented field sequencing on CSVPanel but needs further rework;
 * HISTORY      : 1)Removed (Yes/No) for questions;
 * HISTORY      : 2)Removed double-checking after Save-As;
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/07/22 20:53:18  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
