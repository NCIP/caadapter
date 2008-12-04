/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmps.ui.jgraph;


import gov.nih.nci.cbiit.cmps.ui.mapping.CmpsMappingPanel;
import gov.nih.nci.cbiit.cmps.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.cbiit.cmps.ui.mapping.MappingTreeScrollPane;
import gov.nih.nci.cbiit.cmps.ui.tree.MappingSourceTree;
import gov.nih.nci.cbiit.cmps.ui.tree.MappingTargetTree;

import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;

/**
 * This class defines a highlighter class for graph presentation.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2008-12-04 21:34:20 $
 */
public class LinkSelectionHighlighter extends MouseAdapter implements GraphSelectionListener, TreeSelectionListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: LinkSelectionHighlighter.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/cmps/src/gov/nih/nci/cbiit/cmps/ui/jgraph/LinkSelectionHighlighter.java,v 1.2 2008-12-04 21:34:20 linc Exp $";

	private CmpsMappingPanel mappingPanel;
	private JGraph graph;
	private MappingMiddlePanel middlePanel;
	
    //private TestAction testAction;
    private JPopupMenu popupMenu = null;
    
	private boolean graphInSelection = false;
	private boolean graphInClearSelectionMode = false;
	private boolean sourceTreeInSelection = false;
	private boolean targetTreeInSelection = false;

	//private String selectedNode; 
	
	public LinkSelectionHighlighter(CmpsMappingPanel mappingPanel, JGraph graph, MappingMiddlePanel middlePanel)
	{
		this.middlePanel = middlePanel;
		this.mappingPanel = mappingPanel;
		this.graph = graph;
		initialize();
	}

	private void initialize()
	{
		if(mappingPanel!=null)
		{
//			mappingPanel.removeMouseListener(this);
//			mappingPanel.addMouseListener(this);
			JTree tree = mappingPanel.getSourceTree();
			if(tree!=null)
			{
				tree.removeMouseListener(this);
				tree.addMouseListener(this);
			}
			tree = mappingPanel.getTargetTree();
			if(tree!=null)
			{
				tree.removeMouseListener(this);
				tree.addMouseListener(this);
			}
		}
		if(graph!=null)
		{
			graph.addMouseListener(this);
		}
	}

//	public LinkSelectionHighlighter(HL7MappingPanel mappingPanel)
//	{
//		this.mappingPanel = mappingPanel;
//	}
//
//	void setGraph(JGraph newGraph)
//	{
//		graph = newGraph;
//	}

//	private void clearSelections(short clearWhichSelection)
//	{
//		switch(clearWhichSelection)
//		{
//			case CLEAR_SELECTION_GRAPH:
//				graph.clearSelection();
//				break;
//		}
//		mappingPanel.getSourceTree().clearSelection();
//		mappingPanel.getTargetTree().clearSelection();
//	}

	/**
	 * Called whenever the value of the selection changes.
	 *
	 * @param e the event that characterizes the change.
	 */
	public void valueChanged(GraphSelectionEvent e)
	{
//		Log.logInfo(this, "A new Graph Cell is selected. '" + (e == null ? e : e.getCell()) + "'");
        try
        {
            if (mappingPanel.isInDragDropMode())
		    {//if in dragging mode, ignore.
			    return;
		    }
        }
        catch(NullPointerException ne)
        {
            JOptionPane.showMessageDialog(mappingPanel, "You should input the source and target file names first(2).", "No Source or Target file", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(graphInSelection)
		{
//			Log.logInfo(this, "In graph selection mode, just a re-entry, so ignore.");
			return;
		}
		//clear tree selections if and only if the call is NOT orignated from any tree node selection
		if(sourceTreeInSelection)
		{//clear the opposite
			mappingPanel.getTargetTree().clearSelection();
		}

		if(targetTreeInSelection)
		{//clear the opposite
			mappingPanel.getSourceTree().clearSelection();
		}

		graphInSelection = true;

		if(!sourceTreeInSelection && !targetTreeInSelection)
		{
            if (mappingPanel.getTargetTree() == null)
            {
                JOptionPane.showMessageDialog(mappingPanel, "You should input the source file name first(2).", "No Source file", JOptionPane.ERROR_MESSAGE);
                return;
            }
            else mappingPanel.getTargetTree().clearSelection();
            if (mappingPanel.getSourceTree() == null)
            {
                JOptionPane.showMessageDialog(mappingPanel, "You should input the target file name first(2).", "No Target file", JOptionPane.ERROR_MESSAGE);
                return;
            }
            else mappingPanel.getSourceTree().clearSelection();

            //mappingPanel.getTargetTree().clearSelection();
			//mappingPanel.getSourceTree().clearSelection();
		}
		if(!isAClearSelectionEvent(e))
		{//ignore if it is a clear selection event
			Object obj = e.getCell();
			if (!graphInClearSelectionMode && obj instanceof DefaultEdge)
			{//only handles edge, when graph is NOT in CLEAR selection mode.
				DefaultEdge edge = (DefaultEdge) obj;
				Object source = edge.getSource();
				Object target = edge.getTarget();

				if(!sourceTreeInSelection)
				{//manually highlight if and only if it is orignated from graph selection.
					Object sourceUserObject = getUserObject(source);
					highlightTreeNodeInTree(mappingPanel.getSourceTree(), sourceUserObject);
				}

				if(!targetTreeInSelection)
				{//manually highlight if and only if it is orignated from graph selection.
					Object targetUserObject = getUserObject(target);
					highlightTreeNodeInTree(mappingPanel.getTargetTree(), targetUserObject);
				}
			}
		}
		graphInSelection = false;
	}

	/**
	 * If it is clear selection event, the isAddedXXX() will return false, so the return will be true, i.e., it is a clear selection event.
	 * @param event
	 * @return true if it is a clear selection event; otherwise, return false.
	 */
	private boolean isAClearSelectionEvent(EventObject event)
	{
		boolean result = false;
		if(event instanceof GraphSelectionEvent)
		{
			result = !((GraphSelectionEvent) event).isAddedCell();
		}
		else if(event instanceof TreeSelectionEvent)
		{
			result = !((TreeSelectionEvent) event).isAddedPath();
		}
		return result;
	}

	private Object getUserObject(Object graphOrTreeNode)
	{
		if(graphOrTreeNode instanceof DefaultPort)
		{
			DefaultMutableTreeNode parentCell = (DefaultMutableTreeNode)((DefaultPort) graphOrTreeNode).getParent();
			return getUserObject(parentCell);
		}
		else if(graphOrTreeNode instanceof DefaultMutableTreeNode)
		{
			return ((DefaultMutableTreeNode)graphOrTreeNode).getUserObject();
		}
		else
		{
			return null;
		}
	}

	private void highlightTreeNodeInTree(JTree tree, Object object)
	{
		if ((!(object instanceof DefaultGraphCell) && (object instanceof DefaultMutableTreeNode)))
		{//screen out possible graph cell but just leave pure tree node to be highlighted
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) object;			
			TreePath treePath = new TreePath(treeNode.getPath());
			
			//System.out.println( "HighlightTreeNodeInTree: " + treePath.toString() );
			
//			tree.scrollPathToVisible(treePath);
			tree.setSelectionPath(treePath);
		}
	}

	/**
	 * Called whenever the value of the selection changes.
	 *
	 * @param e the event that characterizes the change.
	 */
	public void valueChanged(TreeSelectionEvent e)
	{
        try
        {
            if (mappingPanel.isInDragDropMode())
		    {//if in dragging mode, ignore.
			    return;
		    }
        }
        catch(NullPointerException ne)
        {
            JOptionPane.showMessageDialog(mappingPanel, "You should input the source and target file names first(1).", "No Source or Target file", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (mappingPanel.isInDragDropMode())
		{//if in dragging mode, ignore.
			return;
		}
		Object eventSource = e.getSource();
		TreePath path = e.getPath();
		Object node = (path==null)? null : path.getLastPathComponent();
//		Log.logInfo(this, "TreeSelectionSource:'" + (eventSource ==null? "null" : eventSource.getClass().getName()) + "'");
		
		//System.out.println( "TreeSelectionSource:'" + (eventSource ==null? "null" : eventSource.getClass().getName()) + "'");
		
		if(graphInSelection || sourceTreeInSelection || targetTreeInSelection)
		{//if graph is in selection, no need to execute further logic; otherwise, we may run into an indefinite loop.
//			Log.logInfo(this, "In Graph or tree selection mode, so just ignore.");
			return;
		}
		else
		{
			if(node==null)
			{
				return;
			}

			String searchMode = null;
			if(eventSource instanceof MappingSourceTree)
			{
//				searchMode = MappingViewCommonComponent.SEARCH_BY_SOURCE_NODE;
				//notify that tree is selection process.
				//System.out.println( "sourceTreeInSelection is true");
				sourceTreeInSelection = true;				
				mappingPanel.getTargetTree().clearSelection();				
			}
			else if(eventSource instanceof MappingTargetTree)
			{
//				searchMode = MappingViewCommonComponent.SEARCH_BY_TARGET_NODE;
				//notify that tree is selection process.
				targetTreeInSelection = true;
				mappingPanel.getSourceTree().clearSelection();	
			}

			graphInClearSelectionMode = true;
			graph.clearSelection();
			graphInClearSelectionMode = false;

			if(!isAClearSelectionEvent(e) && searchMode!=null)
			{//if not a clear selection event and searchMode is not null.
				MiddlePanelJGraphController dataManager = mappingPanel.getMiddlePanelJGraphController();
//				List<MappingViewCommonComponent> compList = dataManager.findMappingViewCommonComponentList(node, searchMode);
//				int size = compList.size();
//				for(int i=0; i<size; i++)
//				{
//					MappingViewCommonComponent comp = compList.get(i);
//					DefaultEdge linkEdge = comp.getLinkEdge();
//					if(graph!=null)
//					{
//						graph.setSelectionCell(linkEdge);
//					}
////					else
////					{//just highlight the tree nodes.
////						GraphSelectionEvent event = new GraphSelectionEvent(this, new Object[]{linkEdge}, new boolean[]{false});
////						valueChanged(event);
////					}
//
////					//highlight the other tree node correspondingly
////					JTree treeToBeHighlighted = null;
////					DefaultMutableTreeNode treeNodeToBeHighlighted = null;
////					if(searchMode == MappingViewCommonComponent.SEARCH_BY_TARGET_NODE)
////					{//to highlight source tree
////						treeToBeHighlighted = mappingPanel.getSourceTree();
////					}
//				}
			}//end of if(searchMode!=null)
			if (eventSource instanceof MappingSourceTree)
			{
				//notify that tree is end of selection process.
				sourceTreeInSelection = false;
			}
			else if (eventSource instanceof MappingTargetTree)
			{
				//notify that tree is end of selection process.
				targetTreeInSelection = false;
			}
		}
	}

	/**
	 * Invoked when the mouse has been clicked on a component.
	 */
	public void mouseClicked(MouseEvent e)
	{
		// If Right Mouse Button
		if (SwingUtilities.isRightMouseButton(e))
		{         						
			Container parentC = e.getComponent().getParent();
			
			while ( !(parentC instanceof JScrollPane))
			{
				parentC=parentC.getParent();
			}
			
			MappingTreeScrollPane mappingScroll=(MappingTreeScrollPane)parentC;
			
			if(mappingScroll.getPaneType().equals(MappingTreeScrollPane.DRAW_NODE_TO_LEFT)) 
			{
				// Create PopupMenu for the Cell
				JPopupMenu menu = createTargetPopupMenu();
						
				// Display PopupMenu
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
			
			if(mappingScroll.getPaneType().equals(MappingTreeScrollPane.DRAW_NODE_TO_RIGHT))
			{
				// Create PopupMenu for the Cell
				JPopupMenu menu = createSourcePopupMenu();
						
				// Display PopupMenu
				menu.show(e.getComponent(), e.getX(), e.getY());
			}	
		}

		//if mouse clicked, it is definitely not in drag and drop.
        boolean previousValue;
        try
        {
            previousValue = mappingPanel.isInDragDropMode();
        }
        catch(NullPointerException ne)
        {
            JOptionPane.showMessageDialog(mappingPanel, "You should input the source and target file names first.", "No Source or Target file", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if(previousValue)
		{//previously in drag and drop mode, so to ensure the highlight back up, generate the corresponding tree or graph selection event.
			Object source = e.getSource();
//			String name = source == null ? "null" : source.getClass().getName();
//			System.out.println("Source is ' " + name + "'," + "LinkSelectionHighlighter's mouseClicked is called");
			//following code tries to trigger the valueChanged() methods above to mimic and restore the "highlight" command
			if(source instanceof JGraph)
			{
				JGraph mGraph = (JGraph) source;			
				mGraph.setSelectionCells(mGraph.getSelectionCells());
//				GraphSelectionEvent event = new GraphSelectionEvent(source, new Object[]{}, )
			}
			else if(source instanceof JTree)
			{
				JTree mTree = (JTree) source;
				//following code does not trigger valueChanged(TreeSelectionEvent) above.
				//mTree.setSelectionPaths(mTree.getSelectionPaths());
				//mTree.setSelectionRows(mTree.getSelectionRows());
				TreePath[] paths = mTree.getSelectionPaths();
				int size = paths==null? 0 : paths.length;
				if(size>0)
				{
					boolean[] areNew = new boolean[size];
					for(int i=0; i<size; i++)
					{
						areNew[i] = true;
					}
					TreePath leadingPath = mTree.getLeadSelectionPath();
					TreeSelectionEvent event = new TreeSelectionEvent(source, paths, areNew, null, leadingPath);
					valueChanged(event);
				}
			}
		}
	}
	
	//
	// PopupMenu
	//
	protected JPopupMenu createSourcePopupMenu()
	{
        JTree sourceTree = mappingPanel.getSourceTree();
        JPopupMenu popupMenu = new JPopupMenu();

        //Primary Key Function
        String primaryKeyText = "Set as Primary Key";

		return popupMenu;
	}
	//
	// PopupMenu
	//
	protected JPopupMenu createTargetPopupMenu()
	{
		JPopupMenu popupMenu = new JPopupMenu();
		
		String lazyText = "Set as Eager";
        String clobText = "Set as CLOB";
        String discriminatorText = "Set as Discrimator";

        //Could change this depending on whether lazy/eager
	
		return popupMenu;
	}

	public String parseNode( String node )
	{
		node = replace( node, ", ", "." );
		node = replace( node, "[", " " );
		node = replace( node, "]", " " );
        node = replace( node, "(A)", " " );
        //node = replace( node, ")", " " );
        node = replace( node, "Data Model.", "" );
        node = replace( node, "Object Model.", "" );
        node = node.trim();        
		return node; 
	}
	
    static String replace(String str, String pattern, String replace) {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();
    
        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e+pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }
    
}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.1  2008/10/30 16:02:14  linc
 * HISTORY: updated.
 * HISTORY:
 */
