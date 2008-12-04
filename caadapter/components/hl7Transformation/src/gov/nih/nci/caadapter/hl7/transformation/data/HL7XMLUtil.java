/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.transformation.data;

import gov.nih.nci.caadapter.common.Log;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Dec 4, 2008
 * @author   LAST UPDATE: $Author: wangeug 
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2008-12-04 20:41:20 $
 * @since caAdapter v4.2
 */

public class HL7XMLUtil {

	public static void cleanNullFlavor(XMLElement element)
	{
		//do not re-set "nullFlavor" for leaf element
		if (element.getChildren().isEmpty())
			return;
		
		//re-set the "nullFlavor" based on its children
		boolean isAllChildrenNullFlavored=true;
		for(XMLElement child:element.getChildren())
		{
			//re-set "nullFlavor" for all children
			cleanNullFlavor(child);
			if (!isElementNullFlavored(child))
			{
				isAllChildrenNullFlavored=false;
			}
		}

		//remove "nullFlavor" attribute if not all children nullFlavored
		if (isElementNullFlavored(element)&&!isAllChildrenNullFlavored)
		{
			for (Attribute xmlAttr:element.getAttributes())
			{
				String attrName=xmlAttr.getName();
				if (attrName!=null&&attrName.equalsIgnoreCase("nullFlavor"))
				{
					Log.logInfo(element, element.getName()+" remove nullFlavore attribute:"+xmlAttr.getName());
					element.getAttributes().remove(xmlAttr);
				}
			}
		}		
	}
	
	private static boolean isElementNullFlavored(XMLElement element)
	{
		if (element.getAttributes().isEmpty())
			return false;
		
		for (Attribute xmlAttr:element.getAttributes())
		{
			String attrName=xmlAttr.getName();
			if (attrName!=null&&attrName.equalsIgnoreCase("nullFlavor"))
				return true;
		}
		
		return false;
	}
}


/**
* HISTORY: $Log: not supported by cvs2svn $
**/