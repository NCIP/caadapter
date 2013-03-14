/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.test;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.csv.CSVDataResult;
import gov.nih.nci.caadapter.common.csv.CSVMetaParserImpl;
import gov.nih.nci.caadapter.common.csv.CSVMetaResult;
import gov.nih.nci.caadapter.common.csv.CsvReader;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.cbiit.cmts.common.XSDParser;
import gov.nih.nci.cbiit.cmts.core.Component;
import gov.nih.nci.cbiit.cmts.core.ComponentType;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.mapping.MappingFactory;
import gov.nih.nci.cbiit.cmts.transform.TransformationUtil;
import gov.nih.nci.cbiit.cmts.transform.XQueryBuilder;
import gov.nih.nci.cbiit.cmts.transform.MappingTransformer;
import gov.nih.nci.cbiit.cmts.transform.csv.CsvData2XmlConverter;
import gov.nih.nci.cbiit.cmts.transform.csv.CsvXsd2MetadataConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.xquery.XQException;

import junit.framework.Assert;
import org.junit.Test;

public class CsvTest {

	/**
	 * Test transform CSV to target XML
	 * @throws JAXBException 
	 * @throws JAXBException
	 * @throws XQException 
	 * @throws XQException
	 * @throws IOException 
	 * @throws ApplicationException 
	 */
	@Test
	public void testTransformCsv2Xml() throws JAXBException, XQException, IOException, ApplicationException
	{
		String mappingFile="workingspace/Csv2Xml/mapping.xml";
 		Mapping map = MappingFactory.loadMapping(new File(mappingFile));
		XQueryBuilder builder = new XQueryBuilder(map);
		String queryString = builder.getXQuery();
		System.out.println("$$$$$$ query: \n"+queryString);

		//parse csv data 
		String xsdFile="";
    	String xsdRoot="";
		for (Component comp:map.getComponents().getComponent())
		{
			if (comp.getType().equals(ComponentType.SOURCE))
			{
				xsdFile=comp.getLocation();
				xsdRoot=comp.getRootElement().getName();
			}	
		}
    	
    	CSVMeta csvXsdMeta=loadCsvMetaFromXSD(xsdFile, xsdRoot);
		String csvFile="workingspace/csv2Xml/COCT_MT010000_Simple.csv";
		//parse CSV stream
		String tempXmlSrc=null;
    	CsvReader reader = readCsvData(csvFile, csvXsdMeta);
		while(reader.hasMoreRecord())
		{
			CSVDataResult nextResult=reader.getNextRecord();
			CsvData2XmlConverter xmlConverter=new CsvData2XmlConverter(nextResult);
					
			System.out.println(xmlConverter.getXmlString() );
			tempXmlSrc=xmlConverter.writeXml2File(null);
			Assert.assertNotNull(nextResult);
		}
		MappingTransformer tester= new MappingTransformer();
//		String xmlResult=tester.executeQuery(queryString, tempXmlSrc);
//		System.out.println("TransformTest.testMappingAndTransformation()\n"+TransformationUtil.formatXqueryResult(xmlResult, false));
		File tmpFile=new File(tempXmlSrc);
		tmpFile.delete();
	}
	/**
	 * test converter
	 */
	@Test
	public void testElementMetaToCvsMetaConverter()
	{
		String xsdFile="workingspace/csv2xml/COCT_MT010000_Simple.xsd";
		String rootElement="ENCOUNTER_HEAD";
		CSVMeta csvMeta=loadCsvMetaFromXSD(xsdFile, rootElement);
		Assert.assertNotNull(csvMeta.getRootSegment());
	}
	
	private CSVMeta loadCsvMetaFromXSD(String xsdFile, String rootName)
	{
		XSDParser p = new XSDParser();
		p.loadSchema(xsdFile, null);
		ElementMeta e = p.getElementMeta(null, rootName);
		CsvXsd2MetadataConverter converter=new CsvXsd2MetadataConverter(e);
		return converter.getCSVMeta();	
	}
	
	/**
	 * test XSD parsing CSV meta
	 */
	@Test
	public void testParseCsvMeta() throws Exception {
		String scsFileName="workingspace/csv2Xml/COCT_MT010000_Simple.scs";
    	CSVMeta csvMeta = loadCsvMetaFromSCS(scsFileName);
    	Assert.assertNotNull(csvMeta.getRootSegment());
	}
	
	private CSVMeta loadCsvMetaFromSCS(String scsFile) throws FileNotFoundException
	{
		CSVMetaParserImpl parser = new CSVMetaParserImpl();
    	CSVMetaResult csvMetaResult = parser.parse(new FileReader(scsFile));
    	CSVMeta csvMeta = csvMetaResult.getCsvMeta();
    	return csvMeta;
	}
	/**
	 * test parsing CSV data
	 * @throws IOException 
	 * @throws ApplicationException 
	 */
	@Test
	public void testParseCsvData() throws IOException, ApplicationException
	{
//		String scsFile="workingspace/csv2Xml/COCT_MT010000_Simple.scs";
//    	CSVMeta csvScsMeta = loadCsvMetaFromSCS(scsFile);
    	String xsdFile="workingspace/csv2Xml/COCT_MT010000_Simple.xsd";
    	String xsdRoot="ENCOUNTER_HEAD";
    	CSVMeta csvXsdMeta=loadCsvMetaFromXSD(xsdFile, xsdRoot);
		String csvFile="workingspace/csv2Xml/COCT_MT010000_Simple.csv";
		
		//parse CSV stream
    	CsvReader reader = readCsvData(csvFile, csvXsdMeta);
		while(reader.hasMoreRecord())
		{
			CSVDataResult nextResult=reader.getNextRecord();
			CsvData2XmlConverter xmlConverter=new CsvData2XmlConverter(nextResult);
					
			System.out.println(xmlConverter.getXmlString() );
			Assert.assertNotNull(nextResult);
		}
	}
	
	private CsvReader readCsvData(String csvFile, CSVMeta csvMeta ) throws IOException
	{
		InputStream  sourceDataStream = new FileInputStream(csvFile);
		CsvReader reader = new CsvReader(sourceDataStream, csvMeta);
		
		return reader;
	}
}
