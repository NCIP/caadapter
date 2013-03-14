/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.mms.generator;
import gov.nih.nci.caadapter.mms.map.AttributeMapping;
import gov.nih.nci.caadapter.mms.map.CumulativeMapping;
import gov.nih.nci.caadapter.mms.map.DependencyMapping;
import gov.nih.nci.caadapter.mms.map.AssociationMapping;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.common.metadata.ColumnMetadata;
import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.common.metadata.TableMetadata;
import gov.nih.nci.caadapter.common.util.Iso21090Util;
import gov.nih.nci.caadapter.mms.validator.AttributeMappingValidator;
import gov.nih.nci.caadapter.mms.validator.DependencyMappingValidator;
import gov.nih.nci.caadapter.mms.validator.SingleAssociationMappingValidator;
import gov.nih.nci.caadapter.ui.common.Iso21090uiUtil;
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
 * @param annotationSite path the annotation element for ISO 21090 datatype annotation
 * @param relativePath relative path of the current element to the annotation element
 * @return boolean
 */
public boolean unmap(String source, String target, String annotationSite, String relativePath){

	boolean successfullyUnmapped = false;
	String sourceMappingType = determineSourceMappingType(source);
	String targetMappingType = determineTargetMappingType(target);

	if (sourceMappingType.equals("dependency")&& targetMappingType .equals("dependency")){
		UMLClass sourceClass = getClass(source);
		UMLClass targetClass = getClass(target);
		successfullyUnmapped = unmapDependency(sourceClass, source, targetClass, target);
	} else if (sourceMappingType .equals("attribute") && targetMappingType.equals("attribute")) {
		if (annotationSite==null||annotationSite.equals(""))
			successfullyUnmapped = unmapAttribute(source,relativePath, target);
		else
			successfullyUnmapped = unmapAttribute(annotationSite,relativePath, target);
	} else if (sourceMappingType.equals("association")&& targetMappingType.equals("attribute")) {
		if (annotationSite==null||annotationSite.equals(""))
			successfullyUnmapped = unmapAssociation(source,relativePath, target);
		else
			successfullyUnmapped = unmapAssociation(annotationSite,relativePath, target);
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
 * @param annotationSite path the annotation element for ISO 21090 datatype annotation
 * @param relativePath relative path of the current element to the annotation element
 * @param updateModel If the underneath UML should be updated as creating a new mapping
 * @return boolean
 */
public boolean map(String source, String target, String annotationSite, String relativePath, boolean updateModel){

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
		successfullyMapped = mapAttribute(source, target,annotationSite, relativePath, updateModel);
		if(!successfullyMapped)
			return successfullyMapped;
		if (!updateModel)
			return successfullyMapped;
		if (relativePath==null||relativePath.equals(""))
			return successfullyMapped;
		//additional work for ISO datatype -- map collection element with/without join table
		//case I: the ancestor annotation attribute is mapped
		//        set both "mapped-collection-table" and "correlation-table"
		//        -- no action
		//case II: the ancestor annotation attribute is not mapped
		//        neither set "mapped-collection-table" nor "correlation-table"
		//		  -- no action
		//case III the ancestor annotation attribute is mapped
		//        only set  "mapped-collection-table"
		//case III.a. the attribute is mapped to the same target table
		//       -- no action, return
		//case III.b. the attribute is mapped to a different table
		//       set "correlation-table" with previous "mapped-collection-table"
		//       and change "mapped-collection-table" value to current table name

		UMLAttribute annotationAttr=ModelUtil.findAttribute(metaModel.getModel(), annotationSite);
		if (annotationAttr.getTaggedValue("correlation-table")!=null)
			//Case I:
			return successfullyMapped;
		if (annotationAttr.getTaggedValue("mapped-collection-table")==null)
			//Case II:
			return successfullyMapped;

		//Case III-- only "mapped-collection-table" is set
		ColumnMetadata columnMeta=(ColumnMetadata)metaModel.getModelMetadata().get(target);
		String tblName=columnMeta.getTableMetadata().getName();
		String tblCollectionName=annotationAttr.getTaggedValue("mapped-collection-table").getValue();
		if (tblName.equals(tblCollectionName))
			//Case III.a:
			return successfullyMapped;

		//Case III.b, set "correlation-table" and change "mapped-collection-table"
		XMIAnnotationUtil.addTagValue(annotationAttr, "correlation-table", tblCollectionName);
		XMIAnnotationUtil.addTagValue(annotationAttr, "mapped-collection-table", tblName);
	} else if (sourceMappingType.equals("association")&& targetMappingType.equals("attribute")) {
		//Then the actual components from the UML model are realized
		successfullyMapped = mapAssociation(source, target, annotationSite, relativePath, updateModel);
		if(!successfullyMapped)
			return successfullyMapped;

		if (!updateModel)
			return successfullyMapped;

		//additional work for ISO datatype -- map collection element with/without join table
		//case I: no child attribute being mapped -- no action return
		//case II: child attribute are mapped to the same target table -- no action since it is "mapped-collection-table"
		//case III: child attribute are mapped to the different table
		//        set "correlation-table" with previous "mapped-collection-table"
		//        and change "mapped-collection-table" value to table name of child mapping target.
		ColumnMetadata columnMeta=(ColumnMetadata)metaModel.getModelMetadata().get(target);
 		List<MetaObject>  allMappedTargetMeta=cumulativeMapping.findMappedSourceOrChild(source);
		if (allMappedTargetMeta==null||allMappedTargetMeta.isEmpty())
			return successfullyMapped;

		ColumnMetadata mappedColumnInOtherTable=null;
		for (MetaObject oneTrgtMeta:allMappedTargetMeta)
		{

			if (oneTrgtMeta instanceof ColumnMetadata)
			{
				ColumnMetadata oneTrgtColumn=(ColumnMetadata)oneTrgtMeta;
				if (!oneTrgtColumn.getParentXPath().equals(columnMeta.getParentXPath()))
				{
					mappedColumnInOtherTable=oneTrgtColumn;
					break;
				}
			}
		}
		if (mappedColumnInOtherTable==null)
			//case I and II
			return successfullyMapped;

		//case III
		String otherTblPath=mappedColumnInOtherTable.getParentXPath();
		TableMetadata otherTblMeta=(TableMetadata)metaModel.getModelMetadata().get(otherTblPath);
		UMLAttribute annotationAttr=ModelUtil.findAttribute(metaModel.getModel(), annotationSite);
		//"mapped-collection-table" just being set in mapAssociation() call
		//copy it to "correlation-table"
		XMIAnnotationUtil.addTagValue(annotationAttr, "correlation-table", annotationAttr.getTaggedValue("mapped-collection-table").getValue());
		//change "mapped-collection-table" to the other table
		XMIAnnotationUtil.addTagValue(annotationAttr, "mapped-collection-table", otherTblMeta.getName());
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
private boolean mapAttribute(String sourcePath, String targetPath, String annotationPath, String relativePath, boolean updateModel){

	LinkedHashMap modelMeta = metaModel.getModelMetadata();
	AttributeMetadata attributeMetadata = (AttributeMetadata)modelMeta.get(annotationPath);//.get(sourcePath);

	ColumnMetadata columnMetadata = (ColumnMetadata)modelMeta.get(targetPath);
	boolean successfullyMapped = false;
	AttributeMapping mapping = new AttributeMapping();

	columnMetadata.setType(columnMetadata.TYPE_ATTRIBUTE);
	mapping.setAttributeMetadata(attributeMetadata);
	mapping.setColumnMetadata(columnMetadata);

	AttributeMappingValidator validator = new AttributeMappingValidator(mapping);
	successfullyMapped = validator.isValid();
	if (successfullyMapped) {
		if (relativePath==null||relativePath.equals(""))
			cumulativeMapping.addAttributeMapping(mapping, sourcePath);
		else
			cumulativeMapping.addAttributeMapping(mapping, annotationPath+"."+relativePath);

		if (updateModel)
		{
			UMLAttribute xpathUMLAttribute=ModelUtil.findAttribute(metaModel.getModel(),columnMetadata.getXPath());
			//remove the leading string:"Logical View.Logical Model." from source path
			String pureSrcPath="";

			if (relativePath==null|relativePath.equals(""))
				pureSrcPath=XMIAnnotationUtil.getCleanPath(metaModel.getMmsPrefixObjectModel(),  sourcePath);
			else
				pureSrcPath=XMIAnnotationUtil.getCleanPath(metaModel.getMmsPrefixObjectModel(),  annotationPath)
					+"."+relativePath;
			XMIAnnotationUtil.addTagValue(xpathUMLAttribute, "mapped-attributes", pureSrcPath);
		}
	}
	else {
		setErrorMessage(validator.getValidationErrorMessage());
	}
	return successfullyMapped;
}
/**
 * @param annotationPath
 * @param relativePath
 * @param targetPath
 */
private boolean unmapAttribute(String annotationPath, String relativePath, String targetPath){
	List<AttributeMapping> attributeMapping = cumulativeMapping.getAttributeMappings();
	for (AttributeMapping attr : attributeMapping) {
//		if (attr.getAttributeMetadata().getXPath().equals(sourcePath) && attr.getColumnMetadata().getXPath().equals(targetPath)) {
		if (attr.getColumnMetadata().getXPath().equals(targetPath)) {
			if (relativePath==null|relativePath.equals(""))
				cumulativeMapping.removeAttributeMapping(attr, annotationPath);
			else
				cumulativeMapping.removeAttributeMapping(attr, annotationPath+"."+relativePath);

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
private boolean mapAssociation(String sourceXPath, String targetXPath, String annotationPath, String relativePath, boolean updateModel){

	LinkedHashMap modelMeta = metaModel.getModelMetadata();
	AssociationMetadata sourceMetadata =null;
	MetaObject metaSrc=	(MetaObject)modelMeta.get(sourceXPath);
	if (metaSrc!=null&&(metaSrc instanceof AssociationMetadata ))
		sourceMetadata=(AssociationMetadata)metaSrc;
	ColumnMetadata targetMetadata = (ColumnMetadata)modelMeta.get(targetXPath);
	boolean successfullyMapped = false;
	AssociationMapping mapping = new AssociationMapping();

	targetMetadata.setType(targetMetadata.TYPE_ASSOCIATION);
	mapping.setAssociationEndMetadata(sourceMetadata);
	mapping.setColumnMetadata(targetMetadata);

	SingleAssociationMappingValidator validator = new SingleAssociationMappingValidator(mapping);
	successfullyMapped = validator.isValid();
	if (successfullyMapped) {
//		cumulativeMapping.addAssociationMapping(mapping);
		if (relativePath==null||relativePath.equals(""))
			cumulativeMapping.addAssociationMapping(mapping, sourceXPath);
		else
			cumulativeMapping.addAssociationMapping(mapping, annotationPath);

		if (updateModel)
		{

			if (relativePath==null|relativePath.equals(""))
				XMIAnnotationUtil.annotateAssociationMapping(metaModel.getModel(),sourceXPath, targetXPath);
			else
				XMIAnnotationUtil.annotateAssociationMapping(metaModel.getModel(),annotationPath, targetXPath);
		}

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
private boolean unmapAssociation(String annotationPath, String relativePath, String targetXPath){
	List<AssociationMapping> singleAssociationMapping = cumulativeMapping.getAssociationMappings();
	for (AssociationMapping assoS : singleAssociationMapping) {
//		if (assoS.getAssociationEndMetadata().getXPath().equals(sourceXPath) && assoS.getColumnMetadata().getXPath().equals(targetXPath)) {
		if (assoS.getColumnMetadata().getXPath().equals(targetXPath))
		{
			if (relativePath==null|relativePath.equals(""))
			{
				cumulativeMapping.removeAssociationMapping(assoS, annotationPath);
				return XMIAnnotationUtil.deAnnotateAssociationMapping(metaModel.getModel(), annotationPath, targetXPath);
			}
			else
			{
				cumulativeMapping.removeAssociationMapping(assoS,  annotationPath);
				return XMIAnnotationUtil.deAnnotateAssociationMapping(metaModel.getModel(),  annotationPath, targetXPath);
			}
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
	Object foundObject=metaModel.getModelMetadata().get(element);
	if (foundObject==null)
		return false;

	if (foundObject instanceof AttributeMetadata)
	{
		if (Iso21090uiUtil.isCollectionDatatype((AttributeMetadata)foundObject))
			return false;
		else if (Iso21090uiUtil.isDatatypeWithCollectionAttribute((AttributeMetadata)foundObject))
			return false;
		//expand scope to treat all ISO complex type as association mapping
		//If an ISO complex is mapped to a table.column, it will create association mapping
		else if (Iso21090Util.iso21090ComplexTypes.contains(((AttributeMetadata)foundObject).getDatatype()))
			return false;
		else
			return true;
	}


	if(foundObject instanceof ColumnMetadata)
		return true;
	return false;
//
//	UMLAttribute xpathUMLAttribute=ModelUtil.findAttribute(metaModel.getModel(),element);
//	if (xpathUMLAttribute!=null)
//		return true;
//	//	UMLAttribute attribute = null;
//	UMLClass clazz = null;
//	String[] modelElements = element.split("\\.");
//	clazz = findClass(metaModel.getModel(), modelElements, 0, modelElements.length-2);
//	if (clazz!=null){
//		LinkedHashMap modelMeta = metaModel.getModelMetadata();
//		if (modelMeta.get(element)!= null) {
//			if (modelMeta.get(element) instanceof AttributeMetadata) return true;
//			else if (modelMeta.get(element) instanceof ColumnMetadata) return true;
//			else return false;
//		}
//	}
//	return isAttribute;
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

//      x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Gene","Logical View.Data Model.GENE", true );
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
