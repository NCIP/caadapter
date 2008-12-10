/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmps.test;


import gov.nih.nci.cbiit.cmps.common.XSDParser;
import gov.nih.nci.cbiit.cmps.core.BaseMeta;
import gov.nih.nci.cbiit.cmps.core.ElementMeta;
import gov.nih.nci.cbiit.cmps.core.Mapping;
import gov.nih.nci.cbiit.cmps.mapping.MappingFactory;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This class is to test the Mapping functions
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.13 $
 * @date       $Date: 2008-12-10 15:43:03 $
 *
 */
public class MappingTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * test XSD parsing and marshaling of the generated Model Object
	 */
	@Test
	public void testParseXSD() throws Exception {
		XSDParser p = new XSDParser();
		p.loadSchema("workingspace/shiporder.xsd");
		ElementMeta e = p.getElementMeta(null, "shiporder");
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Marshaller u = jc.createMarshaller();
		u.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		u.marshal(new JAXBElement(new QName("ElementMeta"),ElementMeta.class, e), new File("bin/shiporder.meta.xml"));
	}
	
	/**
	 * test XSD parsing and marshaling of the generated Model Object
	 * @throws Exception
	 */
	@Test
	public void testParseXSD1() throws Exception {
		XSDParser p = new XSDParser();
		p.loadSchema("workingspace/shiporder1.xsd");
		ElementMeta e = p.getElementMeta(null, "shiporder");
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Marshaller u = jc.createMarshaller();
		u.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		u.marshal(new JAXBElement(new QName("ElementMeta"),ElementMeta.class, e), new File("bin/shiporder1.meta.xml"));
	}
	
	/**
	 * test XSD parsing and marshaling of the generated Model Object
	 */
	@Test
	public void testParseXSD2() throws Exception {
		XSDParser p = new XSDParser();
		p.loadSchema("workingspace/shiporder2.xsd");
		ElementMeta e = p.getElementMeta(null, "shiporder");
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Marshaller u = jc.createMarshaller();
		u.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		u.marshal(new JAXBElement(new QName("ElementMeta"),ElementMeta.class, e), new File("bin/shiporder2.meta.xml"));
	}
	
	/**
	 * test XSD parsing and marshaling of the generated Model Object
	 */
	@Test
	public void testParseXSD3() throws Exception {
		XSDParser p = new XSDParser();
		p.loadSchema("workingspace/shiporder3.xsd");
		ElementMeta e = p.getElementMeta(null, "shiporder");
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Marshaller u = jc.createMarshaller();
		u.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		u.marshal(new JAXBElement(new QName("ElementMeta"),ElementMeta.class, e), new File("bin/shiporder3.meta.xml"));
	}
	
	/**
	 * test HL7 v2 XSD parsing and marshaling of the generated Model Object
	 */
	@Ignore
	public void testParseHL7v2XSD() throws Exception {
		XSDParser p = new XSDParser();
		p.loadSchema(XSDParser.getResource("hl7v2xsd/2.5.1/ADT_A01.xsd").toString());
		ElementMeta e = p.getElementMeta("urn:hl7-org:v2xml", "ADT_A01");
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Marshaller u = jc.createMarshaller();
		u.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		u.marshal(new JAXBElement(new QName("ElementMeta"),ElementMeta.class, e), new File("bin/ADT_A01.meta.xml"));
	}
	
	/**
	 * test HL7 v3 XSD parsing and marshaling of the generated Model Object
	 */
	@Ignore
	public void testParseHL7v3XSD() throws Exception {
		XSDParser p = new XSDParser();
		p.loadSchema("workingspace/hl7v3/HL7v3Schema/COCT_MT010000UV01.xsd");
		ElementMeta e = p.getElementMetaFromComplexType("urn:hl7-org:v3", "COCT_MT010000UV01.Encounter");
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Marshaller u = jc.createMarshaller();
		u.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		u.marshal(new JAXBElement(new QName("ElementMeta"),ElementMeta.class, e), new File("bin/COCT_MT010000UV01.meta.xml"));
	}
	
	/**
	 * test create Mapping from a pair of XSD and marshaling the Mapping 
	 */
	@Test
	public void testMarshalMapping() throws Exception {
		String srcComponentId = "workingspace/shiporder.xsd";
		String tgtComponentId = "workingspace/printorder.xsd";
		Mapping m = MappingFactory.createMappingFromXSD(
				srcComponentId, "shiporder", tgtComponentId, "printorder");
		//add links;
		m.setLinks(new Mapping.Links());
		MappingFactory.addLink(m, "0", "/shiporder", "1", "/printorder");
		MappingFactory.addLink(m, "0", "/shiporder/shipto", "1", "/printorder/address");
		
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Marshaller u = jc.createMarshaller();
		u.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		u.marshal(new JAXBElement(new QName("mapping"),Mapping.class, m), new File("bin/mapping.out.xml"));
	}
	
	/**
	 * test create Mapping from a pair of XSD and marshaling the Mapping 
	 */
	@Test
	public void testMarshalMapping1() throws Exception {
		String srcComponentId = "workingspace/shiporder.xsd";
		String tgtComponentId = "workingspace/item.xsd";
		Mapping m = MappingFactory.createMappingFromXSD(
				srcComponentId, "shiporder", tgtComponentId, "item");
		//add links;
		m.setLinks(new Mapping.Links());
		
		MappingFactory.saveMapping(new File("bin/mapping1.out.xml"), m);
	}
	
	/**
	 * test round trip marshaling and unmarshaling of Mapping 
	 */
	@Test
	public void testUnmarshalMapping() throws Exception {
		Mapping m = MappingFactory.loadMapping(new File("workingspace/mapping.xml"));
		MappingFactory.saveMapping(new File("bin/mapping_roundtrip.out.xml"), m);
		//assertEquals(new File("workingspace/mapping.xml").length(), new File("bin/mapping1.out.xml").length());
	}
	
	/**
	 * test find node method 
	 */
	@Test
	public void testFindNodeById() throws Exception {
		Mapping m = MappingFactory.loadMapping(new File("workingspace/mapping.xml"));
		String cid = "1";
		String id = "/printorder/@orderid";
		BaseMeta b = MappingFactory.findNodeById(m, cid, id);
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Marshaller mar = jc.createMarshaller();
		mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		mar.marshal(new JAXBElement(new QName("meta"), b.getClass(), b), new File("bin/mapping_findObj.out.xml"));
	}
	
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.12  2008/12/09 19:04:17  linc
 * HISTORY: First GUI release
 * HISTORY:
 * HISTORY: Revision 1.11  2008/12/03 20:46:14  linc
 * HISTORY: UI update.
 * HISTORY:
 * HISTORY: Revision 1.10  2008/11/04 21:19:34  linc
 * HISTORY: core mapping and transform demo.
 * HISTORY:
 * HISTORY: Revision 1.9  2008/10/22 19:01:17  linc
 * HISTORY: Add comment of public methods.
 * HISTORY:
 * HISTORY: Revision 1.8  2008/10/22 15:45:39  linc
 * HISTORY: unit tested.
 * HISTORY:
 * HISTORY: Revision 1.7  2008/10/21 20:49:08  linc
 * HISTORY: Tested with HL7 v3 schema
 * HISTORY:
 * HISTORY: Revision 1.6  2008/10/21 15:59:57  linc
 * HISTORY: updated.
 * HISTORY:
 * HISTORY: Revision 1.5  2008/10/21 15:56:55  linc
 * HISTORY: updated
 * HISTORY:
 * HISTORY: Revision 1.4  2008/10/20 20:46:15  linc
 * HISTORY: updated.
 * HISTORY:
 * HISTORY: Revision 1.3  2008/10/08 18:54:42  linc
 * HISTORY: updated
 * HISTORY:
 * HISTORY: Revision 1.2  2008/10/01 18:59:14  linc
 * HISTORY: updated.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/09/30 17:30:41  linc
 * HISTORY: updated.
 * HISTORY:
 */

