/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.ui.main;

import gov.nih.nci.cbiit.cmts.ui.actions.DefaultCloseAction;
import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmts.ui.common.ContextManagerClient;
import gov.nih.nci.cbiit.cmts.ui.common.MenuConstants;
import gov.nih.nci.cbiit.cmts.web.MainApplet;

import java.awt.Container;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JRootPane;

public abstract class AbstractTabPanel extends JPanel implements ContextManagerClient {
	private File saveFile = null;
	private boolean panelChanged;
	private String viewFileExtension=".xml";
    protected MainFrameContainer mainFrame = null;
	/**
	 * Return the save file.
	 * @return the save file.
	 */
	public File getSaveFile()
	{
		return saveFile;
	}
	/**
	 * Set a new save file.
	 *
	 * @param saveFile
	 * @return true if the value is changed, false otherwise.
	 */
	public void setSaveFile(File saveFile)
	{
		this.saveFile = saveFile;
		updateTitle(this.saveFile.getName());
	}
	
	public String getViewFileExtension() {
		return viewFileExtension;
	}
	public void setViewFileExtension(String viewFileExtension) {
		this.viewFileExtension = viewFileExtension;
	}
	/**
	 * Overridable function to update Title in the tabbed pane.
	 * @param newTitle
	 */
	private void updateTitle(String newTitle)
	{
		JRootPane rootPane = getRootPane();
		if (rootPane != null)
		{
			Container container = rootPane.getParent();
			if (container instanceof MainFrame)
			{
				((MainFrame)container).setCurrentPanelTitle(newTitle);
			}
            if (container instanceof MainApplet)
			{
				((MainApplet)container).setCurrentPanelTitle(newTitle);
			}
        }
	}
	@Override
	public List<File> getAssociatedFileList() {
		// TODO Auto-generated method stub
		List<File> resultList = new ArrayList<java.io.File>();
		if(getSaveFile()!=null)
		{
			resultList.add(getSaveFile());
		}
		return resultList;
	}
	@Override
	public Action getDefaultOpenAction() {
		// TODO Auto-generated method stub
		return null;
	}
	public Action getDefaultCloseAction() {
//		// TODO Auto-generated method stub
		Map actionMap = getMenuItems(MenuConstants.FILE_MENU_NAME);
		Action closeAction = (Action) actionMap.get(ActionConstants.CLOSE);
		if (closeAction==null)
			closeAction=new DefaultCloseAction(mainFrame);
		actionMap.put(ActionConstants.CLOSE, closeAction);
		return closeAction;
	}
	
	/**
	 * return the save action inherited with this client.
	 * @return the save action inherited with this client.
	 */
	public Action getDefaultSaveAction()
	{
		Map actionMap = getMenuItems(MenuConstants.FILE_MENU_NAME);
		Action saveAction = (Action) actionMap.get(ActionConstants.SAVE);
		return saveAction;
	}
	
	public List<Action> getToolbarActionList() {
		// TODO Auto-generated method stub
		java.util.List<Action> actions = new ArrayList<Action>();
		return actions;
	}
	
	@Override
	public boolean isChanged() {
		// TODO Auto-generated method stub
		return panelChanged;
	}
	@Override
	public void setChanged(boolean newValue) {
		// TODO Auto-generated method stub
		panelChanged=newValue;
	}
	
	public abstract void persistFile(File dataFile);
}
