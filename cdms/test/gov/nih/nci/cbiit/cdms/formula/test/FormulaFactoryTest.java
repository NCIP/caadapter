package gov.nih.nci.cbiit.cdms.formula.test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.JAXBException;

import gov.nih.nci.cbiit.cdms.formula.FormulaFactory;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaType;
import gov.nih.nci.cbiit.cdms.formula.core.OperationType;
import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;
import gov.nih.nci.cbiit.cdms.formula.core.TermType;
import org.junit.Test;

public class FormulaFactoryTest {
	
	/**
	 * test XSD parsing and marshaling of the generated Model Object
	 * @throws JAXBException 
	 */
	@Test
	public void testExcute() throws JAXBException
	{
		HashMap<String, String> paramHash=new HashMap<String, String>();
		String testFile="workingspace/BSA8.xml";
		FormulaMeta myFormula=FormulaFactory.loadFormula(new File(testFile));
		System.out.println("FormulaFactoryTest.testExcute()...formula:"+myFormula);
		System.out.println("FormulaFactoryTest.testExcute()...javaSt:"+myFormula.formatJavaStatement());
		System.out.println("FormulaFactoryTest.testExcute()...paramter:"+myFormula.getExpression().listParameters());
		paramHash.put("weight", "5");
		paramHash.put("height", "3");		
		
		System.out.println("FormulaFactoryTest.testExcute()...result:"+myFormula.getExpression().excute(paramHash));	

	}
	

	/**
	 * test XSD parsing and marshaling of the generated Model Object
	 * @throws JAXBException 
	 */
	@Test
	public void testLoading() throws JAXBException
	{
		String testFile="workingspace/logFormula.xml";
		FormulaMeta myFormula=FormulaFactory.loadFormula(new File(testFile));
		System.out.println("FormulaFactoryTest.testLoading()..:"+myFormula.toString());
		System.out.println("FormulaFactoryTest.testLoading().. java Statement:"+myFormula.formatJavaStatement());
	}
	
	/**
	 * test XSD parsing and marshaling of the generated Model Object
	 * @throws JAXBException 
	 */
	@Test
	public void testSaving() throws JAXBException
	{
		String outFile="workingspace/savedExample.xml";
		

		FormulaMeta myFormula=new FormulaMeta();
		myFormula.setName("testForula");
		myFormula.setType(FormulaType.MATH);
		myFormula.setAnnotation("This is a formula created from scratch..");
		
		//the following create formula expression
		TermMeta formulaExpression=new TermMeta();
		formulaExpression.setName("testExpression");
		formulaExpression.setType(TermType.EXPRESSION);
		formulaExpression.setOperation(OperationType.ADDITION);
		formulaExpression.setTerm(new ArrayList<TermMeta>());
		myFormula.setExpression(formulaExpression);
		
		
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
		
		FormulaFactory.saveFormula(myFormula, new File(outFile));
	}
}
