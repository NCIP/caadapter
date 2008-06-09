/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.common.validation;

import gov.nih.nci.caadapter.common.ApplicationException;

/**
 * An application exception that that occurs when business rules are violated.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @version $Revision: 1.2 $
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
