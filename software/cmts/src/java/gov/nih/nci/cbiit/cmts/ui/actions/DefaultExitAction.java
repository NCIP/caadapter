/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.ui.actions;


import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrame;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrameContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import netscape.javascript.JSObject;
import netscape.javascript.JSException;

/**
 * This class defines the exit action.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2009-11-23 18:32:47 $
 */
public class DefaultExitAction extends AbstractContextAction
{
    private static final String COMMAND_NAME = "      " + ActionConstants.EXIT;
    private static final Character COMMAND_MNEMONIC = new Character('E');
    //hotkey//private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.ALT_MASK, false);

    private MainFrameContainer mainFrame;

    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    public DefaultExitAction(MainFrameContainer mainFrame)
    {
        this(COMMAND_NAME, mainFrame);
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a default icon.
     */
    public DefaultExitAction(String name, MainFrameContainer mainFrame)
    {
        this(name, (Icon) null, mainFrame);
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public DefaultExitAction(String name, Icon icon, MainFrameContainer mainFrame)
    {
        super(name, icon);
        this.mainFrame = mainFrame;
        setMnemonic(COMMAND_MNEMONIC);
        //hotkey//setAcceleratorKey(ACCELERATOR_KEY_STROKE);
        setActionCommandType(DESKTOP_ACTION_TYPE);
        //do not know how to set the icon location name, or just do not matter.
    }

    /**
     * The abstract function that descendant classes must be overridden to provide customsized handling.
     *
     * @param e
     * @return true if the action is finished successfully; otherwise, return false.
     */
    protected boolean doAction(ActionEvent e)
    {
        if (mainFrame.getMainFrame() != null)
        {
            WindowEvent we = new WindowEvent(mainFrame.getMainFrame(), WindowEvent.WINDOW_CLOSING);
            mainFrame.getMainFrame().processWindowEvent(we);
            System.out.println("Exit caAdapter_cmts from Running on the MainFrame");
            setSuccessfullyPerformed(true);
            return true;
        }
        if (mainFrame.getMainApplet() != null)
        {
            JOptionPane.showMessageDialog(getAssociatedUIComponent(), "CMTS is running on a web browser. \nPlease, Use the 'Exit' menu of this Web Browser.", "CMTS on a Web Browser", JOptionPane.WARNING_MESSAGE);
            return false;
            /*
            try
            {
                JSObject win = (JSObject) JSObject.getWindow(mainFrame.getMainApplet());
                win.eval("self.close();");
                //win.eval("window.close()");
                System.out.println("Exit caAdapter_cmts from Running on a Web Browser : " + win.getClass().getCanonicalName());

                setSuccessfullyPerformed(true);
                return true;
            }
            catch(JSException je)
            {
                System.out.println("JSException: " + je.getMessage());
                Container com = mainFrame.getMainApplet();
                while(com != null)
                {
                    //System.out.println("  This Container: " + com.getClass().getCanonicalName());
                    if (com instanceof Window)
                    {
                        Window win = (Window) com;
                        WindowEvent we = new WindowEvent(win, WindowEvent.WINDOW_CLOSING);
                        System.out.println("Exit caAdapter_cmts from Running on a AppletViewer or another Applet applecation: " + com.getClass().getCanonicalName());
                        mainFrame.getMainApplet().processWindowEvent(we, win);
                        setSuccessfullyPerformed(true);
                        return true;
                    }
                    com = com.getParent();
                }
            }
            */
        }
        System.out.println("*****Exit Failure!!!");
        setSuccessfullyPerformed(false);
        return false;

    }

    /**
     * Return the associated UI component.
     *
     * @return the associated UI component.
     */
    protected Component getAssociatedUIComponent()
    {
        return mainFrame.getAssociatedUIComponent();
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2008/12/09 19:04:17  linc
 * HISTORY      : First GUI release
 * HISTORY      :
 * HISTORY      : Revision 1.1  2008/12/03 20:46:14  linc
 * HISTORY      : UI update.
 * HISTORY      :
 */
