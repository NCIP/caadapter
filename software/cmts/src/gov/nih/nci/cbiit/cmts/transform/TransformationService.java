/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.transform;

public interface TransformationService {
	public final static String TRANSFER_XML_TO_XML="XML_TO_XML";
	public final static String TRANSFER_CSV_TO_XML="CSV_TO_XML";
	public final static String TRANSFER_HL7_v2_TO_XML="HL7_V2_TO_XML";
	/**
	 * Transfer source data into target data using mapping file
	 * @param sourceFile URI of source data file
	 * @param mappingFile URI of mapping data file
	 * @return
	 */
	public String Transfer(String sourceFile, String mappingFile);
	
}
