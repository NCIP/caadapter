/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ws;

import gov.nih.nci.caadapter.security.dao.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.jdom.JDOMException;
import org.jdom.input.DOMBuilder;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Add Web Service Mapping Secnario class
 *
 * @author OWNER: Sandeep Phadke
 * @author LAST UPDATE $Author: phadkes $
 * @version $Revision: 1.1 $
 * @date $$Date: 2008-11-11 17:33:11 $
 * @since caadapter v1.3.1
 */


public class validateUser extends HttpServlet {
        String userId;
        String password;
        HttpSession session = null;

           /** **********************************************************
            *  doPost()
            ************************************************************ */
        public void doPost (HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
     {

        try
        {
      //      System.out.println(req.getParameterNames());

            session = req.getSession(false);
            if (session!=null){
                session.invalidate();
            }
            userId = req.getParameter("userid");
            password = req.getParameter("password");
            System.out.println("userid: " + userId + ", password: " + password);
            System.out.println("userid: " + userId + ", password: " + password);
            AbstractSecurityDAO abstractDao= DAOFactory.getDAO();
            SecurityAccessIF getSecurityAccess = abstractDao.getSecurityAccess();
            boolean valid = getSecurityAccess.validateUser(userId, password);
            System.out.println(valid);
            // Parse the request
            session = req.getSession(true);
            String rsltMsg="Test message";
            if (valid){

                if(session.isNew())
                {
                    session.setAttribute("userid", userId);
                    session.setAttribute("password",password);
                }
                System.out.println("/caAdapterWS/createScenario.do");
                res.sendRedirect("/caAdapterWS/createScenario.do");
                return;
            }
            else {
//             RequestDispatcher dispatch = req.getRequestDispatcher("/caAdapterWS/successmsg.do");
//             dispatch.forward(req, res);

//          out.println("Complete!");
                if (session!=null){
                    session.invalidate();
                }
            res.sendRedirect("/caAdapterWS/errormsg.do");
            return;
          }
      }catch(Exception e) {
              System.out.println("Error in doPost: " + e);
                res.sendRedirect("/caAdapterWS/error.do");
          }
     }


}
