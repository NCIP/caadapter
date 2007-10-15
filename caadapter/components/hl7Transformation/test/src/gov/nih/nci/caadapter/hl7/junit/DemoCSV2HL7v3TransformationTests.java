/**
 * <!-- LICENSE_TEXT_START -->
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
 * @version Since caAdapter v4.0 revision $Revision: 1.4 $ date $Date: 2007-10-15 21:29:21 $
 */

public class DemoCSV2HL7v3TransformationTests extends TestCase {


	 /**
	  * Test general transformation
	  */
	 @Test public void testTransformation() throws Exception{
	     String csvSrcFile=System.getProperty("hl7.transformation.src.file");
	     String mapFile=System.getProperty("hl7.transformation.map.file");
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
