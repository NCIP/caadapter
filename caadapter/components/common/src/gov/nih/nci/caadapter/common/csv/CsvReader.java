/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.common.csv;

import gov.nih.nci.caadapter.common.util.CaadapterUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.ApplicationException;

/**
 * Reader for reading CSV logical records from an input stream
 * @author Chunqing Lin
 *
 */
public class CsvReader {
	private InputStream dataStream;
	private CSVMeta csvMeta;
	private String rootName;
	private LineNumberReader lRd;
	private String[] nextHeader = null;
	private boolean hasMoreRecord = false;
	private long readCount = 0;

	public CsvReader(InputStream data, CSVMeta meta) throws IOException
	{
		this.dataStream = data;
		this.csvMeta = meta;
		this.rootName = csvMeta.getRootSegment().getName();
		lRd =new LineNumberReader(new InputStreamReader(dataStream));
		this.readCount = 0;
		nextHeader = this.getNextCsvSegment();
		if(!nextHeader[0].equalsIgnoreCase(rootName))
			throw new IOException("Root segment name not valid as in CVS Meta data.");
		hasMoreRecord = true;
	}

	/**
	 * @return has more record in input stream
	 */
	public synchronized boolean hasMoreRecord()
	{
		return this.hasMoreRecord;
	}
	/**
	 * @return the readCount
	 */
	public long getReadCount() {
		return readCount;
	}

	/**
	 * @return next logical record
	 * @throws IOException
	 * @throws ApplicationException
	 */
	public synchronized CSVDataResult getNextRecord() throws IOException, ApplicationException
	{
		ArrayList<String[]> list = new ArrayList<String[]>();
		if(nextHeader!=null) list.add(nextHeader);
		while(this.hasMoreRecord){
			String[] nextSegment = this.getNextCsvSegment();
			if(nextSegment==null || nextSegment.length==0){
				this.nextHeader = null;
				this.close();
				break;
			}
			if(nextSegment[0].equalsIgnoreCase(rootName))
			{
				this.nextHeader = nextSegment;
				break;
			}else
				list.add(nextSegment);
		}
		String[][] record=new String[list.size()][];

		for (int i=0;i<list.size();i++)
		{
			record[i]=list.get(i);
			//System.out.println("["+i+"]"+Arrays.toString(record[i]));
		}
		return SegmentedCSVParserImpl.parse(record, csvMeta);
	}

	private String[] getNextCsvSegment() throws IOException
	{
		StringBuffer csvLine = new StringBuffer();
		boolean isComplete=true;

		String lValue=lRd.readLine();
		if(lValue!=null) readCount += lValue.length();
		while (lValue!=null&&!lValue.trim().equals(""))
		{
			//check if this line is a complete new line
			if (lValue.indexOf("\r")>-1)
				System.out.println("CsvCache.getCsvFromInputStream()..found CR:"+lValue);

			isComplete=CsvCache.isDoubleQuoteClosed(isComplete,lValue);
			csvLine.append(csvLine.length()==0?"":CaadapterUtil.LINEFEED_ENCODE).append(lValue);
			if(isComplete) break;
			lValue=lRd.readLine();
		}
		if(csvLine.length()==0) return null;
		CsvDataStringParser cParser = new CsvDataStringParser(csvLine.toString());
		String[] ret = cParser.getDataFields();
		//System.out.println("read next line:"+Arrays.toString(ret));
		return ret;
	}

	public synchronized void close() throws IOException
	{
		this.hasMoreRecord = false;
		this.dataStream.close();
	}

}
