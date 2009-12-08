package gov.nih.nci.cbiit.cmps.ui.function;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import gov.nih.nci.cbiit.cmps.core.FunctionDef;
import gov.nih.nci.cbiit.cmps.ui.common.CommonTransferHandler;
import gov.nih.nci.cbiit.cmps.ui.common.MappableNode;
import gov.nih.nci.cbiit.cmps.ui.common.UIHelper;
import gov.nih.nci.cbiit.cmps.ui.mapping.CmpsMappingPanel;
import gov.nih.nci.cbiit.cmps.ui.mapping.ElementMetaLoader;
import gov.nih.nci.cbiit.cmps.ui.tree.DefaultSourceTreeNode;



public class FunctionTransferHandler extends CommonTransferHandler 
{
	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	 */
	@Override
	protected Transferable createTransferable(JComponent c) {
		JTree tree = (JTree)c;
		TreePath path = tree.getSelectionPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		String pathString = null;
		FunctionDef f =null;
		if(node.getUserObject() instanceof FunctionTypeNodeLoader.MyTreeObject){
			f=((FunctionDef)((FunctionTypeNodeLoader.MyTreeObject)node.getUserObject()).getObj()); 
			pathString = f.getGroup()+":"+f.getName();
		}
		System.out.println("FunctionTransferHandler.createTransferable() ..createTransferable: obj="+pathString);
		return new StringSelection(pathString);
	}
	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#canImport(javax.swing.TransferHandler.TransferSupport)
	 */
	@Override
	public boolean canImport(TransferSupport info) {	
		if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return false;
        }
        return true;
	}
	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#importData(javax.swing.TransferHandler.TransferSupport)
	 */
	@Override
	public boolean importData(TransferSupport info) 
	{
		DropLocation dl = info.getDropLocation();
		Point dlP=        dl.getDropPoint();
        String data;
        try {
            data = (String)info.getTransferable().getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException e) {
        	e.printStackTrace();
            return false;
        } catch (IOException e) {
        	e.printStackTrace();
            return false;
        }
        System.out.println("FunctionTransferHandler.importData()..drop:"+data +"...position point:"+dlP);
//        JTree.DropLocation dl = (JTree.DropLocation)info.getDropLocation();
//        TreePath path = dl.getPath();
//        DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) path.getLastPathComponent();
//        DefaultMutableTreeNode sourceNode = UIHelper.findTreeNodeWithXmlPath((DefaultMutableTreeNode)panel.getSourceTree().getModel().getRoot(), data);
        
//        boolean ret = this.panel.getMiddlePanel().getGraphController().createMapping((MappableNode)sourceNode, (MappableNode)targetNode);
//        System.out.println("TreeTransferHandler.importData()..dragged object:"+data +"...accepted:"+ret);
        return true;
	}
}
