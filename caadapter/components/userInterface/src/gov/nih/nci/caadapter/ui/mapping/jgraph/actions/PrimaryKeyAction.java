/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/jgraph/actions/PrimaryKeyAction.java,v 1.6 2007-09-20 16:40:14 schroedn Exp $
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

import gov.nih.nci.caadapter.mms.metadata.ModelMetadata;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;

import javax.swing.*;
import javax.swing.tree.*;

public class PrimaryKeyAction extends AbstractContextAction
{
	private static final String COMMAND_NAME = "Make Primary Key";
	
	private static final String LOGID = "$RCSfile: PrimaryKeyAction.java,v $";
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/jgraph/actions/PrimaryKeyAction.java,v 1.6 2007-09-20 16:40:14 schroedn Exp $";
	
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

