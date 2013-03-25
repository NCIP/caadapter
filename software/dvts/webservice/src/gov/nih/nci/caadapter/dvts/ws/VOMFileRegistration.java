/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.ws;



import gov.nih.nci.caadapter.dvts.common.util.vom.ManageVOMFile;
import gov.nih.nci.caadapter.dvts.common.util.Config;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Oct 7, 2011
 * Time: 2:57:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class VOMFileRegistration extends HttpServlet
{

    String filename="";

    String filetype="";
    int ser;

    File f = new File("");
    MultipartRequest multi = null;
    CaadapterWSUtil util = new CaadapterWSUtil();
    GeneralUtilitiesWS gUtil = new GeneralUtilitiesWS();
    Enumeration files = null;
    String fileDataPath = "";



    public void init(ServletConfig config) throws ServletException
    {

        try
        {
            super.init(config);

        }
        catch(Exception e)
        {
            throw new UnavailableException(this, "Can't get connection");
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse response)
                throws ServletException, IOException
    {
        util.setBaseURLToPropertyfile(req);
        response.setContentType("text/html;charset=KSC5601");
        PrintWriter out = response.getWriter();
        out.println(getInitialHTML(req));
    }

    public void doPost(HttpServletRequest req, HttpServletResponse response)
                throws ServletException, IOException
    {
        response.setContentType("text/html;charset=KSC5601");

        PrintWriter out = response.getWriter();
        String ipAddr = req.getRemoteAddr();
        //String userPath = "";
        //String loginID = "unknown";
        System.out.println("\nVOM File Registration at : " + gUtil.getNowDate() + ", From => " + ipAddr);

        System.out.println(req.getHeader("Content-Type"));

        //boolean fileTag = false;
        //boolean fileTag2 = false;
        String res = "";
        String filepath = "";
        File dir = null;

        String user = "unknown";
        String pass = "";

        // create temp working dir
        while(true)
        {
            filepath = util.getWorkDir() + gUtil.getRandomNumber(5);
            dir = new File(filepath);
            if ((dir.exists())&&(dir.isDirectory())) continue;
            if (!dir.mkdirs())
            {
                util.returnMessageAndLogging(out, "Temp Directory creation Failure", util.codeERROR(), "Temp Directory creation Failure : " + filepath, fileDataPath, user, ipAddr, this);
                return;
            }
            break;
        }



        // authorizing login
        try
        {
            multi = new MultipartRequest(req, filepath, 20*1024*1024);

            if (multi == null)
            {
                gUtil.deleteFile(dir);
                util.returnMessage(out, "MultiPartRequest object is Null.", util.codeERROR(), res);
                return;
            }

            user = multi.getParameter("user");
            pass = multi.getParameter("pass");

            if (user == null) user = "";
            else user = user.trim();

            if (pass == null) pass = "";
            else pass = pass.trim();

            if (user.equals(util.getAdministratorID()))
            {
                gUtil.deleteFile(dir);
                util.returnMessage(out, "Administrator's VOM", util.codeERROR(), "Administrator can not have any own VOM.");
                return;
            }

            if (pass.equals(""))
            {
                if (user.startsWith(util.getSessionTag())) user = user.substring(util.getSessionTag().length());
                res = util.checkSession(user, ipAddr, this);
            }
            else
            {
                res = util.simpleLogin(user, pass, ipAddr, this);
            }

            if (res != null)
            {
                gUtil.deleteFile(dir);
                util.returnMessage(out, "Unauthorized Login or Session", util.codeERROR(), res);
                return;
            }
            fileDataPath = util.checkLoginID(user);
            if (fileDataPath == null)
            {
                gUtil.deleteFile(dir);
                util.returnMessageAndLogging(out, "Invalid User ID", util.codeERROR(), "Invalid User ID : " + user, fileDataPath, user, ipAddr, this);
                return;
            }
            if (!fileDataPath.endsWith(File.separator)) fileDataPath = fileDataPath + File.separator;
        }
        catch(Exception ex)
        {
            gUtil.deleteFile(dir);
            util.returnMessageAndLogging(out, "Exception on MultipartRequest", util.codeERROR(), "" + ex + ":"+ex.getMessage(), fileDataPath, user, ipAddr, this);
            ex.printStackTrace();
            return;
        }

        File vomFile = null;

        try
        {
            files = multi.getFileNames();
            while (files.hasMoreElements())
            {
                String name = (String)files.nextElement();
                 //System.out.println("files.hasMoreElements() = " + name );
                if ((name==null)||(name.trim().equals(""))) continue;
//                {
//                    gUtil.deleteFile(dir);
//                    util.returnMessageAndLogging(out, "NULL name File", util.codeERROR(), "Null named file detected.", fileDataPath, user, ipAddr, this);
//                    return;
//                }

                filename = gUtil.from8859(multi.getFilesystemName(name));
                name = name.trim();
                //System.out.println("multi.getFilesystemName(name)= " + filename);
                if (filename == null) filename = "";
                if ((filename.toLowerCase().endsWith("." + Config.VOCABULARY_MAPPING_DIR))||
                    (filename.toLowerCase().endsWith(".xml"))||
                    (filename.toLowerCase().endsWith(".dvm")))
                {
//                    if (vomFile != null)
//                    {
//                        gUtil.deleteFile(dir);
//                        util.returnMessageAndLogging(out, "Duplicate VOM File", util.codeERROR(), "VOM File is duplicated : " + filename, fileDataPath, user, ipAddr, this);
//                        return;
//                    }
                    vomFile = multi.getFile(name);
                }
                else
                {
                    if ((name.equalsIgnoreCase("vomfile"))&&(filename.trim().equals("")))
                    {

                        //gUtil.deleteFile(dir);
                        //util.returnMessageAndLogging(out, "VOM file is absent", util.codeERROR(), "VOM File is not received. : " + filename, fileDataPath, user, ipAddr, this);
                        //return;
                        vomFile = null;
                    }
                    else
                    {
                        gUtil.deleteFile(dir);
                        util.returnMessageAndLogging(out, "Invalid file type", util.codeERROR(), "This file is not a VOM file. : " + filename, fileDataPath, user, ipAddr, this);
                        return;
                    }
                }
                filetype = multi.getContentType(name);

                //System.out.println("Filename = " + filename + ", Filetype = " + filetype);
            }
        }
        catch(Exception ex)
        {
            gUtil.deleteFile(dir);
            util.returnMessageAndLogging(out, "Exception on File receiving (MultipartRequest)", util.codeERROR(), "" + ex + ":"+ex.getMessage(), fileDataPath, user, ipAddr, this);
            return;
        }

//        if (vomFile == null)
//        {
//            gUtil.deleteFile(dir);
//            util.returnMessageAndLogging(out, "No VOM File", util.codeERROR(), "No VOM File is received.", fileDataPath, user, ipAddr, this);
//            return;
//        }

        String vom_Name = multi.getParameter("vom");

        if ((vom_Name==null)||(vom_Name.trim().equals("")))
        {
            vom_Name = "";
        }
        else vom_Name = vom_Name.trim();

        String overwrite = multi.getParameter("overwrite");

        if (overwrite == null) overwrite = "";
        else overwrite = overwrite.trim();

        if (overwrite.equals("")) overwrite = "No";

        if (overwrite.equalsIgnoreCase("delete"))
        {
            if (vom_Name.equals(""))
            {
                gUtil.deleteFile(dir);
                util.returnMessageAndLogging(out, "No VOM File for delete", util.codeERROR(), "No VOM File is received. (deleting)", fileDataPath, user, ipAddr, this);
                if(vomFile != null) vomFile.delete();
                return;
            }
        }
        else
        {
            if (vomFile == null)
            {
                gUtil.deleteFile(dir);
                util.returnMessageAndLogging(out, "No VOM File", util.codeERROR(), "No VOM File is received.", fileDataPath, user, ipAddr, this);
                if(vomFile != null) vomFile.delete();
                return;
            }
        }

//        if ((vomFile == null)&&(vom_Name.equals("")))
//        {
//            gUtil.deleteFile(dir);
//            util.returnMessageAndLogging(out, "No VOM File", util.codeERROR(), "No VOM File is received.", fileDataPath, user, ipAddr, this);
//            if(vomFile != null) vomFile.delete();
//            return;
//        }



        String userPath = fileDataPath;

        File sDir = new File(fileDataPath + Config.VOCABULARY_MAPPING_DIR);

        if ((!sDir.exists())||(!sDir.isDirectory()))
        {
            if (overwrite.equalsIgnoreCase("delete"))
            {
                gUtil.deleteFile(dir);
                util.returnMessageAndLogging(out, "No 'VOM' Directory for deleting", util.codeERROR(), "No 'VOM' Directory for deleting : " + user + File.separator + "vom", userPath, user, ipAddr, this);
                if(vomFile != null) vomFile.delete();
                return;
            }
            if (!sDir.mkdirs())
            {
                gUtil.deleteFile(dir);
                util.returnMessageAndLogging(out, "'VOM' Directory Creation Failure", util.codeERROR(), "VOM Directory Creation Failure : " + user + File.separator + "vom", userPath, user, ipAddr, this);
                if(vomFile != null) vomFile.delete();
                return;
            }

        }

        String valResult = null;
        if (vomFile != null) valResult = ManageVOMFile.validateNewVOMFile(vomFile.getAbsolutePath());
        if (valResult != null)
        {
            gUtil.deleteFile(dir);

            util.returnMessageAndLogging(out, "Invalid VOM File", util.codeERROR(), valResult, userPath, user, ipAddr, this);
            if(vomFile != null) vomFile.delete();
            return;
        }
        String sDirS = sDir.getAbsolutePath();
        if (!sDirS.endsWith(File.separator)) sDirS = sDirS + File.separator;

//        File f2 = new File(sDirS + vomFile.getName());
//        if ((f2.exists())&&(f2.isFile()))
//        {
//            if (overwrite.equalsIgnoreCase("no"))
//            {
//                gUtil.deleteFile(dir);
//
//                util.returnMessageAndLogging(out, "Already exist file : ", util.codeERROR(), "This File is already exist : " + vomFile.getName(), userPath, user, ipAddr, this);
//                if(vomFile != null) vomFile.delete();
//                return;
//            }
//        }

        List<String> duplicateDomains = null;
        if (vomFile != null) duplicateDomains = ManageVOMFile.validateContextForNewVOM(sDir.getAbsolutePath(), vomFile.getAbsolutePath());
        if ((duplicateDomains != null)&&(duplicateDomains.size() > 0))
        {
            gUtil.deleteFile(dir);
            if(vomFile != null) vomFile.delete();
            if ((duplicateDomains.size() == 1))
                util.returnMessageAndLogging(out, "Duplicate Domain(s)", util.codeERROR(), "The domain names '"+duplicateDomains.get(0)+"' in this VOM file is already exist in the context. Delete or change it", userPath, user, ipAddr, this);
            else
                util.returnMessageAndLogging(out, "Duplicate Domains", util.codeERROR(), "Following domain names are already exist in the context. Delete or change them", userPath, user, ipAddr, this, duplicateDomains);
            return;
        }
        try
        {

            File vFile = new File(sDirS + filename);

            if (overwrite.equalsIgnoreCase("delete"))
            {
                //File vFileDesc = new File(vFile.getAbsolutePath() + ".desc");
                File f = new File(sDirS + vom_Name);

                if ((!f.exists())||(!f.isFile()))
                {
                    gUtil.deleteFile(dir);
                    if(vomFile != null) vomFile.delete();
                    util.returnMessageAndLogging(out, "Not found file for delete", util.codeERROR(), "This file is not found. (deleting) : " + vom_Name, userPath, user, ipAddr, this);
                    return;
                }
                if (!f.delete())
                {
                    gUtil.deleteFile(dir);
                    if(vomFile != null) vomFile.delete();
                    util.returnMessageAndLogging(out, "File delete failure", util.codeERROR(), "Deleting this file is failure. : " + vom_Name, userPath, user, ipAddr, this);
                    return;
                }

                ManageVOMFile.refreshContext(sDir.getAbsolutePath());
                gUtil.deleteFile(dir);
                if(vomFile != null) vomFile.delete();
                util.returnMessageAndLogging(out, "VOM File Delete complete", util.codeINFO(), "This VOM file has been deleted. : " + vom_Name, userPath, user, ipAddr, this);
                return;

            }

            if ((vFile.exists())&&(vFile.isFile()))
            {
                if (overwrite.equalsIgnoreCase("yes"))
                {
                    if (!vFile.delete())
                    {
                        gUtil.deleteFile(dir);
                        if(vomFile != null) vomFile.delete();
                        util.returnMessageAndLogging(out, "File delete failure for overwriting", util.codeERROR(), "Deleting this file is failure. (overwriting) : " + vFile.getName(), userPath, user, ipAddr, this);
                        return;
                    }
                }
                else
                {
                    if(vomFile != null) vomFile.delete();
                    gUtil.deleteFile(dir);
                    util.returnMessageAndLogging(out, "Already exist VOM file", util.codeERROR(), "This VOM file name is already exist. : " + user + File.separator + vFile.getName(), userPath, user, ipAddr, this);
                    return;
                }
            }
            fileDataPath = sDirS;

            if (overwrite.equalsIgnoreCase("delete"))
            {
                gUtil.deleteFile(dir);
                util.returnMessageAndLogging(out, "Not found file for deleting : ", util.codeERROR(), "Not found this file for deleting : " + vomFile.getName(), userPath, user, ipAddr, this);
                return;
            }

            if (!vomFile.renameTo(new File(fileDataPath + vomFile.getName())))
            {
                gUtil.deleteFile(dir);
                util.returnMessageAndLogging(out, "VOM File Copy Failure", util.codeERROR(), "VOM File Copy Failure : " + vomFile.getName(), userPath, user, ipAddr, this);
                return;
            }


            gUtil.deleteFile(dir);

//            if (!gUtil.saveStringIntoFile(fileDataPath, filename + ".desc", vom_Name + "\r\n" + multi.getParameter("comment")))
//            {
//                util.returnMessageAndLogging(out, "VOM description File Writing Failure", util.codeERROR(), "VOM description File Writing Failure : " + user + File.separator + "vom" + File.separator + filename, userPath, user, ipAddr, this);
//                return;
//            }
            util.returnMessageAndLogging(out, "VOM File Registration Complete!", util.codeINFO(), "This VOM file is successfully registered. : " + user + File.separator + "vom" + File.separator + filename, userPath, user, ipAddr, this);
            ManageVOMFile.refreshContext(sDir.getAbsolutePath());
        }
        catch(Exception e)
        {
            util.returnMessageAndLogging(out, "General Error...", util.codeERROR(), "" + e + ":" +e.getMessage(), userPath, user, ipAddr, this);

            //util.returnMessage(out, "General Error...", util.codeERROR(), e.getMessage());
        }
    }
    private String getInitialHTML(HttpServletRequest req)
    {
        return getInitialHTML(req, true, null);
    }
    public String getInitialHTMLFromOutSide(HttpServletRequest req, String user)
    {
        return getInitialHTML(req, false, user);
    }
    private String getInitialHTML(HttpServletRequest req, boolean isInside, String user)
    {
        String program = this.getClass().getName();
        if ((user == null)||(user.trim().equals(""))) user = "";
        else user = user.trim();

        while(true)
        {
            int idx = program.indexOf(".");
            if (idx < 0) break;
            program = program.substring(idx+1);
        }
        String url = util.getBaseURLFromRequest(req);
        if (url == null) url = util.getBaseURL() + "servlet/" + program;
        else url = url + "/" + program;

        int seq = 0;
        String h = "";
        h = "<html>\n" +
            "<head>\n" +
            "<title>VOM File Registration</title>\n" +
            "</head>\n" +
            "<body bgcolor='lightblue'>\n" +
            "<font color='green'>\n" +
            "<center>\n" +
            "<h1>\n" +
            "caAdapter DVTS Web Service VOM File Registration\n" +
            "</h1>\n" +
            "\n" +
            "</font>\n" +
            "\n" +
            "<br>\n" +
            "\n" +
            //"** 'DVM' (Domain Value Mapping) and 'VOM' (VOcabulary Mapping) are the same meaning<br>\n" +
            "<form name='VOM' method='post' action='"+url+"' ENCTYPE='multipart/form-data'>\n" +
            "\n" +
            "<table border=2 cellpading=10>\n";
        if (isInside) h = h +
            "<tr><td>\n" +
            "<h3><font color=blue>"+ (++seq) +". Context Identification</font></h3>\n" +
            "</td><td>" +
            "  <table borde=0>\n" +
            "     <tr>\n" +
            "        <td align='right'>Context Name</td>\n" +
            "        <td align='left'>&nbsp;&nbsp;<input type='text' name='user' value=''> &nbsp;<br></td>\n" +
            "     </tr>\n" +
            "     <tr>\n" +
            "         <td align='right'>PASSWORD</td>" +
            "         <td align='left'>&nbsp;&nbsp;<input type='password' name='pass' value=''> &nbsp;</td>\n" +
            "     </tr>" +
            "   </table>" +
            "  \n" +
            "</td></tr>\n";
        else h = h + "          <input type='hidden' name='user' value='"+util.getSessionTag() + user+"'>\n";
        h = h +

            "<tr><td>\n" +
            "<h3><font color=blue>"+ (++seq) +". Select one job</font>\n" +
            "</td><td>" +
            "  <input type=radio name='overwrite' value='Yes'>Overwrite VOM File&nbsp;&nbsp;&nbsp;<br>\n" +
            "  <input type=radio name='overwrite' value='No' checked>New VOM File Registration&nbsp;&nbsp;&nbsp;<br>\n" +
            "  <input type=radio name='overwrite' value='Delete'>Delete VOM File&nbsp;&nbsp;&nbsp;<br>\n" +
            //"  <input type=radio name='overwrite' value='Backup'>Backup&nbsp;&nbsp;&nbsp;</h3>\n" +
            "</td></tr>\n" +
            "<tr><td>\n" +
            "<h3><font color=blue>"+ (++seq) +". If you select 'Delete a VOM file', input the file name. </font>\n" + //<font color=red>*</font>\n" +
            "</td><td>\n" +
            " <input type=text name='vom' value=''> &nbsp;&nbsp;&nbsp;</h3>\n" +
            "  \n" +
            "</td></tr>\n" +
            "<tr><td>\n" +
            "<h3><font color=blue>"+ (++seq) +". If you select 'Overwrite' or 'New' VOM File, input the file.</h3><h5>Any duplicate domain name is not allowed in the same context.</h5></font>\n" +
            "</td><td>\n" +
            "  <input type=\"file\" name=\"vomfile\" size=\"30\" style=\"border-top-width:1; style=\"border-right-width:1; \n" +
            "         style=\"border-bottom-width:1; style=\"border-left-width:1px; border-style:dashed;\">\n" +
            "  \n" +
            "</td></tr>\n" +

            //"<tr><td>\n" +
            //"<h3><font color=blue>"+ (++seq) +". Comment</font></h3>\n" +
            //"\n" +
            //"<textarea name='comment' rows=3 cols=50>\n" +
            //"\n" +
            //"</textarea>\n" +
            //"</td></tr>\n" +
            "\n" +
            "\n" +
            "<tr><td colspan=2>\n" +
            "<input type='submit' value='Submit'>&nbsp;&nbsp;&nbsp;\n" +
            "<input type='reset' value='Cancel'></td>\n" +
            "</tr>\n" +
            "</table>\n" +
            "<br>\n" +
            "</h2>\n" +
            "</center>\n" +
            "</form>\n" +
            "</body>\n" +
            "</html>";
        return h;
    }

    public void distory()
      {

        }
}



