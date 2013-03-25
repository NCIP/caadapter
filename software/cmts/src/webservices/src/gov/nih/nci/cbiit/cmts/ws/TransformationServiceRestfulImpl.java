/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.ws;

import java.util.ArrayList;

public class TransformationServiceRestfulImpl implements TransformationWebServiceRestful{

	private TransformationWebService wsServer;
	@Override
	public ResultList restfulTransferData(String mappingScenario,
			String sourceData) {
		if (wsServer==null)
			wsServer=new TransformationServiceImpl();
		System.out
				.println("TransformationServiceRestfullImpl.restfulTransferData...called");
		System.out
				.println("TransformationServiceRestfullImpl.restfulTransferData...scenario:"+mappingScenario);
		//System.out
		//		.println("TransformationServiceRestfullImpl.restfulTransferData...sourcedata:\n"+sourceData);
		ArrayList<String> transList=wsServer.transferData(mappingScenario, sourceData);
		ResultList rtnList= new ResultList();

		rtnList.getResultData().add("mappingScenario="+mappingScenario);
		rtnList.getResultData().add("sourceData="+sourceData);
		for (String transResult:transList)
		{
			rtnList.getResultData().add(transResult);
		}
		return rtnList;
	}
	@Override
	public ResultList restfulTransferResource(String mappingScenario,
			String sourceURL) {
		if (wsServer==null)
			wsServer=new TransformationServiceImpl();
		System.out
				.println("TransformationServiceRestfullImpl.restfulTransferResource...called");
		System.out
				.println("TransformationServiceRestfullImpl.restfulTransferResource...scenario:"+mappingScenario);
		System.out
				.println("TransformationServiceRestfullImpl.restfulTransferResource...sourcedata:\n"+sourceURL);
		ArrayList<String> transList=wsServer.transferResource(mappingScenario, sourceURL);
		ResultList rtnList= new ResultList();

		rtnList.getResultData().add("mappingScenario="+mappingScenario);
		rtnList.getResultData().add("sourceData="+sourceURL);
		for (String transResult:transList)
		{
			rtnList.getResultData().add(transResult);
		}
		return rtnList;
	}	
}
