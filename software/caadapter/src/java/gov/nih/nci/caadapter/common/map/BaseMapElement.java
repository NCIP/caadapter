/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.common.map;
import gov.nih.nci.caadapter.common.MetaObject;

/**
 * Half of a map, represents an object that has been mapped to or from.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.5 $
 * @date        $Date: 2008-09-25 19:15:46 $
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

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
