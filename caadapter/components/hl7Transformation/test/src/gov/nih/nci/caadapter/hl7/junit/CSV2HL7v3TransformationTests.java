/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.junit;

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
 * @version Since caAdapter v4.0 revision $Revision: 1.1 $ date $Date: 2007-08-06 22:05:34 $
 */

public class CSV2HL7v3TransformationTests extends TestCase {

	/**
	  * Test Mapping Scenario #1
	  */
	 @Test public void testMappingScenario3_1() throws Exception{
	        TransformationService ts = new TransformationService("data/Transformation/COCT_MT150003_MAP3-1.map",
			"data/Transformation/COCT_MT150003_MAP_Scenario_Test.csv");

	        List<XMLElement> xmlElements = ts.process();
	        assertEquals(1,xmlElements.size());
	        for(XMLElement rootElement: xmlElements) {
	        	assertEquals("Organization", rootElement.getName());
        		assertEquals(1,rootElement.getChildren().size());
	        	for(XMLElement childElement: rootElement.getChildren()) {
	        		assertEquals(2,childElement.getChildren().size());
	        		assertEquals("addr", childElement.getChildren().get(0).getName());
	        	}
	        }
	 }

	 @Test public void testMappingScenario7_1() throws Exception{
	        TransformationService ts = new TransformationService("data/Transformation/COCT_MT150003_MAP7-1.map",
			"data/Transformation/COCT_MT150003_MAP_Scenario_Test.csv");

	        List<XMLElement> xmlElements = ts.process();
	        assertEquals(1,xmlElements.size());
	        for(XMLElement rootElement: xmlElements) {
	        	assertEquals("Organization", rootElement.getName());
	        	assertEquals(2,rootElement.getChildren().size());
	        	for(XMLElement childElement: rootElement.getChildren()) {
	        		assertEquals(1,childElement.getChildren().size());
	        		assertEquals("addr", childElement.getChildren().get(0).getName());
	        	}
	        }
	 }
	 public static junit.framework.Test suite() {
		  return new JUnit4TestAdapter(CSV2HL7v3TransformationTests.class);    
		}
}
