/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/RemoveCloneAction.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.specification.hsm.actions;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.hl7.clone.meta.CloneMeta;
import gov.nih.nci.caadapter.hl7.clone.meta.HL7V3MetaUtil;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.specification.hsm.HSMPanel;
import gov.nih.nci.caadapter.ui.specification.hsm.wizard.AssociationListWizard;

import org.hl7.meta.Association;
import org.hl7.meta.CloneClass;
import org.hl7.meta.MessageType;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * This class defines the remove optional clone action.
 *
 * @author OWNER: Eric Chen
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:18:15 $
 */
public class RemoveCloneAction extends AbstractHSMContextCRUDAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: RemoveCloneAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/RemoveCloneAction.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $";

	private static final String COMMAND_NAME = "Remove Clone";
	private static final Character COMMAND_MNEMONIC = new Character('C');

	private transient JTree tree;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public RemoveCloneAction(HSMPanel parentPanel)
	{
		this(COMMAND_NAME, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public RemoveCloneAction(String name, HSMPanel parentPanel)
	{
		this(name, null, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public RemoveCloneAction(String name, Icon icon, HSMPanel parentPanel)
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
			JOptionPane.showMessageDialog(tree.getRootPane().getParent(),
                "Tree has no selection",
				"No Selection",
                JOptionPane.WARNING_MESSAGE);
			setSuccessfullyPerformed(false);
			return false;
		}

        DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
		Object obj = targetNode.getUserObject();
		if(obj instanceof CloneMeta)
		{
			CloneMeta cloneMeta = (CloneMeta) obj;
			{
				try
				{
                    final MessageType messageType = parentPanel.getMessageType();
                    final CloneClass cloneClass = HL7V3MetaUtil.findCloneClass(messageType, cloneMeta);

                    final List<Association> associations = HL7V3MetaUtil.getRemovableAssociations(cloneClass, cloneMeta);
                    AssociationListWizard cloneListWizard =
                        new AssociationListWizard(associations, false, (JFrame)tree.getRootPane().getParent(), "Clone List", true);
                    DefaultSettings.centerWindow(cloneListWizard);
		            cloneListWizard.setVisible(true);
                    if (cloneListWizard.isOkButtonClicked())
                    {
                        final List<Association> userSelectedAssociation = cloneListWizard.getUserSelectedAssociation();
                        HL7V3MetaUtil.removeClone(userSelectedAssociation, cloneMeta);
                        parentPanel.getDefaultHSMNodeLoader().refreshSubTreeByGivenMetaObject(targetNode, cloneMeta, parentPanel.getTree());
                    }
                    setSuccessfullyPerformed(true);
				}
				catch (Exception e1)
				{
					Log.logException(getClass(), e1);
					reportThrowableToUI(e1, parentPanel);
					setSuccessfullyPerformed(false);
				}
			}
		}
		return isSuccessfullyPerformed();
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.9  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/09/15 16:01:36  chene
 * HISTORY      : SelectChoice GUI/Backend Support
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/09/14 03:04:14  chene
 * HISTORY      : Add/Remove Optional Clone support
 * HISTORY      :
 */
