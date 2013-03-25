/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.hl7message.instanceGen;

import gov.nih.nci.caadapter.common.standard.impl.MetaSegmentImpl;
import gov.nih.nci.caadapter.common.standard.CommonSegment;
import gov.nih.nci.caadapter.common.ApplicationException;

import gov.nih.nci.caadapter.ui.hl7message.instanceGen.type.H3SInstanceSegmentType;

import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/instanceGen/H3SInstanceMetaSegment.java,v 1.3 2008-06-09 19:53:52 phadkes Exp $";

    private H3SInstanceSegmentType segmentType;
    private String CLONE_NAME_SEQUANCE_SEPARATOR = "XX";

    public H3SInstanceMetaSegment(H3SInstanceSegmentType type)
    {
        super();
        segmentType = type;
    }
    public H3SInstanceMetaSegment(H3SInstanceSegmentType type, String name) throws ApplicationException
    {
        super();
        this.setName(name);

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
        //System.out.println("CVVV 7-1 : " + originalName );
        int count = 0;
        try
        {
            count = countName(originalName);
        }
        catch(Exception ee)
        {
            //System.out.println("CVVV 7-2 : " + originalName );
            return originalName;
        }
        if (count == 1)
        {
            //System.out.println("CVVV 7-3 : " + originalName );
            return originalName;
        }
        else if (count == 0)
        {
            //System.out.println("CVVV 7-4 : " + originalName + " **********");
            return originalName;
        }
        //System.out.println("CVVV 7-5 : " + originalName + getSequenceSeparator() + getNameSequence() );
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
        //System.out.println("CVVV 8-0 : " + siblings.size());
        int count = 0;
        for(int i=0;i<siblings.size();i++)
        {
            H3SInstanceMetaSegment seg = (H3SInstanceMetaSegment)siblings.get(i);
            //System.out.println("CVVV 8-1 : " + name + " : " + seg.getOriginalName() + " : " + getH3SSegmentType().toString() + " : " + seg.getH3SSegmentType().toString());
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
 * HISTORY      : Revision 1.2  2007/08/03 05:01:32  umkis
 * HISTORY      : add items which have to be input data
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/08/02 16:29:40  umkis
 * HISTORY      : This package was moved from the common component
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/08/02 15:43:55  umkis
 * HISTORY      : This package was moved from the common component
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/08/02 14:24:46  umkis
 * HISTORY      : Update test instance generator engine
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:37:07  umkis
 * HISTORY      : test instance generating.
 * HISTORY      :
 */
