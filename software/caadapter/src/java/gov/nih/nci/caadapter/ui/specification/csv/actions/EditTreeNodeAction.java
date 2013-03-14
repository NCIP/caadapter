/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.specification.csv.actions;

import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVMetaImpl;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.validation.CSVMetaValidator;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.specification.csv.CSVPanel;
import gov.nih.nci.caadapter.ui.specification.csv.CSVSegmentDefinitionDialog;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

//
/**
 * This class defines the edit tree node action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-06-09 19:54:07 $
 */
public class EditTreeNodeAction extends AbstractCsvContextCRUDAction {
    public static final String COMMAND_NAME_GENERAL = "Edit...";
    public static final String COMMAND_NAME_CHOICE = "Edit Choice Segment...";
    private static final String COMMAND_NAME = COMMAND_NAME_GENERAL;
    private static final Character COMMAND_MNEMONIC = new Character('E');
    private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false);
    private transient JTree tree;

    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    public EditTreeNodeAction(CSVPanel parentPanel) {
        this(COMMAND_NAME, parentPanel);
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a default icon.
     */
    public EditTreeNodeAction(String name, CSVPanel parentPanel) {
        this(name, null, parentPanel);
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public EditTreeNodeAction(String name, Icon icon, CSVPanel parentPanel) {
        super(name, icon, parentPanel);
        setMnemonic(COMMAND_MNEMONIC);
        setAcceleratorKey(ACCELERATOR_KEY_STROKE);
        setActionCommandType(DOCUMENT_ACTION_TYPE);
    }

    private JTree getTree() {
        if (this.tree == null) {
            this.tree = parentPanel.getTree();
        }
        return this.tree;
    }

    /**
     * Invoked when an action occurs.
     */
    protected boolean doAction(ActionEvent e) {
//		Log.logInfo(this, "EditTreeNodeAction is called.");
        super.doAction(e);
        if (!isSuccessfullyPerformed()) {
            return false;
        }
        //only support single select.
        TreePath treePath = getTree().getSelectionPath();
        if (treePath == null) {
            JOptionPane.showMessageDialog(tree.getRootPane().getParent(), "Tree has no selection",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            setSuccessfullyPerformed(false);
            return false;
        }
        DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
        MetaObject userObject = (MetaObject) targetNode.getUserObject();
        String currentValue = userObject.getName();
        String[] inputValue = getValidatedUserInput(userObject);
        // inputValue is null if the user hits cancel.
        if (inputValue != null) {
            boolean isChanged = false;
            if (!GeneralUtilities.areEqual(inputValue[0], currentValue)) {
                userObject.setName(inputValue[0]);
                isChanged = true;
            }
            if (userObject instanceof CSVSegmentMeta) {
                String cardinal = ((CSVSegmentMeta) userObject).getCardinalityType().toString();
                if (!GeneralUtilities.areEqual(inputValue[1], cardinal)) {
                    ((CSVSegmentMeta) userObject).setCardinalityWithString(inputValue[1]);
                    isChanged = true;
                }
            }
            if (isChanged) {
                TreeModel treeModel = tree.getModel();
                if (treeModel instanceof DefaultTreeModel) {
                   // ((DefaultTreeModel) treeModel).nodeChanged(targetNode);
                   ((DefaultTreeModel) treeModel).nodeStructureChanged(targetNode);
                }
            }
        }
        setSuccessfullyPerformed(true);
        return isSuccessfullyPerformed();
    }

    /**
     * Return a valid user input after validated, or null or empty string if user cancelled the action.
     *
     * @param segmentOrFieldMeta
     * @return a valid user input after validated, or null or empty string if user cancelled the action.
     */
    private String[] getValidatedUserInput(MetaObject segmentOrFieldMeta) {
        String[] newInputValue = null;
        CSVMeta rootMeta = parentPanel.getCSVMeta(false);
        String[] currentValue = new String[2];
        currentValue[0] = segmentOrFieldMeta.getName();
        String cosmeticName = (DefaultSettings.getClassNameWithoutPackage(segmentOrFieldMeta.getClass()).toLowerCase().indexOf("segment") != -1) ? "Segment" : "Field";
        Frame tempParent = null;
        Container tempContainer = parentPanel;
        while (true) {
            tempContainer = tempContainer.getParent();
            if (tempContainer == null) {
                JOptionPane.showMessageDialog(getAssociatedUIComponent(), "Can not open DB Connection setup dialog", "Invalid Frame type", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            if (tempContainer instanceof Frame) {
                tempParent = (Frame) tempContainer;
                break;
            }
        }
        do {
            if (segmentOrFieldMeta instanceof CSVFieldMeta) {
                newInputValue = new String[2];
                newInputValue[0] = (String) JOptionPane.showInputDialog(parentPanel,
                        "Edit a " + cosmeticName + " name", COMMAND_NAME, JOptionPane.INFORMATION_MESSAGE, null, null, currentValue[0]);
                newInputValue[1] = null;
            } else if (segmentOrFieldMeta instanceof CSVSegmentMeta) {
                //boolean cardinalityEditable = (parentPanel.getCSVMeta(true).getRootSegment() != (CSVSegmentMeta)segmentOrFieldMeta);
                CSVSegmentMeta segmentMeta = (CSVSegmentMeta) segmentOrFieldMeta;
                newInputValue = null;
                String titleOfScreen = "";
                if (segmentMeta.isChoiceMemberSegment()) {
                    currentValue[1] = segmentMeta.getCardinalityWithString();
                    String tempCardinality = segmentMeta.getParent().getCardinalityWithString();
                    String str = (String) JOptionPane.showInputDialog(tempParent, "Change Name of CSV Choice menber Segment\n(The Cardinality is inherited from parent. '" + tempCardinality + "')", "Add Segment of Choice", JOptionPane.INFORMATION_MESSAGE, null, null, currentValue[0]);
                    if ((GeneralUtilities.isBlank(str)) || (GeneralUtilities.isBlank(tempCardinality))) {
                        break;
                    }
                    newInputValue = new String[2];
                    newInputValue[0] = str;
                    newInputValue[1] = tempCardinality;
                } else {
                    if (segmentMeta.isChoiceSegment()) {
                        titleOfScreen = COMMAND_NAME_CHOICE;
                        currentValue[1] = segmentMeta.getCardinalityWithString() + " " + Config.SUFFIX_OF_CHOICE_CARDINALITY;
                    } else {
                        titleOfScreen = COMMAND_NAME_GENERAL;
                        currentValue[1] = segmentMeta.getCardinalityWithString();
                    }
                    CSVSegmentDefinitionDialog dialog = new CSVSegmentDefinitionDialog(tempParent, titleOfScreen, (((CSVSegmentMeta) segmentOrFieldMeta).getParent() != null));

//                    if (segmentMeta.getParent().isChoiceSegment()) dialog.setSingleCardinality(currentValue[1]);
//                    else dialog.setCardinality(currentValue[1]);
                    dialog.setCardinality(currentValue[1]);
                    dialog.setSegmentName(currentValue[0]);
                    DefaultSettings.centerWindow(dialog);
                    dialog.setVisible(true);
                    if (!dialog.isOkButtonClicked()) break;
                    newInputValue = new String[2];
                    newInputValue[0] = dialog.getSegmentName();
                    newInputValue[1] = dialog.getCardinality();
                }
            }
            if ((newInputValue == null) || (GeneralUtilities.isBlank(newInputValue[0]))) {
//				Log.logInfo(this, "user may cancelled the input");
                newInputValue = null;
                break;
            } else {
                if (rootMeta == null) {//hope this section of code never is called.
                    System.err.println("WARNING: CSV Root Meta is null!");
                    rootMeta = new CSVMetaImpl();
//					rootMeta.setRootSegment(segmentOrFieldMeta);
                    parentPanel.setCsvMeta(rootMeta);
                }
                //change only for validation purpose
                segmentOrFieldMeta.setName(newInputValue[0]);

//				CSVMetaValidator validator = new CSVMetaValidator(rootMeta);
                ValidatorResults validatorResults = new ValidatorResults();
                if (segmentOrFieldMeta instanceof CSVSegmentMeta) {
                    //per meeting discussion on defect #164, will only validate the name not the whole CSV tree.
                    ((CSVSegmentMeta) segmentOrFieldMeta).setCardinalityWithString(newInputValue[1]);
                    validatorResults.addValidatorResults(CSVMetaValidator.validateSegmentMetaName((CSVSegmentMeta) segmentOrFieldMeta));
                    ((CSVSegmentMeta) segmentOrFieldMeta).setCardinalityWithString(currentValue[1]);
                    CSVMetaValidator validator = new CSVMetaValidator(rootMeta);
                    //Check if 2 or more segments with same name in SCM.
                    validatorResults.addValidatorResults(validator.ScmRule1());
                    //Check if it is ALLCAPS.
                    validatorResults.addValidatorResults(validator.ScmRule4());
                } else if (segmentOrFieldMeta instanceof CSVFieldMeta) {
                    //per meeting discussion on defect #164, will only validate the name not the whole CSV tree.
                    validatorResults.addValidatorResults(CSVMetaValidator.validateFieldMetaName((CSVFieldMeta) segmentOrFieldMeta));
                    CSVMetaValidator validator = new CSVMetaValidator(rootMeta);
                    //Check if 2 or more fields with same name in same segment in SCM (case-insensitive).
                    validatorResults.addValidatorResults(validator.ScmRule2());
                    //Check if field with default field name in SCM.
                    validatorResults.addValidatorResults(validator.ScmRule7());
                    //Field name valiation
                    validatorResults.addValidatorResults(validator.ScmRule8());
                }

                //clean up after validation purpose
                segmentOrFieldMeta.setName(currentValue[0]);
                if (validatorResults.getAllMessages().size() == 0) {
                    break;
                }
                displayValidationResults(validatorResults);
            }
        }
        while (true);
        return newInputValue;
    }
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2007/10/09 20:47:17  jayannah
 * HISTORY      : Made changes to refresh the tree when the changes are made to the root and also to update the properties pane when a field is added
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/12 15:48:50  umkis
 * HISTORY      : csv cardinality
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.19  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/11/01 23:09:31  jiangsc
 * HISTORY      : UI Enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/10/31 21:31:52  jiangsc
 * HISTORY      : Fix to Defect 164 and 162.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/10/24 19:09:40  jiangsc
 * HISTORY      : Implement some validation upon CRUD.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/10/18 13:35:26  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/23 19:11:29  jiangsc
 * HISTORY      : action status update
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/22 17:16:03  jiangsc
 * HISTORY      : Enhanced the display on edit dialog
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/22 16:02:39  jiangsc
 * HISTORY      : Work on Add Field/Segment
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/22 20:53:09  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
