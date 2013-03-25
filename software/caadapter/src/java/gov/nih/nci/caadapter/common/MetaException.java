/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common;


/**
 * An application exception that is specific to meta related functionality.
 *
 * @author OWNER: Eric Chen  Date: Jun 3, 2005
 * @author LAST UPDATE: $Author: phadkes $
 * @version $Revision: 1.3 $
 * @date $$Date: 2008-09-24 19:51:48 $
 * @since caAdapter v1.2
 */

public class MetaException extends ApplicationException {
    public MetaException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetaException(String message, Throwable cause, String severity) {
        super(message, cause, severity);
    }
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
