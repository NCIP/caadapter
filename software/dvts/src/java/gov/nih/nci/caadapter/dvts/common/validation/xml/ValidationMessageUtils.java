/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.dvts.common.validation.xml;

import gov.nih.nci.caadapter.dvts.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.dvts.common.validation.ValidatorResults;

/**
 * This class defines common utility methods to facilitate validation related handling.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-06-09 19:53:50 $
 */
public class ValidationMessageUtils
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: ValidationMessageUtils.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/validation/ValidationMessageUtils.java,v 1.4 2008-06-09 19:53:50 phadkes Exp $";

	/**
	 * Never can be instantiated
	 */
	private ValidationMessageUtils()
	{
	}

	/**
	 * Return a string representing the total number of messages on each level, in the format of "a and b" or "a, b, and c".
	 *
	 * @param results
	 * @return the string representing the message.
	 */
	public static final String generateStatMessage(ValidatorResults results)
	{
		if (results == null || results.getLevels().size() == 0)
		{
			return "no validation message";
		}

		//since the getLevels() methods only return levels with more than 0 messages, following code will assume this logic.
		java.util.List levelList = results.getLevels();
		StringBuffer buf = new StringBuffer();
		int size = levelList.size();
		for (int i = 0; i < size; i++)
		{
			ValidatorResult.Level level = (ValidatorResult.Level) levelList.get(i);
			java.util.List msgList = results.getMessages(level);
			int len = msgList == null ? 0 : msgList.size();

			buf.append(len + " " );
			if (level.equals(ValidatorResult.Level.ALL))
			{
				if (len>1)
					buf.append("messages: ");
				else
					buf.append("message: ");
			}
			else
			{
				buf.append(level);
				if (len>1)
					buf.append("s");
			}

			if ((i!=0)&&(i!=(size-1)))
			{
				buf.append(", ");
			}
		}

		return buf.toString();
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2007/07/31 17:43:05  wangeug
 * HISTORY      : resolve issues with preliminary test of release 4.0
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/12 16:07:54  wangeug
 * HISTORY      : Add "ALL" as option in the validation message type dropdown so you can see all types of validation messages
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/03 18:23:11  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.5  2006/08/02 18:44:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.4  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.3  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/12/19 16:37:16  jiangsc
 * HISTORY      : Created dumpAllValidatorResultsToFile() function to temporarily dump validator results to files.
 * HISTORY      :
 */
