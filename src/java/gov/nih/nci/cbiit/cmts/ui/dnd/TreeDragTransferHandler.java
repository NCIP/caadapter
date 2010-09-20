package gov.nih.nci.cbiit.cmts.ui.dnd;


import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;


public class TreeDragTransferHandler extends CommonTransferHandler {

	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	 */
	@Override
	protected Transferable createTransferable(JComponent c) {
		JTree tree = (JTree)c;
		TreePath path = tree.getSelectionPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		if (node!=null)
			return new TreeTransferableNode(node);
		
		return super.createTransferable(c);
	}
}
