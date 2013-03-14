/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.common.tree;


import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.JPanel;

/**
 * This class provides a customized tree to support the "target" in the mapping.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:53:52 $
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
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/07/03 19:26:31  wangeug
 * HISTORY      : clean code
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/11/09 23:05:51  jiangsc
 * HISTORY      : Back to previous version.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/04 22:22:15  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */

