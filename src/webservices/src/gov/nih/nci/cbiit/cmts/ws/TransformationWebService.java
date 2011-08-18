/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.cbiit.cmts.ws;

import gov.nih.nci.cbiit.cmts.transform.TransformationService;
import gov.nih.nci.cbiit.cmts.transform.TransformerFactory;
import gov.nih.nci.cbiit.cmts.ws.object.ScenarioRegistration;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.xquery.XQException;

/**
 * caadapter Commong Mapping and Transformation Service module Web Service to
 * provide transformation service
 * 
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: altturbo $
 * @version $Revision: 1.15 $
 * @date $$Date: 2009-11-25 02:14:48 $
 * @since caadapter v1.3.1
 */

public class TransformationWebService {
	/**
	 * caadapter Commong Mapping and Transformation Service module Web Service
	 * to provide transformation service
	 * 
	 * @param mappingScenario
	 *            The name of the mapping scenario
	 * @param sourceDataString
	 *            source data in String format
	 * @param sourceType valid formats are: XML, CSV, HL7_v2
	 * @return A collection of the transformed XML message
	 */
	public ArrayList<String> transformationService(String mappingScenario,
			String sourceDataString, String sourceType) {

		ArrayList<String> result = new ArrayList<String>();
		System.out.println("TransformationWebService.transformationService...scenarioName:"+mappingScenario);
	    System.out.println("TransformationWebService.transformationService...sourceString:"+sourceDataString);
	    System.out.println("TransformationWebService.transformationService...sourceType:"+sourceType);
		ScenarioRegistration scenario=null;
		try {
			scenario = ScenarioUtil.findScenario(mappingScenario);
			System.out
					.println("TransformationWebService.transformationService()...found mapping scenario:"+scenario);
			if (scenario == null) {
				result.add("Scenario is not found:" + mappingScenario);
				return result;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String fileName="xmlFile"+Calendar.getInstance().getTimeInMillis();
        FileWriter fw = null;
        
        try
        {
            fw = new FileWriter(new File(fileName));
            fw.write(sourceDataString);
            fw.close();
        }
        catch(Exception ie)
        {
        	result.add("File Writing Error(" + fileName + ") : " + ie.getMessage() );
    		return result;
        }
        File tempSourceFile=new File(fileName);
        if (!tempSourceFile.exists())
        {
        	result.add("source data is empty: " + sourceDataString );
    		return result;
        }

        try {
        	String mappingFilePath=ScenarioUtil.CMTS_SCENARIO_HOME+File.separator+scenario.getName()
        	+File.separator+scenario.getMappingFile();
			result=transferData(mappingFilePath, tempSourceFile.getPath(), sourceType);
		} catch (XQException e) {
			e.printStackTrace();
			result.add("error in transfering: " + e.getMessage() );
		}
		tempSourceFile.delete();
		return result;
	}

	private ArrayList<String> transferData(String mappingFile, String sourceDataFile, String sourceDataType) throws XQException
	{
		ArrayList<String> result = new ArrayList<String>();
		TransformationService transformer =TransformerFactory.getTransformer(sourceDataType) ;
		System.out.println("TransformationWebService.transferData()...sourceFile:"+sourceDataFile);
		String xmlResult=transformer.transfer(sourceDataFile, mappingFile);
		System.out.println("TransformationWebService.transferData()...resultData:"+xmlResult);

		result.add(xmlResult);
		return result;
	}
}
