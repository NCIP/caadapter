/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/functions/FunctionLibraryPane.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
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

package gov.nih.nci.caadapter.ui.common.functions;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.function.FunctionManager;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.ui.common.nodeloader.FunctionMetaNodeLoader;
import gov.nih.nci.caadapter.ui.common.tree.TreeDefaultDragTransferHandler;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.dnd.DnDConstants;

/**
 * This class displays a scrollable panel listing functions available in FunctionMetaImpl and organizes by Group name.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
 */
public class FunctionLibraryPane extends JPanel// implements TreeSelectionListener
{
	private JTree tree;
	/**
	 * Creates a Function Library tree and adds it to a panel for display.
	 *
	 * @@param lstFunction a FunctionMetaIml object
	 */
	public FunctionLibraryPane()
	{
		initialize();
	}

	public JTree getFunctionTree()
	{
		return tree;
	}

	private void initialize()
	{
		//set the default layout
        this.setLayout(new BorderLayout());
		//Create the nodes.
		FunctionMetaNodeLoader nodeLoader = new FunctionMetaNodeLoader();
		try
		{
			DefaultMutableTreeNode kindNode = (DefaultMutableTreeNode) nodeLoader.loadData(FunctionManager.getInstance());//new DefaultMutableTreeNode(Config.FUNCTION_DEFINITION_DEFAULT_KIND);

			//Create a tree that allows one selection at a time.
			tree = new JTree(kindNode);
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			new TreeDefaultDragTransferHandler(tree, DnDConstants.ACTION_LINK);

			//Create the scroll pane and add the tree to it.
			JScrollPane treeView = new JScrollPane(tree);
			//Add treeView to this panel.
			this.add(treeView, BorderLayout.CENTER);
		}
		catch(Throwable e)
		{
			Log.logException(getClass(), e);
		}
	}


	/**
	 * Return the user selection of a give function. If nothing is selected, will return null.
	 * @return
	 */
	public FunctionMeta getFunctionSelection()
	{
		FunctionMeta result = null;
		TreePath treePath = tree.getSelectionPath();
		if(treePath!=null)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
			result = (FunctionMeta) node.getUserObject();
		}
		return result;
	}

	/*** FOLLOWING ARE FOR TEST ONLY WRITTEN BY Jayfus ******************************************************/
	//Optionally set the look and feel.
	private static boolean useSystemLookAndFeel = false;

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI()
	{
		if (useSystemLookAndFeel)
		{
			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch (Exception e)
			{
				System.err.println("Couldn't use system look and feel.");
			}
		}

		//Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		//Create and set up the window.
		JFrame frame = new JFrame("Function Window Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		FunctionLibraryPane newContentPane = new FunctionLibraryPane();
		newContentPane.setOpaque(true); //content panes must be opaque
		frame.setContentPane(newContentPane);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args)
	{
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				createAndShowGUI();
			}
		});
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.15  2006/10/03 14:45:08  jayannah
 * HISTORY      : Changed the package name to point to
 * HISTORY      :             "gov.nih.nci.caadapter.ui.main.functions"
 * HISTORY      :     from "gov.nih.nci.caadapter.ui.main.jgraph"
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/06/13 18:12:12  jiangsc
 * HISTORY      : Upgraded to catch Throwable instead of Exception.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/14 21:37:19  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/07/27 22:41:15  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/07/22 20:53:15  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
