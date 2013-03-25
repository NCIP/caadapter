/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.ui.tree;

import gov.nih.nci.cbiit.cmts.core.AttributeMeta;
import gov.nih.nci.cbiit.cmts.core.BaseMeta;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.mapping.ElementMetaLoader;

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
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.5 $
 * @date       $Date: 2009-11-09 18:33:59 $
 *
 */
public class DefaultMappingTreeCellRender extends DefaultTreeCellRenderer //extends JPanel implements TreeCellRenderer
{
	private static final Color NONDRAG_COLOR = new Color(140, 140, 175);
	private static final Color MANYTOONE_COLOR = Color.pink;
	private static final Color DISABLED_CHOICE_BACK_GROUND_COLOR = new Color(100, 100, 100);
	private static ImageIcon elementNodeIcon = new ImageIcon(DefaultSettings.getImage("elementNode.gif"));
	private static ImageIcon attributeNodeIcon = new ImageIcon(DefaultSettings.getImage("attributeNode.gif"));
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		Component returnValue = null;
		try {
			if (!selected)
				setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
			returnValue = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object userObj = node.getUserObject();
			if(userObj instanceof ElementMetaLoader.MyTreeObject)
				userObj = ((ElementMetaLoader.MyTreeObject)userObj).getUserObject();
			BaseMeta baseMeta=(BaseMeta)userObj;
			if (baseMeta.getNameSpace()!=null &&!baseMeta.getNameSpace().equals(""))
				setText(cardinalityView(baseMeta) +" ("+baseMeta.getNameSpace() +")");
			else
				setText(cardinalityView(baseMeta));
			
			if (baseMeta instanceof ElementMeta)
			{
				if (node.isLeaf())
					setIcon(elementNodeIcon);
				
				if (((ElementMeta)baseMeta).isIsChosen())
				{
					String lbText="<html><font color=red><b>"+getText()+"</b></font></html>";
					setText(lbText);
				}
				if (baseMeta.getName().startsWith("<choice>")) 
				{	 
					String lbText=getText();
					String htmlText="<html><font color=blue><I>"+lbText.replace("<choice>", "&lt;choice&gt;")+"</I></font></html>";
					setText(htmlText);
				}
			}
			else if (baseMeta instanceof AttributeMeta)
			{
				setIcon(attributeNodeIcon);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnValue;
	}
	
	private String cardinalityView( BaseMeta baseMeta )
	{
		StringBuffer rtBuffer=new StringBuffer(baseMeta.getName());
		if (baseMeta instanceof ElementMeta )
		{
			ElementMeta elMeta=(ElementMeta)baseMeta;
			if (elMeta.getMaxOccurs()==null|elMeta.getMinOccurs()==null)
				return rtBuffer.toString();
				
			rtBuffer.append("["+elMeta.getMinOccurs()+"...");
	    	if (elMeta.getMaxOccurs()!=null&&elMeta.getMaxOccurs().intValue()==-1)
	    		rtBuffer.append("*]");
	    	else
	    		rtBuffer.append(elMeta.getMaxOccurs()+"]");
		}
		else if (baseMeta instanceof AttributeMeta )
		{
			AttributeMeta attMeta=(AttributeMeta)baseMeta;
			if (attMeta.isIsRequired())
	    		rtBuffer.append(" [Required");
	    	else
	    		rtBuffer.append(" [Optional");
	    	
	    	if (attMeta.getFixedValue()!=null)
	    		rtBuffer.append(":fixed/"+attMeta.getFixedValue());
	    	else if (attMeta.getDefaultValue()!=null)
	    		rtBuffer.append(":default/"+attMeta.getDefaultValue());
	    	
	    	rtBuffer.append("]");
		}
		return rtBuffer.toString();
	}
}


/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.4  2009/11/04 19:11:11  wangeug
 * HISTORY: display metaMeta element nameSpace
 * HISTORY:
 * HISTORY: Revision 1.3  2009/10/16 17:35:08  wangeug
 * HISTORY: add icon to tree node
 * HISTORY:
 * HISTORY: Revision 1.2  2008/12/03 20:46:14  linc
 * HISTORY: UI update.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/10/27 20:06:30  linc
 * HISTORY: GUI first add.
 * HISTORY:
 */




