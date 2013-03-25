/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common;




import gov.nih.nci.caadapter.common.util.GeneralUtilities;

import java.lang.reflect.Array;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The wrapper of logging class.
 *
 * @author OWNER: Eric Chen, Scott Jiang Date: Jul 18, 2005
 * @author LAST UPDATE: $Author: wangeug $
 * @version $Revision: 1.5 $
 * @date $$Date: 2009-09-30 17:10:24 $
 * @since caAdapter v1.2
 */

public class Log
{
	public static final String MAP_LOG = "map";

	/**
	 * Call this function to log a serious exception.
	 *
	 * @param sender  java.lang.Object sender of this log action, such as String, Class, or Object
	 * @param message
	 */
	public static final void logError(Object sender, Object message)
	{
		if (message instanceof Throwable)
		{
			Logger.getLogger(getLogName(sender)).log(Level.SEVERE, getPrintOut(sender, message), (Throwable) message);
		}
		else
		{
			Logger.getLogger(getLogName(sender)).log(Level.SEVERE, getPrintOut(sender, message));
		}
	}

	/**
	 * Call this function to log a warning.
	 *
	 * @param sender  java.lang.Object sender of this log action, such as String, Class, or Object
	 * @param message
	 */
	public static final void logWarning(Object sender, Object message)
	{
		if (message instanceof Throwable)
		{
			Logger.getLogger(getLogName(sender)).log(Level.WARNING, getPrintOut(sender, message), (Throwable) message);
		}
		else
		{
			Logger.getLogger(getLogName(sender)).log(Level.WARNING, getPrintOut(sender, message));
		}
	}

	/**
	 * Call this function to log an information only message.
	 *
	 * @param sender  java.lang.Object sender of this log action, such as String, Class, or Object
	 * @param message
	 */
	public static final void logInfo(Object sender, Object message)
	{
		if (message instanceof Throwable)
		{
			Logger.getLogger(getLogName(sender)).log(Level.INFO, getPrintOut(sender, message), (Throwable) message);
		}
		else
		{
			Logger.getLogger(getLogName(sender)).log(Level.INFO, getPrintOut(sender, message));
		}
	}

	/**
	 * Call this function to log a debug
	 *
	 * @param sender  java.lang.Object sender of this log action, such as String, Class, or Object
	 * @param message
	 */
	public static final void logDebug(Object sender, Object message)
	{
		if (message instanceof Throwable)
		{
			Logger.getLogger(getLogName(sender)).log(Level.FINE, getPrintOut(sender, message), (Throwable) message);
		}
		else
		{
			Logger.getLogger(getLogName(sender)).log(Level.FINE, getPrintOut(sender, message));
		}
	}


	/**
	 * Call this function to log an Exception
	 *
	 * @param sender java.lang.Object sender of this log action, such as String, Class, or Object
	 * @param t	  Throwable
	 */
	public static final void logException(Object sender, Throwable t)
	{
		logException(sender, null, t);
	}

	/**
	 * Call this function to log an Exception
	 *
	 * @param sender  java.lang.Object sender of this log action, such as String, Class, or Object
	 * @param message
	 * @param t	   Throwable
	 */
	public static final void logException(Object sender, String message, Throwable t)
	{
		Level level = null;
		if (t instanceof ApplicationException)
		{
			ApplicationException local = (ApplicationException) t;
			if (GeneralUtilities.areEqual(ApplicationException.SEVERITY_LEVEL_ERROR, local.getSeverity()))
			{
				level = Level.SEVERE;
			}
			else
			{
				level = Level.WARNING;
			}
		}
		else if (t instanceof SystemException)
		{
			level = Level.SEVERE;
		}
		else
		{
			level = Level.WARNING;
			if (t == null)
			{
				t = new Exception("null, no message");
			}
		}
		privateLog(level, getLogName(sender), message, t);

	}


	private static final void privateLog(Level level, String loggerName, String message, Throwable t)
	{
		String msg = null;
		if (t instanceof ApplicationException)
		{
			ApplicationException local = (ApplicationException) t;
			if (!local.isLogged())
			{//log only once.
				msg = local.getDetailedMessage();
				local.setLogged(true);
			}
		}
		else if (t instanceof SystemException)
		{
			SystemException local = (SystemException) t;
			if (!local.isLogged())
			{//log only once.
				msg = local.getMessage();
				local.setLogged(true);
			}
		}
		else
		{
			msg = t.getMessage();
		}

		msg = message == null ? msg : "[" + message + "] " + msg;
		Logger.getLogger(loggerName).log(level, msg, t);
	}

	/**
	 * Creates a string representation of the object and all it's components
	 *
	 * @param sender the sender of this log request.
	 * @param object Object the specific object to turn into a String
	 * @return String representing the full object
	 */
	private static String getPrintOut(Object sender, Object object)
	{
		StringBuffer buf = new StringBuffer();
		if(sender==null)
		{
			buf.append("Non-retraceable sender: ");
		}
//		else
//		{
//			buf.append("Sender type ('" + sender.getClass().getName() + "'): ");
//		}
		if (object == null)
		{
			buf.append("message is 'null'.");
		}
		else
		{
			StringBuffer sb = new StringBuffer();
			if (object.getClass().isArray())
			{
				for (int i = 0; i < Array.getLength(object); i++)
				{
//					sb.append((Array.get(object, i)).toString() + " \n");
					sb.append(getSingleObjectMessage(Array.get(object, i)) + " \n");
				}
				buf.append(sb.toString());
			}
			else
			{
				buf.append(getSingleObjectMessage(object));
			}
		}
		return buf.toString();
	}

	private static String getSingleObjectMessage(Object object)
	{
		StringBuffer buf = new StringBuffer();
		if (!(object instanceof Throwable))
		{
			LogCallerFinder finder = new LogCallerFinder(Log.class);
			buf.append(finder.getStackTraceInformation() + "\n");
		}
		buf.append(object.toString());
		return buf.toString();
	}

	/**
	 * If the passed in Object is a string it returns the String value, if not, it gets
	 * the class name out of the sender.
	 *
	 * @param sender Object the sender containing the desired class name
	 * @return String representing the class name
	 */
	protected static String getLogName(Object sender)
	{
		if (sender == null)
		{
			return "null";
		}
		String name;
		if (sender instanceof String)
		{
			name = ((String) sender);
		}
		else if (sender instanceof Class)
		{
			name = ((Class) sender).getName();
		}
		else
		{
			name = sender.getClass().getName();
		}
		return name;
	}


}

/**
 * This defines a package level class to help the Log class above figure out the the source class and method name,
 * similarly as what the LogRecord class does, but more generically.
 *
 * Could consider to generate the full stack trace out og this class.
 */
class LogCallerFinder
{
	/**
	 * @serial Class that issued logging call
	 */
	private String sourceClassName;

	/**
	 * @serial Method that issued logging call
	 */
	private String sourceMethodName;

	private String stackTraceInformation;

	private transient boolean needToInferCaller;

	private Class boundryClass;

	public LogCallerFinder(Class boundryClass)
	{
		this.boundryClass = boundryClass;
		needToInferCaller = true;
	}

	/**
	 * Get the  name of the class that (allegedly) issued the logging request.
	 * <p/>
	 * Note that this sourceClassName is not verified and may be spoofed.
	 * This information may either have been provided as part of the
	 * logging call, or it may have been inferred automatically by the
	 * logging framework.  In the latter case, the information may only
	 * be approximate and may in fact describe an earlier call on the
	 * stack frame.
	 * <p/>
	 * May be null if no information could be obtained.
	 *
	 * @return the source class name
	 */
	public String getSourceClassName()
	{
		if (needToInferCaller)
		{
			inferCaller();
		}
		return sourceClassName;
	}

	/**
	 * Set the name of the class that (allegedly) issued the logging request.
	 *
	 * @param sourceClassName the source class name (may be null)
	 */
	public void setSourceClassName(String sourceClassName)
	{
		this.sourceClassName = sourceClassName;
		needToInferCaller = false;
	}

	/**
	 * Get the  name of the method that (allegedly) issued the logging request.
	 * <p/>
	 * Note that this sourceMethodName is not verified and may be spoofed.
	 * This information may either have been provided as part of the
	 * logging call, or it may have been inferred automatically by the
	 * logging framework.  In the latter case, the information may only
	 * be approximate and may in fact describe an earlier call on the
	 * stack frame.
	 * <p/>
	 * May be null if no information could be obtained.
	 *
	 * @return the source method name
	 */
	public String getSourceMethodName()
	{
		if (needToInferCaller)
		{
			inferCaller();
		}
		return sourceMethodName;
	}

	/**
	 * Set the name of the method that (allegedly) issued the logging request.
	 *
	 * @param sourceMethodName the source method name (may be null)
	 */
	public void setSourceMethodName(String sourceMethodName)
	{
		this.sourceMethodName = sourceMethodName;
		needToInferCaller = false;
	}

	public String getStackTraceInformation()
	{
		if (needToInferCaller)
		{
			inferCaller();
		}
		return stackTraceInformation;
	}

	public void setStackTraceInformation(String stackTraceInformation)
	{
		this.stackTraceInformation = stackTraceInformation;
		needToInferCaller = false;
	}

	/**
	 * Private method to infer the caller's class and method names.
	 * This method is copied from LogRecord class with some modifications.
	 */
	private void inferCaller()
	{
		needToInferCaller = false;
		// Get the stack trace.
		StackTraceElement stack[] = (new Throwable()).getStackTrace();
		// First, search back to a method in the Logger class.
		int ix = 0;
		while (ix < stack.length)
		{
			StackTraceElement frame = stack[ix];
			String cname = frame.getClassName();
			if (cname.equals(boundryClass.getName()))
			{
				break;
			}
			ix++;
		}
		// Now search for the first frame before the "Logger" class.
		while (ix < stack.length)
		{
			StackTraceElement frame = stack[ix];
			String cname = frame.getClassName();
			if (!cname.equals(boundryClass.getName()))
			{
				// We've found the relevant frame.
				setSourceClassName(cname);
				setSourceMethodName(frame.getMethodName());
				setStackTraceInformation(frame.toString());
				return;
			}
			ix++;
		}
		// We haven't found a suitable frame, so just punt.  This is
		// OK as we are only committed to making a "best effort" here.
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.4  2009/09/29 17:40:00  wangeug
 * HISTORY      : exclude valueDomain from mapping panel view
 * HISTORY      :
 * HISTORY      : Revision 1.3  2008/06/09 19:53:49  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/09/11 19:02:12  wangeug
 * HISTORY      : create log file if not exist
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:02:37  wangeug
 * HISTORY      : initial loading of common module
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/08/02 18:44:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/03 22:37:19  jiangsc
 * HISTORY      : Enhanced logging and logging configuration.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/04 19:32:54  jiangsc
 * HISTORY      : Updated the logException method not to "eat" the original exception.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/07/21 21:35:33  chene
 * HISTORY      : replace log name to object log sender
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/21 20:50:23  chene
 * HISTORY      : replace log name to object log sender
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/21 17:23:42  chene
 * HISTORY      : Switch logMessage and logException implementation
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/20 21:25:45  chene
 * HISTORY      : Add logDebug method
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/18 22:13:34  jiangsc
 * HISTORY      : First implementation of the Log functions.
 * HISTORY      :
 * HISTORY      :
 */
