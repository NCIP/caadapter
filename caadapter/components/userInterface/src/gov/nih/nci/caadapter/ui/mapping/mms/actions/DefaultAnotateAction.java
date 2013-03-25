/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping.mms.actions;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.DefaultSaveAsAction;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * This class defines a default implementation of Validate action.
 * The primary focus of this class is to provide consistent look and feel definition
 * to ValidateAction across the system.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:54:06 $
 */
public class DefaultAnotateAction extends DefaultSaveAsAction
{
	public static final String COMMAND_NAME = ActionConstants.ANOTATE;
	public static final Character COMMAND_MNEMONIC = new Character('V');
	public static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("validation.gif"));
	public static final String TOOL_TIP_DESCRIPTION = ActionConstants.ANOTATE + " " + Config.COMMON_METADATA_DISPLAY_NAME;

	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: DefaultAnotateAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/mms/actions/DefaultAnotateAction.java,v 1.2 2008-06-09 19:54:06 phadkes Exp $";

	protected transient AbstractMainFrame mainFrame = null;

	public DefaultAnotateAction(AbstractMainFrame mainFrame)
	{
		this(COMMAND_NAME, mainFrame);
	}

	public DefaultAnotateAction(String name, AbstractMainFrame mainFrame)
	{
		this(name, IMAGE_ICON, mainFrame);
	}

	public DefaultAnotateAction(String name, Icon icon, AbstractMainFrame mainFrame)
	{
		super(name, icon,mainFrame);
		this.mainFrame = mainFrame;
		setAdditionalAttributes();
	}

	protected void setAdditionalAttributes()
	{
		setMnemonic(COMMAND_MNEMONIC);
		//		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DESKTOP_ACTION_TYPE);
		setShortDescription(TOOL_TIP_DESCRIPTION);
		//do not know how to set the icon location name, or just do not matter.
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
		return true;
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return mainFrame;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:57  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/09/26 15:48:54  wuye
 * HISTORY      : New default actions for object 2 db mapping
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/08/02 18:44:20  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/14 21:37:16  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/31 23:26:05  jiangsc
 * HISTORY      : Minor update due to font change
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/10/03 18:21:54  jiangsc
 * HISTORY      : default validation action
 * HISTORY      :
 */
