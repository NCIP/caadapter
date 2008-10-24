/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.v2meta;

import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;
import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.csv.CSVDataResult;
import gov.nih.nci.caadapter.common.csv.CSVMetaResult;
import gov.nih.nci.caadapter.common.csv.data.CSVField;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.csv.data.impl.CSVFieldImpl;
import gov.nih.nci.caadapter.common.csv.data.impl.CSVSegmentImpl;
import gov.nih.nci.caadapter.common.csv.data.impl.CSVSegmentedFileImpl;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVFieldMetaImpl;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
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
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2008-10-24 19:37:18 $
 * @since caAdapter v4.2
 */

public class HL7V2XmlSaxContentHandler extends DefaultHandler {

	private MessageElementXmlPath pathToRoot;
	private CSVDataResult dataResult;
	
	public void startDocument() throws SAXException
	{
		super.startDocument();
		pathToRoot=new MessageElementXmlPath();
	}
	
	public void  endElement(String uri, String localName, String qName) throws SAXException 
	{
		super.endElement(uri, localName, qName);
		pathToRoot.removeLeaf(qName);
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		super.startElement(uri, localName, qName, attributes);
		if (dataResult==null)
		{
			//initialize the DataResult for root element
	        ValidatorResults validatorResults = new ValidatorResults();
	        CSVMeta v2CsvMeta=V2MetaXSDUtil.createDefaultCsvMeta(qName);
	        CSVSegmentedFileImpl segmentedFile = new CSVSegmentedFileImpl();
	        
	        dataResult = new CSVDataResult(segmentedFile, validatorResults);
	        CSVSegmentImpl rootSegment = new CSVSegmentImpl(v2CsvMeta.getRootSegment());
	        rootSegment.setXmlPath(qName);
	        rootSegment.setCardinalityType(CardinalityType.VALUE_1);
	        segmentedFile.addLogicalRecord(rootSegment);
		}		
		pathToRoot.add(qName);

	}
	
    public void characters( char[] charSeq, int start, int len )
    {
        String str = new String( charSeq, start, len );
        str = str.trim();
        addDataField(pathToRoot.toString(), str);
    }

	/**
	 * @return the dataResult
	 */
	public CSVDataResult getDataResult() {
		return dataResult;
	}
	
	private void addDataField(String name, String value)
	{
//		System.out.println("HL7V2XmlSaxContentHandler.addDataField()..."+name+"=="+value);
		CSVSegment rootSeg= dataResult.getCsvSegmentedFile().getLogicalRecords().get(0);
		String rootName=rootSeg.getName();
		String fldRealName=name.substring(rootName.length()+1);
		fldRealName=fldRealName.replace(".", "__");
		int colCnt=rootSeg.getFields().size();
		
//		CSVSegmentMeta rootSegMeta=(CSVSegmentMeta)rootSeg.getMetaObject();
//		CSVFieldMeta csvFieldMeta=new CSVFieldMetaImpl(colCnt, fldRealName, rootSegMeta);
//		csvFieldMeta.setXmlPath(pathToRoot.toString());
//		rootSegMeta.addField(csvFieldMeta);
//		CSVField csvData=new CSVFieldImpl(csvFieldMeta, colCnt, value) ;
//		System.out.println("HL7V2XmlSaxContentHandler.addDataField()...pathToRoot:"+pathToRoot);
//		System.out.println("HL7V2XmlSaxContentHandler.addDataField()...fieldMetaXmlPath:"+csvFieldMeta.getXmlPath());
		String fldXmlPath=rootName+"."+fldRealName;
		CSVField csvData=new CSVFieldImpl(null, colCnt, value) ;
		csvData.setXmlPath(fldXmlPath);
		rootSeg.getFields().add(csvData);
	}
}


/**
* HISTORY: $Log: not supported by cvs2svn $
**/