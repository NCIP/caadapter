/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.mapping.sdtm.actions;

import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.2 revision $Revision: 1.4 $
 * @date       $Date: 2008-09-29 21:22:50 $
 */
public class OpenQueryBuilderAction extends AbstractContextAction
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String COMMAND_NAME = "SQL Query Builder";

    private static final Character COMMAND_MNEMONIC = new Character('Q');

    private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK, false);

    private AbstractMainFrame mainFrame;



    /**
     * Defines an <code>Action</code> object with a default description
     * string and default icon.
     */
    public OpenQueryBuilderAction(AbstractMainFrame mainFrame)
    {
        this(COMMAND_NAME, mainFrame);
        // mainContextManager = cm;
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a default icon.
     */
    public OpenQueryBuilderAction(String name, AbstractMainFrame mainFrame)
    {
        this(name, null, mainFrame);
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public OpenQueryBuilderAction(String name, Icon icon, AbstractMainFrame mainFrame)
    {
        super(name, icon);
        this.mainFrame = mainFrame;
        setMnemonic(COMMAND_MNEMONIC);
        setAcceleratorKey(ACCELERATOR_KEY_STROKE);
        setActionCommandType(DESKTOP_ACTION_TYPE);
        // do not know how to set the icon location name, or just do not
        // matter.
    }

    /**
     * The abstract function that descendant classes must be overridden to
     * provide customsized handling.
     *
     * @param e
     * @return true if the action is finished successfully; otherwise,
     *         return false.
     */
    protected boolean doAction(ActionEvent e) throws Exception
    {
        // new NewSDTMWizard(mainFrame);
//        if (_qbFrame != null)
//        {
//            _qbFrame._jf.setVisible(true);
//            //_qbFrame._jf.setAlwaysOnTop(true);
//            return true;
//        }
        //MainDataViewerFrame _qbFrame;
        /*
          old start
         */
//        final Dialog d = new Dialog(mainFrame, "SQL Query", true);
//        (new Thread()
//        {
//            public void run()
//            {
//                MainDataViewerFrame _qbFrame = new MainDataViewerFrame(mainFrame, false, d);
//                d.dispose();
//            }
//        }).start();
//        //d = new Dialog(mainFrame, "SQL Query", true);
//        JPanel pane = new JPanel();
//        TitledBorder _title = BorderFactory.createTitledBorder("Visual SQL Builder");
//        pane.setBorder(_title);
//        pane.setLayout(new GridLayout(0, 1));
//        JLabel _jl = new JLabel("SQL Query Builder Loading , please wait.....");
//        pane.add(_jl);
//        d.add(pane, BorderLayout.CENTER);
//        d.setLocation(400, 400);
//        d.setSize(500, 130);
//        d.setVisible(true);
//        setSuccessfullyPerformed(true);
//        return isSuccessfullyPerformed();
        /*
           old end
         */

       // new MainDataViewerFrame(mainFrame, false, null);
        return true;
    }

    /**
     * Return the associated UI component.
     *
     * @return the associated UI component.
     */
    protected Component getAssociatedUIComponent()
    {
        return mainFrame;
    }
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
