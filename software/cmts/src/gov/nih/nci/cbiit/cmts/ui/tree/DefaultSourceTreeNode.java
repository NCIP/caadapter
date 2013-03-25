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

/**
 * This class extends the default mutable tree node as the tree node used to
 * construct Source Tree for left-pane MetaData in mapping panel or
 * other occurrences related to CSV or other type of metadata loaders in the whole UI arena.
 * One of primary reasons to have a distinct class is for differentiation purpose for future use of instanceof, for example.
 * 
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-27 20:06:30 $
 *
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
 * HISTORY: $Log: not supported by cvs2svn $
 */

