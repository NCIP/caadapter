/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
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
 *          Time:       7:44:05 PM $
 */
public interface CommonSegment extends CommonNode
{
    //public void setParent(CommonSegment a);
    //public CommonSegment getParent();
    //public void addChildNode(CommonNode a) throws ApplicationException;
    public void addChildNode(CommonNode a) throws ApplicationException;
    public boolean addChildNodeEnforced(CommonNode a);
    //public void addField(CommonField a) throws ApplicationException;
    public void insertChildNode(CommonNode insertPoint, CommonNode a) throws ApplicationException;
    //public void insertField(CommonField insertPoint, CommonField a) throws ApplicationException;
    public List<CommonSegment> getChildSegments();
    public List<CommonNode> getChildNodes();
    public List<CommonNode> getReorganizedChildNodes();
    public List<CommonField> getFields();
    public int getNumberOfChildNodes();
    public int getNumberOfChildSegments();
    public int getNumberOfFields();
    public CommonNode getChildNodeWithSequence(int n);
    public CommonSegment getChildSegmentWithSequence(int n);
    public CommonField getFieldWithSequence(int n);
    public CommonNode findChildNodeWithName(String s);
    public CommonSegment findChildSegmentWithName(String s);
    public CommonField findFieldWithName(String s);
    public int getSequenceOfChildNode(CommonNode c);
    public int getSequenceOfChildSegment(CommonSegment c);
    public int getSequenceOfField(CommonField c);
    public boolean removeChildNode(CommonNode c);
    public boolean removeChildSegment(CommonSegment c);
    public boolean removeField(CommonField c);
    public boolean mutateFieldToSegment(CommonField f, CommonSegment c);
    public boolean mutateSegmentToField(CommonSegment s, CommonField c);
    public boolean switchChildNode(CommonNode c1, CommonNode c2);
    public boolean switchChildSegment(CommonSegment c1, CommonSegment c2);
    public boolean switchField(CommonField c1, CommonField c2);
    public void setValueInputEnabled(boolean s);
    public boolean isValueInputEnabled();
    //public CommonNode cloneNode(CommonNode target, CommonNode source, String newUUID, String newXPath, CommonSegment newParent) throws ApplicationException;
    public CommonSegment createNewInstance();
    public CommonField createNewFieldInstance();
    public CommonSegment cloneNode(CommonSegment target, CommonNode source, String newUUID, String newXPath, CommonSegment newParent) throws ApplicationException;

    public void setRepetitiveOnly(boolean tag) throws ApplicationException;
    public void setOptionalOnly(boolean tag) throws ApplicationException;
    public boolean isRepetitiveOnly();
    public boolean isOptionalOnly();

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

