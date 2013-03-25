/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common.validation;

import gov.nih.nci.caadapter.common.ApplicationException;

/**
 * An application exception that that occurs when business rules are violated.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @version $Revision: 1.3 $
 * @since caAdapter v1.2
 */

public class ValidationException extends ApplicationException
{
	public ValidationException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ValidationException(String message, Throwable cause, String severity)
	{
		super(message, cause, severity);
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
