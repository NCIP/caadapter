/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.common.functions;

import gov.nih.nci.caadapter.common.function.FunctionManager;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.function.meta.GroupMeta;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;

import java.util.*;

/**
 * This class manages loading the series of functional box definitions identified in the system.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.7 $
 *          date        $Date: 2008-11-21 16:18:38 $
 */
public class FunctionBoxViewManager
{
	public static final String GROUP_NAME_DELIMITER = ":";
	//key: uuid of the FunctionMeta, value: the FunctionMeta;
	private Map functionDefinitionsMap;
    private static final FunctionBoxViewManager INSTANCE = new FunctionBoxViewManager();
	private FunctionManager functionManager;

	private FunctionBoxViewManager()
	{
//		functionDefinitionsMap = Collections.synchronizedMap(new HashMap());
//		FunctionBoxMutableViewInterface concat = new FunctionBoxMutableViewInterfaceImpl(null, FUNCTION_CONCAT, 2, 1);
//		FunctionBoxMutableViewInterface split = new FunctionBoxMutableViewInterfaceImpl(null, FUNCTION_SPLIT, 2, 2);
//		FunctionBoxMutableViewInterface connect = new FunctionBoxMutableViewInterfaceImpl(null, FUNCTION_CONNECT, 1, 1);
//		functionDefinitionsMap.put(concat.getName(), concat);
//		functionDefinitionsMap.put(split.getName(), split);
//		functionDefinitionsMap.put(connect.getName(), connect);
		initialize();
	}

	/**
	 * Will reload the list of functions from scratch again.
	 */
	public void reload()
	{
		initialize();
	}

	private void initialize()
	{
		functionDefinitionsMap = Collections.synchronizedMap(new HashMap());
		functionManager = FunctionManager.getInstance();
		List<GroupMeta> groupMetaList = functionManager.getGroupList();
		int groupSize = groupMetaList==null? 0 : groupMetaList.size();
		for(int j=0; j<groupSize; j++)
		{
			GroupMeta groupMeta = groupMetaList.get(j);
			List<FunctionMeta> functionDefinitionList = functionManager.getFunctionList(groupMeta.getGroupName());
			int size = functionDefinitionList==null? 0 : functionDefinitionList.size();
			for(int i=0; i<size; i++)
			{
				FunctionMeta functionMeta = functionDefinitionList.get(i);
	//			StringBuffer keyBuf = new StringBuffer(functionMeta.getGroupName());
	//			keyBuf.append(":").append(functionMeta.getFunctionUUID());
				functionDefinitionsMap.put(functionMeta.getXmlPath(), functionMeta);
	//			FunctionBoxMutableViewInterfaceImplunctionMetaView = new FunctionBoxMutableViewInterfaceImplull, functionMeta.getName(), functionMeta.getSizeOfDefinedInput(), functionMeta.getSizeOfDefinedOutput());
	//			functionMetaView.setFunctionMeta(functionMeta);
	//			functionDefinitionsMap.put(functionMetaView.getName(), functionMetaView);
			}
		}
	}

	public static final FunctionBoxViewManager getInstance()
	{
		return INSTANCE;
	}

//	public List getAllFunctionNames()
//	{
//		return new ArrayList(functionDefinitionsMap.keySet());
//	}
//
	/**
	 * @param function the uuid of the FunctionMeta, or of type FunctionMeta,
	 *                 or of type of FunctionBoxMutableViewInterface, or of type FunctionComponent
	 * @return
	 */
	public int getTotalNumberOfDefinedInputs(Object function)
	{
		FunctionMeta funcBox = getOneFunctionalBoxSpecification(function);
		if(funcBox!=null)
		{
			return funcBox.getSizeOfDefinedInput();
		}
		else
		{
			System.err.println("Cannot find function of '" + function + "'");
		}
		return -1;
	}

	/**
	 * @param function the uuid of the FunctionMeta, or of type FunctionMeta,
	 *                 or of type of FunctionBoxMutableViewInterface, or of type FunctionComponent
	 * @return
	 */
	public int getTotalNumberOfDefinedOutputs(Object function)
	{
		FunctionMeta funcBox = getOneFunctionalBoxSpecification(function);
		if (funcBox != null)
		{
			return funcBox.getSizeOfDefinedOutput();
		}
		else
		{
			System.err.println("Cannot find function of '" + function + "'");
		}
		return -1;
	}

	/**
	 * @param function the uuid of the FunctionMeta, or of type FunctionMeta,
	 *                 or of type of FunctionBoxMutableViewInterface, or of type FunctionComponent
	 * @return
	 */
	public FunctionMeta getOneFunctionalBoxSpecification(Object function)
	{
		function = getFunctionUUID(function);
		return (FunctionMeta) functionDefinitionsMap.get(function);
	}

	private String getFunctionUUID(Object function)
	{
		String functionUUID = null;
		if (function instanceof String)
		{
			functionUUID = (String) function;
		}
		else if (function instanceof FunctionBoxMutableViewInterface)
		{
			functionUUID = ((FunctionMeta)((FunctionBoxMutableViewInterface) function).getFunctionMeta()).getXmlPath();
		}
		else if (function instanceof FunctionComponent)
		{
			FunctionMeta meta = ((FunctionComponent) function).getMeta();
			functionUUID = meta.getXmlPath();
		}
		else if (function instanceof FunctionMeta)
		{
			functionUUID = ((FunctionMeta) function).getXmlPath();
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
 * HISTORY      : Revision 1.6  2008/11/17 20:10:47  wangeug
 * HISTORY      : Move FunctionComponent and VocabularyMap from HL7 module to common module
 * HISTORY      :
 * HISTORY      : Revision 1.5  2008/06/09 19:53:51  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2008/04/01 21:43:14  umkis
 * HISTORY      : minor change
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/07/16 19:27:12  wangeug
 * HISTORY      : change UIUID to xmlPath
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/03 18:57:48  wangeug
 * HISTORY      : relocate "FunctionComponent" object from  other package
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/27 22:41:16  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/22 20:53:19  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
