/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/wizard/CSVMetaFromInstanceUtil.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
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
 * @version $Revision: 1.1 $
 * @date $Date: 2007-04-03 16:18:15 $
 * @since caAdapter v1.2
 */

public class CSVMetaFromInstanceUtil implements CSVMetaGeneratorUtil {
    private static final String LOGID = "$RCSfile: CSVMetaFromInstanceUtil.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/wizard/CSVMetaFromInstanceUtil.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $";

    private String filename = null;
    private String[][] fileData = null;
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
        csvMetaResult.setCsvMeta(meta);

        ArrayList<String> segmentNames = getUniqueSegmentNames();

        // create the segments.
        boolean isFirstSegment = true;
        CSVSegmentMetaImpl rootSegment = null;
        for (int i = 0; i < segmentNames.size(); i++) {
            String segmentName = segmentNames.get(i);
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
}
