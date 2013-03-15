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
import gov.nih.nci.caadapter.common.standard.type.CommonNodeType;
import gov.nih.nci.caadapter.common.standard.type.CommonNodeModeType;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.BasicDataType;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.4 $
 *          date        Jul 2, 2007
 *          Time:       7:42:39 PM $
 */
public interface CommonNode
{
    //private void setNodeType(CommonNodeType type);
    public CommonNodeType getNodeType();
    //public void setModeType(CommonNodeModeType type) throws ApplicationException;
    public CommonNodeModeType getModeType();
    public void setXmlPath(String uuid);
    public void setXmlPath();
    public void setName(String name)throws ApplicationException;
    public String getXmlPath();
    public void setParent(CommonNode seg)throws ApplicationException;
    public CommonNode getParent();
    public String getName();
    public String getXPath();
    public void setXPath();
    public void setXPath(String xPath);
    public int getDepth();
    public String getIndentationSpaces();
    public CommonSegment getHeadSegment();

    public int getSiblingSequence();
    public boolean isThisLastSibling();

    public int getSequenceInSameNames();
    public String generateXPath();
    public String generateXPath(String levelDelimiter);

    public void setAnotherValue(String val);
    public String getAnotherValue();

    public void setValue(String val) throws ApplicationException;
    public String getValue();
    public String getXMLFormatValue(String val);
    public void deleteValue();
    public boolean doesHaveValue();
    public boolean doesThisNodeHaveValues();
    public void setDataType(String type) throws ApplicationException;
    public String getDataType();
    public void setBasicDataType(BasicDataType type);
    public BasicDataType getBasicDataType();

    public CardinalityType getCardinalityType();
    public void setCardinalityType(CardinalityType type) throws ApplicationException;
    public int getMaxCardinality();
    public int getMinCardinality();
    public boolean isChoiceNode();
    public boolean isChoiceMember();

    public CommonNode cloneNode(CommonNode target, CommonNode source, String newUUID, String newXPath, CommonNode newParent) throws ApplicationException;
    public CommonNode createNewInstance();

    public CommonAttribute getAttributes();
    public void setAttributes(CommonAttribute attributes) throws ApplicationException;
    public void addAttributeItem(CommonAttributeItem att) throws ApplicationException;
    public void setDataTypeOfAttributeItem(String att, BasicDataType type) throws ApplicationException;
    public void setValueOfAttributeItem(String att, String val) throws ApplicationException;
    public void deleteValueOfAttributeItem(String att) throws ApplicationException;
    public void removeAttributeItem(String att) throws ApplicationException;
}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2008/06/06 18:54:28  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/17 16:09:43  wangeug
 * HISTORY      : change UIUID to xmlPath
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:39:24  umkis
 * HISTORY      : Basic resource programs for csv cardinality and test instance generating.
 * HISTORY      :
 */

