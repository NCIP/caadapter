package gov.nih.nci.cbiit.cmts.function;

import java.util.Map;

import gov.nih.nci.cbiit.cmts.core.FunctionType;

public class MathFunction {

	/**
	 * Returns the absolute value of the argument
	 * Example: abs(3.14)
	 * Result: 3.14
	 * 
	 * Example: abs(-3.14)
	 * Result: 3.14
	 * 
	 * @param functionType
	 * @param paramters
	 * @return
	 */
	public String abs(FunctionType functionType, Map<String, String> paramters)
	{
		StringBuffer rtnBf=new StringBuffer();
		String stOne=(String)paramters.get("input");
		rtnBf.append("abs(number("+stOne+"))");
		return rtnBf.toString();
	}
	/**
	 *Returns the smallest integer that is greater than the number argument
	 * Example: ceiling(3.14)
	 * Result: 4
	 * @param functionType
	 * @param paramters
	 * @return
	 */
	public String ceiling(FunctionType functionType, Map<String, String> paramters)
	{
		StringBuffer rtnBf=new StringBuffer();
		String stOne=(String)paramters.get("input");
		rtnBf.append("ceiling(number("+stOne+"))");
		return rtnBf.toString();
	}
	/**
	 * Returns the largest integer that is not greater than the number argument
	 * Example: floor(3.14)
	 * Result: 3
	 * @param functionType
	 * @param paramters
	 * @return
	 */
	public String floor(FunctionType functionType, Map<String, String> paramters)
	{
		StringBuffer rtnBf=new StringBuffer();
		String stOne=(String)paramters.get("input");
		rtnBf.append("floor(number("+stOne+"))");
		return rtnBf.toString();
	}
	/**
	 * Rounds the number argument to the nearest integer
	 * Example: round(3.14)
	 * Result: 3
	 * @param functionType
	 * @param paramters
	 * @return
	 */
	public String round(FunctionType functionType, Map<String, String> paramters)
	{
		StringBuffer rtnBf=new StringBuffer();
		String stOne=(String)paramters.get("input");
		rtnBf.append("round(number("+stOne+"))");
		return rtnBf.toString();
	}
	/**
	 * 	Returns the sum of the two terms
	 * 	Example: sum(3,-6)
	 *  Result: -3
	 * @param functionType
	 * @param paramters
	 * @return
	 */	
	public String addition(FunctionType functionType, Map<String, String> paramters)
	{
		StringBuffer rtnBf=new StringBuffer();
		String stOne=(String)paramters.get("term1");
		String stTwo=(String)paramters.get("term2");
		rtnBf.append("number("+stOne+")+number(" +stTwo+")");
//		rtnBf.append("sum(number("+stOne+"),number(" +stTwo+"))");
		return rtnBf.toString();
	}
	
	/**
	 * 	Returns the difference of the two terms 
	 * 	Example: subtraction(-3,5)
	 *  Result: -8
	 * @param functionType
	 * @param paramters
	 * @return
	 */
	
	public String subtraction(FunctionType functionType, Map<String, String> paramters)
	{
		StringBuffer rtnBf=new StringBuffer();
		String stOne=(String)paramters.get("term1");
		String stTwo=(String)paramters.get("term2");
	
		rtnBf.append("number("+stOne+")- number(" +stTwo+")");
		return rtnBf.toString();
	}
	/**
	 * 	Returns the product of the two factors
	 * 	Example: multiplication(-3,5)
	 *  Result: -15
	 * @param functionType
	 * @param paramters
	 * @return
	 */
	
	public String multiplication(FunctionType functionType, Map<String, String> paramters)
	{
		StringBuffer rtnBf=new StringBuffer();
		String stOne=(String)paramters.get("factor1");
		String stTwo=(String)paramters.get("factor2");
	
		rtnBf.append("number("+stOne+")* number(" +stTwo+")");
		return rtnBf.toString();
	}
	/**
	 * 	Returns the quotient of the dividend and divisor arguments
	 * 	Example: division(-3,5)
	 *  Result: -0.6
	 * @param functionType
	 * @param paramters
	 * @return
	 */
	
	public String division(FunctionType functionType, Map<String, String> paramters)
	{
		StringBuffer rtnBf=new StringBuffer();
		String stOne=(String)paramters.get("dividend");
		String stTwo=(String)paramters.get("divisor");
	
		rtnBf.append("(number("+stOne+") div number(" +stTwo+"))");
		return rtnBf.toString();
	}
	
	/**
	 * 	Returns the modulus of the dividend and divisor arguments
	 * 	Example: modulus(-22,5)
	 *  Result: -2
	 * @param functionType
	 * @param paramters
	 * @return
	 */
	
	public String modulus(FunctionType functionType, Map<String, String> paramters)
	{
		StringBuffer rtnBf=new StringBuffer();
		String stOne=(String)paramters.get("dividend");
		String stTwo=(String)paramters.get("divisor");
	
		rtnBf.append("(number("+stOne+") mod number(" +stTwo+"))");
		return rtnBf.toString();
	}
}
