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


import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * By given csv file and mapping file, call generate method which will return the list of TransformationResult.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.3 $
 * @date $Date: 2007-07-20 17:00:20 $
 * @since caAdapter v1.2
 */

public class TransformationService
{
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/transformation/TransformationService.java,v 1.3 2007-07-20 17:00:20 wangeug Exp $";

    private boolean isCsvString = false;
    private boolean isInputStream = false;
    private String csvString = "";
    private File mapfile = null;
    private File csvfile = null;
    private File scsfile = null;
    private InputStream csvStream = null;
    private CSVSegmentedFile csvSegmentedFile = null;

    public TransformationService(String mapfilename, String csvfilename)
    {
        if (mapfilename == null || csvfilename == null)
        {
            throw new IllegalArgumentException("Map File or csv File should not be null!");
        }

        this.mapfile = new File(mapfilename);
        this.csvfile = new File(csvfilename);
    }
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
     * @return list of MapGenerateRusult.
     */

    
    public List process() throws Exception
    {
    	
    	Hashtable mappings = new Hashtable();
    	Hashtable<String, FunctionComponent> funcations = new Hashtable<String, FunctionComponent>();
    	/*
    	 * TODO Exception handling here
    	 */
        MapParser mapParser = new MapParser();
        mappings = mapParser.processOpenMapFile(mapfile);
        funcations = mapParser.getFunctions();

        
        
        if (!mapParser.getValidatorResults().isValid())
        {	
        	System.out.println("Invalid .map file");
        	return null;
        }
        
        CSVDataResult csvDataResult = null;
        if (isInputStream) {
        	csvDataResult=parseCsvInputStream(mapParser.getSCSFilename());
        }
        else if (isCsvString) {
        	csvDataResult= parseCsvString(mapParser.getSCSFilename());
        }
        else {
        	csvDataResult= parseCsvfile(mapParser.getSCSFilename());
        }

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

        
        
        MIFClass mifClass = loadMIF(fullh3sfilepath);
        
        MapProcessor mapProcess = new MapProcessor();
        
        List<XMLElement> xmlElements = mapProcess.process(mappings, funcations, csvSegmentedFile, mifClass);
        for(XMLElement xmlElement:xmlElements) {
        	System.out.println("Message:"+xmlElement.toXML());
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
	public MIFClass loadMIF(String mifFileName) {
		MIFClass mifClass = null;
		InputStream is;
		try {
			is = new FileInputStream(mifFileName);
		}catch(Exception e) {
			//Cannot file the file
			return null;
		}
		try{
			ObjectInputStream ois = new ObjectInputStream(is);
			mifClass = (MIFClass)ois.readObject();
			ois.close();
			is.close();
			return mifClass;
		}catch (Exception e) {
			return null;
		}
	}

    public static void main(String[] argv) throws Exception {
        long begintime2 = System.currentTimeMillis();

       	TransformationService ts = new TransformationService("C:/Projects/caadapter-gforge-2007-May/tests/150003.map",
		"C:/Projects/caadapter-gforge-2007-May/tests/COCT_MT150003.csv");
       	ts.process();
       	System.out.println(System.currentTimeMillis()-begintime2);
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/07/19 15:11:15  wuye
 * HISTORY      : Fixed the attribute sort problem
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/17 19:54:09  wuye
 * HISTORY      : cvs to HL7 v3 transformation
 * HISTORY      :
 * HISTORY      :
 */
