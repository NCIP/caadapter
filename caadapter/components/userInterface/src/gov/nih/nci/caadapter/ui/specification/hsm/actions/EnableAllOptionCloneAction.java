/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/EnableAllOptionCloneAction.java,v 1.1 2007-07-12 19:16:40 wangeug Exp $
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

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
//import gov.nih.nci.caadapter.hl7.clone.meta.CloneMeta;
import gov.nih.nci.caadapter.hl7.validation.MIFAssociationValidator;
import gov.nih.nci.caadapter.hl7.validation.MIFAttributeValidator;
import gov.nih.nci.caadapter.hl7.validation.MIFClassValidator;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.DefaultValidateAction;
import gov.nih.nci.caadapter.ui.common.nodeloader.NewHSMBasicNodeLoader;
import gov.nih.nci.caadapter.ui.specification.hsm.HSMPanel;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Iterator;

import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
/**
 * This class defines the action to invoke validation of HSM.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-07-12 19:16:40 $
 */
public class EnableAllOptionCloneAction extends AbstractHSMContextCRUDAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: EnableAllOptionCloneAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/EnableAllOptionCloneAction.java,v 1.1 2007-07-12 19:16:40 wangeug Exp $";

	private static final String COMMAND_NAME = "Select All Options";
	private static final Character COMMAND_MNEMONIC = new Character('S');
	private static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("enableOptionAll.gif"));
	private static final String TOOL_TIP_DESCRIPTION = "Select All Option Clones";

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public EnableAllOptionCloneAction(HSMPanel parentPanel)
	{
		this(COMMAND_NAME, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public EnableAllOptionCloneAction(String name, HSMPanel parentPanel)
	{
		this(name, IMAGE_ICON, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public EnableAllOptionCloneAction(String name, Icon icon, HSMPanel parentPanel)
	{
		super(name, icon, parentPanel);
		setMnemonic(COMMAND_MNEMONIC);
		//		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setShortDescription(TOOL_TIP_DESCRIPTION);
	}


	/**
	 * Invoked when an action occurs.
	 */
	protected boolean doAction(ActionEvent e)
	{
		boolean superGood=super.doAction(e);
		if (!superGood)
			return superGood;
		
		//no need to check the change status as of now 
		JTree mifTree=parentPanel.getTree();
		TreePath treePath = null;
		//use root as the default selection.
		Object rootObj = mifTree.getModel().getRoot();
		if(rootObj instanceof DefaultMutableTreeNode)
		{
				treePath = new TreePath(((DefaultMutableTreeNode)rootObj).getPath());
		}
		
		DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
		Object obj = targetNode.getUserObject();
		if (obj instanceof MIFClass)
		{
			MIFClass rootMif=(MIFClass)obj;
			enableAllOptionalClones(rootMif);
			NewHSMBasicNodeLoader mifTreeLoader=new NewHSMBasicNodeLoader(true);
            DefaultMutableTreeNode  newTreeMifNode =mifTreeLoader.buildObjectNode(rootMif);

            ((DefaultTreeModel) mifTree.getModel()).setRoot(newTreeMifNode);//.nodeStructureChanged(parentNode);
            ((DefaultTreeModel) mifTree.getModel()).reload();
		}
		else //if (!(obj instanceof MIFClass))
		{
			JOptionPane.showMessageDialog(parentPanel.getParent(), "Error to enable optional clones", "Wrong Selection", JOptionPane.WARNING_MESSAGE);
			setSuccessfullyPerformed(false);
		}
		return isSuccessfullyPerformed();
	}
	private void enableAllOptionalClones(MIFClass mifClass)
	{
		HashSet <MIFAssociation>asscHash=mifClass.getAssociations();
		for (MIFAssociation assc:asscHash)
		{
			if (!assc.isMandatory())
				assc.setOptionChosen(true);
			MIFClass asscMif=assc.getMifClass();
			enableAllOptionalClones(asscMif);
		}
		
		 
	}
}
