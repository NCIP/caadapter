/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/BaseObjectImpl.java,v 1.4 2007-08-07 20:49:57 schroedn Exp $
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

import gov.nih.nci.caadapter.common.util.UUIDGenerator;

/**
 * The default implementation for the base class for all meta and data objects.
 *
 * @author OWNER: Eric Chen  Date: Jun 3, 2005
 * @author LAST UPDATE: $Author: schroedn $
 * @version $Revision: 1.4 $
 * @date $$Date: 2007-08-07 20:49:57 $
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
	public boolean equals(Object o)
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
	public int hashCode()
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
