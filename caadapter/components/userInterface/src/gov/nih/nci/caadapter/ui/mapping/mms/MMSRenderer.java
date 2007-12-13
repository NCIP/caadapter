/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.ui.mapping.mms;

import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
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
public class MMSRenderer extends DefaultTreeCellRenderer
{
  // this control comes here	
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        ImageIcon tutorialIcon;
        String table = "";

        ModelMetadata modelMetadata = ModelMetadata.getInstance();
        HashSet<String> lazyKeys = modelMetadata.getLazyKeys();
        HashSet<String> clobKeys = modelMetadata.getClobKeys();
        HashSet<String> discriminatorKeys = modelMetadata.getDiscriminatorKeys();

//        System.out.println( "*** Current Lazy Keys = " + lazyKeys );

        try
        {
            String _tmpStr = (String) ((DefaultMutableTreeNode) value).getUserObject();
            if (_tmpStr.equalsIgnoreCase("Data Model"))
            {
                tutorialIcon = createImageIcon("database.png");
                setIcon(tutorialIcon);
                setToolTipText("Data model");
            }
            else
            {
                tutorialIcon = createImageIcon("schema.png");
                setIcon(tutorialIcon);
                setToolTipText("Schema");
            }
            return this;
        } catch (Exception e) 
        { 
        	//continue 
        }

        try
        {
            gov.nih.nci.caadapter.common.metadata.TableMetadata qbTableMetaData = (gov.nih.nci.caadapter.common.metadata.TableMetadata) ((DefaultTargetTreeNode) value).getUserObject();
            table = qbTableMetaData.getName();
            //System.out.println("Tables " + table );

            if (qbTableMetaData.getType().equalsIgnoreCase("normal"))
            {
                tutorialIcon = createImageIcon("table.png");
                setIcon(tutorialIcon);
                setToolTipText("Table");
            } else if (qbTableMetaData.getType().equalsIgnoreCase("VIEW"))
            {
                tutorialIcon = createImageIcon("view.png");
                setIcon(tutorialIcon);
                setToolTipText("View");
            }
        } catch (ClassCastException e)
        {
            try
            {
                gov.nih.nci.caadapter.common.metadata.ColumnMetadata discriminatorColumnMeta = (gov.nih.nci.caadapter.common.metadata.ColumnMetadata) ((DefaultTargetTreeNode) value).getUserObject();
                //System.out.println("Column " + queryBuilderMeta.getXPath() );
                boolean lazyKeyFound = false;
                boolean clobKeyFound = false;
                boolean discriminatorKeyFound = false;

                for( String key : lazyKeys )
                {
                   if ( discriminatorColumnMeta.getXPath().contains( key ))
                   {
                       lazyKeyFound = true;
                   }

                }
                for( String key : clobKeys )
                {
                   if ( discriminatorColumnMeta.getXPath().contains( key ))
                   {
                       clobKeyFound = true;
                   }

                }
                for( String key : discriminatorKeys )
                {
                   if ( discriminatorColumnMeta.getXPath().contains( key ))
                   {
                	   discriminatorColumnMeta.getTableMetadata().setHasDiscriminator(true);
                       discriminatorKeyFound = true;
                   }

                }
                if ( discriminatorKeyFound ) {
                    tutorialIcon = createImageIcon("columnDiscriminator.png");
                    setIcon(tutorialIcon);
                    setToolTipText("Discriminator");
                } else if ( clobKeyFound ) {
                    tutorialIcon = createImageIcon("columnCLOB.png");
                    setIcon(tutorialIcon);
                    setToolTipText("CLOB");
                } else if ( lazyKeyFound ) {
                    tutorialIcon = createImageIcon("columneager.png");
                    setIcon(tutorialIcon);
                    setToolTipText("Eager");
                }
                else {
                       tutorialIcon = createImageIcon("columnlazy.png");
                       setIcon(tutorialIcon);
                       setToolTipText("Lazy");
                }

               // }
            } catch (Exception ee)
            {
                try
                {
                    //String queryBuilderMeta = (String) ((DefaultSourceTreeNode) value).getUserObject();
                    tutorialIcon = createImageIcon("load.gif");
                    setIcon(tutorialIcon);
                } catch (Exception e1)
                {
                    setToolTipText(null);
                }
            }
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

