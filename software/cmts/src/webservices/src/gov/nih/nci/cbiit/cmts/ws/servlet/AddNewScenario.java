/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location:
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.cbiit.cmts.ws.servlet;

import gov.nih.nci.caadapter.security.dao.AbstractSecurityDAO;
import gov.nih.nci.caadapter.security.dao.DAOFactory;
import gov.nih.nci.caadapter.security.dao.SecurityAccessIF;
import gov.nih.nci.caadapter.security.domain.Permissions;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.cbiit.cmts.ws.ScenarioUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.net.URLEncoder;

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

    private String SOURCE_DIRECTORY_TAG = "source";
    private String TARGET_DIRECTORY_TAG = "target";
    private String path;

    private List<String> includedXSDList = null;
    //private List<String> targetXSDList = new ArrayList<String>();
    //private String scenarioHomePath = "";

       /** **********************************************************
	    *  doPost()
	    ************************************************************ */
	   public void doPost (HttpServletRequest req, HttpServletResponse res)
	      throws ServletException, IOException
	   {
           includedXSDList = new ArrayList<String>();
           List<String> fileList = new ArrayList<String>();
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

	    	  // Create a factory for disk-based file items
	    	  DiskFileItemFactory  factory = new DiskFileItemFactory();

	    	  // Create a new file upload handler
	    	  ServletFileUpload upload = new ServletFileUpload(factory);

	    	  // Parse the request
	    	  List <FileItem> /* FileItem */ items =upload.parseRequest(req);

	    	  // Process the uploaded items
	    	  String method=""; // add, update or delete scenario
              String scenarioName = null; //mapping scenario name
              Iterator <FileItem> iter = items.iterator();
	    	  String transType="";

              boolean deletionTag = false;

              List<FileItem> fileItemList = new ArrayList<FileItem>();

              while (iter.hasNext())
              {
	    		  FileItem item = (FileItem) iter.next();

	    		  if (item.isFormField())
                  {
	    			  System.out.println("AddNewScenario.doPost()..item is formField:"+item.getFieldName()+"="+item.getString());
	    			  //if (item.getFieldName().equals("jobType")) jobType = item.getString();

                      if (item.getFieldName().equals("transformationType")) transType = item.getString();
                      if (item.getFieldName().equals("methd"))
                      {
                          method = item.getString();
                          System.out.println("AddNewScenario.doPost()...methodName:"+method);
                      }

                      if (item.getFieldName().equals("scenarioName"))
                      {
	    				  scenarioName = item.getString();

                          if (scenarioName != null)
                          {
                              scenarioName = scenarioName.trim();
                              String scenarioDeleteTag = "##scenariodelete:";

                              if (scenarioName.startsWith(scenarioDeleteTag))
                              {
                                  scenarioName = scenarioName.substring(scenarioDeleteTag.length());
                                  String deletionPassword = "";
                                  String scenarioName2 = scenarioName;
                                  int iddx = scenarioName.indexOf(":");
                                  if (iddx > 0)
                                  {
                                      scenarioName2 = scenarioName.substring(0, iddx).trim();
                                      deletionPassword = scenarioName.substring(iddx + 1).trim();
                                  }
                                  if (deletionPassword.equals("12345A"))
                                  {
                                      deletionTag = true;
                                      scenarioName = scenarioName2;
                                  }
                                  else
                                  {
                                      String errMsg="Failure : Invalid Password for scenario deletion";
                                      System.out.println("AddNewScenario.doPost()...ERROR:"+errMsg);
                                      req.setAttribute("rtnMessage", errMsg);
                                      res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                                      return;
                                  }
                              }
                          }
                      }
                  }
                  else fileItemList.add(item);
              }

              if ((scenarioName == null)||(scenarioName.equals("")))
              {
                  String errMsg="Failure : Scenario Name is Null";
                  System.out.println("AddNewScenario.doPost()...ERROR:"+errMsg);
                  req.setAttribute("rtnMessage", errMsg);
                  res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                  return;
              }
              else
              {
                  scenarioName = scenarioName.trim();
                  path = System.getProperty("gov.nih.nci.cbiit.cmts.path");
                  if (path==null)
                      path=ScenarioUtil.CMTS_SCENARIO_HOME;
                  File scnHome=new File(path);
                  if (!scnHome.exists())
                  {
                      if (!scnHome.mkdir())
                      {
                          String errMsg="Scenario home directory creation filure, not able to save:"+scenarioName;
                          System.out.println("AddNewScenario.doPost()...:"+errMsg);
                          req.setAttribute("rtnMessage", errMsg);
                          res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                          return;
                      }
                  }

                  String scenarioPath=path+File.separator +scenarioName;
                  System.out.println("AddNewScenario.doPost()...scenarioPath:"+scenarioPath);
                  File scenarioDir = null;
                  boolean exists = false;
                  for(File f2:scnHome.listFiles())
                  {
                      if (!f2.isDirectory()) continue;
                      String fName = f2.getName();
                      if (fName.equalsIgnoreCase(scenarioName))
                      {
                          scenarioDir = f2;
                          exists = true;
                          break;
                      }
                  }
                  //boolean exists = scenarioDir.exists();
                  //scenarioHomePath = scenarioPath;
                  if (exists)
                  {
                      if (deletionTag)
                      {
                          boolean deletionSuccess = true;
                          if (deleteScenario(scenarioDir))
                          {
                              try
                              {
                                ScenarioUtil.deleteOneScenarioRegistration(scenarioName);
                              }
                              catch(Exception e1)
                              {
                                 deletionSuccess = false;
                              }
                          }
                          else deletionSuccess = false;

                          if (deletionSuccess) res.sendRedirect("successmsg.do?message="+ URLEncoder.encode("Scenario Deletion Success : " + scenarioName, "UTF-8"));
                          else
                          {
                                String errMsg="Scenario deleting failure:"+scenarioName;
                                System.out.println("AddNewScenario.doPost()...:"+errMsg);
                                req.setAttribute("rtnMessage", errMsg);
                                res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                          }
                          return;
                      }
                      else
                      {
                        String errMsg="Scenario exists, not able to save:"+scenarioName;
                        System.out.println("AddNewScenario.doPost()...:"+errMsg);
                        req.setAttribute("rtnMessage", errMsg);
                        res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                        return;
                      }
                  } else {

                      if (deletionTag)
                      {
                          String errMsg="This Scenario is NOT exists for deleting:"+scenarioName;
                          System.out.println("AddNewScenario.doPost()...:"+errMsg);
                          req.setAttribute("rtnMessage", errMsg);
                          res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                          return;
                      }
                        boolean success = (new File(scenarioPath)).mkdir();
                        if (!success)
                        {
                            String errMsg="Faild to create scenario:"+scenarioName;
                            System.out.println("AddNewScenario.doPost()...:"+errMsg);
                            req.setAttribute("rtnMessage", errMsg);
                            res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                            return;
                        }
                        fileList.add(scenarioPath);

                        String sourcePath = scenarioPath+File.separator+SOURCE_DIRECTORY_TAG;
                        success = (new File(sourcePath)).mkdir();
                        if (!success)
                        {
                            String errMsg="Faild to create source schema folder:"+scenarioName;
                            System.out.println("AddNewScenario.doPost()...:"+errMsg);
                            req.setAttribute("rtnMessage", errMsg);
                            deleteDirAndFilesOnError(fileList);
                            res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                            return;
                        }
                        fileList.add(sourcePath);
                        String targetPath = scenarioPath+File.separator+TARGET_DIRECTORY_TAG;
                        success = (new File(targetPath)).mkdir();
                        if (!success)
                        {
                            String errMsg="Faild to create target schema folder:"+scenarioName;
                            System.out.println("AddNewScenario.doPost()...:"+errMsg);
                            req.setAttribute("rtnMessage", errMsg);
                            deleteDirAndFilesOnError(fileList);
                            res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                            return;
                        }
                        fileList.add(targetPath);
                  }
	    	  }

              boolean isSourceZip = false;
              boolean isTargetZip = false;
              if (fileItemList.size() > 0)
              {
                  for(FileItem item:fileItemList)
                  {
	    			  String fieldName = item.getFieldName();
	    			  System.out.println("AddNewScenario.doPost()..item is NOT formField:"+item.getFieldName());//+"="+item.getString());
	    			  String filePath = item.getName();
	    			  String fileName=extractOriginalFileName(filePath);
	    			  System.out.println("AddNewScenario.doPost()..original file Name:"+fileName + ", Path="+filePath);
                      //System.out.println("                       ..original file Path:"+filePath);
                      if (fileName==null||fileName.equals(""))
	    				  continue;
	    			  String uploadedFilePath=path+File.separator+scenarioName+File.separator+ fileName.toLowerCase();
	    			  if (fieldName.equals("mappingFileName")) {
	    				  System.out.println("AddNewScenario.doPost()...mapping file:"+uploadedFilePath);
	    				  String uploadedMapBak=uploadedFilePath+ ".bak";
	    				  //write bak of Mapping file
	    				  item.write(new File(uploadedMapBak));
	    				  if (uploadedFilePath.toLowerCase().endsWith(".map"))
                          {
                              updateMapping(uploadedMapBak, path+File.separator+scenarioName);
                              fileList.add(uploadedMapBak);
                              fileList.add(uploadedFilePath);
                          }
                          else //xslt and xq
                          {
                              item.write(new File(uploadedFilePath));
                              fileList.add(uploadedFilePath);
                          }
                      }
                      //else if (fieldName.equals("sourceXsdName"))
                      else
                      {
                          String sourceORtarget = null;
                          if (fieldName.startsWith("sourceXsdName")) sourceORtarget = SOURCE_DIRECTORY_TAG;
                          else if (fieldName.startsWith("targetXsdName")) sourceORtarget = TARGET_DIRECTORY_TAG;

                          if (sourceORtarget != null)
                          {
                              if (fileName.toLowerCase().endsWith(".xsd"))
                              {
                                  uploadedFilePath=path+File.separator+scenarioName+File.separator+sourceORtarget+File.separator+fileName;
                                  //File file = new File(uploadedFilePath);
                                  //if ((file.exists())&&(file.isFile()))
                                  //{
                                  //
                                  //}
                                  //else
                                  //{
                                      System.out.println("AddNewScenario.doPost().."+sourceORtarget+" schema file:"+uploadedFilePath);
                                      item.write(new File(uploadedFilePath));
                                      fileList.add(uploadedFilePath);
                                      String bakupFileName = uploadedFilePath + ".bak";
                                      if (replaceXSDFile(uploadedFilePath, bakupFileName, sourceORtarget))
                                      {
                                          fileList.add(bakupFileName);

                                      }
                                      else
                                      {
                                          String errMsg="Faild to upload this "+sourceORtarget+" schema file:"+fileName;
                                          System.out.println("AddNewScenario.doPost()...ERROR:"+errMsg);
                                          req.setAttribute("rtnMessage", errMsg);
                                          deleteDirAndFilesOnError(fileList);
                                          res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                                          return;
                                      }
                                  //}
                              }
                              else if ((fileName.toLowerCase().endsWith(".zip"))||(fileName.toLowerCase().endsWith(".jar")))
                              {
                                  uploadedFilePath=path+File.separator+scenarioName+File.separator+sourceORtarget + fileName.substring(fileName.length()-4, fileName.length());//sourceORtarget+File.separator+fileName;
                                  //File file = new File(uploadedFilePath);
                                  //if ((file.exists())&&(file.isFile()))
                                  //{
                                  //
                                  //}
                                  //else
                                  //{
                                      System.out.println("AddNewScenario.doPost().."+sourceORtarget+" schema file:"+uploadedFilePath);
                                      item.write(new File(uploadedFilePath));
                                      fileList.add(uploadedFilePath);
                                      if (sourceORtarget.equals(SOURCE_DIRECTORY_TAG))
                                      {
                                          if (isSourceZip)
                                          {
                                              String errMsg="Only one zip file allowed ("+sourceORtarget+") :"+fileName;
                                              System.out.println("AddNewScenario.doPost()...ERROR:"+errMsg);
                                              req.setAttribute("rtnMessage", errMsg);
                                              deleteDirAndFilesOnError(fileList);
                                              res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                                              return;
                                          }
                                          else isSourceZip = true;
                                      }
                                      else
                                      {
                                          if (isTargetZip)
                                          {
                                              String errMsg="Only one zip file allowed ("+sourceORtarget+") :"+fileName;
                                              System.out.println("AddNewScenario.doPost()...ERROR:"+errMsg);
                                              req.setAttribute("rtnMessage", errMsg);
                                              deleteDirAndFilesOnError(fileList);
                                              res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                                              return;
                                          }
                                          else isTargetZip = true;
                                      }
                                  //}
                              }
                              //else
                              //{
                              //
                              //}
                          }
                      }
                  }
	    	  }

              List<String> notFoundfiles = new ArrayList<String>();
              for(String pathXSD:includedXSDList)
              {
                  String xsdPath = path+File.separator+scenarioName+File.separator+ pathXSD;
                  File ff = new File(xsdPath);
                  if ((!ff.exists())||(!ff.isFile()))
                  {
                      if (isSourceZip)
                      {
                          if (pathXSD.startsWith(SOURCE_DIRECTORY_TAG + File.separator)) {}
                          else notFoundfiles.add(pathXSD);
                      }
                      else if (isTargetZip)
                      {
                          if (pathXSD.startsWith(TARGET_DIRECTORY_TAG + File.separator)) {}
                          else notFoundfiles.add(pathXSD);
                      }
                      else notFoundfiles.add(pathXSD);
                  }
                  else
                  {
//                      String sourceORtarget = null;
//                      if (isSourceZip)
//                      {
//                          if (pathXSD.startsWith(SOURCE_DIRECTORY_TAG + File.separator)) sourceORtarget = SOURCE_DIRECTORY_TAG;
//                      }
//                      if (isTargetZip)
//                      {
//                          if (pathXSD.startsWith(TARGET_DIRECTORY_TAG + File.separator)) sourceORtarget = TARGET_DIRECTORY_TAG;
//                      }
//                      if (sourceORtarget != null)
//                      {
//                          String errMsg=sourceORtarget + " is ZIP file";
//                          System.out.println("AddNewScenario.doPost()...ERROR:"+errMsg);
//                          req.setAttribute("rtnMessage", errMsg);
//                          deleteDirAndFilesOnError(fileList);
//                          res.sendRedirect("errormsg.do");
//                          return;
//                      }
                  }
              }

              if (notFoundfiles.size() > 0)
              {
                  String errMsg="Incomplete XSD files. " + notFoundfiles.size() + " files are absent. - ";
                  if (notFoundfiles.size() == 1) errMsg="Incomplete XSD files. One file is absent. - ";
                  for (String c:notFoundfiles) errMsg = errMsg + "<br>" + c;
                  System.out.println("AddNewScenario.doPost()...ERROR:"+errMsg);

                  req.setAttribute("rtnMessage", errMsg);
                  deleteDirAndFilesOnError(fileList);
                  res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                  return;
              }


              ScenarioUtil.addNewScenarioRegistration(scenarioName, transType);
	    	  res.sendRedirect("successmsg.do");

		}catch(NullPointerException ne) {
	            System.out.println("NullPointerException in doPost: " + ne);
              ne.printStackTrace();
                req.setAttribute("rtnMessage", ne.getMessage());
                deleteDirAndFilesOnError(fileList);
                res.sendRedirect("errormsg.do"+ "?message=" + URLEncoder.encode(ne.getMessage(), "UTF-8"));
	        }
		catch(Exception e) {
            System.out.println("Error in doPost: " + e);
            e.printStackTrace();
            req.setAttribute("rtnMessage", e.getMessage());
            deleteDirAndFilesOnError(fileList);
            res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
	   }
       private void deleteDirAndFilesOnError(List<String> fileList)
	   {

           if (fileList.size() == 0) return;
           for(int i=1;i<=fileList.size();i++)
           {
               String path = fileList.get(fileList.size() - i);
               File file = new File(path);
               file.delete();
           }

       }
	   private String extractOriginalFileName(String filePath)
	   {
		   if (filePath==null||filePath.trim().equals(""))
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
	     * @param  mapplingBakFileName  mapping file name
	     */
	    public void updateMapping(String mapplingBakFileName, String fileHome) throws Exception{
	    	
	    	Document xmlDOM = readFile(mapplingBakFileName);
	    	NodeList components = xmlDOM.getElementsByTagName("component");
	    	for(int i = 0; i< components.getLength();i++) {
	    		Element component = (Element)components.item(i);
	    		//update location of SCS, H3S
	    		Attr locationAttr = component.getAttributeNode("location");
	    		Attr typeAttr = component.getAttributeNode("type");
	    	
	    		if (locationAttr!=null)
	    		{
	    			String cmpType="";
	    			if (typeAttr!=null)
	    				cmpType=typeAttr.getValue();
	    			if (cmpType!=null&&cmpType.equalsIgnoreCase("v2"))
	    				continue;
			    	String localName=extractOriginalFileName(locationAttr.getValue());
//			    	locationAttr.setValue(fileHome+File.separator+cmpType+File.separator+localName);
			    	locationAttr.setValue(cmpType+File.separator+localName.toLowerCase());
                    includedXSDList.add(cmpType+File.separator+localName.toLowerCase());
                    System.out.println("CCCCC updateMapping Add file list : " + cmpType+File.separator+localName.toLowerCase());
                    //locationAttr.setValue(cmpType+File.separator+localName);
                }
//	    		//update VOM reference
//	    		Attr groupAttr = component.getAttributeNode("group");
//	    		if (groupAttr!=null
//	    				&&groupAttr.getValue()!=null
//	    				&&groupAttr.getValue().equalsIgnoreCase("vocabulary"))
//	    		{
//	    			NodeList chldComps = component.getElementsByTagName("data");
//	    			for(int j=0;j<chldComps.getLength();j++)
//	    			{
//	    				Element chldElement = (Element)chldComps.item(j);
//	    				Attr valueAttr=chldElement.getAttributeNode("value");
//	    				if (valueAttr!=null)
//	    				{
//	    					String localFileName=extractOriginalFileName(valueAttr.getValue());
//	    					valueAttr.setValue(localFileName);
//	    				}
//	    			}
//	    		}
	    	}
	    	System.out.println("AddNewScenario.updateMapping()..mapbakfile:"+mapplingBakFileName);

	    	outputXML(xmlDOM,mapplingBakFileName.substring(0, mapplingBakFileName.lastIndexOf(".bak"))  );
	    }
        /**
	     * replace XSD file - change the XSD path value of include element
	     *
	     * @param fileName XSD File name
	     *
	     */
		private boolean replaceXSDFile(String fileName, String backupFileName, String sourceORtarget)
        {
            List<String> xsdList = null;
            try
            {
                xsdList = FileUtil.readFileIntoList(fileName);
            }
            catch(IOException ie)
            {
                return false;
            }

            List<String> xsdList2 = new ArrayList<String>();

            String locationAttr = "schemaLocation=\"";
            for (int i=0;i<xsdList.size();i++)
            {
                String xsd = xsdList.get(i);
                String xsd2 = "";
                while(true)
                {
                    int idx1 = xsd.indexOf(locationAttr);
                    if (idx1 < 0)
                    {
                        xsd2 = xsd2 + xsd;
                        break;
                    }
                    String sub = xsd.substring(0, idx1 + locationAttr.length());
                    xsd2 = xsd2 + sub;
                    xsd = xsd.substring(idx1 + locationAttr.length());
                    int idx2 = xsd.indexOf("\"");
                    if (idx2 < 0) return false;
                    String path = xsd.substring(0, idx2);
                    xsd = xsd.substring(idx2);
                    while(true)
                    {
                        int idx3 = path.indexOf("/");
                        if (idx3 < 0) idx3 = path.indexOf("\\");
                        if (idx3 < 0) break;

                        path = path.substring(idx3 + 1);
                    }
                    xsd2 = xsd2 + path.toLowerCase();
                    includedXSDList.add(sourceORtarget + File.separator + path.toLowerCase());
                    System.out.println("CCCCC replaceXSDFile : " + sourceORtarget + File.separator + path.toLowerCase());
                    //xsd2 = xsd2 + path;
                }
                xsdList2.add(xsd2);
            }
            File file1 = new File(fileName);
            File file2 = new File(backupFileName);
            file1.renameTo(file2);

            FileWriter fw = null;

            try
            {
                fw = new FileWriter(fileName);
                for (int i=0;i<xsdList2.size();i++)
                {
                    String xsd = xsdList2.get(i);
                    fw.write(xsd + "\n");
                }
                fw.close();
             }
            catch(Exception ie)
            {
                return false;
            }

            return true;
        }
        /**
	     * Parse a XML document into DOM Tree
	     *
	     * @param fileName File name
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
    private boolean deleteScenario(File scenarioDir)
    {
        if (scenarioDir.isFile()) return scenarioDir.delete();

        File[] fileL = scenarioDir.listFiles();
        for(File f:fileL)
        {
            if (!deleteScenario(f)) return false;
        }
        return scenarioDir.delete();
    }
}
