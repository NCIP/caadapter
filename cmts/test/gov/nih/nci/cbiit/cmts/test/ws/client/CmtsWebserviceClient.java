/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
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
import java.util.*;

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
      
//      String endpointURL = "http://localhost:30210/caadapterWS-cmts/services/cmtsTransformationService";
      if (args.length<4)
      {
    	  System.out.println("TestCaadapterWebservice Usage:[scenarioName]|[sourceString]|[sourceType]|[endURL]");
    	  return;
      }
      System.out.println("CmtsWebserviceClient.main...scenarioName:"+args[0]);
      System.out.println("CmtsWebserviceClient.main...sourceString:"+args[1]);
      System.out.println("CmtsWebserviceClient.main...sourecType:"+args[2]);
      System.out.println("CmtsWebserviceClient.main...endURL:"+args[3]);
      
      //read WS paramters
      String scenarioName= args[0];
      String sourceDataFileName=args[1];
      String sourceDataString =CmtsWebserviceClient.buildCsvString(sourceDataFileName);      
      String sourceType=args[2];
      String endpointURL =args[3];
      
      
      //build service call
      Service service = new Service();
      Call call = (Call)service.createCall();
      call.setTargetEndpointAddress(new java.net.URL(endpointURL));
      String methodName = "transformationService";
      call.setOperationName(methodName);
      call.addParameter("mappingScenario", XMLType.XSD_STRING,ParameterMode.IN );
      call.addParameter("sourceDataString",  XMLType.XSD_STRING, ParameterMode.IN );
      call.addParameter("sourceType", XMLType.XSD_STRING,ParameterMode.IN );
      call.setReturnClass(java.util.ArrayList.class);
      ArrayList res = (ArrayList)call.invoke(new Object[]{scenarioName,sourceDataString, sourceType});
      System.out.println(res);
    }catch(Exception e) {
     	 e.printStackTrace();
    }
 }
}
