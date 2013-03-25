/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui.properties;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PropertiesResult
{

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