/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.mapping.mms.actions;

import java.awt.event.ActionEvent;
import java.util.Vector;

import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.common.util.Iso21090Util;
import gov.nih.nci.caadapter.mms.generator.CumulativeMappingGenerator;
import gov.nih.nci.caadapter.mms.generator.XMIAnnotationUtil;
import gov.nih.nci.caadapter.ui.common.Iso21090uiUtil;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.caadapter.ui.mapping.mms.DialogUserInput;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLTaggedValue;
import gov.nih.nci.ncicb.xmiinout.util.ModelUtil;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Jul 7, 2009
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-07-30 17:38:06 $
 * @since caAdapter v4.2
 */

public class AttributeAnnotationAction extends ItemAnnotationAction {

	public static int REMOVE_ANNOTATION_TAG=1;
	public static int SET_AS_PK=2;
	public static int SET_CONSTANT_VALUE=3;
	public static int SET_COLLECTION_ELEMENT_TYPE=4;
 	public static int SET_NULLFLAOVR_CONSTANT=5;

	private String annotationTagName;
	/**
	 * @return the annotationTagName
	 */
	public String getAnnotationTagName() {
		return annotationTagName;
	}

	/**
	 * @param annotationTagName the annotationTagName to set
	 */
	public void setAnnotationTagName(String annotationTagName) {
		this.annotationTagName = annotationTagName;
	}

	/**
	 * @param actionName
	 * @param actionType
	 * @param midPane
	 */
	public AttributeAnnotationAction(String actionName, int actionType,
			MappingMiddlePanel midPane) {
		super(actionName, actionType, midPane);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	protected boolean doAction(ActionEvent e) throws Exception {
		// TODO Auto-generated method stub
		AttributeMetadata attrMeta=(AttributeMetadata)this.getMetaAnnoted();;
		ModelMetadata modelMetadata = CumulativeMappingGenerator.getInstance().getMetaModel();
		UMLAttribute xpathAttr=ModelUtil.findAttribute(modelMetadata.getModel(),attrMeta.getXPath());
		if (xpathAttr==null&&attrMeta.isDerived())
		{
			ObjectMetadata holderObject=(ObjectMetadata)modelMetadata.getModelMetadata().get(attrMeta.getParentXPath());
			xpathAttr=Iso21090uiUtil.findInheritedAttributeDefinition(modelMetadata.getModel(), attrMeta, holderObject.getUmlClass());
		}
		if (getAnnotationActionType()==REMOVE_ANNOTATION_TAG)
		{
			XMIAnnotationUtil.removeTagValue(xpathAttr, this.getAnnotationTagName());
			return true;
		}
		else if (getAnnotationActionType()==SET_AS_PK)
		{
			String parentFullpath=attrMeta.getParentXPath();
			String parentCleanpath=XMIAnnotationUtil.getCleanPath(modelMetadata.getMmsPrefixObjectModel(), parentFullpath);

			XMIAnnotationUtil.addTagValue(xpathAttr, getAnnotationTagName(), parentCleanpath);
			UMLClass parentClass=ModelUtil.findClass(modelMetadata.getModel(), attrMeta.getParentXPath());
			//remove all the "id-attribute" column
			for (UMLAttribute sblAttribute:parentClass.getAttributes())
			{
				if (!sblAttribute.getName().equals(xpathAttr.getName()))
					XMIAnnotationUtil.removeTagValue(sblAttribute, "id-attribute");
			}
			return true;
		}
		else if (getAnnotationActionType()==SET_CONSTANT_VALUE
				||getAnnotationActionType()==SET_NULLFLAOVR_CONSTANT
				||getAnnotationActionType()==SET_COLLECTION_ELEMENT_TYPE)
		{
			//collect user's input and set tag value
			UMLTaggedValue tagToSet=xpathAttr.getTaggedValue(this.getAnnotationTagName());
			String defaultTxt="";
			if (tagToSet!=null)
				defaultTxt=tagToSet.getValue();
			Vector<Object> dfValues=new Vector<Object>();
			dfValues.add(defaultTxt);
			String dialogName="Constant Value";
			int dialogType=DialogUserInput.INPUT_TYPE_TEXT;

			if (getAnnotationActionType()==SET_NULLFLAOVR_CONSTANT)
			{
				dialogName="Local NullFlavor Constant";
				dfValues=Iso21090Util.NULL_FLAVORS;
				dialogType=DialogUserInput.INPUT_TYPE_CHOOSE;
			}
			else if (getAnnotationActionType()==SET_COLLECTION_ELEMENT_TYPE)
			{
				dialogName="Collection Element Type";
				dfValues=Iso21090Util.ADXP_SUBTYPES;
				dialogType=DialogUserInput.INPUT_TYPE_CHOOSE;
			}
			DialogUserInput dialog = new DialogUserInput(null,(Object)defaultTxt, dfValues, dialogName, dialogType);

			if (dialog.getUserInput()!=null)
			{
				//annotate object with new tag value
				XMIAnnotationUtil.addTagValue(xpathAttr, getAnnotationTagName(),(String)dialog.getUserInput());
				return true;
			}
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