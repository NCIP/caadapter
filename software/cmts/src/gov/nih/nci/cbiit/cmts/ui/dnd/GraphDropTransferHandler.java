/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.ui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
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
import gov.nih.nci.cbiit.cmts.ui.function.FunctionBoxGraphPort;
import gov.nih.nci.cbiit.cmts.ui.function.FunctionBoxGraphCell;
import gov.nih.nci.cbiit.cmts.ui.function.FunctionTypeNodeLoader;
import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.cbiit.cmts.ui.tree.DefaultSourceTreeNode;

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
   
    if (transferableObject instanceof DefaultMutableTreeNode)
    {
    	DefaultMutableTreeNode treeNodeTransfered=(DefaultMutableTreeNode)transferableObject;
    	JGraph middlePanelGraph=(JGraph)info.getComponent();
    	MappingMiddlePanel rootMappingPanel=(MappingMiddlePanel)retrieveRootMappingPanel(middlePanelGraph);
    	if (treeNodeTransfered.getUserObject() instanceof FunctionTypeNodeLoader.MyTreeObject)
    	{
    		FunctionDef cloneData=(FunctionDef)((FunctionTypeNodeLoader.MyTreeObject)treeNodeTransfered.getUserObject()).getObj();
    		return rootMappingPanel.getMappingPanel().getGraphController().addFunction(cloneData, info.getDropLocation().getDropPoint());
    	}
    	Point2D pDrop=info.getDropLocation().getDropPoint();
    	    	
    	boolean inputPort=false;
    	if (treeNodeTransfered instanceof DefaultSourceTreeNode)
    		inputPort=true;
    	FunctionBoxGraphPort fPort= findNearestFunctionPortOnFunctionBox (middlePanelGraph, pDrop, inputPort);
    	
    	//create a link between tree node and function port
    	TreePath pathSelected=rootMappingPanel.getMappingPanel().getTargetTree().getSelectionPath();    	
    	if (inputPort)
    		pathSelected=rootMappingPanel.getMappingPanel().getSourceTree().getSelectionPath();

		DefaultMutableTreeNode nodeSelected = (DefaultMutableTreeNode) pathSelected.getLastPathComponent();   	 
    	boolean isSucess= rootMappingPanel.getGraphController().createMapping((MappableNode)nodeSelected,fPort);
    	if (isSucess)
    	{
    		if (inputPort)
    			rootMappingPanel.getMappingPanel().getSourceTree().repaint();
    		else
    			rootMappingPanel.getMappingPanel().getTargetTree().repaint();
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

/* (non-Javadoc)
 * @see javax.swing.TransferHandler#canImport(javax.swing.TransferHandler.TransferSupport)
 */
@Override
public boolean canImport(TransferSupport info) {
	if (info.getTransferable().isDataFlavorSupported(TreeTransferableNode.mutableTreeNodeFlavor))
		return true;
    return super.canImport(info);
}

/**
 * Search the nearest port if Drop on a FunctionBox
 * @param dropLocation
 * @return
 */
private FunctionBoxGraphPort findNearestFunctionPortOnFunctionBox(JGraph graph, Point2D dropLocation, boolean inputPort)
{
	FunctionBoxGraphPort rtnPort=null;
	Object object = graph.getFirstCellForLocation(dropLocation.getX(), dropLocation.getY());
	if (!(object instanceof FunctionBoxGraphCell))
		return rtnPort;

	List<DefaultGraphCell> children=((FunctionBoxGraphCell)object).getChildren();
	int disDif=0;
	for (DefaultGraphCell cell: children)
	{
		if (cell instanceof FunctionBoxGraphPort)
		{
			FunctionBoxGraphPort fPort=(FunctionBoxGraphPort)cell;
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
