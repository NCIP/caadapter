/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.test;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xquery.XQException;

import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.mapping.MappingFactory;
import gov.nih.nci.cbiit.cmts.transform.XsltTransformer;
import gov.nih.nci.cbiit.cmts.transform.artifact.StylesheetBuilder;
import gov.nih.nci.cbiit.cmts.transform.artifact.XSLTCallTemplate;
import gov.nih.nci.cbiit.cmts.transform.artifact.XSLTStylesheet;
import gov.nih.nci.cbiit.cmts.transform.artifact.XSLTTemplate;

import org.junit.Test;

public class XSLTTransformerTest {

	@Test
	public void testStlesheetBuilder() throws JAXBException, IOException
	{
		String mappingFile="workingspace/simplemapping/nfmapping.map";
		Mapping map=MappingFactory.loadMapping(new File(mappingFile));
		StylesheetBuilder xsltBuilder = new StylesheetBuilder(map);
		XSLTStylesheet xsltSheet = xsltBuilder.buildStyleSheet();
		Writer out = new BufferedWriter(new OutputStreamWriter(System.out));	 
		xsltSheet.writeOut(out);
		out.close();
		Writer fileOut=new FileWriter(new File("workingspace/simplemapping/test_g.xsl"));
		xsltSheet.writeOut(fileOut);
		fileOut.close();
	}

	@Test
	public void testXsltTransformation() 
	{
		 String inXML ="workingspace/simpleMapping/shiporder.xml";// arg[0];
		  String inXSL ="workingspace/simpleMapping/test_g.xsl"; //arg[1];
		  String outTXT ="workingspace/simpleMapping/xsltOut.xml";// arg[2];


		  try {
			  XsltTransformer st = new XsltTransformer();
			  st.transform(inXML,inXSL,outTXT);
			  System.out.println("XSLTTransformerTest.testXsltTransformation()..:\n"+st.transfer(inXML, inXSL));
		  } catch(TransformerConfigurationException e) {
			  System.err.println("Invalid factory configuration");
			  System.err.println(e);
		  } catch(TransformerException e) {
			  System.err.println("Error during transformation");
			  System.err.println(e);
		  } catch (XQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@Test
	public void testXsltTempate()
	{
		XSLTStylesheet stylesheet=new XSLTStylesheet();
		XSLTTemplate xlstTemplate=new XSLTTemplate();
		xlstTemplate.setMatch("/");
		stylesheet.addTempate(xlstTemplate);
		XSLTTemplate calledTemp=new XSLTTemplate();
		stylesheet.addTempate(calledTemp);
		calledTemp.setTemplatename("toBecalled");
		XSLTCallTemplate callTemp=new XSLTCallTemplate();
		callTemp.setCalledTemplate(calledTemp.getTemplatename());
		xlstTemplate.addContent(callTemp);
		
		File outFile = new File("testOut"); 
		try {
			FileWriter out = new FileWriter(outFile);
			stylesheet.writeOut(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
