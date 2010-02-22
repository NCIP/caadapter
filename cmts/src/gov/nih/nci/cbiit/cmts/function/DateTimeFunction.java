package gov.nih.nci.cbiit.cmts.function;

import java.util.Map;

import gov.nih.nci.cbiit.cmts.core.FunctionType;

public class DateTimeFunction {

	
	/**
	 * Returns the duration in days (integer)
	 * @param functionType
	 * @return
	 */
	public String countDays(FunctionType functionType,  Map<String, String>  paramters)
	{
		//use day-from-date
		//Example: day-from-date(xs:date("2005-04-23"))
		//Result: 23

		return "current-time()";
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
	
}
