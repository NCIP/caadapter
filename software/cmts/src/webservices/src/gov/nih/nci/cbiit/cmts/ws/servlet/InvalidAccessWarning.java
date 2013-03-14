/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.ws.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Feb 27, 2012
 * Time: 12:25:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class InvalidAccessWarning extends HttpServlet
{
    public void doPost (HttpServletRequest req, HttpServletResponse res)
          throws ServletException, IOException
    {
        String errMsg="Invalid Access Warning : You are trying to Invalid Access.";
        System.out.println("AddNewScenario.doPost()...InvalidAccessWarning:"+errMsg);
        req.setAttribute("rtnMessage", errMsg);
        res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
    }

    public void doGet (HttpServletRequest req, HttpServletResponse res)
          throws ServletException, IOException
    {
        doPost(req, res);
    }
}
