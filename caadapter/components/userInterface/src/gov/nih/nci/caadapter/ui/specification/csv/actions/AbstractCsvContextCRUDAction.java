/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/actions/AbstractCsvContextCRUDAction.java,v 1.2 2007-10-05 19:10:47 wangeug Exp $
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

import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVFieldMetaImpl;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVSegmentMetaImpl;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.common.message.ValidationMessageDialog;
import gov.nih.nci.caadapter.ui.common.nodeloader.SCMTreeNodeLoader;
import gov.nih.nci.caadapter.ui.specification.csv.CSVPanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * This class defines a common base action to support property pane data change checking.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2007-10-05 19:10:47 $
 */
public class AbstractCsvContextCRUDAction extends AbstractContextAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: AbstractCsvContextCRUDAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/actions/AbstractCsvContextCRUDAction.java,v 1.2 2007-10-05 19:10:47 wangeug Exp $";

	protected transient CSVPanel parentPanel;

//	/**
//	 * Defines an <code>Action</code> object with a default
//	 * description string and default icon.
//	 */
//	public AbstractCsvContextCRUDAction(CSVPanel parentPanel)
//	{
//		this.parentPanel = parentPanel;
//	}

//	/**
//	 * Defines an <code>Action</code> object with the specified
//	 * description string and a default icon.
//	 */
//	public AbstractCsvContextCRUDAction(String name, CSVPanel parentPanel)
//	{
//		super(name);
//		this.parentPanel = parentPanel;
//	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public AbstractCsvContextCRUDAction(String name, Icon icon, CSVPanel parentPanel)
	{
		super(name, icon);
		this.parentPanel = parentPanel;
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e)
	{
		if (parentPanel != null)
		{
			boolean changed = parentPanel.isPropertiesPaneVisible() && parentPanel.getPropertiesPane().isDataChanged();
			if (changed)
			{
				JOptionPane.showMessageDialog(parentPanel, "Property data has changed in the Property pane. You are expected to first save your changes before performing any Add or Remove action, or click \"Reset\" button to roll back your changes.", "Reminder", JOptionPane.INFORMATION_MESSAGE);
				setSuccessfullyPerformed(false);
			}
			else
			{
				setSuccessfullyPerformed(true);
			}
		}
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return parentPanel;
	}

	protected CSVSegmentMetaImpl constructCSVSegmentMeta(String inputValue, CSVSegmentMeta parentMeta, boolean addToParent)
	{
		CSVSegmentMetaImpl segmentMeta = new CSVSegmentMetaImpl(inputValue.trim(), parentMeta);
		if (parentMeta != null && addToParent)
		{
			parentMeta.addSegment(segmentMeta);
		}
		return segmentMeta;
	}

	protected CSVFieldMetaImpl constructCSVFieldMeta(String inputValue, CSVSegmentMeta parentMeta, boolean addToParent)
	{
		//the column number will automatically reassigned upon calling targetNode.add();
		CSVFieldMetaImpl fieldMeta = new CSVFieldMetaImpl(-1, inputValue.trim(), parentMeta);
		if (parentMeta != null && addToParent)
		{
			parentMeta.addField(fieldMeta);
		}
		return fieldMeta;
	}

	protected void displayValidationResults(ValidatorResults validatorResults)
	{
		Container parentContainer = parentPanel.getRootContainer();
		ValidationMessageDialog.displayValidationResults(parentContainer, validatorResults);
	}
	
    public DefaultMutableTreeNode constructDefaultTreeNode(Object userObject, boolean allowsChildren)
    {
    	SCMTreeNodeLoader scmNodeLoader = new SCMTreeNodeLoader();
        return scmNodeLoader.constructTreeNode(userObject, allowsChildren);
    }

	public CSVPanel getParentPanel() {
		return parentPanel;
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 18:26:16  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/11/01 23:09:31  jiangsc
 * HISTORY      : UI Enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/24 19:09:40  jiangsc
 * HISTORY      : Implement some validation upon CRUD.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/23 19:57:00  jiangsc
 * HISTORY      : Name change
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/23 19:54:46  jiangsc
 * HISTORY      : Name change
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/22 16:02:38  jiangsc
 * HISTORY      : Work on Add Field/Segment
 * HISTORY      :
 */
