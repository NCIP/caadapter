/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.datatype;

import gov.nih.nci.caadapter.common.util.CaadapterUtil;

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
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-01-09 21:32:59 $
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
				defaultFi = CaadapterUtil.class.getClassLoader().getResource(DATA_TYPE_CORE_ATTRIBUTE_DEFAULT).openStream();	
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
				userSettingFi = CaadapterUtil.class.getClassLoader().getResource(DATA_TYPE_CORE_ATTRIBUTE_USER_SETTING).openStream();	
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
**/