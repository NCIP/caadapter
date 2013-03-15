/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping.GME.actions;

import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.actions.DefaultSaveAction;
import gov.nih.nci.caadapter.ui.common.context.DefaultContextManagerClientPanel;

import java.awt.event.ActionEvent;

/**
 * This class defines the concrete implementation of "Save" action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:54:05 $
 */
public class SaveXsdToXmiMappingReportAction extends SaveAsXsdToXmiMappingReportAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: SaveXsdToXmiMappingReportAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/GME/actions/SaveXsdToXmiMappingReportAction.java,v 1.2 2008-06-09 19:54:05 phadkes Exp $";

	private static final String TOOL_TIP_DESCRIPTION = "Save HL7 v3 Message to a File";

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public SaveXsdToXmiMappingReportAction(DefaultContextManagerClientPanel contentPanel)
	{
		super(contentPanel);
		setIcon(DefaultSaveAction.IMAGE_ICON);
		setName("      " + ActionConstants.SAVE);
	}


	/**
	 * Invoked when an action occurs.
	 */
	public boolean doAction(ActionEvent e) throws Exception
	{
		/**
		 * Design Rationale:
		 * 1) Get the latest file from GUI panel;
		 * 2) if defaultFile and fileFromPanel are not equal, let the defaultFile be the latest value;
		 * 3) if the latest value is null, trigger SaveAs function, i.e., ask for user input;
		 * 4) if not, proceed the saving;
		 */
		if (defaultFile == null)
		{
			return super.doAction(e);
		}
		else
		{
			return processSaveFile(defaultFile);
		}
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2008/02/04 15:10:34  schroedn
 * HISTORY      : XSD to XMI Mapping - GME initial load
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/12/12 19:54:20  wangeug
 * HISTORY      : support both data model and object model
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/03 19:33:17  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/10/26 16:22:10  jiangsc
 * HISTORY      : Face lift to provide better error report.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/17 22:39:02  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/09/30 18:06:35  jiangsc
 * HISTORY      : Resolved save defect.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/03 16:56:15  jiangsc
 * HISTORY      : Further consolidation of context sensitive management.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/03 14:39:09  jiangsc
 * HISTORY      : Further consolidation of context sensitive management.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/02 22:28:53  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/27 22:41:08  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 */
