/*
 * <!-- LICENSE_TEXT_START -->
 *  $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/CommonNode.java,v 1.3 2008-06-06 18:54:28 phadkes Exp $
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
 * <!-- LICENSE_TEXT_END -->
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
 *          revision    $Revision: 1.3 $
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
 * HISTORY      : Revision 1.2  2007/07/17 16:09:43  wangeug
 * HISTORY      : change UIUID to xmlPath
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:39:24  umkis
 * HISTORY      : Basic resource programs for csv cardinality and test instance generating.
 * HISTORY      :
 */

