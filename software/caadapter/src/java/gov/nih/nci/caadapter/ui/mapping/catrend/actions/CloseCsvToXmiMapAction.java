/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.mapping.catrend.actions;


import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.actions.DefaultContextCloseAction;
import gov.nih.nci.caadapter.ui.common.context.DefaultContextManagerClientPanel;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.catrend.CsvToXmiMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.catrend.CsvToXmiMappingReportPanel;
import gov.nih.nci.caadapter.ui.mapping.hl7.actions.CloseMapAction;


/**
 * This class defines the close action of HL7 panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-09-29 20:28:13 $
 */
public class CloseCsvToXmiMapAction extends CloseMapAction
{
	private DefaultContextManagerClientPanel clientPanel;
	@Override
	protected boolean doAction(ActionEvent e) {
		// TODO Auto-generated method stub
		CsvToXmiMappingPanel mpPane=(CsvToXmiMappingPanel)clientPanel;
		System.out.println("CloseCsvToXmiMapAction.doAction()...reporting pane:"+mpPane.getReportPanel());

		if (mpPane.getReportPanel()!=null)
		{
	        JFrame holderFrame=findHolderFrame(mpPane);
			AbstractMainFrame mainFrame= (AbstractMainFrame)holderFrame;
			if (mpPane.getReportPanel().getReporter()!=null)
			{
				mainFrame.getTabbedPane().setSelectedComponent(mpPane.getReportPanel());
				mainFrame.closeTab();
			}
//			CloseCsvToXmiMappingReportAction clsReportAction=(CloseCsvToXmiMappingReportAction)mpPane.getDefaultCloseAction();
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
	private static final String LOGID = "$RCSfile: CloseCsvToXmiMapAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/catrend/actions/CloseCsvToXmiMapAction.java,v 1.4 2008-09-29 20:28:13 wangeug Exp $";

	public CloseCsvToXmiMapAction(DefaultContextManagerClientPanel contentPanel)
	{
		super(COMMAND_NAME, null, (AbstractMappingPanel)contentPanel);
		clientPanel=contentPanel;
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
**/
