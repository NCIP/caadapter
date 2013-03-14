/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.ws;

import edu.knu.medinfo.hl7.v2tree.ByteTransform;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Oct 11, 2011
 * Time: 12:29:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileDownloader extends HttpServlet
{

    public void doGet(HttpServletRequest req, HttpServletResponse res)
                throws ServletException, IOException
    {
        CaadapterWSUtil util = new CaadapterWSUtil();
        GeneralUtilitiesWS gUtil = new GeneralUtilitiesWS();

        //String fileDataPath = "";

        util.setBaseURLToPropertyfile(req);
        res.setContentType("text/html;charset=KSC5601");
        //PrintWriter out = res.getWriter();

        /*
        String exsampleTag = req.getParameter("example");
        if (exsampleTag == null) exsampleTag = "";
        else exsampleTag = exsampleTag.trim();

        if ((exsampleTag.toLowerCase().equals("yes"))||
            (exsampleTag.toLowerCase().equals("true")))
        {
            doExampleFileDownLoading(req, res);
            return;
        }
        */

        String adminID = req.getParameter("adminID");
        if (adminID == null) adminID = "";
        else adminID = adminID.trim();
        String userPath = "";
        String ipAddr = req.getRemoteAddr();

        if (!adminID.equals(util.getSessionTag()))
        {
            util.returnMessageAndLogging(res.getWriter(), "File Downloading without Admin Session", util.codeFATAL(), "File Downloading must use an Admin Session", userPath, adminID, ipAddr, this);
            return;
        }

        userPath = util.getRootScenarioPath() + util.getAdministratorID();
        String resLogin = util.checkSession(util.getAdministratorID(), ipAddr, this);
        if (resLogin != null)
        {
            util.returnMessageAndLogging(res.getWriter(), "Unauthorized Admin Session", util.codeFATAL(), resLogin, userPath, adminID, ipAddr, this);
            return;
        }

        String path = req.getParameter("path");
        if ((path==null)||((path.trim().equals(""))))
        {
            util.returnMessageAndLogging(res.getWriter(), "Null path downloading", util.codeERROR(), "Downloading dir path is null.", userPath, adminID, ipAddr, this);
            return;
        }

        ByteTransform bt = new ByteTransform();
        String pathdecoded = bt.decodeHexString(path.trim());
        if (!pathdecoded.endsWith(File.separator)) pathdecoded = pathdecoded + File.separator;

        String file = req.getParameter("file");
        if ((file==null)||((file.trim().equals(""))))
        {
            util.returnMessageAndLogging(res.getWriter(), "Null file name downloading", util.codeERROR(), "Downloading file name is null.", userPath, adminID, ipAddr, this);
            return;
        }

        File fileO = new File(pathdecoded + file.trim());
        if ((!fileO.exists())||(!fileO.isFile()))
        {
            util.returnMessageAndLogging(res.getWriter(), "Not exist file downloading", util.codeERROR(), "Downloading file name is not exist. : " + pathdecoded + file.trim(), userPath, adminID, ipAddr, this);
            return;
        }


        ServletContext sc = getServletContext();
        //String filename = sc.getRealPath("image.gif");

            // Get the MIME type of the image
            String mimeType = sc.getMimeType(fileO.getAbsolutePath());
            if (mimeType == null) {

                util.returnMessageAndLogging(res.getWriter(), "Finding MIME type failure", util.codeERROR(), "Could not get MIME type of "+fileO.getAbsolutePath(), userPath, adminID, ipAddr, this);
                return;
            }

            // Set content type
            res.setContentType(mimeType);

            // Set content size

            res.setContentLength((int)fileO.length());

            // Open the file and output streams
            FileInputStream in = new FileInputStream(fileO);
            OutputStream outStream = res.getOutputStream();

            // Copy the contents of the file to the output stream
            byte[] buf = new byte[1024];
            int count = 0;
            while ((count = in.read(buf)) >= 0) {
                outStream.write(buf, 0, count);
            }
            in.close();
            outStream.close();


        /*
        FileInputStream fis = new FileInputStream(fileO);
        DataInputStream dis = new DataInputStream(fis);
        while(true)
        {
             int b = -1;

            try
            {
                b = dis.read();
                //b = fis.read();
            }
            catch(Exception ee)
            {
                break;
            }

            if (b < 0) break;
            out.print(b);
        }
        */
        System.out.println("File downloading finish (mimeType="+mimeType+") : " + fileO.getAbsolutePath());
    }
    /*
    private void doExampleFileDownLoading(HttpServletRequest req, HttpServletResponse res)
                throws ServletException, IOException
    {
        CaadapterWSUtil util = new CaadapterWSUtil();


        util.setBaseURLToPropertyfile(req);
        res.setContentType("text/html;charset=KSC5601");
        //PrintWriter out = res.getWriter();

        String fileName = req.getParameter("file");
        if (fileName == null) fileName = "";
        else fileName = fileName.trim();

        if (fileName.equals("")) return;

        if (fileName.toLowerCase().endsWith(".jar")) fileName = "caAdapter-dvts.jar";
        else ()
    }
    */
}
