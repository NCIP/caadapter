package gov.nih.nci.caadapter.dvts.ws;

import gov.nih.nci.caadapter.dvts.FunctionVocabularyMapping;
import gov.nih.nci.caadapter.dvts.common.util.Config;
import gov.nih.nci.caadapter.dvts.common.function.FunctionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Oct 14, 2011
 * Time: 1:18:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class ContextVOMTranslation
{

    public void doGet(HttpServletRequest req, HttpServletResponse res)
                throws ServletException, IOException
    {
        CaadapterWSUtil util = new CaadapterWSUtil();
        GeneralUtilitiesWS gUtil = new GeneralUtilitiesWS();

        String fileDataPath = "";

        util.setBaseURLToPropertyfile(req);
        res.setContentType("text/html;charset=KSC5601");
        PrintWriter out = res.getWriter();


        String ip = req.getRemoteAddr();
        String domain = "";
        String value = "";
        String context = "";
        //String vom = "";
        boolean inverse = false;

        domain = req.getParameter("domain");
        value = req.getParameter("value");
        if ((value == null)||(value.trim().equals("")))
        {
            value = req.getParameter("source");
            if ((value == null)||(value.trim().equals("")))
            {
                value = req.getParameter("sourceValue");
                if ((value == null)||(value.trim().equals("")))
                {
                    value = req.getParameter("val");
                }
            }
        }
        context = req.getParameter("context");
        if ((context == null)||(context.trim().equals("")))
        {
            context = req.getParameter("user");
            if ((context == null)||(context.trim().equals("")))
            {
                context = req.getParameter("userid");
            }
        }

        String inverseS = req.getParameter("inverse");

        if ((context == null)||(context.trim().equals("")))
        {
            out.println(assemblResultMessage("Error", "No Context", "", ip, domain, value));
            return;
        }
        context = context.trim();
        fileDataPath = util.checkLoginID(context);
        if (fileDataPath == null)
        {
            out.println(assemblResultMessage("Error", "Not Found Context : " + context, "", ip, domain, value));
            return;
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
            out.println(assemblResultMessage("Error", "Not found VOM Repositary : " + vomPath, "", ip, domain, value));
            return;
        }
        if ((domain == null)||(domain.trim().equals("")))
        {
            out.println(assemblResultMessage("Error", "No domain name parameter", "", ip, domain, value));
            return;
        }
        if ((value == null)||(value.trim().equals("")))
        {
            out.println(assemblResultMessage("Error", "No source value parameter", "", ip, domain, value));
            return;
        }

        File vomFile = null;
        FunctionVocabularyMapping fvm0 = new FunctionVocabularyMapping();
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
                if (domain1.trim().equals(domain))
                {
                    vomFile = f;
                    break;
                }
            }
            if (vomFile != null) break;
        }

        if ((inverseS != null)&&(!inverseS.trim().equals("")))
        {
            inverseS = inverseS.trim().toLowerCase();
            if ((inverseS.equals("true"))||(inverseS.equals("yes"))) inverse = true;
        }

        System.out.println(gUtil.getNowDate() + ": TestFunctionVocabularyMappingService from " + ip + ", vom file="+vomPath+", domain=" + domain + ", value=" + value);



        String result = "";
        String level = "Information";
        String message = "Successful searching";

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
            out.println(assemblResultMessage("Error", "FunctionException("+fe.getErrorNumber()+") : " + fe.getMessage(), "", ip, domain, value));
            System.out.println("FunctionException("+fe.getErrorNumber()+") : vom=" + vomFile.getAbsolutePath());
            fe.printStackTrace();
            return;
        }

        out.println(assemblResultMessage(level, message, result, ip, domain, value));


    }

    public void doPost(HttpServletRequest req, HttpServletResponse response)
                throws ServletException, IOException
    {
        doGet(req, response);
    }

    private String assemblResultMessage(String level, String msg, String valueC, String ipS, String domainP, String sourceH)
    {
        String r = "<?xml version=\"1.0\" encoding=\"euc-kr\"?>\r\n" +
                "<VocabularyMappingData>\r\n" +
                "    <Message level=\""+ level + "\">\r\n" +
                "      " + msg + "\r\n" +
                "    </Message>\r\n" +
                "    <MappingSource ip=\""+ipS+"\" domain=\"" + domainP + "\" value=\"" + sourceH + "\"/>\r\n";
        if ((valueC != null)&&(!valueC.trim().equals(""))) r = r + "    <MappingResult value=\"" + valueC.trim() + "\"/>\r\n";
        r = r + "</VocabularyMappingData>";
        return r;
    }
}
