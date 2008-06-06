/*
 * <!-- LICENSE_TEXT_START -->
 *  : /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/common/standard/impl/SampleTempV2FromCSV.java,v 1.00 Jan 15, 2008 1:03:44 PM umkis Exp $
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
* <!-- LICENSE_TEXT_END -->
 */
 
package gov.nih.nci.caadapter.hl7.v2v3.v2gene;

import gov.nih.nci.caadapter.common.util.FileUtil;

import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.4 $
 *          date        Jan 15, 2008
 *          Time:       1:03:44 PM $
 */
public class SampleTempV2FromCSV
{
    public static void main(String[] args)
    {
        TempV2FromCSV gen = null;
        try
        {
            // argument 1 : input csv file name
            // argument 2 : output directory (full pathname)
            gen = new TempV2FromCSV("C:\\baylorhealth\\Sample input file Test 1.csv", "C:\\baylorhealth\\test");
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        // At this point the v2 message file may be generated at the output directory.
        // The all messages are saved to the output file of which file name can be gotten by getOutputFileName().
        // "Message Control-ID" consists of "VISION" + Generating Time(yyyymmddhhMMss) + "_" + 6 digit record_number in the input csv file.


        String outFile = gen.getOutputFileName();       // get the output v2 message file name
        String logFile = gen.getLogFileName();          // get the log file name for each csv record.
        String errorFile = gen.getErrorListFileName();  // get the error message list file name for each failure csv record.
        int successCount = gen.getSuccessCount();       // get the number of the records generated successfully
        int errorCount = gen.getErrorCount();           // get the number of the error records
        int recordCount = gen.getRecordCount();         // get the number of the total records in the csv file


        System.out.println("\n\n--------------------------\nMessagr File ("+outFile+")\n\n" + FileUtil.readFileIntoString(outFile));
        System.out.println("\n\n--------------------------\nLog File ("+logFile+")\n\n" + FileUtil.readFileIntoString(logFile));
        System.out.println("\n\n--------------------------\nError File ("+errorFile+")\n\n" + FileUtil.readFileIntoString(errorFile));


    }

}

/**
 * HISTORY      : : SampleTempV2FromCSV.java,v $
 */
