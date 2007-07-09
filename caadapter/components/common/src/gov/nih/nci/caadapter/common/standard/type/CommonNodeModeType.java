/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/type/CommonNodeModeType.java,v 1.1 2007-07-09 15:39:24 umkis Exp $
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

package gov.nih.nci.caadapter.common.standard.type;

import java.util.Hashtable;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.1 $
 *          date        Jul 2, 2007
 *          Time:       7:53:52 PM $
 */
public class CommonNodeModeType implements java.io.Serializable
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: CommonNodeModeType.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/type/CommonNodeModeType.java,v 1.1 2007-07-09 15:39:24 umkis Exp $";


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The Common mode
     */
    public static final int COMMON_TYPE = 0;

    /**
     * The instance of the Common mode
     */
    public static final CommonNodeModeType COMMON = new CommonNodeModeType(COMMON_TYPE, "Common");

    /**
     * The Meta mode
     */
    public static final int META_TYPE = 1;

    /**
     * The instance of the Meta mode
     */
    public static final CommonNodeModeType META = new CommonNodeModeType(META_TYPE, "Meta");

    /**
     * The Data mode
     */
    public static final int DATA_TYPE = 2;

    /**
     * The instance of the Data mode
     */
    public static final CommonNodeModeType DATA = new CommonNodeModeType(DATA_TYPE, "Data");



    /**
     * Field _memberTable
     */
    private static java.util.Hashtable _memberTable = init();

    /**
     * Field type
     */
    private int type = -1;

    /**
     * Field stringValue
     */
    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private CommonNodeModeType(int type, java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- gov.nih.nci.hl7.csv.meta.impl.types.BasicDataType(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerate
     *
     * Returns an enumeration of all possible instances of
     * BasicDataType
     *
     * @return Enumeration
     */
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate()

    /**
     * Method getType
     *
     * Returns the type of this BasicDataType
     *
     * @return int
     */
    public int getType()
    {
        return this.type;
    } //-- int getType()

    /**
     * Method init
     *
     *
     *
     * @return Hashtable
     */
    private static java.util.Hashtable init()
    {
        Hashtable members = new Hashtable();
        members.put(COMMON.toString(), COMMON);
        members.put(META.toString(), META);
        members.put(DATA.toString(), DATA);

        return members;
    } //-- java.util.Hashtable init()

    /**
     * Method readResolve
     *
     *  will be called during deserialization to replace the
     * deserialized object with the correct constant instance.
     * <br/>
     *
     * @return Object
     */
    private java.lang.Object readResolve()
    {
        return valueOf(this.stringValue);
    } //-- java.lang.Object readResolve()

    /**
     * Method toString
     *
     * Returns the String representation of this BasicDataType
     *
     * @return String
     */
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString()

    /**
     * Method valueOf
     *
     * Returns a new BasicDataType based on the given String value.
     *
     * @param string
     * @return BasicDataType
     */
    public static CommonNodeModeType valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid CommonNodeModeType";
            throw new IllegalArgumentException(err);
        }
        return (CommonNodeModeType) obj;
    } //-- gov.nih.nci.hl7.csv.meta.impl.types.BasicDataType valueOf(java.lang.String)

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
