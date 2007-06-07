package gov.nih.nci.caadapter.mms;
import java.io.File;

import gov.nih.nci.caadapter.mms.map.CumulativeMapping;

import gov.nih.nci.caadapter.mms.validator.MasterValidator;
import gov.nih.nci.caadapter.mms.validator.SemanticMappingValidator;
import gov.nih.nci.caadapter.mms.generator.XMIGenerator;

/**
 * This is a singleton class whose purpose is to coordinate the mapping
 * of objects and object attributes to datasource tables and columns,
 * the validation of that mapping, wrting out of the final mapping file,
 * and the annotation of the xmi file with tagged values and
 * dependencies based on the contents of the mapping file.
 * @version 1.0
 * @created 11-Aug-2006 8:18:18 AM
 */
public class SDKIntegrationService {

	public CumulativeMapping cummulativeMapping;
	public MasterValidator masterValidator;
	public SemanticMappingValidator semanticMappingValidator;
	public static SDKIntegrationService uniqueSDKIntegrationService;
	public XMIGenerator XMIAnnotator;
	public File xmiFile;
	public CumulativeMapping thirdPartyCumulativeMapping;
	public CumulativeMapping commonCummulativeMapping;
	public File thirdPartyXMIFile;

	private SDKIntegrationService(){
	}
	
}