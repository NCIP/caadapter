/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.specification.csv;

import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.ui.specification.csv.actions.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This class defines the mouse listener to responds mouse events occurred on the tree view of CSV Panel.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-06-09 19:54:06 $
 */
public class CSVMetadataTreeMouseAdapter extends MouseAdapter
{
    public static String CHANGE_DISPLAYNAME = "Change Displayname";
	private CSVPanel parentPanel = null;
	private JTree tree = null;
    private JPopupMenu popupMenu;
//    ActionListener actionListener;

	private EditTreeNodeAction editAction;
	private DeleteTreeNodeAction deleteAction;
	private AddSegmentAction addSegmentAction;
	private AddFieldAction addFieldAction;
    private AddChoiceSegmentAction addChoiceSegmentAction;

    public CSVMetadataTreeMouseAdapter(CSVPanel parentPanel)
	{
        super();
		this.parentPanel = parentPanel;
		this.tree = parentPanel.getTree();
		editAction = new EditTreeNodeAction(parentPanel);
		deleteAction = new DeleteTreeNodeAction(parentPanel, true);
		addSegmentAction = new AddSegmentAction(this.parentPanel);
		addFieldAction = new AddFieldAction(this.parentPanel);
        addChoiceSegmentAction = new AddChoiceSegmentAction(this.parentPanel);

        tree.getInputMap().put(editAction.getAcceleratorKey(), editAction.getName());
		tree.getActionMap().put(editAction.getName(), editAction);

//		tree.registerKeyboardAction(editAction, editAction.getName(),
//				editAction.getAcceleratorKey(), JComponent.WHEN_IN_FOCUSED_WINDOW);
		tree.getInputMap().put(deleteAction.getAcceleratorKey(), deleteAction.getName());
		tree.getActionMap().put(deleteAction.getName(), deleteAction);
//		tree.registerKeyboardAction(deleteAction, deleteAction.getName(),
//				deleteAction.getAcceleratorKey(), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void showIfPopupTrigger(MouseEvent mouseEvent)
	{
		if (tree.getSelectionCount() <= 0)
		{//specify at least one selected node.
			// find the selected node.
			TreePath t = tree.getClosestPathForLocation(mouseEvent.getX(), mouseEvent.getY());
			// highlight it.
			tree.setSelectionPath(t);
		}

        if (mouseEvent.isPopupTrigger())
		{
            // setup the right-click popup menu.
			TreePath treePath = tree.getSelectionPath();
			if(treePath!=null)
			{
                retrievePopupMenu();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
				Object userObj = node.getUserObject();

                if(node.getParent()==null)
				{//cannot delete root
					deleteAction.setEnabled(false);
				}
				else
				{
					deleteAction.setEnabled(true);
				}

				if(userObj instanceof CSVFieldMeta)
				{
					addSegmentAction.setEnabled(false);
                    addChoiceSegmentAction.setEnabled(false);
                    addFieldAction.setEnabled(false);
				}
				else if(userObj instanceof CSVSegmentMeta)
				{
					addSegmentAction.setEnabled(true);

                    CSVSegmentMeta seg = (CSVSegmentMeta) userObj;
                    addFieldAction.setEnabled(!seg.isChoiceSegment());
                    addChoiceSegmentAction.setEnabled(!seg.isChoiceSegment());
                    CSVMeta rootMeta=parentPanel.getCSVMeta(false);
					if (rootMeta!=null&&rootMeta.isNonStructure())
					{
						addSegmentAction.setEnabled(false);
						addChoiceSegmentAction.setEnabled(false);
					}
                }

				popupMenu.show(mouseEvent.getComponent(),
						mouseEvent.getX(), mouseEvent.getY());
			}
        }
//		else if(mouseEvent.getClickCount()>=2)
//		{//if not popup trigger and clicked more than twice, assume it is an edit command.
//			ActionEvent ae = new ActionEvent(tree, 0, editAction.getName());
//			editAction.actionPerformed(ae);
//		}

    }

    public void mousePressed(MouseEvent mouseEvent)
	{
        showIfPopupTrigger(mouseEvent);
    }

    public void mouseReleased(MouseEvent mouseEvent)
	{
        showIfPopupTrigger(mouseEvent);
    }

	private JPopupMenu retrievePopupMenu()
	{

        if(popupMenu==null)
		{
			popupMenu = new JPopupMenu("Tree Manipulation");
			//already initiated in constructor.
			popupMenu.add(addSegmentAction);
			popupMenu.add(addFieldAction);
			popupMenu.addSeparator();
            popupMenu.add(addChoiceSegmentAction);
            popupMenu.addSeparator();
            popupMenu.add(editAction);
			popupMenu.add(deleteAction);
		}

		return popupMenu;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2007/12/21 16:00:32  wangeug
 * HISTORY      : disable "addSegment" and "addChoiceSegment" actions if "NON_STRUCTURE"
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/12 15:48:37  umkis
 * HISTORY      : csv cardinality
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
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
 * HISTORY      : Revision 1.12  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/08/22 20:27:56  jiangsc
 * HISTORY      : Enabled AddSegment/AddField
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/22 16:02:40  jiangsc
 * HISTORY      : Work on Add Field/Segment
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/19 20:39:07  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/19 20:38:20  jiangsc
 * HISTORY      : To implement Add Segment/Field
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/07/29 21:59:36  jiangsc
 * HISTORY      : Enhanced.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/25 21:56:50  jiangsc
 * HISTORY      : 1) Added expand all and collapse all;
 * HISTORY      : 2) Added toolbar on the mapping panel;
 * HISTORY      : 3) Consolidated menus;
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/22 20:53:12  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
