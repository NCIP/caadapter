/*
 * <!-- LICENSE_TEXT_START -->
 *  $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/CommonTreeMeta.java,v 1.2 2008-06-06 18:54:28 phadkes Exp $
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
import gov.nih.nci.caadapter.common.standard.type.CommonNodeModeType;
import gov.nih.nci.caadapter.common.standard.type.NodeIdentifierType;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.2 $
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
 * HISTORY      : Revision 1.1  2007/07/09 15:39:24  umkis
 * HISTORY      : Basic resource programs for csv cardinality and test instance generating.
 * HISTORY      :
 */

