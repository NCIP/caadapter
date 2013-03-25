/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.common.preferences;

import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.ui.main.MainMenuBar;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Jul 26, 2007
 * Time: 2:40:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class CaWindowClosingListener implements WindowListener
{


    public void windowActivated(WindowEvent e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowClosed(WindowEvent e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowClosing(WindowEvent e)
    {
        CaAdapterPref.setCaAdapterPreferences(CaadapterUtil.getCaAdapterPreferences());
    }

    public void windowDeactivated(WindowEvent e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowDeiconified(WindowEvent e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowIconified(WindowEvent e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowOpened(WindowEvent e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
