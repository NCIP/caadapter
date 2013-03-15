/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/*





 */

package gov.nih.nci.caadapter.common.standard.impl;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.standard.CommonSegment;
import gov.nih.nci.caadapter.common.standard.CommonField;
import gov.nih.nci.caadapter.common.standard.CommonNode;
import gov.nih.nci.caadapter.common.standard.type.CommonNodeType;
import gov.nih.nci.caadapter.common.standard.type.CommonNodeModeType;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Jul 2, 2007
 *          Time:       8:02:58 PM $
 */
public class CommonFieldImpl extends CommonNodeImpl implements CommonField
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: CommonFieldImpl.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/impl/CommonFieldImpl.java,v 1.3 2008-06-09 19:53:49 phadkes Exp $";


    protected CommonFieldImpl()
    {
        super(CommonNodeType.FIELD,  CardinalityType.valueOf(Config.CARDINALITY_ZERO_TO_ONE));
    }
    protected CommonFieldImpl(CommonNodeModeType mode)
    {
        super(CommonNodeType.FIELD, mode);
        try
        {
            this.setCardinalityType(CardinalityType.valueOf(Config.CARDINALITY_ZERO_TO_ONE));
        }
        catch(ApplicationException ae)
        {
            System.err.println("Common Field Construction Error : " + ae.getMessage());
        }
    }
    public CommonFieldImpl(String nam) throws ApplicationException
    {
        super(CommonNodeType.FIELD, nam);
        this.setCardinalityType(CardinalityType.valueOf(Config.CARDINALITY_ZERO_TO_ONE));
    }
    public final void setParent(CommonNode seg) throws ApplicationException
    {
        if (seg instanceof CommonSegment) super.setParent((CommonSegment)seg);
        else throw new ApplicationException("This node is not a segment for a parent.");
    }

    public CommonSegment getParent()
    {
        return (CommonSegment) super.getParent();
    }

    public CommonField createNewInstance()
    {
        return new CommonFieldImpl();
    }
    public CommonField cloneNode(CommonField target, CommonField source, String newUUID, String newXPath, CommonNode newParent) throws ApplicationException
    {
        return (CommonField) super.cloneNode(target, source, newUUID, newXPath, newParent);

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
