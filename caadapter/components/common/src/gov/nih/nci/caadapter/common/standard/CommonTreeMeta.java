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
import gov.nih.nci.caadapter.common.standard.type.CommonNodeModeType;
import gov.nih.nci.caadapter.common.standard.type.NodeIdentifierType;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Jul 2, 2007
 *          Time:       7:45:04 PM $
 */
public interface CommonTreeMeta
{
    public CommonNodeModeType getModeType();
    public void setHeadSegment(CommonSegment a) throws ApplicationException;
    public NodeIdentifierType getNodeIdentifierType();
    public void setNodeIdentifierType(NodeIdentifierType type);
    public CommonSegment getHeadSegment();
    public CommonNode nextTraverse(CommonNode a) throws ApplicationException;
    //public int getSequence(CommonSegment a);
    //public boolean isLastSequence(CommonSegment a);
    public CommonNode findNodeWithName(CommonNode node, String name);
    public CommonNode findNodeWithUUID(String uuid);

    public CommonNode findNodeWithXPath(String xpath);
    public void setXPaths(CommonSegment a);
    //public void setUUIDs(CommonSegment a);
    public boolean doesThisBranchHaveValues(CommonNode a);
    public boolean isAncestor(CommonSegment ancestor, CommonNode descendant);
    public CommonSegment findCommonAncestor(CommonNode node1, CommonNode node2);
//    public int getDepth(CommonNode node);
    public CommonSegment cloneBranch(CommonSegment segment);
    //public gov.nih.nci.hl7.validation.ValidatorResults validateTree();
    public CommonSegment findNearestSegmentWithName(CommonNode node, String sName);
    public CommonSegment findNextSegmentWithName(CommonNode node, String sName);
    public ValidatorResults validateTree();

    public void displayTreeWithText();

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

