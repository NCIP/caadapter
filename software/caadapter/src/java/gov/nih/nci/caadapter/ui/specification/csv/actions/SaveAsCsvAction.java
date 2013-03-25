/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.specification.csv.actions;

import gov.nih.nci.caadapter.common.csv.CSVMetaBuilder;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.DefaultSaveAsAction;
import gov.nih.nci.caadapter.ui.common.nodeloader.SCMTreeNodeLoader;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.specification.csv.CSVPanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * This class defines a concrete "Save As" action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:54:07 $
 */
public class SaveAsCsvAction extends DefaultSaveAsAction
{
//	private static final String COMMAND_NAME = ActionConstants.SAVE_AS;
//	private static final Character COMMAND_MNEMONIC = new Character('a');
	protected transient CSVPanel csvPanel;

	//define this variable but does not provide access methods. Leave descendant classes to do it, since this class does not need to memerize it.
	protected transient File defaultFile = null;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public SaveAsCsvAction(CSVPanel csvPanel)
	{
		this(COMMAND_NAME, csvPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public SaveAsCsvAction(String name, CSVPanel csvPanel)
	{
		this(name, null, csvPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public SaveAsCsvAction(String name, Icon icon, CSVPanel csvPanel)
	{
		super(name, icon, ((AbstractMainFrame)csvPanel.getRootPane().getParent()));
		this.csvPanel = csvPanel;
	}

	/**
	 * Invoked when an action occurs.
	 */
	public boolean doAction(ActionEvent e) throws Exception
	{
		File file = DefaultSettings.getUserInputOfFileFromGUI(this.csvPanel, //getUIWorkingDirectoryPath(),
				Config.CSV_METADATA_FILE_DEFAULT_EXTENTION, "Save As...", true, true);
		if (file != null)
		{
			setSuccessfullyPerformed(processSaveFile(file, true));
		}
//		else
//		{
//			Log.logInfo(this, COMMAND_NAME + " command cancelled by user.");
//		}
		return isSuccessfullyPerformed();
	}

	/**
	 * Do the action to save the file.
	 * @param file
	 * @param resetUUID if true, will tell loader to reset UUID field; otherwise, it will keep existing UUID;
	 * The reason to have the option is that the original data may come from another CSV metadata file and
	 * UUIDs of those data should be re-assigned before being persisted.
	 * @return whether the action is performed successfully.
	 */
	protected boolean processSaveFile(File file, boolean resetUUID) throws Exception
	{
		preActionPerformed(csvPanel);
		FileOutputStream fw = null;
		BufferedOutputStream bw = null;
		boolean oldChangeValue = csvPanel.isChanged();
		try
		{
			SCMTreeNodeLoader loader = new SCMTreeNodeLoader();
			CSVMeta metaData = loader.unLoadData((DefaultMutableTreeNode) csvPanel.getTree().getModel().getRoot(), resetUUID);
			CSVMeta existMeta = null;
 			if(!resetUUID)
			{//if not resetUUID, preserve in the existing one
				existMeta = csvPanel.getCSVMeta(true);
				existMeta.setRootSegment(metaData.getRootSegment());
			}
			else
			{//otherwise, preserve in a new one.
			    existMeta = metaData;
			}

            if ( csvPanel.getCSVMeta(false).isNonStructure() )
                    existMeta.setNonStructure( true );

            //Build the file from the meta objects.
			CSVMetaBuilder builder = CSVMetaBuilder.getInstance();
//			fw = new FileOutputStream(file);
//			bw = new BufferedOutputStream(fw);
//			builder.build(bw, existMeta);
			builder.build(file, existMeta);
            //builder.build(file, csvPanel.getCSVMeta(false));

            if (!GeneralUtilities.areEqual(defaultFile, file))
			{//not equal, change it.
				removeFileUsageListener(defaultFile, csvPanel);
				defaultFile = file;
			}
			csvPanel.setSaveFile(file);
			//clear the change flag.
			csvPanel.setChanged(false);

			//try to notify affected panels
			postActionPerformed(csvPanel);

			JOptionPane.showMessageDialog(csvPanel.getParent(), "CSV specification has been saved successfully.", "Save Complete", JOptionPane.INFORMATION_MESSAGE);

			return true;
		}
		catch(Throwable e)
		{
			//restore the change value since something occurred and believe the save process is aborted.
			csvPanel.setChanged(oldChangeValue);
			//rethrow the exception
			throw new Exception(e);
//			return false;
		}
		finally
		{
			try
			{
				//close buffered writer will automatically close enclosed file writer.
				if(bw!=null) bw.close();
			}
			catch(Throwable e)
			{//intentionally ignored.
			}
		}
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/12/20 17:01:24  schroedn
 * HISTORY      : Added NON_STRUCTURE type to CSVmeta
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.30  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.29  2006/06/13 18:12:13  jiangsc
 * HISTORY      : Upgraded to catch Throwable instead of Exception.
 * HISTORY      :
 * HISTORY      : Revision 1.28  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.27  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.26  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.25  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.24  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.23  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.22  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.21  2005/10/26 16:22:10  jiangsc
 * HISTORY      : Face lift to provide better error report.
 * HISTORY      :
 * HISTORY      : Revision 1.20  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/10/17 22:12:39  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/10/10 20:48:57  jiangsc
 * HISTORY      : Enhanced dialog operation.
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/09/30 20:44:39  jiangsc
 * HISTORY      : Minor update - corrected wording
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/09/30 20:27:17  jiangsc
 * HISTORY      : Minor update
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/08/30 20:48:14  jiangsc
 * HISTORY      : minor update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/08/17 21:27:52  chene
 * HISTORY      : Refactor MetaBuilder to be singleton
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/08/11 22:10:28  jiangsc
 * HISTORY      : Open/Save File Dialog consolidation.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/08/05 20:35:46  jiangsc
 * HISTORY      : 0)Implemented field sequencing on CSVPanel but needs further rework;
 * HISTORY      : 1)Removed (Yes/No) for questions;
 * HISTORY      : 2)Removed double-checking after Save-As;
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/08/04 22:36:34  jiangsc
 * HISTORY      : Updated to use new build method using file parameter directly.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/08/04 20:40:48  jiangsc
 * HISTORY      : Updated to persist uuid information upon save and/or save as.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/04 20:25:55  jiangsc
 * HISTORY      : Updated to persist csvMeta's uuid information.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/04 18:54:02  jiangsc
 * HISTORY      : Consolidated tabPane management into MainFrame
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/07/22 20:53:09  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
