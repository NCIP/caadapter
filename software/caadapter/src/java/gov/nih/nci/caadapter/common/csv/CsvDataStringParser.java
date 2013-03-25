/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.common.csv;

import gov.nih.nci.caadapter.common.util.GeneralUtilities;


/*
 * This class process a string and convert it into CSV fields based on delimiter(s).
 * The following is the order of priority of each delimiter from low to high
 * 1. "," is used as the default delimiter
 * 2. "\"" override the default delimiter
 * 3. User's delimiter is the first choosen one
 * @author OWNER: $Author: wangeug $
 * @author LAST UPDATE $Author: wangeug $
 * @since      caAdapter  v4.2
 * @version    $Revision: 1.6 $
 * @date       $Date: 2008-11-17 20:12:51 $
 */
public class CsvDataStringParser {

	private String DEFAULT_DELIMITER=",";
	private String DOUBLE_QUOTE_DELIMITER="\"";
	private String USER_DELIMITER;
	private String contentData="";
	private static String  DOUBLE_QUOTE_ENCODER="CAADAPTER_&#34;";
	private static String  COMMA_ENCODER="CAADAPTER_&#44;";
	/**
	 * Constructor with the default delimiter
	 * @param s
	 */
	public CsvDataStringParser(String s)
	{
		contentData=s;
	}

	/**
	 * Constructor with the default delimiter
	 * @param s
	 */
	public CsvDataStringParser(String s, String delimiter)
	{
		contentData=s;
		USER_DELIMITER=delimiter;
	}

	public String [] getDataFields()
	{
		if (USER_DELIMITER!=null
				&&!USER_DELIMITER.trim().equals(""))
		{
			//parse the content with user's delimiter
			return contentData.split(USER_DELIMITER);//parseStringToAarryWithDelimiter(contentData, USER_DELIMITER, false);
		}
		else if (contentData.indexOf(DOUBLE_QUOTE_DELIMITER)<0)
		{
			//parse the content with the DEFAULT delimiter
			return contentData.split(DEFAULT_DELIMITER);//parseStringToAarryWithDelimiter(contentData, DEFAULT_DELIMITER, false);
		}

		//parse the String containing one or more DOUBLE_QUOTE_DELIMITER
		encodeData();
		String [] encodedFields= contentData.split(DEFAULT_DELIMITER);
		//decode the encodeFields
		for (int i=0;i<encodedFields.length;i++)
		{
			String oneField=encodedFields[i];
			//remove the boundary DOUBLE_QUOTE
			oneField=oneField.replace("\"", " ");
			oneField=oneField.replace(DOUBLE_QUOTE_ENCODER, "\"");
			encodedFields[i]=oneField.replace(COMMA_ENCODER,",");
		}
		return encodedFields;
	}

	/**
	 * Encode the contentData string
	 * Encode DOULBE_QUOT and COMMA within a pair of delimiters (DOUBLE_QUOTE).
	 *
	 */
	private void encodeData()
	{
		//encode NULL_DOUBLE_QUOTE_ENCODER
		String doubleQuotedNull=",\"\",";
		String replaceNull=","+GeneralUtilities.CAADAPTER_DATA_FIELD_NULL+",";
		if (contentData.contains(doubleQuotedNull))
		{
			String encodeContentData=contentData.replace(doubleQuotedNull, replaceNull);
			contentData=encodeContentData;
		}
		//encode DOUBLE_QUOTE -- the "" within a "..."
		if (contentData.contains("\"\""))
		{
			String encodeContentData=contentData.replace("\"\"", DOUBLE_QUOTE_ENCODER);
			contentData=encodeContentData;
		}
		//encode COMMA
		boolean isInDoubleQuote=false;
		char[] stChars=contentData.toCharArray();
		StringBuffer encodeSb=new StringBuffer();
		for (int i=0;i<stChars.length;i++)
		{
			//chck if the DOUBLE_QUOTE is closed
			if (stChars[i]=='\"')
				isInDoubleQuote=!isInDoubleQuote;

			if (stChars[i]==','&&isInDoubleQuote)
				encodeSb.append(COMMA_ENCODER);
			else
				encodeSb.append(stChars[i]);
		}
		contentData=encodeSb.toString().replace("\"", "");
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.5  2008/09/24 20:52:36  phadkes
 * HISTORY      : Changes for code standards
 * HISTORY      :
*/
