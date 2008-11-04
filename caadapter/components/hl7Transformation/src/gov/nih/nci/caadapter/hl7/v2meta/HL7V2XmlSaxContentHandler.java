/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.v2meta;

import java.util.Stack;

import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;
import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.csv.CSVDataResult;
import gov.nih.nci.caadapter.common.csv.data.CSVField;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.csv.data.impl.CSVFieldImpl;
import gov.nih.nci.caadapter.common.csv.data.impl.CSVSegmentImpl;
import gov.nih.nci.caadapter.common.csv.data.impl.CSVSegmentedFileImpl;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
//import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
//import gov.nih.nci.caadapter.common.csv.meta.impl.CSVFieldMetaImpl;
//import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
//import gov.nih.nci.caadapter.common.csv.meta.impl.CSVSegmentMetaImpl;
//import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.v3csv.MessageElementXmlPath;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Oct 20, 2008
 * @author   LAST UPDATE: $Author: wangeug 
 * @version  REVISION: $Revision: 1.2 $
 * @date 	 DATE: $Date: 2008-11-04 21:07:54 $
 * @since caAdapter v4.2
 */

public class HL7V2XmlSaxContentHandler extends DefaultHandler {

	private MessageElementXmlPath pathToRoot;
	private CSVDataResult dataResult;
	private Stack<CSVSegment> csvSegmentStack;
	public void startDocument() throws SAXException
	{
		super.startDocument();
		pathToRoot=new MessageElementXmlPath();
        csvSegmentStack=new Stack<CSVSegment>();
	}
	
 
	public void  endElement(String uri, String localName, String qName) throws SAXException 
	{
		super.endElement(uri, localName, qName);
		String xmlName=qName.replace(".","_");
		pathToRoot.removeLeaf(xmlName);
		//remove one CSVSegment
		CSVSegment segment=csvSegmentStack.pop();
		Log.logInfo(this, "End Element:"+qName +"..."+localName);
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		super.startElement(uri, localName, qName, attributes);
		String xmlName=qName.replace(".","_");
		pathToRoot.add(xmlName);
		Log.logInfo(this, "Start Element:"+qName +"..."+localName);
		if (dataResult==null)
		{
			//initialize the DataResult for root element
	        ValidatorResults validatorResults = new ValidatorResults();
	        //The CVSMeta is required to retrieve the CVS rootSegemt name or message type
	        CSVMeta v2CsvMeta=V2MetaXSDUtil.createDefaultCsvMeta(xmlName);
	        CSVSegmentedFileImpl segmentedFile = new CSVSegmentedFileImpl();
	        CSVSegmentImpl rootSegment = new CSVSegmentImpl(v2CsvMeta.getRootSegment());
	        rootSegment.setXmlPath(xmlName);
	        rootSegment.setCardinalityType(CardinalityType.VALUE_1);
	        
	        //add the one and only one  rootSegment to CSVSegmentFile for each message
	        segmentedFile.addLogicalRecord(rootSegment);
	        dataResult = new CSVDataResult(segmentedFile, validatorResults);
	        csvSegmentStack.push(rootSegment);
		}		
		else
		{
			//create CsvSegmentMeta
			CSVSegment parentSeg=csvSegmentStack.peek();
//			CSVSegmentMeta parentMeta=(CSVSegmentMeta)parentSeg.getMetaObject();
//			CSVSegmentMetaImpl segmentMeta=new CSVSegmentMetaImpl(xmlName, parentMeta);
//			parentMeta.addSegment(segmentMeta);
			
			//create CsvSegment data
			CSVSegmentImpl elementSeg=new CSVSegmentImpl(null);//segmentMeta);
			elementSeg.setXmlPath(pathToRoot.toString());
 
        	//add the new segment       
	        elementSeg.setParentSegment(parentSeg);
	        parentSeg.addChildSegment(elementSeg);
	        csvSegmentStack.push(elementSeg);
		}
	}
	
    public void characters( char[] charSeq, int start, int len )
    {
        String str = new String( charSeq, start, len );
        str = str.trim();
        addDataField(str);
    }

	/**
	 * @return the dataResult
	 */
	public CSVDataResult getDataResult() {
		return dataResult;
	}
	
	private void addDataField(String value)
	{
		//assume only the deepest element has inline value
		//the field should be added to its parent element
		CSVSegment mySegment=this.csvSegmentStack.pop();
				
		//remove current segment from parentSegment	
		CSVSegment parentSegment=this.csvSegmentStack.peek();
		parentSegment.getChildSegments().remove(mySegment);
		//remove current CSVSegmentMeta from parentMeta:not required
//		CSVSegmentMeta mySegmentMeta=(CSVSegmentMeta)mySegment.getMetaObject();
//		CSVSegmentMeta parentSegMeta=(CSVSegmentMeta)parentSegment.getMetaObject();
//		parentSegMeta.removeSegment(mySegmentMeta);
		
		//create CSVFieldMeta
		String fldXmlPath=mySegment.getXmlPath();
		int colCnt=parentSegment.getFields().size(); //start from 0
//		String fldName=fldXmlPath.substring(fldXmlPath.lastIndexOf(".")+1);
//		CSVFieldMeta csvFieldMeta=new CSVFieldMetaImpl(colCnt, fldName, parentSegMeta);
//		csvFieldMeta.setXmlPath(pathToRoot.toString());
//		parentSegMeta.addField(csvFieldMeta);
		
		//create CSVField and add it to parent CSVSegment
		CSVField csvData=new CSVFieldImpl(null, colCnt, value) ;
		csvData.setXmlPath(fldXmlPath);
		parentSegment.getFields().add(csvData);
		
		//the current CSV segment will be popped at "endElement"
		csvSegmentStack.push(mySegment);		
	}
}


/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.1  2008/10/24 19:37:18  wangeug
* HISTORY: transfer a v2 message into v3 message using SUN v2 schema
* HISTORY:
**/