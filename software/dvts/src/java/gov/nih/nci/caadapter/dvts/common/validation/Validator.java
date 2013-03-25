/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.dvts.common.validation;


/**
 * The base class of validator
 *
 * @author OWNER: Eric Chen  Date: Aug 18, 2005
 * @author LAST UPDATE: $Author: phadkes $
 * 			$Revision: 1.2 $
 * 			$$Date: 2008-06-09 19:53:50 $
 * @since caAdapter v1.2
 */


public abstract class Validator
{
    protected Object toBeValidatedObject = new Object();

	protected Validator()
	{
	}

	public Validator(Object object)
    {
        this.toBeValidatedObject = object;
    }

    public abstract ValidatorResults validate();

    public Object getToBeValidatedObject()
    {
        return toBeValidatedObject;
    }

    public void setToBeValidatedObject(Object toBeValidatedObject)
    {
        this.toBeValidatedObject = toBeValidatedObject;
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
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
 * HISTORY      : Revision 1.8  2005/12/30 22:23:31  chene
 * HISTORY      : Update JavaDoc
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 15:39:07  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/23 19:18:11  chene
 * HISTORY      : First Cut of Validation
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/23 16:07:58  chene
 * HISTORY      : Updated addValidarResults method
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/22 17:18:47  chene
 * HISTORY      : Updated validator package
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/22 16:50:09  jiangsc
 * HISTORY      : Added a no op constructor.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/18 22:47:21  chene
 * HISTORY      : Save Point
 * HISTORY      :
 */
