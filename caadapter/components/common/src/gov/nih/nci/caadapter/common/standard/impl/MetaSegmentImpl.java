/*
 * <!-- LICENSE_TEXT_START -->
 *  $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/impl/MetaSegmentImpl.java,v 1.3 2008-06-06 18:54:28 phadkes Exp $
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
 *          revision    $Revision: 1.3 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/impl/MetaSegmentImpl.java,v 1.3 2008-06-06 18:54:28 phadkes Exp $";

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
 * HISTORY      : Revision 1.2  2007/07/17 16:11:38  wangeug
 * HISTORY      : change UIUID to xmlPath
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/09 15:39:24  umkis
 * HISTORY      : Basic resource programs for csv cardinality and test instance generating.
 * HISTORY      :
 */
