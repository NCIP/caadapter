/*L
 * Copyright SAIC.
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
 * To change this template use File | Settings | File Templates.
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Jul 26, 2007
 * Time: 2:40:41 PM
 * @author LAST UPDATE $Author: phadkes $
 * @since      caAdapter  v4.2
 * @version    $Revision: 1.5 $
 * @date       $Date: 2008-09-24 17:53:33 $
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
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
