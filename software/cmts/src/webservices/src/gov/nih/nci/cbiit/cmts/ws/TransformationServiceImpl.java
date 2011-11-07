package gov.nih.nci.cbiit.cmts.ws;

import gov.nih.nci.cbiit.cmts.transform.TransformationService;
import gov.nih.nci.cbiit.cmts.transform.TransformerFactory;
import gov.nih.nci.cbiit.cmts.ws.object.ScenarioRegistration;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;

import javax.jws.WebService;
import javax.xml.xquery.XQException;

@WebService(endpointInterface = "gov.nih.nci.cbiit.cmts.ws.TransformationWebService")
public class TransformationServiceImpl implements TransformationWebService{

	@Override
	public ArrayList<String>  transformationService(String mappingScenario,
			String sourceData) {
		ArrayList<String> result = new ArrayList<String>();
		System.out.println("TransformationServiceImpl.transformationService()...scenarioName:"+mappingScenario);
		System.out.println("TransformationServiceImpl.transformationService()...sourceData:\n"+sourceData);
		ScenarioRegistration scenario=null;
		try {
			scenario = ScenarioUtil.findScenario(mappingScenario);
			if (scenario == null) {
				result.add("Scenario is not found:" + mappingScenario);
 				return result;
			}
			System.out
			.println("TransformationServiceImpl.transformationService()...found mapping scenario:"+scenario.getName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String fileName="xmlFile"+Calendar.getInstance().getTimeInMillis();
        FileWriter fw = null;
        
        try
        {
            fw = new FileWriter(new File(fileName));
            fw.write(sourceData);
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
        	result.add("source data is empty: " + sourceData );
        	return result;
        }

        try {
        	String mappingFilePath=ScenarioUtil.CMTS_SCENARIO_HOME+File.separator+scenario.getName()
        	+File.separator+scenario.getMappingFile();
			result=transferData(mappingFilePath, tempSourceFile.getPath());
		} catch (XQException e) {
			e.printStackTrace();
			result.add("error in transfering: " + e.getMessage() );
		}
		tempSourceFile.delete();
		System.out.println("TransformationServiceImpl.transformationService()..return:\n"+result);
		return result;
	}
	
	private ArrayList<String> transferData(String mappingFile, String sourceDataFile) throws XQException
	{
		ArrayList<String> result = new ArrayList<String>();
		System.out.println("TransformationServiceImpl.transferData()...looking for transformer...");
		TransformationService transformer =TransformerFactory.getTransformer("xml") ;
		String xmlResult=transformer.transfer(sourceDataFile, mappingFile);
		System.out.println("TransformationServiceImpl.transferData()...resultData:\n"+xmlResult);
		result.add(xmlResult);
		return result;
	}
}
