package gov.nih.nci.cbiit.cmts.transform;

 
import gov.nih.nci.cbiit.cmts.transform.artifact.RDFEncoder;
import gov.nih.nci.caadapter.common.util.FileUtil;

import java.io.StringWriter;
import java.io.File;
import java.io.CharArrayReader;
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

        String str = sWriter.getBuffer().toString();
        //System.out.println("CCCC out = " + str);
        RDFEncoder rdfEncoder=new RDFEncoder(str);
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

        String xml = FileUtil.readFileIntoString(sourceFile);
        if ((xml == null)||(xml.trim().equals("")))
        {
            throw new TransformerException("Not found or reading failure of this source XML file : " +  sourceFile);
        }
        String s = checkNameSpaceAttr(xml);
        StreamSource sourceStream = new StreamSource(new CharArrayReader(s.toCharArray()));

        //System.out.println("CCCC DDD xml=" + s);
        //StreamSource sourceStream = new StreamSource(sourceFile);
		transformer.transform(sourceStream,resultStream);
	}
	private String checkNameSpaceAttr(String xml)
    {
        String namespaceTag = " xmlns=\"";
        if (xml.indexOf(namespaceTag) < 0) return xml;
        String s = "";
        boolean head = false;
        boolean foundHead = false;
        boolean remark = false;
        boolean skip = false;
        String before = "";

        for(int i=0;i<xml.length();i++)
        {
            String achar = xml.substring(i, i+1);
            if (skip)
            {
               if (achar.equals("\""))
               {
                   skip = false;
                   foundHead = true;
                   continue;
               }
            }
            else s = s + achar;
            if (foundHead) continue;
            if (remark)
            {
                if (s.endsWith("-->")) remark = false;
                continue;
            }
            if (s.endsWith("<!--"))
            {
                remark = true;
                continue;
            }

            if (achar.equals(">"))
            {
                skip = false;
                if (head)
                {
                    foundHead = true;
                    head = false;
                }
                continue;
            }

            if (head)
            {

                if (s.endsWith(namespaceTag))
                {
                    skip = true;
                    s = s.substring(0, s.length() - namespaceTag.length());
                }
                continue;
            }

            if (before.equals("<"))
            {
                char ch = achar.toCharArray()[0];
                byte bt = (byte) ch;
                boolean isNormalChar = false;
                if ((bt >= 48)&&(bt <=57)) isNormalChar = true;
                if ((bt >= 97)&&(bt <=122)) isNormalChar = true;
                if ((bt >= 65)&&(bt <=90)) isNormalChar = true;
                if (isNormalChar)
                {
                    head = true;
                    continue;
                }
            }

            before = achar;
        }
        return s;
    }
    public static void main(String args[]){

        String source_xml = "";
        String xslt = "";
        String output = null;
        String tempOut = "result_out199293948493948.xml";

        if (args.length<2)
		{
			//System.out.println("XsltTransformer.main()...\nusage:sourcedata:stylesheet");
			//System.exit(0);

            //source_xml = "c:\\tmp\\cdcatalog2.xml";
            //xslt = "c:\\tmp\\cdcatalog2.xsl";

            source_xml = "C:\\project\\subversion\\software\\cmts\\workingspace\\cda\\ADT_A03_test.xml";
            xslt = "C:\\project\\subversion\\software\\cmts\\workingspace\\cda\\tt2.xsl";
        } else if (args.length<3)
		{
            source_xml = args[0];
            xslt = args[1];
            //args[2]="result_out.xml";
		}
        else if (args.length==3)
		{
            source_xml = args[0];
            xslt = args[1];
            output = args[2];
		}
        else
        {
            System.out.println("XsltTransformer.main()...\nusage: sourceXML_File stylesheet_File [outputXML_File]");
			System.exit(0);
        }

        boolean t = false;
        if (source_xml == null)  t = true;
        else if (source_xml.trim().equals(""))  t = true;
        else if (xslt == null)  t = true;
        else if (xslt.trim().equals(""))  t = true;

        if (t)
        {
            System.out.println("XsltTransformer.main()...\nsource xml or XSLT is null.");
			System.exit(0);
        }

        try {
			XsltTransformer st = new XsltTransformer();
			System.out.println("XsltTransformer.main()...Source Data:"+source_xml);
			System.out.println("XsltTransformer.main()...XSLT Templage:"+xslt);

            if ((output == null)||(output.trim().equals("")))  output = tempOut;
            else
            {
                System.out.println("XsltTransformer.main()...Result Data:"+output);
            }
			
			  st.transform(source_xml, xslt, output);

            if (output.equals(tempOut))
            {
                File file = new File(output);

                if ((file.exists())&&(file.isFile()))
                {
                    System.out.println("XSLT transforming result("+file.getAbsolutePath()+"):");

                    System.out.println(FileUtil.readFileIntoString(file.getAbsolutePath()));
                    file.delete();
                }
                else System.out.println("XSLT transforming result: something wrong!!");
            }
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
