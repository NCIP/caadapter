/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.ws;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Dec 6, 2010
 * Time: 8:58:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormulaCalculateService extends HttpServlet
{
    HttpServletRequest req = null;
    //HttpServletResponse res = null;
    PrintWriter out = null;
    boolean isDoGet = false;

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        this.req = req;
        this.out = res.getWriter();
        isDoGet = true;
        doMain();
    }

   public void doPost(HttpServletRequest req, HttpServletResponse res)
                throws ServletException, IOException
   {
       this.req = req;
       this.out = res.getWriter();
       isDoGet = false;
       doMain();
   }

    private void doMain()
    {
        String formula = null;
        List<String> parameterNames = new ArrayList<String>();
        List<String> parameterValues = new ArrayList<String>();

        try
        {
            Enumeration en = req.getParameterNames();

            String paramName = "";
            String param = "";

            while(en.hasMoreElements())
            {
                paramName = (String) en.nextElement();
                if ((paramName == null)||(paramName.trim().equals(""))) continue;
                paramName = paramName.trim();

                param = req.getParameter(paramName);
                if (param == null) param = "";
                else param = param.trim();
                if (param.equals("")) continue;

                if (paramName.equalsIgnoreCase("formula")) formula = param;
                else
                {
                    parameterNames.add(paramName);
                    parameterValues.add(param);
                }
            }
        }
        catch(Exception ee)
        {
            formula = null;
        }

        if ((formula == null)||(formula.trim().equals("")))
        {
            generateResponseHTML("Input Formula", null, null);
            return;
        }

        if (isDoGet) formula = reformCharacter(formula);

        List<String> vars = CalculateFormula.getVariables(formula);
        if ((vars == null)||(vars.size() == 0))
        {
            if (isDoGet) out.println("Invalid Formula : " + formula);
            else generateResponseHTML("Invalid Formula! Input again.", null, null);
            return;
        }
        if (parameterNames.size() == 0)
        {
            if (isDoGet) out.println("No Variable value");
            else generateResponseHTML("Input parameter values", formula, vars);
            return;
        }

        HashMap<String, String> paramHash=new HashMap<String, String>();

        for(int i=0;i<parameterNames.size();i++)
        {
            paramHash.put(parameterNames.get(i), parameterValues.get(i));
        }
        out.println(CalculateFormula.execute(formula, paramHash));
    }

    private void generateResponseHTML(String title, String formula, List<String> list)
    {
        if ((formula == null)||(formula.trim().equals(""))) formula = "..Overwrite Formula Here..";
        else formula = formula.trim();

        String variables = "";
        String buttonName = "Find Variables";
        if ((list != null)&&(list.size() > 0))
        {
            buttonName = "Calculate";
            int n = 0;
            for (String var:list)
            {
                n++;
                variables = variables +
                         "<tr><td>\n" +
                         "<h3><font color=blue>Variable "+n+" : "+var+"</font></h3>\n" +
                         "\n" +
                         "  <input type=\"text\" name=\""+var+"\" size=\"30\" style=\"border-top-width:1; style=\"border-right-width:1; \n" +
                         "         style=\"border-bottom-width:1; style=\"border-left-width:1px; border-style:dashed;\"/> \n" +
                         "  \n" +
                         "</td></tr>\n";
            }
        }

        String html = "<html>\n" +
                "<head>\n" +
                "<title>Calculating Formula Service</title>\n" +
                "</head>\n" +
                "<body bgcolor='pink'>\n" +
                "<font color='green'>\n" +
                "<center>\n" +
                "<h1>\n" +
                "Calculating Formula Service\n" +
                "</h1>\n" +
                "\n" +
                "</font>\n" +
                "\n" +
                "<br>\n" +
                "\n" +
                "\n" +
                "<form name='calculateFormula' method='post' action='"+getRequstURL(req)+"' >\n" +
                "\n" +
                "<table border=2 cellpading=10>\n" +
                "<tr><td>\n" +
                "<h3><font color=blue>"+title+"</font></h3>\n" +
                "</td></tr>\n" +
                "<tr><td>\n" +
                "<textarea name='formula' rows=20 cols=80>\n" +
                formula + "\n" +
                "</textarea>\n" +
                "</td></tr>\n" +
                variables +
                "<tr><td>\n" +
                "<input type='submit' value='"+buttonName+"'/>&nbsp;&nbsp;&nbsp;\n" +
                "<input type='reset' value='Cancel'/></td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "<br>\n" +
                "</h2>\n" +
                "</center>\n" +
                "</form>\n" +
                "</body>\n" +
                "</html>\n" +
                "\n" +
                "";


        out.println(html);
    }

    public String getBaseURLFromRequest(HttpServletRequest req)
    {
        String url = getRequstURL(req);
        if (url == null) return null;
        url = url.trim();
        while(url.endsWith("/")) url = url.substring(0, url.length()-1).trim();

        String url2 = url;
        while(true)
        {
            String achar = url2.substring(url2.length()-1, url2.length());
            url2 = url2.substring(0,url2.length()-1);
            if (url2.equals("")) return url;
            if (achar.equals("/")) break;
        }

        return url2;
    }
    public String getRequstURL(HttpServletRequest req)
    {
        if (req == null) return null;
        StringBuffer urlB = req.getRequestURL();
        if (urlB == null) return null;
        String url = urlB.toString().trim();
        if (url.equals("")) return null;
        int idx = url.indexOf("?");
        if (idx > 0) url = url.substring(0, idx);
        return url;
    }

    public void sendOutMessage(String title, String level, String message, String link)
    {
        String retLine = "";
        retLine = retLine + "<html>";
        retLine = retLine + "<head><title>"+title+"</title></head>";
        retLine = retLine + "<body bgcolor='white'>";
        retLine = retLine + "<font color='brown'><center><br><br>";
        retLine = retLine + "<h1>"+title+"</h1></center></font><br>";

        retLine = retLine + "<font color='green'>";
        retLine = retLine + "<h5>MESSAGE LEVEL: " + level + "<!--END--></h5><br>";
        retLine = retLine + "<h5>MESSAGE: " + message + "<!--END--></h5><br>";
        if ((link != null)&&(!link.trim().equals("")))
        {
            retLine = retLine + "<h5><a href='"+link+"'>Link Here!</a></h5><br>";
        }
        retLine = retLine + "</font></body>";
        retLine = retLine + "</html>";

        out.println(retLine);
    }

    private String reformCharacter(String str)
    {
        String tt = str;
        String res = "";
        while(true)
        {
            int idx = tt.indexOf("%");
            if (idx < 0)
            {
                res = res + tt;
                break;
            }
            res = res + tt.substring(0, idx);
            tt = tt.substring(idx);
            boolean found = true;

            if (tt.startsWith("%22")) res = res + "\"";
            else if (tt.startsWith("%20")) res = res + " ";
            else if (tt.startsWith("%3C")) res = res + "<";
            else if (tt.startsWith("%3D")) res = res + "=";
            else if (tt.startsWith("%3E")) res = res + ">";
            else if (tt.startsWith("%2F")) res = res + "/";
            else if (tt.startsWith("%52")) res = res + "^";
            else if (tt.startsWith("%3B")) res = res + ";";
            else if (tt.startsWith("%3A")) res = res + ":";
            else if (tt.startsWith("%2E")) res = res + ".";
            else if (tt.startsWith("%25")) res = res + "%";
            else if (tt.startsWith("%2B")) res = res + "+";
            else if (tt.startsWith("%26")) res = res + "&";
            else if (tt.startsWith("%40")) res = res + "@";
            else if (tt.startsWith("%3F")) res = res + "?";
            else if (tt.startsWith("%21")) res = res + "!";
            else if (tt.startsWith("%23")) res = res + "#";
            else if (tt.startsWith("%2A")) res = res + "*";
            else if (tt.startsWith("%2D")) res = res + "-";
            else if (tt.startsWith("%2C")) res = res + ",";
            else if (tt.startsWith("%24")) res = res + "$";
            else
            {
                found = false;
                res = res + "%";
            }
            if (found) tt = tt.substring(3);
            else tt = tt.substring(1);
        }
        return res;

    }
}

