package gov.nih.nci.cbiit.cmts.test;


import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import gov.nih.nci.cbiit.cmts.transform.XsltTransformer;

import org.junit.Test;

public class XSLTTransformerTest {

	@Test
	public void testXsltTransformation() 
	{
		 String inXML ="workingspace/XSLT/famousePerson.xml";// arg[0];
		  String inXSL ="workingspace/XSLT/person.xsl"; //arg[1];
		  String outTXT ="xsltOut.html";// arg[2];

		  XsltTransformer st = new XsltTransformer();
		  try {
		  st.transform(inXML,inXSL,outTXT);
		  } catch(TransformerConfigurationException e) {
		  System.err.println("Invalid factory configuration");
		  System.err.println(e);
		  } catch(TransformerException e) {
		  System.err.println("Error during transformation");
		  System.err.println(e);
		  }

	}
}
