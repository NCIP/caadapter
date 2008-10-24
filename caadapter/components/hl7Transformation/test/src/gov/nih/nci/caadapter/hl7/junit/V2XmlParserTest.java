/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.junit;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import gov.nih.nci.caadapter.common.MetaException;
import gov.nih.nci.caadapter.common.csv.CSVDataResult;
import gov.nih.nci.caadapter.common.csv.CSVMetaBuilder;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.csv.data.impl.CSVSegmentImpl;
import gov.nih.nci.caadapter.common.csv.data.impl.CSVSegmentedFileImpl;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.hl7.v2meta.HL7V2XmlSaxContentHandler;
import gov.nih.nci.caadapter.hl7.v2meta.V2MetaXSDUtil;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;

import org.xml.sax.SAXException;

import com.sun.encoder.Encoder;
import com.sun.encoder.EncoderConfigurationException;
import com.sun.encoder.EncoderException;
import com.sun.encoder.EncoderFactory;
import com.sun.encoder.EncoderType;
import com.sun.encoder.MetaRef;

import junit.framework.TestCase;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Oct 20, 2008
 * @author   LAST UPDATE: $Author: wangeug 
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2008-10-24 19:38:05 $
 * @since caAdapter v4.2
 */

public class V2XmlParserTest extends TestCase {

	public void testParseV2Stream()
	{
   			
		try {
			//Get the encoder factory instance
			EncoderFactory factory = EncoderFactory.newInstance();

			//Get the encoder type instance using an encoding style
			EncoderType type = factory.makeType("hl7encoder-1.0");

			//Specify a top element
			QName topElem = new QName("urn:hl7-org:v2xml", "ADT_A01");

			//Construct the metadata instance
			MetaRef meta = factory.makeMeta(this.getClass().getClassLoader().getResource("hl7v2xsd/2.5/ADT_A01.xsd"),topElem);
					//.getResource("C:/eclipseJ2ee/workspace/HL7V2Decoder/hl7v2xsd/2.5/ADT_A03.xsd"), topElem);

			//Create the encoder instance, HL7Encoder
			Encoder coder = factory.newEncoder(type, meta);
			//Decode the data
			javax.xml.transform.Source source = coder.decodeFromStream(new FileInputStream(new File("data/ADT_A01.hl7")));
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer serializer = tf.newTransformer();
			
			//forward transformed XML to next step
			HL7V2XmlSaxContentHandler saxHandler= new HL7V2XmlSaxContentHandler();
			SAXResult saxResult=new SAXResult(saxHandler); 
			serializer.transform(source, saxResult);
			CSVDataResult parsedData= saxHandler.getDataResult();
			System.out.println("V2XmlParserTest.testParseV2Stream().."+parsedData);
			CSVMetaBuilder builder = CSVMetaBuilder.getInstance();
//			fw = new FileOutputStream(file);
//			bw = new BufferedOutputStream(fw);
//			builder.build(bw, existMeta);
			File metaOut=new File("metaOut.scs");
			CSVSegment rootSeg=(CSVSegment)parsedData.getCsvSegmentedFile().getLogicalRecords().get(0);
			CSVMeta v2CsvMeta=V2MetaXSDUtil.createDefaultCsvMeta("ADT_A01");
//			CSVMeta v2CsvMeta= (CSVMeta)((CSVSegmentImpl)rootSeg).getMetaObject();
//			CSVSegmentedFileImpl segFile=(CSVSegmentedFileImpl)parsedData.getCsvSegmentedFile();
			v2CsvMeta.setRootSegment((CSVSegmentMeta)rootSeg.getMetaObject());
			try {
				builder.build(metaOut, v2CsvMeta);
			} catch (MetaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (EncoderConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EncoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
	}
}


/**
* HISTORY: $Log: not supported by cvs2svn $
**/