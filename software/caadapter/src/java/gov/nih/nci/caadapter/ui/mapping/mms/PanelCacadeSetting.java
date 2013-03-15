/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.mapping.mms;

import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Jul 27, 2009
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-07-30 17:37:44 $
 * @since caAdapter v4.2
 */

public class PanelCacadeSetting extends JPanel {
	private static Vector<String> selectableItem=new Vector<String>();
	private Vector<JCheckBox> selections=new Vector<JCheckBox>();
	static  {
		selectableItem.add("none");
		selectableItem.add("all");
		selectableItem.add("save-update");
		selectableItem.add("delete");
		selectableItem.add("all-delete-orphan");
		selectableItem.add("delete-orphan");
	 }
	public PanelCacadeSetting(String existingValue)
	{
		super(new GridLayout(3, 2));
		this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		Vector<String> existingSeletions=extractExistingValue(existingValue);
		System.out.println("PanelCacadeSetting.PanelCacadeSetting()...:"+existingSeletions);
		for (String oneSelectable: selectableItem)
		{
			JCheckBox slctCheck=new JCheckBox(oneSelectable);
			if (existingSeletions.contains(oneSelectable))
			{
				slctCheck.setSelected(true);
			}
			this.add(slctCheck);
			selections.add(slctCheck);
		}
	}

	public  String getUserSelection()
	{
		StringBuffer rtnBf=new StringBuffer();
		for (JCheckBox userSelected:selections)
		{
			if (userSelected.isSelected())
				rtnBf.append(userSelected.getText()+",");
		}
		return rtnBf.toString();
	}

	private Vector extractExistingValue(String values)
	{
		Vector<String> existingSelections=new Vector<String>();
		if (values==null||values.equals(""))
			return existingSelections;
		String[] selections=values.split(",");
		if (selections.length==0)
			return existingSelections;

		for (String oneValue:selections)
			existingSelections.add(oneValue.trim());

		return existingSelections;


	}
}


/**
* HISTORY: $Log: not supported by cvs2svn $
**/