package gov.nih.nci.caadapter.dvts.ws;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;   // date

/**
 * Created by IntelliJ IDEA.
 * User: kium
 * Date: Jun 16, 2009
 * Time: 12:39:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScenarioFileRegistration extends HttpServlet
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
		System.out.println("\nScenario Registration at : " + gUtil.getNowDate() + ", From => " + ipAddr);

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
                util.returnMessage(out, "Administrator no scenario", util.codeERROR(), "Administrator can not have any own scenario.");
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
			if (!fileDataPath.endsWith(File.separator)) fileDataPath = fileDataPath + File.separator;
		}
		catch(Exception ex)
		{
			gUtil.deleteFile(dir);
			util.returnMessageAndLogging(out, "Exception on MultipartRequest", util.codeERROR(), "" + ex + ":"+ex.getMessage(), fileDataPath, user, ipAddr, this);
            ex.printStackTrace();
            return;
		}
		boolean isV2V3 = false;
		String paramV2V3 = multi.getParameter("v2v3");
		if (paramV2V3 == null) paramV2V3 = "";
		if (paramV2V3.trim().equalsIgnoreCase("true")) isV2V3 = true;

		String isXSDVal = "false";
		String paramXsdValid = multi.getParameter("xsdvalidation");
		if (paramXsdValid == null) paramXsdValid = "";
		if (paramXsdValid.trim().equalsIgnoreCase("true")) isXSDVal = "true";

		String isDatatypeIncluded = "false";
		String datatypeIncluded = multi.getParameter("datatypeIncluded");
		if (datatypeIncluded == null) datatypeIncluded = "";
		if (datatypeIncluded.trim().equalsIgnoreCase("true")) isDatatypeIncluded = "true";

		File scsFile = null;
		File h3sFile = null;
		File mapFile = null;
		File xsdFile = null;
		List<File> vomFiles = null;

		try
		{
			files = multi.getFileNames();
	        while (files.hasMoreElements())
			{
                String name = (String)files.nextElement();
 	            //System.out.println("files.hasMoreElements() = " + name );
			    if ((name==null)||(name.trim().equals("")))
				{
					gUtil.deleteFile(dir);
					util.returnMessageAndLogging(out, "NULL name File", util.codeERROR(), "Null named file detected.", fileDataPath, user, ipAddr, this);
					return;
				}

                filename = gUtil.from8859(multi.getFilesystemName(name));
				name = name.trim();
				//System.out.println("multi.getFilesystemName(name)= " + filename);
				if (filename == null) filename = "";
				if (filename.toLowerCase().endsWith(".scs"))
				{
					if (scsFile != null)
					{
						gUtil.deleteFile(dir);
						util.returnMessageAndLogging(out, "Duplicate SCS File", util.codeERROR(), "SCS File is duplicated : " + filename, fileDataPath, user, ipAddr, this);
						return;
					}
					scsFile = multi.getFile(name);
				}
				else if (filename.toLowerCase().endsWith(".h3s"))
				{
					if (h3sFile != null)
					{
						gUtil.deleteFile(dir);
						util.returnMessageAndLogging(out, "Duplicate H3S File", util.codeERROR(), "H3S File is duplicated : " + filename, fileDataPath, user, ipAddr, this);
						return;
					}
					h3sFile = multi.getFile(name);
				}
				else if (filename.toLowerCase().endsWith(".map"))
				{
					if (mapFile != null)
					{
						gUtil.deleteFile(dir);
						util.returnMessageAndLogging(out, "Duplicate MAP File", util.codeERROR(), "MAP File is duplicated : " + filename, fileDataPath, user, ipAddr, this);
						return;
					}
					mapFile = multi.getFile(name);
				}
				else if (filename.toLowerCase().endsWith(".xsd"))
				{
					if (xsdFile != null)
					{
						gUtil.deleteFile(dir);
						util.returnMessageAndLogging(out, "Duplicate schema File", util.codeERROR(), "Schema File is duplicated : " + filename, fileDataPath, user, ipAddr, this);
						return;
					}
					xsdFile = multi.getFile(name);
				}
				else if (filename.toLowerCase().endsWith(".vom"))
				{
					if (vomFiles == null) vomFiles = new ArrayList<File>();
					File vomFile = multi.getFile(name);
					vomFiles.add(vomFile);
				}
				else
				{
					if ((name.equalsIgnoreCase("xsdfile"))&&(filename.trim().equals(""))) {}
					else if ((name.toLowerCase().startsWith("vomfile"))&&(filename.trim().equals(""))) {}
					else if ((name.equalsIgnoreCase("scsfile"))&&(filename.trim().equals("")))
					{
					    if (!isV2V3)
						{
							gUtil.deleteFile(dir);
							util.returnMessageAndLogging(out, "SCS file is absent", util.codeERROR(), "SCS File is needed as a component of scenario. : " + filename, fileDataPath, user, ipAddr, this);
							return;
						}
					}
					else
					{
						gUtil.deleteFile(dir);
						util.returnMessageAndLogging(out, "Invalid file type", util.codeERROR(), "This file cannot be a component of scenario. : " + filename, fileDataPath, user, ipAddr, this);
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

		if (scsFile == null)
		{
		    if (!isV2V3)
			{
				gUtil.deleteFile(dir);
				util.returnMessageAndLogging(out, "No SCS File", util.codeERROR(), "SCS File is needed as a component of scenario.", fileDataPath, user, ipAddr, this);
				return;
			}
		}
		if (mapFile == null)
		{
			gUtil.deleteFile(dir);
			util.returnMessageAndLogging(out, "No MAP File", util.codeERROR(), "MAP File is needed as a component of scenario.", fileDataPath, user, ipAddr, this);
			return;
		}
		if (h3sFile == null)
		{
			gUtil.deleteFile(dir);
			util.returnMessageAndLogging(out, "No H3S File", util.codeERROR(), "H3S File is needed as a component of scenario.", fileDataPath, user, ipAddr, this);
			return;
		}

		String scenario = "";
		String overwrite = "";

		String userPath = fileDataPath;
		try
		{
			scenario = multi.getParameter("scenario");
			overwrite = multi.getParameter("overwrite");

			if (overwrite == null) overwrite = "no";
			overwrite = overwrite.trim();
			if ((scenario==null)||(scenario.trim().equals("")))
			{
				gUtil.deleteFile(dir);
				util.returnMessageAndLogging(out, "Null Scenario ID", util.codeERROR(), "Scenario ID is null or empty.", userPath, user, ipAddr, this);
				return;
			}
			scenario = scenario.trim();
			File sDir = new File(fileDataPath + scenario);

			if ((!sDir.exists())||(!sDir.isDirectory()))
			{
				if (!sDir.mkdirs())
				{
					gUtil.deleteFile(dir);
					util.returnMessageAndLogging(out, "Scenario Directory Creation Failure", util.codeERROR(), "Scenario Directory Creation Failure : " + user + File.separator + scenario, userPath, user, ipAddr, this);
					return;
				}
			}
			else
			{
				if (overwrite.equalsIgnoreCase("yes"))
				{
					File[] files = sDir.listFiles();
					for (File fileF:files)
					{
						if (fileF.isFile()) fileF.delete();
					}
				}
				else if (overwrite.equalsIgnoreCase("backup"))
				{
					String backupDir = fileDataPath + scenario + File.separator + "BACKUP_" + gUtil.getNowDate();
					File dirB = new File(backupDir);
					dirB.mkdirs();
					File[] files = sDir.listFiles();
					for (File fileF:files)
					{
						if (!fileF.isFile()) continue;
						String fName = fileF.getName();
						fileF.renameTo(new File(backupDir + File.separator + fName));
					}
				}
				else
				{
					gUtil.deleteFile(dir);
					util.returnMessageAndLogging(out, "Duplicated Scenario", util.codeERROR(), "This Scenario name is already exist. : " + user + File.separator + scenario, userPath, user, ipAddr, this);
					return;
				}
			}
			fileDataPath = sDir.getAbsolutePath();
			if (!fileDataPath.endsWith(File.separator)) fileDataPath = fileDataPath + File.separator;

			if (!scsFile.renameTo(new File(fileDataPath + scsFile.getName())))
			{
				gUtil.deleteFile(dir);
				util.returnMessageAndLogging(out, "SCS File Copy Failure", util.codeERROR(), "SCS File Copy Failure : " + scsFile.getName(), userPath, user, ipAddr, this);
				return;
			}
			if (!h3sFile.renameTo(new File(fileDataPath + h3sFile.getName())))
			{
				gUtil.deleteFile(dir);
				util.returnMessageAndLogging(out, "H3S File Copy Failure", util.codeERROR(), "H3S File Copy Failure : " + h3sFile.getName(), userPath, user, ipAddr, this);
				return;
			}
			if (!mapFile.renameTo(new File(fileDataPath + mapFile.getName())))
			{
				gUtil.deleteFile(dir);
				util.returnMessageAndLogging(out, "MAP File Copy Failure", util.codeERROR(), "MAP File Copy Failure : " + mapFile.getName(), userPath, user, ipAddr, this);
				return;
			}
			String schemaF = "";
			if (xsdFile != null)
			{
				if (!xsdFile.renameTo(new File(fileDataPath + xsdFile.getName())))
				{
					gUtil.deleteFile(dir);
					util.returnMessageAndLogging(out, "Schema File Copy Failure", util.codeERROR(), "Schema File Copy Failure : " + xsdFile.getName(), userPath, user, ipAddr, this);
					return;
				}
				schemaF = xsdFile.getName();
			}
			String[] vomFileNames = null;
			if ((vomFiles != null)&&(vomFiles.size() > 0))
			{
				vomFileNames = new String[vomFiles.size()];
				for (int i=0;i<vomFiles.size();i++)
				{
					File vomFile = vomFiles.get(i);
					if (!vomFile.renameTo(new File(fileDataPath + vomFile.getName())))
					{
						gUtil.deleteFile(dir);
						util.returnMessageAndLogging(out, "VOM File Copy Failure", util.codeERROR(), "VOM File Copy Failure : " + vomFile.getName(), userPath, user, ipAddr, this);
						return;
					}
					vomFileNames[i] = vomFile.getName();
				}
			}

			gUtil.deleteFile(dir);

			String scsName = "";
			if (scsFile != null) scsName = scsFile.getName();
			else scsName = util.getTagV2V3();

			if (!util.saveProperties(fileDataPath, scsName, h3sFile.getName(), mapFile.getName(), schemaF, multi.getParameter("comment"), gUtil.getNowDate(), isXSDVal, vomFileNames))
			{
				util.returnMessageAndLogging(out, "Property File Writing Failure", util.codeERROR(), "Property File Writing Failure : " + user + File.separator + scenario, userPath, user, ipAddr, this);
				return;
			}
			util.returnMessageAndLogging(out, "Scenario Registration Complete!", util.codeINFO(), "This Scenario is successfully registered. : " + user + File.separator + scenario, userPath, user, ipAddr, this);

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
            "<title>caAdapter V3 Scenario Registration</title>\n" +
            "</head>\n" +
            "<body bgcolor='pink'>\n" +
            "<font color='green'>\n" +
            "<center>\n" +
            "<h1>\n" +
            "caAdapter Web Service V3 Scenario Registration\n" +
            "</h1>\n" +
            "\n" +
            "</font>\n" +
            "\n" +
            "<br>\n" +
            "\n" +
            "Each mark with \"*\" in red color means a essential item<br>\n" +
            "<form name='scenario' method='post' action='"+url+"' ENCTYPE='multipart/form-data'>\n" +
            "\n" +
            "<table border=2 cellpading=10>\n";
        if (isInside) h = h +
            "<tr><td>\n" +
            "<h3><font color=blue>"+ (++seq) +". User Identification</font><font color=red>*</font></h3>\n" +
            "\n" +
            "  USER-ID :<input type=text name='user' value=''> &nbsp;&nbsp;&nbsp;<br>\n" +
            "  PASSWORD:<input type=password name='pass' value=''> &nbsp;&nbsp;&nbsp;\n" +
            "  \n" +
            "</td></tr>\n";
        else h = h + "          <input type='hidden' name='user' value='"+util.getSessionTag() + user+"'>\n";
        h = h +
            "<tr><td>\n" +
            "<h3><font color=blue>"+ (++seq) +". Senario Name</font><font color=red>*</font>\n" +
            "\n" +
            " <input type=text name='scenario' value=''> &nbsp;&nbsp;&nbsp;</h3>\n" +
            "  \n" +
            "</td></tr>\n" +
            "<tr><td>\n" +
            "<h3><font color=blue>"+ (++seq) +". Do You Want to Overwrite?</font>\n" +
            "  <input type=radio name='overwrite' value='Yes'>Yes&nbsp;&nbsp;&nbsp;\n" +
            "  <input type=radio name='overwrite' value='No' checked>No&nbsp;&nbsp;&nbsp;\n" +
            "  <input type=radio name='overwrite' value='Backup'>Backup&nbsp;&nbsp;&nbsp;</h3>\n" +
            "</td></tr>\n" +
            "<tr><td>\n" +
            "<h3><font color=blue>"+ (++seq) +". Is this scenario for V2-V3?</font>\n" +
            "  <input type=radio name='v2v3' value='true'>Yes&nbsp;&nbsp;&nbsp;\n" +
            "  <input type=radio name='v2v3' value='false' checked>No&nbsp;&nbsp;&nbsp;\n" +
            "</td></tr>\n" +
            "<tr><td>\n" +
            "<h3><font color=blue>"+ (++seq) +". Does this scenario apply XML validation?</font>\n" +
            "  <input type=radio name='xsdvalidation' value='true' checked>Yes&nbsp;&nbsp;&nbsp;\n" +
            "  <input type=radio name='xsdvalidation' value='false'>No&nbsp;&nbsp;&nbsp;\n" +
            "</td></tr>\n" +
            "<tr><td>\n" +
            "<h3><font color=blue>"+ (++seq) +". SCS File</font><font color=red>*</font>\n" +
            "\n" +
            "  <input type=\"file\" name=\"scsfile\" size=\"30\" style=\"border-top-width:1; style=\"border-right-width:1; \n" +
            "         style=\"border-bottom-width:1; style=\"border-left-width:1px; border-style:dashed;\"> </h3>\n" +
            "  \n" +
            "</td></tr>\n" +
            "<tr><td>\n" +
            "<h3><font color=blue>"+ (++seq) +". H3S File</font><font color=red>*</font>\n" +
            "\n" +
            "  <input type=\"file\" name=\"h3sfile\" size=\"30\" style=\"border-top-width:1; style=\"border-right-width:1; \n" +
            "         style=\"border-bottom-width:1; style=\"border-left-width:1px; border-style:dashed;\"></h3> \n" +
            "  \n" +
            "</td></tr>\n" +
            "<tr><td>\n" +
            "<h3><font color=blue>"+ (++seq) +". MAP File</font><font color=red>*</font>\n" +
            "\n" +
            "  <input type=\"file\" name=\"mapfile\" size=\"30\" style=\"border-top-width:1; style=\"border-right-width:1; \n" +
            "         style=\"border-bottom-width:1; style=\"border-left-width:1px; border-style:dashed;\"></h3> \n" +
            "  \n" +
            "</td></tr>\n" +
            "<tr><td>\n" +
            "<h3><font color=blue>"+ (++seq) +". XSD File</font>\n" +
            "\n" +
            "  <input type=\"file\" name=\"xsdfile\" size=\"30\" style=\"border-top-width:1; style=\"border-right-width:1; \n" +
            "         style=\"border-bottom-width:1; style=\"border-left-width:1px; border-style:dashed;\"> </h3>\n" +
            "  \n" +
            "</td></tr>\n" +
            "<tr><td>\n" +
            "<h3><font color=blue>"+ (++seq) +". VOM File</font>\n" +
            "\n" +
            "  <input type=\"file\" name=\"vomfile1\" size=\"30\" style=\"border-top-width:1; style=\"border-right-width:1; \n" +
            "         style=\"border-bottom-width:1; style=\"border-left-width:1px; border-style:dashed;\"> <br>\n" +
            "  <input type=\"file\" name=\"vomfile2\" size=\"30\" style=\"border-top-width:1; style=\"border-right-width:1; \n" +
            "         style=\"border-bottom-width:1; style=\"border-left-width:1px; border-style:dashed;\"> <br>\n" +
            "  <input type=\"file\" name=\"vomfile3\" size=\"30\" style=\"border-top-width:1; style=\"border-right-width:1; \n" +
            "         style=\"border-bottom-width:1; style=\"border-left-width:1px; border-style:dashed;\"> <br>\n" +
            "  <input type=\"file\" name=\"vomfile4\" size=\"30\" style=\"border-top-width:1; style=\"border-right-width:1; \n" +
            "         style=\"border-bottom-width:1; style=\"border-left-width:1px; border-style:dashed;\">\n" +
            "   </h3>\n" +
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


