/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/MetaObjectImpl.java,v 1.2 2007-10-09 20:14:56 jayannah Exp $
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
 * @author LAST UPDATE: $Author: jayannah $
 * @version $Revision: 1.2 $
 * @date $Date: 2007-10-09 20:14:56 $
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
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
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
