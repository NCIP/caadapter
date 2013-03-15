/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */


package gov.nih.nci.cbiit.cmts.util;

import gov.nih.nci.cbiit.cmts.common.XSDParser;
import gov.nih.nci.cbiit.cmts.function.DateFunction;
import gov.nih.nci.cbiit.cmts.function.FunctionException;
import gov.nih.nci.cbiit.cmts.ui.util.GeneralUtilities;

import java.io.*;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.logging.FileHandler;

/**
 * File related utility class
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-29 22:18:18 $
 *
 */

public class FileUtil
{
    private static final String OUTPUT_DIR_NAME = "out";
	private static final String SCHEMA_LOCATION = null;
	private static final String TEMPORARY_FILE_EXTENSION = null;
	private static final String TEMPORARY_FILE_PREFIX = null;
	private static final String CAADAPTER_HOME_DIR_TAG = null;
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
    
    public static String getAssociatedFileAbsolutePath(String holderFile, String associatedFile)
    {
    	if(associatedFile.indexOf(File.separator)>-1)
    		return associatedFile;
    	File holder=new File(holderFile);
    	File associted=new File(associatedFile);
    	if (!holder.exists())
    		return associatedFile;
    	if (holder.isDirectory())
    		return associatedFile;

    	String holderParent=holder.getParent();

    	String rntPath=holderParent+File.separator+associatedFile;
    	
    	return rntPath;
    }
/**
 * Compare the name/absolute path of a holder file with its associated file
 * return the name of the assoicated file without parentPath if they have the same
 * parent, otherwise return the input value of the associated file
 * @param holderFile the name/absolutePath of a holder file
 * @param associatedFile the name/absolutePath of an associated file
 * @return name of the associated file to be used as reference from the holder file
 */
    public static String getAssociatedFileRelativePath(String holderFile, String associatedFile)
    {
    	File holder=new File(holderFile);
    	File associted=new File(associatedFile);
    	if (!holder.exists())
    		return associatedFile;
    	if (holder.isDirectory())
    		return associatedFile;
    	if (!associted.exists())
    		return associatedFile;
    	if (associted.isDirectory())
    		return associatedFile;
    	String holderParent=holder.getParent();
    	String associateParent=associted.getParent();
    	if (!holderParent.equals(associateParent))
    		return associatedFile;
    	
    	return associted.getName();
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
        f.mkdirs();
        return f.getAbsolutePath();
        //return getV2DataDirPath(null);
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
  public static String searchMessageTypeSchemaFileName(String messageType, String fileExtension)
        throws FileNotFoundException
  {
      String schemaFileNamePath ="";
      for (int i = -1; i < 100; i++)
      {
          String pad = "";
          if (i < 0) pad = "";
          else pad = i < 10 ? "UV0" + i : "UV" + String.valueOf(i);
          String schemaFileName = SCHEMA_LOCATION+messageType+pad + "." + fileExtension;
          schemaFileNamePath=FileUtil.getWorkingDirPath() + File.separator + schemaFileName;
          File file = new File(schemaFileNamePath);
          if ((file.exists())&&(file.isFile()))
        	  return schemaFileName;
//        	  return file.getAbsolutePath();
          URL fileURL= ClassLoader.getSystemResource(schemaFileName);
          if (fileURL!=null)
        	  return schemaFileName;
//        	  return fileURL.getFile();
      }
	  //Throw exception since file is not found....
      throw new FileNotFoundException("File Directory:" + SCHEMA_LOCATION + " Message Type:" + messageType
                + " File Extenstion:" + fileExtension +  ", "+schemaFileNamePath);
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
        return getTemporaryFileName(TEMPORARY_FILE_EXTENSION);
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
            return getUIWorkingDirectoryPath() + File.separator + TEMPORARY_FILE_PREFIX + (new DateFunction()).getCurrentTime(dateFormat) + "_" + getRandomNumber(4) + extension;
        }
        catch(FunctionException fe)
        {
        	fe.printStackTrace();
            return getUIWorkingDirectoryPath() + File.separator + TEMPORARY_FILE_PREFIX + (new DateFunction()).getCurrentTime() + "_" + getRandomNumber(4) + extension;
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
               //(fileName.endsWith(TEMPORARY_FILE_EXTENSION)) &&
               (fileName.indexOf(TEMPORARY_FILE_PREFIX) >= 0)
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
        if (fileName.startsWith(CAADAPTER_HOME_DIR_TAG)) fileName = fileName.replace(CAADAPTER_HOME_DIR_TAG, getWorkingDirPath());
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
        if (directory.startsWith(CAADAPTER_HOME_DIR_TAG)) directory = directory.replace(CAADAPTER_HOME_DIR_TAG, getWorkingDirPath());
        if (fileName.startsWith(CAADAPTER_HOME_DIR_TAG)) fileName = fileName.replace(CAADAPTER_HOME_DIR_TAG, getWorkingDirPath());

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
        if (fullFilename.startsWith(CAADAPTER_HOME_DIR_TAG)) fullFilename = fullFilename.replace(CAADAPTER_HOME_DIR_TAG, getWorkingDirPath());
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
            //Log.logWarning(FileUtil.class, "constructHL7V3MessageFileNames(): user specified file is null.");
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
        if (absoluteFileName.startsWith(CAADAPTER_HOME_DIR_TAG)) absoluteFileName = absoluteFileName.replace(CAADAPTER_HOME_DIR_TAG, getWorkingDirPath());
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
     * Retrieve a resource URL: work for both standealone and Webstart deployment
     * @param rscName
     * @return URL
     */
    public static URL retrieveResourceURL(String rscName)
    {
        if (rscName == null) return null;
        rscName = rscName.trim();
        if (rscName.equals("")) return null;

        URL rtnURL=null;
    	//System.out.println("FileUtil.retrieveResourceURL().1.resourceName:"+rscName);
    	rtnURL=Thread.currentThread().getClass().getResource("/"+rscName);
		//System.out.println("FileUtil.retrieveResourceURL().2.Thread.currentThread().getClass().getResource..standalone URL:/"+rscName+"="+rtnURL);
		if (rtnURL==null)
		{
			rtnURL=Thread.currentThread().getClass().getResource(rscName);
			//System.out.println("FileUtil.retrieveResourceURL().3.Thread.currentThread().getClass().getResource..standalone URL:"+rscName+"="+rtnURL);
		}
		//load resource for webstart deployment
		if (rtnURL==null)
		{
			rtnURL=FileUtil.class.getClassLoader().getResource(rscName);
			//System.out.println("FileUtil.retrieveResourceURL().4.FileUtil.class.getClassLoader().getResource..webstart URL:"+rscName+"="+rtnURL);
			if (rtnURL==null)
			{
				rtnURL=FileUtil.class.getClassLoader().getResource("/"+rscName);
				//System.out.println("FileUtil.retrieveResourceURL().5.FileUtil.class.getClassLoader().getResource..webstart URL:/"+rscName+"="+rtnURL);
			}
		}

        String path = FileUtil.searchFile(rscName);
        if (path != null)
        {
            File pathF = new File(path);

            if (pathF.exists())
            {
                try
                {
                    rtnURL = pathF.toURI().toURL();
                }
                catch(MalformedURLException me)
                {
                    System.out.println("FileUtil.retrieveResourceURL().6. MalformedURLException : " + me.getMessage());
                }
            }
            //else System.out.println("FileUtil.retrieveResourceURL().7.");
        }
        //else System.out.println("FileUtil.retrieveResourceURL().8.");
        if (rtnURL == null) System.out.println("This resource file cannot be found : " + rtnURL);
        return rtnURL;
    }
    
    public static String searchFile(String fileName)
    {
        return searchFile(null, null, fileName, null, true);
    }
    public static String searchDir(String dirName)
    {
        return searchFile(null, null, dirName, null, false);
    }
    public static String searchFile(String fileName, File startDir)
    {
        return searchFile(startDir, null, fileName, null, true);
    }
    public static String searchDir(String dirName, File startDir)
    {
        return searchFile(startDir, null, dirName, null, false);
    }
    private static String searchFile(File startDir, File dir, String fileName, List<String> searchedDirList, boolean isFile)
    {
        boolean isStart = false;
        if (searchedDirList == null)
        {
            isStart = true;
            searchedDirList = new ArrayList<String>();
        }
        if (fileName == null) return null;
        fileName = fileName.trim();
        if (fileName.equals("")) return null;

        File ff = new File(fileName);
        if ((ff.exists())&&(ff.isFile())) return ff.getAbsolutePath();

        String c = "";

        for (int i=0;i<fileName.length();i++)
        {
            String achar = fileName.substring(i, i+1);

            if (achar.equals("/")) c = c + File.separator;
            else c = c + achar;
        }
        fileName = c;

        if (fileName.endsWith(File.separator)) fileName = fileName.substring(0, fileName.length()-File.separator.length());
        if (fileName.startsWith(File.separator)) fileName = fileName.substring(File.separator.length());

        if (dir == null)
        {
            if (startDir != null)
            {
                dir = startDir;
            }
            else
            {
                File f = new File(fileName);
                if (f.exists())
                {
                    if ((isFile)&&(f.isFile())) return f.getAbsolutePath();
                    if ((!isFile)&&(f.isDirectory())) return f.getAbsolutePath();
                }
                dir = new File(getWorkingDirPath());
                startDir = dir;
            }
        }

        if ((!dir.exists())||(!dir.isDirectory())||(dir.isHidden())) return null;

        String dirName = dir.getAbsolutePath();
        if (!dirName.endsWith(File.separator)) dirName = dirName + File.separator;
        File f = new File(dirName + fileName);

        if (f.exists())
        {
            if ((isFile)&&(f.isFile())) return f.getAbsolutePath();
            if ((!isFile)&&(f.isDirectory())) return f.getAbsolutePath();
        }
        searchedDirList.add(dir.getAbsolutePath());
        File[] files = dir.listFiles();
        if ((files == null)||(files.length == 0)) return null;
        for(File file:files)
        {
            String abFileName = file.getAbsolutePath();

            if (file.isDirectory())
            {
                boolean isSearchedDir = false;
                for(String line:searchedDirList)
                {
                    if (line.equals(abFileName)) isSearchedDir = true;
                }
                if (!isSearchedDir)
                {
                    String res = searchFile(startDir, file, fileName, searchedDirList, isFile);
                    if (res != null) return res;
                }
            }
        }
        if (isStart)
        {
            if (dir.getParentFile() == null) return null;
            String dName = startDir.getName();
            if ((dName.equalsIgnoreCase("bin"))||(dName.equalsIgnoreCase("dist")))
            {
                dir = dir.getParentFile();
                String res = searchFile(startDir, dir, fileName, searchedDirList, isFile);
                if (res != null) return res;
            }
        }
        return null;
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

    public static String getRelativePath(File f){
    	String workDir = new File("").getAbsolutePath();
    	String fPath = f.getAbsolutePath();
    	String ret = null;
    	if(fPath.startsWith(workDir)){
    		ret = fPath.substring(workDir.length());
    		ret = ret.replace('\\', '/');
    		while(ret.startsWith("/")){
    			ret = ret.substring(1);
    		}
    	}
    	return ret;
    }

	/**
	 * Utility method to get resource
	 * @param name - resource name
	 * @return URL of resource
	 */
	public static URL getResource(String name){
		URL ret = null;
		ret = XSDParser.class.getClassLoader().getResource(name);
		if(ret!=null) return ret;
		ret = XSDParser.class.getClassLoader().getResource("/"+name);
		if(ret!=null) return ret;
		ret = ClassLoader.getSystemResource(name);
		if(ret!=null) return ret;
		ret = ClassLoader.getSystemResource("/"+name);
        if(ret!=null) return ret;
        ret = findFile(name);
        return ret;
	}
	
	/**
	 * Utility method to get resource
	 * @param name
	 * @return An enumeration of URL objects for the resource
	 */
    public static Enumeration<URL> getResources(String name) {
        Enumeration<URL> ret = null;
        try {
            ret = FileUtil.class.getClassLoader().getResources(name);
            if(ret!=null&&ret.hasMoreElements()) return ret;
            ret = FileUtil.class.getClassLoader().getResources("/"+name);
            if(ret!=null&&ret.hasMoreElements()) return ret;
            ret = ClassLoader.getSystemResources(name);
            if(ret!=null&&ret.hasMoreElements()) return ret;
            ret = ClassLoader.getSystemResources("/"+name);
            if(ret!=null&&ret.hasMoreElements()) return ret;
            final URL urlx = findFile(name);
            if (urlx != null)
            {
                return new Enumeration<URL>()
                {
                    private URL url = urlx;

                    public URL nextElement()
                    {
                        if (url == null)
                        {
                            throw new NoSuchElementException();
                        }
                        URL u = url;
                        url = null;
                        return u;
                    }

                    public boolean hasMoreElements()
                    {
                        return (url != null);
                    }
                };
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Utility method to find file in dir or zip,jar file
     * @param name
     * @return A file URL object
     */
    public static URL findFile(String name)
    {
        if ((name == null)||(name.trim().equals(""))) return null;
        name = name.trim();
        return findFileExe(name, new File(FileUtil.getWorkingDirPath()));
    }
    private static URL findFileExe(String name, File dir)
    {
        String sp = File.separator;
        String nName = name;

        if (dir.isDirectory())
        {
            File[] files = dir.listFiles();
            if (files.length == 0) return null;
            List<File> dFiles = new ArrayList<File>();
            List<File> zFiles = new ArrayList<File>();

            if ((!sp.equals("/"))&&(name.indexOf("/") >= 0)) nName = nName.replace("/", sp);
            if (nName.startsWith(sp)) nName = nName.substring(sp.length());
            if (nName.endsWith(sp)) nName = nName.substring(0, nName.length() - sp.length());

            for (File f:files)
            {
                if (f.isDirectory()) dFiles.add(f);
                else if (f.isFile())
                {
                    String fName = f.getAbsolutePath();
                    if ((fName.endsWith(".zip"))||(fName.endsWith(".jar"))) zFiles.add(f);
                    //System.out.println(" === Searching (dir) : " + f.getAbsolutePath() );
                    if (fName.endsWith(sp + nName))
                    {
                        try
                        {
                            URL url = f.toURI().toURL();
                            //System.out.println("Find File : " + url);
                            return url;
                        }
                        catch(MalformedURLException me)
                        {
                            System.out.println("MalformedURLException (" + f.getAbsolutePath() + ") : " + me.getMessage());
                        }
                    }
                }
            }
            for (File f:zFiles)
            {
                URL ff = findFileExe(name, f);
                if (ff != null) return ff;
            }
            for (File f:dFiles)
            {
                URL ff = findFileExe(name, f);
                if (ff != null) return ff;
            }
            return null;
        }

        if ((!sp.equals("/"))&&(name.indexOf(sp) >= 0)) nName = nName.replace(sp, "/");
        if (nName.startsWith("/")) nName = nName.substring(1);
        if (nName.endsWith("/")) nName = nName.substring(0, nName.length() - 1);

        URL url = null;
        try
        {
            url = dir.toURI().toURL();
        }
        catch(MalformedURLException me)
        {
            System.out.println("MalformedURLException(dir) (" + dir.getAbsolutePath() + ") : " + me.getMessage());
            return null;
        }

        JarFile jar = null;
        try
        {
            jar = new JarFile(dir);
        }
        catch(IOException ie)
        {
            System.out.println("This is neither a zip nor a jar file  : " + dir.getAbsolutePath());
            return null;
        }
        Enumeration<JarEntry> entries = jar.entries();
        while(entries.hasMoreElements())
        {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            //System.out.println(" === Searching (jar) : " + dir.getAbsolutePath() + " : " + entryName );
            if ((entryName.endsWith("/" + nName))||(entryName.equals(nName)))
            {
                URL fUrl = null;
                String strURL = "jar:" + url.toString() + "!/" + entryName;
                try
                {
                    fUrl = new URL(strURL);
                }
                catch(MalformedURLException me)
                {
                    System.out.println("MalformedURLException(entry) (" + strURL + ") : " + me.getMessage());
                    return null;
                }
                //System.out.println("Find URL (entry) (" + strURL + ") : " + fUrl.toString());
                return fUrl;
            }
        }
        return null;
    }
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.3  2008/12/09 19:04:17  linc
 * HISTORY: First GUI release
 * HISTORY:
 * HISTORY: Revision 1.2  2008/12/03 20:46:14  linc
 * HISTORY: UI update.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/10/27 20:06:30  linc
 * HISTORY: GUI first add.
 * HISTORY:
 */

