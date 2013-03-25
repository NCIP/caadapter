/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui.tree;

import gov.nih.nci.cbiit.cdms.formula.core.DataElement;
import gov.nih.nci.cbiit.cdms.formula.core.DataElementUsageType;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaStore;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import javax.swing.tree.DefaultMutableTreeNode;

public class TreeNodeFormulaStore  extends DefaultMutableTreeNode {

	public TreeNodeFormulaStore (FormulaStore store)
	{
		super(store);
		if (store==null)
		{
			//create a dummy FormulaStore object and set
			//it as tree root node
			store=new FormulaStore();
			store.setName("No Formula Defined");
			setUserObject(store);

		}
		if (store.getFormula()!=null)
		{
			for (FormulaMeta formula:store.getFormula())
				addFormulaNode(formula);
		}
	}
	
	private void addFormulaNode(FormulaMeta formula)
	{
		DefaultMutableTreeNode formulaNode=new DefaultMutableTreeNode(formula);
        if (formula.getParameter()!=null)
        {
        	//add transformation data element
            for (DataElement parameter:formula.getParameter())
            {
            	if (parameter.getUsage().equals(DataElementUsageType.TRANSFORMATION))
            		formulaNode.add(new DefaultMutableTreeNode(parameter));
            }
            
            //add parameter data element
            for (DataElement parameter:formula.getParameter())
            {
            	if (parameter.getUsage().equals(DataElementUsageType.TRANSFORMATION))
      				continue;
                formulaNode.add(new DefaultMutableTreeNode(parameter));
            }
        }
		add(formulaNode);
	}
}
