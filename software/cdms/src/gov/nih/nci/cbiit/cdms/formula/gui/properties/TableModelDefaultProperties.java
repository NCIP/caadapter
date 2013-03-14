/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui.properties;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import javax.swing.table.AbstractTableModel;

	public class TableModelDefaultProperties extends AbstractTableModel
	{
		protected PropertiesResult propertiesResult;
		protected PropertyDescriptor[] propertiesDescriptorArray;	protected String[] columnNameArray = {"Name", "Value"};
		final protected String emptyContent = "";

		public PropertiesResult getPropertiesResult()
		{
			return propertiesResult;
		}

		public void setPropertiesResult(PropertiesResult propertiesResult)
		{
			this.propertiesResult = propertiesResult;
			if(propertiesResult==null)
			{
				propertiesDescriptorArray = new PropertyDescriptor[0];
			}
			else
			{
				propertiesDescriptorArray = this.propertiesResult.getAllPropertyDescriptors().toArray(new PropertyDescriptor[0]);
			}
			fireTableDataChanged();
		}

		public String[] getColumnNameArray()
		{
			return columnNameArray;
		}

		public void setColumnNameArray(String[] columnNameArray)
		{
			if(columnNameArray==null)
			{
				throw new IllegalArgumentException("column name array cannot be set to null!");
			}
			this.columnNameArray = columnNameArray;
			fireTableStructureChanged();
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
			if(propertiesResult !=null)
			{
				return propertiesResult.getTotalPropertiesCount();
			}
			else
			{
				return 0;
			}
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
			return columnNameArray==null ? 0 : columnNameArray.length;
		}

		/**
		 * Return the name of the given column; if not found, return an empty string.
		 *
		 * @param column the column being queried
		 * @return a string containing the default name of <code>column</code>
		 */
		public String getColumnName(int column)
		{
			String result = emptyContent;
			if(column<getColumnCount())
			{
				result = columnNameArray[column];
			}
			return result;
		}

		/**
		 * Returns the class of given columnIndex.
		 *
		 * @param columnIndex the column being queried
		 * @return the Object.class
		 */
		public Class<?> getColumnClass(int columnIndex)
		{
			Class result = null;
			if(columnIndex<getColumnCount())
			{
				if(columnIndex==0)
				{//assuming the first column is the name column, which will be string
					result = String.class;
				}
				else
				{
					result = Object.class;

				}
			}
			return result;
		}

		/**
		 * Returns the value for the cell at <code>columnIndex</code> and
		 * <code>rowIndex</code>.
		 *
		 * @param	rowIndex	the row whose value is to be queried
		 * @param	columnIndex the column whose value is to be queried
		 * @return	the value Object at the specified cell
		 */
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if(rowIndex>=getRowCount() || columnIndex>=getColumnCount())
			{
				return null;
			}

			Object result = emptyContent;
			PropertyDescriptor propDescriptor = propertiesDescriptorArray[rowIndex];
			switch(columnIndex)
			{
				case 0 : //assume the name
					result = propDescriptor.getName();
					break;
				case 1 : //assume the value
					{
						Method getterMethod = propDescriptor.getReadMethod();
						if (getterMethod == null)
							return emptyContent;
						Object targetItem = propertiesResult.getTargetObject(propDescriptor);
						if(targetItem==null)
							return result;
						try
						{
							result = getterMethod.invoke(targetItem, new Object[0]);
						}
						catch (Exception e)
						{
							e.printStackTrace();
							//Log.logException(this, e);
						}
						if (result == null)
							result = emptyContent;

						break;
					}//end of case 1:
			}
			return result;
		}

}
