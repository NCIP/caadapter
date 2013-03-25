/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.junit;

import gov.nih.nci.caadapter.hl7.v2meta.V2MessageLinefeedEncoder;
import gov.nih.nci.caadapter.hl7.v2meta.V2MetaXSDUtil;
import gov.nih.nci.cbiit.cmps.core.ElementMeta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.junit.Test;
import junit.framework.TestCase;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import com.sun.encoder.Encoder;
import com.sun.encoder.EncoderFactory;
import com.sun.encoder.EncoderProperties;
import com.sun.encoder.EncoderType;
import com.sun.encoder.MetaRef;


public class V2Test extends TestCase {

	
//	@Test
//	public void testSchemaIndexLoader() throws Exception {
//		V2MessageSchemaIndexLoader.loadMessageInfos();
//	}
	
	@Test
	public void wtestParseHl7V2XSD() throws Exception {
//		String msg="ADT_A01.xsd";
//		String schmFile="hl7v2xsd_saved/2.4/"+msg;
		String msg="COCT_MT150001UV01.xsd";
		String schmFile="schemas/multicacheschemas/"+msg;
		ElementMeta e =V2MetaXSDUtil.loadMessageMeta(schmFile);
		System.out.println("V2Test.testParseHl7V2XSD()..:"+e.getName());
//		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
//		Marshaller u = jc.createMarshaller();
	}
	
	@Test
	public void testEncode() {// throws Exception {
		try{
			//Get the encoder factory instance
			EncoderFactory factory = EncoderFactory.newInstance();
			String msg="ADT_A01";
			//Get the encoder type instance using an encoding style
			//EncoderType type = factory.makeType("customencoder-1.0");
			EncoderType type = factory.makeType("hl7encoder-1.0");
	
			//Specify a top element
			QName topElem = new QName("urn:hl7-org:v2xml", msg);
			//QName topElem = new QName("urn:hl7-org:v2xml", "ORU_R01");
			//Construct the metadata instance
			MetaRef meta = factory.makeMeta(this.getClass().getClassLoader().getResource("hl7v2xsd/2.4/"+msg+".xsd"),topElem);
			//Create the encoder instance, HL7Encoder
			Encoder encoder = factory.newEncoder(type, meta);
			EncoderProperties prop=encoder.getProperties();

			//Decode the data
			String dataPath="workingspace/V2Meta_to_V3/";
			V2MessageLinefeedEncoder lfEncoder= new V2MessageLinefeedEncoder(new FileInputStream(new File(dataPath+msg+".hl7")));
	//		javax.xml.transform.Source source = coder.decodeFromStream(new FileInputStream(new File("data/"+msg+".hl7")));
//			InputStream v2In=lfEncoder.getEncodedInputStream();
//			System.out.println("V2Test.testEncode()..process v2 pipe in:");
//			Source saxSource = encoder.decodeFromStream(v2In);
 
		
			StreamResult streamResult = new StreamResult(new FileOutputStream(msg+".xml"));
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
	//		transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"users.dtd");
			transformer.setOutputProperty(OutputKeys.INDENT,"yes");
			ArrayList<byte[]> v2Bytes=lfEncoder.getEncodeByteList();
			for(byte[] v2MsgByte:v2Bytes)
			{
				Source saxSource = encoder.decodeFromBytes(v2MsgByte);
				transformer.transform(saxSource, streamResult);
//				transformer.reset();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
}
