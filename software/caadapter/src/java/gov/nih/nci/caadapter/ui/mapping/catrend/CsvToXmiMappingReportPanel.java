/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.ui.mapping.catrend;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.context.DefaultContextManagerClientPanel;
import gov.nih.nci.caadapter.ui.common.context.ContextManager;
import gov.nih.nci.caadapter.ui.common.context.MenuConstants;
import gov.nih.nci.caadapter.ui.common.message.ValidationMessagePane;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JSplitPane;
import javax.swing.JLabel;
import javax.swing.JComponent;

import javax.swing.Action;
import javax.swing.BorderFactory;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;

/**
 * This class is the main entry point to display HL7V3 message panel.
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-09-29 20:27:57 $
 */
public class CsvToXmiMappingReportPanel extends DefaultContextManagerClientPanel
{

    private JTextField sourceFileNameField;
    private JTextField targetFileNameField;

    private CsvToXmiMappingReporter reporter;
    private JScrollPane scrollPane = null;
    private ValidationMessagePane validationMessagePane = null;

	public CsvToXmiMappingReportPanel()
    {
        setLayout(new BorderLayout());
        add(contructNorthPanel(), BorderLayout.NORTH);
		add(contructCenterPanel(), BorderLayout.CENTER);
	}

	private JComponent contructNorthPanel()
	{
		JPanel fieldsOuterPanel = new JPanel(new BorderLayout());
		JPanel fieldsPanel = new JPanel(new GridBagLayout());
		Insets insets = new Insets(5, 5, 5, 5);
		int posY = 0;
		JLabel dataFileNameLabel = new JLabel("Source File:");
		fieldsPanel.add(dataFileNameLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		sourceFileNameField = new JTextField();
		Dimension fieldDimension = new Dimension(dataFileNameLabel.getPreferredSize().width, sourceFileNameField.getPreferredSize().height);
		sourceFileNameField.setEditable(false);
		sourceFileNameField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		sourceFileNameField.setPreferredSize(fieldDimension);
		fieldsPanel.add(sourceFileNameField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		posY++;
		JLabel mapFileNameLabel = new JLabel("Target File:");
		fieldsPanel.add(mapFileNameLabel, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		targetFileNameField = new JTextField();
		fieldDimension = new Dimension(mapFileNameLabel.getPreferredSize().width, targetFileNameField.getPreferredSize().height);
		targetFileNameField.setEditable(false);
		targetFileNameField.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		targetFileNameField.setPreferredSize(fieldDimension);
		fieldsPanel.add(targetFileNameField, new GridBagConstraints(1, posY, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		fieldsOuterPanel.add(fieldsPanel, BorderLayout.CENTER);

		return fieldsOuterPanel;
	}

	private JComponent contructCenterPanel()
	{
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		DefaultSettings.setDefaultFeatureForJSplitPane(splitPane);
		splitPane.setBorder(BorderFactory.createEmptyBorder());

		scrollPane = new JScrollPane();
		setReporter(null);
		scrollPane.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3), (int) (Config.FRAME_DEFAULT_HEIGHT / 2)));
		splitPane.setTopComponent(scrollPane);

		validationMessagePane = new ValidationMessagePane();
		//turn off the display as popup dialog but display it at other location.
		validationMessagePane.setDisplayPopupConfirmationMessage(false);
		validationMessagePane.setValidatorResults(null);
		validationMessagePane.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3), (int) (Config.FRAME_DEFAULT_HEIGHT / 10)));

		splitPane.setBottomComponent(validationMessagePane);

		splitPane.setDividerLocation(0.8);
		return splitPane;
	}



    public Map getMenuItems(String menu_name)
	{
		Action action = null;
		ContextManager contextManager = ContextManager.getContextManager();
		String menuMapName="VALIDATE_CSV_TO_XMI_MAPPING";
		Map <String, Action>actionMap = contextManager.getClientMenuActions(menuMapName, menu_name);
				action = new gov.nih.nci.caadapter.ui.mapping.catrend.actions.SaveCsvToXmiMappingReportAction(this);
				contextManager.addClientMenuAction(menuMapName, MenuConstants.FILE_MENU_NAME,ActionConstants.SAVE, action);
				contextManager.addClientMenuAction(menuMapName, MenuConstants.TOOLBAR_MENU_NAME,ActionConstants.SAVE, action);
				action.setEnabled(true);

				action = new gov.nih.nci.caadapter.ui.mapping.catrend.actions.SaveAsCsvToXmiMappingReportAction(this);
				contextManager.addClientMenuAction(menuMapName, MenuConstants.FILE_MENU_NAME,ActionConstants.SAVE_AS, action);
				contextManager.addClientMenuAction(menuMapName, MenuConstants.TOOLBAR_MENU_NAME,ActionConstants.SAVE_AS, action);
				action.setEnabled(true);

				action = new gov.nih.nci.caadapter.ui.mapping.catrend.actions.CloseCsvToXmiMappingReportAction(this);
				contextManager.addClientMenuAction(menuMapName, MenuConstants.FILE_MENU_NAME,ActionConstants.CLOSE, action);
				action.setEnabled(true);

				actionMap = contextManager.getClientMenuActions(menuMapName, menu_name);

		return actionMap;
	}


    /**
     * return the open action inherited with this client.
     */
    public Action getDefaultOpenAction()
    {
    	ContextManager contextManager = ContextManager.getContextManager();
        Action openAction = null;
        return openAction;
    }


	public boolean isChanged() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setChanged(boolean newValue) {
		if (newValue)
			try {
				getReporter().reload();
				reload();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public CsvToXmiMappingReporter getReporter() {
		return reporter;
	}

	public void setReporter(CsvToXmiMappingReporter reporter) {
		this.reporter = reporter;
		try {
			reload();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void reload() throws Exception {
		// TODO Auto-generated method stub
		String rptText="";
		if (reporter!=null)
		{
			sourceFileNameField.setText(reporter.getSourceFileName());
			targetFileNameField.setText(reporter.getTargetFileName());
			rptText=reporter.getReportAsString();
		}
		JTextArea outputMessageArea = new JTextArea(rptText);
        outputMessageArea.setEditable(false);
        outputMessageArea.setBackground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
        scrollPane.getViewport().setView(outputMessageArea);
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
**/