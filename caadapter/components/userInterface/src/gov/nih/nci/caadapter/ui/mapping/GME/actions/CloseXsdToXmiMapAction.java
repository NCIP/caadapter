/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping.GME.actions;


import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.context.DefaultContextManagerClientPanel;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.GME.XsdToXmiMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.catrend.CsvToXmiMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.hl7.actions.CloseMapAction;


/**
 * This class defines the close action of HL7 panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:54:05 $
 */
public class CloseXsdToXmiMapAction extends CloseMapAction
{
	private DefaultContextManagerClientPanel clientPanel;
	@Override
	protected boolean doAction(ActionEvent e) {
		// TODO Auto-generated method stub
		XsdToXmiMappingPanel mpPane=(XsdToXmiMappingPanel)clientPanel;
		System.out.println("CloseXsdToXmiMapAction.doAction()...reporting pane:"+mpPane.getReportPanel());

		if (mpPane.getReportPanel()!=null)
		{
	        JFrame holderFrame=findHolderFrame(mpPane);
			AbstractMainFrame mainFrame= (AbstractMainFrame)holderFrame;
			if (mpPane.getReportPanel().getReporter()!=null)
			{
				mainFrame.getTabbedPane().setSelectedComponent(mpPane.getReportPanel());
				mainFrame.closeTab();
			}
//			CloseXsdToXmiMappingReportAction clsReportAction=(CloseXsdToXmiMappingReportAction)mpPane.getDefaultCloseAction();
//			clsReportAction.doAction(e);
		}
		return super.doAction(e);
	}

	private JFrame findHolderFrame(JPanel holder)
	{
		Container container=holder.getParent();
		while (!( container instanceof JFrame))
		{
			container= container.getParent();
		}
		return (JFrame)container;
	}
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: CloseXsdToXmiMapAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/GME/actions/CloseXsdToXmiMapAction.java,v 1.2 2008-06-09 19:54:05 phadkes Exp $";

	public CloseXsdToXmiMapAction(DefaultContextManagerClientPanel contentPanel)
	{
		super(COMMAND_NAME, null, (AbstractMappingPanel)contentPanel);
		clientPanel=contentPanel;
	}

}

