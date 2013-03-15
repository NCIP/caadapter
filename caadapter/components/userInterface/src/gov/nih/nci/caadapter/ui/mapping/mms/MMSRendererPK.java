/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.ui.mapping.mms;

import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.common.metadata.XmiModelMetadata;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.tree.DefaultSourceTreeNode;
import gov.nih.nci.caadapter.ui.common.tree.DefaultTargetTreeNode;

import java.awt.Component;
import java.util.HashSet;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * @author wuye
 *
 */
public class MMSRendererPK extends DefaultTreeCellRenderer
{
  // this control comes here
	private XmiModelMetadata xmiMeta;

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        ImageIcon tutorialIcon;
        HashSet<String> primaryKeys;
        if (xmiMeta!=null)
        	primaryKeys=xmiMeta.getPrimaryKeys();
        else
        {
        	ModelMetadata modelMetadata = ModelMetadata.getInstance();
        	primaryKeys = modelMetadata.getPrimaryKeys();
        }
        try
        {
            String _tmpStr = (String) ((DefaultMutableTreeNode) value).getUserObject();

            if (_tmpStr.equalsIgnoreCase("Object Model"))
            {
                tutorialIcon = createImageIcon("schema.png");
                setIcon(tutorialIcon);
                setToolTipText("Data model");
            }
            return this;
        } catch (Exception e)
        {
        	//continue
        }

        try
        {
            if( ((DefaultSourceTreeNode) value).getUserObject() instanceof  gov.nih.nci.caadapter.common.metadata.AttributeMetadata )
            {
                gov.nih.nci.caadapter.common.metadata.AttributeMetadata attMeta = (gov.nih.nci.caadapter.common.metadata.AttributeMetadata) ((DefaultSourceTreeNode) value).getUserObject();
                System.out.println("attMeta" + attMeta );
                boolean primaryKeyFound = false;

                for( String key : primaryKeys )
                {
                   if ( attMeta.getXPath().contains( key ))
                   {
                       System.out.println("Found a Primary Key " + key );
                       primaryKeyFound = true;
                   }

                }

                if ( primaryKeyFound ) {
                    tutorialIcon = createImageIcon("bullet_key.png");
                    setIcon(tutorialIcon);
                    setToolTipText("Primary");
                }
            }
            if( ((DefaultSourceTreeNode) value).getUserObject() instanceof  gov.nih.nci.caadapter.common.metadata.AssociationMetadata )
            {
                gov.nih.nci.caadapter.common.metadata.AssociationMetadata attMeta = (gov.nih.nci.caadapter.common.metadata.AssociationMetadata) ((DefaultSourceTreeNode) value).getUserObject();
                System.out.println("attMeta" + attMeta );
                boolean primaryKeyFound = false;

                for( String key : primaryKeys )
                {
                   if ( attMeta.getXPath().contains( key ))
                   {
                       System.out.println("Found a Primary Key " + key );
                       primaryKeyFound = true;
                   }

                }
                if ( primaryKeyFound ) {
                    tutorialIcon = createImageIcon("bullet_key.png");
                    setIcon(tutorialIcon);
                    setToolTipText("Primary");
                }
            }
            if( ((DefaultMutableTreeNode) value).getUserObject() instanceof  gov.nih.nci.caadapter.common.metadata.AssociationMetadata )
            {
                gov.nih.nci.caadapter.common.metadata.AssociationMetadata attMeta = (gov.nih.nci.caadapter.common.metadata.AssociationMetadata) ((DefaultSourceTreeNode) value).getUserObject();
                System.out.println("attMeta" + attMeta );
                boolean primaryKeyFound = false;

                for( String key : primaryKeys )
                {
                   if ( attMeta.getXPath().contains( key ))
                   {
                       System.out.println("Found a Primary Key " + key );
                       primaryKeyFound = true;
                   }

                }
                if ( primaryKeyFound ) {
                    tutorialIcon = createImageIcon("bullet_key.png");
                    setIcon(tutorialIcon);
                    setToolTipText("Primary");
                }
            }
        } catch (Exception eee )
        {
//            try
//            {
//                //String queryBuilderMeta = (String) ((DefaultSourceTreeNode) value).getUserObject();
//                tutorialIcon = createImageIcon("load.gif");
//                setIcon(tutorialIcon);
//            } catch (Exception e1)
//            {
//                setToolTipText(null);
//            }
        }

        return this;
    }

    public ImageIcon createImageIcon(String path)
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

	public XmiModelMetadata getXmiMeta() {
		return xmiMeta;
	}

	public void setXmiMeta(XmiModelMetadata xmiMeta) {
		this.xmiMeta = xmiMeta;
	}
}
