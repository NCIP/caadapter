/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.mapping.GME.actions;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.SDKMetaData;
import gov.nih.nci.caadapter.common.map.BaseComponent;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;

import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.validation.MapLinkValidator;
import gov.nih.nci.caadapter.hl7.map.Mapping;
import gov.nih.nci.caadapter.hl7.map.Map;
import gov.nih.nci.caadapter.hl7.map.impl.BaseMapElementImpl;
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
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;



/**
 * This class handles drop-related data manipulation for target tree on the mapping panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.9 $
 *          date        $Date: 2008-09-24 18:03:54 $
 */
public class XsdToXmiTargetTreeDropTransferHandler extends TreeDefaultDropTransferHandler
{
	private MappingDataManager mappingDataMananger;
//	protected O2DBDropTargetAdapter dropTargetAdapter;

    public XsdToXmiTargetTreeDropTransferHandler(JTree tree, MappingDataManager mappingDataMananger)
	{
		this(tree, mappingDataMananger, DnDConstants.ACTION_MOVE);
	}

	public XsdToXmiTargetTreeDropTransferHandler(JTree tree, MappingDataManager mappingDataMananger, int action)
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
        DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) transferableNode.getSelectionList().get(0);
        //System.out.println("targetNode class " + targetNode.getUserObject().getClass().toString() );
        if( sourceNode instanceof MappableNode )
        {
            MappableNode mappableNode = (MappableNode) sourceNode;
            if( mappableNode.isMapped() )
			{
					return false;
			}
        }

        if (targetNode.getUserObject() instanceof String )
        {
            return false;
        }

        if( targetNode instanceof MappableNode )
		{
			MappableNode mappableNode = (MappableNode) targetNode;

			if( mappableNode.isMapped() )
			{
					return false;
			}


//            System.out.println("[ sourceNode: " + sourceNode.toString() + " <-> " + "targetNode: " + targetNode.toString() + " ]");
//            System.out.println("[ sourceNode: " + sourceNode.getClass().toString() + " <-> " + "targetNode: " + targetNode.getClass().toString() + " ]");
            //only CSVField is allowed to map with a target node
//			if (!(sourceNode.getUserObject() instanceof CSVFieldMeta))
//				return false;

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
/////////////// Changes for Bug id 14800 by Sandeep Phadke on 7/11/2008
		TransferableNode dragSourceObj = (TransferableNode) transferredData;
		List dragSourceObjList = dragSourceObj.getSelectionList();
		DefaultMutableTreeNode sourceTreeNode = (DefaultMutableTreeNode) dragSourceObjList.get(0);
		String sourceXPath = ((SDKMetaData) sourceTreeNode.getUserObject()).getXPath();
		String sourceParentXPath = sourceXPath.substring( 0,sourceXPath.lastIndexOf(".") );
	    Mapping mData = mappingDataMananger.retrieveMappingData(true);
        List<Map> maps = mData.getMaps();

        String targetParentXPath = null;
    	String targetXPath = ((SDKMetaData) targetNode.getUserObject()).getXPath();
    	targetParentXPath = targetXPath.substring( 0,targetXPath.lastIndexOf(".") );
    	LinkedHashMap currentMappings = new LinkedHashMap();

        for (int j=0; j < maps.size(); j++ )
        {
            Map tempMap = maps.get(j);
            String xpath = tempMap.getSourceMapElement().getMetaObject().getXmlPath();
            String mappedSourcePath = xpath.substring( 0,xpath.lastIndexOf(".") );
            xpath = tempMap.getTargetMapElement().getMetaObject().getXmlPath();
            String mappedTargetPath = xpath.substring( 0,xpath.lastIndexOf(".") );
            //loop thru the existing mappings and populate the linkedhashmap.
            if (!currentMappings.containsKey(mappedSourcePath) && (!currentMappings.containsValue(mappedTargetPath)) )
            	currentMappings.put(mappedSourcePath, mappedTargetPath);
        }
        // populate the linkedhashmap with new mapping source/target, if you are trying to map
        // new element whose target has already been mapped earlier this put will fail here,
        // thus that entry would not be there in the map and will be caught and message will be displayed.
        if (!currentMappings.containsKey(sourceParentXPath) && (!currentMappings.containsValue(targetParentXPath)) )
        	currentMappings.put(sourceParentXPath, targetParentXPath);

        // check if new mapping exists in the linkedhashmap, if mapping is valid it should
        // contain in the linkedhashmap else display the message.
        if (currentMappings.containsKey(sourceParentXPath) ){
        	String targetPath = (String) currentMappings.get(sourceParentXPath);


        	if (!targetPath.equals(targetParentXPath)){
        		JOptionPane.showMessageDialog(getTree().getRootPane().getParent(),
				"You can not do a cross reference mapping for GME objects.",
				"Mapping Error",
				JOptionPane.ERROR_MESSAGE);
        		return false;
        	}

        } else {
    		JOptionPane.showMessageDialog(getTree().getRootPane().getParent(),
    				"You can not do a cross reference mapping for GME objects.",
    				"Mapping Error",
    				JOptionPane.ERROR_MESSAGE);
            		return false;

        }
//////////////	End change for bug id 14800

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
                        System.out.println("[ Creating Mapping: sourceNode: " + sourceNode.toString() + " <-> " + "targetNode: " + targetNode.toString() + " ]");
                        isSuccess = mappingDataMananger.createMapping((MappableNode)sourceNode, (MappableNode)targetNode);

                        if ( isSuccess )
                        {
                            System.out.println("[ Current Mappings ]" );
// commented as this has been moved up for bug id 14800
//                            Mapping mData = mappingDataMananger.retrieveMappingData(true);
//
//                            List<Map> maps = mData.getMaps();

                            for (int j=0; j < maps.size(); j++ )
                            {
                                Map tempMap = maps.get(j);
                                System.out.println(tempMap.getClass().toString());
                                System.out.println("[ source: " + tempMap.getSourceMapElement().getMetaObject().getName() + " <-> " + " target: " + tempMap.getTargetMapElement().getMetaObject().getName() + " ]");
                            }
                        }
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
*/
