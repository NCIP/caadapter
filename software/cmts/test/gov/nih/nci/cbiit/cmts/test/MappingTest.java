/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.test;

import gov.nih.nci.cbiit.cmts.common.XSDParser;
import gov.nih.nci.cbiit.cmts.core.AttributeMeta;
import gov.nih.nci.cbiit.cmts.core.BaseMeta;
import gov.nih.nci.cbiit.cmts.core.Component;
import gov.nih.nci.cbiit.cmts.core.ComponentType;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.mapping.MappingFactory;
import gov.nih.nci.cbiit.cmts.util.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This class is to test the Mapping functions
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.15 $
 * @date       $Date: 2009-10-15 18:33:55 $
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
		p.loadSchema("workingspace/simpleMapping/shiporder.xsd");
		ElementMeta e = p.getElementMeta(null, "shiporder");
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmts.core" );
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
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmts.core" );
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
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmts.core" );
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
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmts.core" );
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
		p.loadSchema(FileUtil.getResource("hl7v2xsd/2.5.1/ADT_A01.xsd").toString());
		ElementMeta e = p.getElementMeta("urn:hl7-org:v2xml", "ADT_A01");
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmts.core" );
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
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmts.core" );
		Marshaller u = jc.createMarshaller();
		u.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		u.marshal(new JAXBElement(new QName("ElementMeta"),ElementMeta.class, e), new File("bin/COCT_MT010000UV01.meta.xml"));
	}
	
	/**
	 * test create Mapping from a pair of XSD and marshaling the Mapping 
	 */
	@Test
	public void testMarshalMapping() throws Exception {
		String srcComponentId = "workingspace/simpleMapping/shiporder.xsd";
		String tgtComponentId = "workingspace/simpleMapping/printorder.xsd";
		Mapping m = new Mapping();
		m.setComponents(new Mapping.Components());
		m.setLinks(new Mapping.Links());
		XSDParser srcP = new XSDParser();
		srcP.loadSchema(srcComponentId);
		MappingFactory.loadMetaXSD(m, srcP, null,"shiporder", ComponentType.SOURCE);
		
		XSDParser trgtP = new XSDParser();
		trgtP.loadSchema(tgtComponentId);
		MappingFactory.loadMetaXSD(m, trgtP,null, "printorder",  ComponentType.TARGET);
		
		//add links;
		m.setLinks(new Mapping.Links());
		MappingFactory.addLink(m, "0", "/shiporder", "1", "/printorder");
		MappingFactory.addLink(m, "0", "/shiporder/shipto", "1", "/printorder/address");
		
		MappingFactory.saveMapping(new File("bin/mapping.out.xml"), m);
//		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmts.core" );
//		Marshaller u = jc.createMarshaller();
//		u.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
//		u.marshal(new JAXBElement(new QName("mapping"),Mapping.class, m), new File("bin/mapping.out.xml"));
	}
	
	
	/**
	 * test round trip marshaling and unmarshaling of Mapping 
	 */
	@Test
	public void testUnmarshalMapping() throws Exception {
		Mapping m = MappingFactory.loadMapping(new File("workingspace/simpleMapping/mapping.xml"));
		MappingFactory.saveMapping(new File("bin/mapping_roundtrip.out.xml"), m);
	}
	
	/**
	 * test find node method 
	 */
	@Test
	public void testFindNodeById() throws Exception {
		Mapping m = MappingFactory.loadMapping(new File("workingspace/mapping.xml"));
		String cid = "1";
		String id = "/printorder/@orderid";

		HashMap<String,Component> map = new HashMap<String,Component>();
		List<Component> l = m.getComponents().getComponent();
		for(Component c:l)
			map.put(c.getId(), c);
		Component c = map.get(cid);
		BaseMeta b = findNodeById(c, id);
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmts.core" );
		Marshaller mar = jc.createMarshaller();
		mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		mar.marshal(new JAXBElement(new QName("meta"), b.getClass(), b), new File("bin/mapping_findObj.out.xml"));
	}

	private BaseMeta findNodeById(Component c, String id){
	StringTokenizer st = new StringTokenizer(id, "/@");
	boolean foundRoot = false;
	ElementMeta e = c.getRootElement();
	while(st.hasMoreTokens()){
		String tmp = st.nextToken();
		if(!foundRoot){
			if(e.getName().equals(tmp)){
				foundRoot = true;
				continue;
			}else
				continue;
		}else{
			List<ElementMeta> childs = e.getChildElement();
			ElementMeta found = null;
			AttributeMeta foundAttr = null;
			for(ElementMeta i:childs){
				if(i.getName().equals(tmp)){
					found = i;
					break;
				}
			}
			if(found == null && st.hasMoreTokens()){
				return null;
			}else if(found == null && !st.hasMoreTokens()){
				List<AttributeMeta> attrs = e.getAttrData();
				for(AttributeMeta i:attrs){
					if(i.getName().equals(tmp)){
						foundAttr = i;
						break;
					}
				}
				return foundAttr;
			}else{
				e = found;
			}
		}
	}
	return e;
}
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.14  2008/12/29 22:18:18  linc
 * HISTORY: function UI added.
 * HISTORY:
 * HISTORY: Revision 1.13  2008/12/10 15:43:03  linc
 * HISTORY: Fixed component id generator and delete link.
 * HISTORY:
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

