/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





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
 * @version $Revision: 1.15 $
 * @date $$Date: 2009-11-25 02:14:48 $
 * @since caadapter v1.3.1
 */

public class caAdapterTransformationService
{
    /**
     * caadapter Web Service to provide transformation service
     *
     * @param mappingScenario The name of the mapping scenario
     * @param csvString csv data in String format
     * @return A collection of the transformed HL7 v3 message
     */
    public ArrayList<String> transformationService(String mappingScenario, String csvString)
    {
        return transformationService(mappingScenario, csvString, null);

    }
	public ArrayList<String> transformationService(String mappingScenario, String csvString, String controlMessage)
    {

		  ArrayList<String> result = new ArrayList<String>();

          String controlMessageFile = null;
            if ((controlMessage != null)&&(!controlMessage.trim().equals("")))
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


                  String tempCSVFile = FileUtil.getTemporaryFileName(".csv");
                  FileUtil.saveStringIntoTemporaryFile(tempCSVFile, csvString);

                  TransformationService transformationService =
					  new TransformationService(mappingFileName,tempCSVFile);

                  String outputZip = FileUtil.getTemporaryFileName(".zip");
                  transformationService.setOutputFile(new File(outputZip));
                  System.out.println("caAdapterTransformationService.transformationService()2..start transformation");

                  transformationService.process(controlMessageFile);

                  File outputZipFile = new File(outputZip);
                  if ((!outputZipFile.exists())||(!outputZipFile.isFile()))
                  {
                      result.add("Output Zip generating failure : " + outputZip);
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
                          if (nam.equals("i"))
                          {
                              if (controlMessageFile == null) continue;
                              else
                              {
                                  result.add("Failure of Control Message Wrapping. Please, check the control message.");
                                  return result;
                              }
                          }
                          else break;
                      }

                      if (nam.equals("i"))
                      {
                          result.add(xmlMsg);
                          result.add("\n\n control message wrapping has done.");
                          return result;
                      }

                      result.add(xmlMsg);
                  }
                  outputZipFile.delete();
                  System.out.println("caAdapterTransformationService.transformationService()2..generated message count:"+result.size());

				  result.add("\n\n "+result.size()+" payload message(s) processed");
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

//&umkis:INSERT=caAdapterTransformationServiceINSERT.java
}
