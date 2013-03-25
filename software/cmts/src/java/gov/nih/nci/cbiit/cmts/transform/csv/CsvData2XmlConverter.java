/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.transform.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import gov.nih.nci.caadapter.common.csv.CSVDataResult;
import gov.nih.nci.caadapter.common.csv.data.CSVField;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.csv.data.CSVSegmentedFile;

public class CsvData2XmlConverter {
	private StringBuffer xmlBuffer;
	public CsvData2XmlConverter(CSVDataResult csvData)
	{
		initXmlbuffer(csvData);
	}
	
	private void initXmlbuffer(CSVDataResult csvData)
	{
		xmlBuffer =new StringBuffer();
		xmlBuffer.append("<?xml version=\"1.0\" ?>\n");
		CSVSegmentedFile segFile=csvData.getCsvSegmentedFile();
		for (CSVSegment seg:segFile.getLogicalRecords())
		{
			processCsvSegment(seg, "");
		}
	}

	private void processCsvSegment(CSVSegment seg, String indent)
	{
		//process CSV fields
		xmlBuffer.append(indent+"<"+seg.getName());
		for(CSVField field:seg.getFields())
		{
			xmlBuffer.append(" "+field.getName()+"=\""+field.getValue()+"\"");
		}
		xmlBuffer.append(">\n");
		//process child CSV segments
		for (CSVSegment childSeg:seg.getChildSegments())
		{
			processCsvSegment(childSeg, indent+"\t");
		}
		xmlBuffer.append(indent+"</"+seg.getName()+">\n");
	}
	
	public String getXmlString()
	{
		return xmlBuffer.toString();
	}
	
	public String writeXml2File(String outputFile)
	{
		String fileName=outputFile;
		if (fileName==null||fileName.isEmpty())
			fileName="xmlFile"+Calendar.getInstance().getTimeInMillis();
		
		try
		{     
			File outFile = new File(fileName); 
			FileWriter out = new FileWriter(outFile);
			out.write(getXmlString());
			out.close();
     
		} catch (IOException e) 
		{ 
			e.printStackTrace(); 
    
		} 
		return fileName;
	}
}
