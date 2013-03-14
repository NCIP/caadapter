/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */


package gov.nih.nci.cbiit.cmts.ui.function;


import gov.nih.nci.cbiit.cmts.common.FunctionManager;
import gov.nih.nci.cbiit.cmts.core.FunctionDef;
import gov.nih.nci.cbiit.cmts.core.ViewType;
import gov.nih.nci.cbiit.cmts.ui.util.GeneralUtilities;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2009-12-02 18:48:54 $
 */
public class FunctionBoxUsageManager
{

    //key: uuid of function instance, function instance.
    private Map<String, FunctionBoxGraphCell> functionBoxGraphCellMap;

    private static final FunctionBoxUsageManager instance = new FunctionBoxUsageManager();


	public static final FunctionBoxUsageManager getInstance()
	{
		return instance;
	}
	
    private FunctionBoxUsageManager()
    {
    	functionBoxGraphCellMap = Collections.synchronizedMap(new HashMap<String, FunctionBoxGraphCell>());
    }


    /**
     * This function is expected to be called by UI's action to manually add/create a function box, which will expect to ask constant definition if it is a constant function.
     * @param function the uuid of the FunctionDef, or of type FunctionDef,
     *                 or of type of FunctionBoxMutableViewInterface, or of type FunctionComponent
     * @param viewInfo
     * @param parentContainer
     * @return a FunctionBoxMutableViewInterface
     */
    public FunctionBoxGraphCell createOneFunctionBoxGraphCell(Object function, ViewType viewInfo, Container parentContainer)
    {
        FunctionDef functionDef = getOneFunctionalBoxSpecification(function);

        FunctionBoxGraphCell newFunctionBoxInstance = null;
        if (functionDef != null)
        {
            newFunctionBoxInstance = new FunctionBoxGraphCell((FunctionDef)functionDef.clone(), viewInfo);
            //register the newly created item in the map.
            functionBoxGraphCellMap.put(newFunctionBoxInstance.getFuncionBoxUUID(), newFunctionBoxInstance);
        }
        newFunctionBoxInstance.setViewMeta(viewInfo);
        return newFunctionBoxInstance;
    }

    /**
     * Return the function box cell instance based on the given instance's UUID.
     * @param functionInstanceUUID
     * @return a FunctionBoxMutableViewInterface
     */
    public FunctionBoxGraphCell findFunctionBoxGraphCell(String functionInstanceUUID)
    {
        return ((FunctionBoxGraphCell) functionBoxGraphCellMap.get(functionInstanceUUID));
    }

     public List<FunctionBoxGraphCell> findFunctionBoxGraphCellByName(String functionName)
    {
        ArrayList<FunctionBoxGraphCell> result = new ArrayList<FunctionBoxGraphCell>();
        Iterator it = functionBoxGraphCellMap.keySet().iterator();
        while(it.hasNext())
        {
            Object key = it.next();
            FunctionBoxGraphCell element = (FunctionBoxGraphCell) functionBoxGraphCellMap.get(key);
            if(GeneralUtilities.areEqual(element.getTitle(), functionName))
            {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * Return all usage functions.
     * @return a list of FunctionBoxGraphCell
     */
    public List<FunctionBoxGraphCell> getAllFunctionGraphCellList()
    {
        ArrayList<FunctionBoxGraphCell> result = new ArrayList<FunctionBoxGraphCell>();
        Iterator it = functionBoxGraphCellMap.keySet().iterator();
        while (it.hasNext())
        {
            Object key = it.next();
            FunctionBoxGraphCell element = (FunctionBoxGraphCell) functionBoxGraphCellMap.get(key);
            result.add(element);
        }
        return result;
    }

    public Object removeFunctionUsage(FunctionBoxGraphCell functionUsage)
    {
        if(functionUsage!=null)
        {
            return functionBoxGraphCellMap.remove(functionUsage.getXmlPath());
        }
        else
        {
            return null;
        }
    }
    /**
	 * @param function the uuid of the FunctionDef, or of type FunctionDef,
	 *                 or of type of FunctionBoxMutableViewInterface, or of type FunctionComponent
	 * @return
	 */
	private FunctionDef getOneFunctionalBoxSpecification(Object function)
	{
		String functionID = getFunctionUUID(function);
		return (FunctionDef) FunctionManager.getInstance().getFunctionMap().get(functionID);
	}

	private String getFunctionUUID(Object function)  
	{
		String functionUUID = null;
		if (function instanceof String)
		{
			functionUUID = (String) function;
		}
		else if (function instanceof FunctionDef)
		{
			FunctionDef fdef=(FunctionDef)function;
			functionUUID=fdef.getGroup()+":"+fdef.getName();
		}
		else
		{
			System.err.println("I don't know this type of function. Its type is '" + (function==null? "null" : function.getClass().getName()) + "'.");
		}
		return functionUUID;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2008/12/29 22:18:18  linc
 * HISTORY      : function UI added.
 * HISTORY      :
 */
