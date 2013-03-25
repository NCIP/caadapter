/*L
 * Copyright SAIC, SAIC-Frederick.
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
import gov.nih.nci.caadapter.ui.common.tree.DefaultSourceTreeNode;
import java.awt.Component;
import java.util.HashSet;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Tree Cell Renderer for MMS Primary key
 *
 * @author OWNER: wuye
 * @author LAST UPDATE $Author: wangeug $
 * @since     caAdatper v4.0
 * @version    $Revision: 1.10 $
 * @date       $Date: 2009-09-29 17:39:16 $
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
        	ModelMetadata modelMetadata = CumulativeMappingGenerator.getInstance().getMetaModel();
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
        	eee.printStackTrace();
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
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.9  2009/06/12 15:53:49  wangeug
 * HISTORY: clean code: caAdapter MMS 4.1.1
 * HISTORY:
 * HISTORY: Revision 1.8  2008/09/26 20:35:27  linc
 * HISTORY: Updated according to code standard.
 * HISTORY:
 */
