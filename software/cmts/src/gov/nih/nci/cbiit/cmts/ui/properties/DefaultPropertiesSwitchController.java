/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.cbiit.cmts.ui.properties;


import gov.nih.nci.cbiit.cmts.common.PropertiesProvider;
import gov.nih.nci.cbiit.cmts.ui.util.GeneralUtilities;

/**
 * This class provides basic functions to help update properties information along user's selection.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.4 $
 * @date       $Date: 2009-10-28 16:49:45 $
 */
public class DefaultPropertiesSwitchController implements PropertiesSwitchController
{
	private static final String DEFAULT_TITLE = "Properties";
	private Object selectedItem;
	private DefaultPropertiesPage propertiesPage;

	public DefaultPropertiesPage getPropertiesPage()
	{
		return propertiesPage;
	}

	public void setPropertiesPage(DefaultPropertiesPage newProperitesView)
	{
		this.propertiesPage = newProperitesView;
	}

	/**
	 * This functions will return an array of PropertyDescriptor that would
	 * help Properties GUI to figure out what column information would be
	 * displayed in the View.
	 */
	public PropertiesResult getPropertyDescriptors()
	{
		PropertiesResult result = new PropertiesResult();
		if(selectedItem instanceof PropertiesProvider)
		{
			try
			{
				PropertiesResult localResult = ((PropertiesProvider) selectedItem).getPropertyDescriptors();
				if(localResult!=null)
					result = localResult;
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
		return result;
	}

	public Object getSelectedItem()
	{
		return selectedItem;
	}

	/**
	 * To faciliate sub-classes to set new selected item from different occassions.
	 * @param newSelectedItem
	 */
	public void setSelectedItem(Object newSelectedItem)
	{
		if(!GeneralUtilities.areEqual(newSelectedItem, selectedItem))
		{
			selectedItem = newSelectedItem;
		}
	}

	public String getTitleOfPropertiesPage()
	{
		String result = DEFAULT_TITLE;
		if (selectedItem instanceof PropertiesProvider)
		{
			try
			{
				result = ((PropertiesProvider) selectedItem).getTitle();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				result = DEFAULT_TITLE;
			}
		}
		return result;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2009/10/28 15:05:31  wangeug
 * HISTORY      : clean codes
 * HISTORY      :
 * HISTORY      : Revision 1.2  2009/10/27 18:23:57  wangeug
 * HISTORY      : hook property panel with tree nodes
 * HISTORY      :
 * HISTORY      : Revision 1.1  2008/12/29 22:18:18  linc
 * HISTORY      : function UI added.
 * HISTORY      :
 */
