/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.common.properties;

import gov.nih.nci.caadapter.common.util.Config;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import java.awt.*;

/**
 * This class defines the default properties pane view.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class DefaultPropertiesPage extends JPanel
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: DefaultPropertiesPage.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/properties/DefaultPropertiesPage.java,v 1.2 2008-06-09 19:53:51 phadkes Exp $";

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
		titledBorder = BorderFactory.createTitledBorder(propertiesController.getTitleOfPropertiesPage());
		scrollPane.setBorder(titledBorder);
		this.add(scrollPane, BorderLayout.CENTER);
		setPreferredSize(new Dimension(Config.FRAME_DEFAULT_WIDTH /4, Config.FRAME_DEFAULT_HEIGHT / 10));
	}

	public void updateProptiesDisplay(ChangeEvent e)
	{
		titledBorder.setTitle(propertiesController.getTitleOfPropertiesPage());
		scrollPane.repaint();
		tableModel.setPropertiesResult(propertiesController.getPropertyDescriptors());
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/23 18:57:18  jiangsc
 * HISTORY      : Implemented the new Properties structure
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/04 22:22:26  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/18 19:45:57  jiangsc
 * HISTORY      : Added textual display for functions and properties.
 * HISTORY      : Beautified port display.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/11 18:17:56  jiangsc
 * HISTORY      : Partially implemented property pane.
 * HISTORY      :
 */
