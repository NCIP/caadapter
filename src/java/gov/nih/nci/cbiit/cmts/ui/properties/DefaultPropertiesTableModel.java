/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */


package gov.nih.nci.cbiit.cmts.ui.properties;


import javax.swing.table.AbstractTableModel;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * This class defines the custom table model to support data rendering in Properties pane.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-29 22:18:18 $
 */
public class DefaultPropertiesTableModel extends AbstractTableModel
{
	protected PropertiesResult propertiesResult;
	protected PropertyDescriptor[] propertiesDescriptorArray;

	protected PropertiesSwitchController propertiesSwitchController;

	protected String[] columnNameArray = {"Name", "Value"};

	final protected String emptyContent = "";

	public DefaultPropertiesTableModel(PropertiesSwitchController propertiesSwitchController)
	{
		if(propertiesSwitchController==null)
		{
			//throw new IllegalArgumentException("Argument is null!");
		}else{
			this.propertiesSwitchController = propertiesSwitchController;
			setPropertiesResult(propertiesSwitchController.getPropertyDescriptors());
		}
	}

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
//				if(propertiesResult!=null)
//				{//considering the value might come as Boolean, String, etc.
					result = Object.class;
//				}
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
//				String name = propertiesResult[rowIndex].getName();
//				System.out.println("getName() returns: '" + name + "'");
//				name = propertiesResult[rowIndex].getDisplayName();
//				System.out.println("getDisplayName() returns: '" + name + "'");
//				name = propertiesResult[rowIndex].getShortDescription();
//				System.out.println("getShortDescription() returns: '" + name + "'");
				break;
			case 1 : //assume the value
				{
					Method getterMethod = propDescriptor.getReadMethod();
					if (getterMethod == null)
					{
//						System.out.println("getterMethod is null");
						return emptyContent;
					}
					else
					{
//						System.out.println("getterMethod is '" + getterMethod + "'.");
					}
					Object targetItem = propertiesResult.getTargetObject(propDescriptor);
					if(targetItem==null)
					{
//						System.out.println("No item is selected.");
						return result;
					}
					try
					{
						result = getterMethod.invoke(targetItem, new Object[0]);
					}
					catch (Exception e)
					{
                        System.out.println("Exception DefaultPropertiesTableModel.getValueAt() : " + e.getMessage() + ", item=" + targetItem);
                        e.printStackTrace();
						//Log.logException(this, e);
					}
					if (result == null)
					{
	//                System.out.println("Value is: '"+obj+"'");
						result = emptyContent;
					}
					else
					{
	//                System.out.println("Value is: '"+obj+"' of type"+obj.getClass().getName());
					}
					break;
				}//end of case 1:
		}
		return result;
	}

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
