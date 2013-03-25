/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.common.nodeloader;

import gov.nih.nci.caadapter.ui.common.tree.DefaultSCMTreeMutableTreeNode;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * This class defines the node loader designated to SCM Tree loading and unloading.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class SCMTreeNodeLoader extends SCMBasicNodeLoader
{
	protected DefaultMutableTreeNode constructTreeNode(Object userObject)
	{
		return this.constructTreeNode(userObject, true);
	}

	public DefaultMutableTreeNode constructTreeNode(Object userObject, boolean allowsChildren)
	{
		DefaultSCMTreeMutableTreeNode node = new DefaultSCMTreeMutableTreeNode(userObject, allowsChildren);
		return node;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:13  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/12 18:06:13  jiangsc
 * HISTORY      : Added comments
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/11 22:10:36  jiangsc
 * HISTORY      : Open/Save File Dialog consolidation.
 * HISTORY      :
 */
