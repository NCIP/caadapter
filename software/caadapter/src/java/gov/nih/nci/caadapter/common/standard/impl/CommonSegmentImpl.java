/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common.standard.impl;

import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;
import gov.nih.nci.caadapter.castor.csv.meta.impl.C_segment;
import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.standard.CommonSegment;
import gov.nih.nci.caadapter.common.standard.CommonNode;
import gov.nih.nci.caadapter.common.standard.CommonField;
import gov.nih.nci.caadapter.common.standard.type.CommonNodeType;
import gov.nih.nci.caadapter.common.standard.type.CommonNodeModeType;
import gov.nih.nci.caadapter.common.util.Config;

import java.util.List;
import java.util.ArrayList;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.5 $
 *          date        Jul 2, 2007
 *          Time:       8:08:54 PM $
 */
public class CommonSegmentImpl extends CommonNodeImpl implements CommonSegment
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: CommonSegmentImpl.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/impl/CommonSegmentImpl.java,v 1.5 2008-06-09 19:53:49 phadkes Exp $";

    private List<CommonNode> children = new ArrayList<CommonNode>();

    private boolean valueInputEnable = false;
    private boolean repetitiveOnly = false;
    private boolean optionalOnly = false;

    public CommonSegmentImpl()
    {
        super(CommonNodeType.SEGMENT, (new C_segment()).getCardinality());

    }
    public CommonSegmentImpl(CommonNodeModeType mode)
    {
        super(CommonNodeType.SEGMENT, mode);
        try
        {
            this.setCardinalityType((new C_segment()).getCardinality());
        }
        catch(ApplicationException ae)
        {
            System.err.println("Common Segment Construction Error : " + ae.getMessage());
        }

    }
    public CommonSegmentImpl(CommonSegment seg) throws ApplicationException
    {
        super(CommonNodeType.SEGMENT, (new C_segment()).getCardinality());

        setParent(seg);
    }
    public CommonSegmentImpl(String name) throws ApplicationException
    {
        super(CommonNodeType.SEGMENT, (new C_segment()).getCardinality());

        setName(name);
    }
    public CommonSegmentImpl(CommonSegment seg, String name) throws ApplicationException
    {
        super(CommonNodeType.SEGMENT, (new C_segment()).getCardinality());

        setParent(seg);
        setName(name);
    }

//    public void addChildNode(CommonNode a) throws ApplicationException
//    {
//        if (a instanceof CommonSegment) addChildSegment((CommonSegment)a);
//        else if (a instanceof CommonField) addField((CommonField)a);
//        else throw new ApplicationException("Invalid Instance for child of 'CommonSegment'");
//    }
    public void addChildNode(CommonNode a) throws ApplicationException
    {
        validateCommonNodeForAddition(a);
        if (a instanceof CommonSegment) addChildSegment((CommonSegment)a);
        else if (a instanceof CommonField) addField((CommonField)a);
        else throw new ApplicationException("Invalid Node instance");
    }
    public boolean addChildNodeEnforced(CommonNode a)
    {
        try
        {
            if (a instanceof CommonSegment) addChildSegment((CommonSegment)a);
            else if (a instanceof CommonField) addField((CommonField)a);
            else return false;
        }
        catch(ApplicationException ae)
        {
            return false;
        }
        return true;
    }
    private void addChildSegment(CommonSegment a) throws ApplicationException
    {
//        if (getModeType().getType() != a.getModeType().getType()) throw new ApplicationException("Inconsistant mode type between parent and child segment");
//        if ((a.getName() == null)||(a.getName().equals(""))) throw new ApplicationException("The name of this Segment is null.");
//        for(int i=0;i<children.size();i++)
//        {
//            CommonNode node = children.get(i);
//            if (!(node instanceof CommonSegment)) continue;
//            CommonSegment segment = (CommonSegment) node;
//            if (segment.getName().equals(a.getName())) throw new ApplicationException("This name is already used by another sibling segment node. : " + a.getName());
//        }
        a.setParent(this);
        children.add(a);
    }
    private void addField(CommonField a) throws ApplicationException
    {
//        if (getModeType().getType() != a.getModeType().getType()) throw new ApplicationException("Inconsistant mode type between parent and child field");
//
//        if ((a.getName() == null)||(a.getName().equals(""))) throw new ApplicationException("The name of this Field is null.");
//        for(int i=0;i<children.size();i++)
//        {
//            CommonNode node = children.get(i);
//            if (!(node instanceof CommonField)) continue;
//            CommonField field = (CommonField) node;
//            if (field.getName().equals(a.getName())) throw new ApplicationException("This name is already used by another sibling field node. : " + a.getName());
//        }
        a.setParent(this);
        children.add(a);
    }
    protected void replaceChildNodes(List<CommonNode> listSource) throws ApplicationException
    {
        List<CommonNode> list = new ArrayList<CommonNode>();
        for (int i=0;i<listSource.size();i++)
        {
            CommonNode node = listSource.get(i);
            validateCommonNodeForAddition(node, list);
            node.setParent(this);
            list.add(node);
        }
        children = list;
    }
    private void validateCommonNodeForAddition(CommonNode node) throws ApplicationException
    {
        //System.out.println("CCC : " + children.size());
        validateCommonNodeForAddition(node, children);
    }
    private void validateCommonNodeForAddition(CommonNode node, List<CommonNode> children) throws ApplicationException
    {
        if (node == null) throw new ApplicationException("This node is Null.");
        if (children == null) throw new ApplicationException("This node list is Null.");
        String name1 = node.getName();
        if ((name1 == null)||(name1.trim().equals(""))) throw new ApplicationException("The name of this node is null. (for '"+this.getName()+"' segment)");
        if (getNodeType().getType() == CommonNodeType.COMMON.getType()) throw new ApplicationException("Common node is not allow to addition or insertion.");
        if (getModeType().getType() == CommonNodeModeType.COMMON.getType()) throw new ApplicationException("Common mode is not allow to addition or insertion.");

        if (getModeType().getType() != node.getModeType().getType()) throw new ApplicationException("Inconsistant mode type between parent and child segment");

        if ((node.getNodeType().getType() != CommonNodeType.SEGMENT.getType())&&(node.getNodeType().getType() != CommonNodeType.FIELD.getType()))
        {
            throw new ApplicationException("This node is neither a segment nor a field.");
        }

        if (getModeType().getType() == CommonNodeModeType.META.getType())
        {
            for (int i=0;i<children.size();i++)
            {
                CommonNode aNode = children.get(i);
                if ((node.getNodeType().getType() == aNode.getNodeType().getType())&&(node.getName().equals(aNode.getName())))
                    throw new ApplicationException("This node name is already used at another " + node.getNodeType() + " node : " + node.getName());
            }
        }
        else if (getModeType().getType() == CommonNodeModeType.DATA.getType())
        {
            boolean cTag = false;
            int checkSeq = 0;
            for (int i=0;i<children.size();i++)
            {
                CommonNode aNode = children.get(i);
                if ((node.getNodeType().getType() == aNode.getNodeType().getType())&&(node.getName().equals(aNode.getName())))
                {
                    cTag =  true;
                    if (checkSeq == 0) checkSeq = i;
                    else
                    {
                        if (i == (checkSeq + 1)) checkSeq = i;
                        else throw new ApplicationException("This (data) node name is already used at another " + node.getNodeType() + " node : " + node.getName());
                    }
                }
            }
            if (cTag)
            {
                CommonNode aNode = children.get(children.size()-1);
                if ((node.getNodeType().getType() == aNode.getNodeType().getType())&&(node.getName().equals(aNode.getName())))
                {
                }
                else throw new ApplicationException("This (data) node name is already used at another " + node.getNodeType() + " node (2) : " + node.getName());
            }
        }
        else throw new ApplicationException("Invalid Mode Type  : " + getModeType().getType());
    }
    public void insertChildNode(CommonNode insertPoint, CommonNode a) throws ApplicationException
    {
        if (getModeType().getType() != CommonNodeModeType.META.getType())
        {
            throw new ApplicationException("Node insertion is allowed in meta mode only.");
        }
        validateCommonNodeForAddition(a);
        //if (a == null) throw new ApplicationException("Null Inserting Node");
        if (insertPoint == null) throw new ApplicationException("Null Inserted point Node");

        //String name1 = a.getName();
        //if ((name1 == null)||(name1.trim().equals(""))) throw new ApplicationException("The name of the inserting node is null");
        String name2 = insertPoint.getName();
        if ((name2 == null)||(name2.trim().equals(""))) throw new ApplicationException("The name of the inserted point node is null");
        boolean cTag = false;
//        for(int i=0;i<children.size();i++)
//        {
//            CommonNode node = children.get(i);
//            if (node.getName().equals(name1)) throw new ApplicationException("This name is already used by another sibling node. : " + a.getName());
//        }
        List<CommonNode> list = new ArrayList<CommonNode>();
        for(int i=0;i<children.size();i++)
        {
            CommonNode aNode = children.get(i);
            if ((insertPoint.getNodeType().getType() == aNode.getNodeType().getType())&&(insertPoint.getName().equals(aNode.getName())))//   .equals(children.get(i)))
            {
                //if (cTag) throw new ApplicationException("Redundant child nodes are found. : " + a.getName());
                if (a instanceof CommonSegment) list.add((CommonSegment)a);
                else if (a instanceof CommonField) list.add((CommonField)a);
                else throw new ApplicationException("Invalid inserting Node instance");

                cTag = true;
            }
            list.add(children.get(i));
        }
        if (cTag) children = list;
        else throw new ApplicationException("This Inserted Point node is not found. : " + insertPoint.getName());
    }
    public List<CommonNode> getChildNodes()
    {
        return children;
    }
    public List<CommonNode> getReorganizedChildNodes()
    {
        List<CommonNode> list = new ArrayList<CommonNode>();
        list.addAll(getFields());
        list.addAll(getChildSegments());
        return list;
    }

    public List<CommonSegment> getChildSegments()
    {
        List<CommonSegment> list = new ArrayList<CommonSegment>();
        for(int i=0;i<children.size();i++)
        {
            CommonNode node = children.get(i);
            if (!(node instanceof CommonSegment)) continue;
            list.add((CommonSegment) node);
        }
        return list;
    }
    public List<CommonField> getFields()
    {
        List<CommonField> list = new ArrayList<CommonField>();
        for(int i=0;i<children.size();i++)
        {
            CommonNode node = children.get(i);
            if (!(node instanceof CommonField)) continue;
            list.add((CommonField) node);
        }
        return list;
    }
    public int getNumberOfChildNodes()
    {
        return children.size();
    }
    public int getNumberOfChildSegments()
    {
        return getChildSegments().size();
    }
    public int getNumberOfFields()
    {
        return getFields().size();
    }
    public CommonNode getChildNodeWithSequence(int n)
    {
        if (n >= getNumberOfChildNodes()) return null;
        return getChildNodes().get(n);
    }
    public CommonSegment getChildSegmentWithSequence(int n)
    {
        if (n >= getNumberOfChildSegments()) return null;
        return getChildSegments().get(n);
    }
    public CommonField getFieldWithSequence(int n)
    {
        if (n >= getNumberOfFields()) return null;
        return getFields().get(n);
    }
    public CommonNode findChildNodeWithName(String s)
    {
        if ((s == null)||(s.trim().equals(""))) return null;
        s = s.trim();
        for(int i=0;i<children.size();i++)
        {
            CommonNode node = children.get(i);
            if (s.equals(node.getName())) return node;
        }
        return null;
    }
    public CommonSegment findChildSegmentWithName(String s)
    {
        if ((s == null)||(s.trim().equals(""))) return null;
        s = s.trim();
        List<CommonSegment> list = getChildSegments();
        for(int i=0;i<list.size();i++)
        {
            CommonSegment segment = list.get(i);
            if (s.equals(segment.getName())) return segment;
        }
        return null;
    }
    public CommonField findFieldWithName(String s)
    {
        if ((s == null)||(s.trim().equals(""))) return null;
        s = s.trim();
        List<CommonField> list = getFields();
        for(int i=0;i<list.size();i++)
        {
            CommonField field = list.get(i);
            if (s.equals(field.getName())) return field;
        }
        return null;
    }
    public int getSequenceOfChildNode(CommonNode c)
    {
        for(int i=0;i<children.size();i++)
        {
            CommonNode node = children.get(i);
            if (c instanceof CommonSegment)
            {
                if (!(node instanceof CommonSegment)) continue;
                CommonSegment segmentC = (CommonSegment) c;
                CommonSegment segment = (CommonSegment) node;
                if (segmentC == segment) return i;
            }
            else if (c instanceof CommonField)
            {
                if (!(node instanceof CommonField)) continue;
                CommonField fieldC = (CommonField) c;
                CommonField field = (CommonField) node;
                if (fieldC == field) return i;
            }
            else return -1;
        }
        return -1;
    }
    public int getSequenceOfChildSegment(CommonSegment c)
    {
        List<CommonSegment> list = getChildSegments();
        for(int i=0;i<list.size();i++)
        {
            CommonSegment node = list.get(i);
            if (c == node) return i;
        }
        return -1;
    }
    public int getSequenceOfField(CommonField c)
    {
        List<CommonField> list = getFields();
        for(int i=0;i<list.size();i++)
        {
            CommonField node = list.get(i);
            if (c == node) return i;
        }
        return -1;
    }

    public boolean removeChildNode(CommonNode c)
    {
        int seq = getSequenceOfChildNode(c);
        if (seq < 0) return false;
        List<CommonNode> list = new ArrayList<CommonNode>();
        for(int i=0;i<children.size();i++)
        {
            if (i != seq) list.add(children.get(i));
        }
        children = list;
        return true;
    }
    public boolean removeChildSegment(CommonSegment c)
    {
        return removeChildNode(c);
    }
    public boolean removeField(CommonField c)
    {
        return removeChildNode(c);
    }
    public boolean mutateFieldToSegment(CommonField f, CommonSegment c)
    {
        if (c == null) return false;
        if (f == null) return false;
        //String name = c.getName();
        //if ((name == null)||(name.trim().equals(""))) return false;
        boolean cTag = false;
        List<CommonNode> list = new ArrayList<CommonNode>();
        for(int i=0;i<children.size();i++)
        {
            if (children.get(i) instanceof CommonField)
            {
                CommonField field = (CommonField) children.get(i);
                if (f == field)//(children.get(i).getName().equals(name))
                {
                    if (cTag) return false;

                    try
                    {
                        c.cloneNode(c, field, field.getXmlPath(), field.getXPath(), field.getParent());
                    }
                    catch(ApplicationException ae)
                    {
                        return false;
                    }
                    list.add(c);
                    cTag = true;
                }
                else list.add(children.get(i));
            }
            else list.add(children.get(i));

        }
        if (cTag) children = list;
        return cTag;
    }
    public boolean mutateSegmentToField(CommonSegment s, CommonField c)
    {
        if (c == null)
        {
            //System.out.println("CCC : A-1 : field is null.");
            return false;
        }
        if (s == null)
        {
            //System.out.println("CCC : A-2 : segment is null");
            return false;
        }
        //String name = s.getName();
        //if ((name == null)||(name.trim().equals(""))) return false;
        boolean cTag = false;
        List<CommonNode> list = new ArrayList<CommonNode>();
        for(int i=0;i<children.size();i++)
        {
            if (children.get(i) instanceof CommonSegment)
            {
                CommonSegment segment = (CommonSegment) children.get(i);
                if (s == segment)//(children.get(i).getName().equals(name))
                {
                    if (cTag)
                    {
                        //System.out.println("CCC : A");
                        return false;
                    }
                    //CommonSegment segment = (CommonSegment) children.get(i);
                    try
                    {
                        c.cloneNode(c, segment, segment.getXmlPath(), segment.getXPath(), segment.getParent());
                    }
                    catch(ApplicationException ae)
                    {
                        //System.out.println("CCC : B : " + ae.getMessage());
                        return false;
                    }
                    list.add(c);
                    cTag = true;
                }
                else list.add(children.get(i));
            }
            else list.add(children.get(i));

        }
        if (cTag) children = list;
        //else System.out.println("CCC : C : Not Found");
        return cTag;
    }

    public boolean switchChildNode(CommonNode c1, CommonNode c2)
    {
        int seq1 = getSequenceOfChildNode(c1);
        if (seq1 < 0) return false;
        int seq2 = getSequenceOfChildNode(c2);
        if (seq2 < 0) return false;
        if (seq1 == seq2) return false;
        CommonNode node1 = children.get(seq1);
        CommonNode node2 = children.get(seq2);
        List<CommonNode> list = new ArrayList<CommonNode>();
        for(int i=0;i<children.size();i++)
        {
            if (i == seq1) list.add(node2);
            else if (i == seq2) list.add(node1);
            else list.add(children.get(i));
        }
        children = list;
        return true;
    }
    public boolean switchChildSegment(CommonSegment c1, CommonSegment c2)
    {
        return switchChildNode(c1, c2);
    }
    public boolean switchField(CommonField c1, CommonField c2)
    {
        return switchChildNode(c1, c2);
    }
    public void setValueInputEnabled(boolean s)
    {
        valueInputEnable = s;
    }
    public boolean isValueInputEnabled()
    {
        return valueInputEnable;
    }
    public void setValue(String val) throws ApplicationException
    {
        if (!valueInputEnable) throw new ApplicationException("This Segment is not allowed to have own value.");
        super.setValue(val);
    }
//    public CommonSegment cloneNode(CommonSegment target, CommonNode source, String newUUID, String newXPath, CommonNode newParent) throws ApplicationException
//    {
//        if ((newParent != null)&&(newParent instanceof CommonSegment)) return cloneNode(target, source, newUUID, newXPath, (CommonSegment) newParent);
//        else throw new ApplicationException("New parent node is not a segment node.");
//    }
    public CommonSegment cloneNode(CommonSegment target, CommonNode source, String newUUID, String newXPath, CommonSegment newParent) throws ApplicationException
    {
        if (source == null) throw new ApplicationException("Null Source node for clonning");
        if (target == null) throw new ApplicationException("Null target node for clonning");
        if (!((source instanceof CommonSegment)||(source instanceof CommonField))) throw new ApplicationException("Target is neither CommonSegment nor CommonField instance.");
        //if (!(source instanceof CommonSegment)) throw new ApplicationException("Source is not CommonSegment instance.");
        if (target != this) throw new ApplicationException("Invalid target CommonSegment Object");

        super.cloneNode(super.getMyself(), source, newUUID, newXPath, newParent);

        this.children = new ArrayList<CommonNode>();

        if (source instanceof CommonSegment)
        {
            CommonSegment sourceSegment = (CommonSegment)source;
            this.setValueInputEnabled(sourceSegment.isValueInputEnabled());

            this.setOptionalOnly(sourceSegment.isOptionalOnly());
            this.setRepetitiveOnly(sourceSegment.isRepetitiveOnly());
        }

        return this;
    }
    public CommonSegment createNewInstance()
    {
        return new CommonSegmentImpl();
    }
    public CommonField createNewFieldInstance()
    {
        return new CommonFieldImpl();
    }
    public final void setParent(CommonNode seg) throws ApplicationException
    {
        //System.out.println("CVVV 4-1 : " + this.getName());
        if (seg instanceof CommonSegment) super.setParent((CommonSegment)seg);
        else throw new ApplicationException("This node is not a segment for a parent.");
        //System.out.println("CVVV 4-2 : " + this.getName());
    }

    public CommonSegment getParent()
    {
        return (CommonSegment) super.getParent();
    }

    public void setRepetitiveOnly(boolean tag) throws ApplicationException
    {
        if (tag)
        {
            if (isOptionalOnly()) throw new ApplicationException("'Optional Only' option was already set.");
            if (isRepetitiveOnly()) throw new ApplicationException("Redundant sepcial mode set! 'Repetitive Only' option was already set.");
            if (isChoiceNode()) throw new ApplicationException("Redundant sepcial mode set! 'Choice' option was already set.");
            super.setCardinalityType(CardinalityType.valueOf(Config.CARDINALITY_ONE_TO_MANY));
        }

        repetitiveOnly = tag;
    }
    public void setOptionalOnly(boolean tag) throws ApplicationException
    {
        if (tag)
        {
            if (isRepetitiveOnly()) throw new ApplicationException("'Repetitive Only' option was already set.");
            if (isOptionalOnly()) throw new ApplicationException("Redundant sepcial mode set! 'Optional Only' option was already set.");
            if (isChoiceNode()) throw new ApplicationException("Redundant sepcial mode set! 'Choice' option was already set.");
            super.setCardinalityType(CardinalityType.valueOf(Config.CARDINALITY_ZERO_TO_ONE));
        }
        optionalOnly = tag;
    }
    public boolean isRepetitiveOnly()
    {
        return repetitiveOnly;
    }
    public boolean isOptionalOnly()
    {
        return optionalOnly;
    }
    public void setCardinalityType(CardinalityType type) throws ApplicationException
    {
        if ((isRepetitiveOnly())||(isOptionalOnly())) throw new ApplicationException("'Repetitive Only' or 'Optional Only' option was already set. Try again after unset it.");
        super.setCardinalityType(type);
    }
}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.4  2008/06/06 18:54:28  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/08/02 14:24:46  umkis
 * HISTORY      : Update test instance generator engine
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/17 16:11:23  wangeug
 * HISTORY      : change UIUID to xmlPath
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:39:24  umkis
 * HISTORY      : Basic resource programs for csv cardinality and test instance generating.
 * HISTORY      :
 */
