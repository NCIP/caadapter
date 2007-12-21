/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/CSVMetaParserImpl.java,v 1.5 2007-12-21 15:59:39 wangeug Exp $
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
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.5 $
 * @date $Date: 2007-12-21 15:59:39 $
 * @since caAdapter v1.2
 */

public class CSVMetaParserImpl implements MetaParser {
    private static final String LOGID = "$RCSfile: CSVMetaParserImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/CSVMetaParserImpl.java,v 1.5 2007-12-21 15:59:39 wangeug Exp $";


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