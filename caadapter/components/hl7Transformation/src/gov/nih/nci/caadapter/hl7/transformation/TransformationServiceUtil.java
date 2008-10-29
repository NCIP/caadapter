/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.transformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Oct 29, 2008
 * @author   LAST UPDATE: $Author: wangeug 
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2008-10-29 19:07:39 $
 * @since caAdapter v4.2
 */

public class TransformationServiceUtil {

	public static Object readObjFromZip(File file, String name) throws FileNotFoundException, ClassNotFoundException, IOException{
		Object ret = null;
		ZipInputStream inZip = null;
		try{
			inZip = new ZipInputStream(new FileInputStream(file));
			String entryName = "";
			while(!entryName.equals(name))
			{
				ZipEntry entry = inZip.getNextEntry();
				if(entry==null) throw new IOException("entry not found.");
				entryName = entry.getName();
			}
			ObjectInputStream objIn = new ObjectInputStream(inZip);
			ret = objIn.readObject();
		}finally{
			try{
				inZip.close();
			}catch(Exception ignored){}
		}
			
		return ret;
	}

	public static String readFromZip(File file, String name) throws FileNotFoundException, IOException{
		ZipInputStream inZip = null;
		StringBuffer ret = new StringBuffer();
		try{
			inZip = new ZipInputStream(new FileInputStream(file));
			String entryName = "";
			while(!entryName.equals(name))
			{
				ZipEntry entry = inZip.getNextEntry();
				if(entry==null) throw new IOException("entry not found.");
				entryName = entry.getName();
			}
			byte[] buf = new byte[1024];
			int count = 0;
			while(count>=0)
			{
				count = inZip.read(buf, 0, 1024);
				if(count>0) ret.append(new String(buf, 0, count));
			}
		}finally{
			try{
				inZip.close();
			}catch(Exception ignored){}
		}
		return ret.toString();
	}

	public static int countEntriesInZip(File file) throws FileNotFoundException, IOException{
		ZipInputStream inZip = new ZipInputStream(new FileInputStream(file));
		int count = 0;
		while(inZip.getNextEntry()!=null) 
			count++;
		inZip.close();
		return count;
	}

}


/**
* HISTORY: $Log: not supported by cvs2svn $
**/