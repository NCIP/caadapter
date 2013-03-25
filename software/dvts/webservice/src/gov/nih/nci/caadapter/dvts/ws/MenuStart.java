/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.ws;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Jun 24, 2009
 * Time: 10:56:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class MenuStart extends HttpServlet
{

	boolean isDoGet = false;
	CaadapterWSUtil util = new CaadapterWSUtil();
	GeneralUtilitiesWS gUtil = new GeneralUtilitiesWS();

    /* ***********************************************************
	 *  doGet()
	 ************************************************************ */
	public void doGet (HttpServletRequest req, HttpServletResponse response)
		throws ServletException, IOException
	{
        util.setBaseURLToPropertyfile(req);
        response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String connectTime = gUtil.getNowDate();
		String ipAddr = req.getRemoteAddr();
        String url = util.getBaseURLFromRequest(req);

        String check = "";
//        check = req.getParameter("ck");
//        if (check == null) check = "";
//        else check = check.trim();
        
        String rl = "\r\n";
        String title = "caAdapter Web Service Work Menu";
        String h;
        h = "<html>" + rl
          + "<head><title>"+title+"</title></head>" + rl
          + "<body>"
          + "    <center>" + rl
          + "      <br><br><br>" + rl
          + "      <h1><font color='brown'>" + title + "<br></font></h1>" + rl
          + "      <h4><font color='brown'>Requested From "+ipAddr+" at "+gUtil.changeFormatedDate(connectTime)+"</font></h4>" + rl
          + "      <font color='green'><br><br><br>" + rl
          + "      <table border=1 bordercolor=\"blue\">" + rl
          + "        <tr>" + rl
          + "          <td align=\"left\" width=\"30%\" bgcolor=\"CBF5FF\">Administrator Works</td>" + rl
          + "          <td width=\"70%\">" + rl
          + "             <a href=\"" + url + "/ManageCaadapterWSUser\">User Management</a>" + rl;
        if (!check.equals("")) h = h + "<br>"
          + "             <a href=\"" + url + "/DosFileHandler\">File Management</a><br>" + rl
          + "             <a href=\"" + url + "/FileUploaderWS\">File Uploading</a>" + rl;
        h = h 
          + "          </td>" + rl
          + "        </tr>" + rl
          + "        <tr>" + rl
          + "          <td align=\"left\" width=\"30%\" bgcolor=\"CBF5FF\">User Works</td>" + rl
          + "          <td width=\"70%\">" + rl
          + "             <a href=\"" + url + "/CaAdapterUserWorks\">User Works</a><br>" + rl
          + "          </td>" + rl
          + "        </tr>" + rl      
          + "      </table>" + rl
          + "      </font></center>\n" + rl
          + "    </center>" + rl
          + "</body>" + rl
          + "</html>";
        out.println(h);
    }
}
