/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;

import gov.nih.nci.cbiit.cmts.function.FunctionException;
import gov.nih.nci.cbiit.cmts.function.FunctionInvoker;
import org.junit.Test;
public class FunctionTest 
{

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
	 * test invoke function method 
	 */
	@Test
	public void testFunctionMethod()  throws Exception 
	{
		String fName="gov.nih.nci.cbiit.cmts.function.DateTimeFunction";
		String fMethod="countDays";
		Map<String, String> paramters =new HashMap<String, String>();
		paramters.put("startDate", "2010-02-10");
		paramters.put("endDate", "2010-02-20");
		Object argList[]=new Object[]{null,paramters };
		
		try {
			FunctionInvoker.invokeFunctionMethod(fName, fMethod, argList);
		} catch (FunctionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * test invoke function method 
	 */
	@Test
	public void testFunctionInvoker()  throws Exception 
	{
		String fName="gov.nih.nci.cbiit.cmts.function.DateFunction";
		String fMethod="countDays";
		String startDate="200902010211";
		String endDate="201002010211";
		String argList[]=new String[]{startDate,endDate };
		
		try {
			FunctionInvoker.invokeFunctionMethod(fName, fMethod, argList);
		} catch (FunctionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
