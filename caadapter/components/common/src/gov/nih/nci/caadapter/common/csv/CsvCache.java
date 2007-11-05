/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/CsvCache.java,v 1.2 2007-11-05 17:44:15 wangeug Exp $
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


package gov.nih.nci.caadapter.common.csv;

/**
 * CVS Cache class
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.2 $
 */

//import com.Ostermiller.util.CSVParser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
       LineNumberReader lRd=new LineNumberReader(new InputStreamReader(dataStream));
       
       ArrayList<String[]> rtnList=new ArrayList<String[]>();
       String lValue=lRd.readLine();
       while (lValue!=null&&!lValue.trim().equals(""))
       {
    	   rtnList.add(convertCsvLineToStringAarry(lValue));
    	   lValue=lRd.readLine();
       }
       dataStream.close();
 
//       CSVParser csvp = new CSVParser(dataStream);
//       csvp.getAllValues();

       String[][] rtnArray=new String[rtnList.size()][];
       for (int i=0;i<rtnList.size();i++)
    	   rtnArray[i]=rtnList.get(i);
       return  rtnArray;
    }
    
    /**
     * Convert a line into a string array
     * @param lineData
     * @return
     */
    private static String[] convertCsvLineToStringAarry(String lineData)
    {
    	StringTokenizer st=new StringTokenizer(lineData,",");
    	String[] rtnArray=new String[st.countTokens()];
    	int i=0;
    	while (st.hasMoreTokens())
    	{	rtnArray[i]=st.nextToken();
    		i++;
    	}
    	return rtnArray;
    }
}


