/*
 * <!-- LICENSE_TEXT_START -->
 *  : /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/common/standard/impl/TestTransformationServiceCSV_V3.java,v 1.00 Mar 6, 2008 12:39:01 PM umkis Exp $
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
 
package gov.nih.nci.caadapter.hl7.v2v3.test;

import gov.nih.nci.caadapter.hl7.transformation.TransformationService;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;

import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.2 $
 *          date        Mar 6, 2008
 *          Time:       12:39:01 PM $
 */
public class TestTransformationServiceCSV_V3
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = ": TestTransformationServiceCSV_V3.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = ": /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/common/standard/impl/TestTransformationServiceCSV_V3.java,v 1.00 Mar 6, 2008 12:39:01 PM umkis Exp $";

    public TestTransformationServiceCSV_V3(String csv, String map)
    {
        TransformationService ts = new TransformationService(csv, map);
        List<XMLElement> msgs = null;
        try
        {
            msgs = ts.process();
        }
        catch(Exception ee)
        {
            System.out.println("Exception : " + ee.getMessage());
            ee.printStackTrace();
            return;
        }
        int n = 0;
        for(XMLElement msg:msgs)
        {
            n++;
            System.out.println("#### MESSAGE " + n + " #####");
            System.out.println(msg.toXML().toString());
        }
        System.out.println("\n#### Validation Results #####");
        System.out.println(ts.getValidatorResults().toString());

    }

    public static void main(String[] args)
    {
        String csv = "C:\\projects\\caadapter\\workingspace\\CDA\\POCD_MT000030.csv";
        String map = "C:\\projects\\caadapter\\workingspace\\CDA\\POCD_MT000030.map";
        new TestTransformationServiceCSV_V3(map, csv);
    }
}

/**
 * HISTORY      : : TestTransformationServiceCSV_V3.java,v $
 */
