/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping.GME.actions;


import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.MappableNode;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.common.actions.DefaultValidateAction;
import gov.nih.nci.caadapter.ui.common.tree.MappingBaseTree;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.GME.XsdToXmiMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.GME.XsdToXmiMappingReportPanel;
import gov.nih.nci.caadapter.ui.mapping.GME.XsdToXmiMappingReporter;
import gov.nih.nci.caadapter.ui.mapping.catrend.CsvToXmiMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.catrend.CsvToXmiMappingReportPanel;
import gov.nih.nci.caadapter.ui.mapping.catrend.CsvToXmiMappingReporter;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * This class defines the action to invoke validation of HSM.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:54:05 $
 */
public class ValidateXsdToXmiMapAction extends AbstractContextAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: ValidateXsdToXmiMapAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/GME/actions/ValidateXsdToXmiMapAction.java,v 1.2 2008-06-09 19:54:05 phadkes Exp $";

	private static final String COMMAND_NAME = DefaultValidateAction.COMMAND_NAME;
	private static final Character COMMAND_MNEMONIC = DefaultValidateAction.COMMAND_MNEMONIC;
	private static final ImageIcon IMAGE_ICON = DefaultValidateAction.IMAGE_ICON;
	private static final String TOOL_TIP_DESCRIPTION = DefaultValidateAction.TOOL_TIP_DESCRIPTION;

	private transient AbstractMappingPanel parentPanel;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public ValidateXsdToXmiMapAction(AbstractMappingPanel parentPanel)
	{
		this(COMMAND_NAME, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public ValidateXsdToXmiMapAction(String name, AbstractMappingPanel parentPanel)
	{
		this(name, IMAGE_ICON, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public ValidateXsdToXmiMapAction(String name, Icon icon, AbstractMappingPanel parentPanel)
	{
		super(name, icon);
		this.parentPanel = parentPanel;
		setAdditionalAttributes();
	}

	protected void setAdditionalAttributes()
	{
		setMnemonic(COMMAND_MNEMONIC);
		//		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
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
		System.out.println("ValidateXsdToXmiMapAction.doAction()");
        //generate mapping report:
		XsdToXmiMappingPanel mappingPanel=(XsdToXmiMappingPanel)parentPanel;
        MappingBaseTree mappingBaseTree=(MappingBaseTree)mappingPanel.getSourceTree();

        XsdToXmiMappingReportPanel mpReportPane = mappingPanel.getReportPanel();
        boolean createNewReport=false;

        if (mpReportPane==null)
        {
        	createNewReport=true;
        }
        else if (mpReportPane.getReporter()==null)
        	createNewReport=true;
        JFrame holderFrame=findHolderFrame(parentPanel);
		AbstractMainFrame mainFrame= (AbstractMainFrame)holderFrame;

        if (createNewReport)
        {
        	mpReportPane=new XsdToXmiMappingReportPanel();
        	mappingPanel.setReportPanel(mpReportPane);
        	XsdToXmiMappingReporter reporter=new XsdToXmiMappingReporter((MappableNode)mappingBaseTree.getRootTreeNode(), XsdToXmiMappingReporter.REPORT_UNMAPPED);
        	reporter.setSourceFileName(mappingPanel.getSourceFileName());
        	reporter.setTargetFileName(mappingPanel.getTargetFileName());
        	mpReportPane.setReporter(reporter);
        	mainFrame.addNewTab(mpReportPane);
        	mpReportPane.setChanged(false);
        }
        else
        {
        	mpReportPane.getReporter().setTreeNode((MappableNode)mappingBaseTree.getRootTreeNode());
        	mpReportPane.setChanged(true);
        	//bring the report pane to front
        	mainFrame.getTabbedPane().setSelectedComponent(mpReportPane);
        }


		return true;
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
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return parentPanel;
	}

}
