package gov.nih.nci.cbiit.cmts.transform.hl7v2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xquery.XQException;

import com.sun.encoder.Encoder;
import com.sun.encoder.EncoderException;

import gov.nih.nci.cbiit.cmts.core.Component;
import gov.nih.nci.cbiit.cmts.core.ComponentType;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.transform.MappingTransformer;

public class Hl7v2XmlTransformer extends MappingTransformer {

	public Hl7v2XmlTransformer() throws XQException {
		super();
		// TODO Auto-generated constructor stub
		setTemporaryFileCreated(true);
	}
	
	@Override
	protected String[] parseRawData(String sourceRawDataFile, Mapping map) throws JAXBException, IOException
	{
		String fileName="xmlFile"+Calendar.getInstance().getTimeInMillis();
		String schemaUri="";
		String msgTopElement="";
		for (Component comp:map.getComponents().getComponent())
		{
			if (comp.getType().equals(ComponentType.SOURCE))
			{
					schemaUri=comp.getLocation();
					msgTopElement=comp.getRootElement().getName();
					comp.getRootElement().setNameSpace(null);
			}
		}
			
		Encoder v2Encoder=Hl7V2MessageEncoderFactory.getV2MessageEncoderWithSchema(schemaUri, msgTopElement);
		String v2MessageFile=sourceRawDataFile;
		FileInputStream sourceDataStream = new FileInputStream(v2MessageFile);
		V2MessageLinefeedEncoder lfEncoder= new V2MessageLinefeedEncoder(sourceDataStream);
		
		//forward transformed XML to next step
		File xmlV2File = new File(fileName); 
		FileWriter xmlV2Out = new FileWriter(xmlV2File);
 
		StreamResult streamResult=new StreamResult(xmlV2Out);
		ArrayList<byte[]> v2Bytes=lfEncoder.getEncodeByteList();
		for(byte[] v2MsgByte:v2Bytes)
		{
 			try {
				Source transformerSource = v2Encoder.decodeFromBytes(v2MsgByte);
				Transformer transformer =  TransformerFactory.newInstance().newTransformer();	
				transformer.transform(transformerSource, streamResult);
			} catch (EncoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		xmlV2Out.close();
		return new String[] {fileName};
	}

}
