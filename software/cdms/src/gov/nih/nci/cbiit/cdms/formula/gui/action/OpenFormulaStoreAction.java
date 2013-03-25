/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui.action;

import gov.nih.nci.cbiit.cdms.formula.common.util.DefaultSettings;
import gov.nih.nci.cbiit.cdms.formula.gui.FrameMain;
import gov.nih.nci.cbiit.cdms.formula.gui.PanelMainFrame;
import gov.nih.nci.cbiit.cdms.formula.gui.constants.ActionConstants;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

public class OpenFormulaStoreAction extends AbstractAction {

	public OpenFormulaStoreAction (String name)
	{
		super(name);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
       FrameMain mainFrame=FrameMain.getSingletonInstance();
       
		File file = DefaultSettings.getUserInputOfFileFromGUI(mainFrame,
               ActionConstants.FORMULA_FILE_EXTENSION, "Open Formula Store", false, false);
       if (file != null)
       {
    	   PanelMainFrame mainPanel= mainFrame.getMainPanel();
    	   mainPanel.openLocalFormulaStore(file);
       }     
	}

}
