/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/function/DateFunction.java,v 1.1 2007-04-03 16:02:37 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.common.function;

import gov.nih.nci.caadapter.common.ApplicationException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: doswellj
 * Date: Aug 5, 2005
 * Time: 2:40:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class DateFunction
{
    SimpleDateFormat sourceDateFormat;
    SimpleDateFormat targetDateFormat;
    SimpleDateFormat defaultDateFormat;
    String defaultDateFormatString;
    Calendar fromCal;
    Calendar toCal;
    java.util.Date sourceDate;
    java.util.Date targetDate;
    java.util.Date nowDate;

    public DateFunction()
    {
        sourceDateFormat = null;
        targetDateFormat = null;
        sourceDate = null;
        targetDate = null;
        nowDate = new java.util.Date();
        defaultDateFormatString = "yyyyMMddHHmmss";
        defaultDateFormat = new SimpleDateFormat(defaultDateFormatString);
        fromCal = Calendar.getInstance();
        toCal = Calendar.getInstance();
    }

    public SimpleDateFormat checkSimpleDateFormat(String sdfStr) throws FunctionException
    {
       SimpleDateFormat sdf;
       try
        {
           sdf = new SimpleDateFormat(sdfStr);
        }
        catch(IllegalArgumentException ie)
        {
            throw new FunctionException("Illegal Date Format [" + sdfStr + "] (refer to the specification of Java's SimpleDateFormat class)", 505, new Throwable(), ApplicationException.SEVERITY_LEVEL_WARNING);
        }
        catch(NullPointerException ie)
        {
            throw new FunctionException("The given date Format String is null value : ", 507,  new Throwable(), ApplicationException.SEVERITY_LEVEL_WARNING);
        }
        return sdf;
    }

    public java.util.Date parseDateFromString(SimpleDateFormat sdf, String dateStr) throws FunctionException
    {
        java.util.Date dt = null;
        try
        {
            dt = sdf.parse(dateStr);
        }
        catch(ParseException pe)
        {
            throw new FunctionException("Invalid date Format in date String : " + dateStr, 509, new Throwable(), ApplicationException.SEVERITY_LEVEL_WARNING);
        }
        return dt;
    }
    public String getCurrentTime(String timeFormat) throws FunctionException
    {
        nowDate = new java.util.Date();
        SimpleDateFormat dateFormat = checkSimpleDateFormat(timeFormat);
        return dateFormat.format(nowDate);
    }
    public String getCurrentTime()
    {
        nowDate = new java.util.Date();
        return defaultDateFormat.format(nowDate);
    }
    public String getDefaultDateFormatString()
    {
        return defaultDateFormatString;
    }
    //public String changeDefaultFormat(String inDate, String fromFormat) throws FunctionException
    //{
    //   return changeFormat(inDate, fromFormat, defaultDateFormatString);
    //}

    public String getYearsBetweenDates(String fromDate, String fromFormat, String toDate, String toFormat) throws FunctionException
    {
       setCalendarInstances(fromDate, fromFormat, toDate, toFormat);
       return Integer.toString(toCal.get(Calendar.YEAR) - fromCal.get(Calendar.YEAR));
    }
    public String getYearsBetweenDatesInDefaultFormat(String fromDate, String toDate) throws FunctionException
    {
       return getYearsBetweenDates(fromDate, defaultDateFormatString, toDate, defaultDateFormatString);
    }

    public String getMonthsBetweenDates(String fromDate, String fromFormat, String toDate, String toFormat) throws FunctionException
    {
       setCalendarInstances(fromDate, fromFormat, toDate, toFormat);
       int years = toCal.get(Calendar.YEAR) - fromCal.get(Calendar.YEAR);
       int months = (years * 12) + toCal.get(Calendar.MONTH) + (12 - fromCal.get(Calendar.YEAR));
       return Integer.toString(months);
    }
    public String getMonthsBetweenDatesInDefaultFormat(String fromDate, String toDate) throws FunctionException
    {
       return getMonthsBetweenDates(fromDate, defaultDateFormatString, toDate, defaultDateFormatString);
    }

    public String getDaysBetweenDates(String fromDate, String fromFormat, String toDate, String toFormat) throws FunctionException
    {
       setCalendarInstances(fromDate, fromFormat, toDate, toFormat);

       Long millis = toCal.getTimeInMillis() - fromCal.getTimeInMillis();
       int days = (int) (millis / (1000*60*60*24));
       int daySecondsOfFrom = (fromCal.get(Calendar.HOUR)*3600) + (fromCal.get(Calendar.MINUTE)*60) + (fromCal.get(Calendar.SECOND));
       int daySecondsOfTo = (toCal.get(Calendar.HOUR)*3600) + (toCal.get(Calendar.MINUTE)*60) + (toCal.get(Calendar.SECOND));
       if (daySecondsOfTo < daySecondsOfFrom) days++;
       return Integer.toString(days);
    }
    public String setMatchedDateFormat(String format, String date)
    {
        date = date.trim();
        format = format.trim();
        if (format.length() == date.length()) return date;
        else if (format.length() < date.length()) return date.substring(0, format.length());

        String str = "";
        for (int i=0;i<(format.length() - date.length());i++) str = str + "0";
        return date + str;
    }
    public int countDays(String fromDate, String toDate) throws FunctionException
    {
        if ((fromDate==null)||(fromDate.trim().equals(""))) throw new FunctionException("Null value of fromDate");
        if ((toDate==null)||(toDate.trim().equals(""))) throw new FunctionException("Null value of toDate");
        fromDate = setMatchedDateFormat(defaultDateFormatString, fromDate);
        toDate = setMatchedDateFormat(defaultDateFormatString, toDate);
        String str = getDaysBetweenDates(fromDate, defaultDateFormatString, toDate, defaultDateFormatString);
        return Integer.parseInt(str);
    }
    public Long getMillisBetweenDates(String fromDate, String fromFormat, String toDate, String toFormat) throws FunctionException
    {
       setCalendarInstances(fromDate, fromFormat, toDate, toFormat);

       Long millis = toCal.getTimeInMillis() - fromCal.getTimeInMillis();
       return millis;
    }

    public void setCalendarInstances(String fromDate, String fromFormat, String toDate, String toFormat) throws FunctionException
    {
       SimpleDateFormat objFromFormat = checkSimpleDateFormat(fromFormat);
       SimpleDateFormat objToFormat = checkSimpleDateFormat(toFormat);
       java.util.Date objFromDate = parseDateFromString(objFromFormat, fromDate);
       java.util.Date objToDate = parseDateFromString(objToFormat, toDate);
       fromCal.setTime(objFromDate);
       toCal.setTime(objToDate);
    }
    public String changeAnotherFormat(String fromFormat, String toFormat, String inDate) throws FunctionException
    {
       SimpleDateFormat objFromFormat = checkSimpleDateFormat(fromFormat);
       SimpleDateFormat objToFormat = checkSimpleDateFormat(toFormat);
       java.util.Date objDate = parseDateFromString(objFromFormat, inDate);
       return objToFormat.format(objDate);
    }
    private String arrangeMonthFormat(String fromFormat)
    {
       String filteredFormat = filteringFormat(fromFormat);
       /*
       int n = 0;
       n = filteredFormat.indexOf("mm");
       if (n < 0) return fromFormat;
       if ((filteredFormat.indexOf("MM")) >= 0) return fromFormat;
       if (((filteredFormat.toUpperCase()).indexOf("Y")) < 0) return fromFormat;
       boolean threeM = false;
       if ((filteredFormat.substring(n)).startsWith("mmm")) threeM = true;
       if (((filteredFormat.toUpperCase()).indexOf("H")) < 0)
       {
           if (threeM) return fromFormat.replace("mmm", "MMM");
           else return fromFormat.replace("mm", "MM");
       }
       */
       int n = 0;
       int m = 0;
       String t = "";
       String temp = "";
       while(true)
       {
           temp = "";
           if (n == 0) temp = "yyyyMMdd";
           else if (n == 1) temp = "yyMMdd";
           else if (n == 2) temp = "yyddMM";
           else if (n == 3) temp = "MMddyyyy";
           else if (n == 4) temp = "MMddyy";
           else if (n == 5) temp = "yyyy?MMM?dd";
           else if (n == 6) temp = "MMM?dd?yyyy";
           else if (n == 7) temp = "yy?MMM?dd";
           else if (n == 8) temp = "MMM?dd?yy";
           else if (n == 9) temp = "yyyy?MM?dd";
           else if (n == 10) temp = "MM?dd?yyyy";
           else if (n == 11) temp = "yy?MM?dd";
           else if (n == 12) temp = "MM?dd?yy";
           else break;
           n++;
           m = 0;
           while(true)
           {
               if (m == 0) t = "/";
               else if (m == 1) t = ".";
               else if (m == 2) t = "-";
               else if (m == 3) t = "|";
               else if (m == 4) t = "\\";
               else if (m == 5) t = "_";
               else break;
               m++;
               String temp2 = temp.replace("?", t);

               if (fromFormat.indexOf(temp2) >= 0) break;
               int j = (fromFormat.toUpperCase()).indexOf(temp2.toUpperCase());
               //System.out.println("ZZZZZZZZZZ : " + temp2 + " : "+ fromFormat + " : "+fromFormat.toUpperCase()+" : "+ j);
               if (j >= 0)
               {
                  fromFormat = fromFormat.substring(0,j) + temp2 + fromFormat.substring(j+temp2.length());
                  break;
               }
           }
       }
       n = 0;
       m = 0;
       if ((filteredFormat.toUpperCase()).indexOf("H") < 0) return fromFormat;
       while(true)
       {
           temp = "";
           if (n == 0) temp = "mmss";
           else if (n == 1) temp = "mm?ss";
           else if (n == 2) temp = "ssmm";
           else if (n == 3) temp = "ss?mm";
           else break;
           n++;
           m = 0;
           while(true)
           {
               if (m == 0) t = ":";
               else if (m == 1) t = ".";
               else break;
               m++;
               String temp2 = temp.replace("?", t);

               if (fromFormat.indexOf(temp2) >= 0) return fromFormat;
               int j = (fromFormat.toUpperCase()).indexOf(temp2.toUpperCase());
               //System.out.println("QQQQQQQQQQQ : " + temp2 + " : "+ fromFormat + " : "+fromFormat.toUpperCase()+" : "+ j);
               if (j >= 0)
                  return fromFormat.substring(0,j) + temp2 + fromFormat.substring(j+temp2.length());
           }
       }
       return fromFormat;
    }

    private String filteringFormat(String fromFormat)
    {
       boolean quotMark = false;
       String filteredFormat = "";
       String achar = "";
       for (int i=0;i<fromFormat.length();i++)
       {
           achar = fromFormat.substring(i, i+1);
           if (achar.equals("'"))
           {
               if (quotMark) quotMark = false;
               else quotMark = true;
               continue;
           }
           if (!quotMark) filteredFormat =  filteredFormat + achar;
       }
       return filteredFormat;
    }
    public String changeFormat(String fromFormat, String inDate) throws FunctionException
    {
       //System.out.println("YYYYYYYYYYYYYYYYYYYYYYYYYYY : Date");
       //if(fromFormat.equals("DD-MON-YYYY")) fromFormat = "dd-MMM-yyyy";
       //if (inDate.equals("")) throw new FunctionException("Year, Month or day data is needed(BBBBB). : " + inDate, 511, new Throwable(), ApplicationException.SEVERITY_LEVEL_WARNING);
       if (inDate.equals("")) return "";
       if (inDate == null) return "";
       String filteredFormat = filteringFormat(fromFormat);
       String arrangedFormat = arrangeMonthFormat(fromFormat);
       //System.out.println("VVVVVV : " + arrangedFormat + " : " + inDate);
       SimpleDateFormat objFromFormat = checkSimpleDateFormat(arrangedFormat);
       //SimpleDateFormat objToFormat = checkSimpleDateFormat(toFormat);
       java.util.Date objDate = parseDateFromString(objFromFormat, inDate);

       //System.out.println("ASAS : " +filteredFormat+":" + filteredFormat.indexOf("y") );
       if ((filteredFormat.toUpperCase()).indexOf("Y") < 0) throw new FunctionException("Year, Month or day data is needed. : " + inDate, 511, new Throwable(), ApplicationException.SEVERITY_LEVEL_WARNING);
       if ((filteredFormat.toUpperCase()).indexOf("H") < 0) return (defaultDateFormat.format(objDate)).substring(0,8) + "000000";
       return defaultDateFormat.format(objDate);
    }
    /*
    public Object changeFormat(Object fromFormatO, Object[] inDateO) throws FunctionException
    {
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXX Date") ;
        String fromFormat = fromFormatO.toString();
        String inDate = "";
        try
        {
            inDate = inDateO[1].toString();
            throw new FunctionException("Too many input date data. : " + inDate, 511, new Throwable(), ApplicationException.SEVERITY_LEVEL_WARNING);
        }
        catch(ArrayIndexOutOfBoundsException e)
        { }


        try
        {
            inDate = inDateO[0].toString();
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            throw new FunctionException("No input date data. : " + inDate, 511, new Throwable(), ApplicationException.SEVERITY_LEVEL_WARNING);
        }
        return (Object) changeFormat(fromFormat, inDate);
    }
    */
    /*
    public String formatDate(String strDate, String strOriginalDateFormat) throws FunctionException
    {
       String strHL7Date = null;
       SimpleDateFormat objHL7Format = null;
       SimpleDateFormat objDateFormat = null;
       Date objDate = null;

        try
        {
           objDateFormat = new SimpleDateFormat(strOriginalDateFormat);
           objDate = objDateFormat.parse(strDate);

           //Define the new date format
           objHL7Format = new SimpleDateFormat("yyyyMMdd");

           //Get the new format
           strHL7Date = objHL7Format.format(objDate);
        }
        catch (ParseException e)
        {
        }
    return strHL7Date;
    }
    */

}
