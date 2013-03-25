/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.datatype;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Dec 12, 2008
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.2 $
 * @date 	 DATE: $Date: 2009-03-12 13:25:54 $
 * @since caAdapter v4.2
 */

public class DatatypeCoreAttributeLoader {

	private SAXBuilder builder;
	private HashMap<String, List> datatypeCoreAttributes;
	/**
	 * @return the datatypeCoreAttributes
	 */
	public HashMap<String, List> getDatatypeCoreAttributes() {
		return datatypeCoreAttributes;
	}

	public DatatypeCoreAttributeLoader()
	{
		builder = new SAXBuilder(false);
	}

	public void loadCoreAttributeConfig(InputStream in)
	{
		Document document;
		try {
			document = builder.build(in);
			Element root = document.getRootElement();
			parseRootElement(root);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void loadCoreAttributeConfig(File f)
	{
 		try {
			loadCoreAttributeConfig(new FileInputStream(new File(f.getAbsolutePath())));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void parseRootElement(Element root)
	{
		datatypeCoreAttributes=new HashMap<String, List>();
		Iterator itr=root.getChildren("datatype").iterator();
		while (itr.hasNext())
		{
			Element datatype = (Element)itr.next();
			String nullableAttr=datatype.getAttributeValue("nullable");
			if (nullableAttr!=null&&nullableAttr.equalsIgnoreCase("true"))
			{
				ArrayList coreAttrList=new ArrayList<String>();
				Iterator coreAttrItr=datatype.getChildren("coreAttribute").iterator();
				while(coreAttrItr.hasNext())
				{
					Element coreAttr = (Element)coreAttrItr.next();
					String attrName=coreAttr.getAttributeValue("name");
					coreAttrList.add(attrName);
				}
				String nameKey=datatype.getAttributeValue("name");
//				System.out
//						.println("DataTypeCoreAttributeLoader.parseRootElement()..datatype="+nameKey
//								+"...coreAttributes:"+coreAttrList);
				datatypeCoreAttributes.put(nameKey, coreAttrList);
			}
		}
	}
}


/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.1  2009/01/09 21:32:59  wangeug
* HISTORY: process core attribute seting with HL7 datatypes
* HISTORY:
**/