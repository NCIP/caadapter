/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/mms/actions/MmsTargetTreeDropTransferHandler.java,v 1.3 2007-09-14 22:40:24 wuye Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.mapping.mms.actions;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.SDKMetaData;
import gov.nih.nci.caadapter.mms.generator.CumulativeMappingGenerator;
import gov.nih.nci.caadapter.mms.metadata.ColumnMetadata;
import gov.nih.nci.caadapter.mms.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.mms.metadata.TableMetadata;
import gov.nih.nci.caadapter.ui.common.MappableNode;
import gov.nih.nci.caadapter.ui.common.TransferableNode;
import gov.nih.nci.caadapter.ui.common.jgraph.MappingDataManager;
import gov.nih.nci.caadapter.ui.common.jgraph.UIHelper;
import gov.nih.nci.caadapter.ui.common.tree.TreeDefaultDropTransferHandler;
import gov.nih.nci.caadapter.ui.mapping.mms.AddDiscriminatorValue;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLGeneralization;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles drop-related data manipulation for target tree on the mapping panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wuye $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2007-09-14 22:40:24 $
 */
public class MmsTargetTreeDropTransferHandler extends TreeDefaultDropTransferHandler
{
	private MappingDataManager mappingDataMananger;
	protected O2DBDropTargetAdapter dropTargetAdapter;
	public MmsTargetTreeDropTransferHandler(JTree tree, MappingDataManager mappingDataMananger)
	{
		this(tree, mappingDataMananger, DnDConstants.ACTION_MOVE);
	}

	public MmsTargetTreeDropTransferHandler(JTree tree, MappingDataManager mappingDataMananger, int action)
	{
		super(tree, action);
		this.mappingDataMananger = mappingDataMananger;
	}

	/**
	 * set up the drag and drop listeners. This must be called
	 * after the constructor.
	 */
	protected void initDragAndDrop()
	{
		TreeCellRenderer cellRenderer = this.getTree().getCellRenderer();
		if (cellRenderer instanceof DefaultTreeCellRenderer)
		{
			DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) cellRenderer;
			this.plafSelectionColor = renderer.getBackgroundSelectionColor();
		}
		else
		{
			this.plafSelectionColor = Color.blue;
		}
		//set up drop stuff
		this.dropTargetAdapter = new O2DBDropTargetAdapter(this,
				acceptableDropAction,
				acceptableDropFlavors,
				preferredLocalFlavors);

		// component, ops, listener, accepting
		this.dropTarget = new DropTarget(this.getTree(),
				acceptableDropAction,
				this.dropTargetAdapter,
				true);
		this.dropTarget.setActive(true);
	}

	/**
	 * Called by the DropTargetAdapter in dragEnter, dragOver and
	 * dragActionChanged
	 */
	public void dragUnderFeedback(boolean ok, DropTargetDragEvent e)
	{
		TreeCellRenderer cellRenderer = this.getTree().getCellRenderer();
		if (cellRenderer instanceof DefaultTreeCellRenderer)
		{
			DefaultTreeCellRenderer renderer =
					(DefaultTreeCellRenderer) cellRenderer;
			if (ok)
			{
				renderer.setBackgroundSelectionColor(this.plafSelectionColor);
				this.drawFeedback = true;
			}
			else
			{
				renderer.setBackgroundSelectionColor(Color.red);
			}
		}

//comments out so that when drag over a folder it will not expand
		Point p = e.getLocation();
		TreePath path = this.getTree().getPathForLocation(p.x, p.y);
		if (path != null)
		{
			this.getTree().setSelectionPath(path);
//	        if(this.getTree().isExpanded(path) == false)
//		    this.getTree().expandPath(path);
		}
	}

	/**
	 * Called by the DropTargetAdapter in dragEnter, dragOver and
	 * dragActionChanged.
	 * Current implementation only accept DefaultSourceTreeNode as the possible transferable data.
	 * In future, if on the manipulation of Target Tree itself, please sub this class.
	 */
	public boolean isDropOk(DropTargetDragEvent e)
	{
		Point p = e.getLocation();
		TransferableNode transferableNode = obtainTransferableNode(e);
		if(transferableNode==null)
		{
			return false;
		}
		TreePath path = this.getTree().getPathForLocation(p.x, p.y);
		if (path == null)
		{
			return false;
		}
		DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) path.getLastPathComponent();

		if(targetNode instanceof MappableNode)
		{//only allows node that is not being mapped, that is, target node could only be mapped once.
			MappableNode mappableNode = (MappableNode) targetNode;
			if(mappableNode.isMapped())
			{
				if (targetNode.getUserObject() instanceof TableMetadata) 
				{
					TableMetadata tm = (TableMetadata)(targetNode.getUserObject());
					if (tm.hasDiscriminator()) return true;
					else return false;
				}
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
			{
				return false;
			}
		}
		DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) path.getLastPathComponent();
		try
		{
			TransferableNode dragSourceObjectSelection = (TransferableNode) transferredData;
			java.util.List dragSourceObjectList = dragSourceObjectSelection.getSelectionList();
			if (dragSourceObjectList == null || dragSourceObjectList.size() < 1)
			{
				return false;
			}

			if(isDataContainsTargetClassObject(dragSourceObjectSelection, DefaultGraphCell.class))
			{
				return processCellsDrop(dragSourceObjectSelection, (MappableNode) targetNode);
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
					if(sourceNode instanceof MappableNode && targetNode instanceof MappableNode)
					{
						CumulativeMappingGenerator cumulativeMappingGenerator = CumulativeMappingGenerator.getInstance();
						SDKMetaData sourceSDKMetaData = (SDKMetaData)sourceNode.getUserObject();
						SDKMetaData targetSDKMetaData = (SDKMetaData)targetNode.getUserObject();
						sourceSDKMetaData.setMapped(true);
						isSuccess = cumulativeMappingGenerator.map(sourceSDKMetaData.getXPath(), targetSDKMetaData.getXPath());
						if (!isSuccess) {
							sourceSDKMetaData.setMapped(false);
							JOptionPane.showMessageDialog(getTree().getRootPane().getParent(),
									cumulativeMappingGenerator.getErrorMessage(),
									"Mapping Error",
									JOptionPane.ERROR_MESSAGE);
						}
						isSuccess = isSuccess && mappingDataMananger.createMapping((MappableNode)sourceNode, (MappableNode)targetNode);
					}
					else
					{
						JOptionPane.showMessageDialog(getTree().getRootPane().getParent(),
								"The target or source you selected can not be mapped.",
								"Mapping Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}

				if (isSuccess)
				{

					boolean isRoot = true;
					
					UMLClass clazz = ((ObjectMetadata)sourceNode.getUserObject()).getUmlClass();
					
					List<UMLGeneralization> clazzGs = clazz.getGeneralizations();

	                for (UMLGeneralization clazzG : clazzGs) {
	                    UMLClass parent = clazzG.getSupertype();
	                    if (parent != clazz) {
	                    	isRoot = false;
	                        break;
	                    }
	                }
	                if (!isRoot)
	                	new AddDiscriminatorValue(new JFrame(),(ObjectMetadata)sourceNode.getUserObject());
				}
			}//end of for
		}
		catch (Exception exp)
		{
			Log.logException(this, exp);
			isSuccess = false;
		}
		finally
		{
			return isSuccess;
		}
	}

	private boolean processCellsDrop(TransferableNode dragSourceObjectSelection, MappableNode targetNode)
	{
		boolean isSuccess = false;
		//collect the list of output ports of the function to ask for user selection
		ArrayList functionOutputPortList = new ArrayList();
		java.util.List dragSourceObjectList = dragSourceObjectSelection.getSelectionList();
		int size = dragSourceObjectList.size();
		for(int i=0; i<size; i++)
		{
			Object obj = dragSourceObjectList.get(i);
			if((obj instanceof DefaultPort) && UIHelper.isPortTypeMatch((DefaultPort) obj, false)
					&& !UIHelper.isPortMapped((DefaultPort) obj))
			{//the list only contains non-mapped port
				functionOutputPortList.add(obj);
			}
		}

		if(functionOutputPortList.size()==1)
		{//no need to ask users to select.
			this.mappingDataMananger.createMapping((MappableNode) functionOutputPortList.get(0), targetNode);
		}
		else if(functionOutputPortList.size()>1)
		{
			Object choice = JOptionPane.showInputDialog(getParentComponent(),
                "Select one output paramater of the function to be mapped.",
                "Select Function Output Parameter",
                JOptionPane.QUESTION_MESSAGE, null,
                functionOutputPortList.toArray(),
                functionOutputPortList.get(0));
			if(choice!=null)
			{
				this.mappingDataMananger.createMapping((MappableNode) choice, targetNode);
			}
			else
			{
				JOptionPane.showMessageDialog(getParentComponent(), "User cancelled this mapping action.");
			}
		}
		else
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
 * HISTORY      : Revision 1.2  2007/09/14 15:06:13  wuye
 * HISTORY      : Added support for table per inheritence structure
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:57  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.5  2006/11/14 15:24:35  wuye
 * HISTORY      : Added validation funcationality
 * HISTORY      :
 * HISTORY      : Revision 1.4  2006/10/23 16:20:58  wuye
 * HISTORY      : Changed error message.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2006/10/20 21:32:05  wuye
 * HISTORY      : Added error message
 * HISTORY      :
 * HISTORY      : Revision 1.2  2006/10/10 17:09:10  wuye
 * HISTORY      : Allow multiple source to one target get drag - n - drop
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/09/26 15:48:10  wuye
 * HISTORY      : New handler for object - 2 - db mapping
 * HISTORY      :
 * HISTORY      : Revision 1.28  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.27  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.26  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.25  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.24  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.23  2005/12/23 16:37:59  jiangsc
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.21  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.20  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/11/23 19:48:52  jiangsc
 * HISTORY      : Enhancement on mapping validations.
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/11/03 22:39:35  jiangsc
 * HISTORY      : Enhance only target mappings.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/11/02 20:23:56  jiangsc
 * HISTORY      : Enhanced to select only not-mapped port
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/10/18 13:35:26  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/10/05 17:23:47  giordanm
 * HISTORY      : CSV validation work.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/09/16 23:18:56  chene
 * HISTORY      : Database prototype GUI support, but can not be loaded
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/09/08 19:37:03  chene
 * HISTORY      : Saved point
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/08/26 14:53:24  chene
 * HISTORY      : Add isValidated method into ValidatorResults
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/08/25 22:40:11  jiangsc
 * HISTORY      : Enhanced mapping validation.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/08/25 14:07:38  jiangsc
 * HISTORY      : Minor fix to display OptionPane nicer
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/24 22:28:42  jiangsc
 * HISTORY      : Enhanced JGraph implementation;
 * HISTORY      : Save point of CSV and HSM navigation update;
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/19 20:25:20  jiangsc
 * HISTORY      : Loose the restriction on mappable.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/04 18:06:26  jiangsc
 * HISTORY      : Updated class description in comments
 * HISTORY      :
 */
