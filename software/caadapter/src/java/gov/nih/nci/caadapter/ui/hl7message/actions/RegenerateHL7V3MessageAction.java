/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.hl7message.actions;

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.util.SwingWorker;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.hl7message.HL7MessagePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * This class defines a action to trigger generation of HL7 v3 message again.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.7 $
 *          date        $Date: 2008-09-29 19:58:08 $
 */
public class RegenerateHL7V3MessageAction extends AbstractContextAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: RegenerateHL7V3MessageAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/actions/RegenerateHL7V3MessageAction.java,v 1.7 2008-09-29 19:58:08 wangeug Exp $";

	public static final String COMMAND_NAME = "Regenerate";
	public static final Character COMMAND_MNEMONIC = new Character('R');
	//	public static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false);
	private static final String TOOL_TIP_DESCRIPTION = COMMAND_NAME;

	private HL7MessagePanel hl7Panel;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public RegenerateHL7V3MessageAction(HL7MessagePanel hl7Panel)
	{
		this(COMMAND_NAME, hl7Panel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public RegenerateHL7V3MessageAction(String name, HL7MessagePanel hl7Panel)
	{
		this(name, null, hl7Panel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public RegenerateHL7V3MessageAction(String name, Icon icon, HL7MessagePanel hl7Panel)
	{
		super(name, icon);
		this.hl7Panel = hl7Panel;
		setAdditionalAttributes();
	}

	protected void setAdditionalAttributes()
	{//override super class's one to plug in its own attributes.
		setMnemonic(COMMAND_MNEMONIC);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
		setShortDescription(TOOL_TIP_DESCRIPTION);
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
		SwingWorker worker = new SwingWorker()
		{
			private File dataFile;
			private File mapFile;
			public Object construct()
			{
				try
				{
					GeneralUtilities.setCursorWaiting(hl7Panel);
					final String dataFilePath = hl7Panel.getDataFileNameFieldValue();
					final String mapFilePath = hl7Panel.getMapFileNameFieldValue();
					dataFile = new File(dataFilePath);
					mapFile = new File(mapFilePath);

					hl7Panel.clearDataFromUI();

					setSuccessfullyPerformed(true);
				}
				catch (Throwable e1)
				{
					DefaultSettings.reportThrowableToLogAndUI(this, e1, "", hl7Panel, false, true);
					Message msg = MessageResources.getMessage("GEN0", new Object[]{e1.getMessage()});
					//report the nice to have message
					DefaultSettings.reportThrowableToLogAndUI(this, null, msg.toString(), hl7Panel, true, false);
					setSuccessfullyPerformed(false);
				}
				finally
				{
					//back to normal, in case exception occurred.
					GeneralUtilities.setCursorDefault(hl7Panel);
					afterMathHandling();
				}
				return null;
			}

			public void finished()
			{
				if (!isSuccessfullyPerformed())
				{//no need to proceed further
					return;
				}

				boolean everythingGood = true;
				try
				{
					GeneralUtilities.setCursorWaiting(hl7Panel);
					ValidatorResults validatorResults = null;
					if (dataFile!=null && mapFile!=null && dataFile.exists() && mapFile.exists())
					{
						validatorResults = hl7Panel.generateMappingMessages(dataFile, mapFile);
						everythingGood = handleValidatorResults(validatorResults);
					}
					else
					{
						StringBuffer buf = new StringBuffer();
						if(dataFile==null)
						{
							buf.append("Data file is null!\n");
						}
						if(mapFile==null)
						{
							buf.append("Map file is null!\n");
						}
						if(dataFile!=null && !dataFile.exists())
						{
							buf.append("Data file does not exist!\n");
						}
						if(mapFile!=null && !mapFile.exists())
						{
							buf.append("Map file does not exist!\n");
						}
						everythingGood = false;

						JOptionPane.showMessageDialog(hl7Panel, buf.toString(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				catch(Throwable e1)
				{
					DefaultSettings.reportThrowableToLogAndUI(this, e1, "", hl7Panel, false, true);
					Message msg = MessageResources.getMessage("GEN0", new Object[]{e1.getMessage()});
					//report the nice to have message
					DefaultSettings.reportThrowableToLogAndUI(this, null, msg.toString(), hl7Panel, true, false);
					everythingGood = false;
				}
				finally
				{
					//back to normal.
					GeneralUtilities.setCursorDefault(hl7Panel);
					setSuccessfullyPerformed(everythingGood);
					afterMathHandling();
				}
			}

			private void afterMathHandling()
			{
				if (!isSuccessfullyPerformed())
				{//do the clean up.
					Container container = hl7Panel.getRootContainer();
					if (container instanceof AbstractMainFrame)
					{
						AbstractMainFrame mainFrame = (AbstractMainFrame) container;
						if (hl7Panel != null && mainFrame.hasComponentOfGivenClass(hl7Panel.getClass(), false) != null)
						{
							//						mainFrame.getTabbedPane().remove(mappingPanel);
							//use close action instead of removing it from tabbed directly so as to allow main frame to clean up maps.
							gov.nih.nci.caadapter.ui.hl7message.actions.CloseHL7V3MessageAction closeAction = new gov.nih.nci.caadapter.ui.hl7message.actions.CloseHL7V3MessageAction(hl7Panel);
							closeAction.actionPerformed(null);
						}
					}//end of instanceof MainFrame
				}
			}
		};
		worker.start();

		return false;
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return hl7Panel;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.6  2008/06/09 19:53:52  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/09/10 16:41:51  wangeug
 * HISTORY      : use a local Progressor instance
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/09/04 20:45:16  wangeug
 * HISTORY      : add progressor
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/09/04 18:30:44  wangeug
 * HISTORY      : add progressor
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/09/04 17:37:09  wangeug
 * HISTORY      : add progressor
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/03 19:33:17  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/14 22:11:56  jiangsc
 * HISTORY      : Updated functionality.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/12/12 22:56:25  jiangsc
 * HISTORY      : With enhanced functions.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/12/08 23:52:17  jiangsc
 * HISTORY      : minor update
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/11/14 19:55:51  jiangsc
 * HISTORY      : Implementing UI enhancement
 * HISTORY      :
 */
