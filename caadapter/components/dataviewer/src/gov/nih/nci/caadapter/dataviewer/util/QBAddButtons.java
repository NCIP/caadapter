package gov.nih.nci.caadapter.dataviewer.util;

import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;
import gov.nih.nci.caadapter.dataviewer.helper.ToolBarAction;

import javax.swing.*;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Apr 27, 2007
 * Time: 11:22:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class QBAddButtons
{

    MainDataViewerFrame maindialog;

    public QBAddButtons(MainDataViewerFrame maindialog)
    {
        this.maindialog = maindialog;
    }

    public void addButtons(JToolBar toolBar)
    {
        JButton button = null;
        button = makeNavigationButton("load", QBConstants.Load, "load", "Load SQL");
        //toolBar.add(button).setEnabled(false);
        button = makeNavigationButton("save", QBConstants.Save, "save", "Save SQL");
        toolBar.add(button);
        button = makeNavigationButton("Run_sql", QBConstants.NEXT, "Run SQL", "Run SQL");
        toolBar.add(button);
        button = makeNavigationButton("reset", QBConstants.Reset, "reset", "Reset Diagram");
        //toolBar.add(button);
        button = makeNavigationButton("Add_tables", QBConstants.ADD, "Add Tables", "Add Tables");
        // toolBar.add(button);
        button = makeNavigationButton("Print", QBConstants.PRINT, "Print", "Print");
        toolBar.add(button);
        button = makeNavigationButton("Help", QBConstants.HELP, "Help", "Help");
        toolBar.add(button);
    }

    public void addButtons(JToolBar toolBar, boolean fromMenu)
    {
        JButton button = null;
        button = makeNavigationButton("load", QBConstants.Load, "load", "Load SQL");
        if (fromMenu)
            toolBar.add(button).setEnabled(true);
        else
            toolBar.add(button).setEnabled(false);
        button = makeNavigationButton("save", QBConstants.Save, "save", "Save SQL");
        toolBar.add(button);
        button = makeNavigationButton("Run_sql", QBConstants.NEXT, "Run SQL", "Run SQL");
        toolBar.add(button);
        button = makeNavigationButton("reset", QBConstants.Reset, "reset", "Reset Diagram");
        //toolBar.add(button);
        button = makeNavigationButton("Add_tables", QBConstants.ADD, "Add Tables", "Add Tables");
        // toolBar.add(button);
        button = makeNavigationButton("Print", QBConstants.PRINT, "Print", "Print");
        toolBar.add(button);
        button = makeNavigationButton("Help", QBConstants.HELP, "Help", "Help");
        toolBar.add(button);
    }

    protected JButton makeNavigationButton(String imageName, String actionCommand, String toolTipText, String altText)
    {
        String imgLocation = "/images/_" + imageName + ".gif";
        URL imageURL = null;
        try
        {
            imageURL = MainDataViewerFrame.class.getResource(imgLocation);
        } catch (Exception e)
        {
            e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
        }
        //Create and initialize the button.
        JButton button = new JButton();
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        button.addActionListener(new ToolBarAction(maindialog));
        if (imageURL != null)
        {//image found
            button.setIcon(new ImageIcon(imageURL, altText));
            button.setText(altText);
        } else
        {//no image found
            button.setText(altText);
            System.err.println("Resource not found: " + imgLocation);
        }
        return button;
    }
}
