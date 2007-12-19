/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/wizard/NewCSVPanelWizard.java,v 1.3 2007-12-19 22:33:50 schroedn Exp $
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


package gov.nih.nci.caadapter.ui.specification.csv.wizard;

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.csv.CSVMetaParserImpl;
import gov.nih.nci.caadapter.common.csv.CSVMetaResult;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVMetaImpl;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVSegmentMetaImpl;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;

/**
 * This is the main class of wizard.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: schroedn $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2007-12-19 22:33:50 $
 */
public class NewCSVPanelWizard extends JDialog implements ActionListener
{
	private static final String OK_COMMAND = "OK";
	private static final String CANCEL_COMMAND = "Cancel";

	//centerWindow
	private NewCSVPanelFrontPage frontPage;
	private boolean okButtonClicked = false;
	private CSVMeta csvMeta;

	public NewCSVPanelWizard(Frame owner, String title, boolean modal) throws HeadlessException
	{
		super(owner, title, modal);
		initialize();
	}

	private void initialize()
	{
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());

		frontPage = new NewCSVPanelFrontPage();
		contentPane.add(frontPage, BorderLayout.CENTER);

		JPanel southPanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));//new BorderLayout());
		JButton okButton = new JButton(OK_COMMAND);
		okButton.setMnemonic('O');
		okButton.addActionListener(this);
		JButton cancelButton = new JButton(CANCEL_COMMAND);
		cancelButton.setMnemonic('C');
		cancelButton.addActionListener(this);
		JPanel tempPanel = new JPanel(new GridLayout(1, 2));
		tempPanel.add(okButton);
		tempPanel.add(cancelButton);
		buttonPanel.add(tempPanel);//, BorderLayout.EAST);
		southPanel.add(buttonPanel, BorderLayout.NORTH);

		contentPane.add(southPanel, BorderLayout.SOUTH);
		pack();
	}


	public boolean isOkButtonClicked()
	{
		return okButtonClicked;
	}


	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		if(OK_COMMAND.equals(command))
		{
			File tempFile = frontPage.getUserSelectionFile();
			int buttonSelectedMode = frontPage.getButtonSelectedMode();
			if(tempFile==null && buttonSelectedMode!=frontPage.BLANK_COMMAND_SELECTED)
			{//tempFile is null but blank button is not clicked.
				JOptionPane.showMessageDialog(this, "Make a selection and enter file information", "No Input Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (tempFile!=null && !tempFile.isFile())
			{
				JOptionPane.showMessageDialog(this, "The '" + tempFile.getAbsolutePath() + "' is not a valid file.", "File Not Found Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			else
			{//now should examine the file.
				CSVMetaResult csvMetaResult = null;
				if (buttonSelectedMode == frontPage.GENERATE_FROM_CSV_INSTANCE_COMMAND_SELECTED)
				{
					CSVMetaFromInstanceUtil util = null;
					try
					{
						util = new CSVMetaFromInstanceUtil(tempFile.getPath());
						csvMetaResult = util.getMetadata();
					}
					catch (Exception e1)
					{
						DefaultSettings.reportThrowableToLogAndUI(this, e1, null, this, false, false);
						return;
					}
				}
				else if (buttonSelectedMode == frontPage.GENERATE_FROM_CSV_NONSTRUCTURE_INSTANCE_COMMAND_SELECTED)
				{
					CSVMetaFromInstanceUtil util = null;
					try
					{
                        util = new CSVMetaFromInstanceUtil(tempFile.getPath());
					    util.setNonStructure(true);
                        util.setNonStructureFilename(tempFile.getName());
                        csvMetaResult = util.getMetadata();
					}
					catch (Exception e1)
					{
						DefaultSettings.reportThrowableToLogAndUI(this, e1, null, this, false, false);
						return;
					}
				}
                else if (buttonSelectedMode == frontPage.NEW_FROM_AN_EXISTING_CSV_SCHEMA_COMMAND_SELECTED)
				{
					CSVMetaParserImpl parser = new CSVMetaParserImpl();
					try
					{
						csvMetaResult = parser.parse(new FileReader(tempFile));
					}
					catch (Exception e1)
					{
						DefaultSettings.reportThrowableToLogAndUI(this, e1, null, this, false, false);
						return;
					}
				}

				if(csvMetaResult!=null)
				{
					csvMeta = csvMetaResult.getCsvMeta();
					ValidatorResults validatorResults = csvMetaResult.getValidatorResults();
					if (validatorResults != null && validatorResults.hasFatal())
					{
						Message msg = validatorResults.getMessages(ValidatorResult.Level.FATAL).get(0);
						DefaultSettings.reportThrowableToLogAndUI(this, null, msg.toString(), this, true, false);
						return;
					}
				}

				if(csvMeta==null)
				{
					if(buttonSelectedMode== frontPage.BLANK_COMMAND_SELECTED)
					{
						CSVSegmentMeta rootUserObject = new CSVSegmentMetaImpl("ROOT", null);
						//						DefaultSCMTreeMutableTreeNode root = new DefaultSCMTreeMutableTreeNode(rootUserObject);
						csvMeta = new CSVMetaImpl(rootUserObject);
					}
					else
					{
						JOptionPane.showMessageDialog(this, "Cannot initiate a CSV specification tree. Please make your selection again.", "Warning", JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
			}
			okButtonClicked = true;
		}
		else if(CANCEL_COMMAND.equals(command))
		{
			okButtonClicked = false;
		}
		else
		{
			System.err.println("Why am I here with command '" + command + "'?");
		}
		setVisible(false);
		dispose();
	}

	/**
	 * Return CSVMeta, never be null.
	 * @return CSVMeta.
	 */
	public CSVMeta getCsvMeta()
	{
		return csvMeta;
	}

	public static void main(String[] args)
	{
		NewCSVPanelWizard wizard = new NewCSVPanelWizard((Frame)null, "test wizard", false);
		wizard.setVisible(true);
		DefaultSettings.centerWindow(wizard);
	}


}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/04/19 14:01:48  wangeug
 * HISTORY      : clean code
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.19  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/12/02 19:05:58  jiangsc
 * HISTORY      : Fix to Defect 219
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/11/21 21:11:04  giordanm
 * HISTORY      : Edit Defect #209
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/11/14 19:55:51  jiangsc
 * HISTORY      : Implementing UI enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/10/24 17:21:26  jiangsc
 * HISTORY      : minor update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/10/21 22:37:49  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/10/21 18:26:37  jiangsc
 * HISTORY      : First round validation implementation in CSV module.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/18 13:35:26  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/13 17:37:41  jiangsc
 * HISTORY      : Enhanced UI reporting on exceptions.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/12 21:42:46  jiangsc
 * HISTORY      : Added validation on invalid file type.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/22 20:53:10  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
