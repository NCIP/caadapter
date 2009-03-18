/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.datatype;

import gov.nih.nci.caadapter.common.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Mar 9, 2009
 * @author   LAST UPDATE: $Author: wangeug 
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-03-18 15:49:13 $
 * @since caAdapter v4.2
 */

public class CustomerDatatypeUtil {
	private static HashMap <String, Datatype>customerDatatypes;
 
	public static Datatype getCustomerDatatype(String dtName)
	{
		if (customerDatatypes==null)
			loadSetting();
	
		if (customerDatatypes!=null)
			return customerDatatypes.get(dtName);
		return null;
	}

	private static void loadSetting()
	{
		String dtSettingPath="conf/hl7-customer-datatype-setting.xml";
		URL mifURL=FileUtil.retrieveResourceURL(dtSettingPath);

		CustomerDatatypeSettingLoader settingLoader= new CustomerDatatypeSettingLoader();
		if (mifURL!=null)
		{
			try {
				InputStream dtSettingIs =mifURL.openStream();
				settingLoader.loadCustomerDatatypeSetting(dtSettingIs);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			settingLoader.loadCustomerDatatypeSetting(new File(dtSettingPath));
		customerDatatypes=settingLoader.getCustmoerDatatypeSetting();
	}
}


/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.3  2009/03/13 16:29:04  wangeug
* HISTORY: support multiple HL& normatives: set default as 2008
* HISTORY:
* HISTORY: Revision 1.2  2009/03/13 14:53:09  wangeug
* HISTORY: support multiple HL& normatives: remember the current version
* HISTORY:
* HISTORY: Revision 1.1  2009/03/12 15:00:46  wangeug
* HISTORY: support multiple HL& normatives
* HISTORY:
**/