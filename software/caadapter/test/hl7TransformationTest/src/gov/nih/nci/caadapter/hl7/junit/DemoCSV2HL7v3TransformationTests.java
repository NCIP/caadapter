/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.junit;

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.hl7.transformation.TransformationService;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;

import java.util.List;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;

import org.junit.Test;

/**
 * The class tests the CSV to HL7v3 transformation service.
 * 
 * @author OWNER: Wang, Eugene
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0 revision $Revision: 1.7 $ date $Date: 2009-04-16 13:29:51 $
 */

public class DemoCSV2HL7v3TransformationTests extends TestCase {


	 /**
	  * Test general transformation
	  */
	 @Test public void testTransformation() throws Exception{
//	     String csvSrcFile=System.getProperty("hl7.transformation.src.file");
//	     String mapFile=System.getProperty("hl7.transformation.map.file");
		 String dataHome="C:\\eclipseJ2ee\\workspace\\caadapter\\workingspace\\V2Meta_to_V3";
	     String mapFile=dataHome+"\\"+"test.map";		 
		 String csvSrcFile=dataHome+"\\"+"ADT_A01.hl7";
		 
//		 dataHome="C:\\eclipseJ2ee\\workspace\\caadapter\\workingspace\\CSV_to_HL7_V3_Example\\COCT_150001";
//		 csvSrcFile=dataHome+"\\"+"Copy_ COCT_MT150001.csv";
//	     mapFile=dataHome+"\\"+"Copy_150001.map";
	     
		 System.out.println("DemoCSV2HL7v3TransformationTests..sourceFie:"+csvSrcFile);
		 System.out.println("DemoCSV2HL7v3TransformationTests..mapFie:"+mapFile);
		 TransformationService ts = new TransformationService(mapFile, csvSrcFile);//
		 List<XMLElement> xmlElements = ts.process();
	     for(XMLElement rootElement: xmlElements)
	        System.out.println("CSV2HL7v3TransformationTests.testTransformation()..message\n"+rootElement.toXML());
	        
	 }

	 public static junit.framework.Test suite() {
		  return new JUnit4TestAdapter(DemoCSV2HL7v3TransformationTests.class);    
	}
}
