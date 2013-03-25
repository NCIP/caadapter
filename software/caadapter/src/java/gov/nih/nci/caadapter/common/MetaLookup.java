/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */
package gov.nih.nci.caadapter.common;


import java.util.Set;

/**
 * Interface that provides quick access for looking up metadata.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE: $Author: phadkes $
 * @version $Revision: 1.3 $
 * @date $$Date: 2008-09-24 19:51:48 $
 * @since caAdapter v1.2
 */

public interface MetaLookup
{
	public MetaObject lookup(String uuid);
	public Set getAllKeys();
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
