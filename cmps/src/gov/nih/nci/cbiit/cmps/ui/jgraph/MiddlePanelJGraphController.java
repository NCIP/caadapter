/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmps.ui.jgraph;

import org.jgraph.JGraph;
import org.jgraph.graph.*;

import gov.nih.nci.cbiit.cmps.core.Mapping;
import gov.nih.nci.cbiit.cmps.ui.common.MappableNode;
import gov.nih.nci.cbiit.cmps.ui.mapping.CmpsMappingPanel;
import gov.nih.nci.cbiit.cmps.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.cbiit.cmps.ui.tree.DefaultSourceTreeNode;

import javax.swing.JTree;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;

/**
 * This is the controller class of Middle Panel JGraph implementation. The MiddlePanelJGraphController class will deal with real implementation of some of
 * actions to modify (mainly CRUD) upon graph, and mainly focuses on drag-and-drop and handlings of repaint of graph, for example. MiddlePanelMarqueeHandler
 * will help handle key and mouse driven events such as display pop menus, etc.
 * 
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-30 16:02:14 $
 *
 */
public class MiddlePanelJGraphController 
{
	private MiddlePanelJGraph graph = null;

	private MiddlePanelMarqueeHandler marqueeHandler = null;

	// the parent panels
	private MappingMiddlePanel middlePanel = null;

	private CmpsMappingPanel mappingPanel = null;

	// a list of MappingViewCommonComponent
	private List mappingViewList = null;

	private Mapping mappingData = null;

	private boolean isGraphChanged = false;

//	private MappingPanelPropertiesSwitchController propertiesSwitchController;

	private Color graphBackgroundColor = new Color(222, 238, 255);

//	private FunctionBoxViewUsageManager usageManager;

	private LinkSelectionHighlighter linkSelectionHighlighter;
	
	
    public LinkSelectionHighlighter getHighLighter(){
            return linkSelectionHighlighter;
    }
	// 
	// Construct the Graph using the Model as its Data Source
	public MiddlePanelJGraphController(MiddlePanelJGraph graph, MappingMiddlePanel middlePanel, CmpsMappingPanel mappingPanel) {
		this.graph = graph;
		// this.model = graph.getModel();
		this.middlePanel = middlePanel;
		this.mappingPanel = mappingPanel;
		initialization(false);
	}
	


	public void setJGraph(MiddlePanelJGraph newGraph)
	{
		this.graph = null;
		if ( linkSelectionHighlighter != null && this.graph != null ) {
			this.graph.removeGraphSelectionListener(linkSelectionHighlighter);
		}
		this.graph = newGraph;
		initialization(true);
	}

	private void initialization(boolean keepSourceTargetComponent)
	{
		// dropTarget = new DropTarget(graph, DnDConstants.ACTION_LINK, this);
		// Use a Custom Marquee Handler
		marqueeHandler = new MiddlePanelMarqueeHandler(graph, this);
		graph.setMarqueeHandler(marqueeHandler); 
		// Make Ports Visible by Default
		this.graph.setPortsVisible(true);
		// Use the Grid (but don't make it Visible)
		this.graph.setGridEnabled(true);
		// Set the Grid Size to 10 Pixel
		this.graph.setGridSize(6);
		// Set the Tolerance to 2 Pixel
		this.graph.setTolerance(2);
		// Accept edits if click on background
		this.graph.setInvokesStopCellEditing(true);
		// dose not allow control-drag
		this.graph.setCloneable(false);
		// // Jump to default port on connect
		// this.graph.setJumpToDefaultPort(true);
		// Container rootPane = csvPanel.getRootPane();
		// if (rootPane != null)
		// {
		// graph.setBackground(rootPane.getBackground());
		// graph.setForeground(rootPane.getBackground());
		// }
		// graph.setPortsVisible(false);
		// graph.setConnectable(true);
		// graph.setAntiAliased(false);
		graph.setSizeable(true);
		// // graph.setBendable(false);
		graph.setDragEnabled(true);
		graph.setDropEnabled(true);
		graph.setEditable(false);
		graph.setMoveable(true);
		graph.setBackground(graphBackgroundColor);
//		graph.getSelectionModel().addGraphSelectionListener(getPropertiesSwitchController());
		// setMappingPairCellMap(Collections.synchronizedMap(new HashMap()));
		setMappingViewList(Collections.synchronizedList(new ArrayList()));
//		if ( this.mappingData != null && keepSourceTargetComponent ) {// just to clear graphs but not the source and target component if any.
//			MappingImpl newMappingImpl = new MappingImpl();
//			newMappingImpl.setSourceComponent(this.mappingData.getSourceComponent());
//			newMappingImpl.setTargetComponent(this.mappingData.getTargetComponent());
//			newMappingImpl.setMappingType(this.mappingData.getMappingType());
//			setMappingData(newMappingImpl);
//		} else {// initialize all
//			setMappingData(new MappingImpl());
//		}
		setGraphChanged(false);
//		usageManager = null;
	}

	/**
	 * To register the highligher into graph and trees.
	 */
	private void registerLinkHighlighter()
	{
		// Register highlighter
		if ( linkSelectionHighlighter != null ) {
			if ( mappingPanel != null ) {
				JTree tree = mappingPanel.getSourceTree();
				if ( tree != null ) {
					tree.removeTreeSelectionListener(linkSelectionHighlighter);
				}
				tree = mappingPanel.getTargetTree();
				if ( tree != null ) {
					tree.removeTreeSelectionListener(linkSelectionHighlighter);
				}
			}
			this.graph.removeGraphSelectionListener(linkSelectionHighlighter);
		}
		linkSelectionHighlighter = new LinkSelectionHighlighter(mappingPanel, this.graph, middlePanel);
		// linkSelectionHighlighter = new LinkSelectionHighlighter(mappingPanel);
		// linkSelectionHighlighter.setGraph(this.graph);
		this.graph.addGraphSelectionListener(linkSelectionHighlighter);
		if ( mappingPanel != null ) {
			JTree tree = mappingPanel.getSourceTree();
			if ( tree != null ) {
				/**
				 * Register the selection listener to the tree level instead of selection model level gives the edge to know where the selection is originated
				 * in the linkSelectionHighlighter.
				 */
				tree.addTreeSelectionListener(linkSelectionHighlighter);
			}
			tree = mappingPanel.getTargetTree();
			if ( tree != null ) {
				tree.addTreeSelectionListener(linkSelectionHighlighter);
			}
		}
	}

	/**
	 * Explicitly set the value.
	 * 
	 * @param newValue
	 */
	public void setGraphChanged(boolean newValue)
	{
		isGraphChanged = newValue;
		if (isGraphChanged)
		{
			//update source and target tree
			mappingPanel.getTargetScrollPane().repaint();
			mappingPanel.getSourceScrollPane().repaint();
		}
	}

	public boolean isGraphChanged()
	{
		return isGraphChanged;
	}

	/**
	 * Return a more concrete implementation of original interface to provide graph selection listener interface.
	 * 
	 * @return MappingPanelPropertiesSwitchController
	 */
//	public MappingPanelPropertiesSwitchController getPropertiesSwitchController()
//	{
//		if ( propertiesSwitchController == null ) {
//			// propertiesSwitchController = new MappingPanelPropertiesSwitchController();
//			propertiesSwitchController = new MappingPanelPropertiesSwitchController(graph);
//		}
//		return propertiesSwitchController; // To change body of implemented methods use File | Settings | File Templates.
//	}

	/**
	 * @param node
	 * @param searchMode
	 *            any of the SEARCH_BY constants defined above.
	 * @return a list of MappingViewCommonComponent if any being found; an empty list if nothing is found.
	 */
//	public List<MappingViewCommonComponent> findMappingViewCommonComponentList(Object node, String searchMode)
//	{
//		return MappingViewCommonComponent.findMappingViewCommonComponentListList(mappingViewList, node, searchMode);
//	}

	public JGraph getGraph()
	{
		return graph;
	}

	public MappingMiddlePanel getMiddlePanel()
	{
		return middlePanel;
	}

	protected List getMappingViewList()
	{
		return mappingViewList;
	}

	/**
	 * Reset the mapping view list.
	 * 
	 * @param mappingViewList
	 */
	protected void setMappingViewList(List mappingViewList)
	{
		if ( this.mappingViewList != null && this.mappingViewList.size() > 0 ) {// clean up the mapping relation before re-assigning
			int size = this.mappingViewList.size();
			for (int i = 0; i < size; i++) {
				Object o = this.mappingViewList.get(i);
//				if ( o instanceof MappingViewCommonComponent ) {
//					MappingViewCommonComponent comp = (MappingViewCommonComponent) o;
//					comp.setMappableFlag(false);
//				}
			}
		}
		this.mappingViewList = mappingViewList;
	}

	public void setMappingData(Mapping mappingData)
	{
		if ( isGraphChanged() || graph.getRoots().length > 0 ) {// if changed, clear them up
			// clean up
			clearAllGraphCells();
		}
		this.mappingData = mappingData;
		if ( mappingData != null ) {
			// clear the flag so that from this point on, any user change on the graph will be considered as change.
			setGraphChanged(false);
			registerLinkHighlighter();
		}
	}

    public void setMappingData(Mapping mappingData, boolean flag)
     {
         setGraphChanged(false);
         registerLinkHighlighter();
     }
    

    /**
	 * Called by MiddlePanelMarqueeHandler Insert a new Edge between source and target
	 */
	public boolean handleConnect(DefaultPort source, DefaultPort target)
	{	
		if ( !source.getEdges().isEmpty() || !target.getEdges().isEmpty() ) {// either port has been used, should report
			StringBuffer msg = new StringBuffer();
			if ( !source.getEdges().isEmpty() ) {
				msg.append("This source port number is being used. Input again another port number.");
			}
			if ( !target.getEdges().isEmpty() ) {
				if ( msg.length() > 0 ) {
					msg.append("\n");
				}
				msg.append("This target port number is being used. Input again another port number.");
			}
			JOptionPane.showMessageDialog(middlePanel.getRootPane().getParent(), msg.toString(), "Mapping Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
//		Log.logInfo(this, getClass().getName() + " will link source and target port.");
		// Construct Edge with no label
		DefaultEdge edge = new DefaultEdge();
		edge.setSource(source);
		edge.setTarget(target);
//		AttributeMap lineStyle = UIHelper.getDefaultUnmovableEdgeStyle(source);
//		if ( graph.getModel().acceptsSource(edge, source) && graph.getModel().acceptsTarget(edge, target) ) {
//			// Create a Map that holds the attributes for the edge
//			edge.getAttributes().applyMap(lineStyle);
//			MappableNode sourceNode = (MappableNode) source;// getMappableNodeThroughPort(source);
//			MappableNode targetNode = (MappableNode) target;// getMappableNodeThroughPort(target);
//			if ( sourceNode == null || targetNode == null ) {
//				StringBuffer msg = new StringBuffer("Cannot find mappable source or target node.");
//				JOptionPane.showMessageDialog(middlePanel.getRootPane().getParent(), msg.toString(), "Mapping Error", JOptionPane.ERROR_MESSAGE);
//			}
//			// Insert the Edge and its Attributes
//			graph.getGraphLayoutCache().insertEdge(edge, source, target);
//			MappingViewCommonComponent comp = new MappingViewCommonComponent(sourceNode, targetNode, source, target, edge);
//			edge.setUserObject(comp);
//			mappingViewList.add(comp);
//			setGraphChanged(true);
//			return true;
//		} else {
//			List reasonList = ((MiddlePanelGraphModel) graph.getModel()).getNotAcceptableReasonList();
//			JOptionPane.showMessageDialog(middlePanel.getRootPane().getParent(), reasonList.toArray(new Object[0]), "Mapping Error", JOptionPane.ERROR_MESSAGE);
//			return false;
//		}
		return false;
	}

      /**
	 * Handle the deletion of all graph cells on the middle panel.
	 */
    public synchronized void handleDeleteAll()
    {     
        clearAllGraphCells();

		//repaint the source and target scrollPanes
        mappingPanel.getSourceScrollPane().repaint();
        mappingPanel.getTargetScrollPane().repaint();

        System.out.println("middlePanel type: " + middlePanel.getKind() );

        setGraphChanged(true);
    }

    /**
	 * Handle the deletion of graph cells on the middle panel.
	 */
	public synchronized void handleDelete()
	{
		Object[] cells = graph.getSelectionCells();
		removeCells(cells, true);
		unmapCells(cells);
		setGraphChanged(true);
	}

	private void unmapCells(Object[] cells)
	{
		//System.out.println("middlePanel kind: " + middlePanel.getKind() );
		for(int i=0; i<cells.length; i++)
		{
			if(cells[i] == null || !(cells[i] instanceof DefaultEdge)) 
				continue;
			DefaultEdge edge = (DefaultEdge) cells[i];

			if ( middlePanel.getKind().equalsIgnoreCase("o2db") ) {
//				MappingViewCommonComponent e = (MappingViewCommonComponent) edge.getUserObject();
//				SDKMetaData sourceSDKMetaData = (SDKMetaData) (((DefaultMutableTreeNode) e.getSourceNode()).getUserObject());
//				SDKMetaData targetSDKMetaData = (SDKMetaData) (((DefaultMutableTreeNode) e.getTargetNode()).getUserObject());
//				CumulativeMappingGenerator cumulativeMappingGenerator = CumulativeMappingGenerator.getInstance();
//				boolean isSuccess = cumulativeMappingGenerator.unmap(sourceSDKMetaData.getXPath(), targetSDKMetaData.getXPath());
//				sourceSDKMetaData.setMapped(false);
			} else if ( middlePanel.getKind().equalsIgnoreCase("SDTM") ) {
//				MappingViewCommonComponent e = (MappingViewCommonComponent) edge.getUserObject();
				@SuppressWarnings("unused")
				//SDTMMetadata sourceSDKMetaData = (SDTMMetadata) (((DefaultMutableTreeNode) e.getSourceNode()).getUserObject());

				StringBuffer _sourceDataAsXPath;
//				DefaultSourceTreeNode _tn0 = (DefaultSourceTreeNode) e.getSourceNode();
				TreeNode t = null;
//				t = _tn0.getParent();
				_sourceDataAsXPath = new StringBuffer();
				ArrayList<String> _tmp = new ArrayList<String>();
				// System.out.println( " The value is "+t.toString());
				_tmp.add(t.toString());
				do {
					try {
						t = t.getParent();
						_tmp.add(t.toString());
					} catch (Exception ee) {
						break;
					}
					// System.out.println( " The value is "+t.toString());
				} while (true);
				// System.out.println( " The value is "+_sb.toString());
				// harsha perform the LIFO operation
				for (int l = 1; l < _tmp.size() + 1; l++) {
					try {
						int sizeNow = _tmp.size() - l;
						_sourceDataAsXPath.append("\\" + _tmp.get(sizeNow));
					} catch (Exception ed) {
						// ed.printStackTrace();
					}
				}
//				String _tmpStr = e.getSourceNode().toString();
//				_tmpStr = _tmpStr.replace("]", "");
//				_sourceDataAsXPath.append("\\" + _tmpStr);



				//	SDTMMetadata targetSDKMetaData = (SDTMMetadata) (((DefaultMutableTreeNode) e.getTargetNode()).getUserObject());



//				DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) e.getTargetNode();
//				SDTMMetadata _sdtmMetadata = (SDTMMetadata) targetNode.getUserObject();
				// Do a check if the chosen target is DM or not

//				String _targetDataAsXpath = _sdtmMetadata.getXPath();
//				System.out.println ("");
//
//				SDTMMappingGenerator.get_sdtmMappingGeneratorReference().removeObject(_sourceDataAsXPath.toString(), _targetDataAsXpath.toString());
//				System.out.println ("");
				// CumulativeMappingGenerator cumulativeMappingGenerator=CumulativeMappingGenerator.getInstance();
				// boolean isSuccess = cumulativeMappingGenerator.unmap(sourceSDKMetaData.getXPath(), targetSDKMetaData.getXPath());
			}
		}
	}
	
	/**
	 * Called by setMappingData() or constructMappingGraph(), and other methods.
	 */
	public void clearAllGraphCells()
	{
		// clean up
		Object[] cells = DefaultGraphModel.getDescendants(graph.getModel(), graph.getRoots()).toArray();
		// call to remove all cells
		removeCells(cells, false);
		unmapCells(cells);		
		setMappingViewList(Collections.synchronizedList(new ArrayList()));
		setGraphChanged(false);
	}

	private void removeCells(Object[] cells, boolean findAssociatedCells)
	{
		//repaint the source and target tree panel if a functionBox is deleted
		boolean repaintSourceTarget=false;
		if (cells!=null&&cells.length>0)
		{
			Object firstToDelete = cells[0];
//			if (firstToDelete instanceof FunctionBoxCell)
//				repaintSourceTarget=true;
		}
		
		cells = DefaultGraphModel.getDescendants(graph.getModel(), cells).toArray();
		if ( !findAssociatedCells ) {// no need to find associated cells, so directly remove them.
			graph.getGraphLayoutCache().remove(cells, true, true);
			return;
		}
		List cellSelectionList = new ArrayList(Arrays.asList(cells));
		List graphToBeDeleteList = new ArrayList();
		List mappingViewToBeDeletedList = new ArrayList();
		int size = mappingViewList.size();
		for (int i = 0; i < size; i++) {
////			MappingViewCommonComponent comp = (MappingViewCommonComponent) mappingViewList.get(i);
//			if ( comp.findMatchedCell(cellSelectionList) ) {
//				mappingViewToBeDeletedList.add(comp);
//			}
		}
		// reverse back in case some additions are added by calling comp.findMatchedCell() above.
		cells = cellSelectionList.toArray();
		if ( cells != null ) {
			cells = DefaultGraphModel.getDescendants(graph.getModel(), cells).toArray();
			// graph.getModel().remove(cells);
			graph.getGraphLayoutCache().remove(cells, true, true);
			// execute the clean-up of mappingViewList and reset the mappable flag only if after
			// succesfully removed them from graph above.
//			for (Iterator it = mappingViewToBeDeletedList.iterator(); it.hasNext();) {
//				((MappingViewCommonComponent) it.next()).setMappableFlag(false);
//			}
//			for (int i = 0; i < cells.length; i++) {
//				if ( cells[i] instanceof FunctionBoxCell ) {
//					FunctionBoxMutableViewInterface userObject = (FunctionBoxMutableViewInterface) ((FunctionBoxCell) cells[i]).getUserObject();
//					getUsageManager().removeFunctionUsage(userObject);
//				}
//			}
			mappingViewList.removeAll(mappingViewToBeDeletedList);
		}
		
		//repaint the source and target scrollPanes
		if (repaintSourceTarget)
		{
			mappingPanel.getSourceScrollPane().repaint();
			mappingPanel.getTargetScrollPane().repaint();
		}
	}
	public boolean createMapping(MappableNode sourceNode,
			MappableNode targetNode) {
		// TODO Auto-generated method stub
		return false;
	}
	public void renderInJGraph(Graphics g) {
		// TODO Auto-generated method stub
		
	}

}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 */
