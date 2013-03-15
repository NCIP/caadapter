/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.common.metadata;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.util.Iso21090Util;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociation;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociationEnd;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLGeneralization;
import gov.nih.nci.ncicb.xmiinout.domain.UMLModel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLPackage;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLAssociationEndBean;
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
 * @author LAST UPDATE $Author: wangeug $
 * @since      caAdapter  v4.2
 * @version    $Revision: 1.14 $
 * @date       $Date: 2009-09-30 17:10:49 $
 */
public class ModelMetadata {
	private static ModelMetadata modelMetadata = null;

	private XmiInOutHandler handler = null;
	private LinkedHashMap modelHashMap = new LinkedHashMap();
	private HashMap inheritanceHashMap = new HashMap();
	private TreeSet sortedModel = new TreeSet(new XPathComparator());
	private String xmiFileName;
	private HashSet<String> primaryKeys = new HashSet<String>();
	private HashSet<String> lazyKeys = new HashSet<String>();
    private HashSet<String> discriminatorKeys = new HashSet<String>();
    private HashSet<String> clobKeys = new HashSet<String>();
    private Hashtable<String, String> discriminatorValues = new Hashtable<String, String>();

    private String mmsPrefixObjectModel = "Logical View.Logical Model";
    private String mmsPrefixDataModel = "Logical View.Data Model";

	public ModelMetadata(String xmiFileName){
		this.xmiFileName = xmiFileName;
		init();
	}

	private  void init()
	{
	    try {
            // Check for the agrUML or EA XMI
            // Decide which parser to use to open this XMI file
            boolean eaExporter = false;

            try {
                BufferedReader in = new BufferedReader(new FileReader( xmiFileName ));
                String str;

                while ((str = in.readLine()) != null)
                {
                    if ( str.contains("<XMI.exporter>Enterprise Architect</XMI.exporter>") )
                    {
                        eaExporter = true;
                        break;
                    }
                }
                in.close();
            } catch (IOException e) {
            	e.printStackTrace();
            }

            if ( eaExporter == true ) {
                System.out.println("Handler using EADefault");
                handler = XmiHandlerFactory.getXmiHandler(HandlerEnum.EADefault);
            } else {
                System.out.println("Handler using ArgoUMLDefault");
                handler = XmiHandlerFactory.getXmiHandler(HandlerEnum.ArgoUMLDefault);
            }

            handler.load(xmiFileName);
            for( UMLPackage pkg :handler.getModel().getPackages() )
            {
                initProcessPackage(pkg);
            }
	    } catch (XmiException e) {
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
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
	  }

	private void initProcessPackage(UMLPackage pkg)
    {
		if(pkg.getName().contains("valueDomain")
				||pkg.getName().contains("ValueDomain")
				||pkg.getName().contains("valuedomain"))
		{
			Log.logInfo(this, "Ignore package...Name="+pkg.getName());
			return;
		}

        for(UMLClass clazz : pkg.getClasses())
        {
			String clzStereoType=clazz.getStereotype();
			if(clzStereoType!=null&&
					(clzStereoType.equalsIgnoreCase("enumeration")
							||clzStereoType.contains("Value")
							||clzStereoType.contains("value")
							||clzStereoType.contains("Domain")
							||clzStereoType.contains("Domain")))
			{
				Log.logInfo(this, "Ignore valueDomain...Name="+clazz.getName()+"...Stereotype="+clzStereoType);
				continue;
			}

            StringBuffer pathKey = new StringBuffer(ModelUtil.getFullPackageName(clazz));
            if (pathKey.toString().contains(mmsPrefixDataModel )) {
                //create a TableMetadata object
                TableMetadata table = new TableMetadata();
                table.setName(clazz.getName());
                pathKey.append(".");
                pathKey.append(clazz.getName());
                table.setXPath(pathKey.toString());

                sortedModel.add(table);
                for(UMLAttribute att : clazz.getAttributes()) {
                	initProcessColumnAttribute(att, table, pathKey);
                }
            } else if (pathKey.toString().contains( mmsPrefixObjectModel) && !pathKey.toString().contains("java")) {

                ObjectMetadata object = new ObjectMetadata();
                object.setUmlClass(clazz);
                object.setName(clazz.getName());
                pathKey.append(".");
                pathKey.append(clazz.getName());
                object.setXPath(pathKey.toString());
                object.setId(clazz.toString());
                sortedModel.add(object);

                /* The following code look through the inheritance hierarchy and populate
                 * attributes of all super classed to current object
                 */
                UMLClass parent = null;
               /*
                 *trace to all of the ancestors.
                 * when class generation list is not empty, it could have supertype, or subtype
                 * To verify it really has a parent, we need to make sure the supertype is different then itself
                 * also, one assumption is one class can only have one supertype
                 */
                for (UMLGeneralization clazzG : clazz.getGeneralizations()) {
                	UMLClass gClass =(UMLClass) clazzG.getSupertype();
                    if (gClass != clazz) {
                    	parent=gClass;
                        inheritanceHashMap.put(object.getXPath(),parent.getName());
                        break;
                    }
                }
                while (parent != null) {
                    for(UMLAttribute att : parent.getAttributes())
                    	initProcessAttribute(att, object, pathKey, true);

                    UMLClass preParent = parent;
                    parent = null;
                    for (UMLGeneralization clazzG : preParent.getGeneralizations())
                    {
                    	UMLClass gClass =(UMLClass) clazzG.getSupertype();
                        if (gClass != preParent)
                        {
                        	parent=gClass;
                        	break;
                        }
                    }
                }
                for(UMLAttribute att : clazz.getAttributes())
                	initProcessAttribute(att, object, pathKey, false);

                for(UMLAssociation assoc : clazz.getAssociations()) {
                	initProcessAssociation(assoc, object, pathKey, clazz);
                }
            }
        }
        for(UMLPackage _pkg : pkg.getPackages()) {
          initProcessPackage(_pkg);
        }
      }


	  private void initProcessAssociation(UMLAssociation assoc, ObjectMetadata object, StringBuffer keyPath, UMLClass clazz) {
		  	boolean isOneToMany = false;
		  	boolean isManyToMany = false;
	    	UMLAssociationEnd assocEndA = (UMLAssociationEnd)assoc.getAssociationEnds().get(0);
	    	UMLAssociationEnd assocEndB = (UMLAssociationEnd)assoc.getAssociationEnds().get(1);
	    	//check if it is self-association
	    	boolean isSelfAssociated=false;
	    	String eaIdEndA=((UMLAssociationEndBean)assocEndA).getJDomElement().getAttributeValue("type");
	    	String eaIdEndB=((UMLAssociationEndBean)assocEndB).getJDomElement().getAttributeValue("type");
	    	if(eaIdEndA!=null && eaIdEndA.equals(eaIdEndB))
	    		isSelfAssociated=true;
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
	    		if (assocEnd.getRoleName().equals(""))
	    			continue;

	    		if (!clazz1.getName().equals(clazz.getName())||isSelfAssociated)
	    		{
	    			if (assocEnd.isNavigable()||isOneToMany || isManyToMany) {
	    				thisEnd.setRoleName(assocEnd.getRoleName());
	    				thisEnd.setMultiplicity(assocEnd.getHighMultiplicity());
	    				thisEnd.setReciprocalMultiplity(findReciprocalMultiplicity(assocEnd));
	    				thisEnd.setXPath(associationKeyPath.toString());
	    				thisEnd.setReturnTypeXPath(clazz1.toString());
	    				thisEnd.setNavigability(assocEnd.isNavigable());
	    				thisEnd.setBidirectional(other.isNavigable()&&assocEnd.isNavigable());
	    				sortedModel.add(thisEnd);
	    				object.addAssociation(thisEnd);
	    			}
	    		}
	    	}
	  }

	  private int findReciprocalMultiplicity(UMLAssociationEnd assocEnd) {
		int otherEndMultiplicity = 0;
		otherEndMultiplicity = findOtherAssociationEnd(assocEnd).getHighMultiplicity();
		return otherEndMultiplicity;
	}

	private UMLAssociationEnd findOtherAssociationEnd(UMLAssociationEnd assocEnd) {
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

	private  void initProcessAttribute(UMLAttribute att, ObjectMetadata object, StringBuffer pathKey, boolean derived) {
		   	String upcaseKey=att.getDatatype().getName().toUpperCase();
		   	if (upcaseKey.startsWith("SEQUENCE"))
		   	{
		   		for (int partIndx=0; partIndx<Iso21090Util.findSequenceSize(upcaseKey);partIndx++)
		   		{
					StringBuffer attributePath = new StringBuffer();
				    attributePath.append(pathKey);
			        AttributeMetadata attMetadata = new AttributeMetadata();
			        String attIndxName=att.getName()+"["+partIndx+"]";
			        attMetadata.setName(attIndxName);
			        attMetadata.setDatatype(att.getDatatype().getName());
			        attributePath.append(".");
			        attributePath.append(attIndxName);
			        attMetadata.setXPath(attributePath.toString());
			        attMetadata.setXmlPath(attributePath.toString());
			        attMetadata.setDerived(derived);
			        sortedModel.add(attMetadata);
			        object.addAttribute(attMetadata);
		   		}

		   		return;
		   	}
			StringBuffer attributePath = new StringBuffer();
		    attributePath.append(pathKey);
	        AttributeMetadata attMetadata = new AttributeMetadata();
	        attMetadata.setName(att.getName());
	        attMetadata.setDatatype(att.getDatatype().getName());
	        attributePath.append(".");
	        attributePath.append(att.getName());
	        attMetadata.setXPath(attributePath.toString());
	        attMetadata.setXmlPath(attributePath.toString());
	        attMetadata.setDerived(derived);
	        sortedModel.add(attMetadata);
	        object.addAttribute(attMetadata);
	        //attMetadata.setSemanticConcept(att.getTaggedValue("conceptId").getValue());
	  }

	  private void initProcessColumnAttribute(UMLAttribute att, TableMetadata object, StringBuffer pathKey)
	  {
	        ColumnMetadata attMetadata = new ColumnMetadata();
	        StringBuffer colPathKey = new StringBuffer();
	        colPathKey.append(pathKey);
	        attMetadata.setName(att.getName());
	        attMetadata.setDatatype(att.getDatatype().getName());
	        colPathKey.append(".");
	        colPathKey.append(att.getName());
	        attMetadata.setXPath(colPathKey.toString());
	        attMetadata.setTableMetadata(object);
	        sortedModel.add(attMetadata);
	        object.addColumn(attMetadata);
//	        System.out.println("xxxxxxxxxxxxxxx COLUMN: " + colPathKey);
	  }
	  	public XmiInOutHandler getHandler() {
		return handler;
	}

		public HashMap getInheritanceMetadata() {
	  		return inheritanceHashMap;
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

		//		public void setPrimaryKeys( HashSet keyList )
//		{
//			primaryKeys = keyList;
//		}

		//		public void setLazyKeys( HashSet lazyKeyList )
//		{
//			lazyKeys = lazyKeyList;
//		}
//
		/**
		 * @return Returns the model.
		 */
		public UMLModel getModel() {
			return handler.getModel();
		}

        public String getMmsPrefixObjectModel() {
            return mmsPrefixObjectModel;
        }

        public void setMmsPrefixObjectModel(String mmsPrefixObjectModelSet) {
            mmsPrefixObjectModel = mmsPrefixObjectModelSet;
        }

        public String getMmsPrefixDataModel() {
            return mmsPrefixDataModel;
        }

        public void setMmsPrefixDataModel(String mmsPrefixDataModelSet) {
        	mmsPrefixDataModel = mmsPrefixDataModelSet;
        }

        public HashSet getPrimaryKeys()
		{
			return primaryKeys;
		}

		//		public void setPrimaryKeys( HashSet keyList )
		//		{
		//			primaryKeys = keyList;
		//		}

		public HashSet getLazyKeys()
		{
			return lazyKeys;
		}

		public HashSet<String> getClobKeys() {
            return clobKeys;
        }

//        public void setClobKeys(HashSet<String> clobKeysSet) {
//            clobKeys = clobKeysSet;
//        }

        public HashSet<String> getDiscriminatorKeys() {
            return discriminatorKeys;
        }
//
//        public void setDiscriminatorKeys(HashSet<String> discriminatorKeysSet) {
//            discriminatorKeys = discriminatorKeysSet;
//        }

        /**
	 * @return the discriminatorValues
	 */
	public Hashtable<String, String> getDiscriminatorValues() {
		return discriminatorValues;
	}

	/**
	 * @param discriminatorValues the discriminatorValues to set
	 */
//	public void setDiscriminatorValues(
//			Hashtable<String, String> discriminatorValuesSet) {
//		discriminatorValues = discriminatorValuesSet;
//	}

//	public List<String> getPreservedMappedTag() {
//		return preservedMappedTag;
//	}

	/**
		 * @param args
		 */
		public static void main(String[] args) {
	//		ModelMetadata myModel = new ModelMetadata("C:/sample.xmi");
			//myModel.setXmiFileName("C:/sample.xmi");
	//		ModelMetadata myModel1 = ModelMetadata.createModel("D:/projects/hl7sdk-new/workingspace/cacore.xmi");

			ModelMetadata myModel = new ModelMetadata("D:/projects/hl7sdk-new/workingspace/sample.xmi");
			LinkedHashMap myMap = myModel.getModelMetadata();
			Set mySet = myMap.keySet();
			for (Iterator i = mySet.iterator(); i.hasNext();) {
			   String key = (String)i.next();
			   //System.out.println(key);
			   Object x = myMap.get(key);
			   //System.out.println(x);
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

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.13  2009/09/29 17:39:44  wangeug
 * HISTORY      : exclude valueDomain from mapping panel view
 * HISTORY      :
 * HISTORY      : Revision 1.12  2009/07/30 17:32:27  wangeug
 * HISTORY      : clean codes: implement 4.1.1 requirements
 * HISTORY      :
 * HISTORY      : Revision 1.11  2009/07/10 19:53:12  wangeug
 * HISTORY      : MMS re-engineering
 * HISTORY      :
 * HISTORY      : Revision 1.10  2009/06/12 15:49:58  wangeug
 * HISTORY      : clean code: caAdapter MMS 4.1.1
 * HISTORY      :
 * HISTORY      : Revision 1.9  2008/09/25 19:30:39  phadkes
 * HISTORY      : Changes for code standards
 * HISTORY      :
*/
