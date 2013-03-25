/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.ui.message;

import gov.nih.nci.cbiit.cmts.transform.TransformationObserver;

import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.ProgressMonitor;

/**
 * Progress dialog for transformation process
 *  
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-09 19:04:17 $
 *
 */
public class TransformationProgressDialog extends ProgressMonitor implements TransformationObserver
{
	private int messageCount; 

	public static final String DEFAULT_OBSERVER_DIALOG_TITLE="";//"Transfomation Progress";

	public TransformationProgressDialog(Component arg0, boolean dialogType)
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
		System.out.println("TransformationProgressDialog.setMessageCount():"+count);
		messageCount=count;
		this.setNote("Total number of message:"+count);
		setMaximum(count);
		
	}
}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 */
