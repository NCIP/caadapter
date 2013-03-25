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
package gov.nih.nci.cbiit.cmts.ui.common;

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
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-27 20:06:30 $
 *
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
 * HISTORY: $Log: not supported by cvs2svn $
 */

