/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
  * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.mif;

import java.io.Serializable;

/**
 * The class contains CMETs reference information.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-09-29 15:44:40 $
 */
public class CMETRef implements Serializable{

	static final long serialVersionUID = 3L;

	private String name;  // Reference name
	private String filename; // CMET filename
	private String className; // MIFClass in the CMETfile

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}
	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 */