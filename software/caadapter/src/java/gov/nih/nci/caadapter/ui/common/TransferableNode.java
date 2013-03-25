/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.common;

import gov.nih.nci.caadapter.common.Log;

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
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version     Since caAdapter v1.2
 * revision    $Revision: 1.2 $
 * date        $Date: 2008-06-09 19:53:51 $
 */
public class TransferableNode implements Serializable, ClipboardOwner, Transferable
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create logging mechanism
	 * to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: TransferableNode.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/TransferableNode.java,v 1.2 2008-06-09 19:53:51 phadkes Exp $";

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
		LOCAL_NODE_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + "; class=gov.nih.nci.caadapter.ui.common.TransferableNode", "Local TransferableNode");
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
		Log.logInfo(this, "TransferableNode lost ownership of " + clipboard.getName());
		Log.logInfo(this, "data: " + contents);
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
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/03 19:11:00  jiangsc
 * HISTORY      : Some cosmetic update and make HSMPanel able to save the same content to different file.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/22 20:53:15  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/06/23 14:30:13  jiangsc
 * HISTORY      : Updated CSVPanel implementation to support basic drag and drop.
 * HISTORY      :
 */
