/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.common.function;


import gov.nih.nci.caadapter.castor.function.impl.C_dataSpec;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

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
    public double remainder(double d, double divisor) throws FunctionException {
        if (divisor == 0.0)
            throw new FunctionException("Cannot divide by zero [remainder]");
        return d % divisor;

    }

    public double compute(String d, String str) throws FunctionException
    {
        return compute(d, null, str);
    }
    public double compute(String d1, String d2, String str) throws FunctionException
    {
        if (d1 == null) d1 = "NULL";
        else d1 = d1.trim();

        if ((str == null)||(str.trim().equals(""))) throw new FunctionException("Expression for computing is null.");
        str = str.trim();

        double p1;
        try
        {
            p1 = Double.parseDouble(d1);
        }
        catch(NumberFormatException ne)
        {
            throw new FunctionException("parameter 1 '" + d1 + "' is not matched value for computing.");
        }
        double p2 = 0.0;
        if (d2 != null)
        {
            try
            {
                p2 = Double.parseDouble(d2);
            }
            catch(NumberFormatException ne)
            {
                throw new FunctionException("parameter 2 '" + d2 + "' is not matched value for computing.");
            }
        }
        if (str.indexOf("##1") < 0) throw new FunctionException("parameter 1 is not used in this expression. : " + str);
        if ((d2 != null)&&(str.indexOf("##2") < 0)) throw new FunctionException("parameter 2 is not used in this expression. : " + str);
        if ((d2 == null)&&(str.indexOf("##2") >= 0)) throw new FunctionException("parameter 2 '" + d2 + "' is not matched value for computing.");

        return computeExe(p1, p2, str);
    }
    private double computeExe(double p1, double p2, String str) throws FunctionException
    {
        String str1 = str.trim();
        if (str1.equals("")) throw new FunctionException("Invalid expression (empty expression) : " + str);

        try
        {
            return Double.parseDouble(str1);
        }
        catch(NumberFormatException ne)
        {}

        String line = "";
//        String word = "";
//        boolean isOpenParentheses = false;
//        boolean isStartFunction = false;

        String beforeChr = null;
        while(!str1.trim().equals(""))
        {
            if (str1.startsWith("##1"))
            {
                line = line + " " + p1;
                str1 = str1.substring(3);
                beforeChr = "1";
                continue;
            }
            if (str1.startsWith("##2"))
            {
                line = line + " " + p2;
                str1 = str1.substring(3);
                beforeChr = "2";
                continue;
            }

            String achar = str1.substring(0, 1);
            str1 = str1.substring(1);

//            if (isStartFunction)
//            {
//                if (achar.equals("("))
//                {
//                    isStartFunction = false;
//                    word = "";
//                }
//                else if (achar.equals(" ")) {}
//                else word = word + achar;
//                continue;
//            }

            char ch = achar.toCharArray()[0];
            byte bb = (byte) ch;
            int ii = (int) bb;

            if (((ii >= 48)&&(ii <= 57))||(ii == 46)||(ii == 32))
            {
                line = line + achar;
                if (!achar.equals(" ")) beforeChr = achar;
                continue;
            }
            if ((achar.equals("+"))||(achar.equals("-"))||(achar.equals("*"))||(achar.equals("/"))||(achar.equals("%")))
            {
                boolean isOperator = true;
                if ((achar.equals("+"))||(achar.equals("-")))
                {
                    String nextChar = str1.substring(0, 1);
                    char ch2 = nextChar.toCharArray()[0];
                    byte bb2 = (byte) ch2;
                    int ii2 = (int) bb2;
                    if ((beforeChr == null)||(beforeChr.equals(""))||(beforeChr.equals("("))||
                        (beforeChr.equals("+"))||(beforeChr.equals("-"))||(beforeChr.equals("*"))||(beforeChr.equals("/"))||(beforeChr.equals("%")))
                    {
                        if (((ii2 >= 48)&&(ii2 <= 57))||(ii2 == 46)) isOperator = false;
                        else throw new FunctionException("Invalid expression (invalid '+' or '-' sign) : " + str);
                    }
                }
                String ad = "";
                if (isOperator) ad = achar;
                else
                {
                    if (achar.equals("+")) ad = ":";
                    if (achar.equals("-")) ad = ";";
                }

                line = line + ad;
                beforeChr = ad;
                continue;

            }

            if (achar.equals(")")) throw new FunctionException("Invalid expression (not opened parentheses) : " + str);

            if (achar.equals("("))
            {
                int n = -1;
                int count = 0;
                for (int i=0;i<str1.length();i++)
                {
                    String ach = str1.substring(i, i+1);
                    if (ach.equals("(")) count++;
                    if (ach.equals(")"))
                    {
                        if (count != 0) count--;
                        else
                        {
                            n = i;
                            break;
                        }
                    }
                }
                if (n < 0) throw new FunctionException("Invalid expression (not closed parentheses) : " + str);
                String ssr = str1.substring(0, n).trim();
                //if (ssr.endsWith(")")) ssr = ssr.substring(0, ssr.length()-1);
                str1 = str1.substring(n);
                if (str1.startsWith(")")) str1 = str1.substring(1);
                line = line + " " + computeExe(p1, p2, ssr);
                beforeChr = ")";
                continue;
            }

            if (((ii >= 65)&&(ii <= 90))||((ii >= 97)&&(ii <= 122)))
            {
                int idx = str1.indexOf("(");
                if ((idx < 0)||(idx > 10)) throw new FunctionException("Invalid function (parentheses not opened or too long) : " + str);

                int n = -1;
                int count = 0;
                for (int i=idx+1;i<str1.length();i++)
                {
                    String ach = str1.substring(i, i+1);
                    if (ach.equals("(")) count++;
                    if (ach.equals(")"))
                    {
                        if (count != 0) count--;
                        else
                        {
                            n = i;
                            break;
                        }
                    }
                }
                if (n < 0) throw new FunctionException("Invalid function (not closed parentheses) : " + str);
                if (n < idx) throw new FunctionException("Invalid function (unmatched parentheses) : " + str);
                String ssr = str1.substring(idx+1, n).trim();
                String fName = achar + str1.substring(0, idx).trim();
                if (fName.indexOf(" ") > 0) throw new FunctionException("Invalid function (invalid funtion name) : " + str);
                //if (ssr.endsWith(")")) ssr = ssr.substring(0, ssr.length()-1);

                str1 = str1.substring(n);
                if (str1.startsWith(")")) str1 = str1.substring(1);
                line = line + " " + computeFunction(p1, p2, fName, ssr);
                beforeChr = ")";
                continue;
            }

            throw new FunctionException("Invalid expression (unrecognized character '" +achar+"') : " + str);
        }

        try
        {
            String tt = line.trim();
            if (tt.startsWith(":")) tt = "+" + tt.substring(1);
            if (tt.startsWith(";")) tt = "-" + tt.substring(1);
            return Double.parseDouble(tt);
        }
        catch(NumberFormatException ne)
        {}

        String s1 = "";
        List<Object> list = new ArrayList<Object>();
        Object formerObj = null;
        beforeChr = null;
        line = line + " ";
        for(int i=0;i<line.length();i++)
        {
            String achar = line.substring(i, i+1);

            if ((achar.equals("+"))||(achar.equals("-"))||(achar.equals("*"))||(achar.equals("/"))||(achar.equals("%")))
            {
                if ((achar.equals("+"))||(achar.equals("-")))
                {
                    if ((beforeChr == null)||(beforeChr.equals(""))||(beforeChr.equals("("))||
                        (beforeChr.equals("+"))||(beforeChr.equals("-"))||(beforeChr.equals("*"))||(beforeChr.equals("/"))||(beforeChr.equals("%")))
                    {
                        if ((i+2) >= line.length()) throw new FunctionException("Invalid expression (ended with '+' or '-' sign) : " + str);
                        String nextChar = line.substring(i+1 , i+2);
                        char ch2 = nextChar.toCharArray()[0];
                        byte bb2 = (byte) ch2;
                        int ii2 = (int) bb2;
                        if (((ii2 >= 48)&&(ii2 <= 57))||(ii2 == 46))
                        {
                            s1 = s1 + achar;
                            beforeChr = achar;
                            continue;
                        }
                        else throw new FunctionException("Invalid expression (invalid '+' or '-' sign)2 : " + str);
                    }
                }
                if (!s1.trim().equals(""))
                {
                    Double dd = null;
                    try
                    {
                        String tt = s1.trim();
                        if (tt.startsWith(":")) tt = "+" + tt.substring(1);
                        if (tt.startsWith(";")) tt = "-" + tt.substring(1);
                        double d = Double.parseDouble(tt);
                        dd = new Double(d);
                    }
                    catch(NumberFormatException ne)
                    {
                        throw new FunctionException("Invalid expression (one or more operands are not double value.) : " + str);
                    }
                    if (!((formerObj == null)||(formerObj instanceof String))) throw new FunctionException("Invalid expression (duplicate operand) : " + str);
                    list.add(dd);
                    formerObj = dd;
                    s1 = "";
                }

                if (formerObj == null) throw new FunctionException("Invalid expression (Null operand) : " + str);
                if (!(formerObj instanceof Double)) throw new FunctionException("Invalid expression (duplicate operator) : " + str);
                list.add(achar);
                formerObj = achar;
            }
            else if (achar.equals(" "))
            {
                if (s1.trim().equals("")) continue;
                Double dd = null;
                try
                {
                    String tt = s1.trim();
                    if (tt.startsWith(":")) tt = "+" + tt.substring(1);
                    if (tt.startsWith(";")) tt = "-" + tt.substring(1);
                    double d = Double.parseDouble(tt);
                    dd = new Double(d);
                }
                catch(NumberFormatException ne)
                {
                    throw new FunctionException("Invalid expression (one or more operands are not double value.)2 : " + str);
                }
                if (!((formerObj == null)||(formerObj instanceof String))) throw new FunctionException("Invalid expression (duplicate operand)2 : " + str);
                list.add(dd);
                formerObj = dd;
                s1 = "";
            }
            else s1 = s1 + achar;

            if (!achar.equals(" ")) beforeChr = achar;
        }

        if (list.size() == 0) throw new FunctionException("Invalid expression (Null value after parsing) : " + str);

        boolean isSecond = false;
        while(list.size() != 1)
        {
            //if (list.size() == 1) break;
            List<Object> list2 = new ArrayList<Object>();
            formerObj = null;
            for(int i=0;i<list.size();i++)
            {
                Object obj = list.get(i);
                if (obj instanceof Double)
                {
                    if (formerObj != null) throw new FunctionException("Invalid expression (Invalid operand array1) : " + str);
                    formerObj = obj;
                }
                else if (obj instanceof String)
                {
                    if (formerObj == null) throw new FunctionException("Invalid expression (Invalid operand array2) : " + str);
                    String operator = (String) obj;
                    if ((operator.equals("*"))||(operator.equals("/"))||(operator.equals("%")))
                    {
                        i++;
                        if (i >= list.size()) throw new FunctionException("Invalid expression array (overflow) : " + str);
                        Object obj2 = list.get(i);
                        if (!(obj2 instanceof Double)) throw new FunctionException("Invalid expression array (duplicated operator) : " + str);
                        double d1 = (Double) formerObj;
                        double d2 = (Double) obj2;
                        double res = 0.0;
                        if (operator.equals("*")) res = d1 * d2;
                        else if (operator.equals("/"))
                        {
                            if (d2 == 0.0) throw new FunctionException("zero dividing ("+d1+"/"+d2+") : " + str);
                            res = d1 / d2;
                        }
                        else if (operator.equals("%"))
                        {
                            if (d2 == 0.0) throw new FunctionException("zero modulus ("+d1+"%"+d2+") : " + str);
                            res = d1 % d2;
                        }

                        formerObj = res;
                    }
                    else
                    {
                        if (isSecond)
                        {
                            i++;
                            if (i >= list.size()) throw new FunctionException("Invalid expression array (overflow)2 : " + str);
                            Object obj2 = list.get(i);
                            if (!(obj2 instanceof Double)) throw new FunctionException("Invalid expression array (duplicated operator)2 : " + str);
                            double d1 = (Double) formerObj;
                            double d2 = (Double) obj2;
                            double res = 0.0;
                            if (operator.equals("+")) res = d1 + d2;
                            else if (operator.equals("-")) res = d1 - d2;
                            else throw new FunctionException("Invalid operator (+ or -) : " + str);

                            formerObj = res;
                        }
                        else
                        {
                            list2.add(formerObj);
                            formerObj = null;
                            list2.add(obj);
                        }
                    }
                }
                else throw new FunctionException("Unknown error (a) : " + str);
            }
            if (formerObj != null) list2.add(formerObj);

            list = list2;
            if ((isSecond)&&(list.size() != 1)) throw new FunctionException("Something wrong (a) : " + str);

            isSecond = true;
        }
        Object obj = list.get(0);
        if (!(obj instanceof Double)) throw new FunctionException("Invalid expression (invalid value type after parsing) : " + str);
        double d0 = (Double) obj;
        //System.out.println("Result computeExe : " + str + ", p1=" + p1 + ", p2=" + p2 + ", res=" +  d0);
        return d0;
    }

    private double computeFunction(double p1, double p2, String fName, String str) throws FunctionException
    {
        str = str.trim();
        if (str.equals("")) throw new FunctionException("Invalid function (no parameter) : " + str);
        List<Double> list = new ArrayList<Double>();
        StringTokenizer st = new StringTokenizer(str, ",");
        while(st.hasMoreTokens())
        {
            String token = st.nextToken();
            if (token.trim().equals("")) throw new FunctionException("Invalid function (empty parameter) : " + str);
            token = token.trim();
            list.add(computeExe(p1, p2, token));
        }

        Class functionClass = Math.class;
//        try {
//            functionClass = Class.forName(this.getImplementationClass());
//        } catch (ClassNotFoundException e) {
//            throw new FunctionException("Class could not be found [" + this.getImplementationClass()+"]");
//        }
        Method functionMethod = null;
        Class[] parameterTypes = new Class[list.size()];
        for(int i=0;i<parameterTypes.length;i++) parameterTypes[i] = double.class;

        Object[] functionResult = null;
        try {
            functionMethod = functionClass.getMethod(fName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new FunctionException("Invalid function (not found this function) : " + fName);
        }
        Object tempResult = null;
        Object[] parameterData = list.toArray();
        try
        {
            //tempResult = functionMethod.invoke(functionClass.newInstance(), parameterData);
            tempResult = functionMethod.invoke(functionClass, parameterData);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new FunctionException("Function invocation Error (IllegalAccessException) : " + str + ", " + e.getMessage());

        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof FunctionException) {
                String errMessage = e.getTargetException().getMessage() + " in '" + str + "'";
                throw new FunctionException(errMessage);
            } else {
                throw new FunctionException("Unexpected error while Math function invoking (InvocationTargetException) : " + str);
            }
        }
        //catch (InstantiationException e) {
        //    throw new FunctionException("Unexpected error while Math function instanciating (InstantiationException) : " + str);
        //}
        double res = 0.0;
        if (tempResult instanceof Integer) res = ((Integer)tempResult).doubleValue();
        else if (tempResult instanceof Double) res = (Double)tempResult;
        else if (tempResult instanceof Float) res = ((Float)tempResult).doubleValue();
        else if (tempResult instanceof Long) res = ((Long)tempResult).doubleValue();
        else throw new FunctionException("unrecognized result instance type : " + str);


        //System.out.println("Result computeFunction : "+fName+"(" + str + "), p1=" + p1 + ", p2=" + p2 + ", res=" +  res);

        return res;
    }

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
