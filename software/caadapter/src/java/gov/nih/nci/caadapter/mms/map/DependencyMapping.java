/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.mms.map;
import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.common.metadata.TableMetadata;

/**
 * Class for dependency mapping
 *
 * @author OWNER: Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     caAdatper v4.0
 * @version    $Revision: 1.5 $
 * @date       $Date: 2008-09-26 20:35:27 $
 * @created 11-Aug-2006 8:18:16 AM
 */
public class DependencyMapping {

	/**
	 * This value should be the fully qualified name of the domain object.
	 */
	private ObjectMetadata sourceDependency;
	/**
	 * This value should be the name of the database table supporting the
	 * source dependency.
	 */
	private TableMetadata targetDependency;


	public DependencyMapping(){
	}

	/**
	 *
	 * @param targetDependency
	 * @param sourceDependency
	 */
	public DependencyMapping(TableMetadata targetDependency, ObjectMetadata sourceDependency){
		this.sourceDependency = sourceDependency;
		this.targetDependency = targetDependency;
	}

	public ObjectMetadata getSourceDependency(){
		return sourceDependency;
	}

	public TableMetadata getTargetDependency(){
		return targetDependency;
	}

	/**
	 *
	 * @param sourceDependency
	 */
	public void setSourceDependency(ObjectMetadata sourceDependency){
		this.sourceDependency = sourceDependency;
	}

	/**
	 *
	 * @param targetDependency
	 */
	public void setTargetDependency(TableMetadata targetDependency){
		this.targetDependency = targetDependency;

	}

	public boolean areMappedEntities(String objectXpath, String tableXpath) {
		boolean areMapped = false;
		if (sourceDependency.getXPath().equals(objectXpath) && targetDependency.getXPath().equals(tableXpath)) {
			areMapped = true;
		}
		return areMapped;
	}

	public boolean isAMappedEntities(String objectXpath) {
			boolean areMapped = false;
			if (sourceDependency.getXPath().equals(objectXpath)) {
				areMapped = true;
			}
			return areMapped;
	}

}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 */
