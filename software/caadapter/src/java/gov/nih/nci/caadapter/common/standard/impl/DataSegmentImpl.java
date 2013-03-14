/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common.standard.impl;

import gov.nih.nci.caadapter.common.standard.DataSegment;
import gov.nih.nci.caadapter.common.standard.MetaSegment;
import gov.nih.nci.caadapter.common.standard.DataField;
import gov.nih.nci.caadapter.common.standard.CommonNode;
import gov.nih.nci.caadapter.common.standard.type.CommonNodeModeType;
import gov.nih.nci.caadapter.common.ApplicationException;

import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.4 $
 *          date        Jul 2, 2007
 *          Time:       8:16:03 PM $
 */
public class DataSegmentImpl extends CommonSegmentImpl implements DataSegment
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: DataSegmentImpl.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/impl/DataSegmentImpl.java,v 1.4 2008-06-09 19:53:49 phadkes Exp $";

    private MetaSegment sourceSegment;

    public DataSegmentImpl()
    {
        super();
        try
        {
            this.setModeType(CommonNodeModeType.DATA);
        }
        catch(ApplicationException ae)
        {}
    }
//    public DataSegmentImpl(DataSegment seg) throws ApplicationException
//    {
//        super(seg);
//        this.setModeType(CommonNodeModeType.DATA);
//    }
//    public DataSegmentImpl(String name) throws ApplicationException
//    {
//        super(name);
//        this.setModeType(CommonNodeModeType.DATA);
//    }
    public DataSegmentImpl(DataSegment seg, String name) throws ApplicationException
    {
        super(seg, name);
        this.setModeType(CommonNodeModeType.DATA);
    }
    public DataSegmentImpl(DataSegment seg, MetaSegment meta) throws ApplicationException
    {
        super(meta.getName());
        this.setModeType(CommonNodeModeType.DATA);
        this.cloneNode(this, meta, meta.getXmlPath(), meta.getXPath(), seg);
        setSourceMetaSegment(meta);
        seg.addChildNode(this);
    }

    public MetaSegment getMetaSegment()
    {
        return sourceSegment;
    }
    public void setSourceMetaSegment(MetaSegment meta) throws ApplicationException
    {
        if (meta == null) throw new ApplicationException("Source Meta is null.");
        sourceSegment = meta;
    }
    public String generateDataFileContent()
    {
        return "";
    }
    public void replaceChildDataNodes(List<CommonNode> listSource) throws ApplicationException
    {
        super.replaceChildNodes(listSource);
    }
    public DataSegment createNewInstance()
    {
        return new DataSegmentImpl();
    }
    public DataField createNewFieldInstance()
    {
        return new DataFieldImpl();
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
