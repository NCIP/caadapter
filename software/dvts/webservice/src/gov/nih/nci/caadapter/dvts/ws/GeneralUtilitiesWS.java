/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.ws;

import java.util.*;
import java.io.*;
import java.text.*;
import gov.nih.nci.caadapter.dvts.common.util.FileUtil;
import gov.nih.nci.caadapter.dvts.common.function.DateFunction;
import gov.nih.nci.caadapter.dvts.common.function.FunctionException;

import edu.knu.medinfo.hl7.v2tree.ByteTransform;

/**
 * Created by IntelliJ IDEA.
 * User: kium
 * Date: Jun 16, 2009
 * Time: 12:43:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class GeneralUtilitiesWS {
     private DateFunction dateUtil = new DateFunction();

    public GeneralUtilitiesWS()
    {
        //initialize(false);
    }

    /*
    public File searchDir(File dir, String fileName)
    {
        return searchDir(dir, fileName, -1, null);
    }
    public File searchDir(File dir, String fileName, String[] underCondition)
    {
        return searchDir(dir, fileName, -1, underCondition);
    }
    public File searchDir(File dir, String fileName, int limitDepth, String[] underCondition)
    {
        return searchFileAndDir(dir, fileName, limitDepth, underCondition, false);
    }
    public File searchFile(File dir, String fileName)
    {
        return searchFile(dir, fileName, -1, null);
    }
    public File searchFile(File dir, String fileName, String[] underCondition)
    {
        return searchFile(dir, fileName, -1, underCondition);
    }
    public File searchFile(File dir, String fileName, int limitDepth, String[] underCondition)
    {
        return searchFileAndDir(dir, fileName, limitDepth, underCondition, true);
    }
    private File searchFileAndDir(File dir, String fileName, int limitDepth, String[] underCondition, boolean isFile)
    {
        if (dir == null) return null;
        if ((!dir.exists())||(!dir.isDirectory())) return null;
        if ((fileName == null)||(fileName.trim().equals(""))) return null;
        fileName = fileName.trim();

        while(true)
        {
            int idx = fileName.indexOf(File.separator);
            if (idx < 0) break;
            fileName = fileName.substring(idx + File.separator.length());
        }

        String dirPath = dir.getAbsolutePath();
        if (!dirPath.endsWith(File.separator)) dirPath = dirPath + File.separator;

        File pFile = new File(dirPath + fileName);
        if (pFile.exists())
        {
            boolean cTag = true;
            File tempP = pFile;
            while ((underCondition != null)&&(underCondition.length > 0))
            {
                boolean dTag = false;
                for (String str:underCondition)
                {
                    if (tempP.getName().equals(str))
                    {
                        dTag = true;
                        break;
                    }
                }
                if (dTag) break;
                if (tempP.getParentFile() == null)
                {
                    cTag = false;
                    break;
                }
                tempP = tempP.getParentFile();
            }
            if (cTag)
            {
                if ((isFile)&&(pFile.isFile())) return pFile;
                if ((!isFile)&&(pFile.isDirectory())) return pFile;
            }
        }

        String temp = dirPath.substring(0, (dirPath.length() - File.separator.length()));
        int depth = 0;
        while(true)
        {
            int idx = temp.indexOf(File.separator);
            if (idx < 0) break;
            depth++;
            temp = temp.substring(idx + File.separator.length());
        }

        if ((limitDepth > 0)&&(depth > limitDepth)) return null;

        File[] files = dir.listFiles();
        for(File file:files)
        {
            if (!file.isDirectory()) continue;
            File res = searchFileAndDir(file, fileName, limitDepth, underCondition, isFile);
            if (res != null) return res;
        }
        return null;
    }
    */
    public String from8859(String src) // 8859??? KSC5601? ??
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
        catch(NullPointerException u) { return "null....1?";}
    }

    public String to8859(String str) // ??? ??? ?? ?? ?? ??? ?? ???
    {
        String result = null;
        if (str == null) return "";
        try
        {
            result = new String(str.getBytes("KSC5601"), "8859_1"); // ?/? (encoding)
        }
        catch(UnsupportedEncodingException u) {}
        catch(NullPointerException u)
        {
            return "null....2?";
        }
        return result;
    }

    public String getNowDate()
    {
        return dateUtil.getCurrentTime();
        //java.util.Date dt = new java.util.Date();
        //SimpleDateFormat sd1 = new SimpleDateFormat("yyyyMMdd");
        //SimpleDateFormat sd2 = new SimpleDateFormat("HHmmss");
        //return sd1.format(dt) + sd2.format(dt);
    }
    public String changeFormatedDate(String date)
    {
        return changeFormatedDate("MM/dd/yyyy HH:mm:ss", date);
    }
    public String changeFormatedDate(String format, String date)
    {
        if (date == null) return null;
        else date = date.trim();
        if (date.equals("")) return null;

        if (format == null) return null;
        else format = format.trim();
        if (format.equals("")) return null;

        String newDate = "";
        try
        {
            newDate = dateUtil.changeAnotherFormat(dateUtil.getDefaultDateFormatString(), format, date);
        }
        catch(FunctionException fe)
        {
            return null;
        }
        return newDate;
    }
    public String getFormatedNowDate()
    {
        return getFormatedNowDate("MM/dd/yyyy HH:mm:ss");
    }
    public String getFormatedNowDate(String format)
    {
        String newDate = changeFormatedDate(format, dateUtil.getCurrentTime());

        return newDate;
    }

    public String getRandomNumber(int digit)
    {
        if (digit == 0) return "";
        Random rnd = new Random();
        int in = 1;
        int la = 0;
        int sa = 0;
        for(int x=0;x<digit;x++)
        {
            in = in * 10;
            //if (x==(digit-2)) sa = in;
        }
        la = in;
        sa = la / 10;
        int n = 0;
        while(true)
        {
            n++;
            in = rnd.nextInt();
            if ((in > sa)&&(in < la)) break;
        }
        //System.out.println("DDDD : " + n);
        return "" + in;
    }

    public void deleteFile(File file)
    {
        if ((file == null)||(!file.exists())) return;
        if (file.isFile())
        {
            file.delete();
            return;
        }
        File[] fList = file.listFiles();
        for (File aFile:fList) deleteFile(aFile);
        file.delete();
    }

    public String sendOutMessage(String title, String level, String message)
    {
        return sendOutMessage(title, level, message, "");
    }
    public void sendOutMessage(PrintWriter out, String title, String level, String message)
    {
        out.println(sendOutMessage(title, level, message, ""));
    }
    public void sendOutMessage(PrintWriter out, String title, String level, String message, String link)
    {
        out.println(sendOutMessage(title, level, message, link));
    }

    public String sendOutMessage(String title, String level, String message, List<String> displayList)
    {
        return sendOutMessage(title, level, message, "", displayList);
    }
    public void sendOutMessage(PrintWriter out, String title, String level, String message, List<String> displayList)
    {
        out.println(sendOutMessage(title, level, message, "", displayList));
    }
    public void sendOutMessage(PrintWriter out, String title, String level, String message, String link, List<String> displayList)
    {
        out.println(sendOutMessage(title, level, message, link, displayList));
    }

    public String sendOutMessage(String title, String level, String message, String link)
    {
        return sendOutMessage(title, level, message, link, null);
    }
    public String sendOutMessage(String title, String level, String message, String link, List<String> displayList)
    {
        String retLine = "";
        retLine = retLine + "<html>";
        retLine = retLine + "<head><title>"+to8859(title)+"</title></head>";
        retLine = retLine + "<body bgcolor='white'>";
        retLine = retLine + "<font color='brown'><center><br><br>";
        retLine = retLine + "<h1>"+to8859(title)+"</h1></center></font><br>";

        retLine = retLine + "<font color='green'>";
        retLine = retLine + "<h5>MESSAGE LEVEL: " + level + "<!--END--></h5><br>";
        retLine = retLine + "<h5>MESSAGE: " + to8859(message) + "<!--END--></h5><br>";
        if ((link != null)&&(!link.trim().equals("")))
        {
            retLine = retLine + "<h5><a href='"+link+"'>Link Here!</a></h5><br>";
        }
        if ((displayList != null)&&(displayList.size() > 0))
        {
            retLine = retLine + "<br><h5><font color='brown'>";
            for(String str:displayList)
                retLine = retLine + str + "<br>";
            retLine = retLine + "</h5></font><br>";
        }
        retLine = retLine + "</font></body>";
        retLine = retLine + "</html>";

        return retLine;
    }

    public boolean saveStringIntoFile(String dir, String filename, String str)
    {
        //System.out.println("dir:"+dir+", filename:"+filename +", str="+str);
        if (dir == null) return false;

        dir = dir.trim();
        if (dir.equals("")) return false;

        File file = new File(dir);
        if (!file.exists()) return false;
        if (!file.isDirectory()) return false;

        dir = file.getAbsolutePath();
        if (!dir.endsWith(File.separator)) dir = dir + File.separator;

        if (writeStringToFile(dir+filename, str) == null) return true;
        return false;
    }

    public String writeStringToFile(String fileName, String str)
    {
        if (str == null) return "Null source string for writing";
        List<String> list = new ArrayList<String>();
        list.add(str);
        return writeListToFile(fileName, list);
    }

    public String addStringLineToFile(String fileName, String str)
    {
        return addStringLineToFile(fileName, -1, str);
    }
    public String addStringLineToFile(String fileName, int limit, String str)
    {
        if ((fileName == null)||(fileName.trim().equals(""))) return "Null file name for adding";
        if ((str == null)||(str.trim().equals(""))) return "Empty source string for adding";

        List<String> list = null;
        File file = new File(fileName);
        if ((!file.exists())||(!file.isFile())) {}//return "Invalid File Name for adding";
        else
        {
            try { list = FileUtil.readFileIntoList(fileName); }
            catch(IOException ie) {}
        }
        if (list == null) list = new ArrayList<String>();
        list.add(str);
        return writeListToFile(fileName, limit, list);
    }
    public String writeListToFile(String fileName, List<String> list)
    {
        return writeListToFile(fileName, -1, list);
    }
    public String writeListToFile(String fileName, int limit, List<String> list)
    {
        if ((fileName == null)||(fileName.trim().equals(""))) return "Null file name for writing";
        if ((list == null)||(list.size() == 0)) return "Empty source list for writing";

        int startsFrom = 0;
        int limitTo = list.size();
        if ((limit > 0)&&(limit < list.size()))
        {
            startsFrom = list.size() - limit;
        }
        FileWriter fw = null;
        try
        {
            fw = new FileWriter(fileName);

            for (int i=startsFrom;i<limitTo;i++)
            {
                if (i >= list.size()) break;
                String str = list.get(i);
                fw.write(str + "\r\n");
            }
            fw.close();
        }
        catch(Exception ie)
        {
            return ie.getMessage();
        }
        return null;
    }

    public void tidyDir(String deleteFilePath, String extension)
    {
        tidyDir(deleteFilePath, extension, false);
    }
    public void tidyDir(String deleteFilePath, String extension, boolean dirCreation)
    {
        if ((deleteFilePath == null)||(deleteFilePath.trim().equals(""))) return;

        deleteFilePath = changeStringFromWeb(deleteFilePath.trim());

        File dir = new File(deleteFilePath);
        if ((!dir.exists())||(!dir.isDirectory()))
        {
            if (dirCreation) dir.mkdirs();
            return;
        }

        File[] files = dir.listFiles();

        if ((files == null)||(files.length == 0)) return;

        for (File file:files)
        {
            if (!file.isFile()) continue;
            String fileName = file.getName().trim();

            if ((extension == null)||(extension.trim().equals(""))) {}
            else
            {
                extension = extension.trim();
                if (fileName.toLowerCase().endsWith(extension.toLowerCase())) {}
                else continue;
            }

            int idx = fileName.indexOf("_");
            if (idx < 0)
            {
                file.delete();
                continue;
            }
            fileName = fileName.substring(idx + 1);
            idx = fileName.indexOf("_");
            if (idx < 0)
            {
                file.delete();
                continue;
            }
            String createdDateOfFile = fileName.substring(0, idx);
            int seconds = getSecondsBetweenDates(createdDateOfFile);
            if ((seconds < 0)||(seconds > 1200))
            {
                //System.out.println("Deleting : " +file.getName()+", createdDate:"+createdDateOfFile + ":"+seconds);
                file.delete();
            }
            //else System.out.println("Not Deleting : " +file.getName()+", createdDate:"+createdDateOfFile + ":"+seconds);

        }

        return;
    }

    public int getSecondsBetweenDates(String fromDate)
    {
        return getSecondsBetweenDates(fromDate, getNowDate());
    }
    public int getSecondsBetweenDates(String fromDate, String toDate)
    {
        long millis = 0l;
        try
        {
            millis = dateUtil.getMillisBetweenDates(fromDate, dateUtil.getDefaultDateFormatString(), toDate, dateUtil.getDefaultDateFormatString());

            if (millis < 0l) millis = millis * -1l;
        }
        catch(FunctionException fe)
        {
            return -1;
        }

        return (int) ( millis / 1000l);
    }

    public String changeStringFromWeb(String str)
    {
        String temp1 = str;
        String temp2 = "";
        //String achar = "";
        String one = "";
        String two = "";
        //String three = "";

        ByteTransform bt = new ByteTransform();

        while(true)
        {
            int idx = temp1.indexOf("%");
            if (idx < 0)
            {
                temp2 = temp2 + temp1;
                break;
            }

            temp2 = temp2 + temp1.substring(0,idx);
            temp1 = temp1.substring(idx);

            //String decoded = "";
            String hex = "";
            while(temp1.length() >= 3)
            {
                one = temp1.substring(0, 1);
                if (!one.equals("%")) break;
                two = temp1.substring(1, 3);
                if (!isHexCharacter(two)) break;

                hex = hex + two;
                temp1 = temp1.substring(3);
            }

            if (hex.equals(""))
            {
                temp2 = temp2 + temp1.substring(0,1);
                temp1 = temp1.substring(1);
            }
            else
            {
                temp2 = temp2 + bt.decodeHexString(hex);
                one = temp1.substring(0, 1);
                if (one.equals("%"))
                {
                    temp2 = temp2 + one;
                    temp1 = temp1.substring(1);
                }
            }
            /*
               if (three.equals("%0A")) achar = "\n";
               else if (three.equals("%20")) achar = " ";
               else if (three.equals("%3F")) achar = "?";
               else if (three.equals("%2C")) achar = ",";
               else if (three.equals("%22")) achar = "\"";
               else if (three.equals("%25")) achar = "%";
               else if (three.equals("%26")) achar = "&";
               else if (three.equals("%5C")) achar = "\\";
               else if (three.equals("%2E")) achar = ".";
               else if (three.equals("%2F")) achar = "/";
               else if (three.equals("%3C")) achar = "<";
               else if (three.equals("%3D")) achar = "=";
               else if (three.equals("%3E")) achar = ">";
               else if (three.equals("%2B")) achar = "+";
               */
        }

        return temp2;
    }

    public boolean isHexCharacter(String str)
    {
        if ((str == null)||(str.trim().equals(""))) return false;
        str = str.trim();
        int len = str.length();
        if ((len % 2) != 0) return false;

        for(int i=0;i<len;i++)
        {
            String achar = str.substring(i, i+1);
            boolean isHex = false;
            if (achar.equals("0")) isHex = true;
            else if (achar.equals("1")) isHex = true;
            else if (achar.equals("2")) isHex = true;
            else if (achar.equals("3")) isHex = true;
            else if (achar.equals("4")) isHex = true;
            else if (achar.equals("5")) isHex = true;
            else if (achar.equals("6")) isHex = true;
            else if (achar.equals("7")) isHex = true;
            else if (achar.equals("8")) isHex = true;
            else if (achar.equals("9")) isHex = true;
            else if (achar.equals("A")) isHex = true;
            else if (achar.equals("B")) isHex = true;
            else if (achar.equals("C")) isHex = true;
            else if (achar.equals("D")) isHex = true;
            else if (achar.equals("E")) isHex = true;
            else if (achar.equals("F")) isHex = true;

            if (!isHex) return false;
        }

        return true;
    }

    public String copyFile(String inputFile, String outputFile)
    {
        FileInputStream fis = null;
        DataInputStream distr = null;
        FileOutputStream fos = null;
        DataOutputStream dos2 = null;

        try
        {
            fis = new FileInputStream(inputFile);
            distr = new DataInputStream(fis);
            fos = new FileOutputStream(outputFile);
            dos2 = new DataOutputStream(fos);
        }
        catch(IOException ie)
        {
            return "Stream object opening failure : " + ie.getMessage();
        }

        try
        {
            byte nn = 0;
            boolean endSig = false;

            while(true)
            {
                try
                {
                    nn = distr.readByte();
                }
                catch(IOException ie)
                {
                    endSig = true;
                }
                catch(NullPointerException ne)
                {
                    endSig = true;
                }

                if (endSig) break;

                dos2.writeByte(nn);
            }

            fis.close();
            distr.close();
            fos.close();
            dos2.close();
        }
        catch(IOException cse)
        {
            return "IOException : " + cse.getMessage();
        }
        catch(NullPointerException ne)
        {
            return "NullPointerException : " + ne.getMessage();
        }
        return null;
    }

    public List<Object> sortObjectList(List<Object> li)
    {
        if (li.size() < 2) return li;
        Object fileArray[] = new Object[li.size()];
        for(int i=0;i<li.size();i++) fileArray[i] = li.get(i);
        Object tempFile = null;
        //Object tempI = null;
        //Object tempJ = null;
        String nameI = "";
        String nameJ = "";

        for(int i=0;i<(li.size()-1);i++)
        {
            //System.out.println("Sort......... : " + fileArray[i].toString());
            for(int j=(i+1);j<li.size();j++)
            {
                //nameI = fileArray[i].getName();
                //nameJ = fileArray[j].getName();
                nameI = fileArray[i].toString();
                nameJ = fileArray[j].toString();
                if(nameI.compareTo(nameJ) > 0)
                {
                    tempFile = fileArray[i];
                    fileArray[i] = fileArray[j];
                    fileArray[j] = tempFile;
                }
            }
        }
        List<Object> li2 = new ArrayList<Object>();
        for(int i=0;i<li.size();i++) li2.add(fileArray[i]);
        return li2;
    }
    public boolean isDirExist(String fname)
    {
        return isThisExist(fname, false);
    }
    public boolean isFileExist(String fname)
    {
        return isThisExist(fname, true);
    }
    private boolean isThisExist(String fname, boolean isFile)
    {
        if (fname == null) return false;
        fname = fname.trim();
        if (fname.equals("")) return false;

        File f = new File(fname);
        if (!f.exists()) return false;
        if ((f.isDirectory())&&(!isFile)) return true;
        if ((f.isFile())&&(isFile)) return true;
        return false;
    }

    public boolean readBooleanParameter(String param, boolean defaultVal)
    {
        if (param == null) param = "";
        else param = param.toLowerCase().trim();

        if ((param.equals("true"))||(param.equals("yes"))||(param.equals("t"))||(param.equals("y"))) return true;
        if ((param.equals("false"))||(param.equals("no"))||(param.equals("f"))||(param.equals("n"))) return false;

        return defaultVal;
    }
}
