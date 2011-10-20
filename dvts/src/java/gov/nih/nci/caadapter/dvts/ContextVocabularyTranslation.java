package gov.nih.nci.caadapter.dvts;

import gov.nih.nci.caadapter.dvts.common.function.FunctionException;
import gov.nih.nci.caadapter.dvts.common.util.FileUtil;

import java.io.File;
import java.util.List;

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
        return translate(context, domain, value, false);
    }
    public static String translate(String context, String domain, String value, boolean inverse) throws Exception
    {
        if (context == null) context = "";
        else context = context.trim();
        if (context.equals("")) throw new Exception("Context value is null.");

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


        java.util.List<String> contextLine = FileUtil.getContextAddresses();

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
                System.out.println("CCCCC ("+context+") name=" + name + ", addr=" + addr);
                if (name.equals(context))
                {
                    context = addr;
                    //break;
                }
            }
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

        String inv = "inverse=true";
        if (!inverse)
        {
            try
            {
                FunctionVocabularyMapping fvm = new FunctionVocabularyMapping(
                                                  (new FunctionVocabularyMapping()).getTypeNamePossibleList()[1],
                                                   context,
                                                   inverse);
                if (inverse) return fvm.translateInverseValue(value);
                else return fvm.translateValue(value);
            }
            catch(FunctionException fe)
            {}
            inv = "inverse=false";
        }

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
        try
        {
            String domainF = "";
            if (domainFile != null) domainF = "@" + domainFile;
            String urlS = cont + "domain=" + domain + domainF + "&" + inv + "&value=" + value;
            //System.out.println(" Input URL: "+urlS);
            FunctionVocabularyMapping fvm = new FunctionVocabularyMapping(
                                              (new FunctionVocabularyMapping()).getTypeNamePossibleList()[1],
                                               urlS,
                                               false);

            return fvm.translateValue(value);
        }
        catch(FunctionException fe)
        {
            throw new Exception("FunctionException : " + domain + " : " + fe.getMessage());
        }

        //throw new Exception("This context and domain are invalid : " + context + ", " + domain);
    }

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
                System.out.println("Usage: commandLine and 3 or 4 arguments - context domain inputValue [inverse=true:false]");
                return;
            }
        }
        else
        {
            System.out.println("Usage: commandLine and 3 or 4 arguments - context domain inputValue [inverse=true:false]");
            return;
        }
        String result = "";
        try
        {
            result = ContextVocabularyTranslation.translate(args[0], args[1], args[2], inverse);
        }
        catch(Exception ee)
        {
            System.out.println("Error: " + ee.getMessage());
            return;
        }
        System.out.println("Result value : " + result);
    }
}
