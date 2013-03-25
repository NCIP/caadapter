/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.common.tree;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Insets;
import java.awt.dnd.Autoscroll;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This class defines the default implementation of JTree with Autoscroll knowledge.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class AutoscrollableTree extends JTree implements Autoscroll
{
	/*****
	 * Overload all JTree constructors to seamlessly provide both JTree and Autoscroll
	 * implementation.
	 */
	/**
	 * Returns a <code>JTree</code> with a sample model.
	 * The default model used by the tree defines a leaf node as any node
	 * without children.
	 *
	 * @see javax.swing.tree.DefaultTreeModel#asksAllowsChildren
	 */
	public AutoscrollableTree()
	{
		super(new DefaultMutableTreeNode("Loading...", true));
	}

	/**
	 * Returns a <code>JTree</code> with each element of the
	 * specified array as the
	 * child of a new root node which is not displayed.
	 * By default, the tree defines a leaf node as any node without
	 * children.
	 *
	 * @param value an array of <code>Object</code>s
	 * @see javax.swing.tree.DefaultTreeModel#asksAllowsChildren
	 */
	public AutoscrollableTree(Object[] value)
	{
		super(value);
	}

	/**
	 * Returns a <code>JTree</code> with each element of the specified
	 * <code>Vector</code> as the
	 * child of a new root node which is not displayed. By default, the
	 * tree defines a leaf node as any node without children.
	 *
	 * @param value a <code>Vector</code>
	 * @see javax.swing.tree.DefaultTreeModel#asksAllowsChildren
	 */
	public AutoscrollableTree(Vector<?> value)
	{
		super(value);
	}

	/**
	 * Returns a <code>JTree</code> created from a <code>Hashtable</code>
	 * which does not display with root.
	 * Each value-half of the key/value pairs in the <code>HashTable</code>
	 * becomes a child of the new root node. By default, the tree defines
	 * a leaf node as any node without children.
	 *
	 * @param value a <code>Hashtable</code>
	 * @see javax.swing.tree.DefaultTreeModel#asksAllowsChildren
	 */
	public AutoscrollableTree(Hashtable<?, ?> value)
	{
		super(value);
	}

	/**
	 * Returns a <code>JTree</code> with the specified
	 * <code>TreeNode</code> as its root,
	 * which displays the root node.
	 * By default, the tree defines a leaf node as any node without children.
	 *
	 * @param root a <code>TreeNode</code> object
	 * @see javax.swing.tree.DefaultTreeModel#asksAllowsChildren
	 */
	public AutoscrollableTree(TreeNode root)
	{
		super(root);
	}

	/**
	 * Returns a <code>JTree</code> with the specified <code>TreeNode</code>
	 * as its root, which
	 * displays the root node and which decides whether a node is a
	 * leaf node in the specified manner.
	 *
	 * @param root               a <code>TreeNode</code> object
	 * @param asksAllowsChildren if false, any node without children is a
	 *                           leaf node; if true, only nodes that do not allow
	 *                           children are leaf nodes
	 * @see javax.swing.tree.DefaultTreeModel#asksAllowsChildren
	 */
	public AutoscrollableTree(TreeNode root, boolean asksAllowsChildren)
	{
		super(root, asksAllowsChildren);
	}

	/**
	 * Returns an instance of <code>JTree</code> which displays the root node
	 * -- the tree is created using the specified data model.
	 *
	 * @param newModel the <code>TreeModel</code> to use as the data model
	 */
	public AutoscrollableTree(TreeModel newModel)
	{
		super(newModel);
	}

	/**
	 * This method returns the <code>Insets</code> describing
	 * the autoscrolling region or border relative
	 * to the geometry of the implementing Component.
	 * <P>
	 * This value is read once by the <code>DropTarget</code>
	 * upon entry of the drag <code>Cursor</code>
	 * into the associated <code>Component</code>.
	 * <P>
	 *
	 * @return the Insets
	 */
	public Insets getAutoscrollInsets()
	{
		/**todo: Implement this java.awt.dnd.Autoscroll method*/
		int margin = 50;
		Rectangle bounds = this.getVisibleRect();
		Insets si = new Insets(bounds.y + margin,
				bounds.x + margin,
				bounds.height - margin,
				bounds.width - margin);
		return si;
	}

	/**
	 * notify the <code>Component</code> to autoscroll
	 *
	 * @param p A <code>Point</code> indicating the
	 *          location of the cursor that triggered this operation.
	 */
	public void autoscroll(Point p)
	{
		/**todo: Implement this java.awt.dnd.Autoscroll method*/
		int crow = this.getClosestRowForLocation(p.x, p.y);
		Rectangle rbounds = getRowBounds(crow);
		//Rectangle bounds = rbounds;
		int size = 30;
		if (rbounds != null)
			size = (int) rbounds.getHeight();
		int hsize = size / 2;
		//	int hsize=size*2;
		Rectangle rect = new Rectangle(p.x - hsize, p.y - hsize, size, size);
		this.scrollRectToVisible(rect);
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/29 16:23:53  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/08 17:12:54  jiangsc
 * HISTORY      : Support Abstract Datatype.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/04 22:22:23  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */
