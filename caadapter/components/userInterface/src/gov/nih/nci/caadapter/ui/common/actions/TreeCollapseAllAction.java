/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.actions;

import gov.nih.nci.caadapter.ui.common.DefaultSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * This class defines the tree "Collapse All" action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class TreeCollapseAllAction extends AbstractContextAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: TreeCollapseAllAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/actions/TreeCollapseAllAction.java,v 1.2 2008-06-09 19:53:51 phadkes Exp $";

	private static final String COMMAND_NAME = "Collapse All";
	private static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("treeCollapseAll.gif"));
	private static final Character COMMAND_MNEMONIC = new Character('C');
	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Event.CTRL_MASK, false);
	private static final String TOOL_TIP_DESCRIPTION = "Collapse All (Ctrl+'-')";

	private transient JTree tree;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public TreeCollapseAllAction(JTree tree)
	{
		this(COMMAND_NAME, tree);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public TreeCollapseAllAction(String name, JTree tree)
	{
		this(name, IMAGE_ICON, tree);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public TreeCollapseAllAction(String name, Icon icon, JTree tree)
	{
		super(name, icon);
		this.tree = tree;
		setAdditionalAttributes();
	}

	/**
	 * Will be called by the constructor to set additional attributes.
	 */
	protected void setAdditionalAttributes()
	{//override super class's one to plug in its own attributes.
		setMnemonic(COMMAND_MNEMONIC);
		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
		setShortDescription(TOOL_TIP_DESCRIPTION);
	}

	/**
	 * Set the new tree value.
	 *
	 * @param newTree
	 */
	public void setTree(JTree newTree)
	{
		this.tree = newTree;
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
		if (tree != null)
		{
			int count = tree.getRowCount();
			//do not collapse the root node
			for (int i = count - 1; i > 0; i--)
			{
				//when collapsing, since the number of row is different after each call,
				// so better collapse from the bottom.
				tree.collapseRow(i);
			}
		}
		setSuccessfullyPerformed(true);
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return tree;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 23:06:18  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/14 21:37:20  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/05 20:52:51  jiangsc
 * HISTORY      : GUI Enhancement.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/05 20:50:31  jiangsc
 * HISTORY      : GUI Enhancement.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/05 20:39:54  jiangsc
 * HISTORY      : GUI Enhancement.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/26 15:57:44  jiangsc
 * HISTORY      : TreeExpandAll and TreeCollapseAll Class package move
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/04 22:22:21  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/25 22:02:27  jiangsc
 * HISTORY      : 1) Added expand all and collapse all;
 * HISTORY      : 2) Added toolbar on the mapping panel;
 * HISTORY      : 3) Consolidated menus;
 * HISTORY      :
 */
