/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.resource;

import javax.swing.*;

import java.awt.*;
import java.io.File;

/**
 * The front page of open HL7 Message panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:53:51 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/resource/OpenHL7ResourceFrontPage.java,v 1.3 2008-06-09 19:53:51 phadkes Exp $";

	public static final String HL7_RESOURCE_SITE = "Target Location:";
	private JTextField dataFileInputField;
    private JCheckBox isSortKeyReassigning = null;
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
            resourcHome="HL7 Normative Home:";
            isSortKeyReassigning = new JCheckBox("Sort Key Reassigning");
        }
        else
			resourcHome="HL7 V2 Resource Home:";

		JLabel dataFileLabel = new JLabel(resourcHome);
		centerPanel.add(dataFileLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		dataFileInputField = new JTextField();
		dataFileInputField.setPreferredSize(new Dimension(350, 25));
		centerPanel.add(dataFileInputField, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		JButton dataFileBrowseButton =new JButton(new BrowseHL7ResourceAction(this, "Browser"));

		centerPanel.add(dataFileBrowseButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));

		JLabel mapFileLabel = new JLabel(HL7_RESOURCE_SITE);
		centerPanel.add(mapFileLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		JLabel targetSit = new JLabel(targetSite);
		targetSit.setPreferredSize(new Dimension(350, 25));
		centerPanel.add(targetSit, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));

        if (isSortKeyReassigning != null)
        {
            centerPanel.add(isSortKeyReassigning, new GridBagConstraints(3, 1, 2, 1, 1.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        }
        this.add(centerPanel, BorderLayout.CENTER);
	}

	public void setUserSelectionFile(File file, String browseMode)
	{
//		System.out.println("OpenHL7ResourceFrontPage.setUserSelectionFile()..selected file:"+file);
		dataFileInputField.setText(file.getAbsolutePath());
	}

	public String getSelectFileHome() {
		return dataFileInputField.getText();
	}

    public boolean isSortKeyReassigning() {
        if (isSortKeyReassigning != null) return isSortKeyReassigning.isSelected();
        return false;
    }

	public String getTargetSite() {
		return targetSite;
	}
}


