/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/properties/DefaultPropertiesTableModel.java,v 1.1 2007-04-03 16:17:15 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.common.properties;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.util.PropertiesResult;

import javax.swing.table.AbstractTableModel;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * This class defines the custom table model to support data rendering in Properties pane.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:15 $
 */
public class DefaultPropertiesTableModel extends AbstractTableModel
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: DefaultPropertiesTableModel.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/properties/DefaultPropertiesTableModel.java,v 1.1 2007-04-03 16:17:15 wangeug Exp $";

	protected PropertiesResult propertiesResult;
	protected PropertyDescriptor[] propertiesDescriptorArray;

	protected PropertiesSwitchController propertiesSwitchController;

	protected String[] columnNameArray = {"Name", "Value"};

	final protected String emptyContent = "";

	public DefaultPropertiesTableModel(PropertiesSwitchController propertiesSwitchController)
	{
		if(propertiesSwitchController==null)
		{
			throw new IllegalArgumentException("Argument is null!");
		}
		this.propertiesSwitchController = propertiesSwitchController;
		setPropertiesResult(propertiesSwitchController.getPropertyDescriptors());
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
						Log.logException(this, e);
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
 * HISTORY      : Revision 1.13  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/23 18:57:18  jiangsc
 * HISTORY      : Implemented the new Properties structure
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/04 22:22:25  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/19 22:28:03  jiangsc
 * HISTORY      : 1) Renamed FunctionalBox to FunctionBox to be consistent;
 * HISTORY      : 2) Added SwingWorker to OpenObjectToDbMapAction;
 * HISTORY      : 3) Save Point for Function Change.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/11 18:17:57  jiangsc
 * HISTORY      : Partially implemented property pane.
 * HISTORY      :
 */
