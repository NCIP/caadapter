/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.specification.csv.wizard;

import javax.swing.*;
import java.io.File;

/**
 * This class provides a generic interface for the first page of a wizard.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:54:07 $
 */
public abstract class FrontPage extends JPanel// implements ActionListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: FrontPage.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/wizard/FrontPage.java,v 1.2 2008-06-09 19:54:07 phadkes Exp $";

	public static final int NO_COMMAND_SELECTED = -1;

	protected int buttonSelectedMode;
	protected File userSelectionFile;

	/**
	 * Creates a new <code>JPanel</code> with a double buffer
	 * and a flow layout.
	 */
	public FrontPage()
	{
		buttonSelectedMode = NO_COMMAND_SELECTED;
		initialize();
	}

	/**
	 * Descendant class needs to implement this UI initialization.
	 */
	protected abstract void initialize();

	/**
	 * It is highly recommended that descendant class overrides this method to provide dynamic
	 * information of file extension (in sync with the getActiveBrowseDialogTitle() return value),
	 * to be utilized by BrowseCsvAction, if needed.
	 */
	public String getActiveFileExtension()
	{
		return "";
	}

	/**
	 * Answers if blank button is selected.
	 *
	 * @return if blank button is selected.
	 */
	public int getButtonSelectedMode()
	{
		return buttonSelectedMode;
	}


	/**
	 * It is highly recommended that descendant class overrides this method to provide dynamic
	 * information of dialog title (in sync with the getActiveFileExtension() return value),
	 * to be utilized by BrowseCsvAction, if needed.
	 */
	public String getActiveBrowseDialogTitle()
	{
		return "";
	}

	public File getUserSelectionFile()
	{
		return userSelectionFile;
	}

	public void setUserSelectionFile(File userSelectionFile)
	{
		this.userSelectionFile = userSelectionFile;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
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
 * HISTORY      : Revision 1.15  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/10/21 18:26:37  jiangsc
 * HISTORY      : First round validation implementation in CSV module.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/10/18 13:35:26  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/10/17 22:12:39  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/10/12 21:49:03  jiangsc
 * HISTORY      : Minor update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/10/12 21:42:46  jiangsc
 * HISTORY      : Added validation on invalid file type.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/09/09 22:41:51  chene
 * HISTORY      : Saved Point
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/03 22:07:54  jiangsc
 * HISTORY      : Save Point.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/07/22 20:53:10  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
