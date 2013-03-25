/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.hl7message;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.map.TransformationResult;
import gov.nih.nci.caadapter.hl7.transformation.TransformationObserver;
import gov.nih.nci.caadapter.hl7.transformation.TransformationService;
import gov.nih.nci.caadapter.hl7.transformation.TransformationServiceUtil;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.context.DefaultContextManagerClientPanel;
import gov.nih.nci.caadapter.ui.common.context.ContextManager;
import gov.nih.nci.caadapter.ui.common.context.MenuConstants;
import gov.nih.nci.caadapter.ui.common.message.ValidationMessagePane;
import gov.nih.nci.caadapter.ui.common.nodeloader.HL7V3MessageLoader;
import gov.nih.nci.caadapter.ui.common.preferences.CaAdapterPref;
import gov.nih.nci.caadapter.ui.hl7message.actions.RegenerateHL7V3MessageAction;
import gov.nih.nci.caadapter.hl7.v3csv.TransformationServiceHL7V3ToCsv;
import gov.nih.nci.caadapter.hl7.validation.HL7V3MessageValidator;
//&umkis import gov.nih.nci.caadapter.hl7.v2v3.tools.SchemaDirUtil;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JSplitPane;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.JOptionPane;
import javax.swing.Action;
import javax.swing.BorderFactory;

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
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.43 $
 *          date        $Date: 2009-10-13 18:03:23 $
 */
public class HL7MessagePanel extends DefaultContextManagerClientPanel implements ActionListener
{
    private static final String NEXT_ITEM = "Next";
    private static final String PREVIOUS_ITEM = "Previous";
    private JButton nextButton = new JButton(NEXT_ITEM);
    private JButton previousButton = new JButton(PREVIOUS_ITEM);
	private JTextField currentMessageField = new JTextField("");
	private JTextField totalNumberOfMessageField = new JTextField();
	private int currentCount = 1;//count from 1...
	//private int totalNumberOfMessages = 0;
    private List<String> outputEntryNames = null;
    private boolean isBatchTransform = false;

//&umkis    private int schemaValidationTag = -1;

    private JTextField mapFileNameField;
    private JTextField dataFileNameField;

    private java.util.List <Object> messageList;
    private JScrollPane scrollPane = null;
    private ValidationMessagePane validationMessagePane = null;
    private boolean dataChanged=false;
    private int messageFileType=0;
    public static int MESSAGE_PANEL_HL7=0;
    public static int MESSAGE_PANEL_CSV=1;
	public HL7MessagePanel()
    {
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
		DefaultSettings.setDefaultFeatureForJSplitPane(splitPane);

		JPanel leftPanel = new JPanel(new BorderLayout());
		JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JLabel totalNumberOfMessageLabel = new JLabel("Total Messages:");
		Dimension preferredSize = new Dimension(totalNumberOfMessageLabel.getPreferredSize().width, previousButton.getPreferredSize().height);
		previousButton.setPreferredSize(preferredSize);
		navigationPanel.add(previousButton);
		previousButton.setEnabled(false);
		previousButton.addActionListener(this);

		currentMessageField.setPreferredSize(previousButton.getPreferredSize());
		currentMessageField.setEditable(false);
		currentMessageField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		navigationPanel.add(currentMessageField);

		navigationPanel.add(nextButton);
		nextButton.addActionListener(this);

		RegenerateHL7V3MessageAction regenerateAction = new RegenerateHL7V3MessageAction(this);
		JButton regenerateButton = new JButton(regenerateAction);
		navigationPanel.add(regenerateButton);
		leftPanel.add(navigationPanel, BorderLayout.NORTH);

		JPanel totalMessagePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		totalNumberOfMessageField.setText("0");
		//so that it will be the same size as currentMessageField
		totalNumberOfMessageField.setPreferredSize(previousButton.getPreferredSize());
		totalNumberOfMessageField.setEditable(false);
		totalNumberOfMessageField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		totalMessagePanel.add(totalNumberOfMessageLabel);
		totalMessagePanel.add(totalNumberOfMessageField);
		leftPanel.add(totalMessagePanel, BorderLayout.SOUTH);

		splitPane.setLeftComponent(leftPanel);

		JPanel fieldsOuterPanel = new JPanel(new BorderLayout());
		JPanel fieldsPanel = new JPanel(new GridBagLayout());
		Insets insets = new Insets(5, 5, 5, 5);
		int posY = 0;
		JLabel dataFileNameLabel = new JLabel("Data File:");
		fieldsPanel.add(dataFileNameLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		dataFileNameField = new JTextField();
		Dimension fieldDimension = new Dimension(dataFileNameLabel.getPreferredSize().width, dataFileNameField.getPreferredSize().height);
		dataFileNameField.setEditable(false);
		dataFileNameField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		dataFileNameField.setPreferredSize(fieldDimension);
		fieldsPanel.add(dataFileNameField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		posY++;
		JLabel mapFileNameLabel = new JLabel("Map Specification:");
		fieldsPanel.add(mapFileNameLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		mapFileNameField = new JTextField();
		fieldDimension = new Dimension(mapFileNameLabel.getPreferredSize().width, mapFileNameField.getPreferredSize().height);
		mapFileNameField.setEditable(false);
		mapFileNameField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
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
		scrollPane.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3), (int) (Config.FRAME_DEFAULT_HEIGHT / 2)));
		splitPane.setTopComponent(scrollPane);

		validationMessagePane = new ValidationMessagePane();
		//turn off the display as popup dialog but display it at other location.
		validationMessagePane.setDisplayPopupConfirmationMessage(false);
		validationMessagePane.setValidatorResults(null);
		validationMessagePane.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3), (int) (Config.FRAME_DEFAULT_HEIGHT / 10)));

		splitPane.setBottomComponent(validationMessagePane);

		splitPane.setDividerLocation(0.8);
		nextButton.setEnabled(messageList.size() > 1);
		currentMessageField.setText(String.valueOf(currentCount));
		return splitPane;
	}

	private void reset()
    {
		mapFileNameField.setText("");
        dataFileNameField.setText("");
		currentCount = 1;
		initializeMessageList();
		changeDisplay();
	}
	/**
	 * The messageList may contain a list of HL7 V3 message or a list of CSV data set
	 * @return
	 */
    public java.util.List  getMessageList()
    {
    	List<Object> v3MessageList=new ArrayList<Object>();
    	for (Object message:messageList)
    	{
//    		if (message instanceof XMLElement )
    			v3MessageList.add(message);
    	}
        return v3MessageList;
    }

    private void setMessageResultList(java.util.List newMessageList)
    {
    	if (newMessageList==null||newMessageList.isEmpty())
    		return;
    	initializeMessageList();
    	for(Object oneMsg:newMessageList)
    	{
    		messageList.add(oneMsg);
    	}
    	currentCount = 1;
		changeDisplay();
	}

    public boolean setSaveFile(File saveFile, boolean refresh) throws Exception
    {
    	System.out.println("HL7MessagePanel.setSaveFile()..refresh:"+refresh);
        if (super.setSaveFile(saveFile))//!GeneralUtilities.areEqual(this.saveFile, saveFile))
        {
            if (refresh)
            {
                HL7V3MessageLoader loader = new HL7V3MessageLoader();
                FileReader fr = null;
                BufferedReader br = null;
                try
                {
                    fr = new FileReader(saveFile);
                    br = new BufferedReader(fr);
                    List<String> loaderResultList = loader.loadData(br);
                    List<TransformationResult> messageList = new ArrayList<TransformationResult>();
                    for (int i = 0; i < loaderResultList.size(); i++)
                    {
                        TransformationResult result = new TransformationResult(loaderResultList.get(i), new ValidatorResults());
                        messageList.add(result);
                    }
//                    setV3MessageResultList(messageList);
                }
                finally
                {
                    if (br != null)
                    {
                        br.close();
                    }
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();
        if (PREVIOUS_ITEM.equals(command))
            currentCount--;
        else if (NEXT_ITEM.equals(command))
            currentCount++;
        else
        	return;

        changeDisplay();
    }

	public void clearDataFromUI()
	{
		reset();
	}


	/**
     * Given a data file and a map file, this funciton will coordinate underline utilities
     * to process HL7 V3 Messages and present them, if any, to the UI.
     *
     * @param dataFile
     * @param mapFileName
     * @return if the process succeeded.
     */
    public ValidatorResults generateMappingMessages(File dataFile, File mapFile)
    {
    	System.out.println(this.getClass().getName()+"generateMappingMessages"+System.currentTimeMillis());
        final ValidatorResults validatorResults = new ValidatorResults();
    	dataFileNameField.setText(dataFile.getAbsolutePath());
    	mapFileNameField.setText(mapFile.getAbsolutePath());

//&umkis        confirmSchemaValidation();

        try
		{
			String dataFileName=dataFile.getName();
			if (dataFileName.contains(Config.CSV_DATA_FILE_DEFAULT_EXTENSTION)
					||(FileUtil.readFileIntoString(dataFile.getAbsolutePath()).trim().startsWith("MSH")))
			{//transfer CSV to HL7 V3
				//at first:watch data loading....progress
				final HL7TransformationProgressDialog progressor=new HL7TransformationProgressDialog(this, false);
				progressor.setMaximum(0);
				progressor.setMaximum(TransformationObserver.TRANSFORMATION_DATA_LOADING_STEPS);
				progressor.setNote(TransformationObserver.TRANSFORMATION_MESSAGE_GENERATING_STEP);
				progressor.setProgress(1);

				final HL7MessagePanel listnerPane=this;
		    	final TransformationService ts=new TransformationService(mapFile, dataFile);

//&umkis                if (schemaValidationTag == JOptionPane.YES_OPTION) ts.setSchemaValidation(true);

                if(this.getSaveFile()!=null)
				{
					ts.setOutputFile(this.getSaveFile());
					listnerPane.isBatchTransform = true;
				}
		    	progressor.setProgress(100);
		    	ts.addProgressWatch(progressor);
				Thread localThread=new Thread(
					new Runnable()
					{
						public void run()
						{

					    	try {
					    		int count = 0;
					    		List<XMLElement> xmlElements = null;
								xmlElements =ts.process();

								if (xmlElements == null||xmlElements.isEmpty())
								{
									ValidatorResults rs=ts.getValidatorResults();
									listnerPane.setMessageText(rs.getAllMessages().toString());
									progressor.close();
								}
								else
								{
									count=xmlElements.size();
									if(count>0){
										//listnerPane.totalNumberOfMessages = count;
                                        listnerPane.outputEntryNames = TransformationServiceUtil.getNamesOfEntriesInZip(listnerPane.getSaveFile(), ".ser");
                                        listnerPane.currentCount = 1;
										listnerPane.changeDisplay();
									}
									listnerPane.setMessageResultList(xmlElements);
								}
					    	} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								StringWriter sw = new StringWriter();
							    PrintWriter pw = new PrintWriter(sw);
							    e.printStackTrace(pw);

								listnerPane.setMessageText(sw.toString());
								progressor.close();
							}
						}
					}
				);
				localThread.start();
				this.setChanged(!this.isBatchTransform);
				setMessageFileType(MESSAGE_PANEL_HL7);
				return validatorResults;
			}
			else
			{
				//Hl7 V3 to CSV
				TransformationServiceHL7V3ToCsv svc= new TransformationServiceHL7V3ToCsv(dataFile,mapFile);
				List<TransformationResult> transResults=svc.process();
//				setMessageText(transResults.get(0).getMessageText());
				this.setMessageResultList(transResults);
				validationMessagePane.setValidatorResults(transResults.get(0).getValidatorResults());
				this.setChanged(true);
				setMessageFileType(MESSAGE_PANEL_CSV);
			}
		}
		catch (Exception e)
		{
			Log.logException(this.getClass(), e);
			JOptionPane.showMessageDialog(this.getRootPane().getParent(), "An unexpected error occurred - consult the log.", "Message Generation Error", JOptionPane.ERROR_MESSAGE);
		}
		return validatorResults;
	}

    private void changeDisplay()
    {
        if (outputEntryNames == null)
        {
            currentMessageField.setText("[No Generated V3 Message Instance]");
            return;
        }
        int totalNumberOfMessages = outputEntryNames.size();

        boolean existControlMessage = false;
        for(String str:outputEntryNames)
        {
            int idx = str.indexOf("i.ser");
            if (idx >= 0) existControlMessage = true;
        }
        if (!isBatchTransform && (messageList==null||messageList.isEmpty()))
    		return;
    	if(!isBatchTransform)
    		totalNumberOfMessages = messageList.size();
		nextButton.setEnabled(currentCount < totalNumberOfMessages);
        previousButton.setEnabled(currentCount > 1);

        String displayedV3Entry = "" + (currentCount-1) + ".xml";
        String displayedValidateResultsEntry = "" + (currentCount-1) + ".ser";

        if (existControlMessage)
        {
            if (currentCount == 1)
            {
                displayedV3Entry = "i.xml";
                displayedValidateResultsEntry = "i.ser";
                currentMessageField.setText("Integrated");
            }
            else
            {
                displayedV3Entry = "" + (currentCount-2) + ".xml";
                displayedValidateResultsEntry = "" + (currentCount-2) + ".ser";
                currentMessageField.setText(""+(currentCount-1));

            }
            totalNumberOfMessageField.setText("" + (totalNumberOfMessages-1) + "and Integrated");
        }
        else
        {
            currentMessageField.setText(""+currentCount);
		    totalNumberOfMessageField.setText(String.valueOf(totalNumberOfMessages));
        }
        String messageValidationLevel=CaadapterUtil.readPrefParams(Config.CAADAPTER_COMPONENT_HL7_TRANSFORMATION_VALIDATION_LEVEL);
		if(totalNumberOfMessages > 0 && !isBatchTransform)
		{
			Object generalMssg= messageList.get(currentCount - 1);
			if (generalMssg instanceof XMLElement)
			{
				XMLElement xmlMsg =(XMLElement)generalMssg;
				setMessageText(xmlMsg.toXML().toString());
				ValidatorResults validatorsToShow=new ValidatorResults();
				//add structure validation ... level_0
				validatorsToShow.addValidatorResults(xmlMsg.getValidatorResults());
				if(messageValidationLevel!=null&&
						!messageValidationLevel.equals(CaAdapterPref.VALIDATION_PERFORMANCE_LEVLE_0))
				{
					//add vocabulary validation ... level_1
					validatorsToShow.addValidatorResults(xmlMsg.validate());
					if(messageValidationLevel.equals(CaAdapterPref.VALIDATION_PERFORMANCE_LEVLE_2))
					{	//add xsd validation
						try {
							String xsdFile=FileUtil.searchMessageTypeSchemaFileName(xmlMsg.getMessageType(),"xsd");
							HL7V3MessageValidator h7v3Validator=new HL7V3MessageValidator();
							//add xsd validation ... level_2
							validatorsToShow.addValidatorResults(h7v3Validator.validate(xmlMsg.toXML().toString(), xsdFile));//"C:/Projects/caadapter-gforge-2007-May/etc/schemas/multicacheschemas/COCT_MT150003UV03.xsd");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				validationMessagePane.setValidatorResults(validatorsToShow);
			}
			else if (generalMssg instanceof TransformationResult)
			{
				TransformationResult hl7ToCsv=(TransformationResult)generalMssg;
				setMessageText(hl7ToCsv.getMessageText());
				validationMessagePane.setValidatorResults(hl7ToCsv.getValidatorResults());
			}
		}
		else if(totalNumberOfMessages>0 && isBatchTransform)
		{
			try{
//&umkis                String ins = displayedV3Entry;
//&umkis                int idx = displayedV3Entry.indexOf(".");
//&umkis                if ((schemaValidationTag == JOptionPane.YES_OPTION)&&(idx > 0)) ins = displayedV3Entry.substring(0, idx) + "_Reorganized" + displayedV3Entry.substring(idx);
//
                String xmlMsg = "";
//&umkis                try { xmlMsg = TransformationServiceUtil.readFromZip(this.getSaveFile(),ins); }
//&umkis                catch (IOException ie)
//&umkis                { xmlMsg = ""; }
                if ((xmlMsg == null)||(xmlMsg.trim().equals("")))
                {
                    try
                    {
                        xmlMsg = TransformationServiceUtil.readFromZip(this.getSaveFile(), displayedV3Entry);
                    }
                    catch(IOException iee)
                    {
                        xmlMsg = "[This V3 Message Instance is not generated]";
                    }
                }
//
                setMessageText(xmlMsg);
//
//				setMessageText(TransformationServiceUtil.readFromZip(this.getSaveFile(),String.valueOf(currentCount-1)+".xml"));

				ValidatorResults validatorsToShow=new ValidatorResults();
				//add structure validation ... level_0
				validatorsToShow.addValidatorResults((ValidatorResults)TransformationServiceUtil.readObjFromZip(this.getSaveFile(), displayedValidateResultsEntry));
//				if(messageValidationLevel!=null&&
//						!messageValidationLevel.equals(CaAdapterPref.VALIDATION_PERFORMANCE_LEVLE_0))
//				{
//					//add vocabulary validation ... level_1
//					validatorsToShow.addValidatorResults(xmlMsg.validate());
//					if(messageValidationLevel.equals(CaAdapterPref.VALIDATION_PERFORMANCE_LEVLE_2))
//					{	//add xsd validation
//						try {
//							String xsdFile=FileUtil.searchMessageTypeSchemaFileName(xmlMsg.getMessageType(),"xsd");
//							HL7V3MessageValidator h7v3Validator=new HL7V3MessageValidator();
//							//add xsd validation ... level_2
//							validatorsToShow.addValidatorResults(h7v3Validator.validate(xmlMsg.toXML().toString(), xsdFile));//"C:/Projects/caadapter-gforge-2007-May/etc/schemas/multicacheschemas/COCT_MT150003UV03.xsd");
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				}
				validationMessagePane.setValidatorResults(validatorsToShow);
			}catch(Exception e){
				e.printStackTrace();
			}

		}
		else
		{//just clean up
			setMessageText("");
			validationMessagePane.setValidatorResults(null);
		}
	}

    private void setMessageText(String text)
    {
    	JTextArea outputMessageArea = new JTextArea(text);
        outputMessageArea.setEditable(false);
        outputMessageArea.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
        scrollPane.getViewport().setView(outputMessageArea);
    }

    public Map getMenuItems(String menu_name)
	{
		Action action = null;
		ContextManager contextManager = ContextManager.getContextManager();
		Map <String, Action>actionMap = contextManager.getClientMenuActions(MenuConstants.HL7_V3_MESSAGE, menu_name);
		if (MenuConstants.FILE_MENU_NAME.equals(menu_name))
		{
			JRootPane rootPane = this.getRootPane();
			if (rootPane != null)
			{//rootpane is not null implies this panel is fully displayed;
				//on the flip side, if it is null, it implies it is under certain construction.
                String msgPaneName=this.getName();
                if (msgPaneName!=null&&msgPaneName.equals("CSV To HL7 V3 Message"))
                	contextManager.enableAction(ActionConstants.NEW_CSV_TO_HL7_V3_MESSAGE, false);
                else if (msgPaneName!=null&&msgPaneName.equals("HL7 V2 To HL7 V3 Message"))
                		contextManager.enableAction(ActionConstants.NEW_HL7_V2_TO_HL7_V3_MESSAGE, false);
                else if (msgPaneName!=null&&msgPaneName.equals("HL7 V3 To CSV Data"))
            		contextManager.enableAction(ActionConstants.NEW_HL7_V3_TO_CSV, false);

			}
		}
		//since the action depends on the panel instance,
		//the old action instance should be removed
		if (actionMap!=null)
			contextManager.removeClientMenuAction(MenuConstants.HL7_V3_MESSAGE, menu_name, "");
//
//		if (actionMap==null)
//		{
				action = new gov.nih.nci.caadapter.ui.hl7message.actions.SaveHL7V3MessageAction(this);
				contextManager.addClientMenuAction(MenuConstants.HL7_V3_MESSAGE, MenuConstants.FILE_MENU_NAME,ActionConstants.SAVE, action);
				contextManager.addClientMenuAction(MenuConstants.HL7_V3_MESSAGE, MenuConstants.TOOLBAR_MENU_NAME,ActionConstants.SAVE, action);
				action.setEnabled(true);

				action = new gov.nih.nci.caadapter.ui.hl7message.actions.SaveAsHL7V3MessageAction(this);
				contextManager.addClientMenuAction(MenuConstants.HL7_V3_MESSAGE, MenuConstants.FILE_MENU_NAME,ActionConstants.SAVE_AS, action);
				contextManager.addClientMenuAction(MenuConstants.HL7_V3_MESSAGE, MenuConstants.TOOLBAR_MENU_NAME,ActionConstants.SAVE_AS, action);
				action.setEnabled(true);

				action = new gov.nih.nci.caadapter.ui.hl7message.actions.CloseHL7V3MessageAction(this);
				contextManager.addClientMenuAction(MenuConstants.HL7_V3_MESSAGE, MenuConstants.FILE_MENU_NAME,ActionConstants.CLOSE, action);
				action.setEnabled(true);

				actionMap = contextManager.getClientMenuActions(MenuConstants.HL7_V3_MESSAGE, menu_name);
//		}
		return actionMap;
	}

/*	public Map getMenuItems_save(String menu_name)
    {
//		if (menuMap == null)
//		{
//			menuMap = Collections.synchronizedMap(new HashMap());
//		}
//
//		Map actionMap = (Map) menuMap.get(menu_name);
//		if (actionMap == null)
//		{//lazy initialization
//			actionMap = new HashMap();
//			menuMap.put(menu_name, actionMap);
//		}//end of if(actionMap==null)

        Map actionMap = super.getMenuItems(menu_name);
        if (MenuConstants.FILE_MENU_NAME.equals(menu_name))
        {
            Action action;
//			Log.logInfo(this, "File updated...");
            JRootPane rootPane = this.getRootPane();
            if (rootPane != null)
            {//rootpane is not null implies this panel is fully displayed;
                //on the flip side, if it is null, it implies it is under certain construction.
                MainFrame mf = (MainFrame) rootPane.getParent();
                ContextManager contextManager = ContextManager.getContextManager();
                contextManager.enableAction(ActionConstants.NEW_HL7_V3_MESSAGE, false);
//				contextManager.enableAction(ActionConstants.OPEN_HL7_V3_MESSAGE, true);
            }
            action = (Action) actionMap.get(ActionConstants.SAVE);
            if (action == null || !(action instanceof gov.nih.nci.caadapter.ui.mapping.hl7.actions.SaveMapAction))
            {
                action = new gov.nih.nci.caadapter.ui.hl7message.actions.SaveHL7V3MessageAction(this);
                actionMap.put(ActionConstants.SAVE, action);
            }
            action.setEnabled(true);

            action = (Action) actionMap.get(ActionConstants.SAVE_AS);
            if (action == null || !(action instanceof gov.nih.nci.caadapter.ui.mapping.hl7.actions.SaveAsMapAction))
            {
                action = new gov.nih.nci.caadapter.ui.hl7message.actions.SaveAsHL7V3MessageAction(this);
                actionMap.put(ActionConstants.SAVE_AS, action);
            }
            action.setEnabled(true);

            action = (Action) actionMap.get(ActionConstants.CLOSE);
            if (action == null || !(action instanceof gov.nih.nci.caadapter.ui.mapping.hl7.actions.CloseMapAction))
            {
                action = new gov.nih.nci.caadapter.ui.hl7message.actions.CloseHL7V3MessageAction(this);
                actionMap.put(ActionConstants.CLOSE, action);
            }
            action.setEnabled(true);
        }//end of if(menu_name == MenuConstants.FILE_MENU_NAME)
        return actionMap;
    }
*/
    /**
     * Indicate whether or not it is changed.
     */
    public boolean isChanged()
    {//ignore, since the content in this panel is read-only.
        return dataChanged;
    }

    /**
     * Explicitly set the value.
     *
     * @param newValue
     */
    public void setChanged(boolean newValue)
    {//ignore, since the content in this panel is read-only
    	dataChanged=newValue;
    }

    /**
     * return the open action inherited with this client.
     */
    public Action getDefaultOpenAction()
    {
    	ContextManager contextManager = ContextManager.getContextManager();
        Action openAction = null;
        System.out.println("HL7MessagePanel.getDefaultOpenAction()..openAction:"+openAction);
        return openAction;
    }

    /**
     * Explicitly reload information from the internal given file.
     *
     * @throws Exception
     */
    public void reload() throws Exception
    {
        setSaveFile(getSaveFile(), true);
    }

	/**
	 * Return the value from the data file name field.
	 * @return the value from the data file name field.
	 */
	public String getDataFileNameFieldValue()
	{
		String result = "";
		if(dataFileNameField!=null)
		{
			result = dataFileNameField.getText();
		}
		return result;
	}

	/**
	 * Return the value from the map file name field.
	 * @return the value from the map file name field.
	 */
	public String getMapFileNameFieldValue()
	{
		String result = "";
		if (mapFileNameField != null)
		{
			result = mapFileNameField.getText();
		}
		return result;
	}

//&umkis    private void confirmSchemaValidation()
//&umkis    {
//&umkis        String prop = FileUtil.searchProperty("SchemaValidation");
//&umkis        if (prop == null) prop = "";
//&umkis        prop = prop.trim();
//&umkis        if ((prop.equalsIgnoreCase("true"))||(prop.equalsIgnoreCase("yes"))||(prop.equalsIgnoreCase("y"))||(prop.equalsIgnoreCase("t")))
//&umkis        {
//&umkis            schemaValidationTag = JOptionPane.YES_OPTION;
//&umkis            return;
//&umkis        }
//&umkis        if ((prop.equalsIgnoreCase("no"))||(prop.equalsIgnoreCase("none"))||(prop.equalsIgnoreCase("false"))||(prop.equalsIgnoreCase("f"))||(prop.equalsIgnoreCase("n")))
//&umkis        {
//&umkis            schemaValidationTag = JOptionPane.NO_OPTION;
//&umkis            return;
//&umkis        }

//&umkis    	String validationLevel=CaadapterUtil.readPrefParams(Config.CAADAPTER_COMPONENT_HL7_TRANSFORMATION_VALIDATION_LEVEL);
//&umkis		if ((validationLevel != null)&&(!validationLevel.equalsIgnoreCase(CaAdapterPref.VALIDATION_PERFORMANCE_LEVLE_2)))
//&umkis        {
//&umkis            schemaValidationTag = JOptionPane.YES_OPTION;
//&umkis            return;
//&umkis        }

//&umkis        if (schemaValidationTag < 0)
//&umkis        {
//&umkis            schemaValidationTag = JOptionPane.showConfirmDialog(this, "Do you want to include xml schema validation to output validation?", "Including XSL Schema validation?", JOptionPane.YES_NO_OPTION);
//&umkis        }
//&umkis    }


	public int getMessageFileType() {
		return messageFileType;
	}

	public void setMessageFileType(int messageFileType) {
		this.messageFileType = messageFileType;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.42  2009/10/12 22:45:07  altturbo
 * HISTORY      : add some remark lines.
 * HISTORY      :
 * HISTORY      : Revision 1.41  2009/09/11 16:42:46  altturbo
 * HISTORY      : for control message wrapper
 * HISTORY      :
 * HISTORY      : Revision 1.40  2009/04/15 21:55:47  altturbo
 * HISTORY      : add remarks
 * HISTORY      :
 * HISTORY      : Revision 1.39  2009/04/15 21:53:08  altturbo
 * HISTORY      : add remarks
 * HISTORY      :
 * HISTORY      : Revision 1.38  2009/04/06 19:07:56  altturbo
 * HISTORY      : minor change
 * HISTORY      :
 * HISTORY      : Revision 1.37  2009/04/06 18:31:36  altturbo
 * HISTORY      : minor change - edit remarks
 * HISTORY      :
 * HISTORY      : Revision 1.36  2009/04/02 06:34:24  altturbo
 * HISTORY      : minor change - remark
 * HISTORY      :
 * HISTORY      : Revision 1.35  2009/04/02 06:03:28  altturbo
 * HISTORY      : minor change - remark
 * HISTORY      :
 * HISTORY      : Revision 1.34  2009/04/02 04:36:33  altturbo
 * HISTORY      : add '//&umkis' tag to several lines
 * HISTORY      :
 * HISTORY      : Revision 1.33  2009/03/19 02:26:13  altturbo
 * HISTORY      : XSD validation codes are hidden and marked with "//&umkis".
 * HISTORY      :
 * HISTORY      : Revision 1.32  2009/03/12 03:42:00  umkis
 * HISTORY      : XSD validation codes are unremarked but deactivated
 * HISTORY      :
 * HISTORY      : Revision 1.31  2009/02/26 19:41:58  wangeug
 * HISTORY      : Disable action based on Message data type
 * HISTORY      :
 * HISTORY      : Revision 1.30  2009/02/26 17:07:09  wangeug
 * HISTORY      : hide XSD validation
 * HISTORY      :
 * HISTORY      : Revision 1.29  2009/02/10 18:42:51  umkis
 * HISTORY      : Include schema validation against xsd files when V3 message generating. - additional modifying
 * HISTORY      :
 * HISTORY      : Revision 1.28  2009/02/10 05:34:18  umkis
 * HISTORY      : Include schema validation against xsd files when V3 message generating.
 * HISTORY      :
 * HISTORY      : Revision 1.27  2009/02/06 20:50:00  wangeug
 * HISTORY      : call only process() method for all source datatype
 * HISTORY      :
 * HISTORY      : Revision 1.26  2009/02/03 15:49:21  wangeug
 * HISTORY      : separate menu item group: csv to HL7 V3 and HL7 V2 to HL7 V3
 * HISTORY      :
 * HISTORY      : Revision 1.25  2008/10/29 19:07:19  wangeug
 * HISTORY      : create TransformationServiceUtil.java to hold Util methods
 * HISTORY      :
 * HISTORY      : Revision 1.24  2008/10/24 19:38:29  wangeug
 * HISTORY      : transfer a v2 message into v3 message using SUN v2 schema
 * HISTORY      :
 * HISTORY      : Revision 1.23  2008/06/26 19:45:51  linc
 * HISTORY      : Change HL7 transformation GUI to use batch api.
 * HISTORY      :
 * HISTORY      : Revision 1.22  2008/06/09 19:53:52  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.21  2007/11/13 16:33:33  wangeug
 * HISTORY      : convert Exception.printStackTrace() into a string
 * HISTORY      :
 * HISTORY      : Revision 1.20  2007/11/13 16:05:33  wangeug
 * HISTORY      : process exception message
 * HISTORY      :
 * HISTORY      : Revision 1.19  2007/10/09 21:00:32  wangeug
 * HISTORY      : save csv data from hl7MessagePanel
 * HISTORY      :
 * HISTORY      : Revision 1.18  2007/10/09 18:20:00  wangeug
 * HISTORY      : warning to save HL7 message before close panel
 * HISTORY      :
 * HISTORY      : Revision 1.17  2007/09/11 18:36:54  wangeug
 * HISTORY      : handle null in generating xml hl7 message
 * HISTORY      :
 * HISTORY      : Revision 1.16  2007/09/10 19:13:01  wangeug
 * HISTORY      : fix bug:display validation result as traverse message list
 * HISTORY      :
 * HISTORY      : Revision 1.15  2007/09/10 16:39:32  wangeug
 * HISTORY      : fix bug: create new actionItem with new Panel
 * HISTORY      :
 * HISTORY      : Revision 1.14  2007/09/07 19:29:03  wangeug
 * HISTORY      : relocate readPreference and savePreference methods
 * HISTORY      :
 * HISTORY      : Revision 1.13  2007/09/04 20:45:04  wangeug
 * HISTORY      : add progressor
 * HISTORY      :
 * HISTORY      : Revision 1.12  2007/09/04 20:41:28  wangeug
 * HISTORY      : add progressor
 * HISTORY      :
 * HISTORY      : Revision 1.11  2007/09/04 17:36:35  wangeug
 * HISTORY      : add progressor
 * HISTORY      :
 * HISTORY      : Revision 1.10  2007/08/28 13:57:29  wangeug
 * HISTORY      : remove schemas folder from caAdapter.jar and set it under root directory: xxxx.xsd use relative path as "include"
 * HISTORY      :
 * HISTORY      : Revision 1.9  2007/08/27 15:05:05  wangeug
 * HISTORY      : add hl7 transformation validation level 2:include xsd validation
 * HISTORY      :
 * HISTORY      : Revision 1.8  2007/08/24 21:15:54  wangeug
 * HISTORY      : add hl7 transformation validation level
 * HISTORY      :
 * HISTORY      : Revision 1.7  2007/07/31 20:53:10  wangeug
 * HISTORY      : display validation result with level and message text
 * HISTORY      :
 * HISTORY      : Revision 1.6  2007/07/31 14:27:13  wangeug
 * HISTORY      : enable HL7 V3 to CSV transformation service
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/07/27 20:37:07  wangeug
 * HISTORY      : clean codes
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/07/26 13:38:29  wangeug
 * HISTORY      : display a list of HL7 message with the HL7 message panel
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/07/24 18:20:20  wangeug
 * HISTORY      : include "HL7 V3 To CSV transformation service"
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/20 17:05:02  wangeug
 * HISTORY      : integrate Hl7 transformation service
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/03 19:33:17  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.29  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.28  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.27  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.26  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.25  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.24  2005/12/03 16:03:15  chene
 * HISTORY      : Re-engineer TransformationServiceCsvToHL7V3 to support estimate record number
 * HISTORY      :
 * HISTORY      : Revision 1.23  2005/12/02 15:35:38  jiangsc
 * HISTORY      : Added total message information to UI.
 * HISTORY      :
 * HISTORY      : Revision 1.22  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.21  2005/11/18 20:28:14  jiangsc
 * HISTORY      : Enhanced context-sensitive menu navigation and constructions.
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/11/14 19:55:51  jiangsc
 * HISTORY      : Implementing UI enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/11/12 02:37:46  chene
 * HISTORY      : Try to add Regenerate button, somehow message textarea is not refreshed
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/11/11 21:09:39  chene
 * HISTORY      : Saved point
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/11/02 20:34:10  chene
 * HISTORY      : Rename the MapDriver to TransformationServiceCsvToHL7V3
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/10/21 18:26:17  jiangsc
 * HISTORY      : Validation Class name changes.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/10/18 22:25:29  jiangsc
 * HISTORY      : Changed saveFile() signature.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/10/17 22:39:01  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/10/17 22:06:39  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/10/13 17:37:41  jiangsc
 * HISTORY      : Enhanced UI reporting on exceptions.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/10/07 19:11:31  jiangsc
 * HISTORY      : Enhanced the Look and Feel of Validation and Properties.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/29 19:56:24  jiangsc
 * HISTORY      : Implemented validation message pane in HL7MessagePanel
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/18 15:30:17  jiangsc
 * HISTORY      : First implementation on Switch control.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/05 20:35:49  jiangsc
 * HISTORY      : 0)Implemented field sequencing on CSVPanel but needs further rework;
 * HISTORY      : 1)Removed (Yes/No) for questions;
 * HISTORY      : 2)Removed double-checking after Save-As;
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/03 16:56:15  jiangsc
 * HISTORY      : Further consolidation of context sensitive management.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/02 22:28:53  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/27 22:41:14  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/27 13:57:47  jiangsc
 * HISTORY      : Added the first round of HSMPanel.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/25 21:56:32  jiangsc
 * HISTORY      : 1) Added expand all and collapse all;
 * HISTORY      : 2) Added toolbar on the mapping panel;
 * HISTORY      : 3) Consolidated menus;
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/07/22 20:53:16  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
