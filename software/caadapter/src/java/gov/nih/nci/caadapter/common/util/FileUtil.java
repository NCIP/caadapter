/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common.util;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.function.DateFunction;
import gov.nih.nci.caadapter.common.function.FunctionException;

//import gov.nih.nci.caadapter.hl7.mif.NormativeVersionUtil;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.logging.FileHandler;

/**
 * File related utility class
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: altturbo $
 * @version $Revision: 1.42 $
 */

public class FileUtil
{
    private static final String OUTPUT_DIR_NAME = "out";
    private static File OUTPUT_DIR = null;
    private static File ODI_FILE = null;


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

    public static String getETCDirPath()  // inserted by umkis 08/09/2006
    {
        File f = new File("./etc");
        return f.getAbsolutePath();
    }

    public static String getExamplesDirPath()
    {
        File f = new File("./workingspace/examples");
        return f.getAbsolutePath();
    }


    public static String searchPropertyAsFilePath(String key)
    {
        return searchProperty(null, key, false, true);
    }
    public static String searchProperty(String key)
    {
        return searchProperty(null, key, false, false);
    }
    public static String searchProperty(String key, boolean useProperty)
    {
        return searchProperty(null, key, useProperty, false);
    }
    private static String searchProperty(File dir, String key, boolean useProperty, boolean isFilePath)
    {
        String rr = searchPropertyExe(dir, key, useProperty, isFilePath);
        if (dir == null)
        {
            if (rr == null)
            {
                String dir2 = searchDir("caAdapterWS" + File.separator + "META-INF");
                if (dir2 != null) rr = searchPropertyExe(new File(dir2), key, useProperty, isFilePath);
            }
        }
        if (rr != null)
        {
            if ((rr.toLowerCase().startsWith(Config.CAADAPTER_HOME_DIR_TAG.toLowerCase()))&&(!isFilePath))
            {
                String homeDir = getWorkingDirPath().trim();
                String subDir = rr.substring(Config.CAADAPTER_HOME_DIR_TAG.length()).trim();
                if (!File.separator.equals("/")) subDir = subDir.replace("/", File.separator);
                else subDir = subDir.replace("\\", File.separator);
                if ((!homeDir.endsWith(File.separator))&&(!subDir.startsWith(File.separator))) rr = homeDir + File.separator + subDir;
                else if ((homeDir.endsWith(File.separator))&&(subDir.startsWith(File.separator))) rr = homeDir + subDir.substring(File.separator.length());
                else rr = homeDir + subDir;
            }
        }
        return rr;
    }
    private static String searchPropertyExe(File dir, String key, boolean useProperty, boolean isFilePath)
    {
        File sDir = null;
        if (dir == null) sDir = new File(getWorkingDirPath());
        else
        {
            if (!dir.isDirectory()) return null;
            sDir = dir;
        }

        String res = null;
        File[] files = sDir.listFiles();
        for(File file:files)
        {
            String fName = file.getName();
            if (file.isFile())
            {
                if ((fName.toLowerCase().endsWith(".properties"))||
                    (fName.toLowerCase().endsWith(".property"))) {}
                else continue;

                if (useProperty)
                {
                    res = getPropertyFromComponentPropertyFile(file.getAbsolutePath(), key);
                    if (res != null)
                    {
                        if (isFilePath)
                        {
                            String path = checkValidFilePath(res);
                            if (path != null) return path;
                        }
                        else return res;
                    }
                    continue;
                }

                List<String> list = null;
                try
                {
                    list = readFileIntoList(file.getAbsolutePath());
                }
                catch(IOException ie)
                {
                    continue;
                }

                for (String line:list)
                {
                    line = line.trim();
                    if (line.startsWith("#")) continue;
                    if (line.startsWith("!")) continue;
                    int idx = line.indexOf("=");
                    if (idx < 0) idx = line.indexOf(":");
                    if (idx <= 0) continue;
                    String keyS = line.substring(0, idx).trim();
                    if (!keyS.equals(key.trim())) continue;
                    res = line.substring(idx+1).trim();
                    if (isFilePath)
                    {
                        String path = checkValidFilePath(res);
                        if (path != null) return path;
                    }
                    else return res;
                }

            }
            else if (file.isDirectory())
            {
                if ((fName.equalsIgnoreCase("conf"))||
                    (fName.equalsIgnoreCase("etc"))) res = searchProperty(file, key, useProperty, isFilePath);
                if (res != null) return res;
            }
        }
        res = null;
        if (sDir.getName().equalsIgnoreCase("dist")) res = searchProperty(sDir.getParentFile(), key, useProperty, isFilePath);
        if (res != null) return res;
        return null;
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

    public static String checkValidFilePath(String data)
    {
        if (data == null) return null;
        data = data.trim();
        if (data.equals("")) return null;

        if (data.toLowerCase().startsWith(Config.CAADAPTER_HOME_DIR_TAG.toLowerCase()))
        {
            String homeDir = getWorkingDirPath().trim();
            String subDir = data.substring(Config.CAADAPTER_HOME_DIR_TAG.length()).trim();
            if (!File.separator.equals("/")) subDir = subDir.replace("/", File.separator);
            else subDir = subDir.replace("\\", File.separator);
            if ((!homeDir.endsWith(File.separator))&&(!subDir.startsWith(File.separator))) data = homeDir + File.separator + subDir;
            else if ((homeDir.endsWith(File.separator))&&(subDir.startsWith(File.separator))) data = homeDir + subDir.substring(File.separator.length());
            else data = homeDir + subDir;
        }

        while(true)
        {
            int idx = data.indexOf(File.separator + File.separator);
            if (idx < 0) break;
            data = data.substring(0, idx) + data.substring(idx + File.separator.length());
        }
        File file = new File(data);
        if (file.exists()) return file.getAbsolutePath();

        return null;
    }

   /*
    public static String getV3XsdFilePath()
    {
        String schemaPath= NormativeVersionUtil.getCurrentMIFIndex().getSchemaPath();

        File f = new File(schemaPath);
        if (!f.exists()) f = new File("../" + schemaPath);

        if (!f.exists())
        {
            System.err.println("Not Found V3 XSD Directory...");
            return null;
        }

        if (f.isDirectory()) return f.getAbsolutePath();

        String parent = f.getParent();
        if (!parent.endsWith(File.separator)) parent = parent + File.separator;
        File sdir = new File(parent + "schemas");
        if ((sdir.exists())&&(sdir.isDirectory())) return sdir.getAbsolutePath();

        System.err.println("Not Found V3 XSD Directory...");
        return null;
    }
    */


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
          String schemaFileName = Config.SCHEMA_LOCATION+messageType+pad + "." + fileExtension;
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
      throw new FileNotFoundException("File Directory:" + Config.SCHEMA_LOCATION + " Message Type:" + messageType
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
        return getTemporaryFileName(Config.TEMPORARY_FILE_EXTENSION);
    }

    /**
     * Generat a Temporary File Name at workingspace directory.
     * @param extension the extention of generated temp file
     * @return a Temporary File Name.
     */
    public static String getTemporaryFileName(String extension) // inserted by umkis 08/09/2006
    {
        tidyWorkingDir();
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
    public static String readFileIntoStringAllowException(String fileName) throws IOException
    {
        List<String> list = null;
        list = readFileIntoList(fileName);

        String output = "";
        for(int i=0;i<list.size();i++) output = output + list.get(i) + "\r\n";
        return output.trim();
    }
    public static String readFileIntoString(String fileName)
    {
        String out = "";
        try { out = readFileIntoStringAllowException(fileName); }
        catch(IOException ie) { return null; }
        return out;
    }

    public static String findODIWithDomainName(String str) throws IOException
    {
        if ((str == null)||(str.trim().equals(""))) return "";

        while(ODI_FILE == null)
        {
            ClassLoaderUtil loaderUtil = null;
            File f = new File("./lib");
            if ((f.exists())&&(f.isDirectory()))
            {
                File[] files = f.listFiles();
                for(File ff:files)
                {
                    if (!ff.isFile()) continue;
                    String ffN = ff.getAbsolutePath();
                    if (!ffN.toLowerCase().endsWith(".zip")) continue;
                    try
                    {
                        loaderUtil = new ClassLoaderUtil("instanceGen/HL7_ODI.csv", ffN, true);
                    }
                    catch(IOException ie)
                    {
                        loaderUtil = null;
                    }
                    if (loaderUtil != null) break;
                }
            }
            else return null;

            if (loaderUtil == null)
            {
                try
                {
                    loaderUtil = new ClassLoaderUtil("instanceGen/HL7_ODI.csv");
                }
                catch(IOException ie)
                {
                    loaderUtil = null;
                }
            }
            if ((loaderUtil == null)||(loaderUtil.getFileNames().size() == 0))
            {
                ODI_FILE = f;
                break;
            }

            //if (loaderUtil.getFileNames().size() == 0) throw new IOException("HL7_ODI.csv file class loading failure.");
            ODI_FILE = new File(loaderUtil.getFileNames().get(0));
            ODI_FILE.deleteOnExit();
            break;
        }

        if (ODI_FILE.isDirectory()) return null;

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
        if ((fileName == null)||(fileName.trim().equals(""))) throw new FileNotFoundException("Null file name...");
        else fileName = fileName.trim();
        if (fileName.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) fileName = fileName.replace(Config.CAADAPTER_HOME_DIR_TAG, getWorkingDirPath());

        // check just the filename
        File f = new File(fileName);
        if (f.exists())
        {
            return f.getAbsolutePath();
        }

        if ((directory == null)||(directory.trim().equals(""))) throw new FileNotFoundException("Null Dirctory...");
        else directory = directory.trim();
        if (directory.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) directory = directory.replace(Config.CAADAPTER_HOME_DIR_TAG, getWorkingDirPath());

        if (!directory.endsWith(File.separator)) directory = directory + File.separator;

        // check directory + filename
        f = new File(directory + fileName);
        if (f.exists())
        {
            return f.getAbsolutePath();
        }

        String temp = fileName;

        if (fileName.endsWith(File.separator)) fileName = fileName.substring(0, fileName.length()-File.separator.length());
        if (fileName.endsWith("/")) fileName = fileName.substring(0, fileName.length()-1);
        while(true)
        {
            int idx = fileName.indexOf(File.separator);
            int len = 0;
            if (idx >= 0) len = File.separator.length();
            else
            {
                idx = fileName.indexOf("/");
                if (idx >= 0) len = 1;
            }
            if (idx < 0) break;
            fileName = fileName.substring(idx + len);
        }

        f = new File(directory + fileName);
        if (f.exists())
        {
            //System.out.println("DDD : " + temp +" ; "+ fileName +" ; "+ f.getAbsolutePath());
            return f.getAbsolutePath();
        }

        fileName = temp;

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
		//FileOutputStream fos = null;

        addr = addr.trim();
        String tempFile = "";

        int idx = -1;
        for(int i=0;i<addr.length();i++)
        {
            String achar = addr.substring(i, i+1);
            if (achar.equals(".")) idx = i;
        }
        if (idx <= 0) tempFile = getTemporaryFileName();
        else tempFile = getTemporaryFileName(addr.substring(idx));

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

    public static String getPropertyFromComponentPropertyFile(String key)
    {
        return getPropertyFromComponentPropertyFile(null, key);
    }
    public static String getPropertyFromComponentPropertyFile(String propertyFile, String key)
    {
        if (key == null) return null;
        key = key.trim();
        if (key.equals("")) return null;
        String result = "";

        String path = "";
        String name = "";

        File file = null;

        if ((propertyFile == null)||(propertyFile.trim().equals("")))
        {
            path = CaadapterUtil.getPathOfComponentPropertyFile();
            name = CaadapterUtil.getNameOfComponentPropertyFile();
        }
        else
        {
            file = new File(propertyFile);
            if ((!file.exists())||(!file.isFile())) return null;
            path = propertyFile;
            name = propertyFile;
        }


        InputStream fi = null;
        //appConfig=new HashMap();
        //load caadapter component types to run
        Properties properties=new Properties();
        try
        {

        	File srcFile=new File(path);
        	if ((srcFile.exists())&&(srcFile.isFile()))
        	{
                //System.out.println("PP1 : " + path);
                fi =new FileInputStream(srcFile);
        	}
        	else
            {
                //System.out.println("PP2 : " + name);
                fi = CaadapterUtil.class.getClassLoader().getResource(name).openStream();
            }
            properties.load(fi);

            if (properties == null) return null;

            //read the value for each component and add it into the ActivatedList
            Enumeration propKeys=properties.keys();
            while (propKeys.hasMoreElements())
            {
                String onePropKey=(String)propKeys.nextElement();
                String onePropValue=(String)properties.getProperty(onePropKey);
                //System.out.println("Component Properties ("+path+") : " + onePropKey + " => " + onePropValue);
                if (onePropKey == null) continue;
                onePropKey = onePropKey.trim();
                if (onePropKey.equals("")) continue;
                if (onePropKey.equals(key))
                {
                    result = onePropValue;
                    //System.out.println("   *** This is the Key!!");
                }
            }
        }
        catch (Exception ex)
        {
            return null;
        }
        finally
        {
            if (fi != null) try
            {
                fi.close();
            }
            catch (IOException ignore)
            {}

        }
        if (result == null) return null;
        result = result.trim();
        if (result.equals("")) return null;
        return result;
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
    public static URL retrieveResourceURL(String rscName, String middle, String fileName)
    {
        if (rscName == null) rscName = "";
        else rscName = rscName.trim();

        if (rscName.equals("")) return null;

        if (middle == null) middle = "";
        else middle = middle.trim();
        if (fileName == null) fileName = "";
        else fileName = fileName.trim();

        String tt = fileName;
        if (!middle.equals("")) tt = middle + "/" + fileName;

        System.out.println("## Searching Resource ("+tt+") to file : " + rscName);
        if ((fileName.equals(""))&&(middle.equals(""))) return retrieveResourceURL(rscName);
        if ((rscName.equals(""))&&(middle.equals(""))) return retrieveResourceURL(fileName);
        URL url = null;

        while(true)
        {
            File ff = new File(rscName);

            if (!ff.exists()) break;

            if (ff.isDirectory())
            {
                if (ff.getName().equals("CVS")) return null;
                File[] list = ff.listFiles();
                for (File f:list)
                {
                    if (f.isHidden()) continue;
                    String fN = f.getAbsolutePath();
                    if (f.isFile())
                    {
                        if (f.getName().equals(fileName))
                        {
                            boolean cTag = false;

                            if (middle.equals("")) cTag = true;
                            else
                            {
                                String middle2 = middle;
                                String fN2 = f.getParentFile().getAbsolutePath();
                                if (!fN2.endsWith(File.separator)) fN2 = fN2 + File.separator;
                                if ((!File.separator.equals("/"))&&(middle2.indexOf("/") >= 0)) middle2 = middle2.replace("/", File.separator);
                                if (!middle2.endsWith(File.separator)) middle2 = middle2 + File.separator;
                                if (!middle2.startsWith(File.separator)) middle2 =  File.separator + middle2;
                                if (fN2.endsWith(middle2)) cTag = true;
                            }
                            if (cTag)
                            {
                                try
                                {
                                    url = f.toURI().toURL();
                                }
                                catch(MalformedURLException me)
                                {
                                    url = null;
                                }
                                if (url != null) break;
                            }
                        }
                        if ((fN.toLowerCase().endsWith(".zip"))||(fN.toLowerCase().endsWith(".jar")))
                        {
                            URL res = retrieveResourceURL(fN, middle, fileName);
                            if (res != null)
                            {
                                url = res;
                                break;
                            }
                        }
                    }
                    else if (f.isDirectory())
                    {
                        URL res = retrieveResourceURL(fN, middle, fileName);
                        if (res != null)
                        {
                            url = res;
                            break;
                        }
                    }
                    else continue;
                }
                break;
            }
            if (!ff.isFile()) break;

            String fN = ff.getAbsolutePath();
            if ((fN.toLowerCase().endsWith(".zip"))||(fN.toLowerCase().endsWith(".jar"))) {}
            else break;

            try
            {

                /*
                ZipUtil zipUtil = new ZipUtil(fN);

                ZipEntry entry = zipUtil.searchEntryWithWholeName(fileName);

                if (entry == null) break;
                boolean cTag = false;
                if (middle.equals("")) cTag = true;
                else
                {
                    String entryName = entry.getName();
                    String fN2 = entryName.substring(0, entryName.indexOf(fileName));
                    String middle2 = middle;
                    if ((!File.separator.equals("/"))&&(middle2.indexOf(File.separator) >= 0)) middle2 = middle2.replace(File.separator, "/");
                    if (!middle2.endsWith("/")) middle2 = middle2 + "/";
                    if (middle2.startsWith("/")) middle2 =  middle2.substring(1);
                    if (fN2.indexOf(middle2) >= 0) cTag = true;
                }

                if (!cTag) break;

                String uuri = zipUtil.getAccessURL(entry);

                URL res = null;

                try
                {
                    res = new URL(uuri);
                }
                catch(MalformedURLException me)
                {
                    res = null;
                }

                if (res != null) url = res;
                */


                ZipFile zip = null;
                //String pref = "";
                if (fN.toLowerCase().endsWith(".zip"))
                {
                    zip = new ZipFile(ff);
                    //pref = "zip:";
                }
                else if (fN.toLowerCase().endsWith(".jar"))
                {
                    zip = new JarFile(ff);
                    //pref = "jar:";
                }
                else break;

                ZipEntry ee = zip.getEntry(tt);
                if (ee == null)
                {
                    Enumeration<? extends ZipEntry> enumer = zip.entries();
                    List<ZipEntry> listEntry = new ArrayList<ZipEntry>();
                    while(enumer.hasMoreElements())
                    {
                        ZipEntry entry = enumer.nextElement();
                        if (entry.getName().endsWith(fileName)) listEntry.add(entry);
                    }
                    if (listEntry.size() == 1) ee = listEntry.get(0);

                    if (ee == null) break;
                }

                if ((!File.separator.equals("/"))&&(fN.indexOf(File.separator) >= 0)) fN = fN.replace(File.separator, "/");
                String uuri = "jar:file:///" + fN + "!/" + ee.getName();

                URL res = null;

                try
                {
                    res = new URL(uuri);
                }
                catch(MalformedURLException me)
                {
                    res = null;
                }

                if (res != null) url = res;

                /*
                InputStream stream = zip.getInputStream(ee);

                String fileName2 = FileUtil.getTemporaryFileName();
                if ((fileName.length() > 5)&&(fileName.substring(fileName.length()-4, fileName.length()-3).equals(".")))
                {
                    fileName2 = fileName2.substring(0, fileName2.length()-4) + "." + fileName.substring(fileName.length()-3);
                }

                try
                {
                    fileName2 = downloadFromInputStreamToFile(stream, fileName2);
                }
                catch(IOException ie)
                {
                    System.out.println("Error 99756 FileUtil.downloadFromInputStreamToFile() for resource searching ("+tt+") : " + ie.getMessage());
                    break;
                }

                File file = new File(fileName2);
                file.deleteOnExit();
                url = file.toURI().toURL();
                */
            }
            catch(Exception ee)
            {
                System.out.println("Error 98246 at FileUtil.retrieveResourceURL() : " + ee.getMessage());
            }
            break;
        }

        if (url == null) url = retrieveResourceURL(rscName + "/" + tt);
        if (url != null) System.out.println("## Find this resource file : " + url.toString());
        return url;
    }
    public static void tidyWorkingDir()
    {
        tidyWorkingDir(null);
    }
    public static void tidyWorkingDir(String extension)
    {
        String deleteFilePath = FileUtil.getUIWorkingDirectoryPath();

        File dir = new File(deleteFilePath);
        if ((!dir.exists())||(!dir.isDirectory())) return;

        File[] files = dir.listFiles();

        if ((files == null)||(files.length == 0)) return;

        for (File file:files)
        {
            if (!file.isFile()) continue;
            String fileName = file.getName().trim();

            if ((extension == null)||(extension.trim().equals(""))) {}
            else
            {
                extension = extension.trim();
                if (fileName.toLowerCase().endsWith(extension.toLowerCase())) {}
                else continue;
            }

            if (fileName.toLowerCase().startsWith(Config.TEMPORARY_FILE_PREFIX.toLowerCase())) fileName = fileName.substring(Config.TEMPORARY_FILE_PREFIX.length());
            else continue;


//            int idx = fileName.indexOf("_");
//            if (idx < 0)
//            {
//                //System.out.println("Not Deleting 1 : " +file.getName());
//                continue;
//            }

            String createdDateOfFile = "";//fileName.substring(0, idx);

            for (int i=0;i<fileName.length();i++)
            {
                String achar = fileName.substring(i, i+1);
                try
                {
                    Integer.parseInt(achar);
                }
                catch(NumberFormatException ne)
                {
                    break;
                }
                createdDateOfFile = createdDateOfFile + achar;
            }

            long millis;
            DateFunction dateUtil = new DateFunction();
            String dFormat = dateUtil.getDefaultDateFormatString();
            if (dFormat.length() < createdDateOfFile.length())
            {
                int start = dFormat.length();
                for (int i=start;i!=createdDateOfFile.length();i++) dFormat = dFormat + "S";
            }
            else if (dFormat.length() > createdDateOfFile.length())
            {
                int start = createdDateOfFile.length();
                for (int i=start;i!=dFormat.length();i++) createdDateOfFile = createdDateOfFile + "0";
            }
            try
            {
                millis = dateUtil.getMillisBetweenDates(dateUtil.getCurrentTime(), dateUtil.getDefaultDateFormatString(), createdDateOfFile, dFormat);

                if (millis < 0l) millis = millis * -1l;
            }
            catch(FunctionException fe)
            {
                //System.out.println("Not Deleting 2 : " +file.getName()+", createdDate:"+createdDateOfFile);
                continue;
            }

            int seconds = (int) ( millis / 1000l);

            if (seconds > 600) file.delete();
        }

        return;
    }


}

/**
 * $Log: not supported by cvs2svn $
 * Revision 1.41  2009/11/24 19:46:29  altturbo
 * add tidyWorkingDir()
 *
 * Revision 1.40  2009/10/30 16:21:54  altturbo
 * minor change
 *
 * Revision 1.39  2009/10/29 21:33:17  altturbo
 * upgrade findODIWithDomainName() and retrieveResourceURL(String rscName, String middle, String fileName)
 *
 * Revision 1.38  2009/10/13 18:15:54  altturbo
 * error debugging on searchProperty()
 *
 * Revision 1.37  2009/10/12 21:06:11  altturbo
 * change searchProperty()
 *
 * Revision 1.36  2009/09/18 16:40:36  altturbo
 * minor change
 *
 * Revision 1.35  2009/09/18 16:39:59  altturbo
 * minor change
 *
 * Revision 1.34  2009/08/17 20:26:32  altturbo
 * Change the searching priority for resource file
 *
 * Revision 1.33  2009/06/22 23:05:41  altturbo
 * File location search if target file not found
 *
 * Revision 1.32  2009/05/21 14:12:19  altturbo
 * upgreade searchProperties()
 *
 * Revision 1.31  2009/04/21 16:55:48  altturbo
 * update downloadFromURLtoTempFile()
 *
 * Revision 1.30  2009/04/17 14:24:20  wangeug
 * clean code:provide meaningful printout messages
 *
 * Revision 1.29  2009/04/02 06:45:30  altturbo
 * move getV3XsdFilePath() out to SchemaDirUtil.java
 *
 * Revision 1.28  2009/04/02 04:16:57  altturbo
 * modify getV3XsdFilePath()
 *
 * Revision 1.27  2009/04/02 04:10:23  altturbo
 * modify getV3XsdFilePath()
 *
 * Revision 1.26  2009/03/12 01:43:18  umkis
 * update filenameLocate()
 *
 * Revision 1.25  2009/03/10 01:28:32  umkis
 * minor change
 *
 * Revision 1.24  2009/03/09 20:21:49  umkis
 * minor change
 *
 * Revision 1.23  2009/03/09 18:10:31  umkis
 * add searchPropertyAsFilePath() and searchProperty()
 *
 * Revision 1.22  2009/03/09 18:02:29  umkis
 * add searchPropertyAsFilePath() and searchProperty()
 *
 * Revision 1.21  2009/02/25 15:56:25  wangeug
 * enable webstart
 *
 * Revision 1.20  2009/02/18 02:27:50  umkis
 * update filenameLocate()
 *
 * Revision 1.19  2008/12/12 22:01:30  umkis
 * add getV3XsdFilePath() and getPropertyFromComponentPropertyFile(String key)
 *
 * Revision 1.18  2008/10/21 21:07:50  umkis
 * update ODI to 2008 NE
 *
 * Revision 1.17  2008/06/09 19:53:50  phadkes
 * New license text replaced for all .java files.
 *
 * Revision 1.16  2008/05/30 01:00:40  umkis
 * update getV2ResourceMetaDataLoader()
 *
 * Revision 1.15  2008/05/29 00:30:56  umkis
 * add getV2ResourceMetaDataLoader()
 *
 * Revision 1.14  2008/05/22 15:59:47  umkis
 * add getV2ResourceMetaDataLoader(String)
 *
 * Revision 1.13  2008/05/22 15:33:42  umkis
 * add getV2ResourceMetaDataLoader()
 *
 * Revision 1.12  2008/04/01 21:06:46  umkis
 * minor change
 *
 * Revision 1.11  2007/11/16 17:17:34  wangeug
 * update SDTM module
 *
 * Revision 1.10  2007/09/24 20:05:28  umkis
 * Add v2 Meta data collector
 *
 * Revision 1.9  2007/09/20 22:41:01  umkis
 * no message
 *
 * Revision 1.8  2007/08/28 14:24:04  wangeug
 * clean code
 *
 * Revision 1.7  2007/08/28 13:58:51  wangeug
 * remove schemas folder from caAdapter.jar and set it under root directory: xxxx.xsd use relative path as "include"
 *
 * Revision 1.6  2007/08/09 01:56:52  umkis
 * add a feature that v2Meta directory creating when search the directory
 *
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

