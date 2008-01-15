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


import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
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

    private List<String> outFileList = new ArrayList<String>();
    private List<String> errorList = new ArrayList<String>();
    private List<String> logList = new ArrayList<String>();

    public TempV2FromCSV(String csvFileStr, String outDirStr) throws Exception
    {
        File csvFile = new File(csvFileStr);
        if (!csvFile.exists()) throw new Exception("Not exist csv file : " + csvFileStr);
        if (!csvFile.isFile()) throw new Exception("Not valid csv file type : " + csvFileStr);

        File outDir = new File(outDirStr);
        if (!outDir.exists()) 
        {
        	outDirStr=System.getProperty("user.dir");
        	outDir = new File(outDirStr);     
        }
        if (!outDir.isDirectory()) throw new Exception("Not valid output directory : " + outDirStr);


        FileReader fr = null;

        try { fr = new FileReader(csvFileStr); }
        catch(FileNotFoundException fe) { throw new Exception("FileNotFoundException in FileUtil.readFileIntoList() : " + csvFileStr); }

        BufferedReader br = new BufferedReader(fr);
        String readLineOfFile = "";
        int n = 0;
        StringBuffer sb=new StringBuffer();
        try
        {
            DateFunction df = new DateFunction();
            //String fileTag = df.getCurrentTime() + "_" + FileUtil.getRandomNumber(4);
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

                //System.out.println(mesg + "\n");

                String errTag = null;
                    sb.append(mesg.toString());
                    sb.append("\n");
                if (mesg.indexOf("%!") >= 0) errTag = "%! Insufficient data";

                if (errTag != null)
                {
                    String err = "ERROR (line "+n+") : " + errTag + " : " + readLineOfFile;
                    System.out.println(err);
                    errorList.add(err);
                    logList.add(err);
                    continue;
                }

           }
            String outFileName=outDir.getAbsolutePath()+File.separator+"messageOut.hl7";
            System.out.println("TempV2FromCSV.TempV2FromCSV()..output File:"+outFileName);
            FileWriter fw=new FileWriter(outFileName);
            fw.write(sb.toString());
            fw.close();
        }
        catch(IOException ie) { throw new Exception("File reading Error in FileUtil.readFileIntoList() : " + csvFileStr); }

        try
        {
            fr.close();
            br.close();
        }
        catch(IOException ie) { throw new IOException("File Closing Error in FileUtil.readFileIntoList() : " + csvFileStr); }
        //for (String a : logList) System.out.println(a);
    }

    public List<String> getOutFileList() { return outFileList; }
    public List<String> getErrorList() { return errorList; }
    public List<String> getLogList() { return logList; }

    public void deleteOutFiles()
    {
        for (int i=0;i<outFileList.size();i++)
        {
            File file = new File(outFileList.get(i));
            if (!file.exists()) continue;
            if (!file.isFile()) continue;

            file.delete();
        }
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
        DateFunction df = new DateFunction();
        String nowDate = df.getCurrentTime();

        String t = "";
        t = "MSH|^~\\&|VISION|VISION-DDAP|LOGIC|LOGIC|%!NOW!%+0000^S|NO SECURITY|ADT^A08|%!ID!%|P|2.3|||AL||||\r\n" +
            "EVN|A08|%!NOW!%+0000^S|||00000|\r\n" +
            "PID||V%!1!%|V%!1!%|V%!1!%|%!2!%^%!3!%^%!4!%||%!5!%|%!6!%|||%!8!%^%!9!%^%!10!%^%!11!%^%!12!%||||||||%!7!%|||||||||||\r\n";
        return t;
    }

    public static void main(String[] args)
    {
//    	String fileHome="C:\\Documents and Settings\\wangeug\\My Documents\\caAdapter\\csvToHLv2";
    	if (args.length<2)
    	{
    		System.out.println("Usage: sourceFileName|output Dir");
    	}

    	try
        {
            new TempV2FromCSV(args[0],args[1]); 
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}

/**
 * HISTORY      : : TempV2FromCSV.java,v $
 */
