/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.validation;

import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;
import gov.nih.nci.cbiit.cdms.formula.core.TermType;

import java.util.ArrayList;
import java.util.List;

public class FormulaValidator {
	private ArrayList messageList ;
	
	public static List <String>validateFormula(FormulaMeta formula)
	{
		ArrayList<String> rtnMsg=new ArrayList<String>();
		if (formula==null)
			return rtnMsg;
		String path=formula.getName();
		TermMeta expression=formula.getExpression();
		if (expression==null)
			rtnMsg.add(path+":no expression defined");
		else
			rtnMsg.addAll(validateTerm(expression, path));
			
		return rtnMsg;
	}
	
	private static List<String> validateTerm(TermMeta term, String path)
	{
		ArrayList<String> rtnMsg=new ArrayList<String>();
		if (term==null)
			return rtnMsg;
//		path=path+"/"+term.getName();
		if (term.getType().equals(TermType.UNKNOWN))
			rtnMsg.add(path+":invalid term type--"+term.getType());
		if (term.getTerm()==null)
			return rtnMsg;
		
		for (TermMeta childTerm:term.getTerm())
			rtnMsg.addAll(validateTerm(childTerm, path+"/"+childTerm.getName()));
		return rtnMsg;
	}
}
