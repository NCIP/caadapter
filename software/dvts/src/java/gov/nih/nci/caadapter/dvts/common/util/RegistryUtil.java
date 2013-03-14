/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.dvts.common.util;

import java.io.IOException;
import java.io.File;
import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Jul 13, 2007
 *          Time:       4:25:51 PM $
 */
public class RegistryUtil
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: RegistryUtil.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/util/RegistryUtil.java,v 1.3 2008-06-09 19:53:50 phadkes Exp $";

    private static String registryFileName = null;

    public static boolean inputRegistry(String regiName, String content) throws IOException
    {
        String regiContent = regiName.trim() + Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR + content.trim();
        if (registryFileName == null)
        {
            System.out.println("inputRegistry : " + regiContent);
            registryFileName = FileUtil.saveStringIntoTemporaryFile("Default_Line"+ Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR+"default\r\n"+regiContent);
            return true;
        }
        List<String> list = FileUtil.readFileIntoList(registryFileName);
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
        FileUtil.saveStringIntoTemporaryFile(registryFileName, output);
        return true;
    }
    public static boolean changeRegistry(String regiName, String content) throws IOException
    {
        String regiContent = regiName.trim() + Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR + content.trim();
        if (registryFileName == null) return false;

        List<String> list = FileUtil.readFileIntoList(registryFileName);
        String output = "";
        String value = "";
        boolean findTag = false;
        for(int i=0;i<list.size();i++)
        {
            String line = list.get(i);
            String regiHead = line.substring(0, line.indexOf(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR));
            if (regiHead.equals(regiName.trim()))
            {
                findTag = true;
                value = line.substring(line.indexOf(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR) + Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR.length());
                output = output + regiContent + "\r\n";
            }
            else output = output + line + "\r\n";
        }

        System.out.println("changeRegistry : " + regiName + ", content=" + content + ", before=" + value + ", find=" + findTag);
        if (!findTag) return false;
        FileUtil.saveStringIntoTemporaryFile(registryFileName, output.trim());
        return true;
    }
    public static boolean deleteRegistry(String regiName) throws IOException
    {
        if (registryFileName == null) return false;

        List<String> list = FileUtil.readFileIntoList(registryFileName);
        String output = "";
        boolean findTag = false;
        for(int i=0;i<list.size();i++)
        {
            String line = list.get(i);
            String regiHead = line.substring(0, line.indexOf(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR));
            if (regiHead.equals(regiName.trim())) findTag = true;
            else output = output + line + "\r\n";
        }
        System.out.println("deleteRegistry : " + regiName + ", find=" + findTag);
        if (!findTag) return false;
        FileUtil.saveStringIntoTemporaryFile(registryFileName, output.trim());
        return true;
    }
    public static String readRegistry(String regiName) throws IOException
    {
        if (registryFileName == null) //return "ERR01: Registry File is not created yet." + regiName;
            throw new IOException("ERR01: Registry File is not created yet." + regiName);

        List<String> list = FileUtil.readFileIntoList(registryFileName);
        String output = "";
        boolean findTag = false;
        for(int i=0;i<list.size();i++)
        {
            String line = list.get(i);
            String regiHead = line.substring(0, line.indexOf(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR));
            if (regiHead.equals(regiName.trim()))
            {
                findTag = true;
                output = line.substring(line.indexOf(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR) + Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR.length());
            }
        }
        System.out.println("readRegistry : " + regiName + ", out=" + output + ", find=" + findTag);
        if (!findTag) //return "ERR02: Not found this variable : " + regiName;
             throw new IOException("ERR02: Not found this variable : " + regiName);
        return output;
    }
    public static String getRegistryFileName()
    {
        return registryFileName;
    }
    public static void setRegistryFileName(String filename) throws IOException
    {
        File file = new File(filename);

        if (!file.exists()) throw new IOException("This file is not exists. : " + filename);
        if (!file.isFile()) throw new IOException("This is not a file. : " +  filename);
        registryFileName = filename;
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2008/06/06 18:54:28  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/14 20:17:08  umkis
 * HISTORY      : new utils
 * HISTORY      :
 */
