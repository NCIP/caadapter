/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common;


/**
 * Base interface for all meta objects (CVS meta, HL7 V3 meta, etc).
 *
 * @author OWNER: Eric Chen
 * @author LAST UPDATE: $Author: phadkes $
 * @version $Revision: 1.2 $
 * @date $Date: 2008-06-09 19:53:49 $
 * @since caAdapter v1.2
 */

public interface MetaObject extends BaseObject
{

    /**
     * @return String
     */

    public String getName();

    /**
     * @param name
     */

    public void setName(String name);

}
