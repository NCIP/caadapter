/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.test;

import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.mapping.MappingFactory;
import gov.nih.nci.cbiit.cmts.transform.XQueryBuilder;
import gov.nih.nci.cbiit.cmts.transform.XQueryTransformer;
import gov.nih.nci.cbiit.cmts.util.FileUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xquery.XQException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class 
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMPS v1.0
 * @version    $Revision: 1.8 $
 * @date       $Date: 2009-12-01 16:45:45 $
 *
 */
public class TransformTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

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

	/*
	 * 
	 */
	@Test
	public void testMappingAndTransformation() throws JAXBException, XQException
	{
//		String mappingFile="workingspace/hl7v3/examples/testMapping.xml";//PORT_MT_TO_IN020001mapping.xml";
//		String mappingFile="workingspace/hl7v3/examples/PORT_MT_TO_IN020001mapping.xml";
//		String srcFile = "workingspace/hl7v3/examples/PORT_EX020001UV.xml";  
//		String mappingFile="workingspace/simpleMapping/mapping.xml";
//		String srcFile = "workingspace/simpleMapping/shiporder.xml";
//		String mappingFile="workingspace/siblingMapping/mapping.xml";
//		String srcFile = "workingspace/siblingMapping/shiporder.xml";
//		String mappingFile="workingspace/parentChildInverted/mapping.xml";
//		String srcFile = "workingspace/parentChildInverted/order.xml";
		String mappingFile="workingspace/ISO_21090/example/mapping.xml";
		String srcFile = "workingspace/ISO_21090/example/purchase.xml";
		Mapping map = MappingFactory.loadMapping(new File(mappingFile));
		XQueryBuilder builder = new XQueryBuilder(map);
		String queryString = builder.getXQuery();
		System.out.println("$$$$$$ query: \n"+queryString);
		XQueryTransformer tester= new XQueryTransformer();

		tester.setFilename(srcFile);
		tester.setQuery(queryString);
		String xmlResult=tester.executeQuery();
		System.out.println("TransformTest.testMappingAndTransformation()\n"+xmlResult);
	}
	/**
	 * Test method for {@link gov.nih.nci.cbiit.cmts.transform.XQueryBuilder#getXQuery()}.
	 * @throws XQException 
	 */
	@Test
	public void testXQueryTransform() throws XQException {
		final String sep = System.getProperty("line.separator");
		String queryString =
			"declare variable $docName as xs:string external;" + sep +
			"for $item_temp0 in doc($docName)/PORT_MT020001UV01.AnnotatedECG return element result {element item {for $item_temp1 in $item_temp0/id return attribute id {data($item_temp1/@root)}, attribute myaattr {\"testing\"}}"+
			" }";

//			"<result>" +
//			"{" +
//			"      for $item in doc($docName)//item " +
//			"    where $item/price > 10 " +
//			"      and $item/quantity > 0 " +
//			" order by $item/title " +
//			"   return " +
//			"<shipitem><name>{$item/title/text()}</name>" +
//			" <buyer>{data($item/../@orderid)}</buyer>" +
//			" {for $subItem in $item/../@orderid return <buyer1>{data($subItem)}</buyer1>}" +
//			" <quantity>{$item/quantity/text()}</quantity></shipitem>" +
//			"}</result>";
		

		
		System.out.println("TransformTest.testXQueryTransform()\n"+queryString);
		XQueryTransformer tester= new XQueryTransformer();
		String srcFile="workingspace/hl7v3/examples/PORT_EX020001UV.xml";
//		String srcFile = "workingspace/simpleMapping/shiporder.xml";//purchase.xml";
//		String srcFile = "workingspace/ISO_21090/example/purchase.xml";
		tester.setFilename(srcFile);
		tester.setQuery(queryString);
		System.out.println(tester.executeQuery());
	}

	@Test
	public void testCMPSTransform() throws XQException, JAXBException, IOException {
		String mapFile="workingspace/simpleMapping/mapping1.xml";
		Mapping map = MappingFactory.loadMapping(new File(mapFile));
		XQueryBuilder builder = new XQueryBuilder(map);
		String queryString = builder.getXQuery();
		//System.out.println("$$$$$$ query: \n"+queryString);
		FileWriter w = new FileWriter("bin/tranform.xq");
		w.write(queryString);
		w.close();
		
		XQueryTransformer tester= new XQueryTransformer();
		tester.setFilename("workingspace/simpleMapping/shiporder.xml");
		tester.setQuery(queryString);
		System.out.println("TransformTest.testCMPSTransform()..:\n"+tester.executeQuery());
//		
//		w = new FileWriter("bin/tranform.out.xml");
//		w.write(tester.executeQuery());
//		w.close();
	}

	@Test
	public void testCMPSTransform1() throws XQException, JAXBException, IOException {
		Mapping map = MappingFactory.loadMapping(new File("workingspace/mapping1.xml"));
		XQueryBuilder builder = new XQueryBuilder(map);
		String queryString = builder.getXQuery();
		//System.out.println("$$$$$$ query: \n"+queryString);
		FileWriter w = new FileWriter("bin/tranform1.xq");
		w.write(queryString);
		w.close();
		
		XQueryTransformer tester= new XQueryTransformer();
		tester.setFilename("workingspace/shiporder.xml");
		tester.setQuery(queryString);
		w = new FileWriter("bin/tranform1.out.xml");
		w.write(tester.executeQuery());
		w.close();
	}

	@Test
	public void testCMPSTransform2() throws XQException, JAXBException, IOException {
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmts.core" );
		Unmarshaller u = jc.createUnmarshaller();
		JAXBElement<Mapping> m = u.unmarshal(new StreamSource(new File("workingspace/mapping2.xml")), Mapping.class);
		Mapping map = m.getValue();
		XQueryBuilder builder = new XQueryBuilder(map);
		String queryString = builder.getXQuery();
		//System.out.println("$$$$$$ query: \n"+queryString);
		FileWriter w = new FileWriter("bin/tranform2.xq");
		w.write(queryString);
		w.close();
		
		XQueryTransformer tester= new XQueryTransformer();
		tester.setFilename("workingspace/shiporder.xml");
		tester.setQuery(queryString);
		w = new FileWriter("bin/tranform2.out.xml");
		w.write(tester.executeQuery());
		w.close();
	}

}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.7  2009/11/24 18:30:14  wangeug
 * HISTORY: add hl7 v3 example
 * HISTORY:
 * HISTORY: Revision 1.6  2008/12/10 15:43:03  linc
 * HISTORY: Fixed component id generator and delete link.
 * HISTORY:
 * HISTORY: Revision 1.5  2008/12/09 19:04:17  linc
 * HISTORY: First GUI release
 * HISTORY:
 * HISTORY: Revision 1.4  2008/11/04 21:19:34  linc
 * HISTORY: core mapping and transform demo.
 * HISTORY:
 * HISTORY: Revision 1.3  2008/10/21 15:59:57  linc
 * HISTORY: updated.
 * HISTORY:
 * HISTORY: Revision 1.2  2008/10/20 20:46:15  linc
 * HISTORY: updated.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/10/01 18:59:14  linc
 * HISTORY: updated.
 * HISTORY:
 */

