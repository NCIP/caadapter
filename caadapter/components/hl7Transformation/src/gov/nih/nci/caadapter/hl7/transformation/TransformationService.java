/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.hl7.transformation;

import gov.nih.nci.caadapter.common.MetaException;
import gov.nih.nci.caadapter.common.csv.CSVDataResult;
import gov.nih.nci.caadapter.common.csv.SegmentedCSVParserImpl;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.csv.data.CSVSegmentedFile;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;
import gov.nih.nci.caadapter.hl7.validation.HL7V3MessageValidator;


import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * By given csv file and mapping file, call generate method which will return the list of TransformationResult.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wuye $
 * @version $Revision: 1.6 $
 * @date $Date: 2007-08-03 13:25:32 $
 * @since caAdapter v1.2
 */

public class TransformationService
{
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/transformation/TransformationService.java,v 1.6 2007-08-03 13:25:32 wuye Exp $";

    private boolean isCsvString = false;
    private boolean isInputStream = false;
    private String csvString = "";
    private File mapfile = null;
    private File csvfile = null;
    private File scsfile = null;
    private InputStream csvStream = null;
    private CSVSegmentedFile csvSegmentedFile = null;

	/**
	 * This method will create a transformer that loads csv data from a file 
	 * and transforms into HL7 v3 messages
	 * 
	 * @param mapfilename the name of the mapping file
	 * @param csvfilename the name of the csv file
	 */

    public TransformationService(String mapfilename, String csvfilename)
    {
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
	 * @param mapfilename the Java mapping file object
	 * @param csvStream the csv file object
	 */
    public TransformationService(File mapfile, File csvfile)
    {
        if (mapfile == null || csvfile == null)
        {
            throw new IllegalArgumentException("Map File or csv File should not be null!");
        }
        this.mapfile = mapfile;
        this.csvfile = csvfile;
    }

    private TransformationService()
    {
    }

    /**
     * @return list of HL7 v3 message object.
     * To get HL7 v3 message of each object, call .toXML() method of each object
     */

    
    public List<XMLElement> process() throws Exception
    {
    	
    	Hashtable mappings = new Hashtable();
    	Hashtable<String, FunctionComponent> funcations = new Hashtable<String, FunctionComponent>();
    	/*
    	 * TODO Exception handling here
    	 */
        long mapbegintime = System.currentTimeMillis();
        MapParser mapParser = new MapParser();
        mappings = mapParser.processOpenMapFile(mapfile);
        funcations = mapParser.getFunctions();
    	System.out.println("Map Parsing time" + (System.currentTimeMillis()-mapbegintime));

        
        
        if (!mapParser.getValidatorResults().isValid())
        {	
        	System.out.println("Invalid .map file");
        	return null;
        }
        long csvbegintime = System.currentTimeMillis();
        CSVDataResult csvDataResult = null;
        if (isInputStream) 
        {
        	csvDataResult=parseCsvInputStream(mapParser.getSCSFilename());
        }
        else if (isCsvString) 
        {
        	csvDataResult= parseCsvString(mapParser.getSCSFilename());
        }
        else 
        {
        	csvDataResult= parseCsvfile(mapParser.getSCSFilename());
        }
    	System.out.println("CSV Parsing time" + (System.currentTimeMillis()-csvbegintime));
    	
        // parse the datafile, if there are errors.. return.
        final ValidatorResults csvDataValidatorResults = csvDataResult.getValidatorResults();
//        prepareValidatorResults.addValidatorResults(csvDataValidatorResults);
        /*
         * TODO consolidate validatorResults
         */
        if (!csvDataValidatorResults.isValid())
        {
            return null;
        }

        csvSegmentedFile = csvDataResult.getCsvSegmentedFile();
        
        String h3sFilename = mapParser.getH3SFilename();
        String fullh3sfilepath = FileUtil.filenameLocate(mapfile.getParent(), h3sFilename);

        
        long loadmifbegintime = System.currentTimeMillis();
        
        MIFClass mifClass = loadMIF(fullh3sfilepath);
        
    	System.out.println("loadmif Parsing time" + (System.currentTimeMillis()-loadmifbegintime));

    	MapProcessor mapProcess = new MapProcessor();
        
        List<XMLElement> xmlElements = mapProcess.process(mappings, funcations, csvSegmentedFile, mifClass);
    	HL7V3MessageValidator validator = new HL7V3MessageValidator();
        for(XMLElement xmlElement:xmlElements) 
        {
        	System.out.println("Message:"+xmlElement.toXML());
        	System.out.println("ValiationResults: " + xmlElement.getValidatorResults().getAllMessages().size());
        	validator.validate(xmlElement.toXML().toString(), "C:/Projects/caadapter-gforge-2007-May/etc/schemas/multicacheschemas/COCT_MT150003UV03.xsd");
        }
        System.out.println("total message" + xmlElements.size());
        return xmlElements;
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

//       	TransformationService ts = new TransformationService("C:/Projects/caadapter-gforge-2007-May/tests/150003.map",
//		"C:/Projects/caadapter-gforge-2007-May/tests/COCT_MT150003.csv");
//       	TransformationService ts = new TransformationService("C:/xmlpathSpec/xmlpath150003.map",
//		"C:/xmlpathSpec/COCT_MT150003.csv");

//        TransformationService ts = new TransformationService("D:/projects/caadapter-HEAD/components/hl7Transformation/test/data/COCT_MT150003_MAP1.map",
//		"D:/projects/caadapter-HEAD/components/hl7Transformation/test/data/COCT_MT150003_MAP_Scenario_Test.csv");

   	TransformationService ts = new TransformationService("C:/xmlpathSpec/NewEncounter_comp.map",
		"C:/xmlpathSpec/NewEncounter_comp2.csv");
        
        ts.process();
       	System.out.println(System.currentTimeMillis()-begintime2);
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
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
