/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/DataTransferActionType.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
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
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
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
