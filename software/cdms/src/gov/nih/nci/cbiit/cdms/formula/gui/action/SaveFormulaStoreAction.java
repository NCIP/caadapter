/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui.action;

import gov.nih.nci.cbiit.cdms.formula.gui.FrameMain;
import gov.nih.nci.cbiit.cdms.formula.gui.PanelMainFrame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class SaveFormulaStoreAction extends AbstractAction {

	private boolean existingStore=true;
	
	public SaveFormulaStoreAction (String name)
	{
		this(name, true);
	}
	public SaveFormulaStoreAction (String name, boolean sameStore)
	{
		super(name);
		existingStore=sameStore;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
       FrameMain mainFrame=FrameMain.getSingletonInstance();     
   	   PanelMainFrame mainPanel= mainFrame.getMainPanel();
   	   mainPanel.saveLocalFormulaStore(existingStore);
	}

}
