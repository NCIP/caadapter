/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.transform;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.cbiit.cmts.common.ApplicationMessage;
import gov.nih.nci.cbiit.cmts.common.ApplicationResult;
import gov.nih.nci.cbiit.cmts.core.ComponentType;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.mapping.MappingFactory;
import gov.nih.nci.cbiit.cmts.transform.validation.XsdSchemaErrorHandler;
import gov.nih.nci.cbiit.cmts.transform.validation.XsdSchemaSaxValidator;

import java.io.File;
import java.io.IOException;
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
public class XQueryTransformer implements TransformationService {

	private Mapping mapping;
	// Connection for querying
	private XQConnection conn;
	private boolean temporaryFileCreated = false;
	
	public boolean isTemporaryFileCreated() {
		return temporaryFileCreated;
	}

	public void setTemporaryFileCreated(boolean temporaryFileCreated) {
		this.temporaryFileCreated = temporaryFileCreated;
	}

	/**
	 * constructor
	 * 
	 * @throws XQException
	 */
	public XQueryTransformer() throws XQException {
		Configuration saxonConfig = new Configuration();
		SaxonXQDataSource dataSource = new SaxonXQDataSource(saxonConfig);
		conn = dataSource.getConnection();
	}

	/**
	 * @return the conn
	 */
	public final XQConnection getConn() {
		return conn;
	}


	/**
	 * execute XQuery
	 * 
	 * @param query
	 *            query string to execute
	 * @param sourceFile
	 *            name of source file
	 * @return
	 * @throws XQException
	 */
	public String executeQuery(String query, String sourceFile)
			throws XQException {
		XQPreparedExpression exp = getConn().prepareExpression(query);
		exp.bindString(new QName("docName"), sourceFile, conn
				.createAtomicType(XQItemType.XQBASETYPE_STRING));
		XQResultSequence result = exp.executeQuery();
		String rawResult = result.getSequenceAsString(new Properties());
		return TransformationUtil.formatXqueryResult(rawResult);
	}

	@Override
	public String Transfer(String sourceFile, String mappingFile) {
		// TODO Auto-generated method stub
		try {
			mapping = MappingFactory.loadMapping(new File(mappingFile));
			// parse raw data to a temporary file
			//if source is HL7 v2, the target namespace is set as null
			String tempXmlSrc = parseRawData(sourceFile, mapping);
			XQueryBuilder builder = new XQueryBuilder(mapping);
			String queryString = builder.getXQuery();
			String xmlResult = executeQuery(queryString, tempXmlSrc);
			if (isTemporaryFileCreated()) {
				File tmpFile = new File(tempXmlSrc);
				tmpFile.delete();
			}
			return xmlResult;
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ApplicationException e) {
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
	 * @throws ApplicationException
	 */
	protected String parseRawData(String sourceRawDataFile, Mapping map)
			throws JAXBException, IOException, ApplicationException {
		// do nothing
		return sourceRawDataFile;
	}

	public List<ApplicationResult> validateXmlData(Mapping maping, String xmlData)
	{
		List<ApplicationResult> rtnList=new ArrayList<ApplicationResult>();
		//using validator
		String targetSchema=null;
		for (gov.nih.nci.cbiit.cmts.core.Component mapComp:maping.getComponents().getComponent())
		{
			if (mapComp.getRootElement()!=null
					&&mapComp.getType().equals(ComponentType.TARGET))
			{
				targetSchema=mapComp.getLocation();
				continue;
			}
		}
		XsdSchemaErrorHandler xsdErrorHandler=new XsdSchemaErrorHandler();
		Schema schema=XsdSchemaSaxValidator.loadSchema(targetSchema, xsdErrorHandler);
		ApplicationMessage xsdInfor=new ApplicationMessage("Target XSD Schema Validation");
		rtnList.add(new ApplicationResult(ApplicationResult.Level.INFO, xsdInfor));
		rtnList.addAll(xsdErrorHandler.getErrorMessage());

		XsdSchemaErrorHandler xmlErrorHandler=new XsdSchemaErrorHandler();
		XsdSchemaSaxValidator.validateXmlData(schema, xmlData, xmlErrorHandler);
		ApplicationMessage xmlInfor=new ApplicationMessage("Result XML Data Validation");
		rtnList.add(new ApplicationResult(ApplicationResult.Level.INFO, xmlInfor));

		rtnList.addAll(xmlErrorHandler.getErrorMessage());
		
		return rtnList;
	}
	@Override
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

