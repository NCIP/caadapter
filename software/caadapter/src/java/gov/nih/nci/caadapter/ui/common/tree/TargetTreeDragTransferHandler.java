/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.tree;

import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
//import gov.nih.nci.caadapter.hl7.validation.MapLinkValidator;
import gov.nih.nci.caadapter.ui.common.MappableNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.dnd.DragGestureEvent;

/**
 * This class handles drag-related data manipulation for target tree on the mapping panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:53:52 $
 */
public class TargetTreeDragTransferHandler extends TreeDefaultDragTransferHandler
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: TargetTreeDragTransferHandler.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/tree/TargetTreeDragTransferHandler.java,v 1.3 2008-06-09 19:53:52 phadkes Exp $";

	public TargetTreeDragTransferHandler(JTree tree)
	{
		super(tree);
	}

	public TargetTreeDragTransferHandler(JTree tree, int dragAction)
	{
		super(tree, dragAction);
	}

	/**
	 * Is the location within the component draggable?  Called by the
	 * DragGestureAdapter in dragGestureRecognized. If false is
	 * returned then there will be no drag initiated.
	 */
	public boolean isStartDragOk(DragGestureEvent e)
	{
		boolean result = super.isStartDragOk(e);
		if(result)
		{//the super already agrees, then do further checking.
			/**
			 * 1) if the node is already mapped;
			 * 2) if the underlying meta object is itself mappable;
			 */
			Point p = e.getDragOrigin();
			TreePath treePath = this.getTree().getPathForLocation(p.x, p.y);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
			if(node instanceof MappableNode)
			{
				MappableNode mappableNode = (MappableNode) node;
				if (mappableNode.isMapped())
				{
					result = false;
				}
			}
			if (result)
			{//check validations
				Object targetUserObject = node.getUserObject();
				ValidatorResults validatorResult = new ValidatorResults();
				if (targetUserObject instanceof MetaObject)
				{//further validate if the target object itself is mappable or not.
//					validatorResult.addValidatorResults(MapLinkValidator.isMetaObjectMappable((MetaObject) targetUserObject));
				}
				result = validatorResult.isValid();

			}
		}

		this.setInDragDropMode(result);
		return result;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/07/03 19:28:46  wangeug
 * HISTORY      : initila loading hl7 code without "clone"
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.6  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.5  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.4  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/11/23 19:48:52  jiangsc
 * HISTORY      : Enhancement on mapping validations.
 * HISTORY      :
 */
