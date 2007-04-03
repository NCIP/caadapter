/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/function/StringFunction.java,v 1.1 2007-04-03 16:02:37 wangeug Exp $
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

import java.util.ArrayList;
import java.util.List;

/**
 * Perfoms String Functions.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:02:37 $
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
     * Removes white space from both ends of this string.
     * This method may be used to trim whitespace from the beginning and end of a string.
     * It trims all ASCII control characters as well.
     *
     * @param strParam a string
     * @return an Object containing the trimmed string
     * @see String
     */

    public String trim(String strParam) {
        if (strParam == null) return null;
        return strParam.trim();
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




