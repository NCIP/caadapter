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
 * This class provides a customized tree to support the "target" in the mapping.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-27 20:06:30 $
 *
 */
public class MappingTargetTree extends MappingBaseTree
{
	public MappingTargetTree(JPanel m, TreeNode root)
	{
		super(m, root);
	}

	protected void loadData()
	{
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("SubstanceAdministrationEvent");
		DefaultMutableTreeNode clone;
		DefaultMutableTreeNode clone_child;
		DefaultMutableTreeNode dt;
		DefaultMutableTreeNode att;

		// set the root.
		DefaultTreeModel dtm = new DefaultTreeModel(root);
		setModel(dtm);

		// setup datatypes off root.
		dt = new DefaultMutableTreeNode("code");
		root.add(dt);
		att = new DefaultMutableTreeNode("code");
		dt.add(att);
		att = new DefaultMutableTreeNode("codeSystemName");
		dt.add(att);
		att = new DefaultMutableTreeNode("displayName");
		dt.add(att);
		att = new DefaultMutableTreeNode("codeSystemVersion");
		dt.add(att);

		dt = new DefaultMutableTreeNode("classcode");
		root.add(dt);
		att = new DefaultMutableTreeNode("code");
		dt.add(att);

		dt = new DefaultMutableTreeNode("moodcode");
		root.add(dt);
		att = new DefaultMutableTreeNode("code");
		dt.add(att);

		dt = new DefaultMutableTreeNode("effectiveTime");
		root.add(dt);
		att = new DefaultMutableTreeNode("value");
		dt.add(att);

		dt = new DefaultMutableTreeNode("routeCode");
		root.add(dt);
		att = new DefaultMutableTreeNode("code");
		dt.add(att);

		dt = new DefaultMutableTreeNode("doseQuantity");
		root.add(dt);
		att = new DefaultMutableTreeNode("value");
		dt.add(att);
		att = new DefaultMutableTreeNode("unit");
		dt.add(att);

		// set up a clone off the root.
		clone = new DefaultMutableTreeNode("directTarget");
		root.add(clone);
		dt = new DefaultMutableTreeNode("typeCode", false);
		clone.add(dt);

		dt = new DefaultMutableTreeNode("manufacturedProduct");
		clone.add(dt);

		clone = new DefaultMutableTreeNode("manufacturedDrugInstance");
		dt.add(clone);

		dt = new DefaultMutableTreeNode("name");
		clone.add(dt);

		att = new DefaultMutableTreeNode("inlineText");
		dt.add(att);

		dt = new DefaultMutableTreeNode("expirationTime");
		clone.add(dt);

		att = new DefaultMutableTreeNode("high");
		dt.add(att);

		// set up a clone off the root.
		clone = new DefaultMutableTreeNode("pertinentInformation1");
		root.add(clone);
		dt = new DefaultMutableTreeNode("typeCode");
		clone.add(dt);
		att = new DefaultMutableTreeNode("code");
		dt.add(att);

		clone_child = new DefaultMutableTreeNode("pertinentObservationDx");
		clone.add(clone_child);


		clone = new DefaultMutableTreeNode("authorOrPerformer");
		root.add(clone);
		dt = new DefaultMutableTreeNode("or more clones", false);
		clone.add(dt);

	}


}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 */

