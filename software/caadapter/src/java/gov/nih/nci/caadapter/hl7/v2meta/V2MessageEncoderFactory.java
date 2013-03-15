/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.hl7.v2meta;

import gov.nih.nci.caadapter.common.util.FileUtil;

import java.net.URL;
import java.util.Hashtable;

import javax.xml.namespace.QName;

import com.sun.encoder.Encoder;
import com.sun.encoder.EncoderConfigurationException;
import com.sun.encoder.EncoderFactory;
import com.sun.encoder.EncoderType;
import com.sun.encoder.MetaRef;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Oct 23, 2008
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.3 $
 * @date 	 DATE: $Date: 2009-04-17 20:07:39 $
 * @since caAdapter v4.2
 */

public class V2MessageEncoderFactory {

	private static Hashtable<String, Encoder> encoderHash=new Hashtable<String, Encoder>();

	public static Encoder getV2MessageEncoder(String v2Version, String msgXsd)
	{
		String encoderKey=v2Version+"_"+msgXsd;
		Encoder rtnEncoder=encoderHash.get(encoderKey);
		if (rtnEncoder==null)
		{
			//first time loading this kind of v2 message
			System.out.println("V2MessageEncoderFactory.getV2MessageEncoder()..intializer v2 message encoder:"+v2Version+"/"+msgXsd);
    		long csvbegintime = System.currentTimeMillis();
			//Get the encoder factory instance
			EncoderFactory factory;
			try {
				factory = EncoderFactory.newInstance();
				//Get the encoder type instance using an encoding style
				EncoderType type = factory.makeType("hl7encoder-1.0");
				//Specify a top element
				String tolName=msgXsd.substring(0, msgXsd.indexOf(".xsd"));
				QName topElem = new QName("urn:hl7-org:v2xml", tolName);
				String v2SchemaHome="hl7v2xsd";
				String v2XsdFile=v2SchemaHome+"/"+v2Version+"/"+msgXsd;
				//Construct the metadata instance
				URL metaURL=null;
				metaURL=FileUtil.retrieveResourceURL(v2XsdFile);
				System.out
						.println("V2MessageEncoderFactory.getV2MessageEncoder()...metatURL:"+metaURL);
				MetaRef meta = factory.makeMeta(metaURL,topElem);
//				MetaRef meta = factory.makeMeta(ClassLoader.getSystemResource(v2XsdFile),topElem);
				System.out.println("V2MessageEncoderFactory.getV2MessageEncoder()...load V2Meta:"+meta+"..time:"+(System.currentTimeMillis()-csvbegintime));

				//Create the encoder instance, HL7Encoder
				rtnEncoder = factory.newEncoder(type, meta);
				System.out.println("V2MessageEncoderFactory.getV2MessageEncoder()...initialize encoder:"+(System.currentTimeMillis()-csvbegintime));
				encoderHash.put(encoderKey, rtnEncoder);
			} catch (EncoderConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return rtnEncoder;
	}

}


/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.2  2009/02/25 15:58:03  wangeug
* HISTORY: enable webstart
* HISTORY:
* HISTORY: Revision 1.1  2008/10/24 19:37:15  wangeug
* HISTORY: transfer a v2 message into v3 message using SUN v2 schema
* HISTORY:
**/