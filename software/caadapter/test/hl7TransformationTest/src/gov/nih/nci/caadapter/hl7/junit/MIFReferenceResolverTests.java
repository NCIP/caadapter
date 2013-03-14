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

import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFIndex;
import gov.nih.nci.caadapter.hl7.mif.MIFIndexParser;
import gov.nih.nci.caadapter.hl7.mif.MIFReferenceResolver;
import gov.nih.nci.caadapter.hl7.mif.MIFToXmlExporter;
import gov.nih.nci.caadapter.hl7.mif.v1.MIFParser;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;

public class MIFReferenceResolverTests extends TestCase {

	
	/**
	 * As an assoication MIFClass has a choice group, it is required to
	 * set traversal name for each choice element. If the MIFClass is a 
	 * reference defined previously, all the choice elements should be
	 * cloned and set with correct traversal name
	 *
	 */
	public void testResolveChoiceReferences()
	{
  		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
  		DocumentBuilder db;
  		String mifFileName="COCT_MT010000UV01.mif";
  		String outFileName="resolved"+mifFileName;
  		outFileName.replace(".mif", ".xml");
		try {
			db = dbf.newDocumentBuilder();
			Document mifDoc = db.parse("T:/YeWu/Edition2006/mif/"+mifFileName);
	   	  	MIFParser mifParser = new MIFParser();
	        mifParser.parse(mifDoc);
	        MIFClass parseredMif= mifParser.getMIFClass();
	        //resolve the internal reference
			
	        MIFReferenceResolver refResolver=new MIFReferenceResolver();
			refResolver.getReferenceResolved(parseredMif);
//	        parseredMif.printMIFClass(0, new HashSet());
	        MIFToXmlExporter xmlExporter;
			xmlExporter = new MIFToXmlExporter(parseredMif);
			xmlExporter.exportToFile(outFileName);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
//          	  mifParser.saveMIFs("C:/temp/serializedMIF/resource/mif/" + filename,msgType);

	}
	 public static junit.framework.Test suite() {
		  return new JUnit4TestAdapter(MIFReferenceResolverTests.class);    
		}
}
