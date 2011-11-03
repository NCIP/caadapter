package gov.nih.nci.caadapter.dvts;

import gov.nih.nci.caadapter.dvts.common.function.FunctionException;
import gov.nih.nci.caadapter.dvts.common.util.FileUtil;
import gov.nih.nci.caadapter.dvts.common.util.Config;
import gov.nih.nci.caadapter.dvts.common.util.vom.ManageVOMFile;
import gov.nih.nci.caadapter.dvts.common.tools.ZipUtil;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Oct 17, 2011
 * Time: 12:29:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContextVocabularyTranslation
{
    public static String translate(String context, String domain, String value) throws Exception
    {
        return translate(null, context, domain, value, false, false);
    }
    public static String translate(String contextAddrFileName, String context, String domain, String value) throws Exception
    {
        return translate(contextAddrFileName, context, domain, value, false, false);
    }

    public static String translate(String contextAddrFileName, String contextSymbol, String domain, String value, boolean inverse) throws Exception
    {
        return translate(contextAddrFileName, contextSymbol, domain, value, inverse, false);
    }
    public static String translate(String contextAddrFileName, String contextSymbol, String domain, String value, boolean inverse, boolean transformSkip) throws Exception
    {

        if (contextSymbol == null) contextSymbol = "";
        else contextSymbol = contextSymbol.trim();
        if (contextSymbol.equals("")) throw new Exception("Context value is null.");

        if (domain == null) domain = "";
        else domain = domain.trim();
        if (domain.equals("")) throw new Exception("Domain value is null.");

        String domainFile = null;
        int idx = domain.indexOf("@");
        if (idx > 0)
        {
            domainFile = domain.substring(idx + 1);
            domain = domain.substring(0, idx);
        }

        if ((domainFile != null)&&(domainFile.trim().equals(""))) domainFile = null;

        if (value == null) value = "";
        else value = value.trim();
        if (value.equals("")) throw new Exception("Source vocabulary value is null.");

        String context = "";
        if (transformSkip) context = contextSymbol;
        else
        {
            context = searchContextPhysicalAddress(contextAddrFileName, contextSymbol);
            if ((context == null)||(context.trim().equals("")))
                context = searchContextSymbolPhysicalAddress(contextAddrFileName, contextSymbol);
        }
        //String context = searchContextPhysicalAddress( contextSymbol);

        if ((context == null)||(context.trim().equals("")))
        {
            File file = new File(contextSymbol);
            //boolean valid = false;
            if (file.exists())
            {
                String conPath = file.getAbsolutePath();
                if (file.isDirectory()) context = contextSymbol;
                else if (file.isFile())
                {
                    if ((conPath.toLowerCase().equals(".jar"))||
                        (conPath.toLowerCase().equals(".zip")))
                    {
                        context = contextSymbol;
                    }
                }
            }
            if (context == null) throw new Exception("This Context cannot be found. : " + contextSymbol);
        }


        File conFile = new File(context);
        if (conFile.exists())
        {
            File vomFile = null;
            if (conFile.isFile())
            {
                if ((conFile.getName().toLowerCase().equals(".jar"))||
                    (conFile.getName().toLowerCase().equals(".zip")))
                {
                    {
                        ZipUtil zipUtil = new ZipUtil(conFile.getAbsolutePath());
                        List<String> entryNames = zipUtil.getEntryNames();

                        String entryN = null;
                        for (String name:entryNames)
                        {
                            if ((name.toLowerCase().endsWith("." + Config.VOCABULARY_MAPPING_DIR))||
                            (name.toLowerCase().endsWith(".xml"))) {}
                            else continue;

                            if ((domainFile != null)&&(!name.equals(domainFile))) continue;

                            List<String> domainL = null;
                            try
                            {
                                domainL = (new FunctionVocabularyMapping()).getDomains(zipUtil.getAccessURL(zipUtil.getZipFile().getEntry(name)));
                            }
                            catch(FunctionException fe)
                            {
                                continue;
                            }
                            if ((domainL == null)||(domainL.size() == 0)) continue;

                            for (String s:domainL)
                            {
                                if (s.trim().equals(domain)) entryN = name;
                                break;
                            }

                            if (entryN != null) break;
                        }
                        if (entryN == null)
                            throw new Exception("This File doesn't include domain. ("+domain+") : " + contextSymbol);

                        String url = zipUtil.getAccessURL(zipUtil.getZipFile().getEntry(entryN));

                        try
                        {
                            FunctionVocabularyMapping fvm = new FunctionVocabularyMapping(
                                                              (new FunctionVocabularyMapping()).getTypeNamePossibleList()[2],
                                                               url,
                                                               domain,
                                                               inverse);
                            if (inverse) return fvm.translateInverseValue(value);
                            else return fvm.translateValue(value);
                        }
                        catch(FunctionException fe)
                        {
                            throw new Exception("Translation Error with ZIP file : " + fe.getMessage());
                        }
                    }
                }
                else
                {
                    domainFile = null;
                    vomFile = conFile;
                }
            }
            else if (conFile.isDirectory())
            {
                if (domainFile == null)
                    domainFile = ManageVOMFile.getVOMFileNameWithDoamin(conFile.getAbsolutePath(), domain);

                if (domainFile != null)
                {
                    String contextPath = conFile.getAbsolutePath();
                    if (!contextPath.endsWith(File.separator)) contextPath = contextPath + File.separator;
                    vomFile = new File(contextPath + domainFile);
                }

                //FunctionVocabularyMapping fvm0 = new FunctionVocabularyMapping();

                /*
                {
                    File[] fileList = conFile.listFiles();

                    for(File f:fileList)
                    {
                        if (!f.isFile()) continue;
                        String fileName = f.getName();

                        if (fileName.equals(domainFile))
                        {
                            List<String> domainList = null;
                            try
                            {
                                domainList = fvm0.getDomains(f.getAbsolutePath());
                            }
                            catch(FunctionException fe)
                            {
                                continue;
                            }
                            if (domainList == null) break;
                            for (String domain1:domainList)
                            {
                                if (domain1.trim().equals(domain))
                                {
                                    vomFile = f;
                                    break;
                                }
                            }

                            break;
                        }
                    }
                }
                if (vomFile == null)
                {
                    for(File f:fileList)
                    {
                        if (!f.isFile()) continue;
                        String fileName = f.getName();
                        if (fileName.toLowerCase().endsWith(".vom")) {}
                        else if (fileName.toLowerCase().endsWith(".xml")) {}
                        else continue;

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
                            if (domain1.trim().equals(domain))
                            {
                                vomFile = f;
                                break;
                            }
                        }
                        if (vomFile != null) break;
                    }
                }
                */
            }
            if ((vomFile == null)||(!vomFile.exists())||(!vomFile.isFile()))
                throw new Exception("Domain("+domain+") is not found in this context : ." + context);

            FunctionVocabularyMapping fvm = new FunctionVocabularyMapping(
                                              (new FunctionVocabularyMapping()).getTypeNamePossibleList()[0],
                                               vomFile.getAbsolutePath(),
                                               domain,
                                               inverse);
            if (inverse) return fvm.translateInverseValue(value);
            else return fvm.translateValue(value);
        }

        try
        {
            FunctionVocabularyMapping fvm = new FunctionVocabularyMapping(
                                              (new FunctionVocabularyMapping()).getTypeNamePossibleList()[2],
                                               context,
                                               domain,
                                               inverse);
            if (inverse) return fvm.translateInverseValue(value);
            else return fvm.translateValue(value);
        }
        catch(FunctionException fe)
        {}

        List<String> resList = getURLTranslation(context, domain, value, inverse, false);

        return resList.get(0);
    }
    private static List<String> getURLTranslation(String context, String domain, String value, boolean inverse, boolean searchDomain) throws Exception
    {
        List<String> res = null;
        if (context.toLowerCase().indexOf("/rest") > 0)
        {
            Exception error = null;
            try
            {
                res = getURLTranslationRestful(context, domain, value, inverse, searchDomain);
            }
            catch(Exception ee)
            {
                error = ee;
                res = null;
            }
            if (res == null)
            {
                try
                {
                    res = getURLTranslationWS(context, domain, value, inverse, searchDomain);
                }
                catch(Exception ee)
                {
                    res = null;
                }
            }
            if (res != null) return res;
            throw error;
        }
        else
        {
            Exception error = null;
            try
            {
                res = getURLTranslationWS(context, domain, value, inverse, searchDomain);
            }
            catch(Exception ee)
            {
                error = ee;
                res = null;
            }
            if (res == null)
            {
                try
                {
                    res = getURLTranslationRestful(context, domain, value, inverse, searchDomain);
                }
                catch(Exception ee)
                {
                    res = null;
                }
            }
            if (res != null) return res;
            throw error;
        }
    }
    private static List<String> getURLTranslationRestful(String context, String domain, String value, boolean inverse, boolean searchDomain) throws Exception
    {

        String inv = "/inverse";
        if (!inverse) inv = "";

        String res = null;
        FunctionVocabularyMapping fvm = null;


        String domainFile = null;
        int idx = domain.indexOf("@");
        if (idx > 0)
        {
            domainFile = domain.substring(idx + 1);
            domain = domain.substring(0, idx);
        }
        if ((domainFile != null)&&(domainFile.trim().equals(""))) domainFile = null;


        String cont = context;
        if (!cont.endsWith("/")) cont = cont + "/";

        String urlS = "";
        try
        {
            String domainF = "";
            if (domainFile != null) domainF = "@" + domainFile;
            urlS = cont + "domain/" + domain + domainF + "/value/" + value + inv;
            //System.out.println(" Input URL: "+urlS);
            fvm = new FunctionVocabularyMapping(
                                              (new FunctionVocabularyMapping()).getTypeNamePossibleList()[1],
                                               urlS,
                                               false);

            res = fvm.translateValue(value);
        }
        catch(FunctionException fe)
        {
            throw new Exception("FunctionException : " + domain + " : " + fe.getMessage());
        }

        if ((res != null)&&(!res.trim().equals("")))
        {
            if (searchDomain)
            {
                //System.out.println("CCC W 2 : " + res);
                //System.out.println("CCC W 2 urlS=" + urlS);
                return fvm.getRecentUrlVomHandler().getMappingResults().getResult();
            }
            else
            {
                List<String> ll = new ArrayList<String>();
                ll.add(res);
                return ll;
            }
        }
        throw new Exception("FunctionException : Any result was not found : domain=" + domain + ", value=" + value);
    }

    private static List<String> getURLTranslationWS(String context, String domain, String value, boolean inverse, boolean searchDomain) throws Exception
    {

        String inv = "inverse=true";
        if (!inverse) inv = "inverse=false";

        if (domain == null) domain = "";
        else domain = domain.trim();

        if (value == null) value = "";
        else value = value.trim();

        if (domain.equals(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_WILD_CHARACTER))
        {
            if (value.equals("")) value = "temp";
        }
        if (searchDomain)
        {
            if (domain.equals("")) domain = "temp";
            if (value.equals("")) value = "temp";
        }

        String domainFile = null;
        int idx = domain.indexOf("@");
        if (idx > 0)
        {
            domainFile = domain.substring(idx + 1);
            domain = domain.substring(0, idx);
        }
        if ((domainFile != null)&&(domainFile.trim().equals(""))) domainFile = null;


        String cont = context;
        if (cont.indexOf("?") > 0)
        {
            if (!cont.endsWith("?"))
            {
               if (!cont.endsWith("&")) cont = cont + "&";
            }

        }
        else cont = cont + "?";
        //if (!cont.endsWith("?")) cont = cont + "?";
        //res = null;
        String urlS = "";

        String domainF = "";
        if (domainFile != null) domainF = "@" + domainFile;
        urlS = cont + "domain=" + domain + domainF + "&" + inv + "&value=" + value;


        String res = null;
        FunctionVocabularyMapping fvm = null;
        if (searchDomain)
        {
            try
            {
                fvm = new FunctionVocabularyMapping(
                                                  (new FunctionVocabularyMapping()).getTypeNamePossibleList()[1],
                                                   cont + "&searchdomain=true",
                                                   false);
                if (inverse) res = fvm.translateInverseValue(value);
                else res = fvm.translateValue(value);
            }
            catch(FunctionException fe)
            {
                System.out.println("getURLTranslationWS() searchDomain Exception : " + fe.getMessage());
            }

            if ((res != null)&&(!res.trim().equals("")))
                return fvm.getRecentUrlVomHandler().getMappingResults().getResult();

        }
        //System.out.println("CCC W 1 : " + res + ", context=" +context);


//        String domainFile = null;
//        int idx = domain.indexOf("@");
//        if (idx > 0)
//        {
//            domainFile = domain.substring(idx + 1);
//            domain = domain.substring(0, idx);
//        }
//        if ((domainFile != null)&&(domainFile.trim().equals(""))) domainFile = null;


//        String cont = context;
//        if (cont.indexOf("?") > 0)
//        {
//            if (!cont.endsWith("?"))
//            {
//               if (!cont.endsWith("&")) cont = cont + "&";
//            }
//
//        }
//        else cont = cont + "?";
        //if (!cont.endsWith("?")) cont = cont + "?";
        //res = null;
//        String urlS = "";
        try
        {
//            String domainF = "";
//            if (domainFile != null) domainF = "@" + domainFile;
//            urlS = cont + "domain=" + domain + domainF + "&" + inv + "&value=" + value;
            //System.out.println(" Input URL: "+urlS);
            fvm = new FunctionVocabularyMapping(
                                              (new FunctionVocabularyMapping()).getTypeNamePossibleList()[1],
                                               urlS,
                                               false);

            res = fvm.translateValue(value);
        }
        catch(FunctionException fe)
        {
            throw new Exception("FunctionException : " + domain + " : " + fe.getMessage());
        }

        if ((res != null)&&(!res.trim().equals("")))
        {
            if (searchDomain)
            {
                //System.out.println("CCC W 2 : " + res);
                //System.out.println("CCC W 2 urlS=" + urlS);
                return fvm.getRecentUrlVomHandler().getMappingResults().getResult();
            }
            else
            {
                List<String> ll = new ArrayList<String>();
                ll.add(res);
                return ll;
            }
        }
        throw new Exception("FunctionException : Any result was not found : domain=" + domain + ", value=" + value);
    }


    public static List<String[]> getDomainInformation(String contextAddrFileName, String contextSymbol, String vomFileName) throws Exception
    {
        List<String[]> domainList = null;
        Exception er = null;
        try
        {
            domainList = getDomainInformation(contextAddrFileName, contextSymbol, vomFileName, true);
        }
        catch(Exception ee)
        {
            er = ee;
        }

        if ((domainList != null)&&(domainList.size() > 0)) return domainList;

        try
        {
            domainList = getDomainInformation(contextAddrFileName, contextSymbol, vomFileName, false);
        }
        catch(Exception ee)
        {
            if (er == null) er = ee;
        }

        if ((domainList != null)&&(domainList.size() > 0)) return domainList;
        if (er == null) throw new Exception("Any Domain information is not found: context=" + contextSymbol +", file="+vomFileName);
        else throw er;
    }

    public static List<String[]> getDomainInformation(String contextAddrFileName, String contextSymbol, String vomFileName, boolean contextTransform) throws Exception
    {
        if (contextSymbol == null) contextSymbol = "";
        else contextSymbol = contextSymbol.trim();
        if (contextSymbol.equals("")) throw new Exception("Context value is null.");

        if (vomFileName == null) vomFileName = "";
        else vomFileName = vomFileName.trim();

        String context = "";
        if (contextTransform)
        {
            context = searchContextPhysicalAddress(contextAddrFileName, contextSymbol);
            //System.out.println("CCCCC XX0 ("+contextSymbol+") : " + context);
        }
        else context = contextSymbol;

        if ((context == null)||(context.trim().equals("")))
        {
            throw new Exception("This Context cannot be found. (1) : " + contextSymbol);
        }

        List<String[]> domainList = new ArrayList<String[]>();

        File conFile = new File(context);
        //System.out.println("CCCCC XX1 : " + context);
        if (conFile.exists())
        {
            //System.out.println("CCCCC XX2 : " + conFile.getAbsolutePath());
            FunctionVocabularyMapping fvm0 = new FunctionVocabularyMapping();
            if (conFile.isFile())
            {
                String conPath = conFile.getAbsolutePath();
                if ((conPath.toLowerCase().equals(".jar"))||
                    (conPath.toLowerCase().equals(".zip")))
                {
                    ZipUtil zipUtil = new ZipUtil(conPath);
                    List<String> entryNames = zipUtil.getEntryNames();
                    for (String name:entryNames)
                    {
                        if ((name.toLowerCase().endsWith("." + Config.VOCABULARY_MAPPING_DIR))||
                        (name.toLowerCase().endsWith(".xml"))) {}
                        else continue;

                        if (!vomFileName.equals(""))
                        {
                            if (name.equals(vomFileName)) {}
                            else if (name.endsWith("/" + vomFileName)) {}
                            else continue;
                        }

                        List<String> domainL = null;
                        try
                        {
                            domainL = fvm0.getDomains(zipUtil.getAccessURL(zipUtil.getZipFile().getEntry(name)));
                        }
                        catch(FunctionException fe)
                        {
                            continue;
                        }
                        if ((domainL == null)||(domainL.size() == 0)) continue;

                        List<String[]> domainList2 = fvm0.getRecentVOMHandler().getDomains();
                        String fileNameTag = "";
                        String cName = name;
                        while(true)
                        {
                            int idx = cName.indexOf("/");
                            if (idx < 0) break;
                            cName = cName.substring(idx+1);
                        }
                        fileNameTag = "@" + cName;

                        for (String[] domain1:domainList2)
                        {
                            String[] newArr = new String[domain1.length];
                            for (int i=0;i<domain1.length;i++)
                            {
                                String item = domain1[i];
                                if (i == 0) item = domain1[i] + fileNameTag;
                                newArr[i] = item;
                            }
                            domainList.add(newArr);
                        }

                    }
                    return domainList;
                }
                else
                {
                    if (vomFileName.equals("")) {}
                    else if (conFile.getName().equals(vomFileName)) {}
                    else throw new Exception("This cannot be context (File Name is not matched). : " + contextSymbol);

                    List<String> domainL = fvm0.getDomains(conPath);
                    List<String[]> domainL2 = fvm0.getRecentVOMHandler().getDomains();
                    List<String[]> domainL_New = new ArrayList<String[]>();
                    for (String[] arr:domainL2)
                    {
                        String[] newArr = new String[arr.length];
                        for (int i=0;i<arr.length;i++)
                        {
                            String item = arr[i];
                            if (i == 0) item = arr[i] + "@" + conFile.getName();
                            newArr[i] = item;
                        }
                        domainL_New.add(newArr);
                    }
                    return domainL_New;
                }
            }
            else if (conFile.isDirectory())
            {
                File[] fileList = conFile.listFiles();

                for(File f:fileList)
                {
                    if (!f.isFile()) continue;
                    String fileName = f.getName();

                    if ((fileName.toLowerCase().endsWith("." + Config.VOCABULARY_MAPPING_DIR))||
                        (fileName.toLowerCase().endsWith(".xml"))) {}
                    else continue;

                    if (!vomFileName.equals(""))
                    {
                       if (!fileName.equals(vomFileName)) continue;
                    }

                    List<String> domainL = null;
                    try
                    {
                        domainL = fvm0.getDomains(f.getAbsolutePath());
                    }
                    catch(FunctionException fe)
                    {
                        continue;
                    }
                    if ((domainL == null)||(domainL.size() == 0)) continue;

                    List<String[]> domainList2 = fvm0.getRecentVOMHandler().getDomains();
                    String fileNameTag = "";
                    //if (vomFileName.equals(""))
                        fileNameTag = "@" + fileName;

                    for (String[] domain1:domainList2)
                    {
                        String[] newArr = new String[domain1.length];
                        for (int i=0;i<domain1.length;i++)
                        {
                            String item = domain1[i];
                            if (i == 0) item = domain1[i] + fileNameTag;
                            newArr[i] = item;
                        }
                        domainList.add(newArr);
                    }
                }
                return domainList;
            }
        }

        if (context.length() < 15)
        {
            throw new Exception("This Context cannot be found. (2) : " + contextSymbol);
        }


        try
        {
            FunctionVocabularyMapping fvm0 = new FunctionVocabularyMapping();
            if (conFile.isFile())
            {
                List<String> domainL = fvm0.getDomains(context);
                return fvm0.getRecentVOMHandler().getDomains();
            }
        }
        catch(FunctionException fe)
        {}

        String params = "searchdomain=true&value=cs";
        if (context.indexOf("?") < 0)
        {
            context = context + "?" + params;
        }
        else
        {
            if (context.endsWith("&")) context = context + params;
            else context = context + "&" + params;
        }
        //System.out.println("CCC XX : " + context);
        List<String> ll = getURLTranslation(context, "Any", "Any", false, true);
        if ((ll != null)&&(ll.size() > 0))
        {
            List<String[]> resList = new ArrayList<String[]>();
            for(String str:ll)
            {
                List<String> list = new ArrayList<String>();
                String buff = "";
                for(int i=0;i<str.length();i++)
                {
                    String achar = str.substring(i, i+1);
                    if (achar.equals("|"))
                    {
                        list.add(buff.trim());
                        buff = "";
                    }
                    else buff = buff + achar;
                }
                if (!buff.trim().equals("")) list.add(buff.trim());

                String[] arr = new String[list.size()];
                for(int i=0;i<list.size();i++) arr[i] = list.get(i);
                resList.add(arr);
            }
            return resList;
        }
        throw new Exception("No domain in this context : " + contextSymbol);
    }
    public static String searchContextPhysicalAddress(String contextAddrFileName, String contextSymbol)
    {
        return searchContextPhysicalAddress(contextAddrFileName, contextSymbol, true);
    }
    public static String searchContextSymbolPhysicalAddress(String contextAddrFileName, String contextAddr)
    {
        return searchContextPhysicalAddress(contextAddrFileName, contextAddr, false);
    }

    private static String searchContextPhysicalAddress(String contextAddrFileName, String contextSymbol, boolean symbol)
    {
        java.util.List<String> contextLine = FileUtil.getContextAddresses(contextAddrFileName);

        String context = null;
        if ((contextLine != null)&&(contextLine.size() > 0))
        {
            for (String line:contextLine)
            {
                if (line == null) continue;
                line = line.trim();
                if (line.startsWith("#")) continue;
                int idx2 = line.indexOf("@");
                if (idx2 < 0)
                {
                    idx2 = line.indexOf("=");
                    if (idx2 < 0) continue;
                }
                String name = line.substring(0, idx2);
                //System.out.println("CCCCC name=" + name);
                String addr = line.substring(idx2+1);
                //System.out.println("CCCCC ("+context+") name=" + name + ", addr=" + addr);
                if (symbol)
                {
                    if (name.equals(contextSymbol))
                    {
                        context = addr;
                        //break;
                    }
                    else if (contextSymbol.equals("EVS"))
                    {
                        if (name.endsWith("/"+contextSymbol)) context = addr;
                    }
                }
                else
                {
                    if (addr.equals(contextSymbol))
                    {
                        context = name;
                        if (context.endsWith("/EVS")) context = "EVS";
                    }
                }
            }
        }
        return context;
        //if ((context == null)||(context.trim().equals(""))) throw new Exception("This Context cannot be found. : " + contextSymbol);
    }

    public static void main(String[] args)
    {
        boolean inverse = false;
        String hasPropertyFile = null;
        String context = null;
        String domain = null;
        String value = null;
        String inverseS = null;

        try
        {
            if (args[0].toLowerCase().startsWith("-f:"))
            {
                hasPropertyFile = args[0].substring(3);
                context = args[1];
                domain = args[2];
                value = args[3];
                if (args.length == 5) inverseS = args[4];
            }
            else
            {
                context = args[0];
                domain = args[1];
                value = args[2];
                if (args.length == 4) inverseS = args[3];
            }
        }
        catch(Exception ee)
        {
            System.out.println("Usage: commandLine [-f:contextAddressPropertyFileName] and 3 or 4 arguments - context domain inputValue [inverse=true:false]");
            return;
        }

        if (inverseS != null)
        {
            String inv = inverseS.trim();
            if (inv.equalsIgnoreCase("true")) inverse = true;
            else if (inv.equalsIgnoreCase("false")) inverse = false;
            else if (inv.equalsIgnoreCase("yes")) inverse = true;
            else if (inv.equalsIgnoreCase("no")) inverse = false;
            else if (inv.equalsIgnoreCase("t")) inverse = true;
            else if (inv.equalsIgnoreCase("f")) inverse = false;
            else if (inv.equalsIgnoreCase("y")) inverse = true;
            else if (inv.equalsIgnoreCase("n")) inverse = false;
            else
            {
                System.out.println("Usage: commandLine [-f:contextAddressPropertyFileName] and 3 or 4 arguments - context domain inputValue [inverse=true:false]");
                return;
            }
        }

        String result = "";

        try
        {
            result = ContextVocabularyTranslation.translate(hasPropertyFile, context, domain, value, inverse, false);
        }
        catch(Exception ee)
        {
            System.out.println("Error: ..." + ee.getMessage());
            return;
        }
        System.out.println("Result value : " + result);
    }


    public static String getDomainXMLPart(String contextAddrFileName, String contextSymbol, String domain) throws Exception
    {

        if (contextSymbol == null) contextSymbol = "";
        else contextSymbol = contextSymbol.trim();
        if (contextSymbol.equals("")) throw new Exception("Context value is null.");

        if (domain == null) domain = "";
        else domain = domain.trim();
        if (domain.equals("")) throw new Exception("Domain value is null.");

        String domainFile = null;
        int idx = domain.indexOf("@");
        if (idx > 0)
        {
            domainFile = domain.substring(idx + 1);
            domain = domain.substring(0, idx);
        }
        if ((domainFile != null)&&(domainFile.trim().equals(""))) domainFile = null;


        String context = searchContextPhysicalAddress(contextAddrFileName, contextSymbol);
        //String context = searchContextPhysicalAddress( contextSymbol);

        if ((context == null)||(context.trim().equals("")))
        {
            String context2 = searchContextSymbolPhysicalAddress(contextAddrFileName, contextSymbol);
            if (context2 == null)
            {
                File file = new File(contextSymbol);

                if (file.exists())
                {
                    String conPath = file.getAbsolutePath();
                    if (file.isDirectory()) context = contextSymbol;
                    else if (file.isFile())
                    {
                        if ((conPath.toLowerCase().equals(".jar"))||
                            (conPath.toLowerCase().equals(".zip")))
                        {
                            context = contextSymbol;
                        }
                    }
                }
                if (context == null) throw new Exception("This Context cannot be found. : " + contextSymbol);

            }
            else context = contextSymbol;
        }

        InputStream vomFileStr = null;
        File vomFile = null;

        File conFile = new File(context);
        if (conFile.exists())
        {

            if (conFile.isFile())
            {
                if ((conFile.getName().toLowerCase().equals(".jar"))||
                    (conFile.getName().toLowerCase().equals(".zip")))
                {
                    {
                        ZipUtil zipUtil = new ZipUtil(conFile.getAbsolutePath());
                        List<String> entryNames = zipUtil.getEntryNames();

                        String entryN = null;
                        for (String name:entryNames)
                        {
                            if ((name.toLowerCase().endsWith(".vom"))||
                            (name.toLowerCase().endsWith(".xml"))) {}
                            else continue;

                            if ((domainFile != null)&&(!name.equals(domainFile))) continue;

                            List<String> domainL = null;
                            try
                            {
                                domainL = (new FunctionVocabularyMapping()).getDomains(zipUtil.getAccessURL(zipUtil.getZipFile().getEntry(name)));
                            }
                            catch(FunctionException fe)
                            {
                                continue;
                            }
                            if ((domainL == null)||(domainL.size() == 0)) continue;

                            for (String s:domainL)
                            {
                                if (s.trim().equals(domain)) entryN = name;
                                break;
                            }

                            if (entryN != null) break;
                        }
                        if (entryN == null)
                            throw new Exception("This File doesn't include domain. ("+domain+") : " + contextSymbol);

                        vomFileStr = zipUtil.getZipFile().getInputStream(zipUtil.getZipFile().getEntry(entryN));
                    }
                }
                else
                {
                    domainFile = null;
                    vomFile = conFile;
                }
            }
            else if (conFile.isDirectory())
            {
                File[] fileList = conFile.listFiles();
                FunctionVocabularyMapping fvm0 = new FunctionVocabularyMapping();
                if (domainFile != null)
                {
                    for(File f:fileList)
                    {
                        if (!f.isFile()) continue;
                        String fileName = f.getName();

                        if (fileName.equals(domainFile))
                        {
                            List<String> domainList = null;
                            try
                            {
                                domainList = fvm0.getDomains(f.getAbsolutePath());
                            }
                            catch(FunctionException fe)
                            {
                                continue;
                            }
                            if (domainList == null) break;
                            for (String domain1:domainList)
                            {
                                if (domain1.trim().equals(domain))
                                {
                                    vomFile = f;
                                    break;
                                }
                            }

                            break;
                        }
                    }
                }
                if (vomFile == null)
                {
                    for(File f:fileList)
                    {
                        if (!f.isFile()) continue;
                        String fileName = f.getName();
                        if (fileName.toLowerCase().endsWith(".vom")) {}
                        else if (fileName.toLowerCase().endsWith(".xml")) {}
                        else continue;

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
                            if (domain1.trim().equals(domain))
                            {
                                vomFile = f;
                                break;
                            }
                        }
                        if (vomFile != null) break;
                    }
                }

            }
            if (vomFile == null) throw new Exception("Domain("+domain+") is not found in this context : ." + context);
        }

        if ((vomFileStr == null)&&(vomFile != null))
        {
            try { vomFileStr = new FileInputStream(vomFile); }
            catch(IOException ie) { vomFileStr = null; }
        }

        if (vomFileStr != null)
        {
            StringBuffer sb = new StringBuffer();
            DataInputStream dis = new DataInputStream(vomFileStr);

            byte bt = 0;

            while(true)
            {
                try { bt = dis.readByte(); }
                catch(IOException ie) { break; }
                catch(NullPointerException ie) { break; }

                char ch = (char) bt;
                sb.append(ch);

            }
            dis.close();
            vomFileStr.close();

            int idx1 = 0;
            String content = null;
            int n = 0;
            while(true)
            {
                idx1 = sb.indexOf("<domain ", idx1);
                if (idx1 < 0)
                {
                    n=0;
                    break;
                }
                int idx2 = sb.indexOf(">", idx1);
                if (idx2 < 0)
                {
                    n=1;
                    break;
                }

                String eleLine = sb.substring(idx1, idx2);
                //System.out.println("CCCC eleLine = " + eleLine);
                eleLine = eleLine.substring(eleLine.indexOf("name=\"") + 6);
                eleLine = eleLine.substring(0, eleLine.indexOf("\""));
                //if (eleLine.indexOf("name=\""+domain+"\"") > 0)
                if (eleLine.trim().equals(domain))
                {
                    //System.out.println("CCCC After Line = " + sb.substring(idx2));
                    int idx3 = sb.indexOf("</domain", idx2);
                    if (idx3 < 0)
                    {
                        n=3;
                        break;
                    }
                    int idx4 = sb.indexOf(">", idx3);
                    if (idx4 < 0)
                    {
                        n=4;
                        break;
                    }

                    return sb.substring(idx1, idx4+1);
                }
                idx1 = idx2;
            }
            throw new Exception("Invalid vom File ("+n+"): context=" + context + ", domain=" + domain);
        }
        throw new Exception("getDomainXMLPart() is not for URL, local access only. : context=" + context + ", domain=" + domain);
    }
}
