/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.ws;

import java.util.*;
import java.util.zip.ZipEntry;
import java.io.*;
import java.text.*;
import gov.nih.nci.caadapter.dvts.common.util.FileUtil;
import gov.nih.nci.caadapter.dvts.common.util.Config;
import gov.nih.nci.caadapter.dvts.common.util.vom.ManageVOMFile;
import gov.nih.nci.caadapter.dvts.common.function.DateFunction;
import gov.nih.nci.caadapter.dvts.common.function.FunctionException;
import gov.nih.nci.caadapter.dvts.common.Message;
import gov.nih.nci.caadapter.dvts.common.util.ZipUtil;
import gov.nih.nci.caadapter.dvts.common.util.FileSearchUtil;
import gov.nih.nci.caadapter.dvts.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.dvts.common.validation.ValidatorResults;
//import gov.nih.nci.caadapter.dvts.hl7.transformation.TransformationServiceUtil;
//import gov.nih.nci.caadapter.dvts.hl7.v2v3.tools.ZipUtil;

import edu.knu.medinfo.hl7.v2tree.ByteTransform;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: kium
 * Date: Jun 16, 2009
 * Time: 12:46:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class CaadapterWSUtil
{
    private String[] ERROR_LEVELS = new String[] {"FATAL", "ERROR", "WARNING", "INFO", "NONE"};
    private String[] PROPERTY_ITEMS = new String[] {"scs", "h3s", "map", "schema", "comment", "date", "schemaValidation"};
    private String PROPERTY_FILE_NAME = "properties.txt";
    private String PASSWORD_FILE_NAME = "password.txt";
    private String SERVICE_NAME = Config.PRODUCT_NAME;
    private String ROOT_PATH = "C:\\"+SERVICE_NAME + File.separator;
    private String ROOT_WEB_DIR = "C:\\resin-2.0.0\\doc\\" + SERVICE_NAME + File.separator;
    private String ENVIRONMENT_PROPERTY_FILE_NAME = "caAdapterDVTS_WSEnvironment.properties";
    private String SCENARIO_DIR_NAME = "scenarios";
    private String ROOT_SCENARIO_PATH = ROOT_PATH + SCENARIO_DIR_NAME + File.separator;
    private String WORKING_DIR_NAME = "working";
    private String WORK_DIR = ROOT_PATH + WORKING_DIR_NAME + File.separator;
    //private String WORK_DIR = FileUtil.getUIWorkingDirectoryPath().trim();
    private String ROOT_URL = null;
    private String BASE_URL = null;
    private String SERVLET_URL = null;
    private String OUTPUT_DIR_URL = null;
    private String OUTPUT_DIR_NAME = "output";
    private String OUTPUT_DIR = ROOT_WEB_DIR + OUTPUT_DIR_NAME + File.separator;

    private String SESSION_FILE_NAME = "session.txt";
    private String ACCESS_LOG_FILE_NAME = "accessLog.txt";
    private String KEY_LOG_FILE_NAME = "keyLog.txt";
    private String TAG_V2_V3 = "%V2-V3%";
    private String TAG_REPLACE_POINT = "<!-- @@!REPLACE_POINT!@@ -->";
    private String TAG_REPLACE_POINT_01 = "<!-- @@!REPLACE_POINT_01!@@ -->";
    private String TAG_REPLACE_POINT_02 = "<!-- @@!REPLACE_POINT_02!@@ -->";
    private String TAG_REPLACE_POINT_03 = "<!-- @@!REPLACE_POINT_03!@@ -->";
    private String SESSION_TAG = "session99";
    private String ADMINISTRATOR_ID = "Administrator";
    private String ADMINISTRATOR_INITIAL_PASSWORD = "1234A";
    private String ACCESS_FAILURE_TAG = "AccFailure";
    private String SERVICE_SUBDIRECTORY = "servlet";

    private boolean arePropertiesFromFile = false;

    private int LOG_LIMIT = 500;
    private boolean responseTypeXML = false;
    private boolean alreadyInitialized = false;

    private DateFunction dateUtil = new DateFunction();
    GeneralUtilitiesWS util = new GeneralUtilitiesWS();

    public CaadapterWSUtil()
    {
        initialize(false);
    }
    public CaadapterWSUtil(boolean responseXML)
    {
        initialize(responseXML);
    }
    public CaadapterWSUtil(boolean responseXML, String serviceName, String propertyFile)
    {
        ENVIRONMENT_PROPERTY_FILE_NAME = propertyFile;
        SERVICE_NAME = serviceName;
        initialize(responseXML);
    }
    private void initialize(boolean responseXML)
    {

        //System.out.println("CCC AA current directory : " + (new File("")).getAbsolutePath() + ", FileUtil.getWorkingDirPath()=" + FileUtil.getWorkingDirPath());
        setupEnvironmentProperties();

        if (!WORK_DIR.endsWith(File.separator)) WORK_DIR = WORK_DIR + File.separator;
        //System.out.println("CCC BB working directory : " + WORK_DIR);
        tidyOutputDir();
        tidyWorkDir();
    }

    public void setReturnTypeXML(boolean responseXML)
    {
        responseTypeXML = responseXML;
    }
    private void setupEnvironmentProperties()
    {
        setupEnvironmentProperties(null);
    }
    private void setupEnvironmentProperties(List<String> list)
    {
        if (alreadyInitialized) return;
        alreadyInitialized = true;
        boolean isRepeated = false;
        if (list == null) list = searchEnvironmentPropertiesFile();
        else isRepeated = true;

        if ((list == null)||(list.size() == 0)) return;

        String serviceName = null;
        String serviceSubdirectory = null;
        String rootDirectory = null;
        String rootWebServiceDirectory = null;
        String rootURL = null;



        for (String line:list)
        {
            //System.out.println("CCCC list line : " + line);
            line = line.trim();
            int idx = line.indexOf("=");
            if (idx <= 0) continue;
            String key = line.substring(0, idx).trim();
            String data = line.substring(idx + 1).trim();

            if ((key.equals(""))||(data.equals(""))) continue;

            if (key.equalsIgnoreCase("SERVICE_NAME")) serviceName = data;
            if (key.equalsIgnoreCase("ROOT_DIRECTORY")) rootDirectory = data;
            if (key.equalsIgnoreCase("ROOT_WEB_SERVICE_DIRECTORY")) rootWebServiceDirectory = data;
            if (key.equalsIgnoreCase("ROOT_URL")) rootURL = data;
            if (key.equalsIgnoreCase("SERVICE_SUBDIRECTORY")) serviceSubdirectory = data;
            if (key.equalsIgnoreCase("LOG_LIMIT"))
            {
                int limit = 0;
                try
                {
                    limit = Integer.parseInt(data);
                }
                catch(NumberFormatException ne)
                {}
                if (limit > (LOG_LIMIT / 2)) LOG_LIMIT = limit;
            }
        }
        if (serviceName != null) SERVICE_NAME = serviceName;

        if ((rootDirectory == null)||(rootDirectory.trim().equals("")))
        {
            String workD = FileUtil.getWorkingDirPath();
            if (!workD.endsWith(File.separator)) workD = workD + File.separator;
            File d = new File(workD);
            if ((d.getName().equalsIgnoreCase("bin"))||
                (d.getName().equalsIgnoreCase("dist"))) d = d.getParentFile();

            File f = (new FileSearchUtil()).searchDir(d, getServiceName(), new String[] {"localhost", "work", "temp"});

            if (f == null)
            {
                System.out.println("Cannot find service root directory : " + getServiceName());
                return;
            }
            //else System.out.println("CCCC Service root directory is found : " + f.getAbsolutePath());




            //while(d.getParentFile() != null) d = d.getParentFile();

            if (rootDirectory == null) rootDirectory = f.getAbsolutePath();
            else
            {
                File dir = new File(rootDirectory);
                if ((dir.exists())&&(dir.isDirectory())) {}
                else rootDirectory = d.getAbsolutePath();
            }
        }
        if (!rootDirectory.endsWith(File.separator)) rootDirectory = rootDirectory + File.separator;

        if (rootWebServiceDirectory == null) rootWebServiceDirectory = rootDirectory + "doc" + File.separator;
        else
        {
            File dir = new File(rootWebServiceDirectory);
            if ((dir.exists())&&(dir.isDirectory()))
            {
                if (!rootWebServiceDirectory.endsWith(File.separator)) rootWebServiceDirectory = rootWebServiceDirectory + File.separator;
            }
            else rootWebServiceDirectory = rootDirectory + "doc" + File.separator;
        }

        if (rootURL == null) rootURL = "http://***:8080/";

        boolean isAllExist = true;
        ROOT_PATH = rootDirectory + SERVICE_NAME + File.separator;
        if (!util.isDirExist(ROOT_PATH)) isAllExist = false;
        ROOT_WEB_DIR = rootWebServiceDirectory + SERVICE_NAME + File.separator;
        if (!util.isDirExist(ROOT_WEB_DIR)) isAllExist = false;
        ROOT_SCENARIO_PATH = ROOT_PATH + getScenarioDirName() + File.separator;
        if (!util.isDirExist(ROOT_SCENARIO_PATH)) isAllExist = false;
        if (!util.isDirExist(ROOT_SCENARIO_PATH + getAdministratorID())) isAllExist = false;

        OUTPUT_DIR = ROOT_WEB_DIR + getOutputDirName() + File.separator;

        if (!rootURL.endsWith("/")) rootURL = rootURL + "/";

        BASE_URL = rootURL;
        ROOT_URL = rootURL + SERVICE_NAME + "/";
        if (serviceSubdirectory == null) serviceSubdirectory = "";
        if ((!serviceSubdirectory.equals(""))&&(!serviceSubdirectory.equalsIgnoreCase(SERVICE_SUBDIRECTORY)))
        {
            SERVICE_SUBDIRECTORY = serviceSubdirectory;
            SERVLET_URL = rootURL + SERVICE_SUBDIRECTORY + "/";
        }
        else SERVLET_URL = rootURL + SERVICE_SUBDIRECTORY + "/";
        OUTPUT_DIR_URL = ROOT_URL + getOutputDirName() + "/";



        //ROOT_PATH = "C:\\"+SERVICE_NAME + File.separator;
    //private String ROOT_WEB_DIR = "C:\\resin-2.0.0\\doc\\" + SERVICE_NAME + File.separator;
    //private String ENVIRONMENT_PROPERTY_FILE_NAME = "caAdapterDVTS_WSEnvironment.properties";
    //private String SCENARIO_DIR_NAME = "scenarios";
    //ROOT_SCENARIO_PATH = ROOT_PATH + SCENARIO_DIR_NAME + File.separator;
    //private String WORKING_DIR_NAME = "working";
    WORK_DIR = ROOT_PATH + WORKING_DIR_NAME + File.separator;




        if ((!isAllExist)&&((!isRepeated)))
        {
            List<String> ll = generateEnvironmentPropertiesFile();
            if ((ll != null)&&(ll.size() > 0)) setupEnvironmentProperties(ll);
        }
    }

    private List<String> searchEnvironmentPropertiesFile()
    {
        String workDir = FileUtil.getWorkingDirPath().trim();
        File workDirS = new File(workDir);
        if (workDirS.getName().equalsIgnoreCase("bin")) workDirS = workDirS.getParentFile();
        File caAdapterWSDirF = (new FileSearchUtil()).searchDir(workDirS, this.getServiceName(), new String[] {"localhost", "work", "temp"});
        if (caAdapterWSDirF == null)//||(caAdapterWSDir.trim().equals("")))
        {
            workDir = FileUtil.getWorkingDirPath().trim();
            if (!workDir.endsWith(File.separator)) workDir = workDir + File.separator;
            workDir = workDir + WORKING_DIR_NAME + File.separator;
            //File workDirS2 = new File(workDir);
            //if (!workDirS2.exists()) workDirS2.mkdirs();
        }
        else workDir = caAdapterWSDirF.getAbsolutePath();

        if (!workDir.endsWith(File.separator)) workDir = workDir + File.separator;

        List<String> list = null;
        try
        {
            list = FileUtil.readFileIntoList(workDir + ENVIRONMENT_PROPERTY_FILE_NAME);
            if ((list != null)&&(list.size() > 0))
            {
                arePropertiesFromFile = true;
                return list;
            }
        }
        catch(IOException ie)
        {}
        return generateEnvironmentPropertiesFile();
    }
    private List<String> generateEnvironmentPropertiesFile()
    {
        //String caAdapterWSDir = (new FileSearchUtil()).searchDir(getServiceName());
        String str = FileUtil.getWorkingDirPath();
        File workDirS = new File(str);
        if (workDirS.getName().equalsIgnoreCase("bin")) workDirS = workDirS.getParentFile();

        File caAdapterWSDirF = (new FileSearchUtil()).searchDir(workDirS, this.getServiceName(), new String[] {"localhost", "work", "temp"});
        if (caAdapterWSDirF == null)//||(caAdapterWSDir.trim().equals("")))
        {
            System.out.println("#### Not found "+getServiceName()+" dir : ");
            return null;
        }
        else System.out.println("CCCC found service dir "+getServiceName()+" dir : ");

        String caAdapterWSDir = caAdapterWSDirF.getAbsolutePath();
        if (!caAdapterWSDir.endsWith(File.separator)) caAdapterWSDir = caAdapterWSDir + File.separator;
        String metaInf = caAdapterWSDir + "META-INF" + File.separator;
        String scenarioDir = metaInf + getServiceName() + File.separator + getScenarioDirName();
        File sDir = new File(scenarioDir);
        if ((!sDir.exists())||(!sDir.isDirectory()))
        {
            if (!sDir.mkdirs())
            {
                System.out.println("#### Scenario Directory creation Failure : ");
                return null;
            }
        }

        File adminDir = new File(scenarioDir + File.separator + getAdministratorID());
        if ((!adminDir.exists())||(!adminDir.isDirectory()))
        {
            if (adminDir.mkdirs())
            {
                if (!savePassword(adminDir.getAbsolutePath(), ADMINISTRATOR_INITIAL_PASSWORD, util.getNowDate() + "99876"))
                {
                    System.out.println("#### Administartor initial password setup failure : ");
                }
                System.out.println("#### Administartor Directory successfully created : " + adminDir.getAbsolutePath());
            }
            else System.out.println("#### Administartor Directory creation Failure : ");
        }

        String tempURL = "";
        String tempSub = "";
        if (arePropertiesFromFile)
        {
           tempURL = BASE_URL;
           tempSub = SERVICE_SUBDIRECTORY;
        }
        List<String> list = new ArrayList<String>();
        list.add("SERVICE_NAME=" + getServiceName());
        list.add("ROOT_DIRECTORY=" + metaInf);
        list.add("ROOT_WEB_SERVICE_DIRECTORY=" + (new File(caAdapterWSDir)).getParent());
        list.add("ROOT_URL=" + tempURL);
        list.add("SERVICE_SUBDIRECTORY=" + tempSub);
        list.add("LOG_LIMIT=" + LOG_LIMIT);

        //String workDir = null;//FileUtil.getWorkingDirPath().trim();  ==








        //if (!workDir.endsWith(File.separator)) workDir = workDir + File.separator;
        try
        {
            String prop = "";
            for (String line:list) prop = prop + line + "\r\n";
            //FileWriter fw = new FileWriter(workDir + ENVIRONMENT_PROPERTY_FILE_NAME);
            FileWriter fw = new FileWriter(caAdapterWSDir + ENVIRONMENT_PROPERTY_FILE_NAME);
            fw.write(prop);
            fw.close();
        }
        catch(Exception ie)
        {
            System.out.println("#### Master property file saving Failure : ");
        }
        arePropertiesFromFile = false;
        return list;
    }
    public void setBaseURLToPropertyfile(HttpServletRequest req)
    {
        if (req == null) return;
        StringBuffer urlB = req.getRequestURL();
        if (urlB == null) return;
        String url = urlB.toString().trim();
        if (url.equals("")) return;

        List<String> list = searchEnvironmentPropertiesFile();
        if ((list == null)||(list.size() == 0)) return;

        String rootURL = null;
        String serviceSubdirectory = null;

        String ROOT_URL = "ROOT_URL";
        String SERVICE_SUBDIRECTORY = "SERVICE_SUBDIRECTORY";
        for (String line:list)
        {
            line = line.trim();
            int idx = line.indexOf("=");
            if (idx <= 0) continue;
            String key = line.substring(0, idx).trim();
            String data = line.substring(idx + 1).trim();

            if ((key.equals(""))||(data.equals(""))) continue;

            if (key.equalsIgnoreCase(ROOT_URL)) rootURL = data;
            if (key.equalsIgnoreCase(SERVICE_SUBDIRECTORY)) serviceSubdirectory = data;
        }
        if (rootURL == null) rootURL = "";
        else rootURL = rootURL.trim();

        if (rootURL.equals("")) return;

        if (url.equals(rootURL)) return;

        if (url.indexOf(rootURL) >= 0) return;

        int idx = url.indexOf("//");
        if (idx < 0) return;
        String temp0 = url.substring(idx + 2);
        String temp1 = url.substring(0, idx+2);
        idx = temp0.indexOf("/");
        if (idx < 0) return;
        temp1 = temp1 + temp0.substring(0, idx+1);
        temp0 = temp0.substring(idx + 1);
        if (!temp0.startsWith(getServiceName()+"/")) return;
        temp0 = temp0.substring((getServiceName()+"/").length());
        idx = temp0.indexOf("/");
        if (idx < 0) return;
        String temp2 = temp0.substring(0, idx);

        try
        {
            String workDir = FileUtil.getWorkingDirPath().trim();
            if (!workDir.endsWith(File.separator)) workDir = workDir + File.separator;
            String prop = "";

            FileWriter fw = new FileWriter(workDir + ENVIRONMENT_PROPERTY_FILE_NAME);
            for (String line:list)
            {
                if (line.toUpperCase().startsWith(ROOT_URL)) fw.write(ROOT_URL + "=" + temp1 + "\r\n");
                else if (line.toUpperCase().startsWith(SERVICE_SUBDIRECTORY)) fw.write(SERVICE_SUBDIRECTORY + "=" + temp2 + "\r\n");
                else fw.write(line + "\r\n");
            }
            fw.write(prop);
            fw.close();
        }
        catch(Exception ie)
        {
            System.out.println("#### Master property file saving Failure (2) : " + ie.getMessage());
        }
    }
//    private File searchCaAdapterWorkDir(File dir)
//    {
//        if ((dir == null)||(!dir.isDirectory())) return null;
//        if (dir.getName().equals(getServiceName())) return dir;
//
//        File[] fList = dir.listFiles();
//        for (File file:fList)
//        {
//            File cFile = searchCaAdapterWorkDir(file);
//            if (cFile != null) return cFile;
//        }
//        return null;
//    }
//    private List<String> searchCaAdapterWorkDir()
//    {
//        String workDir = FileUtil.getWorkingDirPath().trim();
//
//        File pFile = null;
//        File dir = null;
//
//        while(true)
//        {
//            dir = new File(workDir);
//            if ((!dir.exists())||(!dir.isDirectory())) return null;
//
//            if (!workDir.endsWith(File.separator)) workDir = workDir + File.separator;
//            pFile = new File(workDir + ENVIRONMENT_PROPERTY_FILE_NAME);
//
//            if ((pFile.exists())&&(pFile.isFile())) break;
//
//            pFile = new File(workDir + SERVICE_NAME + File.separator + ENVIRONMENT_PROPERTY_FILE_NAME);
//
//            if ((pFile.exists())&&(pFile.isFile())) break;
//
//            if (dir.getParentFile() == null)
//            {
//                pFile = util.searchFile(dir, ENVIRONMENT_PROPERTY_FILE_NAME, 4);
//                if (pFile == null) return null;
//                else break;
//            }
//            else dir = dir.getParentFile();
//
//
//            workDir = dir.getAbsolutePath();
//        }
//        List<String> list = null;
//
//        try
//        {
//            list = FileUtil.readFileIntoList(pFile.getAbsolutePath());
//        }
//        catch(IOException ie)
//        {
//            return null;
//        }
//        return list;
//    }

    public int codeFATAL() { return 0; }
    public int codeERROR() { return 1; }
    public int codeWARNING() { return 2; }
    public int codeINFO() { return 3; }
    public int codeNONE() { return 4; }

    public String[] getErrorLevels() { return ERROR_LEVELS; }

    public int codeSCS() { return 0; }
    public int codeH3S() { return 1; }
    public int codeMAP() { return 2; }
    public int codeXSD() { return 3; }
    public int codeCOMMENT() { return 4; }
    public int codeDATE() { return 5; }
    public int codeSchemaValidation() { return 6; }

    public String[] getPropertyItems() { return PROPERTY_ITEMS; }

    public String getPropertyFileName() { return PROPERTY_FILE_NAME; }
    public String getPasswordFileName() { return PASSWORD_FILE_NAME; }
    public String getServiceName() { return SERVICE_NAME; }
    public String getRootPath() { return ROOT_PATH; }
    public String getEnvironmentPropertyFileName() { return ENVIRONMENT_PROPERTY_FILE_NAME; }
    public String getRootScenarioPath() { return ROOT_SCENARIO_PATH; }
    public String getWorkDir() { return WORK_DIR; }
    public String getRootURL() { return ROOT_URL; }
    public String getBaseURL() { return BASE_URL; }
    public String getServletURL() { return SERVLET_URL; }
    public String getOutputDirURL() { return OUTPUT_DIR_URL; }
    public String getOutputDir() { return OUTPUT_DIR; }
    public String getRootWebDir() { return ROOT_WEB_DIR; }
    public String getTagV2V3() { return TAG_V2_V3; }
    public String getTagReplacePoint() { return TAG_REPLACE_POINT; }
    public String getTagReplacePoint01() { return TAG_REPLACE_POINT_01; }
    public String getTagReplacePoint02() { return TAG_REPLACE_POINT_02; }
    public String getTagReplacePoint03() { return TAG_REPLACE_POINT_03; }
    public String getSessionTag() { return SESSION_TAG; }
    public String getAdministratorID() { return ADMINISTRATOR_ID; }
    public String getAccessFailureTag() { return ACCESS_FAILURE_TAG; }
    public String getScenarioDirName() { return SCENARIO_DIR_NAME; }
    public String getWorkingDirName() { return WORKING_DIR_NAME; }
    public String getOutputDirName() { return OUTPUT_DIR_NAME; }

    public String getErrorLevel(int i)
    {
        String str = "";
        try
        {
            str = getErrorLevels()[i];
        }
        catch(ArrayIndexOutOfBoundsException ae)
        {
            str = null;
        }

        return str;
    }

    public void returnMessage(PrintWriter out, String title, int level, String message)
    {
        returnMessage(out, title, level, message, null, null);
    }
    public void returnMessage(PrintWriter out, String title, int level, String message, String link)
    {
//        if (responseTypeXML)
//        {
//            returnMessageXML(out, title, level, message, link);
//        }
//        else
//        {
            String sr = getErrorLevel(level);
            if (sr == null) sr = getErrorLevel(codeNONE());
            util.sendOutMessage(out, title, sr, message, link, null);
//        }
    }

    public String returnMessage(String title, int level, String message)
    {
        return returnMessage(title, level, message, null, null);
    }
    public String returnMessage(String title, int level, String message, String link)
    {
//        if (responseTypeXML)
//        {
//            return returnMessageXML(title, level, message, link);
//        }
//        else
//        {
            String sr = getErrorLevel(level);
            if (sr == null) sr = getErrorLevel(codeNONE());
            return util.sendOutMessage(title, sr, message, link, null);
//        }
    }
    public void returnMessage(PrintWriter out, String title, int level, String message, List<String> displayList)
    {
        returnMessage(out, title, level, message, null, displayList);
    }
    public void returnMessage(PrintWriter out, String title, int level, String message, String link, List<String> displayList)
    {
//        if (responseTypeXML)
//        {
//            returnMessageXML(out, title, level, message, link);
//        }
//        else
//        {
            String sr = getErrorLevel(level);
            if (sr == null) sr = getErrorLevel(codeNONE());
            util.sendOutMessage(out, title, sr, message, link, displayList);
//        }
    }

    public String returnMessage(String title, int level, String message, List<String> displayList)
    {
        return returnMessage(title, level, message, null, displayList);
    }
    public String returnMessage(String title, int level, String message, String link, List<String> displayList)
    {
//        if (responseTypeXML)
//        {
//            return returnMessageXML(title, level, message, link);
//        }
//        else
//        {
            String sr = getErrorLevel(level);
            if (sr == null) sr = getErrorLevel(codeNONE());
            return util.sendOutMessage(title, sr, message, link, displayList);
//        }
    }
    private void returnMessageXML(PrintWriter out, String title, int level, String messageStr, String link)
    {
        out.println(returnMessageXML(title, level, messageStr, link));
    }
    private String returnMessageXML(String title, int level, String messageStr, String link)
    {
        String retLine = "";
        String lf = "\r\n";
        retLine = retLine + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + lf;
        retLine = retLine + "<caAdapterWebServiceResponse title=\""+util.to8859(title)+"\">" + lf;

        String sr = getErrorLevel(level);
        if (sr == null) sr = getErrorLevel(codeNONE());

        String message = "    <ResponseMessage level=\""+sr+"\" levelCode=\""+level+"\">" + util.to8859(messageStr) + "</ResponseMessage>" + lf;
        String tailEle = "";
        if ((link == null)||(link.trim().equals(""))) retLine = retLine + message;
        else
        {
            String fileNameLink = link;
            while(true)
            {
                int idx = fileNameLink.indexOf("/");
                if (idx < 0) break;
                fileNameLink = fileNameLink.substring(idx + 1);
            }
            File zipFile = new File(getOutputDir() + fileNameLink);
            boolean cTag = true;
            ZipUtil zipUtil = null;

            try
            {
                zipUtil = new ZipUtil(zipFile.getAbsolutePath());
            }
            catch(IOException ie)
            {
                String sr1 = getErrorLevel(codeERROR());
                String message1 = "    <ResponseMessage level=\"" + sr1 + "\" levelCode=\"" + codeERROR() +
                                "\">ZIP Util IOException ("+link+ ") : " + ie.getMessage() + "</ResponseMessage>" + lf;
                retLine = retLine + message1;
                retLine = retLine + message;
                cTag = false;
            }

            List<String> entryNames = zipUtil.getEntryNames();
            List<String[]> instanceNames = new ArrayList<String[]>();

            int n = 0;
            boolean reorganizedTag = true;
            //for(String nn:entryNames) System.out.println("DDDDD : entry NAme : " + nn);
            while(true)
            {
                String[] rr = findInstanceInZip(n, entryNames);
                if (rr == null) break;

                if (rr[0].toLowerCase().indexOf("_reorgan") < 0) reorganizedTag = false;
                instanceNames.add(rr);
                n++;
            }
            String[] integratedInstance = findInstanceInZip(-1, entryNames);

            if (instanceNames.size() == 0)
            {
                String sr1 = getErrorLevel(codeERROR());
                String message1 = "    <ResponseMessage level=\"" + sr1 + "\" levelCode=\"" + codeERROR() +
                                "\">No Output Instance : "+link + "</ResponseMessage>" + lf;
                retLine = retLine + message1;
                retLine = retLine + message;
                cTag = false;
            }

            int currentCountT = -1;
            String[] currentInstance = null;

            while(cTag)
            {

                if (currentCountT >= instanceNames.size()) break;
                else if (currentCountT < 0) currentInstance = integratedInstance;
                else currentInstance = instanceNames.get(currentCountT);
                int currentCount = currentCountT + 1;
                currentCountT++;

                if (currentInstance == null) continue;

                String currentXML = currentInstance[0];
                String currentSER = currentInstance[1];

                String xmlMsg = null;


                //try { xmlMsg = TransformationServiceUtil.readFromZip(zipFile ,currentXML); }
                //catch (Exception ie)
                //{
                //    xmlMsg = null;
                //}


//                try
//                {
//                    ZipEntry entry = zipUtil.searchEntryWithWholeName(currentXML);
//                    FileUtil.readFileIntoString(FileUtil.downloadFromURLtoTempFile(zipUtil.getAccessURL(entry)));
//                }
//                catch (Exception ie)
//                {
//                    xmlMsg = null;
//                }
//
//                if ((xmlMsg != null)&&(!xmlMsg.trim().equals(""))) break;


                if ((xmlMsg == null)||(xmlMsg.trim().equals("")))
                {
                    if (currentCount <= 1)
                    {
                        String sr1 = getErrorLevel(codeERROR());
                        String message1 = "    <ResponseMessage level=\"" + sr1 + "\" levelCode=\"" + codeERROR() +
                                        "\">Output zip file reading failure : "+link+")</ResponseMessage>" + lf;
                        retLine = retLine + message1;
                        retLine = retLine + message;
                    }
                    //else
                    //{
                    //    out.println("    </Results>");
                    //}
                    zipFile.delete();
                    break;
                }

                if (((integratedInstance == null)&&(currentCount == 1))||((integratedInstance != null)&&(currentCount == 0)))
                {
                    retLine = retLine + message;
                    retLine = retLine + "    <Results>" + lf;
                    tailEle = "    </Results>" + lf;
                }
                String currentSeq = "" + currentCount;
                if (currentCount == 0) currentSeq = "Integrated";
                retLine = retLine + "        <Result sequence=\""+currentSeq+"\">" + lf;
                String reorganized = "true";
                if (!reorganizedTag) reorganized = "false";
                retLine = retLine + "            <V3Message reorganized=\""+reorganized+"\">" + lf;
                if (xmlMsg.startsWith("<?xml"))
                {
                    xmlMsg = xmlMsg.substring(5);
                    int idxx = xmlMsg.indexOf("<");
                    if (idxx < 0) System.out.println("This V3 message is Invalid XML Format, " + xmlMsg);
                    else xmlMsg = xmlMsg.substring(idxx);
                }
                retLine = retLine + xmlMsg;
                retLine = retLine + "            </V3Message>" + lf;

                ValidatorResults validatorsToShow=new ValidatorResults();

                //try
                //{
                //    validatorsToShow.addValidatorResults((ValidatorResults)TransformationServiceUtil.readObjFromZip(zipFile ,currentSER));
                    //validatorsToShow.addValidatorResults((ValidatorResults)TransformationServiceUtil.readObjFromZip(zipFile ,String.valueOf(currentCount-1)+".ser"));
                //}
                //catch(Exception ee)
                //{
                //    validatorsToShow = null;
                //}
                if (validatorsToShow == null)
                {
                    retLine = retLine + "        </Result>" + lf;
                    continue;
                }

                List<ValidatorResult> results = validatorsToShow.getValidationResult(ValidatorResult.Level.ALL);

                if (results.size() == 0)
                {
                    retLine = retLine + "        </Result>" + lf;
                    continue;
                }

                retLine = retLine + "            <ValidationResults>" + lf;

                for (ValidatorResult validatorResult : results)
                {
                    int levelCode = 0;
                    if (validatorResult.getLevel() == ValidatorResult.Level.FATAL)
                    {
                        levelCode = codeFATAL();
                    }
                    else if (validatorResult.getLevel() == ValidatorResult.Level.ERROR)
                    {
                        levelCode = codeERROR();
                    }
                    else if (validatorResult.getLevel() == ValidatorResult.Level.WARNING)
                    {
                        levelCode = codeWARNING();
                    }
                    else if (validatorResult.getLevel() == ValidatorResult.Level.INFO)
                    {
                        levelCode = codeINFO();
                    }
                    else levelCode = codeNONE();
                    retLine = retLine + "                <ValidationMessage level=\""+getErrorLevel(levelCode)+"\" levelCode=\""+levelCode+"\">" + validatorResult.getMessage() + "</ValidationMessage>" + lf;
                }
                retLine = retLine + "            </ValidationResults>" + lf;
                retLine = retLine + "        </Result>" + lf;
            }
        }
        retLine = retLine + tailEle + "</caAdapterWebServiceResponse>" + lf;
        return retLine;
    }
    private String[] findInstanceInZip(int n, List<String> entryNames)
    {
        String findXML = null;
        String findSER = null;
        for(int i=0;i<2;i++)
        {
            String ins = "_Reorganized";
            if (i == 1) ins = "";
            String entryXML = String.valueOf(n)+ins+".xml";
            if (n < 0) entryXML = "i.xml";
            for (int j=0;j<entryNames.size();j++)
            {
                String name = entryNames.get(j);
                if (name.toLowerCase().endsWith(entryXML.toLowerCase()))
                {
                    findXML = name;
                    break;
                }
            }
            if (findXML != null) break;
        }
        if (findXML == null) return null;
        String entrySER = String.valueOf(n)+".ser";
        if (n < 0) entrySER = "i.ser";
        for (int j=0;j<entryNames.size();j++)
        {
            String name = entryNames.get(j);
            if (name.toLowerCase().endsWith(entrySER.toLowerCase()))
            {
                findSER = name;
                break;
            }
        }
        if (findSER == null) findSER = "";
        return new String[] {findXML, findSER};
    }
    public String returnMessageAndLogging(String title, int code, String res, String userPath, String loginID, String ipAddr, Object sourceObj)
    {
        String out = returnMessage(title, code, res, null, null);
        String sourceClass = "";
        if (sourceObj != null) sourceClass = sourceObj.getClass().getName();
        addLogRecord(userPath, util.getNowDate() + " : " + title + " ("+loginID+") : from " + ipAddr + ", called by " + sourceClass + " => " + res);
        return out;
    }
    public void returnMessageAndLogging(PrintWriter out, String title, int code, String res, String userPath, String loginID, String ipAddr, Object sourceObj)
    {
        returnMessage(out, title, code, res, null, null);
        String sourceClass = "";
        if (sourceObj != null) sourceClass = sourceObj.getClass().getName();
        addLogRecord(userPath, util.getNowDate() + " : " + title + " ("+loginID+") : from " + ipAddr + ", called by " + sourceClass + " => " + res);
    }
    public String returnMessageAndLogging(String title, int code, String res, String userPath, String loginID, String ipAddr, Object sourceObj, List<String> displayList)
    {
        String out = returnMessage(title, code, res, displayList);
        String sourceClass = "";
        if (sourceObj != null) sourceClass = sourceObj.getClass().getName();
        addLogRecord(userPath, util.getNowDate() + " : " + title + " ("+loginID+") : from " + ipAddr + ", called by " + sourceClass + " => " + res);
        return out;
    }
    public void returnMessageAndLogging(PrintWriter out, String title, int code, String res, String userPath, String loginID, String ipAddr, Object sourceObj, List<String> displayList)
    {
        returnMessage(out, title, code, res, displayList);
        String sourceClass = "";
        if (sourceObj != null) sourceClass = sourceObj.getClass().getName();
        addLogRecord(userPath, util.getNowDate() + " : " + title + " ("+loginID+") : from " + ipAddr + ", called by " + sourceClass + " => " + res);
    }
    private String readProperties(String dir, String prop)
    {
        List<String> res = readPropertiesList(dir, prop);
        if (res == null) return null;
        if (res.size() == 0) return null;
        return res.get(0);
    }
    private String[] readPropertiesVOM(String dir)
    {
        List<String> res = readPropertiesList(dir, "VOM");
        if (res == null) return null;
        int size = res.size();
        if (size == 0) return null;
        String[] resArr = new String[size];
        for(int i=0;i<size;i++) resArr[i] = res.get(i);
        return resArr;
    }
    private List<String> readPropertiesList(String dir, String prop)
    {
        if (dir == null) return null;
        dir = dir.trim();
        if (!dir.endsWith(File.separator)) dir = dir + File.separator;
        List<String> lines = null;
        try
        {
            lines = FileUtil.readFileIntoList(dir + getPropertyFileName());
        }
        catch(IOException ie){}

        if ((lines == null)||(lines.size() == 0)) return null;

        List<String> res = new ArrayList<String>();

        for (String line:lines)
        {
            line = line.trim();
            if (line.startsWith("#")) continue;
            if (line.startsWith("!")) continue;
            int index = line.indexOf("=");
            if (index <= 0) continue;
            String head = line.substring(0, index).trim();
            String tail = line.substring(index + 1).trim();
            if (head.equalsIgnoreCase(prop)) res.add(tail);
            else if (head.toUpperCase().startsWith(prop.toUpperCase()))
            {
                if (head.endsWith("]")) res.add(tail);
            }
        }
        return res;
    }

    public String getPropertySCS(String dir) { return readProperties(dir, getPropertyItems()[codeSCS()]); }
    public String getPropertyH3S(String dir) { return readProperties(dir, getPropertyItems()[codeH3S()]); }
    public String getPropertyMAP(String dir) { return readProperties(dir, getPropertyItems()[codeMAP()]); }
    public String getPropertySchema(String dir) { return readProperties(dir, getPropertyItems()[codeXSD()]); }
    public String getPropertyComment(String dir) { return readProperties(dir, getPropertyItems()[codeCOMMENT()]); }
    public String getPropertyDate(String dir) { return readProperties(dir, getPropertyItems()[codeDATE()]); }
    public String[] getPropertyVOM(String dir) { return readPropertiesVOM(dir); }
    public boolean getPropertySchemaValidation(String dir)
    {
        String res = readProperties(dir, getPropertyItems()[codeSchemaValidation()]);
        if (res == null) return false;
        if (res.trim().equalsIgnoreCase("true")) return true;
        else return false;
    }


    public String getPassword(String dir)
    {
        if (dir == null) return null;

        dir = dir.trim();
        if (!dir.endsWith(File.separator)) dir = dir + File.separator;

        List<String> res = null;
        try
        {
            res = FileUtil.readFileIntoList(dir + getPasswordFileName());
            //res = FileUtil.readFileIntoString(dir + getPasswordFileName());
        }
        catch(IOException ie) { res = null; }

        if ((res == null)||(res.size() == 0)) return null;
        if (res.size() == 1) return res.get(0).trim();

        String pass = res.get(0).trim();
        if (pass.equals("")) return null;
        String key = res.get(1).trim();
        if (key.equals("")) return pass;

        String decodedPassword = "";

        try
        {
            ByteTransform bt = new ByteTransform();
            bt.setMimeSecurityKey(key);
            decodedPassword = bt.decodeMimeString(pass);
        }
        catch(Exception ie)
        {
            return pass;
        }
        if (decodedPassword.equals("")) return pass;

        return decodedPassword;
    }

    public boolean savePassword(String dir, String pass)
    {
        return util.saveStringIntoFile(dir, getPasswordFileName(), pass);
    }
    public boolean savePassword(String dir, String pass, String key)
    {
        if ((key == null)||(key.trim().equals(""))) return savePassword(dir, pass);
        String encodedPassword = "";
        try
        {
            ByteTransform bt = new ByteTransform();
            bt.setMimeSecurityKey(key);
            encodedPassword = bt.encodeMimeString(pass);
        }
        catch(Exception ie)
        {
            encodedPassword = "";
        }
        if ((encodedPassword == null)||(encodedPassword.equals(""))) return savePassword(dir, pass);
        return util.saveStringIntoFile(dir, getPasswordFileName(), encodedPassword + "\r\n" + key);
    }
    public boolean saveProperties(String dir, String scs, String h3s, String map, String schema, String comment, String date, String schVal, String[] vom)
    {
        String[] data = new String[] {scs, h3s, map, schema, comment, date, schVal};
        return saveProperties(dir, data, vom);
    }
    public boolean saveProperties(String dir, String scs, String h3s, String map, String schema, String comment, String date, String schVal)
    {
        return saveProperties(dir, scs, h3s, map, schema, comment, date, schVal, null);
    }
    public boolean saveProperties(String dir, String scs, String h3s, String map, String schema, String comment, String date)
    {
        return saveProperties(dir, scs, h3s, map, schema, comment, date, "true", null);
    }
    public boolean saveProperties(String dir, String scs, String h3s, String map, String schema, String comment, String date, String[] vom)
    {
        return saveProperties(dir, scs, h3s, map, schema, comment, date, "true", vom);
    }

    public boolean saveProperties(String dir, String[] data, String[] vom)
    {
        String res = "";
        String[] items = getPropertyItems();
        //String[] data = new String[] {scs, h3s, map, schema, comment, date};

        for (int i=0;i<items.length;i++)
        {
            String item = items[i];
            String dataItem = data[i];
            if (dataItem == null) dataItem = "";
            else dataItem = dataItem.trim();
            if ((i < 3)&&(dataItem.equals(""))) return false;
            if (i == codeSchemaValidation())
            {
                if (dataItem.equalsIgnoreCase("true")) dataItem = "true";
                else dataItem = "false";
            }
            res = res + item + "=" + dataItem + "\r\n";
        }

        if (vom != null)
        {
            for (int i=0;i<vom.length;i++)
            {
                res = res + "VOM[" + i + "]" + "=" + vom[i] + "\r\n";
            }
        }
        return util.saveStringIntoFile(dir, getPropertyFileName(), res);
    }

    public void addLogRecord(String dir, String str)
    {
        int logLimit = LOG_LIMIT;
        if (logLimit < 200) logLimit = 500;

        File file = new File(dir);
        if ((dir == null)||(dir.trim().equals(""))||
            (!file.exists())||(!file.isDirectory()))
        {
            logLimit = logLimit * 2;
            dir = getRootScenarioPath() + getAccessFailureTag();
            file = new File(dir);
            if ((!file.exists())||(!file.isDirectory())) file.mkdirs();
        }
        else dir = file.getAbsolutePath();

        if (!dir.endsWith(File.separator)) dir = dir + File.separator;

        String str2 = "";
        String dateFormat = dateUtil.getDefaultDateFormatString();
        str = str.trim();
        String logingDate = str.substring(0, dateFormat.length());

        str = str.substring(dateFormat.length());

        String newLogingDate = util.getFormatedNowDate();
        if (newLogingDate != null) str2 = newLogingDate + str;
        else str2 = logingDate + str;

        util.addStringLineToFile(dir + ACCESS_LOG_FILE_NAME, logLimit, str2);
    }

    public String simpleLogin(String loginID, String password, String ipAddr)
    {
        return checkLogin(true, loginID, password, ipAddr, null);
    }
    public String simpleLogin(String loginID, String password, String ipAddr, Object sourceObj)
    {
        return checkLogin(true, loginID, password, ipAddr, sourceObj);
    }
    public String loginAndCreateSession(String loginID, String password, String ipAddr)
    {
        return checkLogin(false, loginID, password, ipAddr, null);
    }
    public String loginAndCreateSession(String loginID, String password, String ipAddr, Object sourceObj)
    {
        return checkLogin(false, loginID, password, ipAddr, sourceObj);
    }
    private String checkLogin(boolean isSimple, String loginID, String password, String ipAddr, Object sourceObj)
    {
        String res = authorizeLoginIDCheck(loginID, password, null, true);
        String userPath = getRootScenarioPath() + loginID + File.separator;

        if ((!isSimple)&&(res == null))
        {
            if ((ipAddr == null)||(ipAddr.trim().equals("")))
            {
                res = "IP Address is Empty.";
            }
            else res = util.writeStringToFile(userPath + SESSION_FILE_NAME, util.getNowDate()+":"+ipAddr.trim());
        }

        String tit = "Creating a Session";
        if (isSimple) tit = "Simple Login";
        String sf = "Successful";
        if (res != null) sf = "Failure";

        String tail = "";
        if (res != null) tail = " => " + res;
        if (sourceObj != null) addLogRecord(userPath, util.getNowDate() + " : "+tit+" "+sf+"("+loginID+") : from " + ipAddr + ", called by " + sourceObj.getClass().getName() + tail);

        return res;
    }
    public String logoutSession(String userID, String ipAddr, Object sourceObj)
    {
        String userPath = checkLoginID(userID);

        String sessionPath = "";
        String res = null;
        while(true)
        {
            if (userPath == null)
            {
                res = "Invalid User-ID";
                break;
            }
            if (!userPath.endsWith(File.separator)) userPath = userPath + File.separator;
            sessionPath = userPath + SESSION_FILE_NAME;
            File file = new File(sessionPath);
            if ((!file.exists())||(!file.isFile()))
            {
                res = "Session is not created yet : " + sessionPath;
                break;
            }
            if ((ipAddr == null)||(ipAddr.trim().equals("")))
            {
                res = "Null IP Address";
                break;
            }
            ipAddr = ipAddr.trim();
            String line = FileUtil.readFileIntoString(sessionPath);
            if ((line == null)||(line.trim().equals("")))
            {
                res = "Empty Session";
                file.delete();
                break;
            }
            line = line.trim() + " ";
            int idx = line.indexOf(":");
            if (idx < 0) idx = 0;
            String sessionDate = line.substring(0, idx).trim();
            String sessionIP = line.substring(idx + 1).trim();
            if ((sessionDate.equals(""))||(sessionIP.equals("")))
            {
                res = "Invalid session format";
                file.delete();
                break;
            }
            if (!ipAddr.equals(sessionIP))
            {
                res = "Invalid Access : IP Address is mismatched.";
                break;
            }
            file.delete();

            break;
        }
        String sf = "Successful";
        if (res != null) sf = "Failure";
        String tail = "";
        if (res != null) tail = " => " + res;

        if (sourceObj != null) addLogRecord(userPath, util.getNowDate() + " : Session Logout "+sf+"("+userID+") : from " + ipAddr + ", called by " + sourceObj.getClass().getName() + tail);

        return res;
    }
    public String checkSession(String userID, String ipAddr, Object sourceObj)
    {
        String userPath = checkLoginID(userID);

        String sessionPath = "";
        String res = null;
        while(true)
        {
            if (userPath == null)
            {
                res = "Invalid User-ID";
                break;
            }
            if (!userPath.endsWith(File.separator)) userPath = userPath + File.separator;
            sessionPath = userPath + SESSION_FILE_NAME;
            File file = new File(sessionPath);
            if ((!file.exists())||(!file.isFile()))
            {
                res = "Session is not created yet : " + sessionPath;
                break;
            }
            if ((ipAddr == null)||(ipAddr.trim().equals("")))
            {
                res = "Null IP Address";
                break;
            }
            ipAddr = ipAddr.trim();
            String line = FileUtil.readFileIntoString(sessionPath);
            if ((line == null)||(line.trim().equals("")))
            {
                res = "Empty Session";
                file.delete();
                break;
            }
            line = line.trim() + " ";
            int idx = line.indexOf(":");
            if (idx < 0) idx = 0;
            String sessionDate = line.substring(0, idx).trim();
            String sessionIP = line.substring(idx + 1).trim();
            if ((sessionDate.equals(""))||(sessionIP.equals("")))
            {
                res = "Invalid session format";
                file.delete();
                break;
            }
            if (!ipAddr.equals(sessionIP))
            {
                res = "Invalid Access : IP Address is mismatched.";
                break;
            }

            int seconds = util.getSecondsBetweenDates(sessionDate);

            if (seconds <= 0)
            {
                res = "Mis-using or invalid format session date";
                file.delete();
                break;
            }
            if (seconds > 720)
            {
                res = "Session time over. (10 minutes)";
                file.delete();
                break;
            }
            res = util.writeStringToFile(sessionPath, util.getNowDate()+":"+ipAddr.trim());

            break;
        }
        String sf = "Successful";
        if (res != null) sf = "Failure";
        String tail = "";
        if (res != null) tail = " => " + res;

        if (sourceObj != null) addLogRecord(userPath, util.getNowDate() + " : Check and Refresh Session "+sf+"("+userID+") : from " + ipAddr + ", called by " + sourceObj.getClass().getName() + tail);

        return res;
    }
    public String checkLoginID(String loginID)
    {
        if (loginID == null) return null;

        loginID = loginID.trim();
        if (loginID.equals("")) return null;

        if (loginID.startsWith(ManageVOMFile.SAMPLE_CONTEXT_TAG)) return null;

        if (loginID.equals(getAccessFailureTag())) return null;

        String userPath = getRootScenarioPath() + loginID;

        File file = new File(userPath);
        if ((file.exists())&&(file.isDirectory())) return file.getAbsolutePath();
        return null;
    }
    public String authorizeLoginID(String loginID, String password, String key, String ipAddr, boolean allowNullKey, Object sourceObj)
    {
        return authorizeLoginID(loginID, password, key, ipAddr, allowNullKey, sourceObj, false);
    }
    public String authorizeLoginID(String loginID, String password, String key, String ipAddr, boolean allowNullKey, Object sourceObj, boolean createSession)
    {
        String res = authorizeLoginIDCheck(loginID, password, key, allowNullKey);
        String userPath = getRootScenarioPath() + loginID + File.separator;
        String sf = "Successful";
        String tail = "";

        if (res == null)
        {
            if (createSession)
            {
                String sessionPath = getRootScenarioPath() + loginID + File.separator + SESSION_FILE_NAME;
                res = util.writeStringToFile(sessionPath, util.getNowDate()+":"+ipAddr.trim());
            }
        }
        if (res != null)
        {
            sf = "Failure";
            tail = " => " + res;
        }

        if (sourceObj != null) addLogRecord(userPath, util.getNowDate() + " : Authorizing "+sf+"("+loginID+") : from " + ipAddr + ", called by " + sourceObj.getClass().getName()+tail);

        return res;
    }
    public String validateLoginID(String loginID)
    {
        if (loginID == null) return "Null ID or password";
        loginID = loginID.trim();

        if (loginID.equals("")) return "Empty ID or password";

        String userPath = checkLoginID(loginID);

        if (userPath == null) return "Invalid User-ID or password";
        return null;
    }
    private String authorizeLoginIDCheck(String loginID, String password, String key, boolean allowNullKey)
    {
        String res = validateLoginID(loginID);
        if (res != null) return res;
        String userPath = checkLoginID(loginID);

        if (password == null) return "Null ID or Password";
        password = password.trim();
        if (password.equals("")) return "Empty ID or Password";
        String keyLogPath = null;
        String decodedPassword = "";
        if ((key == null)||(key.trim().equals("")))
        {
            if (!allowNullKey) return "Key or login date is needed";
            decodedPassword = password;
        }
        else
        {
            key = key.trim();
            keyLogPath = userPath + File.separator + KEY_LOG_FILE_NAME;
            List<String> list = null;
            try
            {
                list = FileUtil.readFileIntoList(keyLogPath);
            }
            catch(IOException ie) {}

            if ((list == null)||(list.size() == 0))
            {
            }
            else
            {
                for (String line:list) if (line.trim().equals(key)) return "Already Used Key..";
            }
            if (key.length() <= dateUtil.getDefaultDateFormatString().length()) return "Invalid format key : 1";
            int secondsKeys = util.getSecondsBetweenDates(key.substring(0, dateUtil.getDefaultDateFormatString().length()), util.getNowDate());
            if (secondsKeys < 0) return "Invalid format key : 2";
            if (secondsKeys > 172800) return "Key is expired.";

            try
            {
                ByteTransform bt = new ByteTransform();
                bt.setMimeSecurityKey(key);
                decodedPassword = bt.decodeMimeString(password);
            }
            catch(Exception ie)
            {
                return "ByteTransform Error : " + ie.getMessage();
            }
            //System.out.println("$$$ decoded=" + decodedPassword + ", original=" + password + ", key=" + key);
        }
        String pass = getPassword(userPath);
        if ((pass == null)||(pass.trim().equals("")))
        {
            savePassword(userPath, decodedPassword, key);
        }
        else
        {
            if (!pass.trim().equals(util.changeStringFromWeb(decodedPassword).trim()))
            {
                //System.out.println("Invalid Password or user : " + password + ", key:" +key + ", decoded:"+decodedPassword + ", pass=" + pass);
                return "Invalid Password or user";
            }
        }
        if (keyLogPath != null) util.addStringLineToFile(keyLogPath, 100, key);

        return null;
    }
    public String getOutputFileName()
    {
        return getOutputFileName(".zip");
    }
    public String getOutputFileName(String extension)
    {
        String outFile = "";
        String outFilePath = "";
        if ((extension == null)||(extension.trim().equals(""))) extension = ".txt";
        while(true)
        {
            outFile = "OUTPUT_" + util.getNowDate() + "_" + util.getRandomNumber(5) + extension;
            outFilePath = getOutputDir() + outFile;
            File file = new File(outFilePath);
            if ((file.exists())&&(file.isFile())) continue;
            break;
        }

        util.writeStringToFile(outFilePath, "Reserved");
        return outFile;
    }
    private void tidyOutputDir()
    {
        util.tidyDir(getOutputDir(), null, true);
    }
    private void tidyWorkDir()
    {
        util.tidyDir(getWorkDir(), null, true);
    }

    public String changeCompressedStringFromWeb(String str) throws IOException
    {
        return changeCompressedStringFromWeb(str, null, null, null);
    }
    public String changeCompressedStringFromWeb(String str, String key) throws IOException
    {
        return changeCompressedStringFromWeb(str, key, null, null);
    }
    public String changeCompressedStringFromWeb(String str, String key, String encoded) throws IOException
    {
        return changeCompressedStringFromWeb(str, key, encoded, null);
    }
    public String changeCompressedStringFromWeb(String str, String key, String encoded, String extensionInput) throws IOException
    {
        if (extensionInput == null) extensionInput = "";
        extensionInput = extensionInput.trim();
        if (extensionInput.equals("")) extensionInput = ".csv";

        if ((str == null)||(str.trim().equals(""))) throw new IOException("Null or Empty "+extensionInput+" source string");
        str = str.trim();

        if (encoded == null) encoded = "";
        encoded = encoded.trim();
        boolean isEncodedOnly = encoded.toLowerCase().equals("true");

        String extension = ".zip";
        if (isEncodedOnly) extension = extensionInput;

        String tempFileName = "";
        File tempZip = null;
        String res = null;
        try
        {
            tempFileName = FileUtil.saveStringIntoTemporaryFile(str);

            String tempZipName = FileUtil.getTemporaryFileName(extension);

            ByteTransform bt = new ByteTransform();
            bt.setMimeSecurityKey(key);
            bt.decodeMimeFile(tempFileName, tempZipName);

            tempZip = new File(tempZipName);
            if ((tempZip.exists())&&(tempZip.isFile())) {}
            else throw new IOException(extension + " file creating failure.");

            if (isEncodedOnly)
            {
                res = FileUtil.readFileIntoStringAllowException(tempZipName);
            }
            else
            {
                String ext = extensionInput;
                List<String> list = null;
                while(true)
                {
                    //list = TransformationServiceUtil.getNamesOfEntriesInZip(tempZip, ext);
                    if ((list != null)&&(list.size() > 0)) break;

                    if (ext.equals(".hl7")) ext = ".csv";
                    else if (ext.equals(".csv")) ext = ".txt";
                    else if (ext.equals(".txt")) break;
                    else break;
                }
                if ((list == null)||(list.size() == 0)) throw new IOException("No "+extensionInput+" data in the "+extensionInput+" String.");
                //res = TransformationServiceUtil.readFromZip(tempZip, list.get(0));
            }
        }
        catch(FileNotFoundException fe)
        {
            throw new IOException("FileNotFoundException : " + fe.getMessage());
        }
        finally
        {
            File file1 = new File(tempFileName);
            if ((file1.exists())&&(file1.isFile())) file1.delete();
            if ((tempZip != null)&&(tempZip.exists())&&(tempZip.isFile())) tempZip.delete();
        }

        return res;
    }
    //public String modifyHTML(String html, String key, String encoded, String extensionInput)
    //{
    //}
    public boolean getUniversalLogin(PrintWriter out, String program, String title, String user, String idVar, String passVar, String addLines, HttpServletRequest req)
    {
        return getUniversalLogin(out, program, title, user, idVar, passVar, addLines, false, req);

    }
//    public boolean getUniversalLogin(PrintWriter out, String program, String title, String user, String idVar, String passVar, String addLines, boolean isMulti)
//    {
//        return getUniversalLogin(out, program, title, user, idVar, passVar, addLines, isMulti, null);
//    }
    public boolean getUniversalLogin(PrintWriter out, String program, String title, String user, String idVar, String passVar, String addLines, boolean isMulti, HttpServletRequest req)
    {
        if (out == null) return false;
        if (program == null) program = "";
        else program = program.trim();

        while(true)
        {
            int idx = program.indexOf(".");
            if (idx < 0) break;
            program = program.substring(idx+1);
        }
        if (program.equals("")) return false;

        if (title == null) title = "";
        else title = title.trim();
        if (title.equals("")) return false;
        if (user == null) user = "";
        else user = user.trim();
        if (idVar == null) idVar = "";
        else idVar = idVar.trim();
        if (idVar.equals("")) return false;
        if (passVar == null) passVar = "";
        else passVar = passVar.trim();
        if (passVar.equals("")) return false;
        if (addLines == null) addLines = "";
        else addLines = addLines.trim();
        String multi = " ENCTYPE=\"multipart/form-data\"";
        if (!isMulti) multi = "";

        String url = getBaseURLFromRequest(req);
        if (url == null) url = getBaseURL() + "servlet/" + program;
        else url = url + "/" + program;

        String rl = "\r\n";
        String html = "<html>" + rl
                    + "  <head>" + rl
                    + "    <title>" + title + "</title>" + rl
                    + "  </head>" + rl
                    + "  <body>" + rl
                    + "    <form method=\"Post\" action=\"" + url + "\"" + multi + ">" + rl
                    + "    <center>" + rl
                    + "      <br><br><br>" + rl
                    + "      <h1><font color='brown'>" + title + "<br></font></h1>" + rl
                    + "      <h4><font color='brown'>" + user + " Log In</font></h4>" + rl
                    + "      <font color='green'><br><br><br>" + rl
                    + "      <table border=1 bordercolor=\"blue\">" + rl
                    + "        <tr>" + rl
                    + "          <td align=\"center\" width=\"30%\" bgcolor=\"CBF5FF\">"+user+" ID</td>" + rl
                    + "          <td width=\"70%\"><input type=\"text\" name=\"" + idVar + "\" size=20 onMouseOver=\"this.style.backgroundColor='yellow'\" onMouseOut=\"this.style.backgroundColor='white'\"></td>" + rl
                    + "        </tr>" + rl
                    + "        <tr>" + rl
                    + "          <td align=\"center\" width=\"30%\" bgcolor=\"CBF5FF\">Password</td>" + rl
                    + "          <td width=\"70%\"><input type=\"password\" name=\"" + passVar + "\" size=20 onMouseOver=\"this.style.backgroundColor='ivory'\" onMouseOut=\"this.style.backgroundColor='white'\"></td>" + rl
                    + "        </tr>" + rl
                    + addLines + rl
                    + "        <tr>" + rl
                    + "          <td colspan=2 align=\"center\">" + rl
                    + "            <input type=\"submit\" value=\"Submit\" style=\"background-color:cbf5ff; color:blue\">&nbsp;" + rl
                    + "            <input type=\"reset\" value=\"Cencel\" style=\"background-color:cbf5ff; color:blue\">" + rl
                    + "          </td>" + rl
                    + "        </tr>" + rl
                    + "      </table>" + rl
                    + "      </font></center>" + rl
                    + "    </form>" + rl
                    + "  </body>" + rl
                    + "</html>";
        out.println(html);
        return true;
    }
    public String getRequstURL(HttpServletRequest req)
    {
        if (req == null) return null;
        StringBuffer urlB = req.getRequestURL();
        if (urlB == null) return null;
        String url = urlB.toString().trim();
        if (url.equals("")) return null;
        int idx = url.indexOf("?");
        if (idx > 0) url = url.substring(0, idx);
        return url;
    }
    public String getBaseURLFromRequest(HttpServletRequest req)
    {
        String url = getRequstURL(req);
        if (url == null) return null;
        url = url.trim();
        while(url.endsWith("/")) url = url.substring(0, url.length()-1).trim();

        String url2 = url;
        while(true)
        {
            String achar = url2.substring(url2.length()-1, url2.length());
            url2 = url2.substring(0,url2.length()-1);
            if (url2.equals("")) return url;
            if (achar.equals("/")) break;
        }

        return url2;
    }
}
