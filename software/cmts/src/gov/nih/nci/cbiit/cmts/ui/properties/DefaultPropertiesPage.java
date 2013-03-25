/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */


package gov.nih.nci.cbiit.cmts.ui.properties;


import java.awt.BorderLayout;
import java.awt.Dimension;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;

/**
 * This class defines the default properties pane view.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.3 $
 * @date       $Date: 2009-10-28 15:01:45 $
 */
public class DefaultPropertiesPage extends JPanel
{
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
        JScrollPane scrollPane = new JScrollPane();
		tableModel = new DefaultPropertiesTableModel(propertiesController);
		JTable propertiesTable = new JTable(tableModel);
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
		tableModel.setPropertiesResult(propertiesController.getPropertyDescriptors());
		this.repaint();
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2009/10/27 18:23:25  wangeug
 * HISTORY      : hook property panel with tree nodes
 * HISTORY      :
 * HISTORY      : Revision 1.1  2008/12/29 22:18:18  linc
 * HISTORY      : function UI added.
 * HISTORY      :
 */
