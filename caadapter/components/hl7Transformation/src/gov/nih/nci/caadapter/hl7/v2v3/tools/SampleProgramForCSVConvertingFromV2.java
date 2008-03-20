/*
 *  : /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/common/standard/impl/SampleProgramForCSVConvertingFromV2.java,v 1.00 Jan 31, 2008 7:21:16 PM umkis Exp $
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

package gov.nih.nci.caadapter.hl7.v2v3.tools;

import gov.nih.nci.caadapter.ui.mapping.V2V3.ConvertFromV2ToCSV;

import javax.swing.*;
import java.util.List;


public class SampleProgramForCSVConvertingFromV2
{



    public SampleProgramForCSVConvertingFromV2()
    {
        // Constructor
        ConvertFromV2ToCSV con = new ConvertFromV2ToCSV("C:\\projects\\temp\\v2Meta" // v2 meta data directory
                                                        , "c:\\v2.msg"               // input v2 message file
                                                        , "ORU^R01"                  // message type
                                                        , "2.4"                      // target version
                                                        , "c:\\fff.csv"              // output csv file
                                                        , "c:\\fff.scs"              // scs file for validation
                                                        , false                      // this value must be false
                                                        );
        // If input v2 file is well made, with only the constructor, output csv file will be generated.

        // APIs

        boolean result = con.isSuccessful();    // if true, output csv file is normally generated.
        String logFile = con.getLogFileName();  // Generated log file name. General format is {output csv file name} + '.log'.
        int errorLevel = con.getErrorLevel();   // Values are followings
                                                // JOptionPane.ERROR_MESSAGE :  output csv file was not generated.
                                                // JOptionPane.WARNING_MESSAGE :  normally generated, but some messages.
                                                // JOptionPane.INFORMATION_MESSAGE :  All the messages are successfully converted.
        String message = con.getMessage();      // This message explains why the errorLevel.
        List<String> errorMessages = con.getErrorMessages(); // this list includes the log file contents.
        int messageCount = con.getMessageCount();   // How many v2 messages are in the input file.
        int failCount = con.getFailCount();         // How many fail messages are among the v2 input messages.
        List<Integer> fails = con.getFailMessageSequenceList(); // The list of sequence number which faild in the input file.

        System.out.println("### result : " + result);
        System.out.println("### logFile : " + logFile);
        System.out.println("### errorLevel : " + errorLevel);
        if (errorLevel == JOptionPane.ERROR_MESSAGE) System.out.println(" ## This errorLevel : ERROR!! ");
        if (errorLevel == JOptionPane.WARNING_MESSAGE) System.out.println(" ## This errorLevel : WARNING!! ");
        if (errorLevel == JOptionPane.INFORMATION_MESSAGE) System.out.println(" ## This errorLevel : INFORMATION!! ");
        System.out.println("### message : " + message);
        for (String st:errorMessages) System.out.println("### errorMessages : " + st);
        System.out.println("### messageCount : " + messageCount);
        System.out.println("### failCount : " + failCount);
        for (int in:fails) System.out.println("### fails : " + in);


    }

    public static void main(String[] args)
    {
        new SampleProgramForCSVConvertingFromV2();
    }

}

/**
 * HISTORY      : : SampleProgramForCSVConvertingFromV2.java,v $
 */
