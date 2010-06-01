/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.hl7.map;

import gov.nih.nci.caadapter.common.BaseObject;
import gov.nih.nci.caadapter.common.map.BaseMapElement;

/**
 * The Map interface that basically represents a relationship between
 * two BaseMapElements (or MetaObjects).
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: wangeug $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.3 $
 * @date        $Date: 2008-09-29 15:47:18 $
 */

public interface Map extends BaseObject{
	public BaseMapElement getTargetMapElement();
	public BaseMapElement getSourceMapElement();
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 */