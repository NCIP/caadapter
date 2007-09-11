/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/jgraph/LinkSelectionHighlighter.java,v 1.5 2007-09-11 20:27:03 schroedn Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.mapping.jgraph;

import gov.nih.nci.caadapter.ui.common.jgraph.MappingDataManager;
import gov.nih.nci.caadapter.ui.common.jgraph.MappingViewCommonComponent;
import gov.nih.nci.caadapter.ui.common.tree.MappingSourceTree;
import gov.nih.nci.caadapter.ui.common.tree.MappingTargetTree;
import gov.nih.nci.caadapter.ui.common.tree.DefaultSourceTreeNode;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.caadapter.ui.mapping.MappingTreeScrollPane;
import gov.nih.nci.caadapter.ui.mapping.jgraph.actions.*;
import gov.nih.nci.caadapter.mms.metadata.ModelMetadata;
import gov.nih.nci.caadapter.mms.metadata.AssociationMetadata;

import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;
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
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: schroedn $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.5 $
 *          date        $Date: 2007-09-11 20:27:03 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/jgraph/LinkSelectionHighlighter.java,v 1.5 2007-09-11 20:27:03 schroedn Exp $";

	private AbstractMappingPanel mappingPanel;
	private JGraph graph;
	private MappingMiddlePanel middlePanel;
	
    //private TestAction testAction;
    private LazyEagerAction lazyEagerAction;
    private PrimaryKeyAction primaryKeyAction;
    private ClobAction clobAction;
    private DiscriminatorAction discriminatorAction;
    private JPopupMenu popupMenu = null;
    
	private boolean graphInSelection = false;
	private boolean graphInClearSelectionMode = false;
	private boolean sourceTreeInSelection = false;
	private boolean targetTreeInSelection = false;

	//private String selectedNode; 
	
	public LinkSelectionHighlighter(AbstractMappingPanel mappingPanel, JGraph graph, MappingMiddlePanel middlePanel)
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
				searchMode = MappingViewCommonComponent.SEARCH_BY_SOURCE_NODE;
				//notify that tree is selection process.
				//System.out.println( "sourceTreeInSelection is true");
				sourceTreeInSelection = true;				
				mappingPanel.getTargetTree().clearSelection();				
			}
			else if(eventSource instanceof MappingTargetTree)
			{
				searchMode = MappingViewCommonComponent.SEARCH_BY_TARGET_NODE;
				//notify that tree is selection process.
				targetTreeInSelection = true;
				mappingPanel.getSourceTree().clearSelection();	
			}

			graphInClearSelectionMode = true;
			graph.clearSelection();
			graphInClearSelectionMode = false;

			if(!isAClearSelectionEvent(e) && searchMode!=null)
			{//if not a clear selection event and searchMode is not null.
				MappingDataManager dataManager = mappingPanel.getMappingDataManager();
				List<MappingViewCommonComponent> compList = dataManager.findMappingViewCommonComponentList(node, searchMode);
				int size = compList.size();
				for(int i=0; i<size; i++)
				{
					MappingViewCommonComponent comp = compList.get(i);
					DefaultEdge linkEdge = comp.getLinkEdge();
					if(graph!=null)
					{
						graph.setSelectionCell(linkEdge);
					}
//					else
//					{//just highlight the tree nodes.
//						GraphSelectionEvent event = new GraphSelectionEvent(this, new Object[]{linkEdge}, new boolean[]{false});
//						valueChanged(event);
//					}

//					//highlight the other tree node correspondingly
//					JTree treeToBeHighlighted = null;
//					DefaultMutableTreeNode treeNodeToBeHighlighted = null;
//					if(searchMode == MappingViewCommonComponent.SEARCH_BY_TARGET_NODE)
//					{//to highlight source tree
//						treeToBeHighlighted = mappingPanel.getSourceTree();
//					}
				}
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
            JOptionPane.showMessageDialog(mappingPanel, "You should input the source and target file names first.", "No Source or Target file", JOptionPane.ERROR_MESSAGE);
            return;
        }
        mappingPanel.setInDragDropMode(false);
        
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
		JPopupMenu popupMenu = new JPopupMenu();
		       
        //Primary Key Function
		primaryKeyAction = new PrimaryKeyAction( mappingPanel, middlePanel );
		JMenuItem menuItem = new JMenuItem(primaryKeyAction);
		popupMenu.add(menuItem);
		
		JTree sourceTree = mappingPanel.getSourceTree();			
		//JTree targetTree = mappingPanel.getTargetTree();
		
		//System.out.println( "Primary Keys: " + primaryKeys );
		
		// Disable PK function if selected node has children
		primaryKeyAction.setEnabled( false );
		//Check to see if anything is selected		
		if( sourceTree.getLeadSelectionPath() != null )
		{
			TreePath leadingPath = sourceTree.getLeadSelectionPath();
			
			//TreePath paths[] = sourceTree.getSelectionPaths();
			DefaultMutableTreeNode mutNode = (DefaultMutableTreeNode)leadingPath.getLastPathComponent();			
		
			if( mutNode.getChildCount() == 0 )
			{
				primaryKeyAction.setEnabled( true );
			}

            //todo: check for (Many to 1), (Many to Many), (1 to Many) set to false
            if ( mutNode.getUserObject() instanceof AssociationMetadata )
            {
                primaryKeyAction.setEnabled( false );
            }
        }
		return popupMenu;
	}
	//
	// PopupMenu
	//
	protected JPopupMenu createTargetPopupMenu()
	{
		JPopupMenu popupMenu = new JPopupMenu();
		
		String lazyText = "Set as Lazy";
        String clobText = "Set as CLOB";
        String discriminatorText = "Set as Discrimator";

        //Could change this depending on whether lazy/eager
    	ModelMetadata modelMetadata = ModelMetadata.getInstance();    	
    	List<String> lazyKeys = modelMetadata.getLazyKeys();
    	List<String> clobKeys = modelMetadata.getClobKeys();
        List<String> discriminatorKeys = modelMetadata.getDiscriminatorKeys();

        JTree targetTree = mappingPanel.getTargetTree();
		TreePath leadingPath = targetTree.getLeadSelectionPath();														
		
		System.out.println( "Lazy Keys: " + lazyKeys );
        if ( lazyKeys.contains( parseNode( leadingPath.toString() ) ) )
        {
        	lazyText = "Set as Eager";
        }
        else {
        	lazyText = "Set as Lazy";
        }

        System.out.println( "Clob Keys: " + clobKeys );
        if ( clobKeys.contains( parseNode( leadingPath.toString() ) ) )
        {
        	clobText = "Unset Clob";
        }
        else {
        	clobText = "Set as Clob";
        }

        System.out.println( "Discriminator Keys: " + discriminatorKeys );
        if ( discriminatorKeys.contains( parseNode( leadingPath.toString() ) ) )
        {
        	discriminatorText = "Unset Discriminator";
        }
        else {
        	discriminatorText = "Set as Discriminator";
        }

		lazyEagerAction = new LazyEagerAction( mappingPanel, middlePanel, lazyText );
        clobAction = new ClobAction( mappingPanel, middlePanel, clobText );
        discriminatorAction = new DiscriminatorAction( mappingPanel, middlePanel, discriminatorText );

        JMenuItem lazyItem = new JMenuItem(lazyEagerAction);
        JMenuItem clobItem = new JMenuItem(clobAction);
        JMenuItem discriminatorItem = new JMenuItem(discriminatorAction);

        popupMenu.add(lazyItem);
        popupMenu.add(clobItem);
        popupMenu.add(discriminatorItem);
		
		lazyEagerAction.setEnabled( false );
        clobAction.setEnabled( false );
        discriminatorAction.setEnabled( false );

        //Check to see if anything is selected
		if( targetTree.getLeadSelectionPath() != null )
		{
			leadingPath = targetTree.getLeadSelectionPath();
			DefaultMutableTreeNode mutNode = (DefaultMutableTreeNode)leadingPath.getLastPathComponent();
			
			// Disable PK function if selected node has children		
			if( mutNode.getChildCount() == 0 )
			{
				lazyEagerAction.setEnabled( true );
                clobAction.setEnabled( true );
                discriminatorAction.setEnabled( true );
            }
		}		
	
		return popupMenu;
	}

	public String parseNode( String node )
	{
		node = replace( node, ", ", "." );
		node = replace( node, "[", " " );
		node = replace( node, "]", " " );     
        node = replace( node, "Data Model.", "" );         
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
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.4  2007/09/05 15:15:47  schroedn
 * HISTORY      : Added icons to PK and Lazy/Eager
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/08/07 20:50:39  schroedn
 * HISTORY      : New Feature, Primary Key and Lazy/Eager functions added to MMS
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/08/07 15:51:25  schroedn
 * HISTORY      : New Feature, Primary Key and Lazy/Eager functions added to MMS
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:57  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.19  2006/12/19 22:48:30  umkis
 * HISTORY      : Null pointer error protetion
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/12/14 21:37:19  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/12/07 19:20:50  jiangsc
 * HISTORY      : With enhanced functions.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/11/23 20:03:59  jiangsc
 * HISTORY      : Enhancement on highlight functionality.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/11/09 23:05:51  jiangsc
 * HISTORY      : Back to previous version.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/24 20:31:00  jiangsc
 * HISTORY      : Turned off auto-scroll feature to comprise mapping issue.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/21 15:12:07  jiangsc
 * HISTORY      : Removed some comments lines
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/18 17:01:03  jiangsc
 * HISTORY      : Changed one function signature in DragDrop component;
 * HISTORY      : Enhanced drag-drop status monitoring in HL7MappingPanel;
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/07 22:20:58  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/03 19:33:59  jiangsc
 * HISTORY      : Implement highlighting tree nodes upon graph selection.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/09/27 21:47:58  jiangsc
 * HISTORY      : Customized edge rendering and initially added a link highlighter class.
 * HISTORY      :
 */
