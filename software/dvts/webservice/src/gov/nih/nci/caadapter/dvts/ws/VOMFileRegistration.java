package gov.nih.nci.caadapter.dvts.ws;

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
    //????? ???? ?? ?????.
    String filename="";
    //??? ??? ???? ?????.
    String filetype="";
    int ser;
    //???? ????? ?? ?????.
    File f = new File("");
    MultipartRequest multi = null;
    CaadapterWSUtil util = new CaadapterWSUtil();
    GeneralUtilitiesWS gUtil = new GeneralUtilitiesWS();
    Enumeration files = null;
    String fileDataPath = "";
    //String fileDataPath = FileUtil.getInnerInstanceTag();

       // init ?? ??? ???? ???
    public void init(ServletConfig config) throws ServletException
    {

        try
        {
            super.init(config);
            // System.out.println("initial<br>");
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
                util.returnMessage(out, "Administrator no VOM", util.codeERROR(), "Administrator can not have any own VOM.");
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
                if (filename.toLowerCase().endsWith(".vom"))
                {
                    if (vomFile != null)
                    {
                        gUtil.deleteFile(dir);
                        util.returnMessageAndLogging(out, "Duplicate VOM File", util.codeERROR(), "VOM File is duplicated : " + filename, fileDataPath, user, ipAddr, this);
                        return;
                    }
                    vomFile = multi.getFile(name);
                }
                else
                {
                    if ((name.equalsIgnoreCase("vomfile"))&&(filename.trim().equals("")))
                    {

                        gUtil.deleteFile(dir);
                        util.returnMessageAndLogging(out, "VOM file is absent", util.codeERROR(), "VOM File is not received. : " + filename, fileDataPath, user, ipAddr, this);
                        return;

                    }
                    else
                    {
                        gUtil.deleteFile(dir);
                        util.returnMessageAndLogging(out, "Invalid file type", util.codeERROR(), "This file is not a vom file. : " + filename, fileDataPath, user, ipAddr, this);
                        return;
                    }
                }
                filetype = multi.getContentType(name);

                System.out.println("Filename = " + filename + ", Filetype = " + filetype);
            }
        }
        catch(Exception ex)
        {
            gUtil.deleteFile(dir);
            util.returnMessageAndLogging(out, "Exception on File receiving (MultipartRequest)", util.codeERROR(), "" + ex + ":"+ex.getMessage(), fileDataPath, user, ipAddr, this);
            return;
        }

        if (vomFile == null)
        {
            gUtil.deleteFile(dir);
            util.returnMessageAndLogging(out, "No VOM File", util.codeERROR(), "No VOM File is received.", fileDataPath, user, ipAddr, this);
            return;
        }

        String vom_Name = "";
        String overwrite = "";

        String userPath = fileDataPath;
        try
        {
            vom_Name = multi.getParameter("vom");
            overwrite = multi.getParameter("overwrite");

            if (overwrite == null) overwrite = "no";
            overwrite = overwrite.trim();
            if ((vom_Name==null)||(vom_Name.trim().equals("")))
            {
                gUtil.deleteFile(dir);
                util.returnMessageAndLogging(out, "Null VOM name", util.codeERROR(), "VOM Name is null or empty.", userPath, user, ipAddr, this);
                return;
            }
            vom_Name = vom_Name.trim();
            File sDir = new File(fileDataPath + "vom");

            if ((!sDir.exists())||(!sDir.isDirectory()))
            {
                if (!sDir.mkdirs())
                {
                    gUtil.deleteFile(dir);
                    util.returnMessageAndLogging(out, "'vom' Directory Creation Failure", util.codeERROR(), "Scenario Directory Creation Failure : " + user + File.separator + "vom", userPath, user, ipAddr, this);
                    return;
                }
            }

            String sDirS = sDir.getAbsolutePath();
            if (!sDirS.endsWith(File.separator)) sDirS = sDirS + File.separator;
            File vFile = new File(sDirS + filename);

            if ((vFile.exists())&&(vFile.isFile()))
            {
                if (overwrite.equalsIgnoreCase("yes"))
                {
                    File vFileDesc = new File(vFile.getAbsolutePath() + ".desc");
                    vFile.delete();
                    if ((vFileDesc.exists())&&(vFileDesc.isFile())) vFileDesc.delete();
                }
                else if (overwrite.equalsIgnoreCase("delete"))
                {
                    File vFileDesc = new File(vFile.getAbsolutePath() + ".desc");
                    String fileN = vFile.getName();
                    vFile.delete();
                    if ((vFileDesc.exists())&&(vFileDesc.isFile())) vFileDesc.delete();
                    gUtil.deleteFile(dir);
                    util.returnMessageAndLogging(out, "Vom File Delete complete", util.codeINFO(), "This VOM fime has been deleted. : " + fileN, userPath, user, ipAddr, this);
                    return;

                }
                else
                {
                    gUtil.deleteFile(dir);
                    util.returnMessageAndLogging(out, "Already exist VOM file", util.codeERROR(), "This VOM fime name is already exist. : " + user + File.separator + vFile.getName(), userPath, user, ipAddr, this);
                    return;
                }
            }
            fileDataPath = sDirS;

            if (overwrite.equalsIgnoreCase("delete"))
            {
                gUtil.deleteFile(dir);
                util.returnMessageAndLogging(out, "Not found file for deleting", util.codeERROR(), "Not found this file for deleting : " + vomFile.getName(), userPath, user, ipAddr, this);
                return;
            }

            if (!vomFile.renameTo(new File(fileDataPath + vomFile.getName())))
            {
                gUtil.deleteFile(dir);
                util.returnMessageAndLogging(out, "VOM File Copy Failure", util.codeERROR(), "VOM File Copy Failure : " + vomFile.getName(), userPath, user, ipAddr, this);
                return;
            }


            gUtil.deleteFile(dir);

            if (!gUtil.saveStringIntoFile(fileDataPath, filename + ".desc", vom_Name + "\r\n" + multi.getParameter("comment")))
            {
                util.returnMessageAndLogging(out, "VOM description File Writing Failure", util.codeERROR(), "VOM description File Writing Failure : " + user + File.separator + "vom" + File.separator + filename, userPath, user, ipAddr, this);
                return;
            }
            util.returnMessageAndLogging(out, "VOM File Registration Complete!", util.codeINFO(), "This VOM file is successfully registered. : " + user + File.separator + "vom" + File.separator + filename, userPath, user, ipAddr, this);

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
            "<body bgcolor='pink'>\n" +
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
            "Each mark with \"*\" in red color means a essential item<br>\n" +
            "<form name='VOM' method='post' action='"+url+"' ENCTYPE='multipart/form-data'>\n" +
            "\n" +
            "<table border=2 cellpading=10>\n";
        if (isInside) h = h +
            "<tr><td>\n" +
            "<h3><font color=blue>"+ (++seq) +". Context Identification</font><font color=red>*</font></h3>\n" +
            "\n" +
            "  Context Name :<input type=text name='user' value=''> &nbsp;&nbsp;&nbsp;<br>\n" +
            "  PASSWORD:<input type=password name='pass' value=''> &nbsp;&nbsp;&nbsp;\n" +
            "  \n" +
            "</td></tr>\n";
        else h = h + "          <input type='hidden' name='user' value='"+util.getSessionTag() + user+"'>\n";
        h = h +
            "<tr><td>\n" +
            "<h3><font color=blue>"+ (++seq) +". Name of Vocabulary Mapping</font><font color=red>*</font>\n" +
            "\n" +
            " <input type=text name='vom' value=''> &nbsp;&nbsp;&nbsp;</h3>\n" +
            "  \n" +
            "</td></tr>\n" +
            "<tr><td>\n" +
            "<h3><font color=blue>"+ (++seq) +". What do You Want for this file?</font>\n" +
            "  <input type=radio name='overwrite' value='Yes'>Overwrite&nbsp;&nbsp;&nbsp;\n" +
            "  <input type=radio name='overwrite' value='No' checked>New File Registratio&nbsp;&nbsp;&nbsp;\n" +
            "  <input type=radio name='overwrite' value='Delete' checked>Delete&nbsp;&nbsp;&nbsp;\n" +
            //"  <input type=radio name='overwrite' value='Backup'>Backup&nbsp;&nbsp;&nbsp;</h3>\n" +
            "</td></tr>\n" +

            "<tr><td>\n" +
            "<h3><font color=blue>"+ (++seq) +". VOM File</font><font color=red>*</font>\n" +
            "\n" +
            "  <input type=\"file\" name=\"vomfile\" size=\"30\" style=\"border-top-width:1; style=\"border-right-width:1; \n" +
            "         style=\"border-bottom-width:1; style=\"border-left-width:1px; border-style:dashed;\"> </h3>\n" +
            "  \n" +
            "</td></tr>\n" +

            "<tr><td>\n" +
            "<h3><font color=blue>"+ (++seq) +". Comment</font></h3>\n" +
            "\n" +
            "<textarea name='comment' rows=3 cols=50>\n" +
            "\n" +
            "</textarea>\n" +
            "</td></tr>\n" +
            "\n" +
            "\n" +
            "<tr><td>\n" +
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



