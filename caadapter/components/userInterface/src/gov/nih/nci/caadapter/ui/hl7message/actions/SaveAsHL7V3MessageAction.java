/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.hl7message.actions;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.map.TransformationResult;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.DefaultSaveAsAction;
import gov.nih.nci.caadapter.ui.hl7message.HL7MessagePanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * This class defines a concrete "Save As" action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-06-09 19:53:52 $
 */
public class SaveAsHL7V3MessageAction extends DefaultSaveAsAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: SaveAsHL7V3MessageAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/actions/SaveAsHL7V3MessageAction.java,v 1.4 2008-06-09 19:53:52 phadkes Exp $";

	protected transient HL7MessagePanel hl7Panel;

	//define this variable but does not provide access methods. Leave descendant classes to do it, since this class does not need to memerize it.
	protected transient File defaultFile = null;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public SaveAsHL7V3MessageAction(HL7MessagePanel hl7Panel)
	{
		this(COMMAND_NAME, hl7Panel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public SaveAsHL7V3MessageAction(String name, HL7MessagePanel hl7Panel)
	{
		this(name, null, hl7Panel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public SaveAsHL7V3MessageAction(String name, Icon icon, HL7MessagePanel hl7Panel)
	{
		super(name, icon, null);
		this.hl7Panel = hl7Panel;
	}

	/**
	 * Invoked when an action occurs.
	 */
	public boolean doAction(ActionEvent e) throws Exception
	{
		File file =null;
		if (hl7Panel.getMessageFileType()==HL7MessagePanel.MESSAGE_PANEL_CSV)
			file=DefaultSettings.getUserInputOfFileFromGUI(this.hl7Panel, //getUIWorkingDirectoryPath(),
					Config.CSV_DATA_FILE_DEFAULT_EXTENSTION, "Save As...", true, true);
		else
			file=DefaultSettings.getUserInputOfFileFromGUI(this.hl7Panel, //getUIWorkingDirectoryPath(),
				Config.HL7_V3_MESSAGE_FILE_DEFAULT_EXTENSION, "Save As...", true, true);

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
		preActionPerformed(hl7Panel);
		FileWriter fw = null;
		BufferedWriter bw = null;
		boolean oldChangeValue = hl7Panel.isChanged();
		try
		{
			List messageList = hl7Panel.getMessageList();
			int size = messageList==null ? 0 : messageList.size();
			String saveFileExt=Config.HL7_V3_MESSAGE_FILE_DEFAULT_EXTENSION;
			if (hl7Panel.getMessageFileType()==HL7MessagePanel.MESSAGE_PANEL_CSV)
				saveFileExt=Config.CSV_DATA_FILE_DEFAULT_EXTENSTION;
			java.util.List<java.io.File> fileList = FileUtil.constructHL7V3MessageFileNames(file, size,
					saveFileExt, true);
			for(int i=0; i<size; i++)
			{
				Object messageResult =  messageList.get(i);
				String message =null;
				if (messageResult instanceof XMLElement)
					message=((XMLElement)messageResult).toXML().toString();//.getHl7V3MessageText();
				else if (messageResult instanceof TransformationResult)
					message=((TransformationResult)messageResult).getMessageText();
				if (messageResult==null)
					continue;

				File messageFile = fileList.get(i);
				fw = new FileWriter(messageFile);
				bw = new BufferedWriter(fw);

				bw.write(message);
				bw.newLine();
				bw.flush();
				bw.close();
			}
			if (!GeneralUtilities.areEqual(defaultFile, file))
			{//not equal, change it.
				removeFileUsageListener(defaultFile, hl7Panel);
				defaultFile = file;
			}
			hl7Panel.setSaveFile(file);
			//clear the change flag.
			hl7Panel.setChanged(false);
			//try to notify affected panels
			postActionPerformed(hl7Panel);
			JOptionPane.showMessageDialog(hl7Panel.getParent(), "HL7 Message data has been saved successfully.", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
			return true;
		}
		catch(Throwable e)
		{
			hl7Panel.setChanged(oldChangeValue);
			//rethrow the exeception
			throw new Exception(e);
		}
		finally
		{
			try
			{
				//close buffered writer will automatically close enclosed file writer.
				if (bw != null) bw.close();
			}
			catch (Exception e)
			{//intentionally ignored.
			}
		}
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2007/10/09 21:00:13  wangeug
 * HISTORY      : save csv data from hl7MessagePanel
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/26 13:38:49  wangeug
 * HISTORY      : display a list of HL7 message with the HL7 message panel
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/03 19:33:17  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.22  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.21  2006/06/13 18:12:13  jiangsc
 * HISTORY      : Upgraded to catch Throwable instead of Exception.
 * HISTORY      :
 * HISTORY      : Revision 1.20  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.19  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/12/03 16:03:15  chene
 * HISTORY      : Re-engineer TransformationServiceCsvToHL7V3 to support estimate record number
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/10/26 16:22:10  jiangsc
 * HISTORY      : Face lift to provide better error report.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/10/25 18:18:22  jiangsc
 * HISTORY      : Convert to use TransformationResult class.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/10/10 20:48:58  jiangsc
 * HISTORY      : Enhanced dialog operation.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/09/30 20:41:47  jiangsc
 * HISTORY      : Minor update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/30 20:48:14  jiangsc
 * HISTORY      : minor update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/12 18:38:10  jiangsc
 * HISTORY      : Enable HL7 V3 Message to be saved in multiple XML file.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/11 22:10:30  jiangsc
 * HISTORY      : Open/Save File Dialog consolidation.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/05 20:35:49  jiangsc
 * HISTORY      : 0)Implemented field sequencing on CSVPanel but needs further rework;
 * HISTORY      : 1)Removed (Yes/No) for questions;
 * HISTORY      : 2)Removed double-checking after Save-As;
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/03 16:56:15  jiangsc
 * HISTORY      : Further consolidation of context sensitive management.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/02 22:28:53  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/27 22:41:08  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 */
