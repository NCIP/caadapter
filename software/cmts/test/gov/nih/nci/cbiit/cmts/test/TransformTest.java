/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.test;

import gov.nih.nci.cbiit.cmts.core.Component;
import gov.nih.nci.cbiit.cmts.core.ComponentType;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.mapping.MappingFactory;
import gov.nih.nci.cbiit.cmts.transform.TransformationUtil;
import gov.nih.nci.cbiit.cmts.transform.XQueryBuilder;
import gov.nih.nci.cbiit.cmts.transform.MappingTransformer;
import gov.nih.nci.cbiit.cmts.transform.XQueryTransformer;
import gov.nih.nci.cbiit.cmts.transform.artifact.RDFEncoder;
import gov.nih.nci.cbiit.cmts.transform.validation.XsdSchemaErrorHandler;
import gov.nih.nci.cbiit.cmts.transform.validation.XsdSchemaSaxValidator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQItemType;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;

import net.sf.saxon.Configuration;
import net.sf.saxon.xqj.SaxonXQDataSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.ErrorHandler;

/**
 * This class 
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
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
	public void testReadRemoteFile() throws IOException
	{
//		String fileURI="//nci6116g.nci.nih.gov/group/NCICB/caAdapter/Temp/";
		String urlValue="http://localhost:30210/caadapter-cmts/index.html";
//		String urlValue="file://nci6116g.nci.nih.gov/group/NCICB/caAdapter/Temp/test.xml";
		URL url = new URL(urlValue); 
//		File f = new File("//10.22.33.122/images");
//		FileReader reader=new FileReader(f);
		InputStream in=url.openStream();
		InputStreamReader sReader=new InputStreamReader(in); 

		LineNumberReader lReader=new LineNumberReader(sReader);
		String line=lReader.readLine();
		while (line!=null)
		{
			System.out.println("TransformTest.testReadRemoteFile()"+line);
			line=lReader.readLine();
		}
		


	}
	
	/*
	 * Test mapping and transformation
	 */
	@Test
	public void testMappingAndTransformation() throws JAXBException, XQException
	{
		String mappingFile="workingspace/RDF/mapping.map";
		String srcFile = "workingspace/RDF/source.xml";
		Mapping map = MappingFactory.loadMapping(new File(mappingFile));
		XQueryBuilder builder = new XQueryBuilder(map);
		String queryString = builder.getXQuery();
		System.out.println("$$$$$$ query: \n"+queryString);
		MappingTransformer tester= new MappingTransformer();

		String xmlResult=tester.transfer(srcFile, mappingFile);
		System.out.println("TransformTest.testMappingAndTransformation()\n"+xmlResult);
		
	}
	/**
	 * Test method for {@link gov.nih.nci.cbiit.cmts.transform.XQueryBuilder#getXQuery()}.
	 * @throws XQException 
	 */
	@Test
	public void testXQueryTransform() throws XQException {
//		String sourceFile="workingspace/simpleMapping/shiporder.xml";
//		String xqFile="workingspace/simpleMapping/testXQ1.xq";
		
		String sourceFile="synderTest.xml";
		String xqFile="synderQuery.xq";
		InputStream in;
		try {
			in = new FileInputStream(new File(xqFile));
			InputStreamReader inputStream=new InputStreamReader(in);
			Configuration saxonConfig = new Configuration();
			SaxonXQDataSource dataSource = new SaxonXQDataSource(saxonConfig);
			XQConnection conn = dataSource.getConnection();
			XQPreparedExpression exp = conn.prepareExpression(inputStream);
			URI sourcUri=new File(sourceFile).toURI();
			exp.bindString(new QName("docName"), sourcUri.getPath(), conn
					.createAtomicType(XQItemType.XQBASETYPE_STRING));
			XQResultSequence result = exp.executeQuery();
			String rawResult = result.getSequenceAsString(new Properties());
			System.out.println("TransformTest.testXQueryExpression()..:\n"+rawResult);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		XQueryTransformer tester= new XQueryTransformer();
//		System.out.println(tester.transfer(sourceFile, xqFile));
	}

	/**
	 * 
	 * @throws XQException 
	 * @throws XQException
	 * @throws JAXBException
	 * @throws IOException
	 */
	@Test
	public void testXQueryExpression() throws XQException
	{
		String sourceFile="synderTest.xml";
		String xqFile="synderQuery1.xq";
		Configuration saxonConfig = new Configuration();
		SaxonXQDataSource dataSource = new SaxonXQDataSource(saxonConfig);
		XQConnection conn = dataSource.getConnection();
		String xsString="for $Template in doc(\"synderTest.xml\")/TemplateContainer/Template/Tags "+
			" return $Template" ;
		

		XQPreparedExpression exp = conn.prepareExpression(xsString);

		URI sourcUri=new File(sourceFile).toURI();
//		exp.bindString(new QName("docName"), sourcUri.getPath(), conn
//				.createAtomicType(XQItemType.XQBASETYPE_STRING));
		XQResultSequence result = exp.executeQuery();
		String rawResult = result.getSequenceAsString(new Properties());

		System.out.println("TransformTest.testXQueryExpression()..:"+rawResult);
//		System.out.println(tester.transfer(sourceFile, xqFile));
	}
	
	@Test
	public void testTransformAndOutput() throws XQException, JAXBException, IOException {
		String mapFile="workingspace/hl7v2/dischargeMap.map";
		Mapping map = MappingFactory.loadMapping(new File(mapFile));
		XQueryBuilder builder = new XQueryBuilder(map);
		String queryString = builder.getXQuery();
		System.out.println("$$$$$$ query: \n"+queryString);
		FileWriter w = new FileWriter("bin/tranform.xq");
		w.write(queryString);
		w.close();
		
		MappingTransformer tester= new MappingTransformer();
		String dataSource="workingspace/hl7v2/ADT_03.xml";
 		System.out.println("TransformTest.testCMTSTransform()..:\n"+TransformationUtil.formatXqueryResult(tester.transfer(dataSource, mapFile), false));
		
		w = new FileWriter("bin/tranform.out.xml");
		String result=tester.transfer(dataSource, mapFile);
		w.write(result);
		w.close();
		
		//using validator
		String targetSchema=null;
		Mapping maping=tester.getTransformationMapping();
		for (Component mapComp:maping.getComponents().getComponent())
		{
			if (mapComp.getRootElement()!=null
					&&mapComp.getType().equals(ComponentType.TARGET))
			{
				targetSchema=mapComp.getLocation();
			}
		}
		ErrorHandler handler=new XsdSchemaErrorHandler();
		
		Schema schema=XsdSchemaSaxValidator.loadSchema(targetSchema, handler);
		XsdSchemaSaxValidator.validateXmlData(schema, result, handler);
	}

	@Test
	public void testXQueryBuilder() throws XQException, JAXBException, IOException {
		String mappingFile="workingspace/cda/mapping.map";
		Mapping map=MappingFactory.loadMapping(new File(mappingFile));
		XQueryBuilder builder = new XQueryBuilder(map);
		String queryString = builder.getXQuery();
		System.out.println("$$$$$$ query: \n"+queryString);
		FileWriter w = new FileWriter("bin/tranform2.xq");
		w.write(queryString);
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

