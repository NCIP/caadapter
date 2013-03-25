/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.ui.hl7message.instanceGen.type;

import java.util.Hashtable;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.2 $
 *          date        Jul 6, 2007
 *          Time:       2:47:27 PM $
 */

public class H3SInstanceSegmentType implements java.io.Serializable
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: H3SInstanceSegmentType.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/instanceGen/type/H3SInstanceSegmentType.java,v 1.2 2008-06-09 19:53:53 phadkes Exp $";

    public H3SInstanceSegmentType()
    {
    }

      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The Common mode
     */
    public static final int CLONE_TYPE = 0;

    /**
     * The instance of the Common mode
     */
    public static final H3SInstanceSegmentType CLONE = new H3SInstanceSegmentType(CLONE_TYPE, "clone");

    /**
     * The Meta mode
     */
    public static final int ATTRIBUTE_TYPE = 1;

    /**
     * The instance of the Meta mode
     */
    public static final H3SInstanceSegmentType ATTRIBUTE = new H3SInstanceSegmentType(ATTRIBUTE_TYPE, "attribute");

//    /**
//     * The Data mode
//     */
//    public static final int DATA_TYPE = 2;
//
//    /**
//     * The instance of the Data mode
//     */
//    public static final H3SSegmentType DATA = new H3SSegmentType(DATA_TYPE, "Data");



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

    private H3SInstanceSegmentType(int type, java.lang.String value) {
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
        members.put(CLONE.toString(), CLONE);
        members.put(ATTRIBUTE.toString(), ATTRIBUTE);
        //members.put(DATA.toString(), DATA);

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
    public static H3SInstanceSegmentType valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid H3SSegmentType";
            throw new IllegalArgumentException(err);
        }
        return (H3SInstanceSegmentType) obj;
    } //-- gov.nih.nci.hl7.csv.meta.impl.types.BasicDataType valueOf(java.lang.String)

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/08/03 05:01:32  umkis
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
