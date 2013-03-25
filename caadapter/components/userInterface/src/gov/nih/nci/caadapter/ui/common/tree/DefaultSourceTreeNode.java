/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.tree;

/**
 * This class extends the default mutable tree node as the tree node used to
 * construct Source Tree for left-pane MetaData in mapping panel or
 * other occurrences related to CSV or other type of metadata loaders in the whole UI arena.
 * One of primary reasons to have a distinct class is for differentiation purpose for future use of instanceof, for example.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:53:52 $
 */
public class DefaultSourceTreeNode extends DefaultMappableTreeNode
{


	public DefaultSourceTreeNode(Object userObject)
	{
		super(userObject);
	}
	public DefaultSourceTreeNode(Object userObject, boolean allowsChildren)
	{
		super(userObject, allowsChildren);
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/05/10 14:24:03  jayannah
 * HISTORY      : added a new constructor
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/29 16:23:53  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/04 22:22:23  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */
