/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.mif;

import java.io.File;
import java.util.HashMap;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Mar 9, 2009
 * @author   LAST UPDATE: $Author: wangeug 
 * @version  REVISION: $Revision: 1.2 $
 * @date 	 DATE: $Date: 2009-03-13 14:53:09 $
 * @since caAdapter v4.2
 */

public class NormativeVersionUtil {
	private static HashMap <String, MIFIndex>normativeSetting;
	private static MIFIndex currentMIFIndex;
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
		if (normativeSetting!=null)
			return normativeSetting.get(copyrightYear);
		return null;
	}

	private static void loadSetting()
	{
		String mifFilePath="conf/hl7-normative-setting.xml";
		NormativeVersionSettingLoader settingLoader= new NormativeVersionSettingLoader();
		settingLoader.loadNomativeSetting(new File(mifFilePath));
		normativeSetting=settingLoader.getNormativeSettings();
	}
}


/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.1  2009/03/12 15:00:46  wangeug
* HISTORY: support multiple HL& normatives
* HISTORY:
**/