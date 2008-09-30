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
import javax.xml.bind.*;
import javax.xml.namespace.QName;

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
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-09-30 17:30:41 $
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
		u.marshal(new JAXBElement(new QName("mapping"),ElementMeta.class, e), new File("etc/data/shiporder1.out.xml"));
	}
	
	@Test
	public void testParseXSD2() throws Exception {
		XSDParser p = new XSDParser();
		p.loadSchema("etc/data/shiporder2.xsd");
		ElementMeta e = p.getElementMeta(null, "shiporder");
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Marshaller u = jc.createMarshaller();
		u.marshal(new JAXBElement(new QName("mapping"),ElementMeta.class, e), new File("etc/data/shiporder2.out.xml"));
	}
	
	@Test
	public void testParseXSD3() throws Exception {
		XSDParser p = new XSDParser();
		p.loadSchema("etc/data/shiporder3.xsd");
		ElementMeta e = p.getElementMeta(null, "shiporder");
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Marshaller u = jc.createMarshaller();
		u.marshal(new JAXBElement(new QName("mapping"),ElementMeta.class, e), new File("etc/data/shiporder3.out.xml"));
	}
	
	@Test
	public void testUnmarshalMapping() throws Exception {
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Unmarshaller u = jc.createUnmarshaller();
		u.unmarshal(new File("mapping.xml"));
	}
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 */

