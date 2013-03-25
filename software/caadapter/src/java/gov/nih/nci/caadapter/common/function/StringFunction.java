/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.common.function;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.util.NullFlavorSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * Perfoms String Functions.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.7 $
 *          date        $Date: 2009-04-01 15:12:40 $
 */

public class StringFunction {

    /**
     * Accepts two String objects.
     * Returns an Object containing the results of concatenating two strings.
     *
     * @param strParam1 the first string to which strParam2 will be concatenated
     * @param strParam2 the second string that will be appended to strParam1
     * @return an String containing the final concatenated String
     */
    public String concat(String strParam1, String strParam2) {
        if (strParam1 == null) strParam1 = "";
        if (strParam2 == null) strParam2 = "";
        return strParam1 + strParam2;
    }

    /**
     * Accepts a String and an int.  Splits a string at position defined by iSplitPos.
     * Returns the split strings in a list of Objects.
     *
     * @param strParam  the String object to be split
     * @param iSplitPos the position in the string to start the split
     * @return an Object array containing two String objects that result from the split.
     */
    //public Object[] split(String strParam, int iSplitPos)
    public List<String> split(String strParam, int iSplitPos) {
        strParam.substring(iSplitPos);
        if (strParam == null || strParam.length() == 0) {
            List<String> lt = new ArrayList<String>(2);
            lt.add("");
            lt.add("");
            return lt;
        }
        if (iSplitPos == 0) {
            List<String> lt = new ArrayList<String>(2);
            lt.add(strParam);
            lt.add("");
            return lt;
        }
        ArrayList<String> lstStrSplit = null;  //Stores two string objects
        int iBeginingIndex = 0;        //Stores the beginning index of the split
        int iArrayCapacity = 2;        //Stores the initial capacity for the ArrayList

        //Split the string
        lstStrSplit = new ArrayList<String>(iArrayCapacity);
        lstStrSplit.add(strParam.substring(iBeginingIndex, iSplitPos)); //Store first part of the string
        lstStrSplit.add(strParam.substring(iSplitPos)); //Store the second part of the string

        return lstStrSplit;  //return a list of Strings
    }

    /**
     * Examine the input data to determine the output value and its corresponding NullFlavor value
     * The output NullFlavor value is retrieved from NullFlavorSetting using output value as key.
     * The NullFlavor default could be either input from source data or user's default setting
     * <ul>
     *  <li>Case I: The input data is available but NullFlavorSetting is not, then<p>
     * 	set output 1 as CAADAPPTER_NULLFLAVOR_ATTRIBUTE_VALUE:value<p>
     *  set output 2 as null, transformation engine will read NullFlavorSetting from H3S file or system default
     *
     * <li>Case II: NullFlavorSetting is available but input data is not, then <p>
     *  set output 1 as CAADAPPTER_NULLFLAVOR_ATTRIBUTE_MARK:NULL <p>
     *  set output 2 as NullFlavorSetting, transformation engine will read the default value and use is a as key to retrieve
     *  value from NullFlavorSetting
     *
     *  <li>Case III: Both the input data and NullFlavorSetting are available, then set both the output 1 and output 2
     * </ul>
     * If dataString is NULL or BLANK, the output value is NULL, otherwise the output value
     * the same as dataString<p>
     *
     * @param dataString The input data to determine output data value and nullFlavor value
     * @param nullFlavorInput The input setting of list of key:value pair of NullFlavor constants
     * @param nullFlavorDefaultSetting The default setting of list of key:value pair of NullFlavor constants
     * @return List of two strings: values of nullFlavor attribute and target data
     */
    public List<String> nullFlavor(String dataString, String nullFlavorInput, String nullFlavorDefaultSetting)
    {
    	//process source data value
    	String rtnValue1=dataString;
    	if(rtnValue1!=null&&rtnValue1.equalsIgnoreCase(GeneralUtilities.CAADAPTER_DATA_FIELD_NULL))
    	{//Null read from CSV
    		rtnValue1=null;
    	}
    	else if (rtnValue1!=null&&rtnValue1.equals("\"\""))
    	{
    		//Null read from constant Function
    		rtnValue1=null;
    	}
    	//set NullFlavorSetting
    	NullFlavorSetting nullSetting=null;
    	if (nullFlavorInput!=null&&!nullFlavorInput.equals(""))
    		nullSetting=new NullFlavorSetting(nullFlavorInput);//use source data input
    	else if (nullFlavorDefaultSetting!=null&&!nullFlavorDefaultSetting.equals(""))
    		nullSetting=new NullFlavorSetting(nullFlavorDefaultSetting);//use user's defaultSetting
    	else
    	{
    		//case I:
    		//NullFlavorSetting is not available
    		//mark the output 1 to enable transformation engine setup "nullFlavor"
    		//transformation engine will retrieve NullFlavorSetting from H3S
        	//or system default, then set "nullFlavor" attribute of target element based value of "output 1"
    		List<String> nullSettingRtnList=new ArrayList<String>();
    		//set value for "output 1"
    		String opt1=GeneralUtilities.CAADAPTER_NULLFLAVOR_ATTRIBUTE_VALUE+":";
    		if (rtnValue1==null)
    			opt1=opt1+"NULL";
    		else
    			opt1=opt1+rtnValue1;
    		nullSettingRtnList.add(opt1);

    		//set "output 2" as null
    		nullSettingRtnList.add(null);
        	//return here,
        	return nullSettingRtnList;
    	}

    	if (dataString==null)
    	{
    		//case II:
    		//source data is not available,
    		//forward NullFlavorSetting to transformation engine and mark the "attribute" to
    		//drive "nullFlavor" setup. Transformation engine will read default value of this
    		//attribute, then set "nullFlavor" attribute for target element using the NullFlavorSetting
    		List<String> nullDataRtnList=new ArrayList<String>();
    		//set "output 1" as CAADAPTER_NULLFLAVOR_SETTING_ATTRIBUTE _MARK to mark it as the attribute to
    		//set "nullFlavor" of the target element
    		String optRtn1=GeneralUtilities.CAADAPTER_NULLFLAVOR_ATTRIBUTE_MARK+":"+null;
    		nullDataRtnList.add(optRtn1);
    		nullDataRtnList.add(nullSetting.toString());
    		//return here
    		return nullDataRtnList;
    	}

    	//case III:
    	//set both "output 1" and "output 2"
    	//set NullFlavorKey based on input data
    	String nullFlavorKey=rtnValue1;
    	if (nullFlavorKey==null)
    		nullFlavorKey="NULL";
    	else if(nullFlavorKey.equals(""))
    		nullFlavorKey="BLANK";

    	//retrieve NullFlavor value from NullFlavorSetting
    	String rtnValue2=(String)nullSetting.get(nullFlavorKey);

		//verify if it is a valid NULL FLAVOR constant defined by HL7
		String nfValuesAllowed=CaadapterUtil.findApplicationConfigValue(Config.CAADAPTER_COMPONENT_HL7_NULLFLAVOR_VALUES_ALLOWED);
		boolean isValidNF=false;
		if (rtnValue2!=null&&nfValuesAllowed!=null)
		{
			String[] valuesAlled=nfValuesAllowed.split(",");

			for (String nfValue:valuesAlled)
			{
				if (nfValue!=null&&nfValue.equalsIgnoreCase(rtnValue2))
				{
					isValidNF=true;
					break;
				}
			}
		}
		if (!isValidNF)
			Log.logWarning(this, "Invalid NullFlavor value is found...key:value="+nullFlavorKey+":"+rtnValue2);

    	String nullifyMissingData=CaadapterUtil.findApplicationConfigValue(Config.CAADAPTER_COMPONENT_HL7_MISSING_DATA_NULLFLAVOR_NULLIFIED);
		if (nullifyMissingData!=null&&nullifyMissingData.equalsIgnoreCase("true"))
		 	rtnValue1=null;

		List<String> rtnList=new ArrayList<String>();
		rtnList.add(rtnValue1);
    	rtnList.add(rtnValue2);
    	return rtnList;
    }

    /**
     * Accepts a String.
     * Returns an Object containing the numeric length of the String.
     *
     * @param strParam a String object
     * @return an Object containing the length of the String
     */
    public int length(String strParam) {
        if (strParam == null)
            return 0;
        else
            return strParam.length();
    }


    /**
     * Returns a new string that is a substring of this string.
     * The substring begins at the specified iStartPos and extends
     * to the character at index endIndex - 1.
     * Thus the length of the substring is endIndex-beginIndex.
     *
     * @param strParam  a string
     * @param iStartPos beginning index
     * @return iEndPos end index
     * @see String
     */

    public String substring(String strParam, int iStartPos, int iEndPos) {
        if (strParam == null || strParam.length() == 0)
            return "";
        if (iEndPos > strParam.length()) iEndPos = strParam.length();
        if (iEndPos < 0) iEndPos = strParam.length();
        if (iStartPos < 0) return "";
        if (iStartPos >= iEndPos) return "";
        String strSubString = null; //Holds the resulting substring.
        strSubString = strParam.substring(iStartPos, iEndPos); //Create a substring.
        return strSubString; //Return the resulting substring.
    }

    /**
     * Returns the position of a substring within a string.
     *
     * @param strSource  the source string
     * @param strPattern the string pattern to search
     * @return the position of the String
     * @see String
     */

    public int instring(String strSource, String strPattern) {
        if ((strSource == null || strSource.length() == 0) || (strPattern == null || strPattern.length() == 0))
            return -1;

        return strSource.indexOf(strPattern);
    }

    /**
     * Replaces all occurences of a pattern within a string.
     *
     * @param strSource      the source string
     * @param strPattern     the string pattern to search
     * @param strReplacement the string pattern that will replace the strPattern
     * @return A new string including the string replacements.
     * @see String
     */

    public Object replace(String strSource, String strPattern, String strReplacement) {
        if (strSource == null) return "";
        if ((strSource.length() == 0) || (strPattern == null || strPattern.length() == 0))
            return strSource;

        return strSource.replaceAll(strPattern, strReplacement);
    }

    /**
     * Replaces all characters in the string with upper case characters.
     *
     * @param strSource the source string
     * @return A new string with all characters in upper case.
     * @see String
     */
    public Object upper(String strSource) {
        //Check to see if the incoming value is empty
        if (strSource == null || strSource.length() == 0)
            return "";
        //else
        return strSource.toUpperCase();
    }

    /**
     * Replaces all characters in the string with lower case characters.
     *
     * @param strSource the source string
     * @return A new string with all characters in lower case.
     * @see String
     */
    public Object lower(String strSource) {

        if (strSource == null || strSource.length() == 0)
            return "";

        return strSource.toLowerCase();
    }

    /**
     * Replaces the fist character of each word in a string to uppercase
     * and all other characters to lower case.
     *
     * @param strSource the source string
     * @return A new string with first character changed to uppercase.
     * @see String
     */
    public Object initcap(String strSource) {
        if (strSource == null || strSource.length() == 0)
            return "";


        //boolean bcapitalize = false;   //Set a flag to make character capital based on whitespace character
        char[] caData = null;          //Hold the incoming string in a character array


        //Check to see if the incoming value is empty


        //else
        boolean bcapitalize = true;
        caData = strSource.toCharArray(); //convert to character array


        //Replace first character in each word to upper case.
        for (int i = 0; i < caData.length; i++) {
            if (caData[i] == ' ' || Character.isWhitespace(caData[i]))
                bcapitalize = true;
            else if (bcapitalize) {
                caData[i] = Character.toUpperCase(caData[i]);
                //System.out.println(caData[i]);
                bcapitalize = false;
            } else {
                caData[i] = Character.toLowerCase(caData[i]);
            }
        }
        return new String(caData);
    }	//initCap

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.6  2009/01/09 21:32:02  wangeug
 * HISTORY      : Implementation NullFlavor function with three cases
 * HISTORY      :
 * HISTORY      : Revision 1.5  2008/12/04 20:35:27  wangeug
 * HISTORY      : support nullFlavor:implment NullFlavor function
 * HISTORY      :
 * HISTORY      : Revision 1.4  2008/09/25 18:57:45  phadkes
 * HISTORY      : Changes for code standards
 * HISTORY      :
*/
