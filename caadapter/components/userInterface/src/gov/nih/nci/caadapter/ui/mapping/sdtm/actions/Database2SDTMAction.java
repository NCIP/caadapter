/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/sdtm/actions/Database2SDTMAction.java,v 1.5 2007-07-19 18:51:37 jayannah Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 3.2
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
package gov.nih.nci.caadapter.ui.mapping.sdtm.actions;

import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.mapping.sdtm.Database2SDTMMappingPanel;

import java.awt.Component;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.prefs.Preferences;

import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 * This class creates a Menu item Create V2 V3 action and assigns the action to
 * it.
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: jayannah $
 * @version Since caAdapter v3.2 revision $Revision: 1.5 $ date $Date:
 *          2006/10/03 13:50:47 $
 */
public class Database2SDTMAction extends AbstractContextAction {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final String COMMAND_NAME = "CSV To SDTM Map Specification";

	private static final Character COMMAND_MNEMONIC = new Character('P');

	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke
			.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK, false);

	private AbstractMainFrame mainFrame;

    private Preferences pref;

    /**
	 * Defines an <code>Action</code> object with a default description string
	 * and default icon.
	 */
	public Database2SDTMAction(AbstractMainFrame mainFrame, Preferences prefs) {
		this(COMMAND_NAME, mainFrame, prefs);
		// mainContextManager = cm;
	}

	/**
	 * Defines an <code>Action</code> object with the specified description
	 * string and a default icon.
	 */
	public Database2SDTMAction(String name, AbstractMainFrame mainFrame, Preferences prefs) {
		this(name, null, mainFrame, prefs);
	}

	/**
	 * Defines an <code>Action</code> object with the specified description
	 * string and a the specified icon.
	 */
	public Database2SDTMAction(String name, Icon icon, AbstractMainFrame mainFrame, Preferences prefs) {
		super(name, icon);
		this.mainFrame = mainFrame;
        this.pref = prefs;
        setMnemonic(COMMAND_MNEMONIC);
		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DESKTOP_ACTION_TYPE);
		// do not know how to set the icon location name, or just do not
		// matter.
	}

	/**
	 * The abstract function that descendant classes must be overridden to
	 * provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return
	 *         false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception {
		Database2SDTMMappingPanel mp = new Database2SDTMMappingPanel(mainFrame,"Test", pref);
		mainFrame.addNewTab(mp);
		setSuccessfullyPerformed(true);
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent() {
		return mainFrame;
	}
}
/**
 * HISTORY : $Log: not supported by cvs2svn $
 * HISTORY : Revision 1.3  2007/05/10 15:40:39  jayannah
 * HISTORY : *** empty log message ***
 * HISTORY :
 * HISTORY : Revision 1.1  2007/04/03 16:17:57  wangeug
 * HISTORY : initial loading
 * HISTORY :
 * HISTORY : Revision 1.5  2006/11/28 15:13:43  jayannah
 * HISTORY : Changed the order and names of the menuitems
 * HISTORY :
 * HISTORY : Revision 1.4  2006/11/15 20:12:36  wuye
 * HISTORY : reorganize menu items
 * HISTORY :
 * HISTORY : Revision 1.3  2006/11/08 15:44:53  wuye
 * HISTORY : Main Menu Re-Org
 * HISTORY :
 * HISTORY : Revision 1.2  2006/10/30 20:27:15  jayannah
 * HISTORY : caAdapter_3_2_QA_10_30_2006_V2
 * HISTORY :
 * HISTORY : Revision 1.1  2006/10/30 18:40:52  jayannah
 * HISTORY : action class for the SDTM module
 * HISTORY : HISTORY : Revision 1.2 2006/10/03
 * 13:50:47 jayannah HISTORY : This class creates a Menu item Create V2 V3
 * action and assigns the action to it HISTORY : HISTORY : Revision 1.1
 * 2006/10/03 13:46:57 jayannah HISTORY : This class creates a Menu item Create
 * V2 V3 action and assigns the action to it. HISTORY :
 */
