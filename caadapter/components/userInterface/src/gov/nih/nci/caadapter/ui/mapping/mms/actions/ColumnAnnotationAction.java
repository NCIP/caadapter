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

import gov.nih.nci.caadapter.common.metadata.ColumnMetadata;
import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.mms.generator.CumulativeMappingGenerator;
import gov.nih.nci.caadapter.mms.generator.XMIAnnotationUtil;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLDependency;
import gov.nih.nci.ncicb.xmiinout.domain.UMLTaggedValue;
import gov.nih.nci.ncicb.xmiinout.util.ModelUtil;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Jul 7, 2009
 * @author   LAST UPDATE: $Author: wangeug 
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-07-10 19:58:16 $
 * @since caAdapter v4.2
 */

public class ColumnAnnotationAction extends ItemAnnotationAction {

	public static int SET_PK_GENERATOR=1;
	public static int REMOVE_PK_GENERATOR=2;
	public static int SET_DISCRIMINATOR_KEY=3;
	public static int REMOVE_DISCRIMINATOR_KEY=4;
	
	/**
	 * @param actionName
	 * @param actionType
	 * @param midPane
	 */
	public ColumnAnnotationAction(String actionName, int actionType,
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
		ColumnMetadata columnMeta=(ColumnMetadata)this.getMetaAnnoted();;
		ModelMetadata modelMetadata = CumulativeMappingGenerator.getInstance().getMetaModel();
		UMLAttribute xpathAttr=ModelUtil.findAttribute(modelMetadata.getModel(),columnMeta.getXPath());

		if (getAnnotationActionType()==REMOVE_DISCRIMINATOR_KEY)
		{
			UMLTaggedValue discKeyTag=xpathAttr.getTaggedValue("discriminator");
			XMIAnnotationUtil.removeTagValue(xpathAttr, "discriminator");
			return true;
		}
		else if (getAnnotationActionType()==SET_DISCRIMINATOR_KEY)
		{
			UMLClass tableClass=ModelUtil.findClass(modelMetadata.getModel(), columnMeta.getParentXPath());
			ArrayList<String> srcObjectPath=new ArrayList<String>();
			for (UMLDependency dpd:tableClass.getDependencies())
			{
				if (dpd.getStereotype().equalsIgnoreCase("DataSource"))
				{
					UMLClass objectClass=(UMLClass)dpd.getSupplier();
					String objectFullPath=ModelUtil.getFullName(objectClass);
					if (objectFullPath!=null&&objectFullPath.length()>27)
						srcObjectPath.add(objectFullPath.substring(27));
					System.out.println("ColumnAnnotationAction.doAction()..src fullName:"+ModelUtil.getFullName(objectClass));
				}
			}
			String newDiscValue=srcObjectPath.get(0);
			System.out.println("ColumnAnnotationAction.doAction()..:"+columnMeta.getXPath() +"..add tag:discriminator="+newDiscValue);
			XMIAnnotationUtil.addTagValue(xpathAttr, "discriminator", newDiscValue);
			//remove all the "discriminator" column
			for (UMLAttribute tableColumn:tableClass.getAttributes())
			{
				if (!tableColumn.getName().equals(xpathAttr.getName()))
					XMIAnnotationUtil.removeTagValue(tableColumn, "discriminator");
			}		
			return true;
		}
		else if (getAnnotationActionType()==REMOVE_PK_GENERATOR)
		{}
		else if (getAnnotationActionType()==SET_PK_GENERATOR)
		{}
		
			
			
		return false;
	}

}


/**
* HISTORY: $Log: not supported by cvs2svn $
**/