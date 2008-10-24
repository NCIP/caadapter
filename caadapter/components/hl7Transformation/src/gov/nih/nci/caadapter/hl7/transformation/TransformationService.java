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
import java.util.zip.ZipInputStream;
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
 * @version $Revision: 1.21 $
 * @date $Date: 2008-10-24 19:36:58 $
 * @since caAdapter v1.2
 */

public class TransformationService
{
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/transformation/TransformationService.java,v 1.21 2008-10-24 19:36:58 wangeug Exp $";

    private boolean isCsvString = false;
    private boolean isInputStream = false;
    private boolean isOutputStream = false;
    private String csvString = "";
    private File mapfile = null;
    private File csvfile = null;
    //private File scsfile = null;
    private File outputFile = null;
    private InputStream csvStream = null;
    private OutputStream outputStream = null;
    //intermediate data
	Hashtable mappings = null;
	Hashtable<String, FunctionComponent> funcations = null;
	MapParser mapParser = null;
    CSVMeta meta = null;
    MIFClass mifClass = null;
    CsvReader reader = null;
    
    private CSVSegmentedFile csvSegmentedFile = null;
    private ArrayList <TransformationObserver>transformationWatchList;
    ValidatorResults theValidatorResults = new ValidatorResults();

	/**
	 * This method will create a transformer that loads csv data from a file 
	 * and transforms into HL7 v3 messages
	 * 
	 * @param mapfilename the name of the mapping file
	 * @param csvfilename the name of the csv file
	 */

    public TransformationService(String mapfilename, String csvfilename)
    {
    	this();
        if (mapfilename == null || csvfilename == null)
        {
            throw new IllegalArgumentException("Map File or csv File should not be null!");
        }

        this.mapfile = new File(mapfilename);
        this.csvfile = new File(csvfilename);
    }

	/**
	 * This method will create a transformer that loads csv data from a String 
	 * and transforms into HL7 v3 messages
	 * 
	 * @param mapfilename the name of the mapping file
	 * @param csvString the string that contains csv data
	 */

    public TransformationService(String mapfilename, String csvString, boolean flag)
    {
    	this();
        if (mapfilename == null)
        {
            throw new IllegalArgumentException("Map File should not be null!");
        }

        this.mapfile = new File(mapfilename);
        this.csvString = csvString;
        this.isCsvString = flag;
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

        this.mapfile = new File(mapfilename);
        this.csvStream = csvStream;
        this.isInputStream = true;
    }

	/**
	 * This method will create a transformer that loads csv data from an Java File object  
	 * and transforms into HL7 v3 messages
	 * 
	 * @param mapfile the Java mapping file object
	 * @param csvfile the csv file object
	 */
    public TransformationService(File mapfile, File csvfile)
    {
    	this();
        if (mapfile == null || csvfile == null)
        {
            throw new IllegalArgumentException("Map File or csv File should not be null!");
        }
        this.mapfile = mapfile;
        this.csvfile = csvfile;
    }

    private TransformationService()
    {
    	transformationWatchList=new ArrayList<TransformationObserver>();
    }

    
    /**
	 * @return the isOutputStream
	 */
	public boolean isOutputStream() {
		return isOutputStream;
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
	 * @return the outputStream
	 */
	public OutputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * @param outputStream the outputStream to set
	 */
	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
		this.isOutputStream = true;
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
    /**
     * @return list of HL7 v3 message object.
     * To get HL7 v3 message of each object, call .toXML() method of each object
     */


    private void loadMap() throws Exception
    {
    	mappings = new Hashtable();
    	funcations = new Hashtable<String, FunctionComponent>();
    	
    	/*
    	 * TODO Exception handling here
    	 */
        long mapbegintime = System.currentTimeMillis();
        mapParser = new MapParser();
        mappings = mapParser.processOpenMapFile(mapfile);
        funcations = mapParser.getFunctions();
    	System.out.println("Map Parsing time" + (System.currentTimeMillis()-mapbegintime));
    }
    
    private void loadH3S()  throws FileNotFoundException
    {
        String h3sFilename = mapParser.getH3SFilename();
        String fullh3sfilepath = FileUtil.filenameLocate(mapfile.getParent(), h3sFilename);
        
        long loadmifbegintime = System.currentTimeMillis();
        
//        if (fullh3sfilepath.endsWith(".h3s"))
//        {
//        	mifClass = loadMIF(fullh3sfilepath);
//        }
//        else 
//        {
        	XmlToMIFImporter xmlToMIFImporter = new XmlToMIFImporter(); 
        	mifClass = xmlToMIFImporter.importMifFromXml(new File(fullh3sfilepath));
//        }
        
    	System.out.println("loadmif Parsing time" + (System.currentTimeMillis()-loadmifbegintime));
    }
    
    public void loadCsvMeta(File metaFile) throws FileNotFoundException
    {
    	CSVMetaParserImpl parser = new CSVMetaParserImpl();
    	CSVMetaResult csvMetaResult = parser.parse(new FileReader(metaFile));
    	meta = csvMetaResult.getCsvMeta();
    }
    
    public List<XMLElement> process() throws Exception
    {
       	informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_START);

        informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_READ_MAPPING);
       	this.loadMap();
        informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_PARSER_MAPPING);
        
        if (!mapParser.getValidatorResults().isValid())
        {	
        	System.out.println("Invalid .map file");
            Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"Invalid MAP file!"});
            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
        	return null;
        }

        this.loadH3S();
        informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_READ_H3S_FILE);
        
        
        long csvbegintime = System.currentTimeMillis();
        informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_READ_SOURCE);
        CSVDataResult csvDataResult = null;
        if (isInputStream) 
        {
        	csvDataResult=parseCsvInputStream(mapParser.getSourceSpecFileName());
        }
        else if (isCsvString) 
        {
        	csvDataResult= parseCsvString(mapParser.getSourceSpecFileName());
        }
        else  if (mapParser.getSourceKind()!=null&&mapParser.getSourceKind().startsWith("2."))
        {
        	csvDataResult=pareV2Message(mapParser.getSourceKind(), V2MetaXSDUtil.formatV2MessageType(mapParser.getSourceSpecFileName()));
        	System.out.println("V2 Message Parsing time" + (System.currentTimeMillis()-csvbegintime));
        	//reformat mapping link xmlPath
        	Enumeration enumKey=mappings.keys();
        	while (enumKey.hasMoreElements())
        	{
        		String oneMappingKey=(String)enumKey.nextElement();
        		String srcXmlPath=(String)mappings.get(oneMappingKey);
        		if (srcXmlPath.startsWith("function"))
        			continue;
        		String leadingSt=srcXmlPath.substring(0, srcXmlPath.indexOf("."));
        		String endingSt=srcXmlPath.substring(srcXmlPath.indexOf(".")+1);
        		endingSt=endingSt.replace(".", "__");
        		srcXmlPath=leadingSt+"."+endingSt;
        		mappings.put(oneMappingKey, srcXmlPath);
        	}
        }
        else
        {
        	csvDataResult= parseCsvfile(mapParser.getSourceSpecFileName());
        }
        
    	System.out.println("Source data parsing time" + (System.currentTimeMillis()-csvbegintime));
        informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_PARSER_SOURCE);    	

        // parse the datafile, if there are errors.. return.
        final ValidatorResults csvDataValidatorResults = csvDataResult.getValidatorResults();

//        prepareValidatorResults.addValidatorResults(csvDataValidatorResults);
        /*
         * TODO consolidate validatorResults
         */
        if (!csvDataValidatorResults.isValid())
        {
            Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"Invalid CSV file! : Please check and validate this csv file against the scs file."});
            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
        	System.out.println("Error parsing source Data" + csvDataResult.getCsvSegmentedFile().getLogicalRecords().size());
            return null;
        }
        csvSegmentedFile = csvDataResult.getCsvSegmentedFile();
    	informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_COUNT_MESSAGE);
        
    	MapProcessor mapProcess = new MapProcessor();
    	
        List<XMLElement> xmlElements = mapProcess.process(mappings, funcations, csvSegmentedFile, mifClass, transformationWatchList);
    	System.out.println("Encode HL7 V3 message__"+this.mifClass.getName()+":" + (System.currentTimeMillis()-csvbegintime));
        System.out.println("total message" + xmlElements.size());
        return xmlElements;
   }

    public int batchProcess() throws Exception
    {
    	ZipOutputStream zipOut = null;
    	int messageCount = 0;
		try{
    		informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_START);

    		informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_READ_MAPPING);
    		this.loadMap();
    		informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_PARSER_MAPPING);

    		if (!mapParser.getValidatorResults().isValid())
    		{	
    			System.out.println("Invalid .map file");
    			Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"Invalid MAP file!"});
    			theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
    			return -1;
    		}

    		this.loadH3S();
    		informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_READ_H3S_FILE);

    		long csvbegintime = System.currentTimeMillis();
    		informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_READ_SOURCE);

    		boolean batchFinished = false;
    		File scsFile = new File(FileUtil.filenameLocate(mapfile.getParent(), mapParser.getSourceSpecFileName()));
    		this.loadCsvMeta(scsFile);

    		if (isCsvString || (!this.isOutputStream && this.outputFile==null && this.outputStream==null)) 
    		{
    			throw new Exception("not valid for batch transformation.");
    		}
    		if(!this.isInputStream && this.csvfile!=null)
    		{
    			this.csvStream = new FileInputStream(this.csvfile);
    		}
    		if(!this.isOutputStream && this.outputFile!=null)
    		{
    			this.outputStream = new FileOutputStream(this.outputFile);
    		}

    		reader = new CsvReader(this.csvStream, this.meta);
    		zipOut = new ZipOutputStream(outputStream);
    		OutputStreamWriter writer = new OutputStreamWriter(zipOut);
    		long bufferStart = 0;
    		long bufferSize = 0;
    		while(reader.hasMoreRecord())
    		{
    			List<XMLElement> xmlElements = processNext();
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
    				bufferSize = reader.getReadCount()-bufferStart;
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

    public List<XMLElement> processNext() throws Exception
    {
    	CSVDataResult csvDataResult = null;
    	csvDataResult=reader.getNextRecord();

		// parse the datafile, if there are errors.. return.
		final ValidatorResults csvDataValidatorResults = csvDataResult.getValidatorResults();

//		prepareValidatorResults.addValidatorResults(csvDataValidatorResults);
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
		csvSegmentedFile = csvDataResult.getCsvSegmentedFile();
		//informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_COUNT_MESSAGE);

		MapProcessor mapProcess = new MapProcessor();

		List<XMLElement> xmlElements = mapProcess.process(mappings, funcations, csvSegmentedFile, mifClass, transformationWatchList);
    	return xmlElements;
    }

    public static String readFromZip(File file, String name) throws FileNotFoundException, IOException{
    	ZipInputStream inZip = null;
    	StringBuffer ret = new StringBuffer();
		try{
    		inZip = new ZipInputStream(new FileInputStream(file));
    		String entryName = "";
    		while(!entryName.equals(name))
    		{
    			ZipEntry entry = inZip.getNextEntry();
    			if(entry==null) throw new IOException("entry not found.");
    			entryName = entry.getName();
    		}
    		byte[] buf = new byte[1024];
    		int count = 0;
    		while(count>=0)
    		{
    			count = inZip.read(buf, 0, 1024);
    			if(count>0) ret.append(new String(buf, 0, count));
    		}
    	}finally{
    		try{
    			inZip.close();
    		}catch(Exception ignored){}
    	}
    	return ret.toString();
    }
    
    public static Object readObjFromZip(File file, String name) throws FileNotFoundException, ClassNotFoundException, IOException{
    	Object ret = null;
    	ZipInputStream inZip = null;
    	try{
    		inZip = new ZipInputStream(new FileInputStream(file));
    		String entryName = "";
    		while(!entryName.equals(name))
    		{
    			ZipEntry entry = inZip.getNextEntry();
    			if(entry==null) throw new IOException("entry not found.");
    			entryName = entry.getName();
    		}
    		ObjectInputStream objIn = new ObjectInputStream(inZip);
    		ret = objIn.readObject();
    	}finally{
    		try{
    			inZip.close();
    		}catch(Exception ignored){}
    	}
    		
    	return ret;
    }
    
    public static int countEntriesInZip(File file) throws FileNotFoundException, IOException{
    	ZipInputStream inZip = new ZipInputStream(new FileInputStream(file));
    	int count = 0;
    	while(inZip.getNextEntry()!=null) 
    		count++;
    	inZip.close();
    	return count;
    }
    
    private CSVDataResult pareV2Message(String v2Version, String msgType)
	{	
    	try {

 			//Create the encoder instance, HL7Encoder
			Encoder coder = V2MessageEncoderFactory.getV3MessageEncoder(v2Version, msgType);
    		long csvbegintime = System.currentTimeMillis();
			Source source = coder.decodeFromStream(new FileInputStream(new File("data/ADT_A01.hl7")));

			Transformer transformer =  TransformerFactory.newInstance().newTransformer();
			
			//forward transformed XML to next step
			HL7V2XmlSaxContentHandler saxHandler= new HL7V2XmlSaxContentHandler();
			SAXResult saxResult=new SAXResult(saxHandler); 
			System.out.println("TransformationService.pareV2Message()...decode source message:"+(System.currentTimeMillis()-csvbegintime));
			transformer.transform(source, saxResult);
			System.out.println("TransformationService.pareV2Message()...decode/transfer data :"+(System.currentTimeMillis()-csvbegintime));
			CSVDataResult parsedData= saxHandler.getDataResult();
			return parsedData;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
    
    private CSVDataResult parseCsvfile(String scsFilename) throws Exception
    {
        SegmentedCSVParserImpl parser = new SegmentedCSVParserImpl();
        String fullscmfilepath = FileUtil.filenameLocate(mapfile.getParent(), scsFilename);
        CSVDataResult csvDataResult = parser.parse(csvfile, new File(fullscmfilepath));
        return csvDataResult;
    }

    private CSVDataResult parseCsvString(String scsFilename) throws Exception
    {
        SegmentedCSVParserImpl parser = new SegmentedCSVParserImpl();
        String fullscmfilepath = FileUtil.filenameLocate(mapfile.getParent(), scsFilename);
        CSVDataResult csvDataResult = parser.parse(csvString, new File(fullscmfilepath));
        return csvDataResult;
    }

    private CSVDataResult parseCsvInputStream(String scsFilename) throws Exception
    {
        SegmentedCSVParserImpl parser = new SegmentedCSVParserImpl();
        String fullscmfilepath = FileUtil.filenameLocate(mapfile.getParent(), scsFilename);
        CSVDataResult csvDataResult = parser.parse(csvStream, new File(fullscmfilepath));
        return csvDataResult;
    }
	public ValidatorResults getValidatorResults() {
		return theValidatorResults;
	}
	
	
/*    private TransformationResult handleException(Exception e) //List<TransformationResult> v3messageResults
    {
		String errorMessage = e.getMessage();
        if ((errorMessage == null) || errorMessage.equalsIgnoreCase("null"))
        {
            errorMessage = "";
        }
        Message msg = MessageResources.getMessage("GEN0", new Object[]{errorMessage});
        ValidatorResult validatorResult = new ValidatorResult(ValidatorResult.Level.FATAL, msg);
        ValidatorResults vrs = new ValidatorResults();
        vrs.addValidatorResult(validatorResult);
        TransformationResult oneResult = new TransformationResult(MessageResources.getMessage("TRF2", new Object[]{}).toString(),
            vrs);
//        v3messageResults.add(oneResult);
        Log.logException(this, e);
		return oneResult;
	}

*/
	private MIFClass loadMIF(String mifFileName) {
		MIFClass mifClass = null;
		InputStream mifFileInputStream;
		try {
			mifFileInputStream = new FileInputStream(mifFileName);
		}catch(Exception e) {
			//Cannot file the file
			return null;
		}
		try{
			ObjectInputStream mifFileObjectInputStream = new ObjectInputStream(mifFileInputStream);
			mifClass = (MIFClass)mifFileObjectInputStream.readObject();
			mifFileObjectInputStream.close();
			mifFileInputStream.close();
			return mifClass;
		}catch (Exception e) {
			return null;
		}
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
