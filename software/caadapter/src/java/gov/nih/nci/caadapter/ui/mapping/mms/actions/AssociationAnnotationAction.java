/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.mapping.mms.actions;

import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.common.metadata.ColumnMetadata;
import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.mms.generator.CumulativeMappingGenerator;
import gov.nih.nci.caadapter.mms.generator.XMIAnnotationUtil;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.caadapter.ui.mapping.mms.DialogUserInput;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociation;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociationEnd;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLTaggedValue;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLAssociationEndBean;
import gov.nih.nci.ncicb.xmiinout.util.ModelUtil;

import java.awt.event.ActionEvent;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Jul 2, 2009
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.3 $
 * @date 	 DATE: $Date: 2009-09-28 20:09:39 $
 * @since caAdapter v4.2
 */

public class AssociationAnnotationAction extends ItemAnnotationAction {

	public static int SET_CASCADE_SETTING=1;
	public static int REMOVE_CASCADE_SETTING=2;
	public static int SET_INVERSEOF=3;
	public static int REMOVE_INVERSEOF=4;

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
		AssociationMetadata asscMeta=(AssociationMetadata)getMetaAnnoted();
		UMLAssociation umlAssc=asscMeta.getUMLAssociation();
		ModelMetadata modelMetadata = CumulativeMappingGenerator.getInstance().getMetaModel();
		UMLClass umlClass=ModelUtil.findClass(modelMetadata.getModel(), asscMeta.getXPath());

		if (this.getAnnotationActionType()==SET_INVERSEOF)
		{

    		ColumnMetadata columnMapped=(ColumnMetadata)CumulativeMappingGenerator.getInstance().getCumulativeMapping().findMappedTarget(asscMeta.getXPath());
        	UMLAttribute xpathAttr=ModelUtil.findAttribute(modelMetadata.getModel(),columnMapped.getXPath());

        	//find the other association end
    		UMLAssociationEnd endOne=(UMLAssociationEnd)umlAssc.getAssociationEnds().get(0);
    		UMLAssociationEnd endTwo=(UMLAssociationEnd)umlAssc.getAssociationEnds().get(1);
     		UMLAssociationEndBean otherEnd=(UMLAssociationEndBean)endOne;
    		UMLAssociationEndBean thisEnd=(UMLAssociationEndBean)endTwo;
    		if (endTwo.getRoleName().equals(asscMeta.getRoleName()))
    		{
    			otherEnd=(UMLAssociationEndBean)endTwo;
    			thisEnd=(UMLAssociationEndBean)endOne;
    		}
    		String otherEndFullPath= ModelUtil.getFullName((UMLClass)otherEnd.getUMLElement())+"."+thisEnd.getRoleName();
    		String otherEndPurePath=XMIAnnotationUtil.getCleanPath(modelMetadata.getMmsPrefixObjectModel(), otherEndFullPath);
    		XMIAnnotationUtil.addTagValue(xpathAttr, "inverse-of", otherEndPurePath);

        	// remove "inverse-of" tag assigned with the other end
        	ColumnMetadata otherColumnMapped=(ColumnMetadata)CumulativeMappingGenerator.getInstance().getCumulativeMapping().findMappedTarget(otherEndFullPath);
        	UMLAttribute otherEndMappedAttr=ModelUtil.findAttribute(modelMetadata.getModel(),otherColumnMapped.getXPath());
        	XMIAnnotationUtil.removeTagValue(otherEndMappedAttr, "inverse-of");
			return true;
		}
		else if (this.getAnnotationActionType()==REMOVE_INVERSEOF)
		{
    		ColumnMetadata columnMapped=(ColumnMetadata)CumulativeMappingGenerator.getInstance().getCumulativeMapping().findMappedTarget(asscMeta.getXPath());
        	UMLAttribute xpathAttr=ModelUtil.findAttribute(modelMetadata.getModel(),columnMapped.getXPath());
        	XMIAnnotationUtil.removeTagValue(xpathAttr, "inverse-of");
        	return true;
		}
		else if (this.getAnnotationActionType()==REMOVE_CASCADE_SETTING)
		{
			String pureCasecadePath=XMIAnnotationUtil.getCleanPath(modelMetadata.getMmsPrefixObjectModel(),  asscMeta.getXPath());
			String newCascadeTagName="NCI_CASCADE_ASSOCIATION#"+pureCasecadePath;
			XMIAnnotationUtil.removeTagValue(umlAssc, newCascadeTagName);
			return true;
		}
		else if (this.getAnnotationActionType()==SET_CASCADE_SETTING)
		{
			String pureCasecadePath=XMIAnnotationUtil.getCleanPath(modelMetadata.getMmsPrefixObjectModel(),  asscMeta.getXPath());
			String newCascadeTagName="NCI_CASCADE_ASSOCIATION#"+pureCasecadePath;
			String dfValues="";
			UMLTaggedValue cascadeTagValue=umlAssc.getTaggedValue(newCascadeTagName);
			if (cascadeTagValue!=null)
				dfValues=cascadeTagValue.getValue();

			DialogUserInput dialog = new DialogUserInput(null, dfValues, "Cascade Setting",DialogUserInput.INPUT_TYPE_SELECTION );
			if (dialog.getUserInput()!=null)
			{
				System.out.println("AssociationAnnotationAction.doAction()..selected value:"+dialog.getUserInput());
				//annotate object with new tag value
 				XMIAnnotationUtil.addTagValue(umlAssc, newCascadeTagName,(String)dialog.getUserInput());
	        }

			return true;
		}
		return false;
	}

}


/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.2  2009/07/30 17:38:06  wangeug
* HISTORY: clean codes: implement 4.1.1 requirements
* HISTORY:
* HISTORY: Revision 1.1  2009/07/10 19:58:16  wangeug
* HISTORY: MMS re-engineering
* HISTORY:
**/