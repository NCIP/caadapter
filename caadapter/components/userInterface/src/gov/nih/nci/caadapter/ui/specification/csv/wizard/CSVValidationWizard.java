/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/wizard/CSVValidationWizard.java,v 1.2 2007-07-09 16:19:31 umkis Exp $
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

import gov.nih.nci.caadapter.common.csv.CSVDataResult;
import gov.nih.nci.caadapter.common.csv.SegmentedCSVParserImpl;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.hl7.validation.CSVMetaValidator;
import gov.nih.nci.caadapter.ui.common.message.ValidationMessagePane;
import gov.nih.nci.caadapter.ui.specification.csv.CSVPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * This class defines ...
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2007-07-09 16:19:31 $
 */
public class CSVValidationWizard extends JDialog implements ActionListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: CSVValidationWizard.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/wizard/CSVValidationWizard.java,v 1.2 2007-07-09 16:19:31 umkis Exp $";

	private static final String VALIDATE_COMMAND = "Validate";
	private static final String CLOSE_COMMAND = "Close";

	//north
	CSVValidationChoicePage frontPage = null;

	//center
	ValidationMessagePane validationMessagePane = null;
//	ValidatorResults validatorResults = null;

	//south
	private JPanel southPanel;
	private JPanel buttonPanel;
	private JButton validateButton;
	private JButton closeButton;
	private JCheckBox dockValidationResultCheckBox;

//	private boolean okButtonClicked = false;

	private CSVPanel csvPanel;

	public CSVValidationWizard(Frame owner, String title, boolean modal, CSVPanel csvPanel) throws HeadlessException
	{
		super(owner, title, modal);
		this.csvPanel = csvPanel;
		initialize();
	}

	public CSVValidationWizard(Dialog owner, String title, boolean modal, CSVPanel csvPanel) throws HeadlessException
	{
		super(owner, title, modal);
		this.csvPanel = csvPanel;
		initialize();
	}

	private void initialize()
	{
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		frontPage = new CSVValidationChoicePage();
		contentPane.add(frontPage, BorderLayout.NORTH);

		validationMessagePane = new ValidationMessagePane();
		contentPane.add(validationMessagePane, BorderLayout.CENTER);

		contentPane.add(getSouthPanel(), BorderLayout.SOUTH);
		setSize(750, 580);
	}

	private JPanel getSouthPanel()
	{
		southPanel = new JPanel(new BorderLayout());
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));//new BorderLayout());
		validateButton = new JButton(VALIDATE_COMMAND);
		validateButton.setMnemonic('V');
		validateButton.addActionListener(this);
		closeButton = new JButton(CLOSE_COMMAND);
		closeButton.setMnemonic('C');
		closeButton.addActionListener(this);
		JPanel tempPanel = new JPanel(new GridLayout(1, 2));
		tempPanel.add(validateButton);
		tempPanel.add(closeButton);
		buttonPanel.add(tempPanel);//, BorderLayout.EAST);
		southPanel.add(buttonPanel, BorderLayout.SOUTH);

		dockValidationResultCheckBox = new JCheckBox("Dock validation results");
		//default as selected.
		dockValidationResultCheckBox.setSelected(true);
		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		tempPanel.add(dockValidationResultCheckBox);
		southPanel.add(tempPanel, BorderLayout.NORTH);
		return southPanel;
	}


	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		if (VALIDATE_COMMAND.equals(command))
		{
			File tempFile = frontPage.getUserSelectionFile();
			int buttonSelectedMode = frontPage.getButtonSelectedMode();
			if (tempFile == null && buttonSelectedMode != frontPage.VALIDATE_SPECIFICATION_COMMAND_SELECTED)
			{//tempFile is null but blank button is not clicked.
				JOptionPane.showMessageDialog(this, "Make a selection and enter file information", "No Input Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (tempFile != null && !tempFile.isFile())
			{
				JOptionPane.showMessageDialog(this, "The '" + tempFile.getAbsolutePath() + "' is not a valid file.", "File Not Found Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			else
			{//now should examine the file.
				if (buttonSelectedMode == frontPage.VALIDATE_SPECIFICATION_COMMAND_SELECTED)
				{
					validateSpecification();
				}
				else if (buttonSelectedMode == frontPage.VALIDATE_DATA_AGAINST_SPECIFICATION_COMMAND_SELECTED)
				{
					validateDataAgainstSpecification(tempFile);
				}
			}
//			okButtonClicked = true;
		}
		else if (CLOSE_COMMAND.equals(command))
		{
//			okButtonClicked = false;
			setVisible(false);
			dispose();
		}
		else
		{
			System.err.println("Why am I here with command '" + command + "'?");
		}
	}

	private void validateSpecification()
	{
		CSVMeta rootMeta = csvPanel.getCSVMeta(false);
		if (rootMeta != null)
		{
			CSVMetaValidator metaValidator = new CSVMetaValidator(rootMeta);
			ValidatorResults results = metaValidator.validate();
			setValidatorResults(results);
		}
	}

	private void validateDataAgainstSpecification(File dataFile)
	{
		CSVMeta rootMeta = csvPanel.getCSVMeta(false);
		if (rootMeta != null)
		{
			SegmentedCSVParserImpl segmentedCSVParser = new SegmentedCSVParserImpl();
            CSVDataResult result = null;
            try
            {
                result = segmentedCSVParser.parse(dataFile, rootMeta);
            }
            catch(ApplicationException ae)
            {
                ValidatorResults validatorResults = new ValidatorResults();
                Message msg = MessageResources.getMessage("EMP_ER", new Object[]{ae.getMessage()});
	            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
                setValidatorResults(validatorResults);
                return;
            }
            setValidatorResults(result.getValidatorResults());
		}
	}

	private void setValidatorResults(ValidatorResults validatorResults)
	{
//		this.validatorResults = validatorResults;
		validationMessagePane.setDisplayPopupConfirmationMessage(false);
		validationMessagePane.setValidatorResults(validatorResults);
	}

	public ValidatorResults getValidatorResults()
	{
//		this.validatorResults = validatorResults;
		return validationMessagePane.getValidatorResults();
	}

	public boolean isToDockValidationResult()
	{
		return dockValidationResultCheckBox.isSelected();
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/10 18:23:54  jiangsc
 * HISTORY      : Corrected a typo.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/31 23:26:05  jiangsc
 * HISTORY      : Minor update due to font change
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/31 21:31:52  jiangsc
 * HISTORY      : Fix to Defect 164 and 162.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/25 17:13:27  jiangsc
 * HISTORY      : Added Validation implemenation.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/10/21 18:26:37  jiangsc
 * HISTORY      : First round validation implementation in CSV module.
 * HISTORY      :
 */
