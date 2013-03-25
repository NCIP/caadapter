/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.specification.csv.wizard;

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.MetaException;
import gov.nih.nci.caadapter.common.csv.CSVMetaGeneratorUtil;
import gov.nih.nci.caadapter.common.csv.CSVMetaResult;
import gov.nih.nci.caadapter.common.csv.CSVParser;
import gov.nih.nci.caadapter.common.csv.CSVParserImpl;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVFieldMetaImpl;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVMetaImpl;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVSegmentMetaImpl;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates a "starting point" csv meta object graph from a CSV instance.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.6 $
 * @date $Date: 2008-09-29 20:12:05 $
 * @since caAdapter v1.2
 */

public class CSVMetaFromInstanceUtil implements CSVMetaGeneratorUtil {
    private static final String LOGID = "$RCSfile: CSVMetaFromInstanceUtil.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/wizard/CSVMetaFromInstanceUtil.java,v 1.6 2008-09-29 20:12:05 wangeug Exp $";

    private String filename = null;
    private String[][] fileData = null;
    private boolean isNonStructure = false;
    private String nonStructureFilename = null;
    private static final int SEGMENT_COLUMN = 0;

    public CSVMetaFromInstanceUtil(String filename) {
        this.filename = filename;
    }

    public CSVMetaResult getMetadata() throws MetaException {
        CSVMetaResult csvMetaResult = new CSVMetaResult();
        ValidatorResults validatorResults = new ValidatorResults();
        csvMetaResult.setValidatorResults(validatorResults);

        // ONLY parse .csv files.
        if (!isExtensionValid()) {
            Message msg = MessageResources.getMessage("GEN0", new Object[]{"File must have a " + Config.CSV_DATA_FILE_DEFAULT_EXTENSTION + " extension."});
            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
            return csvMetaResult;
        }

        CSVParser c = new CSVParserImpl();
        try {
            this.fileData = c.fetch(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CSVMetaImpl meta = new CSVMetaImpl();
       // CSVMetaImpl meta = new CSVMetaImpl();
        //csvMetaResult.setCsvMeta(meta);

        ArrayList<String> segmentNames = getUniqueSegmentNames();

        // create the segments.
        boolean isFirstSegment = true;
        CSVSegmentMetaImpl rootSegment = null;
        CSVSegmentMetaImpl firstSegment = null;

        if( isNonStructure ) {
            meta.setNonStructure( true );
            rootSegment = new CSVSegmentMetaImpl(nonStructureFilename, null);
            meta.setRootSegment(rootSegment);

            String segmentName = segmentNames.get(0);
            firstSegment = new CSVSegmentMetaImpl(segmentName, null);

            ArrayList<String> fieldNames = getFieldList(firstSegment.getName());

            for (int i = 0; i < fieldNames.size(); i++) {
                 String strFieldName = fieldNames.get(i);
                 //System.out.println("str " + strFieldName);
                 CSVFieldMetaImpl field = new CSVFieldMetaImpl(i+1, strFieldName, rootSegment);
                 rootSegment.addField(field);
            }
        }
        else {
            for (int i = 0; i < segmentNames.size(); i++) {
                String segmentName = segmentNames.get(i).toUpperCase();
                if (isFirstSegment) {
                    rootSegment = new CSVSegmentMetaImpl(segmentName, null);
                    meta.setRootSegment(rootSegment);
                    isFirstSegment = false;
                } else {
                    CSVSegmentMetaImpl segment = new CSVSegmentMetaImpl(segmentName, rootSegment);
                    rootSegment.addSegment(segment);
                }
            }

            // create the fields.
            genFields(rootSegment);
            List<CSVSegmentMeta> childSegments = rootSegment.getChildSegments();
            for (int i = 0; i < childSegments.size(); i++) {
                CSVSegmentMeta csvSegmentMeta = childSegments.get(i);
                genFields(csvSegmentMeta);
            }
        }
        csvMetaResult.setCsvMeta(meta);
        return csvMetaResult;
    }

    private boolean isExtensionValid() {
        String[] array = this.filename.split("\\.");
        if (Config.CSV_DATA_FILE_DEFAULT_EXTENSTION.contains(array[array.length - 1])) {
            return true;
        } else {
            return false;
        }
    }

    private void genFields(CSVSegmentMeta segment) {
        int fieldCount = findMaxColumns(segment.getName());
        for (int j = 0; j < fieldCount; j++) {
            CSVFieldMetaImpl field = new CSVFieldMetaImpl(j + 1, "FieldName" + (j + 1), segment);
            segment.addField(field);
        }
    }

    private void genNonStructureFields(CSVSegmentMeta segment) {

            ArrayList<String> fieldNames = getFieldList(segment.getName());

            for (int i = 0; i < fieldNames.size(); i++) {
                 String strFieldName = fieldNames.get(i);
                 System.out.println("str " + strFieldName);
                 CSVFieldMetaImpl field = new CSVFieldMetaImpl(i+1, strFieldName, segment);
                 segment.addField(field);
            }
    }

    private ArrayList<String> getUniqueSegmentNames() {
        // setup the array.
        ArrayList<String> a = new ArrayList<String>();
        for (int i = 0; i < fileData.length; i++) {
            String[] row = fileData[i];
            // only add if it isn't there already.
            if (!a.contains(row[SEGMENT_COLUMN])) a.add(row[SEGMENT_COLUMN]);
        }
        // return it.
        return a;
    }

    private int findMaxColumns(String segmentName) {
        int colCount = 0;
        for (int i = 0; i < fileData.length; i++) {
            String[] row = fileData[i];
            // Is this the row based on the segment that you want?
            if (segmentName.equalsIgnoreCase(row[SEGMENT_COLUMN])) {
//                System.out.println(segmentName + " row : " + i + " colcount : " + row.length);
                // how many columns are in this row?
                int tempCount = 0;
                for (int j = 0; j < row.length; j++) {
                    // is there something in this cell?
                    if (row[j] != null && !"".equalsIgnoreCase(row[j])) {
                        // if so, update the temporary count.
                        //System.out.println("row: " + row[j]);
                        tempCount = j;
                    }
                }
                // update the count if this is greater than what
                // we have already.
                if (tempCount > colCount) {
                    // you would expect to subtract 1 here becuase the segment name doesn't count.
                    // we don't have to becuse our counting began at zero.
                    colCount = tempCount;
                }
            } else {
                // skip this row - it's a segment we don't care about.
            }
        }
        return colCount;
    }

       private ArrayList<String> getFieldList(String segmentName) {
        ArrayList<String> a = new ArrayList<String>();
        int colCount = 0;
        for (int i = 0; i < fileData.length; i++) {
            String[] row = fileData[i];
            // Is this the row based on the segment that you want?
            if (segmentName.equalsIgnoreCase(row[SEGMENT_COLUMN])) {
//                System.out.println(segmentName + " row : " + i + " colcount : " + row.length);
                // how many columns are in this row?
                int tempCount = 0;
                for (int j = 0; j < row.length; j++) {
                    // is there something in this cell?
                    if (row[j] != null && !"".equalsIgnoreCase(row[j])) {
                        // if so, update the temporary count.
                        System.out.println("row: " + row[j]);
                        a.add(row[j]);
                        tempCount = j;
                    }
                }
                // update the count if this is greater than what
                // we have already.
                if (tempCount > colCount) {
                    // you would expect to subtract 1 here becuase the segment name doesn't count.
                    // we don't have to becuse our counting began at zero.
                    colCount = tempCount;
                }
            } else {
                // skip this row - it's a segment we don't care about.
            }
        }
        return a;
    }

    public boolean isNonStructure() {
        return isNonStructure;
    }

    public void setNonStructure(boolean nonStructure) {
        isNonStructure = nonStructure;
    }

    public String getNonStructureFilename() {
        return nonStructureFilename;
    }

    public void setNonStructureFilename(String nonStructureFilename) {
        this.nonStructureFilename = nonStructureFilename;
    }
}
/**
 * HISTORY: $Log: not supported by cvs2svn $
**/
