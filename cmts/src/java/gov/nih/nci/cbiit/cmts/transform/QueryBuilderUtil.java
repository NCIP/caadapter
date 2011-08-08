package gov.nih.nci.cbiit.cmts.transform;

import gov.nih.nci.cbiit.cmts.core.FunctionType;
import gov.nih.nci.cbiit.cmts.function.FunctionException;
import gov.nih.nci.cbiit.cmts.function.FunctionInvoker;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

public class QueryBuilderUtil {

	/**
	 * Retrieve the relative path of an xpath based on current home path
	 * @param referencePath
	 * @param absolutePath
	 * @return
	 */
	public static String  retrieveRelativePath(String referencePath, String absolutePath)
	{
		if (referencePath==null)
			return absolutePath; //the root element
		if (absolutePath.startsWith(referencePath))
		{
			String pathEnd=absolutePath.substring(referencePath.length());
			if (pathEnd.startsWith("/")||pathEnd.equals(""))
				return pathEnd;
		}
		
		StringBuilder ancestorPath = new StringBuilder();
		StringTokenizer stCur = new StringTokenizer(referencePath, "/");
		StringTokenizer stNew = new StringTokenizer(absolutePath, "/");
 
		//find the common shared ancestor
		while(stCur.hasMoreTokens() && stNew.hasMoreTokens()) 
		{
			String t = stCur.nextToken(); 
			if (t.equals(stNew.nextToken()))
 				ancestorPath.append("/").append(t);
			else 
				break;
		}
		String ret = "";
		if (ancestorPath.length()!=0)//there not any shared ancestor
		{	
			//There are shared ancestor
			ret += "/..";
			while(stCur.hasMoreTokens()) {
				ret += "/..";
				stCur.nextToken();
			}
			
			ret += absolutePath.substring(ancestorPath.toString().length());
		}
		System.out.println("QueryBuilderUtil.retrieveRelativePath()...return...home="+referencePath+"...path="+absolutePath+"..relative="+ret);
		return ret;	
	}
	
	public static String buildXPath(Stack<String>  pathStack) 
	{
		StringBuilder sb = new StringBuilder();
		for (String s:pathStack) {
			sb.append("/").append(s);
		}
		return sb.toString();
	}
	
	/**
	 * Generate XPath expression for a data manipulation function without input port
	 * @param functionType
	 * @return
	 */
	public static String generateXpathExpressionForFunctionWithoutInput(FunctionType functionType)
	{
		if (functionType.getData().size()!=1)
			return "invalid function:"+functionType.getGroup()+":"+functionType.getName()+":"+functionType.getMethod();
		Map<String,String> parameterMap=new HashMap<String,String>();
		Object argList[]=new Object[]{functionType, parameterMap};
		
		try {
			String xqueryString=(String)FunctionInvoker.invokeFunctionMethod(functionType.getClazz(), functionType.getMethod(), argList);
			return xqueryString ;
		} catch (FunctionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
