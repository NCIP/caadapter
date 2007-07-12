/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/validation/ValidatorResult.java,v 1.2 2007-07-12 14:37:49 wangeug Exp $
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
 * @author LAST UPDATE: $Author: wangeug $
 * @version $Revision: 1.2 $
 * @date $$Date: 2007-07-12 14:37:49 $
 * @since caAdapter v1.2
 */


public class ValidatorResult
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
