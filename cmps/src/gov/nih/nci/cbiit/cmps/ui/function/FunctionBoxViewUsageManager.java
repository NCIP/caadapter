/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */


package gov.nih.nci.cbiit.cmps.ui.function;


import gov.nih.nci.cbiit.cmps.core.FunctionDef;
import gov.nih.nci.cbiit.cmps.core.ViewType;
import gov.nih.nci.cbiit.cmps.function.FunctionException;
import gov.nih.nci.cbiit.cmps.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmps.ui.util.GeneralUtilities;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class may look similar to FunctionalBoxManager, but it is solely designated
 * to support MiddlePanelJGraphController to manage the list of function box usages.
 *
 * Therefore, the implementation of FunctionalBoxManager is singleton while this class is
 * instance oriented, as each instance targets to one instance of mapping.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-29 22:18:18 $
 */
public class FunctionBoxViewUsageManager
{

    //key: uuid of function instance, function instance.
    private Map functionInstanceMap;

    public FunctionBoxViewUsageManager()
    {
        clear();
    }

    public void clear()
    {
        this.functionInstanceMap = Collections.synchronizedMap(new HashMap());
    }

    /**
     * This function is expected to be called by UI when it is loading a map file to display and tries to convert a functionComponent to an instance of GUI component.
     * This function will handle constant function as well.
     * @param functionComponent the uuid of the FunctionDef, or of type FunctionDef,
     *                 or of type of FunctionBoxMutableViewInterface, or of type FunctionComponent
     * @return null if the given functionComponent is null or could not find specified function within the functionComponent.
     */
    public FunctionBoxUserObject createOneFunctionBoxUserObject(FunctionDef function, ViewType view)
    {
        
        FunctionDef functionDefinition = FunctionBoxViewManager.getInstance().getOneFunctionalBoxSpecification(function);
        FunctionBoxUserObject newFunctionBox = null;
        if (functionDefinition != null)
        {//just to confirm the system has it.

            newFunctionBox = new FunctionBoxUserObject(function, view);
            //register the newly created item in the map.
            functionInstanceMap.put(newFunctionBox.getXmlPath(), newFunctionBox);
        }
        return newFunctionBox;
    }

    /**
     * This function is expected to be called by UI's action to manually add/create a function box, which will expect to ask constant definition if it is a constant function.
     * @param function the uuid of the FunctionDef, or of type FunctionDef,
     *                 or of type of FunctionBoxMutableViewInterface, or of type FunctionComponent
     * @param viewInfo
     * @param parentContainer
     * @return a FunctionBoxMutableViewInterface
     */
    public FunctionBoxUserObject createOneFunctionBoxUserObject(Object function, ViewType viewInfo, Container parentContainer)
    {
        FunctionDef FunctionDef = FunctionBoxViewManager.getInstance().getOneFunctionalBoxSpecification(function);

        FunctionBoxUserObject newFunctionBoxInstance = null;
        if (FunctionDef != null)
        {
            newFunctionBoxInstance = new FunctionBoxUserObject(FunctionDef, viewInfo);
            //register the newly created item in the map.
            functionInstanceMap.put(newFunctionBoxInstance.getXmlPath(), newFunctionBoxInstance);
        }
        return newFunctionBoxInstance;
    }

    /**
     * Return the function usage instance based on the given instance's UUID.
     * @param functionInstanceUUID
     * @return a FunctionBoxMutableViewInterface
     */
    public FunctionBoxUserObject findFunctionUsageInstance(String functionInstanceUUID)
    {
        return ((FunctionBoxUserObject) functionInstanceMap.get(functionInstanceUUID));
    }

     public List<FunctionBoxUserObject> findFunctionUsageInstanceByName(String functionName)
    {
        ArrayList<FunctionBoxUserObject> result = new ArrayList<FunctionBoxUserObject>();
        Iterator it = functionInstanceMap.keySet().iterator();
        while(it.hasNext())
        {
            Object key = it.next();
            FunctionBoxUserObject element = (FunctionBoxUserObject) functionInstanceMap.get(key);
            if(GeneralUtilities.areEqual(element.getTitle(), functionName))
            {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * Return all usage functions.
     * @return a list of FunctionBoxMutableViewInterface
     */
    public List<FunctionBoxUserObject> getAllFunctionUsageList()
    {
        ArrayList<FunctionBoxUserObject> result = new ArrayList<FunctionBoxUserObject>();
        Iterator it = functionInstanceMap.keySet().iterator();
        while (it.hasNext())
        {
            Object key = it.next();
            FunctionBoxUserObject element = (FunctionBoxUserObject) functionInstanceMap.get(key);
            result.add(element);
        }
        return result;
    }

    public Object removeFunctionUsage(FunctionBoxUserObject functionUsage)
    {
        if(functionUsage!=null)
        {
            return functionInstanceMap.remove(functionUsage.getXmlPath());
        }
        else
        {
            return null;
        }
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
