/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location:
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.cbiit.cmts.ws;

import gov.nih.nci.cbiit.cmts.ws.object.ScenarioRegistration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
	public static final String CMTS_SCENARIO_HOME="cmtsScenarioRegistration";
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

		File scnHomeFolder = (new File(CMTS_SCENARIO_HOME));
		System.out.println("ScenarioUtil.initRepository()..initialize repository");
		if (!scnHomeFolder.exists())
			throw new Exception ("Web Service Registration System is down, please try it later!");
		regList=new ArrayList<ScenarioRegistration>();
		for (File childFile:scnHomeFolder.listFiles())
		{
			String scnName=childFile.getName();
			ScenarioRegistration oneReg=loadScenarioRegistration(CMTS_SCENARIO_HOME+"/"+ scnName);
			oneReg.setName(scnName);
			regList.add(oneReg);
		}
 	}

	public static void addNewScenarioRegistration(String scnName) throws ParserConfigurationException, SAXException, IOException
	{
		System.out.println("ScenarioUtil.addNewScenarioRegistration()..add scenario into respository:"+scnName);
		String scnPath=CMTS_SCENARIO_HOME+"/"+scnName;
		ScenarioRegistration oneReg=loadScenarioRegistration(scnPath);
		oneReg.setName(scnName);
		try {
			ScenarioUtil.retrieveScenarioRegistrations().add(oneReg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static ScenarioRegistration loadScenarioRegistration(String scenarioName) throws ParserConfigurationException, SAXException, IOException
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
			if (chldFileName.endsWith(".map")
					||chldFileName.endsWith(".map"))
			{
				oneReg.setMappingFile(chldFileName);
				System.out.println("ScenarioUtil.loadScenarioRegistration()..mapping:"+chldFileName);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                //dbf.setValidating(true);
                DocumentBuilder db = dbf.newDocumentBuilder();

                Document xmlDOM = db.parse(childFile);
		    	NodeList components =xmlDOM.getElementsByTagName("component");
		    	for(int i = 0; i< components.getLength();i++) {
		    		Element component = (Element)components.item(i);
		    		//update location of SCS, H3S
		    		Attr locationAttr = component.getAttributeNode("location");
		    		Attr typeAttr = component.getAttributeNode("type");
		    		if (typeAttr.getValue().equals("source"))
		    			oneReg.setSourceSpecFile(locationAttr.getValue());
		    		else if (typeAttr.getValue().equals("target"))
		    			oneReg.setTargetFile(locationAttr.getValue());
		    	}
			}
		}

		return oneReg;
	}

}


/**
* HISTORY: $Log: not supported by cvs2svn $
**/