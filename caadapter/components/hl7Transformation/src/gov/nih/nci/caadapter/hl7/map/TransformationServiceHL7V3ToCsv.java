package gov.nih.nci.caadapter.hl7.map;

import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.map.impl.MapParserImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
		    MapParserImpl parser = new MapParserImpl();
		    MappingResult mappingResult = parser.parse(mapFile.getParent(), new FileReader(mapFile));
			ValidatorResults prepareValidatorResults =mappingResult.getValidatorResults();
			if (!mappingResult.getValidatorResults().isValid())
			{
	            transformationResults.add(new TransformationResult(MessageResources.getMessage("TRF2", new Object[]{}).toString(),
	                prepareValidatorResults));
	            return transformationResults;
	        }
					
			SAXParser saxParser=SAXParserFactory.newInstance().newSAXParser();
			InputSource is=new InputSource(new FileReader(sourceFile));
			HL7V3SaxContentHandler saxContentHandler= new HL7V3SaxContentHandler();
			
			Mapping csvH3sMap=mappingResult.getMapping();
			saxContentHandler.setMapping(csvH3sMap);
			saxParser.parse(is, saxContentHandler);
			System.out.println("TransformationServiceHL7V3ToCsv.process() \n"+saxContentHandler.getCsvDataWrapper());
			String msgGenerated=saxContentHandler.getCsvDataWrapper().toString();
			TransformationResult transferResult=new TransformationResult(msgGenerated , null);
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
