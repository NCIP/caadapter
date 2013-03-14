/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.common.standard;

import gov.nih.nci.caadapter.castor.csv.meta.impl.types.BasicDataType;
import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.standard.type.CommonNodeModeType;

import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Apr 24, 2007
 *          Time:       7:09:06 PM $
 */
public interface CommonAttribute
{
    //public void setModeType(CommonNodeModeType mode) throws ApplicationException;
    public CommonNodeModeType getModeType();
    //public void setParent(CommonSegment segment) throws ApplicationException;
    public CommonNode getParent();
    public void addNewAttributeItem(CommonAttributeItem item) throws ApplicationException;
    public void addAttributeItems(List<CommonAttributeItem> itemsL) throws ApplicationException;
    //public void addNewAttribute(String att) throws ApplicationException;
    //public void addAttributeList(List<String> atts) throws ApplicationException;
    //public void addAttributeArray(String[] atts) throws ApplicationException;
    public void setAttributeItems(List<CommonAttributeItem> items) throws ApplicationException;
    public List<CommonAttributeItem> getAttributeItems();
    public List<String> getAttributeItemNames();
    public CommonAttributeItem getAttributeItem(String att);
    public CommonAttributeItem getAttributeItem(int index);
    public boolean switchAttributeItems(int index1, int index2);
    public boolean replaceAttributeItem(int index1, CommonAttributeItem item);
    public boolean insertAttributeItem(int index1, CommonAttributeItem item);
    public boolean existsAttributeItem(String att);
    public boolean removeAttributeItem(String att);
    public int getIndexOfAttributeItem(String att);
    public int getTheNumberOfAttribute();
    public void deleteAllItemsValues();

    public int getTheNumberOfDataStoredAttribute();
    public boolean isRequiredFull();
    public boolean isAllFull();
    public int howManyValues();
    public String outputAttributesInXMLFormat();
    public boolean setValueOfOneAttributeItem(String att, String val);

    public boolean deleteValueOfOneAttributeItem(String att);
    public boolean setDataTypeOfOneAttributeItem(String att, BasicDataType type);
    public String getValueOfOneAttributeItem(String att) throws ApplicationException;
    public CommonAttributeItem createNewItemInstance();
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2008/06/06 18:54:28  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:39:24  umkis
 * HISTORY      : Basic resource programs for csv cardinality and test instance generating.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/05/22 14:53:14  umkis
 * HISTORY      : Common Standard Node and Tree
 * HISTORY      :
 */



/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2008/06/06 18:54:28  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:39:24  umkis
 * HISTORY      : Basic resource programs for csv cardinality and test instance generating.
 * HISTORY      :
 */

