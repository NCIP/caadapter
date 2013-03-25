/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





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
