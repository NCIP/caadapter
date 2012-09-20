package gov.nih.nci.cbiit.cmts.function;

import java.util.Map;

import gov.nih.nci.cbiit.cmts.core.FunctionType;
import gov.nih.nci.cbiit.cmts.util.FileUtil;

public class StringFunction {

    /**
     * Converts the string argument to lower-case
     * Example: toLowerCase('The XML')
     * Result: 'the xml'
     * @param functionType
     * @param paramters
     * @return
     */
    public String toLowerCase(FunctionType functionType, Map<String, String> paramters)
    {
        StringBuffer rtnBf=new StringBuffer();
        String stOne=(String)paramters.get("string");
        rtnBf.append("lower-case("+stOne+")");
        return rtnBf.toString();
    }
    /**
     * Converts the string argument to upper-case
     * Example: toUpperCase('The XML')
     * Result: 'THE XML'
     * @param functionType
     * @param paramters
     * @return
     */
    public String toUpperCase(FunctionType functionType, Map<String, String> paramters)
    {
        StringBuffer rtnBf=new StringBuffer();
        String stOne=(String)paramters.get("string");
        rtnBf.append("upper-case("+stOne+")");
        return rtnBf.toString();
    }
    /**
     * Returns the length of the specified string. If there is no string argument it returns the length of the string value of the current node
     * Example: stringLength('Beatles')
     * Result: 7
     * @param functionType
     * @param paramters
     * @return
     */
    public String stringLength(FunctionType functionType, Map<String, String> paramters)
    {
        StringBuffer rtnBf=new StringBuffer();
        String stOne=(String)paramters.get("string");
        rtnBf.append("string-length("+stOne+")");
        return rtnBf.toString();
    }

    /**
     * 	Returns the concatenation of the strings
     * 	Example: concatenate('XPath ','is ','FUN!')
     *  Result: 'XPath is FUN!'
     * @param functionType
     * @param paramters
     * @return
     */

    public String concatenate(FunctionType functionType, Map<String, String> paramters)
    {
        StringBuffer rtnBf=new StringBuffer();
        String stOne=(String)paramters.get("string1");
        if (stOne==null)
            stOne="\"\"";
        String stTwo=(String)paramters.get("string2");
        if (stTwo==null)
            stTwo="\"\"";
        rtnBf.append("concat("+stOne+"," +stTwo+")");
        return rtnBf.toString();
    }
    /**
     * Returns the start of string1 before string2 occurs in it
     * Example: substringBefore('12/10','/')
     * Result: '12'
     * @param functionType
     * @param paramters
     * @return
     */
    public String substringBefore(FunctionType functionType, Map<String, String> paramters)
    {
        StringBuffer rtnBf=new StringBuffer();
        String stOne=(String)paramters.get("string1");
        String stTwo=(String)paramters.get("string2");

        rtnBf.append("substring-before("+stOne+"," +stTwo+")");
        return rtnBf.toString();
    }

    /**
     * Returns the remainder of string1 after string2 occurs in it
     * Example: substringAfter('12/10','/')
     * Result: '10'
     * @param functionType
     * @param paramters
     * @return
     */
    public String substringAfter(FunctionType functionType, Map <String, String>paramters)
    {
        StringBuffer rtnBf=new StringBuffer();
        String stOne=(String)paramters.get("string1");
        String stTwo=(String)paramters.get("string2");

        rtnBf.append("substring-after("+stOne+"," +stTwo+")");
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
    public String substring(FunctionType functionType, Map<String, String> paramters)
    {
        StringBuffer rtnBf=new StringBuffer();
        String stOne=(String)paramters.get("string");
        String stStart=(String)paramters.get("start");
        String stEnd=(String)paramters.get("end");
        rtnBf.append("substring("+stOne+","
                + FileUtil.findNumericValue(stStart)+","+FileUtil.findNumericValue(stEnd)+")");
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
    public String replace(FunctionType functionType, Map<String, String> paramters)
    {
        StringBuffer rtnBf=new StringBuffer();
        String stOne=(String)paramters.get("string");
        String stPattern=(String)paramters.get("pattern");
        String stReplace=(String)paramters.get("replace");

        rtnBf.append("replace("+stOne+"," +stPattern+","+stReplace+")");
        return rtnBf.toString();
    }
}
