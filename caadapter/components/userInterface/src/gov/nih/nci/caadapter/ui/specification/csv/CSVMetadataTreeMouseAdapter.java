/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/CSVMetadataTreeMouseAdapter.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $
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
import gov.nih.nci.caadapter.ui.specification.csv.actions.AddFieldAction;
import gov.nih.nci.caadapter.ui.specification.csv.actions.AddSegmentAction;
import gov.nih.nci.caadapter.ui.specification.csv.actions.DeleteTreeNodeAction;
import gov.nih.nci.caadapter.ui.specification.csv.actions.EditTreeNodeAction;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This class defines the mouse listener to responds mouse events occurred on the tree view of CSV Panel.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:18:15 $
 */
public class CSVMetadataTreeMouseAdapter extends MouseAdapter
{
    public static String CHANGE_DISPLAYNAME = "Change Displayname";
	//private CSVPanel parentPanel = null;
	private JTree tree = null;
    private JPopupMenu popupMenu;

    private EditTreeNodeAction editAction;
	private DeleteTreeNodeAction deleteAction;
	private AddSegmentAction addSegmentAction;
	private AddFieldAction addFieldAction;

	public CSVMetadataTreeMouseAdapter(CSVPanel parentPanel)
	{
        super();
		this.tree = parentPanel.getTree();
		editAction = new EditTreeNodeAction(parentPanel);
		deleteAction = new DeleteTreeNodeAction(parentPanel, true);
		addSegmentAction = new AddSegmentAction(parentPanel);
		addFieldAction = new AddFieldAction(parentPanel);

		tree.getInputMap().put(editAction.getAcceleratorKey(), editAction.getName());
		tree.getActionMap().put(editAction.getName(), editAction);

		tree.getInputMap().put(deleteAction.getAcceleratorKey(), deleteAction.getName());
		tree.getActionMap().put(deleteAction.getName(), deleteAction);
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
					addFieldAction.setEnabled(false);
				}
				else if(userObj instanceof CSVSegmentMeta)
				{
					addSegmentAction.setEnabled(true);
					addFieldAction.setEnabled(true);
				}

				popupMenu.show(mouseEvent.getComponent(),
						mouseEvent.getX(), mouseEvent.getY());
			}
        }
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
			popupMenu.add(editAction);
			popupMenu.add(deleteAction);
		}

		return popupMenu;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
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
