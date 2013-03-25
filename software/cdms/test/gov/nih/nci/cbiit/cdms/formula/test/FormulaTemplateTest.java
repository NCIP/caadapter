/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.test;

import java.util.Date;

import javax.xml.bind.JAXBException;

import gov.nih.nci.cbiit.cdms.formula.FormulaFactory;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaType;
import gov.nih.nci.cbiit.cdms.formula.core.OperationType;
import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;
import gov.nih.nci.cbiit.cdms.formula.core.TermType;
import org.junit.Test;

public class FormulaTemplateTest {
	
	
	/**
	 * test XSD parsing and marshaling of the generated Model Object
	 * @throws JAXBException 
	 */
	@Test
	public void testTempate() throws JAXBException
	{
		TermMeta testTerm=FormulaFactory.createTemplateTerm(OperationType.ADDITION);
		TermMeta testTerm2=FormulaFactory.createTemplateTerm(OperationType.ADDITION);
		System.out.println("FormulaTemplateTest.testTempate()..addion:"+testTerm);
	}
	
	@Test
	public void testCreateFormula()
	{		
		FormulaMeta formula=new FormulaMeta();
		formula.setName("testForula");
		formula.setType(FormulaType.MATH);
		formula.setAnnotation("This is a formula created from scratch..");
		
		//the following create formula expression
		TermMeta formulaExpression=FormulaFactory.createTemplateTerm(OperationType.ADDITION);
		formulaExpression.setName("testExpression");
		formula.setExpression(formulaExpression);
		formula.setDateModified(new Date());
		
		
		//the following add terms to formula expression
		TermMeta addend1=formula.getExpression().getTerm().get(0);
		addend1.setType(TermType.CONSTANT);
		addend1.setValue("2345");
		
		TermMeta addend2=formula.getExpression().getTerm().get(1);
		addend2.setType(TermType.VARIABLE);
		addend2.setValue("xyz");
		
		formulaExpression.getTerm().add(addend1);
		formulaExpression.getTerm().add(addend2);
		System.out.println("FormulaTemplateTest.testCreateFormula()...formula:\n"+formula);
	}
}
