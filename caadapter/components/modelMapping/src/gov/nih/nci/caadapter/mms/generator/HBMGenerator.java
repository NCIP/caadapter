/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.mms.generator;
import gov.nih.nci.caadapter.mms.map.CumulativeMapping;
import gov.nih.nci.caadapter.mms.map.SingleAssociationMapping;
import gov.nih.nci.caadapter.mms.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.mms.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.mms.metadata.ColumnMetadata;
import gov.nih.nci.caadapter.mms.metadata.ModelMetadata;
import gov.nih.nci.caadapter.mms.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.mms.metadata.TableMetadata;
import gov.nih.nci.ncicb.xmiinout.domain.UMLModel;
import gov.nih.nci.ncicb.xmiinout.handler.XmiException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * @version 1.0
 * @created 11-Aug-2006 8:18:16 AM
 */
public class HBMGenerator 
{
	public CumulativeMapping cumulativeMapping;
	private Document doc = new Document();
	private Document hbmDoc = new Document();
	private String outputDirectory;    	
	private List parentDependencies;
	private List subclassDependencies;
	private List attributes;
	private List associations;
	private List manytomanys;
	
	private UMLModel model;
	private ModelMetadata metaModel;
	private LinkedHashMap metaMap;
	
	/**
	 * 
	 * @param xmiFileName
	 */
	public HBMGenerator( String xmiFileName )
	{		
		init( xmiFileName );
	}
	
	/**
	 * @param xmiFileName
	 */
	public void init( String xmiFileName ) 
	{
		try 
		{			
			//initialize the ArrayLists
			this.parentDependencies = new ArrayList();
			this.subclassDependencies = new ArrayList();
			this.attributes = new ArrayList();
			this.associations = new ArrayList();
			this.manytomanys = new ArrayList();
			
			//Load ModelMetadata from xmi file
			metaModel = ModelMetadata.getInstance();
		    if (metaModel == null) {
		    	ModelMetadata.createModel(xmiFileName);
			   	metaModel = ModelMetadata.getInstance();
		   }
			
			metaMap = metaModel.getModelMetadata();
			model = metaModel.getModel();
			cumulativeMapping = CumulativeMapping.getInstance();				

			//System.out.println( "Testing Eclipse...");
			//System.out.println( "Loading LinkedHashMap" );
			//System.out.println( "|--------------------------------------|" );
			ModelMetadata myModel = ModelMetadata.getInstance();
			metaMap = myModel.getModelMetadata();

			for ( Iterator it = metaMap.keySet().iterator(); it.hasNext(); ) 
			{
			   String key = (String) it.next();			   
			   Object value = metaMap.get(key);
			   
			   //System.out.println( "key: " + key );
			   //System.out.println( "key: " + metaMap.get(key) );
			   
			   if ( value instanceof TableMetadata )
			   {
				   //System.out.println( "TABLEMETADAD!");
				   TableMetadata tabledata = (TableMetadata) value;
				   //System.out.println( "T Datatype:" + tabledata.getType() );
			   } 
			   
			   if ( value instanceof ColumnMetadata )
			   {
				   //System.out.println( "COLUMNMETADATA!");
				   ColumnMetadata columndata = (ColumnMetadata) value;
				   //System.out.println( "Classname: " + columndata.getName() );
				   //System.out.println( "C Datatype:" + columndata.getDatatype() );
			   }
			   
			   if ( value instanceof AttributeMetadata )
			   {
				   //System.out.println( "ATTRIBUTEMETADATA!");
				   AttributeMetadata attdata = (AttributeMetadata) value;
				   //System.out.println( "A Datatype:" + attdata.getDatatype() );
			   }

			   if ( value instanceof AssociationMetadata )
			   {
				   //System.out.println( "ASSOCIATIONMETADATA!");
				   AssociationMetadata assodata = (AssociationMetadata) value;
				   //System.out.println( "AS Datatype:" + assodata.getName() );
				   
				   //cumulativeMapping.addSingleAssociationMapping( (SingleAssociationMapping) value);
			   }
			   
			   if ( value instanceof ObjectMetadata )
			   {
				   //System.out.println( "OBJECTMETADATA!");
				   ObjectMetadata objdata = (ObjectMetadata) value;
				   //System.out.println( "O Datatype:" + objdata.getName() );
			   }
			   //System.out.println(key + "\n");
			}
			
		} catch (XmiException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    } catch (Exception e) {
		  e.printStackTrace();
	    }
	 }
	
	/**
	 * Logical.View Model.gov.nci.gov.domain.Gene
	 *                                       |  |
	 * @param name
	 * @return
	 *
	 */
	public String getClassId( String name )
	{
		int lastDot = name.lastIndexOf( "." );						
		String classId = name.substring( lastDot + 1, name.length());
		return classId;
	}
	
	/**
	 * Logical.View Model.gov.nci.gov.domain.Gene
	 * |                                   |
	 * @param name
	 * @return
	 */	  
	public String getParentPackage( String name )
	{
		int lastDot = name.lastIndexOf( "." );								
		String longPackageId = name.substring( 0, lastDot );
		return longPackageId;				
	
	}
	
	/**
	 * Logical.View Model.gov.nci.gov.domain.Gene
	 *                    |                     |
	 * @param name
	 * @return
	 */
	public String getAttName( String name )
	{
		int lastSpace = name.lastIndexOf( " " );		
		String classId = name.substring( lastSpace + 1, name.length());
		int modelDot = classId.lastIndexOf( "Model." );		
		String attName1 = classId.substring( modelDot + 1, classId.length() );
		int firstDot = attName1.indexOf( "." );
		String attName2 = attName1.substring( firstDot + 1, attName1.length() );
		//System.out.println( "attName:" + attName2 );
		return attName2;
	}
	
	/**
	 * Logical.View Model.gov.nci.gov.domain.Gene
	 *                    |                | 
	 * @param name
     * @return
     */	
	public String getPackageId( String name )
	{		
		int lastSpace = name.lastIndexOf( " " );		
		String classId = name.substring( lastSpace + 1, name.length());
		int lastDot = classId.lastIndexOf( "." );
		int firstDot = classId.indexOf( "." );
		String packageId = classId.substring( firstDot + 1 , lastDot );		
		return packageId;
	}
	
	/**
	 * Logical.View Model.gov.nci.gov.domain.Gene
	 *                    |                     |
	 * @param name
     * @return
     */	
	public String getWholePackage( String name )
	{		
		int lastSpace = name.lastIndexOf( " " );		
		String classId = name.substring( lastSpace + 1, name.length());
		int firstDot = classId.indexOf( "." );
		String packageId = classId.substring( firstDot + 1 , classId.length() );		
		return packageId;
	}
	
	/**
	 * Logical View.Data Model.GENE_SEQUENCE.GENE_ID
	 *                         |           |
	 * @param name
	 * @return
	 */
	public String getTABLE( String name )
	{
		int lastdot = name.lastIndexOf( "." );		
		String table = name.substring( 0, lastdot );
		table = table.substring( table.lastIndexOf( "." ) + 1 , table.length() );
		return table;
	}
	
	/**
	 * 
	 * @param parentElement
	 */
	public void findSubclass( Element parentElement )
	{
		  Iterator subclassIterator = subclassDependencies.iterator();
		  while (subclassIterator.hasNext()) 
		  {
			Element subclassElement = (Element) subclassIterator.next();
			
			if( subclassElement.getAttributeValue("parent").equals( parentElement.getChildText("source")))
			{	
				//System.out.println( "SUBCLASS found: " + subclassElement.getAttributeValue("parent") );
				//System.out.println( "Searching for subclasses..." );
				findSubclass( subclassElement );
			}
		  }
	}
	
	/**
	 * This method iterates through the arrays of objects contained within
	 * the cumulativeMap object and writes out to the output directory
	 * hibernate mapping files.
	 */
	public void generateHBMFiles( String xmlfile )
	{
		//System.out.println( "Generating HBM Files from xmiFile..." );
		
		//Load the xml file into Document object
		File xmlFile = new File( xmlfile );		
		loadMappingFile( xmlFile );
		
		//Process the root element of the xmi file, populate lists
		processTree( doc.getRootElement() );
		
		//System.out.println( "Processing all parentDependencies (" + parentDependencies.size() + ")" );
		//System.out.println( "|--------------------------------------------------|" );
		
		//Create hbm files		
		//step 1: find all parentDependencies
		//        each parentDependency creates its own hbm file		
		Iterator parentIterator = parentDependencies.iterator();			
		while ( parentIterator.hasNext() ) 
		{
		  Element parentElement = (Element) parentIterator.next();		  
		  
		  //System.out.println( "[ " + parentElement.getChildText( "source" ) + " ]" );
		  
		  //Create the hibernate-mapping Element
		  Element hibernateMappingElement = new Element( "hibernate-mapping" );
		  hibernateMappingElement.setAttribute( "package", getPackageId( parentElement.getChildText( "source" )));
		  
		  //Create the parentElement
		  Element classElement = new Element( "class" );
		  classElement.setAttribute( "name", getClassId( parentElement.getChildText( "source" )) );
		  classElement.setAttribute( "table", getClassId( parentElement.getChildText( "target" )) );
		  classElement.setAttribute( "lazy", "true" );
		  classElement.setAttribute( "polymorphism", "explicit" );
  
		  //Add cache
		  Element cacheElement = new Element( "cache");
		  cacheElement.setAttribute( "usage", "read-write" );		  
		  classElement.addContent( cacheElement );
		  
		  //System.out.println( "| Processing subclassDependencies (" + subclassDependencies.size() + ") |" );
		  
		  //Step 2: find all subclasses of this parentDependency
		  //        match: SubClass parent="value" => ParentDependency source contents 
		  Iterator subclassIterator = subclassDependencies.iterator();
		  while (subclassIterator.hasNext()) 
		  {
			Element subclassElement = (Element) subclassIterator.next();
			
			if( subclassElement.getAttributeValue("parent").equals( parentElement.getChildText("source")))
			{				
				//System.out.println( "Subclass found: " + subclassElement.getAttributeValue("parent") );
				
				//Create the subclass eleemnt
				Element joinedsubclassElement = new Element( "joined-subclass" );
				joinedsubclassElement.setAttribute( "name", getAttName( subclassElement.getChildText( "source" )) );
				joinedsubclassElement.setAttribute( "table", getClassId( subclassElement.getChildText( "target" )) );
				joinedsubclassElement.setAttribute( "lazy", "true" );
				
				//System.out.println( "> Searching for attributes of subclass (" + attributes.size() + ")" );
				
				//Search for attributes of subclass
				Iterator attributeIterator = attributes.iterator();
				while ( attributeIterator.hasNext() )
				{
					Element attributeElement = (Element) attributeIterator.next();
					if( getWholePackage( subclassElement.getChildText( "source" ) ).equals( getPackageId( attributeElement.getChildText( "source" ))) )
					{						
						//Special case: id
						if( getClassId(attributeElement.getChildText( "source" )).equals( "id" ) )
						{
							//System.out.println( "Attribute found: id" );
							Element keyElement = new Element( "key" );
							keyElement.setAttribute( "column", getClassId( attributeElement.getChildText( "target" )) );
							joinedsubclassElement.addContent( keyElement );
						}
						else 
						{																				
							Element propertyElement = new Element( "property" );							
							AttributeMetadata attdata = (AttributeMetadata) metaMap.get( attributeElement.getChildText( "source" ) );
							String datatype = attdata.getDatatype();   
							propertyElement.setAttribute( "name", getClassId(attributeElement.getChildText( "source" )) );
							propertyElement.setAttribute( "type", "java.lang." + datatype );
							propertyElement.setAttribute( "column", getClassId(attributeElement.getChildText( "target" )) );			
							joinedsubclassElement.addContent( propertyElement );
							//System.out.println( "Attribute found:" +  getClassId(attributeElement.getChildText( "source" )) + " Type: " + datatype);
						}
					}
				}
				
				//System.out.println( "> Searching for Associations of subclass (" + associations.size() + ")" );
				
				//Seach for assocations 
				Iterator associationsIterator = associations.iterator();
				while ( associationsIterator.hasNext() ) 
				{
					Element associationsElement = (Element) associationsIterator.next();	
					AssociationMetadata assocMeta = (AssociationMetadata) metaMap.get( associationsElement.getChildText( "source" ) );
					
					if( getWholePackage( subclassElement.getChildText( "source" )).equals( getPackageId( associationsElement.getChildText( "source" ))) )
					{
						//System.out.println( "Association Found!" );
						//System.out.println( "Element subclass source     : " + subclassElement.getChildText( "source" ) );
						//System.out.println( "Element assocElement source : " + associationsElement.getChildText( "source" ) );
						
						//System.out.println( "Xpath: " + getWholePackage( assocMeta.getReturnTypeXPath() ) );
												
						Element manytooneElement = new Element( "many-to-one" );
						manytooneElement.setAttribute( "name", getClassId(associationsElement.getChildText( "source" )) );
						manytooneElement.setAttribute( "class", getWholePackage( assocMeta.getReturnTypeXPath()) );						
						manytooneElement.setAttribute( "column", getClassId(associationsElement.getChildText( "target" )) );
						manytooneElement.setAttribute( "lazy", "proxy" );
						joinedsubclassElement.addContent( manytooneElement );
					}										
				}				 
				classElement.addContent( joinedsubclassElement );
				
				//step 3: find all subclasses of each subclass (recursive search)				
				findSubclass( subclassElement );				
			}			
		  }		  

		  //System.out.println( "| Processing Attributes (" + attributes.size() + ") |" );
		  
		  //Step 4: find all attributes of the parentDependency
		  //        match: Attribute source (subset) => parentDependency source contents
		  Iterator attributeIterator = attributes.iterator();
		  while ( attributeIterator.hasNext() )
		  {
			Element attributeElement = (Element) attributeIterator.next();		  			
			String attributeSource = attributeElement.getChildText( "source" );
			
			//grab the attribute from the source	
			String attributeParent = getClassId( attributeSource );
			
			//Match only the attributes for this parent
			if( getParentPackage( attributeElement.getChildText( "source" )).equals( parentElement.getChildText( "source" ) )) 
			{
				//Create a attributeElement
				Element attributeElementHBM = null;
				
				//Special case for an ID
				if ( attributeParent.equals( "id" ) )
				{					
					attributeElementHBM = new Element( "id" );	
					
					//Access the TableHashmap to retrieve the datatype of this attribute
					AttributeMetadata attdata = (AttributeMetadata) metaMap.get( attributeElement.getChildText( "source" ) );
					String datatype = attdata.getDatatype();   
					attributeElementHBM.setAttribute( "name", "id" );
					attributeElementHBM.setAttribute( "type", "java.lang." + datatype );
					attributeElementHBM.setAttribute( "column", getClassId( attributeElement.getChildText( "target" )) );
					
					Element generatorElement = new Element( "generator" );
					generatorElement.setAttribute( "class", "assigned" );				
					attributeElementHBM.addContent( generatorElement );
					//System.out.println( "Attribute found: " + attributeElement.getChildText( "source" ) + " Type:" + datatype );
				} 
				else 
				{
					attributeElementHBM = new Element( "property" );
					attributeElementHBM.setAttribute( "name", attributeParent );
										
					//Access the TableHashmap to retrieve the datatype of this attribute
					AttributeMetadata attdata = (AttributeMetadata) metaMap.get( attributeElement.getChildText( "source" ) );
					String datatype = attdata.getDatatype();   
					attributeElementHBM.setAttribute( "type", "java.lang." + datatype );				
					attributeElementHBM.setAttribute( "column", getClassId(attributeElement.getChildText( "target" )) );
					//System.out.println( "Attribute found: " + attributeElement.getChildText( "source" ) + " Type:" + datatype );
					
				}
				
				classElement.addContent( attributeElementHBM );
			}			
		  }
		  
		  //System.out.println( "| Processing Associations (" + associations.size() + ") |" );
		  
		  //Step 5: find all associations of the parentDependency
		  //        match: Association source (subset) => parentDependency source contents
		  Iterator associationsIterator = associations.iterator();
		  while (associationsIterator.hasNext()) 
		  {
			Element associationsElement = (Element) associationsIterator.next();	
			AssociationMetadata assocMeta = (AssociationMetadata) metaMap.get( associationsElement.getChildText( "source" ) );
			String associationsSource = associationsElement.getChildText( "source" );
			
			//grab the associations from the source
			String associationsParent = getClassId( associationsSource );	
			
			//Match only the associations for this parent
			if( getParentPackage( associationsElement.getChildText( "source" )).equals( parentElement.getChildText( "source" )) )
			{	
				
				if ( assocMeta.isBidirectional() == false && assocMeta.isManyToOne() == false )
				{
					//System.out.println( "***** one-to-one ?: " + assocMeta.getReturnTypeXPath() + "  " + associationsElement.getChildText( "source" ));
					
					Element manyToOneElementHBM = new Element( "many-to-one" );
					manyToOneElementHBM.setAttribute( "name", getClassId( associationsElement.getChildText( "source")) );
					manyToOneElementHBM.setAttribute( "class", getWholePackage( assocMeta.getReturnTypeXPath()) );
					manyToOneElementHBM.setAttribute( "column", getClassId( associationsElement.getChildText( "target")) );
					manyToOneElementHBM.setAttribute( "lazy", "true" );
					classElement.addContent( manyToOneElementHBM );
				}
				
				// Parent finding the association (Many-to-one)
				if ( assocMeta.isBidirectional() == true && assocMeta.isManyToOne() == false )
				{					
					//System.out.println( "***** one-to-one bi?: " + assocMeta.getReturnTypeXPath() + "  " + associationsElement.getChildText( "source" ));
				
					Element oneToOneElementHBM = new Element( "many-to-one" );
					oneToOneElementHBM.setAttribute( "name", "TODO" );
					oneToOneElementHBM.setAttribute( "class", getWholePackage( assocMeta.getReturnTypeXPath() ) );
					oneToOneElementHBM.setAttribute( "column", "TODO" );
					oneToOneElementHBM.setAttribute( "unique", "true" );
					oneToOneElementHBM.setAttribute( "lazy", "proxy" );
					classElement.addContent( oneToOneElementHBM );
				}		
				if( assocMeta.isBidirectional() == true && assocMeta.isManyToOne() == true)
				{
					//Create a associations Element
					Element manyToOneElementHBM = new Element( "many-to-one" );						
					
					manyToOneElementHBM.setAttribute( "name", getClassId( associationsElement.getChildText( "source")) );
					manyToOneElementHBM.setAttribute( "class",  getWholePackage( assocMeta.getReturnTypeXPath() ) );
					manyToOneElementHBM.setAttribute( "column", getClassId( associationsElement.getChildText( "target")) );
					manyToOneElementHBM.setAttribute( "lazy", "true" );
					manyToOneElementHBM.setAttribute( "fetch", "join" );
					
					classElement.addContent( manyToOneElementHBM );
					
					//System.out.println( "Association found: " + associationsElement.getChildText( "source" ) + " XPath: " + getWholePackage( assocMeta.getReturnTypeXPath() ) + " isBiDirectional: " + assocMeta.isBidirectional());
				}	
			}
			
			//if Xpath equals source, linked!
			if( assocMeta.getReturnTypeXPath().equals( parentElement.getChildText( "source" )) )
			{
				// Child finding the parent association (one-to-one)
				if ( assocMeta.isBidirectional() == true && assocMeta.isManyToOne() == false )
				{
					//System.out.println( "***** one-to-one bi?: " + assocMeta.getReturnTypeXPath() + "  " + associationsElement.getChildText( "source" ));
					
					Element oneToOneElementHBM = new Element( "one-to-one" );
					oneToOneElementHBM.setAttribute( "name", "TODO" );
					oneToOneElementHBM.setAttribute( "class", getPackageId( associationsElement.getChildText( "source" )) );
					oneToOneElementHBM.setAttribute( "property-ref", getClassId( associationsElement.getChildText( "source" )) );
					classElement.addContent( oneToOneElementHBM );
				}	
					
				if( assocMeta.isManyToOne() == true )
				{								
					Element setElement = new Element( "set" );
					if ( assocMeta.getUMLAssociation().getAssociationEnds().get(0).getRoleName().contains( "Collection" ) ) 
					{
						setElement.setAttribute( "name", assocMeta.getUMLAssociation().getAssociationEnds().get(0).getRoleName() );				
					} 
					else
					{
						setElement.setAttribute( "name", assocMeta.getUMLAssociation().getAssociationEnds().get(1).getRoleName() );
					}
					
					setElement.setAttribute( "lazy", "true" );
					
					//setElement.setAttribute( "inverse", "true" );
					
				    //Add cache
					Element cache = new Element( "cache" );
				    cache.setAttribute( "usage", "read-write" );	
					setElement.addContent( cache );
					
//CumulativeMappingGenerator, get SingleAssoc List, find a match, get columnMetadata
//					List singleAssocMapList = new ArrayList();
//					
//					CumulativeMappingGenerator cumulativeMappingGenerator = new CumulativeMappingGenerator( );
//					cumulativeMappingGenerator.init( "c:/dev/testdata/sdk.xmi" );
//					singleAssocMapList = cumulativeMappingGenerator.getCumulativeMapping().getSingleAssociationMappings();
//					Iterator singleAssocMapIterator = singleAssocMapList.iterator();					
//					while ( singleAssocMapIterator.hasNext() ) 
//					{
//						//System.out.println( "here" );
//						SingleAssociationMapping singleAssoc = (SingleAssociationMapping) singleAssocMapIterator.next();
//						//System.out.println( "SingleAssociationMapping:" + singleAssoc.getColumnMetadata().getName() );				  
//					}
					
					//ColumnMetadata columnMeta = (ColumnMetadata) metaMap.get( parentElement.getChildText( "target" ) );
					//TableMetadata tableMeta = (TableMetadata) metaMap.get( associationsElement.getChildText( "source" ) );
					//columnMeta.getTableMetadata();
					////System.out.println( "ColumnMeta: " + columnMeta.getClassName() );
					
					//Add key				
					Element keyElement = new Element( "key" );		
					keyElement.setAttribute( "column", "_" );
					setElement.addContent( keyElement );
					
					Element manyToManyElement = new Element( "one-to-many" );
					manyToManyElement.setAttribute( "class", getWholePackage( assocMeta.getReturnTypeXPath()) );			
					setElement.addContent( manyToManyElement );
					
					classElement.addContent( setElement );
				}
			}			
		  }
		  		  
		  //System.out.println( "| Processing Manytomanys ("  + manytomanys.size() + ") |" );
		  
		  //Step 6: find all manytomany of the parentDependency		  		 
		  Iterator manytomanyIterator = manytomanys.iterator();		  
		  while (manytomanyIterator.hasNext()) 
		  {
			Element manytomanyElement = (Element) manytomanyIterator.next();		
			
			//Match only the many-to-many for this parent
			if( getParentPackage( manytomanyElement.getChildText( "source" )).equals( parentElement.getChildText( "source" )) ) 
			{	
				Element setElement = new Element( "set" );
				setElement.setAttribute( "name", getClassId( manytomanyElement.getChildText( "source" )) );
				setElement.setAttribute( "table", getTABLE( manytomanyElement.getChildText( "target" )) );				
				setElement.setAttribute( "lazy", "true" );
				setElement.setAttribute( "inverse", "true" );				
				
			    //Add cache
				Element cache = new Element( "cache" );
			    cache.setAttribute( "usage", "read-write" );	
				setElement.addContent( cache );

				AssociationMetadata assocMeta = (AssociationMetadata) metaMap.get( manytomanyElement.getChildText( "source" ) );

				//Add key
				Element keyElement = new Element( "key" );
//TODO: key column			
				keyElement.setAttribute( "column", "TODO!" );
				setElement.addContent( keyElement );
				
				Element manyToManyElement = new Element( "many-to-many" );
				manyToManyElement.setAttribute( "class", getWholePackage( assocMeta.getReturnTypeXPath()) );
				manyToManyElement.setAttribute( "column", getClassId( manytomanyElement.getChildText( "target" )) );			
				setElement.addContent( manyToManyElement );
				
				classElement.addContent( setElement );
							
				//Find the matching pair for this many-to-many relationship, match the data model Table
//				Iterator manytomanyIterator2 = manytomanys.iterator();				
//				while ( manytomanyIterator2.hasNext() )
//				{
//					Element manytomanyPair = (Element) manytomanyIterator2.next();
//					if( getTABLE(manytomanyPair.getChildText( "target" )).equals( getTABLE(manytomanyElement.getChildText( "target" ))) )
//					{						
//						if( !manytomanyElement.getChildText( "source" ).equals( manytomanyPair.getChildText( "source" )) )
//						{
//							//System.out.println( "MaanytoMany found: " );
//							//System.out.println( "Element   :" + getTABLE(manytomanyElement.getChildText( "target" )) );
//							//System.out.println( "Pair      :" + getTABLE(manytomanyPair.getChildText( "target" )) );												
//					
//							//System.out.println( "Searching for: " + manytomanyElement.getChildText("source") );								
//							//System.out.println( "Looking up many-to-many: " + manytomanyElement.getChildText( "source" ));
//											
//							//Check for inverse_of - if exists add inverse attribute			
//							Element setElement = new Element( "set" );
//							setElement.setAttribute( "name", getClassId( manytomanyElement.getChildText( "source" )) );
//							setElement.setAttribute( "table", getTABLE( manytomanyElement.getChildText( "target" )) );				
//							setElement.setAttribute( "lazy", "true" );
//							setElement.setAttribute( "inverse", "true" );
//							
//						    //Add cache
//							Element cache = new Element( "cache" );
//						    cache.setAttribute( "usage", "read-write" );	
//							setElement.addContent( cache );
//							
//							Element keyElement = new Element( "key" );
//							keyElement.setAttribute( "column", getClassId( manytomanyPair.getChildText( "target" )) );
//							setElement.addContent( keyElement );	
//							
//							Element manyToManyElement = new Element( "many-to-many" );
//							manyToManyElement.setAttribute( "class", getPackageId( manytomanyPair.getChildText( "source" )) );
//							manyToManyElement.setAttribute( "column", getClassId( manytomanyElement.getChildText( "target" )) );			
//							setElement.addContent( manyToManyElement );
//							
//							classElement.addContent( setElement );
//						} //end of if
//					}//end of if
//				}//end of while
			}//end of if
		  }//end of while
		  
		  //Step 7: write everything to new .hbm file
		  hibernateMappingElement.addContent( classElement );
		  hbmDoc.setRootElement( hibernateMappingElement );	
		  
		  XMLOutputter outp = new XMLOutputter();
		  outp.setFormat( Format.getPrettyFormat() );
		  
	      try {
			File myFile = new File( outputDirectory + "/" + getClassId( parentElement.getChildText( "source" )) + ".hbm.xml" );
			FileOutputStream myStream = new FileOutputStream( myFile );
			outp.output( hbmDoc, myStream );
			
			//System.out.println( ">> File created ( " + outputDirectory + "/" + getClassId( parentElement.getChildText( "source" )) + ".hbm.xml )\n\n"  );
		  } catch ( Exception e ) {
			e.printStackTrace();
		  }
		}	
	}

	/**
	 * 
	 * @param xmlfile
	 */
	public void loadMappingFile( File xmlfile )
	{
		//System.out.println( "Loading mapping file..." + xmlfile.getPath() );
		
		try {			
			if (xmlfile.exists())
				{
					SAXBuilder builder = new SAXBuilder();
					this.doc = builder.build(xmlfile);				
				}
			} catch (org.jdom.JDOMException jex) {
				jex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}			
	}
	
	/**
	 * 
	 * @return
	 */
	public CumulativeMapping getCumulativeMapping()
	{
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public String getOutputDirectory()
	{
		return this.outputDirectory;
	}

	/**
	 * @param cumulativeMapping
	 */
	public void setCumulativeMapping(CumulativeMapping cumulativeMapping)
	{
		this.cumulativeMapping = cumulativeMapping;
	}

	/**
	 * @param outputDirectory
	 */
	public void setOutputDirectory(String outputDirectory)
	{
		this.outputDirectory = outputDirectory;
	}

	/**
	 * Recursively descend the tree
	 * @param element
	 */
	public void processTree(Element element) 
	{
	    
	  inspect(element);
	  List content = element.getContent();
	  Iterator iterator = content.iterator();
	    
	  while (iterator.hasNext()) 
	  {
	    Object o = iterator.next();
	    
	    if (o instanceof Element) 
	    {
	      Element child = (Element) o;
	      processTree(child);
	    }
	  }
	}
	
	/**
	 * Print the properties of each element
	 * @param element
	 */
	public void inspect(Element element) 
	{
	   //Sort all elements into their lists
	   if( element.getAttributeValue( "type" ) != null ) 
	   {
		   if ( element.getAttributeValue( "type" ).equals( "dependency" ))
		   {	    	
		   	  String parentValue = element.getAttributeValue( "parent" );
		   	  if( parentValue != null && parentValue.equals( "null" ) ) 
		   	  {
		   		  //System.out.println( "  * Found a Parent! Adding to parentDependencies list" );
		   		  this.parentDependencies.add( element );
		   	  } 
		   	  else 
		   	  {		   		  
		   		  //System.out.println( "  * Found a Subclass Parent! Adding to subclassDependencies list" );
		   		  this.subclassDependencies.add( element );		   		  
		   	  }
		   }
		   
		   //Find Attribute
		   else if( element.getAttributeValue( "type" ).equals( "attribute" ))
		   {	    	
	   		  //System.out.println( "  * Found a attribute element!" );
	   		  this.attributes.add( element );
		   }
		   
		   //Find Associations
		   else if( element.getAttributeValue( "type" ).equals( "association" ))
		   {	    	
	   		  //System.out.println( "  * Found an association element!" );
	   		  this.associations.add( element );
		   }
		   
		   //Find Manytomany
		   else if( element.getAttributeValue( "type" ).equals( "manytomany" ))
		   {	    	
	   		  //System.out.println( "  * Found a manytomany element!" );
	   		  this.manytomanys.add( element );
		   }
		   
		   else {}
	   }	 
	}
	  	
	/**
	 * 
	 * @param args
	 */
	public static void main( String[] args )
    {
		//System.out.println( "Testing..." );
		
    	//Need to be passed the xmi file to use to create hbms
    	//Also will need the Metadata file too	
		String xmifile = "c:/dev/testdata/sdk.xmi";
		String xmlfile = "c:/dev/testdata/sdk.map";
		
    	HBMGenerator myGenerator = new HBMGenerator( xmifile );
    	myGenerator.setOutputDirectory( "C:/dev/testdata/hbms" );
    	
        try
        {
        	myGenerator.generateHBMFiles( xmlfile );
        	
        } catch (Exception e)
        {
            //Log.logException(new Object(), t);
        	//System.out.println( e );
        	e.printStackTrace();
        }
        
        //System.out.println( "Finished" );
    }        
}