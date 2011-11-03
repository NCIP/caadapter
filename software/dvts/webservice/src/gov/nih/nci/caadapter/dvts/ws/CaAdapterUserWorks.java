package gov.nih.nci.caadapter.dvts.ws;

import gov.nih.nci.caadapter.dvts.common.function.DateFunction;
import gov.nih.nci.caadapter.dvts.common.util.FileUtil;

import java.io.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * Created by IntelliJ IDEA.
 * User: kium
 * Date: Jun 18, 2009
 * Time: 5:36:58 PM
 * To change this template use File | Settings | File Templates.
 */

public class CaAdapterUserWorks  extends HttpServlet
{

	boolean isDoGet = false;
	boolean useSession = false;
	CaadapterWSUtil util = new CaadapterWSUtil();
	GeneralUtilitiesWS gUtil = new GeneralUtilitiesWS();
	String ipAddr;
	String ADMINISTRATOR_ID = util.getAdministratorID();
	String SESSION_TAG = util.getSessionTag();
	PrintWriter out = null;
	//String loginID = null;
	String userPath = util.getRootScenarioPath() + util.getAccessFailureTag();
    String currentCommand = "";
    String currentUser = "";

    /* ***********************************************************
	 *  doGet()  Initial Attach point of this managing user
	 ************************************************************ */
	public void doGet (HttpServletRequest req, HttpServletResponse response)
		throws ServletException, IOException
	{
	    response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		String rep = "<tr><td align=\"center\" width=\"30%\" bgcolor=\"CBF5FF\">Command</td>"
                   + "<td width=\"70%\">"
                   //+ "<input type=radio name='command' value='scenarioRegistration'>Scenario Registration&nbsp;&nbsp;&nbsp;<br>"
                   + "<input type=radio name='command' value='vomRegistration'>VOM File Registration or deleting&nbsp;&nbsp;&nbsp;<br>"
                   + "<input type=radio name='command' value='checkPassword' checked>Test Password&nbsp;&nbsp;&nbsp;<br>"
                   + "<input type=radio name='command' value='changePassword'>Change Password&nbsp;&nbsp;&nbsp;<br>"
                   + "</td></tr>";

		util.getUniversalLogin(out, this.getClass().getName(), "caAdapter DVTS User Works", "User", "username", "password", rep, req);

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
//		String startTime = "";
//		String endTime = "";
		String res = "";
//		int count = 0;
//		String outputFile = "";
		ipAddr = req.getRemoteAddr();
		DateFunction df = new DateFunction();

		useSession = false;

		// Parameter variables
		String username = null;
		String password = null;

		String userPass1 = null;
		String userPass2 = null;

		String command = null;
		String[] paramArray = new String[] {username, password, userPass1, userPass2, command};

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

				if (paramName.equalsIgnoreCase("username")) idxP = 0;
				else if (paramName.equalsIgnoreCase("password")) idxP = 1;
				else if (paramName.equalsIgnoreCase("userPass1")) idxP = 2;
				else if (paramName.equalsIgnoreCase("userPass2")) idxP = 3;
				else if (paramName.equalsIgnoreCase("command")) idxP = 4;
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
				if (i == 0) username = paramArray[i];
				else if (i == 1) password = paramArray[i];
				else if (i == 2) userPass1 = paramArray[i];
				else if (i == 3) userPass2 = paramArray[i];
				else if (i == 4) command = paramArray[i];
			}

			util.setReturnTypeXML(false);
			if ((username == null)||(username.trim().equals("")))
			{
				util.returnMessageAndLogging(out, "Null User ID", util.codeFATAL(), "Null User ID", userPath, ADMINISTRATOR_ID, ipAddr, this);
				return;
			}
			username = username.trim();
            boolean authorized = false;
            if (username.equals(ADMINISTRATOR_ID))
			{
                userPath = util.getRootScenarioPath() + username;
                util.returnMessageAndLogging(out, "Unauthorized Admin Work", util.codeFATAL(), "This is not a Administrator's work", userPath, username, ipAddr, this);
				return;

//
//				String resLogin = util.authorizeLoginID(username, password, null, ipAddr, true, this, true);
//				if (resLogin != null)
//				{
//					res = resLogin;
//					util.returnMessageAndLogging(out, "Unauthorized Admin User", util.codeFATAL(), resLogin, userPath, username, ipAddr, this);
//					return;
//				}
			}
            else
            {
                String id;
                if (username.startsWith(SESSION_TAG))
                {
                    id = username.substring(SESSION_TAG.length());
                    userPath = util.getRootScenarioPath() + id;
                    String resLogin = util.checkSession(id, ipAddr, this);
                    if (resLogin != null)
                    {
                        res = resLogin;
                        util.returnMessageAndLogging(out, "Unauthorized User Session", util.codeFATAL(), resLogin, userPath, id, ipAddr, this);
                        return;
                    }
                    //authorized = true;
                    //currentUser = id;
                    useSession = true;
                }
                else id = username;

                userPath = util.getRootScenarioPath() + id;
                String resLogin = util.authorizeLoginID(id, password, null, ipAddr, true, this, true);
                if (resLogin != null)
                {
                    res = resLogin;
                    util.returnMessageAndLogging(out, "Unauthorized User or Password", util.codeFATAL(), resLogin, userPath, username, ipAddr, this);
                    return;
                }
                authorized = true;
                currentUser = id;

            }

            if (!authorized)
            {
                util.returnMessageAndLogging(out, "Authorization Failure", util.codeFATAL(), "Authorization Failure : " + username, userPath, ADMINISTRATOR_ID, ipAddr, this);
                return;
            }
            String userID = currentUser;

			if (userPass1 == null) userPass1 = "";
			else userPass1 = userPass1.trim();
			if (userPass2 == null) userPass2 = "";
			else userPass2 = userPass2.trim();

			if (useSession)
			{
			    if ((userPass1.equals(""))||(userPass2.equals("")))
				{
					util.returnMessageAndLogging(out, "Null or Empty Password items", util.codeERROR(), "Null or Empty Password items", userPath, userID, ipAddr, this);
					return;
				}
//				if (!command.equalsIgnoreCase("createUser"))
//				{
//					String resLogin = util.authorizeLoginID(userID, userPass1, null, ipAddr, true, this);
//					if (resLogin != null)
//					{
//						if (command.equalsIgnoreCase("checkPassword"))
//						{
//						    util.returnMessageAndLogging(out, "Invalid Password", util.codeWARNING(), "The input Password is invalid for this user : " + userID, userPath, userID, ipAddr, this);
//							return;
//						}
//						else
//						{
//							util.returnMessageAndLogging(out, "Unauthorized User", util.codeERROR(), resLogin, userPath, username, ipAddr, this);
//							return;
//						}
//					}
//				}
				//loginID = userID;
				//userPath = util.getRootScenarioPath() + userID;
			}

            currentCommand = command;
            if (command.equalsIgnoreCase("scenarioRegistration"))
			{
//				if (useSession)
//				{
//				    util.returnMessageAndLogging(out, "Invalid Session Usage", util.codeERROR(), "Invalid Session Usage for 'scenarioRegistration' Command" + userID, userPath, userID, ipAddr, this);
//					return;
//				}
//				else
//                {
//                    ScenarioFileRegistration sr = new ScenarioFileRegistration();
//                    out.println(sr.getInitialHTMLFromOutSide(req, currentUser));
//                    return;
//                }
            }
            else if (command.equalsIgnoreCase("vomRegistration"))
			{
				if (useSession)
				{
				    util.returnMessageAndLogging(out, "Invalid Session Usage", util.codeERROR(), "Invalid Session Usage for 'vomRegistration' Command" + userID, userPath, userID, ipAddr, this);
					return;
				}
				else
                {
                    VOMFileRegistration sr = new VOMFileRegistration();
                    out.println(sr.getInitialHTMLFromOutSide(req, currentUser));
                    return;
                }
            }
            else if (command.equalsIgnoreCase("checkPassword"))
			{
			    util.returnMessageAndLogging(out, "Valid Password", util.codeINFO(), "The input Password is valid for this user : " + userID, userPath, userID, ipAddr, this);
				return;
			}
			else if (command.equalsIgnoreCase("changePassword"))
			{
			    if (useSession)
				{
				    if (userPass1.equals(""))
					{
					    util.returnMessageAndLogging(out, "Changing Password Failure", util.codeERROR(), "New password is empty : " + userID, userPath, userID, ipAddr, this);
						return;
					}
				    if (!userPass1.equals(userPass2))
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
				else sendHTML(req);
			}
			else
			{
				util.returnMessageAndLogging(out, "Invalid Command", util.codeFATAL(), "Invalid Command" + command + " for " + userID, userPath, ADMINISTRATOR_ID, ipAddr, this);
				return;
			}
		}
		catch(Exception ee)
		{

			util.returnMessageAndLogging(out, "Servlet parameter Parsing ERROR - User Works", util.codeERROR(),"User:" + currentUser + " - " + ee.getMessage(), userPath, currentUser, ipAddr, this);

			ee.printStackTrace();
			return;
		}

		if (useSession)
		{
			//endTime = df.getCurrentTime();

			util.returnMessage(out, "Command running finish well", util.codeINFO(), "Command running finish well - " + command + " for user " + currentUser);
			util.addLogRecord(userPath, gUtil.getNowDate() + "Command running finish well - " + command + " for user " + currentUser + " from " + ipAddr + ", called by " + this.getClass().getName());
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

            h = "<html>\t\n" +
                "  <head>\t\t\n" +
                "    <title>caAdapter Web Service User Management - Change Password</title>\t\n" +
                "  </head>\n" +
                "  <body>\t\n" +
                "    <form method=\"Post\" action=\""+url+"\">\n" +
                "      <center>\n" +
                "      <br><br><br>\n" +
                "      <h1><font color='brown'>caAdapter Web Service Change Password<br></font></h1>\n" +
                "      <h4><font color='brown'>Input Old and New Passwords</font></h4>\n" +
                "      <font color='green'><br><br><br>\n" +
                "        <table border=1 bordercolor=\"blue\">\n" +
                "          <tr>\n" +
                "            <td align=\"center\" width=\"30%\" bgcolor=\"CBF5FF\">Old Password</td>\n" +
                "            <td width=\"70%\"><input type=\"password\" name=\"password\" size=20 onMouseOver=\"this.style.backgroundColor='ivory'\" onMouseOut=\"this.style.backgroundColor='white'\"></td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td align=\"center\" width=\"30%\" bgcolor=\"CBF5FF\">New Password</td>\n" +
                "            <td width=\"70%\"><input type=\"password\" name=\"userPass1\" size=20 onMouseOver=\"this.style.backgroundColor='ivory'\" onMouseOut=\"this.style.backgroundColor='white'\"></td>\n" +
                "          </tr>\n" +
                "          <tr>\n" +
                "            <td align=\"center\" width=\"30%\" bgcolor=\"CBF5FF\">New Password-Confirm</td>\n" +
                "            <td width=\"70%\"><input type=\"password\" name=\"userPass2\" size=20 onMouseOver=\"this.style.backgroundColor='ivory'\" onMouseOut=\"this.style.backgroundColor='white'\"></td>\n" +
                "          </tr>\n" +
                "          <input type='hidden' name='username' value='"+SESSION_TAG + currentUser+"'>\n" +
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

        out.println(h);
    }

}

