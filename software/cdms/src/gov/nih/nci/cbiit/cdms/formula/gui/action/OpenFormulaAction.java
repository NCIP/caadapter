package gov.nih.nci.cbiit.cdms.formula.gui.action;


import gov.nih.nci.cbiit.cdms.formula.gui.FormulaMainPanel;
import gov.nih.nci.cbiit.cdms.formula.gui.constants.ActionConstants;
import gov.nih.nci.cbiit.cdms.formula.common.util.DefaultSettings;

import javax.swing.*;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 16, 2010
 * Time: 12:53:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class OpenFormulaAction extends AbstractAction
{

    //Icon icon = null;
    private File openFile;
    private FormulaMainPanel mainPanel;
    String name = "";
    boolean isSuccessfullyDone = false;
    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    public OpenFormulaAction(FormulaMainPanel mainPanel)
    {
        this("Open", mainPanel);
    }

//    /**
//     * Defines an <code>Action</code> object with the specified
//     * description string and a default icon.
//     */
//    public OpenFormulaAction(String name,  FormulaMainPanel mainPanel)
//    {
//        this(name, IMAGE_ICON, mainPanel);
//    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public OpenFormulaAction(String name, FormulaMainPanel mainPanel)
    {
        //super(name, icon, mainPanel);
        this.mainPanel = mainPanel;
        this.name = name;
        //setAdditionalAttributes();
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
//					GeneralUtilities.setCursorWaiting(mainPanel);
//					mainPanel.addNewTab(mappingPanel);
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
//					GeneralUtilities.setCursorDefault(mainPanel);
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
//					GeneralUtilities.setCursorWaiting(mainPanel);
//					mappingPanel.processOpenMapFile(file);
//				}
//				catch (Throwable e1)
//				{
//					e1.printStackTrace();
//					//log the exception, but not report
//					DefaultSettings.reportThrowableToLogAndUI(this, e1, "", mainPanel, false, true);
//					//report the nice to have message
//					everythingGood = false;
//				}
//				finally
//				{
//					//back to normal.
//					GeneralUtilities.setCursorDefault(mainPanel);
//					setSuccessfullyPerformed(everythingGood);
//
//					if (!everythingGood)
//					{//do the clean up.
////						Message msg = MessageResources.getMessage("GEN3", new Object[0]);
////						JOptionPane.showMessageDialog(mainPanel, msg.toString(), "Error", JOptionPane.ERROR_MESSAGE);
//						if (mappingPanel != null && mainPanel.hasComponentOfGivenClass(getContextClientClass(), false) != null)
//						{
//						//						mainPanel.getTabbedPane().remove(mappingPanel);
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
//    protected void launchPanel(final ContextManagerClient panel, final File file)
//    {
//        final MappingMainPanel mappingPanel  = (MappingMainPanel) panel;
//        //have to add the new tab so as the panel may update its panel title in the tabbed pane.
//        SwingWorker worker = new SwingWorker()
//        {
//            public Object doInBackground() throws Exception
//            {
//                try
//                {
//                    GeneralUtilities.setCursorWaiting(mainPanel);
//                    mainPanel.addNewTab(mappingPanel);
//                    setSuccessfullyPerformed(true);
//                }
//                catch(Throwable t)
//                {
//                    t.printStackTrace();
//                    //Log.logException(getClass(), "May ignore and proceed", t);
//                    setSuccessfullyPerformed(false);
//                }
//                finally
//                {
//                    //back to normal, in case exception occurred.
//                    GeneralUtilities.setCursorDefault(mainPanel);
//                    return null;
//                }
//            }
//
//            protected void done() {
//                if (!isSuccessfullyPerformed())
//                {//no need to proceed further
//                    return ;
//                }
//                //this variable will help determine whether or not to close the created panel in the event of validation errors or exceptions.
//                boolean everythingGood = true;
//                try
//                {
//                    GeneralUtilities.setCursorWaiting(mainPanel);
//                    mappingPanel.processOpenMapFile(file);
//                }
//                catch (Throwable e1)
//                {
//                    //log the exception, but not report
//                    DefaultSettings.reportThrowableToLogAndUI(this, e1, "", mainPanel, false, true);
//                    //report the nice to have message
//                    everythingGood = false;
//                }
//                finally
//                {
//                    //back to normal.
//                    GeneralUtilities.setCursorDefault(mainPanel);
//                    setSuccessfullyPerformed(everythingGood);
//
//                    if (!everythingGood)
//                    {//do the clean up.
//                        if (mappingPanel != null && mainPanel.hasComponentOfGivenClass(getContextClientClass(), false) != null)
//                        {
//                        //						mainPanel.getTabbedPane().remove(mappingPanel);
//                        //use close action instead of removing it from tabbed directly so as to allow main frame to clean up maps.
////							gov.nih.nci.caadapter.ui.mapping.hl7.actions.CloseMapAction closeAction = new gov.nih.nci.caadapter.ui.mapping.hl7.actions.CloseMapAction(mappingPanel);
////							closeAction.actionPerformed(actionEvent);
//                        }
//                    }
//                }
//                return ;
//            }
//        };
//        worker.execute();
//    }
//
    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        File file = DefaultSettings.getUserInputOfFileFromGUI(mainPanel, //getUIWorkingDirectoryPath(),
                ActionConstants.FORMULA_FILE_EXTENSION, "Open Transformation Mapping", false, false);
        if (file != null)
        {


            try {

//               isSuccessfullyDone = mainPanel.openFile(file);
               openFile = file;
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        //return isSuccessfullyPerformed();
    }
//
//    @Override
//    protected Component getAssociatedUIComponent() {
//        return mainPanel;
//    }
    public boolean isSuccessfullyPerformed()
    {
        return isSuccessfullyDone;
    }
}
