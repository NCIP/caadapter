/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/util/PropertiesResult.java,v 1.1 2007-04-03 16:02:37 wangeug Exp $
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


package gov.nih.nci.caadapter.common.util;

import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * This class defines a compound class to associate a target object with its PropertyDescriptors.
 * So that caller of this class, such as gov.nih.nci.caadapter.ui.main.properties.DefaultPropertiesTableModel could
 * utilize the properties descriptor directly against the target object.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:02:37 $
 */
public class PropertiesResult
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: PropertiesResult.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/util/PropertiesResult.java,v 1.1 2007-04-03 16:02:37 wangeug Exp $";

	private Map<Object, List<PropertyDescriptor>> targetObjectToPropertyDescriptorMap;
	private Map<PropertyDescriptor, Object> propertyDescriptorToTargetObjectMap;

	public PropertiesResult()
	{
		//perserve the sequence
		targetObjectToPropertyDescriptorMap = new LinkedHashMap<Object, List<PropertyDescriptor>>();
		propertyDescriptorToTargetObjectMap = new LinkedHashMap<PropertyDescriptor, Object>();
	}

	/**
	 * Add the list of property descriptors the list that already associates with targetObject.
	 * Use the setPropertyDescriptors() instead, if you'd like to complete replace the existing list.
	 * @param targetObject
	 * @param propertyDescriptorList
	 */
	public void addPropertyDescriptors(Object targetObject, List propertyDescriptorList)
	{
		if(targetObject==null || propertyDescriptorList==null)
		{
			return;
		}
		List<PropertyDescriptor> currentList = targetObjectToPropertyDescriptorMap.get(targetObject);
		if(currentList==null)
		{
			currentList = new ArrayList<PropertyDescriptor>(propertyDescriptorList);
			targetObjectToPropertyDescriptorMap.put(targetObject, currentList);
		}
		else
		{
			currentList.addAll(propertyDescriptorList);
		}
		registerPropertyDescriptorToTargetObject(propertyDescriptorList, targetObject);
	}

	private void registerPropertyDescriptorToTargetObject(List<PropertyDescriptor> propList, Object targetObject)
	{
		int size = propList.size();
		for(int i=0; i<size; i++)
		{
			propertyDescriptorToTargetObjectMap.put(propList.get(i), targetObject);
		}
	}

	private void removePropertyDescriptorToTargetObject(List<PropertyDescriptor> propList)
	{
		int size = propList.size();
		for(int i=0; i<size; i++)
		{
			propertyDescriptorToTargetObjectMap.remove(propList.get(i));
		}
	}

	/**
	 * Set all PropertyDescriptors in the given list as the latest registry of the given target Object.
	 * @param targetObject
	 * @param propertyDescriptorList
	 */
	public void setPropertyDescriptors(Object targetObject, List propertyDescriptorList)
	{
		if(targetObject==null || propertyDescriptorList==null)
		{
			return;
		}
		List<PropertyDescriptor> currentList = targetObjectToPropertyDescriptorMap.get(targetObject);
		if(currentList!=null)
		{//found existing one, remove
			removePropertyDescriptorToTargetObject(currentList);
		}
		//construct new from scratch
		currentList = new ArrayList<PropertyDescriptor>(propertyDescriptorList);
		targetObjectToPropertyDescriptorMap.put(targetObject, currentList);
		registerPropertyDescriptorToTargetObject(currentList, targetObject);
	}

	/**
	 * Remove all PropertyDescriptors in the given list from the registry of the given target Object.
	 * @param targetObject
	 * @param propertyDescriptorList
	 */
	public void removePropertyDescriptors(Object targetObject, List propertyDescriptorList)
	{
		if(targetObject==null || propertyDescriptorList==null)
		{
			return;
		}
		List<PropertyDescriptor> currentList = targetObjectToPropertyDescriptorMap.get(targetObject);
		if(currentList!=null)
		{//function if and only if it is not null.
			currentList.removeAll(propertyDescriptorList);
			removePropertyDescriptorToTargetObject(propertyDescriptorList);
		}
	}

	/**
	 * Return the list of property descriptors of the given target object; if nothing found, return an empty list rather than a null.
	 * @param targetObject
	 * @return the list of property descriptors of the given target object
	 */
	public List<PropertyDescriptor> getPropertyDescriptors(Object targetObject)
	{
		 List<PropertyDescriptor> currentList = targetObjectToPropertyDescriptorMap.get(targetObject);
		if(currentList==null)
		{//so as never return null
			currentList = new ArrayList<PropertyDescriptor>();
		}
		return currentList;
	}

	/**
	 * Return the list of all property descriptors; if nothing found, return an empty list rather than a null.
	 * @return the list of property descriptors of the given target object
	 */
	public List<PropertyDescriptor> getAllPropertyDescriptors()
	{
		List<PropertyDescriptor> resultList = new ArrayList<PropertyDescriptor>();
		Iterator it = targetObjectToPropertyDescriptorMap.keySet().iterator();
		while(it.hasNext())
		{
			resultList.addAll(getPropertyDescriptors(it.next()));
		}
		return resultList;
	}

	/**
	 * Return the total number of propertyDescriptors available in this list, regardless the registered object.
	 * @return the total number of propertyDescriptors available in this list, regardless the registered object.
	 */
	public int getTotalPropertiesCount()
	{
		int count = 0;
		Iterator it = targetObjectToPropertyDescriptorMap.keySet().iterator();
		while(it.hasNext())
		{
			count += getPropertyDescriptors(it.next()).size();
		}
		return count;
	}

	/**
	 * Return the list of target objects that are registered in this object.
	 * @return the list of target objects that are registered in this object.
	 */
	public List getTargetObjectList()
	{
		List resultList = new ArrayList(targetObjectToPropertyDescriptorMap.keySet());
		return resultList;
	}

	/**
	 * Given a propertyDescriptor, return its associated target Object; if nothing is found, return null.
	 * @param propertyDescriptor
	 * @return the given propertyDescriptor associated target Object; if nothing is found, return null.
	 */
	public Object getTargetObject(PropertyDescriptor propertyDescriptor)
	{
		return propertyDescriptorToTargetObjectMap.get(propertyDescriptor);
	}

	/**
	 * Return string representation of this object.
	 * @return string representation of this object.
	 */
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		Iterator it = targetObjectToPropertyDescriptorMap.keySet().iterator();
		while(it.hasNext())
		{
			Object targetObject = it.next();
			List propList = getPropertyDescriptors(targetObject);
			buf.append("obj:'" + targetObject + "', # of Prop descriptors: '" + propList.size() + "'");
		}
		return buf.toString();
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.6  2006/08/02 18:44:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.5  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.4  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/23 18:57:19  jiangsc
 * HISTORY      : Implemented the new Properties structure
 * HISTORY      :
 */
