package gov.nih.nci.caadapter.dvts.ws;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: kium
 * Date: Jun 16, 2009
 * Time: 1:07:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeleteOutputFile extends HttpServlet
{
	public void doGet(HttpServletRequest req, HttpServletResponse res)
		throws IOException, ServletException
	{
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();

		String deleteFilePath = req.getParameter("file");
		CaadapterWSUtil util = new CaadapterWSUtil();
		//GeneralUtilities gUtil = new GeneralUtilities();

		if ((deleteFilePath == null)||(deleteFilePath.trim().equals("")))
		{
			util.returnMessage(out, "No deleting file", util.codeINFO(), "Target delete file wasn't given.");
		}
		else
		{
			String temp = deleteFilePath;
			while(true)
	        {
	            int idx = temp.indexOf(File.separator);
				if ((!File.separator.equals("/"))&&(idx < 0)) idx = temp.indexOf("/");

	            if (idx < 0) break;
	            temp = temp.substring(idx + 1);
	        }
        	File f = new File(util.getOutputDir() + temp);

			if ((!f.exists())||(!f.isFile()))
				util.returnMessage(out, "Deleting Failure", util.codeWARNING(), "This file is not found : " + deleteFilePath);

			if (f.delete())
				util.returnMessage(out, "File deleted", util.codeINFO(), "Successfully delete : " + deleteFilePath);
			else
				util.returnMessage(out, "Deleting Failure", util.codeWARNING(), "This outpu file couldn't be deleted. : " + deleteFilePath);
		}

		out.close();
	}
}
