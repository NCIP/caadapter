/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.cbiit.cmts.common;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import gov.nih.nci.cbiit.cmts.core.AttributeMeta;
import gov.nih.nci.cbiit.cmts.core.Component;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.core.KindType;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociation;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociationEnd;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLDatatype;
import gov.nih.nci.ncicb.xmiinout.domain.UMLGeneralization;
import gov.nih.nci.ncicb.xmiinout.domain.UMLModel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLPackage;
import gov.nih.nci.ncicb.xmiinout.handler.HandlerEnum;
import gov.nih.nci.ncicb.xmiinout.handler.XmiException;
import gov.nih.nci.ncicb.xmiinout.handler.XmiHandlerFactory;
import gov.nih.nci.ncicb.xmiinout.handler.XmiInOutHandler;

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

public class XmiModelParser {
	private XmiInOutHandler handler = null;
	private String xmiFileName;
	private Component rootComponent;


	private static String mmsPrefixObjectModel = "Logical View.Logical Model";
    private static String mmsPrefixDataModel = "Logical View.Data Model";

	/**
	 * Construct a ModelMetaData with xmi file name
	 * @param xmiFile
	 */

	public XmiModelParser(String xmiFile){
		xmiFileName = xmiFile;
		loadXmiModel();
	}

	private void loadXmiModel()
	{
		long stTime=System.currentTimeMillis();
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

	    ElementMeta xmiElementMeta = loadUMLModel(umlModel);
	    rootComponent =new Component();
	    rootComponent.setRootElement(xmiElementMeta);
	    rootComponent.setKind(KindType.fromValue("xmi"));

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

	private ElementMeta loadUMLModel(UMLModel model)
    {
		ElementMeta rtnMeta=new ElementMeta();
		rtnMeta.setName(model.getName());

		XmiTraversalPath xmiPath=new XmiTraversalPath(model.getName());
		for( UMLPackage pkg : model.getPackages() )
        {
			ElementMeta pkgElement=loadPackage(xmiPath, pkg);
			//ignore empty package or "Data Model" package
			if (pkgElement.getChildElement().isEmpty())
				continue;
			rtnMeta.getChildElement().add(pkgElement);
        }
        return rtnMeta;
    }

    private ElementMeta loadPackage(XmiTraversalPath traversalPath, UMLPackage pkg)
    {
    	traversalPath.addOnePathElement(pkg.getName());
		ElementMeta packageMeta=new ElementMeta();
		packageMeta.setName(pkg.getName());
		for(UMLPackage childPkg:pkg.getPackages())
		{
			ElementMeta childPkgElement=loadPackage(traversalPath, childPkg);
			//ignore empty package or "Data Model" package
			if (childPkgElement.getChildElement().isEmpty())
				continue;
			packageMeta.getChildElement().add(childPkgElement);
		}

        for(UMLClass clazz : pkg.getClasses())
        {
        	//ignore table
        	if (clazz.getStereotype()!=null&&clazz.getStereotype().equalsIgnoreCase("table"))
        		continue;
        	packageMeta.getChildElement().add(parseUMLClass(traversalPath, clazz));
        }

        traversalPath.removeLastPathElement(pkg.getName());
        return packageMeta;
      }

    private ElementMeta parseUMLClass(XmiTraversalPath traversalPath, UMLClass umlClass)
    {
    	traversalPath.addOnePathElement(umlClass.getName());

        //load ObjectMeta
        ElementMeta objectMeta = new ElementMeta();
        objectMeta.setName(umlClass.getName());


        /* The following code look through the inheritance hierachy and populate
         * attributes of all parents to the object
         */
        ArrayList<UMLClass> ancestors = new ArrayList<UMLClass>();
        UMLClass parent = null;
        UMLClass grandParent = null;
        List<UMLGeneralization> clazzGs = umlClass.getGeneralizations();

        /* Step 1
         * trace to all of the ancesters.
         * when clazzG is not empty, it could have supertype, or subtype
         * To verify it really has a parent, we need to make sure the supertype is different then itself
         * also, one assumption is one class can only have one supertype
         */
		for (UMLGeneralization clazzG : clazzGs) {
		    parent = (UMLClass)clazzG.getSupertype();
		    if (parent != umlClass)
		        break;
		}
        if (parent!=umlClass) {
            while (parent != grandParent) {
                ancestors.add(parent);
                clazzGs = parent.getGeneralizations();
                grandParent = parent;
                parent = null;
                for (UMLGeneralization clazzG : clazzGs) {
                    parent = (UMLClass)clazzG.getSupertype();
                    if (parent != grandParent)
                    	{break;}
                }
            }
            //parse all attribute defined by all ancestors
            for(UMLClass p : ancestors) {
                for(UMLAttribute att : p.getAttributes()) {
                	objectMeta.getAttrData().add(parseAttribute(traversalPath, att, true));
                }
            }
        }

        //parse the attribute defined with current object
        for(UMLAttribute att : umlClass.getAttributes()) {
        	objectMeta.getAttrData().add(parseAttribute(traversalPath, att, false));
        }
        for(UMLAssociation assoc : umlClass.getAssociations()) {
        	ElementMeta assocElement=parseAssociation(traversalPath, assoc, umlClass);
        	if (assocElement!=null)
        		objectMeta.getChildElement().add(assocElement);
        }
        traversalPath.removeLastPathElement(umlClass.getName());
        return objectMeta;
    }

	  private ElementMeta parseAssociation(XmiTraversalPath traversalPath, UMLAssociation assoc, UMLClass objectClass) {
	    	ElementMeta thisEnd=null;
		    for(UMLAssociationEnd assocEnd : assoc.getAssociationEnds())
		    {
	    		UMLClass endClass =(UMLClass)assocEnd.getUMLElement();
//	  			if (assocEnd.getRoleName().equals(""))
//	  				continue;
		    	if (!assocEnd.isNavigable())
		    		continue;
		    	//only attach one end with the holding class
	  			if (endClass.getName().equals(objectClass.getName()))
	  				continue;
		    	thisEnd=new ElementMeta();
  				thisEnd.setName(assocEnd.getRoleName());

  				thisEnd.setMaxOccurs(BigInteger.valueOf(assocEnd.getHighMultiplicity()));
  				thisEnd.setMinOccurs(BigInteger.valueOf(assocEnd.getLowMultiplicity()));
	  		}
		    return thisEnd;
	  }

	  private AttributeMeta parseAttribute(XmiTraversalPath traversalPath, UMLAttribute att,  boolean derived) {
		  	traversalPath.addOnePathElement(att.getName());
		  	AttributeMeta rtnMeta=new AttributeMeta();
		  	rtnMeta.setName(att.getName());
		  	rtnMeta.setIsRequired(true);
		  	rtnMeta.setIsEnabled(true);

	        UMLDatatype attDt=att.getDatatype();
	        if (attDt!=null)
	        	rtnMeta.setType(attDt.getName());

	        traversalPath.removeLastPathElement(att.getName());
		  	return rtnMeta;
	  }


	  public XmiInOutHandler getHandler() {
		return handler;
	}

	public void setHandler(XmiInOutHandler handler) {
			this.handler = handler;
	}


	/**
	 * @return the rootComponent
	 */
	public Component getRootComponent() {
		return rootComponent;
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

	public static String getMmsObjectModelPrefix() {
	    return mmsPrefixObjectModel;
	}

    public static void setMmsObjectModelPrefix(String mmsPrefixObjectModel) {
    	XmiModelParser.mmsPrefixObjectModel = mmsPrefixObjectModel;
    }

    public static String getMmsDataModelPrefix() {
        return mmsPrefixDataModel;
    }

    public static void setMmsDataModelPrefix(String mmsPrefixDataModel) {
		XmiModelParser.mmsPrefixDataModel = mmsPrefixDataModel;
	}

	/**
	* @param args
	*/
    public static void main(String[] args) {
//		String xmiPath="C:\\CVS\\caadapter\\workingspace\\Object_to_DB_Example\\sample.xmi";
    	String xmiPath="workingspace\\objectModel\\sample.xmi";
    	XmiModelParser myModel = new XmiModelParser(xmiPath);
			printElement(myModel.getRootComponent().getRootElement());
	}

	private static void printElement(ElementMeta meta)
	{
		System.out.println("XmiModelMetadata.printElement()..:"+meta.getName());
		for(AttributeMeta attrMeta:meta.getAttrData())
			System.out.println("XmiModelMetadata.printElement()..\t"+attrMeta.getName());
		for (ElementMeta elmntMeta:meta.getChildElement())
			printElement(elmntMeta);

	}
}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.14  2008/09/25 19:30:39  phadkes
 * HISTORY      : Changes for code standards
 * HISTORY      :
*/
