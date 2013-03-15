/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping;

import gov.nih.nci.caadapter.ui.common.jgraph.*;
import gov.nih.nci.caadapter.ui.mapping.jgraph.MiddlePanelJGraphController;
import gov.nih.nci.caadapter.ui.mapping.jgraph.MiddlePanelJGraphScrollAdjustmentAdapter;
import gov.nih.nci.caadapter.ui.mapping.jgraph.MiddlePanelScrollAdjustmentCoordinator;

import org.jgraph.graph.GraphModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.dnd.DnDConstants;

/**
 * The panel is used to render graphical respresentation of the mapping relations between
 * source and target tree panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:53:55 $
 */
public class MappingMiddlePanel extends JPanel //implements ActionListener
{
	private MiddlePanelJGraph graph = null;
	private MiddlePanelJGraphController graphController = null;
	private MiddlePanelJGraphViewFactory graphViewFactory = null;
	private MiddlePanelJGraphDropTargetHandler middlePanelDropTransferHandler = null;
	private AbstractMappingPanel mappingPanel = null;
	private String kind ="";
	private JScrollPane graphScrollPane = new JScrollPane();
	private MiddlePanelScrollAdjustmentCoordinator adjustmentCoordinator = null;
	private MiddlePanelJGraphScrollAdjustmentAdapter graphAdjustmentAdapter = null;
	//for test purpose
	private GraphModel model = null;

    public MappingMiddlePanel(AbstractMappingPanel mappingPanel)
	{
        this.mappingPanel = mappingPanel;
		setBorder(BorderFactory.createRaisedBevelBorder());
		setLayout(new BorderLayout());
		graphScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		graphScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		MappingPanelAdjustmentHandler listener=new MappingPanelAdjustmentHandler();
		listener.addAdjustmentObserver(mappingPanel.getSourceScrollPane());
		listener.addAdjustmentObserver(mappingPanel.getTargetScrollPane());
		graphScrollPane.getVerticalScrollBar().addAdjustmentListener(listener);
		add(graphScrollPane, BorderLayout.CENTER);
		initGraph();
    }

	public void resetGraph()
	{
		if(graphController.isGraphChanged())
		{
			initGraph();
		}
	}

	public JScrollPane getGraphScrollPane()
	{
		return graphScrollPane;
	}

	private void initGraph()
	{
		boolean changed = false;
		model = new MiddlePanelGraphModel();
		if (graph == null)
		{
			graph = new MiddlePanelJGraph(model);
			graphController = null;
			graphScrollPane.getViewport().setView(graph);
			adjustmentCoordinator = new MiddlePanelScrollAdjustmentCoordinator(this, graphScrollPane);
			graphAdjustmentAdapter = new MiddlePanelJGraphScrollAdjustmentAdapter(this);
			changed = true;
		}
		else
		{
			graph.setModel(model);
		}
		if (graphController == null)
		{
			graphController = new MiddlePanelJGraphController(graph, this, mappingPanel);
			changed = true;
		}
		else
		{
			graphController.setJGraph(graph);
		}
		if(changed)
		{
			graphViewFactory = new MiddlePanelJGraphViewFactory();
			graph.getGraphLayoutCache().setFactory(graphViewFactory);
			middlePanelDropTransferHandler = new MiddlePanelJGraphDropTargetHandler(graph, graphController, DnDConstants.ACTION_LINK);//DnDConstants.ACTION_LINK,
		}
	}


    public MiddlePanelJGraphController getGraphController(){
        return this.graphController;
    }

    public MappingDataManager getMappingDataManager()
	{
		return this.graphController;
	}

	public void paintComponent(Graphics g)
	{
//        System.out.println("test.gov.nih.nci.hl7.jgraph.MiddlePanel.paintComponent");
		super.paintComponent(g);
		if (mappingPanel == null)
		{
			return;// || mappingPanel.getMappings() == null) return;
		}
		graphController.renderInJGraph(g);
	}

    public Object getFunctionBoxSelection()
	{
		return mappingPanel.getFunctionPane().getFunctionSelection();
	}

	public void setPreferredSize(Dimension preferredSize)
	{
//		System.out.println("MappingMiddlePanel's setPreferredSize()");
		graph.setPreferredSize(new Dimension(preferredSize.width - 4, preferredSize.height - 25));
	}

	/**
	 * Return the middlePanelDropTransferHandler.
	 * @return the middlePanelDropTransferHandler.
	 */
	public MiddlePanelJGraphDropTargetHandler getMiddlePanelDropTransferHandler()
	{
		return middlePanelDropTransferHandler;
	}

	public MiddlePanelScrollAdjustmentCoordinator getAdjustmentCoordinator()
	{
		return adjustmentCoordinator;
	}

	public MiddlePanelJGraphScrollAdjustmentAdapter getGraphAdjustmentAdapter()
	{
		return graphAdjustmentAdapter;
	}

	public AbstractMappingPanel getMappingPanel()
	{
		return mappingPanel;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/10/18 20:16:21  jayannah
 * HISTORY      : -Added a new method in MappingMiddlePanel to get the reference to MiddlePanelJGraphController
 * HISTORY      : -Added a new method in MiddlePanelJGraphController to get the reference to linkselectionhighlighter
 * HISTORY      : -Added linkselectionhighlighter to source and targe trees as tree selection listener
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:36  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.31  2006/10/10 17:17:51  wuye
 * HISTORY      : Add an attribute kind to distinguish the delete action
 * HISTORY      :
 * HISTORY      : Revision 1.30  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.29  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.28  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.27  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.26  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.25  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.24  2005/11/23 19:48:52  jiangsc
 * HISTORY      : Enhancement on mapping validations.
 * HISTORY      :
 * HISTORY      : Revision 1.23  2005/11/09 23:05:51  jiangsc
 * HISTORY      : Back to previous version.
 * HISTORY      :
 * HISTORY      : Revision 1.21  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.20  2005/10/21 15:11:55  jiangsc
 * HISTORY      : Resolve scrolling issue.
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/10/20 22:29:29  jiangsc
 * HISTORY      : Resolve scrolling issue.
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/10/20 20:31:50  jiangsc
 * HISTORY      : to Scroll consistently for source, target, and map panel on the HL7MappingPanel.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/10/18 17:01:03  jiangsc
 * HISTORY      : Changed one function signature in DragDrop component;
 * HISTORY      : Enhanced drag-drop status monitoring in HL7MappingPanel;
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/10/18 15:46:19  jiangsc
 * HISTORY      : Added scroll pane to allow more vertical visibility.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/08/04 22:22:17  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */
