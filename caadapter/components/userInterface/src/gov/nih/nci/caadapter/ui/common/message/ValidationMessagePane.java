/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.message;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.validation.ValidationMessageUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JSplitPane;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines a message panel to display validation and/or other messages.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.7 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class ValidationMessagePane extends JPanel implements ActionListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: ValidationMessagePane.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/message/ValidationMessagePane.java,v 1.7 2008-06-09 19:53:51 phadkes Exp $";

	private JPanel navigationPanel = null;
	private JPanel levelPanel = null;
    private JPanel messagePanel = null;
    private JComboBox levelComboBox = null;

    private JTextArea messageDisplaying;
    private JSplitPane splitPane;
    private DefaultMessageTableModel tableModel;

	private boolean comboBoxLoading = false;
	private ValidatorResults results = null;

	private boolean displayPopupConfirmationMessage = true;
	private String confirmationMessage = "";

	private JPanel confirmationMessagePanel;
	private JLabel confirmationMessageField;

    private int selectedRow = -1;
    private boolean startedTag = false;
    private boolean displayValidatedElement=false;
    private JTextField elementField;

    /**
	 * Creates a new <code>JPanel</code> with a double buffer
	 * and a flow layout.
	 */
	public ValidationMessagePane()
	{
		this(false);
	}

	public ValidationMessagePane(boolean displayObject)
	{
		displayValidatedElement=displayObject;
		initialize();

	}
	private void initialize()
	{
		setLayout(new BorderLayout());
        messagePanel = new JPanel(new BorderLayout());
        navigationPanel = new JPanel(new BorderLayout());//new FlowLayout(FlowLayout.LEADING));
		levelPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JLabel label = new JLabel("Message Level:");
		levelPanel.add(label);
		levelComboBox = new JComboBox();
		levelComboBox.addActionListener(this);

		SaveAsValidateMessageAction saveAction = new SaveAsValidateMessageAction(this);
        JButton saveButton = new JButton(saveAction);
        PrintingValidateMessageAction printAction = new PrintingValidateMessageAction(this);
        JButton printButton = new JButton(printAction);

        levelPanel.add(levelComboBox);
        levelPanel.add(saveButton);
        levelPanel.add(printButton);
        if (displayValidatedElement)
        {
        	JLabel labelElement = new JLabel("Validated Element:");
    		levelPanel.add(labelElement);
    		elementField = new JTextField();
    		levelPanel.add(elementField);
        }

        navigationPanel.add(levelPanel, BorderLayout.NORTH);

		JLabel confirmationMessageLabel = new JLabel("Confirmation Message: ");
		confirmationMessageField = new JLabel();
//		confirmationMessageField.setEnabled(false);
		confirmationMessagePanel = new JPanel(new BorderLayout());
		confirmationMessagePanel.add(confirmationMessageLabel, BorderLayout.WEST);
		confirmationMessagePanel.add(confirmationMessageField, BorderLayout.CENTER);
		navigationPanel.add(confirmationMessagePanel, BorderLayout.SOUTH);
		//hide by default because of displayPopupConfirmationMessage settings.
		confirmationMessagePanel.setVisible(false);

		messagePanel.add(navigationPanel, BorderLayout.NORTH);
		navigationPanel.setVisible(false);
		tableModel = new DefaultMessageTableModel();
		JTable messageTable = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(messageTable);
		messagePanel.add(scrollPane, BorderLayout.CENTER);
		messagePanel.setBorder(BorderFactory.createTitledBorder("Validation Messages"));
        add(messagePanel, BorderLayout.CENTER);

        // Following part was inserted by umkis for fixing defect# 223 on 12/05/2005
        // When clicking a message of messageTable, a screen will show up for displaying the whole body of the message.
        messageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ListSelectionModel rowSM = messageTable.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                if (e.getValueIsAdjusting()) return;

                ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if (lsm.isSelectionEmpty())
                {
                    if (selectedRow >= 0) displaySelectedMessage(selectedRow);
                    //System.out.println("No rows are selected.");
                }
                else
                {
                    if (!startedTag)
                    {
                        constructSplitPane();
                        startedTag = true;
                    }
                    if (!messageDisplaying.isVisible()) messageDisplaying.setVisible(true);
                    //if (!messageDisplayLabel.isVisible()) messageDisplayLabel.setVisible(true);
                    if (messageDisplaying.isEditable()) messageDisplaying.setEditable(false);
                    selectedRow = lsm.getMinSelectionIndex();
                    displaySelectedMessage(selectedRow);
                }
            }
        });
        //---------------------------------------------------------------------------

    }
    public void displaySelectedMessage(int sRow)
	{
        //JOptionPane.showMessageDialog(this, tableModel.getValueAt(selectedRow, 0).toString(), "Validation Message", JOptionPane.INFORMATION_MESSAGE);
        //new HTMLViewer("text", tableModel.getValueAt(sRow, 0).toString(), -100, 200);
        String st = tableModel.getValueAt(sRow, 1).toString();
        if (st.startsWith("##IndexOutOfBoundsException"))
        {
            remove(splitPane);
            add(messagePanel, BorderLayout.CENTER);
            startedTag = false;
            updateUI();
        }
        else messageDisplaying.setText(st);
    }
    private void constructSplitPane()
    {
        remove(messagePanel);
        JPanel messageDisplayingPanel = new JPanel(new BorderLayout());
        messageDisplaying = new JTextArea();
		messageDisplaying.setLineWrap(true);
		messageDisplaying.setWrapStyleWord(true);
		JScrollPane js = new JScrollPane(messageDisplaying);
        js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        js.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //messageDisplayLabel = new JLabel("Full content of selected message");
        //messageDisplayingPanel.add(messageDisplayLabel, BorderLayout.NORTH);
        messageDisplayingPanel.add(js, BorderLayout.CENTER);
        messageDisplayingPanel.setBorder(BorderFactory.createTitledBorder("Full content of the selected message"));


        //messagePanel.setPreferredSize(new Dimension(240, 530));
        //        messagePanel.setMinimumSize(new Dimension(10, 10));


        //        messageDisplayingPanel.setPreferredSize(new Dimension(550, 530));
		messageDisplayingPanel.setMinimumSize(new Dimension(30, 100));

		//Put the editor pane and the text pane in a split pane.
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
											  messagePanel,
											  messageDisplayingPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.8);

        add(splitPane, BorderLayout.CENTER);

        messageDisplaying.setVisible(false);
        //messageDisplayLabel.setVisible(false);

        this.updateUI();
    }
    /**
	 * Return if the message dialog would display confirmation message in a popup dialog.
	 * @return if the message dialog would display confirmation message in a popup dialog.
	 */
	public boolean isDisplayPopupConfirmationMessage()
	{
		return displayPopupConfirmationMessage;
	}

	/**
	 * Turn on or off the display message on popup.
	 * @param displayPopupConfirmationMessage
	 */
	public void setDisplayPopupConfirmationMessage(boolean displayPopupConfirmationMessage)
	{
		this.displayPopupConfirmationMessage = displayPopupConfirmationMessage;
	}

	/**
	 * Return the validation results.
	 * @return the validation results.
	 */
	public ValidatorResults getValidatorResults()
	{
		return this.results;
	}

	public ValidatorResult.Level getSelectedMessageLevel()
	{
		return ( ValidatorResult.Level )levelComboBox.getSelectedItem();
	}
	/**
	 * If validatation being applied with different elements of the same
	 * scope, set the elment being validated
	 * @param validateObject
	 */
	public void setValidatedElement(Object validateObject)
	{
		if (validateObject!=null)
			elementField.setText(validateObject.toString());
	}
	/**
	 * Call this function to present a traversable view to display all messages in the results based on level.
	 * @param results
	 */
	public void setValidatorResults(ValidatorResults results)
	{
		//clear message display
		tableModel.setMessageList(new ArrayList());
		levelPanel.setVisible(true);
		this.results = results;
		//clear confirmationMessage value.
		confirmationMessage = "";
		confirmationMessageField.setText("");
		confirmationMessagePanel.setVisible(false);

		comboBoxLoading = true;
		levelComboBox.setEnabled(true);
		levelComboBox.removeAllItems();
		navigationPanel.setVisible(false);

		if (results == null)
		{
			levelPanel.setVisible(false);
			this.results = results;
			return;
		}

		navigationPanel.setVisible(true);
		java.util.List levelList = results.getLevels();
		int size = levelList.size();
		for(int i=0; i<size; i++)
		{
			this.levelComboBox.addItem(levelList.get(i));
		}

		this.comboBoxLoading = false;
		if(size>0)
		{//default to select the first
			this.levelComboBox.setSelectedIndex(0);
			String statMsg = ValidationMessageUtils.generateStatMessage(results);
			StringBuffer buf = new StringBuffer("Validation process completed, but received ");
			buf.append(statMsg + ".");
			this.confirmationMessage = buf.toString();
		}
		else
		{//no need to enable level combo
			levelPanel.setVisible(false);
			this.confirmationMessage = "Validation process completed successfully with no message received.";
		}

		if (isDisplayPopupConfirmationMessage())
		{
			JOptionPane.showMessageDialog(this, confirmationMessage);
		}
//		else
//		{
			confirmationMessagePanel.setVisible(true);
			if(size>0)
			{
				confirmationMessageField.setForeground(Color.RED);
			}
			else
			{
				confirmationMessageField.setForeground(Color.BLUE);
			}
			confirmationMessageField.setText(confirmationMessage);
//		}
			Component parentCom=this.getParent();
			if (parentCom instanceof JTabbedPane)
			{
				JTabbedPane parentTab=(JTabbedPane)parentCom;
				parentTab.setSelectedComponent(this);
			}

	}

	/**
	 * Call this function to display only a list of messages to the UI.
	 * Call setValidatorResults() instead, if you'd like to display all messages in ths results.
	 * @param messageList
	 */
	public void setMessageList(java.util.List messageList)
	{
		navigationPanel.setVisible(false);
		tableModel.setMessageList(messageList);
		setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3), (int) (Config.FRAME_DEFAULT_HEIGHT / 2)));
		repaint();
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if(source==levelComboBox)
		{//if comboBox is NOT oading
            if (!comboBoxLoading)
            {
                Object selected = levelComboBox.getSelectedItem();
//			    java.util.List messageList = results.getMessages((ValidatorResult.Level) selected);
//			    tableModel.setMessageList(messageList);
			    tableModel.setMessageList(results.getValidationResult((ValidatorResult.Level) selected));
            }
            if (startedTag)
            {
                remove(splitPane);
                add(messagePanel, BorderLayout.CENTER);
                updateUI();
                startedTag = false;
            }
        }

    }

	/**
	 * Return the confirmation message.
	 * @return the confirmation message.
	 */
	public String getConfirmationMessage()
	{
		return confirmationMessage;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.6  2007/07/31 18:42:05  wangeug
 * HISTORY      : display validation result with level and message text
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/07/31 17:44:25  wangeug
 * HISTORY      : resolve issues with preliminary test of release 4.0
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/07/12 14:38:21  wangeug
 * HISTORY      : Add "ALL" as option in the validation message type dropdown so you can see all types of validation messages
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/07/11 17:56:11  wangeug
 * HISTORY      : enable HSM validation at any level of tree hierarchy
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/10 17:35:04  wangeug
 * HISTORY      : update code:reset propertyPane/validationPane with JTabbedPane
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.23  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.22  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.21  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.20  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/12/19 16:37:15  jiangsc
 * HISTORY      : Created dumpAllValidatorResultsToFile() function to temporarily dump validator results to files.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/12/15 19:06:14  umkis
 * HISTORY      : Defact# 235  remove IndexOutOfBoundsException
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/12/15 18:41:56  umkis
 * HISTORY      : Defact# 235  remove IndexOutOfBoundsException
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/12/15 16:43:54  umkis
 * HISTORY      : Defact# 235  remove IndexOutOfBoundsException
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/12/15 16:10:39  umkis
 * HISTORY      : Defact# 235  remove IndexOutOfBoundsException
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/12/15 05:28:17  umkis
 * HISTORY      : only when clicking on a validation message, the displaying text box can be seen.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/06 16:14:59  umkis
 * HISTORY      : defect# 223
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/06 00:06:05  umkis
 * HISTORY      : defect# 223
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/05 16:01:51  umkis
 * HISTORY      : defect# 227 the size of the JTextFields was made wider for in put parameters.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/05 15:55:42  umkis
 * HISTORY      : defect# 227 the size of the JTextFields was made wider for in put parameters.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/05 15:33:09  umkis
 * HISTORY      : defect# 223, When clicking on one message line of the message table, message viewing screen shows up and the whole body of the message can be displayed.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/11/29 16:23:53  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/14 19:55:51  jiangsc
 * HISTORY      : Implementing UI enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/02 19:31:49  jiangsc
 * HISTORY      : Implemented Context sensitive condition on the Save and Print button.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/11/02 05:37:38  umkis
 * HISTORY      : defect# 172
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/31 23:26:05  jiangsc
 * HISTORY      : Minor update due to font change
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/10/21 18:26:17  jiangsc
 * HISTORY      : Validation Class name changes.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/10/07 19:09:28  jiangsc
 * HISTORY      : Enhanced the Look and Feel of Validation Confirmation Messages
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/07 18:40:17  jiangsc
 * HISTORY      : Enhanced the Look and Feel of Validation and Properties.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/05 22:27:37  jiangsc
 * HISTORY      : Update message logic
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/04 20:51:33  jiangsc
 * HISTORY      : Validation enhancement.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/29 19:46:41  jiangsc
 * HISTORY      : minor update
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/26 20:57:46  jiangsc
 * HISTORY      : Validation Display
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/26 16:31:06  jiangsc
 * HISTORY      : Validation Display
 * HISTORY      :
 */
