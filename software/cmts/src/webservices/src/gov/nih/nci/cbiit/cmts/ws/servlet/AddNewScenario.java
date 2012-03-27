/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location:
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.cbiit.cmts.ws.servlet;

//import gov.nih.nci.caadapter.security.dao.AbstractSecurityDAO;
//import gov.nih.nci.caadapter.security.dao.DAOFactory;
//import gov.nih.nci.caadapter.security.dao.SecurityAccessIF;
//import gov.nih.nci.caadapter.security.domain.Permissions;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.hl7.v2v3.tools.ZipUtil;
import gov.nih.nci.cbiit.cmts.ws.ScenarioUtil;
//import gov.nih.nci.cbiit.cmts.common.XSDParser;
import gov.nih.nci.cbiit.cmts.mapping.MappingFactory;
import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
//import gov.nih.nci.cbiit.cmts.util.ZipFileUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.net.URLEncoder;
import java.net.URL;

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
    private boolean isDoGet = false;

    private String sourceOriginalXSDPath = null;
    private String targetOriginalXSDPath = null;
    private String mappingFileName = null;

    private List<String> includedXSDList = null;
    private List<String> tempXSDList = null;
    private List<String> alreadyFoundXSDURLList = null;
    //private List<String> targetXSDList = new ArrayList<String>();
    //private String scenarioHomePath = "";

       /** **********************************************************
	    *  doPost()
	    ************************************************************ */
	   public void doPost (HttpServletRequest req, HttpServletResponse res)
	      throws ServletException, IOException
	   {
           path = null;

           sourceOriginalXSDPath = null;
           targetOriginalXSDPath = null;
           mappingFileName = null;

           tempXSDList = null;
           alreadyFoundXSDURLList = null;

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
              String securityCode = null;

              String deletionTag = null;

              List<FileItem> fileItemList = new ArrayList<FileItem>();

              while (iter.hasNext())
              {
	    		  FileItem item = (FileItem) iter.next();

	    		  if (item.isFormField())
                  {
                      String formFieldName = item.getFieldName();
                      String itemValue = item.getString();
                      System.out.println("AddNewScenario.doPost()..item is formField:"+formFieldName+"="+itemValue);
	    			  //if (item.getFieldName().equals("jobType")) jobType = item.getString();

                      if (formFieldName.equals("transformationType"))
                      {
                          transType = itemValue.trim().toLowerCase();
                          if (transType.startsWith("xq")) transType = "xq";
                          if (transType.startsWith("xsl")) transType = "xsl";
                      }
                      if (formFieldName.equals("methd"))
                      {
                          method = itemValue;
                          //System.out.println("AddNewScenario.doPost()..."+formFieldName+":"+method);
                      }
                      if (formFieldName.equals("deleteSecurityCode"))
                      {
                          securityCode = itemValue;
                          ///System.out.println("AddNewScenario.doPost()..."+formFieldName+":"+method);
                      }

                      if (formFieldName.equals("scenarioName"))
                      {
	    				  scenarioName = itemValue;

                          if ((scenarioName != null)&&(!scenarioName.trim().equals("")))
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

                                  if (!deletionPassword.trim().equals(""))
                                  {
                                      deletionTag = deletionPassword;
                                      scenarioName = scenarioName2;
                                  }
                                  else
                                  {
                                      String errMsg="Failure : Null Password for scenario deletion";
                                      System.out.println("AddNewScenario.doPost()...ERROR:"+errMsg);
                                      req.setAttribute("rtnMessage", errMsg);
                                      res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                                      return;
                                  }
                              }
                          }
                          else
                          {

                          }
                      }
                  }
                  else fileItemList.add(item);
              }

              if ((deletionTag != null)||(method.equalsIgnoreCase("deleteScenario")))
              {
                  String errMsg="Sorry.. Delete scenario is currently not in service.";
                  System.out.println("AddNewScenario.doPost()...ERROR:"+errMsg);
                  req.setAttribute("rtnMessage", errMsg);
                  res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                  return;
              }

              if ((scenarioName == null)||(scenarioName.trim().equals("")))
              {
                  String errMsg="Failure : Scenario Name is Null";
                  System.out.println("AddNewScenario.doPost()...ERROR:"+errMsg);
                  req.setAttribute("rtnMessage", errMsg);
                  res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                  return;
              }
              scenarioName = scenarioName.trim();


                for (char c:scenarioName.toCharArray())
                {
                    byte b = (byte) c;
                    int i = (int) b;
                    String bl = null;
                    if ((i >= 97)&&(i <= 122)) bl = "" + c;         // lowercase alphabetic
                    else if ((i >= 65)&&(i <= 90)) bl = "" + c;     // uppercase alphabetic
                    else if ((i >= 48)&&(i <= 57)) bl = "" + c;     // numeric char
                    else if (i == 45) bl = "" + c;                  // "-"
                    else if (i == 95) bl = "" + c;                  // "_"

                    if (bl == null)
                    {
                          String errMsg="Invalid character for scenario name : only alpha-numeric, hypen and underscore. : " + scenarioName;
                          System.out.println("AddNewScenario.doPost()...ERROR:"+errMsg);
                          req.setAttribute("rtnMessage", errMsg);
                          res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                          return;
                    }
                }

              //if (method.equalsIgnoreCase("deleteScenario"))
              //{
              //
              //}
              //else
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
                          String errMsg="Scenario home directory creation failure. Not able to save:"+scenarioName;
                          System.out.println("AddNewScenario.doPost()...Error:"+errMsg);
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
                      if ((deletionTag != null)||(method.equalsIgnoreCase("deleteScenario")))
                      {
                          if ((deletionTag == null)&&(securityCode == null))
                          {
                                String errMsg="No delete confirmation code for delete scenario:"+scenarioName;
                                System.out.println("AddNewScenario.doPost()...Error:"+errMsg);
                                req.setAttribute("rtnMessage", errMsg);
                                res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                              return;
                          }
                          boolean pass = false;
                          for(File f2:scenarioDir.listFiles())
                          {
                              if (!f2.isFile()) continue;
                              String fName = f2.getName();
                              if ((fName.equalsIgnoreCase(ActionConstants.SCENARIO_DELETE_SECURITY_CONFIRMATION_CODE_FILE))||
                                  (fName.equalsIgnoreCase("password.txt")))
                              {
                                  String tr = FileUtil.readFileIntoString(f2.getAbsolutePath());
                                  if ((tr == null)||(tr.trim().equals(""))) continue;
                                  if ((deletionTag != null)&&(deletionTag.trim().equals(tr.trim()))) pass = true;
                                  if ((securityCode != null)&&(securityCode.trim().equals(tr.trim()))) pass = true;
                              }
                              if (pass) break;
                          }
                          if (!pass)
                          {
                              //String daTag = ActionConstants.NEW_ADD_SCENARIO_TAG;

                              //if ((deletionTag != null)&&(deletionTag.trim().equals(daTag))) pass = true;
                              //if ((securityCode != null)&&(securityCode.trim().equals(daTag))) pass = true;
                          }
                          if (!pass)
                          {
                              String errMsg="Invalid Delete Confirmation Code for delete scenario:"+scenarioName;
                                System.out.println("AddNewScenario.doPost()..Error:"+errMsg);
                                req.setAttribute("rtnMessage", errMsg);
                                res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                              return;
                          }
                          boolean deletionSuccess = true;
                          if (deleteFile(scenarioDir))
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
                                String errMsg="Scenario deletion failure:"+scenarioName;
                                System.out.println("AddNewScenario.doPost()...Error:"+errMsg);
                                req.setAttribute("rtnMessage", errMsg);
                                res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                          }
                          return;
                      }
                      else // method value is 'addNewScenario'
                      {
                        //String errMsg="Scenario already exists. Not able to save:"+scenarioName+"<br>If you want to update this scenario. Delete the scenario first.";
                        String errMsg="Scenario already exists. Not able to save:"+scenarioName;
                        System.out.println("AddNewScenario.doPost()...:"+errMsg);
                        req.setAttribute("rtnMessage", errMsg);
                        res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                        return;
                      }
                  } else {

                      if ((deletionTag != null)||(method.equalsIgnoreCase("deleteScenario")))
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

                      if ((securityCode == null)||(securityCode.trim().equals("")))
                      {
                          //String errMsg="Null delete confirmation code: This must be input for delete scenario in the future.";
                          //System.out.println("AddNewScenario.doPost()...Error:"+errMsg);
                          //req.setAttribute("rtnMessage", errMsg);
                          //deleteDirAndFilesOnError(fileList);
                          //res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                          //return;
                      }
                      else
                      {
                          String securityCodePath = scenarioPath+File.separator+ActionConstants.SCENARIO_DELETE_SECURITY_CONFIRMATION_CODE_FILE;
                          FileWriter fw = null;
                          try
                          {
                              fw = new FileWriter(securityCodePath);
                              fw.write(securityCode);
                              fw.close();
                          }
                          catch(Exception ie)
                          {
                              String errMsg="Delete confirmation Code Saving error.";
                              System.out.println("AddNewScenario.doPost()...Error:"+errMsg);
                              req.setAttribute("rtnMessage", errMsg);
                              deleteDirAndFilesOnError(fileList);
                              res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                              return;
                          }
                          fileList.add(securityCodePath);
                      }


                        String sourcePath = scenarioPath+File.separator+SOURCE_DIRECTORY_TAG;
                        success = (new File(sourcePath)).mkdir();
                        if (!success)
                        {
                            String errMsg="Faild to create source schema folder:"+scenarioName;
                            System.out.println("AddNewScenario.doPost()...Error:"+errMsg);
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
                            System.out.println("AddNewScenario.doPost()...Error:"+errMsg);
                            req.setAttribute("rtnMessage", errMsg);
                            deleteDirAndFilesOnError(fileList);
                            res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                            return;
                        }
                        fileList.add(targetPath);
                  }
	    	  }

              String pathSourceZip = null;
              String pathTargetZip = null;
              boolean instructionFileFound = false;
              if (fileItemList.size() > 0)
              {
                  for(FileItem item:fileItemList)
                  {
	    			  String fieldName = item.getFieldName();


                      //System.out.println("AddNewScenario.doPost()..item is NOT formField:"+item.getFieldName());//+"="+item.getString());
	    			  String filePath = item.getName();
	    			  String fileName=extractOriginalFileName(filePath);
                      if (fileName==null||fileName.equals(""))
	    				  continue;
                      System.out.println("AddNewScenario.doPost()..original file Name:"+fileName + ", Path="+filePath + ", TransType=" + transType);
                      //System.out.println("                       ..original file Path:"+filePath);


                      String tempFileName = fileName;


                      String uploadedFilePath=path+File.separator+scenarioName+File.separator+ fileName.toLowerCase();
	    			  if (fieldName.equals("mappingFileName")) {
	    				  System.out.println("AddNewScenario.doPost()...mapping file:"+uploadedFilePath);
	    				  String uploadedMapBak=uploadedFilePath+ ".bak";
	    				  //write bak of Mapping file
	    				  item.write(new File(uploadedMapBak));

                          int idxT = tempFileName.lastIndexOf(".");
                          if (idxT <= 0)
                          {
                              String errMsg = "This File cannot be identify its file type : " + tempFileName;
                              System.out.println("AddNewScenario.doPost()...ERROR:"+errMsg);
                              req.setAttribute("rtnMessage", errMsg);
                              deleteDirAndFilesOnError(fileList);
                              res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                              return;
                          }
                          tempFileName = tempFileName.substring(idxT + 1);
                          if (tempFileName.toLowerCase().startsWith(transType.toLowerCase()))
                          {
                              instructionFileFound = true;
                          }
                          else
                          {
                              String errMsg = "Not Matched between Transformation Type `"+transType+"` and the Instruction File type. : " + tempFileName;
                              System.out.println("AddNewScenario.doPost()...ERROR:"+errMsg);
                              req.setAttribute("rtnMessage", errMsg);
                              deleteDirAndFilesOnError(fileList);
                              res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                              return;
                          }


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
                                  if ((transType.equals("xq"))||(transType.equals("xsl")))
                                  {
                                      String errMsg = "Transformation Type -" + transType + "- doesn't need any schema file.";
                                      System.out.println("AddNewScenario.doPost()...ERROR:"+errMsg);
                                      req.setAttribute("rtnMessage", errMsg);
                                      deleteDirAndFilesOnError(fileList);
                                      res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                                      return;
                                  }
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
                                      boolean ok = true;
                                      String er = "";
                                      try
                                      {
                                          ok = replaceXSDFile(uploadedFilePath, bakupFileName, sourceORtarget, null);
                                      }
                                      catch(Exception ee)
                                      {
                                          er = ee.getMessage();
                                          ok = false;
                                      }
                                      if (ok)
                                      {
                                          fileList.add(bakupFileName);
                                      }
                                      else
                                      {
                                          String errMsg="Faild to upload this "+sourceORtarget+" schema file - "+fileName + ": " + er;
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
                                  if ((transType.equals("xq"))||(transType.equals("xsl")))
                                  {
                                      String errMsg = "Transformation Type `" + transType + "` doesn`t need any zip file.";
                                      System.out.println("AddNewScenario.doPost()...ERROR:"+errMsg);
                                      req.setAttribute("rtnMessage", errMsg);
                                      deleteDirAndFilesOnError(fileList);
                                      res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                                      return;
                                  }
                                  uploadedFilePath=path+File.separator+scenarioName+File.separator+sourceORtarget + File.separator + fileName;//sourceORtarget+File.separator+fileName;
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
                                          if (pathSourceZip != null)
                                          {
                                              String errMsg="Only one zip file allowed - "+sourceORtarget+" :"+fileName;
                                              System.out.println("AddNewScenario.doPost()...ERROR:"+errMsg);
                                              req.setAttribute("rtnMessage", errMsg);
                                              deleteDirAndFilesOnError(fileList);
                                              res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                                              return;
                                          }
                                          else pathSourceZip = uploadedFilePath;
                                      }
                                      else
                                      {
                                          if (pathTargetZip != null)
                                          {
                                              String errMsg="Only one zip file allowed - "+sourceORtarget+" :"+fileName;
                                              System.out.println("AddNewScenario.doPost()...ERROR:"+errMsg);
                                              req.setAttribute("rtnMessage", errMsg);
                                              deleteDirAndFilesOnError(fileList);
                                              res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                                              return;
                                          }
                                          else pathTargetZip = uploadedFilePath;
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

              //boolean sourceZipCheckPass = false;
              //boolean targetZipCheckPass = false;
              if ((pathSourceZip != null)||(sourceOriginalXSDPath != null))
              {
                  try
                  {
                      extractXSDFileFromZip(pathSourceZip, sourceOriginalXSDPath, SOURCE_DIRECTORY_TAG);
                      sourceOriginalXSDPath = null;
                  }
                  catch(Exception ee)
                  {
                      String errMsg="Incomplete Source ZIP - "+pathSourceZip+" - file. : " + ee.getMessage();
                      req.setAttribute("rtnMessage", errMsg);
                      deleteDirAndFilesOnError(fileList);
                      res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                      return;
                  }
                  //sourceZipCheckPass = true;
              }
              if ((pathTargetZip != null)||(targetOriginalXSDPath != null))
              {
                  try
                  {
                      extractXSDFileFromZip(pathTargetZip, targetOriginalXSDPath, TARGET_DIRECTORY_TAG);
                      targetOriginalXSDPath = null;
                  }
                  catch(Exception ee)
                  {
                      String errMsg="Incomplete Target ZIP - "+pathTargetZip+" - file. : " + ee.getMessage();
                      req.setAttribute("rtnMessage", errMsg);
                      deleteDirAndFilesOnError(fileList);
                      res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                      return;
                  }
                  //targetZipCheckPass = true;
              }

              List<String> notFoundfiles = new ArrayList<String>();
              for(String pathXSD:includedXSDList)
              {
                  String xsdPath = path+File.separator+scenarioName+File.separator+ pathXSD;
                  File ff = new File(xsdPath);
                  if ((!ff.exists())||(!ff.isFile()))
                  {
                      //if ((sourceZipCheckPass)&&(pathXSD.startsWith(SOURCE_DIRECTORY_TAG + File.separator))) {}
                      //else if ((targetZipCheckPass)&&(pathXSD.startsWith(TARGET_DIRECTORY_TAG + File.separator))) {}
                      //else
                          notFoundfiles.add(pathXSD);
                  }
              }

              if ((notFoundfiles.size() > 0)||(!instructionFileFound))
              {
                  String errMsg="Incomplete XSD files. Following " + notFoundfiles.size() + " required files are not uploaded. - ";
                  if (notFoundfiles.size() == 1) errMsg="Incomplete XSD files. Following one required file is not uploaded. - ";

                  if (!instructionFileFound) errMsg = "No Instruction File is uploaded.";
                  else for (String c:notFoundfiles) errMsg = errMsg + "<br>" + c;
                  System.out.println("AddNewScenario.doPost()...ERROR:"+errMsg);

                  req.setAttribute("rtnMessage", errMsg);
                  deleteDirAndFilesOnError(fileList);
                  res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                  return;
              }

              if ((transType.toLowerCase().equals("map"))&&(mappingFileName != null))
              {
                  try
                  {
                      MappingFactory.loadMapping(new File(mappingFileName));
                  }
                  catch(Exception ee)
                  {
                      String errMsg = ee.getMessage();
                      System.out.println("AddNewScenario.doPost()...ERROR on Load mapping file:"+errMsg);

                      req.setAttribute("rtnMessage", errMsg);
                      deleteDirAndFilesOnError(fileList);
                      res.sendRedirect("errormsg.do" + "?message=" + URLEncoder.encode(errMsg, "UTF-8"));
                      return;
                  }
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

    public void doGet (HttpServletRequest req, HttpServletResponse res)
	      throws ServletException, IOException
	   {
            isDoGet = true;
           doPost(req, res);
       }

    private void extractXSDFileFromZip(String zipPath, String xsdPath, String sourceORtarget) throws Exception
    {
        if ((zipPath == null)||(zipPath.trim().equals(""))) return;
        if ((xsdPath == null)||(xsdPath.trim().equals(""))) return;

        File zipFile = new File(zipPath);
        if ((!zipFile.exists())||(!zipFile.isFile()))
        {
            throw new Exception("This is not a file : " + zipPath);
        }
        ZipUtil zipUtil = new ZipUtil(zipFile.getAbsolutePath());

        String dir = (new File(zipPath)).getParentFile().getAbsolutePath();
        if (!dir.endsWith(File.separator)) dir = dir + File.separator;
        alreadyFoundXSDURLList = new ArrayList<String>();

        Exception ee1 = null;
        try
        {
            extractXSDFileFromZip(zipUtil, xsdPath, null, sourceORtarget);
        }
        catch (Exception ee)
        {
            ee1 = ee;
        }
        alreadyFoundXSDURLList = null;
        try
        {
            zipUtil.getZipFile().close();
        }
        catch(IOException ie)
        {
            System.out.println("ERROR : Zip file closing failure ("+zipPath+") : " + ie.getMessage());
        }
        if (ee1 != null) throw ee1;
    }
    private void extractXSDFileFromZip(ZipUtil zipUtil, String xsdLocation, String parentUrl, String sourceORtarget) throws Exception
    {

        String urlT = "";
        String errMsg = null;
        if ((parentUrl == null)||(parentUrl.trim().equals("")))
        {
            ZipEntry zipEntry = MappingFactory.getXSDZipEntryFromZip(zipUtil, xsdLocation);
            if (zipEntry == null)
            {
                System.out.println("ERROR01 : Zip file finding entry failure : " + xsdLocation);
                return;
            }

            urlT = zipUtil.getAccessURL(zipEntry);
        }
        else
        {
            String tempUrl = parentUrl;
            String tempLoc = xsdLocation;
            //System.out.println("CCCCC 55 location=" + xsdLocation + ", parUrl=" + tempUrl);
            while(true)
            {
                if (tempUrl.endsWith("/")) tempUrl = tempUrl.substring(0, tempUrl.length()-1);
                int idx = tempLoc.indexOf("/");
                if (idx < 0) idx = tempLoc.indexOf("\\");
                if (idx < 0)
                {
                    tempUrl = tempUrl + "/" + tempLoc;
                    break;
                }
                String d = tempLoc.substring(0, idx + 1).trim();
                tempLoc = tempLoc.substring(idx + 1);
                if (d.equals("./")) continue;
                else if (d.equals(""))
                {
                    throw new Exception("invalid xsd path in this zip. (2) url=" + parentUrl + ", Location=" + xsdLocation);
                }
                else if (d.equals("../"))
                {
                    int idx2 = tempUrl.lastIndexOf("/");
                    if (idx2 < 0)
                    {
                        errMsg = "invalid xsd path in this zip. url=" + parentUrl + ", Location=" + xsdLocation;
                        break;
                        //throw new Exception(errMsg);
                    }
                    tempUrl = tempUrl.substring(0, idx2 + 1);
                    if (tempUrl.indexOf("!") < 0)
                    {
                        errMsg = "invalid xsd path in this zip. Null parent! url=" + parentUrl + ", Location=" + xsdLocation;
                        break;
                        //throw new Exception(errMsg);
                    }

                }
                else tempUrl = tempUrl + "/" + d;
            }
            urlT = tempUrl;
        }
        List<String> lines = null;

        boolean alreadyFoundURL = false;
        for (String str:alreadyFoundXSDURLList)
        {
            if (urlT.equals(str)) alreadyFoundURL = true;
        }
        if (alreadyFoundURL) return;

        String fn = null;
        for(int i=0;i<2;i++)
        {
            try
            {
                fn = FileUtil.downloadFromURLtoTempFile(urlT);
                lines = FileUtil.readFileIntoList(fn);
                //(new File(fn)).delete();
                break;
            }
            catch(IOException ie)
            {
                boolean makeError = true;
                while(i == 0)
                {
                    int idx32 = urlT.indexOf("!/");
                    //if (idx32 < 0) break;
                    ZipEntry zipEntry = null;
                    if ((errMsg != null)||(idx32 < 0)) zipEntry = MappingFactory.getXSDZipEntryFromZip(zipUtil, xsdLocation);
                    else zipEntry = MappingFactory.getXSDZipEntryFromZip(zipUtil, urlT.substring(idx32 + 2));
                    if (zipEntry == null)
                    {
                        //System.out.println("CCCC 54" + "zip entry is null. : " + xsdLocation + ", url=" + urlT);
                        break;
                    }
                    urlT = zipUtil.getAccessURL(zipEntry);
                    makeError = false;
                    break;
                }
                if (makeError)
                {
                    if (errMsg != null)
                    {
                        System.out.println("ERROR : " + errMsg + ", message=" + ie.getMessage());
                        //throw new Exception(errMsg);
                        return;
                    }
                    else
                    {

                        System.out.println("ERROR : ZIP URL dowm Load error ("+urlT+") : message=" + ie.getMessage());
                        return;
                        //throw new Exception("ZIP URL dowm Load error ("+urlT+") : " + ie.getMessage());
                    }
                }
            }
        }
        int idx21 = urlT.lastIndexOf("/");
        //if (idx21 < 0) idx21 = urlT.lastIndexOf("!");
        if (idx21 < 0) throw new Exception("Invalid Url form : " + urlT);
        String parUrl = urlT.substring(0, idx21);
        if ((lines == null)||(lines.size() == 0)) throw new Exception("Empty zip entry : " + urlT);
        else
        {
            boolean isEmpty = true;
            for (String line:lines) if (!line.trim().equals("")) isEmpty = false;
            if (isEmpty) throw new Exception("Empty zip entry (2) : " + urlT);
        }
        alreadyFoundXSDURLList.add(urlT);
        //System.out.println("CCCC Found XSD zip entry : " + urlT + ", parUrl=" +parUrl);

        File fileToDir = (new File(zipUtil.getZipFile().getName())).getParentFile();
        String fileToDirStr = fileToDir.getAbsolutePath();
        if (!fileToDirStr.endsWith(File.separator)) fileToDirStr = fileToDirStr + File.separator;
        String fileName = extractOriginalFileName(xsdLocation);
        replaceXSDFile(fn, null, sourceORtarget, fileToDirStr + fileName.toLowerCase());

        if ((tempXSDList != null)&&(tempXSDList.size() > 0))
        {
            for (String str:tempXSDList)
            {
                if (!str.trim().equals("")) extractXSDFileFromZip(zipUtil, str, parUrl, sourceORtarget);
            }
        }
    }






    /*
    private void extractXSDFileFromZip2(ZipUtil zipUtil, String xsdLocation, String dir, String parentUrl)
    {
        //String xsdName = extractOriginalFileName(xsdLocation);
        if ((parentUrl == null)||(parentUrl.trim().equals("")))
        {
            ZipEntry zipEntry = getXSDZipEntryFromZip(zipUtil, xsdLocation);
            if (zipEntry == null)
            {
                System.out.println("ERROR01 : Zip file finding entry failure : " + xsdLocation);
                return;
            }
            String tempFileName = "";
            try
            {
                tempFileName = zipUtil.copyEntryToFile(zipEntry, dir);
            }
            catch(IOException ie)
            {
                System.out.println("ERROR02 : ZipEntry tempfile saving failure ("+xsdLocation+") : " + ie.getMessage());
                return;
            }
            if (tempFileName == null)
            {
                System.out.println("ERROR03 : ZipEntry tempfile saving failure. Already Exist.("+xsdLocation+") : ");
                return;
            }
        }
        if (!replaceXSDFile(tempFileName, null, (new File(dir)).getName()))
        {
            System.out.println("ERROR04 : Zip file replaceXSDFile() failure ("+xsdLocation+") : " + tempFileName);
            return;
        }
        alreadyWriteXSDList.add(xsdLocation);
        if (tempXSDList.size() > 0)
        {
            Object[] d = tempXSDList.toArray();
            tempXSDList = null;
            for(Object o:d)
            {
                String ss = (String) o;
                boolean found = false;
                for(String cc:alreadyWriteXSDList)
                {
                    if (cc.equals(ss))
                    {
                        found = true;
                        break;
                    }
                }
                if (!found) extractXSDFileFromZip(zipUtil, ss, dir);
            }
        }
    }

    private ZipEntry getXSDZipEntryFromZip1(ZipUtil zipUtil, String xsdLocation)
    {
        String xsdName = extractOriginalFileName(xsdLocation);

        ZipEntry zipEntry = null;
        try
        {
            zipEntry = zipUtil.searchEntryWithWholeName(xsdName);
        }
        catch(IOException ie)
        {
            List<String> list1 = zipUtil.getEntryNames();
            String select = null;
            int number = -1;
            for(String str:list1)
            {
                int count = -1;

                if (str.equalsIgnoreCase(xsdName))
                {
                    select = str;
                    break;
                }

                if (str.toLowerCase().endsWith("/"+xsdName.toLowerCase())) {}
                else if (str.toLowerCase().endsWith("\\"+xsdName.toLowerCase())) {}
                else continue;
                String loc = xsdLocation.substring(0, xsdLocation.length()-xsdName.length());
                String ent = str.substring(0, str.length()-xsdName.length());
                int cnt2 = 0;
                for(int i=0;i<ent.length();i++)
                {
                    if (i >= loc.length()) break;
                    String acharL = loc.substring(loc.length()-(i+1), loc.length()-i);
                    String acharE = ent.substring(ent.length()-(i+1), ent.length()-i);
                    if (acharL.equals("\\")) acharL = "/";
                    if (acharE.equals("\\")) acharE = "/";
                    if (acharL.equalsIgnoreCase(acharE)) cnt2++;
                    else break;
                }
                if (cnt2 > 0) count = cnt2;

                if (count > number)
                {
                    number = count;
                    select = str;
                }
            }
            if (select != null)
            {
                zipEntry = zipUtil.getZipFile().getEntry(select);
            }
            else zipEntry = null;
        }

        //System.out.println("CCCC new XSD location:" + xsdF);
        return zipEntry;
    }
    */
       private void deleteDirAndFilesOnError(List<String> fileList)
	   {
           if (fileList.size() == 0) return;
           for(int i=1;i<=fileList.size();i++)
           {
               String path = fileList.get(fileList.size() - i);
               File file = new File(path);
               if (!deleteFile(file))
               {
                   System.out.println("delete scenario file failure : " + path);
               }
           }
       }
       private boolean deleteFile(File file)
       {
           if (file.isFile()) return file.delete();
           if (!file.isDirectory()) return false;
           File[] list = file.listFiles();
           boolean res = true;
           for(File file2:list)
           {
                if (!deleteFile(file2)) res = false;
           }
           if (!res) return false;
           return file.delete();
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
                    String originalLoc = locationAttr.getValue();
                    String localName=extractOriginalFileName(originalLoc);
//			    	locationAttr.setValue(fileHome+File.separator+cmpType+File.separator+localName);

                    //includedXSDList.add(cmpType+File.separator+localName.toLowerCase());
                    if (cmpType.equalsIgnoreCase(SOURCE_DIRECTORY_TAG))
                    {
                        sourceOriginalXSDPath = originalLoc;
                        locationAttr.setValue(SOURCE_DIRECTORY_TAG+File.separator+localName.toLowerCase());
                        includedXSDList.add(SOURCE_DIRECTORY_TAG+File.separator+localName.toLowerCase());
                    }
                    if (cmpType.equalsIgnoreCase(TARGET_DIRECTORY_TAG))
                    {
                        targetOriginalXSDPath = originalLoc;
                        locationAttr.setValue(TARGET_DIRECTORY_TAG+File.separator+localName.toLowerCase());
                        includedXSDList.add(TARGET_DIRECTORY_TAG+File.separator+localName.toLowerCase());
                    }
                    //System.out.println("CCCCC updateMapping Add file list : " + cmpType+File.separator+localName.toLowerCase());
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

        private boolean replaceXSDFile(String fileName, String backupFileName, String sourceORtarget, String toFileName) throws Exception
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

            File file1 = new File(fileName);
            if ((toFileName != null)&&(!toFileName.trim().equals(""))) fileName = toFileName;
            List<String> xsdList2 = new ArrayList<String>();
            tempXSDList = new ArrayList<String>();
            String locationAttr = "schemaLocation=\"";

            String xsdT = "";
            String xsd = null;
            boolean skipping = false;
            for (int i=0;i<xsdList.size();i++)
            {
                String xsdT2 = xsdList.get(i);
                xsdT = "";
                xsd = null;
                int idxx1 = -1;
                int idxx2 = -1;

                while(true)
                {
                    idxx1 = xsdT2.indexOf("<!--");
                    idxx2 = xsdT2.indexOf("-->");
                    if (idxx1 >= 0)
                    {
                        if (idxx2 > 0)
                        {
                            //System.out.println("both - idxx1=" + idxx1 + ", idxx2=" + idxx2 + ", skipping=" + skipping + ", line=" + xsdList.get(i));
                            if (idxx2 < idxx1)
                            {
                                if (!skipping) throw new Exception("Invalid XSD Remark (3)("+fileName+") : " + xsdList.get(i));
                                xsdT2 = xsdT2.substring(idxx2 + 3);
                                skipping = false;
                                //throw new Exception("Invalid XSD Remark ("+fileName+") : " + xsdT2);
                            }
                            else
                            {
                                xsdT = xsdT + xsdT2.substring(0, idxx1);
                                xsdT2 = xsdT2.substring(idxx2 + 3);
                                skipping = false;
                            }
                        }
                        else
                        {
                            //System.out.println("CCCCC 55 idxx1 only   idxx1=" + idxx1 + ", idxx2=" + idxx2 + ", skipping=" + skipping + ", line=" + xsdList.get(i));
                            xsd = xsdT2.substring(0, idxx1);
                            skipping = true;
                            break;
                        }
                    }
                    else if (idxx2 >= 0)
                    {
                        //System.out.println("CCCCC 55 idxx2 only   idxx1=" + idxx1 + ", idxx2=" + idxx2 + ", skipping=" + skipping + ", line=" + xsdList.get(i));
                        if (!skipping) throw new Exception("Invalid XSD Remark (2)("+fileName+") : " + xsdT2);
                        xsdT2 = xsdT2.substring(idxx2 + 3);
                        skipping = false;
                    }
                    else break;
                }
                if (skipping)
                {
                    //System.out.println("CCCCC 55 skipping   idxx1=" + idxx1 + ", idxx2=" + idxx2 + ", skipping=" + skipping + ", line=" + xsdList.get(i));
                    if (xsd != null) xsdT = xsdT + xsd;
                    else continue;
                }
                else xsdT = xsdT + xsdT2;

                if (xsdT.trim().equals("")) continue;
                xsd = xsdT;
                String xsd2 = "";
                while(true)
                {
                    int idx1 = xsd.indexOf(locationAttr);
                    if (idx1 < 0)
                    {
                        xsd2 = xsd2 + xsd;
                        break;
                    }
                    else if (idx1 == 0) {}
                    else
                    {
                        String ach = xsd.substring(idx1-1, idx1);
                        if ((ach.equals(" "))||(ach.equals("\t"))) {}
                        else
                        {
                            xsd2 = xsd2 + xsd;
                            break;
                        }
                    }
                    String sub = xsd.substring(0, idx1 + locationAttr.length());
                    xsd2 = xsd2 + sub;
                    xsd = xsd.substring(idx1 + locationAttr.length());
                    int idx2 = xsd.indexOf("\"");
                    if (idx2 < 0) return false;
                    String path = xsd.substring(0, idx2);
                    xsd = xsd.substring(idx2);

                    boolean found = false;
                    for(String str:tempXSDList)
                    {
                        if (str.equals(path))
                        {
                            found = true;
                            break;
                        }
                    }
                    if (!found) tempXSDList.add(path);
                    while(true)
                    {
                        int idx3 = path.indexOf("/");
                        if (idx3 < 0) idx3 = path.indexOf("\\");
                        if (idx3 < 0) break;

                        path = path.substring(idx3 + 1);
                    }
                    xsd2 = xsd2 + path.toLowerCase();

                    boolean found2 = false;
                    String path2 = sourceORtarget + File.separator + path.toLowerCase();
                    for(String str:includedXSDList)
                    {
                        if (str.equals(path2))
                        {
                            found2 = true;
                            break;
                        }
                    }
                    if (!found2) includedXSDList.add(path2);

                    //System.out.println("CCCCC replaceXSDFile : " + sourceORtarget + File.separator + path.toLowerCase());
                    //xsd2 = xsd2 + path;
                }
                xsdList2.add(xsd2);
            }

            //File file1 = new File(fileName);
            String nm = file1.getName();
            File parent = file1.getParentFile();
            if ((toFileName != null)&&(!toFileName.trim().equals("")))
            {
                File file2 = new File(toFileName);
                nm = file2.getName();
                parent = file2.getParentFile();
            }

            while(!parent.getName().equalsIgnoreCase(sourceORtarget))
            {
                parent = parent.getParentFile();
                if (parent == null)
                {
                    throw new Exception("ERROR : Not found " + sourceORtarget + "directory.");
                    //return false;
                }
            }
            String parPath = parent.getAbsolutePath();
            if ((backupFileName != null)&&(!backupFileName.trim().equals("")))
            {
                File file2 = new File(backupFileName);
                file1.renameTo(file2);
            }
            else file1.delete();

            //if (!nm.equals(nm.toLowerCase()))
            //{
                if (!parPath.endsWith(File.separator)) parPath = parPath + File.separator;
                fileName = parPath + nm.toLowerCase();
            //}

            FileWriter fw = null;

            try
            {
                fw = new FileWriter(fileName);
                for (int i=0;i<xsdList2.size();i++)
                {
                    String xsdC = xsdList2.get(i);
                    fw.write(xsdC + "\n");
                }
                fw.close();
             }
            catch(Exception ie)
            {
                throw new Exception("Failure writing XSD file from ZIP : " + ie.getMessage());
            }
            //System.out.println("CCCC XSD file writing success : " + fileName);
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
           mappingFileName = file.getAbsolutePath();
       }

}
