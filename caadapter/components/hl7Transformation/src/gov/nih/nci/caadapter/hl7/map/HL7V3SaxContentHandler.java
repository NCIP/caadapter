package gov.nih.nci.caadapter.hl7.map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.data.impl.CSVSegmentedFileExtension;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

public class HL7V3SaxContentHandler extends DefaultHandler {

	private MessageElementXmlPath pathToRoot;
	private Mapping mapping;
	private MapProcessorHelper mapHelper;
	private CSVSegmentedFileExtension csvDataWrapper;
	private ValidatorResults parseDocumentResults;
	
	public void startDocument() throws SAXException
	{
		super.startDocument();
		pathToRoot=new MessageElementXmlPath();
		//retrive csv MetaData
		CSVMeta csvMeta=(CSVMeta)getMapping().getSourceComponent().getMeta();
		csvDataWrapper = new CSVSegmentedFileExtension();
		
		parseDocumentResults=new ValidatorResults();
		Message msg = MessageResources.getMessage("HL7TOCSV0", new Object[]{"Start"});
		parseDocumentResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.INFO, msg));
		csvDataWrapper.setBuildCSVResult(parseDocumentResults);
		Log.logInfo(this, msg);
		csvDataWrapper.setCsvMeta(csvMeta);
	}
	
	public void  endElement(String uri, String localName, String qName) throws SAXException 
	{
		super.endElement(uri, localName, qName);
		pathToRoot.removeLeaf(qName);
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		super.startElement(uri, localName, qName, attributes);
		//remove the MessageType from the element name and add it to xmlPath
		if (qName.indexOf(".")>-1)
			pathToRoot.add(qName.substring(qName.indexOf(".")+1));
		else
			pathToRoot.add(qName);
		int attrLength=attributes.getLength();
		for (int i=0;i<attrLength;i++)
		{
			String attrQName=attributes.getQName(i);
			String attrValue=attributes.getValue(i);
			if (attrValue.length()==0)
				continue;
        	String srcFieldRef=mapHelper.findSourceDataReferenceFromTargetPath(pathToRoot.getPathValue()+"."+attrQName);
        	if (srcFieldRef!=null)
        		csvDataWrapper.insertCsvField(srcFieldRef, attrValue);	
        	else
        	{
        		Message msg = MessageResources.getMessage("HL7TOCSV0", new Object[]{pathToRoot+ " attribute is not mapped ... "+attrQName +" = "+attrValue});
        		parseDocumentResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));;
        		Log.logWarning(this, msg);
        	}
		}
	}
	
    public void characters( char[] charSeq, int start, int len )
    {
        String str = new String( charSeq, start, len );
        str = str.trim();
        if (str.length() > 0) {
        	String srcFieldRef=mapHelper.findSourceDataReferenceFromTargetPath(pathToRoot.getPathValue());//+".inlineText");
        	if (srcFieldRef!=null)
        		csvDataWrapper.insertCsvField(srcFieldRef, str);
        	else
        	{
        		Message msg = MessageResources.getMessage("HL7TOCSV0", new Object[]{pathToRoot+ " is not mapped ... value: "+str});
        		parseDocumentResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));;
        		Log.logWarning(this, msg);
        	}
       }
    }
    
	public Mapping getMapping() {
		return mapping;
	}
	public void setMapping(Mapping userMapping) {
		this.mapping = userMapping;
		mapHelper=new MapProcessorHelper(mapping);
	}

	public CSVSegmentedFileExtension getCsvDataWrapper() {
		return csvDataWrapper;
	}

	public ValidatorResults getParseDocumentResults() {
		return parseDocumentResults;
	}
	
}
