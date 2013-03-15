/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.functions;

import gov.nih.nci.caadapter.common.function.FunctionConstant;
import gov.nih.nci.caadapter.common.function.FunctionException;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.hl7.map.FunctionVocabularyMapping;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;

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
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class FunctionBoxViewUsageManager
{
    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: FunctionBoxViewUsageManager.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/functions/FunctionBoxViewUsageManager.java,v 1.4 2008-06-09 19:53:51 phadkes Exp $";

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
     * @param functionComponent the uuid of the FunctionMeta, or of type FunctionMeta,
     *                 or of type of FunctionBoxMutableViewInterface, or of type FunctionComponent
     * @return null if the given functionComponent is null or could not find specified function within the functionComponent.
     */
    public FunctionBoxMutableViewInterface createOneFunctionalBoxMutableViewInstance(FunctionComponent functionComponent)
    {
        if(functionComponent==null)
        {
            return null;
        }

        FunctionMeta functionMetaFromComponent = functionComponent.getMeta();
        FunctionMeta functionDefinition = FunctionBoxViewManager.getInstance().getOneFunctionalBoxSpecification(functionMetaFromComponent);
        FunctionBoxMutableViewInterface newFunctionBox = null;
        if (functionDefinition != null)
        {//just to confirm the system has it.

            newFunctionBox = new FunctionBoxMutableViewInterfaceImpl(functionComponent);
            if(functionMetaFromComponent.isConstantFunction() || functionComponent.getFunctionConstant()!=null)
            {
                //System.out.println("CCCCCx : " + functionComponent.getFunctionConstant().getConstantFunctionName() + ", type="+functionComponent.getFunctionConstant().getType()+ ", value="+functionComponent.getFunctionConstant().getValue());
                newFunctionBox.setFunctionConstant(functionComponent.getFunctionConstant());
            }
            if(functionMetaFromComponent.isFunctionVocabularyMapping() || functionComponent.getFunctionVocabularyMapping()!=null)
            {
                newFunctionBox.setFunctionVocabularyMapping(functionComponent.getFunctionVocabularyMapping());
            }
            //register the newly created item in the map.
            functionInstanceMap.put(newFunctionBox.getXmlPath(), newFunctionBox);
        }
        return newFunctionBox;
    }

    /**
     * This function is expected to be called by UI's action to manually add/create a function box, which will expect to ask constant definition if it is a constant function.
     * @param function the uuid of the FunctionMeta, or of type FunctionMeta,
     *                 or of type of FunctionBoxMutableViewInterface, or of type FunctionComponent
     * @param viewInfo
     * @param parentContainer
     * @return a FunctionBoxMutableViewInterface
     */
    public FunctionBoxMutableViewInterface createOneFunctionalBoxMutableViewInstance(Object function, gov.nih.nci.caadapter.common.map.View viewInfo, Container parentContainer)
    {
        FunctionMeta functionMeta = FunctionBoxViewManager.getInstance().getOneFunctionalBoxSpecification(function);

        FunctionConstant functionConstant = null;
        FunctionVocabularyMapping vocabularyMapping = null;
        if(functionMeta.isConstantFunction())
        {//a constant function, need to ask for input values
            //following does not work. It only works for one input type only.
//			Object returnValue = JOptionPane.showInputDialog(getMiddlePanel(), new Object[]{"Type", "Value"},"Define a Constant", JOptionPane.INFORMATION_MESSAGE, null, null, new Object[]{"", ""});
//			Log.logInfo(this, "return value: '" + returnValue + "'.");
            FunctionConstantDefinitionDialog dialog = null;
            if (parentContainer instanceof Frame)
            {
                dialog = new FunctionConstantDefinitionDialog((Frame) parentContainer, functionMeta.getFunctionName());
            }
            else if (parentContainer instanceof Dialog)
            {
                dialog = new FunctionConstantDefinitionDialog((Dialog) parentContainer, functionMeta.getFunctionName());
            }
            if (dialog != null)
            {
                DefaultSettings.centerWindow(dialog);
                dialog.setVisible(true);
                if (dialog.isOkButtonClicked())
                {
                    String typeValue = "";
                    if (dialog.getConstantTypeClass() != null) typeValue = DefaultSettings.getClassNameWithoutPackage(dialog.getConstantTypeClass());
			        else typeValue = dialog.getConstantFunctionName();
                    try
                    {
                        functionConstant = new FunctionConstant(dialog.getConstantFunctionName(), typeValue, dialog.getConstantValue());
                    }
                    catch(FunctionException fe)
                    {
                        JOptionPane.showMessageDialog(parentContainer, fe.getMessage(), "Constant function Creation Failure. : " + dialog.getConstantFunctionName(), JOptionPane.WARNING_MESSAGE);
                        return null;
                    }
                }
                else
                {//adding constant was cancelled.
                    return null;
                }
            }
            else
            {//adding constant was cancelled.
                return null;
            }
        }
        if(functionMeta.isFunctionVocabularyMapping())
        {
            FunctionVocabularyMappingDefinitionDialog dialog = null;
            FunctionVocabularyMapping functionVocabularyMapping = new FunctionVocabularyMapping();
            boolean inverseTag = false;
            if(functionVocabularyMapping.getMethodNamePossibleList()[1].equalsIgnoreCase(functionMeta.getFunctionName())) inverseTag = true;
            else if(functionVocabularyMapping.getMethodNamePossibleList()[0].equalsIgnoreCase(functionMeta.getFunctionName())) inverseTag = false;
            else
            {
                JOptionPane.showMessageDialog(parentContainer, "Invalid function method name of vocabulary mapping : " + functionMeta.getFunctionName(), "Invalid function method name", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            if (parentContainer instanceof Frame)
            {
                dialog = new FunctionVocabularyMappingDefinitionDialog((Frame) parentContainer, inverseTag);
            }
            else if (parentContainer instanceof Dialog)
            {
                dialog = new FunctionVocabularyMappingDefinitionDialog((Dialog) parentContainer, inverseTag);
            }
            if (dialog != null)
            {
                DefaultSettings.centerWindow(dialog);
                dialog.setVisible(true);
                if (dialog.isOkButtonClicked())
                {
                    try
                    {
                        vocabularyMapping = new FunctionVocabularyMapping(dialog.getMappingTypeClass(), dialog.getMappingValue(), inverseTag);
                    }
                    catch(FunctionException fe)
                    {
                        JOptionPane.showMessageDialog(parentContainer, fe.getMessage(), "External FunctionException(23)", JOptionPane.WARNING_MESSAGE);
                        return null;
                    }
                }
                else
                {//adding constant was cancelled.
                    return null;
                }
            }
            else
            {//adding constant was cancelled.
                return null;
            }
        }
        FunctionBoxMutableViewInterface newFunctionBoxInstance = null;
        if (functionMeta != null)
        {
            newFunctionBoxInstance = new FunctionBoxMutableViewInterfaceImpl(functionMeta, viewInfo);
            if(functionConstant!=null)
            {//implies the function is a constant function
                newFunctionBoxInstance.setFunctionConstant(functionConstant);
            }
            if(vocabularyMapping!=null)
            {//implies the function is a constant function
                newFunctionBoxInstance.setFunctionVocabularyMapping(vocabularyMapping);
            }
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
    public FunctionBoxMutableViewInterface findFunctionUsageInstance(String functionInstanceUUID)
    {
        return ((FunctionBoxMutableViewInterface) functionInstanceMap.get(functionInstanceUUID));
    }

    /**
     * Find the function usage instance based on the given functionComponent's UUID.
     * @param functionComponent
     * @return return null if either functionComponent is null or no one is found.
     */
    public FunctionBoxMutableViewInterface findFunctionUsageInstanceByComponentUUID(FunctionComponent functionComponent)
    {
        if(functionComponent==null)
        {
            return null;
        }
        FunctionBoxMutableViewInterface result = null;
        Iterator it = functionInstanceMap.keySet().iterator();
        while(it.hasNext())
        {
            Object key = it.next();
            FunctionBoxMutableViewInterface value = (FunctionBoxMutableViewInterface) functionInstanceMap.get(key);
            FunctionComponent localComp = value.getFunctionComponent(false);
            if(localComp!=null && GeneralUtilities.areEqual(localComp.getXmlPath(), functionComponent.getXmlPath()))
            {
                result = value;
                break;
            }
        }
        return result;
    }

    public List<FunctionBoxMutableViewInterface> findFunctionUsageInstanceByName(String functionName)
    {
        ArrayList<FunctionBoxMutableViewInterface> result = new ArrayList<FunctionBoxMutableViewInterface>();
        Iterator it = functionInstanceMap.keySet().iterator();
        while(it.hasNext())
        {
            Object key = it.next();
            FunctionBoxMutableViewInterface element = (FunctionBoxMutableViewInterface) functionInstanceMap.get(key);
            if(GeneralUtilities.areEqual(element.getName(), functionName))
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
    public List<FunctionBoxMutableViewInterface> getAllFunctionUsageList()
    {
        ArrayList<FunctionBoxMutableViewInterface> result = new ArrayList<FunctionBoxMutableViewInterface>();
        Iterator it = functionInstanceMap.keySet().iterator();
        while (it.hasNext())
        {
            Object key = it.next();
            FunctionBoxMutableViewInterface element = (FunctionBoxMutableViewInterface) functionInstanceMap.get(key);
            result.add(element);
        }
        return result;
    }

    public Object removeFunctionUsage(FunctionBoxMutableViewInterface functionUsage)
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
 * HISTORY      : Revision 1.3  2007/07/16 19:27:36  wangeug
 * HISTORY      : change UIUID to xmlPath
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/03 18:58:11  wangeug
 * HISTORY      : relocate "FunctionComponent" object from  other package
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.20  2006/12/28 20:50:36  umkis
 * HISTORY      : saveValue() and readValue() in FunctionConstant
 * HISTORY      :
 * HISTORY      : Revision 1.19  2006/10/11 18:36:35  umkis
 * HISTORY      : protect inputting 'URL' type when inverse mapping.
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/10/02 18:05:08  umkis
 * HISTORY      : Vocabulary mapping function upgrade which allow to mapping through a URL and domained .vom file.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/09/06 18:58:35  umkis
 * HISTORY      : remove ".vom" into Config.VOCABULARY_MAPPING_FILE_EXTENSION
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/09/06 18:29:20  umkis
 * HISTORY      : The new implement of Vocabulary Mapping function.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/09/05 17:25:25  umkis
 * HISTORY      : The new implement of Vocabulary Mapping function.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/10/12 20:47:54  jiangsc
 * HISTORY      : GUI Enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/23 18:57:16  jiangsc
 * HISTORY      : Implemented the new Properties structure
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/22 21:35:27  jiangsc
 * HISTORY      : Changed BaseComponentFactory and other UI classes to use File instead of string name;
 * HISTORY      : Added first implementation of Function Constant;
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/22 17:39:24  jiangsc
 * HISTORY      : Persistence of Function involved mapping.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/21 17:07:48  jiangsc
 * HISTORY      : First round to implement Functions in mapping persistence.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/20 22:00:54  jiangsc
 * HISTORY      : Save point.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/19 22:28:14  jiangsc
 * HISTORY      : 1) Renamed FunctionalBox to FunctionBox to be consistent;
 * HISTORY      : 2) Added SwingWorker to OpenObjectToDbMapAction;
 * HISTORY      : 3) Save Point for Function Change.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/14 22:24:33  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 */
