/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.mapping.jgraph.actions;

import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.HashSet;
import javax.swing.*;
import javax.swing.tree.*;
/**
 * This class defines the Primary Key action.
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.10 $
 *          date        $Date: 2008-09-29 20:36:23 $
 */
public class PrimaryKeyAction extends AbstractContextAction
{
	private static final String COMMAND_NAME = "Make Primary Key";
	
	private static final String LOGID = "$RCSfile: PrimaryKeyAction.java,v $";
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/jgraph/actions/PrimaryKeyAction.java,v 1.10 2008-09-29 20:36:23 wangeug Exp $";
	
	private static final Character COMMAND_MNEMONIC = new Character('P');
	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);

    public String contextText = "Set as Primary Key";
    private AbstractMappingPanel absMappingPanel;
	private MappingMiddlePanel middlePanel;
	
	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public PrimaryKeyAction(AbstractMappingPanel abstractPanel, MappingMiddlePanel midPanel, String superText)
	{	
		super(superText, null);
        contextText = superText;
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
        if( contextText.equals("Set as Primary Key") )
        {
            int userChoice = JOptionPane.showConfirmDialog(middlePanel,
                    "Make this a Primary Key?", "Question",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
            if (userChoice == JOptionPane.YES_OPTION)
            {
                try {
                    JTree sourceTree = absMappingPanel.getSourceTree();

                    ModelMetadata modelMetadata = ModelMetadata.getInstance();
                    HashSet<String> primaryKeys = modelMetadata.getPrimaryKeys();

                    if ( sourceTree.getSelectionRows() != null )
                    {
                        TreePath leadingPath = sourceTree.getLeadSelectionPath();
                        String node = leadingPath.toString();
                        node = parseNode( node );

                        TreePath paths[] = sourceTree.getSelectionPaths();
                        DefaultMutableTreeNode mutNode = (DefaultMutableTreeNode)leadingPath.getLastPathComponent();
                        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)mutNode.getParent();

                        //Get the list of all siblings for the node, only one PK allowed among siblings
                        if (parent.getChildCount() >= 0)
                        {
                            for (Enumeration enu = parent.children(); enu.hasMoreElements(); )
                            {
                                DefaultMutableTreeNode n = (DefaultMutableTreeNode)enu.nextElement();
                                TreePath treePath = new TreePath(n.getPath());

                                // remove any PK among siblings
                                if ( primaryKeys.contains( parseNode( treePath.toString() ) ) )
                                {
                                    primaryKeys.remove( parseNode( treePath.toString() ) );
                                    ((DefaultTreeModel) sourceTree.getModel()).nodeStructureChanged( n );
                                }
                            }
                        }
                        primaryKeys.add( node );
                    }

                    if( primaryKeys != null ) {
                        System.out.println( "Current primary Keys = \n" + primaryKeys );
                    } else {
                        System.out.println( "No primary Keys" );
                    }

                    modelMetadata.setPrimaryKeys( primaryKeys );

                } catch (Exception exception){
                    exception.printStackTrace();
                }
            }
        } else {

            int userChoice = JOptionPane.showConfirmDialog(middlePanel,
                    "Unset this Primary Key?", "Question",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if ( userChoice == JOptionPane.YES_OPTION )
            {
  			    try {
			    	JTree sourceTree = absMappingPanel.getSourceTree();
			    	TreePath leadingPath = sourceTree.getLeadSelectionPath();
                      
                    ModelMetadata modelMetadata = ModelMetadata.getInstance();
			    	HashSet<String> primaryKeys = modelMetadata.getPrimaryKeys();

			        if ( primaryKeys.contains( parseNode( leadingPath.toString() ) ) )
			        {
			        	System.out.println( "Removing primary Key... " );
			        	primaryKeys.remove( parseNode( leadingPath.toString() ) );
			        }

			        modelMetadata.setPrimaryKeys( primaryKeys );

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
        node = replace( node, "(A)", " " );

//        int start = node.indexOf('(');
//        int end = node.lastIndexOf(')');
//
//        //index of out range problem!
//        String substr = node.substring( start, end + 1 );
//        node = replace( node, substr, " " );
        
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

    	/**
	 * Returns an ImageIcon, or null if the path was invalid.
	 */
	protected static ImageIcon createImageIcon(String path)
	{
	    //java.net.URL imgURL = Database2SDTMMappingPanel.class.getResource(path);
	    java.net.URL imgURL = DefaultSettings.class.getClassLoader().getResource("images/" + path);
	    if (imgURL != null)
	    {
	        //System.out.println("class.getResource is "+imgURL.toString());
	        return new ImageIcon(imgURL);
	    } else
	    {
	        System.err.println("Couldn't find file: " + imgURL.toString() + " & " + path);
	        return null;
	    }
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.9  2008/09/29 20:32:34  wangeug
 * HISTORY      : enforce code standard: license file, file description, changing history
 * HISTORY      :
 **/