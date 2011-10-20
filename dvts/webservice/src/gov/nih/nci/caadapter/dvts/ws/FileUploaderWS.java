package gov.nih.nci.caadapter.dvts.ws;


import gov.nih.nci.caadapter.dvts.common.util.FileUtil;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;   // date
import java.text.*;  // simple data format
import java.sql.*;
/**
 * Created by IntelliJ IDEA.
 * User: kium
 * Date: Jun 16, 2009
 * Time: 1:13:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileUploaderWS extends HttpServlet
{
  //????? ???? ?? ?????.
  String filename="";
  //??? ??? ???? ?????.
	String filetype="";
	int ser;
  //???? ????? ?? ?????.
  File f = new File("");
	MultipartRequest multi = null;
	Enumeration files = null;
    CaadapterWSUtil util = new CaadapterWSUtil();
	GeneralUtilitiesWS gUtil = new GeneralUtilitiesWS();
	//HL7MessageTree hmt = null;

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

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	            throws ServletException, IOException
	{
        util.setBaseURLToPropertyfile(req);
        res.setContentType("text/html");
		String rep = "<tr><td align=\"center\" width=\"30%\" bgcolor=\"CBF5FF\">Select File</td>"
                   + "<td width=\"70%\">"
                   + "<input type=\"file\" name=\"up_file\" size=\"30\" style=\"border-top-width:1;\" style=\"border-right-width:1;\""
				   + "style=\"border-bottom-width:1;\" style=\"border-left-width:1px; border-style:dashed;\">"
                   + "</td></tr>";


		util.getUniversalLogin(res.getWriter(), this.getClass().getName(), "caAdapter Web Service File Upload", "Administrator", "adminID", "adminPass", rep, true, req);
	}

  public void doPost(HttpServletRequest req, HttpServletResponse res)
	            throws ServletException, IOException
	  {
		  res.setContentType("text/html;charset=KSC5601"); // original => ("text/html;charset=euc-kr")
			// ?????? ??? ??, ?????? ? ????? ??
			PrintWriter out = res.getWriter();

			String startTime = gUtil.getFormatedNowDate();
			String ipAddr = req.getRemoteAddr();
			// PrintWriter out = new PrintWriter(res, getOutputStream(),"8859_1");
			System.out.println("\nFile uploading Requested at : " + startTime + ", From => " + ipAddr);
			// ??? ???? ??
			System.out.println(req.getHeader("Content-Type"));
			//String adminID_o = util.getAdministratorID();

			String filepath = util.getRootWebDir();

          File dd = new File(filepath);
          if ((dd.exists())&&(dd.isDirectory()))
          {
              if (!filepath.endsWith(File.separator)) filepath = filepath + File.separator;
              filepath = filepath + "doc" + File.separator + "file_exchange" + File.separator;
              File dd2 = new File(filepath);
              if ((dd2.exists())&&(dd2.isDirectory())) {}
              else dd2.mkdirs();
          }
          else
          {
            filepath = FileUtil.searchDir(util.getServiceName() + File.separator + "META-INF");
            if (!filepath.endsWith(File.separator)) filepath = filepath + File.separator;
            filepath = filepath + "doc" + File.separator + "file_exchange" + File.separator;
              File dd2 = new File(filepath);
              dd2.mkdirs();
          }
          /*
			try
			  {

			    //Class.forName("org.gjt.mm.mysql.Driver"); // ???? ??
			    Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
				}
			catch(ClassNotFoundException cfe)
			  {
				  System.out.println("Error from the class for name !!");
					System.out.println(cfe.getMessage());
					System.exit(0);
			  }
			*/
			String name = "";
			String userPath = "";
			List<String> list = new ArrayList<String>();
			try
			  {
			   	multi = new MultipartRequest(req, filepath, 1024*1024*1024);

				String adminID = multi.getParameter("adminID");
			    String adminPass = multi.getParameter("adminPass");

				if (adminID == null) adminID = "";
				else adminID = adminID.trim();
				if (adminPass == null) adminPass = "";
				else adminPass = adminPass.trim();
				String resLogin = util.authorizeLoginID(adminID, adminPass, null, ipAddr, true, this, false);
				if ((resLogin == null)&&(!adminID.equals(util.getAdministratorID()))) resLogin = "Not Administrator ID";
				userPath = util.getRootScenarioPath() + util.getAdministratorID();
				if (resLogin != null)
				{

					util.returnMessageAndLogging(out, "Unauthorized Admin User", util.codeFATAL(), resLogin, userPath, adminID, ipAddr, this);
					return;
				}
	        files = multi.getFileNames();

	        while (files.hasMoreElements())
					  {
              name = (String)files.nextElement();
 	            //????? ??? ??? ???? ???.
              filename = multi.getFilesystemName(name);
	            filetype = multi.getContentType(name);
							list.add(filename);
	            //??? ??
              f = multi.getFile(name);
							System.out.println("Filename = " + filename + ", Filetype = " + filetype);
							//if (filename.equals("")) fileTag = false;
							//else fileTag = true;
	          }
				}
			catch(Exception ex)
			  {
				util.returnMessageAndLogging(out, "Multipart Request Error (File receiving)", util.codeERROR(), "Multipart Request Error (File receiving) : "+filename+ " : " + ex.getMessage(), userPath, util.getAdministratorID(), ipAddr, this);
					return;

				  //out.println("<html><head><title>Multipart Request Error (File receiving)</title></head>");
					//out.println("<body bgcolor='white'>");
					//out.println("<font color='green'><center><br><br>");
					//out.println("<h1>Multipart Request Error (File receiving) : "+filename+"</h1><br><br>");
					//out.println("<h5> " + to8859(ex.getMessage()) + "</h5>");
					//out.println("</body></html>");
					//return;
				}
			//out.println("<html><head><title>Multipart Request (File receiving) was successfully finished</title></head>");
			//out.println("<body bgcolor='white'>");
			//out.println("<font color='green'><center><br><br>");
			//out.println("<h1>Multipart Request (File receiving) was successfully finished. </h1><br><br>");
			//out.println("<h5>Requesting IP Address : "+ipAddr+"<br> Start Time : "+startTime+", End Time : "+getNowDate()+"</h5>");
			//out.println("<h5> Received File(s) </h5><br>");
			//for(int i=0;i<list.size();i++) out.println("<h5> " + to8859(list.get(i)) + "</h5>");
			//out.println("</body></html>");
			//return;
			String rr = "Multipart Request (File receiving) was successfully finished. : " + "Start Time : "+startTime+", End Time : "+gUtil.getFormatedNowDate() + " : ";
            String rr1 = "";
            for(int i=0;i<list.size();i++) rr1 = rr1 + ", " + list.get(i) + "(" + filepath+")";
            rr1 = rr1.substring(1).trim();
            rr = rr + rr1;
            util.returnMessageAndLogging(out, "Multipart Request (File receiving) Success", util.codeINFO(), rr, userPath, util.getAdministratorID(), ipAddr, this);
			return;
		}


}


