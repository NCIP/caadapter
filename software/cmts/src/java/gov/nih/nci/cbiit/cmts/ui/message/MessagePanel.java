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
    private JTextField resultSeq;
    private JButton decreaseSeq;
    private JButton increaseSeq;
    private String transformationType;
    private File targetDataFile;
    private java.util.List<Object> messageList;
    private JScrollPane scrollPane = null;
    private ValidationMessagePane validationMessagePane = null;
    private TransformationService transformer = null;
    private int messageSeq = -1;

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
		//JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JPanel navigationPanel = buildNavigationPanel();
        /*
//		RegenerateHL7V3MessageAction regenerateAction = new RegenerateHL7V3MessageAction(this);
		JButton regenerateButton = new JButton("Regenerate");//regenerateAction);
		regenerateButton.addActionListener(this);

        navigationPanel.add(regenerateButton, BorderLayout.NORTH);

        decreaseSeq = new JButton("<");
		decreaseSeq.addActionListener(this);
        resultSeq = new JTextField();
        resultSeq.setEditable(false);
        increaseSeq = new JButton(">");
		increaseSeq.addActionListener(this);

        JPanel navigationSouthPanel = new JPanel(new BorderLayout());
        navigationSouthPanel.add(resultSeq, BorderLayout.CENTER);
        navigationSouthPanel.add(decreaseSeq, BorderLayout.WEST);
        navigationSouthPanel.add(increaseSeq, BorderLayout.EAST);

        navigationPanel.add(regenerateButton, BorderLayout.NORTH);
        navigationPanel.add(navigationSouthPanel, BorderLayout.SOUTH);
        */

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

    private JPanel buildNavigationPanel()
    {

        JPanel navigationPanel = new JPanel(new GridBagLayout());


		JButton regenerateButton = new JButton("Regenerate");//regenerateAction);
		regenerateButton.addActionListener(this);

        decreaseSeq = new JButton("<");
		decreaseSeq.addActionListener(this);
        resultSeq = new JTextField("       ");
        resultSeq.setEditable(false);
        increaseSeq = new JButton(">");
		increaseSeq.addActionListener(this);

        GridBagConstraints gbc = new GridBagConstraints();

        Insets insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        navigationPanel.add(regenerateButton, gbc);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = insets;

        navigationPanel.add(decreaseSeq, gbc2);
        navigationPanel.add(resultSeq, gbc2);
        navigationPanel.add(increaseSeq, gbc2);

        return navigationPanel;
    }

    private JComponent contructCenterPanel()
	{
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		DefaultSettings.setDefaultFeatureForJSplitPane(splitPane);
		splitPane.setBorder(BorderFactory.createEmptyBorder());

		scrollPane = new JScrollPane();
		setMessageText(new String[] {""});
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
        if ((e.getSource() == increaseSeq)||(e.getSource() == decreaseSeq))
        {
            if ((messageList == null)||(messageList.size() < 1)) return;
            if (e.getSource() == increaseSeq) messageSeq++;
            else messageSeq--;
            setupNavigationData();
            return;
        }

        try {
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

			    TransformationService transformer=TransformerFactory.getTransformer(transformationType);
                String xmlResult[]=transformer.transfer(sourceFile, mappingFile);
			    setMessageText(xmlResult);
			    setSourceDataURI(sourceFile);
			    setTransformationMappingURI(mappingFile);
			    //setValidationMessage(transformer.validateXmlData(((MappingTransformer)transformer).getTransformationMapping(),xmlResult));


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
                setMessageText(new String[] {""});
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

			    setMessageText(new String[] {xmlResult});
			    //JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "Regeneration has completed successfully !", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
            }

    	} catch (XQException e1) {
			// TODO Auto-generated catch block
            JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent(), "XQException : " + e1.getMessage(), "XQException", JOptionPane.ERROR_MESSAGE);
            e1.printStackTrace();
		} catch (IOException e2) {
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

	public void setMessageText(String[] text)
    {
    	//JTextArea outputMessageArea = new JTextArea(setupXQueryStructuredIndenation(text));
        if ((text == null)||(text.length == 0))
        {
            JOptionPane.showMessageDialog(this, "Message is null or empty", "Eepty Message", JOptionPane.ERROR_MESSAGE);
            decreaseSeq.setEnabled(false);
		    increaseSeq.setEnabled(false);
            resultSeq.setText("       ");
            return;
        }
        messageList = new java.util.ArrayList<Object>();
        for(int i=0;i<text.length;i++) messageList.add(text[i]);

        messageSeq = 0;

        setupNavigationData();
    }
    private void setupNavigationData()
    {
        if (messageList.size() == 1)
        {
            decreaseSeq.setEnabled(false);
		    increaseSeq.setEnabled(false);
            resultSeq.setText("  1/1  ");
        }
        else
        {
            if (messageSeq == 0) decreaseSeq.setEnabled(false);
            else decreaseSeq.setEnabled(true);
            if (messageSeq == (messageList.size() - 1)) increaseSeq.setEnabled(false);
            else increaseSeq.setEnabled(true);

            String ss = "" + (messageSeq + 1) + "/" + messageList.size();
            if (ss.length() == 3) ss = "  " + ss  + "  ";
            if (ss.length() == 4) ss = "  " + ss  + " ";
            if (ss.length() == 5) ss = " " + ss  + " ";
            if (ss.length() == 6) ss = " " + ss  + "";
            resultSeq.setText(ss);
        }
        String message = (String)messageList.get(messageSeq);
        JTextArea outputMessageArea = new JTextArea(message);
        outputMessageArea.setEditable(false);
        scrollPane.getViewport().setView(outputMessageArea);
        try
        {
            if (transformer instanceof MappingTransformer)
                this.setValidationMessage( transformer.validateXmlData(((MappingTransformer)transformer).getTransformationMapping(), message));

            //TransformationService transformer=TransformerFactory.getTransformer(trType);
            //setValidationMessage(transformer.validateXmlData(((MappingTransformer)transformer).getTransformationMapping(), message));
        }
        catch(Exception xe)
        {

        }
    }
    

    public void setTransformationService(TransformationService transform)
    {
        transformer = transform;
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
			e.printStackTrace();
		}
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2009/11/10 19:13:33  wangeug
 * HISTORY      : setup message panel
 * HISTORY      :
 *
 */
