/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.common;


import gov.nih.nci.cbiit.cmts.core.FunctionData;
import gov.nih.nci.cbiit.cmts.core.FunctionDef;
import gov.nih.nci.cbiit.cmts.core.FunctionMeta;
import gov.nih.nci.cbiit.cmts.core.FunctionType;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

/**
 * This is the controller class of Middle Panel JGraph implementation.
 * <p/>
 * The MiddlePanelJGraphController class will deal with real implementation of some of actions
 * to modify (mainly CRUD) upon graph, and mainly focuses on drag-and-drop and handlings of repaint of graph, for example.
 * MiddlePanelMarqueeHandler will help handle key and mouse driven events such as display pop menus, etc.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-29 22:18:18 $
 */
public class FunctionManager
{
	private static final FunctionManager instance = new FunctionManager();
	private List<FunctionDef> lstFunctionType = null;   
	private Map<String,FunctionDef> functionMap = null;
	private Map<String,List<FunctionDef>> lstGroupMeta = null;

	public static final FunctionManager getInstance()
	{
		return instance;
	}

	//Build the FunctionTypeImpl object
	private FunctionManager()
	{
		try {
            String funcLoc = DefaultSettings.FUNCTION_DEFINITION_FILE_LOCATION;
            URL u = FileUtil.getResource(funcLoc);
            //if ((u == null)&&(funcLoc.startsWith("etc/"))) u = FileUtil.getResource(funcLoc.substring(4));
            InputStream in = null;
			if(u!=null) in = u.openStream();
			else in = new FileInputStream(new File(DefaultSettings.FUNCTION_DEFINITION_FILE_LOCATION));
			FunctionMeta meta = parseFunctionMeta(in);
			lstFunctionType = meta.getFunction();
			lstGroupMeta = new HashMap<String,List<FunctionDef>>();
			functionMap = new HashMap<String,FunctionDef>();
			for(FunctionDef i:lstFunctionType){
				List<FunctionDef> l = lstGroupMeta.get(i.getGroup());
				if(l == null){
					l = new ArrayList<FunctionDef>();
					lstGroupMeta.put(i.getGroup(), l);
				}
				l.add(i);
				functionMap.put(i.getGroup()+":"+i.getName(), i);
			}
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static FunctionMeta parseFunctionMeta(InputStream in) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmts.core" );
		Unmarshaller u = jc.createUnmarshaller();
		JAXBElement<FunctionMeta> m = u.unmarshal(new StreamSource(in), FunctionMeta.class);
		return  m.getValue();
	}

	/**
	 * Returns a GroupMeta Object containing a complete set of Function Groups.
	 *
	 * @return A GroupMeta object list
	 */

	public Map<String,List<FunctionDef>> getGroupList()
	{
		return lstGroupMeta;
	}

	/**
	 * @return the functionMap
	 */
	public Map<String, FunctionDef> getFunctionMap() {
		return functionMap;
	}

	/**
	 * Returns a GroupMeta Object containing a Function Group that matches the strGroupSearchName.
	 *
	 * @return A GroupMeta object
	 */

	public List<FunctionDef> getGroupByName(String strGroupSearchName)
	{
		return lstGroupMeta.get(strGroupSearchName);
	}



	public FunctionDef getFunctionType(String strGroup, String strFunction) //throws FunctionException
	{
		return functionMap.get(strGroup+":"+strFunction);
	}



	/**
	 * Get a FunctionType list based on a group name.
	 *
	 * @param strGroupSearchName Group Name to search
	 * @return A FunctionType object
	 */

	//Get the FunctionTypeImpl object containing a list of function definitions.
	public List<FunctionDef> getFunctionList(String strGroupSearchName)
	{
		return lstGroupMeta.get(strGroupSearchName);
	}

	//Get the FunctionTypeImpl object containing a set of function definitions with the same name.
	public List<FunctionDef> getFunctionByName(String strFunctionSearchName)
	{
		List<FunctionDef> ret = new ArrayList<FunctionDef>();
		for(FunctionDef i:lstFunctionType){
			if(i.getName().equals(strFunctionSearchName))
				ret.add(i);
		}
		return ret;
	}


	public static List<FunctionData> getFunctionMappingInputList(FunctionType fmtSelectFunctionType)
	{
		List<FunctionData> lstInputFunctionData = null;  //Stores a list of input FunctionData objects.
		lstInputFunctionData = fmtSelectFunctionType.getData();  //Get the inputlist from a FunctionTypeImpl object.
		return lstInputFunctionData;
	}


	public List<FunctionData> getFunctionMappingOutputList(FunctionType fmtSelectFunctionType)
	{
		List<FunctionData> lstOutputFunctionData = null;
		lstOutputFunctionData = fmtSelectFunctionType.getData() ;  //Get the outputlist from a FunctionTypeImpl object.
		return lstOutputFunctionData;
	}

	//getNumberofInputs
	//getNumberofOutputs

	//Checks the list of user input and output and compares it against the function specification file.
	public boolean isFunctionMappingComplete(FunctionType FunctionType, List userInputList, List userOutputList)
	{
		//Make separate calls to isFunctionMappingInputComplete and isFunctionMappingOuputComplete
		return isFunctionMappingInputComplete(FunctionType, userInputList) && isFunctionMappingOutputComplete(FunctionType, userOutputList);
	}

	/**
	 * Return true if the user's inputs complete the function required inputs.
	 * @param FunctionType
	 * @param userInputList
	 * @return
	 */
	public boolean isFunctionMappingInputComplete (FunctionType FunctionType, List userInputList)
	{
		List<FunctionData> listFunctionData = FunctionType.getData();
		return isListSizeEquals(listFunctionData, userInputList);
	}


	/**
	 * Return true if the user's outputs complete the function required outputs.
	 * @param FunctionType
	 * @param userOutputList
	 * @return
	 */
	public boolean isFunctionMappingOutputComplete (FunctionType FunctionType, List userOutputList)
	{
		List<FunctionData> listFunctionData = FunctionType.getData();
		return isListSizeEquals(listFunctionData, userOutputList);
	}

	private boolean isListSizeEquals(List defList, List userList)
	{
		int defListSize = defList==null ? 0 : defList.size();
		int userListSize = userList==null ? 0 : userList.size();
		return defListSize==userListSize;
	}

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
