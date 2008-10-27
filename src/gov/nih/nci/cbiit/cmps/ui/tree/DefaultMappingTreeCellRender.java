/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmps.ui.tree;

import gov.nih.nci.cbiit.cmps.core.AttributeMeta;
import gov.nih.nci.cbiit.cmps.core.BaseMeta;
import gov.nih.nci.cbiit.cmps.core.ElementMeta;
import gov.nih.nci.cbiit.cmps.ui.common.DefaultSettings;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Color;
import java.awt.Component;

/**
 * The class defines the default tree cell renderer for mapping panel.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-27 20:06:30 $
 *
 */
public class DefaultMappingTreeCellRender extends DefaultTreeCellRenderer //extends JPanel implements TreeCellRenderer
{
	private static final Color NONDRAG_COLOR = new Color(140, 140, 175);
	private static final Color MANYTOONE_COLOR = Color.pink;
	private static final Color DISABLED_CHOICE_BACK_GROUND_COLOR = new Color(100, 100, 100);
	private static ImageIcon pseudoRootIcon = new ImageIcon(DefaultSettings.getImage("pseudo_root.gif"));
	private static ImageIcon disableItemIcon = new ImageIcon(DefaultSettings.getImage("blue.png"));
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		if (!selected)
		{
			setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
//			if (node.getUserObject() instanceof AssociationMetadata) {
//				AssociationMetadata assoMeta = (AssociationMetadata)node.getUserObject();
//				if (!assoMeta.getNavigability())
//					setBackgroundNonSelectionColor(NONDRAG_COLOR);
//				if (assoMeta.getNavigability()&&!assoMeta.isBidirectional()&&assoMeta.isManyToOne())
//					setBackgroundNonSelectionColor(MANYTOONE_COLOR);
//			}
		}
		Component returnValue = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
//		if (value instanceof PseudoRootTreeNode)
//		{
//			setIcon(pseudoRootIcon);
//		}
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		if (node.getUserObject() instanceof ElementMeta)
		{
			ElementMeta nodeBase=(ElementMeta)node.getUserObject();
			returnValue.setEnabled(nodeBase.isIsEnabled());
			if (!nodeBase.isIsEnabled())
			{
				setIcon(disableItemIcon);
				returnValue.setBackground(DISABLED_CHOICE_BACK_GROUND_COLOR);
			}
			String treeCellText=nodeBase.getName();	
			setText(treeCellText);
		}else if (node.getUserObject() instanceof AttributeMeta){
			AttributeMeta nodeBase = (AttributeMeta)node.getUserObject();
		}
		return returnValue;
	}
}


/**
 * HISTORY: $Log: not supported by cvs2svn $
 */




