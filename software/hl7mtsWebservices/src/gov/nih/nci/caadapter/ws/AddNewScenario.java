/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ws;

import gov.nih.nci.caadapter.security.dao.AbstractSecurityDAO;
import gov.nih.nci.caadapter.security.dao.DAOFactory;
import gov.nih.nci.caadapter.security.dao.SecurityAccessIF;
import gov.nih.nci.caadapter.security.domain.Permissions;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Iterator;
import java.util.List;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Add Web Service Mapping Secnario class
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.7 $
 * @date $$Date: 2009-04-14 20:02:40 $
 * @since caadapter v1.3.1
 */

public class AddNewScenario extends HttpServlet {
    private String MSName;
    private String scsFileName;
    private String h3sFileName;
    private String mappingFileName;
    private String path;

	   /** **********************************************************
	    *  doPost()
	    ************************************************************ */
	   public void doPost (HttpServletRequest req, HttpServletResponse res)
	      throws ServletException, IOException
	   {
          try
          {
            /* disable security for caAdatper 4.3 release 03-31-2009
             HttpSession session = req.getSession(false);

              if(session==null)
              {
                  res.sendRedirect("/caAdapterWS/login.do");
                  return;
              }

              String user = (String) session.getAttribute("userid");
              System.out.println(user);
              AbstractSecurityDAO abstractDao= DAOFactory.getDAO();
              SecurityAccessIF getSecurityAccess = abstractDao.getSecurityAccess();
              Permissions perm = getSecurityAccess.getUserObjectPermssions(user, 1);
              System.out.println(perm);
              if (!perm.getCreate()){
                  System.out.println("No create Permission for user" + user);
                  res.sendRedirect("/caAdapterWS/permissionmsg.do");
                  return;
              }
              */
        	  String name = "EMPTY";

	    	  // Create a factory for disk-based file items
	    	  DiskFileItemFactory  factory = new DiskFileItemFactory();

	    	  // Create a new file upload handler
	    	  ServletFileUpload upload = new ServletFileUpload(factory);

	    	  // Parse the request
	    	  List /* FileItem */ items = upload.parseRequest(req);

	    	  // Process the uploaded items
	    	  Iterator iter = items.iterator();
	    	  while (iter.hasNext()) {
	    		  FileItem item = (FileItem) iter.next();

	    		  if (item.isFormField()) {
	    			  if (item.getFieldName().equals("MSName")) {
	    				  MSName = item.getString();
	    				  path = System.getProperty("gov.nih.nci.caadapter.path");
	    				  if (path==null)
	    					  path=ScenarioUtil.SCENARIO_HOME;
	    				  File scnHome=new File(path);
	    				  if (!scnHome.exists())
	    					  scnHome.mkdir();
	    				  path=path+"/";
	    				  System.out.println(path);
	    				  boolean exists = (new File(path+ MSName)).exists();
	    				    if (exists) {
	    				    	System.out.println("Scenario exists, overwriting ... ...");
	    				    	String errMsg="Scenario exists, not able to save:"+MSName;
	    				    	req.setAttribute("rtnMessage", errMsg);
	    				    	 res.sendRedirect("/caAdapterWS/errormsg.do");
	    				    	 return;
	    				    } else {
	    				    	boolean success = (new File(path+MSName)).mkdir();
	    				        if (!success) {
		    				    	System.out.println("New scenario, Creating ... ...");
	    				        }
	    				    }

	    			  }
	    		  } else {
	    			  System.out.println(item.getFieldName());
	    			  name = item.getFieldName();

	    			  String filePath = item.getName();
	    			  String fileName=extractOriginalFileName(filePath);
	    			  System.out.println("AddNewScenario.doPost()..original file Name:"+fileName);
	    			  if (fileName==null||fileName.equals(""))
	    				  continue;
	    			  String uploadedFilePath=path+MSName+"/"+ fileName;
	    			  System.out.println("AddNewScenario.doPost()...write data to file:"+uploadedFilePath);
	    			  File uploadedFile = new File(uploadedFilePath);
	    			  if (name.equals("mappingFileName")) {
	    				  String uploadedMapBak=uploadedFilePath+ ".bak";
	    				  //write bak of Mapping file
	    				  item.write(new File(uploadedMapBak));
	    				  updateMapping(uploadedMapBak);
	    			  }
	    			  else
	    				  item.write(uploadedFile);
	    		  }
	    	  }
	    	  ScenarioUtil.addNewScenarioRegistration(MSName);
	    	  res.sendRedirect("/caAdapterWS/success.do");

		}catch(NullPointerException ne) {
	            System.out.println("Error in doPost: " + ne);
	            req.setAttribute("rtnMessage", ne.getMessage());
		    	  res.sendRedirect("/caAdapterWS/errormsg.do");
	        }
		catch(Exception e) {
            System.out.println("Error in doPost: " + e);
            req.setAttribute("rtnMessage", e.getMessage());
              res.sendRedirect("/caAdapterWS/error.do");
        }
	   }

	   private String extractOriginalFileName(String filePath)
	   {
		   if (filePath==null||filePath.equalsIgnoreCase(""))
			   return null;
		   int subIndx=0;
		   if (filePath.lastIndexOf("/")>-1)
			   subIndx=filePath.lastIndexOf("/");
		   else if (filePath.lastIndexOf("\\")>-1)
			   subIndx=filePath.lastIndexOf("\\");

		   if (subIndx>0)
			   return filePath.substring(subIndx+1);

		   //the original string is file name
		   return filePath;

	   }
	    /**
	     * Update the reference in .map to the .scs and .h3s files.
	     *
	     * @param  mapplingFileName  mapping file name
	     */
	    public void updateMapping(String mapplingBakFileName) throws Exception{
	    	Document xmlDOM = readFile(mapplingBakFileName);

	    	NodeList components = xmlDOM.getElementsByTagName("component");
	    	for(int i = 0; i< components.getLength();i++) {
	    		Element component = (Element)components.item(i);
	    		//update location of SCS, H3S
	    		Attr locationAttr = component.getAttributeNode("location");
	    		Attr kindAttr = component.getAttributeNode("kind");
	    		if (locationAttr!=null)
	    		{
	    			String cmpKind="";
	    			if (kindAttr!=null)
	    				cmpKind=kindAttr.getValue();
	    			if (cmpKind!=null&&cmpKind.equalsIgnoreCase("v2"))
	    				continue;
			    	String localName=extractOriginalFileName(locationAttr.getValue());
			    	locationAttr.setValue(localName);
	    		}
	    		//update VOM reference
	    		Attr groupAttr = component.getAttributeNode("group");
	    		if (groupAttr!=null
	    				&&groupAttr.getValue()!=null
	    				&&groupAttr.getValue().equalsIgnoreCase("vocabulary"))
	    		{
	    			NodeList chldComps = component.getElementsByTagName("data");
	    			for(int j=0;j<chldComps.getLength();j++)
	    			{
	    				Element chldElement = (Element)chldComps.item(j);
	    				Attr valueAttr=chldElement.getAttributeNode("value");
	    				if (valueAttr!=null)
	    				{
	    					String localFileName=extractOriginalFileName(valueAttr.getValue());
	    					valueAttr.setValue(localFileName);
	    				}
	    			}
	    		}
	    	}
	    	System.out.println("AddNewScenario.updateMapping()..mapbakfile:"+mapplingBakFileName);

	    	outputXML(xmlDOM,mapplingBakFileName.substring(0, mapplingBakFileName.lastIndexOf(".bak"))  );
	    }
	    /**
	     * Parse a XML document into DOM Tree
	     *
	     * @param file File name
	     * @return XML DOM Tree
	     */
		private Document readFile(String fileName) throws Exception {
	        if (fileName == null) {
	                throw new Exception("Wrong filename for readFile()");
	        }
	        return readFile(new File(fileName));
		}
	    /**
	     * Parse a XML document into DOM Tree
	     *
	     * @param file The input File handler
	     * @return XML DOM Tree
	     */
		private Document readFile(File file) throws Exception {
		    org.w3c.dom.Document doc;

	        try {
	                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	                //dbf.setValidating(true);
	                DocumentBuilder db = dbf.newDocumentBuilder();

	                doc = db.parse(file);

	                return doc;
	        }
	        catch(SAXParseException ex) {
	                throw(ex);
	        }
	        catch(SAXException ex) {
	                Exception x = ex.getException();
	                throw ((x==null) ? ex : x);
	        }
	    }
	    /**
	     * Save the modified .map file.
	     *
	     * @param domDoc .map file's dom tree
	     * @param outputFileName the target .map file you want save
	     */
		private void outputXML(Document domDoc, String outputFileName)
	       throws JDOMException, IOException {
	       // Create new DOMBuilder, using default parser
	       DOMBuilder builder = new DOMBuilder();
	       org.jdom.Document jdomDoc = builder.build(domDoc);

	       XMLOutputter outputter = new XMLOutputter();
	       File file = new File(outputFileName);
	       FileWriter writer = new FileWriter(file);
	       outputter.output(jdomDoc,writer);
	       writer.close();
	   }
}
