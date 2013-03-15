/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.hl7.report;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.map.BaseMapElement;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.map.Mapping;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * This class defines functions to generate a basic map report in Excel format.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:53:50 $
 */
public class MapReportGenerator
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: MapReportGenerator.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/report/MapReportGenerator.java,v 1.3 2008-06-09 19:53:50 phadkes Exp $";

	/**
	 * Terminology used within this class:
	 * Origination: means the source of a map link, could be a Source, a Function's output;
	 * Destination: means the target of a map link, could be a Target, a Function's input;
	 *
	 * Source: refer to the data source that the most map is generated from;
	 * Target: refer to the data source that the most map is pointed to;
	 */

	public static final String MAPPED_WORKSHEET_TITLE = "Mapped";
	public static final String UNMAPPED_WORKSHEET_TITLE = "Unmapped";

	public static final String SOURCE_TITLE = "Source";
	public static final String TARGET_TITLE = "Target";
	public static final String FUNCTION_TITLE = "Function";

	public static final String MAPPED_SOURCE_TO_FUNCTION_TYPE = "(" + SOURCE_TITLE + "_" + FUNCTION_TITLE + ")";
	public static final String MAPPED_SOURCE_TO_TARGET_TYPE = "(" + SOURCE_TITLE + "_" + TARGET_TITLE + ")";
	public static final String MAPPED_FUNCTION_TO_TARGET_TYPE = "(" + FUNCTION_TITLE + "_" + TARGET_TITLE + ")";
	public static final String MAPPED_FUNCTION_TO_FUNCTION_TYPE = "(" + FUNCTION_TITLE + "_" + FUNCTION_TITLE + ")";

	public static final String UNMAPPED_SOURCE_TITLE = UNMAPPED_WORKSHEET_TITLE + "_" + SOURCE_TITLE;
	public static final String UNMAPPED_TARGET_TITLE = UNMAPPED_WORKSHEET_TITLE + "_" + TARGET_TITLE;


	private HSSFWorkbook workbook = null;

	private HSSFSheet sourceToTargetWorksheet = null;
	private HSSFSheet sourceToFunctionWorksheet = null;
	private HSSFSheet functionToFunctionWorksheet = null;
	private HSSFSheet functionToTargetWorksheet = null;

	HashSet<String> allCSVElement = new HashSet<String>();
	HashSet<String> allMifElement = new HashSet<String>();
	Hashtable<String,String> allMifElementMappedXML = new Hashtable<String,String>();
	HashSet<String> visitiedCSVElement = new HashSet<String>();
	HashSet<String> visitiedMifElement = new HashSet<String>();

	private HashMap<String, Integer> sheetRowCount = new HashMap<String, Integer>();

	public MapReportGenerator()
	{
	}

	/**
	 * The method will generate an Excel spredsheet that contains all mapped and unmapped elements.
	 * @param file the Excel file
	 * @param mappingMeta contains the actual mapping information
	 * @return validation results during the generation
	 */
	public ValidatorResults generate(File file, Mapping mappingMeta) //throws Exception
	{
		ValidatorResults validatorResults = new ValidatorResults();
		FileOutputStream fo = null;
		BufferedOutputStream bo = null;
		try
		{
			init(mappingMeta);
			workbook = new HSSFWorkbook();
			fo = new FileOutputStream(file);
			bo = new BufferedOutputStream(fo);

			//keep this sequence intact, since unmapped generation will depend on result from mapped object generation.
			generatedMappedReport(mappingMeta);
			generateUnmappedReport(mappingMeta);

			workbook.write(bo);
			bo.flush();
		}
		catch (Exception e)
		{
			Log.logException(this, e);
			Message msg = MessageResources.getMessage("GEN0", new Object[]{e.getMessage()});
			validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
		}
		finally
		{
			try
			{
				if(bo!=null)
				{
					bo.close();
				}
			}
			catch(Exception e)
			{//intentionally ignore
			}
		}
		return validatorResults;
	}

	private void init(Mapping mappingMeta) throws Exception
	{
		loadSCS(((CSVMeta)(mappingMeta.getSourceComponent().getMeta())).getRootSegment());
		loadH3S((MIFClass)(mappingMeta.getTargetComponent().getMeta()), "", "");
	}


	private void loadSCS(CSVSegmentMeta csvSegmentMeta) throws Exception
	{
		allCSVElement.add(csvSegmentMeta.getXmlPath());
		for (CSVFieldMeta cvsField:csvSegmentMeta.getFields()) {
			allCSVElement.add(cvsField.getXmlPath());

		}
		for (CSVSegmentMeta csvChildSegmentMeta:csvSegmentMeta.getChildSegments()) {
			loadSCS(csvChildSegmentMeta);
		}
	}

	private void loadH3S(MIFClass mifClass, String parentXmlPath, String parentXmlPathMapped)
	{
		allMifElement.add(parentXmlPath+mifClass.getName());
		allMifElementMappedXML.put(parentXmlPathMapped+mifClass.getName(),parentXmlPath+mifClass.getName());

    	if (mifClass.getChoices().size()>0) { //it's a choice class,

    		HashSet<MIFClass> choices = mifClass.getChoices();

    		for(MIFClass mifClassChoice : choices) {
    			if (mifClassChoice.isChoiceSelected()) {
    				if (parentXmlPath.equals(""))
    					loadH3S(mifClassChoice, mifClass.getName(), mifClass.getName());
    				else {
    					loadH3S(mifClassChoice, parentXmlPath, parentXmlPathMapped);
    				}
    			}
        	}
    	}
    	else {
        	HashSet<MIFAttribute> attributes = mifClass.getAttributes();

        	for(MIFAttribute mifAttribute:attributes) {
				if (parentXmlPath.equals(""))
					loadH3S(mifAttribute, mifClass.getName(), mifClass.getName());
				else {
					loadH3S(mifAttribute, parentXmlPath, parentXmlPathMapped);
				}
        	}

        	HashSet<MIFAssociation> associations = mifClass.getAssociations();

        	for(MIFAssociation mifAssociation : associations) {
				if (parentXmlPath.equals(""))
					loadH3S(mifAssociation, mifClass.getName(), mifClass.getName());
				else {
					loadH3S(mifAssociation, parentXmlPath, parentXmlPathMapped);
				}
        	}
    	}
	}

	private void loadH3S(MIFAssociation mifAssociation, String parentXmlPath, String parentXmlPathMapped) {
    	if (mifAssociation.getMifClass()!= null) {
	    	allMifElementMappedXML.put(parentXmlPathMapped+"." + mifAssociation.getNodeXmlName(),parentXmlPath+"." + mifAssociation.getName());
    		loadH3S(mifAssociation.getMifClass(),parentXmlPath+"."+mifAssociation.getName(), parentXmlPathMapped + "."+mifAssociation.getNodeXmlName());
    	}
	}

	private void loadH3S(MIFAttribute mifAttribute, String parentXmlPath, String parentXmlPathMapped) {
    	if (mifAttribute.getDatatype() == null) return; //Abstract attrbiute
    	loadH3S(mifAttribute.getDatatype(),parentXmlPath+"."+mifAttribute.getName(),parentXmlPathMapped+"."+mifAttribute.getNodeXmlName());
	}

	private void loadH3S(Datatype datatype, String parentXPath,  String parentXmlPathMapped) {
    	if (!datatype.isEnabled()) return;
    	allMifElement.add(parentXPath);
    	allMifElementMappedXML.put(parentXmlPathMapped,parentXPath);
//    	System.out.println("mapped:"+parentXmlPathMapped);

    	if (datatype.isSimple()) {
    		return;
    	}

    	for(String attributeName:(Set<String>)(datatype.getAttributes().keySet())) {

    		Attribute attr = (Attribute)datatype.getAttributes().get(attributeName);
    		boolean isSimple = false;

//    		String datatypeattribute = parentXPath+"."+attr.getNodeXmlName();
    		if (attr.getReferenceDatatype() == null) {
    			isSimple = true;
    		}
    		else {
    			if (attr.getReferenceDatatype().isSimple()) isSimple = true;
    		}
    		if (isSimple) {
    			allMifElement.add(parentXPath+"."+attributeName);
    	    	allMifElementMappedXML.put(parentXmlPathMapped+"." + attributeName,parentXPath+"." + attributeName);
//    	    	System.out.println("mapped attr:"+parentXmlPathMapped+"." + attributeName);

    		}//End is Simple
    		else {//is complex
    			loadH3S(attr.getReferenceDatatype(), parentXPath+"."+attributeName, parentXmlPathMapped+"."+attributeName);
    		}
    	}
	}

	private void generatedMappedReport(Mapping mappingMeta)
	{
		List<gov.nih.nci.caadapter.hl7.map.Map> mappingLinkList = mappingMeta.getMaps();
		int size = mappingLinkList==null ? 0 : mappingLinkList.size();
		for(int i=0; i<size; i++)
		{
			gov.nih.nci.caadapter.hl7.map.Map oneLink = mappingLinkList.get(i);
			BaseMapElement sourceMapElement = oneLink.getSourceMapElement();
			BaseMapElement targetMapElement = oneLink.getTargetMapElement();

			String actualMappedOriginationSet = null;
			String actualMappedDestinationSet = null;
			HSSFSheet worksheet = null;
			String firstRowText = null;
			boolean toPrintHeader = false;
			if(sourceMapElement.isComponentOfSourceType() && targetMapElement.isComponentOfTargetType())
			{//direct mapping
				visitiedCSVElement.add(sourceMapElement.getXmlPath());
				visitiedMifElement.add(allMifElementMappedXML.get(targetMapElement.getDataXmlPath()));
				firstRowText = MAPPED_WORKSHEET_TITLE + MAPPED_SOURCE_TO_TARGET_TYPE;
				if (sourceToTargetWorksheet == null)
				{
					sourceToTargetWorksheet = workbook.createSheet(firstRowText);
					toPrintHeader = true;
				}
				actualMappedOriginationSet = sourceMapElement.getDataXmlPath();
				actualMappedDestinationSet = allMifElementMappedXML.get(targetMapElement.getDataXmlPath());
				worksheet = sourceToTargetWorksheet;
			}
			else if(sourceMapElement.isComponentOfSourceType() && targetMapElement.isComponentOfFunctionType())
			{
				visitiedCSVElement.add(sourceMapElement.getDataXmlPath());
				firstRowText = MAPPED_WORKSHEET_TITLE + MAPPED_SOURCE_TO_FUNCTION_TYPE;
				if (sourceToFunctionWorksheet == null)
				{
					sourceToFunctionWorksheet = workbook.createSheet(firstRowText);
					toPrintHeader = true;
				}
				actualMappedOriginationSet = sourceMapElement.getDataXmlPath();
				actualMappedDestinationSet = targetMapElement.getDataXmlPath();
				worksheet = sourceToFunctionWorksheet;
			}
			else if(sourceMapElement.isComponentOfFunctionType() && targetMapElement.isComponentOfFunctionType())
			{
				firstRowText = MAPPED_WORKSHEET_TITLE + MAPPED_FUNCTION_TO_FUNCTION_TYPE;
				if (functionToFunctionWorksheet == null)
				{
					functionToFunctionWorksheet = workbook.createSheet(firstRowText);
					toPrintHeader = true;
				}
				actualMappedOriginationSet = sourceMapElement.getDataXmlPath();
				actualMappedDestinationSet = targetMapElement.getDataXmlPath();
				worksheet = functionToFunctionWorksheet;
			}
			else
			{//by default, the function to target mapping
				visitiedMifElement.add(allMifElementMappedXML.get(targetMapElement.getDataXmlPath()));
				firstRowText = MAPPED_WORKSHEET_TITLE + MAPPED_FUNCTION_TO_TARGET_TYPE;
				if (functionToTargetWorksheet == null)
				{
					functionToTargetWorksheet = workbook.createSheet(firstRowText);
					toPrintHeader = true;
				}
				actualMappedOriginationSet = sourceMapElement.getDataXmlPath();
				actualMappedDestinationSet = allMifElementMappedXML.get(targetMapElement.getDataXmlPath());
				worksheet = functionToTargetWorksheet;
			}

			if(toPrintHeader)
			{//first time creation.
				printHeading(worksheet, firstRowText, true);
			}
			printMappedContent(worksheet, actualMappedOriginationSet, actualMappedDestinationSet, firstRowText);
		}
	}

	/**
	 * Do not generate unmapped function parameter here, since the structure is different.
	 */
	private void generateUnmappedReport(Mapping mappingMeta)
	{
		HSSFSheet csvWorksheet = workbook.createSheet(UNMAPPED_SOURCE_TITLE);
		printHeading(csvWorksheet,  UNMAPPED_SOURCE_TITLE, false);
		int rowCount = sheetRowCount.get(UNMAPPED_SOURCE_TITLE).intValue();
		HSSFRow row = null;
		for(String string:allCSVElement)
		{
			if (!visitiedCSVElement.contains(string))
			{
				row = csvWorksheet.createRow(rowCount);
				HSSFCell cell = row.createCell((short) 0);
				cell.setCellValue(string);
				rowCount++;
			}
		}

		HSSFSheet h3sWorksheet = workbook.createSheet(UNMAPPED_TARGET_TITLE);
		printHeading(h3sWorksheet,  UNMAPPED_TARGET_TITLE, false);
		rowCount = sheetRowCount.get(UNMAPPED_TARGET_TITLE).intValue();
		row = null;
		for(String string:allMifElement)
		{
			if (!visitiedMifElement.contains(string))
			{
				row = h3sWorksheet.createRow(rowCount);
				HSSFCell cell = row.createCell((short) 0);
				cell.setCellValue(string);
				rowCount++;
			}
		}
	}

	/**
	 * Print once and only once when the worksheet is created.
	 * @param worksheet
	 */
	private void printHeading(HSSFSheet worksheet, String firstRowText, boolean mappedHeading)
	{
		HSSFRow row = worksheet.createRow(0);
		HSSFCell cell = null;
		cell = row.createCell((short) 0);
		cell.setCellValue(firstRowText);
		if(mappedHeading)
		{
			row = worksheet.createRow(1);
			cell = row.createCell((short) 0);
			cell.setCellValue("Path of Origination");
			cell = row.createCell((short) 1);
			cell.setCellValue("Path of Destination");
			sheetRowCount.put(firstRowText, new Integer(2));
		}
		else
		{
			sheetRowCount.put(firstRowText, new Integer(1));
		}
	}

	private void printMappedContent(HSSFSheet worksheet, String source, String target, String keyToRowCount)
	{

		Integer rowCount = sheetRowCount.get(keyToRowCount);
		HSSFRow row = worksheet.createRow(rowCount.intValue());
		HSSFCell cell = null;
		cell = row.createCell((short) 0);
		cell.setCellValue(source);
		cell = row.createCell((short) 1);
		cell.setCellValue(target);

		//update the row count
		Integer updatedRowCount = new Integer(rowCount.intValue() + 1);
		sheetRowCount.put(keyToRowCount, updatedRowCount);
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/08/31 20:00:35  wuye
 * HISTORY      : Fixed the missing association name issue
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/31 21:37:16  wuye
 * HISTORY      : New map report generator
 * HISTORY      :
 */
