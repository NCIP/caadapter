/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.specification.hsm.actions;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.util.SwingWorker;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.specification.hsm.HSMPanel;
import gov.nih.nci.caadapter.ui.specification.hsm.wizard.NewHSMWizard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * This class defines the new HSM panel action
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.8 $
 *          date        $Date: 2008-06-09 19:54:07 $
 */
public class NewHSMAction extends AbstractContextAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: NewHSMAction.java,v $";
	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/NewHSMAction.java,v 1.8 2008-06-09 19:54:07 phadkes Exp $";

	private static final String COMMAND_NAME = ActionConstants.NEW_HSM_FILE_TXT;
	private static final Character COMMAND_MNEMONIC = new Character('S');
	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_H, Event.CTRL_MASK, false);

	private AbstractMainFrame mainFrame;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public NewHSMAction(AbstractMainFrame mainFrame)
	{
		this(COMMAND_NAME, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public NewHSMAction(String name, AbstractMainFrame mainFrame)
	{
		this(name, null, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public NewHSMAction(String name, Icon icon, AbstractMainFrame mainFrame)
	{
		super(name, icon);
		this.mainFrame = mainFrame;
		setMnemonic(COMMAND_MNEMONIC);
		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DESKTOP_ACTION_TYPE);
		//do not know how to set the icon location name, or just do not matter.
	}

	private void launchPanel(final String messageType, final ActionEvent actionEvent)
	{
		//have to add the new tab so as the panel may update its panel title in the tabbed pane.
		SwingWorker worker = new SwingWorker()
		{
			private HSMPanel hsmPanel;

			public Object construct()
			{
				try
				{
					GeneralUtilities.setCursorWaiting(mainFrame);
					hsmPanel = new HSMPanel(messageType);
					mainFrame.addNewTab(hsmPanel);
					setSuccessfullyPerformed(true);
				}
				catch (Throwable t)
				{
					setSuccessfullyPerformed(false);
					Log.logException(getClass(), "May ignore and proceed", t);
				}
				finally
				{
					//back to normal, in case exception occurred.
					GeneralUtilities.setCursorDefault(mainFrame);
					return null;
				}
			}

			public void finished()
			{
				boolean everythingGood = true;
                String errmsg = "";
                try
				{
					if (!isSuccessfullyPerformed())
					{//no need to proceed further
						return;
					}
					//to be replaced:Eugene
//					GeneralUtilities.setCursorWaiting(mainFrame);
//					HL7V3MetaObjectParser v3MetaObjectParser = new HL7V3MetaObjectParser();
//					HL7V3Meta metaObject = (HL7V3Meta) v3MetaObjectParser.parse(messageType);
//					hsmPanel.setHl7V3MetaRoot(metaObject);
					//do to be replaced: Eugene
					everythingGood = true;
				}
				catch (Throwable e1)
				{
					DefaultSettings.reportThrowableToLogAndUI(this, e1, "", mainFrame, false, true);
					everythingGood = false;
                    errmsg = e1.getMessage();
                }
				finally
				{
					//back to normal.
					GeneralUtilities.setCursorDefault(mainFrame);
					setSuccessfullyPerformed(everythingGood);
					if(!everythingGood)
					{
						Message msg = MessageResources.getMessage("GEN3", new Object[0]);
						JOptionPane.showMessageDialog(mainFrame, msg.toString() + "\n" + errmsg, "Error", JOptionPane.ERROR_MESSAGE);
//						JOptionPane.showMessageDialog(mainFrame, e1.getMessage(), "New HSM Error", JOptionPane.ERROR_MESSAGE);
						//add the new tab if and only if the process is succeeded.
						if (hsmPanel != null && mainFrame.hasComponentOfGivenClass(HSMPanel.class, false) != null)
						{
							//						mainFrame.getTabbedPane().remove(mappingPanel);
							//use close action instead of removing it from tabbed directly so as to allow main frame to clean up maps.
							gov.nih.nci.caadapter.ui.specification.hsm.actions.CloseHSMAction closeAction = new gov.nih.nci.caadapter.ui.specification.hsm.actions.CloseHSMAction(hsmPanel);
							closeAction.actionPerformed(actionEvent);
						}
					}
				}//end of finally
			}
		};
		worker.start();
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
		if (!super.isRequestAuthorized(mainFrame))
		{
			setSuccessfullyPerformed(false);
			return isSuccessfullyPerformed();
		}
//		verify resource
		if (!isResourceReady(mainFrame))
		{
			setSuccessfullyPerformed(false);
			return isSuccessfullyPerformed();
		}

        NewHSMWizard wizard = null;
        try
        {
            wizard = new NewHSMWizard(mainFrame, ActionConstants.NEW_HSM_FILE_TXT, true);
        }
        catch(Exception ee)
        {
            JOptionPane.showMessageDialog(mainFrame, "NewHSMWizard Error (1) : " + ee.getMessage(), "New H3S creation failure !! ", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String msg = wizard.getErrorMessage();

        if (msg != null)
        //if (!((wizard.getErrorMessage() == null)||(wizard.getErrorMessage().trim().equals(""))))
        {
            //System.out.println("EEE2 " + msg);
            if (!msg.trim().equals(""))
            {
                JOptionPane.showMessageDialog(mainFrame, "NewHSMWizard Error (2) : " + wizard.getErrorMessage(), "New H3S creation failure !! ", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        DefaultSettings.centerWindow(wizard);
		wizard.setVisible(true);

        if (wizard.isOkButtonClicked())
		{
			String messageType = wizard.getUserSelectedMessageType();
			launchPanel(messageType, e);
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
		return this.mainFrame;
	}

	@Override
	protected ArrayList getMissedResources() {
		// TODO Auto-generated method stub
		return CaadapterUtil.getModuleResourceMissed(Config.CAADAPTER_HL7_TRANSFORMATION_RESOURCE_REQUIRED);
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.7  2008/03/26 14:42:15  umkis
 * HISTORY      : Re-assigning sortkey
 * HISTORY      :
 * HISTORY      : Revision 1.6  2007/10/04 18:10:06  wangeug
 * HISTORY      : verify resource based on module
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/10/03 18:47:03  umkis
 * HISTORY      : Protect from crashing and display a fit message when resouce.zip is absent.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/09/19 16:42:48  wangeug
 * HISTORY      : authorized user request
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/07/16 19:31:13  wangeug
 * HISTORY      : change UIUID to xmlPath
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/03 20:25:59  wangeug
 * HISTORY      : initila loading hl7 code without "clone"
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.20  2006/11/15 19:57:38  wuye
 * HISTORY      : reorgnize menu items
 * HISTORY      :
 * HISTORY      : Revision 1.19  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/06/13 18:12:13  jiangsc
 * HISTORY      : Upgraded to catch Throwable instead of Exception.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/02/16 18:43:10  umkis
 * HISTORY      : Detail error message can be shown when GEN3 error occurred.
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/12/14 21:37:16  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/09/12 21:57:16  chene
 * HISTORY      : Saved Point
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/09/09 22:41:49  chene
 * HISTORY      : Saved Point
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/09/08 19:37:01  chene
 * HISTORY      : Saved point
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/09/04 21:41:16  chene
 * HISTORY      : Add Meta Object Parser Mimimum
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/30 18:09:48  jiangsc
 * HISTORY      : Fix to #95 to avoid hour glass after map loading.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/04 18:54:04  jiangsc
 * HISTORY      : Consolidated tabPane management into MainFrame
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/02 22:23:11  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/27 13:57:41  jiangsc
 * HISTORY      : Added the first round of HSMPanel.
 * HISTORY      :
 */
