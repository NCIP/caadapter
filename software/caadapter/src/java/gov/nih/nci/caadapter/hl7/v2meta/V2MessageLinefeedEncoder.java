/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.hl7.v2meta;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Dec 5, 2008
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.3 $
 * @date 	 DATE: $Date: 2009-02-06 20:50:27 $
 * @since caAdapter v4.2
 */

public class V2MessageLinefeedEncoder {
	ArrayList<byte[]> byteOutList;

	public V2MessageLinefeedEncoder(InputStream in)
	{
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
	 * @return the encodedByteList of V2 message
	 */
	public ArrayList<byte[]> getEncodeByteList()
	{
		return byteOutList;
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
		byteOutList =new ArrayList<byte[]>();
		ByteArrayOutputStream byteOut =new ByteArrayOutputStream();

		// read each line of the file until EOF is reached
        String curLine = null;
//        char lfChar=10; //line feed character
        char crtChar=13; //Carriage return character

        while ((curLine = br.readLine()) != null)
        {
            curLine = curLine.trim();
            //skip blank line
            if (curLine.equals(""))
            	continue;

			if (curLine.startsWith("MSH"))
			{
				if (byteOut.toByteArray().length>0)
				{
					byteOutList.add(byteOut.toByteArray());
					byteOut=new ByteArrayOutputStream();
 				}
			}
			byteOut.write(curLine.getBytes());
			byteOut.write((byte)crtChar);
		}
        byteOutList.add(byteOut.toByteArray());
        byteOut.close();
        in.close();
	}
}


/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.2  2009/02/02 15:33:50  wangeug
* HISTORY: handle blank line in V2 message
* HISTORY:
* HISTORY: Revision 1.1  2008/12/08 18:50:32  wangeug
* HISTORY: pre-process V2 message : attach CTR at end of each message segment
* HISTORY:
**/