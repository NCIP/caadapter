/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.mapping.sdtm.actions;

/*
 * saves the SDTM action on the UI
 * @author OWNER: $Author: phadkes $
 * @author LAST UPDATE $Author: phadkes $
 * @since  caAdapter v4.2
 * @version    $Revision: 1.4 $
 * @date       $Date: 2008-09-29 21:22:50 $
*/

import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.sdtm.SDTMMappingGenerator;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.DefaultSaveAction;
import gov.nih.nci.caadapter.ui.mapping.hl7.HL7MappingPanel;
import gov.nih.nci.caadapter.ui.mapping.sdtm.Database2SDTMMappingPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public class SaveSdtmAction extends SaveAsSdtmAction {
    public static final String COMMAND_NAME = ActionConstants.SAVE;
    public static final Character COMMAND_MNEMONIC = new Character('S');
    public static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK, false);
    public static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("fileSave.gif"));
    public static final String TOOL_TIP_DESCRIPTION = "Save";
    /**
     * Logging constant used to identify source of log entry, that could be later used to create logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: SaveSdtmAction.java,v $";
    /**
     * String that identifies the class version and solves the serial version UID problem. This String is for informational purposes only and MUST not be made
     * final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/sdtm/actions/SaveSdtmAction.java,v 1.4 2008-09-29 21:22:50 phadkes Exp $";

    // private static final String TOOL_TIP_DESCRIPTION = "Save a Mapping File";
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    public SaveSdtmAction(HL7MappingPanel mappingPanel) {
        this(DefaultSaveAction.COMMAND_NAME, mappingPanel);
    }

    /**
     * Defines an <code>Action</code> object with the specified description string and a default icon.
     */
    public SaveSdtmAction(String name, HL7MappingPanel mappingPanel) {
        this(name, DefaultSaveAction.IMAGE_ICON, mappingPanel);
    }

    /**
     * Defines an <code>Action</code> object with the specified description string and a the specified icon.
     */
    public SaveSdtmAction(String name, Icon icon, HL7MappingPanel mappingPanel) {
        super(name, icon, mappingPanel);
        // setAdditionalAttributes();
    }

    public SaveSdtmAction(Database2SDTMMappingPanel mappingPanel, SDTMMappingGenerator _sdtmMappingGenerator) {
        super(COMMAND_NAME, mappingPanel, _sdtmMappingGenerator);
        // setAdditionalAttributes();
    }

    /**
     * Will be called by the constructor to set additional attributes.
     */
    protected void setAdditionalAttributes() {// override super class's one to plug in its own attributes.
        setIcon(DefaultSaveAction.IMAGE_ICON);
        setMnemonic(DefaultSaveAction.COMMAND_MNEMONIC);
        setAcceleratorKey(DefaultSaveAction.ACCELERATOR_KEY_STROKE);
        setActionCommandType(DOCUMENT_ACTION_TYPE);
        setShortDescription(TOOL_TIP_DESCRIPTION);
    }

    /**
     * Invoked when an action occurs.
     */
    protected boolean doAction(ActionEvent e) throws Exception {
        /**
         * Design Rationale: <br>
         * 1) Get the latest file from GUI panel; <br>
         * 2) if defaultFile and fileFromPanel are not equal, let the defaultFile be the latest value; <br>
         * 3) if the latest value is null, trigger SaveAs function, i.e., ask for user input; <br>
         * 4) if not, proceed the saving; <br>
         */
        File fileFromPanel = mappingPanel.getSaveFile();
        if (!GeneralUtilities.areEqual(defaultFile, fileFromPanel)) {
            defaultFile = fileFromPanel;
        }
        if (defaultFile == null) {
            return super.doAction(e);
        } else {
            return processSaveFile(defaultFile);
		}
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
