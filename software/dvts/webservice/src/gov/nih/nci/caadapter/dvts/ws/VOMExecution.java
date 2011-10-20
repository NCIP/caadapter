package gov.nih.nci.caadapter.dvts.ws;

import gov.nih.nci.caadapter.dvts.common.function.FunctionException;
import gov.nih.nci.caadapter.dvts.common.util.Config;
import gov.nih.nci.caadapter.dvts.FunctionVocabularyMapping;
//import gov.nih.nci.caadapter.dvts.hl7.map.FunctionVocabularyMapping;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Oct 11, 2011
 * Time: 10:57:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class VOMExecution extends HttpServlet
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
        String user = "";
        String vom = "";
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
        user = req.getParameter("userid");
        if ((user == null)||(user.trim().equals("")))
        {
            user = req.getParameter("user");
        }
        vom = req.getParameter("vom");
        if ((vom == null)||(vom.trim().equals("")))
        {
            vom = req.getParameter("vomfile");
            if ((vom == null)||(vom.trim().equals("")))
            {
               vom = req.getParameter("file");
            }
        }
        String inverseS = req.getParameter("inverse");
        
        if ((user == null)||(user.trim().equals("")))
        {
            out.println(assemblResultMessage("Error", "No User ID", "", ip, domain, value));
            return;
        }
        user = user.trim();
        fileDataPath = util.checkLoginID(user);
        if (fileDataPath == null)
        {
            out.println(assemblResultMessage("Error", "Invalid User ID : " + user, "", ip, domain, value));
            return;
        }
        if (!fileDataPath.endsWith(File.separator)) fileDataPath = fileDataPath + File.separator;
        if ((vom == null)||(vom.trim().equals("")))
        {
            out.println(assemblResultMessage("Error", "No VOM file", "", ip, domain, value));
            return;
        }
        String vomPath = fileDataPath + "vom" + File.separator + vom;
        File vomFile =  new File(vomPath);
        if ((!vomFile.exists())||(!vomFile.isFile()))
        {
            out.println(assemblResultMessage("Error", "Not found VOM File : " + vomPath, "", ip, domain, value));
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
                                   vomFile.getAbsolutePath() + Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR + domain,
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
