/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmps.test;


import gov.nih.nci.cbiit.cmps.common.XSDParser;
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
import org.junit.Test;

/**
 * This class 
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.7 $
 * @date       $Date: 2008-10-21 20:49:08 $
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
	
	@Test
	public void testParseXSD1() throws Exception {
		XSDParser p = new XSDParser();
		p.loadSchema("etc/data/shiporder1.xsd");
		ElementMeta e = p.getElementMeta(null, "shiporder");
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Marshaller u = jc.createMarshaller();
		u.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		u.marshal(new JAXBElement(new QName("mapping"),ElementMeta.class, e), new File("bin/shiporder1.out.xml"));
	}
	
	@Test
	public void testParseXSD2() throws Exception {
		XSDParser p = new XSDParser();
		p.loadSchema("etc/data/shiporder2.xsd");
		ElementMeta e = p.getElementMeta(null, "shiporder");
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Marshaller u = jc.createMarshaller();
		u.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		u.marshal(new JAXBElement(new QName("mapping"),ElementMeta.class, e), new File("bin/shiporder2.out.xml"));
	}
	
	@Test
	public void testParseHL7v2XSD() throws Exception {
		XSDParser p = new XSDParser();
		p.loadSchema(XSDParser.getResource("hl7v2xsd/2.5.1/ADT_A01.xsd").toString());
		ElementMeta e = p.getElementMeta("urn:hl7-org:v2xml", "ADT_A01");
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Marshaller u = jc.createMarshaller();
		u.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		u.marshal(new JAXBElement(new QName("mapping"),ElementMeta.class, e), new File("bin/adt.out.xml"));
	}
	
	@Test
	public void testParseHL7v3XSD() throws Exception {
		XSDParser p = new XSDParser();
		p.loadSchema("etc/data/hl7v3/HL7v3Schema/COCT_MT010000UV01.xsd");
		ElementMeta e = p.getElementMetaFromComplexType("urn:hl7-org:v3", "COCT_MT010000UV01.Encounter");
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Marshaller u = jc.createMarshaller();
		u.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		u.marshal(new JAXBElement(new QName("mapping"),ElementMeta.class, e), new File("bin/COCT_MT010000UV01.out.xml"));
	}
	
	@Test
	public void testMarshalMapping() throws Exception {
		String srcComponentId = "etc/data/shiporder3.xsd";
		String tgtComponentId = "etc/data/printorder.xsd";
		Mapping m = MappingFactory.createMappingFromXSD(
				srcComponentId, "shiporder", tgtComponentId, "printorder");
		//add links;
		m.setLinks(new Mapping.Links());
		MappingFactory.addLink(m, srcComponentId, "/shiporder", tgtComponentId, "/printorder");
		MappingFactory.addLink(m, srcComponentId, "/shiporder/shipto", tgtComponentId, "/printorder/address");
		
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Marshaller u = jc.createMarshaller();
		u.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		u.marshal(new JAXBElement(new QName("mapping"),Mapping.class, m), new File("bin/mapping.out.xml"));
	}
	
	@Test
	public void testUnmarshalMapping() throws Exception {
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Unmarshaller u = jc.createUnmarshaller();
		JAXBElement<Mapping> m = u.unmarshal(new StreamSource(new File("etc/data/mapping.xml")), Mapping.class);
		Marshaller mar = jc.createMarshaller();
		mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		mar.marshal(m, new File("bin/mapping1.out.xml"));
		//assertEquals(new File("etc/data/mapping.xml").length(), new File("bin/mapping1.out.xml").length());
	}
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
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

