/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.cbiit.cmts.ui.message;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.0
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2009-11-10 19:13:33 $
 */
public class DefaultMessageTableModel extends AbstractTableModel
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: DefaultMessageTableModel.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/cmts/src/gov/nih/nci/cbiit/cmts/ui/message/DefaultMessageTableModel.java,v 1.1 2009-11-10 19:13:33 wangeug Exp $";

	private static final String[] COLUMN_NAMES_ARRAY = new String[]{"Level","Message"};

	private java.util.List messageList;

	public DefaultMessageTableModel()
	{//do nothing
		super();
	}

	public List getMessageList()
	{
		if(messageList==null)
		{
			messageList = new ArrayList();
		}
		return messageList;
	}

	public void setMessageList(List messageList)
	{
		this.messageList = messageList;
		if(this.messageList==null)
		{
			this.messageList = new ArrayList();
		}
		fireTableDataChanged();
	}

	/**
	 * Returns the number of rows in the model. A
	 * <code>JTable</code> uses this method to determine how many rows it
	 * should display.  This method should be quick, as it
	 * is called frequently during rendering.
	 *
	 * @return the number of rows in the model
	 * @see #getColumnCount
	 */
	public int getRowCount()
	{
		return getMessageList().size();
	}

	/**
	 * Returns the number of columns in the model. A
	 * <code>JTable</code> uses this method to determine how many columns it
	 * should create and display by default.
	 *
	 * @return the number of columns in the model
	 * @see #getRowCount
	 */
	public int getColumnCount()
	{
		return COLUMN_NAMES_ARRAY.length;
	}

	/**
	 * Returns a default name for the column using spreadsheet conventions:
	 * A, B, C, ... Z, AA, AB, etc.  If <code>column</code> cannot be found,
	 * returns an empty string.
	 *
	 * @param column the column being queried
	 * @return a string containing the default name of <code>column</code>
	 */
	public String getColumnName(int column)
	{
		return COLUMN_NAMES_ARRAY[column];
	}

	/**
	 * Returns the value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 *
	 * @param	rowIndex	the row whose value is to be queried
	 * @param	columnIndex the column whose value is to be queried
	 * @return the value Object at the specified cell
	 */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		try
        {
            Object obj = messageList.get(rowIndex);
//            if (obj instanceof ValidatorResult)
//            {
//            	ValidatorResult rslt=(ValidatorResult)obj;
//            	if (columnIndex==0)
//            		return rslt.getLevel();
//            	else
//            		return rslt.getMessage();
//
//            }
//            else
//            {
//            	if (columnIndex==0)
//            		return "";
//            }
		    return obj;
        }
        catch(IndexOutOfBoundsException ie)
        {
            String st = "##IndexOutOfBoundsException : " + rowIndex + " : " + messageList.size();
            //System.out.println(st);
            return (Object) st;

        }
    }
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 *
 */
