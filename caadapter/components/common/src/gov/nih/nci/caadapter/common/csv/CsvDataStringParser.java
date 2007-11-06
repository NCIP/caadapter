package gov.nih.nci.caadapter.common.csv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/*
 * This class process a string and convert it into CSV fields based on delimiter(s).
 * The following is the order of priority of each delimiter from low to high
 * 1. "," is used as the default delimiter
 * 2. "\"" override the default delimiter
 * 3. User's delimiter is the first choosen one
 */
public class CsvDataStringParser {

	private String DEFAULT_DELIMITER=",";
	private String DOUBLE_QUOTE_DELIMITER="\"";
	private String USER_DELIMITER;
	private String contentData="";
	
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
			return parseStringToAarryWithDelimiter(contentData, USER_DELIMITER, false);
		}
		else if (contentData.indexOf(DOUBLE_QUOTE_DELIMITER)<0)
		{
			//parse the content with the DEFAULT delimiter
			return parseStringToAarryWithDelimiter(contentData, DEFAULT_DELIMITER, false);
		}
		
		ArrayList<String> fldList=new ArrayList<String>();
		//parse the String containing one or more DOUBLE_QUOTE_DELIMITER
		String [] preRtnData=parseStringToAarryWithDelimiter(contentData,DOUBLE_QUOTE_DELIMITER, true);
		boolean isInField=false;
		for(int i=0;i<preRtnData.length;i++)
		{
			String onePiece=preRtnData[i].trim();
			if (onePiece.equals(DOUBLE_QUOTE_DELIMITER))
			{
				//start or end a field delimitted with DOUBLE_QUOTE_DELIMITER
				if (isInField)
					isInField=false;
				else
					isInField=true;		
			}
			else
			{
				if (isInField)
					fldList.add(onePiece); //the whole content is one field
				else
				{
					//this piece of data is out of DOUBLE_QUOTE_DELIMITER,parse it with default delimiter
					String [] pieceData=parseStringToAarryWithDelimiter(onePiece, DEFAULT_DELIMITER, false);
					if (pieceData.length==0)
						continue;
					for (int j=0;j<pieceData.length;j++)
						 fldList.add(pieceData[j]);
				}
			}
		}
		
		String[] rtnArray=new String[fldList.size()];
	    for (int i=0;i<fldList.size();i++)
	    	   rtnArray[i]=fldList.get(i);
	    
		return rtnArray;
	}
	
	 /**
     * Convert a line into a string array
     * @param lineData
     * @return
     */
    private String[] parseStringToAarryWithDelimiter(String lineData, String delimiter, boolean returnDelimiter)
    {
    	StringTokenizer st=new StringTokenizer(lineData,delimiter,returnDelimiter);
    	String[] rtnArray=new String[st.countTokens()];
    	int i=0;
    	while (st.hasMoreTokens())
    	{	rtnArray[i]=st.nextToken();
    		i++;
    	}
    	return rtnArray;
    }
}
