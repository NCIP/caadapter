/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.tree;


/**
 * This class defines a pseudo root tree node as the place holder to indicate the type of tree.
 *
 * The instance of this class is simply a UI class. It does not participate in any drag-and-drop or other business logic related actions.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:52 $
 */
public class PseudoRootTreeNode extends DefaultMappableTreeNode//DefaultMutableTreeNode
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: PseudoRootTreeNode.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/tree/PseudoRootTreeNode.java,v 1.2 2008-06-09 19:53:52 phadkes Exp $";

	/**
	 * Creates a tree node with no parent, no children, initialized with
	 * the specified user object, and that allows children only if
	 * specified.
	 *
	 * @param userObject	 an Object provided by the user that constitutes
	 *                       the node's data
	 * @param allowsChildren if true, the node is allowed to have child
	 *                       nodes -- otherwise, it is always a leaf node
	 */
	public PseudoRootTreeNode(Object userObject, boolean allowsChildren)
	{
		super(userObject, allowsChildren);
	}

//	/**
//	 * Return true if both mapFlag and userObject is equal.
//	 *
//	 * @param o
//	 * @return true if both are equals; false otherwise.
//	 */
//	public boolean equals(Object o)
//	{
//		if (this == o) return true;
//		if (!(o instanceof PseudoRootTreeNode)) return false;
//
//		final PseudoRootTreeNode PseudoRootTreeNode = (PseudoRootTreeNode) o;
//
//		Object mUserObject = getUserObject();
//		Object thatUserObject = PseudoRootTreeNode.getUserObject();
//		boolean boolEquals = mUserObject == null ? thatUserObject == null : mUserObject.equals(thatUserObject);
//
//		return boolEquals;
//	}
//
//	/**
//	 * Include mapFlag and userObject in comparison.
//	 *
//	 * @return the hashCode of this object.
//	 */
//	public int hashCode()
//	{
//		Object userObject = getUserObject();
//		int result = (userObject == null ? 0 : userObject.hashCode());
//		return result;
//	}


}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.5  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/11/11 19:23:59  jiangsc
 * HISTORY      : Support Pseudo Root in Mapping Panel.
 * HISTORY      :
 */
