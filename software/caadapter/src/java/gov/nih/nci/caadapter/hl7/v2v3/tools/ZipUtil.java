/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.v2v3.tools;

import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.function.DateFunction;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.List;
import java.util.Enumeration;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: kium
 * Date: Oct 1, 2009
 * Time: 12:24:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class ZipUtil
{
    private ZipFile zipFile = null;
    private String pivotDirectory = null;
    private String initialFile = null;

    public ZipUtil(String file) throws IOException
    {
        ZipFile zip = new ZipFile(file);

        zipFile = zip;
    }
    public List<String> getEntryNames()
    {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        List<String> list = new ArrayList<String>();
        while(entries.hasMoreElements())
        {
            ZipEntry entry = entries.nextElement();
            list.add(entry.getName());
        }
        return list;
    }
    public List<ZipEntry> searchEntryWithNameAsPart(String name, String extension)
    {
        return searchEntryWithName(name, extension, false);
    }
    public ZipEntry searchEntryWithWholeName(String name) throws IOException
    {
        List<ZipEntry> list = searchEntryWithName(name, null, true);
        if (list.size() != 1) throw new IOException("Count of ZipEntry '"+name+"' is not one. : " + list.size());
        return list.get(0);
    }
    private List<ZipEntry> searchEntryWithName(String name, String extension, boolean isWhole)
    {

        if ((name == null)||(name.trim().equals(""))) return null;

        while(true)
        {
            int idx5 = name.indexOf("/");
            if (idx5 < 0) name.indexOf(File.separator);
            if (idx5 < 0) break;
            name = name.substring(idx5+1);
        }

        if(extension != null)
        {
            extension = extension.trim();
            if (extension.equals("")) extension = null;
            else if (extension.startsWith(".")) extension = extension.substring(1);
        }

        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        List<ZipEntry> list = new ArrayList<ZipEntry>();

        while(entries.hasMoreElements())
        {
            ZipEntry entry = null;
            ZipEntry entry1 = entries.nextElement();
            String name1 = entry1.getName();

            while(true)
            {
                int idx = name1.indexOf("/");
                if (idx < 0) idx = name1.indexOf(File.separator);
                if (idx < 0) break;
                else name1 = name1.substring(idx+1);
            }

            if (isWhole)
            {
                if (name1.equals(name)) entry = entry1;
            }
            else
            {
                if (name1.startsWith(name))
                {
                    if (extension == null) entry = entry1;
                    else
                    {
                        if (name1.endsWith("." + extension)) entry = entry1;
                    }
                }
            }
            if (entry != null) list.add(entry);
        }
        return list;
    }

    public String copyEntryToFile(ZipEntry entry , String dir) throws IOException
    {
        //ZipEntry entry = zipFile.getEntry(entryName);
        if (entry == null) throw new IOException("ZipEntry is null. ");
        String entryName = entry.getName();

        if ((dir == null)||(dir.trim().equals(""))) throw new IOException("null directory Name");
        File d = new File(dir);
        if ((!d.exists())||(!d.isDirectory())) throw new IOException("This directory is not exist : " + dir);

        dir = d.getAbsolutePath();
        if (dir.endsWith(File.separator)) dir = dir.substring(0, dir.length()-File.separator.length());

        String nm = "";
        String dName = "";
        String ds = dir;
        for (int i=0;i<entryName.length();i++)
        {
            String achar = entryName.substring(i, i+1);
            if ((achar.equals("/"))||(achar.equals(File.separator)))
            {
                nm = nm + File.separator;
                if (!dName.equals(""))
                {
                    ds = ds + File.separator + dName;
                    File d2 = new File(ds);
                    if ((!d2.exists())||(!d2.isDirectory()))
                    {
                        if (!d2.mkdir()) throw new IOException("This directory creation failure : " + ds);
                    }
                }
                dName = "";
            }
            else
            {
                dName = dName + achar;
                nm = nm + achar;
            }
        }
        if (!nm.startsWith(File.separator)) nm = File.separator + nm;

        String fileName = dir + nm;
        File f = new File(fileName);
        // null means avoiding duplicate copy.
        if ((f.exists())&&(f.isFile())) return null;
        FileUtil.downloadFromInputStreamToFile(zipFile.getInputStream(entry), fileName, true);
        return fileName;
    }
    public ZipFile getZipFile()
    {
        return zipFile;
    }
    public String getAccessURL(ZipEntry entry)
    {
        if (entry == null) return null;
        if (zipFile == null) return null;
        String filePath = zipFile.getName();
        try
        {
            InputStream is = zipFile.getInputStream(entry);
            if (is == null) return null;
        }
        catch(Exception ee)
        {
            return null;
        }
        if (!File.separator.equals("/")) filePath = filePath.replace(File.separator, "/");
        return "jar:file:///" + filePath + "!/" + entry.getName();
    }
    public String getPivotDirectory()
    {
        return pivotDirectory;
    }
    public String getInitialFile()
    {
        return initialFile;
    }
    public String getParentDir()
    {
        return (new File(getZipFile().getName())).getParentFile().getAbsolutePath();
    }

    public String copyIncludedFiles(ZipEntry entry) throws IOException
    {
        return copyIncludedFiles(entry, null);
    }
    public String copyIncludedFiles(ZipEntry entry, String xsdFile) throws IOException
    {
        String tempDir = "";
        while(true)
        {
            tempDir = getParentDir() + File.separator + Config.TEMPORARY_FILE_PREFIX + (new DateFunction()).getCurrentTime() + "_" + FileUtil.getRandomNumber(4);
            File tDir = new File(tempDir);
            if ((tDir.exists())&&(tDir.isDirectory())) continue;
            if (!tDir.mkdir()) throw new IOException("At ValidateXMLSchema.isValidSAX(), Temp directroy creation failure : " + tempDir);
            break;
        }
        return copyIncludedFiles(tempDir, entry, xsdFile);
    }
    public String copyIncludedFiles(String pivotDir, ZipEntry entry, String xsdFile) throws IOException
    {
        if (entry == null) throw new IOException("Entry is Null at ZipUtil.copyIncludedFiles().");

        String res = copyIncludedEachFile(pivotDir, entry, xsdFile, true);
        //pivotDirectory = pivotDir;
        return res;
    }
    private String copyIncludedEachFile(String pivotDir, ZipEntry entry, String xsdFile, boolean first) throws IOException
    {
        if (xsdFile == null) xsdFile = "";
        else xsdFile = xsdFile.trim();

        if (!xsdFile.equals(""))
        {
            File f = new File(xsdFile);
            if ((!f.exists())||(!f.isFile()))
            {
                xsdFile = "";
                //throw new IOException("The target xsd file is not exist. : " + xsdFile);
            }
            else xsdFile = f.getAbsolutePath();
        }

        if ((pivotDir == null)||(pivotDir.trim().equals(""))) throw new IOException("null directory Name (2)");
        File d = new File(pivotDir);
        if ((!d.exists())||(!d.isDirectory())) throw new IOException("This directory is not exist (2) : " + pivotDir);
        pivotDir = d.getAbsolutePath();
        if (pivotDir.endsWith(File.separator)) pivotDir = pivotDir.substring(0, pivotDir.length()-File.separator.length());

//        String res = pivotDir + File.separator + entry.getName();
//        if (!File.separator.equals("/"))
//        {
//            String rr = "";
//            for (int i=0;i<res.length();i++)
//            {
//                String achar = res.substring(i, i+1);
//                if (achar.equals("/")) rr = rr + File.separator;
//                else rr = rr + achar;
//            }
//            res = rr;
//        }

        String res = copyEntryToFile(entry , pivotDir);
        if (res == null) return null;

        List<String> list = FileUtil.readFileIntoList(res);

        boolean started = false;

        for (String line:list)
        {
            String lineL = line.toLowerCase();
            int idx1 = lineL.indexOf("include");
            int idx2 = lineL.indexOf("schemalocation");
            if ((idx1 < 0)||(idx2 < 0))
            {
                if (started) break;
                else continue;
            }
            started = true;
            line = line.substring(idx2 + ("schemalocation").length());
            int idx3 = line.indexOf("\"");
            if (idx3 < 0) throw new IOException("Invalid Schema file format : 'include' element (1) : " + entry.getName());
            line = line.substring(idx3 + 1);
            int idx4 = line.indexOf("\"");
            if (idx4 < 0) throw new IOException("Invalid Schema file format : 'include' element (2) : " + entry.getName());
            line = line.substring(0, idx4);

            while(true)
            {
                int idx5 = line.indexOf("/");
                if (idx5 < 0) line.indexOf(File.separator);
                if (idx5 < 0) break;
                line = line.substring(idx5+1);
            }

            ZipEntry ent = searchEntryWithWholeName(line);
            if (ent == null) throw new IOException("Not fount this included entry name : " + line);
            copyIncludedEachFile(pivotDir, ent, null, false);
        }

        File f = new File(res);
        if ((!f.exists())||(!f.isFile())) throw new IOException("Copy xsd File failure : " + res);

        if (!xsdFile.equals(""))
        {
            String parent = f.getParent();
            if (!parent.endsWith(File.separator)) parent = parent + File.separator;
            String xsdStr = FileUtil.readFileIntoString(xsdFile);
            String fileN = parent + FileUtil.getTemporaryFileName("xsd");
            FileUtil.saveStringIntoTemporaryFile(fileN, xsdStr);
            res = fileN;
        }
        if (first)
        {
            pivotDirectory = pivotDir;
            initialFile = res;
        }
        return res;
    }

    public boolean deleteDirectory()
    {
        if ((pivotDirectory == null)||(pivotDirectory.trim().equals(""))) return false;
        File file = new File(pivotDirectory);
        pivotDirectory = null;
        return deleteDirectory(file);
    }
    public boolean deleteDirectory(File file)
    {
        if (file == null) return false;
        if (!file.exists()) return false;
        if (file.isFile()) return file.delete();
        File[] files = file.listFiles();

        boolean res = true;
        for(File fi:files)
        {
            if (!deleteDirectory(fi)) res = false;
        }
        if (!file.delete()) res = false;
        return res;
    }

    public static void main(String[] args)
    {
        String fileName = "";
        try
        {
            fileName = args[0];
            ZipUtil ss = new ZipUtil(fileName);
            System.out.println("zip File Name : " + ss.getZipFile().getName() + ", Parent : " + ss.getParentDir());
            List<String> li = ss.getEntryNames();
            for (String str:li) System.out.println("Entry Name : "+str);
            List<ZipEntry> list = ss.searchEntryWithNameAsPart("voc.xsd", "xsd");
            for (ZipEntry en:list) System.out.println("Find : "+ en.getName());
        }
        catch(Exception ee)
        {
            System.out.println("ER" + ee.getMessage());
        }
    }
}
