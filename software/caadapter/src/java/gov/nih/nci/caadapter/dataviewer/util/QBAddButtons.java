/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dataviewer.util;

import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;
import gov.nih.nci.caadapter.dataviewer.helper.ToolBarAction;

import javax.swing.*;
import java.net.URL;

/**
 * This class creates all the JButtons on the toolbar
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.10 $
 *          $Date: 2008-06-09 19:53:50 $
 */
public class QBAddButtons {
    MainDataViewerFrame maindialog = null;
    private JButton saveButton = null;
    private JButton panelSaveButton = null;

    public JButton getPanelSaveButton() {
        return panelSaveButton;
    }

    public QBAddButtons(MainDataViewerFrame maindialog) {
        this.maindialog = maindialog;
        this.saveButton = makeNavigationButton("saveandexit", "exit", "Save All & Exit", "Save All & Exit");
    }

    public void addButtons(JToolBar toolBar) {
        JButton button = null;
        //String imageName, String actionCommand, String toolTipText, String altText
        toolBar.add(saveButton);
        //this.getSaveButton().setEnabled(false);
        //panelSaveButton = makeNavigationButton("save", "Save", "Save", "Save SQL");
        //toolBar.add(panelSaveButton);
        button = makeNavigationButton("Exit", "exitwithoutsave", "Exit w/o Save", "Exit w/o Save");
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
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.9  2007/09/11 18:20:08  jayannah
 * added save with out exit button
 *
 * Revision 1.8  2007/09/05 13:25:43  jayannah
 * made changes to the upper lower case image file name
 *
 * Revision 1.7  2007/08/17 20:12:04  jayannah
 * added a new image for QBAddButtons
 * Reduced the height for viewing the 3 buttons in the center panel
 *
 * Revision 1.6  2007/08/16 18:53:55  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */