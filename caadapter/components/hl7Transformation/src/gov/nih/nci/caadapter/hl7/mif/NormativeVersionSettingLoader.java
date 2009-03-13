/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.mif;

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
 * @date 	 DATE: $Date: 2009-03-13 14:52:17 $
 * @since caAdapter v4.2
 */

public class NormativeVersionSettingLoader {

	private SAXBuilder builder;
	private HashMap<String, MIFIndex> nomativeSetting;
	/**
	 * @return the datatypeCoreAttributes
	 */
	public HashMap<String, MIFIndex> getNormativeSettings() {
		return nomativeSetting;
	}

	public NormativeVersionSettingLoader()
	{
		builder = new SAXBuilder(false);
	}
	
	public void loadNomativeSetting(InputStream in)
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
	public void loadNomativeSetting(File f)
	{
 		try {
			loadNomativeSetting(new FileInputStream(new File(f.getAbsolutePath())));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void parseRootElement(Element root)
	{
		nomativeSetting=new HashMap<String, MIFIndex>();
		Iterator itr=root.getChildren("normative").iterator();
		while (itr.hasNext())
		{
			Element normative= (Element)itr.next();
			String copyrightYear=normative.getAttributeValue("copyrightYear");
			String description=normative.getAttributeValue("description");		
			String mifFilePath=normative.getChild("mifFile").getValue();
			String schemaFilePath=normative.getChild("schemaFile").getValue();
			try {
				MIFIndex mifIndexInfos=MIFIndexParser.loadMIFIndexFromZipFile(mifFilePath);
				mifIndexInfos.setCopyrightYears(copyrightYear);
				mifIndexInfos.setMifPath(mifFilePath);
				mifIndexInfos.setSchemaPath(schemaFilePath);
				mifIndexInfos.setNormativeDescription(description);
				nomativeSetting.put(copyrightYear, mifIndexInfos);
//				MIFIndexParser.printMIFIndex(mifIndexInfos);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//				nomativeSetting.put(nameKey, coreAttrList);
			}
		}
	
    public static void main(String[] args) throws Exception {
    	String mifFilePath="conf/hl7-normative-setting.xml";
    	NormativeVersionSettingLoader settingLoader=new NormativeVersionSettingLoader();
    	settingLoader.loadNomativeSetting(new File(mifFilePath));
    	
		HashMap<String, MIFIndex> normatives=settingLoader.getNormativeSettings();
		
	} 
}


/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.1  2009/03/12 15:00:46  wangeug
* HISTORY: support multiple HL& normatives
* HISTORY:
* HISTORY: Revision 1.1  2009/01/09 21:32:59  wangeug
* HISTORY: process core attribute seting with HL7 datatypes
* HISTORY:
**/