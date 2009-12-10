/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.common.function;


import java.math.BigDecimal;

/**
 * Perfoms Math Functions.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-09-25 18:57:45 $
 */

public class MathFunction {

    public double add(double a1, double a2) {
        return a1 + a2;
    }

    public double subtract(double a1, double a2) {
        return a1 - a2;
    }

    public double multiply(double a1, double a2) {
        return a1 * a2;
    }

    public double divide(double numerator, double denominator) throws FunctionException {
        if (denominator == 0.0)
            throw new FunctionException("Cannot divide by zero [" + numerator + "," + denominator + "]");
        return numerator / denominator;
    }

    public double round(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(d);
        bd = bd.setScale(decimalPlace,BigDecimal.ROUND_HALF_DOWN);
        d = bd.doubleValue();
        //System.out.println(d);
        return d;

    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
