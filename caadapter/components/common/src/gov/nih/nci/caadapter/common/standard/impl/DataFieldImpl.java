/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/impl/DataFieldImpl.java,v 1.2 2007-07-17 16:11:38 wangeug Exp $
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
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.2 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/impl/DataFieldImpl.java,v 1.2 2007-07-17 16:11:38 wangeug Exp $";

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
 * HISTORY      : Revision 1.1  2007/07/09 15:39:24  umkis
 * HISTORY      : Basic resource programs for csv cardinality and test instance generating.
 * HISTORY      :
 */
