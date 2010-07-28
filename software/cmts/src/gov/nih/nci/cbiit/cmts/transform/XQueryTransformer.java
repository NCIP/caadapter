/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.transform;

import java.util.Properties;

import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQItemType;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;

import javax.xml.namespace.QName;

import net.sf.saxon.Configuration;
import net.sf.saxon.xqj.SaxonXQDataSource;

/**
 * This class performs the transformation using XQuery
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.3 $
 * @date       $Date: 2008-10-22 19:01:17 $
 *
 */
public class XQueryTransformer {
	// Filename for XML document to query
	private String sourceFileName;
	
	// Data Source for querying
//	private SaxonXQDataSource dataSource;

	// Connection for querying
	private XQConnection conn;

	// Query String
	private String queryString;
//	
//	// Prepared Query
//	private XQPreparedExpression exp;
	
	/**
	 * constructor
	 * @throws XQException
	 */
	public XQueryTransformer() throws XQException {
		Configuration  saxonConfig=new Configuration();
		SaxonXQDataSource dataSource = new SaxonXQDataSource(saxonConfig);
		conn = dataSource.getConnection();
	}

	/**
	 * @return the filename
	 */
	public final String getFilename() {
		return sourceFileName;
	}

	/**
	 * @param filename the filename to set
	 */
	public final void setSourceFileName(String filename) {
		this.sourceFileName = filename;
	}

	/**
	 * @return the conn
	 */
	public final XQConnection getConn() {
		return conn;
	}

	/**
	 * set XQuery
	 * @param queryString
	 * @throws XQException
	 */
	public void setQuery(String query) throws XQException {
		queryString=query;
	}
	
	/**
	 * execute XQuery with pre-set query and source file
	 * @see #executeQuery(String, String)
	 * @return
	 * @throws XQException
	 */
	public String executeQuery() throws XQException {
		return TransformationUtil.formatXqueryResult(executeQuery(queryString, sourceFileName));
	}

	/**
	 * execute XQuery
	 * @param query query string to execute
	 * @param sourceFile name of source file
	 * @return
	 * @throws XQException
	 */
	public String executeQuery(String query, String sourceFile) throws XQException {
		XQPreparedExpression exp=conn.prepareExpression(query);
		exp.bindString(new QName("docName"), sourceFile,
				conn.createAtomicType(XQItemType.XQBASETYPE_STRING));
		XQResultSequence result = exp.executeQuery();
		return result.getSequenceAsString(new Properties());
	}
	
	/**
	 * test method
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: java ibm.dw.xqj.XQueryTester [XML filename]");
			System.exit(-1);
		}

		try {
			String xmlFilename = args[0];
			XQueryTransformer tester= new XQueryTransformer();
			tester.setSourceFileName(xmlFilename);

			final String sep = System.getProperty("line.separator");
			String queryString =
				"declare variable $docName as xs:string external;" + sep +
				"      for $cd in doc($docName)/CATALOG/CD " +
				"    where $cd/YEAR > 1980 " +
				"      and $cd/COUNTRY = 'USA' " +
				" order by $cd/YEAR " +
				"   return " +
				"<cd><title>{$cd/TITLE/text()}</title>" +
				" <year>{$cd/YEAR/text()}</year></cd>";
			tester.setQuery(queryString);
			System.out.println(tester.executeQuery());
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.err.println(e.getMessage());
		}
	}

}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.2  2008/10/20 20:46:15  linc
 * HISTORY: updated.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/10/01 18:59:13  linc
 * HISTORY: updated.
 * HISTORY:
 */

