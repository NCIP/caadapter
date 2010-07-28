/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.ui.actions;


import gov.nih.nci.caadapter.common.csv.CSVDataResult;
import gov.nih.nci.caadapter.common.csv.CsvReader;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.cbiit.cmts.common.XSDParser;
import gov.nih.nci.cbiit.cmts.core.ComponentType;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.mapping.MappingFactory;
import gov.nih.nci.cbiit.cmts.transform.XQueryBuilder;
import gov.nih.nci.cbiit.cmts.transform.XQueryTransformer;
import gov.nih.nci.cbiit.cmts.transform.csv.CsvData2XmlConverter;
import gov.nih.nci.cbiit.cmts.transform.csv.CsvXsd2MetadataConverter;
import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrame;
import gov.nih.nci.cbiit.cmts.ui.message.MessagePanel;
import gov.nih.nci.cbiit.cmts.ui.message.OpenMessageWizard;
import gov.nih.nci.cbiit.cmts.util.FileUtil;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;

import junit.framework.Assert;

/**
 * This class defines the new message transformation action.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.5 $
 * @date       $Date: 2009-11-23 18:32:47 $
 */
public class NewMessageAction extends AbstractContextAction
		{
	private static final String COMMAND_NAME = ActionConstants.NEW_MESSAGE_FILE;
	private static final Character COMMAND_MNEMONIC = new Character('M');
	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_M, Event.CTRL_MASK, false);

	private MainFrame mainFrame;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public NewMessageAction(MainFrame mainFrame)
	{
		this(COMMAND_NAME, mainFrame);
		//mainContextManager = cm;
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public NewMessageAction(String name, MainFrame mainFrame)
	{
		this(name, null, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public NewMessageAction(String name, Icon icon, MainFrame mainFrame)
	{
		super(name, icon);
		this.mainFrame = mainFrame;
		setMnemonic(COMMAND_MNEMONIC);
		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DESKTOP_ACTION_TYPE);
		//do not know how to set the icon location name, or just do not matter.
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
		OpenMessageWizard w = new OpenMessageWizard(mainFrame, COMMAND_NAME, true);
		DefaultSettings.centerWindow(w);
		w.setVisible(true);
		if(w.isOkButtonClicked()){
			MessagePanel newMsgPane=new MessagePanel();
			
			mainFrame.addNewTab(newMsgPane);
			Mapping map = MappingFactory.loadMapping(w.getMapFile());
			XQueryBuilder builder = new XQueryBuilder(map);
			String queryString = builder.getXQuery();
			System.out.println("$$$$$$ query: \n"+queryString);
			XQueryTransformer tester= new XQueryTransformer();
			String srcFile = FileUtil.getRelativePath(w.getDataFile());
			if (srcFile.endsWith("csv"))
			{
				//parse csv data 
				String xsdFile="";
		    	String xsdRoot="";
				for (gov.nih.nci.cbiit.cmts.core.Component comp:map.getComponents().getComponent())
				{
					if (comp.getType().equals(ComponentType.SOURCE))
					{
						xsdFile=comp.getLocation();
						xsdRoot=comp.getRootElement().getName();
					}	
				}
		    	
				XSDParser p = new XSDParser();
				p.loadSchema(xsdFile);
				ElementMeta element = p.getElementMeta(null, xsdRoot);
				CsvXsd2MetadataConverter converter=new CsvXsd2MetadataConverter(element);
				CSVMeta csvMeta= converter.getCSVMeta();
				
		    	String tempXmlSrc=null;
				InputStream  sourceDataStream = new FileInputStream(srcFile);
				CsvReader reader = new CsvReader(sourceDataStream, csvMeta);
				
				while(reader.hasMoreRecord())
				{
					CSVDataResult nextResult=reader.getNextRecord();
					CsvData2XmlConverter xmlConverter=new CsvData2XmlConverter(nextResult);
					tempXmlSrc=xmlConverter.writeXml2File(null);
				}
				srcFile=tempXmlSrc;
			}
			tester.setSourceFileName(srcFile);
			tester.setQuery(queryString);
			String xmlResult=tester.executeQuery();
			newMsgPane.setMessageText(xmlResult);
			FileWriter writer = new FileWriter(w.getDestFile());
			writer.write(xmlResult);
			writer.close();
			
		}
		setSuccessfullyPerformed(true);
		JOptionPane.showMessageDialog(mainFrame, "Transformation has completed successfully.", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
		
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return mainFrame;
	}

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.4  2009/11/10 19:14:11  wangeug
 * HISTORY      : setup message panel
 * HISTORY      :
 * HISTORY      :
 */
