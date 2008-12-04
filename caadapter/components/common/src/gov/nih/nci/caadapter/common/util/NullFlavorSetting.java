/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.common.util;

import java.util.HashMap;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Dec 3, 2008
 * @author   LAST UPDATE: $Author: wangeug 
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2008-12-04 20:35:45 $
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
		if (setting!=null)
			decodeValue(setting);
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
**/