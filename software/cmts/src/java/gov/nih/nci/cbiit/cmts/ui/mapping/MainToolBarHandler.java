/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */


package gov.nih.nci.cbiit.cmts.ui.mapping;




import gov.nih.nci.cbiit.cmts.ui.common.ToolBarHandler;

import javax.swing.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class defines the list of functions performed to support main toolbar under context sensitive manamangement.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-03 20:46:14 $
 */
public class MainToolBarHandler implements ToolBarHandler
{

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
 * HISTORY : $Log: 
 */
