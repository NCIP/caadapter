/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.core;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "usageType")
@XmlEnum

public enum DataElementUsageType {
    @XmlEnumValue("parameter")
    PARAMETER("parameter"),
    @XmlEnumValue("transformation")
    TRANSFORMATION("transformation");
 
    private final String value;
    
    DataElementUsageType (String v)
    {
    	value=v;
    }
    
    public String value() {
        return value;
    }
}
