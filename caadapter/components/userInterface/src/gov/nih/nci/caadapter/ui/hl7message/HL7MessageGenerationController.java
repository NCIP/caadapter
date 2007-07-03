/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/HL7MessageGenerationController.java,v 1.1 2007-07-03 19:33:17 wangeug Exp $
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

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralTask;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.util.SwingWorker;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.map.TransformationResult;
import gov.nih.nci.caadapter.hl7.map.TransformationServiceCsvToHL7V3;
import gov.nih.nci.caadapter.ui.hl7message.actions.RegenerateHL7V3MessageAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * This class coordinates HL7MessagePanel and other resources to oversee the HL7 v3 message generation, which may potentially take
 * long time.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-07-03 19:33:17 $
 */
public class HL7MessageGenerationController implements Observer
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: HL7MessageGenerationController.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/HL7MessageGenerationController.java,v 1.1 2007-07-03 19:33:17 wangeug Exp $";

	private HL7MessagePanel hl7Panel;
	private File dataFile;
	private File mapFile;
	private List messageList;
	private boolean taskComplete;

	private ProgressMonitor progressMonitor;
	private GeneralTask longTask;

	private Timer timer;

	public final static int ONE_SECOND = 1000;

	public HL7MessageGenerationController(HL7MessagePanel hl7Panel, File dataFile, File mapFile)
	{
		this.hl7Panel = hl7Panel;
		this.dataFile = dataFile;
		this.mapFile = mapFile;
		longTask = new GeneralTask();
		//Create a timer.
		timer = new Timer(ONE_SECOND, new TimerListener());
	}

	/**
	 * The actionPerformed method in this class
	 * is called each time the Timer "goes off".
	 */
	class TimerListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			int nextValue = (int) longTask.getCurrent();
//			System.out.println("\nTimerListener: current is'" + nextValue + "'");
//			System.out.println("Is ProgressMonitor visible?" + progressMonitor.getAccessibleContext().getAccessibleValue());
			progressMonitor.setProgress(nextValue);
			String s = longTask.getStatMessage();
//			System.out.println("what is s?'" + s + "'");
			if (s != null)
			{
				progressMonitor.setNote(s);
			}
			if (progressMonitor.isCanceled() || longTask.isDone() || longTask.isCanceled())
			{
//				System.out.println("progressMonitor.isCanceled()?" + progressMonitor.isCanceled() + ",longTask.isDone()?" + longTask.isDone());
				taskComplete = true;
				progressMonitor.close();
				longTask.stop();
				Toolkit.getDefaultToolkit().beep();
				timer.stop();
				if (longTask.isDone())
				{
//					taskOutput.append("Task completed." + newline);
//					System.out.println("task complets, now to start to update the message.");
					final ValidatorResults validatorResults = new ValidatorResults();
					validatorResults.addValidatorResults(assembleValidatorResults());
					if(validatorResults.hasFatal())
					{
						//just to borrow the utility to report the issues
						RegenerateHL7V3MessageAction action = new RegenerateHL7V3MessageAction(hl7Panel);
						action.handleValidatorResults(validatorResults);
					}
					else
					{
						JOptionPane.showMessageDialog(hl7Panel, "The process is complete.", "Information", JOptionPane.INFORMATION_MESSAGE);
						hl7Panel.setV3MessageResultList(messageList);
					}
				}
				else
				{
//					taskOutput.append("Task canceled." + newline);
					JOptionPane.showMessageDialog(hl7Panel, "The process is being cancelled.", "Information", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}

	private ValidatorResults assembleValidatorResults()
	{
		ValidatorResults validatorResults = new ValidatorResults();
		int size = messageList == null ? 0 : messageList.size();
		for (int i = 0; i < size; i++)
		{
			TransformationResult oneResult = (TransformationResult) messageList.get(i);
			if (oneResult != null)
			{
				ValidatorResults oneValidatorResults = oneResult.getValidatorResults();
				validatorResults.addValidatorResults(oneValidatorResults);

				//temporary dump the validator result to a file.
//				String dataFileName = dataFile.getName();
//				String mapDataFileName = mapFileName.getName();
//				//remove the appendics;
//				dataFileName = dataFileName.substring(0, dataFileName.lastIndexOf("."));
//				mapDataFileName = mapDataFileName.substring(0, mapDataFileName.lastIndexOf("."));
//				String fileName = FileUtil.getExamplesDirPath() + File.separator + "ValidatorResultsDump_on_" + dataFileName + "_and_" + mapDataFileName + ".txt";
//				String titleMessage = "Message " + (i + 1) + " has: " + ValidationMessageUtils.generateStatMessage(oneValidatorResults);
//				dumpAllValidatorResultsToFile(fileName, titleMessage, i!=0, oneValidatorResults);
			}
		}
		return validatorResults;
	}

	public ValidatorResults process() throws Exception
	{
		final TransformationServiceCsvToHL7V3 driver = new TransformationServiceCsvToHL7V3(mapFile, dataFile);
//		final TransformationServiceCsvToHL7V3 driver = new DummyTransformationServiceTest(mapFileName, dataFile);
		final ValidatorResults validatorResults = new ValidatorResults();

		TransformationResult estimateResult = null;
		messageList = new ArrayList();
		try
		{
			GeneralUtilities.setCursorWaiting(hl7Panel.getRootContainer());
			estimateResult = driver.getEstimate();
		}
		finally
		{
			GeneralUtilities.setCursorDefault(hl7Panel.getRootContainer());
		}
		
		if (estimateResult != null)
		{
			ValidatorResults localValidatorResults = estimateResult.getValidatorResults();
			if (!localValidatorResults.isValid())
			{//no further operation is necessary
				validatorResults.addValidatorResults(localValidatorResults);
				this.messageList.add(estimateResult);
				return validatorResults;
			}
			//is valid and proceed
		}
		else
		{
			Message message = MessageResources.getMessage("GEN0", new Object[]{"TransformationResult from estimate work is null."});
			validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, message));
			return validatorResults;
		}
		
		final long estimatedLengthOfTask = estimateResult.getLogicalRecordNumber();//Config.DEFAULT_SECOND_PER_RECORD_LENGTH;
		taskComplete = false;

		if(estimatedLengthOfTask<=Config.DEFAULT_SYNC_VS_ASYNC_THRESHOLD)
		{
			validatorResults.addValidatorResults(doSynchronizedProcess(driver));
			validatorResults.addValidatorResults(assembleValidatorResults());
		}
		else
		{//go with asynchronized option if and only if the time is long
			doAsynchronizedProcess(driver, estimatedLengthOfTask);
		}

//		System.out.println("About to report now.");
		return validatorResults;
	}

	public List getMessageList()
	{
		return messageList;
	}

	private ValidatorResults doSynchronizedProcess(final TransformationServiceCsvToHL7V3 driver)
	{
		ValidatorResults validatorResults = new ValidatorResults();
		try
		{
			GeneralUtilities.setCursorWaiting(hl7Panel.getRootContainer());
			messageList = driver.process(longTask);
			GeneralUtilities.setCursorDefault(hl7Panel.getRootContainer());
		}
		catch(Throwable t)
		{
			GeneralUtilities.setCursorDefault(hl7Panel.getRootContainer());
			Message msg = GeneralUtilities.convertToGeneralMessage(t);
			validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
		}
		finally
		{
			GeneralUtilities.setCursorDefault(hl7Panel.getRootContainer());
		}	
		return validatorResults;
	}

	private void doAsynchronizedProcess(final TransformationServiceCsvToHL7V3 driver, final long estimateLengthOfTask)
	{
		boolean toShowInMinutes = estimateLengthOfTask > Config.DEFAULT_THRESHOLD_FOR_SECOND_VS_MINUTE;
		String estimatedTimeStr= null;
		int oneStep = 1;
		if(toShowInMinutes)
		{
			estimatedTimeStr = estimateLengthOfTask / 60 + " minutes";
			oneStep = (int) estimateLengthOfTask / 100;
		}
		else
		{
			estimatedTimeStr = estimateLengthOfTask + " seconds";
			oneStep = 1;
		}

		boolean toShowUserConfirmation = estimateLengthOfTask > Config.DEFAULT_TRANSFORMATION_SERVICE_THRESHOLD_FOR_USER_CONFIRMATION;
		int userChoice = JOptionPane.YES_OPTION;
		if(toShowUserConfirmation)
		{
			userChoice = JOptionPane.showConfirmDialog(hl7Panel, "It will take about " + estimatedTimeStr + " for transformation service to generate the HL7 v3 messages.\n\n Start the process now?", "Question", JOptionPane.YES_NO_OPTION);
		}
		if (userChoice != JOptionPane.YES_OPTION)
		{
			return;
		}
		longTask.setLengthOfTask(estimateLengthOfTask);
//		System.out.println("The estimate length is '" + (int) longTask.getLengthOfTask() + "'.");
//		progressMonitor = new ProgressMonitor(hl7Panel, "Running a Long Task", "", 0, (int) longTask.getLengthOfTask());
//		progressMonitor.setProgress(0);
//		progressMonitor.setMillisToDecideToPopup(ONE_SECOND / 100);
//		timer.start();
		int lengthOfTask = (int) longTask.getLengthOfTask();
		progressMonitor = new ProgressMonitor(hl7Panel, "Running a Long Task", "", 0, lengthOfTask);
		progressMonitor.setMillisToDecideToPopup(0);
		progressMonitor.setMillisToPopup(0);
		progressMonitor.setProgress(oneStep);
		timer.start();
		SwingWorker worker = new SwingWorker()
		{
			public Object construct()
			{
				longTask.addObserver(HL7MessageGenerationController.this);
				messageList = driver.process(longTask);
				longTask.deleteObserver(HL7MessageGenerationController.this);
				return null;
			}

			/**
			 * Called on the event dispatching thread (not on the worker thread)
			 * after the <code>construct</code> method has returned.
			 */
//			public void finished()
//			{
//				Thread timerThread = new Thread(new Runnable(){
//					public void run()
//					{
//						longTask.addObserver(HL7MessageGenerationController.this);
//						messageList = driver.process(longTask);
//						longTask.deleteObserver(HL7MessageGenerationController.this);
//					}
//				});
//			}
		};
		longTask.start(worker);
	}


	/**
	 * This method is called whenever the observed object is changed. An
	 * application calls an <tt>Observable</tt> object's
	 * <code>notifyObservers</code> method to have all the object's
	 * observers notified of the change.
	 *
	 * @param o   the observable object.
	 * @param arg an argument passed to the <code>notifyObservers</code>
	 *            method.
	 */
	public void update(Observable o, Object arg)
	{
		if(taskComplete || longTask.isDone() || longTask.isCanceled())
		{
			return;
		}
		if(arg instanceof TransformationResult)
		{
			messageList.add(arg);
		}
		progressMonitor.setProgress((int)longTask.getCurrent());
		String s = longTask.getStatMessage();
		if (s != null)
		{
			progressMonitor.setNote(s);
		}
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.14  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/07/31 18:21:53  jiangsc
 * HISTORY      : Enhanced ProgressBar functionality to request user to confirm the start of the process.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/07/27 19:28:10  jiangsc
 * HISTORY      : Wording change
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/06/13 18:12:12  jiangsc
 * HISTORY      : Upgraded to catch Throwable instead of Exception.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/23 16:06:04  jiangsc
 * HISTORY      : Fix to defect 243
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/19 16:37:15  jiangsc
 * HISTORY      : Created dumpAllValidatorResultsToFile() function to temporarily dump validator results to files.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/12/16 17:04:17  jiangsc
 * HISTORY      : Checked in Enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/12/16 16:27:11  jiangsc
 * HISTORY      : Minor update
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/12/14 23:09:24  jiangsc
 * HISTORY      : Updated functionality.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 */
