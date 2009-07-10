/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.ui.mapping.mms.actions;

import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.mms.generator.CumulativeMappingGenerator;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociation;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLModel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLTaggedValue;
import gov.nih.nci.ncicb.xmiinout.util.ModelUtil;

import java.awt.event.ActionEvent;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Jul 2, 2009
 * @author   LAST UPDATE: $Author: wangeug 
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-07-10 19:58:16 $
 * @since caAdapter v4.2
 */

public class AssociationAnnotationAction extends ItemAnnotationAction {

	public static int SET_CORRELATION_TABLE=1;
	public static int REMOVE_CORRELATION_TABLE=2;
	public static int SET_CASCADE_SETTING=3;
	public static int REMOVE_CASCADE_SETTING=4;
	public static int SET_INVERSEOF=5;
	public static int REMOVE_INVERSEOF=6;

	/**
	 * @param txt
	 * @param midPane
	 * @param meta
	 */
	public AssociationAnnotationAction(String nameTxt, int actionType, MappingMiddlePanel midPane)
	{
		super(nameTxt, actionType, midPane );
		// TODO Auto-generated constructor stub
	}
 
	/* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	protected boolean doAction(ActionEvent e) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}


/**
* HISTORY: $Log: not supported by cvs2svn $
**/