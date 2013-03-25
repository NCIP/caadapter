/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common.map;
import gov.nih.nci.caadapter.common.MetaObject;

/**
 * Half of a map, represents an object that has been mapped to or from.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.4 $
 * @date        $Date: 2008-06-09 19:53:49 $
 */
public interface BaseMapElement
{
	public String getDataXmlPath();
	public BaseComponent getComponent();
//	public String getComponentuuid();
	public MetaObject getMetaObject();
	public String getXmlPath();

	public void setXmlPath(String datatypeBaseObject);
    public boolean isSource();
    public boolean isTarget();

	/**
	 * This utility method will answer if the contained component is of source type.
	 * @return
	 */
    public boolean isComponentOfSourceType();
	/**
	 * This utility method will answer if the contained component is of target type.
	 * @return
	 */
    public boolean isComponentOfTargetType();
	/**
	 * This utility method will answer if the contained component is of function type.
	 * @return
	 */
    public boolean isComponentOfFunctionType();
}
