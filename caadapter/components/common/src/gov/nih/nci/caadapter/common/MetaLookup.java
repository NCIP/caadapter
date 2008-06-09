/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.common;


import java.util.Set;

/**
 * Interface that provides quick access for looking up metadata.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE: $Author: phadkes $
 * @version $Revision: 1.2 $
 * @date $$Date: 2008-06-09 19:53:49 $
 * @since caAdapter v1.2
 */

public interface MetaLookup
{
	public MetaObject lookup(String uuid);
	public Set getAllKeys();
}
