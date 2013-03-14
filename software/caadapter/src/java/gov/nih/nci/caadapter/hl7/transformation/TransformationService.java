/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.hl7.transformation;

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
//import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.csv.CSVMetaParserImpl;
import gov.nih.nci.caadapter.common.csv.CSVMetaResult;
import gov.nih.nci.caadapter.common.csv.CsvReader;
import gov.nih.nci.caadapter.common.csv.CSVDataResult;
import gov.nih.nci.caadapter.common.csv.data.CSVSegmentedFile;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
//import gov.nih.nci.caadapter.common.util.Config;
//import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.XmlToMIFImporter;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;
import gov.nih.nci.caadapter.hl7.v2meta.HL7V2XmlSaxContentHandler;
import gov.nih.nci.caadapter.hl7.v2meta.V2MessageEncoderFactory;
import gov.nih.nci.caadapter.hl7.v2meta.V2MessageLinefeedEncoder;
//import gov.nih.nci.caadapter.hl7.validation.complement.ReorganizingForValidating;
//import gov.nih.nci.caadapter.hl7.validation.complement.XSDValidationTree;
//import gov.nih.nci.caadapter.hl7.validation.XMLValidator;
import gov.nih.nci.caadapter.hl7.v2v3.tools.XmlReorganizingTree;
import gov.nih.nci.caadapter.hl7.v2v3.tools.ZipUtil;
//import gov.nih.nci.caadapter.hl7.v2v3.tools.SchemaDirUtil;
//import gov.nih.nci.caadapter.hl7.v2v3.tools.XmlTreeBrowsingNode;
//&umkis import gov.nih.nci.caadapter.hl7.validation.XMLValidator;
//&umkis import gov.nih.nci.caadapter.hl7.v2v3.tools.SchemaDirUtil;
//&umkis import gov.nih.nci.caadapter.common.util.Config;
//&umkis import gov.nih.nci.caadapter.common.util.GeneralUtilities;

import java.io.*;
import java.util.ArrayList;
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
import javax.swing.tree.DefaultMutableTreeNode;

import com.sun.encoder.Encoder;
import com.sun.encoder.EncoderException;

/**
 * By given csv file and mapping file, call generate method which will return the list of TransformationResult.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: altturbo $
 * @version $Revision: 1.50 $
 * @date $Date: 2009-10-12 16:57:28 $
 * @since caAdapter v1.2
 */

public class TransformationService
{
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/transformation/TransformationService.java,v 1.50 2009-10-12 16:57:28 altturbo Exp $";

//    private String dataString = "";
    private boolean isStringData=false;
    private File mapFile = null;
    private File outputFile = null;
    private InputStream sourceDataStream = null;

//&umkis    private boolean schemaValidation = false;
//&umkis    private int levelOfDatatypeIncluding = -1;
//&umkis    private String schemaFileName = null;
//&umkis    private String nameSpacePrefix = "";
//&umkis    private boolean makeLowercaseHeadElement = false;

    //intermediate data
    private MapParser mapParser = null;
    private CSVMeta csvMeta = null;
    private MIFClass mifClass = null;

    private ArrayList <TransformationObserver>transformationWatchList;
    private ValidatorResults theValidatorResults = new ValidatorResults();

    private XmlReorganizingTree controlMessageTemplate = null;

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
	 * @param dataInString the string that contains csv data
     * @param isDataStringFlag true if dataInString is string csv data, false is dataInString is file path.
	 */

    public TransformationService(String mapfilename, String dataInString, boolean isDataStringFlag)
    {
    	this(mapfilename, new StringBufferInputStream(dataInString));
    	isStringData=isDataStringFlag;
       	System.out.println("TransformationService.TransformationService()..invoked by webservice client..");
    }

	/**
	 * This method will create a transformer that loads csv data from an Java File object
	 * and transforms into HL7 v3 messages
	 *
	 * @param mF the Java mapping file object
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
	 * This method will create a transformer that loads csv data from an inputstream
	 * and transforms into HL7 v3 messages
	 *
	 * @param mapfilename the name of the mapping file
	 * @param csvStream the inputstream that contains csv data
	 */
	private TransformationService(String mapfilename, InputStream csvStream)
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
    public synchronized void addProgressWatch(TransformationObserver observer)
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
 		if (mapParser.getSourceKind()!=null&&!mapParser.getSourceKind().equalsIgnoreCase("v2"))
 		{
			File scsFile = new File(FileUtil.filenameLocate(mapFile.getParent(), mapParser.getSourceSpecFileName()));
	    	CSVMetaParserImpl parser = new CSVMetaParserImpl();
	    	CSVMetaResult csvMetaResult = parser.parse(new FileReader(scsFile));
	    	csvMeta = csvMetaResult.getCsvMeta();
	    	informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_READ_CVS_META);
 		}
    }

    /**
     * Process input data as DOM style generating target message for all logical records once
     * @return list of HL7 v3 message object.
     * To get HL7 v3 message of each object, call .toXML() method of each object
     */
    public List<XMLElement> process() throws Exception
    {
        return process(null);
    }
    public List<XMLElement> process(String controlFile) throws Exception
    {
        try
        {
            return processWithException(controlFile);
        }
        catch(Exception ee)
        {
            if (controlMessageTemplate != null)
            {
                ZipUtil zipUtil = controlMessageTemplate.getZipUtil();
                if (zipUtil != null) zipUtil.deleteDirectory();
            }
            throw ee;
        }
    }
    private List<XMLElement> processWithException(String controlFile) throws Exception
    {
//&umkis        if (checkTime()) System.out.println("checkTime() 1001 : " + GeneralUtilities.getCurrentTime("yyyyMMdd-hh:mm:ss.SSS"));
        informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_START);
    	loadMapAndMetas(mapFile);
        if (!theValidatorResults.isValid())
        	return null;
        long csvbegintime = System.currentTimeMillis();
        informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_READ_SOURCE);
        List<XMLElement> xmlElements=null ;

        // search Control message file
//&umkis        if (checkTime()) System.out.println("checkTime() 1002 : " + GeneralUtilities.getCurrentTime("yyyyMMdd-hh:mm:ss.SSS"));
        controlMessageTemplate = ControlMessageRelatedUtil.searchControlMessage(mifClass, controlFile, this);
//&umkis        if (checkTime()) System.out.println("checkTime() 1003 : " + GeneralUtilities.getCurrentTime("yyyyMMdd-hh:mm:ss.SSS"));

        if (mapParser.getSourceKind()!=null&&mapParser.getSourceKind().equalsIgnoreCase("v2"))
        {
            CSVDataResult csvDataResult = null;
        	csvDataResult=parseV2Message(mapParser.getSourceSpecFileName());
        	System.out.println("Source data parsing time" + (System.currentTimeMillis()-csvbegintime));
            informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_PARSER_SOURCE);
        	xmlElements =transferSourceData(csvDataResult);
        }
        else
        {
            //parse CSV stream
        	CsvReader reader = new CsvReader(this.sourceDataStream, this.csvMeta);
    		while(reader.hasMoreRecord())
    		{
    			List<XMLElement> xmlOneRecord = transferSourceData(reader.getNextRecord());
    			if(xmlOneRecord!=null)
    			{
        			if (xmlElements==null)
        				xmlElements=xmlOneRecord;
        			else
        				xmlElements.addAll(xmlOneRecord);
    			}
    		}
        	System.out.println("Source data parsing time" + (System.currentTimeMillis()-csvbegintime));
            informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_PARSER_SOURCE);
        }
//&umkis        if (checkTime()) System.out.println("checkTime() 1004 : " + GeneralUtilities.getCurrentTime("yyyyMMdd-hh:mm:ss.SSS"));

        if (isStringData)
        {
        	//innvoked from WebService
   	        System.out.println("TransformationService.process()...Transfer data: invoked by Webservice client");
   	        //return here to web service client
   	        return xmlElements;
        }

        System.out.println("Encode HL7 V3 message__:" + (System.currentTimeMillis()-csvbegintime));
        int c = -1;
        if (xmlElements != null) c=xmlElements.size();
        System.out.println("total message" + c);
        ZipOutputStream zipOut = prepareZipout();
		if (xmlElements!=null)
		{
			try {
				writeOutMessage(xmlElements, 0, zipOut);
			} catch (Exception e) {
				throw e;
	    	}finally{
	    		try{
	    			zipOut.close();
	    		}catch(Exception ignored){}
	    	}
		}
		else
		{
			System.out.println("[WARNING] got no message...");
		}

        if (controlMessageTemplate != null)
        {
            ZipUtil zipUtil = controlMessageTemplate.getZipUtil();
            if (zipUtil != null) zipUtil.deleteDirectory();
        }

        return xmlElements;
   }

    /**
     * Process the input data as SAX style generating target data for each logical record one by one
     * @return
     * @throws Exception
     * @deprecated
     * @see process()
     */
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
    			throw new Exception("not valid for batch transformation.");

    		CsvReader reader = new CsvReader(this.sourceDataStream, this.csvMeta);
    		zipOut = prepareZipout();
    		while(reader.hasMoreRecord())
    		{
    			List<XMLElement> xmlElements = transferSourceData(reader.getNextRecord());
    			if(xmlElements!=null)
    			{
    				writeOutMessage(xmlElements, messageCount, zipOut);
    				messageCount += xmlElements.size();
    			}else
    			{
    				System.out.println("[WARNING] got no message, current count="+messageCount);
    				return -1;
    			}
    		}
    		informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_PARSER_SOURCE);
    		System.out.println("total message" + messageCount);
    	}finally{
    		try{
    			zipOut.close();
    		}catch(Exception ignored){}
    	}
    	return messageCount;
    }

    /**
     * Prepare a ZipOutputStream for generated HL7 message
     * @return	message zipOutput  stream
     * @throws FileNotFoundException
     */
    private ZipOutputStream prepareZipout() throws FileNotFoundException
    {
    	ZipOutputStream rtnOut=null;
    	OutputStream outputStream=null;
		if(this.outputFile!=null)
		{
			outputStream = new FileOutputStream(this.outputFile);
		}
		rtnOut = new ZipOutputStream(outputStream);
    	return rtnOut;
    }

     /**
     * Write a list of HL7 message into a zipOutput
     * @param xmlElements
     * @param messageCount
     * @param zipOut
     * @throws IOException
     */
    private void writeOutMessage(List<XMLElement> xmlElements, int messageCount, ZipOutputStream zipOut) throws IOException
    {
//&umkis        if (checkTime()) System.out.println("checkTime() 2001 : " + GeneralUtilities.getCurrentTime("yyyyMMdd-hh:mm:ss.SSS"));
        OutputStreamWriter writer=new OutputStreamWriter(zipOut);

        int wrappedMessageCount = 0;
        ValidatorResults controlValidator = null;

        for(int i=0; i<xmlElements.size(); i++)
		{
            String v3Message = null;

//&umkis            if ((getLevelOfDatatypeIncluding() > 0)||
//&umkis                (getMakeLowercaseHeadElement())||
//&umkis                (!getNameSpacePrefix().equals("")))
//&umkis                v3Message = xmlElements.get(i).toXML(getLevelOfDatatypeIncluding(), getMakeLowercaseHeadElement(), getNameSpacePrefix()).toString();
//&umkis            else
//&umkis            if (checkTime()) System.out.println(" --checkTime() 210"+i+" : " + GeneralUtilities.getCurrentTime("yyyyMMdd-hh:mm:ss.SSS"));
            v3Message = xmlElements.get(i).toXML().toString();

            zipOut.putNextEntry(new ZipEntry(String.valueOf(messageCount+i)+".xml"));
			writer.write(v3Message);
			writer.flush();

            ValidatorResults validatorsToShow = xmlElements.get(i).getValidatorResults();

//&umkis            if (getSchemaValidation())
//&umkis            {
//&umkis                Object[] obs = ControlMessageRelatedUtil.excuteXSDValidationForTransformationService(validatorsToShow, messageCount, i, v3Message, zipOut, writer, schemaFileName , mifClass, controlMessageTemplate);     //&umkis
//&umkis                if (obs != null)
//&umkis                {
//&umkis                    v3Message = (String) obs[0];
//&umkis                    controlMessageTemplate = (XmlReorganizingTree) obs[1];
//&umkis                    validatorsToShow = (ValidatorResults) obs[2];
//&umkis                }
//&umkis            }

            if ((controlMessageTemplate != null)&&(controlMessageTemplate.getHeadNode() != null))
            {
                if (ControlMessageRelatedUtil.insertV3IntoControlMessage(controlMessageTemplate, v3Message, mifClass, controlValidator, i)) wrappedMessageCount++;
            }

            zipOut.putNextEntry(new ZipEntry(String.valueOf(messageCount+i)+".ser"));
            ObjectOutputStream objOut = new ObjectOutputStream(zipOut);
            objOut.writeObject(validatorsToShow);
            objOut.flush();
        }
//&umkis        if (checkTime()) System.out.println("checkTime() 2002 : " + GeneralUtilities.getCurrentTime("yyyyMMdd-hh:mm:ss.SSS"));
        if ((controlMessageTemplate != null)&&(controlMessageTemplate.getHeadNode() != null))
        {
            String integratedMessage = controlMessageTemplate.printStringXML();
            controlValidator = controlMessageTemplate.validate(false);
            zipOut.putNextEntry(new ZipEntry("i.xml"));
            writer.write(integratedMessage);
            writer.flush();
        }
        if(controlValidator != null)
        {
            if (xmlElements.size() == wrappedMessageCount)
            {
                if (wrappedMessageCount == 1) controlValidator = GeneralUtilities.addValidatorMessageInfo(controlValidator, "The payload message was inserted to the control message(i.xml).");
                else controlValidator = GeneralUtilities.addValidatorMessageInfo(controlValidator, "The All " + wrappedMessageCount + "payload messages were inserted to the control message(i.xml).");
            }
            else if (wrappedMessageCount > 0) controlValidator = GeneralUtilities.addValidatorMessageInfo(controlValidator, "" + wrappedMessageCount + "payload message(s) was(were) inserted to the control message(i.xml).");
            else controlValidator = GeneralUtilities.addValidatorMessage(controlValidator, "The control message is not generated.");

            zipOut.putNextEntry(new ZipEntry("i.ser"));
            ObjectOutputStream objOut = new ObjectOutputStream(zipOut);
            objOut.writeObject(controlValidator);
            objOut.flush();
        }
//&umkis        if (checkTime()) System.out.println("checkTime() 2003 : " + GeneralUtilities.getCurrentTime("yyyyMMdd-hh:mm:ss.SSS"));
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

            theValidatorResults.addValidatorResults(csvDataValidatorResults);

            return null;
		}
		CSVSegmentedFile csvSegmentedFile = csvDataResult.getCsvSegmentedFile();
		Hashtable<String, String>mappings=mapParser.getMappings();
		Hashtable<String, FunctionComponent> functions=mapParser.getFunctions();
		MapProcessor mapProcess = new MapProcessor();
		List<XMLElement> xmlElements = mapProcess.process(mappings, functions, csvSegmentedFile, mifClass, transformationWatchList);
    	return xmlElements;
    }

    private CSVDataResult parseV2Message(String v2MessageSchema)
	{
    	//format v2 message schema
    	int spIndx=v2MessageSchema.indexOf("/");
    	if (v2MessageSchema==null||spIndx<0)
    		System.out.println("TransformationService.parseV2Message()...invalid V2 message schema:"+v2MessageSchema);
    	String v2Version = v2MessageSchema.substring(0, spIndx);
    	String v2Type=v2MessageSchema.substring(spIndx+1);
    	try {
 			//Create the encoder instance, HL7Encoder
			Encoder encoder = V2MessageEncoderFactory.getV2MessageEncoder(v2Version, v2Type);
    		if (encoder==null)
    		{
    			System.out.println("TransformationService.parseV2Message()..coder initialization failed"+ v2MessageSchema);
    			return null;
    		}
			long csvbegintime = System.currentTimeMillis();
    		V2MessageLinefeedEncoder lfEncoder= new V2MessageLinefeedEncoder(sourceDataStream);
			Transformer transformer =  TransformerFactory.newInstance().newTransformer();

			//forward transformed XML to next step
			HL7V2XmlSaxContentHandler saxHandler= new HL7V2XmlSaxContentHandler();
			SAXResult saxResult=new SAXResult(saxHandler);
			System.out.println("TransformationService.parseV2Message()...decode source message:"+(System.currentTimeMillis()-csvbegintime));
			ArrayList<byte[]> v2Bytes=lfEncoder.getEncodeByteList();
			for(byte[] v2MsgByte:v2Bytes)
			{
				Source saxSource = encoder.decodeFromBytes(v2MsgByte);
				transformer.transform(saxSource, saxResult);
			}
			System.out.println("TransformationService.parseV2Message()...decode/transfer data :"+(System.currentTimeMillis()-csvbegintime));
			return saxHandler.getDataResult();
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




//    private CSVDataResult parseCsvSourceData()throws Exception
//    {
//    	SegmentedCSVParserImpl parser = new SegmentedCSVParserImpl();
//
//        CSVDataResult csvDataResult = null;
//        if (sourceDataStream==null)
//        	csvDataResult = parser.parse(csvString,  csvMeta);
//        else
//        	csvDataResult = parser.parse(sourceDataStream, csvMeta);
//        informProcessProgress(TransformationObserver.TRANSFORMATION_DATA_LOADING_PARSER_SOURCE);
//        return csvDataResult;
//    }


   /**
//&umkis	 * @param schemaValidation a boolean tag whether do schema validation or not.
//&umkis	 */
//&umkis    public void setSchemaValidation(boolean schemaValidation)
//&umkis    {
//&umkis        this.schemaValidation = schemaValidation;
//&umkis    }

//&umkis    /**
//&umkis	 * @return the boolean tag whether do schema validation or not.
//&umkis	 */
//&umkis    public boolean getSchemaValidation()
//&umkis    {
//&umkis        return schemaValidation;
//&umkis    }

//&umkis    /**
//&umkis	 * @return a integer value for level of data type including.
//&umkis     *      0 or less = all data type attribute are shown, 1=concreted data type from abstract data type only, 2=all data type cannot be shown.
//&umkis     *      -1 or less = the value of FileUtil.searchProperty("levelOfDatatypeOutput") will be used.
//&umkis	 */
//&umkis    public int getLevelOfDatatypeIncluding()
//&umkis    {
//&umkis        return levelOfDatatypeIncluding;
//&umkis    }

//&umkis    /**
//&umkis	 * @param levelOfDatatypeInclude a integer value for level of data type including..
//&umkis	 */
//&umkis    public void setLevelOfDatatypeIncluding(int levelOfDatatypeInclude)
//&umkis    {
//&umkis        levelOfDatatypeIncluding = levelOfDatatypeInclude;
//&umkis    }

//&umkis    /**
//&umkis     * @param schemaFile
//&umkis     */
//&umkis    public boolean setSchemaFileName(String schemaFile)
//&umkis    {
//&umkis        if (!schemaFile.toLowerCase().endsWith(".xsd"))
//&umkis        {
//&umkis            return false;
//&umkis        }
//&umkis        File file = new File(schemaFile);
//&umkis        if ((file.exists())&&(file.isFile()))
//&umkis        {
//&umkis            schemaFileName = file.getAbsolutePath().trim();
//&umkis            schemaValidation = true;
//&umkis            return true;
//&umkis        }
//&umkis        //schemaFileName = null;
//&umkis        return false;
//&umkis    }

//&umkis    /**
//&umkis     * @return schemaFileName.
//&umkis     */
//&umkis    public String getSchemaFileName()
//&umkis    {
//&umkis        return schemaFileName;
//&umkis    }

//&umkis    /**
//&umkis	 * @return a string value xml name space prefix ex) <v3:Patient.
//&umkis	 */
//&umkis    public String getNameSpacePrefix()
//&umkis    {
//&umkis        if (nameSpacePrefix == null) return "";
//&umkis        return nameSpacePrefix.trim();
//&umkis    }

//&umkis    /**
//&umkis	 * @param nameSpace xml name space prefix ex) <v3:Patient.
//&umkis	 */
//&umkis    public void setNameSpacePrefix(String nameSpace)
//&umkis    {
//&umkis        nameSpacePrefix = nameSpace;
//&umkis    }

//&umkis    /**
//&umkis	 * @return if true, message will starts with a lowercase character ex) <patient
//&umkis     *         if false, message will starts with a capital character ex) <Patient
//&umkis	 */
//&umkis    public boolean getMakeLowercaseHeadElement()
//&umkis    {
//&umkis        return makeLowercaseHeadElement;
//&umkis    }

//&umkis    /**
//&umkis	 * @param lowerCase : if true, message will starts with a lowercase character ex) <patient
//&umkis     *                    if false (default value), message will starts with a capital character ex) <Patient
//&umkis	 */
//&umkis    public void setMakeLowercaseHeadElement(boolean lowerCase)
//&umkis    {
//&umkis        makeLowercaseHeadElement = lowerCase;
//&umkis    }

    public ValidatorResults getValidatorResults() {
		return theValidatorResults;
	}
//&umkis    private boolean checkTime()
//&umkis    {
//&umkis        String sr = FileUtil.searchProperty("checkTimeTransformationService");
//&umkis        if (sr == null) return false;
//&umkis        sr = sr.toLowerCase().trim();
//&umkis        if ((sr.equals("true"))||(sr.equals("yes"))) return true;
//&umkis        return false;
//&umkis    }

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
 * HISTORY      : Revision 1.49  2009/10/06 06:19:23  altturbo
 * HISTORY      : Using schemas.zip file for xml validation.
 * HISTORY      :
 * HISTORY      : Revision 1.48  2009/09/29 15:55:42  altturbo
 * HISTORY      : update the part of control message wrapping - process(String wrappingControlMessageFileName)
 * HISTORY      :
 * HISTORY      : Revision 1.47  2009/09/11 18:21:51  altturbo
 * HISTORY      : for control message wrapper
 * HISTORY      :
 * HISTORY      : Revision 1.46  2009/05/28 19:37:59  altturbo
 * HISTORY      : upgrade for nameSpace and lowercase Head Element
 * HISTORY      :
 * HISTORY      : Revision 1.45  2009/04/21 17:40:48  altturbo
 * HISTORY      : minor change -- add comment
 * HISTORY      :
 * HISTORY      : Revision 1.44  2009/04/17 20:08:17  wangeug
 * HISTORY      : enable web service:process V2 messages
 * HISTORY      :
 * HISTORY      : Revision 1.43  2009/04/06 18:39:34  altturbo
 * HISTORY      : minor change - edit remarks
 * HISTORY      :
 * HISTORY      : Revision 1.42  2009/04/02 06:34:52  altturbo
 * HISTORY      : minor change - remark
 * HISTORY      :
 * HISTORY      : Revision 1.41  2009/04/02 06:03:42  altturbo
 * HISTORY      : minor change - remark
 * HISTORY      :
 * HISTORY      : Revision 1.40  2009/03/19 02:26:28  altturbo
 * HISTORY      : XSD validation codes are hidden and marked with "//&umkis".
 * HISTORY      :
 * HISTORY      : Revision 1.39  2009/03/12 03:59:21  umkis
 * HISTORY      : XSD validation codes are unremarked but deactivated
 * HISTORY      :
 * HISTORY      : Revision 1.38  2009/03/06 18:29:03  wangeug
 * HISTORY      : enable web services
 * HISTORY      :
 * HISTORY      : Revision 1.37  2009/02/26 17:04:46  wangeug
 * HISTORY      : hide XSD validation
 * HISTORY      :
 * HISTORY      : Revision 1.36  2009/02/25 15:57:41  wangeug
 * HISTORY      : enable webstart
 * HISTORY      :
 * HISTORY      : Revision 1.35  2009/02/17 18:53:10  umkis
 * HISTORY      : schemaFileName is inserted for specialized schema validation
 * HISTORY      :
 * HISTORY      : Revision 1.34  2009/02/16 16:23:08  umkis
 * HISTORY      : per Eugenes asking, the line "import gov.nih.nci.caadapter.ui.common.DefaultSettings;" was deleted.
 * HISTORY      :
 * HISTORY      : Revision 1.33  2009/02/10 18:42:51  umkis
 * HISTORY      : Include schema validation against xsd files when V3 message generating. - additional modifying
 * HISTORY      :
 * HISTORY      : Revision 1.32  2009/02/10 05:34:18  umkis
 * HISTORY      : Include schema validation against xsd files when V3 message generating.
 * HISTORY      :
 * HISTORY      : Revision 1.31  2009/02/06 20:51:17  wangeug
 * HISTORY      : process multiple v2 messages in one source data file; call only process() method for all source datatype
 * HISTORY      :
 * HISTORY      : Revision 1.30  2009/02/02 14:54:18  wangeug
 * HISTORY      : Load V2 meta with version number and message schema name; do not use the absolute path of schema file
 * HISTORY      :
 * HISTORY      : Revision 1.29  2009/01/13 17:34:01  wangeug
 * HISTORY      : write  generate HL7 message into zip file
 * HISTORY      :
 * HISTORY      : Revision 1.28  2009/01/13 14:53:02  wangeug
 * HISTORY      : comment out testing code and correct misspelled method name
 * HISTORY      :
 * HISTORY      : Revision 1.27  2008/12/08 18:59:22  wangeug
 * HISTORY      : pre-process V2 message : attach CTR at end of each message segment
 * HISTORY      :
 * HISTORY      : Revision 1.26  2008/11/21 16:19:36  wangeug
 * HISTORY      : Move back to HL7 module from common module
 * HISTORY      :
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
