/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.cbiit.cmts.util;

import gov.nih.nci.cbiit.cmts.common.XSDParser;
import gov.nih.nci.cbiit.cmts.ui.util.GeneralUtilities;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;

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
    private static URL codeBaseURL = null;

    public static void setCodeBase(URL url)
    {
        codeBaseURL = url;
    }
    public static URL getCodeBase()
    {

        return codeBaseURL;
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
     * Utility method to find file in dir or zip,jar file
     * @param name
     * @return A file URL object
     */
    private static URL findFile(String name)
    {

        if ((name == null)||(name.trim().equals(""))) return null;
        name = name.trim();
        return findFileExe(name, new File(FileUtil.getWorkingDirPath()));
    }


    public static URL findFileExe(String name, File dir)
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

    public static String findRelativePath(String refPath, String filePath)
    {
    	String relPath;
    	if (refPath==null)
    		return filePath;

    	if (filePath.startsWith(refPath))
    		relPath= filePath.substring(refPath.length()+1);
    	else
    	{
    		String parentRelative=findRelativePath((new File(refPath)).getParent(), filePath);
    		relPath= "..\\"+parentRelative;
    	}
		return relPath;
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
    private static final String getFileExtension(File file, boolean includeDelimiter)
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
        if((name.startsWith("etc/"))||(name.startsWith("etc\\"))) return getResource(name.substring(4));
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

                    public boolean hasMoreElements()
                    {
                        return (url != null);
                    }

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
                };
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
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
    private static String getWorkingDirPath()
    {
        File f = new File("");
        String path = null;
        path = f.getAbsolutePath();
        return path;
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

    public static String searchFile(String fileName)
    {
        return searchFile(null, null, fileName, null, true);
    }
    public static String findNumericValue(String str)
    {
        return findNumericValue(str, true);
    }
    public static String findNumericValue(String str, boolean checkNumeric)
    {
        if (isNumeric(str)) return str;
        String s1 = "string(\"";
        String s2 = "\")";
        int idx1 = str.toLowerCase().indexOf(s1);
        int idx2 = str.indexOf(s2);
        if ((idx1 >= 0)&&(idx2 > 0)&&(idx1 < idx2))
        {
            String c = str.substring(idx1 + s1.length(), idx2);
            if (checkNumeric)
            {
                if (isNumeric(c)) return c;
            }
            else return c;
        }
        return str;
    }
    public static boolean isNumeric(String str)
    {
        if ((str == null)||(str.trim().equals(""))) return false;
        try
        {
            Integer.parseInt(str);
        }
        catch(NumberFormatException ne)
        {
            return false;
        }
        return true;
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

