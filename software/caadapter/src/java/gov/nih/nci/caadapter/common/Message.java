/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common;




import java.text.MessageFormat;

/**
 * A text message which could take parameter
 *
 * @author OWNER: Eric Chen  Date: Aug 18, 2005
 * @author LAST UPDATE: $Author: linc $
 * @version $Revision: 1.3 $
 * @date $$Date: 2008-06-26 19:45:50 $
 * @since caAdapter v1.2
 */


public class Message extends Object implements java.io.Serializable
{
    private String template="";
    private Object[] args =  new Object[0];

    public Message(String template, Object[] args)
    {
        this.template = template;
        this.args = args;
		if(this.args==null)
		{
			this.args = new Object[0];
		}
	}

    public String toString()
    {
        String value = template;
        if (args.length > 0)
        {
            try
            {
                value = MessageFormat.format(template, args);
            }
            catch (Exception e)
            {
                Log.logWarning(this, "Message Format Problem: Template = '" + template + "', args = " + args);
            }
        }
        return value;
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2008/06/09 19:53:49  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:02:37  wangeug
 * HISTORY      : initial loading of common module
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/08/02 18:44:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/05 18:01:58  chene
 * HISTORY      : Update with Exception handling
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/26 14:53:24  chene
 * HISTORY      : Add isValidated method into ValidatorResults
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/22 18:57:59  jiangsc
 * HISTORY      : Enhanced args to never be null.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/22 17:18:47  chene
 * HISTORY      : Updated validator package
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/18 22:47:20  chene
 * HISTORY      : Save Point
 * HISTORY      :
 */
