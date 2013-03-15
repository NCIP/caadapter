/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common;

import java.awt.dnd.DnDConstants;
import java.io.Serializable;

/**
 * <p/>
 * <b><u>Design Rationale</u></b>
 * The TransferActionType is a Class that refers to the action,
 * which may be performed during data transfer
 * i.e., at moment of cut/copy/paste and drag/drop.
 * <p/>
 * <br>
 * It has and only has two different type, namely,
 * <code>Move</code> and <code>Copy</code>.
 * <br>
 * <code>Move</code> means that the data will be deleted from the source place after being
 * transferred to the destination.
 * <br>
 * <code>Copy</code> means that after the data is copied to the destination, the source place
 * still keeps a copy of the data.
 * <br>
 * When the receiver (i.e. the data is dropped or pasted to a target) receives the
 * data from XXSelection, in addition to the actual data object,
 * it needs to know what kind of action, either cut/drop with move,
 * or copy/drop with copy, caused the data to be transferred, because the
 * receiver may behave differently or make different decision upon different
 * action type.
 * <p/>
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class DataTransferActionType implements Serializable, Cloneable, Comparable
{
    public static final DataTransferActionType LINK =
			new DataTransferActionType("Link", DnDConstants.ACTION_LINK);
	public static final DataTransferActionType MOVE =
			new DataTransferActionType("Move", DnDConstants.ACTION_MOVE);
	public static final DataTransferActionType COPY =
			new DataTransferActionType("Copy", DnDConstants.ACTION_COPY);

	private String actionCode;
	private int dndActionValue;

	public static final DataTransferActionType getDataTransferActionType(int dndAction)
	{
		DataTransferActionType actionType;
		if (dndAction == DnDConstants.ACTION_COPY)
			actionType = DataTransferActionType.COPY;
		else if (dndAction == DnDConstants.ACTION_MOVE)
			actionType = DataTransferActionType.MOVE;
		else if (dndAction == DnDConstants.ACTION_LINK)
			actionType = DataTransferActionType.LINK;
		else
			actionType = null;
		return actionType;
	}

	private DataTransferActionType(String code, int dndAction)
	{
		this.actionCode = code;
		this.dndActionValue = dndAction;
	}

	public int getDnDActionValue()
	{
		return this.dndActionValue;
	}

	public String toString()
	{
		return actionCode;
	}

	public boolean equals(Object o)
	{
		if (this.compareTo(o) == 0)
			return true;
		else
			return false;
	}

	/**
	 * Override the hashCode() function
	 */
	public int hashCode()
	{
		return this.actionCode.hashCode();
	}

	/**
	 * the logic of comparison is
	 * if actionCode is null the value depends on the
	 * condition if obj is null
	 * if actionCode is not null, this function will compare this actionCode with
	 * obj's actionCode.
	 * if obj is null or obj is not an intance of DataTransferActionType,
	 * this is always greater than obj
	 */
	public int compareTo(Object obj)
	{
		/**Implement this java.lang.Comparable method*/
		if (actionCode == null)
			return (obj == null) ? 0 : -1;

		if (obj == null)
			return 1;
		if (obj instanceof DataTransferActionType)
		{
			DataTransferActionType code = (DataTransferActionType) obj;
			return this.actionCode.compareTo(code.actionCode);
		}
		else
			return 1;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
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
 * HISTORY      : Revision 1.7  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/22 20:53:14  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
