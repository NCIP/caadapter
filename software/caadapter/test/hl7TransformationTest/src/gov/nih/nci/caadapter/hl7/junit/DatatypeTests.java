/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.hl7.junit;

import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeParser;

import java.util.HashSet;
import java.util.Hashtable;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.*;

/**
 * The class will test the following features of the Datatype Parser.
 * 1. Simple datatype parser
 * 2. Complex datatype parser
 * 3. Vocucabulary parser
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision $Revision: 1.2 $ date $Date: 2008-06-09 19:53:51 $
 */

public class DatatypeTests extends TestCase {
	public Hashtable datatypes = null;
	 @Before public void loadDatatypes() {
		 if (datatypes == null) {
			DatatypeParser datatypeParser = new DatatypeParser();
			datatypeParser.loadDatatypes();
			datatypes = datatypeParser.getDatatypes();
		 }
	 }

	 /*
	  * Make sure complex datatype II is populated correctly
	  */
	 @Test public void testComplexDatatype () {
		 loadDatatypes();
		 assertNotNull(datatypes);
		 Datatype datatype = (Datatype) datatypes.get("II");
		 assertNotNull(datatype);
		 assertFalse(datatype.isSimple());
		 Hashtable attributes = datatype.getAttributes();
		 Attribute attribute = (Attribute)attributes.get("root");
		 assertNotNull(attribute);
		 attribute = (Attribute)attributes.get("extension");
		 assertNotNull(attribute);
		 attribute = (Attribute)attributes.get("assigningAuthorityName");
		 assertNotNull(attribute);
		 attribute = (Attribute)attributes.get("displayable");
		 assertNotNull(attribute);
		 attribute = (Attribute)attributes.get("nullFlavor");
		 assertNotNull(attribute);

		 datatype = (Datatype) datatypes.get("GTS");
		 assertNotNull(datatype);
	 }
	 /*
	  * Make sure simple datatype cs is populated correctly
	  */
	 @Test public void testSimple() {
		 loadDatatypes();
		 assertNotNull(datatypes);
		 Datatype datatype = (Datatype) datatypes.get("cs");
		 assertNotNull(datatype);
		 assertTrue(datatype.isSimple());
	 }
	 /*
	  * Make sure voc datatype AcknowledgementDetailNotSupportedCode is populated correctly
	  */
	 @Test public void testVocabulary() {
		 loadDatatypes();
		 assertNotNull(datatypes);
		 Datatype datatype = (Datatype) datatypes.get("AcknowledgementDetailNotSupportedCode");
		 assertNotNull(datatype);
		 assertTrue(datatype.isSimple());
		 HashSet pred = datatype.getPredefinedValues();
		 assertNotNull(pred.contains("NS260"));
		 assertNotNull(pred.contains("NS261"));
		 assertNotNull(pred.contains("NS200"));
		 assertNotNull(pred.contains("NS250"));
		 assertNotNull(pred.contains("NS202"));
		 assertNotNull(pred.contains("NS203"));
	 }
	 public static junit.framework.Test suite() {
		  return new JUnit4TestAdapter(DatatypeTests.class);
		}
	 public static void main(String[] argvs) {
		 DatatypeTests dt = new DatatypeTests();
		 dt.loadDatatypes();
		 Datatype data = (Datatype)dt.datatypes.get("II");
		 System.out.println(data);
	 }
}
