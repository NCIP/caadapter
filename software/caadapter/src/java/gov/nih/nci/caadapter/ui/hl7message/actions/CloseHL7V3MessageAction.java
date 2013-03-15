/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.hl7message.actions;

import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.actions.DefaultContextCloseAction;
import gov.nih.nci.caadapter.ui.common.context.ContextManager;
import gov.nih.nci.caadapter.ui.hl7message.HL7MessagePanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * This class defines the close action of HL7 panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2009-02-03 15:49:21 $
 */
public class CloseHL7V3MessageAction extends DefaultContextCloseAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: CloseHL7V3MessageAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/actions/CloseHL7V3MessageAction.java,v 1.4 2009-02-03 15:49:21 wangeug Exp $";

	private transient HL7MessagePanel hl7Panel;

	public CloseHL7V3MessageAction(HL7MessagePanel hl7Panel)
	{
		this(COMMAND_NAME, hl7Panel);
	}

	public CloseHL7V3MessageAction(String name, HL7MessagePanel hl7Panel)
	{
		this(name, null, hl7Panel);
	}

	public CloseHL7V3MessageAction(String name, Icon icon, HL7MessagePanel hl7Panel)
	{
		super(name, icon, hl7Panel);
		this.hl7Panel = hl7Panel;
	}

	protected boolean postActionPerformed(ActionEvent e)
	{
		if (mainFrame != null)
		{
			//todo: should use some better way to call contextmanager
			ContextManager cm = ContextManager.getContextManager();
			cm.enableAction(ActionConstants.OPEN_HL7_V3_MESSAGE, true);
			cm.enableAction(ActionConstants.NEW_CSV_TO_HL7_V3_MESSAGE, true);
		}
		hl7Panel.clearDataFromUI();
		return true;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2008/06/09 19:53:52  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/09/10 16:40:35  wangeug
 * HISTORY      : fix bug: create new actionItem with new Panel
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/03 19:33:17  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.5  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/27 22:41:09  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 */
