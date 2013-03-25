/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ws;

import gov.nih.nci.caadapter.ws.object.ScenarioRegistration;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Apr 2, 2009
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-04-13 15:25:25 $
 * @since caAdapter v4.2
 */

public class ScenarioUtil {
	public static final String SCENARIO_HOME="ScenarioRegistration";
	private static ArrayList<ScenarioRegistration> regList;

	public static ScenarioRegistration findScenario(String scenarioName) throws Exception
	{
		if (regList==null)
			initRepository();

		if (regList==null||regList.isEmpty())
			return null;
		for (ScenarioRegistration scenario:regList)
		{
			if (scenario.getName().equalsIgnoreCase(scenarioName.trim()))
				return scenario;
		}
		return null;
	}
	public static List<ScenarioRegistration> retrieveScenarioRegistrations() throws Exception
	{
		if (regList==null)
			initRepository();
		return regList;
	}

	private static void initRepository()throws Exception
	{

		File scnHomeFolder = (new File(SCENARIO_HOME));
		System.out.println("ScenarioUtil.initRepository()..initialize repository");
		if (!scnHomeFolder.exists())
			throw new Exception ("Web Service Registration System is down, please try it later!");
		regList=new ArrayList<ScenarioRegistration>();
		for (File childFile:scnHomeFolder.listFiles())
		{
			String scnName=childFile.getName();
			ScenarioRegistration oneReg=loadScenarioRegistration(SCENARIO_HOME+"/"+ scnName);
			oneReg.setName(scnName);
			regList.add(oneReg);
		}
 	}

	public static void addNewScenarioRegistration(String scnName)
	{
		System.out.println("ScenarioUtil.addNewScenarioRegistration()..add scenario into respository:"+scnName);
		String scnPath=SCENARIO_HOME+"/"+scnName;
		ScenarioRegistration oneReg=loadScenarioRegistration(scnPath);
		oneReg.setName(scnName);
		try {
			ScenarioUtil.retrieveScenarioRegistrations().add(oneReg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static ScenarioRegistration loadScenarioRegistration(String scenarioName)
	{
		ScenarioRegistration oneReg=new ScenarioRegistration();
		File scenarioFolder=new File(scenarioName);
		if (!scenarioFolder.exists())
		{
			oneReg.setName(scenarioName+" is invalid");
			return oneReg;
		}
		oneReg.setName(scenarioName);
		//empty scenario
		if (!scenarioFolder.isDirectory())
			return oneReg;
		long lastMdTime=scenarioFolder.lastModified();
		Date mdDate=new Date(lastMdTime);
		oneReg.setDateCreate(mdDate);
		for (File childFile:scenarioFolder.listFiles())
		{
			String chldFileName=childFile.getName();
			if (chldFileName.endsWith(".h3s")
					||chldFileName.endsWith(".H3S"))
				oneReg.setTargetFile(chldFileName);
			else if (chldFileName.endsWith(".map")
					||chldFileName.endsWith(".map"))
				oneReg.setMappingFile(chldFileName);

			else if (chldFileName.endsWith(".scs")
					||chldFileName.endsWith(".SCS"))
				oneReg.setSourceSpecFile(chldFileName);

			else if (chldFileName.endsWith(".vom")
					||chldFileName.endsWith(".VOM"))
				oneReg.addVocabuaryMappingFile(chldFileName);

		}

		return oneReg;
	}

}


/**
* HISTORY: $Log: not supported by cvs2svn $
**/