package gov.nih.nci.cbiit.cmts.ui.function;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import gov.nih.nci.cbiit.cmts.core.FunctionDef;
import gov.nih.nci.cbiit.cmts.ui.common.CommonTransferHandler;
import gov.nih.nci.cbiit.cmts.ui.jgraph.MiddlePanelJGraphController;

public class FunctionTransferHandler extends CommonTransferHandler 
{
	private MiddlePanelJGraphController graphController;

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
		if (f!=null)
			return new FunctionDefTransfer(f);
		return super.createTransferable(c);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent, java.awt.datatransfer.DataFlavor)
	 */
	@Override
	 public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		for (int i=0; i<transferFlavors.length;i++)
		{
			if (transferFlavors[i].getHumanPresentableName().equals(FunctionDefTransfer.FUNCTION_DEFINITION_FLAVOR))
				return true;
		}
		return super.canImport(comp, transferFlavors);
	 }
	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#canImport(javax.swing.TransferHandler.TransferSupport)
	 */
	@Override
	public boolean canImport(TransferSupport info) {
		DataFlavor[] transferFlavors=info.getDataFlavors();
		for (int i=0; i<transferFlavors.length;i++)
		{
			if (transferFlavors[i].getHumanPresentableName().equals(FunctionDefTransfer.FUNCTION_DEFINITION_FLAVOR))
				return true;
		}
        return super.canImport(info);
	}
	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#importData(javax.swing.TransferHandler.TransferSupport)
	 */
	@Override
	public boolean importData(TransferSupport info) 
	{
		Object data;
        try {
            data = info.getTransferable().getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException e) {
        	e.printStackTrace();
            return false;
        } catch (IOException e) {
        	e.printStackTrace();
            return false;
        }
        if (data instanceof FunctionDef)
        	return getGraphController().addFunction((FunctionDef)data, info.getDropLocation().getDropPoint());
        
        return false;
	}
	
	/**
	 * Return middle panel graph controller
	 * @return
	 */
	public MiddlePanelJGraphController getGraphController() {
		return graphController;
	}
	public void setGraphController(MiddlePanelJGraphController graphController) {
		this.graphController = graphController;
	}
	
}
