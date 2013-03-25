/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ws;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Add Web Service Mapping Secnario class
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: phadkes $
 * @version $Revision: 1.4 $
 * @date $$Date: 2008-06-09 19:54:07 $
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
	      DataInputStream in = null;
	      FileOutputStream fileOut = null;
	      String name, filename, type;
	      long formDatalength = 0;


	      name = filename = type = "EMPTY";
	      try
	      {
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
	    				  Properties caadapterProperties = new Properties();
	    				  path = System.getProperty("gov.nih.nci.caadapter.path");
	    				  System.out.println(path);
	    				  boolean exists = (new File(path + MSName)).exists();
	    				    if (exists) {
	    				    	System.out.println("Scenario exists, overwriting ... ...");
	    				    } else {
	    				    	boolean success = (new File(path + MSName)).mkdir();
	    				        if (!success) {
		    				    	System.out.println("New scenario, Creating ... ...");
	    				        }
	    				    }

	    			  }
	    		  } else {
	    			  System.out.println(item.getFieldName());
	    			  name = item.getFieldName();
	    			  if (name.equals("scsFileName")) {
	    				  File uploadedFile = new File(path + MSName+ "/" + MSName + ".scs");
	    				  item.write(uploadedFile);
	    			  }
	    			  if (name.equals("h3sFileName")) {
	    				  if (item.getName().endsWith("h3s"))
	    				  {
	    					  File uploadedFile = new File(path + MSName+ "/" + MSName + ".h3s");
	    					  item.write(uploadedFile);
	    				  }
	    				  else {
	    					  File uploadedFile = new File(path + MSName+ "/" + MSName + ".xml");
	    					  item.write(uploadedFile);
	    				  }
	    			  }
	    			  if (name.equals("mappingFileName")) {
	    				  File uploadedFile = new File(path + MSName+ "/" + MSName + ".bak");
	    				  item.write(uploadedFile);
	    				  updateMapping(path + MSName+ "/" + MSName + ".bak");
	    			  }
	    		  }
	    	  }
//	    	  out.println("Complete!");
	    	  res.sendRedirect("/caAdapterWS/success.do");
		}catch(Exception e) {
	            System.out.println("Error in doPost: " + e);
		    	  res.sendRedirect("/caAdapterWS/error.do");
	        }
	   }
	    /**
	     * Update the reference in .map to the .scs and .h3s files.
	     *
	     * @param  mapplingFileName  mapping file name
	     */
	    public void updateMapping(String mapplingFileName) throws Exception{
	    	Document xmlDOM = readFile(mapplingFileName);
	    	NodeList components = xmlDOM.getElementsByTagName("component");
	    	for(int i = 0; i< components.getLength();i++) {
	    		Element component = (Element)components.item(i);
	    		Attr attr = component.getAttributeNode("kind");
	    		if (attr.getValue().equals("scs")) {
		    		Attr locationAttr = component.getAttributeNode("location");
		    		locationAttr.setValue(MSName+".scs");
	    		}
	    		if (attr.getValue().equals("HL7v3")||attr.getValue().equals("h3s")) {
		    		Attr locationAttr = component.getAttributeNode("location");
		    		if (locationAttr.getValue().endsWith("h3s"))
		    			locationAttr.setValue(MSName+".h3s");
		    		else
		    			locationAttr.setValue(MSName+".xml");
	    		}
	    	}
	    	outputXML(xmlDOM,path + MSName+ "/" + MSName + ".map");
	    }
	    /**
	     * Parse a XML document into DOM Tree
	     *
	     * @param file File name
	     * @return XML DOM Tree
	     */
		public Document readFile(String fileName) throws Exception {
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
		public Document readFile(File file) throws Exception {
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
		public void outputXML(org.w3c.dom.Document domDoc, String outputFileName)
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
