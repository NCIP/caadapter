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
public class QBAddButtons {
    MainDataViewerFrame maindialog;
    JButton saveButton = null;

    public QBAddButtons(MainDataViewerFrame maindialog) {
        this.maindialog = maindialog;
        this.saveButton = makeNavigationButton("wrench", "exit", "Save & Exit", "Save & Exit");
    }

    public void addButtons(JToolBar toolBar) {
        JButton button = null;
        //String imageName, String actionCommand, String toolTipText, String altText
        toolBar.add(saveButton);
        this.getSaveButton().setEnabled(false);
        button = makeNavigationButton("wrench", "Save", "Save", "Save SQL");
        toolBar.add(button);
        button = makeNavigationButton("Run_sql", QBConstants.NEXT, "Run SQL", "Run SQL");
        toolBar.add(button);
        button = makeNavigationButton("Check", "Validate", "Validate", "Validate");
        toolBar.add(button);
        button = makeNavigationButton("Print", QBConstants.PRINT, "Print", "Print");
        toolBar.add(button);
        button = makeNavigationButton("Help", QBConstants.HELP, "Help", "Help");
        toolBar.add(button);
    }

    protected JButton makeNavigationButton(String imageName, String actionCommand, String toolTipText, String altText) {
        String imgLocation = "/images/_" + imageName + ".gif";
        URL imageURL = null;
        try {
            imageURL = MainDataViewerFrame.class.getResource(imgLocation);
        } catch (Exception e) {
            System.out.println("Unable to find image " + imageName);
            e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
        }
        //Create and initialize the button.
        JButton button = new JButton();
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        button.addActionListener(new ToolBarAction(maindialog));
        if (imageURL != null) {//image found
            button.setIcon(new ImageIcon(imageURL, altText));
            button.setText(altText);
        } else {//no image found
            button.setText(altText);
            System.err.println("Resource not found: " + imgLocation);
        }
        return button;
    }

    public JButton getSaveButton() {
        return saveButton;
    }
}
