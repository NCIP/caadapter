/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.ws;

import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.FormulaFactory;

import javax.xml.bind.JAXBException;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Dec 2, 2010
 * Time: 10:15:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class FormulaCalculatingService
{
    public String executeCalculate(String formula, HashMap<String, String> paramHash)
    {
        FormulaMeta myFormula = null;
        try
        {
            myFormula= FormulaFactory.loadFormula(formula);
        }
        catch(JAXBException je)
        {
            return je.getMessage();
        }

        if (paramHash == null) return "no variable";

        return myFormula.getExpression().excute(paramHash);
    }
}
