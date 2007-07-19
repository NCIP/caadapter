package gov.nih.nci.caadapter.ui.mapping.sdtm.actions;

/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/sdtm/actions/NewSDTMStructureAction.java,v 1.4 2007-07-19 18:51:37 jayannah Exp $
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

import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.mapping.sdtm.NewSDTMWizard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.prefs.Preferences;

/**
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: jayannah $
 * @version Since caAdapter v3.2 revision $Revision: 1.4 $
 */
public class NewSDTMStructureAction extends AbstractContextAction
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String COMMAND_NAME = "SDTM Text File";

    private static final Character COMMAND_MNEMONIC = new Character('S');

    private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_T, Event.CTRL_MASK, false);

    private AbstractMainFrame mainFrame;

    Preferences prefs;

    /**
     * Defines an <code>Action</code> object with a default description
     * string and default icon.
     */
    public NewSDTMStructureAction(AbstractMainFrame mainFrame, Preferences pref)
    {
        this(COMMAND_NAME, mainFrame,pref);
        // mainContextManager = cm;
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a default icon.
     */
    public NewSDTMStructureAction(String name, AbstractMainFrame mainFrame,Preferences pref)
    {
        this(name, null, mainFrame,pref);
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public NewSDTMStructureAction(String name, Icon icon, AbstractMainFrame mainFrame,Preferences pref)
    {
        super(name, icon);
        this.mainFrame = mainFrame;
        this.prefs = pref;
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
     * @return true if the action is finished successfully; otherwise,
     *         return false.
     */
    protected boolean doAction(ActionEvent e) throws Exception
    {
        new NewSDTMWizard(mainFrame, prefs);
        setSuccessfullyPerformed(true);
        return isSuccessfullyPerformed();
    }

    /**
     * Return the associated UI component.
     *
     * @return the associated UI component.
     */
    protected Component getAssociatedUIComponent()
    {
        return mainFrame;
    }
}


