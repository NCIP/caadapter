/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





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
 * @version  REVISION: $Revision: 1.2 $
 * @date 	 DATE: $Date: 2009-06-24 17:59:27 $
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
        if (mifURL==null)
        {
            String path = FileUtil.searchFile(dtSettingPath);
            if (path!=null) mifURL=FileUtil.retrieveResourceURL(path);
        }
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
* HISTORY: Revision 1.1  2009/03/18 15:49:13  wangeug
* HISTORY: support customer datatypes
* HISTORY:
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