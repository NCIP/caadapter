package gov.nih.nci.caadapter.hl7.map;

import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.csv.CSVDataResult;
import gov.nih.nci.caadapter.common.util.GeneralTask;
import gov.nih.nci.caadapter.common.util.Stats;
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

	private String msgGenerated;
	private Stats statistics = null;
	
	private File mapFile = null;
	private File sourceFile = null;
	private File specFile = null;
	private Mapping mapping = null;
	private ValidatorResults prepareValidatorResults = new ValidatorResults();
	private boolean preparedFlag = false;
	  
	public TransformationServiceHL7V3ToCsv(String sourceFileName, String mapFileName)
	{
		this(new File( sourceFileName ),new File(mapFileName));		
	}
	
	public TransformationServiceHL7V3ToCsv(File source , File map )
	{
		sourceFile=source;
		mapFile=map;
	}
 
	public List<TransformationResult> process(GeneralTask task) 
	{
		List<TransformationResult> transformationResults = new ArrayList<TransformationResult>();
		if (!preparedFlag)
        {
			try {
				verifyMappingAndCsvSpecfication();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            if (!prepareValidatorResults.isValid())
            {
                transformationResults.add(new TransformationResult(MessageResources.getMessage("TRF2", new Object[]{}).toString(),
                    prepareValidatorResults));
                return transformationResults;
            }
        }

		try {
			msgGenerated="";
			SAXParser saxParser=SAXParserFactory.newInstance().newSAXParser();
			InputSource is=new InputSource(new FileReader(sourceFile));
			HL7V3SaxContentHandler saxContentHandler= new HL7V3SaxContentHandler();
			saxContentHandler.setMapping(mapping);
			saxParser.parse(is, saxContentHandler);
			System.out.println("TransformationServiceHL7V3ToCsv.process() \n"+saxContentHandler.getCsvDataWrapper());
			msgGenerated=saxContentHandler.getCsvDataWrapper().toString();
			//			System.out.println("TransformationServiceHL7V3ToCsv.process() \n"+saxContentHandler.getCsvDataWrapper().getBuildCSVResults());
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
		}
		return transformationResults;
	}

	 private MappingResult parseMapfile() throws Exception
	 {
	        MapParserImpl parser = new MapParserImpl();
	        long begintime = System.currentTimeMillis();
	        MappingResult mappingResult = parser.parse(mapFile.getParent(), new FileReader(mapFile));
	        statistics.mapParseTime += System.currentTimeMillis() - begintime;
	        return mappingResult;
	 }
	 
	private void verifyMappingAndCsvSpecfication() throws Exception
    {
        // statistics.
    	statistics = new Stats();
        statistics.begintime = System.currentTimeMillis();
        statistics.mapFileName = mapFile.getName();
        if (sourceFile != null)   
        	statistics.sourceFileName = sourceFile.getName();
        // parse the mapFileName, if there are errors, return with validate results
        MappingResult mappingResult = parseMapfile();
        final ValidatorResults mappingValidatorResults = mappingResult.getValidatorResults();
        prepareValidatorResults.addValidatorResults(mappingValidatorResults);

        if (!mappingValidatorResults.isValid())
        {
            return;
        }

        mapping = mappingResult.getMapping();
        specFile = new File(mapping.getSourceComponent().getFileAbsolutePath());
/*
        // parse the datafile, if there are errors.. return.
        CSVDataResult csvDataResult = null;

        if (inputStringFlag) {
        	csvDataResult= parseCsvString();
        }
        else {
        	csvDataResult= parseCsvfile();
        }
        final ValidatorResults csvDataValidatorResults = csvDataResult.getValidatorResults();
        prepareValidatorResults.addValidatorResults(csvDataValidatorResults);

        if (!csvDataValidatorResults.isValid())
        {
            return ;
        }

        csvSegmentedFile = csvDataResult.getCsvSegmentedFile();
*/
        // set the statistics
        statistics.mapFilesize = mapFile.length();
        statistics.scsFilesize = specFile.length();
        statistics.h3sFilesize = new File(mapping.getTargetComponent().getFileAbsolutePath()).length();
        if (sourceFile != null) 
        	statistics.sourceFilesize = sourceFile.length();
        
        preparedFlag = true;
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
		svc.process(null);
	}

	public String getMsgGenerated() {
		return msgGenerated;
	}
	
}
