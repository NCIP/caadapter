/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.hl7.map;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

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
	private Hashtable<String,String> linkMapping;
	private Hashtable <String, FunctionComponent> functions = new Hashtable<String, FunctionComponent>();
	private CSVSegmentedFileExtension csvDataWrapper;
	private ValidatorResults parseDocumentResults;
	private CSVMeta csvMeta;

	public void startDocument() throws SAXException
	{
		super.startDocument();
		pathToRoot=new MessageElementXmlPath();
		//retrive csv MetaData
		csvDataWrapper = new CSVSegmentedFileExtension();
		parseDocumentResults=new ValidatorResults();
		Message msg = MessageResources.getMessage("HL7TOCSV0", new Object[]{"Start"});
		parseDocumentResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.INFO, msg));
		csvDataWrapper.setBuildCSVResult(parseDocumentResults);
		csvDataWrapper.setCsvMeta(csvMeta);
		Log.logInfo(this, msg);
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
		if (pathToRoot.size()==1) //reformat xmlpath
			reformatLinkMapping();

		int attrLength=attributes.getLength();
		for (int i=0;i<attrLength;i++)
		{
			String attrQName=attributes.getQName(i);
			String attrValue=attributes.getValue(i);
			if (attrValue.length()==0)
				continue;
        	String srcFieldRef=findCsvReferenceFieldPath(pathToRoot.getPathValue()+"."+attrQName);
        	if (srcFieldRef!=null)
        		csvDataWrapper.insertCsvField(srcFieldRef, attrValue);
        	else
        	{
        		Message msg = MessageResources.getMessage("HL7TOCSV1", new Object[]{pathToRoot+ "."+attrQName, attrValue});
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
        	String srcFieldRef=findCsvReferenceFieldPath(pathToRoot.getPathValue()+".inlineText");
        	if (srcFieldRef!=null)
        		csvDataWrapper.insertCsvField(srcFieldRef, str);
        	else
        	{
        		Message msg = MessageResources.getMessage("HL7TOCSV1", new Object[]{pathToRoot+ " .inlineText",str});
        		parseDocumentResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));;
        		Log.logWarning(this, msg);
        	}
       }
    }


	public CSVSegmentedFileExtension getCsvDataWrapper() {
		return csvDataWrapper;
	}

	public ValidatorResults getParseDocumentResults() {
		return parseDocumentResults;
	}

	public Hashtable<String, FunctionComponent> getFunctions() {
		return functions;
	}

	public void setFunctions(Hashtable<String, FunctionComponent> functions) {
		this.functions = functions;
	}

	public Hashtable<String, String> getLinkMapping() {
		return linkMapping;
	}
	public void setLinkMapping(Hashtable<String, String> linkxmlMapp) {
		linkMapping=linkxmlMapp;
	}
	public void reformatLinkMapping()
	{
		Enumeration keys=linkMapping.keys();
		while(keys.hasMoreElements())
		{
			String targetXmlPath=(String)keys.nextElement();
			String srcXmlPath =linkMapping.get(targetXmlPath);

//			reset the target element xmlPath removing index
			linkMapping.put(resetTargetElementXmlPath(targetXmlPath), srcXmlPath);
		}

	}

	public CSVMeta getCsvMeta() {
		return csvMeta;
	}

	public void setCsvMeta(CSVMeta csvMetaNew) {
		csvMeta = csvMetaNew;
	}

	private String resetTargetElementXmlPath(String oldxmlPath)
	{
		if (!oldxmlPath.startsWith(pathToRoot.getRootName()))
				return oldxmlPath; //do not reformat for CSV element xmlPath
		StringBuffer sb=new StringBuffer();
		StringTokenizer  tk=new StringTokenizer(oldxmlPath,".");
		while (tk.hasMoreTokens())
		{
			String nxtTk=tk.nextToken();
			String lstTwoLetters=nxtTk.substring(nxtTk.length()-2);
			try
			{
				Integer.parseInt(lstTwoLetters);
				//remove the last two letter
				sb.append("."+nxtTk.substring(0, nxtTk.length()-2));
			}
			catch(NumberFormatException ne)
			{
				sb.append("."+nxtTk);
			}

		}
		return sb.toString().substring(1);//remove the first "."
	}

	private String findCsvReferenceFieldPath(String hl7Path)
	{
		if (hl7Path==null)
			return null;
		String srcRef=linkMapping.get(hl7Path);
		if (srcRef==null)
			return srcRef;
		else if (srcRef.startsWith("function"))
		{
			String fcOutput=srcRef;
			String fcName=fcOutput.substring(0, srcRef.indexOf("outputs"));
			String fcInput=fcName+"inputs.0";
			srcRef=linkMapping.get(fcInput);
			String msgPara="Construct function HL7 target:"	+hl7Path
			+" ... function output:"+fcOutput
			+ " ... function input:"+fcInput
			+"... CSV Field:"+srcRef;
    		Message msg = MessageResources.getMessage("HL7TOCSV0", new Object[]{msgPara});
    		parseDocumentResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.INFO, msg));;
		}
		return srcRef;
	}
}
