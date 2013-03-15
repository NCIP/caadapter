/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.hl7.junit;

import static org.junit.Assert.*;

import gov.nih.nci.caadapter.hl7.map.TransformationResult;
import gov.nih.nci.caadapter.hl7.map.TransformationServiceHL7V3ToCsv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import java.io.FileWriter;;
/**
 * @author wangeug
 *
 */
public class Hl7V3ToCsvTransformationTest extends TestCase {


	/**
	 * Test method for {@link gov.nih.nci.caadapter.hl7.map.TransformationServiceHL7V3ToCsv#TransformationServiceHL7V3ToCsv(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testTransformationServiceHL7V3ToCsvStringString() {
//		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link gov.nih.nci.caadapter.hl7.map.TransformationServiceHL7V3ToCsv#TransformationServiceHL7V3ToCsv(java.io.File, java.io.File)}.
	 */
	@Test
	public void testTransformationServiceHL7V3ToCsvFileFile() {
		String mapFileName="C:\\CVS\\caadapter\\components\\hl7Transformation\\test\\data\\COCT_MT150003.map";
		String srcFileName="C:\\CVS\\caadapter\\components\\hl7Transformation\\test\\data\\COCT_MT150003_1.xml";

		File mapFile=new File(mapFileName);
		File srcFile=new File(srcFileName);

		String csvOut=srcFile.getParent()+File.separator+"transferOut.csv";
		TransformationServiceHL7V3ToCsv ts=new TransformationServiceHL7V3ToCsv(srcFile,mapFile);
		List csvList=ts.process();
		assertNotNull(csvList);
		assertNotNull(csvList.get(0));
		TransformationResult tsResult=(TransformationResult)csvList.get(0);
		assertNotNull(tsResult);
		assertTrue(tsResult.getValidatorResults().isValid());
		assertNotNull(tsResult.getMessageText());
		assertNotSame(tsResult.getMessageText(), "");
		try {
			FileWriter fw= new FileWriter(csvOut);
			fw.write(tsResult.getMessageText());
			fw.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
