/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/*





 */

package gov.nih.nci.caadapter.common.standard.type;

import gov.nih.nci.caadapter.common.function.DateFunction;
import gov.nih.nci.caadapter.common.function.FunctionException;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.BasicDataType;

import java.util.Date;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Jul 2, 2007
 *          Time:       7:51:41 PM $
 */
public class BasicDataTypeValueValidate
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: BasicDataTypeValueValidate.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/type/BasicDataTypeValueValidate.java,v 1.3 2008-06-09 19:53:49 phadkes Exp $";


    private BasicDataType dataType = null;
    private String output = null;

    public BasicDataTypeValueValidate(BasicDataType type)
    {
        dataType = type;
    }

    public boolean validate(Object value)
    {
        if (value == null) return false;
        if (dataType == null) return false;
        if (dataType.getType() == BasicDataType.STRING.getType())
        {
            if (value instanceof String)
            {
                output = (String) value;
                return true;
            }
            else return false;
        }
        else if (dataType.getType() == BasicDataType.NUMBER.getType())
        {
            if (checkInteger(value)) return true;
            else return checkDouble(value);
        }
        else if (dataType.getType() == BasicDataType.INTEGER.getType())
        {
            return checkInteger(value);
        }
        else if (dataType.getType() == BasicDataType.DOUBLE.getType())
        {
            return checkDouble(value);
        }
        else if (dataType.getType() == BasicDataType.DATETIME.getType())
        {
            return checkDateTime(value);
        }
        else return false;
    }
    public String getStringOutput(Object value)
    {
        if (validate(value)) return output;
        else return null;
    }
    private boolean checkInteger(Object value)
    {
        if ((value instanceof Integer)||(value instanceof Long))
        {
            output = "" + value;
            return true;
        }
        else if (value instanceof String)
        {
            try
            {
                Integer.parseInt((String) value);
            }
            catch(NumberFormatException ne)
            {
                try
                {
                    Long.parseLong((String) value);
                }
                catch(NumberFormatException nee)
                {
                    return false;
                }
            }
            output = (String) value;
            return true;
        }
        else return false;
    }
    private boolean checkDouble(Object value)
    {
        if ((value instanceof Double)||(value instanceof Float))
        {
            output = "" + value;
            return true;
        }
        else if (value instanceof String)
        {
            try
            {
                Double.parseDouble((String) value);
            }
            catch(NumberFormatException ne)
            {
                return false;
            }
            output = (String) value;
            return true;
        }
        else return false;
    }
    private boolean checkDateTime(Object value)
    {
        DateFunction function = new DateFunction();
        if (value instanceof Date)
        {
            Date date = (Date) value;
            try
            {
                output = function.checkSimpleDateFormat(function.getDefaultDateFormatString()).format(date);
            }
            catch(FunctionException fe)
            {
                return false;
            }
            return true;
        }
        else if (value instanceof String)
        {
            try
            {
                Date date = function.parseDateFromString(function.checkSimpleDateFormat(function.getDefaultDateFormatString()), (String) value);
                output = (String) value;
            }
            catch(FunctionException fe)
            {
                return false;
            }
            return true;
        }
        else return false;
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
