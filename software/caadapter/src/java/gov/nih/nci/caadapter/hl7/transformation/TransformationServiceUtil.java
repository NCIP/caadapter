/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.hl7.transformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.List;
import java.util.ArrayList;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Oct 29, 2008
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.2 $
 * @date 	 DATE: $Date: 2009-04-02 19:17:48 $
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

    /*
    public static int countEntriesInZip(File file) throws FileNotFoundException, IOException{
		ZipInputStream inZip = new ZipInputStream(new FileInputStream(file));
		int count = 0;
		while(inZip.getNextEntry()!=null)
			count++;
		inZip.close();
		return count;
	}
    */

    public static int countEntriesInZip(File file) throws FileNotFoundException, IOException{
        return countEntriesInZip(file, null);
    }
    public static int countEntriesInZip(File file, String extension) throws FileNotFoundException, IOException{

        return  getNamesOfEntriesInZip(file, extension).size();
    }

    public static List<String> getNamesOfEntriesInZip(File file) throws FileNotFoundException, IOException{
        return getNamesOfEntriesInZip(file, null);
    }
    public static List<String> getNamesOfEntriesInZip(File file, String extension) throws FileNotFoundException, IOException{
        ZipInputStream inZip = null;
        List<String> list = new ArrayList<String>();
        if (extension == null) extension = "";
        extension = extension.trim();

        try
        {
            inZip = new ZipInputStream(new FileInputStream(file));
            while(true)
            {
                ZipEntry entry = inZip.getNextEntry();
                if (entry == null) break;

                String entryName = entry.getName();
                if (extension.equals("")) list.add(entryName);
                else
                {
                    if (entryName.toLowerCase().trim().endsWith(extension.toLowerCase())) list.add(entryName);
                }
            }
            inZip.close();
        }
        finally
        {
            try{
                inZip.close();
            }catch(Exception ignored){}
        }

        return list;
    }
}


/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.1  2008/10/29 19:07:39  wangeug
* HISTORY: create TransformationServiceUtil.java to hold Util methods
* HISTORY:
**/