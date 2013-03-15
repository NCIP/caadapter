/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 $Header: /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/ui/jgraph/MiddlePanelJGraphController.java,v 1.65 2006/10/10 17:18:26



 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.ui.mapping.jgraph;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.SDKMetaData;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.function.meta.ParameterMeta;
import gov.nih.nci.caadapter.common.map.BaseComponent;
import gov.nih.nci.caadapter.common.map.BaseComponentFactory;
import gov.nih.nci.caadapter.common.map.BaseMapElement;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;
import gov.nih.nci.caadapter.hl7.map.Mapping;
import gov.nih.nci.caadapter.common.map.ViewImpl;
import gov.nih.nci.caadapter.common.map.View;
import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;

import gov.nih.nci.caadapter.hl7.map.impl.BaseMapElementImpl;
import gov.nih.nci.caadapter.hl7.map.impl.MapImpl;
import gov.nih.nci.caadapter.hl7.map.impl.MappingImpl;
import gov.nih.nci.caadapter.mms.generator.CumulativeMappingGenerator;
import gov.nih.nci.caadapter.sdtm.SDTMMetadata;
import gov.nih.nci.caadapter.ui.common.MappableNode;
import gov.nih.nci.caadapter.ui.common.functions.FunctionBoxCell;
import gov.nih.nci.caadapter.ui.common.functions.FunctionBoxDefaultPort;
import gov.nih.nci.caadapter.ui.common.functions.FunctionBoxDefaultPortView;
import gov.nih.nci.caadapter.ui.common.functions.FunctionBoxMutableViewInterface;
import gov.nih.nci.caadapter.ui.common.functions.FunctionBoxViewManager;
import gov.nih.nci.caadapter.ui.common.functions.FunctionBoxViewUsageManager;
import gov.nih.nci.caadapter.ui.common.jgraph.MappingDataManager;
import gov.nih.nci.caadapter.ui.common.jgraph.MappingPanelPropertiesSwitchController;
import gov.nih.nci.caadapter.ui.common.jgraph.MappingViewCommonComponent;
import gov.nih.nci.caadapter.ui.common.jgraph.MiddlePanelGraphModel;
import gov.nih.nci.caadapter.ui.common.jgraph.MiddlePanelJGraph;
import gov.nih.nci.caadapter.ui.common.jgraph.UIHelper;
import gov.nih.nci.caadapter.ui.common.tree.DefaultTargetTreeNode;
import gov.nih.nci.caadapter.ui.common.tree.DefaultSourceTreeNode;
import gov.nih.nci.caadapter.ui.common.tree.MappingBaseTree;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.caadapter.sdtm.SDTMMappingGenerator;
import org.jgraph.JGraph;
import org.jgraph.graph.*;
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
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: linc $
 * @version Since caAdapter v1.2 revision $Revision: 1.27 $ date $Date: 2008-06-16 15:50:50 $
 */
public class MiddlePanelJGraphController implements MappingDataManager// , DropTargetListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: MiddlePanelJGraphController.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem. This String is for informational purposes only and MUST not be made
	 * final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/jgraph/MiddlePanelJGraphController.java,v 1.27 2008-06-16 15:50:50 linc Exp $";

	private MiddlePanelJGraph graph = null;

	private MiddlePanelMarqueeHandler marqueeHandler = null;

	// the parent panels
	private MappingMiddlePanel middlePanel = null;

	private AbstractMappingPanel mappingPanel = null;

	// a list of MappingViewCommonComponent
	private List mappingViewList = null;

	private Mapping mappingData = null;

	private boolean isGraphChanged = false;

	private MappingPanelPropertiesSwitchController propertiesSwitchController;

	private Color graphBackgroundColor = new Color(222, 238, 255);

	private FunctionBoxViewUsageManager usageManager;

	private LinkSelectionHighlighter linkSelectionHighlighter;


    public LinkSelectionHighlighter getHighLighter(){
            return linkSelectionHighlighter;
    }
	//
	// Construct the Graph using the Model as its Data Source
	public MiddlePanelJGraphController(MiddlePanelJGraph graph, MappingMiddlePanel middlePanel, AbstractMappingPanel mappingPanel) {
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
		graph.getSelectionModel().addGraphSelectionListener(getPropertiesSwitchController());
		// setMappingPairCellMap(Collections.synchronizedMap(new HashMap()));
		setMappingViewList(Collections.synchronizedList(new ArrayList()));
		if ( this.mappingData != null && keepSourceTargetComponent ) {// just to clear graphs but not the source and target component if any.
			MappingImpl newMappingImpl = new MappingImpl();
			newMappingImpl.setSourceComponent(this.mappingData.getSourceComponent());
			newMappingImpl.setTargetComponent(this.mappingData.getTargetComponent());
			newMappingImpl.setMappingType(this.mappingData.getMappingType());
			setMappingData(newMappingImpl);
		} else {// initialize all
			setMappingData(new MappingImpl());
		}
		setGraphChanged(false);
		usageManager = null;
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
	public MappingPanelPropertiesSwitchController getPropertiesSwitchController()
	{
		if ( propertiesSwitchController == null ) {
			// propertiesSwitchController = new MappingPanelPropertiesSwitchController();
			propertiesSwitchController = new MappingPanelPropertiesSwitchController(graph);
		}
		return propertiesSwitchController; // To change body of implemented methods use File | Settings | File Templates.
	}

	/**
	 * @param node
	 * @param searchMode
	 *            any of the SEARCH_BY constants defined above.
	 * @return a list of MappingViewCommonComponent if any being found; an empty list if nothing is found.
	 */
	public List<MappingViewCommonComponent> findMappingViewCommonComponentList(Object node, String searchMode)
	{
		return MappingViewCommonComponent.findMappingViewCommonComponentListList(mappingViewList, node, searchMode);
	}

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
				if ( o instanceof MappingViewCommonComponent ) {
					MappingViewCommonComponent comp = (MappingViewCommonComponent) o;
					comp.setMappableFlag(false);
				}
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
			constructMappingGraph();
			// clear the flag so that from this point on, any user change on the graph will be considered as change.
			setGraphChanged(false);
			registerLinkHighlighter();
		}
	}

    public void setMappingData(Mapping mappingData, boolean flag)
     {
         constructMappingGraph();
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
		Log.logInfo(this, getClass().getName() + " will link source and target port.");
		// Construct Edge with no label
		DefaultEdge edge = new DefaultEdge();
		edge.setSource(source);
		edge.setTarget(target);
		AttributeMap lineStyle = UIHelper.getDefaultUnmovableEdgeStyle(source);
		if ( graph.getModel().acceptsSource(edge, source) && graph.getModel().acceptsTarget(edge, target) ) {
			// Create a Map that holds the attributes for the edge
			edge.getAttributes().applyMap(lineStyle);
			MappableNode sourceNode = (MappableNode) source;// getMappableNodeThroughPort(source);
			MappableNode targetNode = (MappableNode) target;// getMappableNodeThroughPort(target);
			if ( sourceNode == null || targetNode == null ) {
				StringBuffer msg = new StringBuffer("Cannot find mappable source or target node.");
				JOptionPane.showMessageDialog(middlePanel.getRootPane().getParent(), msg.toString(), "Mapping Error", JOptionPane.ERROR_MESSAGE);
			}
			// Insert the Edge and its Attributes
			graph.getGraphLayoutCache().insertEdge(edge, source, target);
			MappingViewCommonComponent comp = new MappingViewCommonComponent(sourceNode, targetNode, source, target, edge);
			edge.setUserObject(comp);
			mappingViewList.add(comp);
			setGraphChanged(true);
			return true;
		} else {
			List reasonList = ((MiddlePanelGraphModel) graph.getModel()).getNotAcceptableReasonList();
			JOptionPane.showMessageDialog(middlePanel.getRootPane().getParent(), reasonList.toArray(new Object[0]), "Mapping Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
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
				MappingViewCommonComponent e = (MappingViewCommonComponent) edge.getUserObject();
				SDKMetaData sourceSDKMetaData = (SDKMetaData) (((DefaultMutableTreeNode) e.getSourceNode()).getUserObject());
				SDKMetaData targetSDKMetaData = (SDKMetaData) (((DefaultMutableTreeNode) e.getTargetNode()).getUserObject());
				CumulativeMappingGenerator cumulativeMappingGenerator = CumulativeMappingGenerator.getInstance();
				boolean isSuccess = cumulativeMappingGenerator.unmap(sourceSDKMetaData.getXPath(), targetSDKMetaData.getXPath());
				sourceSDKMetaData.setMapped(false);
			} else if ( middlePanel.getKind().equalsIgnoreCase("SDTM") ) {
				MappingViewCommonComponent e = (MappingViewCommonComponent) edge.getUserObject();
				@SuppressWarnings("unused")
				//SDTMMetadata sourceSDKMetaData = (SDTMMetadata) (((DefaultMutableTreeNode) e.getSourceNode()).getUserObject());

				StringBuffer _sourceDataAsXPath;
				DefaultSourceTreeNode _tn0 = (DefaultSourceTreeNode) e.getSourceNode();
				TreeNode t;
				t = _tn0.getParent();
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
				String _tmpStr = e.getSourceNode().toString();
				_tmpStr = _tmpStr.replace("]", "");
				_sourceDataAsXPath.append("\\" + _tmpStr);



				@SuppressWarnings("unused")
				//	SDTMMetadata targetSDKMetaData = (SDTMMetadata) (((DefaultMutableTreeNode) e.getTargetNode()).getUserObject());



				DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) e.getTargetNode();
				SDTMMetadata _sdtmMetadata = (SDTMMetadata) targetNode.getUserObject();
				// Do a check if the chosen target is DM or not

				String _targetDataAsXpath = _sdtmMetadata.getXPath();
				System.out.println ("");

				SDTMMappingGenerator.get_sdtmMappingGeneratorReference().removeObject(_sourceDataAsXPath.toString(), _targetDataAsXpath.toString());
				System.out.println ("");
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
			if (firstToDelete instanceof FunctionBoxCell)
				repaintSourceTarget=true;
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
			MappingViewCommonComponent comp = (MappingViewCommonComponent) mappingViewList.get(i);
			if ( comp.findMatchedCell(cellSelectionList) ) {
				mappingViewToBeDeletedList.add(comp);
			}
		}
		// reverse back in case some additions are added by calling comp.findMatchedCell() above.
		cells = cellSelectionList.toArray();
		if ( cells != null ) {
			cells = DefaultGraphModel.getDescendants(graph.getModel(), cells).toArray();
			// graph.getModel().remove(cells);
			graph.getGraphLayoutCache().remove(cells, true, true);
			// execute the clean-up of mappingViewList and reset the mappable flag only if after
			// succesfully removed them from graph above.
			for (Iterator it = mappingViewToBeDeletedList.iterator(); it.hasNext();) {
				((MappingViewCommonComponent) it.next()).setMappableFlag(false);
			}
			for (int i = 0; i < cells.length; i++) {
				if ( cells[i] instanceof FunctionBoxCell ) {
					FunctionBoxMutableViewInterface userObject = (FunctionBoxMutableViewInterface) ((FunctionBoxCell) cells[i]).getUserObject();
					getUsageManager().removeFunctionUsage(userObject);
				}
			}
			mappingViewList.removeAll(mappingViewToBeDeletedList);
		}

		//repaint the source and target scrollPanes
		if (repaintSourceTarget)
		{
			mappingPanel.getSourceScrollPane().repaint();
			mappingPanel.getTargetScrollPane().repaint();
		}
	}

	/**
	 * Add the specified function at the specific start location.
	 *
	 * @param function
	 * @param startPoint
	 * @return if the function has been successfully added.
	 */
	public boolean addFunction(FunctionMeta function, Point2D startPoint)
	{
		if ( startPoint == null ) {// set to default value.
			startPoint = new Point(25, 25);
		}
		ViewImpl viewInfo =ViewImpl.getViewImpl(true, (int) startPoint.getX(), (int) startPoint.getY(), 200, 200, null);
		return addFunction(function, viewInfo);
	}

	/**
	 * Called by constructMappingGraph() to insert in functions defined in the mappingMeta.
	 *
	 * @param functionComponent
	 * @return if the function has been successfully added.
	 */
	public boolean addFunction(FunctionComponent functionComponent)
	{
		if ( functionComponent != null ) {
			FunctionBoxMutableViewInterface functionInstance = getUsageManager().createOneFunctionalBoxMutableViewInstance(functionComponent);
			return addFunctionInstance(functionInstance);
		} else {
			Log.logInfo(Log.MAP_LOG, "functionComponent in addFunction(FunctionComponent functionComponent) is null!");
			return false;
		}
	}

	public boolean addFunction(FunctionMeta function, View viewInfo)
	{
		// Log.logInfo(this, "Drop a function box: '" + function + "'.");
		if ( function == null ) {
			Log.logError(this, "Illegal argument! " + "Function shall not be null!");
			return false;
		}
		// FunctionConstant functionConstant = null;
		// if(function.isConstantFunction())
		// {//a constant function, need to ask for input values
		// //following does not work. It only works for one input type only.
		// // Object returnValue = JOptionPane.showInputDialog(getMiddlePanel(), new Object[]{"Type", "Value"},"Define a Constant",
		// JOptionPane.INFORMATION_MESSAGE, null, null, new Object[]{"", ""});
		// // Log.logInfo(this, "return value: '" + returnValue + "'.");
		// FunctionConstantDefinitionDialog dialog = new FunctionConstantDefinitionDialog(((JFrame) mappingPanel.getRootPane().getParent()));
		// DefaultSettings.centerWindow(dialog);
		// dialog.setVisible(true);
		// if(dialog.isOkButtonClicked())
		// {
		// String typeValue = DefaultSettings.getClassNameWithoutPackage(dialog.getConstantTypeClass());
		// functionConstant = new FunctionConstant(typeValue, dialog.getConstantValue());
		// }
		// else
		// {//adding constant was cancelled.
		// return false;
		// }
		// }
		// Add FunctionBoxMutableViewInterface as the user object of FunctionBoxCell.
		FunctionBoxMutableViewInterface functionInstance = getUsageManager().createOneFunctionalBoxMutableViewInstance(function, viewInfo, mappingPanel.getRootContainer());
		if ( functionInstance == null ) {
			return false;
		}
		// if(functionConstant!=null)
		// {//implies the function is a constant function
		// functionInstance.setFunctionConstant(functionConstant);
		// }
		return addFunctionInstance(functionInstance);
	}

	private boolean addFunctionInstance(FunctionBoxMutableViewInterface functionInstance)
	{
		FunctionMeta function = functionInstance.getFunctionMeta();
		View viewInfo = functionInstance.getViewMeta();
		Point2D startPoint = new Point(viewInfo.getX() < 0 ? 25 : viewInfo.getX(), viewInfo.getY() < 0 ? 25 : viewInfo.getY());
		// Construct Vertex with Label
		FunctionBoxCell functionBoxVertex = new FunctionBoxCell(functionInstance);// createDefaultGraphCell(function);
		Dimension functionBoxDimension = new Dimension(viewInfo.getWidth() <= 0 ? 200 : viewInfo.getWidth(), viewInfo.getHeight() <= 0 ? 200 : viewInfo.getHeight());
		// Create a Map that holds the attributes for the functionBoxVertex
		// functionBoxVertex.getAttributes().applyMap(createCellAttributes(startPoint, functionBoxDimension));
		Color backGroundColor = viewInfo.getColor() == null ? UIHelper.DEFAULT_VERTEX_COLOR : viewInfo.getColor();
		Map funcBoxAttrbutes = UIHelper.createBounds(new AttributeMap(), startPoint, functionBoxDimension, backGroundColor, true);
		GraphConstants.setSizeable(funcBoxAttrbutes, true);
		// Insert the functionBoxVertex (including child port and attributes)
		Map portAttributes = new Hashtable();
		ParentMap parentMap = new ParentMap();
		int numOfInputs = FunctionBoxViewManager.getInstance().getTotalNumberOfDefinedInputs(function);
		int numOfOutputs = FunctionBoxViewManager.getInstance().getTotalNumberOfDefinedOutputs(function);
		int maximumPorts = Math.max(numOfInputs, numOfOutputs);
		addGraphPorts(function, portAttributes, parentMap, functionBoxVertex, funcBoxAttrbutes, numOfInputs, UIHelper.getDefaultFunctionalBoxInputOrientation(), maximumPorts);
		addGraphPorts(function, portAttributes, parentMap, functionBoxVertex, funcBoxAttrbutes, numOfOutputs, UIHelper.getDefaultFunctionalBoxOutputOrientation(), maximumPorts);
		// Create a Map that holds the attributes for the Vertex
		functionBoxVertex.getAttributes().applyMap(funcBoxAttrbutes);
		graph.getGraphLayoutCache().insert(functionBoxVertex);
		graph.getGraphLayoutCache().insert(functionBoxVertex.getChildren().toArray(), portAttributes, null, parentMap, null);
		setGraphChanged(true);
		return true;
		// EDIT does not work!
		// graph.getGraphLayoutCache().edit(functionBoxVertex.getChildren().toArray(), portAttributes);
		// graph.getGraphLayoutCache().edit(portAttributes);
		// graph.getGraphLayoutCache().insert(new Object[]{functionBoxVertex}, funcBoxAttrbutes, null, parentMap, null);
		// Log.logInfo(this, "functionBoxVertex.getChildren().size(): " + functionBoxVertex.getChildren().size());
		// this.getGraphLayoutCache().insert(functionBoxVertex.getChildren().toArray(), portAttributes, null, parentMap);
		// following received java.lang.ClassCastException
		// graph.getModel().insert(new Object[]{functionBoxVertex}, funcBoxAttrbutes, null, null, null);
		// graph.getModel().edit(portAttributes, null, null, null);
	}

	/**
	 * construct and add graph ports to the given cell with constructed attributes to the map.
	 *
	 * @param function
	 * @param portAttributes
	 * @param parentMap
	 * @param cell
	 * @param cellAttributes
	 * @param numberOfPorts
	 * @param portOrientation
	 * @param maxPortsOfGivenFunction
	 *            the max number of input and output ports to help figure out the offset of the title area.
	 * @return the map of attributes.
	 */
	protected Map addGraphPorts(FunctionMeta function, Map portAttributes, ParentMap parentMap, DefaultGraphCell cell, Map cellAttributes, int numberOfPorts, int portOrientation, int maxPortsOfGivenFunction)
	{
		// Log.logInfo(this, "numOfPorts: " + numberOfPorts + ",orientation=" + portOrientation);
		// key=port, value=its attribute map of portAttributes
//		Rectangle2D bounds = GraphConstants.getBounds(cellAttributes);
		Dimension portDimension = new Dimension(FunctionBoxDefaultPortView.MY_SIZE, FunctionBoxDefaultPortView.MY_SIZE);
		// create ports and need 100 percent unit for relative positioning.
		int unit = GraphConstants.PERMILLE;
		int offsetX = (int) portDimension.getWidth() / 2;
		int offsetY = (int) portDimension.getHeight() / 2;
		int interimFactor = (int) (unit / (numberOfPorts + 1));
		int offsetTitleHeight = ((int) (unit / (maxPortsOfGivenFunction + 1))) - interimFactor / 2;// interimFactor + 10;
		List<ParameterMeta> paramList = (portOrientation == UIHelper.PORT_LEFT) ? function.getInputDefinitionList() : function.getOuputDefinitionList();
		for (int i = 0; i < numberOfPorts; i++) {
			Map attriMap = new Hashtable();
			DefaultPort port = null;
			if ( portOrientation == UIHelper.PORT_LEFT ) {
				attriMap = UIHelper.getDefaultFunctionBoxPortAttributes(attriMap, portDimension);
				// GraphConstants.setOffset(attriMap, new Point2D.Double(bounds.getX() - offsetX, bounds.getY() + (interimFactor * (i + 1)) - offsetY));
				GraphConstants.setOffset(attriMap, new Point2D.Double(-offsetX, (interimFactor * (i + 1)) - offsetY + offsetTitleHeight));
				// port = new FunctionBoxDefaultPort(paramList.get(i));//UIHelper.getDefaultFunctionalBoxInputCaption() + " " + i);
			} else if ( portOrientation == UIHelper.PORT_RIGHT ) {
				attriMap = UIHelper.getDefaultFunctionBoxPortAttributes(attriMap, portDimension);
				// GraphConstants.setOffset(attriMap, new Point2D.Double(bounds.getX() + bounds.getWidth() - portDimension.getWidth() - offsetX, bounds.getY() +
				// (interimFactor * (i + 1)) - offsetY));
				GraphConstants.setOffset(attriMap, new Point2D.Double(unit + offsetX, (interimFactor * (i + 1)) - offsetY + offsetTitleHeight));
				// port = new FunctionBoxDefaultPort(UIHelper.getDefaultFunctionalBoxOutputCaption() + " " + i);
			}
			port = new FunctionBoxDefaultPort(paramList.get(i));// UIHelper.getDefaultFunctionalBoxInputCaption() + " " + i);
			cell.add(port);
			portAttributes.put(port, attriMap);
			parentMap.addEntry(port, cell);
		}
		// Add one Floating Port
		return portAttributes;
	}

	public void renderInJGraph(Graphics g)
	{
		/** the real renderer */
		ConnectionSet cs = new ConnectionSet();
		Map attributes = new Hashtable();

		// render links
//		List visibleSrcNodes=new ArrayList<DefaultMutableTreeNode>();
//		if(mappingPanel.getSourceTree()!=null)
//			visibleSrcNodes=((MappingBaseTree)mappingPanel.getSourceTree()).getAllVisibleMappedNode();
//		List visibleTgrtNodes=new ArrayList<DefaultMutableTreeNode>();;
//		if (mappingPanel.getTargetTree()!=null)
//			visibleTgrtNodes=((MappingBaseTree)mappingPanel.getTargetTree()).getAllVisibleMappedNode();
		int mappingSize = mappingViewList.size();
		for (int i = 0; i < mappingSize; i++) {
			MappingViewCommonComponent mappingComponent = (MappingViewCommonComponent) mappingViewList.get(i);
			MappableNode sourceNode = mappingComponent.getSourceNode();
			MappableNode targetNode = mappingComponent.getTargetNode();
			DefaultGraphCell sourceCell = mappingComponent.getSourceCell();
			DefaultGraphCell targetCell = mappingComponent.getTargetCell();
			DefaultEdge linkEdge = mappingComponent.getLinkEdge();
			AttributeMap lineStyle = linkEdge.getAttributes();
			AttributeMap sourceNodeCellAttribute = sourceCell.getAttributes();
			AttributeMap targetNodeCellAttribute = targetCell.getAttributes();
//			boolean sourceNodeDisplayed=true;
//			boolean targetNodeDisplayed=true;
			try {
				if ( sourceNode instanceof FunctionBoxDefaultPort )
				{
					if ( targetNode instanceof FunctionBoxDefaultPort )
					{// todo: consider how to draw functional box movement.
					}
					else if ( targetNode instanceof DefaultMutableTreeNode )
					{
						DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) targetNode;
//						targetNodeDisplayed=visibleTgrtNodes.contains(treeNode);
						adjustToNewPosition(treeNode, targetNodeCellAttribute);
					}
				}
				else if ( sourceNode instanceof DefaultMutableTreeNode )
				{// neither sourceNode nor targetNode is functional box, so this implies a direct map
					if ( !(targetNode instanceof FunctionBoxDefaultPort) && (targetNode instanceof DefaultMutableTreeNode) ) {
						// change target node
						DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) targetNode;
//						targetNodeDisplayed=visibleTgrtNodes.contains(treeNode);
						adjustToNewPosition(treeNode, targetNodeCellAttribute);
					}
					// change source node
					DefaultMutableTreeNode srcNode = (DefaultMutableTreeNode) sourceNode;
//					sourceNodeDisplayed=visibleSrcNodes.contains(srcNode);
					adjustToNewPosition(srcNode, sourceNodeCellAttribute);
				}// end of else if(sourceNode instanceof DefaultMutableTreeNode)
				if ( sourceNodeCellAttribute != null
						&&targetNodeCellAttribute!=null) {// put in attribute if and only if it is constructed.
					attributes.put(sourceCell, sourceNodeCellAttribute);
					attributes.put(targetCell, targetNodeCellAttribute);
					//reset link color
//					if (!targetNodeDisplayed||!sourceNodeDisplayed)
//					{
//						lineStyle.put("linecolor",this.graphBackgroundColor);
//					}
					attributes.put(linkEdge, lineStyle);
					// cs.connect(linkEdge, sourceCell.getChildAt(0), targetCell.getChildAt(0));
					// Log.logInfo(this, "Drew line for : " + mappingComponent.toString());
				}
			} catch (Throwable e) {
				Log.logInfo(this, "Did not draw line for : " + mappingComponent.toString(true));
			}
		}// end of for
		graph.getGraphLayoutCache().edit(attributes, cs, null, null);
		graph.getGraphLayoutCache().setSelectsAllInsertedCells(false);
	}


	/**
	 * Adjust the given treenode's display coordinates. If given tree node is null or the root, will simply ignore.
	 *
	 * @param treeNode
	 *            the tree node
	 * @param oldAttributeMap
	 *            the existing attribute on the graph associated with the given tree node
	 * @return the oldAttributeMap after applying the newly calculated attribute.
	 */
	private AttributeMap adjustToNewPosition(DefaultMutableTreeNode treeNode, AttributeMap oldAttributeMap)
	{
		if ( treeNode != null && !treeNode.isRoot() ) {// change the render value if and only if neither it is null nor a root.
			boolean isFromSourceTree = UIHelper.isDataFromSourceTree(treeNode);
			int sourceYpos = -1;
			AttributeMap newTreeNodeAttribute = null;
			if ( isFromSourceTree ) {
				// Find the Y position for the source for this mappingNode.
				// find the # of pixels hidden. For example : 30
				sourceYpos = calculateScrolledDistanceOnY(mappingPanel.getSourceScrollPane(), treeNode, true);
				// To hide the vertex body from the graph
				newTreeNodeAttribute = UIHelper.getDefaultInvisibleVertexBounds(new Point(0, sourceYpos), true);
			} else {
				sourceYpos = calculateScrolledDistanceOnY(mappingPanel.getTargetScrollPane(), treeNode, true);
				newTreeNodeAttribute = UIHelper.getDefaultInvisibleVertexBounds(new Point(getMaximalXValueOnPane(), sourceYpos), false);
			}
			if ( oldAttributeMap == null ) {// never return null.
				oldAttributeMap = new AttributeMap();
			}
			oldAttributeMap.applyMap(newTreeNodeAttribute);
		}
		return oldAttributeMap;
	}
	/**
	 * Return the number of pixels changed due to scrolling.
	 *
	 * @param treeScrollPane
	 * @param treeNode
	 * @param reCalculateToNearestParent
	 * @return the number of pixels changed due to scrolling.
	 */
	private int calculateScrolledDistanceOnY(JScrollPane treeScrollPane, DefaultMutableTreeNode treeNode, boolean reCalculateToNearestParent)
	{
		/**
		 * Design rationale: 1) check the given tree node, if it is null or root, set the nodePositionBasedOnTotalPanel to the default root value, i.e., 8; 2)
		 * if the tree node is not root or null, proceed with normal calculation.
		 */
		final int DEFAULT_ROOT_Y_VALUE = 8;
		int nodePositionBasedOnTotalPanel = 0;
		// find the # of pixels hidden. For example : 30
		if ( treeNode == null || treeNode.getParent() == null ) {
			// Log.logInfo(this, (treeNode == null ? "Tree node is null." : "Tree node is the root") + " will use default value.");
			nodePositionBasedOnTotalPanel = DEFAULT_ROOT_Y_VALUE;
		} else {
			// System.out.println("To figure out the value via scroll bar positions.");
			// find the Y coordinate of the node. For example : 300
			TreePath tp = new TreePath(treeNode.getPath());
			JTree tree = ((JTree) treeScrollPane.getViewport().getView());
			int row = tree.getRowForPath(tp);
			Rectangle pathBounds = tree.getPathBounds(tp);
			if ( pathBounds == null ) {
				// Log.logInfo(this, "path bounds is null. tp is '" + tp.toString() + "'.");
				// System.out.println("The path bounds is null! on '" + treeNode + "' of type " + treeNode.getClass().getName());
				if ( reCalculateToNearestParent ) {// escape if not reCal or if the treeNode is the root.
					return calculateScrolledDistanceOnY(treeScrollPane, (DefaultMutableTreeNode) treeNode.getParent(), reCalculateToNearestParent);
				} else {// default to the root
					row = 0;
				}
			}
			if ( row == -1 )// (r==null)
			{
				// Log.logInfo(this, "tp is '" + tp.toString() + "'.");
				// System.out.println("the row value is -1! on '" + treeNode + "' of type " + treeNode.getClass().getName());
				if ( reCalculateToNearestParent && treeNode.getParent() != null ) {// escape if not reCal or if the treeNode is the root.
					return calculateScrolledDistanceOnY(treeScrollPane, (DefaultMutableTreeNode) treeNode.getParent(), reCalculateToNearestParent);
				} else {// default to the root
				// System.out.println("Default set to the root!");
					row = 0;
				}
			}
			// System.out.println("Row value: " + row);
			if ( row > 0 ) {
				Rectangle r = tree.getRowBounds(row);
				Point point = r.getLocation();
				int graphHeightHidden = (int) middlePanel.getGraphScrollPane().getViewport().getViewPosition().getY();
				int treeHeightHidden = (int) treeScrollPane.getViewport().getViewPosition().getY();
				nodePositionBasedOnTotalPanel = (int) point.getY() + (int) r.getHeight() / 2 + graphHeightHidden - treeHeightHidden;
			} else {
				nodePositionBasedOnTotalPanel = DEFAULT_ROOT_Y_VALUE;
			}
			// find the Y coordinate based on the *visible* area.
			// for example : 300 - 30 + 1/2(the node height) = 290
			// Log.logInfo(this, treeNode.toString() + " view position:' " + treeHeightHidden + "'");
			// Log.logInfo(this, treeNode.toString() + " tree node Y:' " + nodePositionBasedOnTotalPanel + "'");
		}
		int newYpos = nodePositionBasedOnTotalPanel;
		if ( newYpos < DEFAULT_ROOT_Y_VALUE ) {// never lower than the NOT_FOUND_VALUE
			newYpos = DEFAULT_ROOT_Y_VALUE;
		}
		// Log.logInfo(this, treeNode.toString() + " new YPos: '" + newYpos + "'.");
		return newYpos;
	}

	/**
	 * Create mapping relation between the source and target nodes.
	 *
	 * @param sourceNode
	 * @param targetNode
	 * @return if mapping is successfully created.
	 */
	public boolean createMapping(MappableNode sourceNode, MappableNode targetNode)
	{
		boolean result = false;
		// to remember the list of cells, edges, etc. that involve in the mapping.
		List graphCellList = new ArrayList();
		try {
			if ( sourceNode == null || targetNode == null ) {
				String msg = (sourceNode == null) ? "source node is null" : "";
				if ( targetNode == null ) {
					if ( msg.length() > 0 ) {
						msg += " and ";
					}
					msg += "target node is null";
				}
				msg += "!";
				Log.logInfo(this, msg);
				result = false;
				return result;
			}
			if ( sourceNode instanceof FunctionBoxDefaultPort ) {// drag from FunctionBoxCell
				if ( targetNode instanceof FunctionBoxDefaultPort ) {
					result = createFunctionBoxPortToFunctionBoxPortMapping((FunctionBoxDefaultPort) sourceNode, (FunctionBoxDefaultPort) targetNode, graphCellList);
				} else if ( targetNode instanceof DefaultMutableTreeNode ) {// functional box to tree link
					DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) targetNode;
					result = createTreeToFunctionBoxPortMapping(treeNode, (FunctionBoxDefaultPort) sourceNode, graphCellList);
				}
			} else if ( sourceNode instanceof DefaultMutableTreeNode ) {// drag from tree to middle panel
				// todo: will source tree always stays at left? If not, the implicit logic between source is left and target is right should have been changed.
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) sourceNode;
				if ( targetNode instanceof FunctionBoxDefaultPort ) {
					result = createTreeToFunctionBoxPortMapping(treeNode, (FunctionBoxDefaultPort) targetNode, graphCellList);
				} else if ( targetNode instanceof DefaultTargetTreeNode )// targetNode instanceof DefaultMutableTreeNode
				{// mapping between source and target tree node
					result = createTreeToTreeDirectMapping((DefaultSourceTreeNode) sourceNode, (DefaultTargetTreeNode) targetNode, graphCellList);
				} else if ( targetNode instanceof DefaultSourceTreeNode )// targetNode instanceof DefaultMutableTreeNode
				{// mapping between source and target tree node
					// reversed drag and drop
					result = createTreeToTreeDirectMapping((DefaultSourceTreeNode) targetNode, (DefaultTargetTreeNode) sourceNode, graphCellList);
				} else {
					Log.logInfo(this, "Not a graph cell or tree node, what is it? '" + (targetNode == null ? "null" : targetNode.toString() + " " + targetNode.getClass().getName()) + "'");
				}
				// Log.logInfo(this, "object is '" + object.getClass().getName() + "'");
			} else {
				Log.logInfo(this, sourceNode + " is not accepted by " + getClass().getName());
			}
		} catch (Exception e) {
			Log.logException(this, e);
		}
		if ( result ) {// successfully mapped, add to mapping
			DefaultGraphCell temp1 = (DefaultGraphCell) graphCellList.get(0);
			DefaultGraphCell temp2 = (DefaultGraphCell) graphCellList.get(1);
			DefaultEdge edge = (DefaultEdge) graphCellList.get(2);
			DefaultGraphCell sourceCell = null;
			DefaultGraphCell targetCell = null;
			if ( sourceNode instanceof FunctionBoxDefaultPort ) {
				sourceCell = temp1 == sourceNode ? temp1 : temp2;
			} else {
				sourceCell = temp1.getUserObject() == sourceNode ? temp1 : temp2;
			}
			if ( targetNode instanceof FunctionBoxDefaultPort ) {
				targetCell = temp1 == targetNode ? temp1 : temp2;
			} else {
				targetCell = temp1.getUserObject() == targetNode ? temp1 : temp2;
			}
			MappingViewCommonComponent viewComp = new MappingViewCommonComponent(sourceNode, targetNode, sourceCell, targetCell, edge);
			edge.setUserObject(viewComp);
//			Log.logInfo(this, "mapped: "+viewComp);
			mappingViewList.add(viewComp);
			setGraphChanged(true);
		}
		return result;
	}

	private boolean createTreeToTreeDirectMapping(DefaultSourceTreeNode sourceNode, DefaultTargetTreeNode targetNode, List graphCellList)
	{
		// boolean result = sourceNode.isMapped() || targetNode.isMapped();
		// no longer need to check anymore.
		boolean result = false;
		if ( !result ) {// neither one has been mapped before
			ConnectionSet cs = new ConnectionSet();
			Map attributes = new Hashtable();
			AttributeMap lineStyle = UIHelper.getDefaultUnmovableEdgeStyle(sourceNode.getUserObject());//.getDefaultUnmovableEdgeStyle();
			Dimension cellDimension = UIHelper.getDefaultSourceOrTargetVertexDimension();
			DefaultGraphCell sourceCell = null;
			DefaultGraphCell targetCell = null;
			DefaultEdge linkEdge = null;
			AttributeMap sourceAttribute = null;
			AttributeMap targetAttribute = null;
			// Find the Y position for the source for this mappingNode.
			// find the # of pixels hidden. For example : 30
			int sourceYpos = calculateScrolledDistanceOnY(mappingPanel.getSourceScrollPane(), sourceNode, false);
			// so the same for the Target side.
			int targetYpos = calculateScrolledDistanceOnY(mappingPanel.getTargetScrollPane(), targetNode, false);
			// if (!(isOutOfBound(sourceYpos) && isOutOfBound(targetYpos)))
			// {
			// process source
			sourceCell = new DefaultGraphCell(sourceNode);
			sourceAttribute = UIHelper.getDefaultInvisibleVertexAttribute(new Point(0, sourceYpos), true);
			// sourceAttribute = (AttributeMap) UIHelper.createBounds(new AttributeMap(), 0 - cellDimension.getWidth(), sourceYpos, cellDimension,
			// UIHelper.DEFAULT_VERTEX_COLOR, false);
			sourceCell.add(new DefaultPort());
			// process target
			targetCell = new DefaultGraphCell(targetNode);
			targetAttribute = UIHelper.getDefaultInvisibleVertexAttribute(new Point(getMaximalXValueOnPane(), targetYpos), false);
			// targetAttribute = (AttributeMap) UIHelper.createBounds(new AttributeMap(), this.middlePanel.getWidth(), targetYpos, cellDimension,
			// UIHelper.DEFAULT_VERTEX_COLOR, false);
			targetCell.add(new DefaultPort());
			// process the edge
			linkEdge = new DefaultEdge();
			attributes.put(sourceCell, sourceAttribute);
			attributes.put(targetCell, targetAttribute);
			attributes.put(linkEdge, lineStyle);
			// return back those being affected.
			graphCellList.add(sourceCell);
			graphCellList.add(targetCell);
			graphCellList.add(linkEdge);
			cs.connect(linkEdge, sourceCell.getChildAt(0), targetCell.getChildAt(0));
			graph.getGraphLayoutCache().insert(new Object[] { sourceCell, targetCell, linkEdge }, attributes, cs, null, null);
			// graph.getGraphLayoutCache().edit(attributes, cs, null, null);
			graph.getGraphLayoutCache().setSelectsAllInsertedCells(false);
			result = true;
			// Log.logInfo(this, "invisible source bounds: '" + GraphConstants.getBounds(sourceCell.getAttributes()) + "'");
			// Log.logInfo(this, "invisible target bounds: '" + GraphConstants.getBounds(targetCell.getAttributes()) + "'");
			// }
			// else
			// {
			// result = false;
			// }
		} else {
			result = false;
		}
		if ( result ) {
			sourceNode.setMapStatus(true);
			targetNode.setMapStatus(true);
		}
		return result;
	}

	private boolean createTreeToFunctionBoxPortMapping(DefaultMutableTreeNode treeNode, FunctionBoxDefaultPort port, List graphCellList)
	{
		boolean isDataFromSourceTree = UIHelper.isDataFromSourceTree(treeNode);
		// port is not null, so let's figure out how draw the link
		int treeNodeYpos = -1;
		AttributeMap treeNodeAttribute = null;
		DefaultGraphCell treeNodeCell = new DefaultGraphCell(treeNode);
		treeNodeCell.add(new DefaultPort());
		ConnectionSet cs = new ConnectionSet();
		Map attributes = new Hashtable();
		// Dimension cellDimension = UIHelper.getDefaultSourceOrTargetVertexDimension();
		DefaultEdge linkEdge = new DefaultEdge();
		if ( isDataFromSourceTree ) {
			treeNodeYpos = calculateScrolledDistanceOnY(mappingPanel.getSourceScrollPane(), treeNode, false);
			treeNodeAttribute = UIHelper.getDefaultInvisibleVertexAttribute(new Point(0, treeNodeYpos), true);
			// treeNodeAttribute = (AttributeMap) UIHelper.createBounds(new AttributeMap(), 0 - cellDimension.getWidth(), treeNodeYpos, cellDimension,
			// UIHelper.DEFAULT_VERTEX_COLOR, false);
			// edge, source, target
			cs.connect(linkEdge, treeNodeCell.getChildAt(0), port);
		} else {
			treeNodeYpos = calculateScrolledDistanceOnY(mappingPanel.getTargetScrollPane(), treeNode, false);
			treeNodeAttribute = UIHelper.getDefaultInvisibleVertexAttribute(new Point(getMaximalXValueOnPane(), treeNodeYpos), false);
			// treeNodeAttribute = (AttributeMap) UIHelper.createBounds(new AttributeMap(), this.middlePanel.getWidth(), treeNodeYpos, cellDimension,
			// UIHelper.DEFAULT_VERTEX_COLOR, false);
			// edge, source, target
			cs.connect(linkEdge, port, treeNodeCell.getChildAt(0));
		}
		attributes.put(treeNodeCell, treeNodeAttribute);
		attributes.put(port, port.getAttributes());
		attributes.put(linkEdge, UIHelper.getDefaultUnmovableEdgeStyle(treeNode.getUserObject()));
		// return back those being affected.
		graphCellList.add(treeNodeCell);
		graphCellList.add(port);
		graphCellList.add(linkEdge);
		graph.getGraphLayoutCache().insert(new Object[] { treeNodeCell, linkEdge }, attributes, cs, null, null);
		if ( treeNode instanceof MappableNode ) {
			((MappableNode) treeNode).setMapStatus(true);
			TreePath treePath = new TreePath(treeNode.getPath());
			if ( isDataFromSourceTree ) {
				((JTree) mappingPanel.getSourceScrollPane().getViewport().getView()).setSelectionPath(treePath);
			} else {// to work around a JTree refreshing defect to reflect the latest linked item
				((JTree) mappingPanel.getTargetScrollPane().getViewport().getView()).setSelectionPath(treePath);
			}
		}
		return true;
	}

	private boolean createFunctionBoxPortToFunctionBoxPortMapping(FunctionBoxDefaultPort source, FunctionBoxDefaultPort target, List graphCellList)
	{
		ConnectionSet cs = new ConnectionSet();
		Map attributes = new Hashtable();
		// Dimension cellDimension = UIHelper.getDefaultSourceOrTargetVertexDimension();
		DefaultEdge linkEdge = new DefaultEdge();
		cs.connect(linkEdge, source, target);
		attributes.put(source, source.getAttributes());
		attributes.put(target, target.getAttributes());
		attributes.put(linkEdge, UIHelper.getDefaultUnmovableEdgeStyle(source));
		// return back those being affected.
		graphCellList.add(source);
		graphCellList.add(target);
		graphCellList.add(linkEdge);
		graph.getGraphLayoutCache().insert(new Object[] { linkEdge }, attributes, cs, null, null);
		return true;
	}

	public FunctionBoxViewUsageManager getUsageManager()
	{
		if ( usageManager == null ) {
			usageManager = new FunctionBoxViewUsageManager();
		}
		return usageManager;
	}

	/**
	 * Get mapping relation consolidated.
	 *
	 * @param refresh
	 *            if true, the underline implementation will refresh data from user's input; otherwise, it will return what it has now, which may not be
	 *            up-to-date;
	 * @return mapping relation consolidated.
	 */
	public Mapping retrieveMappingData(boolean refresh)
	{
		/**
		 * Design rationale: 1) for each of functions in the graph, create FunctionComponent; 2) for each of direct mapping, create map object associated with
		 * it. Caveat: for simplicity, all previous map information is removed. Therefore, new component may carry different UUID then.
		 */
		if ( !refresh ) {// return what it is now.
			return mappingData;
		}
		if ( mappingData == null ) {
			// mappingData = new MappingImpl();
			throw new IllegalStateException("If refresh is true, the mapping data in " + getClass().getName() + " shall not be null. Please call registerXYZs() to add in soruce and target components.");
		}
		// clear out the data before adding.
		mappingData.removeAllMaps();
		// SHALL NOT remove function components, but do screening below, since
		// map information may refer existing old uuids.
		// mappingData.removeAllFunctionComponents();
		java.util.List<FunctionBoxMutableViewInterface> functionUsageList = getUsageManager().getAllFunctionUsageList();
		java.util.Map<FunctionBoxCell, BaseComponent> cellComponentMap = new HashMap<FunctionBoxCell, BaseComponent>();
		java.util.Map<String, FunctionComponent> uuidComponentMap = new HashMap<String, FunctionComponent>();
		int size = functionUsageList.size();
		for (int i = 0; i < size; i++) {
			FunctionBoxMutableViewInterface functionView = functionUsageList.get(i);
			FunctionBoxCell functionCell = functionView.getFunctionBoxCell();
			FunctionComponent functionComponent = functionView.getFunctionComponent(true);
			AttributeMap attrMap = functionCell.getAttributes();
			Rectangle2D bound = GraphConstants.getBounds(attrMap);
			Color backgroundColor = GraphConstants.getBackground(attrMap);
			ViewImpl viewImpl = (ViewImpl) functionView.getViewMeta();
			if ( viewImpl == null ) {
				viewImpl = ViewImpl.getViewImpl(true, (int) bound.getX(), (int) bound.getY(), (int) bound.getHeight(), (int) bound.getWidth(), null);
			} else {// re-use the view object.
				viewImpl.setX((int) bound.getX());
				viewImpl.setY((int) bound.getY());
				viewImpl.setHeight((int) bound.getHeight());
				viewImpl.setWidth((int) bound.getWidth());
				viewImpl.setColor(null);
				// viewImpl.setColor(backgroundColor);
			}
			functionComponent.setView(viewImpl);
			cellComponentMap.put(functionCell, functionComponent);
			uuidComponentMap.put(functionComponent.getXmlPath(), functionComponent);
		}
		consolidateFunctionComponentList(mappingData, uuidComponentMap);
		for (Iterator it = mappingViewList.iterator(); it.hasNext();) {
			MappingViewCommonComponent comp = (MappingViewCommonComponent) it.next();
			MappableNode sourceNode = comp.getSourceNode();
			MappableNode targetNode = comp.getTargetNode();
			if ( !(sourceNode instanceof FunctionBoxDefaultPort) && !(targetNode instanceof FunctionBoxDefaultPort) ) {// direct mapping
				retrieveDirectMappingData(sourceNode, targetNode, mappingData);
			}// end of direct map handling
			else {// either source or target is function
				BaseComponent sourceComponent = null;
				BaseComponent targetComponent = null;
				MetaObject localSourceUserObject = null;
				Object localTargetUserObject = null;
				if ( sourceNode instanceof FunctionBoxDefaultPort ) {
					localSourceUserObject = (MetaObject) ((FunctionBoxDefaultPort) sourceNode).getUserObject();
					sourceComponent = findFunctionComponent(cellComponentMap, (FunctionBoxDefaultPort) sourceNode);
				} else // a tree node
				{
					localSourceUserObject = (MetaObject) ((DefaultMutableTreeNode) sourceNode).getUserObject();
					if ( UIHelper.isDataFromSourceTree((DefaultMutableTreeNode) sourceNode) ) {// from source tree
						sourceComponent = mappingData.getSourceComponent();
					}
					// no else occurs since the MappingViewCommonComponent guarantees the sourceNode is the source of a link and the targetNode is the target of
					// a link.
					// else
					// {//from target tree
					// sourceComponent = mappingData.getTargetComponent();
					// }
				}
				if ( targetNode instanceof FunctionBoxDefaultPort ) {
					localTargetUserObject = (MetaObject) ((FunctionBoxDefaultPort) targetNode).getUserObject();
					targetComponent = findFunctionComponent(cellComponentMap, (FunctionBoxDefaultPort) targetNode);
				} else // a tree node
				{
//					localTargetUserObject = (MetaObject) ((DefaultMutableTreeNode) targetNode).getUserObject();
					localTargetUserObject = (Object) ((DefaultMutableTreeNode) targetNode).getUserObject();
					if ( !UIHelper.isDataFromSourceTree((DefaultMutableTreeNode) targetNode) ) {// from target tree
						targetComponent = mappingData.getTargetComponent();
					}
					// no else occurs since the MappingViewCommonComponent guarantees the sourceNode is the source of a link and the targetNode is the target of
					// a link.
					// else
					// {//from source tree
					// targetComponent = mappingData.getSourceComponent();
					// }
				}
				MapImpl mapImpl = new MapImpl();
				BaseMapElement sourceMapElement = new BaseMapElementImpl(sourceComponent, localSourceUserObject);
				mapImpl.setSourceMapElement(sourceMapElement);
				BaseMapElement targetMapElement = new BaseMapElementImpl(targetComponent, localTargetUserObject);
				mapImpl.setTargetMapElement(targetMapElement);
				mappingData.addMap(mapImpl);
			}// end of either is function
		}
		return mappingData;
	}

	/**
	 * Locate the function box component in the component map.
	 *
	 * @param componentMap
	 * @param portNode
	 * @return the function box component in the component map.
	 */
	private FunctionComponent findFunctionComponent(java.util.Map componentMap, FunctionBoxDefaultPort portNode)
	{
		FunctionComponent component = null;
		FunctionBoxCell parentCell = (FunctionBoxCell) portNode.getParent();
		FunctionComponent functionComp = (FunctionComponent) componentMap.get(parentCell);
		if ( functionComp != null ) {
			component = functionComp;
		} else {
			System.err.println("Cannot find function component for '" + portNode + "'!");
		}
		return component;
	}

	/**
	 * This method will help remove those function deleted in scene from mappingData and add those from scene to mappingData. Therefore, after calling this
	 * method, both mappingData and/or uuidComponentMap structure may change.
	 *
	 * @param mappingData
	 * @param uuidComponentMap
	 */
	private void consolidateFunctionComponentList(Mapping mappingData, java.util.Map<String, FunctionComponent> uuidComponentMap)
	{
		List<FunctionComponent> oldFunctionCompList = mappingData.getFunctionComponent();
		Iterator it = oldFunctionCompList.iterator();
		while (it.hasNext()) {// remove those not exist anymore in new mapping
			FunctionComponent functionComp = (FunctionComponent) it.next();
			FunctionComponent newFunctionComp = uuidComponentMap.get(functionComp.getXmlPath());
			if ( newFunctionComp == null ) {// could not find the old functionComp in new graph, that implies it is deleted already
				it.remove();
			} else {
				// update view and functionMeta info, if any
				functionComp.setMeta(newFunctionComp.getMeta());
				functionComp.setView(newFunctionComp.getView());
				// then remove the new one from the map, so after this filtering, the given map will just contain those need to be added.
				uuidComponentMap.remove(functionComp.getXmlPath());
			}
		}
		// now add new function components into mappingData
		it = uuidComponentMap.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			FunctionComponent comp = uuidComponentMap.get(key);
			Log.logInfo(this, "Added '" + comp + "' to mappingData.");
			mappingData.addFunctionComponent(comp);
		}
	}

	/**
	 * Construct the direct mapping information and put it into the given mappingData
	 *
	 * @param sourceNode
	 * @param targetNode
	 * @param mappingData
	 */
	private void retrieveDirectMappingData(MappableNode sourceNode, MappableNode targetNode, Mapping mappingData)
	{
		BaseComponent sourceComponent = mappingData.getSourceComponent();
		BaseComponent targetComponent = mappingData.getTargetComponent();
		DefaultMutableTreeNode localRealSourceNode = null;
		DefaultMutableTreeNode localRealTargetNode = null;
		if ( sourceNode instanceof DefaultSourceTreeNode ) {
			localRealSourceNode = (DefaultMutableTreeNode) sourceNode;
		} else// if(sourceNode instanceof DefaultTargetTreeNode)
		{
			localRealTargetNode = (DefaultMutableTreeNode) sourceNode;
		}
		if ( targetNode instanceof DefaultSourceTreeNode ) {
			localRealSourceNode = (DefaultMutableTreeNode) targetNode;
		} else// if(sourceNode instanceof DefaultTargetTreeNode)
		{
			localRealTargetNode = (DefaultMutableTreeNode) targetNode;
		}
		MapImpl mapImpl = new MapImpl();
		MetaObject userObject = (MetaObject) ((DefaultSourceTreeNode) localRealSourceNode).getUserObject();
		BaseMapElement sourceMapElement = new BaseMapElementImpl(sourceComponent, userObject);
		mapImpl.setSourceMapElement(sourceMapElement);
//		userObject = (MetaObject) ((DefaultTargetTreeNode) localRealTargetNode).getUserObject();
		Object baseUserObject =((DefaultTargetTreeNode) localRealTargetNode).getUserObject();
		BaseMapElement targetMapElement = new BaseMapElementImpl(targetComponent, baseUserObject);
		mapImpl.setTargetMapElement(targetMapElement);
		mappingData.addMap(mapImpl);
	}

	/**
	 * Call this method only if you do not have a base component handy; otherwise, call the overloaded function, so as to help you preserve your UUID value.
	 *
	 * @param metaObject
	 * @param file
	 */
	public void registerSourceComponent(MetaObject metaObject, File file)
	{
		BaseComponent sourceComponent = BaseComponentFactory.getDefaultSourceComponent(metaObject, file, null);
		registerSourceComponent(sourceComponent);
	}

	public void registerSourceComponent(BaseComponent sourceComponent)
	{
		mappingData.setSourceComponent(sourceComponent);
	}

	/**
	 * Call this method only if you do not have a base component handy; otherwise, call the overloaded function, so as to help you preserve your UUID value.
	 *
	 * @param metaObject
	 * @param file
	 */
	public void registerTargetComponent(MetaObject metaObject, File file)
	{
		BaseComponent targetComponent = BaseComponentFactory.getDefaultTargetComponent(metaObject, file, null);
		registerTargetComponent(targetComponent);
	}

	public void registerTargetComponent(BaseComponent targetComponent)
	{
		mappingData.setTargetComponent(targetComponent);
	}

	/**
	 * Called to render mapping (functional-box-driven or direct) after setMappingData() is called. When this is called, it assumes the source and target tree
	 * have been loaded successfully.
	 */
	private synchronized void constructMappingGraph()
	{
		List functionComponentList = mappingData.getFunctionComponent();
		int functionSize = functionComponentList.size();
		List mapList = mappingData.getMaps();
		int mapSize = mapList.size();
		if ( functionSize == 0 && mapSize == 0 )
        {
			// Log.logInfo(this, "No need to refresh graph.");
			return;
		}
		// render functional box first
		// Log.logInfo(this, "Total function component: '" + functionSize + "'.");
		for (int i = 0; i < functionSize; i++)
        {
			FunctionComponent functionComp = (FunctionComponent) functionComponentList.get(i);
			addFunction(functionComp);
		}
		// render map second
		for (int i = 0; i < mapSize; i++)
        {
			MapImpl map = (MapImpl) mapList.get(i);
			BaseMapElement sourceMapComp = map.getSourceMapElement();
			BaseMapElement targetMapComp = map.getTargetMapElement();


            MappableNode sourceNode = null;
            MappableNode targetNode = null;

            sourceNode = getSourceMappableNode(sourceMapComp);
            if (sourceNode == null)
            {
                sourceNode = getTargetMappableNode(sourceMapComp);
                targetNode = getSourceMappableNode(targetMapComp);
            }
            else targetNode = getTargetMappableNode(targetMapComp);

			createMapping(sourceNode, targetNode);
		}
	}

    private MappableNode getSourceMappableNode(BaseMapElement sourceMapComp)
    {
        MappableNode sourceNode = null;

			if ( sourceMapComp.isComponentOfSourceType()
					||sourceMapComp.getMetaObject() instanceof CSVFieldMeta
					||sourceMapComp.getMetaObject() instanceof CSVSegmentMeta)
            {
				sourceNode = UIHelper.constructMappableNode(mappingPanel.getSourceTree().getModel().getRoot(), sourceMapComp.getMetaObject());
			    //if (sourceNode == null) System.out.println("QQQQ3-1 :");
            }
            else if ( sourceMapComp.isComponentOfFunctionType() )
            {
				FunctionComponent functionComp = (FunctionComponent) sourceMapComp.getComponent();
				FunctionBoxMutableViewInterface functionView = getUsageManager().findFunctionUsageInstanceByComponentUUID(functionComp);
				FunctionBoxCell functionBoxCell = functionView.getFunctionBoxCell();
				ParameterMeta paramMeta = (ParameterMeta) sourceMapComp.getMetaObject();
				sourceNode = functionBoxCell.findPortByParameterMeta(paramMeta);
                //if (sourceNode == null)
                //    System.out.println("QQQQ3-2 : paramMeta is '" + paramMeta.toString() + "',field type:'" + paramMeta.getParameterType() + "'.");

                // Log.logInfo(this, "paramMeta is '" + paramMeta.toString() + "',field type:'" + paramMeta.getParameterType() + "'.");
			}
            else if ( sourceMapComp.isComponentOfTargetType()
					||sourceMapComp.getMetaObject() instanceof DatatypeBaseObject)
            {// flip back the real source and target
				sourceNode = UIHelper.constructMappableNodeObjectXmlPath(mappingPanel.getSourceTree().getModel().getRoot(), sourceMapComp.getXmlPath());
                //if (sourceNode == null) System.out.println("QQQQ3-3 : " + sourceMapComp.getXmlPath());
            }
			else if (sourceMapComp.getMetaObject() instanceof MetaObject )
			{
				sourceNode = UIHelper.constructMappableNodeObjectXmlPath(mappingPanel.getSourceTree().getModel().getRoot(), sourceMapComp.getXmlPath());
                //if (sourceNode == null) System.out.println("QQQQ3-4 :");
            }
			else
            {
				if ( sourceMapComp.getComponent() != null )
                {
					throw new IllegalArgumentException("map's sourceMapComponent has an invalid component '" + sourceMapComp.getComponent() + "' of type as of '" + sourceMapComp.getComponent().getType() + "'.");
				}
                else
                {
					throw new NullPointerException("map's sourceMapComponent has a null component!");
				}
			}
        return sourceNode;
    }
    private MappableNode getTargetMappableNode(BaseMapElement targetMapComp)
    {
        MappableNode targetNode = null;
            if ( targetMapComp.isComponentOfSourceType()
					||targetMapComp.getMetaObject() instanceof CSVFieldMeta
					||targetMapComp.getMetaObject() instanceof CSVSegmentMeta)
            {
				targetNode = UIHelper.constructMappableNode(mappingPanel.getSourceTree().getModel().getRoot(), targetMapComp.getMetaObject());
			}
            else if ( targetMapComp.isComponentOfFunctionType() )
            {
				FunctionComponent functionComp = (FunctionComponent) targetMapComp.getComponent();
				FunctionBoxMutableViewInterface functionView = getUsageManager().findFunctionUsageInstanceByComponentUUID(functionComp);
				FunctionBoxCell functionBoxCell = functionView.getFunctionBoxCell();
				ParameterMeta paramMeta = (ParameterMeta) targetMapComp.getMetaObject();
				targetNode = functionBoxCell.findPortByParameterMeta(paramMeta);
			}
            else if ( targetMapComp.isComponentOfTargetType()
					||targetMapComp.getMetaObject() instanceof DatatypeBaseObject)
			{
					targetNode = UIHelper.constructMappableNodeObjectXmlPath(mappingPanel.getTargetTree().getModel().getRoot(), targetMapComp.getXmlPath());
			}
			else if(targetMapComp.getDataXmlPath()!=null)
			{
				targetNode = UIHelper.constructMappableNodeObjectXmlPath(mappingPanel.getTargetTree().getModel().getRoot(), targetMapComp.getDataXmlPath());
			}
			else if (targetMapComp.getMetaObject() instanceof MetaObject )
			{
				targetNode = UIHelper.constructMappableNodeObjectXmlPath(mappingPanel.getTargetTree().getModel().getRoot(), targetMapComp.getXmlPath());
			}
			else
            {
				if ( targetMapComp.getComponent() != null )
                {
					throw new IllegalArgumentException("map's targetMapComponent has an invalid component '" + targetMapComp.getComponent() + "' of type as of '" + targetMapComp.getComponent().getType() + "'.");
				}
                else
                {
					throw new NullPointerException("map's targetMapComponent has a null component!");
				}
			}
        return targetNode;
    }

    private int getMaximalXValueOnPane()
	{
		int visibleWidth = (int) this.middlePanel.getGraphScrollPane().getVisibleRect().getWidth();
		int viewPortVisibleWidth = (int) this.middlePanel.getGraphScrollPane().getViewport().getVisibleRect().getWidth();
		int viewPortViewSizeWidth = (int) this.middlePanel.getGraphScrollPane().getViewport().getViewSize().getWidth();
		int viewPortViewRectWidth = (int) this.middlePanel.getGraphScrollPane().getViewport().getViewRect().getWidth();
		int middlePanelWidth = this.middlePanel.getWidth();
		// Log.logInfo(this, "middlePanelWidth='" + middlePanelWidth + "',visibleWidth='" + visibleWidth + "'.");
		// Log.logInfo(this, "viewPortVisibleWidth='" + viewPortVisibleWidth + "',viewPortViewSizeWidth='" + viewPortViewSizeWidth + "'," +
		// "',viewPortViewRectWidth='" + viewPortViewRectWidth + "'.");
		// return viewPortVisibleWidth;// - 23;
		return visibleWidth - 20;
	}
//	public Mapping getMappingData() {
//		return mappingData;
//	}
}
/**
 * HISTORY : $Log: not supported by cvs2svn $
 * HISTORY : Revision 1.26  2008/06/09 19:54:06  phadkes
 * HISTORY : New license text replaced for all .java files.
 * HISTORY :
 * HISTORY : Revision 1.25  2008/04/02 16:24:06  umkis
 * HISTORY : add 'if (sourceNode == null)' block
 * HISTORY :
 * HISTORY : Revision 1.24  2008/04/01 21:21:01  umkis
 * HISTORY : add getTargetMappableNode() and getSourceMappableNode
 * HISTORY :
 * HISTORY : Revision 1.23  2008/03/04 16:09:00  schroedn
 * HISTORY : Handling DeleteAll
 * HISTORY :
 * HISTORY : Revision 1.22  2008/02/28 19:17:23  wangeug
 * HISTORY : load mapping from xsd to Xmi
 * HISTORY :
 * HISTORY : Revision 1.21  2008/02/28 19:12:18  wangeug
 * HISTORY : load mapping from xsd to Xmi
 * HISTORY :
 * HISTORY : Revision 1.20  2008/01/08 18:45:03  wangeug
 * HISTORY : clean codes
 * HISTORY :
 * HISTORY : Revision 1.19  2007/12/13 21:09:11  wangeug
 * HISTORY : resolve code dependence in compiling
 * HISTORY :
 * HISTORY : Revision 1.18  2007/12/06 20:41:13  wangeug
 * HISTORY : support both data model and object model
 * HISTORY :
 * HISTORY : Revision 1.17  2007/12/04 15:13:08  wangeug
 * HISTORY : clean codes
 * HISTORY :
 * HISTORY : Revision 1.16  2007/12/03 15:26:43  wangeug
 * HISTORY : look for maping end node with XmlPath
 * HISTORY :
 * HISTORY : Revision 1.15  2007/10/19 17:49:04  jayannah
 * HISTORY : Changes to add link selection highlighter for the map file
 * HISTORY :
 * HISTORY : Revision 1.14  2007/10/18 20:16:22  jayannah
 * HISTORY : -Added a new method in MappingMiddlePanel to get the reference to MiddlePanelJGraphController
 * HISTORY : -Added a new method in MiddlePanelJGraphController to get the reference to linkselectionhighlighter
 * HISTORY : -Added linkselectionhighlighter to source and targe trees as tree selection listener
 * HISTORY :
 * HISTORY : Revision 1.13  2007/10/02 14:51:39  schroedn
 * HISTORY : Removed green dotted lines
 * HISTORY :
 * HISTORY : Revision 1.12  2007/08/31 13:10:43  wangeug
 * HISTORY : parsemapping
 * HISTORY :
 * HISTORY : Revision 1.11  2007/08/07 15:52:19  schroedn
 * HISTORY : New Feature, Primary Key and Lazy/Eager functions added to MMS
 * HISTORY :
 * HISTORY : Revision 1.10  2007/07/23 16:14:53  wangeug
 * HISTORY : bug fixing: refresh source and target tree when new mapping is created
 * HISTORY :
 * HISTORY : Revision 1.9  2007/07/20 17:06:42  wangeug
 * HISTORY : integrate Hl7 transformation service
 * HISTORY :
 * HISTORY : Revision 1.8  2007/07/18 20:42:42  wangeug
 * HISTORY : create CSV-H7L mapping with mapppingV4.0.xsd
 * HISTORY :
 * HISTORY : Revision 1.7  2007/07/17 16:19:13  wangeug
 * HISTORY : change UIUID to xmlPath
 * HISTORY :
 * HISTORY : Revision 1.6  2007/07/16 19:29:48  wangeug
 * HISTORY : change UIUID to xmlPath
 * HISTORY :
 * HISTORY : Revision 1.5  2007/07/05 15:18:28  wangeug
 * HISTORY : initila loading hl7 code without "clone"
 * HISTORY :
 * HISTORY : Revision 1.2  2007/04/19 14:07:37  wangeug
 * HISTORY : set link color based on linkType
 * HISTORY :
 * HISTORY : Revision 1.1  2007/04/03 16:17:57  wangeug
 * HISTORY : initial loading
 * HISTORY :
 * HISTORY : Revision 1.67  2006/11/27 20:38:08  jayannah
 * HISTORY : Changes to handle delete action on the "Open Map File" use case
 * HISTORY : HISTORY : Revision 1.65 2006/10/10 17:18:26 wuye HISTORY : Use getKind to determine what to do with
 * delete HISTORY : HISTORY : Revision 1.64 2006/09/28 19:31:12 wuye HISTORY : change private to public for clearall methods HISTORY : HISTORY : Revision 1.63
 * 2006/08/02 18:44:22 jiangsc HISTORY : License Update HISTORY : HISTORY : Revision 1.62 2006/07/13 19:51:49 jiangsc HISTORY : Save point. HISTORY : HISTORY :
 * Revision 1.61 2006/01/03 19:16:52 jiangsc HISTORY : License Update HISTORY : HISTORY : Revision 1.60 2006/01/03 18:56:24 jiangsc HISTORY : License Update
 * HISTORY : HISTORY : Revision 1.59 2005/12/29 23:06:17 jiangsc HISTORY : Changed to latest project name. HISTORY : HISTORY : Revision 1.58 2005/12/14 21:37:19
 * jiangsc HISTORY : Updated license information HISTORY : HISTORY : Revision 1.57 2005/11/29 16:23:54 jiangsc HISTORY : Updated License HISTORY : HISTORY :
 * Revision 1.56 2005/11/23 19:48:52 jiangsc HISTORY : Enhancement on mapping validations. HISTORY : HISTORY : Revision 1.55 2005/11/17 16:33:37 umkis HISTORY :
 * (defect# 196) MappingPanelPropertiesSwitchController(JGraphPanel) HISTORY : HISTORY : Revision 1.54 2005/11/11 19:23:59 jiangsc HISTORY : Support Pseudo Root
 * in Mapping Panel. HISTORY : HISTORY : Revision 1.53 2005/11/09 23:05:51 jiangsc HISTORY : Back to previous version. HISTORY : HISTORY : Revision 1.50
 * 2005/10/26 18:12:29 jiangsc HISTORY : replaced printStackTrace() to Log.logException HISTORY : HISTORY : Revision 1.49 2005/10/25 22:00:42 jiangsc HISTORY :
 * Re-arranged system output strings within UI packages. HISTORY : HISTORY : Revision 1.48 2005/10/21 21:10:04 jiangsc HISTORY : Removed sys out messages.
 * HISTORY : HISTORY : Revision 1.47 2005/10/21 15:11:55 jiangsc HISTORY : Resolve scrolling issue. HISTORY : HISTORY : Revision 1.46 2005/10/20 22:29:29
 * jiangsc HISTORY : Resolve scrolling issue. HISTORY : HISTORY : Revision 1.45 2005/10/20 20:31:50 jiangsc HISTORY : to Scroll consistently for source, target,
 * and map panel on the HL7MappingPanel. HISTORY : HISTORY : Revision 1.44 2005/10/18 13:35:26 umkis HISTORY : no message HISTORY : HISTORY : Revision 1.43
 * 2005/10/13 21:07:56 jiangsc HISTORY : Enhanced the source and target allocation in the MappingViewCommonComponent HISTORY : HISTORY : Revision 1.42
 * 2005/10/12 20:47:54 jiangsc HISTORY : GUI Enhancement HISTORY : HISTORY : Revision 1.41 2005/10/03 19:34:17 jiangsc HISTORY : Implement highlighting tree
 * nodes upon graph selection. HISTORY : HISTORY : Revision 1.40 2005/09/27 21:47:59 jiangsc HISTORY : Customized edge rendering and initially added a link
 * highlighter class. HISTORY : HISTORY : Revision 1.39 2005/08/31 19:41:14 jiangsc HISTORY : Fixed some UI medium defects. Thanks to Dan's test. HISTORY :
 * HISTORY : Revision 1.38 2005/08/24 22:28:39 jiangsc HISTORY : Enhanced JGraph implementation; HISTORY : Save point of CSV and HSM navigation update; HISTORY :
 * HISTORY : Revision 1.37 2005/08/23 18:57:17 jiangsc HISTORY : Implemented the new Properties structure HISTORY : HISTORY : Revision 1.36 2005/08/22 22:01:55
 * jiangsc HISTORY : Enhanced mapping deletion HISTORY : HISTORY : Revision 1.35 2005/08/22 21:35:28 jiangsc HISTORY : Changed BaseComponentFactory and other UI
 * classes to use File instead of string name; HISTORY : Added first implementation of Function Constant; HISTORY : HISTORY : Revision 1.34 2005/08/19 21:20:24
 * jiangsc HISTORY : Loose the restriction on mappable. HISTORY : HISTORY : Revision 1.33 2005/08/18 15:30:18 jiangsc HISTORY : First implementation on Switch
 * control. HISTORY : HISTORY : Revision 1.32 2005/08/17 20:41:37 jiangsc HISTORY : Removed some comments HISTORY : HISTORY : Revision 1.31 2005/08/05 20:35:52
 * jiangsc HISTORY : 0)Implemented field sequencing on CSVPanel but needs further rework; HISTORY : 1)Removed (Yes/No) for questions; HISTORY : 2)Removed
 * double-checking after Save-As; HISTORY : HISTORY : Revision 1.30 2005/08/04 22:22:11 jiangsc HISTORY : Updated license and class header information. HISTORY :
 * HISTORY : Revision 1.29 2005/08/04 20:41:17 jiangsc HISTORY : Updated to fix mis-matched mapping issue. HISTORY : HISTORY : Revision 1.28 2005/07/22 17:39:24
 * jiangsc HISTORY : Persistence of Function involved mapping. HISTORY : HISTORY : Revision 1.27 2005/07/21 17:51:00 jiangsc HISTORY : Changed to use logInfo.
 * HISTORY : HISTORY : Revision 1.26 2005/07/21 17:07:49 jiangsc HISTORY : First round to implement Functions in mapping persistence. HISTORY : HISTORY :
 * Revision 1.25 2005/07/20 22:00:54 jiangsc HISTORY : Save point. HISTORY : HISTORY : Revision 1.24 2005/07/19 22:28:16 jiangsc HISTORY : 1) Renamed
 * FunctionalBox to FunctionBox to be consistent; HISTORY : 2) Added SwingWorker to OpenObjectToDbMapAction; HISTORY : 3) Save Point for Function Change. HISTORY :
 * HISTORY : Revision 1.23 2005/07/18 19:45:56 jiangsc HISTORY : Added textual display for functions and properties. HISTORY : Beautified port display. HISTORY :
 * HISTORY : Revision 1.22 2005/07/15 18:58:52 jiangsc HISTORY : 1) Reconstucted Menu bars; HISTORY : 2) Integrated FunctionPane to display property; HISTORY :
 * 3) Enabled drag and drop functions to mapping panel. HISTORY : HISTORY : Revision 1.21 2005/07/14 22:24:38 jiangsc HISTORY : Save point HISTORY : HISTORY :
 * Revision 1.20 2005/07/14 17:20:04 jiangsc HISTORY : no message HISTORY : HISTORY : Revision 1.19 2005/07/13 22:04:26 jiangsc HISTORY : UI Update HISTORY :
 * HISTORY : Revision 1.18 2005/07/12 20:09:26 jiangsc HISTORY : Added severity support. HISTORY : HISTORY : Revision 1.17 2005/07/11 18:18:00 jiangsc HISTORY :
 * Partially implemented property pane. HISTORY : HISTORY : Revision 1.16 2005/07/07 19:16:14 jiangsc HISTORY : New Structure HISTORY : HISTORY : Revision 1.15
 * 2005/06/24 21:45:45 jiangsc HISTORY : Save Point HISTORY : HISTORY : Revision 1.14 2005/06/24 20:58:10 jiangsc HISTORY : Save Point HISTORY : HISTORY :
 * Revision 1.13 2005/06/21 23:03:07 jiangsc HISTORY : Put in new CSVPanel Implementation. HISTORY : HISTORY : Revision 1.12 2005/06/09 19:03:43 jiangsc HISTORY :
 * Fixed a minor error on reference to graphController.renderInJGraph(g, mappingsList); HISTORY : HISTORY : Revision 1.11 2005/06/09 17:48:21 jiangsc HISTORY :
 * Fix the issue of vertex for tree node display when tree collaping. HISTORY : HISTORY : Revision 1.10 2005/06/09 16:57:43 jiangsc HISTORY : Further save
 * point. HISTORY : HISTORY : Revision 1.9 2005/06/09 16:39:05 jiangsc HISTORY : Could create and modify map file. HISTORY : HISTORY : Revision 1.8 2005/06/08
 * 23:02:04 jiangsc HISTORY : Implemented New UI. HISTORY : HISTORY : Revision 1.7 2005/06/07 21:15:51 jiangsc HISTORY : Start implementation of CRUD on middle
 * panel. HISTORY : HISTORY : Revision 1.6 2005/06/06 22:34:47 jiangsc HISTORY : Save Point. HISTORY : HISTORY : Revision 1.5 2005/06/06 21:32:03 jiangsc
 * HISTORY : Save Point. HISTORY : HISTORY : Revision 1.4 2005/06/06 16:14:42 jiangsc HISTORY : Refactored on HL7 metadata loading. HISTORY : HISTORY : Revision
 * 1.3 2005/06/03 14:23:00 jiangsc HISTORY : Updated with new implementation of DnD and GUI consolidation.
 */
