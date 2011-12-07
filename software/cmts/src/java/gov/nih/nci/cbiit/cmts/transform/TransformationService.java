package gov.nih.nci.cbiit.cmts.transform;

import java.util.List;

import gov.nih.nci.cbiit.cmts.common.ApplicationResult;
public interface TransformationService {
	public final static String TRANSFER_XML_TO_XML="XML_TO_XML";
	public final static String TRANSFER_CSV_TO_XML="CSV_TO_XML";
	public final static String TRANSFER_HL7_V2_TO_XML="HL7_V2_TO_XML";
	public final static String TRANSFER_XML_TO_CDA="XML_TO_CDA";
	public final static String TRANSFER_CSV_TO_CDA="CSV_TO_CDA";
	public final static String TRANSFER_HL7_V2_TO_CDA="HL7_V2_TO_CDA";
	
	/**
	 * Verify an XML data against validator
	 * @param validator
	 * @param xmlData
	 * @return
	 */
	public List<ApplicationResult> validateXmlData(Object validator, String xmlData);
	/**
	 * Transfer source data into target data using mapping file
	 * @param sourceFile URI of source data file
	 * @param processInstruction URI of transformation file
	 * @return
	 */
	public String[] transfer(String sourceFile, String processInstruction);
	public void setPresentable(boolean value);
	public boolean isPresentable();
    public String[] getSourceDataInstances();

}
