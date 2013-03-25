/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.common.validation;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;


/**
 * A validation result for an atomic validation event. It includes the validation level and it's related message <br>
 *
 * There are total four levels:<br>
 * FATAL = This is an unrecoverable event which halts the normal business flow, it prevents the user
 *         from proceeding until this issue is resolved.<br>
 * ERROR = This is an recoverable event which impedes the normal business flow, preventing the user from
 *          proceeding not now, but later and therefore should be resolved eventually.<br>
 * WARNING = This is an recoverable event which an inconsistency has been recognized.  It may or may not be an
 *          issue but could lead to unpredictable behavior.<br>
 * INFO = This is an recoverable event which a contextual description of a business practice or method is disclosed
 *
 *
 * @author OWNER: Eric Chen  Date: Aug 18, 2005
 * @author LAST UPDATE: $Author: linc $
 * @version $Revision: 1.4 $
 * @date $$Date: 2008-06-26 19:45:50 $
 * @since caAdapter v1.2
 */


public class ValidatorResult extends Object implements java.io.Serializable
{

    private Level level;
    private Message message;

    public ValidatorResult()
    {
    }

    public ValidatorResult(Level level, Message message)
	{
		this(level, message, false);
	}

	public ValidatorResult(Level level, Message message, boolean toLogMessage)
    {
        this.level = level;
        this.message = message;

        //TODO: Need to re-design: Consolidate the ApplicationException Level with ValidatorResult Level
        //Only log Fetal, Error and WARNING level now
        if(toLogMessage && (Level.FATAL == level || Level.ERROR == level))
        {
            Log.logError(this, message);
        }
        else if (toLogMessage && Level.WARNING == level)
        {
            Log.logWarning(this, message);
        }
    }

    public static enum Level
    {
        FATAL,
        ERROR,
        WARNING,
        INFO,
        ALL
    }

    public Level getLevel()
    {
        return level;
    }

    public Message getMessage()
    {
        return message;
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2008/06/09 19:53:50  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/12 14:37:49  wangeug
 * HISTORY      : Add "ALL" as option in the validation message type dropdown so you can see all types of validation messages
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:02:37  wangeug
 * HISTORY      : initial loading of common module
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/08/02 18:44:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/05/03 19:46:42  chene
 * HISTORY      : Correct the Spelling Error
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/04 23:36:00  chene
 * HISTORY      : Support structure is mappable if we can not find the default value from HMD
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/11/03 22:37:19  jiangsc
 * HISTORY      : Enhanced logging and logging configuration.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/25 21:48:42  chene
 * HISTORY      : Kludge Fix to add a log at ValidatorResult constructor
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/26 14:53:25  chene
 * HISTORY      : Add isValidated method into ValidatorResults
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/23 16:07:59  chene
 * HISTORY      : Updated addValidarResults method
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/22 17:18:47  chene
 * HISTORY      : Updated validator package
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/18 22:47:20  chene
 * HISTORY      : Save Point
 * HISTORY      :
 */
