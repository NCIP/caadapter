/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.mapping;

import gov.nih.nci.cbiit.cmts.core.ComponentType;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.core.KindType;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.core.TagType;

public class MappingAnnotationUtil {

	public static int findMaxMultiplicityIndex(ElementMeta parentElement, String childName)
	{
		int rtnIndx=0;
		if (parentElement.getChildElement().isEmpty())
			return rtnIndx;
		for (ElementMeta childMeta:parentElement.getChildElement())
		{
			if(childMeta.getName().startsWith(childName+"["))
			{
				if (childMeta.getMultiplicityIndex().intValue()>rtnIndx)
					rtnIndx=childMeta.getMultiplicityIndex().intValue();
			}
		}
		return rtnIndx;
	}
	
	public static void addTag(Mapping mapping, ComponentType componentType, KindType kind, String key, String value)
	{
		TagType tag=new TagType();
		tag.setComponentType(componentType);
		tag.setKind(kind);
		tag.setKey(key);
		tag.setValue(value);
		
		mapping.getTags().getTag().add(tag);
		
	}
	
	public static boolean removeTag(Mapping mapping, ComponentType componentType, KindType kind, String key, String value)
	{
		for (TagType tag:mapping.getTags().getTag())
		{
			if (tag.getComponentType().equals(componentType)
					&&tag.getKind().equals(kind)
					&&tag.getKey().equals(key)
					&tag.getValue().equals(value))
			{
				mapping.getTags().getTag().remove(tag);
				return true;
			}
		}
		
		return false;
	}
}
