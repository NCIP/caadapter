/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.ws;

import gov.nih.nci.hl7.map.TransformationResult;
import gov.nih.nci.hl7.map.TransformationService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * caadapter Web Service to provide transformation service
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.1 $
 * @date $$Date: 2007-01-26 20:53:51 $
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

		  boolean exists = (new File(path+mappingScenario)).exists();
		    if (exists) {
		    	
		    	String mappingFileName = path+mappingScenario+"/"+mappingScenario + ".map";
				TransformationService transformationService = 
					new TransformationService(mappingFileName,csvString,true);
				List<TransformationResult> mapGenerateResults = transformationService.process();
				for (int i = 0; i < mapGenerateResults.size(); i++)
				{
					TransformationResult mapGenerateResult = mapGenerateResults.get(i);
					result.add(mapGenerateResult.getHl7V3MessageText());
				}
				return result;
		    } else {
		    	return null;
		    }
	}
}
