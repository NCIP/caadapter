/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.ui.util;

import gov.nih.nci.cbiit.cmts.ui.common.DataTransferActionType;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * This class is designated to handle data transfer and drag-n-drop, etc., for classes in caAdapter.
 * Due to the fact that possible multi-selecitons may involve in, this class is designed to handle multi-selection.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2008-12-03 20:46:14 $
 *
 */
public class TransferableNode implements Serializable, ClipboardOwner, Transferable
{
	/**
	 * Following are drag/drop, cut/copy/paste related variables, including data
	 * flavor definition
	 */
	//--begin of drag/drop, cut/copy/paste related variables
	public static final DataFlavor NODE_FLAVOR;
	public static final DataFlavor LOCAL_NODE_FLAVOR;
//	public static final DataFlavor NODE_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "Node");

	static
	{
		NODE_FLAVOR = new DataFlavor(TransferableNode.class, "TransferableNode");
		LOCAL_NODE_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + "; class=gov.nih.nci.cbiit.cmts.ui.jgraph.TransferableNode", "Local TransferableNode");
	}

	public static final DataFlavor[] transferDataFlavors = {NODE_FLAVOR, LOCAL_NODE_FLAVOR};

	private static final List flavorList = Arrays.asList(transferDataFlavors);
	private DataTransferActionType actionType;
	private List selectionList;
	private boolean isUsingClipboard;
	//--end of drag/drop, cut/copy/paste related variables

	public TransferableNode(List selectionList, DataTransferActionType type, boolean usingClipboard)
	{
		this.selectionList = selectionList;
		this.actionType = type;
		this.isUsingClipboard = usingClipboard;
	}

	/**
	 * Called by Clipboard to notify this object is no longer in clipboard.
	 * @param clipboard
	 * @param contents
	 */
	public void lostOwnership(Clipboard clipboard, Transferable contents)
	{
		//Log.logInfo(this, "TransferableNode lost ownership of " + clipboard.getName());
		//Log.logInfo(this, "data: " + contents);
		selectionList = null;
		this.actionType = null;
		this.isUsingClipboard = false;
	}

	public DataFlavor[] getTransferDataFlavors()
	{
		/**Implement this java.awt.datatransfer.Transferable method*/
		return (DataFlavor[]) transferDataFlavors.clone();
	}

	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		/**Implement this java.awt.datatransfer.Transferable method*/
		return flavorList.contains(flavor);
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
	{
		/**Implement this java.awt.datatransfer.Transferable method*/
		if (flavor.equals(NODE_FLAVOR))
		{
//			Log.logInfo(this, getClass() + " the flavor requested is NODE_FLAVOR");
			return this;
		}
		else if (flavor.equals(LOCAL_NODE_FLAVOR))
		{
//			Log.logInfo(this, getClass() + " the flavor requested is LOCAL_NODE_FLAVOR");
			return this;
		}
		else
		{
			throw new UnsupportedFlavorException(flavor);
		}
	}

	public List getSelectionList()
	{
		return this.selectionList;
	}

	public synchronized DataTransferActionType getActionType()
	{
		return this.actionType;
	}

	public String toString()
	{
		return (this.selectionList == null) ? null : this.selectionList.toString();
	}
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.1  2008/10/27 20:06:30  linc
 * HISTORY: GUI first add.
 * HISTORY:
 */

