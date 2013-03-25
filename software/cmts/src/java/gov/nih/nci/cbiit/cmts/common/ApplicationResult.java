/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.common;

 
public class ApplicationResult {

	
    private Level level;
    private ApplicationMessage message;

    public ApplicationResult()
    {
    }

    public ApplicationResult(Level level, ApplicationMessage message)
	{
		this(level, message, false);
	}

	public ApplicationResult(Level level, ApplicationMessage message, boolean toLogMessage)
    {
        this.level = level;
        this.message = message;

//        //TODO: Need to re-design: Consolidate the ApplicationException Level with ValidatorResult Level
//        //Only log Fetal, Error and WARNING level now
//        if(toLogMessage && (Level.FATAL == level || Level.ERROR == level))
//        {
//            Log.logError(this, message);
//        }
//        else if (toLogMessage && Level.WARNING == level)
//        {
//            Log.logWarning(this, message);
//        }
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

    public ApplicationMessage getMessage()
    {
        return message;
    }
}
