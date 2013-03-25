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
 			return new MappingTransformer();
		else if (transformerType.equals(TransformationService.TRANSFER_HL7_V2_TO_XML))
			return new Hl7v2XmlTransformer();
 		else if (transformerType.equals(TransformationService.TRANSFER_CSV_TO_XML))
 			return new Csv2XmlTransformer();
 		else if (transformerType.equals(TransformationService.TRANSFER_XML_TO_CDA))
 		{	MappingTransformer rtnTransformer= new MappingTransformer();
 			rtnTransformer.setPresentable(true);
 			return rtnTransformer;
 		}
 		else if (transformerType.equals(TransformationService.TRANSFER_CSV_TO_CDA))
 		{	MappingTransformer rtnTransformer= new Csv2XmlTransformer();
 			rtnTransformer.setPresentable(true);
 			return rtnTransformer;
 		}
 		else if (transformerType.equals(TransformationService.TRANSFER_HL7_V2_TO_CDA))
 		{	MappingTransformer rtnTransformer= new Hl7v2XmlTransformer();
 			rtnTransformer.setPresentable(true);
 			return rtnTransformer;
 		}
        else if ((transformerType.toLowerCase().equals(".map"))||
                 (transformerType.toLowerCase().equals("map"))
                )
 			return new MappingTransformer();
        else if ((transformerType.toLowerCase().equals(".xsl"))||
                 (transformerType.toLowerCase().equals(".xslt"))||
                 (transformerType.toLowerCase().equals("xsl"))||
                 (transformerType.toLowerCase().equals("xslt"))
                )
 			return new XsltTransformer();
 		else if ((transformerType.toLowerCase().equals(".xq"))||
                 (transformerType.toLowerCase().equals(".xql"))||
                 (transformerType.toLowerCase().equals(".xquery"))||
                 (transformerType.toLowerCase().equals("xq"))||
                 (transformerType.toLowerCase().equals("xql"))||
                 (transformerType.toLowerCase().equals("xquery"))
                )
 			return new XQueryTransformer();
 		else if (transformerType.equalsIgnoreCase("XML"))
 			return new MappingTransformer();
		else if (transformerType.equalsIgnoreCase("CSV"))
 			return new Csv2XmlTransformer();
 		else if (transformerType.toUpperCase().contains("HL7"))
 			return new Hl7v2XmlTransformer();
		
		return new MappingTransformer();
	}
}
