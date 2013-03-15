/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.mapping.sdtm.actions;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.mapping.sdtm.Database2SDTMMappingPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * This class defines the action to carry out process report functionality on a SCM file.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2 revision $Revision: 1.3 $ date $Date: 2008-09-29 21:22:50 $
 */
public class GenerateSDTMReportAction extends AbstractContextAction {
	protected static final String COMMAND_NAME = "Generate Report...";

	protected static final Character COMMAND_MNEMONIC = new Character('G');

	/**
	 * Logging constant used to identify source of log entry, that could be later used to create logging mechanism to
	 * uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: GenerateSDTMReportAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem. This String is for
	 * informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/sdtm/actions/GenerateSDTMReportAction.java,v 1.3 2008-09-29 21:22:50 phadkes Exp $";

	protected Database2SDTMMappingPanel sdtmMappingPanel;

	/**
	 * Defines an <code>Action</code> object with a default description string and default icon.
	 */
	public GenerateSDTMReportAction(Database2SDTMMappingPanel sdtmMappingPanel) {
		this(COMMAND_NAME, sdtmMappingPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified description string and a default icon.
	 */
	public GenerateSDTMReportAction(String name, Database2SDTMMappingPanel sdtmMappingPanel) {
		this(name, null, sdtmMappingPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified description string and a the specified icon.
	 */
	public GenerateSDTMReportAction(String name, Icon icon, Database2SDTMMappingPanel sdtmMappingPanel) {
		super(name, icon);
		this.sdtmMappingPanel = sdtmMappingPanel;
		setAdditionalAttributes();
	}

	/**
	 * provide descendant class to override.
	 */
	protected void setAdditionalAttributes() {
		setMnemonic(COMMAND_MNEMONIC);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception {
		// File file = DefaultSettings.getUserInputOfFileFromGUI(this.mappingPanel, getUIWorkingDirectoryPath(),
		// Config.REPORT_FILE_DEFAULT_EXTENSION, "Select File to Save Generated Report", true, true);
		File file = DefaultSettings.getUserInputOfFileFromGUI(this.sdtmMappingPanel, Config.REPORT_FILE_DEFAULT_EXTENSION, "Select File to Save Generated Report", true, true);
		// New Code
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		ArrayList reportArray = sdtmMappingPanel.getSdtmMappingGenerator().results;
		out.write("Mapped(Source_Target)\nPath of origination \t Path of destination\n");
		for (int i = 0; i < reportArray.size(); i++) {
			EmptyStringTokenizer str = new EmptyStringTokenizer(reportArray.get(i).toString(), "~");
			EmptyStringTokenizer sourceStr = new EmptyStringTokenizer(str.nextToken(), "\\");
			sourceStr.deleteTokenAt(1);
			EmptyStringTokenizer targetStr = new EmptyStringTokenizer(str.nextToken(), "\\");
			targetStr.deleteTokenAt(1);
			String str001 = sourceStr.toReportString();
			String str002 = targetStr.toReportString();
			str001 = str001.substring(1, str001.length() - 1);
			str002 = str002.substring(1, str002.length() - 1);
			out.write(str001 + "\t" + str002 + "\n");
		}
		out.close();
		// New Code
		if (file != null) {
			boolean everythingGood = true; // handleValidatorResults(validatorResults);
			if (everythingGood) {
				JOptionPane.showMessageDialog(sdtmMappingPanel.getParent(), "Report has been successfully generated.", "Action Complete", JOptionPane.INFORMATION_MESSAGE);
			}
			setSuccessfullyPerformed(everythingGood);
		}
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent() {
		return sdtmMappingPanel;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
