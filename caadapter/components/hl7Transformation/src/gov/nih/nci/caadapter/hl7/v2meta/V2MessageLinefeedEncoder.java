/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.v2meta;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
//import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
 
/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Dec 5, 2008
 * @author   LAST UPDATE: $Author: wangeug 
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2008-12-08 18:50:32 $
 * @since caAdapter v4.2
 */

public class V2MessageLinefeedEncoder {
 
	ByteArrayOutputStream byteOut;
	/**
	 * @return the encodedInputStream
	 */
	public InputStream getEncodedInputStream() {
// 		System.out.println("V2MessageLinefeedEncoder.getEncodedInputStream()..:\n"+byteOut.toString());
		ByteArrayInputStream rtnStream= new ByteArrayInputStream(byteOut.toByteArray());
		return rtnStream;
	}

 
	public V2MessageLinefeedEncoder(InputStream in)
	{
//		tmpFileName=System.getProperty("user.dir")+File.separator+System.currentTimeMillis();
 		try {
			processInupt(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Read the input stream, attach Carriage_Return at the end of each segment
	 * @param in
	 * @throws IOException
	 */
	private void processInupt(InputStream in) throws IOException
	{
		// convert the InputStream into a BufferedReader
		InputStreamReader streamIn=new InputStreamReader( in);
		BufferedReader br = new BufferedReader(streamIn);
		byteOut =new ByteArrayOutputStream();
		// read each line of the file until EOF is reached
        String curLine = null;
//        char lfChar=10; //line feed character
        char crtChar=13; //Carriage return character
        
        while ((curLine = br.readLine()) != null) 
        {
            curLine = curLine.trim();
//			System.out.println("V2MessageLinefeedEncoder.processInupt()..read line:"+curLine);
			byteOut.write(curLine.getBytes());
			byteOut.write((byte)crtChar);
		}
        byteOut.close();
        in.close();
	}
}


/**
* HISTORY: $Log: not supported by cvs2svn $
**/