/*
 *  : /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/common/standard/impl/TempV2FromCSV.java,v 1.00 Jan 14, 2008 10:27:02 PM umkis Exp $
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

package gov.nih.nci.caadapter.hl7.v2v3.v2gene;

import gov.nih.nci.caadapter.common.function.DateFunction;
import gov.nih.nci.caadapter.common.util.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.7 $
 *          date        Jan 14, 2008
 *          Time:       10:27:02 PM $
 */
public class TempV2FromCSV
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = ": TempV2FromCSV.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = ": /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/common/standard/impl/TempV2FromCSV.java,v 1.00 Jan 14, 2008 10:27:02 PM umkis Exp $";

    private String errorListFileName = null;
    private String logFileName = null;
    private String outputFileName = null;
    private int recordCount = 0;
    private int errorCount = 0;
    private int successCount = 0;

    public TempV2FromCSV(String csvFileStr, String outFileStr) throws Exception
    {
        File csvFile = new File(csvFileStr);
        if (!csvFile.exists()) throw new Exception("Not exist csv file : " + csvFileStr);
        if (!csvFile.isFile()) throw new Exception("Not valid csv file type : " + csvFileStr);
        csvFileStr = csvFile.getAbsolutePath();

        File outFile = new File(outFileStr);
        if (!outFile.isDirectory())
        {
            throw new Exception("This output file name is not a directory. : " + outFileStr);
        }
        outFileStr = outFile.getAbsolutePath();
        //FileUtil.saveStringIntoTemporaryFile(outFileStr, "test");

        DateFunction df = new DateFunction();
        String fname = outFileStr + File.separator + "VISION" + df.getCurrentTime() + "_" + FileUtil.getRandomNumber(4);

        errorListFileName = fname + ".err";
        logFileName = fname + ".log";
        outputFileName = fname + ".hl7";

        FileWriter fw_msg = new FileWriter(outputFileName);
        FileWriter fw_err = new FileWriter(errorListFileName);
        FileWriter fw_log = new FileWriter(logFileName);

        String init = "SOURCE CSV FILE NAME : " + csvFileStr + "\r\n"
                    + "OUTPUT HL7 V2 MESSAGE FILE NAME : " + outputFileName + "\r\n" + "\r\n";

        fw_msg.write("");
        try
        {
            fw_err.write("This is a Error list file : \r\n" + init);
        }
        catch(IOException ie)
        {
            System.err.println("Error List file initail writing failure : " + errorListFileName);
        }
        try
        {
            fw_log.write("This is a log file : \r\n" + init);
        }
        catch(IOException ie)
        {
            System.err.println("Log file initail writing failure : " + logFileName);
        }

        FileReader fr = new FileReader(csvFileStr);

        //try { fr = new FileReader(csvFileStr); }
        //catch(FileNotFoundException fe) { throw new Exception("FileNotFoundException in FileUtil.readFileIntoList() : " + csvFileStr); }

        BufferedReader br = new BufferedReader(fr);
        String readLineOfFile = "";
        int n = 0;
        StringBuffer sb=new StringBuffer();
        try
        {

            while((readLineOfFile=br.readLine())!=null)
            {
                String[] csvData = getCSVDataFromReadLine(readLineOfFile);
                if (csvData == null) continue;
                boolean cTag = false;
                for(int i=0;i<csvData.length;i++) if (!((csvData[i]==null)||(csvData[i].trim().equals("")))) cTag = true;
                if (!cTag) continue;
                
                n++;
                String lineNum = "" + n;
                while(lineNum.length() < 6) lineNum = "0" + lineNum;

                String mesg = getSampleV2MessageFormat();

                for (int i=0;i<csvData.length;i++)
                {
                    String data = csvData[i];
                    if (data == null) data = "";
                    String tag = "%!" + (i+1) + "!%";
                    mesg = mesg.replaceAll(tag, data);
                }

                String now = df.getCurrentTime();
                String mesg_ID = "VISION" + now + "_" + lineNum;
                mesg = mesg.replaceAll("%!NOW!%", now);
                mesg = mesg.replaceAll("%!ID!%", mesg_ID);

                String errTag = null;
                if (mesg.indexOf("%!") >= 0) errTag = "%! Insufficient data";
                else
                {
                    try
                    {
                        fw_msg.write(mesg);
                        successCount++;
                    }
                    catch(IOException ie)
                    {
                        errTag = "Message writing error \r\n--------------------\r\n" + mesg + "***********************\r\n";
                        //System.err.println(errTag);
                    }
                }
                //sb.append(mesg.toString());
                //sb.append("\r\n");


                if (errTag == null)
                {
                    try
                    {
                        fw_log.write("SUCCESS (line "+n+") : " + readLineOfFile + "\r\n");
                    }
                    catch(IOException ie)
                    {

                    }
                }
                else
                {
                    String err = "ERROR (line "+n+") : " + errTag + " : " + readLineOfFile;
                    System.out.println(err);
                    try
                    {
                        fw_err.write(err + "\r\n");
                        errorCount++;
                        fw_log.write("ERROR (line "+n+") : " + readLineOfFile + "\r\n");
                    }
                    catch(IOException ie)
                    {
                        
                    }

                }

            }
            recordCount = n;
            try
            {
                fw_err.write("\r\n\r\n  TOTAL COUNT = " + recordCount + "\r\n  ERROR COUNT = " + errorCount);
                fw_err.close();
                fw_log.write("\r\n\r\n  TOTAL COUNT = " + recordCount + "\r\n  SUCCESS COUNT = " + successCount + "\r\n  ERROR COUNT = " + errorCount);
                fw_log.close();
                fw_msg.close();
            }
            catch(IOException ie)
            {

            }

            if (recordCount == 0)
            {
                (new File(this.getLogFileName())).delete();
                (new File(this.getErrorListFileName())).delete();
                (new File(this.getOutputFileName())).delete();
                throw new Exception("No record in this csv file : " + csvFileStr);
            }
            //if (errorCount == 0) (new File(this.getErrorListFileName())).delete();

        }
        catch(IOException ie) { throw new Exception("File reading Error in FileUtil.readFileIntoList() : " + csvFileStr); }

        try
        {
            fr.close();
            br.close();
        }
        catch(IOException ie) { throw new IOException("File Closing Error in FileUtil.readFileIntoList() : " + csvFileStr); }
    }

    private String[] getCSVDataFromReadLine(String line)
    {
        String[] data = new String[12];
        for(int i=0;i<data.length;i++) data[i] = null;
        if ((line == null)||(line.trim().equals(""))) return null;
        line = line.trim();
        int n = 0;
        boolean quot = false;
        for(int i=0;i<line.length();i++)
        {
            if (data[n] == null) data[n] = "";
            String achar = line.substring(i, i+1);
            if (quot)
            {
                if (achar.equals("\"")) quot = false;
                else data[n] = data[n] + achar;
            }
            else
            {
                if (achar.equals("\"")) quot = true;
                else if (achar.equals(",")) n++;
                else data[n] = data[n] + achar;
            }
        }
        return data;
    }

    private String getSampleV2MessageFormat()
    {
        String t = "";
        t = "MSH|^~\\&|VISION|VISION-DDAP|LOGIC|LOGIC|%!NOW!%+0000^S|NO SECURITY|ADT^A08|%!ID!%|P|2.3|||AL||||\r\n" +
            "EVN|A08|%!NOW!%+0000^S|||00000|\r\n" +
            "PID||V%!1!%|V%!1!%|V%!1!%|%!2!%^%!3!%^%!4!%||%!5!%|%!6!%|||%!8!%^%!9!%^%!10!%^%!11!%^%!12!%||||||||%!7!%|||||||||||\r\n" +
            "PV1||N|This segment is a dummy for protecting from V2 message validation error|\r\n\r\n";
        return t;
    }

    public String getErrorListFileName() { return errorListFileName; }
    public String getLogFileName() { return logFileName; }
    public String getOutputFileName() { return outputFileName; }
    public int getRecordCount() { return recordCount; }
    public int getErrorCount() { return errorCount; }
    public int getSuccessCount() { return successCount; }

    public static void main(String[] args)
    {
    	if (args.length < 2) System.out.println("Usage: sourceFileName|output Dir");
        else
        {
            try
            {
                TempV2FromCSV gen = new TempV2FromCSV(args[0], args[1]);
                System.out.println("** " + gen.getSuccessCount() + " HL7 v2 Messages are successfully generated.");
                System.out.println("HL7 v2 Message file => " + gen.getOutputFileName());
                System.out.println("Error recode count => " + gen.getErrorCount());
                if (gen.getErrorCount() > 0) System.out.println("Error list file => " + gen.getErrorListFileName());
                System.out.println("Log file => " + gen.getLogFileName());
            }
            catch(Exception e)
            {
                System.err.println("Error : " + e.getMessage());
            }
        }
    }
}

/**
 * HISTORY      : : TempV2FromCSV.java,v $
 */
