/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.hl7message;

import java.awt.Component;
import java.awt.HeadlessException;

import gov.nih.nci.caadapter.hl7.transformation.TransformationObserver;
import javax.swing.ProgressMonitor;
/**
 * This class is the main entry point to display HL7V3 message panel.
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.7 $
 *          date        $Date: 2008-09-29 19:56:37 $
 */
public class HL7TransformationProgressDialog extends ProgressMonitor implements TransformationObserver
{
	private int messageCount;

	public static final String DEFAULT_OBSERVER_DIALOG_TITLE="";//"Transfomation Progress";

	public HL7TransformationProgressDialog(Component arg0, boolean dialogType)
			throws HeadlessException {
		super(arg0,DEFAULT_OBSERVER_DIALOG_TITLE,"Progress",0,1);
		setMillisToDecideToPopup(0);
		setMillisToPopup(0);
		setProgress(1);
	}

	public boolean isRequestCanceled() {
		// TODO Auto-generated method stub
		return this.isCanceled();
	}

	public void progressUpdate(int completionPercent) {
		// TODO Auto-generated method stub
		//the completionPercent start from 0:the first message
		int completionCnt=completionPercent+1;
		String msgTextStart="Generating message : ";
		String msgTextEnd="";
		if (messageCount>0)
		{
			msgTextEnd=completionCnt +" of "+ this.messageCount;
		}
		else
		{
			msgTextStart="Loading data ... ";
			switch (completionPercent)
			{
				case TRANSFORMATION_DATA_LOADING_START:
					msgTextEnd=" start";
				case TRANSFORMATION_DATA_LOADING_READ_MAPPING:
					msgTextEnd="read mapping";
				case TRANSFORMATION_DATA_LOADING_PARSER_MAPPING:
					msgTextEnd="parser mapping";
				case TRANSFORMATION_DATA_LOADING_READ_SOURCE:
					msgTextEnd="read source data";
				case TRANSFORMATION_DATA_LOADING_PARSER_SOURCE:
					msgTextEnd="parser source data";
				case TRANSFORMATION_DATA_LOADING_READ_CVS_META:
					msgTextEnd="parser source meta";
				case TRANSFORMATION_DATA_LOADING_READ_H3S_FILE:
					msgTextEnd="parser target meta";
				case TRANSFORMATION_DATA_LOADING_COUNT_MESSAGE:
					msgTextEnd="count message(s)";
			}
		}
		setNote(msgTextStart+msgTextEnd);
		this.setProgress(completionCnt);
	}

	public void setMessageCount(int count) {
		// TODO Auto-generated method stub
		System.out.println("HL7TransformationProgressDialog.setMessageCount():"+count);
		messageCount=count;
		this.setNote("Total number of message:"+count);
		setMaximum(count);

	}
}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 *
 * */
