/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/SystemException.java,v 1.1 2007-04-03 16:02:37 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.common;

import gov.nih.nci.caadapter.common.util.UUIDGenerator;

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
 * @author LAST UPDATE: $Author: wangeug $
 * @version $Revision: 1.1 $
 * @date $$Date: 2007-04-03 16:02:37 $
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
