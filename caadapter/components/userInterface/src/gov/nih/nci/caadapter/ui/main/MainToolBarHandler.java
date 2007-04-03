/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/main/MainToolBarHandler.java,v 1.1 2007-04-03 16:17:36 wangeug Exp $
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
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:36 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/main/MainToolBarHandler.java,v 1.1 2007-04-03 16:17:36 wangeug Exp $";

	private JToolBar toolbar = null;
	//key: action, value: the JButton on the toolbar.
	private Map <Action, JButton>actionButtonMap = null;
    //private ContextManager contextManager = null;

	public MainToolBarHandler() //ContextManager contextManager)
	{
//		this.contextManager = contextManager;
		toolbar = new JToolBar();
		actionButtonMap = Collections.synchronizedMap(new HashMap<Action, JButton>());
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
		actionButtonMap = Collections.synchronizedMap(new HashMap<Action, JButton>());
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.main.AbstractToolBarHandler#repaintToolBar()
	 */
	public void repaintToolBar()
	{
		toolbar.repaint();
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
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
