/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.ws;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Dec 6, 2010
 * Time: 8:56:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class CalculateOnWSDL
{
    public String execute(String formula, HashMap<String, String> param)
    {
        String res = CalculateFormula.execute(formula, param);
        if ((res == null)||(res.trim().equals(""))) return null;
        double rr = 0.0;
        try
        {
            rr = Double.parseDouble(res);
        }
        catch(NumberFormatException ne)
        {
            return null;
        }
        return "" + rr;
    }

    public String execute(String formula, String[] paramNames, String[] values)
    {
        if (paramNames.length != values.length) return null;
        HashMap<String, String> paramHash=new HashMap<String, String>();
        for(int i=0;i<paramNames.length;i++)
        {
            paramHash.put(paramNames[i], values[i]);
        }
        return execute(formula, paramHash);
    }
}