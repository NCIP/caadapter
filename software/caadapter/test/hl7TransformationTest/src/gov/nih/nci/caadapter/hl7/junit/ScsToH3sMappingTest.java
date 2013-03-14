/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
 
package gov.nih.nci.caadapter.hl7.junit;

import static org.junit.Assert.*;
import gov.nih.nci.caadapter.hl7.transformation.MapParser;
import gov.nih.nci.caadapter.hl7.v3csv.TransformationServiceHL7V3ToCsv;
import gov.nih.nci.caadapter.common.csv.CSVMetaParserImpl;
import gov.nih.nci.caadapter.common.csv.CSVMetaResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

import gov.nih.nci.caadapter.hl7.map.TransformationResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import java.io.FileWriter;;
/**
 * @author wangeug
 *
 */
public class ScsToH3sMappingTest extends TestCase {

	/**
	 * Test method for {@link gov.nih.nci.caadapter.hl7.transformation.MapParser#processOpenMapFile(java.io.File)}.
	 */
	@Test
	public void testProcessMappingFile() {
		String mapFileName="C:\\CVS\\caadapter\\components\\hl7Transformation\\test\\data\\COCT_MT150003.map";
		File mapFile=new File(mapFileName);

        MapParser mapParser = new MapParser();
        try {
			Hashtable mappings = mapParser.processOpenMapFile(mapFile);
	        ValidatorResults mappingValidatorResults=mapParser.getValidatorResults();
			assertTrue(mappingValidatorResults.isValid());
			assertTrue(!mappings.isEmpty());
			assertTrue(!mapParser.getFunctions().isEmpty());
			assertNotNull(mapParser.getH3SFilename());
			assertNotSame(mapParser.getH3SFilename(), "");
			assertNotNull(mapParser.getSourceSpecFileName());
			assertNotSame(mapParser.getSourceSpecFileName(), "");
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
