/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.mms.generator;
import gov.nih.nci.caadapter.mms.map.AttributeMapping;
import gov.nih.nci.caadapter.mms.map.CumulativeMapping;
import gov.nih.nci.caadapter.mms.map.DependencyMapping;
import gov.nih.nci.caadapter.mms.map.ManyToManyMapping;
import gov.nih.nci.caadapter.mms.map.SingleAssociationMapping;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.common.metadata.ColumnMetadata;
import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.common.metadata.TableMetadata;
import gov.nih.nci.caadapter.mms.validator.AttributeMappingValidator;
import gov.nih.nci.caadapter.mms.validator.DependencyMappingValidator;
import gov.nih.nci.caadapter.mms.validator.ManyToManyMappingValidator;
import gov.nih.nci.caadapter.mms.validator.SingleAssociationMappingValidator;
import gov.nih.nci.ncicb.xmiinout.domain.*;
import gov.nih.nci.ncicb.xmiinout.handler.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author connellm
 * The purpose of this class is to create and maintain a CumulativeMapping
 * object. As a caAdapter user drags and drops a source to a target in the UI
 * the system will first determine what type of mapping the user is attempting
 * to create then it will determine if the mapping is valid based on various business rules.
 * If the mapping is found to be valid it will add it to the CumulativeMapping
 * object as either a DependencyMapping, AttributeMapping, SingleAssociationMapping, or ManyToManyMapping object.
 */
public class CumulativeMappingGenerator {

private static CumulativeMapping cumulativeMapping;
private static UMLModel model = null;
private static ModelMetadata metaModel = null;
private static LinkedHashMap metaMap = null;
private static CumulativeMappingGenerator cumulativeMappingGenerator;
private static String xmiFileName;
private static String errorMessage;

/**
 * @param xmiFileName_local
 */
public CumulativeMappingGenerator(String xmiFileName_local)
{
	init(xmiFileName_local);
	xmiFileName = xmiFileName_local;
}

public CumulativeMappingGenerator(){
}

/**
 * @param xmiFileName_local
 */
public static boolean init(String xmiFileName_local)
{
	if (cumulativeMappingGenerator != null)
	{
		cumulativeMappingGenerator = null;
		CumulativeMapping.reset();
		System.gc();
	}

	cumulativeMappingGenerator = new CumulativeMappingGenerator();
	xmiFileName = xmiFileName_local;

	try {
		ModelMetadata.createModel(xmiFileName_local);
		metaModel = ModelMetadata.getInstance();
		metaMap = metaModel.getModelMetadata();
		model = metaModel.getModel();
		cumulativeMapping = CumulativeMapping.getInstance();
	} catch (XmiException e){
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }catch (Exception e){
      e.printStackTrace();
      return false;
    }
    return true;
}

public static CumulativeMappingGenerator getInstance()
{
	return cumulativeMappingGenerator;
}

public static String getXmiFileName()
{
	return xmiFileName;
}

/**
 * This method would be used to remove a previously created source to target mapping.
 * @param source Source element to be unmapped
 * @param target Target element to be unmapped
 * @return boolean
 */
public static boolean unmap(String source, String target){

	boolean successfullyUnmapped = false;
	String sourceMappingType = determineSourceMappingType(source);
	String targetMappingType = determineTargetMappingType(target);

	if (sourceMappingType.equals("dependency")&& targetMappingType .equals("dependency")){
		UMLClass sourceClass = getClass(source);
		UMLClass targetClass = getClass(target);
		successfullyUnmapped = unmapDependency(sourceClass, source, targetClass, target);
	} else if (sourceMappingType .equals("attribute") && targetMappingType.equals("attribute")) {
		successfullyUnmapped = unmapAttribute(source, target);
	} else if (sourceMappingType.equals("singleassociation")&& targetMappingType.equals("attribute")) {
		successfullyUnmapped = unmapSingleAssociation(source, target);
	} else if (sourceMappingType.equals("manytomanyassociation")&& targetMappingType.equals("attribute")){
		successfullyUnmapped = unmapManyToManyAssociation(source, target);
	} else {
		setErrorMessage(source.substring(source.lastIndexOf(".")+1) + "  to " + target.substring(target.lastIndexOf(".")+1) + " is invalid/not_supported");
	}
	return successfullyUnmapped;
}

/**
 * @param source Source element to be mapped.
 * @param target Target element to be mapped.
 * @return boolean
 */
public static boolean map(String source, String target){

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
		successfullyMapped = mapDependency(sourceClass, source, targetClass, target);
	} else if (sourceMappingType.equals("attribute") && targetMappingType.equals("attribute")) {
		//Then the actual components from the UML model are realized
		successfullyMapped = mapAttribute(source, target);
	} else if (sourceMappingType.equals("singleassociation")&& targetMappingType.equals("attribute")) {
		//Then the actual components from the UML model are realized
		successfullyMapped = mapSingleAssociation(source, target);
	} else if (sourceMappingType.equals("manytomanyassociation")&& targetMappingType.equals("attribute")){
		//Then the actual components from the UML model are realized
		UMLAssociationEnd sourceEnd = getAssociationEnd(source);
		successfullyMapped = mapManyToManyAssociation(sourceEnd, source, target);
	}else {
		setErrorMessage(source.substring(source.lastIndexOf(".")+1) + "  to " + target.substring(target.lastIndexOf(".")+1) + " is invalid/not_supported");
	}
	return successfullyMapped;
}

public static boolean setPrimaryKey( String source )
{
	return true;
}

/**
 * This method determines if the source in the mapping is an object, attribute or association
 * @param source
 * @return String sourceMappingType
 */
public static String determineSourceMappingType(String source){

	String mappingType = null;
	if (isClass(source)){
		mappingType = "dependency";
	} else if (isAttribute(source)){
		mappingType = "attribute";
	//} //TO_DO else if (isSingleAssociation(source)&& isManyToManyAssociation(source)){
	} else if (isSingleAssociation(source)){
		mappingType = "singleassociation";
	} else if (isOneToManyAssociation(source)){
		mappingType = "singleassociation";
//		mappingType = "onetomanyassociation";
	} else if (isManyToManyAssociation(source)){
		mappingType = "manytomanyassociation";
	}
	return mappingType;
}

/**
 * This method first checks to see if the target is a table or attribute and what kind of table or attribute
 * the target is in order to determine what type of target mapping is being attempted.
 * @param target
 * @return String target mapping type
 */
public static String determineTargetMappingType(String target){
	String mappingType = null;
	//We need to determine if the target is a table for a dependency mapping or a many to many mapping.

	if (isClass(target) && !isCorrelationTable(target)){
		mappingType = "dependency";
		//TO_DO if you want to make the mapping tool "fool proof" you would need to determine if the
		//columns the user is mapping are foregein keys, primary keys, correct datatypes, etc. at
		//this point we are assuming the user is going to have a correctly designed database
		//and knows what needs to be mapped to what. In my experience though users don't always
		//know enough about their data and object oriented program, so you may want to implment some
		//checking.
		/*  if we need to validate whether or not the column is a foreign key uncomment this code
		 * however, the UI must be made to change the column type in the model metadata object
		 * for the appropriate ColumnMetadata object to "foreignKey"
	} else if (isAttribute(target) && !isForeignKey(target)){
		mappingType = "attribute";
	} else if (isAttribute(target) && isForeignKey(target)){
		mappingType = "foreignkey";
		*/
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

public static UMLClass getClass(String pathToClass){
	UMLClass clazz = null;
	String[] modelElements = pathToClass.split("\\.");
    clazz = findClass(model, modelElements, 0, modelElements.length-1);
	return clazz;
}

///**
// * This method uses the path to the attribute being mapped to retrieve the actual UMLAttribute entity from the UML model(xmi file)
// * @param pathToAttribute
// * @return UMLAttribute
// */
//public static UMLAttribute getAttribute(String pathToAttribute){
//	UMLAttribute attribute = null;
//	UMLClass clazz = null;
//	String[] modelElements = pathToAttribute.split("\\.");
//	clazz = findClass(model, modelElements, 0,modelElements.length-2);
//
//	if (clazz!=null){
//		LinkedHashMap modelMeta = metaModel.getModelMetadata();
//		 for(UMLAttribute att : clazz.getAttributes()) {
//			  if (att.getName().equals(modelElements[modelElements.length-1])) {
//				  attribute = att;
//			  }
//		 }
//	}
//	return attribute;
//}

/**
 * This method uses the path to the rolename (association) being mapped to retrieve the actual UMLAssociationEnd entity from the UML model(xmi file)
 * @param pathToAssociation
 * @return UMLAssociationEnd
 */
public static UMLAssociationEnd getAssociationEnd(String pathToAssociation){
	UMLAssociationEnd end = null;
	UMLClass clazz = null;
	String[] modelElements = pathToAssociation.split("\\.");
	clazz = findClass(model, modelElements, 0, modelElements.length-2);
	if (clazz!=null){
		 for(UMLAssociation association : clazz.getAssociations()) {
			 for(UMLAssociationEnd associationEnd : association.getAssociationEnds()) {
			  if (associationEnd.getRoleName().equals(modelElements[modelElements.length-1]) && associationEnd.getHighMultiplicity()< 2) {
				  end = associationEnd;
			  }
		    }
		 }
	}
	return end;
}

/**
 * This method finds the other end (UMLAssociationEnd)of a UMLAssociation based on a starting UMLAssociatonEnd obejct.
 * @param associatonEnd
 * @return UMLAssociationEnd
 */
public static UMLAssociationEnd getOtherAssociationEnd(UMLAssociationEnd associatonEnd){
	UMLAssociation association = associatonEnd.getOwningAssociation();
    UMLAssociationEnd otherEnd = null;
	if (association!=null){
		for (UMLAssociationEnd end : association.getAssociationEnds()){
			if (!end.getRoleName().equals(associatonEnd.getRoleName())){
				otherEnd = end;
			}

		}
	}
	return otherEnd;
}

/**
 * This method creates a dependency mapping by extracting values from target and source UMLClasses as well as the paths to
 * those objects extracted originally from the xmi file. After creating and validating the mapping the
 * method adds the mapping to the cumulative mapping object.
 * @param source
 * @param sourceXPath
 * @param target
 * @param targetXPath
 * @return boolean
 */
public static boolean mapDependency(UMLClass source, String sourceXPath, UMLClass target, String targetXPath){
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
public static boolean unmapDependency(UMLClass source, String sourceXPath, UMLClass target, String targetXPath){
	List<DependencyMapping> dependencyMapping = cumulativeMapping.getDependencyMappings();
	for (DependencyMapping d : dependencyMapping) {
		if (d.getSourceDependency().getXPath().equals(sourceXPath) && d.getTargetDependency().getXPath().equals(targetXPath)) {
			cumulativeMapping.removeDependencyMapping(d);
			return true;
		}
	}
	return false;
}

/**
 * @param sourcePath
 * @param targetPath
 * @return boolean
 */
public static boolean mapAttribute(String sourcePath, String targetPath){

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
public static boolean unmapAttribute(String sourcePath, String targetPath){
	List<AttributeMapping> attributeMapping = cumulativeMapping.getAttributeMappings();
	for (AttributeMapping attr : attributeMapping) {
		if (attr.getAttributeMetadata().getXPath().equals(sourcePath) && attr.getColumnMetadata().getXPath().equals(targetPath)) {
			cumulativeMapping.removeAttributeMapping(attr);
			return true;
		}
	}
	return false;
}



/**
 * @param sourceXPath
 * @param targetXPath
 * @return boolean
 */
public static boolean mapSingleAssociation(String sourceXPath, String targetXPath){

	LinkedHashMap modelMeta = metaModel.getModelMetadata();
	AssociationMetadata sourceMetadata = (AssociationMetadata)modelMeta.get(sourceXPath);
	ColumnMetadata targetMetadata = (ColumnMetadata)modelMeta.get(targetXPath);
	boolean successfullyMapped = false;
	SingleAssociationMapping mapping = new SingleAssociationMapping();

	targetMetadata.setType(targetMetadata.TYPE_ASSOCIATION);
	mapping.setAssociationEndMetadata(sourceMetadata);
	mapping.setColumnMetadata(targetMetadata);

	SingleAssociationMappingValidator validator = new SingleAssociationMappingValidator(mapping);
	successfullyMapped = validator.isValid();
	if (successfullyMapped) {
		cumulativeMapping.addSingleAssociationMapping(mapping);
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
public static boolean unmapSingleAssociation(String sourceXPath, String targetXPath){
	List<SingleAssociationMapping> singleAssociationMapping = cumulativeMapping.getSingleAssociationMappings();
	for (SingleAssociationMapping assoS : singleAssociationMapping) {
		if (assoS.getAssociationEndMetadata().getXPath().equals(sourceXPath) && assoS.getColumnMetadata().getXPath().equals(targetXPath)) {
			cumulativeMapping.removeSingleAssociationMapping(assoS);
			return true;
		}
	}
	return false;
}

/**
 * @param source UMLAssociationEnd
 * @param sourceXPath String
 * @param targetXPath String
 * @return boolean
 */
public static boolean mapManyToManyAssociation(UMLAssociationEnd source, String sourceXPath, String targetXPath) {
	boolean successfullyMapped = false;
	ManyToManyMapping mapping = new ManyToManyMapping();
	AssociationMetadata thisEnd = new AssociationMetadata();
	thisEnd.setUMLAssociation(source.getOwningAssociation());
	UMLAssociationEnd end = getOtherAssociationEnd(source);
	AssociationMetadata otherEnd = new AssociationMetadata();
	otherEnd.setUMLAssociation(end.getOwningAssociation());
	otherEnd.setRoleName(end.getRoleName());
	otherEnd.setMultiplicity(end.getHighMultiplicity());
	otherEnd.setNavigability(end.isNavigable());
	otherEnd.setReciprocolRoleName(thisEnd.getRoleName());
	otherEnd.setXPath(targetXPath);

	LinkedHashMap modelMeta = metaModel.getModelMetadata();
	ColumnMetadata col = (ColumnMetadata)modelMeta.get(targetXPath);
	col.setType(col.TYPE_ASSOCIATION);

	thisEnd.setRoleName(source.getRoleName());
	thisEnd.setMultiplicity(source.getHighMultiplicity());
	thisEnd.setNavigability(source.isNavigable());
	thisEnd.setXPath(sourceXPath);
	thisEnd.setReciprocolRoleName(end.getRoleName());
	mapping.setAssociationEndMetadata(thisEnd);
	mapping.setOtherAssociationEndMetadata(otherEnd);
	mapping.setThisEndColumn(col);
	ManyToManyMappingValidator validator = new ManyToManyMappingValidator(mapping);
	successfullyMapped = validator.isValid();
	if (successfullyMapped) {
		cumulativeMapping.addManyToManyMapping(mapping);
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
public static boolean unmapManyToManyAssociation(String sourceXPath, String targetXPath) {
	List<ManyToManyMapping> manyToManyMapping = cumulativeMapping.getManyToManyMappings();
	for (ManyToManyMapping assoM : manyToManyMapping) {
		if (assoM.getAssociationEndMetadata().getXPath().equals(sourceXPath) && assoM.getOtherAssociationEndMetadata().getXPath().equals(targetXPath)) {
			cumulativeMapping.removeManyToManyMapping(assoM);
			return true;
		}
	}
	return false;
}
/**
 * @param pathToAttribute
 * @return TableMetadata
 */
public static TableMetadata getParentTableMetadata(String pathToAttribute){
	TableMetadata table = null;
	int end = pathToAttribute.lastIndexOf(".");
	table = (TableMetadata)metaMap.get(pathToAttribute.substring(0,end));
	return table;
}

/**
 * @param element
 * @return boolean
 */
public static boolean isClass(String element){
	UMLClass clazz = null;
	boolean isClass = false;
	String[] modelElements = element.split("\\.");
	clazz = findClass(model, modelElements, 0, modelElements.length-1);
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
public static boolean isAttribute(String element){
	boolean isAttribute = false;
	UMLAttribute attribute = null;
	UMLClass clazz = null;
	String[] modelElements = element.split("\\.");
	clazz = findClass(model, modelElements, 0, modelElements.length-2);
	if (clazz!=null){
		LinkedHashMap modelMeta = metaModel.getModelMetadata();
		if (modelMeta.get(element)!= null) {
			if (modelMeta.get(element) instanceof AttributeMetadata) return true;
			else if (modelMeta.get(element) instanceof ColumnMetadata) return true;
			else return false;
		}
	}
//		 for(UMLAttribute att : clazz.getAttributes()) {
//			  if (att.getName().equals(modelElements[modelElements.length-1])) {
//				  attribute = att;
//			  }
//		 }
//	}
//    if (attribute !=null){
//    	isAttribute = true;
//    }
	return isAttribute;
}

/**
 * @param element
 * @return boolean
 */
public static boolean isSingleAssociation(String element) {
	boolean isSingleAssociation = false;
	UMLAssociationEnd end = null;
	UMLClass clazz = null;
	String[] modelElements = element.split("\\.");
	clazz = findClass(model, modelElements, 0, modelElements.length-2);
	if (clazz!=null){
		 for(UMLAssociation association : clazz.getAssociations()) {
			 for(UMLAssociationEnd associationEnd : association.getAssociationEnds()) {
			  if (associationEnd.getRoleName().equals(modelElements[modelElements.length-1]) && associationEnd.getHighMultiplicity()< 2 && associationEnd.getHighMultiplicity()!= -1) {
				  end = associationEnd;
			  }
		    }
		 }
	}
    if (end !=null){
    	isSingleAssociation = true;
    }
	return isSingleAssociation;
	}

/**
 * @param element
 * @return boolean
 */
public static boolean isOneToManyAssociation(String element){
	boolean isManyToManyAssociation = false;
	UMLAssociationEnd end = null;
	UMLClass clazz = null;
	String[] modelElements = element.split("\\.");
	String thisEndRoleName = modelElements[modelElements.length-1];
	clazz = findClass(model, modelElements, 0, modelElements.length-2);
	if (clazz!=null){
		 for(UMLAssociation association : clazz.getAssociations()) {
			 for(UMLAssociationEnd associationEnd : association.getAssociationEnds()) {
				 if (associationEnd.getRoleName().equals(thisEndRoleName) && getOtherAssociationEnd(associationEnd).getHighMultiplicity()>=0) {
				  end = associationEnd;
			  }
		    }
		 }
	}
    if (end !=null){
    	isManyToManyAssociation = true;
    }
	return isManyToManyAssociation;
	}

/**
 * @param element
 * @return boolean
 */
public static boolean isManyToManyAssociation(String element){
	boolean isManyToManyAssociation = false;
	UMLAssociationEnd end = null;
	UMLClass clazz = null;
	String[] modelElements = element.split("\\.");
	String thisEndRoleName = modelElements[modelElements.length-1];
	clazz = findClass(model, modelElements, 0, modelElements.length-2);
	if (clazz!=null){
		 for(UMLAssociation association : clazz.getAssociations()) {
			 for(UMLAssociationEnd associationEnd : association.getAssociationEnds()) {
				 if (associationEnd.getRoleName().equals(thisEndRoleName) && getOtherAssociationEnd(associationEnd).getHighMultiplicity()==-1) {
				  end = associationEnd;
			  }
		    }
		 }
	}
    if (end !=null){
    	isManyToManyAssociation = true;
    }
	return isManyToManyAssociation;
	}

/**
 * @param element
 * @return boolean
 */
public static boolean isForeignKey(String element){
	boolean isForeignKey = false;
	if (metaMap.get(element)!=null){

	}
	if (metaMap.get(element).getClass().getName().equals("ColumnMetadata")) {
		ColumnMetadata col = (ColumnMetadata)metaMap.get(element);
		if (col.isForeignKey()) {
			isForeignKey = true;
		}
	}
	return isForeignKey;
}

/**
 * @param element
 * @return boolean
 */
public static boolean isCorrelationTable(String element){
	boolean isCorrelationTable = false;
	UMLClass clazz = null;
	String[] modelElements = element.split("\\.");
	clazz = findClass(model, modelElements, 0, modelElements.length-1);
	if (clazz != null){
	//If clazz is not null then we have a UMLClass
	//Then we check the Metadata model to see if the Class is a table
	//which it should be, then we need to check that the table is of type "correlation".
	//If it is a correlation table then this is a many to many mapping situation.
		if (metaMap.get(element).getClass().getName().equals("TableMetadata")) {
			TableMetadata table = (TableMetadata)metaMap.get(element);
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
private static UMLClass findClass(UMLModel model, String[] className, int start, int end)
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
private static UMLClass findClass(UMLPackage pkg, String[] className, int start, int end) {
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
 * @return CumulativeMapping
 */
public static CumulativeMapping getCumulativeMapping() {
	return cumulativeMapping;
}

/**
 * @param cumulativeMapping
 */
public static void setCumulativeMapping(CumulativeMapping cumulativeMapping) {
	cumulativeMapping = cumulativeMapping;
}


public static UMLAssociation getAssociationFromColumn(String column)
{
	List<SingleAssociationMapping> singleAssocs = cumulativeMapping.getSingleAssociationMappings();
	for (SingleAssociationMapping singleAssoc : singleAssocs)
	{
		String name = singleAssoc.getColumnMetadata().getParentXPath() + "." + singleAssoc.getColumnMetadata().getName();
		if (name.equals(column))
		{
			return singleAssoc.getAssociationEndMetadata().getUMLAssociation();
		}
	}
	List <ManyToManyMapping> many2manys = cumulativeMapping.getManyToManyMappings();
	for(ManyToManyMapping many2many : many2manys)
	{
		String name = many2many.getThisEndColumn().getParentXPath() + "." + many2many.getThisEndColumn().getName();
		if (name.equals(column))
		{
			return many2many.getAssociationEndMetadata().getUMLAssociation();
		}
		if ( many2many.getOtherEndColumn() == null ) continue;
		name = many2many.getOtherEndColumn().getParentXPath() + "." + many2many.getOtherEndColumn().getName();
		if (name.equals(column))
		{
			return many2many.getOtherAssociationEndMetadata().getUMLAssociation();
		}
	}
	return null;
}

public static String getColumnFromAssociation(UMLAssociation association)
{
	List<SingleAssociationMapping> singleAssocs = cumulativeMapping.getSingleAssociationMappings();
	for (SingleAssociationMapping singleAssoc : singleAssocs)
	{
		if (singleAssoc.getAssociationEndMetadata().getUMLAssociation() == association)
		{
			return singleAssoc.getColumnMetadata().getParentXPath() + "." + singleAssoc.getColumnMetadata().getName();
		}
	}
	List <ManyToManyMapping> many2manys = cumulativeMapping.getManyToManyMappings();
	for(ManyToManyMapping many2many : many2manys)
	{
		if (many2many.getAssociationEndMetadata().getUMLAssociation()==association)
		{
			return many2many.getThisEndColumn().getParentXPath() + "." + many2many.getThisEndColumn().getName();
		}

		if (many2many.getOtherAssociationEndMetadata().getUMLAssociation() == association)
		{
			return many2many.getOtherEndColumn().getParentXPath() + "." + many2many.getOtherEndColumn().getName();
		}
	}
	return null;
}


/**
	 * @param args
	 */
	public static void main(String[] args) {
      CumulativeMappingGenerator x = new CumulativeMappingGenerator("C:/sample.xmi");

      x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Gene","Logical View.Data Model.GENE" );
      x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Taxon","Logical View.Data Model.TAXON");
      x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Clone","Logical View.Data Model.CLONE");
      x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Chromosome","Logical View.Data Model.CHROMOSOME");
      x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Sequence","Logical View.Data Model.SEQUENCE");
  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Target","Logical View.Data Model.TARGET");
  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Library","Logical View.Data Model.LIBRARY");

  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Taxon.id","Logical View.Data Model.TAXON.TAXON_ID");
  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Clone.id","Logical View.Data Model.CLONE.CLONE_ID");
  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Library.id","Logical View.Data Model.LIBRARY.LIBRARY_ID");
  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Target.id","Logical View.Data Model.TARGET.TARGET_ID");
  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Gene.id","Logical View.Data Model.GENE.GENE_ID");
  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Sequence.id","Logical View.Data Model.SEQUENCE.SEQUENCE_ID");
  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Chromosome.id","Logical View.Data Model.CHROMOSOME.CHROMOSOME_ID");

  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Gene.chromosome","Logical View.Data Model.GENE.CHROMOSOME_ID");
  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Chromosome.taxon","Logical View.Data Model.CHROMOSOME.TAXON_ID");
  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Sequence.clone","Logical View.Data Model.SEQUENCE.CLONE_ID");
  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Clone.library","Logical View.Data Model.CLONE.LIBRARY_ID");

  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Gene.sequenceCollection","Logical View.Data Model.GENE_SEQUENCE.GENE_ID");
  	  x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Sequence.geneCollection","Logical View.Data Model.GENE_SEQUENCE.SEQUENCE_ID");

  	  CumulativeMapping y = x.getCumulativeMapping();
	}

	public static String getErrorMessage() {
		return errorMessage;
	}

	public static void setErrorMessage(String errorMessage) {
		CumulativeMappingGenerator.errorMessage = errorMessage;
	}


}
