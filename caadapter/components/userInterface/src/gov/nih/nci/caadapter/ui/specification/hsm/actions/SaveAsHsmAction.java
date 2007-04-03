/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/SaveAsHsmAction.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
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


package gov.nih.nci.caadapter.ui.specification.hsm.actions;

import gov.nih.nci.caadapter.common.MetaBuilder;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.hl7.clone.meta.HL7V3Meta;
import gov.nih.nci.caadapter.hl7.clone.meta.HL7V3MetaBuilder;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.DefaultSaveAsAction;
import gov.nih.nci.caadapter.ui.common.nodeloader.HSMBasicNodeLoader;
import gov.nih.nci.caadapter.ui.specification.hsm.HSMPanel;

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
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:18:15 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/SaveAsHsmAction.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $";

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
		File file = DefaultSettings.getUserInputOfFileFromGUI(this.hsmPanel, Config.HSM_META_DEFINITION_FILE_DEFAULT_EXTENSION, "Save As...", true, true);
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
		MetaBuilder builder = HL7V3MetaBuilder.getInstance();
		FileOutputStream fw = null;
		BufferedOutputStream bw = null;
		boolean oldChangeValue = hsmPanel.isChanged();
		try
		{
			HSMBasicNodeLoader nodeLoader = hsmPanel.getDefaultHSMNodeLoader();
			HL7V3Meta metaData = nodeLoader.unLoadData((DefaultMutableTreeNode) hsmPanel.getTree().getModel().getRoot(), resetUUID);
			HL7V3Meta existMeta = null;
			if(!resetUUID)
			{//if not resetUUID, preserve in the existing one
				existMeta = hsmPanel.getHl7V3MetaRoot();
				existMeta.setRootCloneMeta(metaData.getRootCloneMeta());
			}
			else
			{//otherwise, preserve in a new one.
				existMeta = hsmPanel.getHl7V3MetaRoot();
				metaData.setMessageID(existMeta.getMessageID());
				metaData.setVersion(existMeta.getVersion());
				existMeta = metaData;
			}

//			fw = new FileOutputStream(file);
//			bw = new BufferedOutputStream(fw);
//			builder.build(bw, existMeta);
            builder.build(file, existMeta);
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
//			return false;
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
 * HISTORY      : Revision 1.26  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.25  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.24  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.23  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.22  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.21  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.20  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/10/26 16:22:10  jiangsc
 * HISTORY      : Face lift to provide better error report.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/10/17 22:32:00  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/10/10 20:48:59  jiangsc
 * HISTORY      : Enhanced dialog operation.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/09/30 20:44:49  jiangsc
 * HISTORY      : Minor update - corrected wording
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/08/30 20:48:15  jiangsc
 * HISTORY      : minor update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/08/19 20:43:47  jiangsc
 * HISTORY      : Change to use HSMBasicNodeLoader
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/08/17 21:27:52  chene
 * HISTORY      : Refactor MetaBuilder to be singleton
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/08/12 18:38:10  jiangsc
 * HISTORY      : Enable HL7 V3 Message to be saved in multiple XML file.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/11 22:10:31  jiangsc
 * HISTORY      : Open/Save File Dialog consolidation.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/05 20:35:50  jiangsc
 * HISTORY      : 0)Implemented field sequencing on CSVPanel but needs further rework;
 * HISTORY      : 1)Removed (Yes/No) for questions;
 * HISTORY      : 2)Removed double-checking after Save-As;
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/04 22:34:53  jiangsc
 * HISTORY      : Fixed the save as version and messageID issue.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/04 21:41:15  chene
 * HISTORY      : Support temporaly file saving
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/04 20:40:49  jiangsc
 * HISTORY      : Updated to persist uuid information upon save and/or save as.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/03 19:11:01  jiangsc
 * HISTORY      : Some cosmetic update and make HSMPanel able to save the same content to different file.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/28 18:18:42  jiangsc
 * HISTORY      : Can Open HSM Panel
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/27 22:41:10  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 */
