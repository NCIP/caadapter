/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.main;



import gov.nih.nci.caadapter.ui.common.ToolBarHandler;

import javax.swing.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class defines the list of functions performed to support main toolbar under context sensitive manamangement.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: linc $
 * @since caAdapter v1.2
 * @version    $Revision: 1.4 $
 * @date       $Date: 2008-09-26 20:35:27 $
 */
public class MainToolBarHandler implements ToolBarHandler
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: MainToolBarHandler.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/main/MainToolBarHandler.java,v 1.4 2008-09-26 20:35:27 linc Exp $";

	private JToolBar toolbar = null;
	private Map <Action, JButton>actionButtonMap = null;

/**
 * Constructor to initialize JToolBar and the action Map
 *
 */
	public MainToolBarHandler()
	{
		removeAllActions();
	}


	/* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.main.AbstractToolBarHandler#addAction(javax.swing.Action, boolean)
	 */
	public void addAction(Action act, boolean checkWithIcon)
	{
		if(checkWithIcon && act.getValue(Action.SMALL_ICON)==null)
		{//if check with icon but action does not contain any icon
			return;
		}
		JButton button = (JButton) actionButtonMap.get(act);
		if(button==null)
		{
			button = new JButton(act);
			button.setText("");
			button.setEnabled(true);
			toolbar.add(button);
		}
		else
		{//only by setAction(null), could remove completely the effect from older action
			button.setAction(null);
			button.setAction(act);
			button.setEnabled(true);
			toolbar.repaint();
		}

		actionButtonMap.put(act, button);
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.main.AbstractToolBarHandler#removeAction(javax.swing.Action)
	 */
	public void removeAction(Action act)
	{
		JButton button = (JButton) actionButtonMap.get(act);
		if (button != null)
		{
			synchronized(this)
			{
				int count = toolbar.getComponentCount();
				if(count>0)
				{
					int index = toolbar.getComponentIndex(button);
					if(index!=-1)
					{
						toolbar.remove(index);
					}
				}
			}
		}
	}

	public JToolBar getToolBar()
	{
		return toolbar;
	}
	/* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.main.AbstractToolBarHandler#removeAllActions()
	 */
	public void removeAllActions()
	{
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		actionButtonMap = Collections.synchronizedMap(new HashMap<Action, JButton>());
	}

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2008/06/09 19:53:53  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/11/14 20:55:43  wangeug
 * HISTORY      : remove unused method: toolbarRepaint(); set toolbar.floatable(false)
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:36  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/08/02 18:44:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 21:37:19  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/24 22:25:08  jiangsc
 * HISTORY      : Enhanced Toolbar navigation and creation so as to work around an AWT ArrayOutofBoundException.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/04 18:54:05  jiangsc
 * HISTORY      : Consolidated tabPane management into MainFrame
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/03 16:56:19  jiangsc
 * HISTORY      : Further consolidation of context sensitive management.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/02 22:28:57  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 */
