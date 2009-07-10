/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.ui.mapping.mms.actions;

import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.mms.generator.CumulativeMappingGenerator;
import gov.nih.nci.caadapter.mms.generator.XMIAnnotationUtil;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.util.ModelUtil;

import java.awt.event.ActionEvent;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Jul 6, 2009
 * @author   LAST UPDATE: $Author: wangeug 
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-07-10 19:58:16 $
 * @since caAdapter v4.2
 */

public class ObjectAnnotationAction extends ItemAnnotationAction 
{
	public static int SET_DISCRIMINATOR_VALUE=1;
	public static int REMOVE_DISCRIMINATOR_VALUE=2;
	public ObjectAnnotationAction(String nameTxt,  int actionType, MappingMiddlePanel midPane) 
	{
		super(nameTxt, actionType, midPane );
	}
 
	/* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	protected boolean doAction(ActionEvent e) throws Exception {
		// TODO Auto-generated method stub
		ObjectMetadata objectMeta=(ObjectMetadata)getMetaAnnoted();
		ModelMetadata modelMetadata = CumulativeMappingGenerator.getInstance().getMetaModel();
		UMLClass umlClass=ModelUtil.findClass(modelMetadata.getModel(), objectMeta.getXPath());
		
		if (this.getAnnotationActionType()==REMOVE_DISCRIMINATOR_VALUE)
		{
			XMIAnnotationUtil.removeTagValue(umlClass, "discriminator");
			return true;
		}
		else if (this.getAnnotationActionType()==SET_DISCRIMINATOR_VALUE)
		{
			//UMLTaggedValue discValueTag=umlClass.getTaggedValue("discriminator");
			XMIAnnotationUtil.addTagValue(umlClass, "discriminator", "myTest");
			return true;
		}
		return false;
	}
	
 

}


/**
* HISTORY: $Log: not supported by cvs2svn $
**/