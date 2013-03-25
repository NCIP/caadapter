/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.mapping.GME.actions;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.DefaultSaveAsAction;
import gov.nih.nci.caadapter.ui.common.context.DefaultContextManagerClientPanel;
import gov.nih.nci.caadapter.ui.mapping.catrend.CsvToXmiMappingReportPanel;
import gov.nih.nci.caadapter.ui.mapping.GME.XsdToXmiMappingReportPanel;

import java.awt.event.ActionEvent;
import java.io.File;

/**
 * This class defines a concrete "Save As" action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-09-17 21:32:24 $
 */
public class SaveAsXsdToXmiMappingReportAction extends DefaultSaveAsAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: SaveAsXsdToXmiMappingReportAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/GME/actions/SaveAsXsdToXmiMappingReportAction.java,v 1.3 2008-09-17 21:32:24 phadkes Exp $";

	private XsdToXmiMappingReportPanel holderPane;
	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public SaveAsXsdToXmiMappingReportAction(DefaultContextManagerClientPanel contentPanel)
	{
		super(COMMAND_NAME, null);
		if (contentPanel instanceof XsdToXmiMappingReportPanel)
		{
			holderPane=(XsdToXmiMappingReportPanel)contentPanel;
			setDefaultFile(holderPane.getSaveFile());
		}
	}


	/**
	 * Invoked when an action occurs.
	 */
	public boolean doAction(ActionEvent e) throws Exception
	{
		File file =this.getDefaultFile();
		if (file==null)
		{
			file=DefaultSettings.getUserInputOfFileFromGUI(this.getAssociatedUIComponent(),
				Config.HL7_V3_MESSAGE_FILE_DEFAULT_EXTENSION, "Save Mapping Report As...", true, true);
			holderPane.setSaveFile(file);
			setDefaultFile(file);
		}
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

	protected boolean processSaveFile(File file) throws Exception
	{
		/**
		 * Possible enhancement:
		 * Remove all previous related message files;
		 */
		holderPane.getReporter().generateReportFile(file);
		return true;
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
**/