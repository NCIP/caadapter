/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.main;


import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.function.FunctionUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.CloseAllAction;
import gov.nih.nci.caadapter.ui.common.context.ContextManagerClient;
import gov.nih.nci.caadapter.ui.common.context.ContextManager;
import gov.nih.nci.caadapter.ui.help.HelpContentViewer;
import gov.nih.nci.caadapter.ui.help.InitialSplashWindow;
import gov.nih.nci.caadapter.ui.hl7message.HL7MessagePanel;
import gov.nih.nci.caadapter.ui.mapping.hl7.HL7MappingPanel;
import gov.nih.nci.caadapter.ui.mapping.sdtm.Database2SDTMMappingPanel;
import gov.nih.nci.caadapter.ui.specification.csv.CSVPanel;
import gov.nih.nci.caadapter.ui.specification.hsm.HSMPanel;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.preferences.CaWindowClosingListener;
import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import javax.swing.*;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;

 /**
 * This class is the main entry of this sdk application.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: linc $
 * @since caAdapter v1.2
 * @version    $Revision: 1.19 $
 * @date       $Date: 2008-09-26 20:35:27 $
 */
public class MainFrame extends AbstractMainFrame
{
    private JTabbedPane tabbedPane = new JTabbedPane();

    private int tabcount = 0;

    private JPanel statusBar = new JPanel();

    private JPanel toolBarPanel;

    private JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING)); // inserted by Kisung for handling the default screen and the tabbedPane on 09/20/05.

    private JPanel currentToolBarPanel;

    private java.util.Map<Class, JComponent> tabMap;

    private JLabel baseScreenJLabel; // inserted by Kisung 09/13/05   The default screen component object

    private HelpContentViewer helpContentViewer;

    /* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.main.AbstractMainFrame#launch()
	 */
    public void launch()
    {
		tabMap = new HashMap<Class, JComponent>();
		ContextManager contextManager = ContextManager.getContextManager();

		MainMenuBar frameMenu=new MainMenuBar(this);
		contextManager.setMenu(frameMenu);
		contextManager.setToolBarHandler(new MainToolBarHandler());
		contextManager.initContextManager(this);
		if(CaadapterUtil.findApplicationConfigValue("caadapter.product.name")!=null)
			this.setTitle(CaadapterUtil.findApplicationConfigValue("caadapter.product.name"));
		else
			this.setTitle(Config.PRODUCT_NAME);
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		//set the icon.
		Image icon = DefaultSettings.getImage("caAdapter-icon.gif");//using default image file
		setIconImage(icon);
		// set the menu bar.
		setJMenuBar(frameMenu);
		//set size before constructing each of those panels since some of them
		//may depend on the size to align components.
		this.setSize(Config.FRAME_DEFAULT_WIDTH, Config.FRAME_DEFAULT_HEIGHT);
		contentPane.add(constructNorthPanel(), BorderLayout.NORTH);
		contentPane.add(constructCenterPanel(), BorderLayout.CENTER); // inserted by Kisung on 09/20/05
		//--------------------------------------
		contentPane.add(statusBar, BorderLayout.SOUTH);
		tabbedPane.addChangeListener(contextManager);
		tabbedPane.setOpaque(false);
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        this.addWindowListener(new CaWindowClosingListener());
		this.setVisible(true);
	    DefaultSettings.centerWindow(this);
	    this.setFocusable(true);
		this.setFocusableWindowState(true);
		//helpContentViewer = new HelpContentViewer(this);

		InitialSplashWindow isw = new InitialSplashWindow();
		//isw.setAlwaysOnTop(true);
		DefaultSettings.centerWindow(isw);
		isw.setVisible(true);
		ArrayList missingList=CaadapterUtil.getModuleResourceMissed("");
		if (!missingList.isEmpty())
		{
			String warningMsg=VerifyResourceDialog.setWarningContext(missingList, VerifyResourceDialog.DEFAULT_CONTEXT_FILE_PATH);
			JOptionPane.showMessageDialog(this, warningMsg, "Warning: Resources Missing ", JOptionPane.DEFAULT_OPTION);
		}
//			new VerifyResourceDialog(this, "Warning: Resources Missing ", missingList);

	    try {
		    Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		isw.dispose();
    }

    private JPanel constructNorthPanel() {
    	Image bannerImage = DefaultSettings.getImage("NCICBIITBanner.jpg");
	    ImageIcon imageIcon = new ImageIcon(bannerImage);
		toolBarPanel = new JPanel(new BorderLayout());
		JPanel northUpperPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JLabel label = new JLabel(imageIcon);
		northUpperPanel.add(label);
		toolBarPanel.add(northUpperPanel, BorderLayout.NORTH);
		currentToolBarPanel = constructToolbarPanel();
		//updateToolBar(getMainContextManager().getToolbar());
		toolBarPanel.add(currentToolBarPanel, BorderLayout.SOUTH);
		return toolBarPanel;
    }

    private JPanel constructToolbarPanel()
    {
        JPanel mainP = new JPanel(new BorderLayout());
        mainP.add(ContextManager.getContextManager().getToolBarHandler().getToolBar(), BorderLayout.CENTER);
        mainP.add(new JPanel(), BorderLayout.EAST);
        return mainP;
    }

    //This method was inserted by Kisung 09/20/05 For initial displaying of the default screen object before the JTabbedPane object is activated.
    private JPanel constructCenterPanel() {
	ImageIcon ii1 = new ImageIcon(DefaultSettings.getImage(Config.DEFAULT_SCREEN_IMAGE_FILENAME));
//FileUtil.getWorkingDirPath() + File.separator + "images" + File.separator + Config.DEFAULT_SCREEN_IMAGE_FILENAME);
	baseScreenJLabel = new JLabel(ii1);
	ii1.setImageObserver(baseScreenJLabel);
	centerPanel.add(baseScreenJLabel);
	centerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
	centerPanel.setOpaque(false);
	return centerPanel;
    }

   public void updateToolBar(JToolBar newToolBar) {
	   updateToolBar(newToolBar, null);
    }

    public void updateToolBar(JToolBar newToolBar, JButton rightSideButton) {
	//remove first in case it already contains
		JToolBar rightSideToolbar = new JToolBar();
		JPanel rightSidePanel = new JPanel(new BorderLayout());
		if (rightSideButton != null)
		{
			rightSideToolbar.add(rightSideButton);
		    rightSidePanel.add(rightSideToolbar, BorderLayout.CENTER);
		}
		toolBarPanel.remove(currentToolBarPanel);
		currentToolBarPanel.removeAll();
		currentToolBarPanel.add(newToolBar, BorderLayout.CENTER);
		currentToolBarPanel.add(rightSidePanel, BorderLayout.EAST);
		toolBarPanel.add(currentToolBarPanel, BorderLayout.SOUTH);
    }

    /* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.main.AbstractMainFrame#addNewTab(javax.swing.JPanel)
	 */
    public void addNewTab(JPanel panel) {
	//Follwing 5 lines are inserted by Kisung 09/20/05 For remove default screen object before the JTabbedPane object is activated.
	if (tabbedPane.getTabCount() == 0) {
	    centerPanel.removeAll();
	    centerPanel.setLayout(new BorderLayout());
	    centerPanel.add(tabbedPane, BorderLayout.CENTER);
	}
	//-----------------------------------------------------------------------------------------
	String title = null;

	if (panel instanceof CSVPanel) {
	    title = "Untitled_" + (++tabcount) + Config.CSV_METADATA_FILE_DEFAULT_EXTENTION;
	} else if (panel instanceof HL7MappingPanel) {
	    title = "Untitled_" + (++tabcount) + Config.MAP_FILE_DEFAULT_EXTENTION;
	} else if (panel instanceof HSMPanel) {
	    title = "Untitled_" + (++tabcount) + Config.HSM_META_DEFINITION_FILE_DEFAULT_EXTENSION;
	} else if (panel instanceof HL7MessagePanel) {
	    title = panel.getName();//"HL7 v3 Message";
	} else if (panel instanceof Database2SDTMMappingPanel) {
        title = "Untitled_" + (++tabcount) + Config.MAP_FILE_DEFAULT_EXTENTION;
    }
	else
		title = "Untitled_" + (++tabcount) + Config.MAP_FILE_DEFAULT_EXTENTION;
	tabbedPane.addTab(title, panel);
	tabbedPane.setSelectedComponent(panel);
	//		Log.logInfo(this, "Panel Class: '" + (panel==null?"null":panel.getClass().getName()) + "'.");
	tabMap.put(panel.getClass(), panel);
    }

    /* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.main.AbstractMainFrame#closeTab()
	 */
    public void closeTab() {
	Component comp = tabbedPane.getSelectedComponent();
	tabbedPane.remove(comp);
	if (tabbedPane.getTabCount() == 0) {//reset if not tab at all.
	    tabcount = 0;
	    //Follwing 4 lines are inserted by Kisung 09/20/05 For remove empty JTabbedPane object and display default screen.
	    centerPanel.removeAll();
	    centerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
	    centerPanel.add(baseScreenJLabel);
	    centerPanel.update(centerPanel.getGraphics());
	    //-------------------------------------------------------------------------------------------------
	}

    if( comp != null ){
        tabMap.remove(comp.getClass());

        if (comp instanceof ContextManagerClient) {
            ContextManager.getContextManager().getContextFileManager().removeFileUsageListener((ContextManagerClient) comp);
        }
    }
    }
    /**
     * Only accessible by the same package so as to
     * avoid abuse of using the tabbedPane directly.
     * @return the tab pane.
     */
    public JTabbedPane getTabbedPane() {
	return tabbedPane;
    }

    /* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.main.AbstractMainFrame#hasComponentOfGivenClass(java.lang.Class, boolean)
	 */
    public JComponent hasComponentOfGivenClass(Class classValue, boolean bringToFront) {
	JComponent component = tabMap.get(classValue);
	if (component != null) {
	    if (bringToFront) {
		try {
		    tabbedPane.setSelectedComponent(component);
		} catch (Throwable e) {
		    Log.logInfo(this, "What kind of Component is this: '" + component.getClass().getName() + "'.");
		    Log.logException(this, e);
		}
	    }
	    return component;
	} else {
	    return null;
	}
    }

    /* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.main.AbstractMainFrame#getAllTabs()
	 */
    public java.util.List<Component> getAllTabs() {
	java.util.List<Component> resultList = new java.util.ArrayList<Component>();
	int count = tabbedPane.getComponentCount();
	for (int i = 0; i < count; i++) {
	    Component comp = tabbedPane.getComponentAt(i);
	    resultList.add(comp);
	}
	return resultList;
    }

    /* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.main.AbstractMainFrame#setCurrentPanelTitle(java.lang.String)
	 */
    public boolean setCurrentPanelTitle(String newTitle) {
	int seleIndex = tabbedPane.getSelectedIndex();
	if (seleIndex != -1) {
	    tabbedPane.setTitleAt(seleIndex, newTitle);
	    return true;
	} else {
	    return false;
	}
    }

    public static void main(String[] args)
    {
        //Preferences.loadDefaults();
        try
        {
	        try
            {
		        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	        }
            catch (ClassNotFoundException e1)
            {
		        // TODO Auto-generated catch block
		        e1.printStackTrace();
	        }
            catch (InstantiationException e1)
            {
		        // TODO Auto-generated catch block
		        e1.printStackTrace();
	        }
            catch (IllegalAccessException e1)
            {
		        // TODO Auto-generated catch block
		        e1.printStackTrace();
	        }
            catch (UnsupportedLookAndFeelException e1)
            {
		        // TODO Auto-generated catch block
		        e1.printStackTrace();
	        }
            new MainFrame().launch();
	    }
        catch (Throwable t)
        {
	        Log.logException(new Object(), t);
	    }
    }

    /* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.main.AbstractMainFrame#showHelpContentViewer()
	 */
    public void showHelpContentViewer() // inserted by umkis on 01/16/2006, for reduce uploading time for help content.
    {
    	if (helpContentViewer==null)
    	{
    		System.out.println("MainFrame.showHelpContentViewer()..the help window has not been intialized yet !!");
    		return;
    	}
	    DefaultSettings.centerWindow(helpContentViewer);
	    helpContentViewer.setVisible(true);
    }

    /* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.main.AbstractMainFrame#showHelpContentWithNodeID(java.lang.String)
	 */
    public void showHelpContentWithNodeID(String id) // inserted by umkis on 01/16/2006, for reduce uploading time for help content.
    {
	    DefaultSettings.centerWindow(helpContentViewer);
	    helpContentViewer.linkNodeID(id);
	    helpContentViewer.setVisible(true);
    }

    /* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.main.AbstractMainFrame#showHelpContentWithNodeID(java.lang.String, java.awt.Dialog)
	 */
    public void showHelpContentWithNodeID(String id, Dialog dispose) // inserted by umkis on 01/16/2006, for reduce uploading time for help content.
    {
	    if (dispose != null)
	        dispose.dispose();
	    showHelpContentWithNodeID(id);
    }

    /* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.main.AbstractMainFrame#resetCenterPanel()
	 */
    public void resetCenterPanel() // inserted by umkis on 01/18/2006, defaect# 252
    {
	    centerPanel.updateUI();
    }


    /* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.main.AbstractMainFrame#processWindowEvent(java.awt.event.WindowEvent)
	 */
    public void processWindowEvent(WindowEvent e) {
	//		Log.logInfo(this, "processWindowEvent() invoked with '" + e + "'.");
	if (e.getID() == WindowEvent.WINDOW_CLOSING) {
		this.getMenuBar();
		MainMenuBar frameMenuBar=(MainMenuBar)ContextManager.getContextManager().getMenu();
		CloseAllAction closeAllAction=(CloseAllAction)frameMenuBar.getDefinedAction(ActionConstants.CLOSE_ALL);//.closeAllAction;
//	    CloseAllAction closeAllAction =ContextManager.getContextManager().getMenu().closeAllAction;
	    if (closeAllAction != null && closeAllAction.isEnabled()) {
		closeAllAction.actionPerformed(null);
		if (closeAllAction.isSuccessfullyPerformed()) {
		    super.processWindowEvent(e);
		} else {//back to normal process.
		    return;
		}
	    } else {
		super.processWindowEvent(e);
	    }
	    exit();
	} else {
	    super.processWindowEvent(e);
	}
    }

    /* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.main.AbstractMainFrame#exit()
	 */
    public void exit() {
    	ContextManager contextManager = ContextManager.getContextManager();
		if (contextManager.isItOKtoShutdown()) {
		    this.exit(0);
		}
    }

    protected void exit(int errorLevel) {
	this.setVisible(false);
	this.dispose();
	FunctionUtil.deleteTemporaryFiles();   //Revision 1.10  2007/09/07 15:19:26  umkis
	Log.logInfo(this, "\r\n\r\nShutting down logging with exit code = " + errorLevel + "\r\n\r\n" + "===============================================================\r\n" + "===============================================================\r\n");
	System.exit(errorLevel);
    }
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.18  2008/07/10 15:51:00  linc
 * HISTORY      : Ready for MMS 4.1 releases.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2008/06/09 19:53:53  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.16  2007/12/14 17:03:33  schroedn
 * HISTORY      : added null check
 * HISTORY      :
 * HISTORY      : Revision 1.15  2007/11/29 14:26:42  wangeug
 * HISTORY      : create CSV_TO_XMI mapping module
 * HISTORY      :
 * HISTORY      : Revision 1.14  2007/10/04 18:09:14  wangeug
 * HISTORY      : verify resource based on module
 * HISTORY      :
 * HISTORY      : Revision 1.13  2007/09/19 16:43:56  wangeug
 * HISTORY      : show missing resources
 * HISTORY      :
 * HISTORY      : Revision 1.12  2007/09/10 16:46:57  wangeug
 * HISTORY      : set name for hlv3 panel
 * HISTORY      :
 * HISTORY      : Revision 1.11  2007/09/08 20:19:12  umkis
 * HISTORY      : Temporary files will be automatically deleted when system exit.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2007/09/07 15:19:26  umkis
 * HISTORY      : Temporary files will be automatically deleted when system exit.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2007/09/06 17:10:49  wangeug
 * HISTORY      : check null to view help window
 * HISTORY      :
 * HISTORY      : Revision 1.8  2007/08/01 17:20:05  jayannah
 * HISTORY      : added untitled name to the panel name
 * HISTORY      :
 * HISTORY      : Revision 1.7  2007/07/27 20:42:22  wangeug
 * HISTORY      : clean codes
 * HISTORY      :
 * HISTORY      : Revision 1.6  2007/07/27 19:44:46  jayannah
 * HISTORY      : changes to display tab title when creating new sdtm map
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/07/14 20:28:27  umkis
 * HISTORY      : reactivate HelpContentViewer for avoid from NullPointerException
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/05/10 15:23:04  jayannah
 * HISTORY      : commented out the preferences
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/05/09 21:06:31  jayannah
 * HISTORY      : *** empty log message ***
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:36  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.72  2007/01/08 21:20:49  umkis
 * HISTORY      : some lines set up indentation
 * HISTORY      :
 * HISTORY      : Revision 1.71  2007/01/08 21:02:03  umkis
 * HISTORY      : some lines set up indentation
 * HISTORY      :
 * HISTORY      : Revision 1.70  2006/12/21 17:11:45  jayannah
 * HISTORY      : removed unused imports
 * HISTORY      :
 * HISTORY      : Revision 1.69  2006/12/21 15:25:27  jayannah
 * HISTORY      : Made changes to bring the mainframe to focus immediately
 * HISTORY      :
 * HISTORY      : Revision 1.68  2006/12/06 18:04:23  wuye
 * HISTORY      : Change "caAdapter Mapping Tool" to "caAdapter"
 * HISTORY      :
 * HISTORY      : Revision 1.67  2006/09/18 18:33:47  umkis
 * HISTORY      : execute FileUtil.deleteTemporaryFiles() before exit(0).
 * HISTORY      :
 * HISTORY      : Revision 1.66  2006/08/10 19:11:43  umkis
 * HISTORY      : Delete the inserted codes of revision 1.65. Go back the stat of revision 1.64.
 * HISTORY      :
 * HISTORY      : Revision 1.65  2006/08/09 22:48:52  umkis
 * HISTORY      : Just before closing mainFrame, all temporary files is deleted.
 * HISTORY      :
 * HISTORY      : Revision 1.64  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.63  2006/06/13 18:12:12  jiangsc
 * HISTORY      : Upgraded to catch Throwable instead of Exception.
 * HISTORY      :
 * HISTORY      : Revision 1.62  2006/01/23 21:32:41  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.61  2006/01/20 21:36:38  umkis
 * HISTORY      : add Extending caAdapter to include new HL7 message types (6.1.3)
 * HISTORY      :
 * HISTORY      : Revision 1.60  2006/01/19 00:45:46  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.59  2006/01/18 19:49:52  umkis
 * HISTORY      : defaect# 252, resetCenterPanel function is inserted
 * HISTORY      :
 * HISTORY      : Revision 1.58  2006/01/17 19:37:48  umkis
 * HISTORY      : HelpContentViewer is Locate in mainFrame
 * HISTORY      :
 * HISTORY      : Revision 1.57  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.56  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.55  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.54  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.53  2005/12/14 21:37:19  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.52  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.51  2005/11/18 07:24:36  umkis
 * HISTORY      : replace close button to right side of toolbar panel
 * HISTORY      :
 * HISTORY      : Revision 1.50  2005/11/15 19:39:47  jiangsc
 * HISTORY      : Changed HL7 to caAdapter
 * HISTORY      :
 * HISTORY      : Revision 1.49  2005/11/04 19:54:26  chene
 * HISTORY      : change function library file extention from ffs to fls
 * HISTORY      :
 * HISTORY      : Revision 1.48  2005/11/02 00:12:52  umkis
 * HISTORY      : The default screen image filename is refered to Config class
 * HISTORY      :
 * HISTORY      : Revision 1.47  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.46  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.45  2005/10/17 22:39:02  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.44  2005/09/28 20:55:08  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.43  2005/09/21 01:10:11  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.42  2005/09/15 16:01:39  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.41  2005/09/14 19:56:27  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.40  2005/09/14 15:21:46  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.39  2005/09/13 22:24:00  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.38  2005/09/13 14:37:15  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.37  2005/09/13 00:52:35  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.36  2005/09/12 22:00:22  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.35  2005/09/12 16:15:56  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.34  2005/09/09 13:18:37  umkis
 * HISTORY      : Help Content Viewer
 * HISTORY      :
 * HISTORY      : Revision 1.33  2005/08/24 22:25:08  jiangsc
 * HISTORY      : Enhanced Toolbar navigation and creation so as to work around an AWT ArrayOutofBoundException.
 * HISTORY      :
 * HISTORY      : Revision 1.32  2005/08/19 18:54:05  jiangsc
 * HISTORY      : Enhanced exit on ask saving
 * HISTORY      :
 * HISTORY      : Revision 1.31  2005/08/18 21:04:39  jiangsc
 * HISTORY      : Save point of the synchronization effort.
 * HISTORY      :
 * HISTORY      : Revision 1.30  2005/08/18 15:30:19  jiangsc
 * HISTORY      : First implementation on Switch control.
 * HISTORY      :
 * HISTORY      : Revision 1.29  2005/08/04 22:22:28  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */
