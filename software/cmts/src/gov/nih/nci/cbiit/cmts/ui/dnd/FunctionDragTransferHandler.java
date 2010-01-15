package gov.nih.nci.cbiit.cmts.ui.dnd;

import java.awt.datatransfer.Transferable;


import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import gov.nih.nci.cbiit.cmts.core.FunctionDef;
import gov.nih.nci.cbiit.cmts.ui.common.CommonTransferHandler;
import gov.nih.nci.cbiit.cmts.ui.function.FunctionTypeNodeLoader;
import gov.nih.nci.cbiit.cmts.ui.function.FunctionTypeNodeLoader.MyTreeObject;

public class FunctionDragTransferHandler extends CommonTransferHandler 
{
	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	 */
	@Override
	protected Transferable createTransferable(JComponent c) {
		JTree tree = (JTree)c;
		TreePath path = tree.getSelectionPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

		FunctionDef f =null;
		if(node.getUserObject() instanceof FunctionTypeNodeLoader.MyTreeObject){
			f=((FunctionDef)((FunctionTypeNodeLoader.MyTreeObject)node.getUserObject()).getObj()); 
		}
		System.out.println("FunctionDragTransferHandler.createTransferable()...:"+f);
		if (f!=null)
			return new FunctionDefTransferable(f);
		return super.createTransferable(c);
	}	
}
