/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.test.ws.client;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import javax.xml.rpc.ParameterMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CmtsWebserviceClient {

	public static String buildCsvString(String fileName)
	{
		StringBuffer rtnBuf=new StringBuffer();
		try {
			File srcFile=new  File(fileName);
			InputStream fi;
			if (srcFile.exists())
        	{
        		fi =new FileInputStream(srcFile);
        	}
			else
				fi=CmtsWebserviceClient.class.getClassLoader().getResource(fileName).openStream();
			InputStreamReader reader=new InputStreamReader(fi);
			BufferedReader bfReader=new BufferedReader(reader);
			String lineSt;
			lineSt = bfReader.readLine();
			while (lineSt!=null)
			{
				rtnBuf.append(lineSt+"\n");
				lineSt = bfReader.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return rtnBuf.toString();
		}
		return rtnBuf.toString();
	}

  public static void main(String[] args) {
    try {
      if (args.length<4)
      {
    	  System.out.println("TestCaadapterWebservice Usage:[scenarioName]|[sourceData]|[endURL]|[operationName]");
    	  return;
      }
      System.out.println("CmtsWebserviceClient.main...scenarioName:"+args[0]);
      System.out.println("CmtsWebserviceClient.main...sourceData:"+args[1]);
      System.out.println("CmtsWebserviceClient.main...endURL:"+args[2]);
      System.out.println("CmtsWebserviceClient.main()...operation:"+args[3]);

      //read WS parameters
      String scenarioName= args[0];
      String sourceDataResource=args[1];

      String endpointURL =args[2];
      String operationnName=args[3];


      //build service call
      Service service = new Service();
      Call call = (Call)service.createCall();
      call.setTargetEndpointAddress(new java.net.URL(endpointURL));
      call.setOperationName(operationnName);
      call.addParameter("arg0", XMLType.XSD_STRING,ParameterMode.IN );
      call.addParameter("arg1",  XMLType.XSD_STRING, ParameterMode.IN );
      call.setReturnClass(java.lang.String.class);
      Object result;
      if(operationnName.equalsIgnoreCase("transferData"))
      {
    	  String sourceDataString =CmtsWebserviceClient.buildCsvString(sourceDataResource);
    	  result = call.invoke(new Object[]{scenarioName,sourceDataString});
      }
      else
    	  result = call.invoke(new Object[]{scenarioName,sourceDataResource});//, sourceType});
//      call.addParameter("mappingScenario", XMLType.XSD_STRING,ParameterMode.IN );
//      call.addParameter("sourceDataString",  XMLType.XSD_STRING, ParameterMode.IN );
//      call.addParameter("sourceType", XMLType.XSD_STRING,ParameterMode.IN );
//      call.setReturnClass(java.util.ArrayList.class);
//      ArrayList<String> res = (ArrayList<String>)call.invoke(new Object[]{scenarioName,sourceDataString});//, sourceType});
     System.out.println("CmtsWebserviceClient.main()..\n"+result);

    }catch(Exception e) {
     	 e.printStackTrace();
    }
 }
}
