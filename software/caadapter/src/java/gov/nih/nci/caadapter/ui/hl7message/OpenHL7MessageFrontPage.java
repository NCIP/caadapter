/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.hl7message;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
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
 *          revision    $Revision: 1.6 $
 *          date        $Date: 2009-02-03 19:02:07 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/OpenHL7MessageFrontPage.java,v 1.6 2009-02-03 19:02:07 wangeug Exp $";

	public static final String DATA_FILE_BROWSE_MODE = "Data File";
	public static final String MAP_FILE_BROWSE_MODE = "Map Specification";
	public static final String DEST_FILE_BROWSE_MODE = "Result File";
	private JTextField dataFileInputField;
	private JTextField mapFileInputField;
	private JTextField destFileInputField;
	private boolean isCSV2HL7 = false;
	private File mapFile;
	private File dataFile;
	private File destFile;
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

		if (!(openWizardTitle!=null&&openWizardTitle.contains(Config.HL7_V3_TO_CSV_MODULE_NAME))){
			this.isCSV2HL7 = true;
			JLabel destFileLabel = new JLabel(DEST_FILE_BROWSE_MODE);
			centerPanel.add(destFileLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
			destFileInputField = new JTextField();
			destFileInputField.setPreferredSize(new Dimension(350, 25));
			centerPanel.add(destFileInputField, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
			JButton destFileBrowseButton = new JButton(new BrowseHLV3MessageAction(this, DEST_FILE_BROWSE_MODE));
			centerPanel.add(destFileBrowseButton, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
		}
		this.add(centerPanel, BorderLayout.CENTER);
	}

	public String getFileExtension(String browseMode)
	{
		String result = null;
		if(DATA_FILE_BROWSE_MODE.equals(browseMode))
		{
			if (openWizardTitle==null)
				result="";
			if (openWizardTitle.contains(Config.HL7_V3_TO_CSV_MODULE_NAME))
				result=Config.HL7_V3_MESSAGE_FILE_DEFAULT_EXTENSION;
			else if (openWizardTitle.contains(ActionConstants.NEW_HL7_V2_TO_HL7_V3_MESSAGE))
				result=".hl7";
			else
				result = Config.CSV_DATA_FILE_DEFAULT_EXTENSTION;
		}
		else if(MAP_FILE_BROWSE_MODE.equals(browseMode))
		{
            result = Config.MAP_FILE_DEFAULT_EXTENTION;
		}
		else if(DEST_FILE_BROWSE_MODE.equals(browseMode))
		{
            result = ".zip";
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
		else if (DEST_FILE_BROWSE_MODE.equals(browseMode))
		{
			this.setDestFile(file);
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
        boolean result = validateInputByMode(DATA_FILE_BROWSE_MODE) && validateInputByMode(MAP_FILE_BROWSE_MODE) && validateInputByMode(DEST_FILE_BROWSE_MODE);
		return result;
	}

	private boolean validateInputByMode(String mode)
	{
		File tempFile = null;
		String fileTypeMsg = null;

		//if csv->hl7, validate dest file
		if(!this.isCSV2HL7 && DEST_FILE_BROWSE_MODE.equals(mode))
			return true;
		if(this.isCSV2HL7 && DEST_FILE_BROWSE_MODE.equals(mode))
        {
        	tempFile = getDestFile();
        	if(tempFile == null){
        		JOptionPane.showMessageDialog(this, "Please choose result file", "File Not Found Error",
    					JOptionPane.ERROR_MESSAGE);
        		return false;
        	}

//        	if(tempFile.exists()){
//        		if( JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(this, getDestFile()+" exists, do you want to overwrite it?"))
//        			return false;
//        	}
        	return true;
        }

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

	/**
	 * Return null if user does not specify any file.
	 * @return
	 */
	public File getDestFile()
	{
		if (destFileInputField==null)
			return null; //not destination file is set/required
		String userInputText = destFileInputField.getText();
		if (userInputText != null && userInputText.length() > 0)
		{
			File tempFile = new File(userInputText);
			if (!GeneralUtilities.areEqual(destFile, tempFile))
			{//user input supersedes browsed value.
				destFile = tempFile;
			}
		}
		else
		{
			destFile = null;
		}

		return destFile;
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

	protected void setDestFile(File destFile)
	{
		this.destFile = destFile;
		if (destFile != null)
		{
			destFileInputField.setText(destFile.getAbsolutePath());
		}
		else
		{
			destFileInputField.setText("");
		}
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.5  2009/02/03 15:49:21  wangeug
 * HISTORY      : separate menu item group: csv to HL7 V3 and HL7 V2 to HL7 V3
 * HISTORY      :
 * HISTORY      : Revision 1.4  2008/09/25 15:41:31  wangeug
 * HISTORY      : check null
 * HISTORY      :
 * HISTORY      : Revision 1.3  2008/06/26 19:45:51  linc
 * HISTORY      : Change HL7 transformation GUI to use batch api.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2008/06/09 19:53:52  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/03 19:33:17  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
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
