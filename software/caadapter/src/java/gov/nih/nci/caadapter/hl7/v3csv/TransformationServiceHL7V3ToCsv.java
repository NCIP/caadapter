/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.hl7.v3csv;
/**
 * The class defines the engine to transfer a HL7 v3 message into a CSV data set.
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2008-10-24 19:35:04 $
 */
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.map.TransformationResult;
import gov.nih.nci.caadapter.hl7.transformation.MapParser;
import gov.nih.nci.caadapter.common.csv.CSVMetaParserImpl;
import gov.nih.nci.caadapter.common.csv.CSVMetaResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TransformationServiceHL7V3ToCsv  {

	private File mapFile = null;
	private File sourceFile = null;

	public TransformationServiceHL7V3ToCsv(String sourceFileName, String mapFileName)
	{
		this(new File( sourceFileName ),new File(mapFileName));
	}

	public TransformationServiceHL7V3ToCsv(File source , File map )
	{
		sourceFile=source;
		mapFile=map;
	}

	public List<TransformationResult> process()
	{
		List<TransformationResult> transformationResults = new ArrayList<TransformationResult>();
		try {
			SAXParser saxParser=SAXParserFactory.newInstance().newSAXParser();
			HL7V3SaxContentHandler saxContentHandler= new HL7V3SaxContentHandler();

	        MapParser mapParser = new MapParser();
	        Hashtable mappings = mapParser.processOpenMapFile(mapFile);
	        saxContentHandler.setLinkMapping(mappings);
	        saxContentHandler.setFunctions(mapParser.getFunctions());
	        ValidatorResults mappingValidatorResults=mapParser.getValidatorResults();
	        if (!mappingValidatorResults.isValid())
	        {
	        	String msg="Mapping validation failed !!";
	        	transformationResults.add(new TransformationResult(msg,mappingValidatorResults ));
	        	return transformationResults;
	        }

	        String fullCsvfilepath = FileUtil.filenameLocate(mapFile.getParent(), mapParser.getSourceSpecFileName());
	        FileReader fileReader = new FileReader(new File(fullCsvfilepath));
            CSVMetaParserImpl csvParser = new CSVMetaParserImpl();
            CSVMetaResult csvMetaResult = csvParser.parse(fileReader);
            if (csvMetaResult!=null
            		&&csvMetaResult.getValidatorResults()!=null
            		&&!csvMetaResult.getValidatorResults().isValid())
            {
            	String msg="CSV specification validation failed !!";
	        	transformationResults.add(new TransformationResult(msg,csvMetaResult.getValidatorResults() ));
	        	return transformationResults;
            }
            saxContentHandler.setCsvMeta(csvMetaResult.getCsvMeta());

			saxParser.parse(new InputSource(new FileReader(sourceFile)), saxContentHandler);
			String msgGenerated=saxContentHandler.getCsvDataWrapper().toString();
			if (!saxContentHandler.getParseDocumentResults().isValid())
	        {
	        	String msg="Document parsing validation failed !!";
	        	transformationResults.add(new TransformationResult(msg,saxContentHandler.getParseDocumentResults()));
	        	return transformationResults;
	        }

			ValidatorResults prcValidatorResults=saxContentHandler.getParseDocumentResults();
			prcValidatorResults.addValidatorResults(csvMetaResult.getValidatorResults());
			prcValidatorResults.addValidatorResults(mappingValidatorResults);
			TransformationResult transferResult=new TransformationResult(msgGenerated , saxContentHandler.getParseDocumentResults());
			transformationResults.add(transferResult);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return transformationResults;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String usrDir= System.getProperty("user.dir");
		System.out.println("HLV3MessageSaxParser.main() user.dir:"+usrDir);
		//String mapFileName="workingspace\\examples\\150003\\150003_test.map";
		String mapFileName="workingspace\\examples\\mif150003\\mif150003.map";
		String fName=usrDir+"\\workingspace\\examples\\mif150003\\example15003_1.xml";
		TransformationServiceHL7V3ToCsv svc= new TransformationServiceHL7V3ToCsv(mapFileName,fName);
		svc.process();
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.7  2008/09/29 15:47:19  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */