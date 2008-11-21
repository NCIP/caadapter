/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.hl7.transformation;

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.csv.CSVMetaParserImpl;
import gov.nih.nci.caadapter.common.csv.CSVMetaResult;
import gov.nih.nci.caadapter.common.csv.CsvReader;
import gov.nih.nci.caadapter.common.csv.CSVDataResult;
import gov.nih.nci.caadapter.common.csv.SegmentedCSVParserImpl;
import gov.nih.nci.caadapter.common.csv.data.CSVSegmentedFile;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.XmlToMIFImporter;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;
import gov.nih.nci.caadapter.hl7.v2meta.HL7V2XmlSaxContentHandler;
import gov.nih.nci.caadapter.hl7.v2meta.V2MessageEncoderFactory;
import gov.nih.nci.caadapter.hl7.v2meta.V2MetaXSDUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.Source;

import com.sun.encoder.Encoder;
import com.sun.encoder.EncoderException;

/**
 * By given csv file and mapping file, call generate method which will return the list of TransformationResult.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.26 $
 * @date $Date: 2008-11-21 16:19:36 $
 * @since caAdapter v1.2
 */

public class TransformationService
{
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/transformation/TransformationService.java,v 1.26 2008-11-21 16:19:36 wangeug Exp $";

    private String csvString = "";
    private File mapFile = null;
    private File outputFile = null;
    private InputStream sourceDataStream = null;

    //intermediate data
    private MapParser mapParser = null;
    private CSVMeta csvMeta = null;
    private MIFClass mifClass = null;

    private ArrayList <TransformationObserver>transformationWatchList;
    private ValidatorResults theValidatorResults = new ValidatorResults();

	/**
	 * This method will create a transformer that loads csv data from a file 
	 * and transforms into HL7 v3 messages
	 * 
	 * @param mapfilename the name of the mapping file
	 * @param csvfilename the name of the csv file
	 */

    public TransformationService(String mapfilename, String csvfilename)
    {
    	this(new File(mapfilename),new File(csvfilename));
    }

	/**
	 * This method will create a transformer that loads csv data from a String 
	 * and transforms into HL7 v3 messages
	 * 
	 * @param mapfilename the name of the mapping file
	 * @param csvString the string that contains csv data
	 */

    public TransformationService(String mapfilename, String csvInString, boolean isDataStringFlag)
    {
    	this();
        if (mapfilename == null)
        {
            throw new IllegalArgumentException("Map File should not be null!");
        }
        mapFile = new File(mapfilename);
        
        if (isDataStringFlag)
        {
	        csvString = csvInString;
	        sourceDataStream=null;
        }
    }

	/**
	 * This method will create a transformer that loads csv data from an inputstream  
	 * and transforms into HL7 v3 messages
	 * 
	 * @param mapfilename the name of the mapping file
	 * @param csvStream the inputstream that contains csv data
	 */
    public TransformationService(String mapfilename, InputStream csvStream)
    {
    	this();
        if (mapfilename == null)
        {
            throw new IllegalArgumentException("Map File should not be null!");
        }

        mapFile = new File(mapfilename);
        sourceDataStream = csvStream;
    }

	/**
	 * This method will create a transformer that loads csv data from an Java File object  
	 * and transforms into HL7 v3 messages
	 * 
	 * @param mapfile the Java mapping file object
	 * @param csvFile the csv file object
	 */
    public TransformationService(File mF, File csvFile)
    {
    	this();
        if (mF == null || csvFile == null)
        {
            throw new IllegalArgumentException("Map File or csv File should not be null!");
        }
        
        mapFile=mF;

        try {
			sourceDataStream = new FileInputStream(csvFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private TransformationService()
    {
    	transformationWatchList=new ArrayList<TransformationObserver>();
    }


	/**
	 * @return the outputFile
	 */
	public File getOutputFile() {
		return outputFile;
	}

	/**
	 * @param outputFile the outputFile to set
	 */
	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	/**
     * Add an oberver to the tranformation server
     * @param observer
     */
    public synchronized  void addProgressWatch(TransformationObserver observer)
    {
    	if (transformationWatchList==null)
    		transformationWatchList=new ArrayList<TransformationObserver>();
    	transformationWatchList.add(observer);
    }
    
    /**
     * Add an oberver to the tranformation server
     * @param observer
     */
    public synchronized void removeProgressWatch(TransformationObserver observer)
    {
    	if (transformationWatchList==null)
    		return;
    	if (transformationWatchList.contains(observer))
    			transformationWatchList.remove(observer);
    }
    
    private void informProcessProgress(int steps)
    {
    	if (transformationWatchList.size()!=0) {
        	for (TransformationObserver tObserver:transformationWatchList)
        	{
        		tObserver.progressUpdate(steps);
        		if (tObserver.isRequestCanceled()) break;
        	}
        }
    }

    private void loadMapAndMetas(File mapFile) throws Exception
    {
       informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_READ_MAPPING);      
        
    	/*
    	 * TODO Exception handling here
    	 */
        long mapbegintime = System.currentTimeMillis();
        mapParser = new MapParser();
        mapParser.processOpenMapFile(mapFile);
//        funcations = mapParser.getFunctions();
    	System.out.println("Map Parsing time" + (System.currentTimeMillis()-mapbegintime));
		informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_PARSER_MAPPING);

		if (!mapParser.getValidatorResults().isValid())
		{	
			System.out.println("Invalid .map file");
			Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"Invalid MAP file!"});
			theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
			return ;
		}

        String h3sFilename = mapParser.getH3SFilename();
        String fullh3sfilepath = FileUtil.filenameLocate(mapFile.getParent(), h3sFilename);
        
        long loadmifbegintime = System.currentTimeMillis();
        
       	XmlToMIFImporter xmlToMIFImporter = new XmlToMIFImporter(); 
       	mifClass = xmlToMIFImporter.importMifFromXml(new File(fullh3sfilepath));        
    	System.out.println("loadmif Parsing time" + (System.currentTimeMillis()-loadmifbegintime));
 		informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_READ_H3S_FILE);
 		
 		//load CSV meta
 		if (mapParser.getSourceKind()!=null&&mapParser.getSourceKind().startsWith("2."))
 		{
 			//v2 message
 		}
 		else
 		{
			File scsFile = new File(FileUtil.filenameLocate(mapFile.getParent(), mapParser.getSourceSpecFileName()));
	    	CSVMetaParserImpl parser = new CSVMetaParserImpl();
	    	CSVMetaResult csvMetaResult = parser.parse(new FileReader(scsFile));
	    	csvMeta = csvMetaResult.getCsvMeta();
	    	informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_READ_CVS_META);
 		}
    }

    /**
     * @return list of HL7 v3 message object.
     * To get HL7 v3 message of each object, call .toXML() method of each object
     */

    public List<XMLElement> process() throws Exception
    {
    	informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_START); 
    	loadMapAndMetas(mapFile);
        if (!theValidatorResults.isValid())
        	return null;
        long csvbegintime = System.currentTimeMillis();
        informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_READ_SOURCE);
        CSVDataResult csvDataResult = null;

        if (mapParser.getSourceKind()!=null&&mapParser.getSourceKind().startsWith("2."))
        {
        	csvDataResult=pareV2Message(mapParser.getSourceKind(), V2MetaXSDUtil.formatV2MessageType(mapParser.getSourceSpecFileName()));
        }
        else 
        {
        	//parse CSV stream
        	csvDataResult=parseCsvSourceData();
        }
        
    	System.out.println("Source data parsing time" + (System.currentTimeMillis()-csvbegintime));
        informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_PARSER_SOURCE);    	
   	
        List<XMLElement> xmlElements =transferSourceData(csvDataResult);   	
        System.out.println("Encode HL7 V3 message__:" + (System.currentTimeMillis()-csvbegintime));
        System.out.println("total message" + xmlElements.size());
        return xmlElements;
   }

    public int batchProcess() throws Exception
    {
    	informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_START);
    	ZipOutputStream zipOut = null;
    	int messageCount = 0;
		try{
    		loadMapAndMetas(mapFile);

    		if (!theValidatorResults.isValid())
            	return -1;
          
    		informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_READ_SOURCE);

    		if (sourceDataStream==null || (outputFile==null )) 
    		{
    			throw new Exception("not valid for batch transformation.");
    		}
    		OutputStream outputStream=null;
    		if(this.outputFile!=null)
    		{
    			outputStream = new FileOutputStream(this.outputFile);
    		}

    		CsvReader reader = new CsvReader(this.sourceDataStream, this.csvMeta);
    		zipOut = new ZipOutputStream(outputStream);
    		OutputStreamWriter writer = new OutputStreamWriter(zipOut);
//    		long bufferStart = 0;
//    		long bufferSize = 0;
    		while(reader.hasMoreRecord())
    		{
    			List<XMLElement> xmlElements = transferSourceData(reader.getNextRecord());
    			if(xmlElements!=null)
    			{
    				for(int i=0; i<xmlElements.size(); i++)
    				{
    					zipOut.putNextEntry(new ZipEntry(String.valueOf(messageCount+i)+".xml"));
    					writer.write(xmlElements.get(i).toXML().toString());
    					writer.flush();
    					zipOut.putNextEntry(new ZipEntry(String.valueOf(messageCount+i)+".ser"));
    					ObjectOutputStream objOut = new ObjectOutputStream(zipOut);
    					objOut.writeObject(xmlElements.get(i).getValidatorResults());
    					objOut.flush();
    				}
    				//System.out.println("Parsed "+xmlElements.size()+" records in " + (System.currentTimeMillis()-csvbegintime)+" ms");
    				messageCount += xmlElements.size();
//    				bufferSize = reader.getReadCount()-bufferStart;
    			}else
    			{
    				System.out.println("[WARNING] got no message, current count="+messageCount);
    				return -1;
    			}
    		}
    		informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_PARSER_SOURCE);    	
    		System.out.println("total message" + messageCount);
    		writer.close();
    	}finally{
    		try{
    			zipOut.close();
    		}catch(Exception ignored){}
    	}
    	return messageCount;
    }

    private List<XMLElement> transferSourceData(CSVDataResult csvDataResult) throws Exception
    {
		// parse the datafile, if there are errors.. return.
		final ValidatorResults csvDataValidatorResults = csvDataResult.getValidatorResults();

		/*
		 * TODO consolidate validatorResults
		 */
		if (!csvDataValidatorResults.isValid())
		{
			Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"Invalid CSV file! : Please check and validate this csv file against the scs file."});
			theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
			System.out.println("Error parsing csv Data" + csvDataResult.getCsvSegmentedFile().getLogicalRecords().size());
			return null;
		}
		CSVSegmentedFile csvSegmentedFile = csvDataResult.getCsvSegmentedFile();
		Hashtable<String, String>mappings=mapParser.getMappings();
		Hashtable<String, FunctionComponent> functions=mapParser.getFunctions();
		MapProcessor mapProcess = new MapProcessor();
		List<XMLElement> xmlElements = mapProcess.process(mappings, functions, csvSegmentedFile, mifClass, transformationWatchList);
    	return xmlElements;
    }

    private CSVDataResult pareV2Message(String v2Version, String msgType)
	{	
    	try {

 			//Create the encoder instance, HL7Encoder
			Encoder coder = V2MessageEncoderFactory.getV3MessageEncoder(v2Version, msgType);
    		long csvbegintime = System.currentTimeMillis();
//			Source source = coder.decodeFromStream(new FileInputStream(new File("data/ADT_A01.hl7")));
    		Source source = coder.decodeFromStream(this.sourceDataStream);
			Transformer transformer =  TransformerFactory.newInstance().newTransformer();
			
			//forward transformed XML to next step
			HL7V2XmlSaxContentHandler saxHandler= new HL7V2XmlSaxContentHandler();
			SAXResult saxResult=new SAXResult(saxHandler); 
			System.out.println("TransformationService.pareV2Message()...decode source message:"+(System.currentTimeMillis()-csvbegintime));
			transformer.transform(source, saxResult);
			System.out.println("TransformationService.pareV2Message()...decode/transfer data :"+(System.currentTimeMillis()-csvbegintime));
			CSVDataResult parsedData= saxHandler.getDataResult();
			return parsedData;
		} catch (EncoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null; 
    }
    

    private CSVDataResult parseCsvSourceData()throws Exception
    {
    	SegmentedCSVParserImpl parser = new SegmentedCSVParserImpl();
        
        CSVDataResult csvDataResult = null;
        if (sourceDataStream==null)
        	csvDataResult = parser.parse(csvString,  csvMeta);
        else
        	csvDataResult = parser.parse(sourceDataStream, csvMeta);
        informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_PARSER_SOURCE);
        return csvDataResult;
    }

	public ValidatorResults getValidatorResults() {
		return theValidatorResults;
	}
	

    public static void main(String[] argv) throws Exception {
        long begintime2 = System.currentTimeMillis();

   	TransformationService ts = new TransformationService("C:/xmlpathSpec/error/choice_basic.map",
		"C:/xmlpathSpec/error/choice_basic_without_patient.csv");
//           	TransformationService ts = new TransformationService("C:/Projects/caadapter/components/hl7Transformation/test/data/Transformation/MissingDataValidation/Scenarios11-Choice/test.map",
//		"C:/Projects/caadapter/components/hl7Transformation/test/data/Transformation/MissingDataValidation/Scenarios11-Choice/COCT_MT150003.csv");
        ts.process();
       	System.out.println(System.currentTimeMillis()-begintime2);
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.25  2008/11/17 20:10:07  wangeug
 * HISTORY      : Move FunctionComponent and VocabularyMap from HL7 module to common module
 * HISTORY      :
 * HISTORY      : Revision 1.24  2008/11/04 21:08:11  wangeug
 * HISTORY      : set xmlPath name of V2Meta element: replacing "." with "_"
 * HISTORY      :
 * HISTORY      : Revision 1.23  2008/10/29 19:09:36  wangeug
 * HISTORY      : clean code: remove unnecessary class variables
 * HISTORY      :
 * HISTORY      : Revision 1.22  2008/10/24 21:07:28  wangeug
 * HISTORY      : read data from dynamic data file
 * HISTORY      :
 * HISTORY      : Revision 1.21  2008/10/24 19:36:58  wangeug
 * HISTORY      : transfer a v2 message into v3 message using SUN v2 schema
 * HISTORY      :
 * HISTORY      : Revision 1.20  2008/09/23 15:18:14  wangeug
 * HISTORY      : caAdapter 4.2 alpha release
 * HISTORY      :
 * HISTORY      : Revision 1.19  2008/06/26 19:45:50  linc
 * HISTORY      : Change HL7 transformation GUI to use batch api.
 * HISTORY      :
 * HISTORY      : Revision 1.18  2008/06/09 19:53:50  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2008/06/03 20:43:55  linc
 * HISTORY      : merge batch transform api to solve OutOfMemory issues.
 * HISTORY      :
 * HISTORY      : Revision 1.16.2.2  2008/05/29 16:37:30  linc
 * HISTORY      : updated.
 * HISTORY      :
 * HISTORY      : Revision 1.16.2.1  2008/05/23 15:48:34  linc
 * HISTORY      : implemented new APIs to batch transform, for solving memory issues.
 * HISTORY      :
 * HISTORY      : Revision 1.16  2007/11/06 16:50:32  umkis
 * HISTORY      : Change the error message => Invalid CSV file! : Please check and validate this csv file against the scs file.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2007/11/06 16:49:39  umkis
 * HISTORY      : Change the error message => Invalid CSV file! : Please check and validate this csv file against the scs file.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2007/09/13 14:01:19  wuye
 * HISTORY      : Remove message print out
 * HISTORY      :
 * HISTORY      : Revision 1.13  2007/09/11 17:57:25  wuye
 * HISTORY      : Added error message when map or csv file is wrong
 * HISTORY      :
 * HISTORY      : Revision 1.12  2007/09/06 15:09:27  wangeug
 * HISTORY      : refine codes
 * HISTORY      :
 * HISTORY      : Revision 1.11  2007/09/04 20:42:14  wangeug
 * HISTORY      : add progressor
 * HISTORY      :
 * HISTORY      : Revision 1.10  2007/09/04 14:07:19  wuye
 * HISTORY      : Added progress bar
 * HISTORY      :
 * HISTORY      : Revision 1.9  2007/09/04 13:47:52  wangeug
 * HISTORY      : add an progress observer list
 * HISTORY      :
 * HISTORY      : Revision 1.8  2007/08/29 00:13:15  wuye
 * HISTORY      : Modified the default value generation strategy
 * HISTORY      :
 * HISTORY      : Revision 1.7  2007/08/13 19:21:56  wuye
 * HISTORY      : load h3s in different format
 * HISTORY      :
 * HISTORY      : Revision 1.6  2007/08/03 13:25:32  wuye
 * HISTORY      : Fixed the mapping scenario #1 bug according to the design document
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/07/31 14:04:30  wuye
 * HISTORY      : Add Comments
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/07/24 17:25:48  wuye
 * HISTORY      : Synch with the new .map format
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/07/20 17:00:20  wangeug
 * HISTORY      : integrate Hl7 transformation service
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/19 15:11:15  wuye
 * HISTORY      : Fixed the attribute sort problem
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/17 19:54:09  wuye
 * HISTORY      : cvs to HL7 v3 transformation
 * HISTORY      :
 * HISTORY      :
 */
