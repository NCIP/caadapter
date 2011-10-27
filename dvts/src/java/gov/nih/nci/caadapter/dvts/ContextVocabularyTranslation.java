package gov.nih.nci.caadapter.dvts;

import gov.nih.nci.caadapter.dvts.common.function.FunctionException;
import gov.nih.nci.caadapter.dvts.common.util.FileUtil;

import java.io.File;
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
        return translate(null, context, domain, value, false);
    }
    public static String translate(String contextAddrFileName, String context, String domain, String value) throws Exception
    {
        return translate(contextAddrFileName, context, domain, value, false);
    }
    public static String translate(String contextAddrFileName, String contextSymbol, String domain, String value, boolean inverse) throws Exception
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

        //String context = searchContextPhysicalAddress(contextAddrFileName, contextSymbol);
        String context = searchContextPhysicalAddress( contextSymbol);

        if ((context == null)||(context.trim().equals("")))
        {
            String context2 = searchContextSymbolPhysicalAddress(contextSymbol);
            if (context2 != null) context = contextSymbol;
            else throw new Exception("This Context cannot be found. : " + contextSymbol);
        }


        File conFile = new File(context);
        if (conFile.exists())
        {
            File vomFile = null;
            if (conFile.isFile())
            {
                domainFile = null;
                vomFile = conFile;
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

        String inv = "inverse=true";
        String res = null;
        FunctionVocabularyMapping fvm = null;
        if (!inverse)
        {
            try
            {
                fvm = new FunctionVocabularyMapping(
                                                  (new FunctionVocabularyMapping()).getTypeNamePossibleList()[1],
                                                   context,
                                                   inverse);
                if (inverse) res = fvm.translateInverseValue(value);
                else res = fvm.translateValue(value);
            }
            catch(FunctionException fe)
            {}
            inv = "inverse=false";
        }

        if ((res != null)&&(!res.trim().equals("")))
        {
            if (searchDomain)
            {
                //System.out.println("CCC W 1 : " + res);
                return fvm.getRecentUrlVomHandler().getMappingResults();
            }
            else
            {
                List<String> ll = new ArrayList<String>();
                ll.add(res);
                return ll;
            }
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
        res = null;
        String urlS = "";
        try
        {
            String domainF = "";
            if (domainFile != null) domainF = "@" + domainFile;
            urlS = cont + "domain=" + domain + domainF + "&" + inv + "&value=" + value;
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
                return fvm.getRecentUrlVomHandler().getMappingResults();
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


    public static List<String[]> getDomainInformation(String contextSymbol, String vomFileName) throws Exception
    {
        List<String[]> domainList = null;
        Exception er = null;
        try
        {
            domainList = getDomainInformation(contextSymbol, vomFileName, true);
        }
        catch(Exception ee)
        {
            er = ee;
        }

        if ((domainList != null)&&(domainList.size() > 0)) return domainList;

        try
        {
            domainList = getDomainInformation(contextSymbol, vomFileName, false);
        }
        catch(Exception ee)
        {
            if (er == null) er = ee;
        }

        if ((domainList != null)&&(domainList.size() > 0)) return domainList;
        if (er == null) throw new Exception("Any Domain information is not found: context=" + contextSymbol +", file="+vomFileName);
        else throw er;
    }
    public static List<String[]> getDomainInformation(String contextSymbol, String vomFileName, boolean contextTransform) throws Exception
    {
        if (contextSymbol == null) contextSymbol = "";
        else contextSymbol = contextSymbol.trim();
        if (contextSymbol.equals("")) throw new Exception("Context value is null.");

        if (vomFileName == null) vomFileName = "";
        else vomFileName = vomFileName.trim();

        String context = "";
        if (contextTransform)
        {
            context = searchContextPhysicalAddress(contextSymbol);
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
                if (vomFileName.equals("")) {}
                else if (conFile.getName().equals(vomFileName)) {}
                else throw new Exception("This cannot be context (File Name is not matched). : " + contextSymbol);

                List<String> domainL = fvm0.getDomains(conFile.getAbsolutePath());
                return fvm0.getRecentVOMHandler().getDomains();
            }
            else if (conFile.isDirectory())
            {
                File[] fileList = conFile.listFiles();

                for(File f:fileList)
                {
                    if (!f.isFile()) continue;
                    String fileName = f.getName();

                    if ((fileName.toLowerCase().endsWith(".vom"))||
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
                    if (vomFileName.equals("")) fileNameTag = "@" + fileName;

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

        String params = "searchdomain=true";
        if (context.indexOf("?") < 0)
        {
            context = context + "?" + params;
        }
        else
        {
            if (context.endsWith("&")) context = context + params;
            else context = context + "&" + params;
        }

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
    public static String searchContextPhysicalAddress(String contextSymbol)
    {
        return searchContextPhysicalAddress(null, contextSymbol, true);
    }
    public static String searchContextSymbolPhysicalAddress(String contextSymbol)
    {
        return searchContextPhysicalAddress(null, contextSymbol, false);
    }

    private static String searchContextPhysicalAddress(String contextAddrFileName, String contextSymbol, boolean symbol)
    {
        java.util.List<String> contextLine = FileUtil.getContextAddresses();

        String context = null;
        if ((contextLine != null)&&(contextLine.size() > 0))
        {
            for (String line:contextLine)
            {
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
    /*
    public static void main(String[] args)
    {
        boolean inverse = false;
        if (args.length == 3) {}
        else if (args.length == 4)
        {
            String inv = args[3].trim();
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
        else
        {
            System.out.println("Usage: commandLine [-f:contextAddressPropertyFileName] and 3 or 4 arguments - context domain inputValue [inverse=true:false]");
            return;
        }
        String result = "";
        if (args[0].)
        try
        {
            result = ContextVocabularyTranslation.translate(args[0], args[1], args[2], inverse);
        }
        catch(Exception ee)
        {
            System.out.println("Error: ..." + ee.getMessage());
            return;
        }
        System.out.println("Result value : " + result);
    }
     */

}
