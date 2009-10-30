/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmps.ui.mapping;

import org.jgraph.graph.GraphModel;

import gov.nih.nci.cbiit.cmps.ui.jgraph.MiddlePanelGraphModel;
import gov.nih.nci.cbiit.cmps.ui.jgraph.MiddlePanelJGraph;
import gov.nih.nci.cbiit.cmps.ui.jgraph.MiddlePanelJGraphController;
import gov.nih.nci.cbiit.cmps.ui.jgraph.MiddlePanelJGraphScrollAdjustmentAdapter;
import gov.nih.nci.cbiit.cmps.ui.jgraph.MiddlePanelJGraphViewFactory;
import gov.nih.nci.cbiit.cmps.ui.jgraph.MiddlePanelScrollAdjustmentCoordinator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.ScrollPaneConstants;

import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * The panel is used to render graphical respresentation of the mapping relations between
 * source and target tree panel.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMPS v1.0
 * @version    $Revision: 1.3 $
 * @date       $Date: 2009-10-30 14:45:09 $
 *
 */
public class MappingMiddlePanel extends JPanel //implements ActionListener
{
	private MiddlePanelScrollAdjustmentCoordinator adjustmentCoordinator = null;
	private MiddlePanelJGraph graph = null;

	private MiddlePanelJGraphScrollAdjustmentAdapter graphAdjustmentAdapter = null;

	private MiddlePanelJGraphController graphController = null;
	private JScrollPane graphScrollPane = new JScrollPane();
	private CmpsMappingPanel mappingPanel = null;

	public MappingMiddlePanel(CmpsMappingPanel mappingPanel)
	{
        this.mappingPanel = mappingPanel;
		setBorder(BorderFactory.createRaisedBevelBorder());
		setLayout(new BorderLayout());
		graphScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		graphScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		MappingPanelAdjustmentHandler listener=new MappingPanelAdjustmentHandler();
		listener.addAdjustmentObserver(mappingPanel.getSourceScrollPane());
		listener.addAdjustmentObserver(mappingPanel.getTargetScrollPane());
		graphScrollPane.getVerticalScrollBar().addAdjustmentListener(listener);
		add(graphScrollPane, BorderLayout.CENTER);
		initGraph();
    }
	public MiddlePanelScrollAdjustmentCoordinator getAdjustmentCoordinator()
	{
		return adjustmentCoordinator;
	}

    public Object getFunctionBoxSelection()
	{
    	return null;
		//return mappingPanel.getFunctionPane().getFunctionSelection();
	}

	/**
	 * @return the graph
	 */
	public MiddlePanelJGraph getGraph() {
		return graph;
	}

	public MiddlePanelJGraphScrollAdjustmentAdapter getGraphAdjustmentAdapter()
	{
		return graphAdjustmentAdapter;
	}

	public MiddlePanelJGraphController getGraphController(){
        return this.graphController;
    }

   
    public JScrollPane getGraphScrollPane()
	{
		return graphScrollPane;
	}

	public CmpsMappingPanel getMappingPanel()
	{
		return mappingPanel;
	}

    public MiddlePanelJGraphController getMiddlePanelJGraphController()
	{
		return this.graphController;
	}

	private void initGraph()
	{
		boolean changed = false;
		GraphModel model = new MiddlePanelGraphModel();
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
			MiddlePanelJGraphViewFactory	graphViewFactory = new MiddlePanelJGraphViewFactory();
			graph.getGraphLayoutCache().setFactory(graphViewFactory);
		}
	}

	@Override
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

	public void resetGraph()
	{
		if(graphController.isGraphChanged())
		{
			initGraph();
		}
	}

	/**
	 * @param graph the graph to set
	 */
	public void setGraph(MiddlePanelJGraph graph) {
		this.graph = graph;
	}

	@Override
	public void setPreferredSize(Dimension preferredSize)
	{
//		System.out.println("MappingMiddlePanel's setPreferredSize()");
		graph.setPreferredSize(new Dimension(preferredSize.width - 4, preferredSize.height - 25));
	}
}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.2  2008/12/04 21:34:20  linc
 * HISTORY: Drap and Drop support with new Swing.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/10/30 16:02:14  linc
 * HISTORY: updated.
 * HISTORY:
 */
