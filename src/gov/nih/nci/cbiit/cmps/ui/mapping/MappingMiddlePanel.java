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
import gov.nih.nci.cbiit.cmps.ui.jgraph.MiddlePanelJGraphDropTargetHandler;
import gov.nih.nci.cbiit.cmps.ui.jgraph.MiddlePanelJGraphScrollAdjustmentAdapter;
import gov.nih.nci.cbiit.cmps.ui.jgraph.MiddlePanelJGraphViewFactory;
import gov.nih.nci.cbiit.cmps.ui.jgraph.MiddlePanelScrollAdjustmentCoordinator;

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
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-30 16:02:14 $
 *
 */
public class MappingMiddlePanel extends JPanel //implements ActionListener
{
	private MiddlePanelJGraph graph = null;
	private MiddlePanelJGraphController graphController = null;
	private MiddlePanelJGraphViewFactory graphViewFactory = null;
	private MiddlePanelJGraphDropTargetHandler middlePanelDropTransferHandler = null;
	private CmpsMappingPanel mappingPanel = null;
	private String kind ="";
	private JScrollPane graphScrollPane = new JScrollPane();
	private MiddlePanelScrollAdjustmentCoordinator adjustmentCoordinator = null;
	private MiddlePanelJGraphScrollAdjustmentAdapter graphAdjustmentAdapter = null;
	//for test purpose
	private GraphModel model = null;

    public MappingMiddlePanel(CmpsMappingPanel mappingPanel)
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

    public MiddlePanelJGraphController getMiddlePanelJGraphController()
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
    	return null;
		//return mappingPanel.getFunctionPane().getFunctionSelection();
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

	public CmpsMappingPanel getMappingPanel()
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
 * HISTORY: $Log: not supported by cvs2svn $
 */
