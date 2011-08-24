package gov.nih.nci.cbiit.cmts.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xquery.XQException;

import junit.framework.Assert;
import gov.nih.nci.cbiit.cmts.transform.MappingTransformer;
import gov.nih.nci.cbiit.cmts.transform.hl7v2.Hl7V2MessageEncoderFactory;
import gov.nih.nci.cbiit.cmts.transform.hl7v2.Hl7v2XmlTransformer;
import gov.nih.nci.cbiit.cmts.transform.hl7v2.V2MessageLinefeedEncoder;

import com.sun.encoder.Encoder;
import com.sun.encoder.EncoderException;
 

import org.junit.Test;

public class Hl7V2Test {
	
	@Test 
	public void testMapAndTransformation() throws XQException
	{
		String mapping="workingspace/Hl7v2ToXml/mapping.xml";
		String srcV2Msg="workingspace/Hl7v2ToXml/ADT_A03.hl7";
		Hl7v2XmlTransformer transformer= new Hl7v2XmlTransformer();
		String result=transformer.transfer(srcV2Msg, mapping);
		System.out.println("Hl7V2Test.testMapAndTransformation()..\n"+result);
		
	}
	@Test public void testLoaderV2Encoder()
	{
		String schemaUri="workingspace/Hl7v2ToXml/hl7v2xsd/2.4/ADT_A03.xsd";
		Encoder v2Encoder=Hl7V2MessageEncoderFactory.getV2MessageEncoderWithSchema(schemaUri, "ADT_A03");
		
	}

	@Test
	public void testTransformation() throws IOException, EncoderException, TransformerFactoryConfigurationError, TransformerException, XQException
	{
		String schemaUri="workingspace/Hl7v2ToXml/hl7v2xsd/2.4/ADT_A03.xsd";
		Encoder v2Encoder=Hl7V2MessageEncoderFactory.getV2MessageEncoderWithSchema(schemaUri, "ADT_A03");
		String v2MessageFile="workingspace/Hl7v2ToXml/ADT_A03.hl7";
		FileInputStream sourceDataStream = new FileInputStream(v2MessageFile);
		V2MessageLinefeedEncoder lfEncoder= new V2MessageLinefeedEncoder(sourceDataStream);
		
		//forward transformed XML to next step
		File outFile = new File("outv2.xml"); 
		FileWriter out = new FileWriter(outFile);
 
		StreamResult streamResult=new StreamResult(out);
		ArrayList<byte[]> v2Bytes=lfEncoder.getEncodeByteList();
		for(byte[] v2MsgByte:v2Bytes)
		{
			Source transformerSource = v2Encoder.decodeFromBytes(v2MsgByte);
			Transformer transformer =  TransformerFactory.newInstance().newTransformer();	
			transformer.transform(transformerSource, streamResult);
		}
//		streamResult.getOutputStream().close();
		out.close();
		
		String mapFile="workingspace/Hl7v2ToXml/mapping.xml";
		MappingTransformer tester= new MappingTransformer();
 
		FileWriter w = new FileWriter("tranform.out.xml");
		w.write(tester.transfer("outv2.xml", mapFile));
		w.close();
		Assert.assertNotNull(v2Encoder);
	}
	
	@Test
	public void testParseV2Message() throws TransformerFactoryConfigurationError, EncoderException, IOException, TransformerException
	{
		Encoder v2Encoder=Hl7V2MessageEncoderFactory.getV2MessageEncoder("2.4", "ADT_A03.xsd");
		String v2MessageFile="workingspace/Hl7v2/ADT_A03.hl7";
		FileInputStream sourceDataStream = new FileInputStream(v2MessageFile);
		V2MessageLinefeedEncoder lfEncoder= new V2MessageLinefeedEncoder(sourceDataStream);
		
		//forward transformed XML to next step
		File outFile = new File("out.xml"); 
		FileWriter out = new FileWriter(outFile);
 
		StreamResult streamResult=new StreamResult(out);
		ArrayList<byte[]> v2Bytes=lfEncoder.getEncodeByteList();
		for(byte[] v2MsgByte:v2Bytes)
		{
			Source transformerSource = v2Encoder.decodeFromBytes(v2MsgByte);
			Transformer transformer =  TransformerFactory.newInstance().newTransformer();

			transformer.setOutputProperty("indent","yes");
			transformer.setOutputProperty("encoding", "UTF-16");

			transformer.transform(transformerSource, streamResult);
		}
		out.close();
		Assert.assertNotNull(streamResult);
	}
}
