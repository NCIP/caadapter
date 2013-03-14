/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.transform.csv;

import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.csv.CSVMetaResult;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVFieldMetaImpl;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVMetaImpl;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVSegmentMetaImpl;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.cbiit.cmts.core.AttributeMeta;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;

/**
 * convert an element meta into a CsvMetadata
 * 
 * @author wangeug
 * 
 */
public class CsvXsd2MetadataConverter {

	private CSVMetaResult csvMetaResult;

	public CsvXsd2MetadataConverter(ElementMeta elementMeta) {
		csvMetaResult = processElementMeta(elementMeta);
	}

	private CSVMetaResult processElementMeta(ElementMeta elementMeta) {
		CSVMetaResult metaResult = new CSVMetaResult();
		ValidatorResults validatorResults = new ValidatorResults();
		if (elementMeta == null) {
			Message msg = MessageResources.getMessage("GEN0",
					new Object[] { "ElementMeta is NULL." });
			validatorResults.addValidatorResult(new ValidatorResult(
					ValidatorResult.Level.FATAL, msg));
		}
		CSVMetaImpl csvFileMetaImpl = new CSVMetaImpl();
		csvFileMetaImpl.setRootSegment(convertElemntMetaToSegment(elementMeta,
				null));
		csvFileMetaImpl.setXmlPath("csvMetaData");
		metaResult.setCsvMeta(csvFileMetaImpl);
		metaResult.setValidatorResults(validatorResults);
		return metaResult;
	}

	private CSVSegmentMetaImpl convertElemntMetaToSegment(
			ElementMeta elementMeta, CSVSegmentMeta parent) {
		CSVSegmentMetaImpl csvSegmentMetaImpl = new CSVSegmentMetaImpl(
				elementMeta.getName(), parent);
		if (parent != null) {
			csvSegmentMetaImpl.setXmlPath(parent.getXmlPath() + "."
					+ elementMeta.getName());
		} else
			csvSegmentMetaImpl.setXmlPath(elementMeta.getName());

		// 1...1
		CardinalityType cardType = CardinalityType.VALUE_1;

		int upBound = 1;
		if (elementMeta.getMaxOccurs() != null) {
			if (elementMeta.getMaxOccurs().intValue() != -1)
				upBound = elementMeta.getMaxOccurs().intValue();
			else
				upBound = 0;
		}

		if (elementMeta.getMinOccurs() != null)
			if (elementMeta.getMinOccurs().intValue() == 0) {
				if (upBound == 1) // 0...1
					cardType = CardinalityType.VALUE_0;
				else
					// 0...*
					cardType = CardinalityType.VALUE_2;
			} else if (elementMeta.getMinOccurs().intValue() == 1) {
				if (upBound == 1) // 1..1
					cardType = CardinalityType.VALUE_1;
				else
					// 1...*
					cardType = CardinalityType.VALUE_3;
			}
		csvSegmentMetaImpl.setCardinalityType(cardType);

		// process child ElementMeta
		for (ElementMeta childElementMeta : elementMeta.getChildElement()) {
			CSVSegmentMetaImpl childCsvSeg = convertElemntMetaToSegment(
					childElementMeta, csvSegmentMetaImpl);
			csvSegmentMetaImpl.addSegment(childCsvSeg);
		}

		// process attribute
		//the first CSV field (index as "0" ) is name of the CSVSegment
		int csvFieldIndx = 1;
		for (AttributeMeta attrMeta : elementMeta.getAttrData()) {
			CSVFieldMetaImpl csvField = new CSVFieldMetaImpl(csvFieldIndx++,
					attrMeta.getName(), csvSegmentMetaImpl);
			csvField.setXmlPath(csvSegmentMetaImpl.getXmlPath() + "."
					+ attrMeta.getName());
			csvSegmentMetaImpl.addField(csvField);
		}
		return csvSegmentMetaImpl;
	}

	public CSVMeta getCSVMeta() {
		if (csvMetaResult != null)
			return csvMetaResult.getCsvMeta();
		return null;
	}
}
