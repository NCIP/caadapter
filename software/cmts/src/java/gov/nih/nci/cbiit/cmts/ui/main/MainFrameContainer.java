/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.ui.main;

import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMainPanel;
import gov.nih.nci.cbiit.cmts.web.MainApplet;
import gov.nih.nci.cbiit.cmts.util.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Jun 9, 2011
 * Time: 1:10:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainFrameContainer
{
    private MainFrame mainFrame;
    private MainApplet mainApplet;
    public MainFrameContainer(MainFrame mf)
    {
        setFrameContainer(mf);
    }
    public MainFrameContainer(MainApplet ma)
    {
        setFrameContainer(ma);
    }

    public MainFrame getMainFrame()
    {
        return mainFrame;
    }
    public MainApplet getMainApplet()
    {


        return mainApplet;
    }

    public void setFrameContainer(MainFrame mf)
    {
        mainFrame = mf;
        mainApplet = null;
    }
    public void setFrameContainer(MainApplet ma)
    {
        mainFrame = null;
        mainApplet = ma;



    }

    public Component getAssociatedUIComponent()
    {
        if (mainFrame != null) return mainFrame;
        if (mainApplet != null)
        {
//            try
//            {
//                URL u =FileUtil.getCodeBase();
//                if (u != null) System.out.println("A - Already exist - Applet documentBase=" + u.toString());
//                else
//                {
//                    URL u1 = mainApplet.getDocumentBase();
//                    System.out.println("A - Now input - Applet documentBase=" + u1.toString());
//                    FileUtil.setCodeBase(u1);
//                }
//            }
//            catch(Exception ee)
//            {
//                ee.printStackTrace();
//            }
            return mainApplet;
        }
        return null;
    }
    public Container getAssociatedUIContainer()
    {
        if (mainFrame != null) return mainFrame;
        if (mainApplet != null)
        {
//            try
//            {
//                URL u =FileUtil.getCodeBase();
//                if (u != null) System.out.println("B - Already exist - Applet documentBase=" + u.toString());
//                else
//                {
//                    URL u1 = mainApplet.getDocumentBase();
//                    System.out.println("B - Now input - Applet documentBase=" + u1.toString());
//                    FileUtil.setCodeBase(u1);
//                }
//            }
//            catch(Exception ee)
//            {
//                ee.printStackTrace();
//            }
            return mainApplet;
        }
        return null;
    }

    public void closeTab()
    {
        if (mainFrame != null) mainFrame.closeTab();
        if (mainApplet != null) mainApplet.closeTab();
    }
    public void resetCenterPanel()
    {
        if (mainFrame != null) mainFrame.resetCenterPanel();
        if (mainApplet != null) mainApplet.resetCenterPanel();
    }
    public java.util.List<Component> getAllTabs()
    {
        if (mainFrame != null) return mainFrame.getAllTabs();
        if (mainApplet != null) return mainApplet.getAllTabs();
        return null;
    }
    public JComponent hasComponentOfGivenClass(Class classValue, boolean bringToFront)
    {
        if (mainFrame != null) return mainFrame.hasComponentOfGivenClass(classValue, bringToFront);
        if (mainApplet != null) return mainApplet.hasComponentOfGivenClass(classValue, bringToFront);
        return null;
    }
    public void addNewTab(JPanel mappingPanel, String tabKind)
    {
        if (mainFrame != null) mainFrame.addNewTab(mappingPanel, tabKind);
        if (mainApplet != null) mainApplet.addNewTab(mappingPanel, tabKind);
    }

    public Frame getOwnerFrame()
    {
        if (mainFrame != null) return mainFrame;

        Container con = mainApplet;

        while(con != null)
        {
            if (con instanceof Frame) return (Frame) con;
            con = con.getParent();
        }
        return null;
    }
    public JTabbedPane getTabbedPane()
    {
        if (mainFrame != null) return mainFrame.getTabbedPane();
        if (mainApplet != null) return mainApplet.getTabbedPane();
        return null;
    }

    public void updateToolBar(JToolBar newToolBar)
    {
        if (mainFrame != null) mainFrame.updateToolBar(newToolBar);
        if (mainApplet != null) mainApplet.updateToolBar(newToolBar);
    }
    public void updateToolBar(JToolBar newToolBar, JButton rightSideButton)
    {
        if (mainFrame != null) mainFrame.updateToolBar(newToolBar, rightSideButton);
        if (mainApplet != null) mainApplet.updateToolBar(newToolBar, rightSideButton);
    }

    public MainMenuBar getMainMenuBar()
    {
        if (mainFrame != null) return mainFrame.getMainMenuBar();
        if (mainApplet != null) return mainApplet.getMainMenuBar();
        return null;
    }
}
