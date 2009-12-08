/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmps.ui.function;


import gov.nih.nci.cbiit.cmps.common.FunctionManager;
import gov.nih.nci.cbiit.cmps.core.FunctionDef;
import gov.nih.nci.cbiit.cmps.core.FunctionDef;

import java.util.*;

/**
 * This class manages loading the series of functional box definitions identified in the system.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-29 22:18:18 $
 */
public class FunctionBoxViewManager
{
	public static final String GROUP_NAME_DELIMITER = ":";
    private static final FunctionBoxViewManager instance = new FunctionBoxViewManager();


	public static final FunctionBoxViewManager getInstance()
	{
		return instance;
	}

	/**
	 * @param function the uuid of the FunctionDef, or of type FunctionDef,
	 *                 or of type of FunctionBoxMutableViewInterface, or of type FunctionComponent
	 * @return
	 */
	public int getTotalNumberOfDefinedInputs(Object function)
	{
		FunctionDef funcBox = getOneFunctionalBoxSpecification(function);
		if(funcBox!=null)
		{
			return funcBox.getData().size();
		}
		else
		{
			System.err.println("Cannot find function of '" + function + "'");
		}
		return -1;
	}

	/**
	 * @param function the uuid of the FunctionDef, or of type FunctionDef,
	 *                 or of type of FunctionBoxMutableViewInterface, or of type FunctionComponent
	 * @return
	 */
	public int getTotalNumberOfDefinedOutputs(Object function)
	{
		FunctionDef funcBox = getOneFunctionalBoxSpecification(function);
		if (funcBox != null)
		{
			return funcBox.getData().size();
		}
		else
		{
			System.err.println("Cannot find function of '" + function + "'");
		}
		return -1;
	}

	/**
	 * @param function the uuid of the FunctionDef, or of type FunctionDef,
	 *                 or of type of FunctionBoxMutableViewInterface, or of type FunctionComponent
	 * @return
	 */
	public FunctionDef getOneFunctionalBoxSpecification(Object function)
	{
		function = getFunctionUUID(function);
		return (FunctionDef) FunctionManager.getInstance().getFunctionMap().get(function);
	}

	private String getFunctionUUID(Object function)  
	{
		String functionUUID = null;
		if (function instanceof String)
		{
			functionUUID = (String) function;
		}
		else if (function instanceof FunctionBoxUserObject)
		{
//			functionUUID = ((FunctionDef)((FunctionBoxUserObject) function).getFunctionType())
		}
		else if (function instanceof FunctionDef)
		{
		}
		else
		{
			System.err.println("I don't know this type of function. Its type is '" + (function==null? "null" : function.getClass().getName()) + "'.");
		}
		return functionUUID;
	}

	private List getListWithSize(int size)
	{
		List list = new ArrayList();
		for(int i=0; i<size; i++)
		{
			list.add("Element_" + i);
		}
		return list;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
