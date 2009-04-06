/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.mif;

import gov.nih.nci.caadapter.common.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Mar 9, 2009
 * @author   LAST UPDATE: $Author: wangeug 
 * @version  REVISION: $Revision: 1.5 $
 * @date 	 DATE: $Date: 2009-04-06 16:07:01 $
 * @since caAdapter v4.2
 */

public class NormativeVersionUtil {
	private static HashMap <String, MIFIndex>normativeSetting;
	private static MIFIndex currentMIFIndex;
    private static String HL7_NORMATIVE_SETTNG_XML_FILE_NAME = "conf/hl7-normative-setting.xml";
    /**
	 * @return the currentMIFIndex
	 */
	public static MIFIndex getCurrentMIFIndex() {
		return currentMIFIndex;
	}

	/**
	 * @param currentMIFIndex the currentMIFIndex to set
	 */
	public static void setCurrentMIFIndex(MIFIndex currentMIFIndex) {
		NormativeVersionUtil.currentMIFIndex = currentMIFIndex;
		System.out.println("NormativeVersionUtil.setCurrentMIFIndex()..curent MIFIndex:"+currentMIFIndex.getCopyrightYears());
	}

	public static   HashMap <String, MIFIndex> loadNormativeSetting()
	{
		if (normativeSetting==null)
			loadSetting();
		
		return normativeSetting;
	}
	
	public static MIFIndex loadMIFIndex(String copyrightYear)
	{
		if (normativeSetting==null)
			loadSetting();
		if (copyrightYear==null||copyrightYear.equals(""))
			copyrightYear=MIFIndex.DEFAULT_COPYRIGHT_YEAR;

		if (normativeSetting!=null)
			return normativeSetting.get(copyrightYear);
		return null;
	}

	private static void loadSetting()
	{
		String mifSettingFilePath = getNormativeSettingXmlFileName();
		URL mifSettingURL=FileUtil.retrieveResourceURL(mifSettingFilePath);
		
		NormativeVersionSettingLoader settingLoader= new NormativeVersionSettingLoader();
		if (mifSettingURL!=null)
		{
			try {
				InputStream mifSettingIs =mifSettingURL.openStream();
				settingLoader.loadNomativeSetting(mifSettingIs);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else
			settingLoader.loadNomativeSetting(new File(mifSettingFilePath));
		normativeSetting=settingLoader.getNormativeSettings();
	}

    public static String getNormativeSettingXmlFileName()
	{
        return HL7_NORMATIVE_SETTNG_XML_FILE_NAME;
    }
}


/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.4  2009/03/18 15:50:36  wangeug
* HISTORY: enable wesstart to support multiple normatives
* HISTORY:
* HISTORY: Revision 1.3  2009/03/13 16:29:04  wangeug
* HISTORY: support multiple HL& normatives: set default as 2008
* HISTORY:
* HISTORY: Revision 1.2  2009/03/13 14:53:09  wangeug
* HISTORY: support multiple HL& normatives: remember the current version
* HISTORY:
* HISTORY: Revision 1.1  2009/03/12 15:00:46  wangeug
* HISTORY: support multiple HL& normatives
* HISTORY:
**/