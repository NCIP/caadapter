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
import javax.swing.ProgressMonitor;

public class HL7TransformationProgressDialog extends ProgressMonitor implements TransformationObserver
{
	private int messageCount; 
	private boolean requestCanceled=false;
	private boolean tranformationComplete=false;

	public static final String DEFAULT_OBSERVER_DIALOG_TITLE="";//"Transfomation Progress";

	public HL7TransformationProgressDialog(Frame arg0, boolean dialogType)
			throws HeadlessException {
		super(arg0,DEFAULT_OBSERVER_DIALOG_TITLE,"Progress",0,1);
		setMillisToDecideToPopup(0);
		setMillisToPopup(0);
		setProgress(1);
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
			setTranformationComplete(true);
		}
			String msgText="Generating message : "+ completionCnt +" of "+ this.messageCount;
			System.out.println("HL7TransformationProgressDialog.progressUpdate():"+msgText);
			if (messageCount>0)
				setNote(msgText);
			
			this.setProgress(completionCnt);
			System.out
					.println("HL7TransformationProgressDialog.progressUpdate()..getNote():"+getNote());
	}

	public void setMessageCount(int count) {
		// TODO Auto-generated method stub
		System.out.println("HL7TransformationProgressDialog.setMessageCount():"+count);
		messageCount=count;
		this.setNote("Total number of message:"+count);
		setMaximum(count);
		
	}

	
	public boolean isTranformationComplete() {
		return tranformationComplete;
	}

	public void setTranformationComplete(boolean tranformationComplete) {
		this.tranformationComplete = tranformationComplete;
	}
}
