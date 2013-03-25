/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.hl7.map.impl;

import gov.nih.nci.caadapter.common.BaseObjectImpl;
import gov.nih.nci.caadapter.common.map.BaseMapElement;
import gov.nih.nci.caadapter.hl7.map.Map;

/**
 * The Map implementation that basically represents a relationship between
 * two BaseMapElements (or MetaObjects).
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.2 $
 * @date        $Date: 2008-06-09 19:53:50 $
 */
public class MapImpl extends BaseObjectImpl implements Map {
    private static final String LOGID = "$RCSfile: MapImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/impl/MapImpl.java,v 1.2 2008-06-09 19:53:50 phadkes Exp $";

	private BaseMapElement targetMapElement;
	private BaseMapElement sourceMapElement;

	public MapImpl(){
	}

    public BaseMapElement getTargetMapElement() {
        return targetMapElement;
    }

    public void setTargetMapElement(BaseMapElement targetMapElement) {
        this.targetMapElement = targetMapElement;
    }

    public BaseMapElement getSourceMapElement() {
        return sourceMapElement;
    }

    public void setSourceMapElement(BaseMapElement sourceMapElement) {
        this.sourceMapElement = sourceMapElement;
    }

}
