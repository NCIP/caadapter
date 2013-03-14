/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.core;


import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "store", propOrder = {
	"formula"
})
public class FormulaStore extends BaseMeta {

	private List<FormulaMeta> formula;
	
	public List<FormulaMeta> getFormula() {
		return formula;
	}

	public void setFormula(List<FormulaMeta> formula) {
		this.formula = formula;
	}

	@Override
	public String formatJavaStatement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Formula Store Properties";
	}

	
}
