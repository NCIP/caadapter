package gov.nih.nci.cbiit.cmts.transform;

 
import gov.nih.nci.cbiit.cmts.transform.artifact.RDFEncoder;



import java.io.StringWriter;
import java.io.StringReader;
import java.io.CharArrayReader;
import java.io.File;
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
        String xslt = gov.nih.nci.caadapter.common.util.FileUtil.readFileIntoString(xsltFile);
        if (xslt == null) throw new TransformerException("XSLT file is not found or the Content is empty.");
        String mm = null;

        try
        {
            mm = modifyMappingInstForWebFunction(xslt, true);
        }
        catch(Exception ee)
        {
            throw new TransformerException(ee.getMessage());
        }

        StringBuffer buf = new StringBuffer(mm);
        
        while(true)
        {
            StreamSource xsltStream = new StreamSource(new CharArrayReader(buf.toString().toCharArray()));

            javax.xml.transform.TransformerFactory xmlTransFactory = javax.xml.transform.TransformerFactory.newInstance();
            Transformer transformer = xmlTransFactory.newTransformer(xsltStream);

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setErrorListener(new XSLTErrorListener());

            StreamSource sourceStream = new StreamSource(sourceFile);

            transformer.transform(sourceStream,resultStream);

            break;
        }
    }
	
	public static void main(String args[]){

        String sourceData = null;
        String xsltFile = null;
        String outFile = null;
        boolean display = false;
        if (args.length<2)
		{
			System.out.println("XsltTransformer.main()...\nusage:sourcedata:stylesheet");
			System.exit(0);
		}
        else if (args.length<3)
		{
            sourceData = args[0];
            xsltFile = args[1];
            outFile = "result_out.xml";
            display = true;
		}
        else
        {
            sourceData = args[0];
            xsltFile = args[1];
            outFile = args[2];
        }

        try {
			XsltTransformer st = new XsltTransformer();
			System.out.println("XsltTransformer.main()...Source Data:" + sourceData);
			System.out.println("XsltTransformer.main()...XSLT Templage:" + xsltFile);
			System.out.println("XsltTransformer.main()...Result Data:" + outFile);
			
			  st.transform(sourceData, xsltFile, outFile);
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

        if (display)
        {
            String outT = gov.nih.nci.caadapter.common.util.FileUtil.readFileIntoString(outFile);
            File f = new File(outFile);
            f.delete();
            if ((outT == null)||(outT.trim().equals(""))) System.out.println("NULL Result...");
            else System.out.println(outT);
        }
    }
}
