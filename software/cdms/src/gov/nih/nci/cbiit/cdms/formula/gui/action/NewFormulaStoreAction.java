package gov.nih.nci.cbiit.cdms.formula.gui.action;

import gov.nih.nci.cbiit.cdms.formula.gui.FrameMain;
import gov.nih.nci.cbiit.cdms.formula.gui.PanelMainFrame;
import gov.nih.nci.cbiit.cdms.formula.gui.constants.ActionConstants;
import gov.nih.nci.cbiit.cdms.formula.common.util.DefaultSettings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 18, 2010
 * Time: 5:38:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewFormulaStoreAction extends AbstractAction
{

    public NewFormulaStoreAction(String name)
    {
        super(name);
    }

    @Override
    public void actionPerformed(ActionEvent arg0)
    {
       FrameMain mainFrame=FrameMain.getSingletonInstance();

       mainFrame.getMainPanel().getCentralSplit().createNewFormulaPanel();
    }
}

