/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping.jgraph.actions;

import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.caadapter.ui.mapping.mms.AddDiscriminatorValue;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;

import javax.swing.*;
import javax.swing.tree.*;

public class DiscriminatorValueAction extends AbstractContextAction
{
	private static final String COMMAND_NAME = "Set Discriminator Value";

	private static final String LOGID = "$RCSfile: DiscriminatorValueAction.java,v $";
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/jgraph/actions/DiscriminatorValueAction.java,v 1.3 2008-06-09 19:54:06 phadkes Exp $";

	private static final Character COMMAND_MNEMONIC = new Character('D');
	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
	private AbstractMappingPanel absMappingPanel;
	private MappingMiddlePanel middlePanel;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public DiscriminatorValueAction(AbstractMappingPanel abstractPanel, MappingMiddlePanel midPanel)
	{
		super(COMMAND_NAME, null);
		this.absMappingPanel = abstractPanel;
		this.middlePanel = midPanel;
		setMnemonic(COMMAND_MNEMONIC);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
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

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e)
	{
		try {

			JTree sourceTree = absMappingPanel.getSourceTree();
			JTree targetTree = absMappingPanel.getTargetTree();

			ModelMetadata modelMetadata = ModelMetadata.getInstance();
			HashSet<String> primaryKeys = modelMetadata.getPrimaryKeys();

			//System.out.println( "targetTree: " + targetTree.getSelectionPath() );
			//System.out.println( "sourceTree: " + sourceTree.getSelectionPath() );

			if ( sourceTree.getSelectionRows() != null )
			{
				TreePath leadingPath = sourceTree.getLeadSelectionPath();
				String node = leadingPath.toString();
				node = parseNode( node );

				//System.out.println( "IN PrimaryKeyAction > " + node );
				//check if parent/root
				//check for multiple primary keys among children, if found then remove and add new

				TreePath paths[] = sourceTree.getSelectionPaths();
				DefaultMutableTreeNode mutNode = (DefaultMutableTreeNode)leadingPath.getLastPathComponent();

				new AddDiscriminatorValue(new JFrame(),(ObjectMetadata)mutNode.getUserObject());
			}


		} catch (Exception exception){
			exception.printStackTrace();
		}
		return isSuccessfullyPerformed();
	}

	public String parseNode( String node )
	{
		System.out.println("node="+node);
		node = replace( node, ", ", "." );
		node = replace( node, "[", " " );
		node = replace( node, "]", " " );


        int start = node.indexOf('(');
        int end = node.lastIndexOf(')');
		System.out.println("node="+node + " " + start + " " + end);

        //index of out range problem!
//        String substr = node.substring( start, end + 1 );
//      node = replace( node, substr, " " );

        node = replace( node, "Object Model.", "" );

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
}

