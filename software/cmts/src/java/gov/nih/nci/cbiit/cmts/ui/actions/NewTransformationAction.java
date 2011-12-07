/**
 * The content of this file is subject to the caAdapter Software License (the "License").
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.ui.actions;


import gov.nih.nci.cbiit.cmts.transform.MappingTransformer;
import gov.nih.nci.cbiit.cmts.transform.TransformationService;
import gov.nih.nci.cbiit.cmts.transform.TransformerFactory;
import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrameContainer;
import gov.nih.nci.cbiit.cmts.ui.message.MessagePanel;
import gov.nih.nci.cbiit.cmts.ui.message.OpenMessageWizard;

import javax.swing.*;

import java.awt.Component;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

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
public class NewTransformationAction extends AbstractContextAction
{

	private static final Character XML_COMMAND_MNEMONIC = new Character('X');
	private static final KeyStroke XML_ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK, false);

	private static final Character CSV_COMMAND_MNEMONIC = new Character('C');
	private static final KeyStroke CSV_ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK, false);

	private static final Character HL7_COMMAND_MNEMONIC = new Character('H');
	private static final KeyStroke HL7_ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_H, Event.CTRL_MASK, false);

	private MainFrameContainer mainFrame;
	private String transformationType=ActionConstants.NEW_XML_Transformation;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public NewTransformationAction(MainFrameContainer mainFrame)
	{
		this(ActionConstants.NEW_XML_Transformation, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public NewTransformationAction(String name, MainFrameContainer mainFrame)
	{
		this(name, null, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public NewTransformationAction(String name, Icon icon, MainFrameContainer mainFrame)
	{
		super(name, icon);
		transformationType=name;
		this.mainFrame = mainFrame;
		if (transformationType==null
				||transformationType==""
					||transformationType==ActionConstants.NEW_XML_Transformation)
		{
			setMnemonic(XML_COMMAND_MNEMONIC);
			setAcceleratorKey(XML_ACCELERATOR_KEY_STROKE);
		}
		else if (transformationType==ActionConstants.NEW_CSV_Transformation)
		{
			setMnemonic(CSV_COMMAND_MNEMONIC);
			setAcceleratorKey(CSV_ACCELERATOR_KEY_STROKE);
		}
		else if (transformationType==ActionConstants.NEW_HL7_V2_Transformation)
		{
			setMnemonic(HL7_COMMAND_MNEMONIC);
			setAcceleratorKey(HL7_ACCELERATOR_KEY_STROKE);
		}
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
		OpenMessageWizard w = new OpenMessageWizard(mainFrame.getOwnerFrame(), this.getName(), true);
		DefaultSettings.centerWindow(w);
		w.setVisible(true);
		if(w.isOkButtonClicked()){
			//MessagePanel newMsgPane=new MessagePanel();
            MessagePanel newMsgPane=new MessagePanel(mainFrame);
            newMsgPane.setTransformationType(transformationType);

			mainFrame.addNewTab(newMsgPane, ".xml");
			String mappingFile=w.getMapFile().getPath();
			TransformationService transformer;
			if (transformationType.equals(ActionConstants.NEW_CSV_Transformation))
				transformer=TransformerFactory.getTransformer(TransformationService.TRANSFER_CSV_TO_XML);
			else if (transformationType.equals(ActionConstants.NEW_HL7_V2_Transformation))
				transformer=TransformerFactory.getTransformer(TransformationService.TRANSFER_HL7_V2_TO_XML);
			else if (transformationType.equals(ActionConstants.NEW_XML_CDA_Transformation))
				transformer=TransformerFactory.getTransformer(TransformationService.TRANSFER_XML_TO_CDA);
			else if (transformationType.equals(ActionConstants.NEW_CSV_CDA_Transformation))
				transformer=TransformerFactory.getTransformer(TransformationService.TRANSFER_CSV_TO_CDA);
			else if (transformationType.equals(ActionConstants.NEW_HL7_V2_CDA_Transformation))
				transformer=TransformerFactory.getTransformer(TransformationService.TRANSFER_HL7_V2_TO_CDA);
			else
			{
				if (mappingFile.toLowerCase().endsWith(".xsl"))
					transformer=TransformerFactory.getTransformer(".xsl");
                else if (mappingFile.toLowerCase().endsWith(".xslt"))
					transformer=TransformerFactory.getTransformer(".xsl");
                else if (mappingFile.toLowerCase().endsWith(".xq"))
					transformer=TransformerFactory.getTransformer(".xq");
                else if (mappingFile.toLowerCase().endsWith(".xquery"))
					transformer=TransformerFactory.getTransformer(".xq");
                else if (mappingFile.toLowerCase().endsWith(".xql"))
					transformer=TransformerFactory.getTransformer(".xq");
                else
					transformer=TransformerFactory.getTransformer(TransformationService.TRANSFER_XML_TO_XML);
			}
			String sourceFile =w.getDataFile().getPath();//FileUtil.getRelativePath(w.getDataFile());
			
			String xmlResult=transformer.transfer(sourceFile, mappingFile);
			newMsgPane.setMessageText(xmlResult);
			newMsgPane.setSourceDataURI(sourceFile);
			newMsgPane.setTransformationMappingURI(mappingFile);
			if (transformer instanceof MappingTransformer)
				newMsgPane.setValidationMessage( transformer.validateXmlData(((MappingTransformer)transformer).getTransformationMapping(),xmlResult));
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
