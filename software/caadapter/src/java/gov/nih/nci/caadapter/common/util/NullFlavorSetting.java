/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.common.util;

import java.util.HashMap;
import java.util.Set;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Dec 3, 2008
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.2 $
 * @date 	 DATE: $Date: 2009-01-09 21:31:19 $
 * @since caAdapter v4.2
 */

public class NullFlavorSetting   extends HashMap {

	static private String  VALUE_DELIMITTER=";";
	static private String  VALUE_KEY_SEPERATOR=":";
	static private String  VALUE_LEADING_NOTE="&";
	public NullFlavorSetting()
	{
		this(null);
	}
	public NullFlavorSetting(String setting)
	{
		super();
		String nullFlavorSetting=setting;

		//use application default
		if (nullFlavorSetting==null)
			nullFlavorSetting=CaadapterUtil.findApplicationConfigValue(Config.CAADAPTER_COMPONENT_HL7_NULLFLAVOR_VALUES_DEFAULT_SETTING);

		if (nullFlavorSetting!=null)
			decodeValue(nullFlavorSetting);
	}

	public String toString()
	{
		StringBuffer rtnB=new StringBuffer();
		Set<String> nullKeys= keySet();
		for (String oneNullKey:nullKeys)
		{
			String oneNullValue=(String)get(oneNullKey);
			rtnB.append(oneNullKey);
			rtnB.append(VALUE_KEY_SEPERATOR);
			rtnB.append(oneNullValue);
			rtnB.append(VALUE_DELIMITTER);
		}
		return rtnB.toString();
	}

	private void decodeValue(String pValue)
	{
		String[] values=pValue.split(VALUE_DELIMITTER);
		for(String pair:values)
		{
			if (pair==null||pair.equals(""))
				continue;
			//remove leading "&"
			while (pair.startsWith(VALUE_LEADING_NOTE))
				pair=pair.replaceFirst(VALUE_LEADING_NOTE, "");

			//set one key/value pair
			if (pair.indexOf(VALUE_KEY_SEPERATOR)>-1)
			{
				String key=pair.substring(0, pair.indexOf(VALUE_KEY_SEPERATOR));
				String nullFlavorValue=pair.substring(pair.indexOf(VALUE_KEY_SEPERATOR)+1);
				//add key/value pair
				put(key, nullFlavorValue);
			}
			else
				put("NULL", pair);//use it as for value NULL
		}

	}
}


/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.1  2008/12/04 20:35:45  wangeug
* HISTORY: support nullFlavor:implment NullFlavor function
* HISTORY:
**/