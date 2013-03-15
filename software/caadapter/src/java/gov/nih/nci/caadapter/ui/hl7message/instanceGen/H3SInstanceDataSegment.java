/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.ui.hl7message.instanceGen;

import gov.nih.nci.caadapter.common.standard.impl.DataSegmentImpl;

import gov.nih.nci.caadapter.ui.hl7message.instanceGen.type.H3SInstanceSegmentType;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Jul 6, 2007
 *          Time:       2:50:26 PM $
 */

public class H3SInstanceDataSegment extends DataSegmentImpl
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: H3SInstanceDataSegment.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/instanceGen/H3SInstanceDataSegment.java,v 1.3 2008-06-09 19:53:52 phadkes Exp $";

    H3SInstanceSegmentType segmentType;

    public H3SInstanceDataSegment(H3SInstanceSegmentType type)
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

    public H3SInstanceDataSegment createNewInstance()
    {
        return new H3SInstanceDataSegment(segmentType);
    }

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
 * HISTORY      : Revision 1.1  2007/07/09 15:37:07  umkis
 * HISTORY      : test instance generating.
 * HISTORY      :
 */
