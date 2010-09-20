/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.ui.tree;

/**
 * This class extends the default mutable tree node as the tree node used to
 * construct Target Tree for right-pane MetaData.
 * One of primary reasons to have a distinct class is for differentiation purpose for future use of instanceof, for example.
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-27 20:06:30 $
 *
 */
public class DefaultTargetTreeNode extends DefaultMappableTreeNode
{
	public DefaultTargetTreeNode(Object userObject, boolean allowsChildren)
	{
		super(userObject, allowsChildren);
	}

	public DefaultTargetTreeNode(Object userObject)
	{
		super(userObject);
	}
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 */

