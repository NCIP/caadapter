package gov.nih.nci.cbiit.cdms.formula.gui.action;

import gov.nih.nci.cbiit.cdms.formula.gui.FormulaMainPanel;
import gov.nih.nci.cbiit.cdms.formula.gui.constants.ActionConstants;
import gov.nih.nci.cbiit.cdms.formula.common.util.DefaultSettings;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 16, 2010
 * Time: 12:07:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class SaveAsFormulaAction extends AbstractAction
{

	protected FormulaMainPanel mainPanel;
    File savingFile = null;
    String name = "";
    /**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public SaveAsFormulaAction(FormulaMainPanel mainPanel)
	{
		this("Save Formula", mainPanel, null);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public SaveAsFormulaAction(String name, FormulaMainPanel mainPanel)
	{
		this(name, mainPanel, null);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public SaveAsFormulaAction(String name, FormulaMainPanel mainPanel, File file)
	{
		//super(name, icon, null);
        this.name = name;
        this.mainPanel = mainPanel;
        savingFile = file;
//		setAdditionalAttributes();
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
        String doc = mainPanel.getFormulaXMLText();
        if ((doc == null)||(doc.trim().equals("")))
        {
            JOptionPane.showMessageDialog(mainPanel, "Formula content is null.", "No Content", JOptionPane.ERROR_MESSAGE);
            return;
        }
        File file = savingFile;
        if (savingFile == null)
            file = DefaultSettings.getUserInputOfFileFromGUI(this.mainPanel, ActionConstants.FORMULA_FILE_EXTENSION, "Save As...", true, true);
		if (file != null) processSaveFile(file, doc);
        else
        {
            JOptionPane.showMessageDialog(mainPanel, "Saving File name is not given", "No File Name", JOptionPane.ERROR_MESSAGE);
            return;
        }



    }

    private void processSaveFile(File file, String doc)
    {
        FileWriter fw = null;
        //File file = null;
        try
        {
            fw = new FileWriter(file.getAbsolutePath());
            fw.write(doc);
            fw.close();

        }
        catch(Exception ie)
        {
            JOptionPane.showMessageDialog(mainPanel, "Error : " + ie.getMessage(), "File saving Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(mainPanel, "Successfully Saved", "File Save OK", JOptionPane.INFORMATION_MESSAGE);
    }

//	@SuppressWarnings("unchecked")
//	protected boolean processSaveFile(File file) throws Exception
//	{
//		preActionPerformed(mainPanel);
//		MiddlePanelJGraphController mappingManager = mainPanel.getGraphController();//.getMiddlePanel().getGraphController();
//		Mapping mappingData = mappingManager.retrieveMappingData(true);
//		Collections.sort(mappingData.getTags().getTag());
//		MappingFactory.saveMapping(file, mappingData);
//		boolean oldChangeValue = mainPanel.isChanged();
//		try
//		{
//			if (!GeneralUtilities.areEqual(defaultFile, file))
//			{//not equal, change it.
//				removeFileUsageListener(defaultFile, mainPanel);
//				defaultFile = file;
//			}
//			//clear the change flag.
//			mainPanel.setChanged(false);
//			//try to notify affected panels
//			postActionPerformed(mainPanel);
//
//			JOptionPane.showMessageDialog(mainPanel.getParent(), "Mapping data has been saved successfully.", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
//
//			mainPanel.setSaveFile(file);
//			return true;
//		}
//		catch(Throwable e)
//		{
//			//restore the change value since something occurred and believe the save process is aborted.
//			mainPanel.setChanged(oldChangeValue);
//			//rethrow the exeception
//			e.printStackTrace();
//			throw new Exception(e);
//
////			return false;
//		}
//	}
}
