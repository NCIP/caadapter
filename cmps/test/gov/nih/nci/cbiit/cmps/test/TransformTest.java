/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmps.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xquery.XQException;

import java.io.*;
import java.util.*;

import gov.nih.nci.cbiit.cmps.core.*;
import gov.nih.nci.cbiit.cmps.common.*;
import gov.nih.nci.cbiit.cmps.transform.XQueryTransformer;

/**
 * This class 
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-01 18:59:14 $
 *
 */
public class TransformTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link gov.nih.nci.cbiit.cmps.transform.XQueryBuilder#getXQuery()}.
	 * @throws XQException 
	 */
	@Test
	public void testXQueryTransform() throws XQException {
		final String sep = System.getProperty("line.separator");
		String queryString =
			"declare variable $docName as xs:string external;" + sep +
			"<result>{" +
			"      for $item in doc($docName)//item " +
			"    where $item/price > 10 " +
			"      and $item/quantity > 0 " +
			" order by $item/title " +
			"   return " +
			"<shipitem><name>{$item/title/text()}</name>" +
			" <quantity>{$item/quantity/text()}</quantity></shipitem>" +
			"}</result>";
		XQueryTransformer tester= new XQueryTransformer();
		tester.setFilename("etc/data/shiporder.xml");
		System.out.println(tester.query(queryString));
	}

}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 */

