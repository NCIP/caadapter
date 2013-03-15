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
import gov.nih.nci.caadapter.mms.generator.CumulativeMappingGenerator;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.tree.DefaultTargetTreeNode;

import java.awt.Component;
import java.util.HashSet;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * TreeCellRenderer for MMS
 *
 * @author OWNER: wuye
 * @author LAST UPDATE $Author: wangeug $
 * @since     caAdatper v4.0
 * @version    $Revision: 1.11 $
 * @date       $Date: 2009-06-12 15:53:49 $
 *
 */
public class MMSRenderer extends DefaultTreeCellRenderer
{
  // this control comes here
	private XmiModelMetadata xmiMeta;
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        ImageIcon tutorialIcon;
        String table = "";
        HashSet<String> lazyKeys ;
        HashSet<String> clobKeys ;
        HashSet<String> discriminatorKeys;

        if (xmiMeta!=null)
        {
        	lazyKeys = xmiMeta.getLazyKeys();
        	clobKeys = xmiMeta.getClobKeys();
        	discriminatorKeys = xmiMeta.getDiscriminatorKeys();

        }
        else
        {
        	ModelMetadata modelMetadata = CumulativeMappingGenerator.getInstance().getMetaModel();
        	lazyKeys = modelMetadata.getLazyKeys();
        	clobKeys = modelMetadata.getClobKeys();
        	discriminatorKeys = modelMetadata.getDiscriminatorKeys();
        }

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

	public XmiModelMetadata getXmiMeta() {
		return xmiMeta;
	}

	public void setXmiMeta(XmiModelMetadata xmiMeta) {
		this.xmiMeta = xmiMeta;
	}
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.10  2008/09/26 20:35:27  linc
 * HISTORY: Updated according to code standard.
 * HISTORY:
 */
