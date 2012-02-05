/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.cbiit.cmts.ui.message;

import gov.nih.nci.cbiit.cmts.transform.MappingTransformer;
import gov.nih.nci.cbiit.cmts.transform.TransformationService;
import gov.nih.nci.cbiit.cmts.transform.TransformerFactory;
import gov.nih.nci.cbiit.cmts.transform.XQueryBuilder;
import gov.nih.nci.cbiit.cmts.transform.artifact.StylesheetBuilder;
import gov.nih.nci.cbiit.cmts.transform.artifact.XSLTStylesheet;
import gov.nih.nci.cbiit.cmts.ui.actions.SaveAsMapAction;
import gov.nih.nci.cbiit.cmts.ui.actions.SaveMapAction;
import gov.nih.nci.cbiit.cmts.ui.actions.NewTransformationAction;
import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmts.ui.common.ContextManager;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.common.MenuConstants;
import gov.nih.nci.cbiit.cmts.ui.main.AbstractTabPanel;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrameContainer;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.mapping.MappingFactory;


import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JSplitPane;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.xml.xquery.XQException;
import javax.xml.bind.JAXBException;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is the main entry point to display HL7V3 message panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.0
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2009-11-24 18:31:25 $
 */
public class MessagePanel extends AbstractTabPanel implements ActionListener
{
	private boolean SOURCE_DATA_REQUIRED=true;
    private JTextField mapFileNameField;
    private JTextField dataFileNameField;
    private String transformationType;
    private File targetDataFile;
    private java.util.List <Object> messageList;
    private JScrollPane scrollPane = null;
    private ValidationMessagePane validationMessagePane = null;
    private boolean isSaved = false;

    public MessagePanel(MainFrameContainer mainFrame)
    {
    	this (mainFrame, true);
    }

    public MessagePanel(MainFrameContainer mainFrame, boolean sourceDataRequired)
    {
        this.mainFrame = mainFrame;
        SOURCE_DATA_REQUIRED=sourceDataRequired;
        initializeMessageList();
        setLayout(new BorderLayout());
        add(contructNorthPanel(), BorderLayout.NORTH);
		add(contructCenterPanel(), BorderLayout.CENTER);
	}

	private void initializeMessageList()
	{
		if(messageList==null)
		{
			messageList = new ArrayList<Object>();
		}
		else
		{
			messageList.clear();
		}
	}

	private JComponent contructNorthPanel()
	{
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		JPanel leftPanel = new JPanel(new BorderLayout());
		JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));

//		RegenerateHL7V3MessageAction regenerateAction = new RegenerateHL7V3MessageAction(this);
		JButton regenerateButton = new JButton("Regenerate");//regenerateAction);
		navigationPanel.add(regenerateButton);
 		regenerateButton.addActionListener(this);
		leftPanel.add(navigationPanel, BorderLayout.NORTH);
		splitPane.setLeftComponent(leftPanel);

		JPanel fieldsOuterPanel = new JPanel(new BorderLayout());
		JPanel fieldsPanel = new JPanel(new GridBagLayout());
		Insets insets = new Insets(5, 5, 5, 5);
		int posY = 0;
		JLabel dataFileNameLabel = new JLabel("Source Data:");
		if (SOURCE_DATA_REQUIRED)
			fieldsPanel.add(dataFileNameLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		dataFileNameField = new JTextField();
		Dimension fieldDimension = new Dimension(dataFileNameLabel.getPreferredSize().width, dataFileNameField.getPreferredSize().height);
		dataFileNameField.setEditable(false);
		dataFileNameField.setPreferredSize(fieldDimension);
		if (SOURCE_DATA_REQUIRED)
			fieldsPanel.add(dataFileNameField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		posY++;
		JLabel mapFileNameLabel = new JLabel("Transformation Mapping:");
		fieldsPanel.add(mapFileNameLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		mapFileNameField = new JTextField();
		fieldDimension = new Dimension(mapFileNameLabel.getPreferredSize().width, mapFileNameField.getPreferredSize().height);
		mapFileNameField.setEditable(false);
		mapFileNameField.setPreferredSize(fieldDimension);
		fieldsPanel.add(mapFileNameField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		fieldsOuterPanel.add(fieldsPanel, BorderLayout.CENTER);
		splitPane.setRightComponent(fieldsOuterPanel);
		return splitPane;
	}

	private JComponent contructCenterPanel()
	{
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		DefaultSettings.setDefaultFeatureForJSplitPane(splitPane);
		splitPane.setBorder(BorderFactory.createEmptyBorder());

		scrollPane = new JScrollPane();
		setMessageText("");
		splitPane.setTopComponent(scrollPane);

		validationMessagePane = new ValidationMessagePane();
		//turn off the display as popup dialog but display it at other location.
		validationMessagePane.setDisplayPopupConfirmationMessage(false);
		validationMessagePane.setValidatorResults(null);
		splitPane.setBottomComponent(validationMessagePane);
		//splitPane.setDividerLocation(0.8);
        splitPane.setDividerLocation(250);
        return splitPane;
	}

	/**
	 * The messageList may contain a list of HL7 V3 message or a list of CSV data set
	 * @return
	 */
    public java.util.List<Object>  getMessageList()
    {
    	List<Object> v3MessageList=new ArrayList<Object>();
    	for (Object message:messageList)
    	{
//    		if (message instanceof XMLElement )
    			v3MessageList.add(message);
    	}
        return v3MessageList;
    }



    public boolean setSaveFile(File saveFile, boolean refresh) throws Exception
    {
    	System.out.println("HL7MessagePanel.setSaveFile()..refresh:"+refresh);
    	return true;
    }

    public void actionPerformed(ActionEvent e)
    {
    	try {

            String displayedMessage = getDisplayedMessage();
            if (displayedMessage == null) displayedMessage = "";
            else displayedMessage = displayedMessage.trim();
            if (transformationType==null
				||transformationType==""
					||transformationType==ActionConstants.NEW_XML_Transformation)
            {
                String mappingFile = mapFileNameField.getText();
                if ((mappingFile == null)||(mappingFile.trim().equals("")))
                {
                    JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "Map file name field is null.", "Null Map File", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String sourceFile = dataFileNameField.getText();
                if ((sourceFile == null)||(sourceFile.trim().equals("")))
                {
                    JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "Source data file name field is null.", "Null Data File", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                NewTransformationAction transformationAction = new NewTransformationAction(mainFrame);
                boolean runRes = true;
                try
                {
                    runRes = transformationAction.mainTransformationProcess(this, transformationType, mappingFile, sourceFile);
                }
                catch(Exception ee)
                {
                    JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), ee.getMessage(), "Transformation error", JOptionPane.ERROR_MESSAGE);
                }
                //TransformationService transformer=TransformerFactory.getTransformer(transformationType);
                //transformer.validateXmlData(((MappingTransformer)transformer).getTransformationMapping(),
                //        sourceFile);
                //String xmlResult=transformer.transfer(sourceFile, mappingFile);
			    //setMessageText(xmlResult);
			    //setSourceDataURI(sourceFile);
			    //setTransformationMappingURI(mappingFile);
			    //setValidationMessage( transformer.validateXmlData(((MappingTransformer)transformer).getTransformationMapping(),xmlResult));


//                setMessageText("");
//                validationMessagePane.setMessageList(null);
//                TransformationService transformer=TransformerFactory.getTransformer(transformationType);
//                String xmlResult=transformer.transfer(dataFileNameField.getText(), mapFileNameField.getText());
//                setMessageText(xmlResult);
//                FileWriter writer = new FileWriter(this.targetDataFile);
//                writer.write(xmlResult);
//                writer.close();
//                setValidationMessage( transformer.validateXmlData(((MappingTransformer)transformer).getTransformationMapping(),xmlResult));
            }
            else
            {
                setMessageText("");
                String xmlResult="";
			    String artType=".xsl";
                String mapFile = mapFileNameField.getText();
                if ((mapFile == null)||(mapFile.trim().equals("")))
                {
                    JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "Map file name field is null.", "Null Map File", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Mapping map= MappingFactory.loadMapping(new File(mapFileNameField.getText()));
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
			    setViewFileExtension(artType);

                if (xmlResult == null) xmlResult = "";
                else xmlResult = xmlResult.trim();

                if (xmlResult.equals(""))
                    JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "This Result is Null or Empty. Check the input data.", "Null Result", JOptionPane.ERROR_MESSAGE);

                setMessageText(xmlResult);

                if ((!displayedMessage.equals(""))&&(!xmlResult.equals("")))
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
                //JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "Regeneration has completed successfully !", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
            }

    	} /*catch (XQException e1) {
			// TODO Auto-generated catch block
            JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "XQException : " + e1.getMessage(), "XQException", JOptionPane.ERROR_MESSAGE);
            e1.printStackTrace();
		}*/ catch (IOException e2) {
			// TODO Auto-generated catch block
            JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "IOException : " + e2.getMessage(), "IOException", JOptionPane.ERROR_MESSAGE);
            e2.printStackTrace();
		} catch (JAXBException e3) {
			// TODO Auto-generated catch block
            JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "JAXBException : " + e3.getMessage(), "JAXBException", JOptionPane.ERROR_MESSAGE);
            e3.printStackTrace();
		}
       	return;

    }
    
	public void setTargetDataFile(File targetDataFile) {
		this.targetDataFile = targetDataFile;
	}

	public void setSourceDataURI(String sourceURI)
	{
		dataFileNameField.setText(sourceURI);
		
	}
	

	public void setTransformationMappingURI(String mappingURI)
	{
		mapFileNameField.setText(mappingURI);
		
	}
	
    public String getTransformationType() {
		return transformationType;
	}

	public void setTransformationType(String transformationType) {

        this.transformationType = transformationType;
	}

	public void setMessageText(String text)
    {
    	//JTextArea outputMessageArea = new JTextArea(setupXQueryStructuredIndenation(text));
        JTextArea outputMessageArea = new JTextArea(text);
        outputMessageArea.setEditable(false);
        scrollPane.getViewport().setView(outputMessageArea);
        isSaved = false;
    }
    public boolean hasBeenSaved()
    {
        return isSaved;
    }
    public String getDisplayedMessage()
    {
        try
        {
            JTextArea msgPane=(JTextArea)scrollPane.getViewport().getView();
			return msgPane.getText();
        }
        catch(Exception ee)
        {
            return null;
        }
    }

    public void setValidationMessage(List validationMessage)
    {
    	validationMessagePane.setMessageList(validationMessage);	
    }
    public Map getMenuItems(String menu_name)
	{
		Action action = null;
		Map <String, Action>actionMap = null;
		ContextManager contextManager = ContextManager.getContextManager();
		actionMap = contextManager.getClientMenuActions("MESSAGE", menu_name);
		contextManager.enableAction(ActionConstants.CLOSE, true);
		contextManager.enableAction(ActionConstants.SAVE, true);
		contextManager.enableAction(ActionConstants.SAVE_AS, true);

		if (actionMap!=null)
			contextManager.removeClientMenuAction("MESSAGE", menu_name, "");

		action = new SaveMapAction(this);
		contextManager.addClientMenuAction("MESSAGE", MenuConstants.FILE_MENU_NAME,ActionConstants.SAVE, action);
		action.setEnabled(true);
		action = new SaveAsMapAction(this);
		contextManager.addClientMenuAction("MESSAGE", MenuConstants.FILE_MENU_NAME,ActionConstants.SAVE_AS, action);
		action.setEnabled(true);
		return contextManager.getClientMenuActions("MESSAGE", menu_name);
	}

	@Override
	public void persistFile(File dataFile) {
		// TODO Auto-generated method stub
		FileWriter writer;
		try {
			writer = new FileWriter(dataFile);
			JTextArea msgPane=(JTextArea)scrollPane.getViewport().getView();
			
			writer.write(msgPane.getText());
			writer.close();
			JOptionPane.showMessageDialog(getParent(), "Data has been saved successfully.", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
            JOptionPane.showMessageDialog(getParent(), e.getMessage(), "Save Failure", JOptionPane.ERROR_MESSAGE);
        }
        isSaved = true;
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2009/11/10 19:13:33  wangeug
 * HISTORY      : setup message panel
 * HISTORY      :
 *
 */
