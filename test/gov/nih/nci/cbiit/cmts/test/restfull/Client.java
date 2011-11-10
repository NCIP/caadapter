 
package gov.nih.nci.cbiit.cmts.test.restfull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.io.CachedOutputStream;

public final class Client {

    private Client() {
    }

    public static void main(String args[]) throws Exception {
        // Sent HTTP GET request to query data
        if (args.length<3)
        {
      	  System.out.println("Client Usage:[scenarioName]|[sourceString]|[sourceType]|[endURL]");
      	  return;
        }
        System.out.println("Client.main...scenarioName:"+args[0]);
        System.out.println("Client.main...sourceString:"+args[1]);
        System.out.println("Client.main...endURL:"+args[2]);
        
        //read WS paramters
        String scenarioName= args[0];
        String sourceDataFileName=args[1];      
        String targetUrl =args[2];
        String srcData=buildSourceDataString(sourceDataFileName);

        System.out.println("connect...:"+targetUrl);

		String srcEncoded=URLEncoder.encode(srcData, "UTF-8");
		String querySt="scenario="+scenarioName+"&source="+srcEncoded;
	
		String urlSt=targetUrl+"?"+querySt;//
		System.out.println("Client.main()..url\n"+urlSt);
        URL url = new URL(urlSt);
        InputStream in = url.openStream();
        String rtnString=getStringFromInputStream(in);
//        System.out.println("Client.main()..return:\n"+rtnString);
        System.out.println("decoded return:\n"+URLDecoder.decode(rtnString, "UTF-8"));
        System.exit(0);
    }

    private static String getStringFromInputStream(InputStream in) throws Exception {
        CachedOutputStream bos = new CachedOutputStream();
        IOUtils.copy(in, bos);
        in.close();
        bos.close();
        return bos.getOut().toString();
    }
    
	private static String buildSourceDataString(String fileName)
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
				fi=Client.class.getClassLoader().getResource(fileName).openStream();
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
}
