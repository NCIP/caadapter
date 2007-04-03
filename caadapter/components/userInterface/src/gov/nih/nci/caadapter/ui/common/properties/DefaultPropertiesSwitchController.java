/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/properties/DefaultPropertiesSwitchController.java,v 1.1 2007-04-03 16:17:15 wangeug Exp $
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
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.util.PropertiesProvider;
import gov.nih.nci.caadapter.common.util.PropertiesResult;

import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * This class provides basic functions to help update properties information along user's selection.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:15 $
 */
public class DefaultPropertiesSwitchController implements PropertiesSwitchController, TreeSelectionListener, FocusListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: DefaultPropertiesSwitchController.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/properties/DefaultPropertiesSwitchController.java,v 1.1 2007-04-03 16:17:15 wangeug Exp $";

	private static final String DEFAULT_TITLE = "Properties";

	private Object selectedItem;

	private DefaultPropertiesPage propertiesPage;

	public DefaultPropertiesSwitchController()
	{
	}

	/**
	 * Called whenever the value of the selection changes.
	 *
	 * @param e the event that characterizes the change.
	 */
	public void valueChanged(TreeSelectionEvent e)
	{
		TreePath newPath = e.getNewLeadSelectionPath();
		if(newPath==null)
		{
			setSelectedItem(null);
			ChangeEvent changeEvent = new ChangeEvent(this);
			notifyPropertiesPageSelectionChanged(changeEvent);
		}
		else
		{
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) newPath.getLastPathComponent();
			Object newSelection = treeNode.getUserObject();
			setSelectedItem(newSelection);
			ChangeEvent changeEvent = new ChangeEvent(this);
			notifyPropertiesPageSelectionChanged(changeEvent);
		}
	}

	protected void notifyPropertiesPageSelectionChanged(ChangeEvent e)
	{
		 propertiesPage.updateProptiesDisplay(e);
	}

	public DefaultPropertiesPage getPropertiesPage()
	{
		return propertiesPage;  //To change body of implemented methods use File | Settings | File Templates.
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
				{
					result = localResult;
				}
			}
			catch(Throwable e)
			{
				Log.logException(this, e);
//				System.out.println("getPropertyDescriptors() received: '" + e + "'.");
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
	protected void setSelectedItem(Object newSelectedItem)
	{
		if(!GeneralUtilities.areEqual(newSelectedItem, selectedItem))
		{
			selectedItem = newSelectedItem;
		}
	}

	public String getTitleOfPropertiesPage()
	{
		String result = null;
		if (selectedItem instanceof PropertiesProvider)
		{
			try
			{
				result = ((PropertiesProvider) selectedItem).getTitle();
			}
			catch (Exception e)
			{
//				System.out.println("getPropertyDescriptors() received: '" + e + "'. I will continue...");
				Log.logException(this, "I will continue...", e);
				result = null;
			}
		}

		if(result==null)
		{
			result = DEFAULT_TITLE;
		}
		return result;
	}

	/**
	 * Invoked when a component gains the keyboard focus.
	 */
	public void focusGained(FocusEvent e)
	{
//		System.out.println(e.getSource() + " Gained focus.");
	}

	/**
	 * Invoked when a component loses the keyboard focus.
	 */
	public void focusLost(FocusEvent e)
	{
//		System.out.println(e.getSource() + " Lost focus.");
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.15  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/06/13 18:12:12  jiangsc
 * HISTORY      : Upgraded to catch Throwable instead of Exception.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/23 20:03:59  jiangsc
 * HISTORY      : Enhancement on highlight functionality.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/23 18:57:18  jiangsc
 * HISTORY      : Implemented the new Properties structure
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/04 22:22:26  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/19 22:28:02  jiangsc
 * HISTORY      : 1) Renamed FunctionalBox to FunctionBox to be consistent;
 * HISTORY      : 2) Added SwingWorker to OpenObjectToDbMapAction;
 * HISTORY      : 3) Save Point for Function Change.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/18 19:45:58  jiangsc
 * HISTORY      : Added textual display for functions and properties.
 * HISTORY      : Beautified port display.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/11 18:17:56  jiangsc
 * HISTORY      : Partially implemented property pane.
 * HISTORY      :
 */
