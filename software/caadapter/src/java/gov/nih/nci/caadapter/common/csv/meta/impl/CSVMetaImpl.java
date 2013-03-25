/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
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
 * @version    $Revision: 1.5 $
 * @date        $Date: 2008-09-24 20:40:14 $
 */

public class CSVMetaImpl extends MetaObjectImpl implements CSVMeta {
    private static final String LOGID = "$RCSfile: CSVMetaImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/meta/impl/CSVMetaImpl.java,v 1.5 2008-09-24 20:40:14 phadkes Exp $";

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
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/

