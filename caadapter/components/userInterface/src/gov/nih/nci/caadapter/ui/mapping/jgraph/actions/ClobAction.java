/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.mapping.jgraph.actions;

import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.caadapter.common.metadata.ModelMetadata;

import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.HashSet;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Sep 7, 2007
 * Time: 11:20:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClobAction extends AbstractContextAction {
	private static final String LOGID = "$RCSfile: ClobAction.java,v $";
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/jgraph/actions/ClobAction.java,v 1.4 2008-06-09 19:54:06 phadkes Exp $";

	private static final String COMMAND_NAME = "Set as Clob";
	private static final Character COMMAND_MNEMONIC = new Character('C');
	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);

	public String contextText = "Set as Clob";
	private AbstractMappingPanel absMappingPanel;
	private MappingMiddlePanel middlePanel;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public ClobAction( AbstractMappingPanel abstractPanel, MappingMiddlePanel midPanel, String superText ) {
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
		if( contextText.equals( "Set as Clob" ) )
		{
			int userChoice = JOptionPane.showConfirmDialog(middlePanel,
					"Set this as Clob?", "Question",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (userChoice == JOptionPane.YES_OPTION)
				{
				    try {
						JTree targetTree = absMappingPanel.getTargetTree();

				    	ModelMetadata modelMetadata = ModelMetadata.getInstance();
				    	HashSet<String> clobKeys = modelMetadata.getClobKeys();

						if ( targetTree.getSelectionRows() != null )
						{
								TreePath leadingPath = targetTree.getLeadSelectionPath();
								String node = leadingPath.toString();
								node = parseNode( node );

								TreePath paths[] = targetTree.getSelectionPaths();
								DefaultMutableTreeNode mutNode = (DefaultMutableTreeNode)leadingPath.getLastPathComponent();
								DefaultMutableTreeNode parent = (DefaultMutableTreeNode)mutNode.getParent();

								clobKeys.add( node );
						}

				    	if(clobKeys != null ) {
				    		System.out.println( "Current clob Keys = \n" + clobKeys );
				    	} else {
				    		System.out.println( "No clob Keys" );
				    	}

				    	modelMetadata.setClobKeys( clobKeys );

				    } catch (Exception exception){
				    	exception.printStackTrace();
				    }
				}
		}
		else {
			int userChoice = JOptionPane.showConfirmDialog(middlePanel,
					"Unset Clob?", "Question",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if ( userChoice == JOptionPane.YES_OPTION )
			{
			    try
                {
			    	JTree targetTree = absMappingPanel.getTargetTree();
			    	TreePath leadingPath = targetTree.getLeadSelectionPath();

			    	ModelMetadata modelMetadata = ModelMetadata.getInstance();
			    	HashSet<String> clobKeys = modelMetadata.getClobKeys();

			        if ( clobKeys.contains( parseNode( leadingPath.toString() ) ) )
			        {
			        	System.out.println( "Removing Clob... " );
			        	clobKeys.remove( parseNode( leadingPath.toString() ) );
			        }

                    modelMetadata.setClobKeys( clobKeys );

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
