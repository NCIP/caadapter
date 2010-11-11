package gov.nih.nci.cbiit.cmts.formula;

import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.context.ContextManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 10, 2010
 * Time: 4:47:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormulaMain extends JFrame implements ActionListener
{
    private JTextField formulaField;
    private JTextArea formulaXML;

    private JMenuItem newMenu;
    private JMenuItem exitMenu;
    private static FormulaMain instance = null;

    private FormulaMain(){
        super();
    }

    /**
     * @return the instance
     */
    public static FormulaMain getInstance() {
        if(instance == null) instance = new FormulaMain();
        return instance;
    }

    /* (non-Javadoc)
      * @see gov.nih.nci.caadapter.ui.main.AbstractMainFrame#launch()
      */
    private void launch()
    {

        try {

            //ContextManager contextManager = ContextManager.getContextManager();

            FormulaMenuBar frameMenu=new FormulaMenuBar(this);
            //contextManager.setMenu(frameMenu);
            //contextManager.setToolBarHandler(new MainToolBarHandler());
            //contextManager.initContextManager(this);
            this.setTitle("Formula Generator");
            Container contentPane = this.getContentPane();
            contentPane.setLayout(new BorderLayout());
            //set the icon.
            //Image icon = DefaultSettings.getMainframeImage();
            //setIconImage(icon);
            // set the menu bar.
            setJMenuBar(frameMenu);
            newMenu = frameMenu.getNewMenuItem();
            newMenu.addActionListener(this);
            exitMenu = frameMenu.getExitMenuItem();
            exitMenu.addActionListener(this);
            //set size before constructing each of those panels since some of them
            //may depend on the size to align components.
            this.setSize(500, 400);
            JPanel panelNorth = new JPanel(new BorderLayout());
            formulaField = new JTextField();
            panelNorth.add(formulaField, BorderLayout.CENTER);
            panelNorth.add(new JLabel("   Java Expression  "), BorderLayout.WEST);
            panelNorth.add(new JLabel("    "), BorderLayout.NORTH);
            panelNorth.add(new JLabel("    "), BorderLayout.EAST);
            panelNorth.add(new JLabel("    "), BorderLayout.SOUTH);
            contentPane.add(panelNorth, BorderLayout.NORTH);
            JPanel panelCenter = new JPanel(new BorderLayout());
            formulaXML = new JTextArea();
            panelCenter.add(formulaXML, BorderLayout.CENTER);
            panelCenter.add(new JLabel("         XML Content  "), BorderLayout.WEST);
            panelCenter.add(new JLabel("     "), BorderLayout.EAST);
            contentPane.add(panelCenter, BorderLayout.CENTER);
            //--------------------------------------
            JPanel statusBar = new JPanel();
            contentPane.add(statusBar, BorderLayout.SOUTH);
            //tabbedPane.addChangeListener(contextManager);
            //tabbedPane.setOpaque(false);
            enableEvents(AWTEvent.WINDOW_EVENT_MASK);
            //        this.addWindowListener(new CaWindowClosingListener());
            this.setVisible(true);
            DefaultSettings.centerWindow(this);
            this.setFocusable(true);
            this.setFocusableWindowState(true);


        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == newMenu)
        {
            NewFormulaWizard wizard = new NewFormulaWizard(this, this, "New Formula", true);
            wizard.setSize(350, 150);
            wizard.setVisible(true);

            wizard.setLocation((new Double(this.getLocation().getX())).intValue() + 20, (new Double(this.getLocation().getX())).intValue() + 20);
        }
        if (e.getSource() == exitMenu)
        {
            exit();
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
            FormulaMain.getInstance().launch();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            //	        Log.logException(new Object(), t);
        }
    }


    /* (non-Javadoc)
      * @see gov.nih.nci.caadapter.ui.main.AbstractMainFrame#processWindowEvent(java.awt.event.WindowEvent)
      */
    public void processWindowEvent(WindowEvent e) {
        //		Log.logInfo(this, "processWindowEvent() invoked with '" + e + "'.");
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            this.getMenuBar();
            //		MainMenuBar frameMenuBar=(MainMenuBar)ContextManager.getContextManager().getMenu();
            //		CloseAllAction closeAllAction=(CloseAllAction)frameMenuBar.getDefinedAction(ActionConstants.CLOSE_ALL);//.closeAllAction;
            //	    CloseAllAction closeAllAction =ContextManager.getContextManager().getMenu().closeAllAction;
            //	    if (closeAllAction != null && closeAllAction.isEnabled()) {
            //		closeAllAction.actionPerformed(null);
            //		if (closeAllAction.isSuccessfullyPerformed()) {
            //		    super.processWindowEvent(e);
            //		} else {//back to normal process.
            //		    return;
            //		}
            //	    } else {
            //		super.processWindowEvent(e);
            //	    }
            exit();
        } else {
            super.processWindowEvent(e);
        }
    }

    /* (non-Javadoc)
      * @see gov.nih.nci.caadapter.ui.main.AbstractMainFrame#exit()
      */
    public void exit() {
        //    	ContextManager contextManager = ContextManager.getContextManager();
        //		if (contextManager.isItOKtoShutdown()) {
        this.exit(0);
        //		}
    }

    protected void exit(int errorLevel) {
        this.setVisible(false);
        this.dispose();
        //	Log.logInfo(this, "\r\n\r\nShutting down logging with exit code = " + errorLevel + "\r\n\r\n" + "===============================================================\r\n" + "===============================================================\r\n");
        System.exit(errorLevel);
    }
}
