/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/map/BaseComponentFactory.java,v 1.2 2007-07-18 20:36:17 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
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
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2007-07-18 20:36:17 $
 */

public class BaseComponentFactory
{
	private static final String LOGID = "$RCSfile: BaseComponentFactory.java,v $";
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/map/BaseComponentFactory.java,v 1.2 2007-07-18 20:36:17 wangeug Exp $";

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
