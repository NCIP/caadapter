package gov.nih.nci.cbiit.cmts.web;

import gov.nih.nci.cbiit.cmts.ui.common.ContextManager;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.common.ContextManagerClient;
import gov.nih.nci.cbiit.cmts.ui.main.MainMenuBar;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrameContainer;
import gov.nih.nci.cbiit.cmts.ui.mapping.MainToolBarHandler;
import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMainPanel;


import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;


public class MainApplet extends JApplet {
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JPanel toolBarPanel;
    private JPanel centerPanel;
    private JPanel currentToolBarPanel;
    private Map<Class, JComponent> tabMap;

    private static MainFrameContainer instanceContainer = null;

public MainApplet() throws HeadlessException
{
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

		}
		catch (Throwable t)
		{
			t.printStackTrace();
			//	        Log.logException(new Object(), t);
		}


        tabMap = new HashMap<Class, JComponent>();
        ContextManager contextManager = ContextManager.getContextManager();

        instanceContainer = new MainFrameContainer(this);

        MainMenuBar frameMenu=new MainMenuBar(instanceContainer);//this);
        contextManager.setMenu(frameMenu);
        contextManager.setToolBarHandler(new MainToolBarHandler());
		contextManager.initContextManager(instanceContainer);
//		this.setTitle("caAdapter Common Mapping and Transformation Service (CMTS)");
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        //set the icon.
        Image icon = DefaultSettings.getMainframeImage();
//		setIconImage(icon);		
//		// set the menu bar.
        setJMenuBar(frameMenu);
        //the size will be controlled by HTML page
        contentPane.add(constructNorthPanel(), BorderLayout.NORTH);
        centerPanel = (JPanel) constructCenterPanel();
        contentPane.add(centerPanel, BorderLayout.CENTER);
        //--------------------------------------
        JPanel statusBar = new JPanel();
        contentPane.add(statusBar, BorderLayout.SOUTH);
        tabbedPane.addChangeListener(contextManager);
        tabbedPane.setOpaque(false);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        this.setVisible(true);
        this.setFocusable(true);
}

    private Component constructCenterPanel() {
        JPanel rtnPanel=new JPanel();
        ImageIcon ii1 = new ImageIcon(DefaultSettings.getImage("default_scr.gif"));
        JLabel baseScreenJLabel = new JLabel(ii1);
        ii1.setImageObserver(baseScreenJLabel);
        rtnPanel.add(baseScreenJLabel);
        rtnPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        rtnPanel.setOpaque(false);
        return rtnPanel;
    }

    private JPanel constructNorthPanel() {
        Image bannerImage = DefaultSettings.getImage("NCICBBanner.jpg");
        ImageIcon imageIcon = new ImageIcon(bannerImage);
        toolBarPanel = new JPanel(new BorderLayout());
        JPanel northUpperPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel label = new JLabel(imageIcon);
        northUpperPanel.add(label);
        toolBarPanel.add(northUpperPanel, BorderLayout.NORTH);
        currentToolBarPanel = constructToolbarPanel();
        toolBarPanel.add(currentToolBarPanel, BorderLayout.SOUTH);
        return toolBarPanel;
    }
    private JPanel constructToolbarPanel()
    {
        JPanel rtnPanel = new JPanel(new BorderLayout());
        rtnPanel.add(ContextManager.getContextManager().getToolBarHandler().getToolBar(), BorderLayout.CENTER);
        rtnPanel.add(new JPanel(), BorderLayout.EAST);
        return rtnPanel;
    }


        public void closeTab() {
        Component comp = tabbedPane.getSelectedComponent();
        tabbedPane.remove(comp);
        if (tabbedPane.getTabCount() == 0) {//reset if not tab at all.
            centerPanel.removeAll();
            ImageIcon imgIcon = new ImageIcon(DefaultSettings.getImage("default_scr.gif"));
            JLabel baseScreenJLabel = new JLabel(imgIcon);
            imgIcon.setImageObserver(baseScreenJLabel);
            centerPanel.add(baseScreenJLabel);
            centerPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        }

        if( comp != null ){
            tabMap.remove(comp.getClass());
            if (comp instanceof ContextManagerClient) {
                ContextManager.getContextManager().getContextFileManager().removeFileUsageListener((ContextManagerClient) comp);
            }
        }
    }

    public void resetCenterPanel() // inserted by umkis on 01/18/2006, defaect# 252
    {
        centerPanel.updateUI();
    }

    public java.util.List<Component> getAllTabs()
    {
        java.util.List<Component> resultList = new java.util.ArrayList<Component>();
        int count = tabbedPane.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component comp = tabbedPane.getComponentAt(i);
            resultList.add(comp);
        }
        return resultList;
    }

    public JComponent hasComponentOfGivenClass(Class classValue, boolean bringToFront)
    {
        JComponent component = tabMap.get(classValue);
        if (component != null) {
            if (bringToFront) {
                try {
                    tabbedPane.setSelectedComponent(component);
                } catch (Throwable e) {
                    e.printStackTrace();
                    //		    Log.logInfo(this, "What kind of Component is this: '" + component.getClass().getName() + "'.");
                    //		    Log.logException(this, e);
                }
            }
            return component;
        } else {
            return null;
        }
    }

    public void addNewTab(JPanel panel)
    {
        if (tabbedPane.getTabCount() == 0) {
            centerPanel.removeAll();
            centerPanel.setLayout(new BorderLayout());
            centerPanel.add(tabbedPane, BorderLayout.CENTER);
        }
        String title = null;

        if (panel instanceof MappingMainPanel) {
            title = "Untitled_" + (tabbedPane.getTabCount()+1) + ".map";
        }
        else
            title = "Untitled_" + (tabbedPane.getTabCount()+1) + ".dat";
        tabbedPane.addTab(title, panel);
        tabbedPane.setSelectedComponent(panel);
        System.out.println("Panel Class: '" + (panel==null?"null":panel.getClass().getName()) + "'.");
        tabMap.put(panel.getClass(), panel);
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public boolean setCurrentPanelTitle(String newTitle) {
		int seleIndex = tabbedPane.getSelectedIndex();
		if (seleIndex != -1) {
			tabbedPane.setTitleAt(seleIndex, newTitle);
			return true;
		} else {
			return false;
		}
	}

    public void updateToolBar(JToolBar newToolBar) {
        updateToolBar(newToolBar, null);
    }

    public void updateToolBar(JToolBar newToolBar, JButton rightSideButton)
    {
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

    public void processWindowEvent(WindowEvent e, Window win) {
		//		Log.logInfo(this, "processWindowEvent() invoked with '" + e + "'.");
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			//his.getMenuBar();
            if (win != null) win.dispose();
            this.setVisible(false);
		    this.stop();
		//	Log.logInfo(this, "\r\n\r\nShutting down logging with exit code = " + errorLevel + "\r\n\r\n" + "===============================================================\r\n" + "===============================================================\r\n");
		    System.exit(0);
		}
	}
}
