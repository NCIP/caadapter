/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.common.function;
/**
 * Util for functions
 * @author OWNER: $Author: phadkes $
 * @author LAST UPDATE $Author: phadkes $
 * @since      caAdapter  v4.2
 * @version    $Revision: 1.4 $
 * @date       $Date: 2008-09-25 18:57:45 $
*/

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.util.RegistryUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class FunctionUtil {


	/**
     * Generat a Temporary File Name at workingspace directory.
     * @return a Temporary File Name.
     */
    public static String getTemporaryFileName() // inserted by umkis 08/09/2006
    {
        DateFunction dateFunction = new DateFunction();
        String dateFormat = dateFunction.getDefaultDateFormatString();
        if (!dateFormat.endsWith("SSS")) dateFormat = dateFormat + "SSS";
        try
        {
            return FileUtil.getUIWorkingDirectoryPath() + File.separator + Config.TEMPORARY_FILE_PREFIX + (new DateFunction()).getCurrentTime(dateFormat) + "_" + FileUtil.getRandomNumber(4) + Config.TEMPORARY_FILE_EXTENSION;
        }
        catch(FunctionException fe)
        {
            return FileUtil.getUIWorkingDirectoryPath() + File.separator + Config.TEMPORARY_FILE_PREFIX + (new DateFunction()).getCurrentTime() + "_" + FileUtil.getRandomNumber(4) + Config.TEMPORARY_FILE_EXTENSION;
        }
    }

	 /**
     * Delete temporary files in the workingspace directory
     */
    public static void deleteTemporaryFiles()  // inserted by umkis 08/09/2006
    {
        File workDir = null;
        File[] files = null;
        try
        {
            workDir = new File(FileUtil.getUIWorkingDirectoryPath());
            files = workDir.listFiles();
            if (files.length == 0) return;
        }
        catch(Exception ee)
        {
            return;
        }
        //===============================================
        File aFile = null;
        String fileName = "";
        boolean wellDone = true;
        DateFunction dateFunction = new DateFunction();
        String dateFormat = dateFunction.getDefaultDateFormatString();
        if (!dateFormat.endsWith("SSS"))
        	dateFormat = dateFormat + "SSS";

        for(int i=0;i<files.length;i++)
		{
			aFile = files[i];
			//System.out.println("fileName : " + aFile.getName());
			if (aFile.isFile())
			{
				fileName = aFile.getName();
				String fileNameM  = "";
				int posUnderBar = fileName.indexOf("_");
				//System.out.println("underBar : " + posUnderBar);
				if (posUnderBar != (Config.TEMPORARY_FILE_PREFIX.length() + dateFormat.length())) continue;

				fileNameM = fileName.substring(Config.TEMPORARY_FILE_PREFIX.length(), posUnderBar);

				SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
				java.util.Date fileDate = null;
        		try
        		{
            		fileDate = sdf.parse(fileNameM);
        		}
        		catch(ParseException pe)
        		{
            		continue;
        		}

				java.util.Date nowDate = new java.util.Date();
				Calendar fileCalendar = Calendar.getInstance();
          		Calendar nowCalendar = Calendar.getInstance();
          		fileCalendar.setTime(fileDate);
			    nowCalendar.setTime(nowDate);
			    fileCalendar.add(Calendar.MINUTE, 10);
				wellDone = true;

				if (nowCalendar.after(fileCalendar))
				{
					if (FileUtil.isTemporaryFileName(fileName))
                    {
                        wellDone = aFile.delete();
                    }
				}
				if (!wellDone) System.out.println("Temporary File delete Failure : " + aFile.getAbsolutePath());
			}
		}
    }
    /**
     * This function will return the file with the given extension. If it already contains, return immediately.
     * @param addr a URL address
     * @return the File object contains the right file name with the given extension.
     * @throws IOException Any Exception will be passed into IOException
     */
    public static String downloadFromURLtoTempFile(String addr) throws IOException
    {
        URL ur = null;
        InputStream is = null;
		FileOutputStream fos = null;
        String tempFile = getTemporaryFileName();

        try
        {
            ur = new URL(addr);
        }
        catch(MalformedURLException ue)
        {
            throw new IOException("Invalid URL");
        }
        URLConnection uc = ur.openConnection();
        uc.connect();
        is = uc.getInputStream();
        DataInputStream dis = new DataInputStream(is);
        fos = new FileOutputStream(tempFile);
        DataOutputStream dos = new DataOutputStream(fos);
        byte bt = 0;

        while(true)
        {
            try
            {
                bt = dis.readByte();
                dos.writeByte(bt);
            }
            catch(IOException ie)
            {
                break;
            }
            catch(NullPointerException ie)
            {
                break;
            }
        }
        dis.close();
        dos.close();
        is.close();
        fos.close();

        File file = null;
        file = new File(tempFile);
        file.deleteOnExit();

        return tempFile;
    }
    /**
     * Create a temporary file which includes the received string parameter.
     *
     * @param string parameter which would like to be saved into this temporary file.
     * @return the temporary file name. this file will be automatically deleted when system exit in according to File.deleteOnExit().
     * @throws IOException when saving is failed.
     */
    public static String saveStringIntoTemporaryFile(String string) throws IOException // inserted by umkis 08/10/2006
    {
        String tempFileName = getTemporaryFileName();
        FileUtil.saveStringIntoTemporaryFile(tempFileName, string);
        return tempFileName;

    }
    public static boolean inputRegistry(String regiName, String content) throws IOException
    {
        String regiContent = regiName.trim() + Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR + content.trim();
        String registryFileName= RegistryUtil.getRegistryFileName();
        if (registryFileName == null)
        {
            System.out.println("inputRegistry : " + regiContent);
            registryFileName = saveStringIntoTemporaryFile("Default_Line"+ Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR+"default\r\n"+regiContent);
            return true;
        }
        List<String> list =FileUtil.readFileIntoList(RegistryUtil.getRegistryFileName());
        String output = "";
        for(int i=0;i<list.size();i++)
        {
            String line = list.get(i);
            String regiHead = line.substring(0, line.indexOf(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR));
            if (regiHead.equals(regiName.trim()))
            {
                String value = line.substring(line.indexOf(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR) + Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR.length());
                System.out.println("Already inputRegistry : " + regiContent + ", value : " + value);
                return false;
            }
            output = output + line + "\r\n";
        }
        output = output + regiContent;
        System.out.println("inputRegistry : " + regiContent);
        FileUtil.saveStringIntoTemporaryFile(RegistryUtil.getRegistryFileName(), output);
        return true;
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
