/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
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
import org.jdom.Element;

import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociation;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociationEnd;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLDatatype;
import gov.nih.nci.ncicb.xmiinout.domain.UMLGeneralization;
import gov.nih.nci.ncicb.xmiinout.domain.UMLModel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLPackage;
import gov.nih.nci.ncicb.xmiinout.domain.bean.JDomDomainObject;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLAttributeBean;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLModelBean;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLPackageBean;
import gov.nih.nci.ncicb.xmiinout.handler.HandlerEnum;
import gov.nih.nci.ncicb.xmiinout.handler.XmiException;
import gov.nih.nci.ncicb.xmiinout.handler.XmiHandlerFactory;
import gov.nih.nci.ncicb.xmiinout.handler.XmiInOutHandler;
import gov.nih.nci.ncicb.xmiinout.util.ModelUtil;

/**
 * @author Eugene Wang
 * This class is used to create a LinkedHashMap of Metadata objects representing the UML
 * model being imported into the caAdapter tool. The class loads an XMI file
 * and parses through it creating a LinkedHashMap composed of keys, which are
 * basically xpath like links to the position in the XMI file where the UML Model element
 * is located. The value in the key/value pair is either an ObjectMetadata,
 * AttributeMetadata, AssociationMetadata, TableMetadata, or ColumnMetadata
 * object. The key/value pairs are loaded into the LinkedHashMap in order of location
 * in the XMI file and can be retrieved in the same order. The contents of the
 * LinkedHashMap can be used to construct the Object and Data model portions of
 * the caAdapter JTree mapping UI.

 * @author LAST UPDATE $Author: wangeug $
 * @since      caAdapter  v4.2
 * @version    $Revision: 1.15 $
 * @date       $Date: 2009-06-12 15:14:21 $
 * */

public class XmiModelMetadata {
	private XmiInOutHandler handler = null;
	private String xmiFileName;

	private LinkedHashMap modelHashMap = new LinkedHashMap();
	private HashMap objectHashMap = new HashMap();
	private HashMap inheritanceHashMap = new HashMap();
	private LinkedHashMap umlHashMap = new LinkedHashMap();

	private HashSet<String> primaryKeys = new HashSet<String>();
	private HashSet<String> lazyKeys = new HashSet<String>();
    private HashSet<String> discriminatorKeys = new HashSet<String>();
    private HashSet<String> clobKeys = new HashSet<String>();
    private Hashtable<String, String> discriminatorValues = new Hashtable<String, String>();

    private static String mmsPrefixObjectModel = "Logical View.Logical Model";
    private static String mmsPrefixDataModel = "Logical View.Data Model";

	/**
	 * Construct a ModelMetaData with xmi file name
	 * @param xmiFile
	 */

	public XmiModelMetadata(String xmiFile){
		xmiFileName = xmiFile;
		loadXmiModel();
	}

	private void loadXmiModel()
	{
		long stTime=System.currentTimeMillis();
//		System.out.println("XmiModelMetadata.XmiModelMetadata()..start loading:"+stTime);
		if(xmiFileName==null)
			return;
		if(xmiFileName.equals(""))
			return;

		XmiInOutHandler xmiHandeler=initXmiHandler(xmiFileName);
		if (xmiHandeler==null)
			return;
		handler=xmiHandeler;

		UMLModel umlModel=null;
		try {
			 handler.load(xmiFileName);
			 umlModel =handler.getModel();
	    } catch (XmiException e) {
		    	e.printStackTrace();
	    } catch (IOException e) {
		    	e.printStackTrace();
	    }

	    if(umlModel==null)
	    	return;

	    TreeSet sortedModel = loadUMLModel(umlModel);
		Object[] sortedArray  = sortedModel.toArray();

		modelHashMap = new LinkedHashMap();
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
		System.out.println("XmiModelMetadata.XmiModelMetadata()..end loading:"+(System.currentTimeMillis()-stTime));
	}

    private XmiInOutHandler initXmiHandler(String fileName)
    {
    	XmiInOutHandler rtnHandler;
        // Check for the agrUML or EA XMI
        // Decide which parser to use to open this XMI file
        boolean eaExporter = false;
        try {
            BufferedReader in = new BufferedReader(new FileReader( xmiFileName ));
            String str;
            while ((str = in.readLine()) != null&&!eaExporter)
            {
                if ( str.contains("<XMI.exporter>Enterprise Architect</XMI.exporter>") )
                {
                    eaExporter = true;
                }
            }
            in.close();
        }
        catch (IOException e) {
        	e.printStackTrace();
        }

        if ( eaExporter == true ) {
            System.out.println("Handler using EADefault:"+HandlerEnum.EADefault);
            rtnHandler = XmiHandlerFactory.getXmiHandler(HandlerEnum.EADefault);
        } else {
            System.out.println("Handler using ArgoUMLDefault:"+HandlerEnum.ArgoUMLDefault);
            rtnHandler = XmiHandlerFactory.getXmiHandler(HandlerEnum.ArgoUMLDefault);
        }
    	return rtnHandler;
    }

	private TreeSet loadUMLModel(UMLModel model)
    {
		TreeSet rtnSet=new TreeSet(new XPathComparator());
		XmiTraversalPath xmiPath=new XmiTraversalPath(model.getName());
//		System.out.println("XmiModelMetadata.loadUMLModel()..path Nevigator:"+xmiPath.pathNevigator());
		umlHashMap.put(xmiPath.pathNevigator(), model);
		for( UMLPackage pkg : model.getPackages() )
        {
			loadPackage(rtnSet,xmiPath, pkg);
        }
        return rtnSet;
    }

    private void loadPackage(TreeSet sortedModel,XmiTraversalPath traversalPath, UMLPackage pkg)
    {
    	traversalPath.addOnePathElement(pkg.getName());
//    	System.out.println("XmiModelMetadata.loadPackage()..path Nevigator:"+traversalPath.pathNevigator());
    	umlHashMap.put(traversalPath.pathNevigator(), pkg);
        for(UMLClass clazz : pkg.getClasses())
        {
        	traversalPath.addOnePathElement(clazz.getName());
//    		System.out.println("XmiModelMetadata.loadClass()..path Nevigator:"+traversalPath.pathNevigator());
    		umlHashMap.put(traversalPath.pathNevigator(), clazz);
            StringBuffer pathKey = new StringBuffer(ModelUtil.getFullPackageName(clazz));
            if (pathKey.toString().contains(  XmiModelMetadata.getMmsDataModelPrefix() )) {
                //create a TableMetadata object
                TableMetadata table = new TableMetadata();
                table.setName(clazz.getName());
                pathKey.append(".");
                pathKey.append(clazz.getName());
                table.setXPath(pathKey.toString());
                table.setXmlPath(pathKey.toString());
                sortedModel.add(table);
                for(UMLAttribute att : clazz.getAttributes()) {
                    loadColumnAttribute(sortedModel,traversalPath, att, table, pathKey);
                }
            } else if (pathKey.toString().contains( XmiModelMetadata.getMmsObjectModelPrefix() ) && !pathKey.toString().contains("java"))
            {
            	//load ObjectMeta
                ObjectMetadata object = new ObjectMetadata();
                object.setUmlClass(clazz);
                object.setName(clazz.getName());
                pathKey.append(".");
                pathKey.append(clazz.getName());
                object.setXPath(pathKey.toString());
                object.setXmlPath(pathKey.toString());
                object.setId(clazz.toString());
                objectHashMap.put(clazz.toString(), pathKey.toString());

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
                    parent = (UMLClass)clazzG.getSupertype();
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
                            parent = (UMLClass)clazzG.getSupertype();
                            if (parent != pre) {break;}
                        }
                        if (parent == pre) parent = null;
                    }
                    for(UMLClass p : parents) {
                        for(UMLAttribute att : p.getAttributes()) {
                            loadAttribute(sortedModel, traversalPath, att, object, pathKey, true);
                        }
                    }
                }
                for(UMLAttribute att : clazz.getAttributes()) {
                	loadAttribute(sortedModel, traversalPath, att, object, pathKey, false);
                }
                for(UMLAssociation assoc : clazz.getAssociations()) {
                     loadAssociation(sortedModel, traversalPath, assoc, object, pathKey, clazz);
                }
            }
            traversalPath.removeLastPathElement(clazz.getName());
        }
        //load sub package
        for(UMLPackage _pkg : pkg.getPackages()) {
          loadPackage(sortedModel,traversalPath, _pkg);
        }
        traversalPath.removeLastPathElement(pkg.getName());
      }

	  private void loadAssociation(TreeSet sortedModel, XmiTraversalPath traversalPath, UMLAssociation assoc, ObjectMetadata object, StringBuffer keyPath, UMLClass clazz) {
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
		    for(UMLAssociationEnd assocEnd : assoc.getAssociationEnds())
		    {
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

	  			if (!clazz1.getName().equals(clazz.getName()))
	  			{
	    			if (assocEnd.isNavigable()||isOneToMany || isManyToMany) {
	    				thisEnd.setRoleName(assocEnd.getRoleName());
	    				thisEnd.setMultiplicity(assocEnd.getHighMultiplicity());
	    				thisEnd.setReciprocalMultiplity(getReciprocalMultiplicity(assocEnd));
	    				thisEnd.setXPath(associationKeyPath.toString());
	    				thisEnd.setXmlPath(associationKeyPath.toString());
	    				thisEnd.setReturnTypeXPath(clazz1.toString());
	    				thisEnd.setNavigability(assocEnd.isNavigable());
	    				thisEnd.setBidirectional(other.isNavigable()&&assocEnd.isNavigable());
	    				sortedModel.add(thisEnd);
	    			}
	    			else {//This is uni-direction
	    			}
	    			traversalPath.addOnePathElement(assocEnd.getRoleName());
//	    			System.out.println("XmiModelMetadata.loadAssociation()..path Nevigator:"+traversalPath.pathNevigator());

	    	    	umlHashMap.put(traversalPath.pathNevigator(), assoc);
	    	    	traversalPath.removeLastPathElement(assocEnd.getRoleName());

		    	}
	    	}

	  }

	  private void loadAttribute(TreeSet sortedModel, XmiTraversalPath traversalPath, UMLAttribute att, ObjectMetadata object, StringBuffer pathKey, boolean derived) {
		  	traversalPath.addOnePathElement(att.getName());
//	    	System.out.println("XmiModelMetadata.loadAttribute()..path Nevigator:"+traversalPath.pathNevigator());
	    	umlHashMap.put(traversalPath.pathNevigator(), att);
	    	StringBuffer attributePath = new StringBuffer();
		    attributePath.append(pathKey);
	        AttributeMetadata attMetadata = new AttributeMetadata();
	        attMetadata.setName(att.getName());
	        UMLDatatype attDt=att.getDatatype();
	        if (attDt!=null)
	        	attMetadata.setDatatype(attDt.getName());
	        attributePath.append(".");
	        attributePath.append(att.getName());
	        attMetadata.setXPath(attributePath.toString());
	        attMetadata.setXmlPath(attributePath.toString());
	        attMetadata.setDerived(derived);
	        sortedModel.add(attMetadata);
	        traversalPath.removeLastPathElement(att.getName());
	        //attMetadata.setSemanticConcept(att.getTaggedValue("conceptId").getValue());
	  }

	  private void loadColumnAttribute(TreeSet sortedModel, XmiTraversalPath traversalPath, UMLAttribute att, TableMetadata tableMeta, StringBuffer pathKey)
	  {
		  	traversalPath.addOnePathElement(att.getName());
//	    	System.out.println("XmiModelMetadata.loadColumnAttribute()..path Nevigator:"+traversalPath.pathNevigator());
	    	umlHashMap.put(traversalPath.pathNevigator(), att);
	        ColumnMetadata attMetadata = new ColumnMetadata();
	        StringBuffer colPathKey = new StringBuffer();
	        colPathKey.append(pathKey);
	        attMetadata.setName(att.getName());
	        UMLDatatype attDt=att.getDatatype();
	        if (attDt!=null)
	        	attMetadata.setDatatype(attDt.getName());
	        colPathKey.append(".");
	        colPathKey.append(att.getName());
	        attMetadata.setXPath(colPathKey.toString());
	        attMetadata.setXmlPath(colPathKey.toString());
	        attMetadata.setTableMetadata(tableMeta);
	        sortedModel.add(attMetadata);
	        traversalPath.removeLastPathElement(att.getName());
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
		loadXmiModel();
	}

	public XmiInOutHandler getHandler() {
		return handler;
	}

	public void setHandler(XmiInOutHandler handler) {
			this.handler = handler;
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
		 * @param args
		 */
		public static void main(String[] args) {
	//		String xmiPath="C:\\CVS\\caadapter\\workingspace\\Object_to_DB_Example\\sample.xmi";
			String xmiPath="workingspace\\Object_to_DB_Example\\sdk.xmi";
			XmiModelMetadata myModel = new XmiModelMetadata(xmiPath);

			LinkedHashMap myMap = myModel.getModelMetadata();
			Set mySet = myMap.keySet();
			for (Iterator i = mySet.iterator(); i.hasNext();) {
			   String key = (String)i.next();
			   System.out.println(key);
			   Object x = myMap.get(key);
			   //System.out.println(x);
			}
		}

	public HashSet getPrimaryKeys()
	{
			return primaryKeys;
	}

		public void setPrimaryKeys( HashSet keyList )
		{
			primaryKeys = keyList;
		}

		public HashSet getLazyKeys()
		{
			return lazyKeys;
		}

		public void setLazyKeys( HashSet lazyKeyList )
		{
			lazyKeys = lazyKeyList;
		}

		public static String getMmsObjectModelPrefix() {
            return mmsPrefixObjectModel;
        }

        public static void setMmsObjectModelPrefix(String mmsPrefixObjectModel) {
        	XmiModelMetadata.mmsPrefixObjectModel = mmsPrefixObjectModel;
        }

        public static String getMmsDataModelPrefix() {
            return mmsPrefixDataModel;
        }

        public static void setMmsDataModelPrefix(String mmsPrefixDataModel) {
        	XmiModelMetadata.mmsPrefixDataModel = mmsPrefixDataModel;
        }

        public   HashSet<String> getClobKeys() {
            return clobKeys;
        }

        public   void setClobKeys(HashSet<String> clobKeys) {
        	clobKeys = clobKeys;
        }

        public   HashSet<String> getDiscriminatorKeys() {
            return discriminatorKeys;
        }

        public  void setDiscriminatorKeys(HashSet<String> discriminatorKeys) {
        	discriminatorKeys = discriminatorKeys;
        }

        private  static int getReciprocalMultiplicity(UMLAssociationEnd assocEnd) {
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
	 * @return the discriminatorValues
	 */
	public Hashtable<String, String> getDiscriminatorValues() {
		return discriminatorValues;
	}

	/**
	 * @param discriminatorValues the discriminatorValues to set
	 */
	public void setDiscriminatorValues(
			Hashtable<String, String> discriminatorValues) {
		discriminatorValues = discriminatorValues;
	}

	public LinkedHashMap getUmlHashMap() {
		return umlHashMap;
	}
	/**
	 * Clean the Tagged value in the "ModelElment.taggedValue" child given the tag name
	 * @param pkg
	 * @param tagName
	 */
	public void cleanPackageTaggedValue(UMLPackage pkg, String tagName)
	{
		UMLPackageBean pkgBean=(UMLPackageBean)pkg;
		Element pkgElmt=pkgBean.getJDomElement();
		UMLModelBean modelBean=(UMLModelBean)getHandler().getModel();
		Element modelElement=(Element)modelBean.getJDomElement();
		Element taggedValueElmnt=pkgElmt.getChild("ModelElement.taggedValue", modelElement.getNamespace());
		cleanElmentTaggedChild(taggedValueElmnt, tagName);
	}

	/**
	 * Remove "taggedValue" element from a JDOM element
	 * @param elmnt
	 * @param tagName
	 */
	private void cleanElmentTaggedChild(Element elmnt, String tagName)
	{
		UMLModelBean modelBean=(UMLModelBean)getHandler().getModel();
		Element modelElement=(Element)modelBean.getJDomElement();
		List<Element> taggedList=elmnt.getChildren("TaggedValue", modelElement.getNamespace());
		for (int i=taggedList.size();i>0;i--)
		{
			Element tagElmnt=taggedList.get(i-1);
			String tagAttrName=tagElmnt.getAttributeValue("tag");
			if (tagAttrName.equalsIgnoreCase(tagName))
				taggedList.remove(i-1);
		}
	}

    public void cleanProjectObjectAnnotation()
    {
 		UMLModelBean modelBean=(UMLModelBean)getHandler().getModel();
        Element prjElmt=modelBean.getJDomElement();
        //UMLModelBean modelBean=(UMLModelBean)getHandler().getModel();
		Element modelElement=(Element)modelBean.getJDomElement();
		Element taggedValueElmnt=prjElmt.getChild("ModelElement.taggedValue", modelElement.getNamespace());

        if (taggedValueElmnt != null)
            cleanElmentTaggedChild(taggedValueElmnt, "NCI_GME_XML_NAMESPACE");

//        modelBean.removeTaggedValue("NCI_GME_XML_NAMESPACE");
    }

    public void annotateProjectObject(String gmeXmlNamespace)
    {
        UMLModelBean modelBean=(UMLModelBean)getHandler().getModel();
        modelBean.addTaggedValue("NCI_GME_XML_NAMESPACE", gmeXmlNamespace );
    }

    /**
	 * Clean all annotation information related with classes, which are set with XMI.content element
	 *
	 */
	public void cleanClassObjectAnnotation()
	{
		UMLModelBean modelBean=(UMLModelBean)getHandler().getModel();
		Element modelElement=(Element)modelBean.getJDomElement();

		Element xmiContent=modelElement.getParentElement();
		cleanElmentTaggedChild(xmiContent, "NCI_GME_XML_NAMESPACE");
		cleanElmentTaggedChild(xmiContent, "NCI_GME_XML_ELEMENT");
 	}

	/**
	 * Annotate XMI file once one target class being mapped
	 * @param gmeXmlNamespace
	 * @param packageModelElementId
	 * @param gmeXmlElementName
	 * @param classModelElementId
	 */
	public void annotateClassObject(String gmeXmlNamespace, String packageModelElementId, String gmeXmlElementName,String classModelElementId)
	{

		addClassObjectAnnotationTag("NCI_GME_XML_NAMESPACE", gmeXmlNamespace,packageModelElementId);

		addClassObjectAnnotationTag("NCI_GME_XML_ELEMENT", gmeXmlElementName,classModelElementId);
	}

    public Element getXmiContent()
    {
        UMLModelBean modelBean=(UMLModelBean)getHandler().getModel();
		Element modelElement=(Element)modelBean.getJDomElement();

		Element xmiContent=modelElement.getParentElement();
        return xmiContent;
    }

    private void addClassObjectAnnotationTag(String tag, String value, String modelElmentId)
	{
		UMLModelBean modelBean=(UMLModelBean)getHandler().getModel();
		Element modelElement=(Element)modelBean.getJDomElement();

		Element xmiContent=modelElement.getParentElement();
		Element newGmeTag=new Element("TaggedValue", modelElement.getNamespace());
		newGmeTag.setAttribute("tag", tag);
		newGmeTag.setAttribute("value", value);
		// create one XMI.ID
		Iterator xsdBeanIt = getUmlHashMap().keySet().iterator();//xsdModelMeta.getAttributeMap().keySet().iterator();
		UMLAttributeBean umlAttrBean=null;
		while (xsdBeanIt.hasNext())
        {
              String umlObjKey=(String)xsdBeanIt.next();
              Object umlAttr=this.getUmlHashMap().get(umlObjKey);
              if (umlAttr instanceof UMLAttributeBean)
              {
            	  umlAttrBean=(UMLAttributeBean)umlAttr;
            	  break;
              }
        }
		String tempXmiId="";
		if (umlAttrBean!=null)
		{
			String tempTagName="CAADAPTER_TEMPORARY_TAG";
			String tempTagValue="CAADAPTER_TEMPORARY_VALUE_AdditionValue";
			umlAttrBean.addTaggedValue(tempTagName,tempTagValue);
			//read XMI.ID from the new <TaggedValue>
			Element taggedValueElmnt=umlAttrBean.getJDomElement().getChild("ModelElement.taggedValue", modelElement.getNamespace());
			List<Element> taggedValues=taggedValueElmnt.getChildren("TaggedValue", modelElement.getNamespace());
			for (Element taggedElm:taggedValues)
			{
				if (taggedElm.getAttributeValue("tag")!=null
						&&taggedElm.getAttributeValue("tag").equals(tempTagName))
					tempXmiId=taggedElm.getAttributeValue("xmi.id");
			}
			umlAttrBean.removeTaggedValue(tempTagName);
		}
		//set XMI.ID attribute
		newGmeTag.setAttribute("xmi.id",tempXmiId);
		newGmeTag.setAttribute("modelElement", modelElmentId);
		xmiContent.getChildren().add(newGmeTag);
	}

	public String findModelElementXmiId(String modelElementXmlPath)
	{
		Object modelObj=umlHashMap.get(modelElementXmlPath);
		if (modelObj==null)
			return "not found:"+modelElementXmlPath;
		if (modelObj instanceof JDomDomainObject)
		{
			JDomDomainObject umlBean=(JDomDomainObject)modelObj;
			return umlBean.getJDomElement().getAttributeValue("xmi.id");
		}
		return "invalideObject:"+modelObj;
	}
}

class XmiXPathComparator implements Comparator {
	public final int compare ( Object a, Object b)
	{
		MetaObjectImpl info1 = (MetaObjectImpl)a;
		MetaObjectImpl info2 = (MetaObjectImpl)b;
	return (info1.getXPath().compareTo(info2.getXPath()));
	} // en
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.14  2008/09/25 19:30:39  phadkes
 * HISTORY      : Changes for code standards
 * HISTORY      :
*/
