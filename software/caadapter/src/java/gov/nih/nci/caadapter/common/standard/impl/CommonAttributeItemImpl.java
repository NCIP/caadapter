/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common.standard.impl;

import gov.nih.nci.caadapter.common.standard.CommonAttributeItem;
import gov.nih.nci.caadapter.common.standard.CommonNode;
import gov.nih.nci.caadapter.common.standard.CommonAttribute;
import gov.nih.nci.caadapter.common.standard.type.CommonNodeModeType;
import gov.nih.nci.caadapter.common.standard.type.CommonNodeType;
import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.BasicDataType;
import gov.nih.nci.caadapter.castor.csv.meta.impl.C_field;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Jul 2, 2007
 *          Time:       8:00:58 PM $
 */
public class CommonAttributeItemImpl extends CommonNodeImpl implements CommonAttributeItem
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: CommonAttributeItemImpl.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/impl/CommonAttributeItemImpl.java,v 1.3 2008-06-09 19:53:49 phadkes Exp $";

    private CommonAttribute parentAttribute;
    private String itemValue;

    private CommonAttributeItemImpl()
    {
        super(CommonNodeType.ATTRIBUTE, CardinalityType.valueOf(Config.CARDINALITY_ZERO_TO_ONE));
        //setParentAttribute(att);
    }

    public CommonAttributeItemImpl(CommonAttribute att) throws ApplicationException
    {
        super(CommonNodeType.ATTRIBUTE, CardinalityType.valueOf(Config.CARDINALITY_ZERO_TO_ONE));
        setParentAttribute(att);
    }

    public CommonAttributeItemImpl(CommonAttribute att, String nam) throws ApplicationException
    {
        super(CommonNodeType.ATTRIBUTE,  CardinalityType.valueOf(Config.CARDINALITY_ZERO_TO_ONE));
        setParentAttribute(att);
        setName(nam);
    }
    public CommonAttributeItemImpl(CommonAttribute att, String nam, String val) throws ApplicationException
    {
        super(CommonNodeType.ATTRIBUTE,  CardinalityType.valueOf(Config.CARDINALITY_ZERO_TO_ONE));
        setParentAttribute(att);
        setName(nam);
        setValue(val);
    }
    public CommonAttributeItemImpl(CommonAttribute att, String nam, BasicDataType type) throws ApplicationException
    {
        super(CommonNodeType.ATTRIBUTE,  CardinalityType.valueOf(Config.CARDINALITY_ZERO_TO_ONE));
        setParentAttribute(att);
        setName(nam);
        if (type == null) setBasicDataType((new C_field()).getDatatype());
        else setBasicDataType(type);
    }
    public CommonAttributeItemImpl(CommonAttribute att, String nam, BasicDataType type, String val) throws ApplicationException
    {
        super(CommonNodeType.ATTRIBUTE,  CardinalityType.valueOf(Config.CARDINALITY_ZERO_TO_ONE));
        setParentAttribute(att);
        setName(nam);
        if (type == null) setDataType((new C_field()).getDatatype().toString());
        else setDataType(type.toString());
        setValue(val);
    }
    public void setParentAttribute(CommonAttribute att) throws ApplicationException
    {
        if (att == null) throw new ApplicationException("Parent Attribute Object is null.");
        super.setModeType(att.getModeType());
        parentAttribute = att;
        super.setParent(att.getParent());

    }
    public CommonAttribute getAttributeObject()
    {
        return parentAttribute;
    }
    public final CommonNode getParent()
    {
        return parentAttribute.getParent();
    }
    public final CommonNodeType getParentNodeType()
    {

        return parentAttribute.getParent().getNodeType();
    }
    public final CommonNodeModeType getModeType()
    {

        return parentAttribute.getParent().getModeType();
    }
    public final void setParent(CommonNode seg) throws ApplicationException
    {
//        CommonNodeModeType modeType = getModeType();
//        try
//        {
//            setModeType(seg.getModeType());
//            super.setParent(seg);
//        }
//        catch(ApplicationException ae)
//        {
//            setModeType(modeType);
//            throw ae;
//        }
        throw new ApplicationException("CommonAttibuteItem.setParent() method is prohibited to use.");
    }
    public void setName(String name) throws ApplicationException
    {
        String check = GeneralUtilities.checkElementName(name);
        if (check != null) throw new ApplicationException(check);

        super.setName(name);
    }

    public void setItemValue(String val)
    {
        itemValue = val;
    }
    public String getItemValue()
    {
        return itemValue;
    }

    public void setCardinalityType(CardinalityType type) throws ApplicationException
    {
        throw new ApplicationException("ATTRIBUTE node can not be set up Cardinality - just apply setRequired() or setOptional().");
    }

    public void setRequired()
    {
        try
        {
            super.setCardinalityType(CardinalityType.valueOf(Config.CARDINALITY_ONE_TO_ONE));
        }
        catch(ApplicationException ae)
        {}
    }
    public void setOptional()
    {
        try
        {
            super.setCardinalityType(CardinalityType.valueOf(Config.CARDINALITY_ZERO_TO_ONE));
        }
        catch(ApplicationException ae)
        {}
    }
    public boolean isThisRequired()
    {
        if (getCardinalityType().getType() == CardinalityType.valueOf(Config.CARDINALITY_ONE_TO_ONE).getType()) return true;
        else return false;
    }

    public CommonAttributeItem createNewInstance()
    {
        return new CommonAttributeItemImpl();
    }
    public CommonAttributeItem cloneNode(CommonAttributeItem target, CommonAttributeItem source, String newUUID, String newXPath, CommonNode newParent) throws ApplicationException
    {
        target.setItemValue(source.getItemValue());
        return (CommonAttributeItem) super.cloneNode(target, source, newUUID, newXPath, newParent);

    }

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
