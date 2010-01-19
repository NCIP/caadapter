package gov.nih.nci.cbiit.cmts.ui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;

import gov.nih.nci.cbiit.cmts.core.FunctionData;
import gov.nih.nci.cbiit.cmts.core.FunctionDef;
import gov.nih.nci.cbiit.cmts.ui.common.MappableNode;
import gov.nih.nci.cbiit.cmts.ui.function.FunctionBoxCell;
import gov.nih.nci.cbiit.cmts.ui.function.FunctionBoxDefaultPort;
import gov.nih.nci.cbiit.cmts.ui.function.FunctionTypeNodeLoader;
import gov.nih.nci.cbiit.cmts.ui.jgraph.MiddlePanelJGraph;
import gov.nih.nci.cbiit.cmts.ui.jgraph.MiddlePanelJGraphController;
import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.cbiit.cmts.ui.tree.DefaultSourceTreeNode;
import gov.nih.nci.cbiit.cmts.ui.tree.MappingSourceTree;

public class GraphDropTransferHandler extends CommonTransferHandler {
private static DataFlavor[] acceptableFlavors =new DataFlavor[]{TreeTransferableNode.mutableTreeNodeFlavor};

private static  final List<DataFlavor> acceptableFlavorsList = Arrays.asList( TreeTransferableNode.flavors );


/* (non-Javadoc)
 * @see javax.swing.TransferHandler#importData(javax.swing.TransferHandler.TransferSupport)
 */
@Override
public boolean importData(TransferSupport info) 
{
	Object transferableObject;
    try {
    	transferableObject = info.getTransferable().getTransferData(TreeTransferableNode.mutableTreeNodeFlavor);
    } catch (UnsupportedFlavorException e) {
    	e.printStackTrace();
        return false;
    } catch (IOException e) {
    	e.printStackTrace();
        return false;
    }
    System.out.println("GraphDropTransferHandler.importData()..transferable object:"+transferableObject);
    if (transferableObject instanceof DefaultMutableTreeNode)
    {
    	DefaultMutableTreeNode treeNodeTransfered=(DefaultMutableTreeNode)transferableObject;
    	MiddlePanelJGraph middlePanelGraph=(MiddlePanelJGraph)info.getComponent();
    	MappingMiddlePanel rootMappingPanel=(MappingMiddlePanel)retrieveRootMappingPanel(middlePanelGraph);
    	if (treeNodeTransfered.getUserObject() instanceof FunctionTypeNodeLoader.MyTreeObject)
    	{
    		FunctionDef cloneData=(FunctionDef)((FunctionTypeNodeLoader.MyTreeObject)treeNodeTransfered.getUserObject()).getObj();
//    		FunctionDef cloneData=(FunctionDef)((FunctionDef)transferableObject).clone();
    		System.out.println("GraphDropTransferHandler.importData()..cloneData hash:"+cloneData.hashCode());
    		return rootMappingPanel.getGraphController().addFunction(cloneData, info.getDropLocation().getDropPoint());
    	}
    	
    	
    	Point2D pDrop=info.getDropLocation().getDropPoint();
    	    	
    	boolean inputPort=false;
    	if (treeNodeTransfered instanceof DefaultSourceTreeNode)
    		inputPort=true;
    	FunctionBoxDefaultPort fPort= findNearestFunctionPortOnFunctionBox (middlePanelGraph, pDrop, inputPort);
    	
    	
    	System.out.println("GraphDropTransferHandler.importData()..drop port:"+((FunctionData)fPort.getUserObject()).getName());
    	System.out.println("GraphDropTransferHandler.importData()..drop port is input:"+fPort.isInput());
    	//create a link between tree node and function port
    	MappingSourceTree srcTree=(MappingSourceTree)rootMappingPanel.getMappingPanel().getSourceTree();
    	TreePath path = srcTree.getSelectionPath();
		DefaultMutableTreeNode srcNodeSelected = (DefaultMutableTreeNode) path.getLastPathComponent();   	 
    	boolean isSucess= rootMappingPanel.getGraphController().createTreeToFunctionBoxPortMapping((MappableNode)srcNodeSelected,fPort,new ArrayList());
    	if (isSucess)
    	{
    		((MappableNode)srcNodeSelected).setMapStatus(true);
    		srcTree.repaint();
    		fPort.setMapStatus(true);
    	}    	
    	return isSucess;
    }

    return false;
}

private JComponent retrieveRootMappingPanel(JComponent childComp)
{
	JComponent rtnComp=null;
	while (childComp.getParent()!=null)
	{
		rtnComp=(JComponent)childComp.getParent();
		if (rtnComp instanceof MappingMiddlePanel)
			return rtnComp;
		childComp=rtnComp;
	}
	return rtnComp;
	
}
///* (non-Javadoc)
// * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent, java.awt.datatransfer.DataFlavor)
// */
//@Override
// public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
//
//	System.out.println("GraphDropTransferHandler.canImport()..check super_flavors:"+super.canImport(comp, transferFlavors));
//	for (int i=0; i<transferFlavors.length;i++)
//	{
//		if (acceptableFlavorsList.contains(transferFlavors[i]))
//			return true;
//	}
//	System.out.println("GraphDropTransferHandler.canImport()..not acceptable DataFlavor kinds:"+transferFlavors.length);
//	return super.canImport(comp, transferFlavors);
// }
/* (non-Javadoc)
 * @see javax.swing.TransferHandler#canImport(javax.swing.TransferHandler.TransferSupport)
 */
@Override
public boolean canImport(TransferSupport info) {
//	System.out.println("GraphDropTransferHandler.canImport()..check super_TransferSupport:"+super.canImport(info));
	if (info.getTransferable().isDataFlavorSupported(TreeTransferableNode.mutableTreeNodeFlavor))
		return true;
//	System.out.println("GraphDropTransferHandler.canImport()..check support..:not support");
    return super.canImport(info);
}

/**
 * Search the nearest port if Drop on a FunctionBox
 * @param dropLocation
 * @return
 */
private FunctionBoxDefaultPort findNearestFunctionPortOnFunctionBox(JGraph graph, Point2D dropLocation, boolean inputPort)
{
	System.out
			.println("GraphDropTransferHandler.findNearestFunctionPortOnFunctionBox()...input port:"+inputPort);
	FunctionBoxDefaultPort rtnPort=null;
	Object object = graph.getFirstCellForLocation(dropLocation.getX(), dropLocation.getY());
	if (!(object instanceof FunctionBoxCell))
		return rtnPort;

	List<DefaultGraphCell> children=((FunctionBoxCell)object).getChildren();
	int disDif=0;
	for (DefaultGraphCell cell: children)
	{
		if (cell instanceof FunctionBoxDefaultPort)
		{
			FunctionBoxDefaultPort fPort=(FunctionBoxDefaultPort)cell;
			//only the un-mapped port interested
			if (fPort.isMapped())
				continue;
			FunctionData fData=(FunctionData)fPort.getUserObject();
			if (fData.isInput()!=inputPort)
				continue;
			Rectangle2D cellBounds=graph.getCellBounds(fPort);
			//caculate the distance between the cellBounds center and the clicked point
			double tempXDif=cellBounds.getCenterX()-dropLocation.getX();
			double tempYDif=cellBounds.getCenterY()-dropLocation.getY();
			int tempDis=(int)Math.sqrt(tempXDif*tempXDif+tempYDif*tempYDif);
			//remember the first un-mapped port or the closer one
			if (tempDis<disDif||disDif==0)
			{
				rtnPort=fPort;
				disDif=tempDis;
			}
		}
	}
	if (rtnPort!=null)
		graph.setSelectionCell(rtnPort);
	return rtnPort;
}
}
