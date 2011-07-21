package gov.nih.nci.cbiit.cmts.transform;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XsltTransformer {

	public void transform(String inXML,String inXSL,String outTXT)
	  throws TransformerConfigurationException, TransformerException 
	  {
		 javax.xml.transform.TransformerFactory xmlTransFactory = javax.xml.transform.TransformerFactory.newInstance();
		 StreamSource xslStream = new StreamSource(inXSL);
		 Transformer transformer = xmlTransFactory.newTransformer(xslStream);
		 transformer.setErrorListener(new XSLTErrorListener());
		 transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		 StreamSource in = new StreamSource(inXML);
		 StreamResult out = new StreamResult(outTXT);
		 transformer.transform(in,out);
	  }
}
