/*
 * <!-- LICENSE_TEXT_START -->
 *  $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/CommonSegment.java,v 1.2 2008-06-06 18:54:28 phadkes Exp $
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

import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.2 $
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
 * HISTORY      : Revision 1.1  2007/07/09 15:39:24  umkis
 * HISTORY      : Basic resource programs for csv cardinality and test instance generating.
 * HISTORY      :
 */

