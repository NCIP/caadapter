/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





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
 * @author LAST UPDATE: $Author: linc $
 * @version $Revision: 1.8 $
 * @date $$Date: 2008-06-26 19:45:50 $
 * @since caAdapter v1.2
 */


public class ValidatorResults extends Object implements java.io.Serializable
{
    private List<ValidatorResult> fatalResults = new ArrayList<ValidatorResult>();
    private List<ValidatorResult> errorResults = new ArrayList<ValidatorResult>();
    private List<ValidatorResult> warningResults = new ArrayList<ValidatorResult>();
    private List<ValidatorResult> infoResults = new ArrayList<ValidatorResult>();

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
            fatalResults.add(validatorResult);
        }
        else if (validatorResult.getLevel() == ValidatorResult.Level.ERROR)
        {
            errorResults.add(validatorResult);
        }
        else if (validatorResult.getLevel() == ValidatorResult.Level.WARNING)
        {
            warningResults.add(validatorResult);
        }
        else if (validatorResult.getLevel() == ValidatorResult.Level.INFO)
        {
            infoResults.add(validatorResult);
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
			fatalResults.addAll(validatorResults.getValidationResult(ValidatorResult.Level.FATAL));
			errorResults.addAll(validatorResults.getValidationResult(ValidatorResult.Level.ERROR));
			warningResults.addAll(validatorResults.getValidationResult(ValidatorResult.Level.WARNING));
			infoResults.addAll(validatorResults.getValidationResult(ValidatorResult.Level.INFO));
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
        List<ValidatorResult> results= getValidationResult(level);
        ArrayList<Message> rtnList=new ArrayList<Message>();
        for(ValidatorResult rslt:results)
        {
        	rtnList.add(rslt.getMessage());
        }
        return rtnList;
    }
    public List <ValidatorResult>  getValidationResult(ValidatorResult.Level level)
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
        	ArrayList<ValidatorResult> rtnList =new ArrayList<ValidatorResult>();
        	rtnList.addAll(fatalResults);
        	rtnList.addAll(errorResults);
        	rtnList.addAll(warningResults);
        	rtnList.addAll(infoResults);
        	return rtnList;
        }
        else
        {
            Log.logWarning(this, "Unknown Validation Level" + level);
            return new ArrayList<ValidatorResult>();
        }

    }
    /**
     * Get all messages ordered by validation level.
     * @return all messages ordered by validation level.
     */
    public List<Message> getAllMessages()
    {

        return this.getMessages(ValidatorResult.Level.ALL);
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
	            sb.append("FATAL: ").append(fatalResults.get(i).getMessage()).append(carriageReturn);
	        }
    	if (level.equals(ValidatorResult.Level.ERROR)
    			||level.equals(ValidatorResult.Level.ALL))
        for (int i = 0; i < errorResults.size(); i++)
        {
            sb.append("ERROR: ").append(errorResults.get(i).getMessage()).append(carriageReturn);

        }
    	if (level.equals(ValidatorResult.Level.WARNING)
    			||level.equals(ValidatorResult.Level.ALL))

    	for (int i = 0; i < warningResults.size(); i++)
        {
            sb.append("WARNING: ").append(warningResults.get(i).getMessage()).append(carriageReturn);

        }

    	if (level.equals(ValidatorResult.Level.INFO)
    			||level.equals(ValidatorResult.Level.ALL))
        for (int i = 0; i < infoResults.size(); i++)
        {
            sb.append("INFO: ").append(infoResults.get(i).getMessage()).append(carriageReturn);

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
 * HISTORY      : Revision 1.7  2008/06/09 19:53:50  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2007/07/31 20:05:41  wangeug
 * HISTORY      : display validation result with level and message text
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/07/31 18:41:05  wangeug
 * HISTORY      : list validation result with level and message text
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/07/31 17:42:25  wangeug
 * HISTORY      : resolve issues with preliminary test of release 4.0
 * HISTORY      :
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
