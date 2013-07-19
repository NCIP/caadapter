package gov.nih.nci.cbiit.cmts.ws;

import gov.nih.nci.cbiit.cmts.transform.TransformationService;
import gov.nih.nci.cbiit.cmts.transform.TransformerFactory;
import gov.nih.nci.cbiit.cmts.ws.object.ScenarioRegistration;
import gov.nih.nci.cbiit.cmts.util.FileUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import javax.jws.WebService;
import javax.xml.xquery.XQException;

@WebService(endpointInterface = "gov.nih.nci.cbiit.cmts.ws.TransformationWebService")
public class TransformationServiceImpl implements TransformationWebService{

	@Override
	public ArrayList<String>  transferData(String mappingScenario,
			String sourceData) {
		ArrayList<String> result = new ArrayList<String>();

        try
        {
            mappingScenario = extractCodeBaseFromScenario(mappingScenario);
        }
        catch(Exception ee)
        {
            result.add(ee.getMessage());
        	return result;
        }

        File tempSourceFile=prepareSourceData(sourceData, result);
        if (!tempSourceFile.exists())
        {
        	result.add("source data is empty: " + sourceData );
        	return result;
        }
        processMappingAndSource(mappingScenario,tempSourceFile,result);

		tempSourceFile.delete();
//		System.out.println("CCCCC 881 TransformationServiceImpl.transferData()..: scenarioName="+mappingScenario);
//        System.out.println("       82 TransformationServiceImpl.transferData()..: sourceData="+sourceData);
//        System.out.println("       83 TransformationServiceImpl.transferData()..: tempSourceFile="+tempSourceFile.getAbsolutePath());
//
//        System.out.println("       84 TransformationServiceImpl.transferData()..: Result="+result);
        return result;
	}
	
	@Override
	public ArrayList<String> transferResource(String mappingScenario,
			String sourceResource) {
		ArrayList<String> result = new ArrayList<String>();
        try
        {
            mappingScenario = extractCodeBaseFromScenario(mappingScenario);
        }
        catch(Exception ee)
        {
            result.add(ee.getMessage());
        	return result;
        }

        File tempSourceFile=prepareSourceDataFromResource(sourceResource, result);
        if (!tempSourceFile.exists())
        {
        	result.add("source data is not available: " + sourceResource );
        	return result;
        }
        processMappingAndSource(mappingScenario,tempSourceFile,result);

		tempSourceFile.delete();
//        System.out.println("CCCCC 891 TransformationServiceImpl.transferResource()..: scenarioName="+mappingScenario);
//        System.out.println("       92 TransformationServiceImpl.transferResource()..: sourceData="+sourceResource);
//        System.out.println("       93 TransformationServiceImpl.transferResource()..: tempSourceFile="+tempSourceFile.getAbsolutePath());
//
//        System.out.println("       94 TransformationServiceImpl.transferResource()..: Result="+result);

        //System.out.println("TransformationServiceImpl.transferResource()..return:\n"+result);
		return result;
	}
	
	private void processMappingAndSource(String mappingScenario, File source, ArrayList<String> result)
	{
		//System.out.println("TransformationServiceImpl.processMappingAndSource()...scenarioName:"+mappingScenario);
		//System.out.println("TransformationServiceImpl.processMappingAndSource()...sourceData:\n"+source);
		ScenarioRegistration scenario=null;
		try {
			scenario = ScenarioUtil.findScenario(mappingScenario);
			if (scenario == null) {
				result.add("Scenario is not found:" + mappingScenario);
 				return ;
			}
			//System.out.println("TransformationServiceImpl.processMappingAndSource()...found mapping scenario:"+scenario.getName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.add("mapping error: " + e.getMessage() );
		}

        try {
        	String mappingFilePath=ScenarioUtil.CMTS_SCENARIO_HOME+File.separator+scenario.getName()
        	+File.separator+scenario.getMappingFile();
//			result=convertData(mappingFilePath, source.getPath());
			TransformationService transformer =TransformerFactory.getTransformer("."+scenario.getTransferType()) ;
			String xmlResult=transformer.transfer(source.getPath(), mappingFilePath);
			result.add(xmlResult);
		} catch (XQException e) {
			e.printStackTrace();
			result.add("transformation error: " + e.getMessage() );
		}
	}
	private File prepareSourceData(String sourceDataString, ArrayList<String> message)
	{
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
        	message.add("File Writing Error(" + fileName + ") : " + ie.getMessage() );
        }
        File tempSourceFile=new File(fileName);
        return tempSourceFile;
	}
	
	private File prepareSourceDataFromResource(String sourceDataURL, ArrayList<String> message)
	{
		String fileName="xmlFile"+Calendar.getInstance().getTimeInMillis();
        FileWriter fw = null;
        
        try
        {
            fw = new FileWriter(new File(fileName));
    		URL url = new URL(sourceDataURL); 
    		InputStream in=url.openStream();
    		InputStreamReader sReader=new InputStreamReader(in); 

    		LineNumberReader lReader=new LineNumberReader(sReader);
    		String line=lReader.readLine();
    		//System.out.println("TransformationServiceImpl.prepareSourceDataFromResource():"+sourceDataURL);
    		while (line!=null)
    		{
    			//System.out.println(line);
    			fw.write(line + "\n");
    			line=lReader.readLine();
    		}            
            fw.close();
        }
        catch(Exception ie)
        {
        	message.add("File Writing Error(" + fileName + ") : " + ie.getMessage() );
        }
        File tempSourceFile=new File(fileName);
        return tempSourceFile;
	}

    private String extractCodeBaseFromScenario(String scenario) throws Exception
    {
        if ((scenario == null)||(scenario.trim().equals(""))) throw new Exception("Scenario Name is NULL or EMPTY.");
        scenario = scenario.trim();
        int idx = scenario.indexOf("@");
        if (idx < 0) return scenario;

        String[] s = new String[2];
        s[0] = scenario.substring(0, idx);
        s[1] = scenario.substring(idx + 1);
        if ((s[0].trim().equals(""))||(s[1].trim().equals(""))) throw new Exception("This Scenario Name is Invalid : " + scenario);

        String urlS = null;

        String wsURL = s[1].trim();

        String SERVICE_NAME = "/caadapter-cmts/";

        if (wsURL.toLowerCase().equals("qa"))
            urlS = "http://caadapter-qa.nci.nih.gov" + SERVICE_NAME;
        else if (wsURL.toLowerCase().equals("dev"))
            urlS = "http://caadapter-dev.nci.nih.gov" + SERVICE_NAME;
        else if (wsURL.toLowerCase().equals("stage"))
            urlS = "http://caadapter-stage.nci.nih.gov" + SERVICE_NAME;
        else if (wsURL.toLowerCase().equals("proc"))
            urlS = "http://caadapter.nci.nih.gov" + SERVICE_NAME;
        else
        {
            if (!wsURL.endsWith("/")) wsURL = wsURL + "/";
            while(wsURL.startsWith("/")) wsURL = wsURL.substring(1);
            if ((wsURL.toLowerCase().startsWith("http://"))||(wsURL.toLowerCase().startsWith("https://"))) {}
            else wsURL = "http://" + wsURL;
            while(true)
            {
                int idx2 = wsURL.indexOf(SERVICE_NAME);
                if (idx2 > 0)
                {
                    urlS = wsURL.substring(0, idx2 + SERVICE_NAME.length());
                    break;
                }

                idx2 = wsURL.toLowerCase().indexOf("/caadapterws");
                if (idx2 > 0)
                {
                    urlS = wsURL.substring(0, idx2) + SERVICE_NAME;
                    break;
                }

                urlS = wsURL + SERVICE_NAME.substring(1);
                break;
            }
        }

        if (urlS == null) urlS = s[1].trim();

        URL url = null;

        try
        {
            url = new URL(urlS);
        }
        catch(Exception ee)
        {
            throw new Exception("Invalid Server address("+s[0].trim()+"): " + urlS);
        }
        if (url != null) FileUtil.setCodeBase(url);
        return s[0].trim();
    }
	
//	private ArrayList<String> convertData(String mappingFile, String sourceDataFile) throws XQException
//	{
//		ArrayList<String> result = new ArrayList<String>();
//		System.out.println("TransformationServiceImpl.transferData()...looking for transformer...");
//		TransformationService transformer =TransformerFactory.getTransformer("xml") ;
//		String xmlResult=transformer.transfer(sourceDataFile, mappingFile);
//		System.out.println("TransformationServiceImpl.transferData()...resultData:\n"+xmlResult);
//		result.add(xmlResult);
//		return result;
//	}


}
