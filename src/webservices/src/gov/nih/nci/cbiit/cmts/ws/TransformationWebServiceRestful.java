package gov.nih.nci.cbiit.cmts.ws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface TransformationWebServiceRestful {

	@GET
	@Produces ({MediaType.TEXT_XML, "application/xml"})
	@Path("transfer")
	public ResultList restfullService(@QueryParam("scenario") String mappingScenario, @QueryParam("source") String sourceData);
}
