/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common.standard.impl;

import gov.nih.nci.caadapter.common.standard.DataField;
import gov.nih.nci.caadapter.common.standard.MetaField;
import gov.nih.nci.caadapter.common.standard.DataSegment;
import gov.nih.nci.caadapter.common.standard.type.CommonNodeModeType;
import gov.nih.nci.caadapter.common.ApplicationException;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.4 $
 *          date        Jul 2, 2007
 *          Time:       8:14:40 PM $
 */
public class DataFieldImpl extends CommonFieldImpl implements DataField
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: DataFieldImpl.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/impl/DataFieldImpl.java,v 1.4 2008-06-09 19:53:49 phadkes Exp $";

    private MetaField sourceMetaField = null;
    private String attributeNameForData = null;

    public DataFieldImpl()
    {
        super();
        try
        {
            this.setModeType(CommonNodeModeType.DATA);
        }
        catch(ApplicationException ae)
        {}
    }

    public DataFieldImpl(DataSegment par, String name) throws ApplicationException
    {
        super(name);
        this.setParent(par);
        this.setModeType(CommonNodeModeType.DATA);
    }
    public DataFieldImpl(DataSegment par, MetaField meta) throws ApplicationException
    {
        super(meta.getName());
        this.setModeType(CommonNodeModeType.DATA);
        this.cloneNode(this, meta, meta.getXmlPath(), meta.getXPath(), par);
        setSourceMetaField(meta);
        par.addChildNode(this);
    }

    public void setSourceMetaField(MetaField meta)
    {
        sourceMetaField = meta;
    }

    public MetaField getSourceMetaField()
    {
        return sourceMetaField;
    }
    public String generateDataFileContent()
    {
        String out = "";
        String val = getValue();
        if ((val == null)||(val.trim().equals(""))) return "";
        val = getXMLFormatValue(getValue().trim());
        if ((attributeNameForData == null)||(attributeNameForData.trim().equals("")))
        {
            String former = "<" + getName() + ">";
            String later = "</" + getName() + ">";
            if (val.trim().length() < 30)
            {
                out = this.getIndentationSpaces() + former + val + later + "\r\n";
            }
            else
            {
                out = this.getIndentationSpaces() + former + "\r\n" +
                      this.getIndentationSpaces() + "    " + val + "\r\n" +
                      this.getIndentationSpaces() + later + "\r\n";
            }
        }
        else
        {
            out = this.getIndentationSpaces() + "<" + getName() + " " + attributeNameForData + "=\"" + val + "\"/>" + "\r\n";
        }

        return out;
    }
    public void setAttributeNameForData(String s)
    {
        attributeNameForData = s;
    }
    public String getAttributeNameForData()
    {
        return attributeNameForData;
    }
    public DataField createNewInstance()
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
