/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/CSVFieldOrderReshuffleTableModel.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $
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
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:18:15 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/CSVFieldOrderReshuffleTableModel.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $";

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
