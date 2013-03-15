/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping.GME.actions;


import java.awt.event.ActionEvent;

import gov.nih.nci.caadapter.ui.common.actions.DefaultContextCloseAction;
import gov.nih.nci.caadapter.ui.common.context.DefaultContextManagerClientPanel;
import gov.nih.nci.caadapter.ui.mapping.catrend.CsvToXmiMappingReportPanel;
import gov.nih.nci.caadapter.ui.mapping.GME.XsdToXmiMappingReportPanel;


/**
 * This class defines the close action of HL7 panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:54:05 $
 */
public class CloseXsdToXmiMappingReportAction extends DefaultContextCloseAction
{
	private DefaultContextManagerClientPanel clientPanel;
	@Override
	protected boolean doAction(ActionEvent e) {
		// TODO Auto-generated method stub
		XsdToXmiMappingReportPanel rptPane=(XsdToXmiMappingReportPanel)clientPanel;
		System.out.println("CloseXsdToXmiMappingReportAction.doAction()...set report to null");
		rptPane.setReporter(null);
		return super.doAction(e);
	}

	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: CloseXsdToXmiMappingReportAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/GME/actions/CloseXsdToXmiMappingReportAction.java,v 1.2 2008-06-09 19:54:05 phadkes Exp $";

	public CloseXsdToXmiMappingReportAction(DefaultContextManagerClientPanel contentPanel)
	{
		super(COMMAND_NAME, null, contentPanel);
		clientPanel=contentPanel;
	}

}

