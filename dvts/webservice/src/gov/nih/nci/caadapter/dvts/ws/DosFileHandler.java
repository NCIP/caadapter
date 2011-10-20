package gov.nih.nci.caadapter.dvts.ws;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
//import java.sql.*;
import java.text.*;  // simple data format
import java.util.*;
import gov.nih.nci.caadapter.dvts.common.util.FileUtil;
import gov.nih.nci.caadapter.dvts.common.function.DateFunction;
import edu.knu.medinfo.hl7.v2tree.ByteTransform;

/**
 * Created by IntelliJ IDEA.
 * User: kium
 * Date: Jun 16, 2009
 * Time: 1:11:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class DosFileHandler extends HttpServlet
{
    boolean useSession = false;
    CaadapterWSUtil util = new CaadapterWSUtil();
    GeneralUtilitiesWS gUtil = new GeneralUtilitiesWS();
    String ipAddr;
    String ADMINISTRATOR_ID = util.getAdministratorID();
    String SESSION_TAG = util.getSessionTag();
    PrintWriter out = null;
    String loginID = null;
    String userPath = util.getRootScenarioPath() + util.getAccessFailureTag();
    File currentDirFile = new File(FileUtil.getWorkingDirPath().trim());
    String currentCommand = "";
    String beforeCommand = "";
    String parameter1 = "";
    String parameter2 = "";
       // init ?? ??? ???? ???
    public void init(ServletConfig config) throws ServletException
    {
        try
        {
            super.init(config);
            System.out.println("initial - DosFileHandler " + gUtil.getFormatedNowDate());
        }
        catch(Exception e)
        {
            throw new ServletException("Can't get connection to " + this.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res)
                throws ServletException, IOException
    {
        util.setBaseURLToPropertyfile(req);
        res.setContentType("text/html");

        util.getUniversalLogin(res.getWriter(), this.getClass().getName(), "caAdapter Web Service File Management", "Administrator", "adminID", "adminPass", "", req);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse response)
                throws ServletException, IOException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        // PrintWriter out = new PrintWriter(res, getOutputStream(),"8859_1");
        String ipAddr = req.getRemoteAddr();

//		String connectTime = gUtil.getFormatedNowDate();
//		String startTime = "";
//		String endTime = "";
        String res = "";
//		int count = 0;
//		String outputFile = "";

        //DateFunction df = new DateFunction();

        useSession = false;

        // Parameter variables
        String adminID = null;
        String adminPass = null;
        String command = null;
        String param1 = null;
        String param2 = null;
        String currentPath = null;
        String lastCommand = null;
        String[] paramArray = new String[] {adminID, adminPass, command, param1, param2, currentPath, lastCommand};


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
            else if (paramName.equalsIgnoreCase("command")) idxP = 2;
            else if (paramName.equalsIgnoreCase("param1")) idxP = 3;
            else if (paramName.equalsIgnoreCase("param2")) idxP = 4;
            else if (paramName.equalsIgnoreCase("currentPath")) idxP = 5;
            else if (paramName.equalsIgnoreCase("lastCommand")) idxP = 6;
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
            if (paramArray[i] == null) paramArray[i] = "";
            else paramArray[i] = paramArray[i].trim();

            if (i == 0) adminID = paramArray[i];
            else if (i == 1) adminPass = paramArray[i];
            else if (i == 2) command = paramArray[i];
            else if (i == 3) param1 = paramArray[i];
            else if (i == 4) param2 = paramArray[i];
            else if (i == 5) currentPath = paramArray[i];
            else if (i == 6) lastCommand = paramArray[i];
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
            //currentDir = getRootDir();
            String hh = getHTML("Start Dos Command Handler", "", req);
            out.println(hh);
            return;
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


            if (command == null) command = "";
            else command = command.toLowerCase();
            if ((command.equalsIgnoreCase("dir"))||
                (command.equalsIgnoreCase("cd"))||
                (command.equalsIgnoreCase("md"))||
                (command.equalsIgnoreCase("del"))||
                //(command.equalsIgnoreCase("deltree"))||
                (command.equalsIgnoreCase("copy"))||
                (command.equalsIgnoreCase("edit"))||
                (command.equalsIgnoreCase("create"))||
                (command.equalsIgnoreCase("save"))||
                (command.equalsIgnoreCase("move"))||
                (command.equalsIgnoreCase("logout"))) {}
            else
            {
                util.returnMessageAndLogging(out, "Invalid Dos Command", util.codeERROR(), "Invalid Dos Command : " + command, userPath, ADMINISTRATOR_ID, ipAddr, this);
                return;
            }

            if (command.equalsIgnoreCase("logout"))
            {
                userPath = util.getRootScenarioPath() + ADMINISTRATOR_ID;
                String resLogin = util.logoutSession(ADMINISTRATOR_ID, ipAddr, this);
                if (resLogin != null)
                {
                    res = resLogin;
                    util.returnMessageAndLogging(out, "Unauthorized Admin Session", util.codeFATAL(), resLogin, userPath, adminID, ipAddr, this);
                }
                else util.returnMessage(out, "Session Logged out", util.codeINFO(), "Session Logged out from caAdapter Web service file managing");

                return;
            }
            currentCommand = command;
            if ((lastCommand != null)&&(!lastCommand.trim().equals(""))) beforeCommand = lastCommand;
            //currentPath;
            if ((currentPath == null)||(currentPath.trim().equals("")))
            {
                out.println(getReturnHTML("Null Current directory...", req));
                    return;
            }
            File fileT = new File(currentPath);
            if (!fileT.exists())
            {
                out.println(getReturnHTML("Not exist directory : " + currentPath, req));
                    return;
            }
            if (!fileT.isDirectory())
            {
                out.println(getReturnHTML("This is not a directory For current Directory : " + currentPath, req));
                    return;
            }
            currentDirFile = fileT;

            if (command.equalsIgnoreCase("create"))
            {
                out.println(getHTML("Creating Text File",
                                   "'create' commamd : Input file name and text content and select 'save' command.'" + "<br>" + displayDir(fileT, req), req));
                return;
            }

            // ** original place of commant check
            String params = param1 + " " + param2;
            if (!((currentCommand.equals("save"))||(currentCommand.equals("edit")))) params = param1;
            params = params.trim();
            if (!params.equals("")) params = " " + params;

            String source = param1;
            if ((source == null)||(source.trim().equals("")))
            {
                if ((command.equals("dir"))||(command.equals("cd")))
                    {
                      source = currentDirFile.getAbsolutePath();
                    }
                    else
                    {
                        out.println(getReturnHTML("null source parameter", req));
                        return;
                    }
            }

            String sourcePath = changeCharactersInPathName(source);
            if (sourcePath.startsWith("ERROR:"))
            {
                    out.println(getReturnHTML("'" + command + params + "' Command Failure : invalid source pathname : " + sourcePath, req));
                    return;
            }

            if (command.equals("del"))
            {
                String rr = deleteProcess(sourcePath);
                File ff = new File(sourcePath);
                File parent = ff.getParentFile();
                if (rr == null)
                {
                    out.println(getHTML("'del"+params+"' command is complete..",
                                    "Weldone delete : " + sourcePath+ "<br>" + displayDir(parent, req), req));

                        return;
                }
                else
                {
                    out.println(getReturnHTML("('del"+params+"') delete Failure : " + sourcePath + " : " + rr, req));
                        return;
                }
            }

            File sourceFile = new File(sourcePath);
            //if (!sourceFile.exists())
            //{
            //    if (!command.equals("md"))
            //	{
            //		out.println(getReturnHTML("'" + command + params + "' Command Failure : invalid source pathname(NullPointerException) : " + sourcePath));
            //		return;
            //	}
            //}

            if (command.equals("md"))
            {
                if (sourceFile.exists())
                {
                    if (sourceFile.isDirectory())
                    {
                        out.println(getReturnHTML("'" + command + params + "' Command Failure : Already exists pathname : " + sourcePath, req));
                        return;
                    }
                    else if (sourceFile.isFile())
                    {
                        out.println(getReturnHTML("'" + command + params +"' Command Failure : This name is already used by another file." + sourcePath, req));
                        return;
                    }
                    else
                    {
                        out.println(getReturnHTML("Invalid dir Name" + sourcePath, req));
                        return;
                    }
                }
                else
                {
                    try { sourceFile.mkdirs(); }
                    catch (SecurityException ne)
                    {
                         out.println(getReturnHTML("Directory making error(SecurityException) : " + sourcePath + " : " + ne.getMessage() , req));
                        return;
                    }
                    out.println(getHTML("'md"+params+"' command is complete..",
                                        "'md"+params+"' command is complete.. "+sourcePath+ "<br>" + displayDir(sourceFile.getParentFile(), req), req));
                    return;
                }
            }

            if (command.equalsIgnoreCase("save"))
            {
                if (!((beforeCommand.equals("create"))||(beforeCommand.equals("edit"))))
                {
                    out.println(getReturnHTML("Invalid 'save' command, not for 'create' or 'edit'.", req));
                    return;
                }

                if ((sourceFile.exists())&&(sourceFile.isFile())&&(beforeCommand.equals("create")))
                {
                    out.println(getReturnHTML("'" + command + params + "' Command Failure : Already exists File name : " + sourcePath, req));
                    return;
                }

                if ((!sourceFile.exists())&&(beforeCommand.equals("edit")))
                {
                    out.println(getReturnHTML("'" + command + params + "' Command Failure : This File is not found. : " + sourcePath, req));
                    return;
                }

                if ((param2 == null)||(param2.trim().equals("")))
                {
                    out.println(getReturnHTML("'" + command + params + "' Command Failure : No Text content for File saving : " + sourcePath, req));
                    return;
                }

                if (param2.toLowerCase().trim().equals("&&empty")) param2 = "";

                FileWriter fw = null;

                try
                {
                    fw = new FileWriter(sourceFile.getAbsolutePath());
                    fw.write(param2);
                    fw.close();
                }
                catch(Exception ie)
                {
                    out.println(getReturnHTML("'" + command + params + "' Command Failure : File saving error (" + sourcePath + ") : " + ie.getMessage(), req));
                    return;
                }

                out.println(getHTML("File saving successful",
                                    "Weldone 'save' command from '"+beforeCommand+"' : File is successfully saved. : " +  sourcePath + "<br>" + displayDir(fileT, req), req));
                return;
            }

            if (!sourceFile.exists())
            {

                    out.println(getReturnHTML("'" + command + params + "' Command Failure : Not Exists file name : " + sourcePath, req));
                        return;

            }

            if ((command.equals("dir"))||(command.equals("cd")))
            {

                if (sourceFile.isDirectory())
                {
                    if (command.equals("cd")) currentDirFile = sourceFile;
                    out.println(getHTML("'" + command + params + "' command complete..", "'" + command + params + "' command complete..<br>" + displayDir(sourceFile, req), req));
                    return;
                }
                else if ((command.equals("dir"))&&(sourceFile.isFile()))
                {
                    currentDirFile = sourceFile.getParentFile();
                    out.println(getHTML("'" + command + params + "' command complete..", "'" + command + params + "' command complete..<br>" + displayDir(sourceFile.getParentFile(), req), req));
                    return;
                }
                else
                {
                    out.println(getReturnHTML("('" + command + params + "') This is not a Directory : " + sourcePath, req));
                    return;
                }
            }

            /*
               if (command.equals("del"))
               {
                   try
                       {
                           File parent = sourceFile.getParentFile();
                           if (sourceFile.delete())
                               {
                                   out.println(getHTML("'del"+params+"' command is complete..",
                                                   "Weldone delete : " + sourcePath+ "<br>" + displayDir(parent)));

                                       return;
                               }
                               else
                               {
                                   out.println(getReturnHTML("(del) delete Failure : " + sourcePath));
                                       return;
                               }
                       }
                       catch(SecurityException se)
                       {
                           out.println(getReturnHTML("(del) delete error : "+se.getMessage()+" :" + sourcePath));
                               return;
                       }
               }
               */
            if (!sourceFile.isFile())
            {
                out.println(getReturnHTML("(" + command + ") This is not a file : " + sourcePath, req));
                    return;
            }

            if (command.equals("edit"))
            {
                String str1 = sourceFile.getAbsolutePath();
                String str2 = "";

                try
                {
                    str2= FileUtil.readFileIntoStringAllowException(str1);
                }
                catch(IOException ie)
                {
                    beforeCommand = "";
                    out.println(getReturnHTML("'" + command + params + "' Command Failure : file reading error (" + sourcePath + ") : " + ie.getMessage(), req));
                    return;
                }
                parameter1 = str1;
                parameter2 = str2;
                out.println(getHTML("Editing Text File",
                                    "'" + command + params + "' Command : Edit the text content and select 'save' command. : " + sourcePath + "<br>" + displayDir(fileT, req), req));
                return;
            }

            String target = param2;
            if (( target == null)||( target.trim().equals("")))
            {
                out.println(getReturnHTML("null target", req));
                    return;
            }
            String targetPath = changeCharactersInPathName(target);
            File targetFile = null;
            try { targetFile = new File(targetPath); }
            catch (NullPointerException ne)
            {
                    out.println(getReturnHTML("invalid target pathname : " + targetPath, req));
                    return;
          }
            if (targetFile.exists())
            {
                out.println(getReturnHTML("(" + command + ") This target path is already exists : " + sourcePath, req));
                    return;
            }
            if (command.equals("copy"))
            {
                    String str = gUtil.copyFile(sourcePath, targetPath);
                    if (str == null)
                    {
                        File parent = (new File(targetPath)).getParentFile();
                            out.println(getHTML("'copy' command is complete..",
                                                "Weldone copy " + sourcePath+ " to " + targetPath + "<br>" + displayDir(parent, req), req));

                    }
                    else
                    {
                        out.println(getReturnHTML("Copy Failure : copy " + sourcePath + " to " + targetPath + " : " + str, req));

                    }
                    return;
            }
            else if (command.equals("move"))
            {
                try
                    {
                        if (sourceFile.renameTo(targetFile))
                            {
                                File parent = (new File(targetPath)).getParentFile();
                                    out.println(getHTML("'move' command is complete..",
                                                "Weldone move " + sourcePath+ " to " + targetPath + "<br>" + displayDir(parent, req), req));

                            }
                            else
                            {
                                out.println(getReturnHTML("Move Failure : move " + sourcePath + " to " + targetPath, req));

                            }
                    }
                    catch(SecurityException se)
                    {
                        out.println(getReturnHTML("(" + command + ") error : "+se.getMessage()+" :" + sourcePath, req));

                    }
                    return;
            }


        }
//  private String getNowDate()
//	  {
//		  java.util.Date dt = new java.util.Date();
//			SimpleDateFormat sd1 = new SimpleDateFormat("yyyyMMdd");
//			SimpleDateFormat sd2 = new SimpleDateFormat("HHmmss");
//			return sd1.format(dt) + sd2.format(dt);
//		}
  private String changeCharactersInPathName(String str)
    {
        return reorganizePathName(str);
        /*
          String file = reorganizePathName(str);
              if (file.startsWith("ERROR:")) return file;

              File newFile = new File(file);
              if (!newFile.exists()) return "ERROR: Not exists file : " + file;
              if (newFile.isFile()) return file;
              if (newFile.isDirectory()) return file;
              return "ERROR: Unidentified file : " + file;
              */
    }
    private String reorganizePathName(String str)
    {
        String currentDir = currentDirFile.getAbsolutePath().trim();
        String sp = File.separator;
        if (str == null) return "";
        else str = str.trim();
        if (str.equals("")) return currentDir;

        File tempF = currentDirFile;
        while(tempF.getParentFile()!=null) tempF = tempF.getParentFile();
        String currentRoot = tempF.getAbsolutePath();
        String parent = "";
        boolean cTag = false;
        try
        {
            parent = currentDirFile.getParentFile().getAbsolutePath();
        }
        catch(NullPointerException ne)
        {
            cTag = true;
        }
        if (str.equals(".")) return currentRoot;
        if (str.equals(".."))
        {
            if (cTag) return currentRoot;
                else return parent;
        }

        if (str.equals(sp)) return currentRoot;

        if (str.equals("?ex")) return getRootDir() + "doc"+sp+"file_exchange";
        if (str.equals("?misel")) return getRootDir() + "misel";
        if (str.equals("?class")) return getRootDir() + "doc"+sp+"WEB-INF"+sp+"classes";
        if (str.equals("?um")) return getRootDir() + "doc"+sp+"um";
        if (str.equals("?doc")) return getRootDir() + "doc";
        if (str.equals("?resin")) return getRootDir();
        if (str.equals("?main")) return getRootPath();
        if (str.equals("?scenario")) return util.getRootScenarioPath();
        if (str.equals("?scen")) return util.getRootScenarioPath();

        if (str.startsWith("##")) return getFileWithNumber(currentDir, str);

        if (str.indexOf(sp) < 0)
        {
            if (currentDir.endsWith(sp)) return currentDir + str;
            else return currentDir + sp + str;
        }

        if (str.startsWith("."+sp))
        {
            if (currentDir.endsWith(sp)) str = currentDir + str.substring(2);
              else str = currentDir + str.substring(1);
        }
        if (str.startsWith(".." + sp))
        {
          if (cTag) return currentRoot;
            else
            {
              if (parent.endsWith(sp)) str = parent + str.substring(3);
              else str = parent + str.substring(2);

            }
        }
        if (str.startsWith(sp)) str = currentRoot + str.substring(1);

        if (str.startsWith("?ex" + sp)) str = getRootDir() + "doc"+sp+"file_exchange" + str.substring(3);
        if (str.startsWith("?misel" + sp)) str =  getRootDir() + "misel" + str.substring(6);
        if (str.startsWith("?class" + sp)) str =  getRootDir() + "doc"+sp+"WEB-INF"+sp+"classes" + str.substring(6);
        if (str.startsWith("?um" + sp)) str =  getRootDir() + "doc"+sp+"um" + str.substring(3);
        if (str.startsWith("?doc" + sp)) str =  getRootDir() + "doc" + str.substring(4);
        if (str.startsWith("?resin" + sp)) str =  getRootDir() + str.substring(6);
        if (str.startsWith("?main" + sp)) str =  getRootPath() + str.substring(6);
        if (str.startsWith("?scenario")) str = util.getRootScenarioPath() + str.substring(9);
        if (str.startsWith("?scen")) str = util.getRootScenarioPath() + str.substring(5);

        String achar = "";
        String st = "";
        for(int i=0;i<str.length();i++)
        {
            achar = str.substring(i, i+1);
                if (achar.equals("`")) st = st + sp;
                else if (achar.equals("|")) st = st + ":";
                else st = st + achar;
        }
        return st;
    }

    private String getFileWithNumber(String currDir, String num)
    {
        if ((currDir == null)||(currDir.trim().equals(""))) return "ERROR: Null Directory";
            if ((num == null)||(num.trim().equals(""))) return "ERROR: Null num";

            currDir = currDir.trim();
            num = num.trim();

            File dir = new File(currDir);

        if ((!dir.exists())||(!dir.isDirectory())) return "ERROR: Not Directory : " + currDir;

            boolean isDir = false;
            if (num.toLowerCase().startsWith("##d:")) isDir =true;
            else if (num.toLowerCase().startsWith("##f:")) isDir = false;
            else return "ERROR: Invalid File or Directory tag : " + num;

            int fileNum = 0;

            try
            {
                fileNum = Integer.parseInt(num.substring(4).trim());
            }
            catch(NumberFormatException ne)
            {
                return "ERROR: Unable to parse this File or Directory tag number : " + num;
            }

            if (fileNum <= 0) return "ERROR: Zero or less number is not allowed : " + num;

            fileNum--;
            java.util.List<File> listFile = new ArrayList<File>();
            java.util.List<File> listDirectory = new ArrayList<File>();
            File[] files = dir.listFiles();
            if (files.length == 0) return "ERROR: No file or Directory : " + currDir;
            File tempFile = null;
            int n = 0;

            while(true)
            {
                    try
                    {
                            tempFile = files[n];
                    }
                    catch(ArrayIndexOutOfBoundsException ae)
                    {
                        break;
                    }
                    catch(NullPointerException ae)
                    {
                        return "ERROR: NullPointerException at "+currDir+" : " + ae.getMessage();
                            //break;
                    }
                    if(tempFile.isFile()) listFile.add(tempFile);
                    else if(tempFile.isDirectory()) listDirectory.add(tempFile);

                    n++;
            }
            listFile = sortFileList(listFile);
            listDirectory = sortFileList(listDirectory);

            File find = null;

            if (isDir)
            {
                    if (listDirectory.size() > 0)
                    {
                      for(int i=0;i<listDirectory.size();i++)
                        {
                            tempFile = listDirectory.get(i);
                                    if (i == fileNum) find = tempFile;
                          }
                    }
            }
            else
            {
                    if (listFile.size() > 0)
                    {
                        for(int i=0;i<listFile.size();i++)
                        {
                                tempFile = listFile.get(i);
                                    if (i == fileNum) find = tempFile;
                        }
                }
            }
            if (find == null) return "ERROR: File or Directory Not Found this number tag : " + num;
      return find.getAbsolutePath();
    }

    private String displayDir(File dir, HttpServletRequest req)
    {
        if (dir == null) return "null Directory";
            if (!dir.isDirectory()) return "Not Directory : " + dir.getAbsolutePath();

            String res = "<br> Directory and File lists of " + dir.getAbsolutePath() + " Directory at " + gUtil.getFormatedNowDate() + "<br>";

            File currDir = currentDirFile;
            if ((!currDir.exists())||(!currDir.isDirectory())) res = res + "WARNING : Current Directory is Invalid " + currentDirFile.getAbsolutePath() + "<br>";

            java.util.List<File> listFile = new ArrayList<File>();
            java.util.List<File> listDirectory = new ArrayList<File>();
            File[] files = dir.listFiles();
            if (files.length == 0) return res + "<br>No file or Directory";
            File tempFile = null;
            int n = 0;

        String downloadURL = null;

        if (dir.getName().toLowerCase().equals("file_exchange"))
        {
            ByteTransform bt = new ByteTransform();
            String pathEncoded = bt.encodeHexString(dir.getAbsolutePath());
            downloadURL = util.getBaseURLFromRequest(req);
            String urlCom = "/FileDownloader?path="+pathEncoded+"&adminID="+util.getSessionTag()+"&file=";
            if (downloadURL == null) downloadURL = util.getBaseURL() + "servlet/"+urlCom;
            else downloadURL = downloadURL + urlCom;
        }

            while(true)
            {
                    try
                    {
                            tempFile = files[n];
                    }
                    catch(ArrayIndexOutOfBoundsException ae)
                    {
                        break;
                    }
                    catch(NullPointerException ae)
                    {
                        res = res + "<br>NullPointerException : " + ae.getMessage();
                            break;
                    }
                    if(tempFile.isFile()) listFile.add(tempFile);
                    else if(tempFile.isDirectory()) listDirectory.add(tempFile);
                    else {}

                    n++;
            }
            listFile = sortFileList(listFile);
            listDirectory = sortFileList(listDirectory);
            if (listDirectory.size() > 0)
            {
                res = res + "<br><table>" +
                            "<tr><th>.</th>" +
                            "<th>Directory Name</th>" +
                            "<th>Modified Date</th></tr>";
                for(int i=0;i<listDirectory.size();i++)
                {
                    tempFile = listDirectory.get(i);
                        java.util.Date dt = getDate(tempFile.lastModified());
                        SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat sd2 = new SimpleDateFormat("HH:mm:ss");
                        String dte = sd1.format(dt) + " " + sd2.format(dt);
                        res = res + "<tr><td>&lt;DIR&gt; :"+(i+1)+"&nbsp;&nbsp;</td><td> " + tempFile.getName() + " </td><td>&nbsp;" +dte+ " </td></tr>";
                        //DirectoryNodeElement temp = new DirectoryNodeElement(current, tempFile.getName(), getDate(tempFile.lastModified()));
                }
                    res = res + "</table>";
            }
            else res = res + "<br> No Directory";

            if (listFile.size() > 0)
            {
                res = res + "<br><table>" +
                            "<tr><th>.</th>" +
                            "<th>File Name</th>" +
                            "<th>Size</th>" +
                            "<th>Modified Date</th></tr>";
                for(int i=0;i<listFile.size();i++)
                {
                    String fileDownHead = "";
                    String fileDownTail = "";

                    tempFile = listFile.get(i);

                    if (downloadURL != null)
                    {
                        fileDownHead = "<a href='" + downloadURL + tempFile.getName() + "'>";
                        fileDownTail = "</a>";
                    }


                        java.util.Date dt = getDate(tempFile.lastModified());
                        SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat sd2 = new SimpleDateFormat("HH:mm:ss");
                        String dte = sd1.format(dt) + " " + sd2.format(dt);
                        res = res + "<tr><td>FILE : "+(i+1)+"&nbsp;&nbsp;</td><td> " + fileDownHead + tempFile.getName() + fileDownTail + " </td><td align='right'>&nbsp;" + addComma((int)tempFile.length()) + " </td><td>&nbsp;" + dte + " </td></tr>";
                        //FileInformation fi = new FileInformation(getDate(tempFile.lastModified()), (int)tempFile.length(), tempFile.getName());
                        //list.add(fi);
                }
                    res = res + "</table>";
            }
            else res = res + "<br> No File";

      return res;
    }
    private String addComma(int val)
    {
        String str = "" + val;
            String res = "";
            int cnt = 0;
            for(int i=str.length();i>0;i--)
            {
                cnt++;
                String achar = str.substring((i-1), i);
                    res = achar + res;
                    if ((cnt % 3) == 0) res = "," + res;
            }
            if (res.startsWith(",")) res = res.substring(1);
            return res;
    }
    private List<File> sortFileList(List<File> li)
    {

        int n = li.size();
        List<Object> ol = new ArrayList<Object>();
        for (int i=0;i<n;i++) ol.add((Object) li.get(i));
        ol = gUtil.sortObjectList(ol);
        li = new ArrayList<File>();
        for (int i=0;i<n;i++) li.add((File) ol.get(i));
        return li;

    }


  private java.util.Date getDate(Long lng)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(lng);
        return cal.getTime();
    }

        private String from8859(String src) // 8859??? KSC5601? ??
      {
          if (src== null) return "";

          try
              {
                  String ret = new String(src.getBytes("8859_1"),"KSC5601"); // ?/? (decoding)
                    return ret;
                }
            catch(UnsupportedEncodingException u)
              {
                  return src;
                }

        }

    private String to8859(String str) // ??? ??? ?? ?? ?? ??? ?? ???
      {
          String result = null;
            try
              {
                  result = new String(str.getBytes("KSC5601"), "8859_1"); // ?/? (encoding)
                }
            catch(UnsupportedEncodingException u) {}
            return result;
        }
//private String getCommandPage(String currDir)
//{
//    return getCommandPage(currDir, null);
//}
private String getCommandPage(String currDir, HttpServletRequest req)
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

    String page = "<font color='green'>"
                + "<center>"
                + "<h1>Input Dos Command</h1></font><br>"
                + "<form method='post' action='"+ url +"'>"
                + "<input type='hidden' name='adminID' value='"+SESSION_TAG+"'>"
                + "<input type='hidden' name='currentPath' value='"+currDir+"'>"
                + "<input type='hidden' name='lastCommand' value='"+currentCommand+"'>"
                + "<table border=2 cellpading=10>"
                + "<tr>"
                + "<td>Command</td><td><select name='command'>";
    if ((currentCommand.equals("edit"))||(currentCommand.equals("create")))
    {
        page = page + "    <option label='save' value='save'>save</option>";
    }
    page = page + "    <option label='cd' value='cd'>cd</option>"
                + "    <option label='dir' value='dir'>dir</option>"
                + "    <option label='md' value='md'>md</option>"
                + "    <option label='del' value='del'>del</option>"
                + "    <option label='copy' value='copy'>copy</option>"
                + "    <option label='move' value='move'>move</option>";

    if (!((currentCommand.equals("edit"))||(currentCommand.equals("create"))))
    {
        page = page + "    <option label='edit' value='edit'>edit</option>";
        page = page + "    <option label='create' value='create'>create</option>";
    }

    page = page + "    <option label='logout' value='logout'>logout</option>"
                + "  </select></td>"
                + "</tr>";


    if ((currentCommand.equals("edit"))||(currentCommand.equals("create")))
    {
        String p1 = parameter1;
        String p2 = parameter2;
        if (currentCommand.equals("create"))
        {
            p1 = "";
            p2 = "";
        }
        page = page + "<tr><td>Source</td><td><input type=text name='param1' size='30' value='" + p1 + "'>&nbsp;&nbsp;</td></tr>"
                    + "<tr><td>Target</td><td><textarea name='param2' rows=12 cols=80>" + p2 + "</textarea></td></tr>";
    }
    else
    {
        page = page + "<tr><td>Source</td><td><input type=text name='param1' size='30'>&nbsp;&nbsp;</td></tr>"
                    + "<tr><td>Target</td><td><input type=text name='param2' size='30'>&nbsp;&nbsp;</td></tr>";
    }


    page = page + "</table>"
                + "<table border=2 cellpading=10>"
                + "<h2>"
                + "<tr><td>"
                + "<input type='submit' value='Submit'></td>"
                + "<td>"
                + "<input type='reset' value='Cancel'></td>"
                + "</tr>"
                + "</table>"
                + "</h2>"
                + "</center>"
                + "</form>"
                + "<br>Dir. Abbr: ex, misel, class, um, doc, resin || Number Tag : ##F: (File), ##D: (Dir.)";
    return page;
}
        private String getHTML(String title, String body, HttpServletRequest req)
        {
            String currentDir = currentDirFile.getAbsolutePath();
            String commandPage = getCommandPage(currentDir, req); //commandPage.replace("%!CURR_DIR!%", currentDir);
            return "<html><head><title>" + to8859(title) + "</title></head><body>"+ to8859(commandPage) + "<br><br>Current Directory : " + currentDir + "<br><br><font size='3' color='blue'><br><br>"
                       + to8859(body) + "</font>" + "</body></html>";
        }
    private String getReturnHTML(String str, HttpServletRequest req)
      {
          String currentDir = currentDirFile.getAbsolutePath();
          String commandPage = getCommandPage(currentDir, req);
          return "<html><head><title>DosFileHandlerWithDoGet Result return message...</title>" +
                   "</head><body>" + to8859(commandPage) + "<br><br>Current Directory : " + currentDir + "<br><br>" + "<font size='3' color='red'><br><br>" +
                         /*"<center>" +*/ to8859(str) + /*"</center>" +*/ "</font>" + "<br><br></body></html>";
        }
    private String getRootDir()
    {
        String dd = FileUtil.getWorkingDirPath().trim();
        if (!dd.endsWith(File.separator)) dd = dd + File.separator;
        return dd;
    }
    private String getRootPath()
    {
        String dd = util.getRootPath().trim();
        if (!dd.endsWith(File.separator)) dd = dd + File.separator;
        return dd;
    }
    private boolean isEmpty(File file)
    {
        if (file == null) return false;
        if (!file.isDirectory()) return false;
        boolean empty = true;
        File[] list = file.listFiles();
        for(File f:list)
        {
            if (f.isFile()) return false;
            if (f.isDirectory())
            {
                if (!isEmpty(f)) empty = false;
            }
        }
        return empty;
    }
    private String deleteEmptyDirectory(File file)
    {
        if (file == null) return "File is null...";
        if (!file.isDirectory()) return "This is not a directory : " + file.getAbsolutePath();
        boolean empty = true;
        File[] list = file.listFiles();
        for(File f:list)
        {
            if (f.isFile()) return "This is NOT a EMPTY directory : " + file.getAbsolutePath();
            if (f.isDirectory())
            {
                String rr = deleteEmptyDirectory(f);
                if (rr != null) return rr;
            }
        }
        try
        {
            if (!file.delete()) return "This file deleting failure : " + file.getAbsolutePath();
        }
        catch(SecurityException se)
        {
            return "This file deleting failure(SecurityException) : " + file.getAbsolutePath() + " : " + se.getMessage();
        }

        return null;
    }
    private String deleteProcess(String file)
    {
        if (file == null) return "File name is null...";
        file = file.trim();
        if (file.equals("")) return "File name is empty...";
        File f = new File(file);
        if (f.exists())
        {
            if (f.isFile())
            {
                if (f.delete()) return null;
                else return "This file deleting failure. : " + file;
            }
            else if (f.isDirectory())
            {
                if (isEmpty(f)) return deleteEmptyDirectory(f);
                else return "This Directory is not empty. : " + file;
            }
        }
        String dir = "";
        String fil = "";
        String temp = file;
        String sp = File.separator;
        while(true)
        {
            int idx = temp.indexOf(sp);
            if (idx < 0)
            {
                fil = temp;
                break;
            }
            dir = dir + temp.substring(0, idx+1).trim();
            temp = temp.substring(idx+1).trim();
        }
        if (dir.equals("")) dir = currentDirFile.getAbsolutePath();
        if (fil.equals("")) return "Invalid File format of Directory : " + file;
        if (!dir.endsWith(sp)) dir = dir + sp;
        File dirS = new File(dir);
        if ((!dirS.exists())||(!dirS.isDirectory())) return "Invalid Directory Format : " + file;
        int count = 0;
        File[] list = dirS.listFiles();
        for(File f1:list)
        {
            if (!f1.isFile()) continue;
            if (checkWildCharacter(f1, fil))
            {
                try
                {
                    if (f1.delete()) count++;
                    else return "This file deleting failure : " + f1.getAbsolutePath();
                }
                catch(SecurityException se)
                {
                    return "This file deleting failure(SecurityException) : " + f1.getAbsolutePath() + " : " + se.getMessage();
                }
            }
        }
        if (count == 0) return "No file with this name or wild character : " + file;
        return null;
    }
    private boolean checkWildCharacter(File file, String name)
    {
        if (name == null) return false;
        if (file == null) return false;
        if (!file.isFile()) return false;
        name = name.trim();
        if (name.equals("")) return false;
        if (name.equals("*.*")) return true;
        if (name.equals("*")) return true;

        String fName = file.getName();
        if (fName.equals(name)) return true;

        String fN = "";
        String fE = "";
        int idx1 = fName.lastIndexOf(".");
        if (idx1 < 0) fN = fName;
        else
        {
            fN = fName.substring(0, idx1);
            fE = fName.substring(idx1 + 1);
        }

        String nN = "";
        String nE = "";
        int idx2 = name.lastIndexOf(".");
        if (idx2 < 0) nN = name;
        else
        {
            nN = name.substring(0, idx2);
            nE = name.substring(idx2 + 1);
        }

        if ((checkName(fN, nN))&&(checkName(fE, nE))) return true;
        return false;
    }
    private boolean checkName(String file, String name)
    {
        if ((name.equals("*"))||(name.equals(file))) return true;

        boolean tag = true;
        for(int i=0;i<name.length();i++)
        {
            String achar = name.substring(i, i+1);
            if ((achar.equals("*"))||(achar.equals("?"))) {}
            else tag = false;
        }
        if (tag) return true;

        boolean start = false;
        boolean end = false;
        String temp1 = name;
        while(temp1.startsWith("*"))
        {
            start = true;
            temp1 = temp1.substring(1);
        }
        while(temp1.endsWith("*"))
        {
            end = true;
            temp1 = temp1.substring(0, temp1.length()-1);
        }

        //int idxE = temp1.indexOf("*");
        //int idxQ = temp1.indexOf("?");

        if ((start)&&(end))
        {
            int idx = file.indexOf(temp1);
            if (idx >= 0) return true;
        }
        else if (start)
        {
            if (file.endsWith(temp1)) return true;
        }
        else if (end)
        {
            if (file.startsWith(temp1)) return true;
        }

        boolean same = true;
        String achar1 = "";
        String achar2 = "";
        for(int i=0;i<name.length();i++)
        {
            try
            {
                if (start)
                {
                    achar1 = name.substring(name.length()-(i+1), name.length()-i);
                    achar2 = file.substring(file.length()-(i+1), file.length()-i);
                }
                else
                {
                    achar1 = name.substring(i, i+1);
                    achar2 = file.substring(i, i+1);
                }
            }
            catch(Exception ee)
            {
                return false;
            }
            if (achar1.equals("*")) break;

            if ((achar1.equals("?"))||(achar1.equals(achar2))) {}
            else return false;
        }
        return true;
    }
}

