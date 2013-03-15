/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.hl7.map;

import gov.nih.nci.caadapter.common.BaseObject;
import gov.nih.nci.caadapter.common.map.BaseMapElement;

/**
 * The Map interface that basically represents a relationship between
 * two BaseMapElements (or MetaObjects).
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.2 $
 * @date        $Date: 2008-06-09 19:53:50 $
 */

public interface Map extends BaseObject{
	public BaseMapElement getTargetMapElement();
	public BaseMapElement getSourceMapElement();
}
