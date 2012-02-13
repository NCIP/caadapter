/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.ui.actions;


import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmts.ui.common.ContextManagerClient;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrame;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrameContainer;
import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMainPanel;
import gov.nih.nci.cbiit.cmts.ui.util.GeneralUtilities;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

/**
 * This class defines the open Map panel action.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.3 $
 * @date       $Date: 2009-11-23 18:32:47 $
 */
public class OpenMapFileAction extends DefaultContextOpenAction
{

	protected transient File openFile;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public OpenMapFileAction(MainFrameContainer mainFrame)
	{
		this(COMMAND_NAME, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public OpenMapFileAction(String name, MainFrameContainer mainFrame)
	{
		this(name, IMAGE_ICON, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public OpenMapFileAction(String name, Icon icon, MainFrameContainer mainFrame)
	{
		super(name, icon, mainFrame);
		this.mainFrame = mainFrame;
		setAdditionalAttributes();
	}

	/**
	 * Return the real implementation of ContextClient class.
	 * @return the class object
	 */
	protected Class getContextClientClass()
	{
		return MappingMainPanel.class;
	}

	/**
	 * Return the real user input of the file to be opened.
	 *
	 * @return the file object
	 */
	protected File getFileFromUserInput()
	{
		return openFile;
	}

//	protected void launchPanel(final ContextManagerClient panel, final File file)
//	{
//		final CmpsMappingPanel mappingPanel  = (CmpsMappingPanel) panel;
//		//have to add the new tab so as the panel may update its panel title in the tabbed pane.
//		gov.nih.nci.cbiit.cmts.ui.util.SwingWorker worker = new gov.nih.nci.cbiit.cmts.ui.util.SwingWorker()
//		{
//			public Object construct()
//			{
//				try
//				{
//					GeneralUtilities.setCursorWaiting(mainFrame);
//					mainFrame.addNewTab(mappingPanel);
//					setSuccessfullyPerformed(true);
//				}
//				catch(Throwable t)
//				{
//					t.printStackTrace();
//					//Log.logException(getClass(), "May ignore and proceed", t);
//					setSuccessfullyPerformed(false);
//				}
//				finally
//				{
//					//back to normal, in case exception occurred.
//					GeneralUtilities.setCursorDefault(mainFrame);
//					return null;
//				}
//			}
//
//			public void finished()
//			{
//				if (!isSuccessfullyPerformed())
//				{//no need to proceed further
//					return;
//				}
//				//this variable will help determine whether or not to close the created panel in the event of validation errors or exceptions.
//				boolean everythingGood = true;
//				try
//				{
//					GeneralUtilities.setCursorWaiting(mainFrame);
//					mappingPanel.processOpenMapFile(file);
//				}
//				catch (Throwable e1)
//				{
//					e1.printStackTrace();
//					//log the exception, but not report
//					DefaultSettings.reportThrowableToLogAndUI(this, e1, "", mainFrame, false, true);
//					//report the nice to have message
//					everythingGood = false;
//				}
//				finally
//				{
//					//back to normal.
//					GeneralUtilities.setCursorDefault(mainFrame);
//					setSuccessfullyPerformed(everythingGood);
//
//					if (!everythingGood)
//					{//do the clean up.
////						Message msg = MessageResources.getMessage("GEN3", new Object[0]);
////						JOptionPane.showMessageDialog(mainFrame, msg.toString(), "Error", JOptionPane.ERROR_MESSAGE);
//						if (mappingPanel != null && mainFrame.hasComponentOfGivenClass(getContextClientClass(), false) != null)
//						{
//						//						mainFrame.getTabbedPane().remove(mappingPanel);
//						//use close action instead of removing it from tabbed directly so as to allow main frame to clean up maps.
////							gov.nih.nci.caadapter.ui.mapping.hl7.actions.CloseMapAction closeAction = new gov.nih.nci.caadapter.ui.mapping.hl7.actions.CloseMapAction(mappingPanel);
////							closeAction.actionPerformed(actionEvent);
//						}
//					}
//				}
//			}
//		};
//		worker.start();
//	}
	/**
	 * Launch the context manager client to UI.
	 *
	 * @param panel
	 * @param file
	 */
	protected void launchPanel(final ContextManagerClient panel, final File file)
	{
		final MappingMainPanel mappingPanel  = (MappingMainPanel) panel;
		//have to add the new tab so as the panel may update its panel title in the tabbed pane.
		SwingWorker worker = new SwingWorker()
		{
			public Object doInBackground() throws Exception
			{
                //System.out.println("SwingWorker. doInBackground() : 1");
                try
				{

                    GeneralUtilities.setCursorWaiting(mainFrame.getAssociatedUIContainer());
					mainFrame.addNewTab(mappingPanel, ".map");
                    //System.out.println("SwingWorker. doInBackground() 2 : " + mainFrame.getTabbedPane().getTitleAt(mainFrame.getTabbedPane().getComponentCount()-1));
                    setSuccessfullyPerformed(true);
				}
				catch(Throwable t)
				{
                    System.out.println("SwingWorker. doInBackground() : 3");
                    t.printStackTrace();
					//Log.logException(getClass(), "May ignore and proceed", t);
					setSuccessfullyPerformed(false);
				}
				finally
				{

                    //back to normal, in case exception occurred.
					GeneralUtilities.setCursorDefault(mainFrame.getAssociatedUIContainer());
                    //System.out.println("SwingWorker. doInBackground() 4 : " + mainFrame.getTabbedPane().getTitleAt(mainFrame.getTabbedPane().getComponentCount()-1));

                    return null;
				}
			}

			protected void done() {

                //System.out.println("SwingWorker.done() 1 : " + mainFrame.getTabbedPane().getTitleAt(mainFrame.getTabbedPane().getComponentCount()-1));

                if (!isSuccessfullyPerformed())
				{//no need to proceed further
					return ;
				}
				//this variable will help determine whether or not to close the created panel in the event of validation errors or exceptions.
				boolean everythingGood = true;
				try
				{

                    GeneralUtilities.setCursorWaiting(mainFrame.getAssociatedUIContainer());
                    //System.out.println("SwingWorker.done() 2 : " + mainFrame.getTabbedPane().getTitleAt(mainFrame.getTabbedPane().getComponentCount()-1));

                    mappingPanel.processOpenMapFile(file);
                    //System.out.println("SwingWorker.done() 3 : " + mainFrame.getTabbedPane().getTitleAt(mainFrame.getTabbedPane().getComponentCount()-1));

                }
                catch (Throwable e1)
				{
                    String message = e1.getMessage();
                    if (message.startsWith(ActionConstants.MESSAGE_NOT_A_MAPPING_FILE))
                    {
                        JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "Sorry! This file cannot be opened : " + file.getName() +"\nFile Opening is Canceled.",
                                ActionConstants.MESSAGE_NOT_A_MAPPING_FILE, JOptionPane.WARNING_MESSAGE);
                    }
                    else DefaultSettings.reportThrowableToLogAndUI(this, e1, "", mainFrame.getAssociatedUIComponent(), false, false);
					mainFrame.closeTab();
                    //report the nice to have message
					everythingGood = false;
				}
				finally
				{

                    //back to normal.
					GeneralUtilities.setCursorDefault(mainFrame.getAssociatedUIContainer());
                    //System.out.println("SwingWorker.done() 5 : " + mainFrame.getTabbedPane().getTitleAt(mainFrame.getTabbedPane().getComponentCount()-1));

                    setSuccessfullyPerformed(everythingGood);

					if (!everythingGood)
					{//do the clean up.
						if (mappingPanel != null && mainFrame.hasComponentOfGivenClass(getContextClientClass(), false) != null)
						{
						//						mainFrame.getTabbedPane().remove(mappingPanel);
						//use close action instead of removing it from tabbed directly so as to allow main frame to clean up maps.
//							gov.nih.nci.caadapter.ui.mapping.hl7.actions.CloseMapAction closeAction = new gov.nih.nci.caadapter.ui.mapping.hl7.actions.CloseMapAction(mappingPanel);
//							closeAction.actionPerformed(actionEvent);
						}
					}
				}
				return ;
			}
		};
		worker.execute();
	}

	/**
	 * Invoked when an action occurs.
	 */
	protected boolean doAction(ActionEvent e)
	{
        File file = null;
        while(true)
        {
            file = DefaultSettings.getUserInputOfFileFromGUI(mainFrame.getAssociatedUIComponent(), //getUIWorkingDirectoryPath(),
			    	DefaultSettings.MAP_FILE_DEFAULT_EXTENTION, ActionConstants.OPEN_MAP_FILE, false, false);
            if (file == null)
            {
                JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "Opening File is Canceled.",
                        "Cancel Open File", JOptionPane.INFORMATION_MESSAGE);
                return isSuccessfullyPerformed();
            }

            if (file.getName().toLowerCase().endsWith(".map")) break;
            else
            {
                int ans = JOptionPane.showConfirmDialog(mainFrame.getAssociatedUIComponent(), "The File type of your select is not a '.map' file. : " + file.getName() + "\nDo you want to go on, anyway?",
                                              "Not Mapping File", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (ans == JOptionPane.YES_OPTION) break;
                else
                {
                    JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "Opening File is Canceled.",
                        "Cancel Open File", JOptionPane.INFORMATION_MESSAGE);
                    return isSuccessfullyPerformed();
                }
            }
        }
        if (file != null)
		{
			openFile = file;
			MappingMainPanel panel;
			try {
				panel = new MappingMainPanel(mainFrame);
			
				launchPanel(panel, file);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return isSuccessfullyPerformed();
	}

	@Override
	protected Component getAssociatedUIComponent() {
		return mainFrame.getAssociatedUIComponent();
	}

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2008/12/29 22:18:18  linc
 * HISTORY      : function UI added.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2008/12/09 19:04:17  linc
 * HISTORY      : First GUI release
 * HISTORY      :
 * HISTORY      : Revision 1.1  2008/12/03 20:46:14  linc
 * HISTORY      : UI update.
 * HISTORY      :
 */
