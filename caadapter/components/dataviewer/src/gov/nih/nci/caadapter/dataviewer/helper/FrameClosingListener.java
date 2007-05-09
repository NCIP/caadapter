package gov.nih.nci.caadapter.dataviewer.helper;

import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: May 7, 2007
 * Time: 1:55:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class FrameClosingListener implements WindowListener
{

    private MainDataViewerFrame mainDataViewerFrame = null;

    public FrameClosingListener(MainDataViewerFrame mD)
    {
        this.mainDataViewerFrame = mD;
    }

    public void windowOpened(WindowEvent e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowClosing(WindowEvent e)
    {
        try
        {
            if (!mainDataViewerFrame.get_con().isClosed())
                mainDataViewerFrame.get_con().close();
        } catch (SQLException e1)
        {
            e1.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
        }
        mainDataViewerFrame.getDialog().dispose();
    }

    public void windowClosed(WindowEvent e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowIconified(WindowEvent e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowDeiconified(WindowEvent e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowActivated(WindowEvent e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowDeactivated(WindowEvent e)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
