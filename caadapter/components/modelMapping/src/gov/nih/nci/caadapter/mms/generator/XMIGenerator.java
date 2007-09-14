package gov.nih.nci.caadapter.mms.generator;
import gov.nih.nci.caadapter.mms.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.mms.metadata.ModelMetadata;
import gov.nih.nci.caadapter.mms.metadata.ObjectMetadata;
import gov.nih.nci.ncicb.xmiinout.handler.HandlerEnum;
import gov.nih.nci.ncicb.xmiinout.handler.XmiException;
import gov.nih.nci.ncicb.xmiinout.handler.XmiHandlerFactory;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociation;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociationEnd;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLDependency;
import gov.nih.nci.ncicb.xmiinout.domain.UMLDependencyEnd;
import gov.nih.nci.ncicb.xmiinout.domain.UMLGeneralization;
import gov.nih.nci.ncicb.xmiinout.domain.UMLModel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLPackage;
import gov.nih.nci.ncicb.xmiinout.domain.UMLTaggedValue;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLClassBean;
import gov.nih.nci.ncicb.xmiinout.handler.XmiInOutHandler;
import gov.nih.nci.ncicb.xmiinout.util.ModelUtil;
import gov.nih.nci.caadapter.common.SDKMetaData;
import gov.nih.nci.caadapter.common.util.Config;
//import gov.nih.nci.caadapter.ui.common.preferences.PreferenceManager;

import java.io.*;
import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;

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
            
            // Remove all dependencies from the Model
		    for ( UMLDependency dep : model.getDependencies() )
		    {		    	
				model.removeDependency( dep );
            }
		    
		    model.emptyDependency();
		    
			for( UMLPackage pkg : model.getPackages() ) 
			{
				//System.out.println( "Package1: " + pkg.getName() );
				getPackages( pkg );
			}								
			
			handler = modelMetadata.getHandler();
	    } 
	    catch (Exception e) 
	    {
	      e.printStackTrace();
	    }
	}
		
	public void getPackages( UMLPackage pkg )
	{
		for ( UMLClass clazz : pkg.getClasses() )
		{
			System.out.println( "Class: " + clazz.getName() );
			for( UMLTaggedValue tagValue : clazz.getTaggedValues() )
			{
				if( tagValue.getName().contains( "discriminator" ))
				{
					//System.out.println( "Removing: " + tagValue.getName() + " " + tagValue.getValue() );
					clazz.removeTaggedValue( "discriminator" );
				}
			}
			
			
			
			for( UMLAttribute att : clazz.getAttributes() ) 
			{
				//System.out.println( "Attribute: " + att.getName() );
				
				for( UMLTaggedValue tagValue : att.getTaggedValues() )
				{
					if( tagValue.getName().contains( "id-attribute" ))
					{
						//System.out.println( "deleted primarykey" );
						att.removeTaggedValue( "id-attribute" );
					}
                    
                    if( tagValue.getName().contains( "mapped-attributes" ))
					{
						//System.out.println( "deleted m-a" );
						att.removeTaggedValue( "mapped-attributes" );
					}
					if( tagValue.getName().contains( "implements-association" ))
					{
						//System.out.println( "deleted assoc" );
						att.removeTaggedValue( "implements-association" );
					}
					if( tagValue.getName().contains( "correlation-table" ))
					{
						//System.out.println( "deleted corr-t" );
						att.removeTaggedValue( "correlation-table" );
					}												
					if( tagValue.getName().contains( "inverse-of" ))
					{
						//System.out.println( "Removing: " + tagValue.getName() + " " + tagValue.getValue() );
						att.removeTaggedValue( "inverse-of" );
					}
					if( tagValue.getName().contains( "type" ))
					{
						//System.out.println( "Removing: " + tagValue.getName() + " " + tagValue.getValue() );
						att.removeTaggedValue( "type" );
					}
					if( tagValue.getName().contains( "discriminator" ))
					{
						//System.out.println( "Removing: " + tagValue.getName() + " " + tagValue.getValue() );
						att.removeTaggedValue( "discriminator" );
					}
				}
			}				
			for( UMLAssociation assc : clazz.getAssociations()) 
			{
				//System.out.println( "Attribute: " + att.getName() );
				
				for( UMLTaggedValue tagValue : assc.getTaggedValues() )
				{
					if( tagValue.getName().contains( "lazy-load" ))
					{
						//System.out.println( "Removing: " + tagValue.getName() + " " + tagValue.getValue() );
						assc.removeTaggedValue( "lazy-load" );
					}
					if( tagValue.getName().contains( "correlation-table" ))
					{
						//System.out.println( "deleted corr-t" );
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
		System.out.println("Lazy keys: " + lazyKeys);
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
	    List <UMLDependency> deps = model.getDependencies();
	    
	    boolean exist = false;	    
	    for(UMLDependency oldDep : deps) 
	    {
	    	if (((UMLClass)(oldDep.getClient())==client) && ((UMLClass)(oldDep.getSupplier())==supplier)) 
	    	{
	    		exist = true;
	    	}
	    }	    
	    if (exist) return;
	 
	    dependencyMap.put(dependency.getChildText( "target"), supplier );
	    UMLDependency dep = model.createDependency( client, supplier, "dependency" );
	    
	    dep = model.addDependency( dep );	    
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
			//	System.out.println( "found dependency!" );			
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
	    target = ModelUtil.findAttribute(model, attribute.getChildText("target"));
	    
	    // Remove all implements-association, correlation-table
	    for (Iterator it=target.getTaggedValues().iterator(); it.hasNext(); ) 
	    {
	    	UMLTaggedValue element = (UMLTaggedValue)it.next();	  
	    	if ( element.getName().equals("implements-association") )
	    	{
	    		System.out.println( "removing implements-association" );
	    		target.removeTaggedValue( element.getName() );	    		
	    	}
	    	if ( element.getName().equals("correlation-table") )
	    	{
	    		System.out.println( "removing correlation-table" );
	    		target.removeTaggedValue( element.getName() );	    	
	    	}
	    	if ( element.getName().equals("inverse-of") )
	    	{
	    		System.out.println( "removing inverse-of:" + element.getName() );
	    		target.removeTaggedValue( element.getName() );	    	
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
	    if(reciprolRoleHasInverseOfTag(attribute.getChildText("source")) == false )
	    {
	    	System.out.println( "reciprol Role has no inverse_of tag value. Checked " + attribute.getChildText("source") + "\n");	    	
	    	addInverseOfTagValue(target,attribute);
	    	saveModel();
	    }
	}
	
	/**
	 * @param Target
	 * @param attribute
	 */
	public void addInverseOfTagValue(UMLAttribute Target, Element attribute) 
	{
		//check to see if this Tag Value already exists
//		if( !checkInverseOfTagValue( getInverseRoleName(attribute.getChildText("source")) ) )
//		{
			//System.out.println( "Adding inverse_of to: " + getInverseRoleName(attribute.getChildText("source")) );
			Target.addTaggedValue( "inverse-of", getInverseRoleName(attribute.getChildText("source")) );
			saveModel();
//		} else {
//			System.out.println( "NOT ADDING already has inverse-of tag value");
//		}	
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
		//System.out.println( "Path to column: " + pathToColumnName );
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
			umlAssociation.addTaggedValue("lazy-load", "YES");
		}
	}

    public void addClobKey( String cKey )
	{
		String clobKey = modelMetadata.getMmsPrefixDataModel() + "." + cKey;
		UMLAttribute column = ModelUtil.findAttribute(this.model, clobKey);
        System.out.println("Looking for CLOBKEY "+ clobKey );

        if( column != null)
		{
            System.out.println("Added a CLOB: " + cKey);
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
		
        System.out.println("Looking for Discriminator "+ discriminatorKey );
        //UMLClass umlClass = ModelUtil.findClass(this.model, discriminatorKey);

        if (objectName!=null && column != null) {

			UMLClass clazz = ModelUtil.findClass(this.model, objectName);
			
			if (clazz == null) return;
 			
			UMLClass oldclazz = null;
			while (!(clazz == oldclazz))
			{
				oldclazz = clazz;
				List<UMLGeneralization> clazzGs = clazz.getGeneralizations();

				for (UMLGeneralization clazzG : clazzGs) {
					UMLClass parent = clazzG.getSupertype();
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
            
            System.out.println("Write --:" + packageName.substring(modelMetadata.getMmsPrefixObjectModel().length()+1));
            column.addTaggedValue( "discriminator",  packageName.substring(modelMetadata.getMmsPrefixObjectModel().length()+1));
            //umlClass.addTaggedValue( "discriminator", dKey );
        }
	}
    
    public void addDiscriminatorValues( String dKey )
	{
		String discriminatorKey = modelMetadata.getMmsPrefixObjectModel() + "." + dKey;
		UMLClass clazz = ModelUtil.findClass(this.model, discriminatorKey);
        System.out.println("Looking for Discriminator -- class "+ discriminatorKey );
        //UMLClass umlClass = ModelUtil.findClass(this.model, discriminatorKey);

        if( clazz != null)
		{
            System.out.println("Added a discrimintor: " + dKey);
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
//		  System.out.println("aaa");
		  try {
			    XMIGenerator generator = new XMIGenerator("C:/Documents and Settings/Administrator/My Documents/MMs Example XMI/All_Mappings_test_validator.map","C:/Documents and Settings/Administrator/My Documents/MMs Example XMI/All_Mappings_test_validator.xmi");
		        generator.init();
		  } catch (Exception e){
			  e.printStackTrace();
		  }
	   
	  }


	
}