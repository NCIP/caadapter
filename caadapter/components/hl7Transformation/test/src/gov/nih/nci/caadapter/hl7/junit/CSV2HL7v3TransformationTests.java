/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.junit;

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.transformation.TransformationService;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;

import java.util.List;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;

import org.junit.Test;

/**
 * The class will test the CSV to HL7v3 transformation service.
 * 
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wuye $
 * @version Since caAdapter v4.0 revision $Revision: 1.2 $ date $Date: 2007-08-13 20:26:50 $
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
	 public static junit.framework.Test suite() {
		  return new JUnit4TestAdapter(CSV2HL7v3TransformationTests.class);    
		}
}
