package gov.nih.nci.cbiit.cmts.transform;

 
import gov.nih.nci.cbiit.cmts.transform.artifact.RDFEncoder;
import gov.nih.nci.cbiit.cmts.transform.handler.SearchHeadNodeHandler;
import gov.nih.nci.caadapter.common.util.FileUtil;

import java.io.StringWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xquery.XQException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
    public String[] transfer(String sourceFile, String instructionFile) {
        // TODO Auto-generated method stub


        String head = searchRootName(sourceFile, instructionFile);
        if ((head == null)||(head.trim().equals("")))
        {
            System.out.println("Not Found Head Node name ");
            return null;
        }
        String[] res = null;
        try
        {
            res = gov.nih.nci.cbiit.cmts.util.FileUtil.divideSourceRawDataFile(sourceFile, head);
        }
        catch(IOException ie)
        {
            System.out.println("Not Found Head Node : IOException : " + ie.getMessage());
            return null;
        }

        boolean temporaryFileCreated = false;
        if ((res.length == 1)&&(res[0].equals(sourceFile))) temporaryFileCreated = false;
        else temporaryFileCreated = true;
        sourceDataInstance = res;
        List<String> resList = new ArrayList<String>();
        for (String sourceUnitFile:res)
        {
            boolean error = false;
            StringWriter sWriter=new StringWriter();
            StreamResult out = new StreamResult(sWriter);
            try {
                xsltTransfer(sourceUnitFile, instructionFile, out);

            } catch (TransformerException e) {
                // TODO Auto-generated catch block
                error = true;
                System.out.println("XML transformation with XSLT fail : " + e.getMessage());
                e.printStackTrace();
            }
            if (!error)
            {
                RDFEncoder rdfEncoder=new RDFEncoder(sWriter.getBuffer().toString());
                String xmlResult=rdfEncoder.getFormatedRDF();
                resList.add(xmlResult);
            }
            if (temporaryFileCreated)
            {
                File f = new File(sourceUnitFile);
                f.delete();
            }
        }
        if (resList.size() == 0)
        {
            System.out.println("No transformed output XML : " + sourceFile);
            return null;
        }
        String[] r = new String[resList.size()];
        for(int i=0;i<resList.size();i++) r[i] = resList.get(i);
        return r;
    }

    private String searchRootName(String sourceFile, String instructionFile)
    {
        File file = new File(sourceFile);
        if ((!file.exists())||(!file.isFile())) return null;

        SearchHeadNodeHandler handler = null;
        XMLReader producer = null;

        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            producer = parser.getXMLReader();
            handler = new SearchHeadNodeHandler();
            producer.setContentHandler(handler);

            producer.parse(new InputSource(new FileInputStream(file)));
        }
        catch(Exception e)
        {
            return null;
        }

        String cont = FileUtil.readFileIntoString(instructionFile);
        if ((cont == null)||(cont.trim().equals(""))) return null;


        int i = 0;
        String c = "";
        int minDepth = -1;
        String eleName = null;

        String xpathTag = null;
        boolean xpathLine = false;

        boolean isRemark = false;

        for(i=0;i<cont.length();i++)
        {
            String achar = cont.substring(i, i+1);
            c = c + achar;
            if (isRemark)
            {
                if (c.endsWith("-->")) isRemark = false;
                continue;
            }
            if (c.endsWith("<!--"))
            {
                isRemark = true;
                continue;
            }
            if (xpathLine)
            {
                if (achar.equals("\""))
                {
                    xpathLine = false;
                    xpathTag = xpathTag.trim();
                    if (xpathTag.startsWith("/")) xpathTag = xpathTag.substring(1);
                    int idx = xpathTag.indexOf("/");
                    if (idx > 0) xpathTag = xpathTag.substring(0, idx);
                    if (!xpathTag.trim().equals(""))
                    {
                        int dd = handler.getMinDepthWithElementName(xpathTag);
                        if (dd >= 0)
                        {
                            if ((minDepth < 0)||(dd < minDepth))
                            {
                                minDepth = dd;
                                eleName = xpathTag;
                            }
                        }
                    }
                }
                else xpathTag = xpathTag + achar;
            }
            if ((c.endsWith(" select=\""))||(c.endsWith(" match=\"")))
            {
                xpathLine = true;
                xpathTag = "";
            }
        }
        return eleName;
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
