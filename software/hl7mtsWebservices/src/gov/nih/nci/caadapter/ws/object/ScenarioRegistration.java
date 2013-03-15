/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ws.object;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Apr 2, 2009
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-04-13 15:25:05 $
 * @since caAdapter v4.2
 */

public class ScenarioRegistration {

	private String name;
	private String mappingFile;
	private String targetFile;
	private String sourceSpecFile;
	private List<String> vocabuaryMappings;
	private Date dateCreated;

	/**
	 * @return the createDate
	 */
	public Date getDateCreate() {
		return dateCreated;
	}
	/**
	 * @param createDate the createDate to set
	 */
	public void setDateCreate(Date createDate) {
		this.dateCreated = createDate;
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
	/**
	 * @return the mappingFile
	 */
	public String getMappingFile() {
		return mappingFile;
	}
	/**
	 * @param mappingFile the mappingFile to set
	 */
	public void setMappingFile(String mappingFile) {
		this.mappingFile = mappingFile;
	}
	/**
	 * @return the targetFile
	 */
	public String getTargetFile() {
		return targetFile;
	}
	/**
	 * @param targetFile the targetFile to set
	 */
	public void setTargetFile(String targetFile) {
		this.targetFile = targetFile;
	}
	/**
	 * @return the sourceSpecFile
	 */
	public String getSourceSpecFile() {
		return sourceSpecFile;
	}
	/**
	 * @param sourceSpecFile the sourceSpecFile to set
	 */
	public void setSourceSpecFile(String sourceSpecFile) {
		this.sourceSpecFile = sourceSpecFile;
	}
	/**
	 * @return the vocabuaryMappings
	 */
	public List<String> getVocabuaryMappings() {
		return vocabuaryMappings;
	}
	/**
	 * @param vocabuaryMappings the vocabuaryMappings to set
	 */
	public void setVocabuaryMappings(List<String> vocabuaryMappings) {
		this.vocabuaryMappings = vocabuaryMappings;
	}

	/**
	 * Add one VOM into mapping scenario
	 * @param newVom
	 */
	public void addVocabuaryMappingFile(String newVom)
	{
		if (vocabuaryMappings==null)
			vocabuaryMappings=new ArrayList<String>();
		vocabuaryMappings.add(newVom);
	}

}


/**
* HISTORY: $Log: not supported by cvs2svn $
**/