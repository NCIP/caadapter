package gov.nih.nci.caadapter.dvts.ws.util;

import gov.nih.nci.caadapter.dvts.FunctionVocabularyMapping;
import gov.nih.nci.caadapter.dvts.ContextVocabularyTranslation;
import gov.nih.nci.caadapter.dvts.ws.CaadapterWSUtil;
import gov.nih.nci.caadapter.dvts.ws.GeneralUtilitiesWS;
import gov.nih.nci.caadapter.dvts.common.function.FunctionException;
import gov.nih.nci.caadapter.dvts.common.util.FileUtil;
import gov.nih.nci.caadapter.dvts.common.util.Config;
import gov.nih.nci.caadapter.dvts.common.util.vom.ManageVOMFile;
import gov.nih.nci.caadapter.dvts.common.meta.VocabularyMappingData;

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
        dataOnly = false;
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
        String tempContext = null;
        if (context.equals(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_WILD_CHARACTER))
        {
            tempContext = context;
            context = ManageVOMFile.SAMPLE_CONTEXT_01_TAG;
        }

        if ((context.equals(ManageVOMFile.SAMPLE_CONTEXT_01_TAG))||
            (context.equals(ManageVOMFile.SAMPLE_CONTEXT_02_TAG)))
        {

            fileDataPath = util.getRootScenarioPath() + context + File.separator + Config.VOCABULARY_MAPPING_DIR;
            File dir = new File(fileDataPath);
            if ((!dir.exists())||(!dir.isDirectory()))
            {
                if (!dir.mkdirs())
                {
                    return assemblResultMessage(context, inverse, "Error", "Sample Context creation Failure(1).", "", ip, domain, value, dataOnly);
                }
                CreateSampleContextUtil cc = new CreateSampleContextUtil(dir);
                if (!cc.createSampleVOMFile())
                {
                    return assemblResultMessage(context, inverse, "Error", "Sample Context creation Failure(2).", "", ip, domain, value, dataOnly);
                }
            }

            if (tempContext != null)
            {
                while(true)
                {
                    if (dir.getParentFile() == null)
                    {
                        dir = null;
                        break;
                    }
                    dir = dir.getParentFile();
                    if (dir.getName().equals(util.getScenarioDirName())) break;
                }
                if (dir == null)
                {
                    return assemblResultMessage(context, inverse, "Error", "Context folder searching failure", "", ip, domain, value, dataOnly);
                }
                List<String> contextList = new ArrayList<String>();
                File[] files = dir.listFiles();
                for(File f:files)
                {
                    if (!f.isDirectory()) continue;
                    if (f.getName().equals(util.getAdministratorID())) continue;
                    if (f.getName().equals(util.getAccessFailureTag())) continue;
                    contextList.add(f.getName());
                }
                return getContextInformationHtml(contextList);
            }
            //return assemblResultMessage(context, true, "Information", "Vocabulary Translation Successfully finished.", "S", ip, "MaritalStatus", "Single", false);
        }
        else fileDataPath = util.checkLoginID(context);

        if (fileDataPath == null)
        {
            return assemblResultMessage(context, inverse, "Error", "Not Found Context : " + context, "", ip, domain, value, dataOnly);
        }
        if (fileDataPath.endsWith(File.separator)) fileDataPath = fileDataPath.substring(0, fileDataPath.length() - File.separator.length());
//        if ((vom == null)||(vom.trim().equals("")))
//        {
//            out.println(assemblResultMessage("Error", "No VOM file", "", ip, domain, value));
//            return;
//        }


        if (!fileDataPath.endsWith(File.separator + Config.VOCABULARY_MAPPING_DIR)) fileDataPath = fileDataPath + File.separator + Config.VOCABULARY_MAPPING_DIR ;

        String vomPath = fileDataPath;
        File vomRepoDir =  new File(vomPath);
        if ((!vomRepoDir.exists())||(!vomRepoDir.isDirectory()))
        {
            return assemblResultMessage(context, inverse, "Error", "Not found VOM (or VOM) Repositary : " + vomPath, "", ip, domain, value, dataOnly);
        }

        if ((showFile != null)&&(!showFile.trim().equals("")))
        {
            if (showFile.toLowerCase().endsWith("."+Config.VOCABULARY_MAPPING_DIR)) {}
            else if (showFile.toLowerCase().endsWith(".xml")) {}
            else
            {
                return assemblResultMessage(context, inverse, "Error", "This is not a VOM file (showing VOM File) : " + showFile, "", ip, domain, value, dataOnly);
            }

            String vomFilePath = fileDataPath + File.separator + showFile;
            File showFileF = new File(vomFilePath);
            if ((!showFileF.exists())||(!showFileF.isFile()))
            {
                return assemblResultMessage(context, inverse, "Error", "This VOM File is not exist. (showing VOM File) : " + vomFilePath, "", ip, domain, value, dataOnly);
            }

            String cont = FileUtil.readFileIntoString(showFileF.getAbsolutePath());
            if ((cont == null)||(cont.trim().equals("")))
            {
                return assemblResultMessage(context, inverse, "Error", "Reading Failure VOM File (showing VOM File) : " + vomFilePath, "", ip, domain, value, dataOnly);
            }
            return cont;
        }

        if (searchDomainB)
        {
            domain = "SearchDomain";
            value = "SearchDomain";
        }
        else if (domain.equals(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_WILD_CHARACTER))
        {
            List<String[]> list = null;
            String msg = "";
            try
            {
                list = ContextVocabularyTranslation.getDomainInformation("", vomPath, "", false);
            }
            catch(Exception ee)
            {
                msg = " : " + ee.getMessage();
                list = null;
            }
            if ((list == null)||(list.size() == 0)) return assemblResultMessage(context, inverse, "Error", "Failure of Collecting domain information" + msg, "", ip, domain, value, dataOnly);

            return getDomainInformationHtml(list, context, inverse);
        }
        else if (value.equals(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_WILD_CHARACTER))
        {
            String res = "";
            try
            {
                res = ContextVocabularyTranslation.getDomainXMLPart("", vomRepoDir.getAbsolutePath(), domain);
            }
            catch(Exception ee)
            {
                return assemblResultMessage(context, inverse, "Error", "Domain XML extracting Failure : " + ee.getMessage(), "", ip, domain, value, dataOnly);
            }
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + res;
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

        String fN = ManageVOMFile.getVOMFileNameWithDoamin(vomRepoDir.getAbsolutePath(), domain);

        if ((fN != null)&&(!fN.trim().equals("")))
        {
            File f = new File(vomPath + File.separator + fN);
            if ((f.exists())&&(f.isFile())) vomFile = f;
            //System.out.println("CCCC tx fN=" + fN + ", vomPath=" + vomPath + File.separator + fN);
        }

        if ((vomFile == null)&&(!searchDomainB))
        {
            return assemblResultMessage(context, inverse, "Error", "This Domain Not Found:" + domain + ", vomPath=" + vomPath + File.separator + fN, "", ip, domain, value, dataOnly);
        }

        //System.out.println(gUtil.getNowDate() + ": TestFunctionVocabularyMappingService from " + ip + ", vom file="+vomPath+", domain=" + domain + ", value=" + value);

        String result = "";
        String level = "Information";
        String message = "Vocabulary Translation Successfully finished.";

        if (searchDomainB)
        {
            List<String[]> allDomainsArr = null;
            try
            {
                allDomainsArr = ContextVocabularyTranslation.getDomainInformation(null, vomRepoDir.getAbsolutePath(), "", false);
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
            boolean elsecaseApplied = false;
            try
            {
                FunctionVocabularyMapping fvm = new FunctionVocabularyMapping(
                                       (new FunctionVocabularyMapping()).getTypeNamePossibleList()[0],
                                       vomFile.getAbsolutePath(),
                                       domain,
                                       inverse);
                if (inverse) result = fvm.translateInverseValue(value);
                else result = fvm.translateValue(value);
                elsecaseApplied = fvm.wasElsecaseApplied();

            }
            catch(FunctionException fe)
            {
                return assemblResultMessage(context, inverse, "Error", "FunctionException("+fe.getErrorNumber()+") : " + fe.getMessage(), "", ip, domain, value, dataOnly);
                //System.out.println("FunctionException("+fe.getErrorNumber()+") : vom=" + vomFile.getAbsolutePath());
                //fe.printStackTrace();

            }

            return assemblResultMessage(context, inverse, level, message, result, ip, domain, value, elsecaseApplied);
        }

    }

    public static String assemblResultMessage(String context, boolean inverse, String level, String msg, String valueC, String ipS, String domainP, String sourceH, boolean dataOnly)
    {
        return assemblResultMessage(context, inverse, level, msg, valueC, ipS, domainP, sourceH, null, dataOnly);
    }
    private static String assemblResultMessage(String context, boolean inverse, String level, String msg, String valueC, String ipS, String domainP, String sourceH, List<String[]> inforList, boolean elsecaseApplied)
    {

//        if (dataOnly)
//        {
//            String resultLines = "";
//            if ((inforList != null)&&(inforList.size() > 0))
//            {
//                for (String[] arr:inforList)
//                {
//                    String result = "";
//                    for (String str:arr)
//                    {
//                        result = result + str.trim() + "|";
//                    }
//                    result = result.trim();
//
//                    if (!result.equals("")) resultLines = resultLines + "\t" + result.substring(0, result.length()-1);
//
//                }
//                if (resultLines.length() > 2) valueC = "%null%";
//
//            }
//
//            if ((valueC == null)||(valueC.trim().equals("")))
//            {
//                valueC = "%null%";
//            }
//            String inverseS = "%null%";
//            if (inverse) inverseS = "true";
//
//            return context + "\t" + inverseS + "\t" + level +"\t"+ msg +"\t"+ valueC +"\t"+ ipS +"\t"+ domainP +"\t"+ sourceH + resultLines;
//
//        }

        String inverseV = "";
        if (inverse) inverseV = "\" inverse=\"true";

        String elsecaseV = "";
        if (elsecaseApplied) elsecaseV = " elsecaseApplied=\"true\"";

        String r = "<?xml version=\"1.0\" encoding=\"euc-kr\"?>\r\n" +
                "<VocabularyMappingData>\r\n" +
                "    <ReturnMessage errorLevel=\""+ level + "\">\r\n" +
                "      " + msg + "\r\n" +
                "    </ReturnMessage>\r\n" +
                "    <MappingSource ip=\""+ipS+"\" domainName=\"" + domainP + "\" sourceValue=\"" + sourceH + "\" context=\"" + context + inverseV + "\"/>\r\n";

        if ((inforList != null)&&(inforList.size() > 0))
        {
            r = r + "    <MappingResults"+elsecaseV+">\r\n";
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
            r = r + "    <MappingResults"+elsecaseV+">\r\n" +
                    "       <Result>" + valueC.trim() + "</Result>\r\n" +
                    "    </MappingResults>\r\n" ;
        }
        r = r + "</VocabularyMappingData>";
        return r;
    }

    private static String getDomainInformationHtml(List<String[]> list, String context, boolean includeVom)
    {
        String retLine = "";
        retLine = retLine + "<html>";
        retLine = retLine + "<head><title>Context Domain Information</title></head>";
        retLine = retLine + "<body bgcolor='white'>";
        retLine = retLine + "<font color='brown'><center><br><br>";
        retLine = retLine + "<h1>'"+context+"' Context Domain List</h1></center></font><br>";
        retLine = retLine + "<font color='green'><h5>";
        retLine = retLine + "<table>";
        retLine = retLine + "<tr>";
        String vomHead = "";
        String aln = " align=\"left\"";
        if (includeVom)  vomHead = "<th"+aln+">VOM File</th>";
        retLine = retLine + vomHead + "<th"+aln+">Domain</th><th"+aln+">Inverse allowed</th><th"+aln+">annotation</th>";
        retLine = retLine + "</tr>";
        for(String[] line:list)
        {
            retLine = retLine + "<tr>";
            String vom ="";
            String domain = "";
            String inverseAllowed = "";
            String comment = "";

            for (int i=0;i<line.length;i++)
            {
                if (i == 0)
                {
                    String item = line[i];
                    int idx = item.indexOf("@");
                    if (idx <= 0)
                    {
                        domain = item;
                    }
                    else
                    {
                        vom = item.substring(idx+1);
                        domain = item.substring(0, idx);
                    }
                }
                //else if (i == 1) domain = line[i];
                else if (i == 1) inverseAllowed = line[i];
                else comment = comment + line[i];
            }
            String vomItem = "";
            if (includeVom)
            {
                if (vom.trim().equals("")) vomItem = "<td></td>";
                else vomItem = "<td><a target=\"_blank\" href=\"./ContextVOMTranslation?context="+context+"&showfile="+vom+"\">"+vom+"</a></td>";
            }

            retLine = retLine + vomItem + "<td><a target=\"_blank\" href=\"./ContextVOMTranslation?context="+context+"&domain="+domain+"&value=*.*\">"+domain+"</a></td>" + "<td>"+inverseAllowed+"</td>" + "<td>"+comment+"</td>";
            retLine = retLine + "</tr>";
        }
        retLine = retLine + "</table>";
        retLine = retLine + "</h5></font></body>";
        retLine = retLine + "</html>";

        return retLine;
    }
    private static String getContextInformationHtml(List<String> contexts)
    {
        String retLine = "";
        retLine = retLine + "<html>";
        retLine = retLine + "<head><title>Context List Information</title></head>";
        retLine = retLine + "<body bgcolor='white'>";
        retLine = retLine + "<font color='brown'><center><br><br>";
        retLine = retLine + "<h1>Context List Information in this service</h1></center></font><br>";
        retLine = retLine + "<font color='green'><h5>";
        if ((contexts == null)||(contexts.size() == 0))
        {
            retLine = retLine + "Any context is not available in this service.";
            retLine = retLine + "</h5></font></body></html>";
            return retLine;
        }
        else if (contexts.size() == 1) retLine = retLine + "<ul><lh>Following context is available in this service.</lh>";
        else retLine = retLine + "<ul><lh>Following contexts are available in this service.</lh>";

        for(String line:contexts)
        {
            retLine = retLine + "<li><a target=\"_top\" href=\"./ContextVOMTranslation?context="+line+"&domain=*.*\">"+line+"</a></li>";
        }
        retLine = retLine + "</ul>";
        retLine = retLine + "</h5></font></body>";
        retLine = retLine + "</html>";

        return retLine;
    }
}
