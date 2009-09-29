/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.mms.generator;
import gov.nih.nci.caadapter.mms.map.AttributeMapping;
import gov.nih.nci.caadapter.mms.map.CumulativeMapping;
import gov.nih.nci.caadapter.mms.map.DependencyMapping;
import gov.nih.nci.caadapter.mms.map.AssociationMapping;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.common.metadata.ColumnMetadata;
import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.common.metadata.TableMetadata;
import gov.nih.nci.caadapter.mms.validator.AttributeMappingValidator;
import gov.nih.nci.caadapter.mms.validator.DependencyMappingValidator;
import gov.nih.nci.caadapter.mms.validator.SingleAssociationMappingValidator;
import gov.nih.nci.ncicb.xmiinout.domain.*;
import gov.nih.nci.ncicb.xmiinout.util.ModelUtil;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * The purpose of this class is to create and maintain a CumulativeMapping
 * object. As a caAdapter user drags and drops a source to a target in the UI 
 * the system will first determine what type of mapping the user is attempting
 * to create then it will determine if the mapping is valid based on various business rules. 
 * If the mapping is found to be valid it will add it to the CumulativeMapping 
 * object as either a DependencyMapping, AttributeMapping, SingleAssociationMapping, or ManyToManyMapping object.
 *
 * @author OWNER: connellm
 * @author LAST UPDATE $Author: wangeug $
 * @since     caAdatper v4.0
 * @version    $Revision: 1.19 $
 * @date       $Date: 2009-09-29 17:39:07 $
 */
public class CumulativeMappingGenerator {

private CumulativeMapping cumulativeMapping;
private ModelMetadata metaModel = null;
private String errorMessage;

private static CumulativeMappingGenerator instance;

private CumulativeMappingGenerator(String xmiName)  throws Exception
{
	metaModel = new ModelMetadata(xmiName);
	cumulativeMapping = new CumulativeMapping();
}

/**
 * @param xmiFileName_local
 */
public static boolean init(String xmiFileName_local) 
{	
	try {
		instance = new CumulativeMappingGenerator(xmiFileName_local);
    }catch (Exception e){      
      e.printStackTrace();
      return false;	  
    }
    return true;
}

public static CumulativeMappingGenerator getInstance() 
{
	return instance;
}


/**
 * @return the metaModel
 */
public ModelMetadata getMetaModel() {
	return metaModel;
}

/**
 * @return CumulativeMapping
 */
public  CumulativeMapping getCumulativeMapping() {
	return cumulativeMapping;
}

/**
 * @param cumulativeMapping
 */
public void setCumulativeMapping(CumulativeMapping cMapping) {
	cumulativeMapping = cMapping;
}

public String getErrorMessage() {
	return errorMessage;
}

public void setErrorMessage(String eMessage) {
	errorMessage = eMessage;
}

/**
 * This method would be used to remove a previously created source to target mapping.
 * @param source Source element to be unmapped
 * @param target Target element to be unmapped
 * @return boolean
 */
public boolean unmap(String source, String target){
	
	boolean successfullyUnmapped = false;
	String sourceMappingType = determineSourceMappingType(source);
	String targetMappingType = determineTargetMappingType(target);

	if (sourceMappingType.equals("dependency")&& targetMappingType .equals("dependency")){
		UMLClass sourceClass = getClass(source);
		UMLClass targetClass = getClass(target);
		successfullyUnmapped = unmapDependency(sourceClass, source, targetClass, target);
	} else if (sourceMappingType .equals("attribute") && targetMappingType.equals("attribute")) {
		successfullyUnmapped = unmapAttribute(source, target);
	} else if (sourceMappingType.equals("association")&& targetMappingType.equals("attribute")) {
		successfullyUnmapped = unmapAssociation(source, target);
//	} else if (sourceMappingType.equals("manytomanyassociation")&& targetMappingType.equals("attribute")){
//		successfullyUnmapped = unmapManyToManyAssociation(source, target);
	} else {
		setErrorMessage(source.substring(source.lastIndexOf(".")+1) + "  to " + target.substring(target.lastIndexOf(".")+1) + " is invalid/not_supported");
	}
	return successfullyUnmapped;
}
 

/**
 * @param source Source element to be mapped.
 * @param target Target element to be mapped.
 * @param updateModel If the underneath UML should be updated as creating a new mapping
 * @return boolean 
 */
public boolean map(String source, String target, boolean updateModel){
	
	boolean successfullyMapped = false;
	// The first thing that needs to be determined is what type of mapping is being attempted, i.e. Dependency, Attribute, Associatin, etc.
	String sourceMappingType = determineSourceMappingType(source);
	String targetMappingType = determineTargetMappingType(target);

    // Then the source and target mapping types are compared. They must be the same for the process to continue. For instance, if an attempt is made
	// to map an Object (vs. attribute) to a column in a table the mapping attempt will fail.
	if (sourceMappingType.equals("dependency")&& targetMappingType .equals("dependency")){
		//Then the actual components from the UML model are realized
		UMLClass sourceClass = getClass(source);
		UMLClass targetClass = getClass(target);
		successfullyMapped = mapDependency(sourceClass, source, targetClass, target, updateModel);		
	} else if (sourceMappingType.equals("attribute") && targetMappingType.equals("attribute")) {
		//Then the actual components from the UML model are realized
		successfullyMapped = mapAttribute(source, target, updateModel);
	} else if (sourceMappingType.equals("association")&& targetMappingType.equals("attribute")) {
		//Then the actual components from the UML model are realized
		successfullyMapped = mapAssociation(source, target, updateModel);
//	} else if (sourceMappingType.equals("manytomanyassociation")&& targetMappingType.equals("attribute")){
//		//Then the actual components from the UML model are realized
//		UMLAssociationEnd sourceEnd = getAssociationEnd(source);
//		successfullyMapped = mapManyToManyAssociation(sourceEnd, source, target);
	}else {
		setErrorMessage(source.substring(source.lastIndexOf(".")+1) + "  to " + target.substring(target.lastIndexOf(".")+1) + " is invalid/not_supported");
	}
	return successfullyMapped;
}


/**
 * This method determines if the source in the mapping is an object, attribute or association
 * @param source
 * @return String sourceMappingType
 */
private String determineSourceMappingType(String source){
	
	String mappingType = null;
	if (isClass(source)){
		mappingType = "dependency";	
	} else if (isAttribute(source)){
		mappingType = "attribute";
	//} //TO_DO else if (isSingleAssociation(source)&& isManyToManyAssociation(source)){
	} else 
		mappingType="association";

	return mappingType;
}

/**
 * This method first checks to see if the target is a table or attribute and what kind of table or attribute
 * the target is in order to determine what type of target mapping is being attempted.
 * @param target
 * @return String target mapping type
 */
private String determineTargetMappingType(String target){
	String mappingType = "undefinedTarget";
	//We need to determine if the target is a table for a dependency mapping or a many to many mapping.
	
	if (isClass(target) && !isCorrelationTable(target)){
		mappingType = "dependency";
	} else if (isAttribute(target)){
		mappingType = "attribute";
	} else if (isCorrelationTable(target)){
		mappingType = "correlationtable";
	}
	return mappingType;
}
//TO_DO many of these convenience methods could be moved to a utility class to facilitate reuse by
//other classes. Didn't have time to do that, but thats what I would do.
/**
 * This method uses the path to the object being mapped to retrieve the actual UMLClass entity from the UML model(xmi file)
 * @param pathToClass
 * @return UMLClass
 */

private  UMLClass getClass(String pathToClass){
	UMLClass clazz = null;
	String[] modelElements = pathToClass.split("\\.");
    clazz = findClass(metaModel.getModel(), modelElements, 0, modelElements.length-1);
	return clazz;
}


/**
 * This method creates a dependency mapping by extracting values from target and source UMLClasses as well as the paths to
 * those objects extracted originally from the xmi file. After creating and validating the mapping the
 * method adds the mapping to the cumulative mapping object.
 * @param source
 * @param sourceXPath
 * @param target
 * @param targetXPath
 * @param updateModel If the underneath UML should be updated as creating a new mapping
 * @return boolean
 */
private  boolean mapDependency(UMLClass source, String sourceXPath, UMLClass target, String targetXPath, boolean updateModel){
	boolean successfullyMapped = false;
	DependencyMapping mapping = new DependencyMapping();
	ObjectMetadata sourceMetadata = new ObjectMetadata();
	sourceMetadata.setName(source.getName());
	sourceMetadata.setXPath(sourceXPath);
	TableMetadata targetMetadata = new TableMetadata();
	targetMetadata.setName(target.getName());
	targetMetadata.setXPath(targetXPath);
	mapping.setSourceDependency(sourceMetadata);
	mapping.setTargetDependency(targetMetadata);
	DependencyMappingValidator validator = new DependencyMappingValidator(mapping);
	successfullyMapped = validator.isValid();
	if (successfullyMapped){
		cumulativeMapping.addDependencyMapping(mapping);
		//add dependency to UMLModel
		if (updateModel)
			XMIAnnotationUtil.addDataObjectDependency(metaModel.getModel(), targetXPath, sourceXPath);
	}
	else {
		setErrorMessage(validator.getValidationErrorMessage());
	}
	return successfullyMapped;
}

/**
 * This method removes a DependencyMapping object from the CumulativeMapping object.
 * @param source
 * @param sourceXPath
 * @param target
 * @param targetXPath
 * @return
 */
private boolean unmapDependency(UMLClass source, String sourceXPath, UMLClass target, String targetXPath){
	List<DependencyMapping> dependencyMapping = cumulativeMapping.getDependencyMappings();
	for (DependencyMapping d : dependencyMapping) {
		if (d.getSourceDependency().getXPath().equals(sourceXPath) && d.getTargetDependency().getXPath().equals(targetXPath)) {
			cumulativeMapping.removeDependencyMapping(d);
			//remove dependency from UMLModel
			return XMIAnnotationUtil.removeDataObjectDependency(metaModel.getModel(), sourceXPath);
		}
	}
	return false;
}

/**
 * @param sourcePath
 * @param targetPath
 * @param updateModel If the underneath UML should be updated as creating a new mapping
 * @return boolean
 */
private boolean mapAttribute(String sourcePath, String targetPath, boolean updateModel){

	LinkedHashMap modelMeta = metaModel.getModelMetadata();
	AttributeMetadata attributeMetadata = (AttributeMetadata)modelMeta.get(sourcePath);
	ColumnMetadata columnMetadata = (ColumnMetadata)modelMeta.get(targetPath);
	boolean successfullyMapped = false;
	AttributeMapping mapping = new AttributeMapping();
	
	columnMetadata.setType(columnMetadata.TYPE_ATTRIBUTE);
	mapping.setAttributeMetadata(attributeMetadata);
	mapping.setColumnMetadata(columnMetadata);
	
	AttributeMappingValidator validator = new AttributeMappingValidator(mapping);
	successfullyMapped = validator.isValid();
	if (successfullyMapped) {
		cumulativeMapping.addAttributeMapping(mapping);
		//add the tag to the UMLAttribute
		if (updateModel)
		{
			UMLAttribute xpathUMLAttribute=ModelUtil.findAttribute(metaModel.getModel(),columnMetadata.getXPath());
			//remove the leading string:"Logical View.Logical Model." from source path
			String pureSrcPath=XMIAnnotationUtil.getCleanPath(metaModel.getMmsPrefixObjectModel(),  sourcePath);
			XMIAnnotationUtil.addTagValue(xpathUMLAttribute, "mapped-attributes", pureSrcPath);
		}
	}
	else {
		setErrorMessage(validator.getValidationErrorMessage());
	}
	return successfullyMapped;
}
/**
 * @param sourcePath
 * @param sourcePath
 * @param targetPath
 * @param targetPath
 * @return boolean
 */
private boolean unmapAttribute(String sourcePath, String targetPath){
	List<AttributeMapping> attributeMapping = cumulativeMapping.getAttributeMappings();
	for (AttributeMapping attr : attributeMapping) {
		if (attr.getAttributeMetadata().getXPath().equals(sourcePath) && attr.getColumnMetadata().getXPath().equals(targetPath)) {
			cumulativeMapping.removeAttributeMapping(attr);
			//remove "mapped-attributes" tag from UMLModel
			UMLAttribute xpathAttr=ModelUtil.findAttribute(metaModel.getModel(),attr.getColumnMetadata().getXPath());
			return XMIAnnotationUtil.removeTagValue(xpathAttr, "mapped-attributes");
		}
	}
	return false;
}



/**
 * @param sourceXPath
 * @param targetXPath
 * @param updateModel If the underneath UML should be updated as creating a new mapping
 * @return boolean
 */
private boolean mapAssociation(String sourceXPath, String targetXPath, boolean updateModel){

	LinkedHashMap modelMeta = metaModel.getModelMetadata();
	AssociationMetadata sourceMetadata = (AssociationMetadata)modelMeta.get(sourceXPath);
	ColumnMetadata targetMetadata = (ColumnMetadata)modelMeta.get(targetXPath);
	boolean successfullyMapped = false;
	AssociationMapping mapping = new AssociationMapping();
		
	targetMetadata.setType(targetMetadata.TYPE_ASSOCIATION);	
	mapping.setAssociationEndMetadata(sourceMetadata);
	mapping.setColumnMetadata(targetMetadata);
	
	SingleAssociationMappingValidator validator = new SingleAssociationMappingValidator(mapping);
	successfullyMapped = validator.isValid();
	if (successfullyMapped) {
		cumulativeMapping.addAssociationMapping(mapping);
		if (updateModel)
			XMIAnnotationUtil.annotateAssociationMapping(metaModel.getModel(),sourceXPath, targetXPath);
	}
	else {
		setErrorMessage(validator.getValidationErrorMessage());
	}
	return successfullyMapped;
}
/**
 * @param sourceXPath String
 * @param targetXPath String
 * @return boolean
 */
private boolean unmapAssociation(String sourceXPath, String targetXPath){
	List<AssociationMapping> singleAssociationMapping = cumulativeMapping.getAssociationMappings();
	for (AssociationMapping assoS : singleAssociationMapping) {
		if (assoS.getAssociationEndMetadata().getXPath().equals(sourceXPath) && assoS.getColumnMetadata().getXPath().equals(targetXPath)) {
			cumulativeMapping.removeAssociationMapping(assoS);
			return XMIAnnotationUtil.deAnnotateAssociationMapping(metaModel.getModel(), sourceXPath, targetXPath);
		}
	}
	return false;
}



/**
 * @param element
 * @return boolean
 */
private boolean isClass(String element){
	UMLClass clazz = null;
	boolean isClass = false;
	String[] modelElements = element.split("\\.");
	clazz = findClass(metaModel.getModel(), modelElements, 0, modelElements.length-1);
	if (clazz != null){
	// If class is not null then we have a UMLClass
		isClass = true;
	}
	return isClass;
}

/**
 * @param element
 * @return boolean
 */
private boolean isAttribute(String element){
	boolean isAttribute = false;
	UMLAttribute attribute = null;
	UMLClass clazz = null;
	String[] modelElements = element.split("\\.");
	clazz = findClass(metaModel.getModel(), modelElements, 0, modelElements.length-2);
	if (clazz!=null){
		LinkedHashMap modelMeta = metaModel.getModelMetadata();
		if (modelMeta.get(element)!= null) {
			if (modelMeta.get(element) instanceof AttributeMetadata) return true;
			else if (modelMeta.get(element) instanceof ColumnMetadata) return true;
			else return false;
		}
	}		
	return isAttribute;
}

/**
 * @param element
 * @return boolean
 */
private boolean isCorrelationTable(String element){
	boolean isCorrelationTable = false;
	UMLClass clazz = null;
	String[] modelElements = element.split("\\.");
	clazz = findClass(metaModel.getModel(), modelElements, 0, modelElements.length-1);
	if (clazz != null){
	//If clazz is not null then we have a UMLClass
	//Then we check the Metadata model to see if the Class is a table
	//which it should be, then we need to check that the table is of type "correlation".
	//If it is a correlation table then this is a many to many mapping situation.
		if (metaModel.getModelMetadata().get(element).getClass().getName().equals("TableMetadata")) {
			TableMetadata table = (TableMetadata)metaModel.getModelMetadata().get(element);
			if (table.getType().equals("correlation")){
				isCorrelationTable=true;
			}
		}
	}
	return isCorrelationTable;
}

/**
 * @param model
 * @param className
 * @return UMLClass
 */
private UMLClass findClass(UMLModel model, String[] className, int start, int end) 
{
  for(UMLPackage pkg : model.getPackages()) {
	  if (pkg.getName().equals(className[start])) {
		  UMLClass c = findClass(pkg, className, start+1, end);
		  if(c != null)
			  return c;
	  }
  }    
  return null;
}

/**
 * @param pkg
 * @param className
 * @return UMLClass
 */
private UMLClass findClass(UMLPackage pkg, String[] className, int start, int end) {
	if (start == end) {
		for(UMLClass clazz : pkg.getClasses()) {
			if(clazz.getName().equals(className[start])) 
				return clazz;
		}
	}
	else {
		for(UMLPackage _pkg : pkg.getPackages()) {
			UMLClass c = findClass(_pkg, className,start+1,end);
			if(c != null)
				return c;
		}
	}
    return null;
  }
/**
	 * @param args
	 */
	public static void main(String[] args) {
		CumulativeMappingGenerator.init("C:/sample.xmi");
      CumulativeMappingGenerator x =CumulativeMappingGenerator.getInstance();
    
      x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Gene","Logical View.Data Model.GENE", true );
//      x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Taxon","Logical View.Data Model.TAXON");
//      x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Clone","Logical View.Data Model.CLONE");
//      x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Chromosome","Logical View.Data Model.CHROMOSOME");
//      x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Sequence","Logical View.Data Model.SEQUENCE");
//  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Target","Logical View.Data Model.TARGET");
//  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Library","Logical View.Data Model.LIBRARY");
//  	  
//  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Taxon.id","Logical View.Data Model.TAXON.TAXON_ID");
//  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Clone.id","Logical View.Data Model.CLONE.CLONE_ID");
//  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Library.id","Logical View.Data Model.LIBRARY.LIBRARY_ID");
//  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Target.id","Logical View.Data Model.TARGET.TARGET_ID");
//  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Gene.id","Logical View.Data Model.GENE.GENE_ID");
//  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Sequence.id","Logical View.Data Model.SEQUENCE.SEQUENCE_ID");
//  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Chromosome.id","Logical View.Data Model.CHROMOSOME.CHROMOSOME_ID");
//  
//  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Gene.chromosome","Logical View.Data Model.GENE.CHROMOSOME_ID");
//  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Chromosome.taxon","Logical View.Data Model.CHROMOSOME.TAXON_ID");
//  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Sequence.clone","Logical View.Data Model.SEQUENCE.CLONE_ID");
//  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Clone.library","Logical View.Data Model.CLONE.LIBRARY_ID");
//  	  
//  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Gene.sequenceCollection","Logical View.Data Model.GENE_SEQUENCE.GENE_ID");
//  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Sequence.geneCollection","Logical View.Data Model.GENE_SEQUENCE.SEQUENCE_ID");
//  	  
  	  CumulativeMapping y = x.getCumulativeMapping();
	}
}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.18  2009/07/14 16:35:49  wangeug
 * HISTORY: clean codes
 * HISTORY:
 * HISTORY: Revision 1.17  2009/07/10 19:55:34  wangeug
 * HISTORY: MMS re-engineering
 * HISTORY:
 * HISTORY: Revision 1.16  2009/06/12 15:50:34  wangeug
 * HISTORY: clean code: caAdapter MMS 4.1.1
 * HISTORY:
 * HISTORY: Revision 1.15  2008/09/26 20:35:27  linc
 * HISTORY: Updated according to code standard.
 * HISTORY:
 */
