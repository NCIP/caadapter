/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/*





 */

package gov.nih.nci.caadapter.common.function;


/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since HL7 SDK v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:53:49 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/function/FunctionVocabularyEventHandlerNode.java,v 1.3 2008-06-09 19:53:49 phadkes Exp $";

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
 * HISTORY      : Revision 1.2  2008/06/06 18:54:28  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:02:37  wangeug
 * HISTORY      : initial loading of common module
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/11/01 02:06:52  umkis
 * HISTORY      : Extending function of vocabulary mapping : URL XML vom file can use.
 * HISTORY      :
 */
