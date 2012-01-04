package gov.nih.nci.cbiit.cmts.util;

public class SchemaAnnotationUtil {

	public static String extractTagValue(String value, String tagName)
	{
		if (value==null||tagName==null)
			return null;
		if (value.indexOf(tagName)<0)
			return null;
		String tagValue=value.substring(value.indexOf(tagName), value.lastIndexOf(tagName));
		if (tagValue==null)
			return null;
		if (tagValue.indexOf(">")<0)
			return tagValue;
		if (tagValue.indexOf("</")<0)
			return tagValue;
		String rtnValue=tagValue.substring(tagValue.indexOf(">")+1, tagValue.lastIndexOf("</"));
		rtnValue=SchemaAnnotationUtil.trim(rtnValue,' ');
		return rtnValue.replaceAll("\\s+", " ");
	}
	
	private static String trim(String source, char c) 
	{      
		int len = source.length();      
		int st = 0;      
		char[] val = source.toCharArray();      
		for (;(st < len) && (val[st] <= c);st++){}      
		for (;(st < len) && (val[len-1]<=c);len--){}      
		return ((st > 0) || (len < source.length())) ?source.substring(st, len) : source;   
	}
}
