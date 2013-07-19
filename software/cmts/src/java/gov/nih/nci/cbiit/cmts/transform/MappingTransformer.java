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
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.mapping.MappingFactory;
import gov.nih.nci.cbiit.cmts.transform.artifact.RDFEncoder;
import gov.nih.nci.cbiit.cmts.transform.validation.XsdSchemaErrorHandler;
import gov.nih.nci.cbiit.cmts.transform.validation.XsdSchemaSaxValidator;


import java.io.*;
import java.net.*;
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
			FileWriter sWriter = new FileWriter(new File(args[2]));
			sWriter.write(transformer.transfer(args[0],args[1]));
			sWriter.flush();
			sWriter.close();
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

        String xql = null;
        try
        {
            xql = builder.getXQuery();
        }
        catch(Exception ee)
        {
            //System.out.println("CCCCC : GGG1=" + ee.getMessage());
            //ee.printStackTrace();
            throw new XQException(ee.getMessage());
        }

        String mm = null;
        try
        {
            mm = modifyMappingInstForWebFunction(xql, false);
        }
        catch(Exception ee)
        {
            //System.out.println("CCCCC : GGG2=" + ee.getMessage());
            //ee.printStackTrace();
            throw new XQException(ee.getMessage());
        }

        XQPreparedExpression exp = getConn().prepareExpression(mm);

		return exp;
	}
	
	@Override
	public String transfer(String sourceFile, String mappingFile) {
		// TODO Auto-generated method stub
		try {

            XQPreparedExpression exp = prepareXQExpression(mappingFile);

            // parse raw data to a temporary file
			//if source is HL7 v2, the target namespace is set as null
            //System.out.println("CCCC 441");
            String tempXmlSrc = parseRawData(sourceFile, mapping);

            URI sourcUri=new File(sourceFile).toURI();
			exp.bindString(new QName("docName"), sourcUri.getPath(), conn.createAtomicType(XQItemType.XQBASETYPE_STRING));

            XQResultSequence result = exp.executeQuery();

            String rawResult = result.getSequenceAsString(new Properties());
			RDFEncoder rdfEncoder=new RDFEncoder(rawResult);
			String xmlResult=rdfEncoder.getFormatedRDF();
			if (isTemporaryFileCreated()) {
				File tmpFile = new File(tempXmlSrc);
				tmpFile.delete();
			}

            return reorganizeTargetNameSpace(xmlResult);

            //return xmlResult;
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
	 *
	 */
	protected String parseRawData(String sourceRawDataFile, Mapping map)
			throws JAXBException, IOException {
		// do nothing
		return sourceRawDataFile;
	}
	@Override
    public List<ApplicationResult> validateXmlData(Object validator, String xmlData)
	{
        return validateXmlData_Base(validator, xmlData, ComponentType.TARGET);
    }
    public List<ApplicationResult> validateSourceXmlData(Object validator, String xmlData)
	{
        return validateXmlData_Base(validator, xmlData, ComponentType.SOURCE);
    }
    private List<ApplicationResult> validateXmlData_Base(Object validator, String xmlData, ComponentType compType)
	{
		List<ApplicationResult> rtnList=new ArrayList<ApplicationResult>();
		//using validator
		String schemaValidate = null;
        //String sourceSchema=null;
        Mapping mapping=(Mapping)validator;
		for (gov.nih.nci.cbiit.cmts.core.Component mapComp:mapping.getComponents().getComponent())
		{
			if (mapComp.getRootElement()!=null
					&&mapComp.getType().equals(ComponentType.TARGET))
			{
                if (compType.equals(ComponentType.TARGET))
                    schemaValidate=mapComp.getLocation();
				continue;
			}
            if (mapComp.getRootElement()!=null
					&&mapComp.getType().equals(ComponentType.SOURCE))
			{
                if (compType.equals(ComponentType.SOURCE))
                    schemaValidate=mapComp.getLocation();

				continue;
			}
        }
        //System.out.println("CCCC targetSchema=" + targetSchema);
        if (schemaValidate == null)
        {
            System.out.println("Not found "+compType.value()+" schema file for validation. error#22");
            return null;
        }
        XsdSchemaErrorHandler xsdErrorHandler=new XsdSchemaErrorHandler();

        Schema schema=XsdSchemaSaxValidator.loadSchema(schemaValidate, xsdErrorHandler);
		if (!xsdErrorHandler.getErrorMessage().isEmpty())
		{
			//add xsd schema validation message only if error occurs
			ApplicationMessage xsdInfor=new ApplicationMessage(compType.value() + " XSD Schema Validation");
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

    private String reorganizeTargetNameSpace(String xmlResult)
    {
        if (mapping == null) return xmlResult;
        List<Component> l = mapping.getComponents().getComponent();
        Component tgt = null;
        Component src = null;
        for (Component c:l) {
            if (c.getType().equals(ComponentType.TARGET))
                tgt = c;
            if (c.getType().equals(ComponentType.SOURCE))
                src = c;
        }
        String targetRootNameSpace = null;
        String sourceRootNameSpace = null;
        if (tgt.getRootElement()!=null&&tgt.getRootElement().getNameSpace()!=null) targetRootNameSpace = tgt.getRootElement().getNameSpace();
        if (src.getRootElement()!=null&&src.getRootElement().getNameSpace()!=null) sourceRootNameSpace = src.getRootElement().getNameSpace();

        String changeResult = "";
        String xmlResult2 = xmlResult;


        while ((targetRootNameSpace != null)&&(!targetRootNameSpace.trim().equals("")))
        {
            targetRootNameSpace = targetRootNameSpace.trim();
            if ((sourceRootNameSpace != null)&&(targetRootNameSpace.equals(sourceRootNameSpace.trim()))) break;

            String searchTag = " xmlns=\"";
            int idx = xmlResult2.indexOf(searchTag);
            if (idx < 0)
            {
                int idx5 = xmlResult2.indexOf("?>");
                if (idx5 > 0)
                {
                    changeResult = changeResult + xmlResult2.substring(0, idx5 + 2);
                    xmlResult2 =  xmlResult2.substring(idx5 + 2);
                }
                int idx6 = xmlResult2.indexOf(">");
                if (idx6 < 0) break;
                changeResult = changeResult + xmlResult2.substring(0, idx6) + searchTag + targetRootNameSpace + "\"" + xmlResult2.substring(idx6);
                return changeResult;
            }
            changeResult = changeResult + xmlResult2.substring(0, idx + searchTag.length());
            xmlResult2 = xmlResult2.substring(idx + searchTag.length());
            int idx2 = xmlResult2.indexOf("\"");
            if (idx2 < 0) break;
            changeResult = changeResult + targetRootNameSpace + xmlResult2.substring(idx2);
            return changeResult;
        }
        return xmlResult;
    }

}

/**
 * HISTORY: $Log: not supported by cvs2svn $ HISTORY: Revision 1.2 2008/10/20
 * 20:46:15 linc HISTORY: updated. HISTORY: HISTORY: Revision 1.1 2008/10/01
 * 18:59:13 linc HISTORY: updated. HISTORY:
 */

