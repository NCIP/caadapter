/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.v2v3.tools;

import gov.nih.nci.caadapter.common.ApplicationException;

import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Apr 21, 2009
 * Time: 10:49:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultDataProcessor
{
    private String TAG_SYMBOL = "%%";
    private String DELIMITER = ";";
    private String SEPARATOR = ":";
    String[] NULL_FLAVOR_VALUES = new String[] {"NI", "OTH", "NINF", "PINF", "UNK", "ASKU", "NAV", "NASK", "TRC", "MSK", "NA", "NP"};



    public DefaultDataProcessor()
    {

    }

    public String getDefaultTagSymbol()
    {
        return TAG_SYMBOL;
    }

    public boolean isValidNullFlavorValue(String nullFlavor)
    {
        if (nullFlavor == null) return false;
        for(String nullF:NULL_FLAVOR_VALUES)
        {
            if (nullF.equals(nullFlavor.trim())) return true;
        }
        return false;
    }

    public String processDefaultValueTag(String defValue) throws ApplicationException
    {
        return processDefaultValueTag(defValue, null);
    }
    public String processDefaultValueTag(String defValue, String nodeValue) throws ApplicationException
    {
        String defVal = defValue;
        if (defVal == null) defVal = "";

        if (!defVal.trim().startsWith(TAG_SYMBOL))
        {
            if ((nodeValue == null)||(nodeValue.trim()).equals("")) return defValue;
            return nodeValue;
        }
        //System.out.println("GGGG  : defValue="+ defValue + " nodeValue=" + nodeValue);
        StringTokenizer st = new StringTokenizer(defVal, DELIMITER);

        String nodeVal = nodeValue;
        if (nodeVal == null) nodeVal = "";
        nodeVal = nodeVal.trim();

        String res = "";
        //String message = "";
        boolean wasStringTag = false;
        boolean wasMathTag = false;
        while(st.hasMoreTokens())
        {
            String next = st.nextToken();
            if (next.trim().equals("")) continue;
            if (!next.startsWith(TAG_SYMBOL)) next = TAG_SYMBOL + "when_null:" + next;
            int idx = next.indexOf(SEPARATOR);
            if (idx < 0) continue;
            String key = next.substring(2, idx).trim();
            String val = next.substring(idx+1).trim();
            if (key.equals("")) continue;

            if (val.startsWith("\"")) val = val.substring(1);
            if (val.endsWith("\"")) val = val.substring(0, val.length()-1);
            if (val.equals("")) throw new ApplicationException("Null data for fuctional default value : " + next);

            if ((key.equalsIgnoreCase("prefix"))&&(!nodeVal.equals("")))
            {
                wasStringTag = true;
                if (res.equals("")) res = val + nodeVal;
                else res = val + res;
            }
            if ((key.equalsIgnoreCase("suffix"))&&(!nodeVal.equals("")))
            {
                wasStringTag = true;
                if (res.equals("")) res = nodeVal + val;
                else res = res + val;
            }
            if ((key.equalsIgnoreCase("when_null"))&&(nodeVal.equals(""))) return val;

            if (!res.equals("")) continue;

            if (nodeVal.equals("")) continue;

            double defDbl = 0.0;
            double nodeDbl = 0.0;
            double resultDbl = 0.0;
            try
            {
                defDbl = Double.parseDouble(val);
                //System.out.println("GGGG default value : " + val);
            }
            catch(NumberFormatException ne)
            {
                throw new ApplicationException("Invalid number format data for fuctional default value : " + next);
            }
            try
            {
                nodeDbl = Double.parseDouble(nodeVal);
                //System.out.println("GGGG node value : " + nodeVal);
            }
            catch(NumberFormatException ne)
            {

                throw new ApplicationException("Invalid number format input data for fuctional default process : " + nodeVal);
            }
            boolean isMathTag = true;
            if (key.equalsIgnoreCase("add")) resultDbl = defDbl + nodeDbl;
            else if (key.equalsIgnoreCase("multiply")) resultDbl = defDbl * nodeDbl;
            else if ((key.equalsIgnoreCase("subtract"))||(key.equalsIgnoreCase("subtract_by"))) resultDbl = nodeDbl - defDbl;
            else if (key.equalsIgnoreCase("subtract_from")) resultDbl = defDbl - nodeDbl;
            else if ((key.equalsIgnoreCase("divide"))||(key.equalsIgnoreCase("divide_by")))
            {
                if (defDbl == 0.0) throw new ApplicationException("Divided by zero exception by default value : " + next);
                else resultDbl = nodeDbl / defDbl;
            }
            else if (key.equalsIgnoreCase("divide_from"))
            {
                if (nodeDbl == 0.0) throw new ApplicationException("Divided by zero exception by mapped value ("+next+") : " + nodeVal);
                else resultDbl = defDbl / nodeDbl;
            }
            else if ((key.equalsIgnoreCase("remainder"))||(key.equalsIgnoreCase("remainder_by")))
            {
                if (defDbl == 0.0) resultDbl = nodeDbl;
                else resultDbl = nodeDbl % defDbl;
            }
            else if (key.equalsIgnoreCase("remainder_from"))
            {
                if (nodeDbl == 0.0) resultDbl = defDbl;
                else resultDbl = defDbl % nodeDbl;
            }
            else
            {
                isMathTag = false;
                throw new ApplicationException("Invalid tag for fuctional default process : "+next);
            }
            if (isMathTag)
            {
                if (wasMathTag) throw new ApplicationException("Duplicate Math tag : " + next);
                wasMathTag = true;
                if ((wasMathTag)&&(wasStringTag)) throw new ApplicationException("Math tag and String tag are mixed in these fuctional default tags : " + defValue);
            }
            if (!isMathTag) continue;

            res = String.valueOf(resultDbl);
            int idx1 = res.indexOf(".");
            if (idx1 < 0)
            {
                continue;
            }
            String integerPart = res.substring(0,idx1);
            int realPartValue = 0;
            try
            {
                realPartValue = Integer.parseInt(res.substring(idx1+1));
            }
            catch(NumberFormatException ne)
            {
                return res;
            }
            if (realPartValue == 0) res = integerPart;
            return res;
        }
        if (!res.equals("")) return res;

        return nodeVal;
    }
}
