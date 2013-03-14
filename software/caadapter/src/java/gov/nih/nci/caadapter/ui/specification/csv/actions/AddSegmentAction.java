/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.specification.csv.actions;

import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVMetaImpl;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVSegmentMetaImpl;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.validation.CSVMetaValidator;
import gov.nih.nci.caadapter.ui.specification.csv.CSVPanel;
import gov.nih.nci.caadapter.ui.specification.csv.CSVSegmentDefinitionDialog;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * This class defines the add segment action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:54:07 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/actions/AddSegmentAction.java,v 1.3 2008-06-09 19:54:07 phadkes Exp $";

    public static final String COMMAND_NAME_GENERAL = "Add Segment...";
    public static final String COMMAND_NAME_CHOICE = "Add Choice Segment...";
    private static String COMMAND_NAME = COMMAND_NAME_GENERAL;
	private static Character COMMAND_MNEMONIC = new Character('S');

    private String jobTitle = "";
//	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);

	private transient JTree tree;


	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public AddSegmentAction(CSVPanel parentPanel)
	{
		this(COMMAND_NAME_GENERAL, parentPanel);
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
        jobTitle = name;
        setMnemonic(COMMAND_MNEMONIC);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
	}

    /**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon, the rest parameters are received from subtype class.
     * This constructor is called by subclasses.
	 */
    public AddSegmentAction(String name, Icon icon, CSVPanel parentPanel, Character Mnemonic, int ActionType)
	{

        super(name, icon, parentPanel);
        jobTitle = name;
        COMMAND_NAME = name;
	    COMMAND_MNEMONIC = Mnemonic;
        setMnemonic(Mnemonic);
		setActionCommandType(ActionType);
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

//            String[] inputValue = new String[2];
//            if (parentSegmentMeta.isChoiceSegment())
//            {
//                if (!jobTitle.equals(COMMAND_NAME_GENERAL))
//                {
//                    JOptionPane.showMessageDialog(tree.getRootPane().getParent(), "Any Choice Segment cannot have its own Choice Segment.",
//					    "Choice Segment!!", JOptionPane.WARNING_MESSAGE);
//			        setSuccessfullyPerformed(false);
//                    return isSuccessfullyPerformed();
//                }
//                inputValue[0] = getValidatedUserInputName(parentSegmentMeta);
//                inputValue[1] = parentSegmentMeta.getCardinalityWithString();
//            }
//            else inputValue = getValidatedUserInput(parentSegmentMeta);

            String[] inputValue = getValidatedUserInput(parentSegmentMeta);//(String) JOptionPane.showInputDialog(tree.getRootPane().getParent(), "Enter CSV Segment Name (All Capital Letters)", "Add Segment", JOptionPane.INFORMATION_MESSAGE, null, null, "");
			// inputValue is null if the user hits cancel.
			//if (!GeneralUtilities.isBlank(inputValue[0]))
            if (inputValue != null)
            {
				//inputValue = inputValue.trim();
				//don't have to add to parent, since the following tree node action will take care of the addition action.

                CSVSegmentMetaImpl segmentMeta = constructCSVSegmentMeta(inputValue[0], parentSegmentMeta, false);
				segmentMeta.setCardinalityWithString(inputValue[1]);

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

	private String[] getValidatedUserInput(CSVSegmentMeta parentSegmentMeta)
	{
		String[] inputValue = null;
		CSVMeta rootMeta = parentPanel.getCSVMeta(false);
        Frame tempParent = null;
        Container tempContainer = parentPanel;
        while(true)
        {
            tempContainer = tempContainer.getParent();
            if (tempContainer == null)
            {
                JOptionPane.showMessageDialog(getAssociatedUIComponent(), "Can not open new segment setup dialog", "Invalid Frame type", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            if (tempContainer instanceof Frame)
            {
                tempParent = (Frame) tempContainer;
                break;
            }
        }
        do
		{
			//inputValue = (String) JOptionPane.showInputDialog(tree.getRootPane().getParent(), "Enter CSV Segment Name", "Add Segment", JOptionPane.INFORMATION_MESSAGE, null, null, "");
            inputValue = null;

            if (parentSegmentMeta.isChoiceSegment())
            {
                if (!jobTitle.equals(COMMAND_NAME_GENERAL))
                {
                    JOptionPane.showMessageDialog(tree.getRootPane().getParent(), "Any Choice Segment cannot have its own Choice Segment.",
					    "Choice Segment!!", JOptionPane.WARNING_MESSAGE);
			        return null;
                }
                String tempCardinality = parentSegmentMeta.getCardinalityType().toString().substring(0, Config.CARDINALITY_ONE_TO_ONE.length());
                String str = (String) JOptionPane.showInputDialog(tree.getRootPane().getParent(), "Enter Name of CSV Choice menber Segment\n(The Cardinality is inherited from parent. '" + tempCardinality + "')", "Add Segment of Choice", JOptionPane.INFORMATION_MESSAGE, null, null, "");
                if(GeneralUtilities.isBlank(str))
			    {
				    break;
			    }
                inputValue = new String[2];
                inputValue[0] = str;
                inputValue[1] = tempCardinality;
            }
            else
            {
                CSVSegmentDefinitionDialog dialog = new CSVSegmentDefinitionDialog(tempParent, jobTitle, true);
                dialog.setDefaultCardinality();
                DefaultSettings.centerWindow(dialog);
                dialog.setVisible(true);
                if (!dialog.isOkButtonClicked()) break;

                inputValue = new String[2];
                inputValue[0] = dialog.getSegmentName();
                inputValue[1] = dialog.getCardinality();
            }

            {
				if(rootMeta==null)
				{//hope this section of code never is called.
					System.err.println("WARNING: CSV Root Meta is null!");
					rootMeta = new CSVMetaImpl();
					rootMeta.setRootSegment(parentSegmentMeta);
					parentPanel.setCsvMeta(rootMeta);
				}
				CSVSegmentMeta segmentMeta = constructCSVSegmentMeta(inputValue[0], parentSegmentMeta, false);
                segmentMeta.setCardinalityWithString(inputValue[1]);
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
 * HISTORY      : Revision 1.2  2007/07/12 15:48:49  umkis
 * HISTORY      : csv cardinality
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
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
