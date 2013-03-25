/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.common.util;

import gov.nih.nci.cbiit.cdms.formula.gui.constants.ActionConstants;

import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.List;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.logging.FileHandler;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.net.SocketTimeoutException;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 22, 2010
 * Time: 10:39:30 AM
 * To change this template use File | Settings | File Templates.
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
    public static String getV2DataDirPath(Component parent)
    {
        File f = new File(getWorkingDirPath() + File.separator + "data" + File.separator + "v2Meta");
        if ((!f.exists())||(!f.isDirectory()))
        {
            if (parent == null) return null;



            String display = "HL7 v2 meta Directory isn't created yet.\nPress 'Yes' button if you want to create directory.\nIt may takes some minutes.";
            //JOptionPane.showMessageDialog(parent, "Making V2 Meta Directory", display, JOptionPane.WARNING_MESSAGE);
            //System.out.println("CCCCV : " + display);
            int res = JOptionPane.showConfirmDialog(parent, display, "Create v2 Meta Directory", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (res != JOptionPane.YES_OPTION) return "";

            ClassLoaderUtil loaderUtil = null;
            try
            {
                loaderUtil = new ClassLoaderUtil("v2Meta");
            }
            catch(IOException ie)
            {
                JOptionPane.showMessageDialog(parent, ie.getMessage() + ".\n Check the resourceV2.zip file in the library.", "Creating V2 Meta Directory failure", JOptionPane.WARNING_MESSAGE);
                //System.err.println("Make V2 Meta Directory : " + ie.getMessage());
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
                    JOptionPane.showMessageDialog(parent, "V2 Meta file is invalid : " + path, "Creating V2 Meta Directory failure", JOptionPane.WARNING_MESSAGE);

                    //System.err.println("V2 Meta file is invalid : " + path);
                    return null;
                }
                File dir = new File(path.substring(0,(index-1)));
                if ((!dir.exists())||(!dir.isDirectory()))
                {
                    if (!dir.mkdirs())
                    {
                        JOptionPane.showMessageDialog(parent, "V2 Meta directory making failure : " + dir, "Creating V2 Meta Directory failure", JOptionPane.WARNING_MESSAGE);

                        //System.err.println("V2 Meta directory making failure : " + dir);
                        return null;
                    }
                }
                File datFile = new File(loaderUtil.getFileName(i));
                if ((!datFile.exists())||(!datFile.isFile()))
                {
                    JOptionPane.showMessageDialog(parent, "Not Found This V2 Meta temporary file : " + path, "Creating V2 Meta Directory failure", JOptionPane.WARNING_MESSAGE);


                    continue;
                }
                if (!datFile.renameTo(new File(path))) System.err.println("V2 Meta rename failure : " + path);

            }
        }
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
            if ((rr.toLowerCase().startsWith(ActionConstants.CAADAPTER_HOME_DIR_TAG.toLowerCase()))&&(!isFilePath))
            {
                String homeDir = getWorkingDirPath().trim();
                String subDir = rr.substring(ActionConstants.CAADAPTER_HOME_DIR_TAG.length()).trim();
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

                java.util.List<String> list = null;
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
    private static String searchFile(File startDir, File dir, String fileName, java.util.List<String> searchedDirList, boolean isFile)
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
        //System.out.println("CCC searchFile("+fileName+") : dir=" + dir.getAbsoluteFile());

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

        if (data.toLowerCase().startsWith(ActionConstants.CAADAPTER_HOME_DIR_TAG.toLowerCase()))
        {
            String homeDir = getWorkingDirPath().trim();
            String subDir = data.substring(ActionConstants.CAADAPTER_HOME_DIR_TAG.length()).trim();
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
        return getTemporaryFileName(ActionConstants.TEMPORARY_FILE_EXTENSION);
    }

    /**
     * Generat a Temporary File Name at workingspace directory.
     * @param extension the extention of generated temp file
     * @return a Temporary File Name.
     */
    public static String getTemporaryFileName(String extension) // inserted by umkis 08/09/2006
    {
        tidyWorkingDir();
        DateUtil dateFunction = new DateUtil();
        String dateFormat = dateFunction.getDefaultDateFormatString();
        if (!dateFormat.endsWith("SSS")) dateFormat = dateFormat + "SSS";
        try
        {
            return getUIWorkingDirectoryPath() + File.separator + ActionConstants.TEMPORARY_FILE_PREFIX + (new DateUtil()).getCurrentTime(dateFormat) + "_" + getRandomNumber(4) + extension;
        }
        catch(Exception fe)
        {
            return getUIWorkingDirectoryPath() + File.separator + ActionConstants.TEMPORARY_FILE_PREFIX + (new DateUtil()).getCurrentTime() + "_" + getRandomNumber(4) + extension;
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
               //(fileName.endsWith(ActionConstants.TEMPORARY_FILE_EXTENSION)) &&
               (fileName.indexOf(ActionConstants.TEMPORARY_FILE_PREFIX) >= 0)
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
    public static java.util.List<String> readFileIntoList(String fileName) throws IOException
    {
        java.util.List<String> list = new ArrayList<String>();

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
        java.util.List<String> list = null;
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
        if (fileName.startsWith(ActionConstants.CAADAPTER_HOME_DIR_TAG)) fileName = fileName.replace(ActionConstants.CAADAPTER_HOME_DIR_TAG, getWorkingDirPath());
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
        if (fileName.startsWith(ActionConstants.CAADAPTER_HOME_DIR_TAG)) fileName = fileName.replace(ActionConstants.CAADAPTER_HOME_DIR_TAG, getWorkingDirPath());

        // check just the filename
        File f = new File(fileName);
        if (f.exists())
        {
            return f.getAbsolutePath();
        }

        if ((directory == null)||(directory.trim().equals(""))) throw new FileNotFoundException("Null Dirctory...");
        else directory = directory.trim();
        if (directory.startsWith(ActionConstants.CAADAPTER_HOME_DIR_TAG)) directory = directory.replace(ActionConstants.CAADAPTER_HOME_DIR_TAG, getWorkingDirPath());

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
        if (fullFilename.startsWith(ActionConstants.CAADAPTER_HOME_DIR_TAG)) fullFilename = fullFilename.replace(ActionConstants.CAADAPTER_HOME_DIR_TAG, getWorkingDirPath());
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
     * Return the absolute file name without the trailing file extension; return absoluteFileName itself if it does not contain any extension.
     * @param absoluteFileName
     * @return the absolute file name without the trailing file extension; return absoluteFileName itself if it does not contain any extension.
     */
    private static final String getFileNameWithoutExtension(String absoluteFileName)
    {
        if (absoluteFileName.startsWith(ActionConstants.CAADAPTER_HOME_DIR_TAG)) absoluteFileName = absoluteFileName.replace(ActionConstants.CAADAPTER_HOME_DIR_TAG, getWorkingDirPath());
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
        if(DefaultSettings.areEqual(extensionLocal, extension))
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
            return null;
            //path = CaadapterUtil.getPathOfComponentPropertyFile();
            //name = CaadapterUtil.getNameOfComponentPropertyFile();
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
                fi = FileUtil.class.getClassLoader().getResource(name).openStream();
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
        if (rscName == null) 
        	return null;
        rscName = rscName.trim();
        if (rscName.equals("")) 
        	return null;

        URL rtnURL=null;
    	
    	rtnURL=Thread.currentThread().getClass().getResource("/"+rscName);
		
		if (rtnURL==null)
			rtnURL=Thread.currentThread().getClass().getResource(rscName);
		
		
		//load resource for webstart deployment
		if (rtnURL==null)
		{
			rtnURL=FileUtil.class.getClassLoader().getResource(rscName);
			if (rtnURL==null)
				rtnURL=FileUtil.class.getClassLoader().getResource("/"+rscName);
		}
		
        if (rtnURL != null) 
        	return rtnURL;

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
            
        }

        if (rtnURL == null) 
        	System.out.println("This resource file cannot be found : " + rscName);
        return rtnURL;
    }
    public static URL retrieveResourceURL(String rscName, String middle, String fileName)
    {
        return retrieveResourceURL_Exe(rscName, middle, fileName, true);
    }
    private static URL retrieveResourceURL_Exe(String rscName, String middle, String fileName, boolean isFirst)
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

        //System.out.println("## Searching Resource ("+tt+") to file : " + rscName);
        if ((fileName.equals(""))&&(middle.equals(""))) return retrieveResourceURL(rscName);
        if ((rscName.equals(""))&&(middle.equals(""))) return retrieveResourceURL(fileName);
        URL url = null;

        while(true)
        {
            File ff = new File(rscName);

            if (!ff.exists()) break;

            if (ff.isDirectory())
            {
                if (ff.isHidden()) return null;
                if (ff.getName().equalsIgnoreCase("CVS")) return null;
                if (ff.getName().equalsIgnoreCase("svn")) return null;
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
                            URL res = retrieveResourceURL_Exe(fN, middle, fileName, false);
                            if (res != null)
                            {
                                url = res;
                                break;
                            }
                        }
                    }
                    else if (f.isDirectory())
                    {
                        URL res = retrieveResourceURL_Exe(fN, middle, fileName, false);
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
            //if ((fN.toLowerCase().endsWith(".zip"))||(fN.toLowerCase().endsWith(".jar"))) {}
            //else break;

            try
            {

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
                    java.util.List<ZipEntry> listEntry = new ArrayList<ZipEntry>();
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

            }
            catch(Exception ee)
            {
                System.out.println("Error at FileUtil.retrieveResourceURL() : " + ee.getMessage());
            }
            break;
        }
        if (isFirst)
        {
            if (url == null) url = retrieveResourceURL(rscName + "/" + tt);
            if (url != null) System.out.println("## Find this resource file : " + url.toString());
        }
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

            if (fileName.toLowerCase().startsWith(ActionConstants.TEMPORARY_FILE_PREFIX.toLowerCase())) fileName = fileName.substring(ActionConstants.TEMPORARY_FILE_PREFIX.length());
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
            DateUtil dateUtil = new DateUtil();
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
            catch(Exception fe)
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
