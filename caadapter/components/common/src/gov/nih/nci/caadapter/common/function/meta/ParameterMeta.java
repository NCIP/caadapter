/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common.function.meta;

import gov.nih.nci.caadapter.common.MetaObject;

/**
 * @version 1.0
 */
public interface ParameterMeta extends MetaObject{
	public void finalize() throws Throwable;

    public int getParameterPosition();
    public void setParameterPosition(int newParamPosition);

    public String getParameterName();
    public void setParameterName(String newParamName);

    public String getParameterType();
    public void setParameterType(String newParamType);

    public boolean isInput();
    public void setIsInput(boolean input);

    public FunctionMeta getFunctionMeta();
//    public String getParameterUUID();
//
//    public void setParameterUUID(String newParamUUID);

}
