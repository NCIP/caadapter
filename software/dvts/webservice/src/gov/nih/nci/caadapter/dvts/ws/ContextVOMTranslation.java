/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.ws;

import gov.nih.nci.caadapter.dvts.FunctionVocabularyMapping;
import gov.nih.nci.caadapter.dvts.ContextVocabularyTranslation;
import gov.nih.nci.caadapter.dvts.ws.util.TranslationResponseUtil;
import gov.nih.nci.caadapter.dvts.common.util.Config;
import gov.nih.nci.caadapter.dvts.common.function.FunctionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Oct 14, 2011
 * Time: 4:20:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class ContextVOMTranslation extends HttpServlet
{

    public void doGet(HttpServletRequest req, HttpServletResponse res)
                throws ServletException, IOException
    {
        CaadapterWSUtil util = new CaadapterWSUtil();

        util.setBaseURLToPropertyfile(req);
        res.setContentType("text/html;charset=KSC5601");
        PrintWriter out = res.getWriter();

        boolean inverse = false;

        String ip = req.getRemoteAddr();

        String context = null;
        String domain = null;
        String searchDomain = null;
        String value = null;
        String inverseS = null;
        String showFile = null;
        String[] paramArray = new String[] {context, domain, searchDomain, value, inverseS, showFile};

        try
        {
            Enumeration en = req.getParameterNames();

            String paramName = "";
            String param = "";
            int idxP = 0;
            while(en.hasMoreElements())
            {
                paramName = (String) en.nextElement();
                if (paramName == null) continue;
                paramName = paramName.trim();
                if (paramName.equals("")) continue;
                param = req.getParameter(paramName);
                if ((param == null)||(param.trim().equals(""))) continue;

                if (paramName.equalsIgnoreCase("context")) idxP = 0;
                else if (paramName.equalsIgnoreCase("domain")) idxP = 1;
                else if (paramName.equalsIgnoreCase("domainName")) idxP = 1;
                else if (paramName.equalsIgnoreCase("searchDomain")) idxP = 2;
                else if (paramName.equalsIgnoreCase("domainSearch")) idxP = 2;
                else if (paramName.equalsIgnoreCase("value")) idxP = 3;
                else if (paramName.equalsIgnoreCase("val")) idxP = 3;
                else if (paramName.equalsIgnoreCase("input")) idxP = 3;
                else if (paramName.equalsIgnoreCase("source")) idxP = 3;
                else if (paramName.equalsIgnoreCase("inverse")) idxP = 4;
                else if (paramName.equalsIgnoreCase("inv")) idxP = 4;
                else if (paramName.equalsIgnoreCase("showfile")) idxP = 5;
                else if (paramName.equalsIgnoreCase("downfile")) idxP = 5;
                else
                {
                    System.out.println("Not assigned parameter = " + paramName + ", value=" + param);
                    continue;
                }

                if (paramArray[idxP] != null)
                {
                    System.out.println("Duplicate parameter=" + paramName + ", ready value=" + paramArray[idxP] + ", duplacate value=" + param);
                }
                else paramArray[idxP] = param;
            }

            for (int i=0;i<paramArray.length;i++)
            {
                String item = paramArray[i];
                if (item == null) item = "";
                else item = item.trim();
                if (i == 0) context = item;
                else if (i == 1) domain = item;
                else if (i == 2) searchDomain = item;
                else if (i == 3) value = item;
                else if (i == 4) inverseS = item;
                else if (i == 5) showFile = item;
            }

            if ((inverseS != null)&&(!inverseS.trim().equals("")))
            {
                inverseS = inverseS.trim().toLowerCase();
                if ((inverseS.equals("true"))||(inverseS.equals("yes"))) inverse = true;
            }
            
        }
		catch(Exception ee)
		{
            out.println(TranslationResponseUtil.assemblResultMessage(context, inverse, "Error", "Parameter parsing error : " + ee.getMessage(), "", ip, domain, value, false));
            return;
		}

//        if (context.startsWith("SampleContext"))
//        {
//
//        }
//        else if (domain.equals("*.*"))
//        {
//
//        }
//        else if(value.equals("*.*"))
//        {
//            ContextVocabularyTranslation.getDomainXMLPart(String contextAddrFileName, String contextSymbol, String domain) throws Exception
//        }

        TranslationResponseUtil.sendTranslationResult(out, ip, context, domain, searchDomain, value, inverse, showFile);

    }
}