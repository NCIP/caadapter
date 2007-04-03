/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/message/ValidationMessagePane.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.common.message;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.validation.ValidationMessageUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * This class defines a message panel to display validation and/or other messages.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/message/ValidationMessagePane.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $";

	private JPanel navigationPanel = null;
	private JPanel levelPanel = null;
    private JPanel messagePanel = null;
    private JPanel messageDisplayingPanel = null;
    private JComboBox levelComboBox = null;
    //private JButton saveButton = null;
    //private JButton printButton = null;

    private JTable messageTable;
    private JTextArea messageDisplaying;
    //private JScrollPane scrollPane = null;
    private JSplitPane splitPane;
    private DefaultMessageTableModel tableModel;

	private boolean comboBoxLoading = false;
	private ValidatorResults results = null;

	private boolean displayPopupConfirmationMessage = true;
	private String confirmationMessage = "";

	private JPanel confirmationMessagePanel;
	private JLabel confirmationMessageLabel;
	private JLabel confirmationMessageField;

    private SaveAsValidateMessageAction saveAction = null;
    private PrintingValidateMessageAction printAction = null;
    private int selectedRow = -1;
     
    private boolean startedTag = false;

    /**
	 * Creates a new <code>JPanel</code> with a double buffer
	 * and a flow layout.
	 */
	public ValidationMessagePane()
	{
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

        saveAction = new SaveAsValidateMessageAction(this);
        JButton saveButton = new JButton(saveAction);
        printAction = new PrintingValidateMessageAction(this);
        JButton printButton = new JButton(printAction);

        levelPanel.add(levelComboBox);
        levelPanel.add(saveButton);
        levelPanel.add(printButton);

        navigationPanel.add(levelPanel, BorderLayout.NORTH);

		confirmationMessageLabel = new JLabel("Confirmation Message: ");
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
		messageTable = new JTable(tableModel);
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
        String st = tableModel.getValueAt(sRow, 0).toString();
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
        messageDisplayingPanel = new JPanel(new BorderLayout());
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
		messageDisplayingPanel.setMinimumSize(new Dimension(30, 30));

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

	/**
	 * Call this function to present a traversable view to display all messages in the results based on level.
	 * @param results
	 */
	public void setValidatorResults(ValidatorResults results)
	{
		//clear message display
		tableModel.setMessageList(new ArrayList());

//		System.out.println("navigationPanel is visible? '" + navigationPanel.isVisible() + "'");
		this.levelPanel.setVisible(true);
		this.results = results;
		//clear confirmationMessage value.
		this.confirmationMessage = "";
		confirmationMessageField.setText("");
		confirmationMessagePanel.setVisible(false);
//		confirmationMessageLabel.setVisible();
		this.comboBoxLoading = true;
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
//		levelComboBox.addItem();
		this.comboBoxLoading = false;
		if(size>0)
		{//default to select the first
			this.levelComboBox.setSelectedIndex(0);
			String statMsg = ValidationMessageUtils.generateStatMessage(results);
			StringBuffer buf = new StringBuffer("Validation process completed, but received ");
			buf.append(statMsg + ".");
//			buf.append(".\nPlease refer to the message area for received validation messages.");
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

//		setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3), (int) (Config.FRAME_DEFAULT_HEIGHT / 2)));
//		repaint();
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
			    java.util.List messageList = results.getMessages((ValidatorResult.Level) selected);
			    tableModel.setMessageList(messageList);
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
