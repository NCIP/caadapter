/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.mapping.catrend.actions;


import java.awt.event.ActionEvent;

import javax.swing.Icon;

import gov.nih.nci.caadapter.ui.common.actions.DefaultContextCloseAction;
import gov.nih.nci.caadapter.ui.common.context.DefaultContextManagerClientPanel;
import gov.nih.nci.caadapter.ui.mapping.catrend.CsvToXmiMappingReportPanel;


/**
 * This class defines the close action of HL7 panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-09-29 20:28:13 $
 */
public class CloseCsvToXmiMappingReportAction extends DefaultContextCloseAction
{
	private DefaultContextManagerClientPanel clientPanel;
	@Override
	protected boolean doAction(ActionEvent e) {
		// TODO Auto-generated method stub
		CsvToXmiMappingReportPanel rptPane=(CsvToXmiMappingReportPanel)clientPanel;
		System.out.println("CloseCsvToXmiMappingReportAction.doAction()...set report to null");
		rptPane.setReporter(null);
		return super.doAction(e);
	}

	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: CloseCsvToXmiMappingReportAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/catrend/actions/CloseCsvToXmiMappingReportAction.java,v 1.4 2008-09-29 20:28:13 wangeug Exp $";

	public CloseCsvToXmiMappingReportAction(DefaultContextManagerClientPanel contentPanel)
	{
		super(COMMAND_NAME, null, contentPanel);
		clientPanel=contentPanel;
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
**/
