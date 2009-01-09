/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.datatype;

import gov.nih.nci.caadapter.hl7.mif.MIFClass;

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
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-01-09 21:32:59 $
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
**/