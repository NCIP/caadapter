/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/HSMTreeMouseAdapter.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.specification.hsm;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.MetaException;
import gov.nih.nci.caadapter.hl7.clone.meta.*;
import gov.nih.nci.caadapter.ui.specification.hsm.actions.*;

import org.hl7.meta.Association;
import org.hl7.meta.CloneClass;
import org.hl7.meta.MessageType;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * This class defines the mouse listener to responds mouse events occurred on the tree view of HSM Panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:18:15 $
 */
public class HSMTreeMouseAdapter extends MouseAdapter
{
    private HSMPanel parentPanel = null;
    private JTree tree = null;
    private JPopupMenu popupMenu;

    private AddCloneAction addCloneAction;
    private RemoveCloneAction removeCloneAction;
    private AddMultipleCloneAction addMultipleCloneAction;
    private RemoveMultipleCloneAction removeMultipleCloneAction;
    private AddMultipleAttributeAction addMultipleAttributeAction;
    private RemoveMultipleAttributeAction removeMultipleAttributeAction;
    private SelectChoiceAction selectChoiceAction;
    private ValidateHSMAction validateHSMAction;

    public HSMTreeMouseAdapter(HSMPanel parentPanel)
    {
        super();
        this.parentPanel = parentPanel;
        this.tree = parentPanel.getTree();
        addCloneAction = new AddCloneAction(this.parentPanel);
        removeCloneAction = new RemoveCloneAction(this.parentPanel);
        addMultipleCloneAction = new AddMultipleCloneAction(this.parentPanel);
        removeMultipleCloneAction = new RemoveMultipleCloneAction(this.parentPanel);
        addMultipleAttributeAction = new AddMultipleAttributeAction(this.parentPanel);
        removeMultipleAttributeAction = new RemoveMultipleAttributeAction(this.parentPanel);
        selectChoiceAction = new SelectChoiceAction(this.parentPanel);
        validateHSMAction = new ValidateHSMAction(this.parentPanel);
    }

    private void setAllEnabled(boolean value)
    {
        addCloneAction.setEnabled(value);
        removeCloneAction.setEnabled(value);
		addMultipleCloneAction.setEnabled(value);
        removeMultipleCloneAction.setEnabled(value);
        addMultipleAttributeAction.setEnabled(value);
        removeMultipleAttributeAction.setEnabled(value);
        selectChoiceAction.setEnabled(value);
        validateHSMAction.setEnabled(value);
    }

    private void showIfPopupTrigger(MouseEvent mouseEvent)
    {
        if (tree.getSelectionCount() <= 0)
        {//specify at least one selected node.
            // find the selected node.
            TreePath t = tree.getClosestPathForLocation(mouseEvent.getX(), mouseEvent.getY());
            // highlight it.
            tree.setSelectionPath(t);
        }

        if (mouseEvent.isPopupTrigger())
        {
// setup the right-click popup menu.
            TreePath treePath = tree.getSelectionPath();
            if (treePath != null)
            {
                retrievePopupMenu();
                setAllEnabled(false);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                Object userObj = node.getUserObject();

                if (userObj == null || userObj instanceof CloneDatatypeFieldMeta)
                {//return right way if it is not CloneMeta or CloneAttributeMeta
                    return;
                }

                if (userObj instanceof CloneMeta)
                {
                    validateHSMAction.setEnabled(true);

                    CloneMeta cloneMeta = (CloneMeta) userObj;
                    MessageType messageType = parentPanel.getMessageType();
                    try
                    {
                        final CloneClass cloneClass = HL7V3MetaUtil.findCloneClass(messageType, cloneMeta);
                        final List<Association> addableAssociations = HL7V3MetaUtil.getAddableAssociations(cloneClass, cloneMeta);
                        final List<Association> removableAssociations = HL7V3MetaUtil.getRemovableAssociations(cloneClass, cloneMeta);
                        if (addableAssociations.size() > 0)
                        {
                            addCloneAction.setEnabled(true);
                        }
                        else
                        {
                            addCloneAction.setEnabled(false);
                        }

                        if (removableAssociations.size() > 0)
                        {
                            removeCloneAction.setEnabled(true);
                        }
                        else
                        {
                            removeCloneAction.setEnabled(false);
                        }

                        if (cloneMeta instanceof CloneChoiceMeta )
                        {
                            CloneChoiceMeta cloneChoiceMeta = (CloneChoiceMeta) cloneMeta;
                            selectChoiceAction.setEnabled(true);

                            if (cloneChoiceMeta.isAbstract())
                            {
                                addCloneAction.setEnabled(false);
                                removeCloneAction.setEnabled(false);
                            }
                        }
                    }
                    catch (MetaException ex)
                    {
                        Log.logWarning(this, ex.getMessage());
                        addCloneAction.setEnabled(false);
                        removeCloneAction.setEnabled(false);
                    }
                }

                if (userObj instanceof CloneMultipleMeta)
                {
                    CloneMultipleMeta multipleMeta = (CloneMultipleMeta) userObj;
                    if (multipleMeta.getMultipleSequenceNumber() == 1)
                    {
                        addMultipleCloneAction.setEnabled(true);
                        if (multipleMeta.getCloneMultipleMetaByName().size() > 1)
                        {
                            removeMultipleCloneAction.setEnabled(true);
                        }
                    }
                }

                if (userObj instanceof CloneAttributeMultipleMeta)
                {
                    CloneAttributeMultipleMeta multipleMeta = (CloneAttributeMultipleMeta) userObj;
                    if (multipleMeta.getMultipleSequenceNumber() == 1)
                    {
                        addMultipleAttributeAction.setEnabled(true);
                        if (multipleMeta.getAttributeMultipleMetaByName().size() > 1)
                        {
                            removeMultipleAttributeAction.setEnabled(true);
                        }
                    }
                }

                popupMenu.show(mouseEvent.getComponent(),
                    mouseEvent.getX(), mouseEvent.getY());
            }
        }
//		else if(mouseEvent.getClickCount()>=2)
//		{//if not popup trigger and clicked more than twice, assume it is an edit command.
//			ActionEvent ae = new ActionEvent(tree, 0, editAction.getName());
//			editAction.actionPerformed(ae);
//		}

    }

    public void mousePressed(MouseEvent mouseEvent)
    {
        showIfPopupTrigger(mouseEvent);
    }

    public void mouseReleased(MouseEvent mouseEvent)
    {
        showIfPopupTrigger(mouseEvent);
    }

    private JPopupMenu retrievePopupMenu()
    {
        if (popupMenu == null)
        {
            popupMenu = new JPopupMenu("Tree Manipulation");
            //already initiated in constructor.
            popupMenu.add(addCloneAction);
            popupMenu.add(removeCloneAction);
            popupMenu.addSeparator();
//			popupMenu.addSeparator();
            popupMenu.add(addMultipleCloneAction);
			popupMenu.add(removeMultipleCloneAction);
            popupMenu.add(addMultipleAttributeAction);
            popupMenu.add(removeMultipleAttributeAction);
            popupMenu.addSeparator();
            popupMenu.add(selectChoiceAction);
            popupMenu.addSeparator();
            popupMenu.add(validateHSMAction);
        }

        return popupMenu;
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.21  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.20  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.19  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/12/08 22:46:27  chene
 * HISTORY      : Add /Remove Multiple Clone Support
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/12/07 23:55:01  chene
 * HISTORY      : Saved point for Clone Multiple Implementation
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/10/07 18:39:14  jiangsc
 * HISTORY      : Minor consolidation
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/10/06 17:27:04  chene
 * HISTORY      : Saving point
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/10/03 18:55:46  chene
 * HISTORY      : Refactor CloneMeta, add CloneChoiceMeta
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/09/26 22:16:39  chene
 * HISTORY      : Add CMET 999900 support
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/09/26 19:29:46  chene
 * HISTORY      : Add Clone Attribute Multiple Class in order to support Multiple Attribute
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/09/22 21:13:05  jiangsc
 * HISTORY      : Removed deprecated classes
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/09/22 17:19:55  jiangsc
 * HISTORY      : Add removeCloneAction into right logic.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/09/15 16:01:41  chene
 * HISTORY      : SelectChoice GUI/Backend Support
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/09/14 03:04:14  chene
 * HISTORY      : Add/Remove Optional Clone support
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/09/09 22:42:03  chene
 * HISTORY      : Saved Point
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/09/08 19:37:03  chene
 * HISTORY      : Saved point
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/28 18:12:27  jiangsc
 * HISTORY      : Implemented Validation on HSM panel.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/24 22:28:37  jiangsc
 * HISTORY      : Enhanced JGraph implementation;
 * HISTORY      : Save point of CSV and HSM navigation update;
 * HISTORY      :
 */
