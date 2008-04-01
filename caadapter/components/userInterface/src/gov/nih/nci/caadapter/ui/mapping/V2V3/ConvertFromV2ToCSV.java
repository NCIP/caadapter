/*
 *  : /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/common/standard/impl/ConvertFromV2ToCSV.java,v 1.00 Jan 31, 2008 2:50:22 PM umkis Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE  
 * ******************************************************************
 *
 *	The caAdapter Software License, Version 1.0
 *
 *	Copyright 2001 SAIC. This software was developed in conjunction with the National Cancer
 *	Institute, and so to the extent government employees are co-authors, any rights in such works
 *	shall be subject to Title 17 of the United States Code, section 105.
 *
 *	Redistribution and use in source and binary forms, with or without modification, are permitted
 *	provided that the following conditions are met:
 *
 *	1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *	and the disclaimer of Article 3, below.  Redistributions in binary form must reproduce the above
 *	copyright notice, this list of conditions and the following disclaimer in the documentation and/or
 *	other materials provided with the distribution.
 *
 *	2.  The end-user documentation included with the redistribution, if any, must include the
 *	following acknowledgment:
 *
 *	"This product includes software developed by the SAIC and the National Cancer
 *	Institute."
 *
 *	If no such end-user documentation is to be included, this acknowledgment shall appear in the
 *	software itself, wherever such third-party acknowledgments normally appear.
 *
 *	3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or
 *	promote products derived from this software.
 *
 *	4. This license does not authorize the incorporation of this software into any proprietary
 *	programs.  This license does not authorize the recipient to use any trademarks owned by either
 *	NCI or SAIC-Frederick.
 *
 *	5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *	WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *	MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *	DISCLAIMED.  IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR
 *	THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *	EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *	PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *	PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 *	OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *	NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * ******************************************************************
 */

package gov.nih.nci.caadapter.ui.mapping.V2V3;

import edu.knu.medinfo.hl7.v2tree.HL7MessageTreeException;
import edu.knu.medinfo.hl7.v2tree.HL7V2MessageTree;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.6 $
 *          date        Jan 31, 2008
 *          Time:       2:50:22 PM $
 */
public class ConvertFromV2ToCSV
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = ": ConvertFromV2ToCSV.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = ": /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/common/standard/impl/ConvertFromV2ToCSV.java,v 1.00 Jan 31, 2008 2:50:22 PM umkis Exp $";

    //private List<String> errorMessages = new ArrayList<String>();
    private int errorLevel = -1;
    private String message = "";
    private String messageTitle = "";
    private int messageCount = 0;
    private List<String> failErrorMessageList = new ArrayList<String>();
    private String logFileName = null;
    private String numberTag = "V2 Message num.";

//    public ConvertFromV2ToCSV()
//    {
//    }

    public ConvertFromV2ToCSV(String v2MetaPath, String v2File, String mType, String versionS, String fileCSV, String fileSCSValidate, boolean strict)
    {
        convertToCSVFile(v2MetaPath, v2File, mType, versionS, fileCSV, fileSCSValidate, strict);
    }
    private void convertToCSVFile(String v2MetaPath, String v2File, String mType, String versionS, String fileCSV, String fileSCSValidate, boolean strict)
    {

        V2ConverterToSCSPanel panel = new V2ConverterToSCSPanel();

        try
        {
            HL7V2MessageTree aTree = new HL7V2MessageTree(v2MetaPath);
            aTree.setVersion(versionS);
            if (!((mType == null)||(mType.trim().equals("")))) aTree.parse(mType);
        }
        catch(HL7MessageTreeException he)
        {
            errorLevel = JOptionPane.ERROR_MESSAGE;
            message = he.getMessage();
            messageTitle = "HL7MessageTreeException";
            return;
        }

        if ((fileSCSValidate == null)||(fileSCSValidate.trim().equals("")))
        {
            errorLevel = JOptionPane.ERROR_MESSAGE;
            message = "SCS file for validation is needed.";
            messageTitle = "Null SCS File";
            return;
        }

        fileSCSValidate = fileSCSValidate.trim();
        File file = new File(fileSCSValidate);
        if ((file.exists())&&(file.isFile())) {}
        else
        {
            errorLevel = JOptionPane.ERROR_MESSAGE;
            message = "This SCS file is not exist. : " + fileSCSValidate;
            messageTitle = "Not Exist SCS File";
            return;
        }

        V2Converter vc = new V2Converter();
        ValidatorResults result = vc.validateSpecification(new File(fileSCSValidate));
        if (!result.isValid())
        {
            errorLevel = JOptionPane.ERROR_MESSAGE;
            message = "Invalid target validation scs file : " + fileSCSValidate;
            messageTitle = "Invalid SCS File";
            return;
        }

        int n = 0;
        int failCount = 0;
        String logFileName = fileCSV + ".log";
        int logCount = 0;

        FileReader fr = null;
        BufferedReader br = null;
        FileWriter fw = null;
        FileWriter fwLog = null;

            try
            {
                fr = new FileReader(v2File);
                br = new BufferedReader(fr);
                fw = new FileWriter(fileCSV);
                fwLog = new FileWriter(logFileName);

                String msg = "";
                String msgF = "";
                boolean isStarted = false;
                boolean isFileEnded = false;

                while(true)
                {
                    String line = br.readLine();
                    if (line == null)
                    {
                        line = "The End.";
                        isFileEnded = true;
                    }

                    line = line.trim();
                    if (line.equals("")) continue;
                    if (isFileEnded) line = "";
                    if ((isFileEnded)||(line.startsWith("MSH")))
                    {
                        msgF = msg;
                        msg = line + "\r";
                        if (!isStarted)
                        {
                            isStarted = true;
                            msg = line + "\r";
                            continue;
                        }
                        else
                        {
                            n++;
                            String msgTagStr = numberTag + n + " ";
                            System.out.println("  Converting Processing " + msgTagStr);
                            //boolean strict = jrStrictValidationYes.isSelected();
                            //String mType = jtInputMessageType.getText();
//                            if (mType == null) mType = "";
//                            mType = mType.trim();
//                            String versionS = ((String)jcHL7Version.getSelectedItem()).trim();

                            HL7V2MessageTree aTree = null;
                            try
                            {
                                if (strict)
                                {
                                    aTree = new HL7V2MessageTree(v2MetaPath);
                                    aTree.setVersion(versionS);
                                    aTree.parse(msgF);
                                }
                                else
                                {
                                    aTree = new HL7V2MessageTree(v2MetaPath);
                                    aTree.setVersion(versionS);
                                    aTree.setFlagDataValidation(false);
                                    if (!mType.equals(""))
                                    {
                                        if ((mType.length() != 7)||(!mType.substring(3, 4).equals("^")))
                                        {
                                            failCount++;
                                            String err = "   Fail Converting " + msgTagStr+" Error: Messge Type is not right : " + mType;
                                            System.err.println(err);
                                            failErrorMessageList.add(err);
                                            //errorMessages.add("###=== "+err);
                                            fwLog.write("###=== "+err+"\r\n");
                                            logCount++;
                                            if (isFileEnded) break;
                                            continue;
                                        }
                                        aTree.makeTreeHead(mType.substring(0, 3), mType.substring(4));
                                    }
                                    aTree.parse(msgF);
                                }
                            }
                            catch(HL7MessageTreeException he)
                            {
                                failCount++;
                                String err = "    Fail Converting " + msgTagStr+" HL7MessageTreeException: " + he.getMessage();
                                System.err.println(err);
                                failErrorMessageList.add(err);
                                //errorMessages.add("###=== "+err);
                                fwLog.write("###=== "+err+"\r\n");
                                logCount++;
                                if (isFileEnded) break;
                                continue;
                            }
                            catch(Exception ee)
                            {
                                failCount++;
                                String err = "   Fail Converting " + msgTagStr+" Unexpected Exception: " + ee.getMessage();
                                System.err.println(err);
                                failErrorMessageList.add(err);
                                //errorMessages.add("###=== "+err);
                                fwLog.write("###=== "+err+"\r\n");
                                logCount++;
                                ee.printStackTrace();
                                if (isFileEnded) break;
                                continue;
                            }
                            if (aTree.getErrorMessageList().size() > 0)
                            {
                                //errorMessages.add("###=== v2 "+msgTagStr+" Parsing complete!! Followings are Error Messages : " + v2File);
                                fwLog.write("###=== "+msgTagStr+" Parsing complete!! Followings are Error Messages : " + v2File + "\r\n");
                                logCount++;
                                for (String ss:aTree.getErrorMessageList())
                                {
                                    //errorMessages.add(ss);
                                    fwLog.write("   " + ss + "\r\n");
                                    logCount++;
                                }
                            }

                            String tempCSVFile = FileUtil.getTemporaryFileName();

                            if (!panel.doPressGenerateSingleMessage(aTree, "", tempCSVFile, fileSCSValidate, true))
                            {
                                failCount++;
                                String err = "   Fail Converting "+msgTagStr+" Conveting Error: ";
                                System.err.println(err);
                                failErrorMessageList.add(err);
                                //errorMessages.add("###=== "+err);
                                fwLog.write("###=== "+err+"\r\n");
                                logCount++;
                                if (isFileEnded) break;
                                continue;
                            }
                            String csvStr = FileUtil.readFileIntoString(tempCSVFile);
                            fw.write(csvStr + "\r\n");
                            (new File(tempCSVFile)).delete();
                        }
                        if (isFileEnded) break;
                        continue;
                    }
                    msg = msg + line + "\r";
                }

                fr.close();
                br.close();
                fw.close();
                if (logCount == 0) fwLog.write("Any Error or Warning was not found during Converting : " + v2File);
                fwLog.close();

                fr = null;
                br = null;
                fw = null;
                fwLog = null;
            }
            catch(FileNotFoundException fe)
            {
                errorLevel = JOptionPane.ERROR_MESSAGE;
                message = "FileNotFoundException : " + fe.getMessage();
                messageTitle = "FileNotFoundException";
            }
            catch(IOException ie)
            {
                errorLevel = JOptionPane.ERROR_MESSAGE;
                message = "IOException : " + ie.getMessage();
                messageTitle = "IOException";
            }

        try { if (fr != null) fr.close(); } catch(IOException ie) {}
        try { if (br != null) br.close(); } catch(IOException ie) {}
        try { if (fw != null) fw.close(); } catch(IOException ie) {}
        try { if (fwLog != null) fwLog.close(); } catch(IOException ie) {}

        if (logCount > 0)
        {
            //logFileName = fileCSV + ".log";
            //fw = new FileWriter(logFileName);
            //for (String err:errorMessages) fw.write(err + "\r\n");
            //fw.close();
            if (failCount == n)
            {
                errorLevel = JOptionPane.ERROR_MESSAGE;
                message = "The All " + failCount + " V2 messages were fail to generate csv file. \n Log file '" + fileCSV + ".log' is created. Take a look if you necessary.";
                messageTitle = "The All" + failCount + " messages Conversion Failure";
            }
            else if (failCount > 0)
            {
                errorLevel = JOptionPane.WARNING_MESSAGE;
                message = "" + failCount + " message(s) were(was) fail among "+n+" V2 messages. \n Log file '" + fileCSV + ".log' is created. Take a look if you necessary.";
                messageTitle = "" + failCount + " Conversion Failure";
            }
            else
            {
                errorLevel = JOptionPane.INFORMATION_MESSAGE;
                message = "" + n + " V2 messages were successfully converted. \n Log file '" + fileCSV + ".log' is created. Take a look if you necessary.";
                messageTitle = "Log File Created";
            }

        }
        else
        {
            errorLevel = JOptionPane.INFORMATION_MESSAGE;
            message = "" + n + " V2 messages were successfully converted. (Neither Error nor Warning)";
            messageTitle = "Successully Converted";

        }
        this.logFileName = logFileName;
        messageCount = n;

        System.out.println("*** " + message);

    }

    public List<String> getErrorMessages()
    {
        if (logFileName == null) return new ArrayList<String>();
        try
        {
            return FileUtil.readFileIntoList(logFileName);
        }
        catch(IOException ie)
        {}
        return null;
    }
    public int getErrorLevel()
    {
        return errorLevel;
    }
    public String getMessage()
    {
        return message;
    }
    public String getMessageTitle()
    {
        return messageTitle;
    }
    public int getMessageCount()
    {
        return messageCount;
    }
    public List<String> getFailErrorMessageList()
    {
        return failErrorMessageList;
    }
    public String getLogFileName()
    {
        return logFileName;
    }
    public int getFailCount()
    {
        return failErrorMessageList.size();
    }
    public boolean isSuccessful()
    {
        if (errorLevel == JOptionPane.ERROR_MESSAGE) return false;
        if (errorLevel < 0) return false;
        return true;
    }
    public List<Integer> getFailMessageSequenceList()
    {
        List<Integer> list = new ArrayList<Integer>();

        for (String str:getFailErrorMessageList())
        {
            str = str.trim();
            int idx = str.indexOf(numberTag);
            if (idx < 0) continue;
            str = str.substring(idx + numberTag.length());
            try
            {
                list.add(Integer.parseInt(str.substring(0,str.indexOf(" "))));
            }
            catch(NumberFormatException ne) { continue; }
        }
        return list;
    }

    public static void main(String[] args)
    {
        if (args.length != 6)
        {
            System.out.println("## Argument List (6 items) : \n      {COMMAND} v2MetaDir v2MsgFile v2MsgType v2Version csvOutputFileName scsFileForValidating");
            System.out.println("   Sample usage : {COMMAND} \"C:\\temp data\\v2Meta\" c:\\c.hl7 ADT^A03 2.4 c:\\a.csv c:\\a.scs");
            return;
        }
        int n = 0;
        for(String arg:args)
        {
            if (arg.startsWith("\"")) arg = arg.substring(1);
            if (arg.endsWith("\"")) arg = arg.substring(0, arg.length()-1);
            arg = arg.trim();
            if ((n==0)&&(arg.equals(""))) arg = FileUtil.getV2DataDirPath();
            if ((n==2)&&(arg.length()==6)) arg = arg.substring(0,3) + "^" + arg.substring(3);
            if ((n==2)&&(arg.length()==7)) arg = arg.substring(0,3) + "^" + arg.substring(4);
            args[n] = arg;

            n++;
        }
        try
        {
            ConvertFromV2ToCSV con = new ConvertFromV2ToCSV(args[0], args[1], args[2], args[3], args[4], args[5], false);
            if (con.isSuccessful()) System.out.println("## Good : " + con.getMessage());
            else System.out.println("## Bad : " + con.getMessage());
        }
        catch(Exception ee)
        {
            System.out.println("## Sorry! : " + ee.getMessage());
        }

    }
}

/**
 * HISTORY      : : ConvertFromV2ToCSV.java,v $
 */
