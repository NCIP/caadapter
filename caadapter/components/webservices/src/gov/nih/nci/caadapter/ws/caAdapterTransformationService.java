/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ws;
import gov.nih.nci.caadapter.hl7.map.TransformationResult;
import gov.nih.nci.caadapter.hl7.transformation.TransformationService;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;
//import gov.nih.nci.caadapter.hl7.map.TransformationServiceCsvToHL7V3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * caadapter Web Service to provide transformation service
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: phadkes $
 * @version $Revision: 1.5 $
 * @date $$Date: 2008-06-09 19:54:07 $
 * @since caadapter v1.3.1
 */

public class caAdapterTransformationService {

	/**
     * caadapter Web Service to provide transformation service
     *
     * @param mappingScenario The name of the mapping scenario
     * @param csvString csv data in String format
     * @return A collection of the tranformed HL7 v3 message
     */

	public ArrayList<String> transformationService(String mappingScenario, String csvString) {

		  Properties caadapterProperties = new Properties();
		  String path = System.getProperty("gov.nih.nci.caadapter.path");
		  ArrayList<String> result = new ArrayList<String>();

		  try {
		  boolean exists = (new File(path+mappingScenario)).exists();
		  if (exists) {

			  String mappingFileName = path+mappingScenario+"/"+mappingScenario + ".map";
			  System.out.println(mappingFileName);
			  System.out.println(csvString);
			  TransformationService transformationService =
				  new TransformationService(mappingFileName,csvString,true);
			  System.out.println("start process");
			  List<XMLElement> mapGenerateResults = transformationService.process();
			  System.out.println(mapGenerateResults);
			  for (int i = 0; i < mapGenerateResults.size(); i++)
			  {
				  XMLElement mapGenerateResult = mapGenerateResults.get(i);
				  result.add(mapGenerateResult.toXML().toString());
			  }
			  return result;
		  } else {
			  return null;
		  }
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  return null;
	}
}
