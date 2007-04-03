/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/actions/AddSegmentAction.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.specification.csv.actions;

import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVMetaImpl;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVSegmentMetaImpl;
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
 * This class defines the add segment action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:18:15 $
 */
public class AddSegmentAction extends AbstractCsvContextCRUDAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: AddSegmentAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/actions/AddSegmentAction.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $";

	private static final String COMMAND_NAME = "Add Segment...";
	private static final Character COMMAND_MNEMONIC = new Character('S');
//	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);

	private transient JTree tree;


	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public AddSegmentAction(CSVPanel parentPanel)
	{
		this(COMMAND_NAME, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public AddSegmentAction(String name, CSVPanel parentPanel)
	{
		this(name, null, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public AddSegmentAction(String name, Icon icon, CSVPanel parentPanel)
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
				CSVSegmentMetaImpl segmentMeta = constructCSVSegmentMeta(inputValue, parentSegmentMeta, false);
				//add to the sub-tree
				DefaultMutableTreeNode newNode =this.constructDefaultTreeNode(segmentMeta, true);// parentPanel.getDefaultTreeNode(segmentMeta, true);
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
			inputValue = (String) JOptionPane.showInputDialog(tree.getRootPane().getParent(), "Enter CSV Segment Name", "Add Segment", JOptionPane.INFORMATION_MESSAGE, null, null, "");
			if(GeneralUtilities.isBlank(inputValue))
			{
//				Log.logInfo(this, "user may cancelled the input");
				break;
			}
			else
			{
				if(rootMeta==null)
				{//hope this section of code never is called.
					System.err.println("WARNING: CSV Root Meta is null!");
					rootMeta = new CSVMetaImpl();
					rootMeta.setRootSegment(parentSegmentMeta);
					parentPanel.setCsvMeta(rootMeta);
				}
				CSVSegmentMeta segmentMeta = constructCSVSegmentMeta(inputValue, parentSegmentMeta, false);
				//add only for validation purpose
				parentSegmentMeta.addSegment(segmentMeta);

				CSVMetaValidator validator = new CSVMetaValidator(rootMeta);
				ValidatorResults validatorResults = new ValidatorResults();
				//Check if 2 or more segments with same name in SCM.
				validatorResults.addValidatorResults(validator.ScmRule1());
				//Check if it is ALLCAPS.
				validatorResults.addValidatorResults(validator.ScmRule4());

				//clean up after validation purpose
				parentSegmentMeta.removeSegment(segmentMeta);

				if(validatorResults.getAllMessages().size()==0)
				{
					break;
				}
				displayValidationResults(validatorResults);
			}
		}
		while(true);
		return inputValue;
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.16  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/10/31 04:22:21  chene
 * HISTORY      : Take 'All Caps' out at 'Enter CSV Segment Name All Caps'
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/10/24 19:09:40  jiangsc
 * HISTORY      : Implement some validation upon CRUD.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/10/21 22:37:49  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/18 13:35:26  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/31 15:03:25  jiangsc
 * HISTORY      : Fixed some UI medium defects. Thanks to Dan's test.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/23 19:11:29  jiangsc
 * HISTORY      : action status update
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/22 16:02:39  jiangsc
 * HISTORY      : Work on Add Field/Segment
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/19 20:38:19  jiangsc
 * HISTORY      : To implement Add Segment/Field
 * HISTORY      :
 */
