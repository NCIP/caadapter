/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.hl7.v2meta;


import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Jan 22, 2009
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.3 $
 * @date 	 DATE: $Date: 2009-02-25 16:52:29 $
 * @since caAdapter v4.2
 */

public class V2MessageSchemaIndexLoader {
	private static V2MessageIndex v2Index;

	public  static V2MessageIndex loadMessageInfos() throws Exception {
		if (v2Index!=null)
			return v2Index;
		String zipFilePath="lib/HL7v2xsd.zip";
		System.out.println("MIFIndexParser.loadMIFInfosZip()..zipFilePath"+zipFilePath);
		ZipFile zip = new ZipFile(zipFilePath);
		Enumeration entryEnum=zip.entries();

		v2Index = new V2MessageIndex();
		while (entryEnum.hasMoreElements())
		{
			ZipEntry zipEntry=(ZipEntry)entryEnum.nextElement();
			String fileName=zipEntry.getName();
 			v2Index.addMessageType(fileName);
		}
		return v2Index;
	}
	public static void saveV2MessageIndexObject() throws Exception {
		OutputStream os = new FileOutputStream("v2MessageIndex.obj");
		ObjectOutputStream oos = new ObjectOutputStream(os);
		V2MessageIndex v2Index= loadMessageInfos() ;
		oos.writeObject(v2Index);
		oos.close();
		os.close();
	}

	public static V2MessageIndex loadV2MessageIndexObject() {
		try {
			System.out
					.println("V2MessageSchemaIndexLoader.loadV2MessageIndexObject()...:v2MessageIndex.obj");
			URL isURL =V2MessageSchemaIndexLoader.class.getClassLoader().getResource("v2MessageIndex.obj");
			if (isURL==null)
				return null;
			InputStream is =isURL.openStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			V2MessageIndex v2Index= (V2MessageIndex)ois.readObject();
			ois.close();
			is.close();
			return v2Index;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args)
	{
		try {
			V2MessageSchemaIndexLoader.saveV2MessageIndexObject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.2  2009/02/25 15:58:16  wangeug
* HISTORY: enable webstart
* HISTORY:
* HISTORY: Revision 1.1  2009/02/24 16:00:40  wangeug
* HISTORY: move from other package
* HISTORY:
* HISTORY: Revision 1.1  2009/01/23 18:22:00  wangeug
* HISTORY: Load V2 meta with version number and message schema name; do not use the absolute path of schema file
* HISTORY:
**/