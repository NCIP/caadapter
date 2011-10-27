package gov.nih.nci.caadapter.dvts.ws.util;

import gov.nih.nci.caadapter.dvts.FunctionVocabularyMapping;
import gov.nih.nci.caadapter.dvts.ContextVocabularyTranslation;
import gov.nih.nci.caadapter.dvts.ws.CaadapterWSUtil;
import gov.nih.nci.caadapter.dvts.ws.GeneralUtilitiesWS;
import gov.nih.nci.caadapter.dvts.common.function.FunctionException;
import gov.nih.nci.caadapter.dvts.common.util.FileUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Oct 25, 2011
 * Time: 10:33:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class TranslationResponseUtil
{
    public static void sendTranslationResult(PrintWriter out, String ip, String context, String domain, String searchDomain, String value, boolean inverse, String showFile)
    {
        out.println(generateTranslationResult(ip, context, domain, searchDomain, value, inverse, showFile));
    }
    public static String generateTranslationResult(String ip, String context, String domain, String searchDomain, String value, boolean inverse, String showFile)
    {
        return generateTranslationResult(ip, context, domain, searchDomain, value, inverse, showFile, false);
    }
    public static String generateTranslationResult(String ip, String context, String domain, String searchDomain, String value, boolean inverse, String showFile, boolean dataOnly)
    {
        CaadapterWSUtil util = new CaadapterWSUtil();
        GeneralUtilitiesWS gUtil = new GeneralUtilitiesWS();

        String fileDataPath = "";

        String[] paramArray = new String[] {context, domain, searchDomain, value, showFile};

        for (int i=0;i<paramArray.length;i++)
        {
            String item = paramArray[i];
            if (item == null) item = "";
            else item = item.trim();
            if (i == 0) context = item;
            else if (i == 1) domain = item;
            else if (i == 2) searchDomain = item;
            else if (i == 3) value = item;
            else if (i == 4) showFile = item;
        }

        boolean searchDomainB = false;
        if ((searchDomain.equalsIgnoreCase("true"))||(searchDomain.equalsIgnoreCase("yes")))
        {
            searchDomainB = true;
            domain = "##SearchDomain";
        }

        if ((context == null)||(context.trim().equals("")))
        {
            return assemblResultMessage(context, inverse, "Error", "No Context", "", ip, domain, value, dataOnly);

        }
        context = context.trim();
        fileDataPath = util.checkLoginID(context);
        if (fileDataPath == null)
        {
            return assemblResultMessage(context, inverse, "Error", "Not Found Context : " + context, "", ip, domain, value, dataOnly);

        }
        if (!fileDataPath.endsWith(File.separator)) fileDataPath = fileDataPath + File.separator;
//        if ((vom == null)||(vom.trim().equals("")))
//        {
//            out.println(assemblResultMessage("Error", "No VOM file", "", ip, domain, value));
//            return;
//        }

        String vomPath = fileDataPath + "vom";// + File.separator + vom;
        File vomRepoDir =  new File(vomPath);
        if ((!vomRepoDir.exists())||(!vomRepoDir.isDirectory()))
        {
            return assemblResultMessage(context, inverse, "Error", "Not found VOM Repositary : " + vomPath, "", ip, domain, value, dataOnly);
        }

        if ((showFile != null)&&(!showFile.trim().equals("")))
        {
            if (showFile.toLowerCase().endsWith(".vom")) {}
            else if (showFile.toLowerCase().endsWith(".xml")) {}
            else
            {
                return assemblResultMessage(context, inverse, "Error", "This is not a VOM file (showing VOM File) : " + showFile, "", ip, domain, value, dataOnly);
            }

            String vomFilePath = fileDataPath + "vom" + File.separator + showFile;
            File showFileF = new File(vomFilePath);
            if ((!showFileF.exists())||(!showFileF.isFile()))
            {
                return assemblResultMessage(context, inverse, "Error", "This VOM File is not exist. (showing VOM File) : " + showFile, "", ip, domain, value, dataOnly);
            }

            String cont = FileUtil.readFileIntoString(showFileF.getAbsolutePath());
            if ((cont == null)||(cont.trim().equals("")))
            {
                return assemblResultMessage(context, inverse, "Error", "Reading Failure VOM File (showing VOM File) : " + showFile, "", ip, domain, value, dataOnly);
            }
            return cont;
        }

        if (searchDomainB)
        {
            domain = "SearchDomain";
            value = "SearchDomain";
        }
        else
        {
            if ((domain == null)||(domain.trim().equals("")))
            {
                return assemblResultMessage(context, inverse, "Error", "No domain name parameter", "", ip, domain, value, dataOnly);

            }
            if ((value == null)||(value.trim().equals("")))
            {
                return assemblResultMessage(context, inverse, "Error", "No source value parameter", "", ip, domain, value, dataOnly);

            }
        }

        File vomFile = null;
        FunctionVocabularyMapping fvm0 = new FunctionVocabularyMapping();

        List<String> allDomains = new ArrayList<String>();
        for(File f:vomRepoDir.listFiles())
        {
            if (!f.isFile()) continue;
            String fileName = f.getName();
            if (!fileName.toLowerCase().endsWith(".vom")) continue;

            List<String> domainList = null;
            try
            {
                domainList = fvm0.getDomains(f.getAbsolutePath());
            }
            catch(FunctionException fe)
            {
                continue;
            }
            if (domainList == null) continue;
            for (String domain1:domainList)
            {
                allDomains.add(domain1);
                if (domain1.trim().equals(domain))
                {
                    vomFile = f;
                }
            }
            if (vomFile != null) break;
        }
        if ((vomFile == null)&&(!searchDomainB))
        {
            return assemblResultMessage(context, inverse, "Error", "This Domain Not Found:" + domain, "", ip, domain, value, dataOnly);

        }


        System.out.println(gUtil.getNowDate() + ": TestFunctionVocabularyMappingService from " + ip + ", vom file="+vomPath+", domain=" + domain + ", value=" + value);



        String result = "";
        String level = "Information";
        String message = "Successful searching";

        if (searchDomainB)
        {
            List<String[]> allDomainsArr = null;
            try
            {
                allDomainsArr = ContextVocabularyTranslation.getDomainInformation(vomRepoDir.getAbsolutePath(), "", false);
            }
            catch(Exception ee)
            {
                return assemblResultMessage(context, inverse, "Error", "Error on domain Search : " + ee.getMessage(), "", ip, domain, value, dataOnly);
                //ee.printStackTrace();

            }
            if ((allDomainsArr == null)||(allDomainsArr.size() == 0))
            {
                return assemblResultMessage(context, inverse, "Error", "No domain result for domain search", "", ip, "DomainSearch", "DomainSearch", dataOnly);

            }
            //for (String[] d:allDomains) result = result + d[0] + ";";
            //if (result.endsWith(";")) result = result.substring(0, result.length()-1);
            return assemblResultMessage(context, inverse, level, message, result, ip, "DomainSearch", "DomainSearch", allDomainsArr, dataOnly);
        }
        else
        {
            try
            {
                FunctionVocabularyMapping fvm = new FunctionVocabularyMapping(
                                       (new FunctionVocabularyMapping()).getTypeNamePossibleList()[0],
                                       vomFile.getAbsolutePath(),
                                       domain,
                                       inverse);
                if (inverse) result = fvm.translateInverseValue(value);
                else result = fvm.translateValue(value);
            }
            catch(FunctionException fe)
            {
                return assemblResultMessage(context, inverse, "Error", "FunctionException("+fe.getErrorNumber()+") : " + fe.getMessage(), "", ip, domain, value, dataOnly);
                //System.out.println("FunctionException("+fe.getErrorNumber()+") : vom=" + vomFile.getAbsolutePath());
                //fe.printStackTrace();

            }

            return assemblResultMessage(context, inverse, level, message, result, ip, domain, value, dataOnly);
        }

    }

    public static String assemblResultMessage(String context, boolean inverse, String level, String msg, String valueC, String ipS, String domainP, String sourceH, boolean dataOnly)
    {
        return assemblResultMessage(context, inverse, level, msg, valueC, ipS, domainP, sourceH, null, dataOnly);
    }
    private static String assemblResultMessage(String context, boolean inverse, String level, String msg, String valueC, String ipS, String domainP, String sourceH, List<String[]> inforList, boolean dataOnly)
    {

        if (dataOnly)
        {
            String resultLines = "";
            if ((inforList != null)&&(inforList.size() > 0))
            {
                for (String[] arr:inforList)
                {
                    String result = "";
                    for (String str:arr)
                    {
                        result = result + str.trim() + "|";
                    }
                    result = result.trim();

                    if (!result.equals("")) resultLines = resultLines + "\t" + result.substring(0, result.length()-1);

                }
                if (resultLines.length() > 2) valueC = "%null%";

            }

            if ((valueC == null)||(valueC.trim().equals("")))
            {
                valueC = "%null%";
            }
            String inverseS = "%null%";
            if (inverse) inverseS = "true";

            return context + "\t" + inverseS + "\t" + level +"\t"+ msg +"\t"+ valueC +"\t"+ ipS +"\t"+ domainP +"\t"+ sourceH + resultLines;

        }

        String inverseV = "";
        if (inverse) inverseV = "\" inverse=\"true";
        String r = "<?xml version=\"1.0\" encoding=\"euc-kr\"?>\r\n" +
                "<VocabularyMappingData>\r\n" +
                "    <ReturnMessage errorLevel=\""+ level + "\">\r\n" +
                "      " + msg + "\r\n" +
                "    </ReturnMessage>\r\n" +
                "    <MappingSource ip=\""+ipS+"\" domainName=\"" + domainP + "\" sourceValue=\"" + sourceH + "\" context=\"" + context + inverseV + "\"/>\r\n";

        if ((inforList != null)&&(inforList.size() > 0))
        {
            r = r + "    <MappingResults>\r\n";
            for (String[] arr:inforList)
            {
                String result = "";
                for (String str:arr)
                {
                    result = result + str.trim() + "|";
                }
                r = r + "       <Result>" + result.substring(0, result.length()-1) + "</Result>\r\n";
            }
            r = r + "    </MappingResults>\r\n" ;
        }
        else if ((valueC != null)&&(!valueC.trim().equals("")))
        {
            r = r + "    <MappingResults>\r\n" +
                    "       <Result>" + valueC.trim() + "</Result>\r\n" +
                    "    </MappingResults>\r\n" ;
        }
        r = r + "</VocabularyMappingData>";
        return r;
    }
}
