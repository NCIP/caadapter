/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.v2meta;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Jan 22, 2009
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-02-25 16:52:11 $
 * @since caAdapter v4.2
 */


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Set;
import java.util.Hashtable;

public class V2MessageIndex implements Serializable {

	private Hashtable <String, ArrayList<String>> schemaFileName=new Hashtable<String, ArrayList<String>>();

	public void addMessageType(String schFileName)
	{
//		System.out.println("V2MessageIndex.addMessageType()..adding:"+schFileName);
		if (schFileName.indexOf(".xsd")<0)
			return;
		String[] tokens=null;
		if (schFileName.indexOf(File.separator)>-1)
			tokens=schFileName.split(File.separator);
		else
			tokens=schFileName.split("/");
		if (tokens.length<3)
			return;
		else
		{
			String versionName=tokens[1];
			String msgSchemaName=tokens[2];
			ArrayList<String> msgList=schemaFileName.get(versionName);
			if (msgList==null)
				msgList=new ArrayList<String>();
			msgList.add(msgSchemaName);
			schemaFileName.put(versionName, msgList);
		}

	}

	public Set<String> getMessageCategory()
	{
		return schemaFileName.keySet();
	}


	public Set<String> fingMessageTypesWithCategory(String msgCat)
	{
		TreeSet<String> rtnSet=new TreeSet<String>();
		ArrayList<String> xsdNames=schemaFileName.get(msgCat);
		if (xsdNames!=null)
		{
			rtnSet.addAll(xsdNames);
		}
		return rtnSet;
	}
}

/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.1  2009/01/23 18:22:00  wangeug
* HISTORY: Load V2 meta with version number and message schema name; do not use the absolute path of schema file
* HISTORY:
**/