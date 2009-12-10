/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.common.function.meta;

import gov.nih.nci.caadapter.common.MetaObject;

import java.util.List;
/**
 * interface GroupMeta
 * @author OWNER: $Author: phadkes $
 * @author LAST UPDATE $Author: phadkes $
 * @since      caAdapter  v4.2    
 * @version    $Revision: 1.4 $
 * @date       $Date: 2008-09-25 18:48:58 $
*/

public interface GroupMeta extends MetaObject
{

    public String getGroupName();
    public void setGroupName(String strNewGroupName);
    public List<FunctionMeta> getFunctionList();
    public void setFunctionList(List<FunctionMeta> lstNewFunctionList);

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
