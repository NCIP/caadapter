/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common;

import gov.nih.nci.caadapter.common.util.UUIDGenerator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An application exception is a custom exception thrown by the application or a
 * third-party library. These are essentially checked exceptions; they denote that
 * some condition in the business logic has not been met. All other application level exceptions
 * should extend from it
 *
 * @author OWNER: Eric Chen
 * @author LAST UPDATE: $Author: phadkes $
 * @version $Revision: 1.3 $
 * @date $$Date: 2008-09-24 19:51:48 $
 * @since caAdapter v1.2
 */

public class ApplicationException extends Exception
{
	protected boolean isLogged;
	protected String uniquieID;
	protected String severity;
	public static final String SEVERITY_LEVEL_WARNING = "warning";
	public static final String SEVERITY_LEVEL_ERROR = "error";

	protected List<ApplicationException> childExceptions = new ArrayList<ApplicationException>();

//    public ApplicationException() {
//        super("Error occurred in application level");
//        uniquieID = UUIDGenerator.getUniqueIDByHostName();
//    }
//
    public ApplicationException(String message) {
        super(message);
        uniquieID = UUIDGenerator.getUniqueIDByHostName();
    }
//
//    public ApplicationException(Throwable cause) {
//        super(cause);
//        uniquieID = UUIDGenerator.getUniqueIDByHostName();
//    }

	public ApplicationException(String message, Throwable cause)
	{
		this(message, cause, SEVERITY_LEVEL_WARNING);
	}

	public ApplicationException(String message, Throwable cause, String severity)
	{
		super(message, cause);
		this.severity = severity;
		uniquieID = UUIDGenerator.getUniqueIDByHostName();
	}

	public void addChildException(ApplicationException e)
	{
		childExceptions.add(e);
	}

	public List<ApplicationException> getChildExceptions()
	{
		return childExceptions;
	}

	public boolean hasChildExceptions()
	{
		return childExceptions.size() > 0;
	}

	public String getDetailedMessage()
	{
		// Get this exception's message.
		StringBuffer msg = new StringBuffer();
		msg.append(super.getMessage());

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
			if (child.getClass() == ApplicationException.class)
			{
				break;
			}
			parent = child;
		}

		// Return the completed message.
		return msg.toString();
	}

	/**
     * Get the list of stack trace traced back to root cause
     * @return
     */
    public List<String> getStackTraceMessages()
	{
		List<String> causeMessages = new ArrayList<String>();
		Throwable parent = this;
		Throwable child;

		causeMessages.add(getStackTraceMessage(parent));

		// Look for nested exceptions.
		while ((child = parent.getCause()) != null)
		{

			causeMessages.add(getStackTraceMessage(child));

			// Any nested ApplicationException will append its own
			// children, so we need to break out of here.
			if (child.getClass() == ApplicationException.class)
			{
				break;
			}
			parent = child;
		}

		Collections.reverse(causeMessages);
		return causeMessages;

	}

	public String getUniquieID()
	{
		return uniquieID;
	}

	public String getSeverity()
	{
		return severity;
	}

	public boolean isLogged()
	{
		return isLogged;
	}

	public void setLogged(boolean logged)
	{
		isLogged = logged;
		Throwable parent = this;
		Throwable child;

		// Look for nested exceptions.
		while ((child = parent.getCause()) != null)
		{
			if(child instanceof ApplicationException)
			{
				((ApplicationException) child).setLogged(logged);
			}
			else if(child instanceof SystemException)
			{
				((SystemException) child).setLogged(logged);
			}
			parent = child;
		}
	}

	public static String getStackTraceMessage(Throwable throwable)
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		throwable.printStackTrace(printWriter);
		return stringWriter.toString();
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
