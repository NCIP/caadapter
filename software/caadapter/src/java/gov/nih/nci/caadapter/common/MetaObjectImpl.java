/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common;

import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.util.PropertiesProvider;
import gov.nih.nci.caadapter.common.util.PropertiesResult;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all meta objects (CVS meta, HL7 V3 meta, etc).
 *
 * @author OWNER: Eric Chen
 * @author LAST UPDATE: $Author: phadkes $
 * @version $Revision: 1.6 $
 * @date $Date: 2008-06-09 19:53:49 $
 * @since caAdapter v1.2
 */

public class MetaObjectImpl extends BaseObjectImpl implements MetaObject, PropertiesProvider
{
	protected String name;
	protected String XPath;
	protected boolean isMapped;

    protected MetaObjectImpl()
    {
    }

    protected MetaObjectImpl(String name)
    {
        this.name = name;
    }

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Provides basic implementation of equals().
	 * Descendant classes shall consider overiding this implementation.
	 *
	 * @param o
	 * @return if this equals to the given objec.t
	 */
	public boolean equals_donotoverride(Object o)
	{
		/**
		 * Implementation Rationale:
		 * 0) compare if this and given o is "==";
		 * 1) compare if class is equal;
		 * 2) compare if name is equal;
		 */
		if (!(super.equals(o))) return false;
		final MetaObjectImpl metaObject = (MetaObjectImpl) o;
		if (name != null ? !name.equals(metaObject.name) : metaObject.name != null) return false;
		return true;
	}

	/**
	 * Provides basic implementation of equals().
	 * Descendant classes shall consider overiding this implementation.
	 *
	 * @return hash code value.
	 */
	public int hashCode_donotoverride()
	{
		/**
		 * Implementation Rationale:
		 * 1) get super's hashCode;
		 * 2) get name's hashCode;
		 */
		int result = super.hashCode();
		result = result * 31 + (name != null ? name.hashCode() : 0);
		return result;
	}

	/**
	 * Overriding the basic implementation of toString() to return name value.
	 *
	 * @return the string representation of this object.
	 */
	public String toString()
	{
		return getName();
	}

	/**
	 * Will clone name value.
	 * @param copyUUID if true, the cloned object will share the same uuid value of this object; otherwise, it will have different UUID value.
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public Object clone(boolean copyUUID) throws CloneNotSupportedException
	{
		MetaObjectImpl metaObj = (MetaObjectImpl) super.clone(copyUUID);
		metaObj.setName(this.getName());
		return metaObj;
	}

	public String getClassName()
	{
		return GeneralUtilities.getClassName(this.getClass(), false);
	}


	/**
	 * Return the title of this provider that may be used to distinguish from others.
	 *
	 * @return the title value of this object for properties display
	 */
	public String getTitle()
	{
		return "Meta Object Properties";
	}

	/**
	 * This functions will return an array of PropertyDescriptor that would
	 * help reflection and/or introspection to figure out what information would be
	 * presented to the user.
	 * <p/>
	 * descendant classes are free to override to provide additional information.
	 */
	public PropertiesResult getPropertyDescriptors() throws Exception
	{
		Class beanClass = this.getClass();

		PropertyDescriptor _name = new PropertyDescriptor("Name", beanClass, "getName", null);
		PropertyDescriptor _class = new PropertyDescriptor("Type", beanClass, "getClassName", null);
//		PropertyDescriptor[] propertiesArray = new PropertyDescriptor[]
// 			{ _name, _class };
		List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>();
		propList.add(_name);
		propList.add(_class);
		PropertiesResult result = new PropertiesResult();
		result.addPropertyDescriptors(this, propList);
		return result;
	}

	public String getXPath() {
		return XPath;
	}

	public void setXPath(String path) {
		XPath = path;
	}

	public boolean isMapped() {
		return isMapped;
	}

	public void setMapped(boolean isMapped) {
		this.isMapped = isMapped;
	}
	/**
	 * Override method
	 * return xPath as xmlPath
	 */
//	public String getXmlPath()
//	{
//        String xpath = this.getXPath();
//        if (xpath == null) return super.getXmlPath();
//        return this.getXPath();
//	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.5  2008/04/01 21:52:54  umkis
 * HISTORY      : minor change
 * HISTORY      :
 * HISTORY      : Revision 1.4  2008/04/01 21:04:09  umkis
 * HISTORY      : delete getXmlPath() for preventing from conflict with same method name of its super class.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/12/06 20:39:33  wangeug
 * HISTORY      : set xPath as xmlPath
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/10/09 20:14:56  jayannah
 * HISTORY      : The overiding of equals and hashcode method was causing problems during times when the duplicate field were entered by the user.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:02:37  wangeug
 * HISTORY      : initial loading of common module
 * HISTORY      :
 * HISTORY      : Revision 1.20  2006/11/14 15:27:31  wuye
 * HISTORY      : added is mapped funcation for validation
 * HISTORY      :
 * HISTORY      : Revision 1.19  2006/10/23 16:21:47  wuye
 * HISTORY      : add getXPath method.
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/08/02 18:44:20  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/01/03 19:16:51  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/01/03 18:27:13  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/12/28 21:50:46  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/10/06 19:35:07  giordanm
 * HISTORY      : prettying the code in the common package.  javadoc, license headers, etc.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/09/15 20:56:57  chene
 * HISTORY      : Database prototype
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/08/23 18:57:14  jiangsc
 * HISTORY      : Implemented the new Properties structure
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/08/17 18:44:14  jiangsc
 * HISTORY      : Added some comments
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/08/04 18:05:02  jiangsc
 * HISTORY      : Refactorized clone() methods to have explicit clone(boolean copyUUID)
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/07/27 19:55:11  jiangsc
 * HISTORY      : Minor update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/07/18 22:18:27  jiangsc
 * HISTORY      : First implementation of the Log functions.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/07/18 19:46:43  jiangsc
 * HISTORY      : Added textual display for functions and properties.
 * HISTORY      : Beautified port display.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/07/11 18:18:01  jiangsc
 * HISTORY      : Partially implemented property pane.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/06/23 14:30:03  jiangsc
 * HISTORY      : Updated CSVPanel implementation to support basic drag and drop.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/06/08 18:33:50  jiangsc
 * HISTORY      : Offload equals, hashCode, toString methods to BaseObjectImpl and override it accordingly to include name.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/06/06 15:43:46  jiangsc
 * HISTORY      : Add basic implementation of toString(), equals(), and hashCode().
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/06/03 18:09:15  chene
 * HISTORY      : Added Clone Meta Stuff
 * HISTORY      :
 */
