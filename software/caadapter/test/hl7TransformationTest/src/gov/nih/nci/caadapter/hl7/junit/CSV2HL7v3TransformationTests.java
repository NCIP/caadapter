/*L
 * Copyright SAIC, SAIC-Frederick.
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
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.transformation.TransformationService;
import gov.nih.nci.caadapter.hl7.transformation.TransformationServiceUtil;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;

import java.util.List;
import java.io.File;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;

import org.junit.Test;

/**
 * The class will test the CSV to HL7v3 transformation service.
 * 
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v4.0 revision $Revision: 1.5 $ date $Date: 2008-12-11 00:33:50 $
 */

public class CSV2HL7v3TransformationTests extends TestCase {

	/**
	  * Test Mapping Scenario #1
	  */
	 @Test public void testMappingScenario1_1() throws Exception{
	        TransformationService ts = new TransformationService("data/Transformation/COCT_MT010000_MAP1-1.map",
			"data/Transformation/COCT_MT01000_Person.csv");

	        List<XMLElement> xmlElements = ts.process();
	        assertEquals(1,xmlElements.size());
	        for(XMLElement rootElement: xmlElements) {
	        	assertEquals("Encounter", rootElement.getName());
        		boolean consultant = false;
	        	for(XMLElement childElement: rootElement.getChildren()) {
	        		if (childElement.getName().equals("consultant"))
	        		{
	        			consultant = true;
	        			assertEquals(2, childElement.getChildren().size()); //1 for modecode and 1 for assignedPerson
	        		}
	        	}
	        	assertEquals(true, consultant);
	        	rootElement.validate();
	        	List<Message> vs = rootElement.getValidatorResults().getAllMessages();
	        	assertEquals(9,vs.size());
	        	boolean messageFlag = false;
	        	for(Message message:vs) {
	        		if (message.toString().equals("H3S Data Object modeCode has 2 attribute(s), but HL7 Attribute Encounter.consultant00.modeCode specifies cardinality 0..1"))
	        		{
	        			messageFlag = true;
	        		}
	        	}
	        	assertEquals(true, messageFlag);
	        }
	        
	 }

	 /**
	  * Test Mapping Scenario #3
	  */
	 @Test public void testMappingScenario3_1() throws Exception{
	        TransformationService ts = new TransformationService("data/Transformation/COCT_MT150003_MAP3-1.map",
			"data/Transformation/COCT_MT150003_MAP_Scenario_Test.csv");

	        List<XMLElement> xmlElements = ts.process();
	        assertEquals(1,xmlElements.size());
	        for(XMLElement rootElement: xmlElements) {
	        	assertEquals("Organization", rootElement.getName());
        		assertEquals(2,rootElement.getChildren().size());
        		boolean contacPartyAppearence = false;
	        	for(XMLElement childElement: rootElement.getChildren()) {
	        		if (childElement.getName().equals("contactParty")) {
	        			contacPartyAppearence = true;
	        			assertEquals(3,childElement.getChildren().size());
	        			assertEquals("addr", childElement.getChildren().get(0).getName());
	        		}
	        	}
	        	assertEquals(true, contacPartyAppearence);
	        }
	 }

	/**
	  * Test Mapping Scenario #7
	  */
	 @Test public void testMappingScenario7_1() throws Exception{
	        TransformationService ts = new TransformationService("data/Transformation/COCT_MT150003_MAP7-1.map",
			"data/Transformation/COCT_MT150003_MAP_Scenario_Test.csv");

	        List<XMLElement> xmlElements = ts.process();
	        assertEquals(1,xmlElements.size());
	        for(XMLElement rootElement: xmlElements) {
	        	assertEquals("Organization", rootElement.getName());
	        	assertEquals(3,rootElement.getChildren().size());
        		boolean contacPartyAppearence = false;
	        	for(XMLElement childElement: rootElement.getChildren()) {
	        		if (childElement.getName().equals("contactParty")) {
	        			contacPartyAppearence = true;
	        			assertEquals(2,childElement.getChildren().size());
	        			assertEquals("addr", childElement.getChildren().get(0).getName());
	        			break;
	        		}
	        	}
	        	assertEquals(true, contacPartyAppearence);
	        }
	 }
	 
	 @Test public void testBatchMappingScenario1_Read_200M() throws Exception{
		 long start = System.currentTimeMillis();
		 //int count = TransformationService.countEntriesInZip(new File("data/Transformation/tmpout2.zip"));
         int count = TransformationServiceUtil.countEntriesInZip(new File("data/Transformation/tmpout2.zip"));
         long end1 = System.currentTimeMillis();
		 System.out.println("counted "+count+" in "+(end1-start)+" ms");
		 int seq = 59127;
		 String ret = TransformationServiceUtil.readFromZip(new File("data/Transformation/tmpout2.zip"), String.valueOf(seq)+".xml");
		 long end2 = System.currentTimeMillis();
		 System.out.println("read #"+seq+" in "+(end2-end1)+" ms:["+ret+"]");
	 }

	 @Test public void testBatchMappingScenario1_1() throws Exception{
		 long start = System.currentTimeMillis();
		 TransformationService ts = new TransformationService("data/Transformation/COCT_MT010000_MAP1-1.map",
		 "data/Transformation/COCT_MT01000_Person.csv");
		 ts.setOutputFile(new File("data/Transformation/tmpout.zip"));
		 int count1 = ts.batchProcess();
		 long end1 = System.currentTimeMillis();
		 System.out.println("batch finished = "+(end1-start)+" ms");
		 ts = new TransformationService("data/Transformation/COCT_MT010000_MAP1-1.map",
		 "data/Transformation/COCT_MT01000_Person.csv");
		 List<XMLElement> xmlElements = ts.process();
		 int count2 = xmlElements.size();
		 long end2 = System.currentTimeMillis();
		 System.out.println("batch time = "+(end1-start)+" ms, old time = "+(end2-end1));
		 assertEquals(count1,count2);
	 }

	 @Test public void testBatchMappingScenario1_1M() throws Exception{
		 long start = System.currentTimeMillis();
		 TransformationService ts = new TransformationService("data/Transformation/COCT_MT010000_MAP1-1.map",
		 "data/Transformation/COCT_MT01000_Person_1M.csv");
		 ts.setOutputFile(new File("data/Transformation/tmpout.zip"));
		 int count1 = ts.batchProcess();
		 long end1 = System.currentTimeMillis();
		 System.out.println("batch finished = "+(end1-start)+" ms");
		 ts = new TransformationService("data/Transformation/COCT_MT010000_MAP1-1.map",
		 "data/Transformation/COCT_MT01000_Person_1M.csv");
		 List<XMLElement> xmlElements = ts.process();
		 int count2 = xmlElements.size();
		 long end2 = System.currentTimeMillis();
		 System.out.println("batch time = "+(end1-start)+" ms, old time = "+(end2-end1));
		 assertEquals(count1,count2);
	 }

	 @Test public void testBatchMappingScenario1_20M() throws Exception{
		 long start = System.currentTimeMillis();
		 TransformationService ts = new TransformationService("data/Transformation/COCT_MT010000_MAP1-1.map",
		 "data/Transformation/COCT_MT01000_Person_20M.csv");
		 ts.setOutputFile(new File("data/Transformation/tmpout.zip"));
		 int count1 = ts.batchProcess();
		 long end1 = System.currentTimeMillis();
		 System.out.println("batch finished = "+(end1-start)+" ms");
		 ts = new TransformationService("data/Transformation/COCT_MT010000_MAP1-1.map",
		 "data/Transformation/COCT_MT01000_Person_20M.csv");
		 List<XMLElement> xmlElements = ts.process();
		 int count2 = xmlElements.size();
		 long end2 = System.currentTimeMillis();
		 System.out.println("batch time = "+(end1-start)+" ms, old time = "+(end2-end1));
		 assertEquals(count1,count2);
	 }

	 @Test public void testBatchMappingScenario1_200M() throws Exception{
		 long start = System.currentTimeMillis();
		 TransformationService ts = new TransformationService("data/Transformation/COCT_MT010000_MAP1-1.map",
		 "data/Transformation/COCT_MT01000_Person_200M.csv");
		 ts.setOutputFile(new File("data/Transformation/tmpout.zip"));
		 int count1 = ts.batchProcess();
		 long end1 = System.currentTimeMillis();
		 System.out.println("batch finished = "+(end1-start)+" ms");
		 ts = new TransformationService("data/Transformation/COCT_MT010000_MAP1-1.map",
		 "data/Transformation/COCT_MT01000_Person_200M.csv");
		 List<XMLElement> xmlElements = ts.process();
		 int count2 = xmlElements.size();
		 long end2 = System.currentTimeMillis();
		 System.out.println("batch time = "+(end1-start)+" ms, old time = "+(end2-end1));
		 assertEquals(count1,count2);
	 }

	 public static junit.framework.Test suite() {
		  return new JUnit4TestAdapter(CSV2HL7v3TransformationTests.class);    
		}
}
