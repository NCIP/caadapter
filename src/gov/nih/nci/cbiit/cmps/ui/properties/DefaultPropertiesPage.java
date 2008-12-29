/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */


package gov.nih.nci.cbiit.cmps.ui.properties;


import gov.nih.nci.cbiit.cmps.ui.common.DefaultSettings;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import java.awt.*;

/**
 * This class defines the default properties pane view.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-29 22:18:18 $
 */
public class DefaultPropertiesPage extends JPanel
{

	private JTable propertiesTable;
	private JScrollPane scrollPane = null;
	private TitledBorder titledBorder = null;
	private PropertiesSwitchController propertiesController;
	private DefaultPropertiesTableModel tableModel;

	/**
	 * Creates a new <code>JPanel</code> with a double buffer
	 * and a flow layout.
	 */
	public DefaultPropertiesPage(PropertiesSwitchController propertiesController)
	{
		this.propertiesController = propertiesController;
		if(propertiesController!=null)
			this.propertiesController.setPropertiesPage(this);
		initialize();
	}

	private void initialize()
	{
        setLayout(new BorderLayout());
		scrollPane = new JScrollPane();
		tableModel = new DefaultPropertiesTableModel(propertiesController);
		propertiesTable = new JTable(tableModel);
		propertiesTable.setShowGrid(true);
		ListSelectionModel tableListSelectionModel = propertiesTable.getSelectionModel();
		tableListSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.getViewport().setView(propertiesTable);
		titledBorder = BorderFactory.createTitledBorder(propertiesController!=null?propertiesController.getTitleOfPropertiesPage():"info");
		scrollPane.setBorder(titledBorder);
		this.add(scrollPane, BorderLayout.CENTER);
		setPreferredSize(new Dimension(DefaultSettings.FRAME_DEFAULT_WIDTH /4, DefaultSettings.FRAME_DEFAULT_HEIGHT / 10));
	}

	public void updateProptiesDisplay(ChangeEvent e)
	{
		titledBorder.setTitle(propertiesController.getTitleOfPropertiesPage());
		scrollPane.repaint();
		//tableModel.setPropertiesResult(propertiesController.getPropertyDescriptors());
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
