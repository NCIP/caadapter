/*
 * <!-- LICENSE_TEXT_START -->
 *  $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/function/FunctionVocabularyEventHandlerNode.java,v 1.2 2008-06-06 18:54:28 phadkes Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 *	The HL7 SDK Software License, Version 1.0
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

package gov.nih.nci.caadapter.common.function;


/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since HL7 SDK v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-06 18:54:28 $
 */
public class FunctionVocabularyEventHandlerNode
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: FunctionVocabularyEventHandlerNode.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/function/FunctionVocabularyEventHandlerNode.java,v 1.2 2008-06-06 18:54:28 phadkes Exp $";

    private String name = "";
    private boolean attribute = false;
    private String value = "";
    private FunctionVocabularyEventHandlerNode upper = null;
    private FunctionVocabularyEventHandlerNode lower = null;
    private FunctionVocabularyEventHandlerNode right = null;
    private FunctionVocabularyEventHandlerNode lowerAttr = null;

    FunctionVocabularyEventHandlerNode()
    { }

    FunctionVocabularyEventHandlerNode(String nm)
    {
        setName(nm);
    }
    FunctionVocabularyEventHandlerNode(FunctionVocabularyEventHandlerNode up, String nm) throws FunctionException
    {
        setUpper(up);
        setName(nm);
    }

    FunctionVocabularyEventHandlerNode(FunctionVocabularyEventHandlerNode up, String nm, boolean bl) throws FunctionException
    {
        setUpper(up);
        setName(nm);
        setAttribute(bl);
    }

    public String getName() { return name; }
    public String getValue() { return value; }
    public boolean isAttribute() { return attribute; }
    public FunctionVocabularyEventHandlerNode getUpper() { return upper; }
    public FunctionVocabularyEventHandlerNode getLower() { return lower; }
    public FunctionVocabularyEventHandlerNode getRight() { return right; }
    public FunctionVocabularyEventHandlerNode getLowerAttr() { return lowerAttr; }
    public void setName(String st) { name = st; }
    public void setValue(String st) { value = st; }
    public void setAttribute(boolean bl) { attribute = bl; }
    public void setAttribute() { attribute = true; }
    public void setUpper(FunctionVocabularyEventHandlerNode nde) throws FunctionException
    {
        if (nde.isAttribute()) throw new FunctionException("Attribute Node cannot set any lower node(1).", new Throwable());
        upper = nde;
    }
    public void setLower(FunctionVocabularyEventHandlerNode nde) throws FunctionException
    {
        if (isAttribute()) throw new FunctionException("Attribute Node cannot set any lower node(2).", new Throwable());
        lower = nde;
    }
    public void setLowerAttr(FunctionVocabularyEventHandlerNode nde) throws FunctionException
    {
        if (isAttribute()) throw new FunctionException("Attribute Node cannot set any lower node(3).", new Throwable());
        lowerAttr = nde;
    }
    public void setRight(FunctionVocabularyEventHandlerNode nde) throws FunctionException
    {
        if (isAttribute() != nde.isAttribute()) throw new FunctionException("Any Element node cannot be linked with an Attribute Node.", new Throwable());
        right = nde;
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:02:37  wangeug
 * HISTORY      : initial loading of common module
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/11/01 02:06:52  umkis
 * HISTORY      : Extending function of vocabulary mapping : URL XML vom file can use.
 * HISTORY      :
 */
