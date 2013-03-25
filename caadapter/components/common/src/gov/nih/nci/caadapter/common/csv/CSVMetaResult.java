/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common.csv;

import gov.nih.nci.caadapter.common.BaseResult;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

/**
 * Contains CSV meta information and validation results.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @version $Revision: 1.2 $
 * @date $Date: 2008-06-09 19:53:49 $
 * @since caAdapter v1.2
 */

public class CSVMetaResult extends BaseResult {
    private static final String LOGID = "$RCSfile: CSVMetaResult.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/CSVMetaResult.java,v 1.2 2008-06-09 19:53:49 phadkes Exp $";

    private CSVMeta csvMeta = null;

    // constructors.
    public CSVMetaResult() {
    }
    public CSVMetaResult(CSVMeta csvMeta, ValidatorResults validatorResults) {
        this.csvMeta = csvMeta;
        this.validatorResults = validatorResults;
    }

    // setters and getters.
    public CSVMeta getCsvMeta() {
        return csvMeta;
    }
    public void setCsvMeta(CSVMeta csvMeta) {
        this.csvMeta = csvMeta;
    }
}

/**
 *  HISTORY      : $Log: not supported by cvs2svn $
 *  HISTORY      : Revision 1.1  2007/04/03 16:02:37  wangeug
 *  HISTORY      : initial loading of common module
 *  HISTORY      :
 *  HISTORY      : Revision 1.7  2006/08/02 18:44:20  jiangsc
 *  HISTORY      : License Update
 *  HISTORY      :
 *  HISTORY      : Revision 1.6  2006/01/03 19:16:51  jiangsc
 *  HISTORY      : License Update
 *  HISTORY      :
 *  HISTORY      : Revision 1.5  2006/01/03 18:27:13  jiangsc
 *  HISTORY      : License Update
 *  HISTORY      :
 *  HISTORY      : Revision 1.4  2005/12/29 23:06:15  jiangsc
 *  HISTORY      : Changed to latest project name.
 *  HISTORY      :
 *  HISTORY      : Revision 1.3  2005/09/16 23:18:53  chene
 *  HISTORY      : Database prototype GUI support, but can not be loaded
 *  HISTORY      :
 */

