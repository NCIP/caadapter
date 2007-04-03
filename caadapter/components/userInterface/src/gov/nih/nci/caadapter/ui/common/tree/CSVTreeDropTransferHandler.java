/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/tree/CSVTreeDropTransferHandler.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.common.tree;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.ui.common.TransferableNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;

/**
 * This class defines the handling methods to support drop action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
 */
public class CSVTreeDropTransferHandler extends TreeDefaultDropTransferHandler
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create logging mechanism
	 * to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: CSVTreeDropTransferHandler.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/tree/CSVTreeDropTransferHandler.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $";

	public CSVTreeDropTransferHandler(JTree tree)
	{
		super(tree);
	}

	public CSVTreeDropTransferHandler(JTree tree, int action)
	{
		super(tree, action);
	}

	/**
	 * Called by the DropTargetAdapter in dragEnter, dragOver and dragActionChanged
	 * override so as to let the tree expand along selection.
	 */
	public void dragUnderFeedback(boolean ok, DropTargetDragEvent e)
	{
        super.dragUnderFeedback(ok, e);
		Point p = e.getLocation();
		TreePath path = this.getTree().getPathForLocation(p.x, p.y);
		if (path != null)
		{
			this.getTree().setSelectionPath(path);
//	        if(this.getTree().isExpanded(path) == false)
//			{
//		    	this.getTree().expandPath(path);
//			}
		}
	}

	/**
	 * Called by the DropTargetAdapter in dragEnter, dragOver and dragActionChanged
	 * descendant class shall override this function, since current implementation if the node is a leaf, the drop is NOT OK; otherwise, it is OK;
	 *
	 * @return if drop is OK.
	 */
	public boolean isDropOk(DropTargetDragEvent e)
	{
		boolean result = super.isDropOk(e);
		if(!result)
		{//not allowed by super, simply return
			return result;
		}
		TransferableNode transferableNode = obtainTransferableNode(e);
		Point p = e.getLocation();
		TreePath path = this.getTree().getPathForLocation(p.x, p.y);
		if (path == null) return false;
		TreeNode node = (TreeNode) path.getLastPathComponent();
		TreeModel treeModel = this.getTree().getModel();

		if(treeModel instanceof CSVMetadataTreeModel)
		{
			CSVMetadataTreeModel localTreeModel = (CSVMetadataTreeModel) treeModel;
			java.util.List transferredDataList = transferableNode.getSelectionList();
	//		boolean result = false;
			int size = transferredDataList.size();
			for (int i = 0; i < size; i++)
			{
				Object transferData = transferredDataList.get(i);
				/**
				 * if node is not null and it is not the top root, and it is a leaf and
				 * the transfer data is of type DefaultSourceTreeNode
				 */
				if ((node instanceof DefaultMutableTreeNode) && (transferData instanceof DefaultMutableTreeNode))
				{
					result = localTreeModel.isChildAcceptableToParent(
							(DefaultMutableTreeNode) transferData, (DefaultMutableTreeNode) node, e.getDropAction() == DnDConstants.ACTION_COPY);
				}
				else
				{
					result = false;
					break;
				}
			}
		}
		return result;
	}


	/**
	 * Called by the DropTargetAdapter in drop
	 * return true if add action succeeded
	 * otherwise return false
	 */
	public boolean setDropData(Object transferredData, DropTargetDropEvent e, DataFlavor chosen)
	{
		boolean isSuccess = false;
		Point p = e.getLocation();
		TreePath path = this.getTree().getPathForLocation(p.x, p.y);
		if (path == null)
		{
			Log.logInfo(this, this.getClass() + " path is null. cannot find the exact path. Going to find closest path.");
			path = this.getTree().getClosestPathForLocation(p.x, p.y);
			if (path == null)
			{
				Log.logInfo(this, this.getClass() + " path is null. Even cannot find the closest path. setDropData() will reject drop.");
				return false;
			}
		}

		DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) path.getLastPathComponent();
		try
		{

			TransferableNode dragSourceObjectSelection = (TransferableNode) transferredData;

			java.util.List dragSourceObjectList = dragSourceObjectSelection.getSelectionList();
			if (dragSourceObjectList == null || dragSourceObjectList.size() < 1)
			{
				return false;
			}
			TreeModel tempModel = mTree.getModel();
			if (!(tempModel instanceof CSVMetadataTreeModel))
			{
				System.err.println("I am expecting CSVMetadataTreeModel, but I received '" + tempModel == null ? "null" : tempModel.getClass().getName() + "'");
				return false;
			}

			int dropAction = e.getDropAction();
			CSVMetadataTreeModel treeModel = (CSVMetadataTreeModel) tempModel;
			isSuccess = treeModel.addChildren(targetNode, dragSourceObjectList, dropAction==DnDConstants.ACTION_COPY);
		}
		catch (Exception exp)
		{
			Log.logInfo(this, "Exception Caught by '" + this.getClass().getName() + "': " + exp.getClass().getName() + ";" + exp.getMessage());
			Log.logException(this, exp);
			isSuccess = false;
		}
		finally
		{
			return isSuccess;
		}
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.14  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/24 19:08:40  jiangsc
 * HISTORY      : Enhanced drag and drop
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/04 18:06:28  jiangsc
 * HISTORY      : Updated class description in comments
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/22 20:53:13  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/06/29 21:21:40  jiangsc
 * HISTORY      : More functionality.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/06/24 19:53:07  jiangsc
 * HISTORY      : Save Point
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/06/23 14:30:06  jiangsc
 * HISTORY      : Updated CSVPanel implementation to support basic drag and drop.
 * HISTORY      :
 */
