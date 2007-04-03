/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/message/ValidationMessageDialog.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
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

import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class defines a modal dialog alternative to display messages in a modal format.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
 */
public class ValidationMessageDialog extends JDialog implements ActionListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: ValidationMessageDialog.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/message/ValidationMessageDialog.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $";

    private static final String FRAME_TITLE = "Message Dialog";

    private static final String OK_COMMAND = "OK";
    private static final String OK_COMMAND_MNEMONIC = "O";

    private JPanel buttonPanel;
    private JButton okButton;

    private ValidationMessagePane validationMessagePane;

	/**
	 * Creates a modal or non-modal dialog without a title and
	 * with the specified owner <code>Frame</code>.  If <code>owner</code>
	 * is <code>null</code>, a shared, hidden frame will be set as the
	 * owner of the dialog.
	 * <p/>
	 * This constructor sets the component's locale property to the value
	 * returned by <code>JComponent.getDefaultLocale</code>.
	 *
	 * @param owner the <code>Frame</code> from which the dialog is displayed
	 * @param modal true for a modal dialog, false for one that allows
	 *              others windows to be active at the same time
	 * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless()
	 *                                    returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see javax.swing.JComponent#getDefaultLocale
	 */
	public ValidationMessageDialog(Frame owner, boolean modal) throws HeadlessException
	{
		super(owner, FRAME_TITLE, modal);
		initialize();
	}

    /**
	 * Creates a modal or non-modal dialog without a title and
	 * with the specified owner <code>Frame</code>.  If <code>owner</code>
	 * is <code>null</code>, a shared, hidden frame will be set as the
	 * owner of the dialog.
	 * <p/>
	 * This constructor sets the component's locale property to the value
	 * returned by <code>JComponent.getDefaultLocale</code>.
	 *
	 * @param owner the <code>Frame</code> from which the dialog is displayed
     * @param frameTitle The title of this validation message dialog
	 * @param modal true for a modal dialog, false for one that allows
	 *              others windows to be active at the same time
	 * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless()
	 *                                    returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see javax.swing.JComponent#getDefaultLocale
	 */
	public ValidationMessageDialog(Frame owner, String frameTitle, boolean modal) throws HeadlessException
	{
		super(owner, frameTitle, modal);
        initialize();
	}

    /**
	 * Creates a modal or non-modal dialog without a title and
	 * with the specified owner dialog.
	 * <p/>
	 * This constructor sets the component's locale property to the value
	 * returned by <code>JComponent.getDefaultLocale</code>.
	 *
	 * @param owner the non-null <code>Dialog</code> from which the dialog is displayed
	 * @param modal true for a modal dialog, false for one that allows
	 *              other windows to be active at the same time
	 * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless()
	 *                                    returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see javax.swing.JComponent#getDefaultLocale
	 */
	public ValidationMessageDialog(Dialog owner, boolean modal) throws HeadlessException
	{
		super(owner, FRAME_TITLE, modal);
		initialize();
	}

    /**
	 * Creates a modal or non-modal dialog without a title and
	 * with the specified owner <code>Frame</code>.  If <code>owner</code>
	 * is <code>null</code>, a shared, hidden frame will be set as the
	 * owner of the dialog.
	 * <p/>
	 * This constructor sets the component's locale property to the value
	 * returned by <code>JComponent.getDefaultLocale</code>.
	 *
	 * @param owner the non-null <code>Dialog</code> from which the dialog is displayed
     * @param frameTitle The title of this validation message dialog
	 * @param modal true for a modal dialog, false for one that allows
	 *              others windows to be active at the same time
	 * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless()
	 *                                    returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see javax.swing.JComponent#getDefaultLocale
	 */
	public ValidationMessageDialog(Dialog owner, String frameTitle, boolean modal) throws HeadlessException
	{
		super(owner, frameTitle, modal);
        initialize();
	}

    private void initialize()
	{
		setLayout(new BorderLayout());
		validationMessagePane = new ValidationMessagePane();
		this.add(validationMessagePane, BorderLayout.CENTER);

		buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		okButton= new JButton(OK_COMMAND);
		okButton.setMnemonic(OK_COMMAND_MNEMONIC.charAt(0));
		okButton.addActionListener(this);
		buttonPanel.add(okButton);

		this.add(buttonPanel, BorderLayout.SOUTH);
		setSize(700, 500);

	}

	/**
	 * Call this function to present a traversable view to display all messages in the results based on level.
	 *
	 * @param results
	 */
	public void setValidatorResults(ValidatorResults results)
	{
		validationMessagePane.setValidatorResults(results);
	}

	/**
	 * Call this function to display only a list of messages to the UI.
	 * Call setValidatorResults() instead, if you'd like to display all messages in ths results.
	 * @param messageList
	 */
	public void setMessageList(java.util.List messageList)
	{
		validationMessagePane.setMessageList(messageList);
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
        if(OK_COMMAND.equals(command))
          {//do something here?
          }
        setVisible(false);
        dispose();
    }

	/**
	 * Return if the message dialog would display confirmation message in a popup dialog.
	 * @return if the message dialog would display confirmation message in a popup dialog.
	 */
//	public boolean isDisplayPopupConfirmationMessage()
//	{
//		return validationMessagePane.isDisplayPopupConfirmationMessage();
//	}

	/**
	 * Turn on or off the display message on popup.
	 * @param displayPopupConfirmationMessage
	 */
	public void setDisplayPopupConfirmationMessage(boolean displayPopupConfirmationMessage)
	{
		validationMessagePane.setDisplayPopupConfirmationMessage(displayPopupConfirmationMessage);
	}

	/**
	 * To display the validation results in a dialog.
	 * @param parentContainer
	 * @param validatorResults
	 */
	public static void displayValidationResults(Container parentContainer, ValidatorResults validatorResults)
	{
		ValidationMessageDialog dialog = null;
		if (parentContainer instanceof Frame)
		{
			dialog = new ValidationMessageDialog((Frame) parentContainer, true);
		}
		else if (parentContainer instanceof Dialog)
		{
			dialog = new ValidationMessageDialog((Dialog) parentContainer, true);
		}
		if (dialog != null)
		{
			dialog.setDisplayPopupConfirmationMessage(false);
			dialog.setValidatorResults(validatorResults);
			DefaultSettings.centerWindow(dialog);
			dialog.setVisible(true);
		}
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.16  2006/11/08 16:41:35  umkis
 * HISTORY      : ValidationMessageDialog(Dialog owner, String frameTitle, boolean modal) and
 * HISTORY      : ValidationMessageDialog(Frame owner, String frameTitle, boolean modal) are added.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/11/29 16:23:53  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/11/02 05:37:38  umkis
 * HISTORY      : defect# 172
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/01 22:37:14  jiangsc
 * HISTORY      : Added a static method
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/11/01 18:38:24  umkis
 * HISTORY      : defect# 172
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/01 17:25:17  umkis
 * HISTORY      : defect# 172
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/31 21:31:52  jiangsc
 * HISTORY      : Fix to Defect 164 and 162.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/24 16:26:57  jiangsc
 * HISTORY      : minor update
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/21 22:37:49  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/10/21 18:26:17  jiangsc
 * HISTORY      : Validation Class name changes.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/07 18:40:17  jiangsc
 * HISTORY      : Enhanced the Look and Feel of Validation and Properties.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/05 20:52:40  jiangsc
 * HISTORY      : GUI Enhancement.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/05 20:50:32  jiangsc
 * HISTORY      : GUI Enhancement.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/05 20:39:55  jiangsc
 * HISTORY      : GUI Enhancement.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/26 16:31:05  jiangsc
 * HISTORY      : Validation Display
 * HISTORY      :
 */
