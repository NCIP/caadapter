/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.mapping.mms.actions;

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.mms.generator.CumulativeMappingGenerator;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.common.actions.DefaultValidateAction;
import gov.nih.nci.caadapter.ui.common.message.ValidationMessageDialog;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;

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
 * @since caAdapter v1.2
 * @version    $Revision: 1.9 $
 * @date       $Date: 2009-09-30 17:07:45 $
 */
public class ValidateObjectToDbMapAction extends AbstractContextAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: ValidateObjectToDbMapAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/mms/actions/ValidateObjectToDbMapAction.java,v 1.9 2009-09-30 17:07:45 wangeug Exp $";

	private static final String COMMAND_NAME = DefaultValidateAction.COMMAND_NAME;
	private static final Character COMMAND_MNEMONIC = DefaultValidateAction.COMMAND_MNEMONIC;
	private static final ImageIcon IMAGE_ICON = DefaultValidateAction.IMAGE_ICON;
	private static final String TOOL_TIP_DESCRIPTION = DefaultValidateAction.TOOL_TIP_DESCRIPTION;

	private transient AbstractMappingPanel parentPanel;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public ValidateObjectToDbMapAction(AbstractMappingPanel parentPanel)
	{
		this(COMMAND_NAME, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public ValidateObjectToDbMapAction(String name, AbstractMappingPanel parentPanel)
	{
		this(name, IMAGE_ICON, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public ValidateObjectToDbMapAction(String name, Icon icon, AbstractMappingPanel parentPanel)
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
		if( CumulativeMappingGenerator.getInstance().getMetaModel()!= null )
		{

        ValidatorResults validatorResults = new ValidatorResults();

        ModelMetadata myModel = CumulativeMappingGenerator.getInstance().getMetaModel();//ModelMetadata.getInstance();
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
				//skip item in "Model.ValueDomains"
				if (metaO.getXPath().indexOf("Model.ValueDomain")>-1)
					continue;

				if (metaO instanceof ObjectMetadata) {
					//if the object is a super class and is extended-- INFO
					//else -- ERROR
					if (((ObjectMetadata)metaO).getUmlClass().getGeneralizations().isEmpty())
					{
						Message msg = MessageResources.getMessage("O2DB3", new Object[]{metaO.getXPath()});
						validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
					}
					else
					{
						Message msg = MessageResources.getMessage("O2DB5", new Object[]{metaO.getXPath()});
						validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.INFO, msg));
					}
				}
				if (metaO instanceof AttributeMetadata) {
					if (((AttributeMetadata)metaO).isDerived()) {

						String superClassName=(String)myModel.getInheritanceMetadata().get(((AttributeMetadata)metaO).getParentXPath());

						Message msg = MessageResources.getMessage("O2DB4", new Object[]{metaO.getXPath(), superClassName});
						validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.INFO, msg));
					}else {
						ObjectMetadata holderClass= (ObjectMetadata)myMap.get(((AttributeMetadata)metaO).getParentXPath());
						if (holderClass.isMapped())
						{
							Message msg = MessageResources.getMessage("O2DB1", new Object[]{metaO.getXPath()});
							validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
						}
						else if (!holderClass.getUmlClass().getGeneralizations().isEmpty())
						{
							Message msg = MessageResources.getMessage("O2DB6", new Object[]{metaO.getXPath(), holderClass.getName()});
							validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.INFO, msg));
						}
						else
						{
							Message msg = MessageResources.getMessage("O2DB1", new Object[]{metaO.getXPath()});
							validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
						}
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
 * HISTORY      : Revision 1.8  2009/07/30 17:38:06  wangeug
 * HISTORY      : clean codes: implement 4.1.1 requirements
 * HISTORY      :
 * HISTORY      : Revision 1.7  2009/06/12 15:53:01  wangeug
 * HISTORY      : clean code: caAdapter MMS 4.1.1
 * HISTORY      :
 * HISTORY      : Revision 1.6  2008/09/26 20:35:27  linc
 * HISTORY      : Updated according to code standard.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2008/06/09 19:54:06  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/12/13 21:09:43  wangeug
 * HISTORY      : resolve code dependence in compiling
 * HISTORY      :
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
