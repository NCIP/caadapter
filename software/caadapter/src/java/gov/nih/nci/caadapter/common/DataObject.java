/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.common;


/**
 * An interface for simple DataObjects.
 *
 * @author OWNER: Eric Chen  Date: Jun 2, 2005
 * @author LAST UPDATE: $Author: phadkes $
 * @version $Revision: 1.3 $
 * @date $$Date: 2008-09-24 19:51:48 $
 * @since caAdapter v1.2
 */

public interface DataObject extends BaseObject {
    public String getName();
    public MetaObject getMetaObject();
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
