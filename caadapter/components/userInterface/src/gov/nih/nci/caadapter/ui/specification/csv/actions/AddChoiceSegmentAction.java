/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.ui.specification.csv.actions;

import gov.nih.nci.caadapter.ui.specification.csv.CSVPanel;

import javax.swing.*;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.2 $
 *          date        Jul 11, 2007
 *          Time:       1:54:32 PM $
 */
public class AddChoiceSegmentAction extends AddSegmentAction
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: AddChoiceSegmentAction.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/actions/AddChoiceSegmentAction.java,v 1.2 2008-06-09 19:54:07 phadkes Exp $";


    //private static String COMMAND_NAME = AddSegmentAction.COMMAND_NAME_CHOICE;
	private static Character COMMAND_MNEMONIC_CHOICE = new Character('C');
//	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);

	private transient JTree tree;


    /**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public AddChoiceSegmentAction(CSVPanel parentPanel)
	{
		this(AddSegmentAction.COMMAND_NAME_CHOICE, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public AddChoiceSegmentAction(String name, CSVPanel parentPanel)
	{
		this(name, null, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public AddChoiceSegmentAction(String name, Icon icon, CSVPanel parentPanel)
	{
		super(name, icon, parentPanel, COMMAND_MNEMONIC_CHOICE, DOCUMENT_ACTION_TYPE);

        //setMnemonic(COMMAND_MNEMONIC);
		//setActionCommandType(DOCUMENT_ACTION_TYPE);
	}



}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/07/12 15:48:48  umkis
 * HISTORY      : csv cardinality
 * HISTORY      :
 */
