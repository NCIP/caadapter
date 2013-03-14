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

import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.v1.MIFParser;

import java.util.HashSet;

import org.junit.Test;
import junit.framework.*;

/**
 * The class will test the following features of the MIF Parser.
 * 1. MIF attribute parser
 * 2. MIF association parser
 * 3. MIF commonModelElement parser
 * 4. MIF choice parser
 * 5. MIF composite datatype parser
 * 
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision $Revision: 1.2 $ date $Date: 2008-06-09 19:53:51 $
 */

public class MIFTests extends TestCase {
	 /*
	  * Test COCT_MT150003UV03.mif to assure mif attribute parser funcationality is correct
	  */
	 @Test public void testMIFAttribute () {
		 MIFParser mifParser = new MIFParser();
		 MIFClass mifClass = mifParser.loadMIF("COCT_MT150003UV03.mif");
		 assertNotNull(mifClass);
		 HashSet<MIFAttribute> mifAttributes = mifClass.getAttributes();
		 assertTrue(mifAttributes.size()== 5);
		 for(MIFAttribute mifAttribute:mifAttributes) {
			 if (mifAttribute.getName().equals("classCode")) {
				 assertEquals("1", mifAttribute.getSortKey());
				 assertEquals("CNE",mifAttribute.getCodingStrength());
				 assertEquals("R",mifAttribute.getConformance());
				 assertEquals("ORG",mifAttribute.getDefaultValue());
				 assertEquals(1,mifAttribute.getMaximumMultiplicity());
				 assertEquals(1,mifAttribute.getMinimumMultiplicity());
				 assertEquals("ORG",mifAttribute.getMnemonic());
				 assertEquals("CS",mifAttribute.getType());
				 assertEquals("EntityClassOrganization",mifAttribute.getDomainName());
				 assertEquals(true,mifAttribute.isStrutural());
			 }
			 if (mifAttribute.getName().equals("determinerCode")) {
				 assertEquals("INSTANCE",mifAttribute.getFixedValue());
			 }
			 if (mifAttribute.getName().equals("id")) {
				 assertEquals("II",mifAttribute.getType());
				 assertEquals(false,mifAttribute.isStrutural());
			 }
		 }
	 }

	 /*
	  * Test COCT_MT150003UV03.mif to assure mif association parser funcationality is correct
	  */
	 @Test public void testMIFAssociation () {
		 MIFParser mifParser = new MIFParser();
		 MIFClass mifClass = mifParser.loadMIF("COCT_MT150003UV03.mif");
		 assertNotNull(mifClass);
		 HashSet<MIFAssociation> mifAssociations = mifClass.getAssociations();
		 assertTrue(mifAssociations.size()==1);
		 for(MIFAssociation mifAssociation:mifAssociations) {
			 assertEquals("contactParty",mifAssociation.getName());
			 assertEquals(null,mifAssociation.getConformance());
			 assertEquals(1,mifAssociation.getMinimumMultiplicity());
			 assertEquals(-1,mifAssociation.getMaximumMultiplicity());
			 assertEquals("BAAAADAA__",mifAssociation.getSortKey());
			 MIFClass mifClass_A = mifAssociation.getMifClass();
			 assertNotNull(mifClass_A);
			 assertEquals("",mifClass_A.getReferenceName());
		 }
	 }
	 
	 /*
	  * Test COCT_MT040203UV01.mif to assure mif commonModelElementref parser funcationality is correct
	  */
	 @Test public void testCommonModelElementRef () {
		 MIFParser mifParser = new MIFParser();
		 MIFClass mifClass = mifParser.loadMIF("COCT_MT040203UV01.mif");
		 assertNotNull(mifClass);
		 HashSet<MIFAssociation> mifAssociations = mifClass.getAssociations();
		 assertTrue(mifAssociations.size()==1);
		 for(MIFAssociation mifAssociation:mifAssociations) {
			 MIFClass mifClass_A = mifAssociation.getMifClass();
			 assertNotNull(mifClass_A);
			 assertEquals("EntityChoicePlayer",mifClass_A.getName());
			 HashSet<MIFClass> mifClasses_Choice = mifClass_A.getChoices();
			 assertEquals(2,mifClasses_Choice.size());
			 for(MIFClass mifClasses_ch:mifClasses_Choice) {
				 if (mifClasses_ch.getName().equals("E_OrganizationContact")) {
					 assertEquals("E_OrganizationContact",mifClasses_ch.getReferenceName());
				 }
			 }
		 }
	 }
	 

	 /*
	  * Test COCT_MT040203UV01.mif to assure mif choice parser funcationality is correct
	  */
	 @Test public void testChoice () {
		 MIFParser mifParser = new MIFParser();
		 MIFClass mifClass = mifParser.loadMIF("COCT_MT040203UV01.mif");
		 assertNotNull(mifClass);
		 HashSet<MIFAssociation> mifAssociations = mifClass.getAssociations();
		 assertTrue(mifAssociations.size()==1);
		 for(MIFAssociation mifAssociation:mifAssociations) {
			 MIFClass mifClass_A = mifAssociation.getMifClass();
			 assertNotNull(mifClass_A);
			 assertEquals("EntityChoicePlayer",mifClass_A.getName());
			 HashSet<MIFClass> mifClasses_Choice = mifClass_A.getChoices();
			 assertEquals(2,mifClasses_Choice.size());
			 int count = 0;
			 for(MIFClass mifClasses_ch:mifClasses_Choice) {
				 if (mifClasses_ch.getName().equals("E_OrganizationContact")) {
					 count++;
					 assertEquals("E_OrganizationContact",mifClasses_ch.getReferenceName());
				 }
				 if (mifClasses_ch.getName().equals("E_PersonContact")) {
					 count++;
				 }
			 }
			 assertEquals(2,count);
		 }
	 }
	 
	 /*
	  * Test COCT_MT210000UV02.mif to assure mif type parser funcationality is correct
	  */
	 @Test public void testType () {
		 MIFParser mifParser = new MIFParser();
		 MIFClass mifClass = mifParser.loadMIF("COCT_MT210000UV02.mif");
		 assertNotNull(mifClass);

		 HashSet<MIFClass> mifClass_Choices = mifClass.getChoices();
		 assertEquals(4,mifClass_Choices.size());
		 int count = 0;
		 for(MIFClass mifClasses_ch:mifClass_Choices) {
			 if (mifClasses_ch.getName().equals("Dillution")) {
				 count = 1;
				 HashSet<MIFAttribute> mifAttributes = mifClasses_ch.getAttributes();
				 assertTrue(mifAttributes.size()== 4);
				 for(MIFAttribute mifAttribute:mifAttributes) {
					 if (mifAttribute.getName().equals("value")) {
						 assertEquals(mifAttribute.getType(),"RTO_QTY_QTY");
					 }
				 }
			 }
		 }
		 assertEquals(1,count);
	 }

	 
	 public static junit.framework.Test suite() {
		  return new JUnit4TestAdapter(MIFTests.class);    
		}
}
