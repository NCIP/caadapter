/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.dvts.common;

import gov.nih.nci.caadapter.dvts.common.util.UUIDGenerator;

/**
 * A system exception is defined as either a checked exception or an unchecked exception.
 * Most often system exceptions are thrown as subclasses of RuntimeException by the JVM.
 * A NullPointerException, or an ArrayOutOfBoundsException, for example, will be thrown
 * due to a bug in the code. Another type of system exception occurs when the system
 * encounters an improperly configured resource such as a misspelled JNDI lookup.
 * In this case, it will throw a checked exception. It makes a lot of sense to catch these
 * checked system exceptions and throw them as unchecked exceptions. The rule of thumb is,
 * if there isn't anything you can do about an exception, it's a system exception and it
 * should be thrown as an unchecked exception.
 *
 * @author OWNER: Eric Chen
 * @author LAST UPDATE: $Author: phadkes $
 * @version $Revision: 1.3 $
 * @date $$Date: 2008-09-24 19:51:48 $
 * @since caAdapter v1.2
 */

public class SystemException extends RuntimeException
{
	protected boolean isLogged;
	protected String uniquieID;

	public SystemException()
	{
		super("Error occurred at system level");
		uniquieID = UUIDGenerator.getUniqueIDByHostName();
	}

	public SystemException(String message)
	{
		super(message);
		uniquieID = UUIDGenerator.getUniqueIDByHostName();
	}

	public SystemException(Throwable cause)
	{
		super(cause);
		uniquieID = UUIDGenerator.getUniqueIDByHostName();
	}

	public SystemException(String message, Throwable cause)
	{
		super(message, cause);
		uniquieID = UUIDGenerator.getUniqueIDByHostName();
	}

	public String getMessage()
	{
		// Get this exception's message.
		StringBuffer msg = new StringBuffer(super.getMessage());

		Throwable parent = this;
		Throwable child;

		// Look for nested exceptions.
		while ((child = parent.getCause()) != null)
		{
			// Get the child's message.
			String msg2 = child.getMessage();

			// If we found a message for the child exception,
			// we append it.
			if (msg2 != null)
			{
				msg.append(":: ").append(msg2);
			}

			// Any nested ApplicationException will append its own
			// children, so we need to break out of here.
			if (child instanceof SystemException)
			{
				break;
			}
			parent = child;
		}

		// Return the completed message.
		return msg.toString();
	}

	public String getUniquieID()
	{
		return uniquieID;
	}

	public boolean isLogged()
	{
		return isLogged;
	}

	public void setLogged(boolean logged)
	{
		isLogged = logged;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
