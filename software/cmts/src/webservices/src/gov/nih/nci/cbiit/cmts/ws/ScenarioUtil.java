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
        if (scenarioName == null) return null;
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
    public static void updateScenarioRegistrations() throws Exception
	{
		regList = null;
		initRepository();
	}
    public static void deleteOneScenarioRegistration(String scenarioName) throws Exception
	{
		if (regList==null)
			initRepository();

		if (regList==null||regList.isEmpty()) return;
        if (scenarioName == null) return;

        for (ScenarioRegistration scenario:regList)
		{
			if (scenario.getName().equalsIgnoreCase(scenarioName.trim()))
            {
                regList.remove(scenario);
                break;
            }
        }
	}
	private static void initRepository()throws Exception
	{

		File scnHomeFolder = (new File(CMTS_SCENARIO_HOME));
		System.out.println("ScenarioUtil.initRepository()..initialize repository");
		if (!scnHomeFolder.exists())
		{
			System.out.println("ScenarioUtil.initRepository()...Web Service Registration System is down, please try it later!");

			return;
		}
		regList=new ArrayList<ScenarioRegistration>();
		for (File childFile:scnHomeFolder.listFiles())
		{
			String scnName=childFile.getName();
			ScenarioRegistration oneReg=loadScenarioRegistration(CMTS_SCENARIO_HOME+"/"+ scnName, null);
			oneReg.setName(scnName);
			regList.add(oneReg);
		}
 	}

	public static void addNewScenarioRegistration(String scnName, String transferType) throws ParserConfigurationException, SAXException, IOException
	{
		System.out.println("ScenarioUtil.addNewScenarioRegistration()..add scenario into respository:"+scnName);
		String scnPath=CMTS_SCENARIO_HOME+"/"+scnName;
		ScenarioRegistration oneReg=loadScenarioRegistration(scnPath, transferType);
		oneReg.setName(scnName);
		if (regList!=null)
		{
			regList.add(oneReg);
			return;
		}
		//the latest scenario information has been saved into repository
		//just load it from XML data repository
		try {
			ScenarioUtil.retrieveScenarioRegistrations();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static ScenarioRegistration loadScenarioRegistration(String scenarioPath, String scenarioType) throws ParserConfigurationException, SAXException, IOException
	{
		ScenarioRegistration oneReg=new ScenarioRegistration();
		oneReg.setTransferType(scenarioType);
		File scenarioFolder=new File(scenarioPath);
		if (!scenarioFolder.exists())
		{
			oneReg.setName(scenarioPath+" is invalid");
			return oneReg;
		}
		oneReg.setName(scenarioPath);
		//empty scenario
		if (!scenarioFolder.isDirectory())
			return oneReg;
		long lastMdTime=scenarioFolder.lastModified();
		Date mdDate=new Date(lastMdTime);
		oneReg.setDateCreate(mdDate);

        String sourceSpecFileName = "";
        String targetSpecFileName = "";
        List<File> subDirs = null;
        for (File childFile:scenarioFolder.listFiles())
		{
			String chldFileName=childFile.getName();
            if (childFile.isFile())
            {
                if (chldFileName.endsWith(".map"))
                {
                    oneReg.setMappingFile(chldFileName);
                    oneReg.setTransferType("map");
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
                        {
                            String val = locationAttr.getValue();
                            oneReg.setSourceSpecFile(val);
                            sourceSpecFileName = val;
//                            int idx = val.lastIndexOf("/");
//                            if (idx < 0) idx = val.lastIndexOf("\\");
//                            if (idx < 0) sourceSpecFileName = val;
//                            else  sourceSpecFileName = val.substring(idx + 1);
                        }

                        else if (typeAttr.getValue().equals("target"))
                        {
                            String val = locationAttr.getValue();
                            oneReg.setTargetFile(val);
                            targetSpecFileName = val;
//                            int idx = val.lastIndexOf("/");
//                            if (idx < 0) idx = val.lastIndexOf("\\");
//                            if (idx < 0) targetSpecFileName = val;
//                            else targetSpecFileName = val.substring(idx + 1);
                        }

                    }
                }
                else if ((chldFileName.toLowerCase().endsWith(".xsl"))||(chldFileName.toLowerCase().endsWith(".xslt")))
                {
                    oneReg.setMappingFile(chldFileName);
                    oneReg.setTransferType("xsl");
                }
                else if ((chldFileName.toLowerCase().endsWith(".xq"))||
                         (chldFileName.toLowerCase().endsWith(".xql"))||
                         (chldFileName.toLowerCase().endsWith(".xquery")))
                {
                    oneReg.setMappingFile(chldFileName);
                    oneReg.setTransferType("xql");
                }
            }
            else if (childFile.isDirectory())
            {
                if (subDirs == null) subDirs = new ArrayList<File>();
                subDirs.add(childFile);

            }
        }
        if (subDirs != null)
        {
            for(File childFile:subDirs)
            {
                String chldFileName = childFile.getName();
                if (chldFileName.equalsIgnoreCase("source"))
                {
                    for (File childSource:childFile.listFiles())
                    {
                        if (!childSource.isFile()) continue;
                        String sname = childSource.getName();
                        if (sname.toLowerCase().endsWith(".bak")) continue;
                        if (sname.equalsIgnoreCase(sourceSpecFileName)) continue;
                        if (sourceSpecFileName.toLowerCase().endsWith("/" + sname.toLowerCase())) continue;
                        if (sourceSpecFileName.toLowerCase().endsWith("\\" + sname.toLowerCase())) continue;
                        oneReg.addSubSourceSpecFiles(chldFileName + "\\" + sname);
                    }

                }
                else if (chldFileName.equalsIgnoreCase("target"))
                {
                    for (File childTarget:childFile.listFiles())
                    {
                        if (!childTarget.isFile()) continue;
                        String tname = childTarget.getName();
                        if (tname.toLowerCase().endsWith(".bak")) continue;
                        if (tname.equalsIgnoreCase(targetSpecFileName)) continue;
                        if (targetSpecFileName.toLowerCase().endsWith("/" + tname.toLowerCase())) continue;
                        if (targetSpecFileName.toLowerCase().endsWith("\\" + tname.toLowerCase())) continue;
                        oneReg.addSubTargetSpecFiles(chldFileName + "\\" + tname);
                    }
                }
            }
        }

		return oneReg;
	}

}


/**
* HISTORY: $Log: not supported by cvs2svn $
**/