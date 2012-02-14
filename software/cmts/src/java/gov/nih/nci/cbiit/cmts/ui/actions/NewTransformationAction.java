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
import gov.nih.nci.cbiit.cmts.mapping.MappingFactory;
import gov.nih.nci.cbiit.cmts.common.ApplicationResult;
import gov.nih.nci.cbiit.cmts.common.ApplicationMessage;
import gov.nih.nci.caadapter.common.util.FileUtil;

import javax.swing.*;

import java.awt.Component;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

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
    //hotkey//private static final KeyStroke XML_ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK, false);

    private static final Character CSV_COMMAND_MNEMONIC = new Character('C');
    //hotkey//private static final KeyStroke CSV_ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK, false);

    private static final Character HL7_COMMAND_MNEMONIC = new Character('H');
    //hotkey//private static final KeyStroke HL7_ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_H, Event.CTRL_MASK, false);

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
            //hotkey//setAcceleratorKey(XML_ACCELERATOR_KEY_STROKE);
        }
        else if (transformationType==ActionConstants.NEW_CSV_Transformation)
        {
            setMnemonic(CSV_COMMAND_MNEMONIC);
            //hotkey//setAcceleratorKey(CSV_ACCELERATOR_KEY_STROKE);
        }
        else if (transformationType==ActionConstants.NEW_HL7_V2_Transformation)
        {
            setMnemonic(HL7_COMMAND_MNEMONIC);
            //hotkey//setAcceleratorKey(HL7_ACCELERATOR_KEY_STROKE);
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
            MessagePanel newMsgPane=new MessagePanel(mainFrame);
            newMsgPane.setTransformationType(transformationType);

            mainFrame.addNewTab(newMsgPane, ".xml");
            boolean runRes = mainTransformationProcess(newMsgPane, transformationType, w.getMapFile().getPath(), w.getDataFile().getPath());
            if (!runRes) return isSuccessfullyPerformed();
        }
        else
            JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "Transformation has cancelled !", "Cancel Complete", JOptionPane.INFORMATION_MESSAGE);
        setSuccessfullyPerformed(true);
        return isSuccessfullyPerformed();
    }

    public boolean mainTransformationProcess(MessagePanel newMsgPane, String transformationType, String mappingFile, String sourceFile) throws Exception
    {
        //MessagePanel newMsgPane=new MessagePanel();

            //String mappingFile=w.getMapFile().getPath();
        String displayedMessage = newMsgPane.getDisplayedMessage();
        newMsgPane.setMessageText("");
        newMsgPane.setValidationMessage(null);


            TransformationService transformer;
            boolean isXMLtoXML = false;
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
                else if (mappingFile.toLowerCase().endsWith(".xqm"))
                    transformer=TransformerFactory.getTransformer(".xq");
                else
                {
                    transformer=TransformerFactory.getTransformer(TransformationService.TRANSFER_XML_TO_XML);
                    isXMLtoXML = true;
                }
            }
            //String sourceFile =w.getDataFile().getPath();//FileUtil.getRelativePath(w.getDataFile());
            String xmlResult = null;

            if ((transformer instanceof MappingTransformer)&&(isXMLtoXML))
            {
                MappingTransformer mappingTransformer = (MappingTransformer) transformer;
                List<ApplicationResult> resultSourceValidation = mappingTransformer.validateSourceXmlData(MappingFactory.loadMapping(new File(mappingFile)), FileUtil.readFileIntoString(sourceFile));
                int errCount = 0;
                String errMsg = "";
                for (ApplicationResult res:resultSourceValidation)
                {
                    ApplicationResult.Level level = res.getLevel();
                    ApplicationMessage appMessage = res.getMessage();
                    if (level == ApplicationResult.Level.FATAL)
                    {
                        throw new Exception("FATAL in source XML ("+sourceFile+") : " + appMessage.toString());
                    }
                    if (level == ApplicationResult.Level.ERROR)
                    {
                        if (errCount == 0) errMsg = appMessage.toString();
                        errCount++;
                    }
                }

                if (errCount > 0)
                {
                    String msg = "" +errCount+ " Errors are found in the source XML file as following.\nThe result may be with something wrong due to these. Do you want to go on, anyway?\nErrors:\n"+ errMsg;
                    if (errCount == 1) msg = "One Error is found in the source XML file as following.\nThe result may be with something wrong due to this. Do you want to go on, anyway?\nError: \n" + errMsg;
                    int rr = JOptionPane.showConfirmDialog(mainFrame.getAssociatedUIComponent(), msg, "Source XML validation report", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (rr != JOptionPane.YES_OPTION) return false;
                }
            }
            try
            {
                xmlResult=transformer.transfer(sourceFile, mappingFile);
            }
            catch(Exception ee)
            {
                throw new Exception("Transformation Error: Check if this source XML is welformed or valid. : " + sourceFile);
            }
            if ((xmlResult == null)||(xmlResult.trim().equals("")))
            {
                JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "Output is null. Check the data", "Null Result", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            else
            {
                newMsgPane.setMessageText(xmlResult);
                newMsgPane.setSourceDataURI(sourceFile);
                newMsgPane.setTransformationMappingURI(mappingFile);
                if (transformer instanceof MappingTransformer)
                    newMsgPane.setValidationMessage( transformer.validateXmlData(((MappingTransformer)transformer).getTransformationMapping(),xmlResult));
                //

                if ((displayedMessage != null)&&(!displayedMessage.equals("")))
                {
                    if (xmlResult.equals(displayedMessage))
                    {
                        JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "Regeneration is successfully finished. But nothing changed", "Regeneration", JOptionPane.WARNING_MESSAGE);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "Regeneration is successfully finished.", "Regeneration", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                else JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "Transformation has completed successfully !", "Transformation Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        return true;
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
