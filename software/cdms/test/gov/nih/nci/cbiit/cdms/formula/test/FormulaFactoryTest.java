/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.bind.JAXBException;

import gov.nih.nci.cbiit.cdms.formula.FormulaFactory;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaStore;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaType;
import gov.nih.nci.cbiit.cdms.formula.core.OperationType;
import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;
import gov.nih.nci.cbiit.cdms.formula.core.TermType;
import org.junit.Test;

public class FormulaFactoryTest {
	
	

	/**
	 * test loading formula store
	 * @throws JAXBException 
	 */
	@Test
	public void testFormulaStoreLoading() throws JAXBException
	{
		String testFile="dataStore/commonFormulae.xml";
		FormulaStore myStore=FormulaFactory.loadFormulaStore(new File(testFile));
		for (FormulaMeta formula:myStore.getFormula())
		{
			System.out.println("FormulaFactoryTest.testLoading()..:\n"+formula.toString());
			System.out.println("FormulaFactoryTest.testLoading().. java Statement:\n"+formula.formatJavaStatement());
			System.out.println("FormulaFactoryTest.testLoading().. XML:\n"+FormulaFactory.convertFormulaToXml(formula));
		}
	}
	
	/**
	 * test XSD parsing and marshaling of the generated Model Object
	 * @throws JAXBException 
	 */
	@Test
	public void testExcute() throws JAXBException
	{
		HashMap<String, String> paramHash=new HashMap<String, String>();
		String testFile="workingspace/BSA8.xml";
		FormulaStore myStore=FormulaFactory.getCommonStore();
		FormulaMeta formula=myStore.getFormula().get(0);
		System.out.println("FormulaFactoryTest.testExcute()...paramter:"+formula.getParameter());
		paramHash.put("annualRate", "6");
		paramHash.put("principal", "100000");
		paramHash.put("NoOfMonths", "360");
		
		System.out.println("FormulaFactoryTest.testExcute()...result:"+formula.getExpression().excute(paramHash));	

	}
	
	/**
	 * test XSD parsing and marshaling of the generated Model Object
	 * @throws JAXBException 
	 */
	@Test
	public void testSaving() throws JAXBException
	{
		String outFile="workingspace/savedExample.xml";
		
		FormulaMeta formula=createTestingFormula();
		try {
			FormulaFactory.saveFormula(formula, new File(outFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private FormulaMeta createTestingFormula()
	{
		
		FormulaMeta formula=new FormulaMeta();
		formula.setName("testForula");
		formula.setType(FormulaType.MATH);
		formula.setAnnotation("This is a formula created from scratch..");
		
		//the following create formula expression
		TermMeta formulaExpression=new TermMeta();
		formulaExpression.setName("testExpression");
		formulaExpression.setType(TermType.EXPRESSION);
		formulaExpression.setOperation(OperationType.ADDITION);
		formulaExpression.setTerm(new ArrayList<TermMeta>());
		formula.setExpression(formulaExpression);
		formula.setDateModified(new Date());
		
		//the following add terms to formula expression
		TermMeta addend1=new TermMeta();
		addend1.setName("addend");
		addend1.setType(TermType.CONSTANT);
		addend1.setValue("2345");
		
		TermMeta addend2=new TermMeta();
		addend2.setName("addend");
		addend2.setType(TermType.VARIABLE);
		addend2.setValue("xyz");
		
		formulaExpression.getTerm().add(addend1);
		formulaExpression.getTerm().add(addend2);
		return formula;
	}
}
