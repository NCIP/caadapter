/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.specification.hsm.actions;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFToXmlExporter;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.DefaultSaveAsAction;
import gov.nih.nci.caadapter.ui.specification.hsm.HSMPanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;

import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * This class defines a concrete "Save As" action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.5 $
 *          date        $Date: 2008-06-09 19:54:07 $
 */
public class SaveAsHsmAction extends DefaultSaveAsAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: SaveAsHsmAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/SaveAsHsmAction.java,v 1.5 2008-06-09 19:54:07 phadkes Exp $";

	protected transient HSMPanel hsmPanel;

	//define this variable but does not provide access methods. Leave descendant classes to do it, since this class does not need to memerize it.
	protected transient File defaultFile = null;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public SaveAsHsmAction(HSMPanel hsmPanel)
	{
		this(COMMAND_NAME, hsmPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public SaveAsHsmAction(String name, HSMPanel hsmPanel)
	{
		this(name, null, hsmPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public SaveAsHsmAction(String name, Icon icon, HSMPanel hsmPanel)
	{
		super(name, icon, null);
		this.hsmPanel = hsmPanel;
	}

	/**
	 * Invoked when an action occurs.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
//		File file = DefaultSettings.getUserInputOfFileFromGUI(this.hsmPanel, getUIWorkingDirectoryPath(), Config.HSM_META_DEFINITION_FILE_DEFAULT_EXTENSION, "Save As...", true, true);
		File file = DefaultSettings.getUserInputOfFileFromGUI(this.hsmPanel,
				Config.HSM_META_DEFINITION_FILE_DEFAULT_EXTENSION+";"+Config.HL7_V3_MESSAGE_FILE_DEFAULT_EXTENSION, "Save As...", true, true);
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

	protected boolean processSaveFile(File file, boolean resetUUID) throws Exception
	{
		preActionPerformed(hsmPanel);
		boolean oldChangeValue = hsmPanel.isChanged();
		try
		{
			String specFileName=file.getPath();
			Object treeRoot=hsmPanel.getTree().getModel().getRoot();
			Object specObject =((DefaultMutableTreeNode)treeRoot).getUserObject();

			if (specFileName.endsWith(Config.HSM_META_DEFINITION_FILE_DEFAULT_EXTENSION))
			{
				System.out.println("SaveAsHsmAction.processSaveFile()..H3S format:"+specFileName);
				OutputStream os = new FileOutputStream(file);
				ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(specObject);
				oos.close();
				os.close();
			}
			else if (specFileName.endsWith(".xml"))
			{
				//save as xml
				MIFToXmlExporter xmlExporter;
				xmlExporter = new MIFToXmlExporter((MIFClass)specObject);
				xmlExporter.exportToFile(specFileName);
			}
			else
				throw new Exception("Invalid format:"+specFileName);
			if (!GeneralUtilities.areEqual(defaultFile, file))
			{//not equal, change it.
				removeFileUsageListener(defaultFile, hsmPanel);
				defaultFile = file;
			}
			hsmPanel.setSaveFile(file);
			//clear the change flag.
			hsmPanel.setChanged(false);
			//try to notify affected panels
			postActionPerformed(hsmPanel);
			JOptionPane.showMessageDialog(hsmPanel.getParent(), "HL7 v3 Specification has been saved successfully.", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
			return true;
		}
		catch (Exception e)
		{
			//restore the change value since something occurred and believe the save process is aborted.
			hsmPanel.setChanged(oldChangeValue);
			//rethrow the exception
			throw e;
		}
		finally
		{
//			try
//			{
//				//close buffered writer will automatically close enclosed file writer.
//				if (bw != null) bw.close();
//			}
//			catch (Exception e)
//			{//intentionally ignored.
//			}
		}
	}
}


