package gov.nih.nci.caadapter.hl7.junit;

import gov.nih.nci.caadapter.hl7.v2meta.V2MetaXSDUtil;
import gov.nih.nci.cbiit.cmps.common.XSDParser;
import gov.nih.nci.cbiit.cmps.core.ElementMeta;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.junit.Test;

import junit.framework.TestCase;

import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.encoder.Encoder;
import com.sun.encoder.EncoderConfigurationException;
import com.sun.encoder.EncoderException;
import com.sun.encoder.EncoderFactory;
import com.sun.encoder.EncoderProperties;
import com.sun.encoder.EncoderType;
import com.sun.encoder.MetaRef;


public class V2Test extends TestCase {

	
	@Test
	public void testParseHl7V2XSD() throws Exception {
		String v2XsdHome="C:/eclipseJ2ee/workspace/hl7v2xsd";
		String msg="ADT_A01";
		ElementMeta e =V2MetaXSDUtil.loadMessageMeta("2.5", msg);
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Marshaller u = jc.createMarshaller();
//		u.marshal(new JAXBElement(new QName("mapping"),ElementMeta.class, e), new File(v2XsdHome+"/parserOut/"+msg+"_out.xml"));
	}
	
	@Test
	public void testEncode() throws Exception {
		//Get the encoder factory instance
		EncoderFactory factory = EncoderFactory.newInstance();

		//Get the encoder type instance using an encoding style
		//EncoderType type = factory.makeType("customencoder-1.0");
		EncoderType type = factory.makeType("hl7encoder-1.0");

		//Specify a top element
		QName topElem = new QName("urn:hl7-org:v2xml", "ADT_A01");
		//QName topElem = new QName("urn:hl7-org:v2xml", "ORU_R01");

		//Construct the metadata instance
		MetaRef meta = factory.makeMeta(this.getClass().getClassLoader().getResource("hl7v2xsd/2.5/ADT_A01.xsd"),topElem);
				//.getResource("C:/eclipseJ2ee/workspace/HL7V2Decoder/hl7v2xsd/2.5/ADT_A03.xsd"), topElem);
		//MetaRef meta = factory.makeMeta(this.getClass().getClassLoader().getResource("hl7v2xsd/2.4/ORU_R01.xsd"), topElem);

		//Create the encoder instance, HL7Encoder
		Encoder coder = factory.newEncoder(type, meta);
		//Decode the data
		javax.xml.transform.Source source = coder.decodeFromStream(new FileInputStream(new File("data/ADT_A01.hl7")));
		//javax.xml.transform.Source source = coder.decodeFromStream(new FileInputStream(new File("etc/data/oru_r01.hl7")));
		StreamResult streamResult = new StreamResult(System.out);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer = tf.newTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
		serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"users.dtd");
		serializer.setOutputProperty(OutputKeys.INDENT,"yes");
		serializer.transform(source, streamResult);
		
	}
}
