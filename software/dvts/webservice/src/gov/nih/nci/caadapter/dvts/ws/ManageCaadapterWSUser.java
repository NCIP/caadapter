/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.ws;

import gov.nih.nci.caadapter.dvts.common.function.DateFunction;
import gov.nih.nci.caadapter.dvts.common.util.FileUtil;
import gov.nih.nci.caadapter.dvts.common.util.vom.ManageVOMFile;

import java.io.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * Created by IntelliJ IDEA.
 * User: kium
 * Date: Jun 16, 2009
 * Time: 1:15:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class ManageCaadapterWSUser extends HttpServlet
{

	boolean isDoGet = false;
	boolean useSession = false;
	CaadapterWSUtil util = new CaadapterWSUtil();
	GeneralUtilitiesWS gUtil = new GeneralUtilitiesWS();
	String ipAddr;
	String ADMINISTRATOR_ID = util.getAdministratorID();
	String SESSION_TAG = util.getSessionTag();
	PrintWriter out = null;
	String loginID = null;
	String userPath = util.getRootScenarioPath() + util.getAccessFailureTag();
    String currentCommand = "";

    /* ***********************************************************
	 *  doGet()  Initial Attach point of this managing user
	 ************************************************************ */
	public void doGet (HttpServletRequest req, HttpServletResponse response)
		throws ServletException, IOException
	{
        util.setBaseURLToPropertyfile(req);
        response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		String rep = "<tr><td align=\"center\" width=\"30%\" bgcolor=\"CBF5FF\">Command</td>"
                   + "<td width=\"70%\">"
                   + "<input type=radio name='command' value='createUser'>Create Context&nbsp;&nbsp;&nbsp;<br>"
                   + "<input type=radio name='command' value='removeUser'>Remove Context&nbsp;&nbsp;&nbsp;<br>"
                   + "<input type=radio name='command' value='checkPassword' checked>Test Context Password&nbsp;&nbsp;&nbsp;<br>"
                   + "<input type=radio name='command' value='changePassword'>Enforced Change Password&nbsp;&nbsp;&nbsp;<br>"
                   + "</td></tr>";

		util.getUniversalLogin(out, this.getClass().getName(), "caAdapter DVTS Context Management", "Administrator", "adminID", "adminPass", rep, req);

	}

    /* ***********************************************************
	 *  doPost()
	 ************************************************************ */
	public void doPost (HttpServletRequest req, HttpServletResponse response)
		throws ServletException, IOException
	{
		response.setContentType("text/html");
		out = response.getWriter();
		String connectTime = gUtil.getNowDate();
		String startTime = "";
		String endTime = "";
		String res = "";
		int count = 0;
		String outputFile = "";
		ipAddr = req.getRemoteAddr();
		DateFunction df = new DateFunction();

		useSession = false;

		// Parameter variables
		String adminID = null;
		String adminPass = null;
		String userID = null;
		String userPass1 = null;
		String userPass2 = null;
		String userPass3 = null;
		String command = null;
		String[] paramArray = new String[] {adminID, adminPass, userID, userPass1, userPass2, userPass3, command};

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
				if (param == null) param = "";

				if (paramName.equalsIgnoreCase("adminID")) idxP = 0;
				else if (paramName.equalsIgnoreCase("adminPass")) idxP = 1;
				else if (paramName.equalsIgnoreCase("userID")) idxP = 2;
				else if (paramName.equalsIgnoreCase("userPass1")) idxP = 3;
				else if (paramName.equalsIgnoreCase("userPass2")) idxP = 4;
				else if (paramName.equalsIgnoreCase("userPass3")) idxP = 5;
				else if (paramName.equalsIgnoreCase("command")) idxP = 6;
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
				if (i == 0) adminID = paramArray[i];
				else if (i == 1) adminPass = paramArray[i];
				else if (i == 2) userID = paramArray[i];
				else if (i == 3) userPass1 = paramArray[i];
				else if (i == 4) userPass2 = paramArray[i];
				else if (i == 5) userPass3 = paramArray[i];
				else if (i == 6) command = paramArray[i];
			}

			util.setReturnTypeXML(false);
			if ((adminID == null)||(adminID.trim().equals("")))
			{
				util.returnMessageAndLogging(out, "Null Administrator ID", util.codeFATAL(), "Null Administrator ID", userPath, ADMINISTRATOR_ID, ipAddr, this);
				return;
			}
			adminID = adminID.trim();
			if (adminID.equals(ADMINISTRATOR_ID))
			{
				userPath = util.getRootScenarioPath() + adminID;

				String resLogin = util.authorizeLoginID(adminID, adminPass, null, ipAddr, true, this, true);
				if (resLogin != null)
				{
					res = resLogin;
					util.returnMessageAndLogging(out, "Unauthorized Admin User", util.codeFATAL(), resLogin, userPath, adminID, ipAddr, this);
					return;
				}
			}
			else if (adminID.equals(SESSION_TAG))
			{
			    userPath = util.getRootScenarioPath() + ADMINISTRATOR_ID;
				String resLogin = util.checkSession(ADMINISTRATOR_ID, ipAddr, this);
				if (resLogin != null)
				{
					res = resLogin;
					util.returnMessageAndLogging(out, "Unauthorized Admin Session", util.codeFATAL(), resLogin, userPath, adminID, ipAddr, this);
					return;
				}
				useSession = true;
			}
			else
			{
				util.returnMessageAndLogging(out, "Invalid Administrator ID", util.codeFATAL(), "Invalid Administrator ID" + adminID, userPath, ADMINISTRATOR_ID, ipAddr, this);
				return;
			}
			loginID = ADMINISTRATOR_ID;

			if (userID == null) userID = "";
			else userID = userID.trim();
			if (userPass1 == null) userPass1 = "";
			else userPass1 = userPass1.trim();
			if (userPass2 == null) userPass2 = "";
			else userPass2 = userPass2.trim();
			if (userPass3 == null) userPass3 = "";
			else userPass3 = userPass3.trim();

			if (useSession)
			{
			    if ((userID.equals(""))||(userPass1.equals("")))
				{
					util.returnMessageAndLogging(out, "Null or Empty UserID, Password", util.codeERROR(), "Null or Empty UserID, Password", userPath, ADMINISTRATOR_ID, ipAddr, this);
					return;
				}
				if (!command.equalsIgnoreCase("createUser"))
				{
                    String resLogin = "";
                    if (command.equalsIgnoreCase("changePassword")) resLogin = util.validateLoginID(userID);
                    else resLogin = util.authorizeLoginID(userID, userPass1, null, ipAddr, true, this);

                    if (resLogin != null)
					{
						if (command.equalsIgnoreCase("changePassword"))
                        {
						    util.returnMessageAndLogging(out, "Invalid User", util.codeWARNING(), resLogin, userPath, userID, ipAddr, this);
							return;
						}
                        else
						{
							util.returnMessageAndLogging(out, "Unauthorized User or Password", util.codeERROR(), resLogin, userPath, adminID, ipAddr, this);
							return;
						}
					}
				}
				loginID = userID;
				userPath = util.getRootScenarioPath() + userID;
			}

            currentCommand = command;
            if (command.equalsIgnoreCase("createUser"))
			{
				if (useSession)
				{
                    if (userID.toLowerCase().startsWith(ManageVOMFile.SAMPLE_CONTEXT_TAG.toLowerCase()))
                    {
					    util.returnMessageAndLogging(out, "Invalid creating a sample context", util.codeERROR(), "User cannot create a sample context. : " + userID, userPath, userID, ipAddr, this);
						return;
					}
                    File dir = new File(userPath);
					if ((dir.exists())&&(dir.isDirectory()))
					{
					    util.returnMessageAndLogging(out, "Creating new User Failure", util.codeERROR(), "Already Exist User : " + userID, userPath, userID, ipAddr, this);
						return;
					}
					if (!userPass1.equals(userPass2))
					{
					    util.returnMessageAndLogging(out, "Creating new User Failure", util.codeERROR(), "Confirming Password is not same for Creating new User : " + userID, userPath, ADMINISTRATOR_ID, ipAddr, this);
						return;
					}
					dir.mkdirs();
					if (!util.savePassword(userPath, userPass1, connectTime + "99987"))
					{
					    userPath = util.getRootScenarioPath() + ADMINISTRATOR_ID;
					    util.returnMessageAndLogging(out, "Create User Failure", util.codeERROR(), "Create User Failure : " + userID, userPath, ADMINISTRATOR_ID, ipAddr, this);
						dir.delete();
						return;
					}
				}
				else sendHTML("CreateUser.html", req);
			}
			else if (command.equalsIgnoreCase("removeUser"))
			{
				if (useSession)
				{
				    if (!userPass1.equals(userPass2))
					{
					    util.returnMessageAndLogging(out, "Removing User Failure", util.codeERROR(), "Confirming Password is not the same for Removing User : " + userID, userPath, ADMINISTRATOR_ID, ipAddr, this);
						return;
					}
				    gUtil.deleteFile(new File(userPath));
				}
				else sendHTML("RemoveUser.html", req);
			}
			else if (command.equalsIgnoreCase("checkPassword"))
			{
			    if (useSession)
				{
					util.returnMessageAndLogging(out, "Valid Password", util.codeINFO(), "The input Password is valid for this user : " + userID, userPath, userID, ipAddr, this);
					return;
				}
				else sendHTML("CheckPassword.html", req);
			}
			else if (command.equalsIgnoreCase("changePassword"))
			{
			    if (useSession)
				{
				    if (userPass2.equals(""))
					{
					    util.returnMessageAndLogging(out, "Changing Password Failure", util.codeERROR(), "New password is empty : " + userID, userPath, userID, ipAddr, this);
						return;
					}
				    if (!userPass2.equals(userPass3))
					{
					    util.returnMessageAndLogging(out, "Changing Password Failure", util.codeERROR(), "Confirming Password is not the same for Changing Password of this user : " + userID, userPath, userID, ipAddr, this);
						return;
					}
					if (!util.savePassword(userPath, userPass2, connectTime + "99988"))
					{
					    util.returnMessageAndLogging(out, "Changing Password Failure", util.codeERROR(), "Saving new Password Failure : " + userID, userPath, userID, ipAddr, this);
						return;
					}
				}
				else sendHTML("ChangePassword.html", req);
			}
			else
			{
				util.returnMessageAndLogging(out, "Invalid Command", util.codeFATAL(), "Invalid Command" + command + " for " + userID, userPath, ADMINISTRATOR_ID, ipAddr, this);
				return;
			}
		}
		catch(Exception ee)
		{

			util.returnMessageAndLogging(out, "Servlet parameter Parsing ERROR - manage user", util.codeERROR(),"User:" + userID + " - " + ee.getMessage(), userPath, loginID, ipAddr, this);

			ee.printStackTrace();
			return;
		}

		if (useSession)
		{
			endTime = df.getCurrentTime();

			util.returnMessage(out, "Command running finish well", util.codeINFO(), "Command running finish well - " + command + " for user " + userID);
			util.addLogRecord(userPath, gUtil.getNowDate() + " : Command running finish well - " + command + " for user " + userID + " from " + ipAddr + ", called by " + this.getClass().getName());
		}

	}

	private void sendHTML(String fileName, HttpServletRequest req)
	{
	    String filePath = util.getRootPath() + "html" + File.separator + fileName;
	    File f = new File(filePath.trim());
		if ((fileName == null)||(!f.isFile()))
		{
            sendHTML(req);
            //util.returnMessageAndLogging(out, "Not exist HTML File Name", util.codeERROR(), "This File is not exist : " + fileName, userPath, loginID, ipAddr, this);
			return;
		}
	    List<String> res = null;
		try
		{
			res = FileUtil.readFileIntoList(filePath);
		}
		catch(IOException ie) { res = null; }

		if ((res == null)||(res.size() == 0))
		{
            sendHTML(req);
            //util.returnMessageAndLogging(out, "HTML Reading failure!!", util.codeERROR(), "HTML Reading failure : " + fileName, userPath, loginID, ipAddr, this);
			return;
		}
		String ipTag = "##ROOT_URL##";
		String sessionTag = "##session_main##";
		for(String line:res)
		{
			int idx1 = line.toUpperCase().indexOf(ipTag);
			if (idx1 > 0)
			{
			    line = line.substring(0, idx1) + util.getBaseURL() + line.substring(idx1 + ipTag.length());
			}

			int idx2 = line.toLowerCase().indexOf(sessionTag);
			if (idx2 > 0)
			{
			    line = line.substring(0, idx2) + SESSION_TAG + line.substring(idx2 + sessionTag.length());
			}
			out.println(line + "\r\n");
		}
	}
    private void sendHTML(HttpServletRequest req)
    {
        String program = this.getClass().getName();
        while(true)
        {
            int idx = program.indexOf(".");
            if (idx < 0) break;
            program = program.substring(idx+1);
        }
        String url = util.getBaseURLFromRequest(req);
        if (url == null) url = util.getBaseURL() + "servlet/" + program;
        else url = url + "/" + program;

        String h = "";
        if (currentCommand.equalsIgnoreCase("createUser"))
        {
            h = "<html>\t\n" +
                "  <head>\t\t\n" +
                "    <title>caAdapter Web Service User Management - Create User</title>\t\n" +
                "  </head>\n" +
                "  <body>\t\n" +
                "    <form method=\"Post\" action=\"" + url + "\">\n" +
                "      <center>\n" +
                "      <br><br><br>\n" +
                "      <h1><font color='brown'>caAdapter DVTS Create Context<br></font></h1>\n" +
                "      <h4><font color='brown'>Input New User ID and Password</font></h4>\n" +
                "      <font color='green'><br><br><br>\n" +
                "        <table border=1 bordercolor=\"blue\">\n" +
                "          <tr>\n" +
                "            <td align=\"center\" width=\"30%\" bgcolor=\"CBF5FF\">Context Name</td>\n" +
                "            <td width=\"70%\"><input type=\"text\" name=\"userID\" size=20 onMouseOver=\"this.style.backgroundColor='yellow'\" onMouseOut=\"this.style.backgroundColor='white'\"></td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td align=\"center\" width=\"30%\" bgcolor=\"CBF5FF\">Password</td>\n" +
                "            <td width=\"70%\"><input type=\"password\" name=\"userPass1\" size=20 onMouseOver=\"this.style.backgroundColor='ivory'\" onMouseOut=\"this.style.backgroundColor='white'\"></td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td align=\"center\" width=\"30%\" bgcolor=\"CBF5FF\">Password-Confirm</td>\n" +
                "            <td width=\"70%\"><input type=\"password\" name=\"userPass2\" size=20 onMouseOver=\"this.style.backgroundColor='ivory'\" onMouseOut=\"this.style.backgroundColor='white'\"></td>\n" +
                "          </tr>\n" +
                "          <input type='hidden' name='adminID' value='"+SESSION_TAG+"'>\n" +
                "          <input type='hidden' name='command' value='"+currentCommand+"'>\n" +
                "          <tr>\n" +
                "            <td colspan=2 align=\"center\">\n" +
                "              <input type=\"submit\" value=\"Submit\" style=\"background-color:cbf5ff; color:blue\">&nbsp;\n" +
                "              <input type=\"reset\" value=\"Cencel\" style=\"background-color:cbf5ff; color:blue\">\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "      </font></center>\n" +
                "    </form>\n" +
                "  </body>\n" +
                "</html>";
        }
        else if (currentCommand.equalsIgnoreCase("removeUser"))
        {
            h = "<html>\t\n" +
                "  <head>\t\t\n" +
                "    <title>caAdapter DVTS Context Management - Remove Context</title>\t\n" +
                "  </head>\n" +
                "  <body>\t\n" +
                "    <form method=\"Post\" action=\""+url+"\">\n" +
                "      <center>\n" +
                "      <br><br><br>\n" +
                "      <h1><font color='brown'>caAdapter DVTS Remove Context<br></font></h1>\n" +
                "      <h4><font color='brown'>Input Context Name and Password for removing</font></h4>\n" +
                "      <font color='green'><br><br>Please, be carefule for delete Context...<br>\n" +
                "        <table border=1 bordercolor=\"blue\">\n" +
                "          <tr>\n" +
                "            <td align=\"center\" width=\"30%\" bgcolor=\"CBF5FF\">Context Name</td>\n" +
                "            <td width=\"70%\"><input type=\"text\" name=\"userID\" size=20 onMouseOver=\"this.style.backgroundColor='yellow'\" onMouseOut=\"this.style.backgroundColor='white'\"></td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td align=\"center\" width=\"30%\" bgcolor=\"CBF5FF\">Password</td>\n" +
                "            <td width=\"70%\"><input type=\"password\" name=\"userPass1\" size=20 onMouseOver=\"this.style.backgroundColor='ivory'\" onMouseOut=\"this.style.backgroundColor='white'\"></td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td align=\"center\" width=\"30%\" bgcolor=\"CBF5FF\">Password-Confirm</td>\n" +
                "            <td width=\"70%\"><input type=\"password\" name=\"userPass2\" size=20 onMouseOver=\"this.style.backgroundColor='ivory'\" onMouseOut=\"this.style.backgroundColor='white'\"></td>\n" +
                "          </tr>\n" +
                "          <input type='hidden' name='adminID' value='"+SESSION_TAG+"'>\n" +
                "          <input type='hidden' name='command' value='"+currentCommand+"'>\n" +
                "          <tr>\n" +
                "            <td colspan=2 align=\"center\">\n" +
                "              <input type=\"submit\" value=\"Submit\" style=\"background-color:cbf5ff; color:blue\">&nbsp;\n" +
                "              <input type=\"reset\" value=\"Cencel\" style=\"background-color:cbf5ff; color:blue\">\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "      </font></center>\n" +
                "    </form>\n" +
                "  </body>\n" +
                "</html>";
        }
        else if (currentCommand.equalsIgnoreCase("checkPassword"))
        {
            h = "<html>\t\n" +
                "  <head>\t\t\n" +
                "    <title>caAdapter DVTS Context Management - Check Password</title>\t\n" +
                "  </head>\n" +
                "  <body>\t\n" +
                "    <form method=\"Post\" action=\""+url+"\">\n" +
                "      <center>\n" +
                "      <br><br><br>\n" +
                "      <h1><font color='brown'>caAdapter DVTS Check Password<br></font></h1>\n" +
                "      <h4><font color='brown'>Input Context Name and Password</font></h4>\n" +
                "      <font color='green'><br><br><br>\n" +
                "        <table border=1 bordercolor=\"blue\">\n" +
                "          <tr>\n" +
                "            <td align=\"center\" width=\"30%\" bgcolor=\"CBF5FF\">Context Name</td>\n" +
                "            <td width=\"70%\"><input type=\"text\" name=\"userID\" size=20 onMouseOver=\"this.style.backgroundColor='yellow'\" onMouseOut=\"this.style.backgroundColor='white'\"></td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td align=\"center\" width=\"30%\" bgcolor=\"CBF5FF\">Password</td>\n" +
                "            <td width=\"70%\"><input type=\"password\" name=\"userPass1\" size=20 onMouseOver=\"this.style.backgroundColor='ivory'\" onMouseOut=\"this.style.backgroundColor='white'\"></td>\n" +
                "          </tr>\n" +
                "          \n" +
                "          <input type='hidden' name='adminID' value='"+SESSION_TAG+"'>\n" +
                "          <input type='hidden' name='command' value='"+currentCommand+"'>\n" +
                "          <tr>\n" +
                "            <td colspan=2 align=\"center\">\n" +
                "              <input type=\"submit\" value=\"Submit\" style=\"background-color:cbf5ff; color:blue\">&nbsp;\n" +
                "              <input type=\"reset\" value=\"Cencel\" style=\"background-color:cbf5ff; color:blue\">\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "      </font></center>\n" +
                "    </form>\n" +
                "  </body>\n" +
                "</html>";
        }
        else if (currentCommand.equalsIgnoreCase("changePassword"))
        {
            h = "<html>\t\n" +
                "  <head>\t\t\n" +
                "    <title>caAdapter DVTS Context Management - Change Password</title>\t\n" +
                "  </head>\n" +
                "  <body>\t\n" +
                "    <form method=\"Post\" action=\""+url+"\">\n" +
                "      <center>\n" +
                "      <br><br><br>\n" +
                "      <h1><font color='brown'>caAdapter DVTS Enforced Change Password<br></font></h1>\n" +
                "      <h4><font color='brown'>Input Context Name and Passwords</font></h4>\n" +
                "      <font color='green'><br><br><br>\n" +
                "        <table border=1 bordercolor=\"blue\">\n" +
                "          <tr>\n" +
                "            <td align=\"center\" width=\"30%\" bgcolor=\"CBF5FF\">Context Name</td>\n" +
                "            <td width=\"70%\"><input type=\"text\" name=\"userID\" size=20 onMouseOver=\"this.style.backgroundColor='yellow'\" onMouseOut=\"this.style.backgroundColor='white'\"></td>\n" +
                "          </tr>\n" +
//                "          <tr>\n" +
//                "            <td align=\"center\" width=\"30%\" bgcolor=\"CBF5FF\">Old Password</td>\n" +
//                "            <td width=\"70%\"><input type=\"password\" name=\"userPass1\" size=20 onMouseOver=\"this.style.backgroundColor='ivory'\" onMouseOut=\"this.style.backgroundColor='white'\"></td>\n" +
//                "          </tr>\n" +
                "          <tr>\n" +
                "            <td align=\"center\" width=\"30%\" bgcolor=\"CBF5FF\">New Password</td>\n" +
                "            <td width=\"70%\"><input type=\"password\" name=\"userPass2\" size=20 onMouseOver=\"this.style.backgroundColor='ivory'\" onMouseOut=\"this.style.backgroundColor='white'\"></td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td align=\"center\" width=\"30%\" bgcolor=\"CBF5FF\">New Password-Confirm</td>\n" +
                "            <td width=\"70%\"><input type=\"password\" name=\"userPass3\" size=20 onMouseOver=\"this.style.backgroundColor='ivory'\" onMouseOut=\"this.style.backgroundColor='white'\"></td>\n" +
                "          </tr>\n" +
                "          <input type='hidden' name='adminID' value='"+SESSION_TAG+"'>\n" +
                "          <input type='hidden' name='command' value='"+currentCommand+"'>\n" +
                "          <tr>\n" +
                "            <td colspan=2 align=\"center\">\n" +
                "              <input type=\"submit\" value=\"Submit\" style=\"background-color:cbf5ff; color:blue\">&nbsp;\n" +
                "              <input type=\"reset\" value=\"Cencel\" style=\"background-color:cbf5ff; color:blue\">\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "      </font></center>\n" +
                "    </form>\n" +
                "  </body>\n" +
                "</html>";
        }
        out.println(h);
    }

}
