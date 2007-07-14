/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/util/RegistryUtil.java,v 1.1 2007-07-14 20:17:08 umkis Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE  
 * ******************************************************************
 *
 *	The caAdapter Software License, Version 1.0
 *
 *	Copyright 2001 SAIC. This software was developed in conjunction with the National Cancer
 *	Institute, and so to the extent government employees are co-authors, any rights in such works
 *	shall be subject to Title 17 of the United States Code, section 105.
 *
 *	Redistribution and use in source and binary forms, with or without modification, are permitted
 *	provided that the following conditions are met:
 *
 *	1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *	and the disclaimer of Article 3, below.  Redistributions in binary form must reproduce the above
 *	copyright notice, this list of conditions and the following disclaimer in the documentation and/or
 *	other materials provided with the distribution.
 *
 *	2.  The end-user documentation included with the redistribution, if any, must include the
 *	following acknowledgment:
 *
 *	"This product includes software developed by the SAIC and the National Cancer
 *	Institute."
 *
 *	If no such end-user documentation is to be included, this acknowledgment shall appear in the
 *	software itself, wherever such third-party acknowledgments normally appear.
 *
 *	3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or
 *	promote products derived from this software.
 *
 *	4. This license does not authorize the incorporation of this software into any proprietary
 *	programs.  This license does not authorize the recipient to use any trademarks owned by either
 *	NCI or SAIC-Frederick.
 *
 *	5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *	WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *	MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *	DISCLAIMED.  IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR
 *	THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *	EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *	PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *	PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 *	OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *	NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * ******************************************************************
 */

package gov.nih.nci.caadapter.common.util;

import java.io.IOException;
import java.io.File;
import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.1 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/util/RegistryUtil.java,v 1.1 2007-07-14 20:17:08 umkis Exp $";

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
 */
