/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.cbiit.cmts.ui.actions;


import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.mapping.MappingFactory;
import gov.nih.nci.cbiit.cmts.transform.TransformationService;
import gov.nih.nci.cbiit.cmts.transform.TransformerFactory;
import gov.nih.nci.cbiit.cmts.transform.XQueryBuilder;
import gov.nih.nci.cbiit.cmts.transform.artifact.StylesheetBuilder;
import gov.nih.nci.cbiit.cmts.transform.artifact.XSLTStylesheet;
import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrameContainer;
import gov.nih.nci.cbiit.cmts.ui.message.MessagePanel;
import gov.nih.nci.cbiit.cmts.ui.message.OpenMessageWizard;
import gov.nih.nci.cbiit.cmts.util.FileUtil;

import javax.swing.*;

import java.awt.Component;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;

/**
 * This class defines the new message transformation action.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.5 $
 * @date       $Date: 2009-11-23 18:32:47 $
 */
@SuppressWarnings("serial")
public class NewArtifactAction extends AbstractContextAction
{

	private static final Character XSLT_COMMAND_MNEMONIC = new Character('S');
	//hotkey//private static final KeyStroke XSLT_ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK, false);

	private static final Character XQUERY_COMMAND_MNEMONIC = new Character('Q');
	//hotkey//private static final KeyStroke XQUERY_ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK, false);

	private MainFrameContainer mainFrame;
	private String transformationType=ActionConstants.NEW_XSLT_STYLESHEET;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public NewArtifactAction(MainFrameContainer mainFrame)
	{
		this(null, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public NewArtifactAction(String name, MainFrameContainer mainFrame)
	{
		this(name, null, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public NewArtifactAction(String name, Icon icon, MainFrameContainer mainFrame)
	{
		super(name, icon);
		if (name!=null)
			transformationType=name;
		this.mainFrame = mainFrame;
		if (transformationType==null
				||transformationType==""
					||transformationType==ActionConstants.NEW_XQUERY_STATEMENT)
		{
			setMnemonic(XQUERY_COMMAND_MNEMONIC);
			//hotkey//setAcceleratorKey(XQUERY_ACCELERATOR_KEY_STROKE);
		}
		else if (transformationType==ActionConstants.NEW_XSLT_STYLESHEET)
		{
			setMnemonic(XSLT_COMMAND_MNEMONIC);
			//hotkey//setAcceleratorKey(XSLT_ACCELERATOR_KEY_STROKE);
		}
		setActionCommandType(DESKTOP_ACTION_TYPE);
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
		OpenMessageWizard w = new OpenMessageWizard(mainFrame.getOwnerFrame(), this.getName(), true);
		DefaultSettings.centerWindow(w);
		w.setVisible(true);
		if(w.isOkButtonClicked()){
			//MessagePanel newMsgPane=new MessagePanel();
            MessagePanel newMsgPane=new MessagePanel(mainFrame, false);
            newMsgPane.setTransformationType(transformationType);

			String xmlResult="";
			String artType=".xsl";
			String mappingFile=w.getMapFile().getPath();
			Mapping map=MappingFactory.loadMapping(new File(mappingFile));
			if (transformationType.equals(ActionConstants.NEW_XSLT_STYLESHEET))
			{
				StylesheetBuilder transformer = new StylesheetBuilder(map);

				StringWriter writer = new StringWriter();
				XSLTStylesheet xsltSheet=transformer.buildStyleSheet();
				xsltSheet.writeOut(writer);
				xmlResult=writer.toString();
			}
			else if (transformationType.equals(ActionConstants.NEW_XQUERY_STATEMENT))
			{
				XQueryBuilder xqueryBuilder=new XQueryBuilder(map);
				xmlResult=xqueryBuilder.getXQuery();
				artType=".xq";
			}

            String xmlResult2 = xmlResult;
            for(String key:new String[] {"amp;", "lt;", "gt;", "quot;"})
            {
                String temp = "";
                String key2 = "&amp;" + key;
                while(true)
                {
                    int idx = xmlResult2.toLowerCase().indexOf(key2);
                    if (idx < 0)
                    {
                        temp = temp + xmlResult2;
                        break;
                    }
                    temp = temp + xmlResult2.substring(0, idx) + "&" + key;
                    xmlResult2 = xmlResult2.substring(idx + key2.length());
                }
                xmlResult2 = temp;
            }
            xmlResult = xmlResult2;

            newMsgPane.setViewFileExtension(artType);
            if (artType.equals(".xq")) mainFrame.addNewTab(newMsgPane,".xql");
            else mainFrame.addNewTab(newMsgPane,artType);
			newMsgPane.setMessageText(xmlResult);
//			newMsgPane.setSourceDataURI(sourceFile);
			newMsgPane.setTransformationMappingURI(mappingFile);
//			newMsgPane.setTargetDataFile(w.getDestFile());
//			FileWriter writer = new FileWriter(w.getDestFile());
//			writer.write(xmlResult);
//			writer.close();

//			newMsgPane.setValidationMessage( transformer.validateXmlData(transformer.getTransformationMapping(),xmlResult));
			JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "Transformation has completed successfully !", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
		}
		else
			JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "Transformation has cancelled !", "Cancel Complete", JOptionPane.INFORMATION_MESSAGE);
		setSuccessfullyPerformed(true);
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return mainFrame.getAssociatedUIComponent();
	}

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.4  2009/11/10 19:14:11  wangeug
 * HISTORY      : setup message panel
 * HISTORY      :
 * HISTORY      :
 */
