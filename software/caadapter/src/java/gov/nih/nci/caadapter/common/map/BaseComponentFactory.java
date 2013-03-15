/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common.map;

import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.util.Config;

import java.io.File;

/**
 * A factory class to facilitate creation of source and target base component,
 * so that classes outside of components directory may not be necessary aware the
 * difference of various source and target component.
 * <p/>
 * This isolation will help us be able to plug different implementation of source and target
 * component implementation without affecting existing implementation.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:53:49 $
 */

public class BaseComponentFactory
{
	private static final String LOGID = "$RCSfile: BaseComponentFactory.java,v $";
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/map/BaseComponentFactory.java,v 1.3 2008-06-09 19:53:49 phadkes Exp $";

//	private BaseComponentFactory()
//	{
//	}

	/**
	 * Return a constructed source component
	 *
	 * @param metaObject
	 * @param file
	 * @param viewObject
	 * @return BaseComponent
	 */
	public static BaseComponent getDefaultSourceComponent(MetaObject metaObject, File file, View viewObject)
	{
		BaseComponent sourceComponent = new BaseComponent();
		if (viewObject == null)
		{
			viewObject = ViewImpl.getViewImpl();//new ViewImpl();
		}
		sourceComponent.setView(viewObject);
		sourceComponent.setMeta(metaObject);
		sourceComponent.setFile(file);//FileUtil.fileLocate("", metaObjectLocationName));
		sourceComponent.setType(Config.MAP_COMPONENT_SOURCE_TYPE);
		//todo: come up better solution on setting the type and kind value.
		sourceComponent.setKind(Config.CSV_DEFINITION_DEFAULT_KIND);
		return sourceComponent;
	}

	/**
	 * Return a constructed target component
	 *
	 * @param metaObject
	 * @return BaseComponent
	 */
	public static BaseComponent getDefaultTargetComponent(MetaObject metaObject, File file, View viewObject)
	{
		BaseComponent targetComponent = new BaseComponent();
		if (viewObject == null)
		{
			viewObject = ViewImpl.getViewImpl();//new ViewImpl();
		}
		targetComponent.setView(viewObject);
		targetComponent.setMeta(metaObject);
		targetComponent.setFile(file);
		targetComponent.setType(Config.MAP_COMPONENT_TARGET_TYPE);
		//todo: come up better solution on setting the type and kind value.
		targetComponent.setKind(Config.HL7_V3_DEFINITION_DEFAULT_KIND);
		return targetComponent;
	}

//	/**
//	 * Return a constructed function component
//	 *
//	 * @param metaObject
//	 * @return BaseComponent
//	 */
//	public static BaseComponent getDefaultFunctionComponent(MetaObject metaObject, File file, View viewObject)
//	{
//		BaseComponent functionComponent = new FunctionComponent();
//		if (viewObject == null)
//		{
//			viewObject = new ViewImpl();
//		}
//		functionComponent.setView(viewObject);
//		functionComponent.setMeta(metaObject);
//		functionComponent.setFile(file);
//		functionComponent.setType(Config.MAP_COMPONENT_FUNCTION_TYPE);
//		return functionComponent;
//	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/07/18 20:36:17  wangeug
 * HISTORY      : create CSV-H7L mapping with mapppingV4.0.xsd
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/05/24 15:03:30  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/08/02 18:44:20  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/19 22:44:16  jiangsc
 * HISTORY      : Feature enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/09/19 20:26:52  giordanm
 * HISTORY      : remove references to CsvComponent and CloneComponent.  I don't think we need these anymore.  I have not deleted them from CVS yet.  In addition, the MapParser and MapBuilder and now handle database (dbf) files.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/22 21:35:26  jiangsc
 * HISTORY      : Changed BaseComponentFactory and other UI classes to use File instead of string name;

 * HISTORY      : Added first implementation of Function Constant;
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/22 17:32:44  giordanm
 * HISTORY      : change the file attribute within BaseComponent from a String to a File,  this checkin also contains some refactor work to the FileUtil.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/07/14 21:48:56  giordanm
 * HISTORY      : purely aesthetic stuff - license headers, class headers, javdoc comments, etc.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/07/07 20:27:36  chene
 * HISTORY      : Change BaseComponenet Meta getData() to Meta getMeta
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/07 20:22:15  chene
 * HISTORY      : Change BaseComponenet Meta getData() to Meta getMeta
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/06/09 16:57:42  jiangsc
 * HISTORY      : Further save point.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/06/09 15:08:36  jiangsc
 * HISTORY      : More functions are added.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/06/09 15:04:40  jiangsc
 * HISTORY      : Facilitate creation of various component classes with some default settings, such as view class.
 * HISTORY      :
 */
