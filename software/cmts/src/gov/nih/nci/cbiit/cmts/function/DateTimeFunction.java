/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.function;

import java.util.Map;

import gov.nih.nci.cbiit.cmts.core.FunctionType;

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
		return "day-from-date(xs:date("+endDay +")) - day-from-date(xs:date("+startDay+"))";
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
