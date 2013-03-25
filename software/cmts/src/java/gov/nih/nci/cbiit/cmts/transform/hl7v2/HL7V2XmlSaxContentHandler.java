/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.transform.hl7v2;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Oct 20, 2008
 * @author   LAST UPDATE: $Author: wangeug 
 * @version  REVISION: $Revision: 1.4 $
 * @date 	 DATE: $Date: 2009-02-06 20:50:27 $
 * @since caAdapter v4.2
 */

public class HL7V2XmlSaxContentHandler extends DefaultHandler {

//	private MessageElementXmlPath pathToRoot;
//	private CSVDataResult resultData;	
//	private Stack<CSVSegment> csvSegmentStack;
	public void startDocument() throws SAXException
	{
		super.startDocument();
		System.out.println("HL7V2XmlSaxContentHandler.startDocument()..start doc");
//		if (resultData==null)
//		{
//			//initialize the DataResult 
//			ValidatorResults validatorResults = new ValidatorResults();
//			CSVSegmentedFileImpl segmentedFile = new CSVSegmentedFileImpl();
//			resultData=new CSVDataResult(segmentedFile, validatorResults);
//		}
//		pathToRoot=new MessageElementXmlPath();
//        csvSegmentStack=new Stack<CSVSegment>();
	}
	
 	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		super.startElement(uri, localName, qName, attributes);
		System.out.println("HL7V2XmlSaxContentHandler.startElement()..:"+qName);
//		String xmlName=qName.replace(".","_");
//		pathToRoot.add(xmlName);
////		Log.logInfo(this, "Start Element:"+qName +"..."+localName);
//		if (csvSegmentStack.isEmpty())
//		{
//			Log.logInfo(this, "Initialize Root Element:"+qName +"..."+localName);
//	        
//	        //The CVSMeta is required to retrieve the CVS rootSegemt name or message type
//	        CSVMeta v2CsvMeta=V2MetaXSDUtil.createDefaultCsvMeta(xmlName);
//	        CSVSegmentImpl rootSegment = new CSVSegmentImpl(v2CsvMeta.getRootSegment());
//	        rootSegment.setXmlPath(xmlName);
//	        rootSegment.setCardinalityType(CardinalityType.VALUE_1);
//	        
//	        //add the one and only one  rootSegment to CSVSegmentFile for each message
//	        CSVSegmentedFileImpl segmentedFile = (CSVSegmentedFileImpl)resultData.getCsvSegmentedFile();
//	        segmentedFile.addLogicalRecord(rootSegment);
//	        csvSegmentStack.push(rootSegment);
//		}		
//		else
//		{
//			//create CsvSegmentMeta
//			CSVSegment parentSeg=csvSegmentStack.peek();
////			CSVSegmentMeta parentMeta=(CSVSegmentMeta)parentSeg.getMetaObject();
////			CSVSegmentMetaImpl segmentMeta=new CSVSegmentMetaImpl(xmlName, parentMeta);
////			parentMeta.addSegment(segmentMeta);
//			
//			//create CsvSegment data
//			CSVSegmentImpl elementSeg=new CSVSegmentImpl(null);//segmentMeta);
//			elementSeg.setXmlPath(pathToRoot.toString());
// 
//        	//add the new segment       
//	        elementSeg.setParentSegment(parentSeg);
//	        parentSeg.addChildSegment(elementSeg);
//	        csvSegmentStack.push(elementSeg);
//		}
	}

	public void  endElement(String uri, String localName, String qName) throws SAXException 
	{
		super.endElement(uri, localName, qName);
		System.out.println("HL7V2XmlSaxContentHandler.endElement()..:"+qName);
//		String xmlName=qName.replace(".","_");
//		pathToRoot.removeLeaf(xmlName);
//		//remove one CSVSegment
//		CSVSegment segment=csvSegmentStack.pop();

	}
	
    public void characters( char[] charSeq, int start, int len )
    {
        String str = new String( charSeq, start, len );
        str = str.trim();
        processInlineData(str);
    }


	/**
	 * Add an inline data to the CSVResult data structure
	 * @param value
	 */
	private void processInlineData(String value)
	{
		System.out.println("HL7V2XmlSaxContentHandler.processInlineData()..:"+value);
//		/*
//		 	Assumptions:
//		  	1.Only the deepest element has inline value
//		  	2.All data value are carried with inline value; no element has any attribute
//		  	Processing	  	
//		  	1. Create a CSVField to hold the inline value (String)
//		  	2. Add the CSVField to parent CSVSegment
//		  	3. Remove the CSVSegment of the current element from its parent
//		 */
// 		CSVSegment mySegment=this.csvSegmentStack.peek(); 				
//		//remove current segment from parentSegment	
//		CSVSegment parentSegment=mySegment.getParentSegment();
//		parentSegment.getChildSegments().remove(mySegment);
//		
// 	
//		//create CSVFieldMeta
//		String fldXmlPath=mySegment.getXmlPath();
//		int colCnt=parentSegment.getFields().size(); //start from 0
// 
//		
//		//create CSVField and add it to parent CSVSegment
//		CSVField csvData=new CSVFieldImpl(null, colCnt, value) ;
//		csvData.setXmlPath(fldXmlPath);
//		parentSegment.getFields().add(csvData);	
	}
}

