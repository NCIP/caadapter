/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


 package gov.nih.nci.caadapter.hl7.map;

import gov.nih.nci.caadapter.common.ApplicationException;

/**
 * An exception for the mapping subsystem.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.2 $
 * @date        $Date: 2008-06-09 19:53:50 $
 */

public class MappingException extends ApplicationException{
    public MappingException(String message, Throwable cause)
	{
        super(message, cause);
    }

	public MappingException(String message, Throwable cause, String severity)
	{
		super(message, cause, severity);
	}
}
