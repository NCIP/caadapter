/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.tree;


import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.JPanel;

/**
 * This class provides a customized tree to support the "source" in the mapping.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:52 $
 */
public class MappingSourceTree extends MappingBaseTree
{

	public MappingSourceTree(JPanel m, TreeNode root)
	{
		super(m,root);
	}

	protected void loadData()
	{
		DefaultMutableTreeNode root =
				new DefaultMutableTreeNode("SUBADEV");
		DefaultMutableTreeNode parent;
		DefaultMutableTreeNode child;
		DefaultMutableTreeNode field;

		// set the root.
		DefaultTreeModel dtm = new DefaultTreeModel(root);
		setModel(dtm);

		// setup parent off root.
		field = new DefaultMutableTreeNode("eventTime");
		root.add(field);

		field = new DefaultMutableTreeNode("routeCode");
		root.add(field);

		field = new DefaultMutableTreeNode("doseQuantity");
		root.add(field);

		field = new DefaultMutableTreeNode("actionTakenCode");
		root.add(field);

		field = new DefaultMutableTreeNode("actionTakenValue");
		root.add(field);

		field = new DefaultMutableTreeNode("drugCode");
		root.add(field);

		field = new DefaultMutableTreeNode("drugInstanceCode");
		root.add(field);

		field = new DefaultMutableTreeNode("drugInstanceName");
		root.add(field);

		field = new DefaultMutableTreeNode("lotNumber");
		root.add(field);

		field = new DefaultMutableTreeNode("drugExpireTime");
		root.add(field);

		field = new DefaultMutableTreeNode("manufactCode");
		root.add(field);

		field = new DefaultMutableTreeNode("manufactName");
		root.add(field);

		/******      INGREDIENT Child         ********/
		parent = new DefaultMutableTreeNode("INGREDIENT");
		root.add(parent);

		field = new DefaultMutableTreeNode("drugcode");
		parent.add(field);

		field = new DefaultMutableTreeNode("drugFormCode");
		parent.add(field);

		/******      PATIENT Child         ********/
		parent = new DefaultMutableTreeNode("PATIENT");
		root.add(parent);

		field = new DefaultMutableTreeNode("id");
		parent.add(field);

		field = new DefaultMutableTreeNode("name");
		parent.add(field);

		field = new DefaultMutableTreeNode("addres");
		parent.add(field);

		field = new DefaultMutableTreeNode("phone");
		parent.add(field);

		/******      PROVIDER Child         ********/
		parent = new DefaultMutableTreeNode("PROVIDER");
		root.add(parent);

		field = new DefaultMutableTreeNode("id");
		parent.add(field);
		field = new DefaultMutableTreeNode("type");
		parent.add(field);
		field = new DefaultMutableTreeNode("name");
		parent.add(field);
		field = new DefaultMutableTreeNode("address");
		parent.add(field);
		field = new DefaultMutableTreeNode("orgId");
		parent.add(field);
		field = new DefaultMutableTreeNode("orgCode");
		parent.add(field);
		field = new DefaultMutableTreeNode("orgName");
		parent.add(field);
		field = new DefaultMutableTreeNode("orgAddress");
		parent.add(field);


		/******      OBSDX Child         ********/
		parent = new DefaultMutableTreeNode("OBSDX");
		root.add(parent);

		field = new DefaultMutableTreeNode("id");
		parent.add(field);
		field = new DefaultMutableTreeNode("code");
		parent.add(field);
		field = new DefaultMutableTreeNode("statusCode");
		parent.add(field);
		field = new DefaultMutableTreeNode("time");
		parent.add(field);
		field = new DefaultMutableTreeNode("value");
		parent.add(field);
		field = new DefaultMutableTreeNode("organ");
		parent.add(field);

		child = new DefaultMutableTreeNode("DXPROVIDER");
		parent.add(child);

		field = new DefaultMutableTreeNode("id");
		child.add(field);
		field = new DefaultMutableTreeNode("type");
		child.add(field);
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/11/09 23:05:51  jiangsc
 * HISTORY      : Back to previous version.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/04 22:22:15  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */
