/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common.csv.meta;

import gov.nih.nci.caadapter.common.MetaObject;

/**
 * Interface for an in-memory segmented csv meta file.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.4 $
 * @date        $Date: 2008-09-24 20:42:38 $
 */

public interface CSVMeta extends MetaObject{
    public CSVSegmentMeta getRootSegment();
	public void setRootSegment(CSVSegmentMeta rootSegment);
    public boolean isNonStructure();
    public void setNonStructure(boolean nonStructure);
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
