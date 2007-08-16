package gov.nih.nci.caadapter.dataviewer.helper;

import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;

/**
 * This is data viewer main window. The RDS module calls this class with arguments to
 * show tables to the user
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: jayannah $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.2 $
 *          $Date: 2007-08-16 18:53:55 $
 */
public class FrameClosingListener implements WindowListener {
    private MainDataViewerFrame mainDataViewerFrame = null;

    public FrameClosingListener(MainDataViewerFrame mD) {
        this.mainDataViewerFrame = mD;
    }

    public void windowOpened(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowClosing(WindowEvent e) {
        try {
            if (!mainDataViewerFrame.get_con().isClosed())
                mainDataViewerFrame.get_con().close();
        } catch (SQLException e1) {
            e1.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
        }
        mainDataViewerFrame.getDialog().dispose();
    }

    public void windowClosed(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowIconified(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowDeiconified(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowActivated(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowDeactivated(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
