/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.mms.validator;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;

import gov.nih.nci.caadapter.mms.map.CumulativeMapping;
import gov.nih.nci.caadapter.common.metadata.ModelMetadata;

/**
 * This is a singleton class and it's purpose is to coordinate all
 * aspects of the object to database mapping validation tasks.
 * @version 1.0
 * @created 11-Aug-2006 8:18:17 AM
 */
public class MasterValidator {

	public CumulativeMapping currentMapping;
	public DatatypeValidator datatypeValidator;
	public AttributeMappingValidator dependencyMappingValidator;
	public SingleAssociationMappingValidator singleAssociationMappingValidator;
	public ManyToManyMappingValidator manyToManyMappingValidator;
	public ModelMetadata modelMetadata;
	private static MasterValidator uniqueInstance;

	private MasterValidator(){
	}

	public static MasterValidator getInstance(){
		return null;
	}

	public boolean validate(){
		return false;
	}

	public boolean validateAttributeMapping(){
		return false;
	}

	public boolean validateDependencyMapping(){
		return false;
	}

	public boolean validateManyToManyMapping(){
		return false;
	}

	public boolean validateSemanticMapping(){
		return false;
	}

	public boolean validateSingleAssociationMapping(){
		return false;
	}

}
