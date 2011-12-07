/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.transform;

import gov.nih.nci.cbiit.cmts.common.ApplicationMessage;
import gov.nih.nci.cbiit.cmts.common.ApplicationResult;
import gov.nih.nci.cbiit.cmts.core.ComponentType;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.core.Component;
import gov.nih.nci.cbiit.cmts.mapping.MappingFactory;
import gov.nih.nci.cbiit.cmts.transform.artifact.RDFEncoder;
import gov.nih.nci.cbiit.cmts.transform.validation.XsdSchemaErrorHandler;
import gov.nih.nci.cbiit.cmts.transform.validation.XsdSchemaSaxValidator;
import gov.nih.nci.cbiit.cmts.ui.mapping.ElementMetaLoader;
import gov.nih.nci.caadapter.common.util.FileUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.validation.Schema;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQItemType;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.swing.tree.TreeNode;

import net.sf.saxon.Configuration;
import net.sf.saxon.xqj.SaxonXQDataSource;

/**
 * This class performs the transformation using XQuery
 * 
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since CMTS v1.0
 * @version $Revision: 1.3 $
 * @date $Date: 2008-10-22 19:01:17 $
 * 
 */
public class MappingTransformer extends DefaultTransformer {

	private Mapping mapping;
	// Connection for querying
	private XQConnection conn;
	private boolean temporaryFileCreated = false;
    private String instructionFile = null;


    /**
	 * constructor
	 * 
	 * @throws XQException
	 */
	public MappingTransformer() throws XQException {
		Configuration saxonConfig = new Configuration();
		SaxonXQDataSource dataSource = new SaxonXQDataSource(saxonConfig);
		conn = dataSource.getConnection();
	}
	public static void main(String args[]){
		if (args.length<2)
		{
			System.out.println("MappingTransformer.main()...\nusage:sourcedata:stylesheet");
			System.exit(0);
		} else if (args.length<3)
		{
			args[2]="result_out.xml";
		}
		System.out.println("MappingTransformer.main()...Source Data:"+args[0]);
		System.out.println("MappingTransformer.main()...Mapping Data:"+args[1]);
		System.out.println("MappingTransformer.main()...Result Data:"+args[2]);
		try {
			MappingTransformer transformer = new MappingTransformer();
			FileWriter sWriter = null;
            String[] res = transformer.transfer(args[0],args[1]);
            if (res.length == 1)
            {
                sWriter = new FileWriter(new File(args[2]));
                sWriter.write(res[0]);
                sWriter.flush();
			    sWriter.close();
            }
            else
            {
                for(int i=0;i<res.length;i++)
                {
                    String fileN = "_" + i + "_" + args[2];
                    sWriter = new FileWriter(new File(fileN));
                    sWriter.write(res[i]);
                    System.out.println("Output #1:" + fileN);
                    sWriter.flush();
			        sWriter.close();
                }
            }

		} catch (XQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	 
	}
	/**
	 * @return the conn
	 */
	protected final XQConnection getConn() {
		return conn;
	}
	public boolean isTemporaryFileCreated() {
		return temporaryFileCreated;
	}

	public void setTemporaryFileCreated(boolean temporaryFileCreated) {
		this.temporaryFileCreated = temporaryFileCreated;
	}


	protected XQPreparedExpression prepareXQExpression(String instruction) throws XQException, JAXBException
	{
		mapping = MappingFactory.loadMapping(new File(instruction));
		XQueryBuilder builder = new XQueryBuilder(mapping);
		String queryString = builder.getXQuery();
        String mm = "";
        for (int i=0;i<queryString.length();i++)
        {
            String achar = queryString.substring(i, i+1);
            if (achar.equals("<")) mm = mm + "&lt;";
            else if (achar.equals(">")) mm = mm + "&gt;";
            else mm = mm + achar;
        }
        XQPreparedExpression exp = getConn().prepareExpression(mm);

		return exp;
	}
	
	@Override
	public String[] transfer(String sourceFile, String mappingFile) {
		// TODO Auto-generated method stub
		try {
            List<String> listStr = new ArrayList<String>();
            XQPreparedExpression exp = prepareXQExpression(mappingFile);
			// parse raw data to a temporary file
			//if source is HL7 v2, the target namespace is set as null
            instructionFile = mappingFile;
            String[] tempXmlSrcArr = parseRawData(sourceFile, mapping);
            for(String tempXmlSrc:tempXmlSrcArr)
            {
                //System.out.println("CCCC file : " + tempXmlSrc);
                //System.out.println("CCCC content : " + FileUtil.readFileIntoString(tempXmlSrc));
                URI sourcUri=new File(tempXmlSrc).toURI();
                exp.bindString(new QName("docName"), sourcUri.getPath(), conn
                        .createAtomicType(XQItemType.XQBASETYPE_STRING));
                XQResultSequence result = exp.executeQuery();
                String rawResult = result.getSequenceAsString(new Properties());
                RDFEncoder rdfEncoder=new RDFEncoder(rawResult);
                String xmlResult=rdfEncoder.getFormatedRDF();
                if (isTemporaryFileCreated()) {
                    File tmpFile = new File(tempXmlSrc);
                    tmpFile.delete();
                }
                listStr.add(xmlResult);
            }
            String[] arr = new String[listStr.size()];
            for(int i=0;i<listStr.size();i++) arr[i] = listStr.get(i);
            return arr;
        } catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	/**
	 * Pre-process raw source data for transformer
	 * 
	 * @param sourceRawDataFile
	 *            URI of source raw data file
	 * @param map
	 *            Mapping object
	 * @return URI of pre-processed source data, it may be a temporary file
	 * @throws JAXBException
	 * @throws IOException
	 * @throws JAXBException
	 * @throws IOException
	 */
	protected String[] parseRawData(String sourceRawDataFile, Mapping map)
			throws JAXBException, IOException {
		//List<String> list = new ArrayList<String>();
        String sourceRoot = null;
        if (map != null)
        {
            Mapping.Components components = map.getComponents();
            List<Component> l = components.getComponent();
            for(Component c:l)
            {
                if(c.getType().equals(ComponentType.SOURCE))
                {
                    sourceRoot = c.getRootElement().getName();
                }
                if (sourceRoot != null) break;
            }
        }
        else
        {
            sourceRoot = searchRootElementName(instructionFile);
        }

        if ((sourceRoot == null)||(sourceRoot.trim().equals("")))
            throw new IOException("Source head node name is not found. : " + instructionFile);

        String[] res = gov.nih.nci.cbiit.cmts.util.FileUtil.divideSourceRawDataFile(sourceRawDataFile, sourceRoot);

        if ((res.length == 1)&&(res[0].equals(sourceRawDataFile))) temporaryFileCreated = false;
        else temporaryFileCreated = true;
        sourceDataInstance = res;
        return res;
    }

    private String searchRootElementName(String insFile)
    {
        List<String> cont = null;
        try
        {
            cont = FileUtil.readFileIntoList(insFile);
        }
        catch(IOException ie)
        {
            System.out.println("searchRootElementName() IOException : " + ie.getMessage());
            return null;
        }
        if ((cont == null)||(cont.size() == 0))
        {
            System.out.println("searchRootElementName() empty file ");
            return null;
        }

        String c = "";
        String before = "";
        String docVar = "";
        String docVarCore = null;
        boolean docVarTag = false;
        String headCore = "";
        boolean headCoreTag = false;
        for(String line:cont)
        {
            line = line.trim() + " ";
            for (int i=0;i<line.length();i++)
            {
                String achar = line.substring(i, i+1);
                if (achar.equals("\t")) achar = " ";
                if ((before.equals(" "))&&(achar.equals(" "))) {}
                else
                {
                    c = c + achar;
                    if (docVarTag) docVar = docVar + achar;
                }

                if (docVarTag)
                {
                    if (achar.equals(";"))
                    {
                        docVarTag = false;
                        docVar = docVar.substring(0, docVar.length()-1).trim();
                        if (docVar.toLowerCase().endsWith(" as xs:string external"))
                        {
                            docVarCore = docVar.substring(0, docVar.indexOf(" "));
                            if (docVarCore.trim().equals("$"))
                            {
                                docVarCore = null;
                                docVar = "";
                            }
                        }
                        else
                        {
                            docVar = "";
                        }
                    }
                    if ((docVarCore == null)&&(c.toLowerCase().endsWith(" declare ")))
                    {
                        docVarTag = false;
                        docVar = "";
                    }
                    if ((c.toLowerCase().endsWith(" element "))||
                        (c.toLowerCase().endsWith(" document "))||
                        (achar.equals("{")))
                    {
                        System.out.println("searchRootElementName() Invalid XQuery format ");
                        return null;
                    }
                }
                if ((docVarCore == null)&&(c.toLowerCase().endsWith("declare variable $")))
                {
                    docVar = "$";
                    docVarTag = true;
                }

                before = achar;
                if (docVarCore == null) continue;
                if (headCoreTag)
                {
                    if ((achar.equals(" "))||
                        (achar.equals("/"))||
                        (achar.equals("}"))||
                        (achar.equals(")")))
                    {
                       return headCore;
                    }
                    else headCore = headCore + achar;
                }
                if (c.endsWith("doc("+docVarCore+")/")) headCoreTag = true;
            }
        }
        return null;
    }

    @Override
	public List<ApplicationResult> validateXmlData(Object validator, String xmlData)
	{
		List<ApplicationResult> rtnList=new ArrayList<ApplicationResult>();
		//using validator
		String targetSchema=null;
		Mapping mapping=(Mapping)validator;
		for (gov.nih.nci.cbiit.cmts.core.Component mapComp:mapping.getComponents().getComponent())
		{
			if (mapComp.getRootElement()!=null
					&&mapComp.getType().equals(ComponentType.TARGET))
			{
				targetSchema=mapComp.getLocation();
				continue;
			}
		}
        //System.out.println("CCCC targetSchema=" + targetSchema);
        XsdSchemaErrorHandler xsdErrorHandler=new XsdSchemaErrorHandler();

        Schema schema=XsdSchemaSaxValidator.loadSchema(targetSchema, xsdErrorHandler);
		if (!xsdErrorHandler.getErrorMessage().isEmpty())
		{
			//add xsd schema validation message only if error occurs
			ApplicationMessage xsdInfor=new ApplicationMessage("Target XSD Schema Validation");
			rtnList.add(new ApplicationResult(ApplicationResult.Level.INFO, xsdInfor));
			rtnList.addAll(xsdErrorHandler.getErrorMessage());
		}
		XsdSchemaErrorHandler xmlErrorHandler=new XsdSchemaErrorHandler();
		XsdSchemaSaxValidator.validateXmlData(schema, xmlData, xmlErrorHandler);
		ApplicationMessage xmlInfor=new ApplicationMessage("Result XML Data Validation");
		rtnList.add(new ApplicationResult(ApplicationResult.Level.INFO, xmlInfor));

		rtnList.addAll(xmlErrorHandler.getErrorMessage());
		
		return rtnList;
	}
	 
	public Mapping getTransformationMapping() {
		// TODO Auto-generated method stub
		return mapping;
	}
}

/**
 * HISTORY: $Log: not supported by cvs2svn $ HISTORY: Revision 1.2 2008/10/20
 * 20:46:15 linc HISTORY: updated. HISTORY: HISTORY: Revision 1.1 2008/10/01
 * 18:59:13 linc HISTORY: updated. HISTORY:
 */

