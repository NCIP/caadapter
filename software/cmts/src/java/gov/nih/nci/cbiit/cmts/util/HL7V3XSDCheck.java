package gov.nih.nci.cbiit.cmts.util;

import gov.nih.nci.cbiit.cmts.common.XSDParser;
import gov.nih.nci.cbiit.cmts.core.BaseMeta;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;

public class HL7V3XSDCheck {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		XSDParser p = new XSDParser();
		String dirPath=args[0];
		String xsdFile=args[1];//"FICR_IN926306UV03.xsd"; //args[0];
		String elmName=xsdFile.substring(0,xsdFile.indexOf(".xsd"));
		p.loadSchema(dirPath+"/multicacheschemas/"+xsdFile, null);
		ElementMeta e = p.getElementMeta("urn:hl7-org:v3", elmName);	
	}

}
