/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.common.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 8, 2011
 * Time: 9:30:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class FileSearchUtil
{
    private String originalF = null;
    public FileSearchUtil()
    {

    }
    public FileSearchUtil(String file)
    {
        originalF = file;
    }

    public String searchFile(String fileName)
    {
        File ff = new File(fileName);
        if ((ff.exists())&&(ff.isFile())) return ff.getAbsolutePath();

        return searchFile(null, null, fileName, null, true, false);
    }
    public String searchFileLight(String fileName)
    {
        File ff = new File(fileName);
        if ((ff.exists())&&(ff.isFile())) return ff.getAbsolutePath();

        return searchFile(null, null, fileName, null, true, true);
    }
    public String searchDir(String dirName)
    {
        File ff = new File(dirName);

        if ((ff.exists())&&(ff.isDirectory())) return ff.getAbsolutePath();
        return searchFile(null, null, dirName, null, false, true);
    }
    public String searchFile(String fileName, File startDir)
    {
        File ff = new File(fileName);
        if ((ff.exists())&&(ff.isFile())) return ff.getAbsolutePath();

        return searchFile(startDir, null, fileName, null, true, false);
    }
    public String searchDir(String dirName, File startDir)
    {
        File ff = new File(dirName);

        if ((ff.exists())&&(ff.isDirectory())) return ff.getAbsolutePath();
        return searchFile(startDir, null, dirName, null, false, true);
    }
    private String searchFile(File startDir, File dir, String fileNameF, List<String> searchedDirList, boolean isFile, boolean light)
    {
        if((originalF!=null)&&(!fileNameF.equals(originalF)))
        {
            //System.out.println("CCCCC here="+ fileNameF+", original="+originalF);
            fileNameF = originalF;
        }
        if (dir == null)
        {
            if (startDir != null)
            {
                dir = startDir;
            }
            else
            {
                dir = new File(FileUtil.getWorkingDirPath());
                startDir = dir;
            }
        }
        if (startDir == null) startDir = dir;

        if ((!dir.exists())||(!dir.isDirectory())||(dir.isHidden())) return null;

        String dirName = dir.getAbsolutePath();
        boolean isStart = false;
        if (searchedDirList == null)
        {
            isStart = true;
            //originalF = fileNameF;
            searchedDirList = new ArrayList<String>();
        }
        for (String path:searchedDirList)
        {
            if (path.equals(dirName)) return null;
        }
        //System.out.println("CCCC : " + dirName + ", " + fileNameF);
        searchedDirList.add(dirName);

        if (fileNameF == null) return null;
        fileNameF = fileNameF.trim();
        if (fileNameF.equals("")) return null;

        String c = "";
        String fileName = fileNameF;
        String fileNameU = fileNameF;

        if ((!File.separator.equals("/"))&&(fileNameF.indexOf("/") >= 0))
        {
            for (int i=0;i<fileNameF.length();i++)
            {
                String achar = fileNameF.substring(i, i+1);

                if (achar.equals("/")) c = c + File.separator;
                else c = c + achar;
            }
            fileName = c;
            c = "";
            for (int i=0;i<fileNameF.length();i++)
            {
                String achar = fileNameF.substring(i, i+1);

                if (achar.equals(File.separator)) c = c + "/";
                else c = c + achar;
            }
            fileNameU = c;
        }

        if (fileName.endsWith(File.separator)) fileName = fileName.substring(0, fileName.length()-File.separator.length());
        if (fileName.startsWith(File.separator)) fileName = fileName.substring(File.separator.length());


        if (!dirName.endsWith(File.separator)) dirName = dirName + File.separator;
        File f = new File(dirName + fileName);

        if (f.exists())
        {
            if ((isFile)&&(f.isFile())) return f.getAbsolutePath();
            if ((!isFile)&&(f.isDirectory())) return f.getAbsolutePath();
        }

        File[] files = dir.listFiles();

        if ((files == null)||(files.length == 0)) return null;

        for(File file:files)
        {
            String abFileName = file.getAbsolutePath();

            if (file.isDirectory())
            {
                String res = searchFile(startDir, file, fileName, searchedDirList, isFile, light);
                if (res != null) return res;
            }
            if (!isFile) continue;
            if (!file.isFile()) continue;
            if (light) continue;
            //System.out.println("    C File : " + abFileName  + ", "  + fileNameF);
            if ((abFileName.toLowerCase().endsWith(".jar"))||(abFileName.toLowerCase().endsWith(".zip")))
            {
                //URL url = FileUtil.retrieveResourceURL(abFileName, "", fileName); ==
                //if (url != null) return url.toString();
                ZipFile zip = null;
                String fN = abFileName;
                //String pref = "";
                try
                {
                    if (fN.toLowerCase().endsWith(".zip"))
                    {
                        zip = new ZipFile(fN);
                        //pref = "zip:";
                    }
                    else if (fN.toLowerCase().endsWith(".jar"))
                    {
                        zip = new JarFile(fN);
                        //pref = "jar:";
                    }
                    else continue;
                }
                catch(IOException ie)
                {
                    continue;
                }

                ZipEntry ee = zip.getEntry(fileNameU);
                if (ee == null)
                {
                    Enumeration<? extends ZipEntry> enumer = zip.entries();
                    List<ZipEntry> listEntry = new ArrayList<ZipEntry>();
                    while(enumer.hasMoreElements())
                    {
                        ZipEntry entry = enumer.nextElement();
                        if (entry.getName().endsWith(fileNameU)) listEntry.add(entry);
                    }
                    if (listEntry.size() == 1) ee = listEntry.get(0);

                    if (ee == null) continue;
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
                    continue;
                }

                return res.toString();
            }
        }
        if ((isStart)&&(!light))
        {
            if (dir.getParentFile() == null) return null;
            String dName = startDir.getName();
            //if ((dName.equalsIgnoreCase("bin"))||(dName.equalsIgnoreCase("dist")))
            if (dName.equalsIgnoreCase("bin"))
            {
                dir = startDir.getParentFile();
                //System.out.println("CCCC At isStart : fileName" + fileName);
                String res = searchFile(dir, dir, fileName, searchedDirList, isFile, light);
                if (res != null)
                {
                    //System.out.println("CCCC found res = " + res);
                    return res;
                }
            }
        }
        return null;
    }

    public File searchDir(File dir, String fileName)
    {
        return searchDir(dir, fileName, -1, null);
    }
    public File searchDir(File dir, String fileName, String[] underCondition)
    {
        return searchDir(dir, fileName, -1, underCondition);
    }
    public File searchDir(File dir, String fileName, int limitDepth, String[] underCondition)
    {
        return searchFileAndDir(dir, fileName, limitDepth, underCondition, false);
    }
    public File searchFile(File dir, String fileName)
    {
        return searchFile(dir, fileName, -1, null);
    }
    public File searchFile(File dir, String fileName, String[] underCondition)
    {
        return searchFile(dir, fileName, -1, underCondition);
    }
    public File searchFile(File dir, String fileName, int limitDepth, String[] underCondition)
    {
        return searchFileAndDir(dir, fileName, limitDepth, underCondition, true);
    }

    private File searchFileAndDir(File dir, String fileName, int limitDepth, String[] underCondition, boolean isFile)
    {
        if (dir == null) return null;
        if ((!dir.exists())||(!dir.isDirectory())) return null;
        if ((fileName == null)||(fileName.trim().equals(""))) return null;
        fileName = fileName.trim();

        while(true)
        {
            int idx = fileName.indexOf(File.separator);
            if (idx < 0) break;
            fileName = fileName.substring(idx + File.separator.length());
        }

        String dirPath = dir.getAbsolutePath();
        if (!dirPath.endsWith(File.separator)) dirPath = dirPath + File.separator;

        File pFile = new File(dirPath + fileName);
        if (pFile.exists())
        {
            boolean cTag = true;
            File tempP = pFile;
            while ((underCondition != null)&&(underCondition.length > 0))
            {
                boolean dTag = false;
                for (String str:underCondition)
                {
                    if (tempP.getName().equals(str))
                    {
                        dTag = true;
                        break;
                    }
                }
                if (dTag) break;
                if (tempP.getParentFile() == null)
                {
                    cTag = false;
                    break;
                }
                tempP = tempP.getParentFile();
            }
            if (cTag)
            {
                if ((isFile)&&(pFile.isFile())) return pFile;
                if ((!isFile)&&(pFile.isDirectory())) return pFile;
            }
        }

        String temp = dirPath.substring(0, (dirPath.length() - File.separator.length()));
        int depth = 0;
        while(true)
        {
            int idx = temp.indexOf(File.separator);
            if (idx < 0) break;
            depth++;
            temp = temp.substring(idx + File.separator.length());
        }

        if ((limitDepth > 0)&&(depth > limitDepth)) return null;

        File[] files = dir.listFiles();
        for(File file:files)
        {
            if (!file.isDirectory()) continue;
            File res = searchFileAndDir(file, fileName, limitDepth, underCondition, isFile);
            if (res != null) return res;
        }
        return null;
    }

}
