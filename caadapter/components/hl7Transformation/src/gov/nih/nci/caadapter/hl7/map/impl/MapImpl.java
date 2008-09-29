/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
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
 * @author LAST UPDATE $Author: wangeug $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.3 $
 * @date        $Date: 2008-09-29 15:45:56 $
 */
public class MapImpl extends BaseObjectImpl implements Map {
    private static final String LOGID = "$RCSfile: MapImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/impl/MapImpl.java,v 1.3 2008-09-29 15:45:56 wangeug Exp $";

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
/**
 * HISTORY :$Log: not supported by cvs2svn $
 */