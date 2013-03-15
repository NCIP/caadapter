/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common.function.meta.impl;

import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.function.meta.ParameterMeta;

/**
 * The implementation class of ParameterMeta interface.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:49 $
 */
public class ParameterMetaImpl extends MetaObjectImpl implements ParameterMeta
{
    //since super classes have uuid and name defined, no need to define anymore.
//	private String uuid;
//	private String name;

	private int position;
	private String typeClass;
	private boolean isInput;
    private FunctionMeta functionMeta;

	// constructors.
    public ParameterMetaImpl(){

	}
    public ParameterMetaImpl(FunctionMeta functionMeta) {
        this.functionMeta = functionMeta;
    }

	public void finalize() throws Throwable
	{

	}

    // setters and getters.
	public int getParameterPosition()
	{
		return position;
	}

	public void setParameterPosition(int newParamPosition)
	{
		this.position = newParamPosition;
	}

	public String getParameterName()
	{
		return name;
	}

	public void setParameterName(String newParamName)
	{
		this.name = newParamName;
	}

	public String getParameterType()
	{
		return typeClass;
	}

	public void setParameterType(String newParamType)
	{
		this.typeClass = newParamType;
	}

	public boolean isInput()
	{
		return isInput;
	}

	public void setIsInput(boolean input)
	{
		isInput = input;
	}

    public FunctionMeta getFunctionMeta() {
        return functionMeta;
    }

    public void setFunctionMeta(FunctionMeta functionMeta) {
        this.functionMeta = functionMeta;
    }

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:02:37  wangeug
 * HISTORY      : initial loading of common module
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/08/02 18:44:20  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 18:56:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/07/27 23:31:41  giordanm
 * HISTORY      : trying to get function.compute() working within the mapprocessor - I'm not there quite yet.  (some minor house cleaning stuff too)
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/19 15:57:03  jiangsc
 * HISTORY      : removed duplicate definition of uuid and name and added comments for license etc.
 * HISTORY      :
 */
