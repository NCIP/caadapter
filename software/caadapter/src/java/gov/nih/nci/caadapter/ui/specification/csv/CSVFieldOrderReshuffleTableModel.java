/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.specification.csv;

import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.ui.common.nodeloader.CSVFieldMetaColumnNumberComparator;

import javax.swing.table.AbstractTableModel;
import java.util.Collections;
import java.util.List;

/**
 * This class defines a table model implementation to support the view and manipulation of
 * data in the table view of CSVFieldOrderReshufflePane class.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:54:06 $
 */
public class CSVFieldOrderReshuffleTableModel extends AbstractTableModel
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: CSVFieldOrderReshuffleTableModel.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/CSVFieldOrderReshuffleTableModel.java,v 1.2 2008-06-09 19:54:06 phadkes Exp $";

	/**
	 * Design Rationale:
	 * Instead of changing field number directly on the fly, which may potentially impact the underlying data structure even before user commits the changes,
	 * This model creates an index array to reference to the actual index value of object, so that, the field object's field number will not be changed during
	 * data manipulation but just its display index value correspondingly saved in the sequenceIndexArray.
	 * For example, at start, sequenceIndexArray[i] will be i, which refers to the ith value in the csvFieldMetaList.
	 *
	 * If user highlights the jth element and move it up one step, the value in sequenceIndexArray[j-1]
	 * and sequenceIndexArray[j] will be swapped and the display is refreshed.
	 *
	 * For simplicity, this table model is designed to have csvFieldMetaList immutable, i.e.,
	 * no setter method is provided to csvFieldMetaList.
	 */

	private List csvFieldMetaList;
	private int[] sequenceIndexArray;
	private static final String[] COLUMN_NAMES = {"Number", "Name"};
	private static final Class[] COLUMN_CLASSES = new Class[]{Integer.class, String.class};

	public CSVFieldOrderReshuffleTableModel(List csvFieldMetaList)
	{
		this.csvFieldMetaList = csvFieldMetaList;
		int size = (csvFieldMetaList==null) ? 0 : csvFieldMetaList.size();
		sequenceIndexArray = new int[size];
		initializeSequenceIndexArray();
	}

	protected void initializeSequenceIndexArray()
	{
		int size = sequenceIndexArray.length;
		if(size>0)
		{
			Collections.sort(csvFieldMetaList, new CSVFieldMetaColumnNumberComparator());
		}
		for(int i=0; i<size; i++)
		{
			sequenceIndexArray[i] = i;
		}
	}

	protected void moveUp(int[] selectedMoveUpIndics)
	{
		for(int i=0; i<selectedMoveUpIndics.length; i++)
		{
			int index = selectedMoveUpIndics[i];
			if(index==0)
			{//skip the first one.
				continue;
			}
			else
			{//swap with previous one
				int value = sequenceIndexArray[index-1];
				sequenceIndexArray[index-1] = sequenceIndexArray[index];
				sequenceIndexArray[index] = value;
			}
		}
//		int fromIndex = 0;
//		int toIndex = selectedMoveUpIndics.length-1;
//		fireTableRowsUpdated(fromIndex, toIndex);
		fireTableDataChanged();
	}

	protected void moveDown(int[] selectedMoveDownIndics)
	{
		for(int i=selectedMoveDownIndics.length-1; i>=0; i--)
		{
			int index = selectedMoveDownIndics[i];
			if(index==(sequenceIndexArray.length-1))
			{//skip the last one
				continue;
			}
			else
			{//swap with previous one
				int value = sequenceIndexArray[index];
				sequenceIndexArray[index] = sequenceIndexArray[index+1];
				sequenceIndexArray[index+1] = value;
			}
		}
//		int fromIndex = 0;
//		int toIndex = selectedMoveDownIndics.length-1;
//		fireTableRowsUpdated(fromIndex, toIndex);
		fireTableDataChanged();
	}

	public int getRowCount()
	{
		return sequenceIndexArray.length;
	}

	public int getColumnCount()
	{
		return COLUMN_NAMES.length;
	}

	public String getColumnName(int column)
	{
		return COLUMN_NAMES[column];
	}

	public Class<?> getColumnClass(int columnIndex)
	{
		return COLUMN_CLASSES[columnIndex];
	}

	public Object getValueAt(int row, int column)
	{
		switch(column)
		{
			case 0:
			{
				return new Integer(row + Config.DEFAULT_FIELD_COLUMN_START_NUMBER);
			}
			case 1:
			{
				int index = sequenceIndexArray[row];
				CSVFieldMeta fieldMeta = (CSVFieldMeta) csvFieldMetaList.get(index);
				return fieldMeta.getName();
			}
			default:
				return null;
		}
	}

	public List getCsvFieldMetaList(boolean withUIChanges)
	{
		if(withUIChanges)
		{
			for(int row=0; row<sequenceIndexArray.length; row++)
			{
				int index = sequenceIndexArray[row];
				CSVFieldMeta fieldMeta = (CSVFieldMeta) csvFieldMetaList.get(index);
				fieldMeta.setColumn(row + Config.DEFAULT_FIELD_COLUMN_START_NUMBER);
			}
			//NOTE: After the withUIChanges==true calling, sequenceIndexArray will return back to regular value,
			//in other term, the isDataChanged will always return false after calling getCsvFieldMetaList(true),
			//since at that time, the property window will have the exact data with the information in the tree .
			//back to normal number.
			initializeSequenceIndexArray();
		}
		return csvFieldMetaList;
	}

	/**
	 * return if data in this model is changed.
	 * @return if data in this model is changed.
	 */
	public boolean isDataChanged()
	{
		boolean result = false;
		for(int i=0; i<sequenceIndexArray.length; i++)
		{
			if(sequenceIndexArray[i]!=i)
			{
				result = true;
				break;
			}
		}
		return result;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/18 15:57:51  giordanm
 * HISTORY      : update config-dist + fix a spelling error
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/14 21:02:38  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/19 18:54:36  jiangsc
 * HISTORY      : Added reshuffle functionality.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/15 22:26:34  jiangsc
 * HISTORY      : Save point of the CSV Field Re-ordering
 * HISTORY      :
 */
