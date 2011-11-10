package gov.nih.nci.cbiit.cmts.ws;

import java.util.ArrayList;

public class TransformationServiceRestfulImpl implements TransformationWebServiceRestful{

	private TransformationWebService wsServer;
	@Override
	public ResultList restfullService(String mappingScenario,
			String sourceData) {
		if (wsServer==null)
			wsServer=new TransformationServiceImpl();
		System.out
				.println("TransformationServiceRestfullImpl.restfullService()...called");
		System.out
				.println("TransformationServiceRestfullImpl.restfullService()...scenario:"+mappingScenario);
		System.out
				.println("TransformationServiceRestfullImpl.restfullService()...sourcedata:\n"+sourceData);
		ArrayList<String> transList=wsServer.transformationService(mappingScenario, sourceData);
		ResultList rtnList= new ResultList();

		rtnList.getResultData().add("mappingScenario="+mappingScenario);
		rtnList.getResultData().add("sourceData="+sourceData);
		for (String transResult:transList)
		{
			rtnList.getResultData().add(transResult);
		}
		return rtnList;
	}	
}
