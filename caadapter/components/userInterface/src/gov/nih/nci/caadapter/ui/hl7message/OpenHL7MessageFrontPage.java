/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/OpenHL7MessageFrontPage.java,v 1.1 2007-07-03 19:33:17 wangeug Exp $
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

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.ui.hl7message.actions.BrowseHLV3MessageAction;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * The front page of open HL7 Message panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-07-03 19:33:17 $
 */
public class OpenHL7MessageFrontPage extends JPanel
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: OpenHL7MessageFrontPage.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/OpenHL7MessageFrontPage.java,v 1.1 2007-07-03 19:33:17 wangeug Exp $";

	public static final String DATA_FILE_BROWSE_MODE = "Data File";
	public static final String MAP_FILE_BROWSE_MODE = "Map Specification";
	private JTextField dataFileInputField;
	private JTextField mapFileInputField;

	private File mapFile;
	private File dataFile;
	private String openWizardTitle;
	/**
	 * Creates a new <code>JPanel</code> with a double buffer
	 * and a flow layout.
	 */
	public OpenHL7MessageFrontPage(OpenHL7MessageWizard wizard)
	{
		openWizardTitle=wizard.getTitle();
		initialize();
	}

	private void initialize()
	{
		this.setLayout(new BorderLayout());//new FlowLayout(FlowLayout.LEADING));
		JPanel centerPanel = new JPanel(new GridBagLayout());
		Insets insets = new Insets(5, 5, 5, 5);
		JLabel dataFileLabel = new JLabel(DATA_FILE_BROWSE_MODE);
		centerPanel.add(dataFileLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		dataFileInputField = new JTextField();
		dataFileInputField.setPreferredSize(new Dimension(350, 25));
		centerPanel.add(dataFileInputField, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		JButton dataFileBrowseButton = new JButton(new BrowseHLV3MessageAction(this, DATA_FILE_BROWSE_MODE));
		centerPanel.add(dataFileBrowseButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));

		JLabel mapFileLabel = new JLabel(MAP_FILE_BROWSE_MODE);
		centerPanel.add(mapFileLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		mapFileInputField = new JTextField();
		mapFileInputField.setPreferredSize(new Dimension(350, 25));
		centerPanel.add(mapFileInputField, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		JButton mapFileBrowseButton = new JButton(new BrowseHLV3MessageAction(this, MAP_FILE_BROWSE_MODE));
		centerPanel.add(mapFileBrowseButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));

		this.add(centerPanel, BorderLayout.CENTER);
	}

	public String getFileExtension(String browseMode)
	{
		String result = null;
		if(DATA_FILE_BROWSE_MODE.equals(browseMode))
		{
			if (openWizardTitle!=null&&openWizardTitle.contains(Config.HL7_V3_TO_CSV_MODULE_NAME))
				result=Config.HL7_V3_MESSAGE_FILE_DEFAULT_EXTENSION;
			else
				result = Config.CSV_DATA_FILE_DEFAULT_EXTENSTION;
		}
		else if(MAP_FILE_BROWSE_MODE.equals(browseMode))
		{
            result = Config.MAP_FILE_DEFAULT_EXTENTION;
		}
		else
		{
			result = "";
		}
		System.out.println("OpenHL7MessageFrontPage.getFileExtension()..file extension:"+result);
		return result;
	}

	public void setUserSelectionFile(File file, String browseMode)
	{
		if (DATA_FILE_BROWSE_MODE.equals(browseMode))
		{
			this.setDataFile(file);
		}
		else if (MAP_FILE_BROWSE_MODE.equals(browseMode))
		{
			this.setMapFile(file);
		}
		else
		{
			System.err.println(getClass().getName() + ".getFileExtension() does not understand this mode '" + browseMode + "'.");
		}
	}


	/**
	 * Return true if user inputs are valid.
	 * @return
	 */
	public boolean validateInputFields()
	{
        boolean result = validateInputByMode(DATA_FILE_BROWSE_MODE) && validateInputByMode(MAP_FILE_BROWSE_MODE);
		return result;
	}

	private boolean validateInputByMode(String mode)
	{
		File tempFile = null;
		String fileTypeMsg = null;
		if(DATA_FILE_BROWSE_MODE.equals(mode))
		{
			tempFile = getDataFile();
			fileTypeMsg = "The data file ";
		}
		else if(MAP_FILE_BROWSE_MODE.equals(mode))
		{
			tempFile = getMapFile();
			fileTypeMsg = "The map specification ";
		}

		if (tempFile == null || !tempFile.isFile())
		{
			String errorDetail = (tempFile==null) ? "should not be null."
					: tempFile.getAbsolutePath() + " does not represent a valid file.";
			JOptionPane.showMessageDialog(this, fileTypeMsg + errorDetail, "File Not Found Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
        else
		{
			return true;
		}
	}

	/**
	 * Return null if user does not specify any file.
	 *
	 * @return
	 */
	public File getMapFile()
	{
		String userInputText = mapFileInputField.getText();
		if (userInputText != null && userInputText.length() > 0)
		{
			File tempFile = new File(userInputText);
			if (!GeneralUtilities.areEqual(mapFile, tempFile))
			{//user input supersedes browsed value.
				mapFile = tempFile;
			}
		}
		else
		{
			mapFile = null;
		}

		return mapFile;
	}

	/**
	 * Return null if user does not specify any file.
	 * @return
	 */
	public File getDataFile()
	{
		String userInputText = dataFileInputField.getText();
		if (userInputText != null && userInputText.length() > 0)
		{
			File tempFile = new File(userInputText);
			if (!GeneralUtilities.areEqual(dataFile, tempFile))
			{//user input supersedes browsed value.
				dataFile = tempFile;
			}
		}
		else
		{
			dataFile = null;
		}

		return dataFile;
	}

	protected void setMapFile(File mapFile)
	{
		this.mapFile = mapFile;
		if (mapFile != null)
		{
			mapFileInputField.setText(mapFile.getAbsolutePath());
		}
		else
		{
			mapFileInputField.setText("");
		}
	}

	protected void setDataFile(File dataFile)
	{
		this.dataFile = dataFile;
		if (dataFile != null)
		{
			dataFileInputField.setText(dataFile.getAbsolutePath());
		}
		else
		{
			dataFileInputField.setText("");
		}
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.10  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/05 15:35:49  umkis
 * HISTORY      : defect# 227 the size of the JTextFields was made wider for in put parameters.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/17 22:06:39  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/26 14:32:38  jiangsc
 * HISTORY      : Chaned due to name change of OpenHL7MessageWizard
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/07 21:41:23  jiangsc
 * HISTORY      : New Structure
 * HISTORY      :
 */
