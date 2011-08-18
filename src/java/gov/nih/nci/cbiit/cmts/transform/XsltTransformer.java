package gov.nih.nci.cbiit.cmts.transform;

import gov.nih.nci.cbiit.cmts.common.ApplicationResult;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import java.io.StringWriter;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XsltTransformer implements TransformationService {
/**
 * Perform XSLT transformation with given XSLT template and source data file.
 * @param inXML
 * @param inXSL
 * @param outTXT
 * @throws TransformerConfigurationException
 * @throws TransformerException
 */

	public void transform(String inXML,String inXSL,String outTXT)
	  throws TransformerConfigurationException, TransformerException 
	  {
		 StreamResult out = new StreamResult(outTXT);
		 xsltTransfer(inXML, inXSL, out);
	  }
	
	private void xsltTransfer(String sourceFile, String xsltFile, StreamResult resultStream) throws TransformerException
	{	
		StreamSource xsltStream = new StreamSource(xsltFile);
		javax.xml.transform.TransformerFactory xmlTransFactory = javax.xml.transform.TransformerFactory.newInstance();
		Transformer transformer = xmlTransFactory.newTransformer(xsltStream);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setErrorListener(new XSLTErrorListener());		
		
		StreamSource sourceStream = new StreamSource(sourceFile);
		transformer.transform(sourceStream,resultStream);
	}
	
	public static void main(String args[]){
		if (args.length<2)
		{
			System.out.println("XsltTransformer.main()...\nusage:sourcedata:stylesheet");
			System.exit(0);
		} else if (args.length<3)
		{
			args[3]="result_out.xml";
		}
		
		XsltTransformer st = new XsltTransformer();
		try {
			System.out.println("XsltTransformer.main()...Source Data:"+args[0]);
			System.out.println("XsltTransformer.main()...XSLT Templage:"+args[1]);
			System.out.println("XsltTransformer.main()...Result Data:"+args[2]);
			
			  st.transform(args[0],args[1],args[2]);
		} catch(TransformerConfigurationException e) {
			  System.err.println("Invalid factory configuration");
			  System.err.println(e);
		} catch(TransformerException e) {
			  System.err.println("Error during transformation");
			  System.err.println(e);
		  }
	}
	@Override
	public Mapping getTransformationMapping() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isPresentable() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public String transfer(String sourceFile, String mappingFile) {
		// TODO Auto-generated method stub
		StringWriter sWriter=new StringWriter();
		StreamResult out = new StreamResult(sWriter);
		try {
			xsltTransfer(sourceFile, mappingFile, out);
			
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return sWriter.getBuffer().toString();
	}
	@Override
	public List<ApplicationResult> validateXmlData(Mapping mapping,
			String xmlData) {
		// TODO Auto-generated method stub
		return null;
	}
}
