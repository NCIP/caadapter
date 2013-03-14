/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.ws;

import java.util.ArrayList;

import javax.jws.WebService;
@WebService
public interface TransformationWebService {
	/**
	 *Transfer a data string into target dataset 
	 * @param mappingScenario
	 *            The name of the mapping scenario
	 * @param sourceData
	 *            source data in String format
	 * @return A collection of the transformed XML message
	 */
	public ArrayList<String> transferData(String mappingScenario, String sourceData);

	/**
	 *Transfer a data string into target dataset 
	 * @param mappingScenario
	 *            The name of the mapping scenario
	 * @param sourceResource
	 *           URL of source data
	 * @return A collection of the transformed XML message
	 */
	public ArrayList<String> transferResource(String mappingScenario, String sourceResource);

}
