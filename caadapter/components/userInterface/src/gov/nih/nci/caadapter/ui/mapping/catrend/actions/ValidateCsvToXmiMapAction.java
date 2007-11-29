/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/catrend/actions/ValidateCsvToXmiMapAction.java,v 1.1 2007-11-29 16:47:52 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.mapping.catrend.actions;

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.mms.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.mms.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.mms.metadata.ModelMetadata;
import gov.nih.nci.caadapter.mms.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.common.actions.DefaultValidateAction;
import gov.nih.nci.caadapter.ui.common.message.ValidationMessageDialog;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
//import gov.nih.nci.caadapter.ui.hl7.map.HL7MappingPanel;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * This class defines the action to invoke validation of HSM.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-11-29 16:47:52 $
 */
public class ValidateCsvToXmiMapAction extends AbstractContextAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: ValidateCsvToXmiMapAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/catrend/actions/ValidateCsvToXmiMapAction.java,v 1.1 2007-11-29 16:47:52 wangeug Exp $";

	private static final String COMMAND_NAME = DefaultValidateAction.COMMAND_NAME;
	private static final Character COMMAND_MNEMONIC = DefaultValidateAction.COMMAND_MNEMONIC;
	private static final ImageIcon IMAGE_ICON = DefaultValidateAction.IMAGE_ICON;
	private static final String TOOL_TIP_DESCRIPTION = DefaultValidateAction.TOOL_TIP_DESCRIPTION;

	private transient AbstractMappingPanel parentPanel;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public ValidateCsvToXmiMapAction(AbstractMappingPanel parentPanel)
	{
		this(COMMAND_NAME, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public ValidateCsvToXmiMapAction(String name, AbstractMappingPanel parentPanel)
	{
		this(name, IMAGE_ICON, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public ValidateCsvToXmiMapAction(String name, Icon icon, AbstractMappingPanel parentPanel)
	{
		super(name, icon);
		this.parentPanel = parentPanel;
		setAdditionalAttributes();
	}

	protected void setAdditionalAttributes()
	{
		setMnemonic(COMMAND_MNEMONIC);
		//		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
		setShortDescription(TOOL_TIP_DESCRIPTION);
		//do not know how to set the icon location name, or just do not matter.
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
		if( ModelMetadata.getInstance() != null )
		{

        ValidatorResults validatorResults = new ValidatorResults();

        ModelMetadata myModel = ModelMetadata.getInstance();
		LinkedHashMap myMap = myModel.getModelMetadata();

		Set keySet = myMap.keySet();
		Iterator keySetIterator = keySet.iterator();
		while (keySetIterator.hasNext()) {
			String key = (String)keySetIterator.next();
			if (key.contains("Data Model")) continue;
			MetaObjectImpl metaO = (MetaObjectImpl)myMap.get(key);
			if (metaO instanceof AssociationMetadata) {
				if (!((AssociationMetadata)metaO).getNavigability())
					continue;
			}
			
			if (!metaO.isMapped()) {
				if (metaO instanceof ObjectMetadata) {
					Message msg = MessageResources.getMessage("O2DB3", new Object[]{metaO.getXPath()});
					validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
				}
				if (metaO instanceof AttributeMetadata) {
					if (((AttributeMetadata)metaO).isDerived()) {
						Message msg = MessageResources.getMessage("O2DB4", new Object[]{metaO.getXPath()});
						validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.INFO, msg));						
					}else {
						Message msg = MessageResources.getMessage("O2DB1", new Object[]{metaO.getXPath()});
						validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
					}
				}
				if (metaO instanceof AssociationMetadata) {
					if (!((AssociationMetadata)metaO).isBidirectional()) {
						Message msg = MessageResources.getMessage("O2DB2", new Object[]{metaO.getXPath()});
						validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
					}
				}
			}
		}
		Container container = parentPanel.getRootContainer();
		ValidationMessageDialog dlg = null;
		if (container instanceof Frame)
		{
			dlg = new ValidationMessageDialog((Frame) container, true);
		}
		else if (container instanceof Dialog)
		{
			dlg = new ValidationMessageDialog((Dialog) container, true);
		}
		if (dlg != null)
		{
			dlg.setValidatorResults(validatorResults);
			DefaultSettings.centerWindow(dlg);
			dlg.setVisible(true);
		}
		
		setSuccessfullyPerformed(true);
		return isSuccessfullyPerformed();
		
		} else {
			return false;
		}			
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
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2007/06/13 20:24:53  schroedn
 * HISTORY      : minor change
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/06/13 20:24:16  schroedn
 * HISTORY      : added check for xmi file, null error fix
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:57  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.3  2006/11/15 06:27:21  wuye
 * HISTORY      : Added message for no-mapped inherted attributes
 * HISTORY      :
 * HISTORY      : Revision 1.2  2006/11/14 16:49:17  wuye
 * HISTORY      : Added one more O2DB messages
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/11/14 15:23:59  wuye
 * HISTORY      : Added validation funcationality
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/21 18:26:17  jiangsc
 * HISTORY      : Validation Class name changes.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/05 20:52:31  jiangsc
 * HISTORY      : GUI Enhancement.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/05 20:50:29  jiangsc
 * HISTORY      : GUI Enhancement.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/10/05 20:39:53  jiangsc
 * HISTORY      : GUI Enhancement.
 * HISTORY      :
 */
