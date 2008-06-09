/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.common.csv.meta.impl;

import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;

/**
 * Implementation of an in-memory segmented csv meta file.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.4 $
 * @date        $Date: 2008-06-09 19:53:49 $
 */

public class CSVMetaImpl extends MetaObjectImpl implements CSVMeta {
    private static final String LOGID = "$RCSfile: CSVMetaImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/meta/impl/CSVMetaImpl.java,v 1.4 2008-06-09 19:53:49 phadkes Exp $";

    // a reference to the child.
    private CSVSegmentMeta rootSegment;
    private boolean isNonStructure = false;

    public boolean isNonStructure() {
        return isNonStructure;
    }

    public void setNonStructure(boolean nonStructure) {
        isNonStructure = nonStructure;

    }// CONSTRUCTORS
    public CSVMetaImpl() {
    }

    public CSVMetaImpl(CSVSegmentMeta rootSegment) {
        this.rootSegment = rootSegment;
    }

    // SETTERS AND GETTERS
    public CSVSegmentMeta getRootSegment() {
        return rootSegment;
    }

    public void setRootSegment(CSVSegmentMeta rootSegment) {
        //rootSegment.setName( "Test_filename.csv");
        this.rootSegment = rootSegment;
    }
}
