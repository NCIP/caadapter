/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Mar 25, 2009
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.2 $
 * @date 	 DATE: $Date: 2009-04-01 17:38:50 $
 * @since caAdapter v4.2
 */

public class WebstartUtil {
	private static boolean isWebstartDeployed=false;

	/**
	 * @return the isWebstartDeployed
	 */
	public static boolean isWebstartDeployed() {
		return isWebstartDeployed;
	}

	/**
	 * @param isWebstartDeployed the isWebstartDeployed to set
	 */
	public static void setWebstartDeployed(boolean isWebstartDeployed) {
		WebstartUtil.isWebstartDeployed = isWebstartDeployed;
	}

	public static void downloadFile(String srcFile, String targetFile)
	{
		System.out.println("WebstartUtil.downloadFile()...source file:"+srcFile);
		try {

			URL isURL=FileUtil.retrieveResourceURL(srcFile);
			if (isURL==null)
				return;
			InputStream fis =isURL.openStream();
			DataInputStream dis = new DataInputStream(new BufferedInputStream(fis));
			File outFile=new File(targetFile);
			//create the path folders
			if (!outFile.exists())
				outFile.getParentFile().mkdirs();

			FileOutputStream fos = new FileOutputStream(targetFile);
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(fos));
			byte b;
			try{
				while(true){
					b = (byte) dis.readByte();
					dos.write(b);
				}
			}
			catch(EOFException e)
			{
				System.err.println("Finish read end of file...");
			}   finally{
				dos.close();
			}

		}catch (Exception e) {
			e.printStackTrace();
		}

	}

}


/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.1  2009/03/25 18:00:46  wangeug
* HISTORY: VOM integration
* HISTORY:
**/