/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/HL7MessagePanel.java,v 1.2 2007-07-20 17:05:02 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.hl7message;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.map.TransformationResult;
import gov.nih.nci.caadapter.hl7.map.TransformationServiceHL7V3ToCsv;
import gov.nih.nci.caadapter.hl7.transformation.TransformationService;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.context.DefaultContextManagerClientPanel;
import gov.nih.nci.caadapter.ui.common.context.ContextManager;
import gov.nih.nci.caadapter.ui.common.context.MenuConstants;
import gov.nih.nci.caadapter.ui.common.message.ValidationMessagePane;
import gov.nih.nci.caadapter.ui.common.nodeloader.HL7V3MessageLoader;
import gov.nih.nci.caadapter.ui.hl7message.actions.RegenerateHL7V3MessageAction;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is the main entry point to display HL7V3 message panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2007-07-20 17:05:02 $
 */
public class HL7MessagePanel extends DefaultContextManagerClientPanel implements ActionListener
{
    private static final String NEXT_ITEM = "Next";
    private static final String PREVIOUS_ITEM = "Previous";
    private JButton nextButton = new JButton(NEXT_ITEM);
    private JButton previousButton = new JButton(PREVIOUS_ITEM);
	private JTextField currentMessageField = new JTextField("");
	private JTextField totalNumberOfMessageField = new JTextField();
	private int currentCount = 0;

    private JTextField mapFileNameField;
    private JTextField dataFileNameField;

    private java.util.List mV3MessageList;
    private JTextArea outputMessageArea = null;
    private JScrollPane scrollPane = null;
    private JSplitPane splitPane = null;
    private ValidationMessagePane validationMessagePane = null;

	public HL7MessagePanel()
    {
		initializeMessageList();
		initialize();
	}

	private void initializeMessageList()
	{
		if(mV3MessageList==null)
		{
			mV3MessageList = new ArrayList();
		}
		else
		{
			mV3MessageList.clear();
		}
	}

	private void initialize()
    {
        this.setLayout(new BorderLayout());
        this.add(contructNorthPanel(), BorderLayout.NORTH);
		this.add(contructCenterPanel(), BorderLayout.CENTER);
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
		currentMessageField.setText(String.valueOf(currentCount));
		currentMessageField.setEditable(false);
		currentMessageField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		navigationPanel.add(currentMessageField);

		navigationPanel.add(nextButton);
		nextButton.setEnabled(false);
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
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
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
		//		validationMessagePane.setBorder(BorderFactory.createTitledBorder("Validation Messages"));
		validationMessagePane.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3), (int) (Config.FRAME_DEFAULT_HEIGHT / 10)));

		splitPane.setBottomComponent(validationMessagePane);

		splitPane.setDividerLocation(0.8);
		nextButton.setEnabled(mV3MessageList.size() > 1);
		currentCount = 1;
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

    public java.util.List getV3MessageList()
    {
        return mV3MessageList;
    }

    void setV3MessageResultList(java.util.List mV3MessageList)
    {
        this.mV3MessageList = mV3MessageList;
        if (mV3MessageList == null || mV3MessageList.size() == 0)
        {
			initializeMessageList();
			return;
        }
		TransformationResult result = (TransformationResult) mV3MessageList.get(0);
        setMessageText(result.getHl7V3MessageText());
        validationMessagePane.setValidatorResults(result.getValidatorResults());
		currentCount = 1;
		changeDisplay();
	}

    public boolean setSaveFile(File saveFile, boolean refresh) throws Exception
    {
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
                    setV3MessageResultList(messageList);
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
        {
            currentCount--;
            changeDisplay();
        }
        else if (NEXT_ITEM.equals(command))
        {
            currentCount++;
            changeDisplay();
        }
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
        ValidatorResults validatorResults = new ValidatorResults();
		try
		{
			String dataFileName=dataFile.getName();
			if (dataFileName.contains(Config.CSV_DATA_FILE_DEFAULT_EXTENSTION))
				validatorResults.addValidatorResults(processFiles(dataFile, mapFile));
			else
			{
				TransformationServiceHL7V3ToCsv svc= new TransformationServiceHL7V3ToCsv(dataFile,mapFile);
				svc.process(null);
				this.setMessageText(svc.getMsgGenerated());
//				validatorResults.addValidatorResult(svc.process(null));
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
		int totalNumberOfMessages = mV3MessageList.size();
		nextButton.setEnabled(currentCount < totalNumberOfMessages);
        previousButton.setEnabled(currentCount > 1);
        currentMessageField.setText(String.valueOf(currentCount));
		totalNumberOfMessageField.setText(String.valueOf(totalNumberOfMessages));
        scrollPane.getViewport().remove(outputMessageArea);
		if(totalNumberOfMessages > 0)
		{
			TransformationResult result = (TransformationResult) mV3MessageList.get(currentCount - 1);
			setMessageText(result.getHl7V3MessageText());
			validationMessagePane.setValidatorResults(result.getValidatorResults());
		}
		else
		{//just clean up
			setMessageText("");
			validationMessagePane.setValidatorResults(null);
		}
	}

    private void setMessageText(String text)
    {
        outputMessageArea = new JTextArea(text);
        outputMessageArea.setEditable(false);
        outputMessageArea.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
        scrollPane.getViewport().setView(outputMessageArea);
		outputMessageArea.repaint();
    }

    private ValidatorResults processFiles(File dataFile, File mapFile) throws Exception
	{
    	ValidatorResults validatorResults = null;
		TransformationServiceHL7V3ToCsv svc= new TransformationServiceHL7V3ToCsv(dataFile,mapFile);
		svc.process(null);
		this.setMessageText(svc.getMsgGenerated());
		TransformationService ts=new TransformationService(mapFile, dataFile);
		List<XMLElement> xmlElements =ts.process();
		if (xmlElements.size()>0)
		{
			XMLElement xmlElement=(XMLElement)xmlElements.get(0);
			this.setMessageText(""+xmlElement.toXML());
		}
//		HL7MessageGenerationController controler = new HL7MessageGenerationController(this, dataFile,  mapFile);
//		validatorResults=controler.process();
//		if( !validatorResults.hasFatal() )
//		{//normal proceeding
//			dataFileNameField.setText(dataFile.getAbsolutePath());
//			mapFileNameField.setText(mapFile.getAbsolutePath());
//			setV3MessageResultList(controler.getMessageList());
//		}
		return validatorResults;
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
                contextManager.enableAction(ActionConstants.NEW_HL7_V3_MESSAGE, false);
			}
		}
		
		if (actionMap==null)
		{
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
		}		
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
        return false;
    }

    /**
     * Explicitly set the value.
     *
     * @param newValue
     */
    public void setChanged(boolean newValue)
    {//ignore, since the content in this panel is read-only
    }

    /**
     * return the open action inherited with this client.
     */
    public Action getDefaultOpenAction()
    {
    	ContextManager contextManager = ContextManager.getContextManager();
        Action openAction = null;

        //do not support open action here.

//		if (contextManager != null)
//		{//contextManager is not null implies this panel is fully displayed;
//			//on the flip side, if it is null, it implies it is under certain construction.
//			openAction = contextManager.getDefinedAction(ActionConstants.OPEN_HL7_V3_MESSAGE);
//		}
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
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
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
