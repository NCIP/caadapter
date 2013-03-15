/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/*





 */

package gov.nih.nci.caadapter.common.standard;

import gov.nih.nci.caadapter.common.ApplicationException;

import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Jul 2, 2007
 *          Time:       7:48:44 PM $
 */
public interface MetaField extends CommonField
{
    public void addLinkedDataSegment(DataField a) throws ApplicationException;
    public List<DataField> getLinkedDataFields();
    public int getNumberOfLinkedDataFields();
    public DataField getLinkedDataFieldInSequence(int n);
    public DataField creatDataField(DataSegment par, DataField target) throws ApplicationException;
    public DataField creatDataField(DataSegment par, DataField target, String val) throws ApplicationException;
    public String generateMetaFileContent();
    public MetaField createNewInstance();
    public DataField createNewDataInstance();
    public void clearLinkedDataFields();

}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2008/06/06 18:54:28  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:39:24  umkis
 * HISTORY      : Basic resource programs for csv cardinality and test instance generating.
 * HISTORY      :
 */

