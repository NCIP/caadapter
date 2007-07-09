/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/impl/CommonAttributeItemImpl.java,v 1.1 2007-07-09 15:39:24 umkis Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE  
 * ******************************************************************
 *
 *	The caAdapter Software License, Version 1.0
 *
 *	Copyright 2001 SAIC. This software was developed in conjunction with the National Cancer
 *	Institute, and so to the extent government employees are co-authors, any rights in such works
 *	shall be subject to Title 17 of the United States Code, section 105.
 *
 *	Redistribution and use in source and binary forms, with or without modification, are permitted
 *	provided that the following conditions are met:
 *
 *	1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *	and the disclaimer of Article 3, below.  Redistributions in binary form must reproduce the above
 *	copyright notice, this list of conditions and the following disclaimer in the documentation and/or
 *	other materials provided with the distribution.
 *
 *	2.  The end-user documentation included with the redistribution, if any, must include the
 *	following acknowledgment:
 *
 *	"This product includes software developed by the SAIC and the National Cancer
 *	Institute."
 *
 *	If no such end-user documentation is to be included, this acknowledgment shall appear in the
 *	software itself, wherever such third-party acknowledgments normally appear.
 *
 *	3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or
 *	promote products derived from this software.
 *
 *	4. This license does not authorize the incorporation of this software into any proprietary
 *	programs.  This license does not authorize the recipient to use any trademarks owned by either
 *	NCI or SAIC-Frederick.
 *
 *	5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *	WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *	MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *	DISCLAIMED.  IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR
 *	THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *	EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *	PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *	PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 *	OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *	NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * ******************************************************************
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
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.1 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/impl/CommonAttributeItemImpl.java,v 1.1 2007-07-09 15:39:24 umkis Exp $";

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
 */
