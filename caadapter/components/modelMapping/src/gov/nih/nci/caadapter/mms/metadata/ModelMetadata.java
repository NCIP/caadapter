/**
 * 
 */
package gov.nih.nci.caadapter.mms.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import gov.nih.nci.caadapter.mms.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.mms.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.ui.common.preferences.PreferenceManager;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociation;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociationEnd;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLGeneralization;
import gov.nih.nci.ncicb.xmiinout.domain.UMLModel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLPackage;
import gov.nih.nci.ncicb.xmiinout.handler.HandlerEnum;
import gov.nih.nci.ncicb.xmiinout.handler.XmiException;
import gov.nih.nci.ncicb.xmiinout.handler.XmiHandlerFactory;
import gov.nih.nci.ncicb.xmiinout.handler.XmiInOutHandler;
import gov.nih.nci.ncicb.xmiinout.util.ModelUtil;

/**
 * @author connellm
 * This class is used to create a LinkedHashMap of Metadata objects representing the UML
 * model being imported into the caAdapter tool. The class loads an XMI file
 * and parses through it creating a LinkedHashMap composed of keys, which are
 * basically xpath like links to the position in the XMI file where the UML Model element
 * is located. The value in the key/value pair is either an ObjectMetadata, 
 * AttributeMetadata, AssociationMetadata, TableMetadata, or ColumnMetadata 
 * object. The key value pairs are loaded into the LinkedHashMap in order of location
 * in the XMI file and can be retrieved in the same order. The contents of the
 * LinkedHashMap can be used to construct the Object and Data model portions of
 * the caAdapter JTree mapping UI. This class is designed to be a singleton because
 * there should never be more than one instance of it in the runtime environment.
 */
public class ModelMetadata {
	private static ModelMetadata modelMetadata = null;
	private static XmiInOutHandler handler = null;
	private static UMLModel model = null;
	private static LinkedHashMap modelHashMap = new LinkedHashMap();
	private static HashMap objectHashMap = new HashMap();
	private static HashMap inheritanceHashMap = new HashMap();
	private static TreeSet sortedModel = new TreeSet(new XPathComparator());
	private static String xmiFileName;
	private static List<String> primaryKeys = new ArrayList<String>();
	private static List<String> lazyKeys = new ArrayList<String>();
	
	public ModelMetadata(){}
	
	public ModelMetadata(String xmiFileName){
		this.xmiFileName = xmiFileName;
	}
	
	public static void init(String xmiFileName) {
	    try {
	    	handler = XmiHandlerFactory.getXmiHandler(HandlerEnum.EADefault);
	    	handler.load(xmiFileName);
	    	model = handler.getModel();
	    } catch (XmiException e) {
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	  }
	public static ModelMetadata getInstance() {
		return modelMetadata;
	}
	
	public static XmiInOutHandler getHandler() {
		return handler;
	}
	
	public static ModelMetadata createModel(String xmiFileName) 
	{
		 if ( modelMetadata != null )
		 {
			sortedModel = null;
			modelHashMap = null;
			modelMetadata = null;
			System.gc();
		 }

		 sortedModel = new TreeSet(new XPathComparator());
		 modelHashMap = new LinkedHashMap();
    	 modelMetadata = new ModelMetadata(xmiFileName);

		 init(xmiFileName);
		 createModel(model);
		 Object[] sortedArray  = sortedModel.toArray();
		 
		 for( int i=0; i < sortedModel.size(); i++ )
		 {
			 modelHashMap.put(((MetaObjectImpl)sortedArray[i]).getXPath(),sortedArray[i]);
		 }
		 
		 for( int i=0; i < sortedModel.size(); i++ ) 
		 {
			 if (sortedArray[i] instanceof AssociationMetadata) 
			 {
				 AssociationMetadata assoMeta = (AssociationMetadata)sortedArray[i];
				 if (assoMeta.getMultiplicity() == -1 && assoMeta.getReciprocalMultiplity() == -1) 
				 {
					 assoMeta.setNavigability(true);
					 assoMeta.setManyToOne(false);
				 }
				 else if (assoMeta.getMultiplicity() == -1 && assoMeta.getReciprocalMultiplity() == 1) 
				 {
					 assoMeta.setNavigability(false);
					 assoMeta.setManyToOne(true);
				 }
				 else if (assoMeta.getMultiplicity() == 1 && assoMeta.getReciprocalMultiplity() == -1) 
				 {
					 assoMeta.setNavigability(true);
					 assoMeta.setManyToOne(true);
				 }
				 else if (assoMeta.getMultiplicity() == 1 && assoMeta.getReciprocalMultiplity() == 1) 
				 {
					 assoMeta.setNavigability(true);
					 assoMeta.setManyToOne(false);
				 }
				 else
				 {
					assoMeta.setNavigability(true);
					 assoMeta.setManyToOne(false);
				 }
			 }
		 }
		 return modelMetadata; 
	}
	
    private static void createModel(UMLModel model)
    {
        for( UMLPackage pkg : model.getPackages() )
        {
            printPackage(pkg);
        }
    }

    private static void printPackage(UMLPackage pkg)
    {
        for(UMLClass clazz : pkg.getClasses())
        {
            StringBuffer pathKey = new StringBuffer(ModelUtil.getFullPackageName(clazz));
            if (pathKey.toString().contains( PreferenceManager.readPrefParams( Config.MMS_PREFIX_DATAMODEL ) )) {
                //create a TableMetadata object
                TableMetadata table = new TableMetadata();
                table.setName(clazz.getName());
                pathKey.append(".");
                pathKey.append(clazz.getName());
                table.setXPath(pathKey.toString());

                System.out.println("DAT clazz.getName : " + clazz.getName() );
                System.out.println("DAT pathKey.toString() : "+ pathKey.toString() );

                sortedModel.add(table);
                for(UMLAttribute att : clazz.getAttributes()) {
                    printColumnAttribute(att, table, pathKey);
                }
            } else if (pathKey.toString().contains( PreferenceManager.readPrefParams( Config.MMS_PREFIX_OBJECTMODEL ) ) && !pathKey.toString().contains("java")) {

                ObjectMetadata object = new ObjectMetadata();
                object.setName(clazz.getName());
//  	    	  System.out.println(clazz);
                pathKey.append(".");
                pathKey.append(clazz.getName());
                object.setXPath(pathKey.toString());
                object.setId(clazz.toString());
                objectHashMap.put(clazz.toString(), pathKey.toString());
                //System.out.println("Class: "+ pathKey.toString() + "\n");
                System.out.println("OBJ clazz.getName : " + clazz.getName() );
                System.out.println("OBJ pathKey.toString() : "+ pathKey.toString() );

                sortedModel.add(object);

                /* The following code look through the inheritance hierachy and populate
                 * attributes of all parents to the object
                 */
                ArrayList<UMLClass> parents = new ArrayList();
                UMLClass parent = null;
                UMLClass pre = null;
                List<UMLGeneralization> clazzGs = clazz.getGeneralizations();

                /* Step 1
                 * trace to all of the ancesters.
                 * when clazzG is not empty, it could have supertype, or subtype
                 * To verify it really has a parent, we need to make sure the supertype is different then itself
                 * also, one assumption is one class can only have one supertype
                 */
                for (UMLGeneralization clazzG : clazzGs) {
                    parent = clazzG.getSupertype();
                    if (parent != clazz) {
                        inheritanceHashMap.put(clazz.toString(),parent.toString());
                        break;
                    }
                }
                if (parent!=clazz) {
                    while (parent != null) {
                        parents.add(parent);
                        clazzGs = parent.getGeneralizations();
                        pre = parent;
                        parent = null;
                        for (UMLGeneralization clazzG : clazzGs) {
                            parent = clazzG.getSupertype();
                            if (parent != pre) {break;}
                        }
                        if (parent == pre) parent = null;
                    }
                    for(UMLClass p : parents) {
                        for(UMLAttribute att : p.getAttributes()) {
                            printAttribute(att, object, pathKey, true);
                        }
                    }
                }
                for(UMLAttribute att : clazz.getAttributes()) {
                    printAttribute(att, object, pathKey, false);
                }
                for(UMLAssociation assoc : clazz.getAssociations()) {
                     printAssociation(assoc, object, pathKey, clazz);
             }
                //create an object metadata object
            }
        }
        for(UMLPackage _pkg : pkg.getPackages()) {
          printPackage(_pkg);
        }
      }


	  private static void printAssociation(UMLAssociation assoc, ObjectMetadata object, StringBuffer keyPath, UMLClass clazz) {
		  	boolean isOneToMany = false;
		  	boolean isManyToMany = false;
	    	UMLAssociationEnd assocEndA = (UMLAssociationEnd)assoc.getAssociationEnds().get(0);
	    	UMLAssociationEnd assocEndB = (UMLAssociationEnd)assoc.getAssociationEnds().get(1);
	    	if ((assocEndA.getHighMultiplicity()==-1 && assocEndB.getHighMultiplicity()==1) || 
	    		(assocEndB.getHighMultiplicity()==-1 && assocEndA.getHighMultiplicity()==1)) {
	    		isOneToMany = true;
	    	}
	    	if ((assocEndA.getHighMultiplicity()==-1 && assocEndB.getHighMultiplicity()==-1) || 
		    		(assocEndB.getHighMultiplicity()==-1 && assocEndA.getHighMultiplicity()==-1)) {
		    		isManyToMany = true;
		    	}
		    for(UMLAssociationEnd assocEnd : assoc.getAssociationEnds()) {
		    	UMLAssociationEnd other = (assocEnd==assocEndA)?assocEndB:assocEndA;
	    		StringBuffer associationKeyPath = new StringBuffer();
	    		associationKeyPath.append(keyPath);
	    		associationKeyPath.append(".");
	    		associationKeyPath.append(assocEnd.getRoleName());
	    		AssociationMetadata thisEnd = new AssociationMetadata();
	    		thisEnd.setUMLAssociation(assoc);
	    		UMLClass clazz1 = (UMLClass)assocEnd.getUMLElement();
  			if (assocEnd.getRoleName().equals("")) continue;
	    		if (!clazz1.getName().equals(clazz.getName())) {
//	    			if (!assocEnd.getRoleName().equals("")) {
	    			if (assocEnd.isNavigable()||isOneToMany || isManyToMany) {
	    				thisEnd.setRoleName(assocEnd.getRoleName());
	    				thisEnd.setMultiplicity(assocEnd.getHighMultiplicity());
	    				thisEnd.setReciprocalMultiplity(getReciprocalMultiplicity(assocEnd));
	    				thisEnd.setXPath(associationKeyPath.toString());
	    				thisEnd.setReturnTypeXPath(clazz1.toString());
	    				thisEnd.setNavigability(assocEnd.isNavigable());
  					thisEnd.setBidirectional(other.isNavigable()&&assocEnd.isNavigable());
	    				sortedModel.add(thisEnd);
	    			}
	    			else {//This is uni-direction
	    			}
	    		}
	    	}
	  }	  	  
	  
	  private static void printAttribute(UMLAttribute att, ObjectMetadata object, StringBuffer pathKey, boolean derived) {
		    StringBuffer attributePath = new StringBuffer();
		    attributePath.append(pathKey);
	        AttributeMetadata attMetadata = new AttributeMetadata();
	        attMetadata.setName(att.getName());
	        attMetadata.setDatatype(att.getDatatype().getName());
	        attributePath.append(".");
	        attributePath.append(att.getName());
	        attMetadata.setXPath(attributePath.toString());
	        attMetadata.setDerived(derived);
	        sortedModel.add(attMetadata);
	        //attMetadata.setSemanticConcept(att.getTaggedValue("conceptId").getValue());   
	  }
	  
	  private static void printColumnAttribute(UMLAttribute att, TableMetadata object, StringBuffer pathKey) 
	  {
	        ColumnMetadata attMetadata = new ColumnMetadata();
	        StringBuffer colPathKey = new StringBuffer();
	        colPathKey.append(pathKey);
	        attMetadata.setName(att.getName());
	        attMetadata.setDatatype(att.getDatatype().getName());
	        colPathKey.append(".");
	        colPathKey.append(att.getName());
	        attMetadata.setXPath(colPathKey.toString());
	        sortedModel.add(attMetadata);
//	        System.out.println("xxxxxxxxxxxxxxx COLUMN: " + colPathKey);
	  }
	  	public HashMap getInheritanceMetadata() {
	  		return inheritanceHashMap;		
	  	}	  

	  	public HashMap getObjectMetadata() {
	  		return objectHashMap;		
	  	}	  

	  	public LinkedHashMap getModelMetadata() {
	  		return modelHashMap;		
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
		
		public List getPrimaryKeys()
		{
			return primaryKeys;
		}
		
		public void setPrimaryKeys( List keyList )		
		{
			primaryKeys = keyList;	
		}
		
		public List getLazyKeys()
		{
			return lazyKeys;
		}
		
		public void setLazyKeys( List lazyKeyList )		
		{
			lazyKeys = lazyKeyList;
		}
		
		/**
		 * @return Returns the model.
		 */
		public UMLModel getModel() {
			return model;
		}
		
		/**
		 * @param model The model to set.
		 */
		public void setModel(UMLModel model) {
			this.model = model;
		}
		
		private static int getReciprocalMultiplicity(UMLAssociationEnd assocEnd) {
			int otherEndMultiplicity = 0;
			otherEndMultiplicity = getOtherAssociationEnd(assocEnd).getHighMultiplicity();
			return otherEndMultiplicity;
		}
		  
		public static UMLAssociationEnd getOtherAssociationEnd(UMLAssociationEnd assocEnd) {
			UMLAssociation assoc = assocEnd.getOwningAssociation();
			UMLAssociationEnd otherAssocEnd = null;
			for (Iterator i = assoc.getAssociationEnds().iterator(); i.hasNext();) {
				UMLAssociationEnd ae = (UMLAssociationEnd) i.next();
				if (ae != assocEnd) {
					otherAssocEnd = ae;
					break;
				}
			}
			return otherAssocEnd;
		}
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		ModelMetadata myModel = new ModelMetadata("C:/sample.xmi");
		//myModel.setXmiFileName("C:/sample.xmi");
//		ModelMetadata myModel1 = ModelMetadata.createModel("D:/projects/hl7sdk-new/workingspace/cacore.xmi");
		ModelMetadata myModel1 = ModelMetadata.createModel("D:/projects/hl7sdk-new/workingspace/sample.xmi");
		ModelMetadata myModel = ModelMetadata.getInstance();
		LinkedHashMap myMap = myModel.getModelMetadata();
		Set mySet = myMap.keySet();
		for (Iterator i = mySet.iterator(); i.hasNext();) {
		   String key = (String)i.next();
		   System.out.println(key);
		   Object x = myMap.get(key);
		   System.out.println(x);
		}
	}
}

class XPathComparator implements Comparator {
	public final int compare ( Object a, Object b) 
	{ 
		MetaObjectImpl info1 = (MetaObjectImpl)a;
		MetaObjectImpl info2 = (MetaObjectImpl)b;
	return (info1.getXPath().compareTo(info2.getXPath())); 
	} // en
}
