/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





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
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-03-18 15:49:13 $
 * @since caAdapter v4.2
 */

public class CustomerDatatypeSettingLoader {

	private SAXBuilder builder;
	private HashMap<String, Datatype> customerDatatypes;
	/**
	 * @return the customerDatatypes
	 */
	public HashMap<String, Datatype> getCustmoerDatatypeSetting() {
		return customerDatatypes;
	}

	public CustomerDatatypeSettingLoader()
	{
		builder = new SAXBuilder(false);
	}

	public void loadCustomerDatatypeSetting(InputStream in)
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
	public void loadCustomerDatatypeSetting(File f)
	{
 		try {
 			loadCustomerDatatypeSetting(new FileInputStream(new File(f.getAbsolutePath())));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void parseRootElement(Element root)
	{
		customerDatatypes=new HashMap<String, Datatype>();
		Iterator itr=root.getChildren("datatype").iterator();
		while (itr.hasNext())
		{
			Element cusDataElement= (Element)itr.next();
			String cusName=cusDataElement.getAttributeValue("name");
			String baseType=cusDataElement.getAttributeValue("base");
			Datatype cusType=DatatypeParserUtil.getDatatype(baseType);
			if (cusType==null)
			{
				//set Default value if not defined at all
				cusType=new Datatype();
				cusType.setParents("ANY");
				cusType.setSimple(false);
			}
			cusType.setName(cusName);

			for (Element attrElm:(List<Element>)cusDataElement.getChildren("attribute"))
			{
				Attribute attribute = new Attribute();
				String attrName=attrElm.getAttributeValue("name");
				attribute.setName(attrName);
				attribute.setType(attrElm.getAttributeValue("type"));
				attribute.setAttribute(true);
				attribute.setProhibited(false);
				attribute.setMax(1);
				attribute.setMin(1);
				cusType.addAttribute(attrName, attribute);
			}
			customerDatatypes.put(cusName, cusType);
		}
	}
    public static void main(String[] args) throws Exception {
    	String mifFilePath="conf/hl7-normative-setting.xml";
//    	CustomerDatatypeSettingLoader settingLoader=new CustomerDatatypeSettingLoader();
//    	settingLoader.loadNomativeSetting(new File(mifFilePath));
//
//		HashMap<String, MIFIndex> normatives=settingLoader.getNormativeSettings();
//
	}
}


/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.2  2009/03/13 14:52:17  wangeug
* HISTORY: support multiple HL& normatives: reload a H3S/Map
* HISTORY:
* HISTORY: Revision 1.1  2009/03/12 15:00:46  wangeug
* HISTORY: support multiple HL& normatives
* HISTORY:
* HISTORY: Revision 1.1  2009/01/09 21:32:59  wangeug
* HISTORY: process core attribute seting with HL7 datatypes
* HISTORY:
**/