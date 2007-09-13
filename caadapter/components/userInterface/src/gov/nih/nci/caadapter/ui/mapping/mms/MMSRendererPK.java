/**
 * 
 */
package gov.nih.nci.caadapter.ui.mapping.mms;

import gov.nih.nci.caadapter.mms.metadata.ModelMetadata;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.tree.DefaultSourceTreeNode;

import java.awt.Component;
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
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        ImageIcon tutorialIcon;

        ModelMetadata modelMetadata = ModelMetadata.getInstance();
        List<String> primaryKeys = modelMetadata.getPrimaryKeys();

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
            if( ((DefaultSourceTreeNode) value).getUserObject() instanceof  gov.nih.nci.caadapter.mms.metadata.AttributeMetadata )
            {
                gov.nih.nci.caadapter.mms.metadata.AttributeMetadata attMeta = (gov.nih.nci.caadapter.mms.metadata.AttributeMetadata) ((DefaultSourceTreeNode) value).getUserObject();
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
            if( ((DefaultSourceTreeNode) value).getUserObject() instanceof  gov.nih.nci.caadapter.mms.metadata.AssociationMetadata )
            {
                gov.nih.nci.caadapter.mms.metadata.AssociationMetadata attMeta = (gov.nih.nci.caadapter.mms.metadata.AssociationMetadata) ((DefaultSourceTreeNode) value).getUserObject();
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
            if( ((DefaultMutableTreeNode) value).getUserObject() instanceof  gov.nih.nci.caadapter.mms.metadata.AssociationMetadata )
            {
                gov.nih.nci.caadapter.mms.metadata.AssociationMetadata attMeta = (gov.nih.nci.caadapter.mms.metadata.AssociationMetadata) ((DefaultSourceTreeNode) value).getUserObject();
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
