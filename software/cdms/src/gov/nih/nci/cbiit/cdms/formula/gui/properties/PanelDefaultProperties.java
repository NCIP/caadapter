/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui.properties;

import gov.nih.nci.cbiit.cdms.formula.core.BaseMeta;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

public class PanelDefaultProperties extends JPanel {

		private TitledBorder titledBorder = null;
		private TableModelDefaultProperties tableModel;
        JScrollPane scrollPane;

    private BaseMeta controllMeta;

        /**
		 * Creates a new <code>JPanel</code> with a double buffer
		 * and a flow layout.
		 */
		public PanelDefaultProperties(BaseMeta meta)
		{
			super();
			initialize(meta);
		}

		private void initialize(BaseMeta meta)
		{
	        setLayout(new BorderLayout());
	        //JScrollPane scrollPane = new JScrollPane();
            scrollPane = new JScrollPane();
            tableModel = new TableModelDefaultProperties();//propertiesController);
			JTable propertiesTable = new JTable(tableModel);
			propertiesTable.setShowGrid(true);
			ListSelectionModel tableListSelectionModel = propertiesTable.getSelectionModel();
			tableListSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			scrollPane.getViewport().setView(propertiesTable);
			titledBorder = BorderFactory.createTitledBorder("");

			scrollPane.setBorder(titledBorder);
			this.add(scrollPane, BorderLayout.CENTER);
			setPreferredSize(new Dimension(250,150));
			if (meta!=null)
			updateProptiesDisplay(meta);
		}

		public void updateProptiesDisplay(BaseMeta controllMeta )
		{
            titledBorder = BorderFactory.createTitledBorder(controllMeta.getTitle());
            scrollPane.setBorder(titledBorder);
            //titledBorder.setTitle(controllMeta.getTitle());	
			try {
				tableModel.setPropertiesResult(controllMeta.getPropertyDescriptors());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            this.controllMeta = controllMeta;
        }
        public BaseMeta getCurrentBaseMeta()
        {
            return controllMeta;
        }
}
