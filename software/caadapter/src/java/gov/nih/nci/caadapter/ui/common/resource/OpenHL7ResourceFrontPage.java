/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.common.resource;

import javax.swing.*;

import java.awt.*;
import java.io.File;

/**
 * The front page of open HL7 Message panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.7 $
 *          date        $Date: 2009-03-25 13:59:15 $
 */
public class OpenHL7ResourceFrontPage extends JPanel
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: OpenHL7ResourceFrontPage.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/resource/OpenHL7ResourceFrontPage.java,v 1.7 2009-03-25 13:59:15 wangeug Exp $";

	public static final String HL7_RESOURCE_SITE = "HL7 Specification Home:";
	private JTextField mifFileInputField;
	private JTextField coreSchemaInputField;
	private JTextField messageSchemaInputField;
	private JTextField hl7HomeInputField;
    private String openWizardType;
	private String targetSite;
	/**
	 * Creates a new <code>JPanel</code> with a double buffer
	 * and a flow layout.
	 */
	public OpenHL7ResourceFrontPage(JFrame frame,String title, String resourceSite)
	{
		openWizardType=title;
		targetSite=resourceSite;
		initialize();
	}

	private void initialize()
	{
		this.setLayout(new BorderLayout());//new FlowLayout(FlowLayout.LEADING));
		JPanel centerPanel = new JPanel(new GridBagLayout());
		Insets insets = new Insets(5, 5, 5, 5);
		String resourcHome="";
		if (openWizardType.equals(BuildHL7ResourceAction.COMMAND_BUILD_V3))
        {
            resourcHome="HL7 V3 MIF File (mif.zip) :";
        }
        else
			resourcHome="HL7 V2 Resource Home:";

		//set MIF file selection
		JLabel mifFileLabel = new JLabel(resourcHome);
		centerPanel.add(mifFileLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		mifFileInputField = new JTextField();
		mifFileInputField.setPreferredSize(new Dimension(350, 25));
		centerPanel.add(mifFileInputField, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		JButton mifFileBrowseButton=new JButton(new BrowseHL7ResourceAction(this, mifFileInputField, "Browser", ".zip"));
		centerPanel.add(mifFileBrowseButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));

		//set coreSchema selection
		JLabel coreSchLabel=new JLabel("Coreschemas (file folder):");
		centerPanel.add(coreSchLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		coreSchemaInputField = new JTextField();
		coreSchemaInputField.setPreferredSize(new Dimension(350, 25));
		centerPanel.add(coreSchemaInputField, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		JButton coreScheamBrowseButton=new JButton(new BrowseHL7ResourceAction(this, coreSchemaInputField, "Browser", null));
		centerPanel.add(coreScheamBrowseButton, new GridBagConstraints(3,1, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));

		//set messageSchema selection
		JLabel messageSchLabel=new JLabel("Multicacheschemas (file folder):");
		centerPanel.add(messageSchLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		messageSchemaInputField = new JTextField();
		messageSchemaInputField.setPreferredSize(new Dimension(350, 25));
		centerPanel.add(messageSchemaInputField, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		JButton messageScheamBrowseButton=new JButton(new BrowseHL7ResourceAction(this, messageSchemaInputField, "Browser", null));
		centerPanel.add(messageScheamBrowseButton, new GridBagConstraints(3,2, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));

		JLabel mapFileLabel = new JLabel(HL7_RESOURCE_SITE);
		centerPanel.add(mapFileLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));

		hl7HomeInputField=new JTextField();;
		hl7HomeInputField.setPreferredSize(new Dimension(350, 25));
		hl7HomeInputField.setText(System.getProperty("user.dir"));
//		JLabel caAdapterHome=new JLabel(System.getProperty("user.dir"));
//		caAdapterHome.setPreferredSize(new Dimension(350, 25));
		centerPanel.add(hl7HomeInputField, new GridBagConstraints(1, 4, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		JButton hl7HomeBrowseButton=new JButton(new BrowseHL7ResourceAction(this, hl7HomeInputField, "Browser", null));
		centerPanel.add(hl7HomeBrowseButton, new GridBagConstraints(3,4, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));

        this.add(centerPanel, BorderLayout.CENTER);
	}

	public void setUserSelectionFile(File file, String browseMode)
	{
//		System.out.println("OpenHL7ResourceFrontPage.setUserSelectionFile()..selected file:"+file);
		mifFileInputField.setText(file.getAbsolutePath());
	}

	public String getSelectFileHome() {
		return mifFileInputField.getText();
	}

//    public boolean isSortKeyReassigning() {
//        if (isSortKeyReassigning != null) return isSortKeyReassigning.isSelected();
//        return false;
//    }

	public String getTargetSite() {
		if (targetSite==null||targetSite.equals(""))
			targetSite=hl7HomeInputField.getText();
		return targetSite;
	}

	public String getCoreSchemaFileDirectory() {
		return coreSchemaInputField.getText();
	}

	public String getMessageSchemaFileDirectory() {
		return messageSchemaInputField.getText();
	}

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.6  2008/09/24 17:55:16  phadkes
 * HISTORY      : Changes for code standards
 * HISTORY      :
*/
