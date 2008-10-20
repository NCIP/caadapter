/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmps.transform;

import java.util.Properties;

import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQItemType;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;

import javax.xml.namespace.QName;

import net.sf.saxon.xqj.SaxonXQDataSource;

/**
 * This class 
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2008-10-20 20:46:15 $
 *
 */
public class XQueryTransformer {
	// Filename for XML document to query
	private String filename;
	
	// Data Source for querying
	private SaxonXQDataSource dataSource;

	// Connection for querying
	private XQConnection conn;

	// Query String
	private String queryString;
	
	// Prepared Query
	private XQPreparedExpression exp;
	
	public XQueryTransformer() throws XQException {
		dataSource = new SaxonXQDataSource();
		conn = dataSource.getConnection();
	}

	/**
	 * @return the filename
	 */
	public final String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public final void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return the dataSource
	 */
	public final SaxonXQDataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @return the conn
	 */
	public final XQConnection getConn() {
		return conn;
	}

	public void setQuery(String queryString) throws XQException {
		exp = conn.prepareExpression(queryString);
		this.queryString = queryString;
	}
	
	public String executeQuery() throws XQException {
		exp.bindString(new QName("docName"), filename,
				conn.createAtomicType(XQItemType.XQBASETYPE_STRING));
		XQResultSequence result = exp.executeQuery();
		return result.getSequenceAsString(new Properties());
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: java ibm.dw.xqj.XQueryTester [XML filename]");
			System.exit(-1);
		}

		try {
			String xmlFilename = args[0];
			XQueryTransformer tester= new XQueryTransformer();
			tester.setFilename(xmlFilename);

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
 * HISTORY: Revision 1.1  2008/10/01 18:59:13  linc
 * HISTORY: updated.
 * HISTORY:
 */

