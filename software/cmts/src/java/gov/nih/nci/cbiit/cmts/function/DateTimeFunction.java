package gov.nih.nci.cbiit.cmts.function;

import java.util.Map;
import java.net.URL;

import gov.nih.nci.cbiit.cmts.core.FunctionType;
import gov.nih.nci.cbiit.cmts.util.FileUtil;

public class DateTimeFunction {


    /**
     * Returns the duration in days (integer)
     * Example: countDays("2010-01-10", "2010-01-20")
     * Result: 10
     * @param functionType
     * @return
     */
    public String countDays(FunctionType functionType,  Map<String, String>  paramters)
    {
        //use day-from-date
        //Example: day-from-date(xs:date("2005-04-23"))
        //Result: 23
        String startDay=paramters.get("startDate");
        String endDay=paramters.get("endDate");
        //return "day-from-date(xs:date("+endDay +")) - day-from-date(xs:date("+startDay+"))";


        URL url = FileUtil.getCodeBase();
        String urlS = null;
        if (url != null) urlS = url.toString();
        else urlS = "http://caadapter.nih.nci.gov/caadapter-cmts/";
        if (!urlS.endsWith("/")) urlS = urlS + "/";
        return "data(doc('"+ urlS + "WebFunctionService?functionName=countDays&amp;val01="+FileUtil.findNumericValue(startDay, false)+"&amp;val02="+FileUtil.findNumericValue(endDay, false)+"')/caAdapterResponse/result)";
        //return r;
    }

    /**
     * Returns the current date (with timezone)
     * @param functionType
     * @return
     */
    public String currentDate(FunctionType functionType,  Map<String, String>  paramters)
    {
        System.out.println("DateTimeFunction.currentDate()...");
        return "current-date()";
    }


    /**
     * Returns the current dateTime (with timezone)
     * @param functionType
     * @return
     */
    public String currentDateTime(FunctionType functionType,  Map<String, String>  paramters)
    {
        return "current-dateTime()";
    }

    /**
     * Returns the current time (with timezone)
     * @param functionType
     * @return
     */
    public String currentTime(FunctionType functionType, Map<String, String>  paramters)
    {
        return "current-time()";
    }




    /**
     * Returns the duration in days (integer) with HL7 formatted date
     * Example: countDays("2010-01-10", "2010-01-20")
     * Result: 10
     * @param functionType
     * @return
     */
    public String countHL7Days(FunctionType functionType,  Map<String, String>  paramters)
    {
        //use day-from-date
        //Example: day-from-date(xs:date("2005-04-23"))
        //Result: 23
        //String startDay=paramters.get("startDate");
        //String endDay=paramters.get("endDate");
        //return "day-from-date(xs:date("+endDay +")) - day-from-date(xs:date("+startDay+"))";

        return countDays(functionType,  paramters);
    }

    /**
     * Returns the current date in HL7 format
     * @param functionType
     * @return
     */
    public String currentDateHL7(FunctionType functionType,  Map<String, String>  paramters)
    {
        System.out.println("DateTimeFunction.currentDateHL7()...");
        return  "concat(" +
                   "substring(string(current-date()),1,4), " +
                   "concat(" +
                      "substring(string(current-date()),6,2), " +
                      "substring(string(current-date()),9,2)" +
                   ")" +
                ")";
    }


    /**
     * Returns the current dateTime in HL7 format
     * @param functionType
     * @return
     */
    public String currentDateTimeHL7(FunctionType functionType,  Map<String, String>  paramters)
    {
        return  "concat(" +
                   "substring(string(current-date()),1,4), " +
                   "concat(" +
                      "substring(string(current-date()),6,2), " +
                      "concat(" +
                         "substring(string(current-date()),9,2), " +
                         "concat(" +
                            "substring(string(current-time()),1,2), " +
                            "concat(" +
                               "substring(string(current-time()),4,2), " +
                               "substring(string(current-time()),7)" +
                            ")" +
                         ")" +
                      ")" +
                   ")" +
                ")";
        //return "current-dateTime()";
    }

    /**
     * Returns the current time in HL7 format
     * @param functionType
     * @return
     */
    public String currentTimeHL7(FunctionType functionType, Map<String, String>  paramters)
    {
        return  "concat(" +
                   "substring(string(current-time()),1,2), " +
                   "concat(" +
                      "substring(string(current-time()),4,2), " +
                      "substring(string(current-time()),7)" +
                   ")" +
                ")";

    }

}
