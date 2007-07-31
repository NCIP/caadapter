/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/validation/ValidatorResults.java,v 1.4 2007-07-31 17:42:25 wangeug Exp $
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

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An aggregation of validator result
 *
 * @author OWNER: Eric Chen  Date: Aug 22, 2005
 * @author LAST UPDATE: $Author: wangeug $
 * @version $Revision: 1.4 $
 * @date $$Date: 2007-07-31 17:42:25 $
 * @since caAdapter v1.2
 */


public class ValidatorResults
{
    private List<Message> fatalResults = new ArrayList<Message>();
    private List<Message> errorResults = new ArrayList<Message>();
    private List<Message> warningResults = new ArrayList<Message>();
    private List<Message> infoResults = new ArrayList<Message>();

    public ValidatorResults()
    {
    }

    /**
     * Add the validator result
     * @param validatorResult
     */
    public void addValidatorResult(ValidatorResult validatorResult)
    {
        if (validatorResult.getLevel() == ValidatorResult.Level.FATAL)
        {
            fatalResults.add(validatorResult.getMessage());
        }
        else if (validatorResult.getLevel() == ValidatorResult.Level.ERROR)
        {
            errorResults.add(validatorResult.getMessage());
        }
        else if (validatorResult.getLevel() == ValidatorResult.Level.WARNING)
        {
            warningResults.add(validatorResult.getMessage());
        }
        else if (validatorResult.getLevel() == ValidatorResult.Level.INFO)
        {
            infoResults.add(validatorResult.getMessage());
        }
        else
        {
            Log.logWarning(this, "Unknown Validation Level" + validatorResult.getLevel());
        }
    }

    /**
     * Add the validator results
     * @param validatorResults
     */
    public void addValidatorResults(ValidatorResults validatorResults)
    {
		if(validatorResults!=null)
		{
			fatalResults.addAll(validatorResults.getMessages(ValidatorResult.Level.FATAL));
			errorResults.addAll(validatorResults.getMessages(ValidatorResult.Level.ERROR));
			warningResults.addAll(validatorResults.getMessages(ValidatorResult.Level.WARNING));
			infoResults.addAll(validatorResults.getMessages(ValidatorResult.Level.INFO));
		}
	}

    /**
     * Is validated or not validated based on there is any fatal and error level result
     * @return whether it is validated or not validated based on there is any fatal and error level result
     */
    public boolean isValid()
    {
        return fatalResults.isEmpty() && errorResults.isEmpty();
    }

    /**
     * Are there any fatal errors?
     * @return if there is any fatal errors.
     */
    public boolean hasFatal()
    {
        return !fatalResults.isEmpty();
    }


    /**
     * Get a list of message based on validation level
     * @param level
     * @return a list of messages based on validation level.
     */
    public List<Message> getMessages (ValidatorResult.Level level)
    {
        if (level == ValidatorResult.Level.FATAL)
        {
            return fatalResults;
        }
        else if (level == ValidatorResult.Level.ERROR)
        {
            return errorResults;
        }
        else if (level == ValidatorResult.Level.WARNING)
        {
            return warningResults;
        }
        else if (level == ValidatorResult.Level.INFO)
        {
            return infoResults;
        }
        else if (level == ValidatorResult.Level.ALL)
        {
        	ArrayList<Message> rtnList =new ArrayList<Message>();
        	rtnList.addAll(fatalResults);
        	rtnList.addAll(errorResults);
        	rtnList.addAll(warningResults);
        	rtnList.addAll(infoResults);
            return rtnList;
        }
        else
        {
            Log.logWarning(this, "Unknown Validation Level" + level);
            return new ArrayList<Message>();
        }
    }

    /**
     * Get all messages ordered by validation level.
     * @return all messages ordered by validation level.
     */
    public List<Message> getAllMessages()
    {
        List<Message> messages = new ArrayList<Message>();
        messages.addAll(fatalResults);
        messages.addAll(errorResults);
        messages.addAll(warningResults);
        messages.addAll(infoResults);
        return messages;
    }

    /**
     *
     * @return a collection of validation levels
     */
    public List<ValidatorResult.Level> getLevels()
    {
        List<ValidatorResult.Level> levels = new ArrayList<ValidatorResult.Level>();
        if (!fatalResults.isEmpty()) levels.add(ValidatorResult.Level.FATAL);
        if (!errorResults.isEmpty()) levels.add(ValidatorResult.Level.ERROR);
        if (!warningResults.isEmpty()) levels.add(ValidatorResult.Level.WARNING);
        if (!infoResults.isEmpty()) levels.add(ValidatorResult.Level.INFO);
        //enable use to see all types of messages 
        if (levels.size()>0)
        	levels.add(0,ValidatorResult.Level.ALL);
        return levels;
    }
    /*
     * Clears all messages in the validatorResult set.
     */
    public void removeAll() {
    	fatalResults.clear();
    	errorResults.clear();
    	warningResults.clear();
    	infoResults.clear();
    }
    /**
     * A list of message ordered by the validation level:  FATAL, ERROR, WARNING, INFO
     * @return String
     */
    public String toString()
    {
        return getSelectedMessage(ValidatorResult.Level.ALL, "\n");
    }

//    private String toString(String carriageReturn)
//    {
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < fatalResults.size(); i++)
//        {
//            sb.append("FATAL: ").append(fatalResults.get(i)).append(carriageReturn);
//        }
//        for (int i = 0; i < errorResults.size(); i++)
//        {
//            sb.append("ERROR: ").append(errorResults.get(i)).append(carriageReturn);
//
//        }
//        for (int i = 0; i < warningResults.size(); i++)
//        {
//            sb.append("WARNING: ").append(warningResults.get(i)).append(carriageReturn);
//
//        }
//        for (int i = 0; i < infoResults.size(); i++)
//        {
//            sb.append("INFO: ").append(infoResults.get(i)).append(carriageReturn);
//
//        }
//        return sb.toString();
//    }

    private String getSelectedMessage(ValidatorResult.Level level,String carriageReturn )
    {
    	StringBuffer sb = new StringBuffer();
    	if (level.equals(ValidatorResult.Level.FATAL)
    			||level.equals(ValidatorResult.Level.ALL))
	        for (int i = 0; i < fatalResults.size(); i++)
	        {
	            sb.append("FATAL: ").append(fatalResults.get(i)).append(carriageReturn);
	        }
    	if (level.equals(ValidatorResult.Level.ERROR)
    			||level.equals(ValidatorResult.Level.ALL))
        for (int i = 0; i < errorResults.size(); i++)
        {
            sb.append("ERROR: ").append(errorResults.get(i)).append(carriageReturn);

        }
    	if (level.equals(ValidatorResult.Level.WARNING)
    			||level.equals(ValidatorResult.Level.ALL))

    	for (int i = 0; i < warningResults.size(); i++)
        {
            sb.append("WARNING: ").append(warningResults.get(i)).append(carriageReturn);

        }
    	
    	if (level.equals(ValidatorResult.Level.INFO)
    			||level.equals(ValidatorResult.Level.ALL))
        for (int i = 0; i < infoResults.size(); i++)
        {
            sb.append("INFO: ").append(infoResults.get(i)).append(carriageReturn);

        }
    	return sb.toString();
    }
    
    public boolean savePrintableFile(String filePath,  ValidatorResult.Level slctdLevel)
    {
       FileWriter fw = null;
       try
        {
          fw = new FileWriter(filePath);
          if (slctdLevel==null)
        	  fw.write(getSelectedMessage(ValidatorResult.Level.ALL, "\r\n"));
          else
        	  fw.write(getSelectedMessage(slctdLevel, "\r\n"));
          fw.close();
        }
      catch(IOException ie)
        {
          return false;
        }
      return true;
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2007/07/27 15:26:33  wuye
 * HISTORY      : Added removeAll method to reset the validatorResult
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/12 14:37:39  wangeug
 * HISTORY      : Add "ALL" as option in the validation message type dropdown so you can see all types of validation messages
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:02:37  wangeug
 * HISTORY      : initial loading of common module
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/08/02 18:44:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/12/29 15:39:07  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/11/15 19:55:13  jiangsc
 * HISTORY      : Added check on nulls.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/11/01 17:27:51  umkis
 * HISTORY      : defect# 172
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/10/06 20:58:05  giordanm
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/10/05 17:23:46  giordanm
 * HISTORY      : CSV validation work.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/09/19 17:20:37  giordanm
 * HISTORY      : update castor beans, dbparser and schema based on a change to the XML structure.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/09/02 19:10:49  giordanm
 * HISTORY      : SCM validation work.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/31 16:52:32  chene
 * HISTORY      : Saved point
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/26 15:34:39  chene
 * HISTORY      : Add getLevels method
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/26 14:53:25  chene
 * HISTORY      : Add isValidated method into ValidatorResults
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/23 19:18:11  chene
 * HISTORY      : First Cut of Validation
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/23 16:07:59  chene
 * HISTORY      : Updated addValidarResults method
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/23 15:29:11  chene
 * HISTORY      : Updated addValidarResults method
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/22 17:18:47  chene
 * HISTORY      : Updated validator package
 * HISTORY      :
 */
