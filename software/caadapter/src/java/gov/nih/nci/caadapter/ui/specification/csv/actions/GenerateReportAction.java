/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.specification.csv.actions;

import gov.nih.nci.caadapter.common.csv.CSVMetaReportGenerator;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.specification.csv.CSVPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * This class defines the action to carry out process report functionality on a SCM file.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:54:07 $
 */
public class GenerateReportAction extends AbstractContextAction
{
	protected static final String COMMAND_NAME = "Generate Report...";
	protected static final Character COMMAND_MNEMONIC = new Character('G');

	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: GenerateReportAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/actions/GenerateReportAction.java,v 1.3 2008-06-09 19:54:07 phadkes Exp $";

	protected CSVPanel csvPanel;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public GenerateReportAction(CSVPanel csvPanel)
	{
		this(COMMAND_NAME, csvPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public GenerateReportAction(String name, CSVPanel csvPanel)
	{
		this(name, null, csvPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public GenerateReportAction(String name, Icon icon, CSVPanel csvPanel)
	{
		super(name, icon);
		this.csvPanel = csvPanel;
		setAdditionalAttributes();
	}

	/**
	 * provide descendant class to override.
	 */
	protected void setAdditionalAttributes()
	{
		setMnemonic(COMMAND_MNEMONIC);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
		//		File file = DefaultSettings.getUserInputOfFileFromGUI(this.csvPanel, getUIWorkingDirectoryPath(), Config.REPORT_FILE_DEFAULT_EXTENSION, "Select File to Save Generated Report", true, true);
		File file = DefaultSettings.getUserInputOfFileFromGUI(this.csvPanel, Config.REPORT_FILE_DEFAULT_EXTENSION, "Select File to Save Generated Report", true, true);
		if (file != null)
		{
			//do something.
			//			Log.logInfo(this, "GenerateReportAction will do something now.");
			CSVMeta csvMeta = csvPanel.getCSVMeta(false);
			CSVMetaReportGenerator generator = new CSVMetaReportGenerator();
			generator.generate(file, csvMeta);
			JOptionPane.showMessageDialog(csvPanel.getParent(), "Report has been successfully generated.", "Action Complete", JOptionPane.INFORMATION_MESSAGE);
			setSuccessfullyPerformed(true);

		}
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return csvPanel;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/04/19 14:00:32  wangeug
 * HISTORY      : clean code
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/02 20:34:10  chene
 * HISTORY      : Rename the MapDriver to TransformationServiceCsvToHL7V3
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/18 15:25:43  chene
 * HISTORY      : Fixed
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/10 20:48:56  jiangsc
 * HISTORY      : Enhanced dialog operation.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/09/30 20:46:37  jiangsc
 * HISTORY      : Integrated process report action.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/09/29 21:13:11  jiangsc
 * HISTORY      : New action
 * HISTORY      :
 */
