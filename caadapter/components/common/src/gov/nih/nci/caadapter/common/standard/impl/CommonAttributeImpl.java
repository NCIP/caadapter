/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/*





 */

package gov.nih.nci.caadapter.common.standard.impl;

import gov.nih.nci.caadapter.common.standard.CommonAttributeItem;
import gov.nih.nci.caadapter.common.standard.CommonAttribute;
import gov.nih.nci.caadapter.common.standard.CommonNode;
import gov.nih.nci.caadapter.common.standard.type.CommonNodeModeType;
import gov.nih.nci.caadapter.common.standard.type.CommonNodeType;
import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.BasicDataType;

import java.util.List;
import java.util.ArrayList;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.4 $
 *          date        Jul 2, 2007
 *          Time:       7:58:00 PM $
 */
public class CommonAttributeImpl implements CommonAttribute
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: CommonAttributeImpl.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/impl/CommonAttributeImpl.java,v 1.4 2008-06-09 19:53:49 phadkes Exp $";

    private List<CommonAttributeItem> items = new ArrayList<CommonAttributeItem>();
    private CommonNode parent;
    //private CommonNodeModeType modeType = CommonNodeModeType.COMMON;

    public CommonAttributeImpl()
    {

    }
    public CommonAttributeImpl(CommonNode par) throws ApplicationException
    {
        setParent(par);
        //setModeType(par.getModeType());

    }
    public CommonAttributeImpl(CommonNode par, List<CommonAttributeItem> list) throws ApplicationException
    {
        setParent(par);
        //setModeType(par.getModeType());
        setAttributeItems(list);
    }

    public CommonNodeModeType getModeType()
    {
        return parent.getModeType();
    }

    private void setParent(CommonNode node) throws ApplicationException
    {
//        String name = segment.getName();
//        List<CommonAttributeItem> itemsN = new ArrayList<CommonAttributeItem>();
//        for(int i=0;i<items.size();i++)
//        {
//            CommonAttributeItem item = items.get(i);
//            if (name.equals(item.getName())) throw new ApplicationException("There is same attribute name with this parent name, : " + name);
//            items.get(i).setParent(segment);
//        }
//        items = itemsN;
        try
        {
            if (node == null) throw new ApplicationException("Null parent is not allowed at any attribute instance");
            if (node.getModeType().getType() == CommonNodeModeType.COMMON.getType())
                throw new ApplicationException("Common Mode type of parent node is not allowed at any attribute instance");
            if (node.getNodeType().getType() == CommonNodeType.ATTRIBUTE.getType())
                throw new ApplicationException("Any Attribute Node type cannot be set as a parent node.");
            if (node.getNodeType().getType() == CommonNodeType.COMMON.getType())
                throw new ApplicationException("Common Node type of parent node is not allowed at any attribute instance");
        }
        catch(ApplicationException ae)
        {
            //ae.printStackTrace();
            throw ae;
        }
        parent = node;
    }
    public CommonNode getParent()
    {
        return parent;
    }
    public void addNewAttributeItem(CommonAttributeItem item) throws ApplicationException
    {
        if (getParent().isChoiceNode()) throw new ApplicationException("Choice Segment cannot have any own Attribute : " + item.getName() + " : " + getParent().generateXPath());

        validateThisAttributeItem(item);
        item.setParentAttribute(this);
        items.add(item);
    }

//    public void addNewAttribute(String att) throws ApplicationException
//    {
//        checkAttributeName(att, items);
//        CommonAttributeItem item = new CommonAttributeItemImpl(att.trim());
//        item.setParent(parent);
//        items.add(item);
//    }
    private boolean checkAttributeName(String att, List<CommonAttributeItem> itemsT) throws ApplicationException
    {
        return checkAttributeName(att, itemsT, -1);
    }
    private boolean checkAttributeName(String att, List<CommonAttributeItem> itemsT, int skip) throws ApplicationException
    {
        if ((att == null)||(att.trim().equals(""))) throw new ApplicationException("Null Attribute name");
        if (itemsT == null) throw new ApplicationException("Null Checked list");
        if (skip >= itemsT.size()) throw new ApplicationException("Skipped indext overflow");
        att = att.trim();
        for(int i=0;i<itemsT.size();i++)
        {
            if (skip == i) continue;
            CommonAttributeItem item = itemsT.get(i);
            if (att.equalsIgnoreCase(item.getName())) throw new ApplicationException("This attribute name is already used. : " + att);
        }
        return true;
    }
//    public void addAttributeList(List<String> atts) throws ApplicationException
//    {
//        if (atts == null) throw new ApplicationException("This attribute name list is null.");
//        if (atts.size() == 0) return;
//        List<CommonAttributeItem> itemsT = items.subList(0, items.size()-1);
//        for(int i=0;i<atts.size();i++)
//        {
//            String itemS = atts.get(i);
//            checkAttributeName(itemS, itemsT);
//            CommonAttributeItem item = new CommonAttributeItemImpl(itemS.trim());
//            item.setParent(parent);
//            itemsT.add(item);
//        }
//        items = itemsT;
//    }
//    public void addAttributeArray(String[] atts) throws ApplicationException
//    {
//        if (atts == null) throw new ApplicationException("This attribute name array is null.");
//        if (atts.length == 0) return;
//        List<String> itemsT = new ArrayList<String>();
//        for(int i=0;i<atts.length;i++)
//        {
//            itemsT.add(atts[i]);
//        }
//        addAttributeList(itemsT);
//    }
    public void setAttributeItems(List<CommonAttributeItem> itemsL) throws ApplicationException
    {
        List<CommonAttributeItem> itemsT = items;
        items = new ArrayList<CommonAttributeItem>();
        try
        {
            for(int i=0;i<itemsL.size();i++) addNewAttributeItem(itemsL.get(i));
//            {
//                CommonAttributeItem item = itemsL.get(i);
//                checkAttributeName(item.getName(), itemsL, i);
//                item.setParent(parent);
//                itemsT.add(item);
//            }
        }
        catch(ApplicationException ae)
        {
            items = itemsT;
            throw ae;
        }
    }
    public void addAttributeItems(List<CommonAttributeItem> itemsL) throws ApplicationException
    {
        List<CommonAttributeItem> itemsT = items;
        //items = new ArrayList<CommonAttributeItem>();
        try
        {
            for(int i=0;i<itemsL.size();i++) addNewAttributeItem(itemsL.get(i));
        }
        catch(ApplicationException ae)
        {
            items = itemsT;
            throw ae;
        }
    }
    public List<CommonAttributeItem> getAttributeItems()
    {
        return items;
    }
    public List<String> getAttributeItemNames()
    {
        List<String> list = new ArrayList<String>();
        for(int i=0;i<items.size();i++)
        {
            list.add(items.get(i).getName());
        }
        return list;
    }
    public CommonAttributeItem getAttributeItem(String att)
    {
        if ((att == null)||(att.trim().equals(""))) return null;
        att = att.trim();
        for(int i=0;i<items.size();i++)
        {
            if (att.equals(items.get(i).getName())) return items.get(i);
        }
        return null;
    }
    public CommonAttributeItem getAttributeItem(int index)
    {
        try
        {
            return items.get(index);
        }
        catch(Exception ee)
        {
            return null;
        }
    }
    public boolean switchAttributeItems(int index1, int index2)
    {
        CommonAttributeItem item1 = null;
        CommonAttributeItem item2 = null;
        try
        {
             item1 = items.get(index1);
             item2 = items.get(index2);
        }
        catch(Exception ee)
        {
            return false;
        }
        List<CommonAttributeItem> itemsN = new ArrayList<CommonAttributeItem>();
        for(int i=0;i<items.size();i++)
        {
            if (i == index1) itemsN.add(item2);
            else if (i == index2) itemsN.add(item1);
            else itemsN.add(items.get(i));
        }
        items = itemsN;
        return true;
    }
    private boolean validateThisAttributeItem(CommonAttributeItem item) throws ApplicationException
    {
        return validateThisAttributeItem(item, -1);
    }
    private boolean validateThisAttributeItem(CommonAttributeItem item, int index) throws ApplicationException
    {
        if (parent == null) throw new ApplicationException("Any null parent node can not have its own attribute.");
        if (item == null) throw new ApplicationException("Null attribute item was tried to input.");

        if (item.getModeType().getType() == CommonNodeModeType.COMMON.getType()) throw new ApplicationException("This attribute is common mode. : " + item.getName());
        try
        {
            if (item.getNodeType().getType() != CommonNodeType.ATTRIBUTE.getType())
            {
                //System.out.println("CCC6 : " + item.getNodeType().toString());
                throw new ApplicationException("This is not a attribute node. : " + item.getName());
            }
        }
        catch(ApplicationException ae)
        {
            //ae.printStackTrace();
            throw ae;
        }
        //if (item.getModeType().getType() != parent.getModeType().getType()) throw new ApplicationException("The Mode type of this attribute item is not matched to the parent node : " + item.getName());
        //if (item.getNodeType().getType() != parent.getNodeType().getType()) throw new ApplicationException("The Node type of this attribute item is not matched to the parent node : " + item.getName());

        checkAttributeName(item.getName(), items, index);
        return true;
    }
    public boolean replaceAttributeItem(int index1, CommonAttributeItem item)
    {
        if ((index1 < 0)||(index1 >= getAttributeItems().size())) return false;
        try
        {
            validateThisAttributeItem(item, index1);
            item.setParent(parent);
        }
        catch(ApplicationException ee)
        {
            return false;
        }
        List<CommonAttributeItem> itemsN = new ArrayList<CommonAttributeItem>();
        for(int i=0;i<items.size();i++)
        {
            if (i == index1)
            {
                itemsN.add(item);
            }
            else itemsN.add(items.get(i));
        }
        items = itemsN;
        return true;
    }
    public boolean insertAttributeItem(int index1, CommonAttributeItem item)
    {
        if ((index1 < 0)||(index1 >= getAttributeItems().size())) return false;
        try
        {
            validateThisAttributeItem(item);
            item.setParent(parent);
        }
        catch(ApplicationException ee)
        {
            return false;
        }
        List<CommonAttributeItem> itemsN = new ArrayList<CommonAttributeItem>();
        for(int i=0;i<items.size();i++)
        {
            if (i == index1) itemsN.add(item);
            itemsN.add(items.get(i));
        }
        items = itemsN;
        return true;
    }
    public boolean existsAttributeItem(String att)
    {
        if ((att == null)||(att.trim().equals(""))) return false;
        try
        {
            checkAttributeName(att, items);
        }
        catch(ApplicationException ae)
        {
            return true;
        }
        return false;
    }
    public boolean removeAttributeItem(String att)
    {
        int idx = getIndexOfAttributeItem(att);
        if (idx < 0) return false;
        List<CommonAttributeItem> itemsN = new ArrayList<CommonAttributeItem>();
        for(int i=0;i<items.size();i++)
        {
            if (i == idx) continue;
            itemsN.add(items.get(i));
        }
        items = itemsN;
        return true;
    }
    public int getIndexOfAttributeItem(String att)
    {
        att = att.trim();
        for(int i=0;i<items.size();i++)
        {
            if (att.equals(items.get(i).getName())) return i;
        }
        return -1;
    }
    public boolean isRequiredFull()
    {
        for(int i=0;i<items.size();i++)
        {
            CommonAttributeItem item = items.get(i);
            if ((item.isThisRequired())&&(!item.doesHaveValue())) return false;
        }
        return true;
    }
    public int getTheNumberOfAttribute()
    {
         return items.size();
    }
    public void deleteAllItemsValues()
    {
        for(int i=0;i<items.size();i++)
        {
            items.get(i).deleteValue();
        }
    }
    public int getTheNumberOfDataStoredAttribute()
    {
        int count = 0;
        for(int i=0;i<items.size();i++)
        {
            if (items.get(i).doesHaveValue()) count++;
        }
        return count;
    }
    public boolean isAllFull()
    {
        for(int i=0;i<items.size();i++)
        {
            CommonAttributeItem item = items.get(i);
            if (!item.doesHaveValue()) return false;
        }
        return true;
    }
    public int howManyValues()
    {
        int count = 0;
        for(int i=0;i<items.size();i++)
        {
            CommonAttributeItem item = items.get(i);
            if (item.doesHaveValue()) count++;
        }
        return count;
    }
    public String outputAttributesInXMLFormat()
    {
        String out = "";
        for(int i=0;i<items.size();i++)
        {
            CommonAttributeItem item = items.get(i);
            if (item.doesHaveValue())
            {
                String val = item.getValue();
                if (item.getValue() == null) val = "";
                if ((item.getBasicDataType().getType() == BasicDataType.DOUBLE.getType())||
                    (item.getBasicDataType().getType() == BasicDataType.INTEGER.getType())||
                    (item.getBasicDataType().getType() == BasicDataType.NUMBER.getType()))
                        val = item.getValue();
                else val = "\"" + val + "\"";
                out = out + " " + item.getName() + "=" + val;
            }
        }
        return out;
    }

    public boolean setValueOfOneAttributeItem(String att, String val)
    {
        boolean cTag = false;
        List<CommonAttributeItem> itemsN = new ArrayList<CommonAttributeItem>();
        att = att.trim();
        for(int i=0;i<items.size();i++)
        {
            CommonAttributeItem item = items.get(i);
            if (att.equals(item.getName()))
            {
                try
                {
                    item.setValue(val);
                    cTag = true;
                }
                catch(ApplicationException ae)
                {
                    return false;
                }
            }
            itemsN.add(item);
        }
        if (cTag) items = itemsN;
        return cTag;
    }
    public boolean deleteValueOfOneAttributeItem(String att)
    {
        boolean cTag = false;
        List<CommonAttributeItem> itemsN = new ArrayList<CommonAttributeItem>();
        att = att.trim();
        for(int i=0;i<items.size();i++)
        {
            CommonAttributeItem item = items.get(i);
            if (att.equals(item.getName()))
            {
                cTag = true;
                item.deleteValue();
            }
            itemsN.add(item);
        }
        if (cTag) items = itemsN;
        return cTag;
    }
    public boolean setDataTypeOfOneAttributeItem(String att, BasicDataType type)
    {
        boolean cTag = false;
        List<CommonAttributeItem> itemsN = new ArrayList<CommonAttributeItem>();
        att = att.trim();
        for(int i=0;i<items.size();i++)
        {
            CommonAttributeItem item = items.get(i);
            if (att.equals(item.getName()))
            {
                item.setBasicDataType(type);
                cTag = true;
            }
            itemsN.add(item);
        }
        if (cTag) items = itemsN;
        return cTag;
    }
    public String getValueOfOneAttributeItem(String att) throws ApplicationException
    {
        boolean cTag = false;
        String out = null;
        att = att.trim();
        for(int i=0;i<items.size();i++)
        {
            CommonAttributeItem item = items.get(i);
            if (att.equals(item.getName()))
            {
                if (!item.doesHaveValue()) throw new ApplicationException("No valid input data");
                out = item.getValue();
                cTag = true;
            }
        }
        if (!cTag) throw new ApplicationException("Not found this attribute: " + att);
        return out;
    }
    public CommonAttributeItem createNewItemInstance()
    {
        if (this.getAttributeItems().size() == 0) return null;
        CommonAttributeItem item = getAttributeItems().get(0);
        return item.createNewInstance();
    }
    public CommonAttribute cloneAttribute(CommonAttribute cloned) throws ApplicationException
    {
        //CommonAttribute cloned = new CommonAttributeImpl();
        if (cloned == null) throw new ApplicationException("The target attribute object is null.");
        //if (itemS == null) throw new ApplicationException("The target attribute item object is null.");
        if (cloned.getParent() == null) throw new ApplicationException("The parent node of the target attribute object is null.");

        for (int i=0;i<items.size();i++)
        {

            CommonAttributeItem item = items.get(i);
            CommonAttributeItem itemS = new CommonAttributeItemImpl(cloned);

            itemS.cloneNode(itemS, item, item.getXmlPath(), item.getXPath(), cloned.getParent());

            cloned.addNewAttributeItem(itemS);
        }

        return cloned;
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2008/06/06 18:54:28  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/17 16:10:05  wangeug
 * HISTORY      : change UIUID to xmlPath
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:39:24  umkis
 * HISTORY      : Basic resource programs for csv cardinality and test instance generating.
 * HISTORY      :
 */
