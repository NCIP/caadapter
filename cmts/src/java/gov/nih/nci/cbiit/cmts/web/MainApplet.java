package gov.nih.nci.cbiit.cmts.web;

import gov.nih.nci.cbiit.cmts.ui.common.ContextManager;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.main.MainMenuBar;
import gov.nih.nci.cbiit.cmts.ui.mapping.MainToolBarHandler;


import java.awt.AWTEvent;
import java.awt.BorderLayout;

import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;


public class MainApplet extends JApplet {
	private JTabbedPane tabbedPane = new JTabbedPane();
	private JPanel toolBarPanel;
	private JPanel centerPanel;
	private JPanel currentToolBarPanel;
	private Map<Class, JComponent> tabMap;
	
public MainApplet() throws HeadlessException 
{
		tabMap = new HashMap<Class, JComponent>();
		ContextManager contextManager = ContextManager.getContextManager();

		MainMenuBar frameMenu=new MainMenuBar(null);//this);
		contextManager.setMenu(frameMenu);
		contextManager.setToolBarHandler(new MainToolBarHandler());
//		contextManager.initContextManager(this);
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
}
