/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.common;

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


public class ApplicationMessage extends Object implements java.io.Serializable
{
    private String template="";
    private Object[] args =  new Object[0];

    public ApplicationMessage(String infor)
    {
    	this(infor, null);
    }
    public ApplicationMessage(String template, Object object)
    {
    	this (template, new Object[]{object});
    }
    public ApplicationMessage(String template, Object[] args)
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
            	e.printStackTrace();
//                Log.logWarning(this, "Message Format Problem: Template = '" + template + "', args = " + args);
            }
        }
        return value;
    }
}
