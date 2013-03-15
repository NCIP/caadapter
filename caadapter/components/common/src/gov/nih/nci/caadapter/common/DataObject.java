/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common;


/**
 * An interface for simple DataObjects.
 *
 * @author OWNER: Eric Chen  Date: Jun 2, 2005
 * @author LAST UPDATE: $Author: phadkes $
 * @version $Revision: 1.2 $
 * @date $$Date: 2008-06-09 19:53:49 $
 * @since caAdapter v1.2
 */

public interface DataObject extends BaseObject {
    public String getName();
    public MetaObject getMetaObject();
}
