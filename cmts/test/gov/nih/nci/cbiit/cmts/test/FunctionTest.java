package gov.nih.nci.cbiit.cmts.test;

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
