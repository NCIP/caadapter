/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/util/FileUtil.java,v 1.6 2007-08-09 01:56:52 umkis Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 *
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 *
 *
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 *
 *
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear.
 *
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software.
 *
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick.
 *
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.common.util;

import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.function.FunctionException;
import gov.nih.nci.caadapter.common.function.DateFunction;

/**
 * File related utility class
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: umkis $
 * @version $Revision: 1.6 $
 */

public class FileUtil
{
    private static final String OUTPUT_DIR_NAME = "out";
    private static File OUTPUT_DIR = null;
    private static File ODI_FILE = null;

    //private static String dateFormat = "yyyyMMddHHmmssSSS";

    /**
     * Create the output directory if it doesn't exist.
     */
    private static void setupOutputDir()
    {
        OUTPUT_DIR = new File(OUTPUT_DIR_NAME);
        if (!OUTPUT_DIR.isDirectory())
        {
            OUTPUT_DIR.mkdir();
        }
    }

    /**
     * Create the output directory if necessary and return a reference to it.
     *
     * @return The output directory
     */
    public static File getOutputDir()
    {
        FileUtil.setupOutputDir();
        return OUTPUT_DIR;
    }

    public static String getWorkingDirPath()
    {
        File f = new File("");
        return f.getAbsolutePath();
    }

    public static String getComponentsDirPath()
    {
        return getWorkingDirPath() + File.separator + "components";
    }

    public static String getCommonDirPath()
    {
        return getComponentsDirPath() + File.separator + "common"; 
    }

    public static String getDataViewerDirPath()
    {
        return getComponentsDirPath() + File.separator + "dataviewer";
    }

    public static String getHL7TransformationDirPath()
    {
        return getComponentsDirPath() + File.separator + "hl7Transformation";
    }

    public static String getModelMappingDirPath()
    {
        return getComponentsDirPath() + File.separator + "modelMapping";
    }

    public static String getSDTMTransformationDirPath()
    {
        return getComponentsDirPath() + File.separator + "sdtmTransformation";
    }

    public static String getUserInterfaceDirPath()
    {
        return getComponentsDirPath() + File.separator + "userInterface";
    }

    public static String getWebServicesDirPath()
    {
        return getComponentsDirPath() + File.separator + "webservices";
    }

    public static String getETCDirPath()  // inserted bt umkis 08/09/2006
    {
        File f = new File("./etc");
        return f.getAbsolutePath();
    }

    public static String getExamplesDirPath()
    {
        File f = new File("./workingspace/examples");
        return f.getAbsolutePath();
    }

    public static String getV2DataDirPath()
    {
        File f = new File(getWorkingDirPath() + File.separator + "data" + File.separator + "v2Meta");
        if ((!f.exists())||(!f.isDirectory()))
        {
            ClassLoaderUtil loaderUtil = null;
            try
            {
                loaderUtil = new ClassLoaderUtil("v2Meta");
            }
            catch(IOException ie)
            {
                System.err.println("Make V2 Meta Drectory : " + ie.getMessage());
                return null;
            }
            for(int i=0;i<loaderUtil.getSizeOfFiles();i++)
            {
                String path = loaderUtil.getPath(i);
                if (!path.trim().toLowerCase().endsWith(".dat")) continue;
                path = path.replace("/", File.separator);
                path = getWorkingDirPath() + File.separator + "data" + File.separator + path;
                int index = -1;
                for (int j=path.length();j>0;j--)
                {
                    String achar = path.substring(j-1, j);
                    if (achar.equals(File.separator))
                    {
                        index = j;
                        break;
                    }
                }
                //System.out.println("V2 Meta : " + path);
                if (index <= 0)
                {
                    System.err.println("V2 Meta file is invalid : " + path);
                    return null;
                }
                File dir = new File(path.substring(0,(index-1)));
                if ((!dir.exists())||(!dir.isDirectory()))
                {
                    if (!dir.mkdirs())
                    {
                        System.err.println("V2 Meta directory making failure : " + dir);
                        return null;
                    }
                }
                File datFile = new File(loaderUtil.getFileName(i));
                if ((!datFile.exists())||(!datFile.isFile()))
                {
                    System.err.println("Not Found This V2 Meta temporary file : " + path);
                    continue;
                }
                if (!datFile.renameTo(new File(path))) System.err.println("V2 Meta rename failure : " + path);

            }
        }
        return f.getAbsolutePath();
    }

    public static String getSchemaFile(String messageId) throws FileNotFoundException
    {
        //String fileLocation = StringUtils.searchMessageTypeFileName(Config.SCHEMA_LOCATION, messageId, "xsd");
       return searchMessageTypeFileName( messageId, "xsd");
    }

    /**
     * Copied from javaSig code
     * SInce 2005 Normative Edition, the name convention is changed. It has to following
     * HL7 v3 artifact naming convention: {UUDD_AAnnnnnnUVnn}.
     * - UU = Sub-Section code
     * - DD = Domain code
     * - AA = Artifact or Document code.
     * (Message Type will be MT) - nnnnnn = Six digit zero-filled number
     * - UV = Universal
     * - nn = ballot version
     *
     * Since the ballot version is different, starting from 01 to unknown, this function find related file
     * by guessing the version number
		 *
		 * THIS METHOD IS NOT USED BY JAVASIG CODE ANY MORE. IF IT WERE, IT WOULD HAVE TO BE
		 * CONVERTED TO NOT USER Files BUT Resources INSTEAD.
		 * TestParseAndBuild uses it, but that's O.K. Just those tests will eventually fail.
     *
     *
     * @param messageType
     * @param fileExtension
     * @return File Name
     */
  public static String searchMessageTypeFileName(String messageType, String fileExtension)
        throws FileNotFoundException
  {
        String schemaFileName = "";
        URL fileURL = null;
        File file = null;
//      String schemaFileName=Config.SCHEMA_LOCATION+messageType+ "." + fileExtension;
//      URL fileURL= ClassLoader.getSystemResource(schemaFileName);
//      if (fileURL!=null)
//    	  return fileURL.getFile();

	  for (int i = -1; i < 100; i++)
      {
          String pad = "";
          if (i < 0) pad = "";
          else pad = i < 10 ? "UV0" + i : "UV" + String.valueOf(i);
          schemaFileName = Config.SCHEMA_LOCATION+messageType+pad + "." + fileExtension;
          String schemaFileNamePath=FileUtil.getETCDirPath() + File.separator + schemaFileName;
          file = new File(schemaFileNamePath);
          if ((file.exists())&&(file.isFile())) return file.getAbsolutePath();

          fileURL= ClassLoader.getSystemResource(schemaFileName);
          if (fileURL!=null)
        	  return fileURL.getFile();
      }
	  //Throw exception since file is not found....
      throw new FileNotFoundException("File Directory:" + Config.SCHEMA_LOCATION + " Message Type:" + messageType
                + " File Extenstion:" + fileExtension);
  }

    /**
     * Return a convenient UI Working Directory, which may or may not be the same as the value from getWorkingDirPath().
     * @return a convenient UI Working Directory, which may or may not be the same as the value from getWorkingDirPath().
     */
    public static String getUIWorkingDirectoryPath()
    {
        File f = new File("./workingspace");
        if ((!f.exists())||(!f.isDirectory()))
        {
            f.mkdirs();
        }
        return f.getAbsolutePath();
    }

    /**
     * Generat a Temporary File Name at workingspace directory.
     * @return a Temporary File Name.
     */
    public static String getTemporaryFileName() // inserted by umkis 08/09/2006
    {
        return getTemporaryFileName(Config.TEMPORARY_FILE_EXTENSION);
    }

    /**
     * Generat a Temporary File Name at workingspace directory.
     * @param extension the extention of generated temp file
     * @return a Temporary File Name.
     */
    public static String getTemporaryFileName(String extension) // inserted by umkis 08/09/2006
    {
        DateFunction dateFunction = new DateFunction();
        String dateFormat = dateFunction.getDefaultDateFormatString();
        if (!dateFormat.endsWith("SSS")) dateFormat = dateFormat + "SSS";
        try
        {
            return getUIWorkingDirectoryPath() + File.separator + Config.TEMPORARY_FILE_PREFIX + (new DateFunction()).getCurrentTime(dateFormat) + "_" + getRandomNumber(4) + extension;
        }
        catch(FunctionException fe)
        {
            return getUIWorkingDirectoryPath() + File.separator + Config.TEMPORARY_FILE_PREFIX + (new DateFunction()).getCurrentTime() + "_" + getRandomNumber(4) + extension;
        }
    }

    /**
     * Check the parameter whether a temporary file name or not.
     *
     * @param fileName
     * @return true if a temporary file name, else is false.
     */
    public static boolean isTemporaryFileName(String fileName) // inserted by umkis 08/10/2006
    {
        if (fileName.length() > 1024) return false;
        if (
               //(fileName.endsWith(Config.TEMPORARY_FILE_EXTENSION)) &&
               (fileName.indexOf(Config.TEMPORARY_FILE_PREFIX) >= 0)
           )
            return true;
        else return false;
    }

    /**
     * Create a temporary file which includes the received string parameter.
     *
     * @param tempFileName file name of this temporary file.
     * @param string parameter which would like to be saved into this temporary file.
     * @throws IOException when saving is failed.
     */
    public static void saveStringIntoTemporaryFile(String tempFileName, String string) throws IOException // inserted by umkis 12/26/2006
    {
        FileWriter fw = null;
        File file = null;
        try
        {
            fw = new FileWriter(tempFileName);
            fw.write(string);
            fw.close();

            file = new File(tempFileName);
        }
        catch(Exception ie)
        {
            throw new IOException("File Writing Error(" + tempFileName + ") : " + ie.getMessage() + ", value : " + string);
        }

        file.deleteOnExit();
    }
    public static List<String> readFileIntoList(String fileName) throws IOException
    {
        List<String> list = new ArrayList<String>();

        FileReader fr = null;

        try { fr = new FileReader(fileName); }
        catch(FileNotFoundException fe) { throw new IOException("FileNotFoundException in FileUtil.readFileIntoList() : " + fileName); }

        BufferedReader br = new BufferedReader(fr);
        String readLineOfFile = "";

        try { while((readLineOfFile=br.readLine())!=null) list.add(readLineOfFile); }
        catch(IOException ie) { throw new IOException("File reading Error in FileUtil.readFileIntoList() : " + fileName); }

        try
        {
            fr.close();
            br.close();
        }
        catch(IOException ie) { throw new IOException("File Closing Error in FileUtil.readFileIntoList() : " + fileName); }
        return list;
    }
    public static String readFileIntoString(String fileName)
    {
        List<String> list = null;
        try { list = readFileIntoList(fileName); }
        catch(IOException ie) { return null; }
        String output = "";
        for(int i=0;i<list.size();i++) output = output + list.get(i) + "\r\n";
        return output.trim();
    }

    public static String findODIWithDomainName(String str) throws IOException
    {
        if (ODI_FILE == null)
        {
            ClassLoaderUtil loaderUtil = new ClassLoaderUtil("instanceGen/HL7_ODI.csv");
            if (loaderUtil.getFileNames().size() == 0) throw new IOException("HL7_ODI.csv file class loading failure.");
            ODI_FILE = new File(loaderUtil.getFileNames().get(0));
            ODI_FILE.deleteOnExit();
        }
        FileReader fr = null;
        //String fileName = ODI_FILE_NAME;
        try { fr = new FileReader(ODI_FILE); }
        catch(FileNotFoundException fe) { throw new IOException("ODI File : FileNotFoundException in FileUtil.readFileIntoList() : " + ODI_FILE.getName()); }

        BufferedReader br = new BufferedReader(fr);
        String readLineOfFile = "";
        String result = "";
        try
        {
            while((readLineOfFile=br.readLine())!=null)
            {
                //System.out.println("CCCYYYY : " + readLineOfFile);
                if ((readLineOfFile.startsWith("1."))||(readLineOfFile.startsWith("2."))||(readLineOfFile.startsWith("3."))||(readLineOfFile.startsWith("4."))) {}
                else continue;

                StringTokenizer st = new StringTokenizer(readLineOfFile, ",");
                int n = 0;
                String odi = "";
                String domainName = "";
                while(st.hasMoreTokens())
                {
                    if (n == 0) odi = st.nextToken().trim();
                    if (n == 1) domainName = st.nextToken().trim();
                    if (n == 2) break;
                    n++;
                }
                //System.out.println("CCCXX : " + str + ", " + domainName + ", " + odi);
                if (str.trim().equals(domainName))
                {
                    result = odi;
                    break;
                }
            }
        }
        catch(IOException ie)
        {
            throw new IOException("ODI File reading Error in FileUtil.readFileIntoList() : " + ODI_FILE.getName());
        }

        try
        {
            fr.close();
            br.close();
        }
        catch(IOException ie) { throw new IOException("ODI File Closing Error in FileUtil.readFileIntoList() : " + ODI_FILE.getName()); }
        return result;
    }



    /**
     * Create a random integer number with digit number which was given by the caller.
     *   For example, when digit number is 5, return value can be 34562 or 98123.
     * @param digit number of generated random number
     * @return generated random number
     */
        public static int getRandomNumber(int digit)  // inserted by umkis 08/10/2006
    {
        if (digit <= 0) return 0;

        int in = 1;
        int la = 0;
        int sa = 0;
        for(int x=0;x<digit;x++)
        {
            in = in * 10;
        }
        la = in;
        sa = la / 10;

        return getRandomNumber(sa, la);

    }
    /**
     * Create a random integer number between max and min number which was given by the caller.
     * @param min : number of generated minimum random number
     * @param max : number of generated maximum random number
     * @return generated random number
     */
    public static int getRandomNumber(int min, int max)  // inserted by umkis 06/13/2007
    {
        if (min == max) return max;
        if (min > max)
        {
            int t = max;
            max = min;
            min = t;
        }
        Random rnd = new Random();
        int in = 0;
        int in1 = 0;
        if (max <= 0)
        {
            int min1 = 0 - max;
            int max1 = 0 - min;
            while(true)
            {
                in = rnd.nextInt();

                in1 = in % max1;
                if (in1 >= min1) break;
            }
            in1 = 0 - in1;
        }
        else
        {
            while(true)
            {
                in = rnd.nextInt();

                in1 = in % max;
                if (in1 >= min) break;
            }
        }
        return in1;
    }
   
    /**
     * Delete a lck file from the output directory.  A lck file is a temporary file that is
     * created by the logger.
     *
     * @param filename
     */
    public static void deleteLckFile(String filename)
    {
        File lckFile = new File(FileUtil.getOutputDir().getAbsolutePath() + File.separator +
                filename + ".lck");
        if (lckFile != null && lckFile.delete())
        {
            // do nothing
        }
        else
        {
            // lck file couldn't be deleted.
        }

    }

    public static String outputFile(String filename, String data) throws IOException
    {
        String fileName = FileUtil.getOutputDir().getAbsolutePath() + File.separator + filename;
        FileWriter out = new FileWriter(fileName);
        out.write(data);
        out.flush();
        out.close();
        return fileName;
    }

    /**
     * Create a filehandler to a log file that is located in the output directory.
     *
     * @param fileName the log that you want to create
     * @return the filehandler
     * @throws IOException
     */
    public static FileHandler getLogFileHandle(String fileName) throws IOException
    {
        return new FileHandler(FileUtil.getOutputDir().getAbsolutePath() + File.separator + fileName);
    }

    /**
     * Search for a file by searching the classpath
     * (calling ClassLoader.getSystemResource()).
     *
     * @param fileName Name of the file you are looking for.
     * @return the path to the file
     * @throws FileNotFoundException
     */
    public static String fileLocateOnClasspath(String fileName)
            throws FileNotFoundException
    {
        if (fileName.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) fileName = fileName.replace(Config.CAADAPTER_HOME_DIR_TAG, getWorkingDirPath());
        File f = new File(fileName);
        if (f.exists())
        {
            return f.getAbsolutePath();
        }

        URL u = null;
        u = ClassLoader.getSystemResource(fileName);
        if (u == null)
        {
            throw new FileNotFoundException(fileName + " - make sure the file is on the classpath.");
        }
        else
        {
            return u.getFile();
        }
    }

    public static File fileLocate(String directory, String fileName)throws FileNotFoundException{
        return new File(FileUtil.filenameLocate(directory,fileName));
    }


    /**
     * Search for a file at the specified location and if it's not
     * found there look on the classpath by calling filenameLocate(fileName).
     *
     * @param directory the directory to look first
     * @param fileName  the name fo the file
     * @return the path to the file
     * @throws FileNotFoundException
     */
    public static String filenameLocate(String directory, String fileName)
            throws FileNotFoundException
    {
        if (directory.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) directory = directory.replace(Config.CAADAPTER_HOME_DIR_TAG, getWorkingDirPath());
        if (fileName.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) fileName = fileName.replace(Config.CAADAPTER_HOME_DIR_TAG, getWorkingDirPath());

        // check directory + filename
        File f = new File(directory + File.separator + fileName);
        if (f.exists())
        {
            return f.getAbsolutePath();
        }
        // check just the filename
        f = new File(fileName);
        if (f.exists())
        {
            return f.getAbsolutePath();
        }

        String fileLocation = null;
        try
        {
            fileLocation = fileLocateOnClasspath(fileName);
        }
        catch (FileNotFoundException fnfe)
        {
            throw new FileNotFoundException(fileName + " - make sure " +
                    "the location is correct OR the file is on the classpath");
        }
        return fileLocation;
    }

    public static String getLogFilename(String fullFilename)
    {
        if (fullFilename.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) fullFilename = fullFilename.replace(Config.CAADAPTER_HOME_DIR_TAG, getWorkingDirPath());
        String justFileName = new File(fullFilename).getName();
        return justFileName + ".log";
    }

    /**
     * Return the extension part given file name.
     * For example, if the name of the file is "foo.bar", ".bar" will be returned
     * if includeDelimiter is true, or "bar" will be returned if includeDelimiter is false;
     * otherwise, if no extension is specified in the file name, empty string is
     * returned instead of null.
     *
     * @param file
     * @param includeDelimiter
     * @return the extension or an empty string if nothing is found
     */
    public static final String getFileExtension(File file, boolean includeDelimiter)
    {
        String result = "";
        if (file != null)
        {
            String absoluteName = file.getAbsolutePath();
            if (absoluteName != null)
            {
                int delimIndex = absoluteName.lastIndexOf(".");
                if (delimIndex != -1)
                {//include the . delimiter
                    if (!includeDelimiter)
                    {//skip the . delimiter
                        delimIndex++;
                    }
                    result = absoluteName.substring(delimIndex);
                }
            }
        }
        return result;
    }

    /**
     * Construct a list of V3 Message file names and return.
     * @param userSpecifiedFile
     * @param numberOfMessages
     * @param extension
     * @param extensionIncludesDelimiter
     * @return a list of V3 Message file names.
     */
    public static final java.util.List<java.io.File> constructHL7V3MessageFileNames(File userSpecifiedFile, int numberOfMessages, String extension, boolean extensionIncludesDelimiter)
    {
        java.util.List<File> resultList = new ArrayList<File>();
        if(userSpecifiedFile==null)
        {
            Log.logWarning(FileUtil.class, "constructHL7V3MessageFileNames(): user specified file is null.");
            return resultList;
        }
        String extensionLocal = getFileExtension(userSpecifiedFile, extensionIncludesDelimiter);
        String absoluteFileName = userSpecifiedFile.getAbsolutePath();
        if(GeneralUtilities.areEqual(extensionLocal, extension))
        {//already contains the given extension, need to strip off so as to append
            absoluteFileName = getFileNameWithoutExtension(absoluteFileName);
        }
        for(int i=1; i<=numberOfMessages; i++)
        {
            String fileName = absoluteFileName + "_" + i;
            File file = new File(fileName);
            file = appendFileNameWithGivenExtension(file, extension, extensionIncludesDelimiter);
            resultList.add(file);
        }
        return resultList;
    }

    /**
     * Return the absolute file name without the trailing file extension; return absoluteFileName itself if it does not contain any extension.
     * @param absoluteFileName
     * @return the absolute file name without the trailing file extension; return absoluteFileName itself if it does not contain any extension.
     */
    private static final String getFileNameWithoutExtension(String absoluteFileName)
    {
        if (absoluteFileName.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) absoluteFileName = absoluteFileName.replace(Config.CAADAPTER_HOME_DIR_TAG, getWorkingDirPath());
        if(absoluteFileName==null)
        {
            return absoluteFileName;
        }
        int extIndex = absoluteFileName.lastIndexOf(".");
        if(extIndex!=-1)
        {
            absoluteFileName = absoluteFileName.substring(0, extIndex);
        }
        return absoluteFileName;
    }

    /**
     * This function will return the file with the given extension. If it already contains, return immediately.
     * @param file
     * @param extension
     * @param extensionIncludesDelimiter
     * @return the File object contains the right file name with the given extension.
     */
    public static final File appendFileNameWithGivenExtension(File file, String extension, boolean extensionIncludesDelimiter)
    {
        String extensionLocal = getFileExtension(file, extensionIncludesDelimiter);
        if(GeneralUtilities.areEqual(extensionLocal, extension))
        {//already contains the given extension, return
            return file;
        }
        else
        {
            String newFileName = file.getAbsolutePath();
            if(extensionIncludesDelimiter)
            {
                newFileName += extension;
            }
            else
            {
                newFileName += "." + extension;
            }
            File resultFile = new File(newFileName);
            return resultFile;
        }
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
        saveStringIntoTemporaryFile(tempFileName, string);
        return tempFileName;
    }

    /**
     * This function will dawnload data from a InputStream and save them into a file.
     * @param addr url address
     * @return the File object contains the right file name with the given extension.
     * @throws IOException Any Exception will be passed into IOException
     */
    public static String downloadFromURLtoTempFile(String addr) throws IOException
    {
        if ((addr == null)||(addr.trim().equals(""))) throw new IOException("Null address.");
        URL ur = null;
        InputStream is = null;
		FileOutputStream fos = null;

        addr = addr.trim();
        String tempFile = "";
        if ((addr.length() >= 5)&&(addr.substring(addr.length()-4, addr.length()-3).equals(".")))
             tempFile = getTemporaryFileName(addr.substring(addr.length()-4));
        else tempFile = getTemporaryFileName();

        try
        {
            ur = new URL(addr);
        }
        catch(MalformedURLException ue)
        {
            throw new IOException("Invalid URL : " + ue.getMessage());
        }
        URLConnection uc = ur.openConnection();
        try
        {
            uc.connect();
        }
        catch(SocketTimeoutException se)
        {
            throw new IOException("SocketTimeoutException : " + se.getMessage());
        }

        return downloadFromInputStreamToFile(uc.getInputStream(), tempFile);
    }

    /**
     * This function will dawnload data from a InputStream and save them into a file.
     * @param is InputStream
     * @param fileName file name - this file will be deleted when system exit.
     * @return the File object contains the right file name with the given extension.
     * @throws IOException Any Exception will be passed into IOException
     */
    public static String downloadFromInputStreamToFile(InputStream is, String fileName) throws IOException
    {
        return downloadFromInputStreamToFile(is, fileName, true);
    }

    /**
     * This function will dawnload data from a InputStream and save them into a file.
     * @param is InputStream
     * @param fileName file name
     * @param deleteOnExit if true this file will be deleted when system exit.
     * @return the File object contains the right file name with the given extension.
     * @throws IOException Any Exception will be passed into IOException
     */
    public static String downloadFromInputStreamToFile(InputStream is, String fileName, boolean deleteOnExit) throws IOException
    {
        if (is == null) throw new IOException("Null InputStream ");
        if ((fileName == null)||(fileName.trim().equals(""))) throw new IOException("Null File Name.");

        DataInputStream dis = new DataInputStream(is);
        FileOutputStream fos = null;
        DataOutputStream dos = null;
        byte bt = 0;

        boolean started = false;
        

        while(true)
        {
            try { bt = dis.readByte(); }
            catch(IOException ie) { break; }
            catch(NullPointerException ie) { break; }

            if (!started)
            {
                try
                {
                    fos = new FileOutputStream(fileName);
                }
                catch(FileNotFoundException fe)
                {
                    throw new IOException("FileNotFoundException : " + fe.getMessage());
                }
                catch(SecurityException se)
                {
                    throw new IOException("SecurityException : " + se.getMessage());
                }
                dos = new DataOutputStream(fos);
                started = true;
            }
            dos.writeByte(bt);

        }

        if (fos == null) throw new IOException("This InputStream object is empty.");

        dis.close();
        dos.close();
        is.close();
        fos.close();

        if (deleteOnExit) setFileDeleteOnExit(fileName);

        return fileName;
    }

    /**
     * This makes parametered file delete when system exit.
     * @param fileName file name
     * @return true or false
     */
    public static boolean setFileDeleteOnExit(String fileName)
    {
        if ((fileName == null)||(fileName.trim().equals(""))) return false;
        File file = new File(fileName);
        if (!file.exists()) return false;
        file.deleteOnExit();
        return true;
    }

}

/**
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2007/08/08 23:05:48  umkis
 * update getV2DataDirPath()
 *
 * Revision 1.4  2007/07/14 20:16:02  umkis
 * add 'downloadFromInputStreamToFile()'
 *
 * Revision 1.3  2007/07/12 17:30:06  umkis
 * add 'getComponentsDirPath()' and directory paths of the componts.
 *
 * Revision 1.2  2007/07/09 15:39:58  umkis
 * Update for csv cardinality and test instance generating.
 *
 * Revision 1.1  2007/04/03 16:02:37  wangeug
 * initial loading of common module
 *
 * Revision 1.49  2006/12/28 20:50:36  umkis
 * saveValue() and readValue() in FunctionConstant
 *
 * Revision 1.48  2006/11/02 18:32:20  umkis
 * Some codes of the method 'downloadFromURLtoTempFile' was changed.
 *
 * Revision 1.47  2006/11/01 19:02:14  umkis
 * The method 'downloadFromURLtoTempFile' was added.
 *
 * Revision 1.46  2006/10/11 21:00:53  umkis
 * Change {caAdapter_Home} tag to absolute path name of the home directory.
 *
 * Revision 1.45  2006/09/19 18:06:11  umkis
 * add getV2DataDirPath()
 *
 * Revision 1.44  2006/09/18 18:32:46  umkis
 * change deleteTemporaryFiles() for checking that it was 10 minutes after generating the temp file.
 *
 * Revision 1.43  2006/08/10 20:32:02  umkis
 * isTemporaryFileName(String) was added.
 *
 * Revision 1.42  2006/08/10 20:11:29  umkis
 * Config.TEMPORARY_FILE_PREFIX is used for generate a temporary file name.
 *
 * Revision 1.41  2006/08/10 19:46:59  umkis
 * saveStringIntoTemporaryFile(String) and getRandomNumber(int) was added.
 *
 * Revision 1.40  2006/08/10 17:39:44  umkis
 * For more precisely distinguishing other temporary file, four dgits random number will be attached to filename.
 *
 * Revision 1.39  2006/08/09 22:53:48  umkis
 * getTemporayFileName(), deleteTemporaryFiles() and getETCDir() were added.
 *
 * Revision 1.38  2006/08/09 22:48:17  umkis
 * Just before closing mainFrame, all temporary files is deleted.
 *
 * Revision 1.37  2006/08/02 18:44:25  jiangsc
 * License Update
 *
 * Revision 1.36  2006/05/04 19:41:58  chene
 * Add 150003 test instance
 *
 * Revision 1.35  2006/01/03 19:16:53  jiangsc
 * License Update
 *
 * Revision 1.34  2006/01/03 18:56:26  jiangsc
 * License Update
 *
 * Revision 1.33  2005/12/30 22:23:30  chene
 * Update JavaDoc
 *
 * Revision 1.32  2005/12/29 23:06:16  jiangsc
 * Changed to latest project name.
 *
 * Revision 1.31  2005/12/29 15:39:06  chene
 * Optimize imports
 *
 * Revision 1.30  2005/12/14 21:29:40  giordanm
 * no message
 *
 * Revision 1.29  2005/11/02 22:36:06  chene
 * change "\\" to "/"
 *
 * Revision 1.28  2005/10/26 21:30:10  chene
 * Clean up e.printStackTrace()
 *
 * Revision 1.27  2005/10/26 17:33:13  giordanm
 * bug #129
 *
 * Revision 1.26  2005/10/25 20:20:25  chene
 * Support Schema Location
 *
 * Revision 1.25  2005/10/20 18:26:58  jiangsc
 * Updated to point to the UI default example directory.
 *
 * Revision 1.24  2005/10/19 21:49:21  chene
 * creat new directory workingspace, move example directory to there
 *
 * Revision 1.23  2005/08/22 17:32:45  giordanm
 * change the file attribute within BaseComponent from a String to a File,  this checkin also contains some refactor work to the FileUtil.
 *
 * Revision 1.22  2005/08/11 22:10:38  jiangsc
 * Open/Save File Dialog consolidation.
 *
 * Revision 1.21  2005/08/09 22:53:04  jiangsc
 * Save Point
 *
 * Revision 1.20  2005/08/08 18:05:50  giordanm
 * a bunch of checkins that changes hard coded paths to relative paths.
 *
 * Revision 1.19  2005/07/19 22:28:03  jiangsc
 * 1) Renamed FunctionalBox to FunctionBox to be consistent;
 * 2) Added SwingWorker to OpenObjectToDbMapAction;
 * 3) Save Point for Function Change.
 *
 * Revision 1.18  2005/06/24 20:58:08  jiangsc
 * Save Point
 *
 * Revision 1.17  2005/06/21 23:03:02  jiangsc
 * Put in new CSVPanel Implementation.
 *
 * Revision 1.16  2005/06/08 23:02:02  jiangsc
 * Implemented New UI.
 *
 * Revision 1.15  2005/06/02 22:12:02  chene
 * no message
 *
 * Revision 1.14  2005/05/17 20:07:16  chene
 * Updated CVS tag test
 *
 * Revision 1.13  2005/05/17 20:05:38  chene
 * Updated CVS tag test
 *
 * Revision 1.12  2005/05/17 17:33:07  giordanm
 * remove the <br> in the javadoc heading
 *
 * Revision 1.11  2005/05/17 17:15:45  giordanm
 * another minor change to the CVS javadoc comments.
 *
 * Revision 1.10  2005/05/17 17:01:20  giordanm
 * Playing around with CVS keywords / javadoc generation.
 *
 */
 
