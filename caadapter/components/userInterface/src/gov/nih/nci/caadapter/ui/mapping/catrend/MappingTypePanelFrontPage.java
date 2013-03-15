/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping.catrend;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.ui.specification.csv.actions.BrowseCsvAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * This class defines the first page for NewCSVPanelWizard.
 *
 * @author OWNER: wangeug
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:54:05 $
 */
public class MappingTypePanelFrontPage extends JPanel implements ActionListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: MappingTypePanelFrontPage.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/catrend/MappingTypePanelFrontPage.java,v 1.3 2008-06-09 19:54:05 phadkes Exp $";

	public static final int OBJECT_SELECTED = 1;
	public static final int DATA_SELECTED = 2;

	private static final String DATA_MODEL = "Data Model ";
	private static final String OBJECT_MODEL = "Object Model ";
	private int selectionType=1;

	private JRadioButton objectButton;
	private JRadioButton dataButton;

	public MappingTypePanelFrontPage()
	{
		initialize();
	}
		private void initialize()
	{
		this.setLayout(new BorderLayout());
		JPanel northPanel = new JPanel(new BorderLayout());
		JTextArea noteArea = new JTextArea("Select the mapping target");
		noteArea.setEditable(false);
		noteArea.setBackground(northPanel.getBackground());
		northPanel.add(noteArea, BorderLayout.CENTER);
		this.add(northPanel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel(new GridLayout(2, 1));
		this.setBorder(BorderFactory.createTitledBorder(this.getBorder(), "" /*ActionConstants.NEW_CSV_SPEC*/));

		objectButton = new JRadioButton(OBJECT_MODEL);
		objectButton.setMnemonic('B');
		objectButton.addActionListener(this);
		centerPanel.add(objectButton);

		dataButton = new JRadioButton(DATA_MODEL);
		dataButton.setMnemonic('G');
		dataButton.addActionListener(this);
		centerPanel.add(dataButton);

		this.add(centerPanel, BorderLayout.CENTER);

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(objectButton);
		buttonGroup.add(dataButton);


		//default selection
		objectButton.setSelected(true);
	}



	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		//every time clear it unless being set by explicit BLANK_COMMAND

		String command = e.getActionCommand();
		if (OBJECT_MODEL.equals(command))
		{//do nothing
			setSelectionType(OBJECT_SELECTED);

		}
		else if (DATA_MODEL.equals(command))
		{
			setSelectionType(DATA_SELECTED);
		}
		else
		{
			System.err.println("Why do I come here? Command: '" + command + "'.");
		}
	}
	public int getSelectionType() {
		return selectionType;
	}
	public void setSelectionType(int selectionType) {
		this.selectionType = selectionType;
	}



}
