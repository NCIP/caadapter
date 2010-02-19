package gov.nih.nci.cbiit.cmts.function;

import java.util.Map;

import gov.nih.nci.cbiit.cmts.core.FunctionType;

public class StringFunction {

	/**
	 * Converts the string argument to lower-case
	 * Example: lower-case('The XML')
	 * Result: 'the xml'
	 * @param functionType
	 * @param paramters
	 * @return
	 */
	public String toLowerCase(FunctionType functionType, Map paramters)
	{
		StringBuffer rtnBf=new StringBuffer();
		String stOne=(String)paramters.get("string");
		rtnBf.append("{ lower-case("+reformatStingParameter(stOne)+")}");
		return rtnBf.toString();
	}
	/**
	 * Converts the string argument to upper-case
	 * Example: upper-case('The XML')
	 * Result: 'THE XML'
	 * @param functionType
	 * @param paramters
	 * @return
	 */
	public String toUpperCase(FunctionType functionType, Map paramters)
	{
		StringBuffer rtnBf=new StringBuffer();
		String stOne=(String)paramters.get("string");
		rtnBf.append("{ upper-case("+reformatStingParameter(stOne)+")}");
		return rtnBf.toString();
	}
	/**
	 * Returns the length of the specified string. If there is no string argument it returns the length of the string value of the current node
	 * Example: string-length('Beatles')
	 * Result: 7
	 */
	public String stringLength(FunctionType functionType, Map paramters)
	{
		StringBuffer rtnBf=new StringBuffer();
		String stOne=(String)paramters.get("string");
		rtnBf.append("{ string-length("+reformatStingParameter(stOne)+")}");
		return rtnBf.toString();
	}
	
	/**
	 * 	Returns the concatenation of the strings
	 * 	Example: concat('XPath ','is ','FUN!')
	 *  Result: 'XPath is FUN!'
	**/
	
	public String concatenate(FunctionType functionType, Map paramters)
	{
		StringBuffer rtnBf=new StringBuffer();
		String stOne=(String)paramters.get("string1");
		String stTwo=(String)paramters.get("string2");
	
		rtnBf.append("{ concat("+reformatStingParameter(stOne)+"," +reformatStingParameter(stTwo)+")}");
		return rtnBf.toString();
	}
	/**
	 * Returns the start of string1 before string2 occurs in it
	 * Example: substring-before('12/10','/')
	 * Result: '12'
	 * @param functionType
	 * @param paramters
	 * @return
	 */
	public String substringBefore(FunctionType functionType, Map paramters)
	{
		StringBuffer rtnBf=new StringBuffer();
		String stOne=(String)paramters.get("string1");
		String stTwo=(String)paramters.get("string2");

		rtnBf.append("{ substring-before("+reformatStingParameter(stOne)+"," +reformatStingParameter(stTwo)+")}");
		return rtnBf.toString();
	}
	
	/**
	 * Returns the remainder of string1 after string2 occurs in it
	 * Example: substring-after('12/10','/')
	 * Result: '10'
	 * @param functionType
	 * @param paramters
	 * @return
	 */
	public String substringAfter(FunctionType functionType, Map paramters)
	{
		StringBuffer rtnBf=new StringBuffer();
		String stOne=(String)paramters.get("string1");
		String stTwo=(String)paramters.get("string2");

		rtnBf.append("{ substring-after("+reformatStingParameter(stOne)+"," +reformatStingParameter(stTwo)+")}");
		return rtnBf.toString();
	}
	/**
	 * Returns the substring from the start position to the specified length. Index of the first character is 1. If length is omitted it returns the substring from the start position to the end
	 *Example: substring('Beatles',1,4)
	 *Result: 'Beat'
	 *Example: substring('Beatles',2)
	 *Result: 'eatles'
	 * @param functionType
	 * @param paramters
	 * @return
	 */
	public String substring(FunctionType functionType, Map paramters)
	{
		StringBuffer rtnBf=new StringBuffer();
		String stOne=(String)paramters.get("string");
		String stStart=(String)paramters.get("start");
		String stEnd=(String)paramters.get("end");
		rtnBf.append("{ substring("+reformatStingParameter(stOne)+"," 
				+stStart+","+stEnd+")}");//+reformatStingParameter(stStart)+"," +reformatStingParameter(stEnd)+")}");
		return rtnBf.toString();
	}
	/**
	 * Returns a string that is created by replacing the given pattern with the replace argument
	 * Example: replace("Bella Italia", "l", "*")
	 * Result: 'Be**a Ita*ia'
	 * Example: replace("Bella Italia", "l", "")
	 * Result: 'Bea Itaia'
	 * @param functionType
	 * @param paramters
	 * @return
	 */
	public String replace(FunctionType functionType, Map paramters)
	{
		StringBuffer rtnBf=new StringBuffer();
		String stOne=(String)paramters.get("string");
		String stPattern=(String)paramters.get("pattern");
		String stReplace=(String)paramters.get("replace");
	
		rtnBf.append("{ replace("+reformatStingParameter(stOne)+"," +reformatStingParameter(stPattern)+","+reformatStingParameter(stReplace)+")}");
		return rtnBf.toString();
	}
	private String reformatStingParameter(String sIn)
	{
		if (sIn.indexOf("}")>-1)
			return sIn.replace("{"," ").replace("}","");
		return "\""+sIn +"\"";
	}
}
