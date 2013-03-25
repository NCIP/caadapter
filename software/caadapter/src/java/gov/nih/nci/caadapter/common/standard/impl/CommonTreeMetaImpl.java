/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common.standard.impl;

import gov.nih.nci.caadapter.common.standard.*;
import gov.nih.nci.caadapter.common.standard.type.CommonNodeModeType;
import gov.nih.nci.caadapter.common.standard.type.NodeIdentifierType;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.BasicDataType;

import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.4 $
 *          date        Jul 2, 2007
 *          Time:       8:11:35 PM $
 */
public class CommonTreeMetaImpl implements CommonTreeMeta
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: CommonTreeMetaImpl.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/impl/CommonTreeMetaImpl.java,v 1.4 2008-06-09 19:53:49 phadkes Exp $";

    private CommonSegment head;
    private NodeIdentifierType indentifierType = NodeIdentifierType.UUID;
    private CommonNodeModeType modeType = CommonNodeModeType.COMMON;
    //int accessCount = 0;
    public CommonTreeMetaImpl(CommonNodeModeType modeType)
    {
        try
        {
            setModeType(modeType);
        }
        catch(ApplicationException ae)
        {
            //System.out.println("CCCC : " + ae.getMessage() + " : " + modeType);
        }
    }

    public CommonTreeMetaImpl(CommonSegment seg) throws ApplicationException
    {
        if (seg == null) throw new ApplicationException("Head Segment is null.");
        setModeType(seg.getModeType());
        setHeadSegment(seg);
    }

    private void setModeType(CommonNodeModeType modeType) throws ApplicationException
    {
        if (modeType.getType() == CommonNodeModeType.COMMON.getType()) throw new ApplicationException("Common mode is not allowed as tree meta mode.");

        this.modeType = modeType;
    }
    public CommonNodeModeType getModeType()
    {
        return modeType;
    }

    public void setHeadSegment(CommonSegment a) throws ApplicationException
    {
        if (getModeType().getType() == CommonNodeModeType.COMMON.getType()) throw new ApplicationException("This tree is set Common mode.");

        if (a.getModeType().getType() != getModeType().getType()) throw new ApplicationException("This TreeMeta was set '" + getModeType().toString() + "', but this head node is '" + a.getModeType().toString() + "'.");
        head = a;
    }
    public NodeIdentifierType getNodeIdentifierType()
    {
        return indentifierType;
    }
    public void setNodeIdentifierType(NodeIdentifierType type)
    {
        indentifierType = type;
    }
    public CommonSegment getHeadSegment()
    {
        return head;
    }
    public CommonNode nextTraverse(CommonNode a) throws ApplicationException
    {
        //accessCount++;
        //if (accessCount > 100000) return null;
        if (a instanceof CommonSegment)
        {
            CommonSegment segment = (CommonSegment) a;
            if (segment.getChildNodes().size() > 0) return segment.getChildNodes().get(0);
            if (segment == getHeadSegment())
            {
                throw new ApplicationException("This tree is Head only.");
            }
        }

        int seq = a.getSiblingSequence();
        //System.out.println("CCC NNNN :a.getSiblingSequence() : " + seq + " : " + a.getName());
        if (seq < 0) //throw new ApplicationException("Invalid node sequence is found.");
        {
            throw new ApplicationException("Invalid node sequence is found.");
        }
        if (a.isThisLastSibling())
        {
            if (a.getParent() == null) return null;
            //if (a.getParent() == getHeadSegment()) return null;
            CommonSegment parentNode = (CommonSegment) a.getParent();
            while(true)
            {
                if (parentNode.isThisLastSibling())
                {
                    if (parentNode.getParent() == null) return null;
                    if (a.getParent() == getHeadSegment()) return null;
                    else parentNode = (CommonSegment) parentNode.getParent();
                }
                else
                {
                    seq = parentNode.getSiblingSequence();
                    if (seq < 0) //throw new ApplicationException("Invalid node sequence is found(2).");
                    {
                        throw new ApplicationException("Invalid node sequence is found(2).");
                    }
                    return ((CommonSegment)parentNode.getParent()).getChildNodes().get(seq+1);
                }

            }
        }
        else
        {
            CommonSegment parentNode = (CommonSegment) a.getParent();
            return parentNode.getChildNodes().get(seq+1);
        }
    }
    //public int getSequence(CommonSegment a);
    //public boolean isLastSequence(CommonSegment a);
    public CommonNode findNodeWithName(CommonNode node, String name)
    {
        CommonNode temp = node;

        while(temp!=null)
        {
            try
            {
                temp = nextTraverse(temp);
                 if (temp == getHeadSegment()) break;
            }
            catch(ApplicationException ae)
            {
                return null;
            }
            if (temp.getName().equals(name)) return temp;
        }
        return null;
    }
    public CommonNode findNodeWithUUID(String uuid)
    {
        CommonNode temp = head;

        while(temp!=null)
        {
            if (temp.getXmlPath().equals(uuid)) return temp;
            List<CommonAttributeItem> list = temp.getAttributes().getAttributeItems();
            for(int i=0;i<list.size();i++)
            {
                CommonAttributeItem item = list.get(i);
                if (item.getXmlPath().equals(uuid)) return item;
            }
            try
            {
                temp = nextTraverse(temp);
                if (temp == getHeadSegment()) break;
            }
            catch(ApplicationException ae)
            {
                return null;
            }
        }
        return null;
    }

    public CommonNode findNodeWithXPath(String xpath)
    {
        CommonNode temp = head;

        while(temp!=null)
        {
            if (temp.getXPath().equals(xpath)) return temp;
            List<CommonAttributeItem> list = temp.getAttributes().getAttributeItems();
            for(int i=0;i<list.size();i++)
            {
                CommonAttributeItem item = list.get(i);
                if (item.getXPath().equals(xpath)) return item;
            }
            try
            {
                temp = nextTraverse(temp);
                if (temp == null) break;
                if (temp == getHeadSegment()) break;
            }
            catch(ApplicationException ae)
            {
                return null;
            }
        }
        return null;
    }
    public void setXPaths(CommonSegment a)
    {
        //TODO
    }
    //public void setUUIDs(CommonSegment a);
    public boolean doesThisBranchHaveValues(CommonNode a)
    {
        if (a.doesHaveValue()) return true;
        if (a.getAnotherValue() != null) return true;
        if (a instanceof CommonAttributeItem) return false;
        if (a.getAttributes().howManyValues() > 0) return true;
        if (a instanceof CommonField) return false;

        CommonSegment segment = (CommonSegment) a;
        if (segment.doesHaveValue()) return true;
        if (segment.getChildNodes().size() == 0) return false;
        CommonNode temp = segment.getChildNodes().get(0);
        while(isAncestor(segment, temp))
        {
            if (temp.doesHaveValue()) return true;
            if (temp.getAnotherValue() != null) return true;
            if (temp.getAttributes().howManyValues() > 0) return true;
            try
            {
                temp = nextTraverse(temp);
                if (temp == null) break;
                if (temp == getHeadSegment()) break;
            }
            catch(ApplicationException ae)
            {
                return false;
            }
        }
        return false;
    }
    public boolean isAncestor(CommonSegment ancestor, CommonNode descendant)
    {
        if (ancestor == null) return false;
        if (descendant == null) return false;
        if (ancestor == descendant) return true;
        if (descendant.getParent() == null) return false;
        CommonSegment segment = (CommonSegment) descendant.getParent();

        while(true)
        {
            if (segment == ancestor) return true;
            if (segment.getParent() == null) return false;
            segment = (CommonSegment) segment.getParent();
        }
    }
    public CommonSegment findCommonAncestor(CommonNode node1, CommonNode node2)
    {
        if (node1 instanceof CommonSegment)
        {
            if (isAncestor((CommonSegment) node1, node2)) return (CommonSegment) node1;
        }
        if (node2 instanceof CommonSegment)
        {
            if (isAncestor((CommonSegment) node2, node1)) return (CommonSegment) node2;
        }
        if ((node2 instanceof CommonSegment)&&(node1 == node2)) return (CommonSegment) node2;
        if ((!(node2 instanceof CommonSegment))&&(node1 == node2)) return null;
        CommonSegment segment1 = (CommonSegment) node1.getParent();
        while(segment1 != null)
        {
            if (isAncestor(segment1, node2)) return segment1;
            segment1 = (CommonSegment) segment1.getParent();
        }

        CommonSegment segment2 = (CommonSegment) node2.getParent();
        while(segment2 != null)
        {
            if (isAncestor(segment2, node1)) return segment2;
            segment2 = (CommonSegment) segment2.getParent();
        }
        return null;
    }
//    public int getDepth(CommonNode node);
    public CommonSegment cloneBranch(CommonSegment segment)
    {
        // ToDo : defined in subclasses
        return null;
    }
    public CommonSegment findNearestSegmentWithName(CommonNode node, String sName)
    {
        CommonSegment segment;
        if (node instanceof CommonSegment)
        {
            segment = (CommonSegment) node;
        }
        else segment = (CommonSegment) node.getParent();

        while(segment!=null)
        {
            CommonSegment tempSeg = segment;
            CommonNode tempNode = tempSeg;
            while(this.isAncestor(segment, tempSeg))
            {
                try
                {
                    tempNode = this.nextTraverse(tempNode);
                }
                catch(ApplicationException ae)
                {
                    return null;
                }
                if (tempNode == null) break;
                if (tempNode == getHeadSegment()) break;

                if (!(tempNode instanceof CommonSegment)) continue;
                tempSeg = (CommonSegment) tempNode;
                if (tempSeg.getName().equals(sName)) return tempSeg;
            }
            segment = (CommonSegment)segment.getParent();
        }
        return null;
    }
    public CommonSegment findNextSegmentWithName(CommonNode node, String sName)
    {
        CommonSegment tempSeg;
        CommonNode tempNode = node;
        while(true)
        {
            try
            {
                tempNode = this.nextTraverse(tempNode);
            }
            catch(ApplicationException ae)
            {
                return null;
            }
            if (tempNode == null) break;
            if (tempNode == getHeadSegment()) break;

            if (!(tempNode instanceof CommonSegment)) continue;
            tempSeg = (CommonSegment) tempNode;
            if (tempSeg.getName().equals(sName)) return tempSeg;
        }
        return null;

    }
    //public gov.nih.nci.hl7.validation.ValidatorResults validateTree();

    public void displayTreeWithText()
    {
        CommonNode temp = getHeadSegment();

        String prompt = "Tree : ";
        if (temp == null)
        {
            System.out.println(prompt + "Null head segment.");
            return;
        }

        prompt = temp.getModeType().toString() + " " + prompt;

        System.out.println("");
        System.out.println(prompt + "START...");
        while(temp!=null)
        {
            displayNodeItemWithText(prompt, temp);
            for (int i=0;i<temp.getAttributes().getAttributeItems().size();i++)
            {
                CommonAttributeItem item = temp.getAttributes().getAttributeItems().get(i);
                displayNodeItemWithText(prompt, item);
            }

            try
            {
                temp = nextTraverse(temp);
            }
            catch(ApplicationException ae)
            {
                System.out.println(prompt + "Display unnormally ending due to traverse error : " + ae.getMessage());
                return;
            }
        }
        System.out.println(prompt + "END...");
    }
    private void displayNodeItemWithText(String prompt, CommonNode temp)
    {
        String str1 = "";
            if (temp instanceof CommonSegment) str1 = "S:";
            else if (temp instanceof CommonField) str1 = "F:";
            else if (temp instanceof CommonAttributeItem) str1 = "  A:";
            else str1 = "U:";

            if (temp.isChoiceNode()) str1 = "C:";

            String str2 = "";
            if (temp.doesHaveValue()) str2 = " = " + temp.getValue();
            else if (temp.getAnotherValue() != null) str2 = " = " + temp.getAnotherValue() + " (Data Type mismatched)";
            else str2 = "";

            String str3 = "";
            if (temp.getBasicDataType().getType() == BasicDataType.STRING.getType()) str3 = "";
            else if (temp.getBasicDataType().getType() == BasicDataType.NUMBER.getType()) str3 = "(N)";
            else if (temp.getBasicDataType().getType() == BasicDataType.INTEGER.getType()) str3 = "(I)";
            else if (temp.getBasicDataType().getType() == BasicDataType.DOUBLE.getType()) str3 = "(D)";
            else if (temp.getBasicDataType().getType() == BasicDataType.DATETIME.getType()) str3 = "(T)";

            String str4 = str4 = "[" + temp.getCardinalityType().toString() + "]";

            if (temp instanceof CommonSegment)
            {
                if (temp.getCardinalityType().toString().equals(Config.CARDINALITY_ZERO_TO_MANY)) str4 = "";
            }
            else if (temp instanceof CommonField)
            {
                if (temp.getCardinalityType().toString().equals(Config.CARDINALITY_ZERO_TO_ONE)) str4 = "";
            }
            else if (temp instanceof CommonAttributeItem)
            {
                CommonAttributeItem item = (CommonAttributeItem) temp;
                if (item.isThisRequired()) str4 = "[R]";
                else str4 = "";

                if (str2.equals(""))
                {
                    if ((item.getItemValue() != null)&&(!item.getItemValue().trim().equals("")))
                    {
                        str2 = " = " + item.getItemValue();
                    }
                }
            }

            if (temp.isChoiceMember()) str4 = "";

            System.out.println(prompt + temp.getIndentationSpaces() + str1 + temp.getName() + str3 + str4 + str2);
    }
    public ValidatorResults validateTree()
    {
        //TODO
        return null;
    }

}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2008/06/06 18:54:28  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/17 16:11:38  wangeug
 * HISTORY      : change UIUID to xmlPath
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:39:24  umkis
 * HISTORY      : Basic resource programs for csv cardinality and test instance generating.
 * HISTORY      :
 */
