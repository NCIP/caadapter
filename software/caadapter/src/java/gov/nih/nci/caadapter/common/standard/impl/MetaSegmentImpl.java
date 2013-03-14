/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.common.standard.impl;

import gov.nih.nci.caadapter.common.standard.MetaSegment;
import gov.nih.nci.caadapter.common.standard.DataSegment;
import gov.nih.nci.caadapter.common.standard.MetaField;
import gov.nih.nci.caadapter.common.standard.type.CommonNodeModeType;
import gov.nih.nci.caadapter.common.ApplicationException;

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
 *          Time:       8:22:14 PM $
 */
public class MetaSegmentImpl extends CommonSegmentImpl implements MetaSegment
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: MetaSegmentImpl.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/impl/MetaSegmentImpl.java,v 1.4 2008-06-09 19:53:49 phadkes Exp $";

    private List<DataSegment> LinkedDataSegment = new ArrayList<DataSegment>();


    public MetaSegmentImpl()
    {
        super(CommonNodeModeType.META);

    }

    public MetaSegmentImpl(MetaSegment seg, String name) throws ApplicationException
    {
        super(seg, name);
        this.setModeType(CommonNodeModeType.META);
    }

    public void addLinkedDataSegment(DataSegment a) throws ApplicationException
    {
        // Tagged CCC
//        if (getNumberOfLinkedDataSegments() >= getMaxCardinality())
//            throw new ApplicationException("This cardinality doesn't allow to add data segment node any more (" + getCardinalityType() + ")");
        LinkedDataSegment.add(a);
    }
    public List<DataSegment> getLinkedDataSegments()
    {
        return LinkedDataSegment;
    }
    public int getNumberOfLinkedDataSegments()
    {
        return LinkedDataSegment.size();
    }
    public DataSegment getLinkedDataSegmentInSequence(int n)
    {
        return LinkedDataSegment.get(n);
    }

    public DataSegment creatDataSegment(DataSegment par, DataSegment target) throws ApplicationException
    {
        if ((getName() == null)||(getName().equals(""))) throw new ApplicationException("This MetaSegment object isn't given any name.");
        if (par == null) throw new ApplicationException("Parent Data Segment node is null.");
        if (target == null) throw new ApplicationException("Target Data Segment node is null.");

        target.setName(this.getName());
        target.cloneNode(target, this, this.getXmlPath(), this.getXPath(), par);
        par.addChildNode(target);
        target.setSourceMetaSegment(this);
        addLinkedDataSegment(target);

        return target;
    }

    public String generateMetaFileContent()
    {
        // TODO
        return "";
    }
    public MetaSegment createNewInstance()
    {
        return new MetaSegmentImpl();
    }
    public MetaField createNewFieldInstance()
    {
        return new MetaFieldImpl();
    }
    public DataSegment createNewDataInstance()
    {
        return new DataSegmentImpl();
    }
    public void clearLinkedDataSegments()
    {
        LinkedDataSegment = new ArrayList<DataSegment>();
    }
}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2008/06/06 18:54:28  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/17 16:11:38  wangeug
 * HISTORY      : change UIUID to xmlPath
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:39:24  umkis
 * HISTORY      : Basic resource programs for csv cardinality and test instance generating.
 * HISTORY      :
 */
