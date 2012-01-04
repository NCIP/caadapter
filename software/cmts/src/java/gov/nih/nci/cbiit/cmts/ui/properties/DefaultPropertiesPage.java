/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */


package gov.nih.nci.cbiit.cmts.ui.properties;

import java.awt.Dimension;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.mapping.CDEPropertyPanel;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
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
public class DefaultPropertiesPage extends JSplitPane
{
	private TitledBorder titledBorder = null;
	private PropertiesSwitchController propertiesController;
	private DefaultPropertiesTableModel tableModel;
	private CDEPropertyPanel cdePane;

	/**
	 * Creates a new <code>JPanel</code> with a double buffer
	 * and a flow layout.
	 */
	public DefaultPropertiesPage(PropertiesSwitchController propertiesController)
	{
		super(JSplitPane.VERTICAL_SPLIT);
		this.propertiesController = propertiesController;
		if(propertiesController!=null)
			this.propertiesController.setPropertiesPage(this);
		initialize();
	}

	private void initialize()
	{
        JScrollPane scrollPane = new JScrollPane();
		tableModel = new DefaultPropertiesTableModel(propertiesController);
		JTable propertiesTable = new JTable(tableModel);
		propertiesTable.setShowGrid(true);
		ListSelectionModel tableListSelectionModel = propertiesTable.getSelectionModel();
		tableListSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.getViewport().setView(propertiesTable);
		titledBorder = BorderFactory.createTitledBorder(propertiesController!=null?propertiesController.getTitleOfPropertiesPage():"info");
		setBorder(titledBorder);

		cdePane=new CDEPropertyPanel();
		setTopComponent(cdePane);
		setBottomComponent( scrollPane );
		setPreferredSize(new Dimension(DefaultSettings.FRAME_DEFAULT_WIDTH /4, DefaultSettings.FRAME_DEFAULT_HEIGHT / 10));
		setDividerLocation(0);
	}

	public void updateProptiesDisplay(ChangeEvent e)
	{
		titledBorder.setTitle(propertiesController.getTitleOfPropertiesPage());
		tableModel.setPropertiesResult(propertiesController.getPropertyDescriptors());
		cdePane.updateSelection(propertiesController.getSelectedItem());
        if (!cdePane.doesHaveOwnData())
        {
            setDividerLocation(0);
        }
       
        else setDividerLocation(120);
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
