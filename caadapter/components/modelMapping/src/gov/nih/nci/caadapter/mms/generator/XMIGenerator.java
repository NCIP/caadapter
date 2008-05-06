/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.mms.generator;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.common.metadata.ColumnMetadata;
import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.mms.map.AttributeMapping;
import gov.nih.nci.caadapter.mms.map.CumulativeMapping;
import gov.nih.nci.caadapter.mms.map.DependencyMapping;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociation;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociationEnd;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLDependency;
import gov.nih.nci.ncicb.xmiinout.domain.UMLGeneralization;
import gov.nih.nci.ncicb.xmiinout.domain.UMLModel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLPackage;
import gov.nih.nci.ncicb.xmiinout.domain.UMLTaggedValue;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLClassBean;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLPackageBean;
import gov.nih.nci.ncicb.xmiinout.handler.XmiInOutHandler;
import gov.nih.nci.ncicb.xmiinout.util.ModelUtil;

import java.io.*;
import java.util.*;

import org.jdom.*;
import org.jdom.filter.ElementFilter;
import org.jdom.input.*;

/**
 * The purpose of this class is to add tagged values and dependencies to
 * an xmi file based on the contents of a source to target mapping file.
 * @version 1.0
 * @created 11-Aug-2006 8:18:19 AM
 */
public class XMIGenerator 
{
    private String mappingFileName;
    private String xmiFileName;
    
    private List dependencies = null;
    private List attributes = null;
    private List associations = null;
    private List manytomanys = null;
    
    private XmiInOutHandler handler = null;
	private UMLModel model = null;
	private Document doc = null;
    private HashMap dependencyMap = new HashMap();
	private ModelMetadata modelMetadata = null;
	private LinkedHashMap myMap = null;

    private static HashSet<String> primaryKeys = new HashSet<String>();
	private static HashSet<String> lazyKeys = new HashSet<String>();
	private static HashSet<String> clobKeys = new HashSet<String>();
    private static HashSet<String> discriminatorKeys = new HashSet<String>();
    private static Hashtable<String, String> discriminatorValues = new Hashtable<String, String>();

    public XMIGenerator(){
	}
    
	
	public XMIGenerator(String mappingFile, String xmiFile)
	{
		this.mappingFileName = mappingFile;
		this.xmiFileName = xmiFile;
	}
	
	private void init() 
	{
	    try {
		    modelMetadata = ModelMetadata.getInstance();

		    if (modelMetadata == null) 
		    {
		    	ModelMetadata.createModel(xmiFileName);
			    modelMetadata = ModelMetadata.getInstance();			    
		    }
					    
		    myMap = modelMetadata.getModelMetadata();					    
		    model = modelMetadata.getModel();
		    
		    //load all primaryKeys
		    primaryKeys = modelMetadata.getPrimaryKeys();
		    lazyKeys = modelMetadata.getLazyKeys();
		    clobKeys = modelMetadata.getClobKeys();
            discriminatorKeys = modelMetadata.getDiscriminatorKeys();
            discriminatorValues = modelMetadata.getDiscriminatorValues();
// changes made by Sandeep Phadke on 5/2/2008 
// The dependencies were not being removed if file came from EA, so commented the following
// for loop and the clear method to remove from model. Added new method addTableClassDependency
// to check dependency either in package or class DomElements. If it doesn't contain in cumulative
// mapping then remove it.
            // Remove all dependencies from the Model
//		    for ( UMLDependency dep : model.getDependencies() )
//		    {		    	
//				model.getDependencies().remove(dep);//.removeDependency( dep );
//            }
//		    model.getDependencies().clear();
		    //model.emptyDependency();
//////////////// Removing the existing dependencies          
            boolean isDependencyMapped = false;
    	    List <UMLDependency> existingDeps = model.getDependencies();
    	    UMLClass clientEnd;
	    	String asscClient, className ;
	    	int i=0;
	    	for(i = (existingDeps.size()-1); i>=0; i--)
    	    {
    	    	UMLDependency oldDep = existingDeps.get(i);
    	    	isDependencyMapped = false;
    	    	clientEnd =  ((UMLClass) oldDep.getClient());
    	    	asscClient = clientEnd.getName();

    	    	String pathKey = new String(ModelUtil.getFullPackageName(clientEnd));
    	    	pathKey = pathKey + "." + clientEnd.getName();
    	    	// if this dependency exists in cumulative mapping, then don't remove.
    	    	if (!checkCumulativeDependency(pathKey)) {
	    	    	for( UMLPackage pkg : model.getPackages() ) 
	    			{
	    	    		checkTableClassDependency(pkg, asscClient, oldDep );
//	    				UMLPackageBean pkgBean=(UMLPackageBean)pkg;
//	    				Element pkgElm=   pkgBean.getJDomElement();
//	    				removeDepElement(pkgElm, oldDep, asscClient);
	    			}
    	    	}
    	    }
///////////////// End changes 5/2/2008           
		    
			for( UMLPackage pkg : model.getPackages() ) 
			{
				getPackages( pkg );
			}								
			
			handler = modelMetadata.getHandler();
	    } 
	    catch (Exception e) 
	    {
	      e.printStackTrace();
	    }
	}

	public void checkTableClassDependency(UMLPackage pkg, String checkClass, UMLDependency oldDep ){
// new method to check for the dependency for the table/Class. It checks it in UMLclass and UMLpackage
// becuase EA keeps it in Class and XMI jar saves it under package. So need to check at both places.		
// created by Sandeep on 4/28/08.		
		for ( UMLClass clazz : pkg.getClasses() )
		{

			if (clazz.getName().equals(checkClass)) {
				
				System.out.println("XMIGenerator.getPackages()..class:"+clazz.getName() +"remove dependency"+clazz.getDependencies());
				UMLClassBean classBean=(UMLClassBean)clazz;
				Element clazzElm=   classBean.getJDomElement();
				Element parentElm = clazzElm.getParentElement();
				parentElm.removeChild("Dependency",parentElm.getNamespace() );
				//also remove it from the model, becuase while saving and 
				//adding dependency back, we check for its existance in the model too.
				model.getDependencies().remove(oldDep);//.removeDependency( dep );
			}
		}
		for ( UMLPackage pkg2 : pkg.getPackages() )
		{
			checkTableClassDependency( pkg2, checkClass, oldDep );
//			UMLPackageBean pkgBean=(UMLPackageBean)pkg2;
//			Element pkgElm=   pkgBean.getJDomElement();
//			removeDepElement(pkgElm, oldDep, checkClass);
		}
	}
		
	public void removeDepElement(Element elm, UMLDependency oldDep, String checkClass){
	// removes the DepElement from any DOM element and its children.	
		List<Element> childrenList = elm.getChildren();
		int i=0;
		for(i = (childrenList.size()-1); i>=0; i--){
			Element childElm = childrenList.get(i);
			String elmName = childElm.getName();
			if (elmName.equals("Dependency")){
				Element parentElm= childElm.getParentElement();
				String parentName = parentElm.getName();
				if (parentName.equals(checkClass)){
					parentElm.removeChild("Dependency",parentElm.getNamespace() );
					model.getDependencies().remove(oldDep);
				}
			} else {
				removeDepElement(childElm, oldDep, checkClass);
			}
		}
	}

	public boolean addTableClassDependency(String xPath){
// created by Sandeep 5/2/2008		
// while saving the XMI checks if the dependecy should be added to the model or not
// since model has been cleaned earlier in init, it may/may not have any depenedency, the Cumulative mapping
// stores the current status, so if user deletes a mapping or adds it in the current session
// it is captured in cumulative mapping. While saving model mapping gets saved. So this syncs both.
// In addition, it should not have same mapping twice so checks no duplicate exists. 		
//  		
//	CumulativeDepExist     ModelDependeciesExists      Result(AddDependency)
//		Y					N						->		Y
//		Y					Y						->		N
//		N					N						->		N
//		N					Y						->		N
		
		
    	boolean modelDepExists = false, addDependencies = false; 
		CumulativeMapping cummulativeMapping = null;
		try {
			cummulativeMapping = CumulativeMapping.getInstance();
		} catch (Exception e) {
			
		}

		List<DependencyMapping> dependencyMapping = cummulativeMapping.getDependencyMappings();
		for (DependencyMapping d : dependencyMapping) {
			try {
				String targetXPath = d.getTargetDependency().getXPath(); 
				if (targetXPath.contains(xPath)){
					modelDepExists = false;
					
					List<UMLDependency> modelMappings = model.getDependencies();
					Iterator itrModel = modelMappings.iterator();
					while (itrModel.hasNext()) {
						try {
							UMLDependency dep = (UMLDependency)itrModel.next();	
							UMLClass depTarget = (UMLClass) dep.getClient();
					    	String pathKeyTarget = new String(ModelUtil.getFullPackageName(depTarget));
					    	pathKeyTarget = pathKeyTarget + "." + depTarget.getName();
							if (pathKeyTarget.equals(xPath)){
								// since dependency already exist in model, dont add again.
								modelDepExists = true;
								break;
							}
						} catch (Exception e) {
							
						}
					}
					if (modelDepExists) {
						addDependencies = false;
					}
					else{
						addDependencies = true;
					}
					break;
					
				} else {
					addDependencies = false;
				}
			} catch (Exception e) {
		            e.printStackTrace();
			}
		}
		return addDependencies;
	}	

	
	public boolean checkCumulativeDependency(String xPath){
		// created by Sandeep 5/5/2008		
// checks if the current xPath mapping exists in Cumulativemapping				
				
		    	boolean mappingExists = false ;
				CumulativeMapping cummulativeMapping = null;
				try {
					cummulativeMapping = CumulativeMapping.getInstance();
				} catch (Exception e) {
					
				}

				List<DependencyMapping> dependencyMapping = cummulativeMapping.getDependencyMappings();
				for (DependencyMapping d : dependencyMapping) {
					try {
						String targetXPath = d.getTargetDependency().getXPath(); 
						if (targetXPath.contains(xPath)){
							mappingExists = true;
							break;
						}
					} catch (Exception e) {
				            e.printStackTrace();
					}
				}
				return mappingExists;
			}		
	
	public void getPackages( UMLPackage pkg )
	{
		for ( UMLClass clazz : pkg.getClasses() )
		{
			for( UMLTaggedValue tagValue : clazz.getTaggedValues() )
			{
				if( tagValue.getName().contains( "discriminator" ))
				{
					clazz.removeTaggedValue( "discriminator" );
				}
			}
			
			for( UMLAttribute att : clazz.getAttributes() ) 
			{
				for( UMLTaggedValue tagValue : att.getTaggedValues() )
				{
					if( tagValue.getName().contains( "id-attribute" ))
					{
						att.removeTaggedValue( "id-attribute" );
					}
                    
                    if( tagValue.getName().contains( "mapped-attributes" ))
					{
						att.removeTaggedValue( "mapped-attributes" );
					}
					if( tagValue.getName().contains( "implements-association" ))
					{
						att.removeTaggedValue( "implements-association" );
					}
					if( tagValue.getName().contains( "correlation-table" ))
					{
						att.removeTaggedValue( "correlation-table" );
					}	
					// commented by Sandeep on 5/8/08 for bug id 12958 per Eugene's instructions.
//					if( tagValue.getName().contains( "inverse-of" ))
//					{
//						att.removeTaggedValue( "inverse-of" );
//					}
					if( tagValue.getName().contains( "type" ))
					{
                        if( tagValue.getValue().equalsIgnoreCase("CLOB"))
                        {
                           att.removeTaggedValue( "type" );
                        }                        
					}
					if( tagValue.getName().contains( "discriminator" ))
					{
						att.removeTaggedValue( "discriminator" );
					}
				}
			}				
			for( UMLAssociation assc : clazz.getAssociations()) 
			{
				for( UMLTaggedValue tagValue : assc.getTaggedValues() )
				{
					if( tagValue.getName().contains( "lazy-load" ))
					{
						assc.removeTaggedValue( "lazy-load" );
					}
					if( tagValue.getName().contains( "correlation-table" ))
					{
						assc.removeTaggedValue( "correlation-table" );
					}												
				}
			}
		}
		
		for ( UMLPackage pkg2 : pkg.getPackages() )
		{
			getPackages( pkg2 );
		}
	}
	
	/**
	 * 
	 * 
	 */
	public void annotateXMI()
	{
        init();
		annotateXMIFile();
		saveModel();
    }
	
	/**
	 * 
	 */
	public void annotateXMIFile()
	{
		loadLinks();
		//Add the Primary Keys
		for( String pKey : primaryKeys )
		{
			addPrimaryKey( pKey );
		}
		
		//Add the Lazy Keys
		for( String lKey : lazyKeys )
		{
			addLazyKey( lKey );
		}

        for( String cKey : clobKeys )
        {
            addClobKey( cKey );
        }

        for ( String dKey : discriminatorKeys )
        {
            addDiscriminatorKey( dKey );
        }

        for ( String dKey : discriminatorValues.keySet() )
        {
            addDiscriminatorValues( dKey );
        }

        addDependencies(this.dependencies);
		addAttributeTaggedValues(this.attributes);
		addAssociationTaggedValues(this.associations);
		addManyToManyTaggedValues(this.manytomanys);
		
		//deleteMappingFile();
	}
	
	/**
	 * 
	 *
	 */
	public void deleteMappingFile()
	{
	    boolean success = (new File(this.mappingFileName)).delete();
	    if (!success) 
	    {
	    	// Deletion failed
	        System.out.println( "Error, Deletion of " + this.mappingFileName + " Failed!");
	    }
	    
	}
	
	/**
	 * @param attributes
	 */
	public void addAttributeTaggedValues( List attributes )
	{
		for (int i = 0; i < attributes.size(); i++)
		{			 
			Element attribute = (Element)attributes.get(i);	
			addAttributeTaggedValue(this.model, attribute);
		}
	}
	
	/**
	 * @param associations
	 */
	public void addAssociationTaggedValues( List associations )
	{
		for (int i = 0; i < associations.size(); i++)
		{
			Element association = (Element)associations.get(i);
			addAssociatonTaggedValue(this.model, association);
		}
	}
	
	/**
	 * @param manytomanys
	 */
	public void addManyToManyTaggedValues(List manytomanys)
	{
		for (int i = 0; i < manytomanys.size(); i++)
		{
			Element manytomany = (Element)manytomanys.get(i);
			addManyToManyTaggedValue(this.model, manytomany);
		}
	}
	
	/**
	 * @param dependencies
	 */
	public void addDependencies(List dependencies) 
	{
		for (int i = 0; i < this.dependencies.size(); i++)
		{
			Element dependency = (Element)this.dependencies.get(i);
			addDependency(this.model,dependency);
		}
	}
		
	public void addObjectClass(UMLModel model, Element dependency)
	{
	}
	/**
	 * @param model
	 * @param dependency
	 */
	public void addDependency(UMLModel model, Element dependency)
	{
	    UMLClass client = null;
	    UMLClass supplier = null;
	    
        client = ModelUtil.findClass(model, dependency.getChildText("target"));
	    supplier = ModelUtil.findClass(model, dependency.getChildText("source"));
	    String pathKey = new String(ModelUtil.getFullPackageName(client));
    	pathKey = pathKey + "." + client.getName();	    
	    boolean addDependency = false;	    
//	    for(UMLDependency oldDep : deps) 
//	    {
//	    	if (((UMLClass)(oldDep.getClient())==client) && ((UMLClass)(oldDep.getSupplier())==supplier)) 
//	    	{
//	    		exist = true;
//	    	}
	    
	    addDependency = addTableClassDependency(pathKey);
	    	if (!addDependency) return; 
//	    }
		    dependencyMap.put(dependency.getChildText( "target"), supplier );
		    UMLDependency dep = model.createDependency( client, supplier, "dependency" );
		    
		    dep = model.addDependency( dep );
		    // added new Stereotype for bug id 11561
		    dep.addStereotype("DataSource");
		    // end changes bug id 11561. By Sandeep on 5/2/08
		    dep.addTaggedValue("stereotype", "DataSource");
		    dep.addTaggedValue("ea_type", "Dependency");
		    // the following to tagged values may not be necessary
		    dep.addTaggedValue("direction", "Source -> Destination");
		    dep.addTaggedValue("style", "3");
	    
	}
		
	/**
	 * @param model
	 * @param attribute
	 */
	public void addAttributeTaggedValue(UMLModel model, Element attribute)
	{
	    UMLAttribute target = null;
	    target = ModelUtil.findAttribute(model, attribute.getChildText("target"));	 	   

	    //Check for dependency, if dependency does not exist, do not save attribute    
	    UMLClass supplier = null;
	    for ( UMLDependency dep : model.getDependencies() )
	    {
	    	supplier = (UMLClass) dep.getSupplier();
			StringBuffer pathKey = new StringBuffer(ModelUtil.getFullPackageName(supplier));			
			
			int lastDot = attribute.getChildText("source").lastIndexOf( "." );								
			String attr = attribute.getChildText("source").substring( 0, lastDot );	
			
			if( attr.equals( pathKey + "." +  supplier.getName() ))
			{
				target.addTaggedValue("mapped-attributes", getCleanPath(attribute.getChildText("source")));
			}
	    }	       	
	}
	
    static String replace(String str, String pattern, String replace) {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();
    
        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e+pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }
    
	/**
	 * @param model
	 * @param attribute
	 */
	public void addAssociatonTaggedValue(UMLModel model, Element attribute)
	{
	    UMLAttribute target = null;
	    target = ModelUtil.findAttribute(model, attribute.getChildText("target"));	 	    	    	    
	    
	    //Check for dependency, if dependency does not exist, do not save attribute    
	    UMLClass supplier = null;
	    
	    String sourceAttr = attribute.getChildText("source");
		int sourceLastDot = attribute.getChildText("source").lastIndexOf( "." );								
		String sourceClassName = attribute.getChildText("source").substring( 0, sourceLastDot );	
	    
	    
	    supplier = ModelUtil.findClass(model, sourceClassName);
	    
	    if (supplier == null) return;
		target.addTaggedValue("implements-association", getCleanPath(attribute.getChildText("source")));
		
		
		
		//Determine corraltionTable
	    String targetAttr = attribute.getChildText("target");
	    targetAttr = targetAttr.substring(0, targetAttr.lastIndexOf("."));
	    
	    UMLClass targetTable = ModelUtil.findClass(model, targetAttr);
	    
	    if (targetTable.getAttributes().size() != 2) return;
	    String columnName1 = targetAttr + "." + targetTable.getAttributes().get(0).getName();
		CumulativeMappingGenerator cumulativeMappingGenerator = CumulativeMappingGenerator.getInstance();
		UMLAssociation umlAssociation = cumulativeMappingGenerator.getAssociationFromColumn(columnName1);
		if (umlAssociation == null) return;
		
	    String columnName2 = targetAttr + "." + targetTable.getAttributes().get(1).getName();
		umlAssociation = cumulativeMappingGenerator.getAssociationFromColumn(columnName2);
		if (umlAssociation == null) return;
	    
		if (umlAssociation.getTaggedValue("correlation-table")== null) 
		{
		    targetAttr = targetAttr.substring(targetAttr.lastIndexOf(".")+1,targetAttr.length());	   
			umlAssociation.addTaggedValue("correlation-table", targetAttr);
		}
	}
	
	/**
	 * @param model
	 * @param attribute
	 */
	
	public void addManyToManyTaggedValue(UMLModel model, Element attribute)
	{
		//Adding tagged values for attributes and single associations is pretty straightforward, however
		//for many to many relationship its a little tricky. The caCORE sdk requires that the 
		//two columns of a correlaton table being used in a many to many relationship mapping
		//each have an "implements-association" tagged value. Additionally, one of the coluns, and
		//only one must also have an "inverse-of" tagged value. In the process of adding the 
		//tagged values the caAdapter tool must determine if either column in a correlation table
		//has an "inverse-of" tag value. If one is not found then one will be added.
		//The process used to determine that may need to be replicated in the hibernate mapping file
		//generation task because one end of the mapping file entry is slightly different then the other
		//so the system will been to be able to keep track of what type of entry in the hibernate
		//mapping file has been made.
		
	    UMLAttribute target = null;
	    boolean inverseofExists = false;
	    target = ModelUtil.findAttribute(model, attribute.getChildText("target"));
	    
	    // Remove all implements-association, correlation-table
	    for (Iterator it=target.getTaggedValues().iterator(); it.hasNext(); ) 
	    {
	    	UMLTaggedValue element = (UMLTaggedValue)it.next();	  
	    	if ( element.getName().equals("implements-association") )
	    	{
	    		target.removeTaggedValue( element.getName() );	    		
	    	}
	    	if ( element.getName().equals("correlation-table") )
	    	{
	    		target.removeTaggedValue( element.getName() );	    	
	    	}
	    	if ( element.getName().equals("inverse-of") )
	    	{
//		    	// commented by Sandeep per Eugene's instructions for bug id 12958. Instruction as follows:
//		    	// A “roleName is assigned with the “Many” end of association
////		    	•	Do not remove the existing Tag Value associated a table.column. It may be either assigned by user from EA or from caAdapter Mapping Tool.
////		    	•	Add Tag Value as a table.column is mapped as the “Many” end of an association and if no existing one associated with it.
////		    	•	Update Tag Valuewith correct value as update a mapping.
	////
////		    	No “roleName” being assigned with the “Many” end of an association. 
////		    	Currently, the Tag Value can only be created from EA since caAdapter annotation process does not have knowledge to determine if a table.column has been involved in an association. 
////		    	•	Leave the existing Tag Value intact.
////		    	•	New Tag Value can only be assigned by user from EA.
	    		// date: 5/6/2008
	    		
	    		inverseofExists = true;
	    		//target.removeTaggedValue( element.getName() );	    	
	    	}
	    }
	    	    
	    String sourceAttr = attribute.getChildText("source");
	    String targetAttr = attribute.getChildText("target");
	    
	    targetAttr = targetAttr.substring(0, targetAttr.lastIndexOf("."));
	    targetAttr = targetAttr.substring(targetAttr.lastIndexOf(".")+1,targetAttr.length());	   
	    
	    AssociationMetadata assoMeta = (AssociationMetadata)myMap.get(sourceAttr);
	    UMLAssociation asso = assoMeta.getUMLAssociation();
	    if (asso.getTaggedValue("correlation-table")== null) 
	    {
	    	asso.addTaggedValue("correlation-table", targetAttr);
	    }
	   
	    target.addTaggedValue("implements-association", getCleanPath(attribute.getChildText("source")));
	    // if inverseof already exists do not add again.
	    if(reciprolRoleHasInverseOfTag(attribute.getChildText("source")) == false && (!inverseofExists) )
	    {
	    	addInverseOfTagValue(target,attribute);
	    	saveModel();
	    }
	}	

	public void addInverseOfTagValue(UMLAttribute Target, Element attribute) 
	{
		//check to see if this Tag Value already exists
			Target.addTaggedValue( "inverse-of", getInverseRoleName(attribute.getChildText("source")) );
			saveModel();
	}
	
	/**
	 * @param roleName
	 * @return inverseRoleName
	 */
	public String getInverseRoleName(String roleName)
	{
		String inverseRoleName = getCleanPath(getRecipricolRolePath(roleName));
		return inverseRoleName;
	}

	/**
	 * @param pathToThisEnd
	 * @return hasInverseOfTagValue
	 */
	public boolean reciprolRoleHasInverseOfTag(String pathToThisEnd)
	{
		//pathToThisEnd is the object model path to the many to many role currently being mapped.
		//we need to determine if the database column that the reciprocol role is mapped to has 
		//an "inverse-of" tagged value, if not return false.
		boolean hasInverseOfTagValue = false;
		for (int i = 0; i < this.manytomanys.size(); i++)
		{
			Element manytomany = (Element)this.manytomanys.get(i);
			if (!manytomany.getChildText("source").equals(pathToThisEnd))
			{	
				hasInverseOfTagValue = checkInverseOfTagValue(manytomany.getChildText("target"));				
			}
		}
		return hasInverseOfTagValue;
	}

	/**
	 * @param pathToColumnName
	 * @return hasInverseOfTaggedValue
	 */
	public boolean checkInverseOfTagValue(String pathToColumnName)
	{
		boolean hasInverseOfTaggedValue = false;
		UMLAttribute column = ModelUtil.findAttribute(this.model, pathToColumnName);
		for(UMLTaggedValue taggedValue : column.getTaggedValues()) 
		{
			if (taggedValue.getName().equals("inverse-of")) 
			{
				hasInverseOfTaggedValue = true;
			}
		}
		return hasInverseOfTaggedValue;
	}

	public void addPrimaryKey( String pKey )
	{
        String primaryKey = modelMetadata.getMmsPrefixObjectModel() + "." + pKey;
		UMLAttribute column = ModelUtil.findAttribute(this.model, primaryKey);
        System.out.println("Looking for Attribute " + primaryKey);
        
        if ( column != null )
		{			
			column.addTaggedValue( "id-attribute", pKey );
		}
	}
	
	public void addLazyKey( String lKey )
	{
		String lazyKey = modelMetadata.getMmsPrefixDataModel() + "." + lKey;
		CumulativeMappingGenerator cumulativeMappingGenerator = CumulativeMappingGenerator.getInstance();
		UMLAssociation umlAssociation = cumulativeMappingGenerator.getAssociationFromColumn(lazyKey);
		
		if (umlAssociation != null)
		{
			umlAssociation.addTaggedValue("lazy-load", "No");
		}
	}

    public void addClobKey( String cKey )
	{
		String clobKey = modelMetadata.getMmsPrefixDataModel() + "." + cKey;
		UMLAttribute column = ModelUtil.findAttribute(this.model, clobKey);
        if( column != null)
		{
            column.addTaggedValue( "type", "CLOB" );
        }
	}

    public void addDiscriminatorKey( String dKey )
	{
		String discriminatorKey = modelMetadata.getMmsPrefixDataModel() + "." + dKey;
		UMLAttribute column = ModelUtil.findAttribute(this.model, discriminatorKey);
		String tableName = "."+dKey.substring(0, dKey.lastIndexOf('.'));
		
		String objectName = null;
		for (int i = 0; i < this.dependencies.size(); i++)
		{
			Element dependency = (Element)this.dependencies.get(i);
	        String target = dependency.getChildText("target");
		    String source = dependency.getChildText("source");
		    
		    if (target.endsWith(tableName)) {
		    	objectName = source;
		    }
		}	
		
        if (objectName!=null && column != null) {

			UMLClass clazz = ModelUtil.findClass(this.model, objectName);
			
			if (clazz == null) return;
 			
			UMLClass oldclazz = null;
			while (!(clazz == oldclazz))
			{
				oldclazz = clazz;
				List<UMLGeneralization> clazzGs = clazz.getGeneralizations();

				for (UMLGeneralization clazzG : clazzGs) {
					UMLClass parent =  (UMLClass)clazzG.getSupertype();
					if (parent != clazz) {
						clazz = parent;
						break;
					}
					else {
						oldclazz = clazz;
						break;
					}
				}
			}
			
			String packageName = "";
			UMLPackage umlPackage = clazz.getPackage();
			while (umlPackage != null)
			{
				packageName = umlPackage.getName() + "." + packageName;
				umlPackage = umlPackage.getParent();
			}
			
            packageName =  packageName + clazz.getName();
            
            column.addTaggedValue( "discriminator",  packageName.substring(modelMetadata.getMmsPrefixObjectModel().length()+1));
            //umlClass.addTaggedValue( "discriminator", dKey );
        }
	}
    
    public void addDiscriminatorValues( String dKey )
	{
		String discriminatorKey = modelMetadata.getMmsPrefixObjectModel() + "." + dKey;
		UMLClass clazz = ModelUtil.findClass(this.model, discriminatorKey);
        //UMLClass umlClass = ModelUtil.findClass(this.model, discriminatorKey);

        if( clazz != null)
		{
            clazz.addTaggedValue( "discriminator", discriminatorValues.get(dKey) );
            //umlClass.addTaggedValue( "discriminator", dKey );
        }
	}

    
    /**
	 * @param pathToThisEnd
	 * @return pathToOtherEnd
	 */
	public String getRecipricolRolePath(String pathToThisEnd)
	{
		StringBuffer pathToOtherEnd = new StringBuffer();
		int end = pathToThisEnd.lastIndexOf(".");
		String roleName = pathToThisEnd.substring(end+1);
		String umlClassNamePath = pathToThisEnd.substring(0,end);
		UMLClass clazz = ModelUtil.findClass(this.model,umlClassNamePath);
		UMLAssociation correctAssociation = null;
		
		for(UMLAssociation assoc : clazz.getAssociations()) 
		{
	        for(UMLAssociationEnd endAssociation : assoc.getAssociationEnds()) 
	        {
	        	if (endAssociation.getRoleName().equals(roleName)) 
	        	{
	        		UMLClass clazz12 = (UMLClass)endAssociation.getUMLElement();
	        		String path = ModelUtil.getFullName((UMLClass)endAssociation.getUMLElement());
					pathToOtherEnd.append(path);
					pathToOtherEnd.append(".");
	        		correctAssociation = endAssociation.getOwningAssociation();
	        	}
	        }
		}
		
		for (UMLAssociationEnd endAssociation1 : correctAssociation.getAssociationEnds()) {
			if (!endAssociation1.getRoleName().equals(roleName)){
				UMLClass clazz1 = (UMLClass)endAssociation1.getUMLElement();
				pathToOtherEnd.append(endAssociation1.getRoleName());
			}
		}
		
		return pathToOtherEnd.toString();
		
	}
	
	/**
	 * @param grossPath
	 * @return cleanPath
	 */
	public String getCleanPath(String grossPath)
    {
		String cleanPath = null;
		if (grossPath.startsWith( modelMetadata.getMmsPrefixObjectModel() ))
		{
		    cleanPath = grossPath.replaceAll( modelMetadata.getMmsPrefixObjectModel() + ".", "" );
		}
		return cleanPath;
	}
	
	public void saveModel() {
	    try 
	    {		  
	      handler.save(xmiFileName);      
	    } catch (Exception e){
	      e.printStackTrace();
	    } 
	  }
	
	/**
	 * This class reads the mapping file and adds Elements to 4 lists depending on the type of link.
	 */
	public void loadLinks()
	{
		 try 
		 {
		      // Request document building without validation
		      SAXBuilder builder = new SAXBuilder( false );
		      this.doc = builder.build( new File( this.mappingFileName ) );
		      
		      // Get the root element
		      Element root = doc.getRootElement();
		      ElementFilter links = new ElementFilter( "link" );
		      
		      List elements = root.getContent( links );
		      Iterator i = elements.iterator();
		      
		      this.dependencies = new ArrayList();
		      this.attributes = new ArrayList();
		      this.associations = new ArrayList();
		      this.manytomanys = new ArrayList();
		      
		      while ( i.hasNext() ) 
		      {
		        Element link = (Element) i.next();		        
		        if (link.getAttribute("type").getValue().equals("dependency")) 
		        {
		        	this.dependencies.add(link);
		        } 
		        else if (link.getAttribute("type").getValue().equals("attribute"))
		        {
		        	this.attributes.add(link);
		        } 
		        else if (link.getAttribute("type").getValue().equals("association")) 
		        {
		        	this.associations.add(link);
		        } 
		        else if (link.getAttribute("type").getValue().equals("manytomany"))
		        {
		        	this.manytomanys.add(link);
		        }
		      }
		    } catch (Exception e) {
		      e.printStackTrace();
		    }	
	}
	
	/**
	 * @return Returns the mappingFile.
	 */
	public String getMappingFile() {
		return mappingFileName;
	}
	
	/**
	 * @param mappingFile The mappingFile to set.
	 */
	public void setMappingFile(String mappingFile) {
		this.mappingFileName = mappingFile;
	}
	
	/**
	 * @return Returns the xmiFileName.
	 */
	public String getXmiFileName() {
		return xmiFileName;
	}
	
	/**
	 * @param xmiFileName The xmiFileName to set.
	 */
	public void setXmiFileName(String xmiFileName) {
		this.xmiFileName = xmiFileName;
	}
	  public static void main(String[] args) {
		  try {
			    XMIGenerator generator = new XMIGenerator("C:/Documents and Settings/Administrator/My Documents/MMs Example XMI/All_Mappings_test_validator.map","C:/Documents and Settings/Administrator/My Documents/MMs Example XMI/All_Mappings_test_validator.xmi");
		        generator.init();
		  } catch (Exception e){
			  e.printStackTrace();
		  }
	   
	  }
}