/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.common.message;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextInsensitiveAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class SaveAsValidateMessageAction extends AbstractContextInsensitiveAction // DefaultSaveAsAction
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: SaveAsValidateMessageAction.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/message/SaveAsValidateMessageAction.java,v 1.3 2008-06-09 19:53:51 phadkes Exp $";



        private static final String COMMAND_NAME = ActionConstants.SAVE;

        protected transient File defaultFile = null;
        protected transient ValidationMessagePane validationMessagePane = null;

        /**
         * Defines an <code>Action</code> object with a default
         * description string and default icon.
         */
        public SaveAsValidateMessageAction(ValidationMessagePane valPanel)
        {
          this(COMMAND_NAME, valPanel);
        }

        /**
         * Defines an <code>Action</code> object with the specified
         * description string and a default icon.
         */
        public SaveAsValidateMessageAction(String name, ValidationMessagePane valPanel)
        {
            super(name, null);
            this.validationMessagePane = valPanel;
        }

        /**
         * Invoked when an action occurs.
         */
        public boolean doAction(ActionEvent e) throws Exception
        {
            File file = DefaultSettings.getUserInputOfFileFromGUI(this.validationMessagePane, //getUIWorkingDirectoryPath(),
				Config.VALIDATION_RESULT_SAVE_FILE_EXTENSION, "Save As...", true, true);
		    if (file != null)
		      {
			    setSuccessfullyPerformed(processSaveFile(file));
		      }
		    else
		      {
			    Log.logInfo(this, COMMAND_NAME + " command cancelled by user.");
		      }
		    return isSuccessfullyPerformed();

        }

        /**
         * Do the action to save the file.
         * @param file
         * @return whether the action is performed successfully.
         */

        protected boolean processSaveFile(File file) throws Exception
        {

            ValidatorResults results = validationMessagePane.getValidatorResults();

            String fileFilterExtension = Config.VALIDATION_RESULT_SAVE_FILE_EXTENSION;

            String fileName = file.getPath();
            if (!fileName.endsWith(fileFilterExtension)) fileName = fileName + fileFilterExtension;

            if (!results.savePrintableFile(fileName,validationMessagePane.getSelectedMessageLevel()))
              {
                JOptionPane.showMessageDialog(validationMessagePane,"Validation Results message saving Failure!!","Saving Error!",JOptionPane.ERROR_MESSAGE);
                return false;
              }
            else
              {
                JOptionPane.showMessageDialog(validationMessagePane,"Validation Results messages were successfully saved","Successful Saving!",JOptionPane.INFORMATION_MESSAGE);
                return true;
              }
        }

        protected Component getAssociatedUIComponent()
        {
            return validationMessagePane;
        }
}






/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/07/31 17:43:28  wangeug
 * HISTORY      : resolve issues with preliminary test of release 4.0
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/14 21:37:20  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/11/03 18:05:17  umkis
 * HISTORY      : Using DefaultSettings.getUserInputOfFileFromGUI
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/11/02 21:10:26  umkis
 * HISTORY      : defect# 172
 * HISTORY      :
 */
