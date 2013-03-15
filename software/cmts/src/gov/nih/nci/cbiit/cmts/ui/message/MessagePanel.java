/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.cbiit.cmts.ui.message;


import gov.nih.nci.cbiit.cmts.ui.actions.DefaultCloseAction;
import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmts.ui.common.ContextManager;
import gov.nih.nci.cbiit.cmts.ui.common.ContextManagerClient;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.common.MenuConstants;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrame;

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
public class MessagePanel extends JPanel implements ActionListener, ContextManagerClient
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

    private JTextField mapFileNameField;
    private JTextField dataFileNameField;

    private java.util.List <Object> messageList;
    private JScrollPane scrollPane = null;
    private ValidationMessagePane validationMessagePane = null;
    private boolean dataChanged=false;
    private int messageFileType=0;
    public static int MESSAGE_PANEL_HL7=0;
    public static int MESSAGE_PANEL_CSV=1;
	public MessagePanel()
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
//		currentMessageField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		navigationPanel.add(currentMessageField);

		navigationPanel.add(nextButton);
		nextButton.addActionListener(this);

//		RegenerateHL7V3MessageAction regenerateAction = new RegenerateHL7V3MessageAction(this);
		JButton regenerateButton = new JButton("Regenerate");//regenerateAction);
		navigationPanel.add(regenerateButton);
		leftPanel.add(navigationPanel, BorderLayout.NORTH);

		JPanel totalMessagePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		totalNumberOfMessageField.setText("0");
		//so that it will be the same size as currentMessageField
		totalNumberOfMessageField.setPreferredSize(previousButton.getPreferredSize());
		totalNumberOfMessageField.setEditable(false);
//		totalNumberOfMessageField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
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
//		dataFileNameField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		dataFileNameField.setPreferredSize(fieldDimension);
		fieldsPanel.add(dataFileNameField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		posY++;
		JLabel mapFileNameLabel = new JLabel("Map Specification:");
		fieldsPanel.add(mapFileNameLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		mapFileNameField = new JTextField();
		fieldDimension = new Dimension(mapFileNameLabel.getPreferredSize().width, mapFileNameField.getPreferredSize().height);
		mapFileNameField.setEditable(false);
//		mapFileNameField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
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
//		scrollPane.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3), (int) (Config.FRAME_DEFAULT_HEIGHT / 2)));
		splitPane.setTopComponent(scrollPane);

		validationMessagePane = new ValidationMessagePane();
		//turn off the display as popup dialog but display it at other location.
		validationMessagePane.setDisplayPopupConfirmationMessage(false);
		validationMessagePane.setValidatorResults(null);
//		validationMessagePane.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3), (int) (Config.FRAME_DEFAULT_HEIGHT / 10)));

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



    public boolean setSaveFile(File saveFile, boolean refresh) throws Exception
    {
    	System.out.println("HL7MessagePanel.setSaveFile()..refresh:"+refresh);
//        if (super.setSaveFile(saveFile))//!GeneralUtilities.areEqual(this.saveFile, saveFile))
//        {
//            if (refresh)
//            {
//                HL7V3MessageLoader loader = new HL7V3MessageLoader();
//                FileReader fr = null;
//                BufferedReader br = null;
//                try
//                {
//                    fr = new FileReader(saveFile);
//                    br = new BufferedReader(fr);
//                    List<String> loaderResultList = loader.loadData(br);
//                    List<TransformationResult> messageList = new ArrayList<TransformationResult>();
//                    for (int i = 0; i < loaderResultList.size(); i++)
//                    {
//                        TransformationResult result = new TransformationResult(loaderResultList.get(i), new ValidatorResults());
//                        messageList.add(result);
//                    }
////                    setV3MessageResultList(messageList);
//                }
//                finally
//                {
//                    if (br != null)
//                    {
//                        br.close();
//                    }
//                }
//            }
//            return true;
//        }
//        else
//        {
//            return false;
//        }
    	return true;
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



    private void changeDisplay()
    {}

    public void setMessageText(String text)
    {
    	JTextArea outputMessageArea = new JTextArea(text);
        outputMessageArea.setEditable(false);
        scrollPane.getViewport().setView(outputMessageArea);
    }

    public Map getMenuItems(String menu_name)
	{
		Action action = null;
		Map <String, Action>actionMap = null;
		ContextManager contextManager = ContextManager.getContextManager();
		actionMap = contextManager.getClientMenuActions("MESSAGE", menu_name);
//		if (MenuConstants.FILE_MENU_NAME.equals(menu_name))
//		{
//			JRootPane rootPane = this.getRootPane();
//			if (rootPane == null){
//				//rootpane is not null implies this panel is fully displayed;
//				//on the flip side, if it is null, it implies it is under certain construction.
//				contextManager.enableAction(ActionConstants.NEW_MAP_FILE, true);
//				contextManager.enableAction(ActionConstants.OPEN_MAP_FILE, true);
//				contextManager.enableAction(ActionConstants.CLOSE, false);
//				contextManager.enableAction(ActionConstants.SAVE, false);
//				contextManager.enableAction(ActionConstants.SAVE_AS, false);
//			}else{
//				contextManager.enableAction(ActionConstants.CLOSE, true);
//				contextManager.enableAction(ActionConstants.SAVE, true);
//				contextManager.enableAction(ActionConstants.SAVE_AS, true);
//			}
//		}
//
//		//since the action depends on the panel instance,
//		//the old action instance should be removed
		if (actionMap!=null)
			contextManager.removeClientMenuAction("MESSAGE", menu_name, "");

		action = getDefaultCloseAction();
		contextManager.addClientMenuAction("MESSAGE", MenuConstants.FILE_MENU_NAME,ActionConstants.CLOSE, action);
		action.setEnabled(true);

		return contextManager.getClientMenuActions("MESSAGE", menu_name);
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


	public int getMessageFileType() {
		return messageFileType;
	}

	public void setMessageFileType(int messageFileType) {
		this.messageFileType = messageFileType;
	}

	public List<File> getAssociatedFileList() {
		// TODO Auto-generated method stub
		return null;
	}

	public Action getDefaultCloseAction() {
		// TODO Auto-generated method stub
//		Map actionMap = getMenuItems(MenuConstants.FILE_MENU_NAME);
//		Action closeAction = (Action) actionMap.get(ActionConstants.CLOSE);

		Action closeAction	=new DefaultCloseAction(MainFrame.getInstance());
		return closeAction;
	}

	public Action getDefaultOpenAction() {
		// TODO Auto-generated method stub
		return null;
	}

	public Action getDefaultSaveAction() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Action> getToolbarActionList() {
		// TODO Auto-generated method stub
		java.util.List<Action> actions = new ArrayList<Action>();
		actions.add(this.getDefaultCloseAction());
		return actions;
	}

	public void reload() throws Exception {
		// TODO Auto-generated method stub

	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2009/11/10 19:13:33  wangeug
 * HISTORY      : setup message panel
 * HISTORY      :
 *
 */
