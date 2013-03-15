/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.transform;

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
}
