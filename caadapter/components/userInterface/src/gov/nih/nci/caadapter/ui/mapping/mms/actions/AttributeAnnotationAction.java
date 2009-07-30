/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.ui.mapping.mms.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.mms.generator.CumulativeMappingGenerator;
import gov.nih.nci.caadapter.mms.generator.XMIAnnotationUtil;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
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

	public static int SET_AS_PK=1;
	public static int REMOVE_PK=2;
	
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

		if (getAnnotationActionType()==REMOVE_PK)
		{
			XMIAnnotationUtil.removeTagValue(xpathAttr, "id-attribute");
			return true;
		}
		else if (getAnnotationActionType()==SET_AS_PK)
		{
			String parentFullpath=attrMeta.getParentXPath();
			String parentCleanpath=XMIAnnotationUtil.getCleanPath(modelMetadata.getMmsPrefixObjectModel(), parentFullpath);
			
			XMIAnnotationUtil.addTagValue(xpathAttr, "id-attribute", parentCleanpath);
			UMLClass parentClass=ModelUtil.findClass(modelMetadata.getModel(), attrMeta.getParentXPath());
			//remove all the "discriminator" column
			for (UMLAttribute sblAttribute:parentClass.getAttributes())
			{
				if (!sblAttribute.getName().equals(xpathAttr.getName()))
					XMIAnnotationUtil.removeTagValue(sblAttribute, "id-attribute");
			}		
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