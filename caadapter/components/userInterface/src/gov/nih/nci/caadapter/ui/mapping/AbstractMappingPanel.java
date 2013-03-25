/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.ui.mapping;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.MappingFileSynchronizer;
import gov.nih.nci.caadapter.ui.common.actions.TreeCollapseAllAction;
import gov.nih.nci.caadapter.ui.common.actions.TreeExpandAllAction;
import gov.nih.nci.caadapter.ui.common.context.DefaultContextManagerClientPanel;
import gov.nih.nci.caadapter.ui.common.functions.FunctionLibraryPane;
import gov.nih.nci.caadapter.ui.common.jgraph.MappingDataManager;
import gov.nih.nci.caadapter.ui.common.properties.DefaultPropertiesPage;
import gov.nih.nci.caadapter.ui.common.tree.MappingSourceTree;
import gov.nih.nci.caadapter.ui.common.tree.MappingTargetTree;
import gov.nih.nci.caadapter.ui.common.tree.TreeDefaultDragTransferHandler;
import gov.nih.nci.caadapter.ui.common.tree.TreeDefaultDropTransferHandler;
import gov.nih.nci.caadapter.ui.common.tree.TargetTreeDragTransferHandler;

import java.awt.Dimension;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;

public abstract class AbstractMappingPanel extends DefaultContextManagerClientPanel  implements ActionListener
{
	protected FunctionLibraryPane functionPane;
	protected DefaultPropertiesPage propertiesPane;
	protected MappingTreeScrollPane sourceScrollPane = new MappingTreeScrollPane(MappingTreeScrollPane.DRAW_NODE_TO_RIGHT);
	protected MappingTreeScrollPane targetScrollPane = new MappingTreeScrollPane(MappingTreeScrollPane.DRAW_NODE_TO_LEFT);

	protected JTextField sourceLocationArea = new JTextField();
	protected JTextField targetLocationArea = new JTextField();
	protected MappingMiddlePanel middlePanel = null;
	protected MappingSourceTree sTree = null;
	protected MappingTargetTree tTree = null;

	protected TreeCollapseAllAction sourceTreeCollapseAllAction;
	protected TreeExpandAllAction sourceTreeExpandAllAction;

	protected TreeCollapseAllAction targetTreeCollapseAllAction;
	protected TreeExpandAllAction targetTreeExpandAllAction;

	protected MappingFileSynchronizer fileSynchronizer;

	protected TreeDefaultDragTransferHandler sourceTreeDragTransferHandler = null;
	protected abstract TreeDefaultDropTransferHandler getTargetTreeDropTransferHandler();

	protected JPanel sourceButtonPanel = null;
	protected JPanel sourceLocationPanel = null;
	protected JPanel targetButtonPanel = null;
	protected JPanel targetLocationPanel = null;

	protected File mappingSourceFile = null;
	protected File mappingTargetFile = null;
	protected static Log logger =new Log();
	// ??? Not Sure the behavior difference of build target tree and source tree besides we have MappingSourceTree TargerTree -Eric
	protected void buildTargetTree(Object metaInfo, File absoluteFile, boolean isToResetGraph) throws Exception
	{
		TreeNode nodes=loadTargetTreeData(metaInfo,absoluteFile);
		//Build the target tree
		tTree = new MappingTargetTree(this.getMiddlePanel(), nodes);
		tTree.getSelectionModel().addTreeSelectionListener((TreeSelectionListener) (getMappingDataManager().getPropertiesSwitchController()));
		targetScrollPane.setViewportView(tTree);
		tTree.expandAll();

//		TargetTreeDragTransferHandler targetTreeDragTransferHandler = null;
//		drag source for DnD to middle panel.
		TargetTreeDragTransferHandler targetTreeDragTransferHandler = new TargetTreeDragTransferHandler(tTree, DnDConstants.ACTION_LINK);

		//register collapse all and expand all actions.
		targetTreeCollapseAllAction.setTree(tTree);
		targetTreeExpandAllAction.setTree(tTree);
		tTree.getInputMap().put(targetTreeCollapseAllAction.getAcceleratorKey(), targetTreeCollapseAllAction.getName());
		tTree.getActionMap().put(targetTreeCollapseAllAction.getName(), targetTreeCollapseAllAction);
		tTree.getInputMap().put(targetTreeExpandAllAction.getAcceleratorKey(), targetTreeExpandAllAction.getName());
		tTree.getActionMap().put(targetTreeExpandAllAction.getName(), targetTreeExpandAllAction);

		if (sTree != null && isToResetGraph)
		{
			resetMiddlePanel();
		}
		if (absoluteFile != null)
		{
			String absoluteFilePath = absoluteFile.getAbsolutePath();
			targetLocationArea.setText(absoluteFilePath);
			targetLocationArea.setToolTipText(absoluteFilePath);
			mappingTargetFile = absoluteFile;//new File(absoluteFilePath);
		}
		else
		{
			mappingTargetFile = null;
		}
		if (this.getRootPane() != null)
		{
			this.getRootPane().repaint();
		}
		getMappingFileSynchronizer().registerFile(MappingFileSynchronizer.FILE_TYPE.Target_File, absoluteFile);
	}

	protected void buildSourceTree(Object metaInfo, File absoluteFile, boolean isToResetGraph) throws Exception
	{
		TreeNode nodes=loadSourceTreeData(metaInfo,absoluteFile);

		//Build the source tree
		sTree = new MappingSourceTree(middlePanel, nodes);
		sTree.getSelectionModel().addTreeSelectionListener((TreeSelectionListener) (getMappingDataManager().getPropertiesSwitchController()));
		sourceTreeDragTransferHandler = new TreeDefaultDragTransferHandler(sTree, DnDConstants.ACTION_LINK);
		sourceScrollPane.setViewportView(sTree);
		sTree.expandAll();

		//register collapse all and expand all actions.
		sourceTreeCollapseAllAction.setTree(sTree);
		sourceTreeExpandAllAction.setTree(sTree);
		sTree.getInputMap().put(sourceTreeCollapseAllAction.getAcceleratorKey(), sourceTreeCollapseAllAction.getName());
		sTree.getActionMap().put(sourceTreeCollapseAllAction.getName(), sourceTreeCollapseAllAction);
		sTree.getInputMap().put(sourceTreeExpandAllAction.getAcceleratorKey(), sourceTreeExpandAllAction.getName());
		sTree.getActionMap().put(sourceTreeExpandAllAction.getName(), sourceTreeExpandAllAction);

		if (tTree != null && isToResetGraph)
		{
			resetMiddlePanel();
		}
		if (absoluteFile != null)
		{
			String absoluteFilePath = absoluteFile.getAbsolutePath();
			sourceLocationArea.setText(absoluteFilePath);
			sourceLocationArea.setToolTipText(absoluteFilePath);
			mappingSourceFile = absoluteFile;//new File(absoluteFilePath);
		}
		else
		{
			mappingSourceFile = null;
		}
		if (this.getRootPane() != null)
		{
			this.getRootPane().repaint();
		}
		getMappingFileSynchronizer().registerFile(MappingFileSynchronizer.FILE_TYPE.Source_File, absoluteFile);
    }

	protected void resetMiddlePanel()
	{
		if (middlePanel != null)
		{
			middlePanel.resetGraph();
			middlePanel.repaint();
		}
	}

	protected JComponent getCenterPanel(boolean functionPaneRequired)
		{//construct the top level layout of mapping panel
			/**
			 * GUI Layout:
			 * JSplitPane - Horizontal:
			 *      left: JSplitPane - Horizontal:
			 *				left: source panel;
			 *				right: JSplitPane - Horizontal:
			 *							left: middle panel for graph;
			 *							right: target panel;
			 * 		right: JSplitPane - Vertical:
			 * 				top: functional pane;
			 *				bottom: properties panel;
			 */

			JSplitPane leftRightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			leftRightSplitPane.setOneTouchExpandable(false);
			DefaultSettings.setDefaultFeatureForJSplitPane(leftRightSplitPane);
			leftRightSplitPane.setDividerLocation(0.85);
			leftRightSplitPane.setLeftComponent(getTopLevelLeftPanel());
			leftRightSplitPane.setRightComponent(getTopLevelRightPanel(functionPaneRequired));
			return leftRightSplitPane;
		}

		/**
		 * This constructs function and properties panels.
		 *
		 * @return the top level right pane.
		 */
		private JComponent getTopLevelRightPanel(boolean functionPaneRequired)
		{
			JSplitPane topBottomSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			DefaultSettings.setDefaultFeatureForJSplitPane(topBottomSplitPane);
			topBottomSplitPane.setDividerLocation(0.5);

			functionPane = new FunctionLibraryPane();
			functionPane.setBorder(BorderFactory.createTitledBorder("Functions"));
			if(functionPaneRequired)
			{
				topBottomSplitPane.setTopComponent(functionPane);
			}
			propertiesPane = new DefaultPropertiesPage(this.getMappingDataManager().getPropertiesSwitchController());
			topBottomSplitPane.setBottomComponent(propertiesPane);

			double topCenterFactor = 0.3;
			Dimension rightMostDim = new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 11), (int) (Config.FRAME_DEFAULT_HEIGHT * topCenterFactor));
			propertiesPane.setPreferredSize(rightMostDim);
			functionPane.setPreferredSize(rightMostDim);
			functionPane.getFunctionTree().getSelectionModel().addTreeSelectionListener((TreeSelectionListener) (getMappingDataManager().getPropertiesSwitchController()));

			topCenterFactor = 1.5;
			rightMostDim = new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 10), (int) (Config.FRAME_DEFAULT_HEIGHT / topCenterFactor));
			topBottomSplitPane.setSize(rightMostDim);

			return topBottomSplitPane;
		}
		/**
		 * The top level left pane is built differently: This method should be over ridden by sub-classes
		 * @return
		 */
		protected abstract JPanel getTopLevelLeftPanel();
		protected abstract TreeNode loadSourceTreeData( Object metaInfo, File absoluteFile)throws Exception;

		protected abstract TreeNode loadTargetTreeData( Object metaInfo, File absoluteFile)throws Exception;
		/**
	 * Return whether the mapping module is in drag-and-drop mode.
	 * @return whether the mapping module is in drag-and-drop mode.
	 */
	public boolean isInDragDropMode()
	{
        boolean checkSourceTreeDragTransferHandler = false;
        boolean checkTargetTreeDropTransferHandler = false;
        boolean checkMiddlePanel = false;
        checkSourceTreeDragTransferHandler = sourceTreeDragTransferHandler.isInDragDropMode();
        checkTargetTreeDropTransferHandler = getTargetTreeDropTransferHandler().isInDragDropMode();
        checkMiddlePanel = middlePanel.getMiddlePanelDropTransferHandler().isInDragDropMode();
        return (checkSourceTreeDragTransferHandler ||
				checkTargetTreeDropTransferHandler ||
				checkMiddlePanel);
	}

	/**
	 * Set a new value for the mode.
	 * @param newValue
	 */
	public void setInDragDropMode(boolean newValue)
	{
        if (sourceTreeDragTransferHandler == null)
        {
            JOptionPane.showMessageDialog(this, "You should input the source file name first.", "No Source file", JOptionPane.ERROR_MESSAGE);
            return;
        }
        else
        	sourceTreeDragTransferHandler.setInDragDropMode(newValue);

        if (getTargetTreeDropTransferHandler() == null)
        {
            JOptionPane.showMessageDialog(this, "You should input the target file name first.", "No Target file", JOptionPane.ERROR_MESSAGE);
            return;
        }
        else
        	getTargetTreeDropTransferHandler().setInDragDropMode(newValue);
		middlePanel.getMiddlePanelDropTransferHandler().setInDragDropMode(newValue);
	}

	/**
	 * Set a new save file.
	 *
	 * @param saveFile
	 * @return true if the value is changed, false otherwise.
	 */
	public boolean setSaveFile(File saveFile)
	{
		boolean result = super.setSaveFile(saveFile);
		if(result)
		{
			getMappingFileSynchronizer().registerFile(MappingFileSynchronizer.FILE_TYPE.Mapping_File, saveFile);
		}
		return result;
	}

	public MappingFileSynchronizer getMappingFileSynchronizer()
	{
		return this.fileSynchronizer;
	}

	public void synchronizeRegisteredFile(boolean notigyOberver)
	{
		getMappingFileSynchronizer().doSynchronizationCheck(notigyOberver);
	}

	public JScrollPane getSourceScrollPane() {
		return sourceScrollPane;
	}

	public JScrollPane getTargetScrollPane() {
		return targetScrollPane;
	}

	public MappingMiddlePanel getMiddlePanel() {
		return middlePanel;
	}

	/**
     * Return the mapping data manager.
     *
     * @return the mapping data manager.
     */
	public MappingDataManager getMappingDataManager() {
		return middlePanel.getMappingDataManager();
	}

	public JTree getSourceTree()
	{
		return sTree;
	}

	public JTree getTargetTree()
	{
		return tTree;
	}
	/**
	 * Return if the source tree has been populated.
	 * @return if the source tree has been populated.
	 */
	public boolean isSourceTreePopulated()
	{
		return sTree!=null;
	}

	/**
	 * Return if the target tree has been populated.
	 * @return if the target tree has been populated.
	 */
	public boolean isTargetTreePopulated()
	{
		return tTree!=null;
	}

	public FunctionLibraryPane getFunctionPane()
	{
		return functionPane;
	}

	/**
	 * Provide the extended implementation of this method by adding additional files of source and target;
	 *
	 * @return a list of file objects that this context is associated with.
	 */
	public java.util.List<File> getAssociatedFileList()
	{
		java.util.List<File> resultList = super.getAssociatedFileList();
		if (mappingSourceFile != null)
		{
			resultList.add(mappingSourceFile);
		}
		if (mappingTargetFile != null)
		{
			resultList.add(mappingTargetFile);
		}
		return resultList;
	}

	public void setSize(Dimension newDimension)
	{
		setSize((int) newDimension.getWidth(), (int) newDimension.getHeight());
	}

	public void setSize(int width, int height)
	{
		this.resize(width, height);
	}

	public void resize(int width, int height)
	{
		double topCenterFactor = 1;
		sourceLocationArea.setSize(new Dimension((int) (width / 6), 25));
		sourceScrollPane.setSize(new Dimension((int) (width / 4.5), (int) (height * topCenterFactor)));
		targetLocationArea.setSize(new Dimension(width / 6, 25));
		targetScrollPane.setSize(new Dimension((int) (width / 4.5), (int) (height * topCenterFactor)));
		middlePanel.setSize(new Dimension((int) (width / 4), (int) (height * topCenterFactor)));

		topCenterFactor = 0.5;
		Dimension rightMostDim = new Dimension((int) (width / 5), (int) (height * topCenterFactor));
		propertiesPane.setSize(rightMostDim);
		functionPane.setSize(rightMostDim);
	}

	/**
	 * Indicate whether or not it is changed.
	 */
	public boolean isChanged()
	{
		return middlePanel.getMappingDataManager().isGraphChanged();
	}

	/**
	 * Explicitly set the value.
	 *
	 * @param newValue
	 */
	public void setChanged(boolean newValue)
	{
		middlePanel.getMappingDataManager().setGraphChanged(newValue);
	}

	public abstract void reload() throws Exception;
	public abstract void reload(Map<MappingFileSynchronizer.FILE_TYPE, File> changedFileMap);

	public abstract void actionPerformed(ActionEvent e);
	@Override
	public abstract Action getDefaultOpenAction();

	@Override
	public abstract Map getMenuItems(String menu_name) ;

}
