/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.mapping.mms.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.common.metadata.ColumnMetadata;
import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.common.metadata.TableMetadata;
import gov.nih.nci.caadapter.mms.generator.CumulativeMappingGenerator;
import gov.nih.nci.caadapter.mms.generator.XMIAnnotationUtil;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.caadapter.ui.mapping.mms.DialogUserInput;
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
 * @version  REVISION: $Revision: 1.3 $
 * @date 	 DATE: $Date: 2009-07-30 19:06:33 $
 * @since caAdapter v4.2
 */

public class ColumnAnnotationAction extends ItemAnnotationAction {

	public static int SET_PK_GENERATOR=1;
	public static int REMOVE_PK_GENERATOR=2;
	public static int SET_DISCRIMINATOR_KEY=3;
	public static int REMOVE_DISCRIMINATOR_KEY=4;

	public static int SET_INVERSE_KEY=5;
	public static int REMOVE_INVERSE_KEY=6;

	public static int SET_IMPLICT_KEY=7;
	public static int REMOVE_IMPLICT_KEY=8;

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

	private UMLClass retrieveTopSuperClass(UMLClass objectClass)
	{
		UMLClass[] superClasses=ModelUtil.getSuperclasses(objectClass);
		if (superClasses==null||superClasses.length==0)
			return objectClass;
		return retrieveTopSuperClass(superClasses[0]);
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
			XMIAnnotationUtil.removeTagValue(xpathAttr, "discriminator");
			return true;
		}
		else if (getAnnotationActionType()==REMOVE_IMPLICT_KEY)
		{
			XMIAnnotationUtil.removeTagValue(xpathAttr, "mapped-attributes");
			return true;
		}
		else if (getAnnotationActionType()==REMOVE_INVERSE_KEY)
		{
			XMIAnnotationUtil.removeTagValue(xpathAttr, "inverse-of");
			return true;
		}
		else if (getAnnotationActionType()==REMOVE_PK_GENERATOR)
		{
			//check if primary key column
			HashMap<String, HashMap<String, String>> pkSetting=XMIAnnotationUtil.findPrimaryKeyGenerrator(xpathAttr);

			Vector<Object> dfValues=new Vector<Object>(pkSetting.keySet());
			DialogUserInput dialog = new DialogUserInput(null, null, dfValues, "Primary Key Generator",DialogUserInput.INPUT_TYPE_CHOOSE );
			if (dialog.getUserInput()!=null)
			{
				//annotate object with new tag value
 				XMIAnnotationUtil.removePrimaryKey(xpathAttr, (String)dialog.getUserInput());
	        }

		}
		else if (getAnnotationActionType()==SET_DISCRIMINATOR_KEY)
		{
			UMLClass tableClass=ModelUtil.findClass(modelMetadata.getModel(), columnMeta.getParentXPath());
			String topSuperPath=null;
			for (UMLDependency dpd:tableClass.getDependencies())
			{
				if (dpd.getStereotype().equalsIgnoreCase("DataSource"))
				{
					UMLClass objectClass=(UMLClass)dpd.getSupplier();
					UMLClass topSuperClass=retrieveTopSuperClass(objectClass);

					String objectFullPath=ModelUtil.getFullName(topSuperClass);
					if (objectFullPath!=null&&objectFullPath.length()>27)
					{
						topSuperPath=objectFullPath.substring(27);
						System.out.println("ColumnAnnotationAction.doAction()..src fullName:"+topSuperPath);
						break;
					}
				}
			}
			if (topSuperPath==null)
				return false;
			System.out.println("ColumnAnnotationAction.doAction()..:"+columnMeta.getXPath() +"..add tag:discriminator="+topSuperPath);
			XMIAnnotationUtil.addTagValue(xpathAttr, "discriminator", topSuperPath);
			//remove all the "discriminator" column
			for (UMLAttribute tableColumn:tableClass.getAttributes())
			{
				if (!tableColumn.getName().equals(xpathAttr.getName()))
					XMIAnnotationUtil.removeTagValue(tableColumn, "discriminator");
			}
			return true;
		}
		else if (getAnnotationActionType()==SET_INVERSE_KEY)
		{
			UMLClass tableClass=ModelUtil.findClass(modelMetadata.getModel(), columnMeta.getParentXPath());

			//remove all the "inverse-of" column
			String inverseValue="";
			for (UMLAttribute tableColumn:tableClass.getAttributes())
			{
				if (tableColumn.getTaggedValue("implements-association")!=null)
					inverseValue=tableColumn.getTaggedValue("implements-association").getValue();
				XMIAnnotationUtil.removeTagValue(tableColumn, "inserse-of");
			}
			//set new value
			XMIAnnotationUtil.addTagValue(xpathAttr, "inverse-of", inverseValue);
			return true;
		}
		else if (getAnnotationActionType()==SET_IMPLICT_KEY)
		{
			UMLClass tableClass=ModelUtil.findClass(modelMetadata.getModel(), columnMeta.getParentXPath());
			//find the implict id
			TableMetadata tblMeta= (TableMetadata)modelMetadata.getModelMetadata().get(columnMeta.getParentXPath());

			String implicitKeyValue="";
			for (UMLAttribute tableColumn:tableClass.getAttributes())
			{
				if (tableColumn.getTaggedValue("mapped-attributes")!=null)
				{
					String srcKey=tblMeta.getXPath()+"."+tableColumn.getName();
					AttributeMetadata srcAttr=(AttributeMetadata)CumulativeMappingGenerator.getInstance().getCumulativeMapping().findMappedSource(srcKey);
					if (srcAttr==null)
						continue;
					String newValue=XMIAnnotationUtil.getCleanPath(modelMetadata.getMmsPrefixObjectModel(), srcAttr.getXmlPath());
					if (implicitKeyValue.equals(""))
						implicitKeyValue=newValue;
					else if (newValue.length()<implicitKeyValue.length())
						implicitKeyValue=newValue;
				}
			}
			//set new value, "<attributefull path>.id" as the implicit primary key
			XMIAnnotationUtil.addTagValue(xpathAttr, "mapped-attributes", implicitKeyValue+".id");
			return true;
		}
		else if (getAnnotationActionType()==SET_PK_GENERATOR)
		{
			HashMap<String, HashMap<String, String>> pkSetting=XMIAnnotationUtil.findPrimaryKeyGenerrator(xpathAttr);
			DialogUserInput dialog = new DialogUserInput(null, pkSetting, "Primary Key Generator",DialogUserInput.INPUT_TYPE_TABBED );
			if (dialog.getUserInput()!=null)
			{
				HashMap<String, HashMap<String, String>> pkSettingInputs=(HashMap<String, HashMap<String, String>> )dialog.getUserInput();
				Iterator<String> dbNameKeys=pkSettingInputs.keySet().iterator();
		    	while (dbNameKeys.hasNext())
		    	{
		    		String paraDbName=dbNameKeys.next();
		    		HashMap<String, String>pkOneDbSetting=(HashMap<String, String>)pkSettingInputs.get(paraDbName);
		    		XMIAnnotationUtil.addPrimaryKey(xpathAttr, paraDbName, pkOneDbSetting);
		    	}
			}
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