/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common.csv.data.impl;

import gov.nih.nci.caadapter.common.DataObjectImpl;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.csv.data.CSVField;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;

/**
 * Implementation of a field that is contained within segmented csv data file.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.3 $
 * @date        $Date: 2008-09-24 20:00:10 $
 */

public class CSVFieldImpl extends DataObjectImpl implements CSVField{
    private static final String LOGID = "$RCSfile: CSVFieldImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/data/impl/CSVFieldImpl.java,v 1.3 2008-09-24 20:00:10 phadkes Exp $";

    private int column;
    private String value;

    // constructors
    public CSVFieldImpl(CSVFieldMeta metaObject) {
        super(metaObject);
    }

    public CSVFieldImpl(MetaObject metaObject, int column, String value) {
        super(metaObject);
        this.column = column;
        this.value = value;
    }

    // getters and setters
    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
