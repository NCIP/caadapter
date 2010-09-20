/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */


package gov.nih.nci.cbiit.cmts.ui.function;


import gov.nih.nci.cbiit.cmts.common.FunctionManager;
import gov.nih.nci.cbiit.cmts.core.FunctionType;
import gov.nih.nci.cbiit.cmts.ui.dnd.GraphDropTransferHandler;
import gov.nih.nci.cbiit.cmts.ui.dnd.TreeDragTransferHandler;
import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMainPanel;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;

/**
 * This class displays a scrollable panel listing functions available in FunctionTypeImpl and organizes by Group name.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2009-12-02 18:47:37 $
 */
public class FunctionLibraryPane extends JPanel// implements TreeSelectionListener
{
	private JTree tree;
	private MappingMainPanel parent;
	/**
	 * Creates a Function Library tree and adds it to a panel for display.
	 *
	 * @@param lstFunction a FunctionTypeIml object
	 */
	public FunctionLibraryPane(MappingMainPanel p)
	{
		this.parent = p;
		initialize();
	}

	public JTree getFunctionTree()
	{
		return tree;
	}

	private void initialize()
	{
		//set the default layout
        this.setLayout(new BorderLayout());
		//Create the nodes.
		FunctionTypeNodeLoader nodeLoader = new FunctionTypeNodeLoader();
		try
		{
			DefaultMutableTreeNode kindNode = (DefaultMutableTreeNode) nodeLoader.loadData(FunctionManager.getInstance().getGroupList());

			//Create a tree that allows one selection at a time.
			tree = new JTree(kindNode);
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			tree.setDragEnabled(true);
			TreeDragTransferHandler dragHandler=new TreeDragTransferHandler();
			tree.setTransferHandler(dragHandler);
		
			GraphDropTransferHandler gDropHandler=new GraphDropTransferHandler();
			parent.getMiddlePanel().getGraph().setTransferHandler(gDropHandler);
			
			//Create the scroll pane and add the tree to it.
			JScrollPane treeView = new JScrollPane(tree);
			//Add treeView to this panel.
			this.add(treeView, BorderLayout.CENTER);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Return the user selection of a give function. If nothing is selected, will return null.
	 * @return
	 */
	public FunctionType getFunctionSelection()
	{
		FunctionType result = null;
		TreePath treePath = tree.getSelectionPath();
		if(treePath!=null)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
			result = (FunctionType) node.getUserObject();
		}
		return result;
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2008/12/29 22:18:18  linc
 * HISTORY      : function UI added.
 * HISTORY      :
 */
