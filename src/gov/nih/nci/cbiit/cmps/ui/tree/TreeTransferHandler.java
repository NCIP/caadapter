/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmps.ui.tree;

import gov.nih.nci.cbiit.cmps.core.AttributeMeta;
import gov.nih.nci.cbiit.cmps.core.ElementMeta;
import gov.nih.nci.cbiit.cmps.core.FunctionDef;
import gov.nih.nci.cbiit.cmps.ui.common.MappableNode;
import gov.nih.nci.cbiit.cmps.ui.common.UIHelper;
import gov.nih.nci.cbiit.cmps.ui.function.FunctionTypeNodeLoader;
import gov.nih.nci.cbiit.cmps.ui.mapping.CmpsMappingPanel;
import gov.nih.nci.cbiit.cmps.ui.mapping.ElementMetaLoader;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * This class 
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.4 $
 * @date       $Date: 2008-12-29 22:18:18 $
 *
 */
public class TreeTransferHandler extends TransferHandler {

	private CmpsMappingPanel panel;
	public static final int READY = 0;
	public static final int START = 1;
	public static final int TRANSFER = 2;
	public static final int IMPORT = 3;
	
	private int state = READY;
	
	/**
	 * @param tree
	 */
	public TreeTransferHandler(CmpsMappingPanel panel) {
		this.panel = panel;
	}

	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}

	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#canImport(javax.swing.TransferHandler.TransferSupport)
	 */
	@Override
	public boolean canImport(TransferSupport info) {
		//System.out.println("canImport:"+info);
        if (!info.isDrop()) {
            return false;
        }

        info.setShowDropLocation(true);

        if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return false;
        }

        JTree.DropLocation dl = (JTree.DropLocation)info.getDropLocation();
        TreePath path = dl.getPath();
        if (path == null) {
            return false;
        }
        if(path.getLastPathComponent() instanceof DefaultSourceTreeNode){
        	return false;
        }
        this.state = TRANSFER;
        return true;

	}

	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	 */
	@Override
	protected Transferable createTransferable(JComponent c) {
		System.out.println("createTransferable:"+c);
		JTree tree = (JTree)c;
		TreePath path = tree.getSelectionPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		String pathString = null;
		if(node.getUserObject() instanceof ElementMetaLoader.MyTreeObject)
			pathString = UIHelper.getPathStringForNode(node);
		else if(node.getUserObject() instanceof FunctionTypeNodeLoader.MyTreeObject){
			FunctionDef f =((FunctionDef)((FunctionTypeNodeLoader.MyTreeObject)node.getUserObject()).getObj()); 
			pathString = f.getGroup()+":"+f.getName();
		}
		System.out.println("createTransferable: obj="+pathString);
        this.state = START;
		return new StringSelection(pathString);
	}

	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent, java.awt.datatransfer.Transferable, int)
	 */
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		// TODO Auto-generated method stub
		System.out.println("exportDone:source="+source+", data="+data);
        this.state = READY;

		super.exportDone(source, data, action);
	}

	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
	 */
	@Override
	public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
	}

	/* (non-Javadoc)
	 * @see javax.swing.TransferHandler#importData(javax.swing.TransferHandler.TransferSupport)
	 */
	@Override
	public boolean importData(TransferSupport info) {
		System.out.println("importData:"+info);
        if (!canImport(info)) {
            return false;
        }
        JTree.DropLocation dl = (JTree.DropLocation)info.getDropLocation();
        TreePath path = dl.getPath();
        int childIndex = dl.getChildIndex();

        String data;
        try {
            data = (String)info.getTransferable().getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

        DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        String targetData = UIHelper.getPathStringForNode(targetNode);
        DefaultMutableTreeNode sourceNode = UIHelper.findTreeNodeWithXmlPath((DefaultMutableTreeNode)panel.getSourceTree().getModel().getRoot(), data);
        
        boolean ret = this.panel.getMappingDataManager().createMapping((MappableNode)sourceNode, (MappableNode)targetNode);
        this.state = IMPORT;

        return true;
	}

}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.3  2008/12/10 15:43:03  linc
 * HISTORY: Fixed component id generator and delete link.
 * HISTORY:
 * HISTORY: Revision 1.2  2008/12/09 19:04:17  linc
 * HISTORY: First GUI release
 * HISTORY:
 * HISTORY: Revision 1.1  2008/12/04 21:34:20  linc
 * HISTORY: Drap and Drop support with new Swing.
 * HISTORY:
 */

