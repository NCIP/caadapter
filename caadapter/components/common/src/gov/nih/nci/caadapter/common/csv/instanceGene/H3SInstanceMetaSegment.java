/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/instanceGene/H3SInstanceMetaSegment.java,v 1.1 2007-07-09 15:37:07 umkis Exp $
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

package gov.nih.nci.caadapter.common.csv.instanceGene;

import gov.nih.nci.caadapter.common.standard.impl.MetaSegmentImpl;
import gov.nih.nci.caadapter.common.standard.CommonSegment;

import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.1 $
 *          date        Jul 6, 2007
 *          Time:       2:56:18 PM $
 */
public class H3SInstanceMetaSegment extends MetaSegmentImpl
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: H3SInstanceMetaSegment.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/instanceGene/H3SInstanceMetaSegment.java,v 1.1 2007-07-09 15:37:07 umkis Exp $";

    private H3SInstanceSegmentType segmentType;
    private String CLONE_NAME_SEQUANCE_SEPARATOR = "XX";

    public H3SInstanceMetaSegment(H3SInstanceSegmentType type)
    {
        super();
        segmentType = type;
    }

    public H3SInstanceSegmentType getH3SSegmentType()
    {
        return segmentType;
    }

    public boolean isAttribute()
    {
        return (segmentType.getType() == H3SInstanceSegmentType.ATTRIBUTE.getType());
    }
    public boolean isClone()
    {
        return (segmentType.getType() == H3SInstanceSegmentType.CLONE.getType());
    }

    public H3SInstanceMetaSegment createNewInstance()
    {
        return new H3SInstanceMetaSegment(segmentType);
    }
    public H3SInstanceDataSegment createNewDataInstance()
    {
        return new H3SInstanceDataSegment(segmentType);
    }

    public String getSequenceSeparator()
    {
        return CLONE_NAME_SEQUANCE_SEPARATOR;
    }


    public String getName()
    {
        String originalName = getOriginalName();
        int count = 0;
        try
        {
            count = countName(originalName);
        }
        catch(Exception ee)
        {
            return originalName;
        }
        if (count == 1) return originalName;
        else if (count == 0) return null;
        return originalName + getSequenceSeparator() + getNameSequence();
    }
    public String getOriginalName()
    {
        return super.getName();
    }
    public int countName(String name)
    {
        H3SInstanceMetaSegment parent = (H3SInstanceMetaSegment) getParent();
        List<CommonSegment> siblings = parent.getChildSegments();

        int count = 0;
        for(int i=0;i<siblings.size();i++)
        {
            H3SInstanceMetaSegment seg = (H3SInstanceMetaSegment)siblings.get(i);
            if (getH3SSegmentType().getType() == seg.getH3SSegmentType().getType())
            {
                if (name.trim().equals(seg.getOriginalName().trim())) count++;
            }
        }
        return count;
    }

    private int getNameSequence()
    {
        String originalName = getOriginalName();
        H3SInstanceMetaSegment parent = (H3SInstanceMetaSegment) getParent();
        List<CommonSegment> siblings = parent.getChildSegments();

        int count = 0;
        for(int i=0;i<siblings.size();i++)
        {
            H3SInstanceMetaSegment seg = (H3SInstanceMetaSegment)siblings.get(i);
            if (getH3SSegmentType().getType() == seg.getH3SSegmentType().getType())
            {
                if (originalName.trim().equals(seg.getOriginalName().trim())) count++;
                if (seg == this) return count;
            }
        }
        return -1;
    }

//    public String getOriginalName()
//    {
//        String originalName = getName();
//        if (segmentType.getType() == H3SSegmentType.CLONE.getType())
//        {
//            int idx = originalName.indexOf(CLONE_NAME_SEQUANCE_SEPARATOR);
//            if (idx < 0) return originalName;
//            return originalName.substring(0, idx);
//        }
//        else return originalName;
//    }
//
//    public int getCloneNameSequence()
//    {
//        String originalName = getName();
//        if (segmentType.getType() == H3SSegmentType.CLONE.getType())
//        {
//            int idx = originalName.indexOf(CLONE_NAME_SEQUANCE_SEPARATOR);
//            if (idx < 0) return -1;
//            String numStr = originalName.substring(idx + CLONE_NAME_SEQUANCE_SEPARATOR.length());
//            try
//            {
//                int res = Integer.parseInt(numStr);
//                if (res < 0) return 0;
//                else return res;
//            }
//            catch(NumberFormatException ne)
//            {
//                return 0;
//            }
//        }
//        else return -1;
//    }

//    public void setName(String nam) throws ApplicationException
//    {
//
//    }
//    public void addChildNode(CommonNode node) throws ApplicationException
//    {
//        if (node instanceof MetaField) super.addChildNode((MetaField) node);
//        else if (node instanceof H3SMetaSegment)
//        {
//            super.addChildNode((MetaField) node);
//        }
//    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
