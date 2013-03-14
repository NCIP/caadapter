/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.specification.csv.actions;

import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVMetaImpl;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.validation.CSVMetaValidator;
import gov.nih.nci.caadapter.ui.specification.csv.CSVPanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;

/**
 * This class defines the add field action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:54:07 $
 */
public class AddFieldAction extends AbstractCsvContextCRUDAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: AddFieldAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/actions/AddFieldAction.java,v 1.3 2008-06-09 19:54:07 phadkes Exp $";

	private static final String COMMAND_NAME = "Add Field...";
	private static final Character COMMAND_MNEMONIC = new Character('F');
//	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);

	private transient JTree tree;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public AddFieldAction(CSVPanel parentPanel)
	{
		this(COMMAND_NAME, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public AddFieldAction(String name, CSVPanel parentPanel)
	{
		this(name, null, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public AddFieldAction(String name, Icon icon, CSVPanel parentPanel)
	{
		super(name, icon, parentPanel);
		setMnemonic(COMMAND_MNEMONIC);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
	}

	private JTree getTree()
	{
		if(this.tree==null)
		{
			this.tree = parentPanel.getTree();
		}
		return this.tree;
	}

	/**
	 * Invoked when an action occurs.
	 */
	protected boolean doAction(ActionEvent e)
	{
		super.doAction(e);
		if(!isSuccessfullyPerformed())
		{
			return false;
		}
		TreePath treePath = getTree().getSelectionPath();
		if(treePath==null)
		{
			JOptionPane.showMessageDialog(tree.getRootPane().getParent(), "Tree has no selection",
					"No Selection", JOptionPane.WARNING_MESSAGE);
			setSuccessfullyPerformed(false);
			return false;
		}
		DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
		Object obj = targetNode.getUserObject();
		if(obj instanceof CSVSegmentMeta)
		{
			CSVSegmentMeta parentSegmentMeta = (CSVSegmentMeta) obj;
			String inputValue = getValidatedUserInput(parentSegmentMeta);//(String) JOptionPane.showInputDialog(tree.getRootPane().getParent(), "Enter CSV Segment Name (All Capital Letters)", "Add Segment", JOptionPane.INFORMATION_MESSAGE, null, null, "");
			// inputValue is null if the user hits cancel.
			if (!GeneralUtilities.isBlank(inputValue))
			{
				inputValue = inputValue.trim();
				//don't have to add to parent, since the following tree node action will take care of the addition action.
				CSVFieldMeta fieldMeta = constructCSVFieldMeta(inputValue, parentSegmentMeta, false);
				//add to the sub-tree
				DefaultMutableTreeNode newNode = this.constructDefaultTreeNode(fieldMeta, false);//parentPanel.getDefaultTreeNode(fieldMeta, false);
				targetNode.add(newNode);
				TreeModel treeModel = tree.getModel();
				if(treeModel instanceof DefaultTreeModel)
				{
					((DefaultTreeModel)treeModel).nodeStructureChanged(targetNode);
				}
			}
			setSuccessfullyPerformed(true);
		}
		else
		{
			JOptionPane.showMessageDialog(tree.getRootPane().getParent(), "Selection is not a Segment Meta",
					"Wrong Selection", JOptionPane.WARNING_MESSAGE);
			setSuccessfullyPerformed(false);
		}
		return isSuccessfullyPerformed();
	}


	private String getValidatedUserInput(CSVSegmentMeta parentSegmentMeta)
	{
		String inputValue = null;
		CSVMeta rootMeta = parentPanel.getCSVMeta(false);
		do
		{
			inputValue = (String) JOptionPane.showInputDialog(tree.getRootPane().getParent(), "Enter CSV Field Name", "Add Field", JOptionPane.INFORMATION_MESSAGE, null, null, "");
			if (GeneralUtilities.isBlank(inputValue))
			{
				//				Log.logInfo(this, "user may cancelled the input");
				break;
			}
			else
			{
				if (rootMeta == null)
				{//hope this section of code never is called.
					System.err.println("WARNING: CSV Root Meta is null!");
					rootMeta = new CSVMetaImpl();
					rootMeta.setRootSegment(parentSegmentMeta);
					parentPanel.setCsvMeta(rootMeta);
				}
				ValidatorResults validatorResults = new ValidatorResults();

				CSVFieldMeta fieldMeta = constructCSVFieldMeta(inputValue, parentSegmentMeta, false);

				//per meeting discussion on defect #164, will only validate the name not the whole CSV tree.
				validatorResults.addValidatorResults(CSVMetaValidator.validateFieldMetaName(fieldMeta));

				if (validatorResults.getAllMessages().size() == 0)
				{
					break;
				}
				displayValidationResults(validatorResults);
			}
		}
		while (true);
		return inputValue;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/04/19 14:01:08  wangeug
 * HISTORY      : clean code
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 18:26:16  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/10/31 21:31:52  jiangsc
 * HISTORY      : Fix to Defect 164 and 162.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/24 19:09:40  jiangsc
 * HISTORY      : Implement some validation upon CRUD.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/18 13:35:26  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/23 19:11:29  jiangsc
 * HISTORY      : action status update
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/22 16:02:38  jiangsc
 * HISTORY      : Work on Add Field/Segment
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/19 20:38:19  jiangsc
 * HISTORY      : To implement Add Segment/Field
 * HISTORY      :
 */
