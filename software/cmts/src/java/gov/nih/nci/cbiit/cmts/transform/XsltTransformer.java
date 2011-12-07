package gov.nih.nci.cbiit.cmts.transform;

 
import gov.nih.nci.cbiit.cmts.transform.artifact.RDFEncoder;

import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xquery.XQException;

public class XsltTransformer extends DefaultTransformer {
public XsltTransformer() throws XQException {
		super();
		// TODO Auto-generated constructor stub
	}

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
	@Override
	public String transfer(String sourceFile, String instructionFile) {
		// TODO Auto-generated method stub
		StringWriter sWriter=new StringWriter();
		StreamResult out = new StreamResult(sWriter);
		try {
			xsltTransfer(sourceFile, instructionFile, out);
			
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		RDFEncoder rdfEncoder=new RDFEncoder(sWriter.getBuffer().toString());
		String xmlResult=rdfEncoder.getFormatedRDF();
		return xmlResult;
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

		try {
			XsltTransformer st = new XsltTransformer();
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
		  } catch (XQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
