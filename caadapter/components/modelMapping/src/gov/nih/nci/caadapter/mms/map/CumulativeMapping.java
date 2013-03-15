/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.mms.map;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;

/**
 * As the user maps objects and attributes to tables and columns this
 * class keeps track of what mapping has been completed. This
 * information is important because the validators will use this
 * information to determine if an attribute or object has already been
 * mapped.
 * @version 1.0
 * @created 11-Aug-2006 8:18:15 AM
 */
public class CumulativeMapping {

	private List<AttributeMapping> attributeMappings = new ArrayList<AttributeMapping>();
	private List<DependencyMapping> dependencyMappings = new ArrayList<DependencyMapping>();
	private List<ManyToManyMapping> manyToManyMappings = new ArrayList<ManyToManyMapping>();
	private List<SingleAssociationMapping> singleAssociationMappings = new ArrayList<SingleAssociationMapping>();

    private List<SemanticMapping> semanticMappings = new ArrayList<SemanticMapping>();

    private static CumulativeMapping instance;

	public CumulativeMapping(){
	}

    public synchronized static CumulativeMapping getInstance() throws Exception
	{
		if (instance == null)
		{
			instance = new CumulativeMapping();
		}
		return instance;
	}
    public synchronized static void reset()
    {
    	if (instance != null) {
    		instance = null;
    		System.gc();
    	}
    }

	/**
	 *
	 * @param attributeMapping
	 */
	public void addAttributeMapping(AttributeMapping attributeMapping){
		attributeMappings.add(attributeMapping);
	}

	/**
	 *
	 * @param dependencyMapping
	 */
	public void addDependencyMapping(DependencyMapping dependencyMapping){
        dependencyMappings.add(dependencyMapping);
	}

	/**
	 *
	 * @param manyToManyMapping
	 */
	public void addManyToManyMapping(ManyToManyMapping manyToManyMapping){
		manyToManyMappings.add(manyToManyMapping);
	}

	/**
	 *
	 * @param semanticMapping
	 */
	public void addSemanticMapping(SemanticMapping semanticMapping){
		semanticMappings.add(semanticMapping);
	}

	/**
	 *
	 * @param singleAssociationMapping
	 */
	public void addSingleAssociationMapping(SingleAssociationMapping singleAssociationMapping){
   		singleAssociationMappings.add(singleAssociationMapping);
	}

	/**
	 *
	 * @param attributeMapping
	 */
	public boolean containsAttributeMapping(AttributeMapping attributeMapping){
		return attributeMappings.contains(attributeMapping);
	}

	/**
	 *
	 * @param dependencyMapping
	 */
	public boolean containsDependencyMapping(DependencyMapping dependencyMapping){
		return dependencyMappings.contains(dependencyMapping);
	}

	/**
	 *
	 * @param associationEndMetadata
	 */
	public boolean isDependencyMapped(AssociationMetadata associationEndMetadata){
		boolean isDependencyMapped = false;
		List dependencyMappings = this.getAttributeMappings();
		Iterator i = dependencyMappings.iterator();
		while (i.hasNext()) {
			try {
				AssociationMetadata m = (AssociationMetadata)i.next();
				if (m.getXPath().equals(associationEndMetadata)){
					isDependencyMapped = true;
				}

			} catch (Exception e) {
		            //LOGGER.fine(e.getMessage());
		            e.printStackTrace();
			}
		}
		return dependencyMappings.contains(associationEndMetadata);
	}
	/**
	 *
	 * @param manyToManyMapping
	 */
	public boolean containsManyToManyMapping(ManyToManyMapping manyToManyMapping){
		return manyToManyMappings.contains(manyToManyMapping);
	}

	/**
	 *
	 * @param singleAssociationMapping
	 */
	public boolean containsSingleAssociationMapping(SingleAssociationMapping singleAssociationMapping){
		return singleAssociationMappings.contains(singleAssociationMapping);
	}

	public List<AttributeMapping> getAttributeMappings(){
		return attributeMappings;
	}

	public List<DependencyMapping> getDependencyMappings(){
		return dependencyMappings;
	}

	public List<ManyToManyMapping> getManyToManyMappings(){
		return manyToManyMappings;
	}

	public List<SemanticMapping> getSemanticMappings(){
		return semanticMappings;
	}

	public List<SingleAssociationMapping> getSingleAssociationMappings(){
		return singleAssociationMappings;
	}

	/**
	 *
	 * @param attributeMapping
	 */
	public void removeAttributeMapping(AttributeMapping attributeMapping){
		attributeMappings.remove(attributeMapping);
	}

	/**
	 *
	 * @param dependencyMapping
	 */
	public void removeDependencyMapping(DependencyMapping dependencyMapping){
		dependencyMappings.remove(dependencyMapping);
	}

	/**
	 *
	 * @param semanticMapping
	 */
	public void removeSemanticMapping(SemanticMapping semanticMapping){
		semanticMappings.remove(semanticMapping);
	}

	/**
	 *
	 * @param singleAssociationMapping
	 */
	public void removeSingleAssociationMapping(SingleAssociationMapping singleAssociationMapping){
		singleAssociationMappings.remove(singleAssociationMapping);
	}

	/**
	 *
	 * @param manyToManyMapping
	 */
	public void removeManyToManyMapping(ManyToManyMapping manyToManyMapping){
		manyToManyMappings.remove(manyToManyMapping);
	}

}
