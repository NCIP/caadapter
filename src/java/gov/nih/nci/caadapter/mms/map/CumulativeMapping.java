/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.mms.map;
import gov.nih.nci.caadapter.common.MetaObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * As the user maps objects and attributes to tables and columns this
 * class keeps track of what mapping has been completed. This
 * information is important because the validators will use this
 * information to determine if an attribute or object has already been
 * mapped.
 * 
 * @author OWNER: Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     caAdatper v4.0
 * @version    $Revision: 1.10 $
 * @date       $Date: 2009-07-30 17:35:46 $
 * @created 11-Aug-2006 8:18:15 AM
 */
public class CumulativeMapping {

	private List<AttributeMapping> attributeMappings = new ArrayList<AttributeMapping>();
	private List<DependencyMapping> dependencyMappings = new ArrayList<DependencyMapping>();	
	private List<AssociationMapping> singleAssociationMappings = new ArrayList<AssociationMapping>();
    private HashMap<String,MetaObject> sourceHashmap=new HashMap<String, MetaObject>();
    private HashMap<String, MetaObject> targetHashmap=new HashMap<String, MetaObject>();
	/**
	 *
	 * @param attributeMapping
	 */
	public void addAttributeMapping(AttributeMapping attributeMapping){
		attributeMappings.add(attributeMapping);
		if (attributeMapping!=null)
		{
			sourceHashmap.put(attributeMapping.getAttributeMetadata().getXPath(),
					attributeMapping.getColumnMetadata());
			targetHashmap.put(attributeMapping.getColumnMetadata().getXPath(),
					attributeMapping.getAttributeMetadata());
		}
		
	}

	/**
	 *
	 * @param dependencyMapping
	 */
	public void addDependencyMapping(DependencyMapping dependencyMapping){
        dependencyMappings.add(dependencyMapping);
		if (dependencyMapping!=null)
		{
			sourceHashmap.put(dependencyMapping.getSourceDependency().getXPath(),
					dependencyMapping.getTargetDependency());
			targetHashmap.put(dependencyMapping.getTargetDependency().getXPath(),
					dependencyMapping.getSourceDependency());
		}
	}

	/**
	 *
	 * @param singleAssociationMapping
	 */
	public void addAssociationMapping(AssociationMapping associationMapping){
   		singleAssociationMappings.add(associationMapping);
		if (associationMapping!=null)
		{
			sourceHashmap.put(associationMapping.getAssociationEndMetadata().getXPath(),
					associationMapping.getColumnMetadata());
			targetHashmap.put(associationMapping.getColumnMetadata().getXPath(),
					associationMapping.getAssociationEndMetadata());
		}
	}

	public List<AttributeMapping> getAttributeMappings(){
		return attributeMappings;
	}

	public List<DependencyMapping> getDependencyMappings(){
		return dependencyMappings;
	}

	public List<AssociationMapping> getAssociationMappings(){
		return singleAssociationMappings;
	}

	/**
	 *
	 * @param attributeMapping
	 */
	public void removeAttributeMapping(AttributeMapping attributeMapping){
		attributeMappings.remove(attributeMapping);
		if (attributeMapping!=null)
		{
			sourceHashmap.remove(attributeMapping.getAttributeMetadata().getXPath());
			targetHashmap.remove(attributeMapping.getColumnMetadata().getXPath());
		}
	}

	/**
	 *
	 * @param dependencyMapping
	 */
	public void removeDependencyMapping(DependencyMapping dependencyMapping){
		dependencyMappings.remove(dependencyMapping);
		if (dependencyMapping!=null)
		{
			sourceHashmap.remove(dependencyMapping.getSourceDependency().getXPath());
			targetHashmap.remove(dependencyMapping.getTargetDependency().getXPath());
		}
	}

	/**
	 *
	 * @param singleAssociationMapping
	 */
	public void removeAssociationMapping(AssociationMapping associationMapping){
		singleAssociationMappings.remove(associationMapping);
		if (associationMapping!=null)
		{
			sourceHashmap.remove(associationMapping.getAssociationEndMetadata().getXPath());
			targetHashmap.remove(associationMapping.getColumnMetadata().getXPath());
		}
	}
	
	public MetaObject findMappedTarget(String srcPath)
	{
		return sourceHashmap.get(srcPath);
	}
	
	public MetaObject findMappedSource(String tgrtPath)
	{
		return targetHashmap.get(tgrtPath);
	}
}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.9  2009/07/14 16:36:00  wangeug
 * HISTORY: clean codes
 * HISTORY:
 * HISTORY: Revision 1.8  2009/06/12 15:51:50  wangeug
 * HISTORY: clean code: caAdapter MMS 4.1.1
 * HISTORY:
 * HISTORY: Revision 1.7  2008/09/26 20:35:27  linc
 * HISTORY: Updated according to code standard.
 * HISTORY:
 */