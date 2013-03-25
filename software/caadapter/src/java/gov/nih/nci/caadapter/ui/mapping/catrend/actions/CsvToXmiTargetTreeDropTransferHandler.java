/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.ui.mapping.catrend.actions;

import gov.nih.nci.caadapter.common.Log;

import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.validation.MapLinkValidator;
import gov.nih.nci.caadapter.ui.common.MappableNode;
import gov.nih.nci.caadapter.ui.common.TransferableNode;
import gov.nih.nci.caadapter.ui.common.jgraph.MappingDataManager;
import gov.nih.nci.caadapter.ui.common.tree.TreeDefaultDropTransferHandler;

import javax.swing.JTree;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.Point;

import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.util.List;



/**
 * This class handles drop-related data manipulation for target tree on the mapping panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.6 $
 *          date        $Date: 2008-09-29 20:28:13 $
 */
public class CsvToXmiTargetTreeDropTransferHandler extends TreeDefaultDropTransferHandler
{
	private MappingDataManager mappingDataMananger;
//	protected O2DBDropTargetAdapter dropTargetAdapter;
	public CsvToXmiTargetTreeDropTransferHandler(JTree tree, MappingDataManager mappingDataMananger)
	{
		this(tree, mappingDataMananger, DnDConstants.ACTION_MOVE);
	}

	public CsvToXmiTargetTreeDropTransferHandler(JTree tree, MappingDataManager mappingDataMananger, int action)
	{
		super(tree, action);
		this.mappingDataMananger = mappingDataMananger;
	}

	/**
	 * Called by the DropTargetAdapter in dragEnter, dragOver and
	 * dragActionChanged.
	 * It is allowed to mapp multiple csvSource nodes to one Xmi target node
	 *
	 */
	public boolean isDropOk(DropTargetDragEvent e)
	{
		TransferableNode transferableNode = obtainTransferableNode(e);
		if(transferableNode==null)
				return false;

		Point p = e.getLocation();
		TreePath path = this.getTree().getPathForLocation(p.x, p.y);
		if (path==null)
			return false;
		DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) path.getLastPathComponent();
		if(targetNode instanceof MappableNode)
		{
			MappableNode mappableNode = (MappableNode) targetNode;

//			if(mappableNode.isMapped())
//			{
//				if (!(targetNode.getUserObject() instanceof TableMetadata)
//						&&!(targetNode.getUserObject() instanceof ColumnMetadata))
//				{
//					return false;
//				}
//			}

			DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) transferableNode.getSelectionList().get(0);
			//only CSVField is allowed to map with a target node
			if (!(sourceNode.getUserObject() instanceof CSVFieldMeta))
				return false;
			MapLinkValidator validator = new MapLinkValidator(sourceNode.getUserObject(), targetNode.getUserObject());
			ValidatorResults validatorResult = validator.validate();
//			Object targetUserObject = targetNode.getUserObject();
//			if(targetUserObject instanceof MetaObject)
//			{//further validate if the target object itself is mappable or not.
//				validatorResult.addValidatorResults(MapLinkValidator.isMetaObjectMappable((MetaObject) targetUserObject));
//			}
			return validatorResult.isValid();
		}
		return true;
	}

	/**
	 * Called by the DropTargetAdapter in drop
	 * return true if add action succeeded
	 * otherwise return false
	 */
	public boolean setDropData(Object transferredData, DropTargetDropEvent e, DataFlavor chosen)
	{
		boolean isSuccess = false;
		Point p = e.getLocation();
		TreePath path = this.getTree().getPathForLocation(p.x, p.y);
		if (path == null)
		{
			path = this.getTree().getClosestPathForLocation(p.x, p.y);
			if (path == null)
				return false;
		}
		DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) path.getLastPathComponent();
		try
		{
			TransferableNode dragSourceObjectSelection = (TransferableNode) transferredData;
			List dragSourceObjectList = dragSourceObjectSelection.getSelectionList();
			if (dragSourceObjectList == null || dragSourceObjectList.size() < 1)
			{
				return false;
			}

			int size = dragSourceObjectList.size();
			for (int i = 0; i < size; i++)
			{
				DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) dragSourceObjectList.get(i);
//				if (targetNode instanceof MappableNode && ((MappableNode) targetNode).isMapped())
				if (false)
				{//the target has a map already.
					JOptionPane.showMessageDialog(getTree().getRootPane().getParent(),
							"The target you selected already has a map.",
							"Mapping Error",
							JOptionPane.ERROR_MESSAGE);
				}
				else
				{// we have a valid map, so go to map it!

                        System.out.println("Creating Mapping");
                        isSuccess = mappingDataMananger.createMapping((MappableNode)sourceNode, (MappableNode)targetNode);
				}

//				if (isSuccess)
//				{
//
//					boolean isRoot = true;
//
//					UMLClass clazz = ((ObjectMetadata)sourceNode.getUserObject()).getUmlClass();
//
//					List<UMLGeneralization> clazzGs = clazz.getGeneralizations();
//
//	                for (UMLGeneralization clazzG : clazzGs) {
//	                    UMLClass parent = clazzG.getSupertype();
//	                    if (parent != clazz) {
//	                    	isRoot = false;
//	                        break;
//	                    }
//	                }
//	                if (!isRoot)
//	                	new AddDiscriminatorValue(new JFrame(),(ObjectMetadata)sourceNode.getUserObject());
//				}
			}//end of for
		}
		catch (Exception exp)
		{
			Log.logException(this, exp);
			isSuccess = false;
		}
		return isSuccess;
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
**/