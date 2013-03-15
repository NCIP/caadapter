/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.specification.csv.actions;

import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.actions.DefaultContextCloseAction;
import gov.nih.nci.caadapter.ui.common.context.ContextManager;
import gov.nih.nci.caadapter.ui.specification.csv.CSVPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * This class defines the close action of CSV specification panel.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-09-29 20:09:49 $
 */
public class CloseCsvAction extends DefaultContextCloseAction//DefaultCloseAction
{
	private transient CSVPanel csvPanel;

	public CloseCsvAction(CSVPanel csvPanel)
	{
		this(COMMAND_NAME, csvPanel);
	}

	public CloseCsvAction(String name, CSVPanel csvPanel)
	{
		this(name, null, csvPanel);
	}

	public CloseCsvAction(String name, Icon icon, CSVPanel csvPanel)
	{
		super(name, icon, csvPanel);
		this.csvPanel = csvPanel;
	}

	protected boolean postActionPerformed(ActionEvent e)
	{
		if (mainFrame != null)
		{
			//todo: should use some better way to call contextmanager
			ContextManager cm = ContextManager.getContextManager();
			cm.enableAction(ActionConstants.OPEN_CSV_SPEC, true);
			cm.enableAction(ActionConstants.NEW_CSV_SPEC, true);
		}
		return true;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2008/06/09 19:54:07  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/07/27 22:41:20  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/22 20:53:08  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
