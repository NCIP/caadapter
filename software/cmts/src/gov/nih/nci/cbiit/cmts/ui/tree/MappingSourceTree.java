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


import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.JPanel;

/**
 * This class provides a customized tree to support the "source" in the mapping.
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-27 20:06:30 $
 *
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
 * HISTORY: $Log: not supported by cvs2svn $
 */

