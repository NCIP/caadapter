/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.transform;

import gov.nih.nci.cbiit.cmts.transform.csv.Csv2XmlTransformer;
import gov.nih.nci.cbiit.cmts.transform.hl7v2.Hl7v2XmlTransformer;

import javax.xml.xquery.XQException;

public class TransformerFactory {
	public static TransformationService getTransformer(String transformerType) throws XQException
	{
		if (transformerType.equals(TransformationService.TRANSFER_XML_TO_XML))
 			return new XQueryTransformer();
		else if (transformerType.equals(TransformationService.TRANSFER_HL7_v2_TO_XML))
			return new Hl7v2XmlTransformer();
 		else if (transformerType.equals(TransformationService.TRANSFER_CSV_TO_XML))
 			return new Csv2XmlTransformer();
		
		return new XQueryTransformer();
	}
}
