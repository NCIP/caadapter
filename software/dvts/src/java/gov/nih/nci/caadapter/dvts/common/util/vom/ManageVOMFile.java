/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.common.util.vom;

import gov.nih.nci.caadapter.dvts.FunctionVocabularyMapping;
import gov.nih.nci.caadapter.dvts.ContextVocabularyTranslation;
import gov.nih.nci.caadapter.dvts.common.function.FunctionException;
import gov.nih.nci.caadapter.dvts.common.util.FileUtil;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Oct 31, 2011
 * Time: 6:21:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ManageVOMFile
{
    public static String CONTEXT_DOMAIN_INDEX_FILE_NAME = "contextDomainIndex.txt";
    public static String CONTEXT_DOMAIN_DEACTIVATED_TAG = "DEACTIVATED";
    public static String CONTEXT_DOMAIN_ACTIVATED_TAG = "ACTIVATED";
    public static String SAMPLE_CONTEXT_TAG  = "SampleContext";
    public static String SAMPLE_CONTEXT_01_TAG = SAMPLE_CONTEXT_TAG + "01";
    public static String SAMPLE_CONTEXT_02_TAG  = SAMPLE_CONTEXT_TAG + "02";

    public static String validateNewVOMFile(String path)
    {
        FunctionVocabularyMapping fvm = new FunctionVocabularyMapping();

        try
        {
            fvm.validateVOMdataFileWithoutRecord(path);
        }
        catch(FunctionException fe)
        {
            return fe.getMessage();
        }
        return null;
    }

    public static List<String> validateContextForNewVOM(String contextDirS, String vomFileS)
    {
        List<String> domainInformation = getDomainInformation(contextDirS);

        if ((domainInformation == null)||(domainInformation.size() == 0)) return null;

        if ((vomFileS == null)||(vomFileS.trim().equals(""))) return null;
        File vomFile = new File(vomFileS);
        if ((!vomFile.exists())||(!vomFile.isFile())) return null;

        List<String> domainL = null;
        try
        {
            domainL = (new FunctionVocabularyMapping()).getDomains(vomFileS);
        }
        catch(FunctionException fe)
        {
            domainL = null;
        }
        if ((domainL == null)||(domainL.size() == 0)) return null;

        List<String> duplicateDomain = new ArrayList<String>();


        for (String str:domainL)
        {
            String d = getInformationItem(domainInformation, contextDirS, str, 0);

            if ((d == null)||(d.trim().equals(""))) continue;

            if (d.equals(vomFile.getName())) continue;

            duplicateDomain.add(str);
        }
        return duplicateDomain;
    }
    public static String getVOMFileNameWithDoamin(String contextDirS, String domain)
    {
        List<String> domainInformation = getDomainInformation(contextDirS);

        if ((domainInformation == null)||(domainInformation.size() == 0))
        {
            System.out.println("CCC Context domain info list is null : " + contextDirS + ", doamin=" + domain);
            return null;
        }

        if ((domain == null)||(domain.trim().equals(""))) return null;
        domain = domain.trim();

        String d = getInformationItem(domainInformation, contextDirS, domain, 0);
        if ((d != null)&&(!d.trim().equals(""))) return d.trim();
        else
        {
            d = getInformationItem(domainInformation, contextDirS, domain, 1);
            if (d != null) return d.trim();
        }

        return null;

    }
    public static String getInformationItem1(String contextDirS, String domain, int itemIndex)
    {
        return getInformationItem(null, contextDirS, domain, itemIndex);
    }
    public static String getInformationItem(List<String> infoList, String contextDirS, String domain, int itemIndex)
    {
        if ((infoList == null)||(infoList.size() == 0))
            infoList = getDomainInformation(contextDirS);

        if ((infoList == null)||(infoList.size() == 0)) return null;

        if (domain == null) domain = "";
        else domain = domain.trim();
        if (domain.equals("")) return null;

        for (String str:infoList)
        {
            int n = 0;
            String buff = "";
            str = str + "\t";

            String domainItem = null;
            String vomFileName = null;
            String inverseAllowed = null;
            String comments = "";
            for (int i=0;i<str.length();i++)
            {
                String achar = str.substring(i, i+1);
                if (achar.equals("\t"))
                {
                    if (n == 0) vomFileName = buff.trim();
                    else if (n == 1)
                    {
                        domainItem = buff.trim();
                        //System.out.println("CCC find index="+itemIndex+", find domain="+domain+", vom=" + vomFileName + ", domain=" + domainItem);
                    }
                    else if (n == 2) inverseAllowed = buff.trim();
                    else comments = comments + buff.trim();
                    n++;
                    buff = "";
                }
                else buff = buff + achar;
            }

            if (domainItem.equals(domain))
            {
                if (itemIndex == 0) return vomFileName;
                if (itemIndex == 1) return domainItem;
                if (itemIndex == 2) return inverseAllowed;
                if (itemIndex == 3) return comments;
            }
        }
        return null;
    }
    public static List<String> getDomainInformation(String contextDirS)
    {
        return getDomainInformation(contextDirS, true);
    }
    public static List<String> getDomainInformation(String contextDirS, boolean readIndexFile)
    {
        if ((contextDirS == null)||(contextDirS.trim().equals(""))) return null;
        File contextDir = new File(contextDirS);
        if (contextDir.exists())
        {
            if (contextDir.isDirectory())
            {
                String contextDirPath = contextDir.getAbsolutePath();
                if (!contextDirPath.endsWith(File.separator)) contextDirPath = contextDirPath + File.separator;

                if (readIndexFile)
                {
                    File indexFile = new File(contextDirPath + CONTEXT_DOMAIN_INDEX_FILE_NAME);
                    if ((indexFile.exists())&&(indexFile.isFile()))
                    {

                        List<String> list = null;
                        try
                        {
                            list = FileUtil.readFileIntoList(indexFile.getAbsolutePath());
                        }
                        catch(IOException ie)
                        {
                            list = null;
                        }
                        if ((list != null)&&(list.size() > 0)) return list;
                    }
                }
            }
            else if (contextDir.isFile())
            {
                if ((contextDir.getName().toLowerCase().endsWith(".jar"))||
                    (contextDir.getName().toLowerCase().endsWith(".zip"))) {}
                else return null;
            }
            else return null;
        }
        else return null;

        List<String[]> result = null;

        try
        {
            result = ContextVocabularyTranslation.getDomainInformation("", contextDirS, "", false);
        }
        catch(Exception ee)
        {
            System.out.println("ContextVocabularyTranslation.getDomainInformation() : " + ee.getMessage());
            return null;
        }

        String fileName = "";
        int row = -1;
        List<String> list  = new ArrayList<String>();
        for(String[] arr: result)
        {
            row++;
            String info = "";
            String line = "";
            for (int i=0;i< arr.length;i++)
            {
                if (i == 0)
                {
                    int idx = arr[i].indexOf("@");
                    String domain = arr[i];
                    String file = "";
                    if (idx > 0)
                    {
                        domain = arr[i].substring(0, idx);
                        file = arr[i].substring(idx+1);
                    }
//                    if (!file.trim().equals(""))
//                    {
//                        if (fileName.equals(file)) file = "";
//                        else fileName = file;
//                    }
//                    else
//                    {
//                        fileName = "";
//                        file = "No File";
//                    }
                    line = line + file + "\t";
                    line = line + domain + "\t";
                }
                else if (i == 1) line = line + arr[i] + "\t";
                else info = info + arr[i];

            }
            line = line + info;
            list.add(line);
        }

        return list;
    }

    public static void refreshContext(String contextDirS)
    {
        if ((contextDirS == null)||(contextDirS.trim().equals(""))) return;
        File contextDir = new File(contextDirS);
        String contextDirPath = null;
        if (contextDir.exists())
        {
            if (contextDir.isDirectory())
            {
                contextDirPath = contextDir.getAbsolutePath();
                if (!contextDirPath.endsWith(File.separator)) contextDirPath = contextDirPath + File.separator;
            }
            else return;
//            else if (contextDir.isFile())
//            {
//                if ((contextDir.getName().toLowerCase().endsWith(".jar"))||
//                    (contextDir.getName().toLowerCase().endsWith(".zip"))) {}
//                else return null;
//            }
//            else return null;
        }
        else return;
        List<String> list = getDomainInformation(contextDir.getAbsolutePath(), false);
        if ((list == null)||(list.size() == 0)) return;

        FileWriter fw = null;
        try
        {
            fw = new FileWriter(contextDirPath + CONTEXT_DOMAIN_INDEX_FILE_NAME);
            for (String str:list)
                fw.write(str + "\r\n");
            fw.close();
        }
        catch(Exception ie)
        {
            System.out.println("CONTEXT DOMAIN INDEX File Writing Error(" + contextDirPath + CONTEXT_DOMAIN_INDEX_FILE_NAME + ") : " + ie.getMessage());
        }
    }

    /*
    public static String getDomainXMLPart(String contextDirS, String domainS)
    {
        if ((contextDirS == null)||(contextDirS.trim().equals(""))) return null;
        File contextDir = new File(contextDirS);
        if (contextDir.exists())
        {
            if (contextDir.isDirectory())
            {
                String contextDirPath = contextDir.getAbsolutePath();
                if (!contextDirPath.endsWith(File.separator)) contextDirPath = contextDirPath + File.separator;
            }
            else if (contextDir.isFile())
            {
                if ((contextDir.getName().toLowerCase().endsWith(".jar"))||
                    (contextDir.getName().toLowerCase().endsWith(".zip"))) {}
                else return null;
            }
            else return null;
        }
        else return null;

        List<String[]> result = null;

        try
        {
            result = ContextVocabularyTranslation.getDomainInformation("", contextDirS, "", false);
        }
        catch(Exception ee)
        {
            return null;
        }

        String fileName = "";
        int row = -1;
        List<String> list  = new ArrayList<String>();
        for(String[] arr: result)
        {
            row++;
            String info = "";
            String line = "";
            for (int i=0;i< arr.length;i++)
            {
                if (i == 0)
                {
                    int idx = arr[i].indexOf("@");
                    String domain = arr[i];
                    String file = "";
                    if (idx > 0)
                    {
                        domain = arr[i].substring(0, idx);
                        file = arr[i].substring(idx+1);
                    }
                    if (!file.trim().equals(""))
                    {
                        if (fileName.equals(file)) file = "";
                        else fileName = file;
                    }
                    else
                    {
                        fileName = "";
                        file = "No File";
                    }
                    line = line + file + "\t";
                    line = line + domain + "\t";
                }
                else if (i == 1) line = line + arr[i] + "\t";
                else info = info + arr[i];

            }
            line = line + info;
            list.add(line);
        }

        return list;
    }
    */
}
