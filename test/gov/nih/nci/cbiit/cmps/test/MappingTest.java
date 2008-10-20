/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmps.test;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.*;

import gov.nih.nci.cbiit.cmps.core.*;
import gov.nih.nci.cbiit.cmps.common.*;

/**
 * This class 
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.4 $
 * @date       $Date: 2008-10-20 20:46:15 $
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
		p.loadSchema(p.getResource("hl7v2xsd/2.5.1/ADT_A01.xsd").toString());
		ElementMeta e = p.getElementMeta("urn:hl7-org:v2xml", "ADT_A01");
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Marshaller u = jc.createMarshaller();
		u.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		u.marshal(new JAXBElement(new QName("mapping"),ElementMeta.class, e), new File("bin/adt.out.xml"));
	}
	
	@Test
	public void testMarshalMapping() throws Exception {
		Mapping m = new Mapping();
		m.setComponents(new Mapping.Components());
		//add source xsd
		Component src = new Component();
		src.setKind(KindType.XML);
		src.setId("file://"+"etc/data/shiporder3.xsd");
		src.setLocation("etc/data/shiporder3.xsd");
		XSDParser p = new XSDParser();
		p.loadSchema("etc/data/shiporder3.xsd");
		ElementMeta e = p.getElementMeta(null, "shiporder");
		src.setRootElement(e);
		src.setType(ComponentType.SOURCE);
		m.getComponents().getComponent().add(src);
		//add destination xsd
		Component dst = new Component();
		dst.setKind(KindType.XML);
		dst.setId("file://"+"etc/data/printorder.xsd");
		dst.setLocation("etc/data/printorder.xsd");
		XSDParser p1 = new XSDParser();
		p1.loadSchema("etc/data/printorder.xsd");
		ElementMeta e1 = p1.getElementMeta(null, "printorder");
		dst.setRootElement(e1);
		dst.setType(ComponentType.TARGET);
		m.getComponents().getComponent().add(dst);
		//add links;
		m.setLinks(new Mapping.Links());
		LinkType l = new LinkType();
		LinkpointType lp = new LinkpointType();
		lp.setComponentid(src.getId());
		lp.setId("/shiporder");
		l.setSource(lp);
		lp = new LinkpointType();
		lp.setComponentid(dst.getId());
		lp.setId("/printorder");
		l.setTarget(lp);
		m.getLinks().getLink().add(l);
		
		l = new LinkType();
		lp = new LinkpointType();
		lp.setComponentid(src.getId());
		lp.setId("/shiporder/shipto");
		l.setSource(lp);
		lp = new LinkpointType();
		lp.setComponentid(dst.getId());
		lp.setId("/printorder/address");
		l.setTarget(lp);
		m.getLinks().getLink().add(l);
				
		l = new LinkType();
		lp = new LinkpointType();
		lp.setComponentid(src.getId());
		lp.setId("@address");
		l.setSource(lp);
		lp = new LinkpointType();
		lp.setComponentid(dst.getId());
		lp.setId("/printorder/address@street");
		l.setTarget(lp);
		m.getLinks().getLink().add(l);
				
		
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

