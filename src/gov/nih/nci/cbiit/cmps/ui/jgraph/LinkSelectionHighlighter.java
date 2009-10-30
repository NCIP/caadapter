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
import gov.nih.nci.cbiit.cmps.ui.properties.DefaultPropertiesSwitchController;
import gov.nih.nci.cbiit.cmps.ui.tree.MappingSourceTree;
import gov.nih.nci.cbiit.cmps.ui.tree.MappingTargetTree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
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
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMPS v1.0
 * @version    $Revision: 1.4 $
 * @date       $Date: 2009-10-30 14:35:01 $
 */
public class LinkSelectionHighlighter implements GraphSelectionListener
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/cmps/src/gov/nih/nci/cbiit/cmps/ui/jgraph/LinkSelectionHighlighter.java,v 1.4 2009-10-30 14:35:01 wangeug Exp $";  
	private MiddlePanelJGraphController graphController;
	public LinkSelectionHighlighter(MiddlePanelJGraphController controller)
	{
		graphController = controller;
	}

	/**
	 * Called whenever the value of the selection changes.
	 *
	 * @param e the event that characterizes the change.
	 */
	public void valueChanged(GraphSelectionEvent e)
	{
//		Log.logInfo(this, "A new Graph Cell is selected. '" + (e == null ? e : e.getCell()) + "'");
		CmpsMappingPanel mappingPanel= graphController.getMappingPanel();
		try
        {
            if (mappingPanel.isInDragDropMode())
			    return;
        }
        catch(NullPointerException ne)
        {
            JOptionPane.showMessageDialog(mappingPanel, "You should input the source and target file names first(2).", "No Source or Target file", JOptionPane.ERROR_MESSAGE);
            return;
        }
        mappingPanel.getMiddlePanelJGraphController().setGraphSelected(true);
		//the graph is in selection mode, do not set property pan for Element or Attribute
        if(!isAClearSelectionEvent(e))
		{//ignore if it is a clear selection event
			Object obj = e.getCell();
			if (obj instanceof DefaultEdge)
			{//only handles edge, when graph is NOT in CLEAR selection mode.
				DefaultEdge edge = (DefaultEdge) obj;
				Object source = edge.getSource();
				Object target = edge.getTarget();

				//manually highlight if and only if it is orignated from graph selection.
				Object sourceUserObject = getUserObject(source);
				highlightTreeNodeInTree(mappingPanel.getSourceTree(), sourceUserObject);
				

				//manually highlight if and only if it is orignated from graph selection.
				Object targetUserObject = getUserObject(target);
				highlightTreeNodeInTree(mappingPanel.getTargetTree(), targetUserObject);
			}
		}
		
		if(e.getCell() instanceof DefaultGraphCell)
		{
			Object newSelection = ((DefaultGraphCell)e.getCell()).getUserObject();
			DefaultPropertiesSwitchController propertySwitchConroller =(DefaultPropertiesSwitchController)mappingPanel.getMiddlePanelJGraphController().getPropertiesSwitchController();
			propertySwitchConroller.setSelectedItem(newSelection);
		}
		mappingPanel.getMiddlePanelJGraphController().getPropertiesSwitchController().getPropertiesPage().updateProptiesDisplay(null);
		mappingPanel.getMiddlePanelJGraphController().setGraphSelected(false);
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
			tree.setSelectionPath(treePath);
		}
	}	    
}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.3  2009/10/28 15:02:44  wangeug
 * HISTORY: hook property panel with link graph
 * HISTORY:
 * HISTORY: Revision 1.2  2008/12/04 21:34:20  linc
 * HISTORY: Drap and Drop support with new Swing.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/10/30 16:02:14  linc
 * HISTORY: updated.
 * HISTORY:
 */
