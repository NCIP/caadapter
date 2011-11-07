package gov.nih.nci.cbiit.cmts.ws;

import java.util.ArrayList;

import javax.jws.WebService;
@WebService
public interface TransformationWebService {
	/**
	 * caadapter Commong Mapping and Transformation Service module Web Service
	 * to provide transformation service
	 * 
	 * @param mappingScenario
	 *            The name of the mapping scenario
	 * @param sourceDataString
	 *            source data in String format
	 * @return A collection of the transformed XML message
	 */
	public ArrayList<String> transformationService(String mappingScenario, String sourceData);
}
