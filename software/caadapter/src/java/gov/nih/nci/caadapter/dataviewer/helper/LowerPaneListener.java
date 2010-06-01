/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location:
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.dataviewer.helper;


import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;
import gov.nih.nci.caadapter.dataviewer.util.Querypanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Apr 27, 2007
 * Time: 5:04:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class LowerPaneListener implements ChangeListener
{

    MainDataViewerFrame mD;

    public LowerPaneListener(MainDataViewerFrame mD)
    {
        this.mD = mD;
    }

    public void stateChanged(ChangeEvent evt)
    {
        JTabbedPane pane = (JTabbedPane) evt.getSource();
        handleLowerPanel(pane.getSelectedIndex());
    }

    public void handleLowerPanel(int _s)
    {

    }
}
