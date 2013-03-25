/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/*





 */

package gov.nih.nci.caadapter.common.standard.impl;

import gov.nih.nci.caadapter.common.Cardinality;

import java.util.List;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.UUIDGenerator;
import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.standard.type.CommonNodeType;
import gov.nih.nci.caadapter.common.standard.type.CommonNodeModeType;
import gov.nih.nci.caadapter.common.standard.type.BasicDataTypeValueValidate;
import gov.nih.nci.caadapter.common.standard.CommonAttribute;
import gov.nih.nci.caadapter.common.standard.CommonAttributeItem;
import gov.nih.nci.caadapter.common.standard.CommonNode;
import gov.nih.nci.caadapter.common.standard.CommonSegment;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.BasicDataType;
import gov.nih.nci.caadapter.castor.csv.meta.impl.C_field;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.7 $
 *          date        Jul 2, 2007
 *          Time:       8:04:33 PM $
 */
public class CommonNodeImpl implements CommonNode
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: CommonNodeImpl.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/impl/CommonNodeImpl.java,v 1.7 2008-06-09 19:53:49 phadkes Exp $";

    private CommonNodeType nodeType = CommonNodeType.COMMON;
    private CommonNodeModeType modeType = CommonNodeModeType.COMMON;
    private String uuid = null;
    private String name = null;
    private String xpath = null;
    private CommonNode parent = null;

    private BasicDataType dataType = (new C_field()).getDatatype();
    private String value = null;
    private String anotherValue = null;
    private boolean gotValue = false;

    private CardinalityType cardinality = CardinalityType.valueOf(Config.CARDINALITY_ZERO_TO_ONE);

    private CommonAttribute attributes;

//    public CommonNodeImpl()
//    {
//
//    }
    protected CommonNodeImpl(CommonNodeType type, CardinalityType cardi)
    {
        try
        {
            setNodeType(type);
            if (type.getType() == CommonNodeType.ATTRIBUTE.getType())
            {
                CommonAttributeItem item = (CommonAttributeItem) this;
                item.setOptional();
            }
            else setCardinalityType(cardi);
            //attributes = new CommonAttributeImpl(this);
        }
        catch(ApplicationException ae)
        {
            System.out.println("CommonNode construction error : " + ae.getMessage());
        }
    }

    protected CommonNodeImpl(CommonNodeType type, String name) throws ApplicationException
    {
        setNodeType(type);
        setName(name);
        attributes = new CommonAttributeImpl(this);

    }
    protected CommonNodeImpl(CommonNodeType type, CommonSegment parent) throws ApplicationException
    {
        setNodeType(type);
        setParent(parent);
        attributes = new CommonAttributeImpl(this);

    }
    protected CommonNodeImpl(CommonNodeType type, CommonSegment parent, String name) throws ApplicationException
    {
        setNodeType(type);
        setParent(parent);
        setName(name);
        attributes = new CommonAttributeImpl(this);
    }
    protected CommonNodeImpl(CommonNodeType type, CommonNodeModeType mode)
    {
        try
        {
            setModeType(mode);
            setNodeType(type);
            attributes = new CommonAttributeImpl(this);
        }
        catch(ApplicationException ae)
        {
            System.out.println("CommonNode construction error (2): " + ae.getMessage());
        }
    }
    protected CommonNodeImpl(CommonNodeType type, CommonNodeModeType mode, String name) throws ApplicationException
    {
        setModeType(mode);
        setNodeType(type);
        setName(name);
        attributes = new CommonAttributeImpl(this);
    }
    protected CommonNodeImpl(CommonNodeType type, CommonNodeModeType mode, CommonSegment parent) throws ApplicationException
    {
        setModeType(mode);
        setNodeType(type);
        setParent(parent);
        attributes = new CommonAttributeImpl(this);
    }
    protected CommonNodeImpl(CommonNodeType type, CommonNodeModeType mode, CommonSegment parent, String name) throws ApplicationException
    {
        setModeType(mode);
        setNodeType(type);
        setParent(parent);
        setName(name);
        attributes = new CommonAttributeImpl(this);
    }

    private void setNodeType(CommonNodeType type) throws ApplicationException
    {
        if (type.getType() == CommonNodeType.COMMON.getType())
            throw new ApplicationException("'COMMON' node type is not allowed in any implemented class.");
        nodeType = type;

    }
    public CommonNodeType getNodeType()
    {
        return nodeType;
    }
    protected final void setModeType(CommonNodeModeType mode) throws ApplicationException
    {
        if (mode.getType() == CommonNodeModeType.COMMON.getType())
            throw new ApplicationException("'COMMON' mode is not allowed in any implemented class.");
        modeType = mode;

    }
    public CommonNodeModeType getModeType()
    {
        return modeType;
    }

    public void setParent(CommonNode seg) throws ApplicationException
    {

        if (seg == null) throw new ApplicationException("This node is null. (CommonNode.setParent())");

        if (seg.getModeType().getType() != modeType.getType())
            throw new ApplicationException("Mode type is not mis-matched.");


        if (seg.getModeType().getType() == CommonNodeModeType.COMMON.getType())
            throw new ApplicationException("Any Common mode node is not allowed to be set as a parent.");

        if (seg.getNodeType().getType() == CommonNodeType.ATTRIBUTE.getType())
            throw new ApplicationException("Any Attribute Node is not allowed to be set as a parent.");

        if (seg.getNodeType().getType() == CommonNodeType.COMMON.getType())
            throw new ApplicationException("Any Common Node is not allowed to be set as a parent.");

        if (getNodeType().getType() == CommonNodeType.COMMON.getType())
            throw new ApplicationException("Any Common Node is not allowed to connect to any parent.");

        if (((getNodeType().getType() == CommonNodeType.SEGMENT.getType())||(this.getNodeType().getType() == CommonNodeType.FIELD.getType()))&&
            (seg.getNodeType().getType() != CommonNodeType.SEGMENT.getType()))
            throw new ApplicationException("A parent of a segment or field node must be a segment node.");

        String tempName = this.getName();
        this.parent = seg;
        name = tempName;

    }
    public CommonNode getParent()
    {
        return parent;
    }

    public void setXmlPath(String uuid)
    {
        if ((uuid == null)||(uuid.trim().equals(""))) setXmlPath();
        else this.uuid = uuid.trim();
    }

    public void setXmlPath()
    {
        this.uuid = UUIDGenerator.getUniqueString();
    }
    public void setName(String name) throws ApplicationException
    {
        if ((name == null)||(name.trim().equals(""))) throw new ApplicationException("Name is null.");
        name = name.trim();
        CommonNode parentL = getParent();
        if (parentL != null)
        {
            //if (parent.getName().equals(name)) throw new ApplicationException("This name is already used by the parent node. : " + name);

            if (this.getNodeType().getType() == CommonNodeType.ATTRIBUTE.getType())
            {
                List<CommonAttributeItem> items = parentL.getAttributes().getAttributeItems();
                for(int i=0;i<items.size();i++)
                {
                    CommonAttributeItem item = items.get(i);
                    if (item == this) continue;
                    if (name.equalsIgnoreCase(item.getName())) throw new ApplicationException("This name is already used by another sibling node. : " + name);
                }
            }
            if (parentL instanceof CommonSegment)
            {
                List<CommonNode> children = ((CommonSegment)parentL).getChildNodes();
                for(int i=0;i<children.size();i++)
                {
                    CommonNode node = children.get(i);
                    if (node == this) continue;
                    if (name.equalsIgnoreCase(node.getName())) throw new ApplicationException("This name is already used by another sibling node. : " + name);
                }
            }
        }
        this.name = name;
    }
    public String getXmlPath()
    {
        return this.uuid;
    }
    public String getName()
    {
        return this.name;
    }
    public String getXPath()
    {
        return this.xpath;
    }
    public void setXPath()
    {
        this.xpath = generateXPath();
    }
    public void setXPath(String xPath)
    {
        this.xpath = xPath;
    }
    public int getDepth()
    {
        int depth = 0;

        CommonNode ancestor = getParent();
        while(ancestor != null)
        {
            depth++;
            ancestor = ancestor.getParent();
        }
        return depth;
    }
    public String getIndentationSpaces()
    {
        String spaces = "";
        for (int i=0;i<getDepth();i++) spaces = spaces + "    ";
        return spaces;
    }
    public CommonSegment getHeadSegment()
    {
        if (this.getParent() == null)
        {
            if (!(this instanceof CommonSegment)) return null;
            else return (CommonSegment) this;
        }

        CommonNode ancestor = getParent();
        while(true)
        {
            if (ancestor.getParent() != null) ancestor = ancestor.getParent();
            else break;
        }
        return (CommonSegment)ancestor;
    }

    public int getSiblingSequence()
    {

        if (getNodeType().getType() == CommonNodeType.ATTRIBUTE.getType())
        {

            List<CommonAttributeItem> list = getParent().getAttributes().getAttributeItems();
            int find = -1;
            for (int i=0;i<list.size();i++)
            {
                CommonAttributeItem item = list.get(i);
                if (getName().equals(item.getName())) find = i;
            }
            return find;
        }
        else if (getNodeType().getType() == CommonNodeType.COMMON.getType()) return -1;
        else
        {
            if (getParent() == null)
            {
                return 0;
            }
            CommonSegment segment = (CommonSegment) getParent();
            List<CommonNode> list = segment.getChildNodes();
            int find = -1;
            for (int i=0;i<list.size();i++)
            {
                CommonNode node = list.get(i);
                //if ((getName().equals(node.getName()))&&(getNodeType().getType() == node.getNodeType().getType())) find = i;
                if (node == this) find = i;
            }
            return find;
        }
    }
    public boolean isThisLastSibling()
    {
        if (getParent() == null) return true;
        int seq = getSiblingSequence();
        if (seq < 0) return false;
        if (getNodeType().getType() == CommonNodeType.ATTRIBUTE.getType())
        {
            if (getParent().getAttributes().getAttributeItems().size() == (seq + 1)) return true;
            else return false;
        }
        else
        {
            CommonSegment segment = (CommonSegment) getParent();
            //System.out.println("CCC VCVCCCC segment.getChildNodes().size() : "+ getName()+ ":" + segment.getChildNodes().size()+ ":" + segment.getName());
            //if (segment == null) System.out.println("CCC XX: " + this.getName());
            if (segment.getChildNodes().size() == (seq + 1)) return true;
            else return false;
        }
    }

    public String toString()
    {
        return getName();
    }

    public int getSequenceInSameNames()
    {
        if (getParent() == null) return 0;
        if (nodeType.getType() == CommonNodeType.ATTRIBUTE.getType()) return 0;
        List<CommonNode> list = ((CommonSegment)getParent()).getChildNodes();

        int count = 0;
        int thisSeq = 0;
        for (int i=0;i<list.size();i++)
        {
            CommonNode node = list.get(i);
            if ((nodeType.getType() == node.getNodeType().getType())&&(getName().equals(node.getName()))) count++;
            if (node == this) thisSeq = count;
        }
        if (thisSeq == 0) return -1;
        if (count == 0) return -1;
        if (count == 1) return 0;
        else return thisSeq;
    }
    public String generateXPath()
    {
        return generateXPath(Config.DEFAULT_XPATH_LEVEL_DELIMITER);
    }
    public String generateXPath(String levelDelimiter)
    {
        String xpath = "";
        String atMark = "";
        String seq = "";

        CommonNode node = this;
        while(node!=null)
        {
            if (node.getNodeType().getType() == CommonNodeType.ATTRIBUTE.getType()) atMark = "@";
            else atMark = "";
            int num = node.getSequenceInSameNames();
            if (num < 0) seq = "[ERROR!]";
            else if (num == 0) seq = "";
            else seq = "[" + num  + "]";
            xpath = levelDelimiter + atMark + node.getName() + seq + xpath;
            node = node.getParent();
        }
        if (xpath.startsWith(levelDelimiter)) xpath = xpath.substring(levelDelimiter.length());
        return xpath;
    }

    public void setAnotherValue(String val)
    {
        anotherValue = val;
    }
    public String getAnotherValue()
    {
        return anotherValue;
    }
    public void setValue(String val) throws ApplicationException
    {
        if (modeType.getType() == CommonNodeModeType.META.getType()) throw new ApplicationException("Meta mode doesn't allow to input value");
        if (modeType.getType() == CommonNodeModeType.COMMON.getType()) throw new ApplicationException("Common mode doesn't allow to input value");

        if (this.isChoiceNode()) throw new ApplicationException("Choice Segment cannot have any own value : " + val + " : " + generateXPath());

        if ((nodeType.getType() == CommonNodeType.ATTRIBUTE.getType())&&(getParent().isChoiceNode()))
            throw new ApplicationException("Choice Segment cannot have any own attribute value : " + val + " : " + generateXPath());

        BasicDataTypeValueValidate valueChecker = new BasicDataTypeValueValidate(dataType);
        if (val == null)
        {
            if ((getBasicDataType().getType() == BasicDataType.DOUBLE.getType())||
                (getBasicDataType().getType() == BasicDataType.INTEGER.getType())||
                (getBasicDataType().getType() == BasicDataType.NUMBER.getType()))
                     throw new ApplicationException("Null value is not allowed for '"+dataType.toString()+"' data type. : " + val + " : " + generateXPath());
            else value = null;
        }
        else if (valueChecker.validate(val)) value = val;
        else
        {
            throw new ApplicationException("This Node value is invalid for '"+dataType.toString()+"' data type. : " + val + " : " + generateXPath());
        }
        gotValue = true;
    }
    public String getValue()
    {
        return value;
    }
    public String getXMLFormatValue(String val)
    {
        String out = "";
        for(int i=0;i<val.length();i++)
        {
            String achar = val.substring(i, i+1);

            if(achar.equals("&")) out = out + "&amp;";
            else if(achar.equals("<")) out = out + "&lt;";
            else if(achar.equals(">")) out = out + "&gt;";
            else if(achar.equals("\"")) out = out + "&quot;";
            else out = out + achar;
        }
        return out;
    }

    public void deleteValue()
    {
        value = null;
        gotValue = false;
    }
    public boolean doesHaveValue()
    {
        return gotValue;
    }
    public boolean doesThisNodeHaveValues()
    {
        if (gotValue) return true;
        if (this.getAttributes().howManyValues() > 0) return true;
        return false;
    }
    public void setDataType(String typ) throws ApplicationException
    {
        try
        {
            dataType = BasicDataType.valueOf(typ);
        }
        catch(IllegalArgumentException ie)
        {
            throw new ApplicationException(ie.getMessage());
        }
    }
    public String getDataType()
    {
        return dataType.toString();
    }
    public void setBasicDataType(BasicDataType type)
    {
        dataType = type;
    }
    public BasicDataType getBasicDataType()
    {
        return dataType;
    }

    public CardinalityType getCardinalityType()
    {
        if (isChoiceNode())
        {
            return CardinalityType.valueOf(cardinality.toString().substring(0, (cardinality.toString().length() - Config.SUFFIX_OF_CHOICE_CARDINALITY.length())).trim());
        }
        else if(isChoiceMember()) return getParent().getCardinalityType();
        else return cardinality;
    }
    public void setCardinalityType(CardinalityType type) throws ApplicationException
    {
        cardinality = type;
    }

    public int getMaxCardinality()
    {
        Cardinality _cardinality = null;
        try
        {
            _cardinality = new Cardinality(getCardinalityType());
        }
        catch(IllegalArgumentException ie) { return -1; }
        return _cardinality.getMaximum();
    }
    public int getMinCardinality()
    {
        Cardinality _cardinality = null;
        try
        {
            _cardinality = new Cardinality(getCardinalityType());
        }
        catch(IllegalArgumentException ie) { return -1; }
        return _cardinality.getMinimum();
    }
    public CommonNode getMyself()
    {
        return this;
    }
    public boolean equals(Object c)
    {
        if (!(c instanceof CommonNode)) return false;
        CommonNode node = (CommonNode) c;
        if (!this.getName().equals(node.getName())) return false;
        if (this.getNodeType().getType() == CommonNodeModeType.COMMON.getType() ) return false;
        if (this.getNodeType().getType() != node.getNodeType().getType()) return false;
        if (this.getModeType().getType() != node.getModeType().getType()) return false;
        if ((this.getParent() == null)&&(node.getParent() == null)) return true;
        if (this.getParent() == node.getParent()) return true;
        else return false;
    }
    public CommonNode cloneNode(CommonNode target, CommonNode source, String newUUID, String newXPath, CommonNode newParent) throws ApplicationException
    {
        if (source == null) throw new ApplicationException("Null Source node for clonning");
        if (target == null) throw new ApplicationException("Null target node for clonning");
        if (target != this) throw new ApplicationException("Invalid target CommonNode Object");

//        if ((this.getNodeType().getType() != CommonNodeType.ATTRIBUTE.getType())&&
//            (newParent.getNodeType().getType() != CommonNodeType.SEGMENT.getType()))
//                throw new ApplicationException("The parent of non-attribute node must be segment node");

        if (source.isChoiceNode())
        {
            this.setCardinalityType(CardinalityType.valueOf(source.getCardinalityType().toString() + " " + Config.SUFFIX_OF_CHOICE_CARDINALITY));
        }
        else this.setCardinalityType(source.getCardinalityType());

        this.uuid = newUUID;

        this.name = source.getName();

        this.xpath = newXPath;

        if (newParent == null) this.parent = null;
        else this.setParent(newParent);

        this.dataType = source.getBasicDataType();
        this.value = null;
        this.gotValue = false;

        this.attributes = new CommonAttributeImpl(this);// source.getAttributes();


        this.attributes.setAttributeItems(source.getAttributes().getAttributeItems());
        this.attributes.deleteAllItemsValues();

        return this;
    }
    public CommonNode createNewInstance()
    {

        return new CommonNodeImpl(this.getNodeType(), this.getModeType());

    }

    public CommonAttribute getAttributes()
    {
        return this.attributes;
    }
    public void setAttributes(CommonAttribute attributes) throws ApplicationException
    {
        if (getNodeType().getType() == CommonNodeType.ATTRIBUTE.getType()) throw new ApplicationException("This is an attribute node Object. Any Attribute is not allowed.");
        if (getNodeType().getType() == CommonNodeType.COMMON.getType()) throw new ApplicationException("This is a common node Object. Any Attribute is not allowed.");

        if (attributes == null) throw new ApplicationException("This attribute set Object is null.");

        CommonAttribute attributeSet = new CommonAttributeImpl(this);

        for (int i=0;i<attributes.getAttributeItems().size();i++)
        {
            CommonAttributeItem item = attributes.getAttributeItem(i);
            attributeSet.addNewAttributeItem(item);
        }
        this.attributes = attributeSet;
    }
    public void addAttributeItem(CommonAttributeItem att) throws ApplicationException
    {

        if (getNodeType().getType() == CommonNodeType.ATTRIBUTE.getType()) throw new ApplicationException("This is an attribute node Object. Any Attribute is not allowed.");
        if (getNodeType().getType() == CommonNodeType.COMMON.getType()) throw new ApplicationException("This is a common node Object. Any Attribute is not allowed.");

        if (attributes == null) attributes = new CommonAttributeImpl(this);

        attributes.addNewAttributeItem(att);
    }
    public void setDataTypeOfAttributeItem(String att, BasicDataType type) throws ApplicationException
    {
        if (attributes == null) throw new ApplicationException("Attribute Instance is not created yet. (set data type)");
        attributes.setDataTypeOfOneAttributeItem(att, type);//addNewAttributeItem(new CommonAttributeItemImpl(attributes, att, type));
    }
    public void setValueOfAttributeItem(String att, String val) throws ApplicationException
    {
        if (getModeType().getType() == CommonNodeModeType.META.getType()) throw new ApplicationException("This node is meta mode. Any Attribute is not allowed.");
        if (getModeType().getType() == CommonNodeModeType.COMMON.getType()) throw new ApplicationException("This node is common mode. Any Attribute is not allowed.");
        if (attributes == null) throw new ApplicationException("Attribute Instance is not created yet. (set value)");

        attributes.setValueOfOneAttributeItem(att, val);
    }
    public void deleteValueOfAttributeItem(String att) throws ApplicationException
    {
        if (attributes == null) throw new ApplicationException("Attribute Instance is not created yet. (delete)");

        attributes.deleteValueOfOneAttributeItem(att);
    }
    public void removeAttributeItem(String att) throws ApplicationException
    {
        if (attributes == null) throw new ApplicationException("Attribute Instance is not created yet. (remove)");

        attributes.removeAttributeItem(att);
    }

    public boolean isChoiceNode()
    {
        return cardinality.toString().endsWith(Config.SUFFIX_OF_CHOICE_CARDINALITY);

    }
    public boolean isChoiceMember()
    {
        if (getParent() == null) return false;
        return getParent().isChoiceNode();
    }


}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.6  2008/06/06 18:54:28  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/08/02 14:24:46  umkis
 * HISTORY      : Update test instance generator engine
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/07/17 16:10:44  wangeug
 * HISTORY      : change UIUID to xmlPath
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/07/10 20:05:34  umkis
 * HISTORY      : replace 'gov.nih.nci.caadapter.hl7.datatype.Cardinality' into 'gov.nih.nci.caadapter.common.Cardinality'
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/10 18:14:38  umkis
 * HISTORY      : substitute 'import org.hl7.meta.Cardinality' into 'gov.nih.nci.caadapter.hl7.datatype.Cardinality'
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:39:24  umkis
 * HISTORY      : Basic resource programs for csv cardinality and test instance generating.
 * HISTORY      :
 */
