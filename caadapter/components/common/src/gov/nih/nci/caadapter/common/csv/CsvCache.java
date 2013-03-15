/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common.csv;

/**
 * CVS Cache class
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @version $Revision: 1.6 $
 */

//import com.Ostermiller.util.CSVParser;


import gov.nih.nci.caadapter.common.util.CaadapterUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class CsvCache {

    private static final HashMap<String, String[][]> cache = new HashMap<String,String[][]>();
    private static final HashMap<String, Long> timestamp = new HashMap<String,Long>();

    public static synchronized String[][] getCsv(String filename) throws FileNotFoundException, IOException {
        String[][] returnArray = (String[][]) cache.get(filename);
        File f = new File(filename);
        long lastmodified = f.lastModified();

        // if the file is in the cache && the most current
        if (returnArray != null && lastmodified == (Long)timestamp.get(filename))
        {
            // do nothing.
        	return returnArray;
        }
        else
        {
            // otherwise, open it up and cache it.
            cache.remove(filename);
            timestamp.remove(filename);
            returnArray = getCsvFromInputStream(new FileInputStream(f));
            cache.put(filename, returnArray);
            timestamp.put(filename, new Long(lastmodified));
        }
        return returnArray;
    }

    public static synchronized String[][] getCsvFromString(String dataString) throws FileNotFoundException, IOException {
        return getCsvFromInputStream(new ByteArrayInputStream(dataString.getBytes()));
    }

    /**
     * Read data from a CSV stream and convert it into a String array of Segments and Fields
     * @param dataStream
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static synchronized String[][] getCsvFromInputStream(InputStream dataStream) throws FileNotFoundException, IOException {
//    	BufferedReader inBf = new BufferedReader(new InputStreamReader(dataStream));
//    	ArrayList<String[]> rtnList=new ArrayList<String[]>();
//    	String lValue="";
//        String lastValue="";
//        boolean isComplete=true;
//    	StringBuffer readBf=new StringBuffer();
//    	int inChar=inBf.read();
//
//    	while (inChar!=-1)
//    	{
//    		if (inChar=='\n')
//    		{
//    			lValue=readBf.toString();
//    			readBf=new StringBuffer();
//
//			   isComplete=isDoubleQuoteClosed(isComplete,lValue);
//			   if (isComplete)
//			   {
//		    	   CsvDataStringParser cParser;
//		    	   if (lastValue.equals(""))
//		    		   cParser=new CsvDataStringParser(lValue);
//		    	   else
//		    		   cParser=new CsvDataStringParser(lastValue+CaadapterUtil.LINEFEED_ENCODE+lValue);
//		    	   lastValue="";
//		    	   rtnList.add(cParser.getDataFields());
//			   }
//			   else
//			   {
//				   if (lastValue.equals(""))
//		    	   {
//		    		   lastValue=lValue;
//		    	   }
//		    	   else
//		    		   lastValue=lastValue+CaadapterUtil.LINEFEED_ENCODE+lValue;
//		   		}
//    		}
//    		else if (inChar==13)
//    			readBf.append(CaadapterUtil.CARTRIAGE_RETURN_ENCODE);
//    		else
//    			readBf.append(Character.toString((char)inChar));
//    		inChar=inBf.read();
//    	}
    	LineNumberReader lRd=new LineNumberReader(new InputStreamReader(dataStream));

       ArrayList<String[]> rtnList=new ArrayList<String[]>();
       String lValue=lRd.readLine();
       String lastValue="";
       boolean isComplete=true;

       while (lValue!=null&&!lValue.trim().equals(""))
       {
    	   //check if this line is a complete new line
    	   if (lValue.indexOf("\r")>-1)
    			   System.out.println("CsvCache.getCsvFromInputStream()..found CR:"+lValue);

    	   isComplete=isDoubleQuoteClosed(isComplete,lValue);
    	   if (isComplete)
    	   {
	    	   CsvDataStringParser cParser;
	    	   if (lastValue.equals(""))
	    		   cParser=new CsvDataStringParser(lValue);
	    	   else
	    		   cParser=new CsvDataStringParser(lastValue+CaadapterUtil.LINEFEED_ENCODE+lValue);
	    	   lastValue="";
	    	   rtnList.add(cParser.getDataFields());
    	   }
    	   else
    	   {
    		   if (lastValue.equals(""))
	    	   {
	    		   lastValue=lValue;
	    	   }
	    	   else
	    		   lastValue=lastValue+CaadapterUtil.LINEFEED_ENCODE+lValue;
       		}
    	   lValue=lRd.readLine();
       }
       dataStream.close();

       String[][] rtnArray=new String[rtnList.size()][];
       for (int i=0;i<rtnList.size();i++)
    	   rtnArray[i]=rtnList.get(i);
       return  rtnArray;
    }

    /**
     * 1. If it is within a double quote, there should be ODD number of double-quote
     * 2. If it is not within a double quote, there should be EVEN number of double-quote
     * @param isNewSegment
     * @param newLineContent
     * @return
     */
    protected static boolean isDoubleQuoteClosed(boolean searchNewQuote,String newLineContent )
    {
    	if (newLineContent==null||newLineContent.equalsIgnoreCase(""))
    		return searchNewQuote;
    	//there is not any DOUBLE_QUOTE in the string
    	if (!newLineContent.contains("\""))
    		return searchNewQuote;

    	for (int i=0;i<newLineContent.length();i++)
    	{
    		if (newLineContent.charAt(i)=='\"')
    			searchNewQuote=!searchNewQuote;
    	}
    	return searchNewQuote;
    }

    protected static String readLineWithCarriageReturn(InputStream dataStream)
    {
    	StringBuffer rtnSb=new StringBuffer();

    	return rtnSb.toString();

    }
}


