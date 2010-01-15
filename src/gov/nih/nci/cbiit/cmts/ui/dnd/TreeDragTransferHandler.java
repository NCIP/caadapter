package gov.nih.nci.cbiit.cmts.ui.dnd;

import gov.nih.nci.cbiit.cmts.ui.common.CommonTransferHandler;

import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;


public class TreeDragTransferHandler extends CommonTransferHandler {

	public TreeDragTransferHandler() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	 */
	@Override
	protected Transferable createTransferable(JComponent c) {
		JTree tree = (JTree)c;
		TreePath path = tree.getSelectionPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
//		String pathString = null;
//		if(node.getUserObject() instanceof ElementMetaLoader.MyTreeObject)
//			pathString = UIHelper.getPathStringForNode(node);
//		else if(node.getUserObject() instanceof FunctionTypeNodeLoader.MyTreeObject){
//			FunctionDef f =((FunctionDef)((FunctionTypeNodeLoader.MyTreeObject)node.getUserObject()).getObj()); 
//			pathString = f.getGroup()+":"+f.getName();
//		}
		System.out.println("TreeDragTransferHandler.createTransferable()...TreeNode:"+node.getUserObject());
		if (node!=null)
			return new TreeTransferableNode(node);
		
		return super.createTransferable(c);
	}
}
