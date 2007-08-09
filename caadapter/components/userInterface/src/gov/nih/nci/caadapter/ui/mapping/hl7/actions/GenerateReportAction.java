/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/hl7/actions/GenerateReportAction.java,v 1.2 2007-08-09 13:49:13 wuye Exp $
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


package gov.nih.nci.caadapter.ui.mapping.hl7.actions;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
//import gov.nih.nci.caadapter.hl7.map.MapReportGenerator;
import gov.nih.nci.caadapter.hl7.map.Mapping;
import gov.nih.nci.caadapter.hl7.report.MapReportGenerator;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.common.jgraph.MappingDataManager;
import gov.nih.nci.caadapter.ui.mapping.hl7.HL7MappingPanel;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * This class defines the action to carry out process report functionality on a SCM file.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wuye $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2007-08-09 13:49:13 $
 */
public class GenerateReportAction extends AbstractContextAction
{
	protected static final String COMMAND_NAME = "Generate Report...";
	protected static final Character COMMAND_MNEMONIC = new Character('G');

	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: GenerateReportAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/hl7/actions/GenerateReportAction.java,v 1.2 2007-08-09 13:49:13 wuye Exp $";

	protected HL7MappingPanel mappingPanel;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public GenerateReportAction(HL7MappingPanel mappingPanel)
	{
		this(COMMAND_NAME, mappingPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public GenerateReportAction(String name, HL7MappingPanel mappingPanel)
	{
		this(name, null, mappingPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public GenerateReportAction(String name, Icon icon, HL7MappingPanel mappingPanel)
	{
		super(name, icon);
		this.mappingPanel = mappingPanel;
		setAdditionalAttributes();
	}

	/**
	 * provide descendant class to override.
	 */
	protected void setAdditionalAttributes()
	{
		setMnemonic(COMMAND_MNEMONIC);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
		//		File file = DefaultSettings.getUserInputOfFileFromGUI(this.mappingPanel, getUIWorkingDirectoryPath(), Config.REPORT_FILE_DEFAULT_EXTENSION, "Select File to Save Generated Report", true, true);
		File file = DefaultSettings.getUserInputOfFileFromGUI(this.mappingPanel, Config.REPORT_FILE_DEFAULT_EXTENSION, "Select File to Save Generated Report", true, true);
		System.out.println("File:"+file);
		if (file != null)
		{
			//do something.
			//			Log.logInfo(this, "GenerateReportAction will do something now.");
			MappingDataManager mappingManager = mappingPanel.getMappingDataManager();
			Mapping mappingData = mappingManager.retrieveMappingData(true);
			String msg = "";
			if(mappingData == null)
			{
				msg = "Mapping data ";
			}
			else
			{
				if(mappingData.getSourceComponent()==null)
				{
					msg = "Source data ";
				}
				if(mappingData.getTargetComponent()==null)
				{
					if(msg.length()>0)
					{
						msg += "and ";
					}
					msg += "Target data ";
				}
				if(msg.length()>0)
				{
					msg += "do not exist.";
				}
			}

			if(msg.length()>0)
			{
				JOptionPane.showMessageDialog(mappingPanel, msg, "Warning", JOptionPane.WARNING_MESSAGE);
				return isSuccessfullyPerformed();
			}

			MapReportGenerator generator = new MapReportGenerator();
			ValidatorResults validatorResults = null;
			try
			{
				GeneralUtilities.setCursorWaiting(mappingPanel);
				validatorResults = generator.generate(file, mappingData);
			}
			finally
			{
				GeneralUtilities.setCursorDefault(mappingPanel);
			}
			boolean everythingGood = handleValidatorResults(validatorResults);
			if (everythingGood)
			{
				JOptionPane.showMessageDialog(mappingPanel.getParent(), "Report has been successfully generated.", "Action Complete", JOptionPane.INFORMATION_MESSAGE);
			}
			setSuccessfullyPerformed(everythingGood);
		}
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return mappingPanel;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/07/03 19:37:42  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/19 22:44:16  jiangsc
 * HISTORY      : Feature enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/12/09 20:12:07  jiangsc
 * HISTORY      : implemented the functionality.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/12/08 23:52:32  jiangsc
 * HISTORY      : Updated.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/12/08 23:22:43  jiangsc
 * HISTORY      : Upgrade the handleValidatorResults() function.
 */
