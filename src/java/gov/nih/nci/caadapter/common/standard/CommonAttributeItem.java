/*
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
 
package gov.nih.nci.caadapter.common.standard;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.standard.type.CommonNodeType;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Jul 2, 2007
 *          Time:       7:40:13 PM $
 */
public interface CommonAttributeItem extends CommonNode
{
//    public void setAttributeName(String name)throws ApplicationException;
//    public String getAttributeName();
    public void setParentAttribute(CommonAttribute att) throws ApplicationException;
    public CommonAttribute getAttributeObject();
    public CommonNodeType getParentNodeType();
    public void setRequired();
    public void setOptional();
    public void setItemValue(String val);
    public String getItemValue();
    public boolean isThisRequired();
    public CommonAttributeItem createNewInstance();
    public CommonAttributeItem cloneNode(CommonAttributeItem target, CommonAttributeItem source, String newUUID, String newXPath, CommonNode newParent) throws ApplicationException;

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2008/06/06 18:54:28  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:39:24  umkis
 * HISTORY      : Basic resource programs for csv cardinality and test instance generating.
 * HISTORY      :
 */

