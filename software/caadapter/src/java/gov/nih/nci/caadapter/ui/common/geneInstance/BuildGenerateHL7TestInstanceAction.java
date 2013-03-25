/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.ui.common.geneInstance;

import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.resource.BuildHL7ResourceDialog;
import gov.nih.nci.caadapter.ui.main.HL7AuthorizationDialog;
import gov.nih.nci.caadapter.ui.mapping.V2V3.V2MetaBasicInstallDialog;
import gov.nih.nci.caadapter.ui.mapping.V2V3.V2MetaCollectorDialog;
import gov.nih.nci.caadapter.hl7.mif.v1.BuildResourceUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.*;


/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.4 $
 *          date        Apr 21, 2008
 *          Time:       3:28:10 PM $
 */
public class BuildGenerateHL7TestInstanceAction extends AbstractContextAction
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = ": BuildGenerateHL7TestInstanceAction.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = ": /share/content/cvsroot/hl7sdk/src/gov/nih/nci/caadapter/ui/common/geneInstance/BuildGenerateHL7TestInstanceAction.java,v 1.00 Apr 21, 2008 3:28:10 PM umkis Exp $";

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static final String COMMAND_Generate_Test_Instance = "Generate HL7 v3 Test Instance";

    private AbstractMainFrame mainFrame;

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a default icon.
     */
    public BuildGenerateHL7TestInstanceAction(String name, AbstractMainFrame mainFrame) {
        this(name, null, mainFrame);
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public BuildGenerateHL7TestInstanceAction(String name, Icon icon, AbstractMainFrame mainFrame) {
        super(name, icon);
        this.mainFrame = mainFrame;
        setActionCommandType(DESKTOP_ACTION_TYPE);
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
        GenerateHL7TestInstanceDialog dialog = new GenerateHL7TestInstanceDialog((JFrame)mainFrame);

        return dialog.wasSuccessful();
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
 * HISTORY      : $Log: not supported by cvs2svn $
*/

