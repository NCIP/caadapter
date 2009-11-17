/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.ws;

import gov.nih.nci.caadapter.hl7.transformation.TransformationService;
import gov.nih.nci.caadapter.hl7.transformation.TransformationServiceUtil;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;
import gov.nih.nci.caadapter.hl7.v2v3.tools.ZipUtil;
import gov.nih.nci.caadapter.ws.object.ScenarioRegistration;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * caadapter Web Service to provide transformation service
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: altturbo $
 * @version $Revision: 1.10 $
 * @date $$Date: 2009-11-17 18:06:03 $
 * @since caadapter v1.3.1
 */

public class caAdapterTransformationService
{
    private String integratedMessage = null;
    private ValidatorResults integratedValidator = null;
    private List<String> generatedMessage = null;
    private List<ValidatorResults> generatedValidator = null;

    /**
     * caadapter Web Service to provide transformation service
     *
     * @param mappingScenario The name of the mapping scenario 
     * @param csvString csv data in String format 
     * @return A collection of the transformed HL7 v3 message
     */
    public ArrayList<String> transformationService(String mappingScenario, String csvString)
    {

		  ArrayList<String> result = new ArrayList<String>();

          try {
			  ScenarioRegistration scenario=ScenarioUtil.findScenario(mappingScenario);
			  if (scenario==null)
			  {
				  result.add("Scenario is not found:"+mappingScenario);
				  return result;
			  }
			  String path =ScenarioUtil.SCENARIO_HOME;
			  String scenarioPath=path+"/"+mappingScenario;
			  boolean exists = (new File(scenarioPath)).exists();
			  if (exists)
			  {
				  System.out.println("caAdapterTransformationService.transformationService()..path:"+path);
				  String mappingFileName = scenarioPath+"/"+scenario.getMappingFile();
				  System.out.println("mapping file:"+mappingFileName);

				  TransformationService transformationService =
					  new TransformationService(mappingFileName,csvString,true);
				  System.out.println("caAdapterTransformationService.transformationService()..start transformation");
				  List<XMLElement> mapGenerateResults = transformationService.process();
				  System.out.println("caAdapterTransformationService.transformationService()..generated message count:"+mapGenerateResults.size());
				  for (int i = 0; i < mapGenerateResults.size(); i++)
				  {
					  XMLElement hl7Xml = mapGenerateResults.get(i);
					  System.out.println("caAdapterTransformationService.transformationService()..message toString:"+hl7Xml);
					  result.add(hl7Xml.toXML().toString());
				  }
				  result.add("\n\nprocessed");
				  return result;
			  } else {
				  result.add("Scenario files are not found:"+scenarioPath);
				  return result;
			  }
		  }catch(Exception e)
		  {
			  result.add(e.getStackTrace().toString());
			  e.printStackTrace();
		  }
		  result.add("no HL7 message");
		  return result;
    }
	public ArrayList<String> transformationService(String mappingScenario, String csvString, String controlMessage)
    {

		  ArrayList<String> result = new ArrayList<String>(); 

          String controlMessageFile = null;
            if ((controlMessage != null)||(controlMessage.trim().equals("")))
            {
                try
                {
                    controlMessageFile = FileUtil.saveStringIntoTemporaryFile(controlMessage);
                }
                catch(IOException ie)
                {
                    controlMessageFile = null;
                }
            }
          try {
			  ScenarioRegistration scenario=ScenarioUtil.findScenario(mappingScenario);
			  if (scenario==null)
			  {
				  result.add("Scenario is not found:"+mappingScenario);
				  return result;
			  }
			  String path =ScenarioUtil.SCENARIO_HOME;
			  String scenarioPath=path+"/"+mappingScenario;
			  boolean exists = (new File(scenarioPath)).exists();
			  if (exists) 
			  {
				  System.out.println("caAdapterTransformationService.transformationService()..path:"+path);
				  String mappingFileName = scenarioPath+"/"+scenario.getMappingFile();
				  System.out.println("mapping file:"+mappingFileName);
	
				  TransformationService transformationService = 
					  new TransformationService(mappingFileName,csvString,true);
                  String outputZip = FileUtil.getTemporaryFileName(".zip");
                  transformationService.setOutputFile(new File(outputZip));
                  System.out.println("caAdapterTransformationService.transformationService()2..start transformation");



                  File outputZipFile = new File(outputZip);
            if ((!outputZipFile.exists())||(!outputZipFile.isFile()))
            {
                result.add("Output Zip generating failure : ");
		        return result;
            }

            int n = -1;
                  while(true)
                  {
                      String xmlMsg = "";
                      String nam = "" + n;
                      if (n < 0) nam = "i";
                      n++;
                      try
                      {
                          xmlMsg = TransformationServiceUtil.readFromZip(outputZipFile , nam + ".xml");
                      }
                      catch (Exception ie)
                      {
                          xmlMsg = null;
                      }
                      if ((xmlMsg == null)||(xmlMsg.trim().equals("")))
                      {
                          if (nam.equals("i")) continue;
                          else break;
                      }
                      result.add(xmlMsg);
                      if (nam.equals("i")) integratedMessage = xmlMsg;
                      else
                      {
                          if (generatedMessage == null) generatedMessage = new ArrayList<String>();
                          generatedMessage.add(xmlMsg);
                      }

                      ValidatorResults validatorsToShow=new ValidatorResults();

                        try
                        {
                            validatorsToShow.addValidatorResults((ValidatorResults)TransformationServiceUtil.readObjFromZip(outputZipFile ,nam + ".ser"));
                        }
                        catch(Exception ee)
                        {
                            validatorsToShow=new ValidatorResults();
                        }

                      if (nam.equals("i")) integratedValidator = validatorsToShow;
                      else
                      {
                          if (generatedValidator == null) generatedValidator = new ArrayList<ValidatorResults>();
                          generatedValidator.add(validatorsToShow);
                      }
                  }
                  outputZipFile.delete();
                  System.out.println("caAdapterTransformationService.transformationService()2..generated message count:"+result.size());

				  result.add("\n\nprocessed");
				  return result;
			  } else {
				  result.add("Scenario files are not found:"+scenarioPath);
				  return result;
			  }
		  }catch(Exception e)
		  {
			  result.add(e.getStackTrace().toString());
			  e.printStackTrace();
		  }
		  result.add("no HL7 message");
		  return result;
	}

    public String getIntegratedMessage() { return integratedMessage; }
    public String getIntegratedValidator()
    {
        if (integratedValidator == null) return null;
        return integratedValidator.toString();
    }
    public String getPayloadMessages(int index)
    {
        if (generatedMessage == null) return null;
        if (index >= generatedMessage.size()) return null;
        return generatedMessage.get(index);
    }
    public String getPayloadValidator(int index)
    {
        if (generatedValidator == null) return null;
        if (index >= generatedValidator.size()) return null;
        return generatedValidator.get(index).toString();
    }
    public int getPayloadMessageCount()
    {
        if (generatedMessage == null) return 0;
        return generatedMessage.size();
    }


//&umkis:INSERT=caAdapterTransformationServiceINSERT.java
}
