package gov.nih.nci.cbiit.cmts.transform;

import java.util.Stack;
import java.util.StringTokenizer;

public class QueryBuilderUtil {

	/**
	 * Retrieve the relative path of an xpath based on current home path
	 * @param currentHome
	 * @param path
	 * @return
	 */
	public static String  retrieveRelativePath(String currentHome, String path)
	{
		
		if (path.startsWith(currentHome))
		{
			String pathEnd=path.substring(currentHome.length());
			if (pathEnd.startsWith("/")||pathEnd.equals(""))
				return pathEnd;
//			return path.substring(currentHome.length());
		}
		
		StringBuilder ancestorPath = new StringBuilder();
		StringTokenizer stCur = new StringTokenizer(currentHome, "/");
		StringTokenizer stNew = new StringTokenizer(path, "/");
 
		//find the common shared ancestor
		while(stCur.hasMoreTokens() && stNew.hasMoreTokens()) 
		{
			String t = stCur.nextToken(); 
			if (t.equals(stNew.nextToken()))
 				ancestorPath.append("/").append(t);
			else 
				break;
		}
		if (ancestorPath.length()==0)//there not any shared ancestor
			return null;
		
		//There are shared ancestor
		String ret = "";
		ret += "/..";
		while(stCur.hasMoreTokens()) {
			ret += "/..";
			stCur.nextToken();
		}
		
		ret += path.substring(ancestorPath.toString().length());
		System.out.println("QueryBuilderUtil.retrieveRelativePath()...return...home="+currentHome+"...path="+path+"..relative="+ret);
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
}
