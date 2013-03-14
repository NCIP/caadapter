/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.ws;

import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;
import gov.nih.nci.cbiit.cdms.formula.core.TermType;
import gov.nih.nci.cbiit.cdms.formula.core.DataElement;
import gov.nih.nci.cbiit.cdms.formula.FormulaFactory;
//import gov.nih.nci.ncicb.cadsr.common.resource.DataElement;

import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Dec 6, 2010
 * Time: 8:57:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class CalculateFormula
{
    public static String execute(String formula, String[] param)
    {
        if ((param == null)||(param.length == 0)) return "No parameter";
        double[] dbls = new double[param.length];
        for (int i=0;i<param.length;i++)
        {
            try
            {
                dbls[i] = Double.parseDouble(param[i]);
            }
            catch(NumberFormatException ne)
            {
                return "parameter is not numeric value : " + param[i];
            }
        }
        return execute(formula, dbls);
    }
    public static String execute(String formula, double[] param)
    {
        List<String> variables = getVariables(formula);
        if (variables == null) return "Invalid formula";
        if (variables.size() == 0) return "no variable";
        if (variables.size() != param.length) return "unmatched variables";

        HashMap<String, String> paramHash=new HashMap<String, String>();

        for (int i=0;i<variables.size();i++)
        {
            paramHash.put(variables.get(i), "" + param[i]);
        }
        return execute(formula, paramHash);
        //return myFormula.getExpression().excute(paramHash);
    }

    public static String execute(String formula, HashMap<String, String> param)
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

        List<String> variables = getVariables(myFormula.getExpression());
        if (variables.size() == 0) return "no variable";
        if (variables.size() > param.size()) return "insufficient parameters";

        HashMap<String, String> paramHash=new HashMap<String, String>();
        Iterator<String> keyset = param.keySet().iterator();
        for(String var:variables)
        {
            String val = param.get(var);
            if ((val == null)||(val.trim().equals("")))
            {
                while(keyset.hasNext())
                {
                    String key = keyset.next();
                    if (key.equalsIgnoreCase(var))
                    {
                        var = key;
                        val = param.get(var);
                        break;
                    }
                }
                if ((val == null)||(val.trim().equals(""))) return "no parameter value for '" + var + "'";
            }

            try
            {
                Double.parseDouble(val);
            }
            catch(NumberFormatException ne)
            {
                return "Invalid parameter value("+val+") for '" + var + "'";
            }
            paramHash.put(var, val);
        }

        return myFormula.getExpression().excute(paramHash);
    }
    public static List<String> getVariables(String formula)
    {
        FormulaMeta myFormula = null;
        try
        {
            myFormula= FormulaFactory.loadFormula(formula);
        }
        catch(JAXBException je)
        {
            System.out.println("Error at getVariables():" + je.getMessage());
            return null;
        }
        List<DataElement> list = myFormula.getParameter();
        List<String> ll = new ArrayList<String>();
        for(DataElement de:list)
        {
            String name = de.getName();
            //System.out.println("   varibale found:" + name);
            ll.add(name);
        }
        return ll;
        //return getVariables(myFormula.getExpression());
    }
    public static List<String> getVariables(TermMeta meta)
    {
        return getVariables(meta, null);
    }
    private static List<String> getVariables(TermMeta meta, List<String> list)
    {
        if (list == null) list = new ArrayList<String>();

        TermType type = meta.getType();
        if (type.equals(TermType.VARIABLE))
        {
            String name = meta.getValue();
            boolean cTag = false;
            for(String nm:list) if (nm.equals(name)) cTag = true;
            if (!cTag) list.add(name);
        }

        if (type.equals(TermType.EXPRESSION))
        {
            List<TermMeta> lt = meta.getTerm();
            for (TermMeta mm:lt) list = getVariables(mm, list);
        }
        return list;
    }
}
