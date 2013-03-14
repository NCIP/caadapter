/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui.action;

import gov.nih.nci.cbiit.cdms.formula.gui.FrameMain;
import gov.nih.nci.cbiit.cdms.formula.gui.NewFormulaWizard;

import javax.swing.*;
import java.awt.event.ActionEvent;

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
       NewFormulaWizard wizard = new NewFormulaWizard(mainFrame, "Create New Formula", true);
       wizard.setLocation(mainFrame.getX()+mainFrame.getWidth()/4,
    		   mainFrame.getY()+mainFrame.getHeight()/4);
       wizard.setSize((int)mainFrame.getSize().getWidth()/2,
				(int)mainFrame.getSize().getHeight()/2);
       wizard.setVisible(true);
    }
}

