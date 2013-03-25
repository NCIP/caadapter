/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common;

import gov.nih.nci.caadapter.common.util.UUIDGenerator;

/**
 * The default implementation for the base class for all meta and data objects.
 *
 * @author OWNER: Eric Chen  Date: Jun 3, 2005
 * @author LAST UPDATE: $Author: phadkes $
 * @version $Revision: 1.6 $
 * @date $$Date: 2008-06-09 19:53:49 $
 * @since caAdapter v1.2
 */

public class BaseObjectImpl implements BaseObject
{
	protected String xmlPath;

	/**
	 * Provides basic implementation of equals().
	 * Descendant classes shall consider overiding this implementation.
	 *
	 * @param o
	 * @return
	 */
	public boolean equals_donotoverride(Object o)
	{
		/**
		 * Implementation Rationale:
		 * 0) compare if this and given o is "==";
		 * 1) compare if class is equal;
		 * 2) compare if name is equal;
		 */
		if (this == o) return true;
		if (!(o instanceof BaseObjectImpl)) return false;
		final BaseObjectImpl baseObject = (BaseObjectImpl) o;
		String thisClass = getClass().getName();
		String thatClass = o.getClass().getName();

		if (!thisClass.equals(thatClass)) return false;
		if (getXmlPath() != null ? !getXmlPath().equals(baseObject.getXmlPath()) : baseObject.getXmlPath() != null) return false;

		return true;
	}

	/**
	 * Provides basic implementation of equals().
	 * Descendant classes shall consider overiding this implementation.
	 *
	 * @return
	 */
	public int hashCode_donotoverride()
	{
		/**
		 * Implementation Rationale:
		 * 1) get class.getName()'s hashCode;
		 * 2) get UUID's hashCode;
		 */
		int result = getClass().getName().hashCode();
		result = result * 31 + getXmlPath().hashCode();
		return result;
	}

	public String toString()
	{
		return this.getXmlPath();//getUUID();
	}

	/**
	 * This function will return a clone of this object following the general clone() requirement.
	 * The difference of this object is to explicit require caller to specify whether to expect a
	 * clone object with the same uuid of this object.
	 *
	 * In other words, if copyUUID is true,
	 * the returned clone object will satisfy clone.equals(thisObject) to true;
	 * otherwise,
	 * the returned clone object will have different uuid value of this object and
	 * clone!=thisObject is true, clone.getClass()==thisObject.getClass() is true,
	 * but clone.equals(thisObject) will always return false;
	 * @param copyUUID
	 * @return
	 * @throws CloneNotSupportedException
	 */
	protected Object clone(boolean copyUUID)  throws CloneNotSupportedException
	{
		BaseObjectImpl metaObj = (BaseObjectImpl) super.clone();
		if(!copyUUID)
		{
			metaObj.setXmlPath(null);
		}
		return metaObj;
	}

	/**
	 * Cloneable, will clone UUID value.
	 * @return
	 * @throws CloneNotSupportedException
	 */
	protected Object clone() throws CloneNotSupportedException
	{
		return clone(true);
	}

	public String getXmlPath() {
		// TODO Auto-generated method stub
		if (xmlPath == null)
		{
			//System.out.println("BaseObjectImpl.getXmlPath()..."+xmlPath);
			xmlPath = UUIDGenerator.getUniqueString();
		}
		return xmlPath;
	}

	public void setXmlPath(String newPath) {
		xmlPath=newPath;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.5  2007/10/09 20:14:55  jayannah
 * HISTORY      : The overiding of equals and hashcode method was causing problems during times when the duplicate field were entered by the user.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/08/07 20:49:57  schroedn
 * HISTORY      : New Feature, Primary Key and Lazy/Eager functions added to MMS
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/07/16 18:03:46  wangeug
 * HISTORY      : change UIUID to xmlPath
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/05/24 15:00:13  wangeug
 * HISTORY      : add xmlPath attribute
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:02:37  wangeug
 * HISTORY      : initial loading of common module
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/08/02 18:44:20  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 19:16:51  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 18:27:13  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/28 21:50:46  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/10/06 19:35:08  giordanm
 * HISTORY      : prettying the code in the common package.  javadoc, license headers, etc.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/30 22:34:34  chene
 * HISTORY      : Add clone support
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/04 18:05:03  jiangsc
 * HISTORY      : Refactorized clone() methods to have explicit clone(boolean copyUUID)
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/07/18 22:18:25  jiangsc
 * HISTORY      : First implementation of the Log functions.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/06/23 14:30:03  jiangsc
 * HISTORY      : Updated CSVPanel implementation to support basic drag and drop.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/06/08 20:09:40  giordanm
 * HISTORY      : lazily instanciate UUID
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/06/08 18:31:34  jiangsc
 * HISTORY      : Updated to use class name vs class object.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/06/08 18:28:40  jiangsc
 * HISTORY      : Added equals(), hashCode(), and toString() methods.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/06/03 16:46:21  chene
 * HISTORY      : Added BaseObject Impl
 * HISTORY      :
 */
