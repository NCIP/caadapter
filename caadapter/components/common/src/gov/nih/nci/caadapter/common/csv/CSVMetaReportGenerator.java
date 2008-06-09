/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.common.csv;

import gov.nih.nci.caadapter.common.MetaException;
import gov.nih.nci.caadapter.common.SystemException;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Generates a SCM report (in Excel format).
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @version $Revision: 1.3 $
 * @date $Date: 2008-06-09 19:53:49 $
 * @since caAdapter v1.2
 */
public class CSVMetaReportGenerator {
    private static final String LOGID = "$RCSfile: CSVMetaReportGenerator.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/CSVMetaReportGenerator.java,v 1.3 2008-06-09 19:53:49 phadkes Exp $";

    private int maxfields = 0;

    public void generate(File file, CSVMeta meta) throws MetaException
	{
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet worksheet = workbook.createSheet("CSV Specification Report");
        try {
            FileOutputStream fileOut = new FileOutputStream(file);//filename);
            //start from the first row and the first field
            processSegment(meta.getRootSegment(),worksheet, 1, 0);
            printHeading(worksheet);
            workbook.write(fileOut);
            fileOut.close();
        }catch (IOException e) {
            throw new SystemException(e);
        }
    }

    private void printHeading( HSSFSheet worksheet){
        HSSFRow row = worksheet.createRow(0);
        HSSFCell segmentcell = row.createCell((short)0);
        segmentcell.setCellValue("Segment Name");
        for (int i = 1; i <= maxfields; i++) {
            HSSFCell fieldcell = row.createCell((short)(i));
            fieldcell.setCellValue("Field " + i);
        }
    }

    private void processSegment(CSVSegmentMeta segment, HSSFSheet worksheet, int currentRow, int indent){
        HSSFRow row = worksheet.createRow(currentRow++);
        HSSFCell segmentcell = row.createCell((short)0);
        segmentcell.setCellValue(getIndent(indent) + segment.getName());

        List<CSVFieldMeta> fields = segment.getFields();
        if(fields.size()>maxfields)
            maxfields = fields.size();

        for (int i = 0; i < fields.size(); i++) {
            CSVFieldMeta csvFieldMeta =  fields.get(i);
            HSSFCell fieldcell = row.createCell((short)(i+1));
            fieldcell.setCellValue(csvFieldMeta.getName());
        }

        List<CSVSegmentMeta> childSegments = segment.getChildSegments();
        for (int i = 0; i < childSegments.size(); i++) {
            CSVSegmentMeta csvSegmentMeta =  childSegments.get(i);
            processSegment(csvSegmentMeta, worksheet, currentRow++, indent+1);
        }
    }

    private String getIndent(int ind){
        String s = "";
        for(int i = 0;i<ind;i++){
            s+="    ";
        }
        return s;
    }
}
