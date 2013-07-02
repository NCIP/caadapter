package gov.nih.nci.cbiit.cmts.web;

//import org.apache.commons.fileupload.disk.DiskFileItemFactory;
//import org.apache.commons.fileupload.servlet.ServletFileUpload;
//import org.apache.commons.fileupload.FileItem;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Enumeration;
import java.io.IOException;
//import java.io.File;
//import java.io.FileWriter;
import java.io.PrintWriter;
//import java.net.URLEncoder;
import java.text.SimpleDateFormat;

//import gov.nih.nci.cbiit.cmts.ws.ScenarioUtil;
//import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
//import gov.nih.nci.cbiit.cmts.mapping.MappingFactory;
import gov.nih.nci.cbiit.cmts.function.DateFunction;
import gov.nih.nci.cbiit.cmts.function.FunctionException;
//import gov.nih.nci.caadapter.common.util.FileUtil;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Aug 17, 2012
 * Time: 11:24:43 AM
 * To change this template use File | Settings | File Templates.
 */

public class WebFunctionService extends HttpServlet {


    public void doPost (HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException
    {

        //res.setContentType("text/html");
		PrintWriter out = res.getWriter();
        try
        {
            String serviceItem = null;
            String val01 = null;
            String val02 = null;
            String val03 = null;
            String val04 = null;

            Enumeration en = req.getParameterNames();

			String formFieldName = "";
			String itemValue = "";
			//int idxP = 0;
            boolean hasSystemCharacter = false;
            while(en.hasMoreElements())
			{
				formFieldName = (String) en.nextElement();
				if (formFieldName == null) continue;
				formFieldName = formFieldName.trim();
				if (formFieldName.equals("")) continue;
				itemValue = req.getParameter(formFieldName);
				if (itemValue == null) itemValue = "";
                //else itemValue = itemValue.trim();

//                if (paramName.equalsIgnoreCase("returntype")) idxP = 0;
//				else if (paramName.equalsIgnoreCase("user")) idxP = 1;
//				else if (paramName.equalsIgnoreCase("date")) idxP = 2;
//				else if (paramName.equalsIgnoreCase("pass")) idxP = 3;
//				else if (paramName.equalsIgnoreCase("scenario")) idxP = 4;
//				else if (paramName.equalsIgnoreCase("compressed")) idxP = 5;
//				else if (paramName.equalsIgnoreCase("csvString")) idxP = 6;
//				else if (paramName.equalsIgnoreCase("encoded")) idxP = 7;
//				else if (paramName.equalsIgnoreCase("datatypeincluded")) idxP = 8;
//				else if (paramName.equalsIgnoreCase("headlowercase")) idxP = 9;
//				else if (paramName.equalsIgnoreCase("namespace")) idxP = 10;

                if ((formFieldName.toLowerCase().equals("serviceitem"))||
                        (formFieldName.toLowerCase().equals("functionitem"))||
                        (formFieldName.toLowerCase().equals("function"))||
                        (formFieldName.toLowerCase().equals("functionname")))
                    {
                        serviceItem = itemValue.trim();
                        if (serviceItem.equals("")) serviceItem = null;
                    }
                    if ((formFieldName.toLowerCase().equals("val01"))||
                        (formFieldName.toLowerCase().equals("val1"))||
                        (formFieldName.toLowerCase().equals("input1"))||
                        (formFieldName.toLowerCase().equals("input01")))
                    {
                        val01 = itemValue.trim();
                        if (val01.equals("")) val01 = null;
                    }
                    if ((formFieldName.toLowerCase().equals("val02"))||
                        (formFieldName.toLowerCase().equals("val2"))||
                        (formFieldName.toLowerCase().equals("input2"))||
                        (formFieldName.toLowerCase().equals("input02")))
                    {
                        val02 = itemValue.trim();
                        if (val02.equals("")) val02 = null;
                    }
                    if ((formFieldName.toLowerCase().equals("val03"))||
                        (formFieldName.toLowerCase().equals("val3"))||
                        (formFieldName.toLowerCase().equals("input3"))||
                        (formFieldName.toLowerCase().equals("input03")))
                    {
                        val03 = itemValue.trim();
                        if (val03.equals("")) val03 = null;
                    }
                    if ((formFieldName.toLowerCase().equals("val04"))||
                        (formFieldName.toLowerCase().equals("val4"))||
                        (formFieldName.toLowerCase().equals("input4"))||
                        (formFieldName.toLowerCase().equals("input04")))
                    {
                        val04 = itemValue.trim();
                        if (val04.equals("")) val04 = null;
                    }
            }

            // Create a factory for disk-based file items
            //DiskFileItemFactory  factory = new DiskFileItemFactory();

            // Create a new file upload handler
            //ServletFileUpload upload = new ServletFileUpload(factory);

            // Parse the request
            //List <FileItem> items =upload.parseRequest(req);

            // Process the uploaded items

            //Iterator<FileItem> iter = items.iterator();

            //List<FileItem> fileItemList = new ArrayList<FileItem>();
            //req.
            //while (iter.hasNext())
            //{   ==
                //FileItem item = (FileItem) iter.next();

                //if (item.isFormField())
                //{ ==
                    //String formFieldName = item.getFieldName();
                    //String itemValue = item.getString();


                //}
                //else fileItemList.add(item);
            //}

            if (serviceItem == null)
            {
                sendResponseAsError("No service item is found.", out);
                return;
            }

            if ((serviceItem.equalsIgnoreCase("countDays"))||(serviceItem.equalsIgnoreCase("countDay")))
            {
                sendResponse(calculatCountDays(val01, val02), out, false);
            }
            else sendResponseAsError("Invalid service or function name: " + serviceItem, out);

        }
        catch(Exception e)
        {
            e.printStackTrace();
            sendResponseAsError(e.getMessage(), out);
        }
    }
    public void doGet (HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException
    {
        doPost (req, res);
    }

    private void sendResponse(String message, PrintWriter out, boolean error)
    {
        String ele = "    <result>"+message+"</result>\r\n";
        if (error) ele = "    <error>"+message+"</error>\r\n";
        String h = "";
        h = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
            "<caAdapterResponse>\r\n" + ele + "</caAdapterResponse>\r\n";
        out.println(h);
    }
    private void sendResponseAsError(String message, PrintWriter out)
    {
        sendResponse(message, out, true);
    }

    private String calculatCountDays(String v1, String v2) throws Exception
    {
        java.util.Date d1 = decodeDate(v1);
        java.util.Date d2 = decodeDate(v2);

        DateFunction df = new DateFunction();
        return df.getDaysBetweenDates(d1, d2);
    }

    private java.util.Date decodeDate(String d) throws Exception
    {
        if ((d == null)||(d.trim().equals(""))) throw new Exception("input data is null.");
        d = d.trim();

        //if (d)

        DateFunction df = new DateFunction();

        int n = 0;
        int m = 0;
        String t = "";
        String temp = "";
        while(true)
        {
            temp = "";
            if (n == 0) temp = "yyyyMMdd";
            //else if (n == 1) temp = "yyMMdd";
            //else if (n == 2) temp = "yyddMM";
            //else if (n == 3) temp = "MMddyyyy";
            //else if (n == 4) temp = "MMddyy";
            else if (n == 1) temp = "yyyy?MM?dd";
            else if (n == 2) temp = "yyyy?MMM?dd";
            else if (n == 3) temp = "MMM?dd?yyyy";
            else if (n == 4) temp = "yy?MMM?dd";
            else if (n == 5) temp = "MMM?dd?yy";
            else if (n == 6) temp = "MM?dd?yyyy";
            else if (n == 7) temp = "MM?dd?yy";
            else if (n == 8) temp = "yy?MM?dd";

            else break;
            n++;
            m = 0;
            if (d.length() < temp.length()) continue;

            int idx = temp.indexOf("?");
            while(true)
            {
                String temp2 = "";
                if (idx < 0)
                {
                    try
                    {
                        Long.parseLong(d);
                    }
                    catch(NumberFormatException ne)
                    {
                        break;
                    }
                    temp2 = temp;
                }
                else
                {
                    if (m == 0) t = "/";
                    else if (m == 1) t = "-";
                    else if (m == 2) t = ".";
                    else if (m == 3) t = "|";
                    else if (m == 4) t = "\\";
                    else if (m == 5) t = "_";
                    else break;
                    m++;
                    if (d.indexOf(t) < 0) continue;

                    temp2 = "";
                    boolean tt = true;
                    for(int i=0;i<temp.length();i++)
                    {
                        String s1 = temp.substring(i, i+1);
                        String s2 = d.substring(i,i+1);
                        if (s1.equals("?"))
                        {
                            if (!s2.equals(t)) tt = false;
                            temp2 = temp2 + t;
                        }
                        else temp2 = temp2 + s1;
                    }
                    if (!tt) break;
                    //while(temp2.indexOf("?") > 0)
                    //    temp2 = temp2.replace("?", t);
                }


                String tempD = d;
                if (d.length() > temp2.length()) tempD = d.substring(0, temp2.length());

                java.util.Date date = null;
                try
                {
                    date = df.parseDateFromString(new SimpleDateFormat(temp2), tempD);
                }
                catch(FunctionException fe)
                {
                    date = null;
                }
                if (date != null)
                {
                    //System.out.println("TTT given=" + d + ", parsedDate=" + date + ", format=" + temp2 + ", data=" + tempD);
                    return date;
                }
                if (idx > 0) break;
            }
        }
        throw new Exception("Invalid Date format : " + d);
    }
}