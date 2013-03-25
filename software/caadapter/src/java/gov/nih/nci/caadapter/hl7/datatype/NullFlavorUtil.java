/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.datatype;

import gov.nih.nci.caadapter.common.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Dec 12, 2008
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.3 $
 * @date 	 DATE: $Date: 2009-06-24 17:58:56 $
 * @since caAdapter v4.2
 */

public class NullFlavorUtil {
	private static HashMap<String, List> nullFlavorDatypes_Default;
	private static HashMap<String, List> nullFlavorDatypes_UserSetting;
	private static String DATA_TYPE_CORE_ATTRIBUTE_DEFAULT="datatype-core-attributes-default.xml";
	private static String DATA_TYPE_CORE_ATTRIBUTE_USER_SETTING="datatype-core-attributes-setting.xml";
	static
	{
		//loading system default setting
		InputStream defaultFi = null;

		try {
			File srcFile=new  File("conf/"+DATA_TYPE_CORE_ATTRIBUTE_DEFAULT);
			if (srcFile.exists())
				defaultFi =new FileInputStream(srcFile);
			else
			{
                String path = FileUtil.searchFile("conf/"+DATA_TYPE_CORE_ATTRIBUTE_DEFAULT);
                if (path!=null) defaultFi = new FileInputStream(path);
                else defaultFi=FileUtil.retrieveResourceURL(DATA_TYPE_CORE_ATTRIBUTE_DEFAULT).openStream();
//				defaultFi = CaadapterUtil.class.getClassLoader().getResource(DATA_TYPE_CORE_ATTRIBUTE_DEFAULT).openStream();

			}
			DatatypeCoreAttributeLoader dtLoader=new DatatypeCoreAttributeLoader();
			dtLoader.loadCoreAttributeConfig(defaultFi);
			nullFlavorDatypes_Default=dtLoader.getDatatypeCoreAttributes();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//loading system default setting
		InputStream userSettingFi = null;

		try {
			File srcFile=new  File("conf/"+DATA_TYPE_CORE_ATTRIBUTE_USER_SETTING);
			if (srcFile.exists())
				userSettingFi =new FileInputStream(srcFile);
			else
            {
                String path = FileUtil.searchFile("conf/"+DATA_TYPE_CORE_ATTRIBUTE_USER_SETTING);
                if (path!=null) userSettingFi = new FileInputStream(path);
                else userSettingFi =FileUtil.retrieveResourceURL(DATA_TYPE_CORE_ATTRIBUTE_USER_SETTING).openStream();
				//CaadapterUtil.class.getClassLoader().getResource(DATA_TYPE_CORE_ATTRIBUTE_USER_SETTING).openStream();
            }
            DatatypeCoreAttributeLoader dtLoader=new DatatypeCoreAttributeLoader();
			dtLoader.loadCoreAttributeConfig(userSettingFi);
			nullFlavorDatypes_UserSetting=dtLoader.getDatatypeCoreAttributes();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static List<String> findDatatypeCoreAttributes(String dtName)
	{
		List rtnList=null;
		if(nullFlavorDatypes_UserSetting!=null)
			rtnList=nullFlavorDatypes_UserSetting.get(dtName);
		if (rtnList!=null&&rtnList.size()>0)
			return rtnList;

		//search system default if user's setting not found
		if (nullFlavorDatypes_Default!=null)
			rtnList=nullFlavorDatypes_Default.get(dtName);
		return rtnList;
	}
}


/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.2  2009/04/17 14:14:01  wangeug
* HISTORY: clean code:use FileUitl to retrieve resource URL
* HISTORY:
* HISTORY: Revision 1.1  2009/01/09 21:32:59  wangeug
* HISTORY: process core attribute seting with HL7 datatypes
* HISTORY:
**/