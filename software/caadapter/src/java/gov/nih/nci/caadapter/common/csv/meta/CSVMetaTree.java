/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/*





 */

package gov.nih.nci.caadapter.common.csv.meta;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.csv.data.CSVDataTree;
import gov.nih.nci.caadapter.common.standard.MetaTreeMeta;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Jul 3, 2007
 *          Time:       11:18:09 AM $
 */
public interface CSVMetaTree extends MetaTreeMeta
{
    public void setSCSFileName(String fileName) throws ApplicationException;
    public String getSCSFileName();
    public CSVDataTree constructInitialDataTree() throws ApplicationException;
}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2008/06/06 18:54:10  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:37:49  umkis
 * HISTORY      : csv cardinality
 * HISTORY      :
 */

