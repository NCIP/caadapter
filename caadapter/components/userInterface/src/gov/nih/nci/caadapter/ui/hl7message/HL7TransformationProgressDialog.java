package gov.nih.nci.caadapter.ui.hl7message;

import gov.nih.nci.caadapter.hl7.transformation.TransformationObserver;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class HL7TransformationProgressDialog extends JDialog implements TransformationObserver,ActionListener
{
	private int messageCount; 
	private boolean requestCanceled=false;
	private JLabel progressMessage;
	private boolean tranformationComplete=false;
	private static final String OK_COMMAND = "OK";
	private static final String CANCEL_COMMAND = "Cancel";

	public static final String DEFAULT_OBSERVER_DIALOG_TITLE="Transfomation Progress";
	public HL7TransformationProgressDialog() throws HeadlessException {
		// TODO Auto-generated constructor stub
	}

	public HL7TransformationProgressDialog(Frame arg0, boolean dialogType)
			throws HeadlessException {
		super(arg0, DEFAULT_OBSERVER_DIALOG_TITLE, dialogType);
		initUI();
//		setVisible(true);
	}

	
	private void initUI()
	{
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		this.setSize(300,150);
		progressMessage=new JLabel("Message Transformation: Start");		
		contentPane.add(progressMessage, BorderLayout.CENTER);
		contentPane.add(setButtonPanel(), BorderLayout.SOUTH);
	}
	
	private JPanel setButtonPanel()
	{
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));//new BorderLayout());
//		JButton okButton = new JButton(OK_COMMAND);
//		okButton.setMnemonic('O');
//		okButton.addActionListener(this);
		JButton cancelButton = new JButton(CANCEL_COMMAND);
		cancelButton.setMnemonic('C');
		cancelButton.addActionListener(this);
		JPanel tempPanel = new JPanel(new GridLayout(1, 2));
//		tempPanel.add(okButton);
		tempPanel.add(cancelButton);
		buttonPanel.add(tempPanel);//, BorderLayout.EAST);
		return buttonPanel;
	}

	public boolean isRequestCancelled() {
		// TODO Auto-generated method stub
		return requestCanceled;
	}

	public boolean isRequestValid() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isServiceReady() {
		// TODO Auto-generated method stub
		return true;
	}

	public void progressUpdate(int completionPercent) {
		// TODO Auto-generated method stub
		//the completionPercent start from 0:the first message
		int completionCnt=completionPercent+1;
		if (completionCnt==this.messageCount)
		{
			this.setVisible(false);
			
			setTranformationComplete(true);
			this.transferFocus();
			ActionEvent ac=new ActionEvent(this,ActionEvent.META_MASK, OK_COMMAND);
			this.actionPerformed(ac);
		}
		else
		{		
			String msgText="Transformation progress: "+ completionCnt +" of "+ this.messageCount;
			System.out.println("HL7TransformationProgressDialog.progressUpdate():"+msgText);
			progressMessage.setText(msgText);
			this.repaint();
		}
		
	}

	public void setMessageCount(int count) {
		// TODO Auto-generated method stub
		System.out.println("HL7TransformationProgressDialog.setMessageCount():"+count);
		messageCount=count;
		
	}

	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
		String command =arg0.getActionCommand();
		if (OK_COMMAND.equals(command))
		{
			System.out
					.println("HL7TransformationProgressDialog.actionPerformed()..OK clicked");
			this.dispose();
		}
		else
		{
			System.out
			.println("HL7TransformationProgressDialog.actionPerformed()..Cancell clicked");
			this.requestCanceled=true;
			dispose();
		}

	}

	public boolean isTranformationComplete() {
		return tranformationComplete;
	}

	public void setTranformationComplete(boolean tranformationComplete) {
		this.tranformationComplete = tranformationComplete;
	}
}
