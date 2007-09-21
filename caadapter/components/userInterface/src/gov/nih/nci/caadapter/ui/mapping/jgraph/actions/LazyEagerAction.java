/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/jgraph/actions/LazyEagerAction.java,v 1.7 2007-09-21 04:41:38 wuye Exp $
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


package gov.nih.nci.caadapter.ui.mapping.jgraph.actions;

import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.jgraph.MiddlePanelJGraphController;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.mms.metadata.ModelMetadata;

import org.jgraph.JGraph;

import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;

/**
 * This class defines the action to delete selected graphic cells.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wuye $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.7 $
 *          date        $Date: 2007-09-21 04:41:38 $
 */
public class LazyEagerAction extends AbstractContextAction {
	private static final String LOGID = "$RCSfile: LazyEagerAction.java,v $";
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/jgraph/actions/LazyEagerAction.java,v 1.7 2007-09-21 04:41:38 wuye Exp $";

	private static final String COMMAND_NAME = "Set as Eager";
	private static final Character COMMAND_MNEMONIC = new Character('L');
	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);

	public String contextText = "Set as Eager";
	private AbstractMappingPanel absMappingPanel;
	private MappingMiddlePanel middlePanel;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public LazyEagerAction( AbstractMappingPanel abstractPanel, MappingMiddlePanel midPanel, String superText )
    {					
        super(superText, null);
        contextText = superText;
        this.absMappingPanel = abstractPanel;
		this.middlePanel = midPanel;
		setMnemonic(COMMAND_MNEMONIC);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e)
	{
		//Set node to lazy, add to lazyKey list
		if( contextText.equals("Set as Eager") )
		{
			int userChoice = JOptionPane.showConfirmDialog(middlePanel,
					"Set this as Eager?", "Question",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (userChoice == JOptionPane.YES_OPTION)
				{
				    try {
						JTree sourceTree = absMappingPanel.getSourceTree();
						JTree targetTree = absMappingPanel.getTargetTree();

				    	ModelMetadata modelMetadata = ModelMetadata.getInstance();
				    	HashSet<String> lazyKeys = modelMetadata.getLazyKeys();

						//System.out.println( "targetTree: " + targetTree.getSelectionPath() );

						if ( targetTree.getSelectionRows() != null )
						{
								TreePath leadingPath = targetTree.getLeadSelectionPath();
								String node = leadingPath.toString();
								node = parseNode( node );

								//System.out.println( "IN LazyKeyAction > " + node );

								TreePath paths[] = targetTree.getSelectionPaths();
								DefaultMutableTreeNode mutNode = (DefaultMutableTreeNode)leadingPath.getLastPathComponent();

								//System.out.println( "Sibling count:" + mutNode.getSiblingCount() );
								//System.out.println( "Child count: " + mutNode.getChildCount() );

								DefaultMutableTreeNode parent = (DefaultMutableTreeNode)mutNode.getParent();
								lazyKeys.add( node );

							//update graphics
						}

				    	modelMetadata.setLazyKeys( lazyKeys );

				    } catch (Exception exception){
				    	exception.printStackTrace();
				    }
				}
		}
		else {
			//Remove node from lazyKey list (set as Eager)
			int userChoice = JOptionPane.showConfirmDialog(middlePanel,
					"Set this as Lazy?", "Question",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if ( userChoice == JOptionPane.YES_OPTION )
			{
			    try {

			    	JTree targetTree = absMappingPanel.getTargetTree();
			    	TreePath leadingPath = targetTree.getLeadSelectionPath();
			    	ModelMetadata modelMetadata = ModelMetadata.getInstance();
			    	HashSet<String> lazyKeys = modelMetadata.getLazyKeys();

			        if ( lazyKeys.contains( parseNode( leadingPath.toString() ) ) )
			        {
			        	lazyKeys.remove( parseNode( leadingPath.toString() ) );
			        }

			       modelMetadata.setLazyKeys( lazyKeys );

			    } catch ( Exception exception ) {
			    	exception.printStackTrace();
			    }
			}
		}

		return isSuccessfullyPerformed();
	}

	public String parseNode( String node )
	{
		node = replace( node, ", ", "." );
		node = replace( node, "[", " " );
		node = replace( node, "]", " " );
        node = replace( node, "Data Model.", "" );
        node = node.trim();
		return node;
	}

	public void traverse(JTree tree)
	{
	   TreeModel model = tree.getModel();
	   if (model != null)
	   {
		      Object root = model.getRoot();
		      System.out.println(root.toString());
		      walk(model,root);
	   }
	   else
	   {
	        System.out.println("Tree is empty.");
	   }
    }

	  protected void walk(TreeModel model, Object o)
	  {
	    int  cc;
	    cc = model.getChildCount(o);
	    for( int i=0; i < cc; i++)
	    {
	      Object child = model.getChild(o, i );
	      if (model.isLeaf(child))
	      {
	        System.out.println(child.toString());
	      }
	      else {
	        System.out.print(child.toString()+"--");
	        walk(model,child );
	        }
	     }
	  }

	protected Component getAssociatedUIComponent()
	{
		return middlePanel;
	}

    static String replace(String str, String pattern, String replace) {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();

        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e+pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }
}


