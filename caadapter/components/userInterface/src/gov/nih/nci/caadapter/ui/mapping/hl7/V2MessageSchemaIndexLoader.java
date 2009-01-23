/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.ui.mapping.hl7;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.hl7.mif.MIFIndex;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Jan 22, 2009
 * @author   LAST UPDATE: $Author: wangeug 
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-01-23 18:22:00 $
 * @since caAdapter v4.2
 */

public class V2MessageSchemaIndexLoader {
	private static V2MessageIndex v2Index;
	
	public  static V2MessageIndex loadMessageInfos() throws Exception {
		if (v2Index!=null)
			return v2Index;
		String zipFilePath="lib/HL7v2xsd.zip";
		System.out.println("MIFIndexParser.loadMIFInfosZip()..zipFilePath"+zipFilePath);
		ZipFile zip = new ZipFile(zipFilePath);
		Enumeration entryEnum=zip.entries();

		v2Index = new V2MessageIndex();
		while (entryEnum.hasMoreElements())
		{
			ZipEntry zipEntry=(ZipEntry)entryEnum.nextElement();			
			String fileName=zipEntry.getName();
 			v2Index.addMessageType(fileName);
		}
		return v2Index;
	}
}


/**
* HISTORY: $Log: not supported by cvs2svn $
**/