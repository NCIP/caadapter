/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui.tree;

import gov.nih.nci.cbiit.cdms.formula.common.util.DefaultSettings;
import gov.nih.nci.cbiit.cdms.formula.core.DataElement;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class CellRenderFormula  extends DefaultTreeCellRenderer{

	private static ImageIcon finalStatusIcon = new ImageIcon(DefaultSettings.getImage("finalStatus.JPG"));
	private static ImageIcon completeStatusIcon = new ImageIcon(DefaultSettings.getImage("completeStatus.JPG"));
	private static ImageIcon draftStatusIcon = new ImageIcon(DefaultSettings.getImage("draftStatus.JPG"));
	
	private static ImageIcon parameterIcon = new ImageIcon(DefaultSettings.getImage("parameter.JPG"));
	private static ImageIcon transformationIcon = new ImageIcon(DefaultSettings.getImage("transformation.JPG"));
	
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		Component returnValue = null;
		try {
			if (!selected)
				setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
			returnValue = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

			Object userObj=node.getUserObject();
			if (userObj instanceof FormulaMeta)
			{
				FormulaMeta formula=(FormulaMeta)userObj;
				this.setText(formula.getName());
				switch(formula.getStatus())
				{
				case DRAFT:
					setIcon(draftStatusIcon);
					break;
				case COMPLETE:
					setIcon(completeStatusIcon);
					break;
				case FINAL:
					setIcon(finalStatusIcon);
					break;
				default:
					setIcon(draftStatusIcon);
					break;
				}
			}
			else if (userObj instanceof DataElement)
			{
				DataElement element =(DataElement)userObj;
				setText(element.getName());
				switch (element.getUsage())
				{
				case PARAMETER:
					setIcon(parameterIcon);
					break;
				case  TRANSFORMATION:
					setIcon(transformationIcon);
					break;
					
				default:
					setIcon(parameterIcon);
					break;
						
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return returnValue;
	}
}
