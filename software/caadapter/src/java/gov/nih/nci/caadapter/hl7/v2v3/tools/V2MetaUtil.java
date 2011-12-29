package gov.nih.nci.caadapter.hl7.v2v3.tools;

import edu.knu.medinfo.hl7.v2tree.meta_old.MetaDataLoader;
import edu.knu.medinfo.hl7.v2tree.HL7MessageTreeException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.awt.*;

import gov.nih.nci.caadapter.common.util.ClassLoaderUtil;
import gov.nih.nci.caadapter.common.util.FileUtil;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Dec 29, 2011
 * Time: 11:44:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class V2MetaUtil
{
    private static MetaDataLoader v2Loader = null;

    public static MetaDataLoader getV2ResourceMetaDataLoader()
    {
        return getV2ResourceMetaDataLoader(null);
    }
    public static MetaDataLoader getV2ResourceMetaDataLoader(String resourceFile)
    {
        if (resourceFile == null) resourceFile = "";
        else resourceFile = resourceFile.trim();

        //if (v2Loader == null) System.out.println("CCC v3 meta loader (1) : " + resourceFile);
        //else System.out.println("CCC v3 meta loader (2) : " + v2Loader.getPath() + ", " + resourceFile);
        if (resourceFile.equalsIgnoreCase("null")) resourceFile = "";
        if (!resourceFile.equals(""))
        {
            MetaDataLoader loader = null;

            File ff = new File(resourceFile);
            if (!ff.exists())
            {
                System.out.println("HL7MessageTreeException00 : This is not a valid file or directory path => " + resourceFile);
                return null;
            }
            if (ff.isFile())
            {
                try
                {
                    loader = new MetaDataLoader(resourceFile, edu.knu.medinfo.hl7.v2tree.util.Config.DEFAULT_VERSION);
                }
                catch(HL7MessageTreeException he)
                {
                    System.out.println("HL7MessageTreeException01 : " + he.getMessage());
                    //he.printStackTrace();
                    try
                    {
                        loader = new MetaDataLoader(resourceFile, null, edu.knu.medinfo.hl7.v2tree.util.Config.DEFAULT_VERSION);
                    }
                    catch(HL7MessageTreeException he1)
                    {
                        System.out.println("HL7MessageTreeException02 : " + he1.getMessage());
                        return null;
                    }
                }
            }
            else
            {
                try
                {
                    loader = new MetaDataLoader(resourceFile, null, edu.knu.medinfo.hl7.v2tree.util.Config.DEFAULT_VERSION);
                }
                catch(HL7MessageTreeException he1)
                {
                    System.out.println("HL7MessageTreeException03 : " + he1.getMessage());
                    return null;
                }
            }
            if (loader != null)
            {
                v2Loader = loader;
                return loader;
            }
        }

        if (v2Loader == null)
        {
            MetaDataLoader loader = null;
            try
            {
                loader = new MetaDataLoader();
            }
            catch(HL7MessageTreeException he)
            {
                loader = null;
            }
            if (loader != null)
            {
                v2Loader = loader;
                return loader;
            }

            String name = "v2Meta/version2.4/MessageStructure/ADT_A01.dat";

            Enumeration<URL> fileURLs = null;
            try
            {
                fileURLs= ClassLoader.getSystemResources(name);
            }
            catch(IOException ie)
            {
                System.out.println("IOException #1 : " + ie.getMessage());
            }
            if (fileURLs == null)
            {
                System.out.println("ClassLoader Result : " + name + " : Not Found");
                return null;
            }
            //System.out.println("Number of Result : " + fileURLs.toString());
            boolean found = false;
            while(fileURLs.hasMoreElements())
            {
                URL fileURL = fileURLs.nextElement();

                String url = fileURL.toString();

                if ((url.toLowerCase().startsWith("jar:"))||(url.toLowerCase().startsWith("zip:")))
                {
                    int idx = url.indexOf("!");
                    if (idx < 0)
                    {
                        System.err.println("Invalid jar file url : " + url);
                        continue;
                    }
                    String jarFileName = url.substring(4, idx);
                    try
                    {
                        v2Loader = new MetaDataLoader(jarFileName);
                        found = true;
                    }
                    catch(HL7MessageTreeException he)
                    {
                        continue;
                    }
                }
                if ((found)&&(v2Loader != null)) return v2Loader;
            }

            v2Loader = null;
            return null;
        }
        else return v2Loader;
    }

        public static String getV2DataDirPath()
    {
        File f = new File(FileUtil.getWorkingDirPath() + File.separator + "data" + File.separator + "v2Meta");
        if ((!f.exists())||(!f.isDirectory()))
        f.mkdirs();
        return f.getAbsolutePath();
        //return getV2DataDirPath(null);
    }
    public static String getV2DataDirPath(Component parent)
    {
        File f = new File(FileUtil.getWorkingDirPath() + File.separator + "data" + File.separator + "v2Meta");
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
                path = FileUtil.getWorkingDirPath() + File.separator + "data" + File.separator + path;
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
}
