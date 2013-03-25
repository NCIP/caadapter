/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common.csv;

import gov.nih.nci.caadapter.castor.csv.meta.impl.C_csvMetadata;
import gov.nih.nci.caadapter.castor.csv.meta.impl.C_field;
import gov.nih.nci.caadapter.castor.csv.meta.impl.C_segment;
import gov.nih.nci.caadapter.castor.csv.meta.impl.C_segmentItem;
import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.MetaParser;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.csv.meta.impl.*;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

import java.io.FileReader;

/**
 * Builds a csv meta object graph from a csv specification.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @version $Revision: 1.7 $
 * @date $Date: 2008-09-24 20:52:36 $
 * @since caAdapter v1.2
 */

public class CSVMetaParserImpl implements MetaParser {
    private static final String LOGID = "$RCSfile: CSVMetaParserImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/CSVMetaParserImpl.java,v 1.7 2008-09-24 20:52:36 phadkes Exp $";


    public CSVMetaResult parse(FileReader metafile){
        C_csvMetadata c = null;
        CSVMetaResult csvMetaResult = new CSVMetaResult();
        try {
            c = (C_csvMetadata) C_csvMetadata.unmarshalC_csvMetadata(metafile);
            csvMetaResult.setCsvMeta(processRoot(c));
        } catch (Exception e) {
            Log.logException(this, e);
            Message msg = MessageResources.getMessage("GEN0", new Object[]{"Could not parse CSV Meta file."});
            ValidatorResults validatorResults = new ValidatorResults();
            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
            csvMetaResult.setValidatorResults(validatorResults);
        }
        return csvMetaResult;
    }

    private CSVMetaImpl processRoot(C_csvMetadata cm) {
        CSVMetaImpl csvFileMetaImpl = new CSVMetaImpl();
        csvFileMetaImpl.setRootSegment(processSegment(cm.getC_segment(), null));
        csvFileMetaImpl.setXmlPath(cm.getXmlPath());
        if (cm.getType()!=null&&cm.getType().equalsIgnoreCase("NON_STRUCTURE"))
        	csvFileMetaImpl.setNonStructure(true);
        return csvFileMetaImpl;
    }

    private CSVSegmentMetaImpl processSegment(C_segment s, CSVSegmentMeta parent) {
        CSVSegmentMetaImpl csvSegmentMetaImpl = new CSVSegmentMetaImpl(s.getName(), parent);
        if (parent != null) csvSegmentMetaImpl.setCardinalityType(s.getCardinality());
        csvSegmentMetaImpl.setXmlPath(s.getXmlPath());

        C_segmentItem[] si = s.getC_segmentItem();
        for (int i = 0; i < si.length; i++) {
            C_segmentItem segmentItem = si[i];
            if (segmentItem.getC_field() != null) {
                csvSegmentMetaImpl.addField(processField(segmentItem.getC_field(), csvSegmentMetaImpl));
            }
            if (segmentItem.getC_segment() != null) {
                csvSegmentMetaImpl.addSegment(processSegment(segmentItem.getC_segment(), csvSegmentMetaImpl));
            }

        }
        return csvSegmentMetaImpl;
    }

    private CSVFieldMetaImpl processField(C_field f, CSVSegmentMetaImpl parent) {
        CSVFieldMetaImpl c = new CSVFieldMetaImpl(f.getColumn(), (String) f.getName(), parent);
        c.setXmlPath(f.getXmlPath());
        return c;
    }
}

/*
    public CSVMetaResult parse(FileReader metafile){
        C_csvMetadata c = null;
        CSVMetaResult csvMetaResult = new CSVMetaResult();
        try {
            c = (C_csvMetadata) C_csvMetadata.unmarshalC_csvMetadata(metafile);
            csvMetaResult.setCsvMeta(processRoot(c));
        } catch (Exception e) {
            Log.logException(this, e);
            Message msg = MessageResources.getMessage("GEN0", new Object[]{"Could not parse CSV Meta file."});
            ValidatorResults validatorResults = new ValidatorResults();
            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
            csvMetaResult.setValidatorResults(validatorResults);
        }
        return csvMetaResult;
    }

    private CSVMetaImpl processRoot(C_csvMetadata cm) {
        CSVMetaImpl csvFileMetaImpl = new CSVMetaImpl();
        csvFileMetaImpl.setRootSegment(processSegment(cm.getC_segment(), null));
        csvFileMetaImpl.setUUID(cm.getUuid());
        return csvFileMetaImpl;
    }

    private CSVSegmentMetaImpl processSegment(C_segment s, CSVSegmentMeta parent) {
        CSVSegmentMetaImpl csvSegmentMetaImpl = new CSVSegmentMetaImpl(s.getName(), parent);
        csvSegmentMetaImpl.setUUID(s.getUuid());
        csvSegmentMetaImpl.setCardinality(s.getCardinality());
        C_segmentItem[] si = s.getC_segmentItem();
        for (int i = 0; i < si.length; i++) {
            C_segmentItem segmentItem = si[i];
            if (segmentItem.getC_field() != null) {
                csvSegmentMetaImpl.addField(processField(segmentItem.getC_field(), csvSegmentMetaImpl));
            }
            if (segmentItem.getC_segment() != null) {
                csvSegmentMetaImpl.addSegment(processSegment(segmentItem.getC_segment(), csvSegmentMetaImpl));
            }

        }
        return csvSegmentMetaImpl;
    }

    private CSVFieldMetaImpl processField(C_field f, CSVSegmentMetaImpl parent) {
        CSVFieldMetaImpl c = new CSVFieldMetaImpl(f.getColumn(), (String) f.getName(), parent);
        c.setUUID(f.getUuid());
        return c;
    }
}
*/

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
