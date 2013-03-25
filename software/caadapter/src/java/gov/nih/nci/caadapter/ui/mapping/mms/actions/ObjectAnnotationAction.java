/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.mapping.mms.actions;

import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.common.util.Iso21090Util;
import gov.nih.nci.caadapter.mms.generator.CumulativeMappingGenerator;
import gov.nih.nci.caadapter.mms.generator.XMIAnnotationUtil;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.caadapter.ui.mapping.mms.DialogUserInput;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLTaggedValue;
import java.awt.event.ActionEvent;
import java.util.Vector;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Jul 6, 2009
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.2 $
 * @date 	 DATE: $Date: 2009-07-30 17:38:06 $
 * @since caAdapter v4.2
 */

public class ObjectAnnotationAction extends ItemAnnotationAction
{
	public static int SET_DISCRIMINATOR_VALUE=1;
	public static int REMOVE_DISCRIMINATOR_VALUE=2;
	public static int SET_GLOBAL_NULLFLAVOR_CONTANT=3;
	public static int REMOVE_GLOBAL_NULLFLAVOR_CONSTANT=4;
	public ObjectAnnotationAction(String nameTxt,  int actionType, MappingMiddlePanel midPane)
	{
		super(nameTxt, actionType, midPane );
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	protected boolean doAction(ActionEvent e) throws Exception {
		ObjectMetadata objectMeta=(ObjectMetadata)getMetaAnnoted();
		ModelMetadata modelMetadata = CumulativeMappingGenerator.getInstance().getMetaModel();
//		UMLClass umlClass=ModelUtil.findClass(modelMetadata.getModel(), objectMeta.getXPath());
		UMLClass umlClass=objectMeta.getUmlClass();
		//default process "discriminator" tag
		String dialogName="Discriminator Value";
		String tagName="discriminator";
		if (this.getAnnotationActionType()==REMOVE_DISCRIMINATOR_VALUE)
		{
			XMIAnnotationUtil.removeTagValue(umlClass,tagName);
			return true;
		}
		if (this.getAnnotationActionType()==REMOVE_GLOBAL_NULLFLAVOR_CONSTANT)
		{
			String objecttCleanpath=XMIAnnotationUtil.getCleanPath(modelMetadata.getMmsPrefixObjectModel(), objectMeta.getXPath());
			tagName="mapped-constant:"+objecttCleanpath+".nullFlavor";
			XMIAnnotationUtil.removeTagValue(umlClass, tagName);
			return true;
		}

		//add tag
		int dialogType=DialogUserInput.INPUT_TYPE_TEXT;
		Vector<Object> dfValues=new Vector<Object>();
		if (this.getAnnotationActionType()==SET_GLOBAL_NULLFLAVOR_CONTANT)
		{
			String objecttCleanpath=XMIAnnotationUtil.getCleanPath(modelMetadata.getMmsPrefixObjectModel(), objectMeta.getXPath());
			tagName="mapped-constant:"+objecttCleanpath+".nullFlavor";
			dialogName="Global NullFlavor Constant";
			dfValues=Iso21090Util.NULL_FLAVORS;
			dialogType=DialogUserInput.INPUT_TYPE_CHOOSE;
		}
		UMLTaggedValue tagToSet=umlClass.getTaggedValue(tagName);
		String defaultTxt="";
		if (tagToSet!=null)
			defaultTxt=tagToSet.getValue();
		DialogUserInput dialog =new DialogUserInput(null,(Object)defaultTxt, dfValues, dialogName, dialogType);
		if (dialog.getUserInput()!=null)
		{
			//annotate object with new tag value
			XMIAnnotationUtil.addTagValue(umlClass, tagName,(String)dialog.getUserInput());
			return true;
		}
		return false;
	}
}


/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.1  2009/07/10 19:58:16  wangeug
* HISTORY: MMS re-engineering
* HISTORY:
**/