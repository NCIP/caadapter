/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.mapping.sdtm.actions;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.sdtm.SDTMMappingGenerator;
import gov.nih.nci.caadapter.sdtm.SDTMMetadata;
import gov.nih.nci.caadapter.sdtm.meta.QBTableMetaData;
import gov.nih.nci.caadapter.ui.common.MappableNode;
import gov.nih.nci.caadapter.ui.common.TransferableNode;
import gov.nih.nci.caadapter.ui.common.jgraph.MappingDataManager;
import gov.nih.nci.caadapter.ui.common.jgraph.UIHelper;
import gov.nih.nci.caadapter.ui.common.tree.DefaultSourceTreeNode;
import gov.nih.nci.caadapter.ui.common.tree.TreeDefaultDropTransferHandler;
import gov.nih.nci.caadapter.ui.mapping.mms.actions.O2DBDropTargetAdapter;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.util.ArrayList;

/**
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.2 revision $Revision: 1.8 $
 * @date       $Date: 2008-09-29 21:22:50 $
 */
public class SdtmDropTransferHandler extends TreeDefaultDropTransferHandler
{

    private MappingDataManager mappingDataMananger;

    protected O2DBDropTargetAdapter dropTargetAdapter;

    private SDTMMappingGenerator sdtmMappingGenerator;

    public SdtmDropTransferHandler(JTree tree, MappingDataManager mappingDataMananger)
    {
        this(tree, mappingDataMananger, DnDConstants.ACTION_MOVE, null);
    }

    public SdtmDropTransferHandler(JTree tree, MappingDataManager mappingDataMananger, int action, SDTMMappingGenerator _sdtmMappingGenerator)
    {
        super(tree, action);
        this.mappingDataMananger = mappingDataMananger;
        this.sdtmMappingGenerator = _sdtmMappingGenerator;
    }

    /**
     * set up the drag and drop listeners. This must be called after the constructor.
     */
    protected void initDragAndDrop()
    {
        TreeCellRenderer cellRenderer = this.getTree().getCellRenderer();
        if (cellRenderer instanceof DefaultTreeCellRenderer)
        {
            DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) cellRenderer;
            this.plafSelectionColor = renderer.getBackgroundSelectionColor();
        } else
        {
            this.plafSelectionColor = Color.blue;
        }
        // set up drop stuff
        this.dropTargetAdapter = new O2DBDropTargetAdapter(this, acceptableDropAction, acceptableDropFlavors, preferredLocalFlavors);
        // component, ops, listener, accepting
        this.dropTarget = new DropTarget(this.getTree(), acceptableDropAction, this.dropTargetAdapter, true);
        this.dropTarget.setActive(true);
    }

    /**
     * Called by the DropTargetAdapter in dragEnter, dragOver and dragActionChanged
     */
    public void dragUnderFeedback(boolean ok, DropTargetDragEvent e)
    {
        TreeCellRenderer cellRenderer = this.getTree().getCellRenderer();
        if (cellRenderer instanceof DefaultTreeCellRenderer)
        {
            DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) cellRenderer;
            if (ok)
            {
                renderer.setBackgroundSelectionColor(this.plafSelectionColor);
                this.drawFeedback = true;
            } else
            {
                renderer.setBackgroundSelectionColor(Color.red);
            }
        }
        // comments out so that when drag over a folder it will not expand
        Point p = e.getLocation();
        TreePath path = this.getTree().getPathForLocation(p.x, p.y);
        if (path != null)
        {
            this.getTree().setSelectionPath(path);
            // if(this.getTree().isExpanded(path) == false)
            // this.getTree().expandPath(path);
        }
    }

    /**
     * Called by the DropTargetAdapter in dragEnter, dragOver and dragActionChanged. Current implementation only accept DefaultSourceTreeNode as the possible
     * transferable data. In future, if on the manipulation of Target Tree itself, please sub this class.
     */
    public boolean isDropOk(DropTargetDragEvent e)
    {
        Point p = e.getLocation();
        TransferableNode transferableNode = obtainTransferableNode(e);
        if (transferableNode == null)
        {
            return false;
        }
        TreePath path = this.getTree().getPathForLocation(p.x, p.y);
        if (path == null)
        {
            return false;
        }
        DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (targetNode instanceof MappableNode)
        {
            // only allows node that is not being mapped, that is, target node could only be mapped once.
            MappableNode mappableNode = (MappableNode) targetNode;
            if (mappableNode.isMapped())
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Called by the DropTargetAdapter in dragExit and drop
     */
    public void undoDragUnderFeedback()
    {
        this.getTree().clearSelection();
        TreeCellRenderer cellRenderer = this.getTree().getCellRenderer();
        if (cellRenderer instanceof DefaultTreeCellRenderer)
        {
            DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) cellRenderer;
            renderer.setBackgroundSelectionColor(this.plafSelectionColor);
        }
        this.drawFeedback = false;
    }

    /**
     * Called by the DropTargetAdapter in drop return true if add action succeeded otherwise return false
     */
    public boolean setDropData(Object transferredData, DropTargetDropEvent e, DataFlavor chosen)
    {

        boolean isSuccess = false;
        StringBuffer _sourceDataAsXPath;
        Point p = e.getLocation();
        // getTree().get
        TreePath path = this.getTree().getPathForLocation(p.x, p.y);
        if (path == null)
        {
            path = this.getTree().getClosestPathForLocation(p.x, p.y);
            if (path == null)
            {
                return false;
            }
        }
        // Object[] _obj = path.getPath();
        // for (int a=0; a<_obj.length ; a++){
        // _targetDataAsXpath.append("\\"+_obj[a].toString() );
        // }
        // System.out.println( " The value of the target is "+_targetDataAsXpath.toString());
        DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        SDTMMetadata _sdtmMetadata = (SDTMMetadata) targetNode.getUserObject();
        // Do a check if the chosen target is DM or not
//        if (!targetNode.getParent().toString().equalsIgnoreCase("DM"))
//        {
//            JOptionPane.showMessageDialog(getTree().getRootPane().getParent(), "The selected target does not belong to DM (Demographics) domain.", "Mapping Error", JOptionPane.ERROR_MESSAGE);
//            return false;
//        }
        String _targetDataAsXpath = _sdtmMetadata.getXPath();
        try
        {
            TransferableNode dragSourceObjectSelection = (TransferableNode) transferredData;
            java.util.List dragSourceObjectList = dragSourceObjectSelection.getSelectionList();
            DefaultSourceTreeNode _tn0 = (DefaultSourceTreeNode) dragSourceObjectList.get(0);
            TreeNode t;
            t = _tn0.getParent();
            _sourceDataAsXPath = new StringBuffer();
            ArrayList<String> _tmp = new ArrayList<String>();
            // System.out.println( " The value is "+t.toString());
            _tmp.add(t.toString());
            do
            {
                try
                {
                    t = t.getParent();
                    _tmp.add(t.toString());
                } catch (Exception ee)
                {
                    break;
                }
                // System.out.println( " The value is "+t.toString());
            } while (true);
            // System.out.println( " The value is "+_sb.toString());
            // harsha perform the LIFO operation
            for (int l = 1; l < _tmp.size() + 1; l++)
            {
                try
                {
                    int sizeNow = _tmp.size() - l;
                    _sourceDataAsXPath.append("\\" + _tmp.get(sizeNow).trim());
                } catch (Exception ed)
                {
                    // ed.printStackTrace();
                }
            }
            String _tmpStr = dragSourceObjectSelection.toString().replace("[", "");
            _tmpStr = _tmpStr.replace("]", "");
            _sourceDataAsXPath.append("\\" + _tmpStr);
            // end
            // System.out.println ( "the value is "+ _sourceDataAsXPath.toString());
            if (dragSourceObjectList == null || dragSourceObjectList.size() < 1)
            {
                return false;
            }
            if (isDataContainsTargetClassObject(dragSourceObjectSelection, DefaultGraphCell.class))
            {
                return processCellsDrop(dragSourceObjectSelection, (MappableNode) targetNode);
            }
            int size = dragSourceObjectList.size();
            for (int i = 0; i < size; i++)
            {
                DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) dragSourceObjectList.get(i);
                if (targetNode instanceof MappableNode && ((MappableNode) targetNode).isMapped())
                {// the
                    // target
                    // has a map
                    // already.
                    JOptionPane.showMessageDialog(getTree().getRootPane().getParent(), "The target you selected already has a map.", "Mapping Error", JOptionPane.ERROR_MESSAGE);
                } else
                {// we have a valid map, so go to map it!
                    if (sourceNode instanceof MappableNode && targetNode instanceof MappableNode)
                    {
                        // SDKMetaData sourceSDKMetaData = (SDKMetaData)sourceNode.getUserObject();
                        // SDKMetaData targetSDKMetaData = (SDKMetaData)targetNode.getUserObject();
                        // isSuccess = cumulativeMappingGenerator.map(sourceSDKMetaData.getXPath(), targetSDKMetaData.getXPath());
                        if (sourceNode.getUserObject() instanceof QBTableMetaData){
                            JOptionPane.showMessageDialog(getTree().getRootPane().getParent(), "The selected source cannot be mapped because it is a table, please choose columns to map.", "Mapping Error", JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                        isSuccess = mappingDataMananger.createMapping((MappableNode) sourceNode, (MappableNode) targetNode);
                        //System.out.println("SdtmDropTransferHandler ------ " + mappingDataMananger.hashCode());
                        if (isSuccess)
                        {
                            sdtmMappingGenerator.put(_sourceDataAsXPath.toString(), _targetDataAsXpath.toString());

                        }
                        // isSuccess = isSuccess &&
                        // mappingDataMananger.createMapping((MappableNode)sourceNode,(MappableNode)targetNode);
                    } else
                    {
                        JOptionPane.showMessageDialog(getTree().getRootPane().getParent(), "The target or source you selected is not right data type.", "Mapping Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                if (isSuccess)
                {
                }
            }// end of for
        } catch (Exception exp)
        {
            Log.logException(this, exp);
            isSuccess = false;
        } finally
        {
            return isSuccess;
        }
    }

    private boolean processCellsDrop(TransferableNode dragSourceObjectSelection, MappableNode targetNode)
    {
        boolean isSuccess = false;
        // collect the list of output ports of the function to ask for user
        // selection
        ArrayList functionOutputPortList = new ArrayList();
        java.util.List dragSourceObjectList = dragSourceObjectSelection.getSelectionList();
        int size = dragSourceObjectList.size();
        for (int i = 0; i < size; i++)
        {
            Object obj = dragSourceObjectList.get(i);
            if ((obj instanceof DefaultPort) && UIHelper.isPortTypeMatch((DefaultPort) obj, false) && !UIHelper.isPortMapped((DefaultPort) obj))
            {// the
                // list only contains non-mapped port
                functionOutputPortList.add(obj);
            }
        }
        if (functionOutputPortList.size() == 1)
        {// no need to ask users to
            // select.
            this.mappingDataMananger.createMapping((MappableNode) functionOutputPortList.get(0), targetNode);
        } else if (functionOutputPortList.size() > 1)
        {
            Object choice = JOptionPane.showInputDialog(getParentComponent(), "Select one output paramater of the function to be mapped.", "Select Function Output Parameter", JOptionPane.QUESTION_MESSAGE, null, functionOutputPortList.toArray(), functionOutputPortList.get(0));
            if (choice != null)
            {
                this.mappingDataMananger.createMapping((MappableNode) choice, targetNode);
            } else
            {
                JOptionPane.showMessageDialog(getParentComponent(), "User cancelled this mapping action.");
            }
        } else
        {
            JOptionPane.showMessageDialog(getParentComponent(), "The specified function does not have any available output parameter to be mapped to.");
        }
        return isSuccess;
    }

    private Component getParentComponent()
    {
        JRootPane rootPane = getTree().getRootPane();
        Component parentComponent = null;
        if (rootPane != null)
        {
            parentComponent = rootPane.getParent();
        }
        return parentComponent;
    }
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
