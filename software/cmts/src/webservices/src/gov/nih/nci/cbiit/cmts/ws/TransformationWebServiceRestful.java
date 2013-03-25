/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.ws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface TransformationWebServiceRestful {

	/**
	 *  RESTfull API: transfer source data from string
	 * @param mappingScenario
	 * @param sourceData
	 * @return
	 */
	@GET
	@Produces ({MediaType.TEXT_XML, "application/xml"})
	@Path("transferData")
	public ResultList restfulTransferData(@QueryParam("scenario") String mappingScenario, @QueryParam("source") String sourceData);
	
	/**
	 * RESTfull API: transfer source data from URL
	 * @param mappingScenario
	 * @param sourceURL
	 * @return
	 */
	@GET
	@Produces ({MediaType.TEXT_XML, "application/xml"})
	@Path("transferResource")
	public ResultList restfulTransferResource(@QueryParam("scenario") String mappingScenario, @QueryParam("source") String sourceURL);

}
