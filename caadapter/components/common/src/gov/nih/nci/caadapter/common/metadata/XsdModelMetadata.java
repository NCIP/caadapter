/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.common.metadata;

import gov.nih.nci.caadapter.common.util.CaadapterUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import org.exolab.castor.util.Configuration;
import org.exolab.castor.util.LocalConfiguration;
import org.exolab.castor.util.NestedIOException;
import org.exolab.castor.xml.Namespaces;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLException;
import org.exolab.castor.xml.schema.AttributeDecl;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Group;
import org.exolab.castor.xml.schema.Particle;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.reader.Sax2ComponentReader;
import org.exolab.castor.xml.schema.reader.SchemaUnmarshaller;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;

public class XsdModelMetadata {
private Schema gmeSchema;
private final Configuration _config;
private HashMap <String, String>packageMap=new HashMap<String, String>();
private TreeMap <String,ObjectMetadata>objectMap=new TreeMap <String,ObjectMetadata>();
private TreeMap <String,AttributeMetadata>attributeMap=new TreeMap <String,AttributeMetadata>();
private TreeMap <String,AssociationMetadata>associationMap=new TreeMap <String,AssociationMetadata>();

private String projectName;
private String projectContext;
private String projectVersion;

public XsdModelMetadata()
{
    _config = LocalConfiguration.getInstance();
}

public void parseSchema(String filename)
{
	final File schemaFile;
    if (filename.startsWith("./")) {
        schemaFile = new File(filename.substring(2));
    } else {
        schemaFile = new File(filename);
    }

    try {
    	FileReader reader = new FileReader(schemaFile);
        InputSource source = new InputSource(reader);
        source.setSystemId(XsdUtil.toURIRepresentation(schemaFile.getAbsolutePath()));
        loadSchemaSource(source);
        reader.close();
    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} finally {

    }
	initModel();
}

private void initModel()
{

	if (gmeSchema==null)
		return;

	//set project name
	setProjectName(CaadapterUtil.findApplicationConfigValue("caadapter.gme.project.name"));
	setProjectContext(CaadapterUtil.findApplicationConfigValue("caadapter.gme.project.content"));
	setProjectVersion(CaadapterUtil.findApplicationConfigValue("caadapter.gme.project.version"));

//	setProjectName(gmeSchema.getAttribute("projectName").getDefaultValue());
//	setProjectContext(gmeSchema.getAttribute("projectContext").getDefaultValue());
//	setProjectVersion(gmeSchema.getAttribute("projectVersion").getDefaultValue());
//	String targetNsURI =gmeSchema.getTargetNamespace();
//	setProjectName(targetNsURI);



	//set class mapping
	buildSchemaClassMapping(gmeSchema);
	//set class mapping from imported schema(s)
	Enumeration imptSchema =gmeSchema.getImportedSchema();
	while(imptSchema.hasMoreElements())
	{
		 Schema schemaImported=(Schema)imptSchema.nextElement();
		 buildSchemaClassMapping(schemaImported);
	}

}
public String findPackageNamespace(String classPackage)
{
	String rtnSt="GME://notDefined";
	Iterator it=getPackageMap().keySet().iterator();
	while (it.hasNext())
	{
		String keyIt =(String)it.next();
		String valueIt=(String)this.getPackageMap().get(keyIt);
		if (valueIt!=null&&valueIt.equals(classPackage))
			rtnSt=keyIt;
	}
	return rtnSt;
}

private void buildSchemaClassMapping(Schema schema)
{
	//set package map
	Namespaces gmeNamespaces=schema.getNamespaces();
	Enumeration nsEnu=gmeNamespaces.getLocalNamespaces();
	while(nsEnu.hasMoreElements())
	{
		String nsURI=(String)nsEnu.nextElement();
		String pkName=XsdUtil.parsePackageNameFromURI(nsURI);
		packageMap.put(nsURI, pkName);
	}
	Enumeration clsEnu=schema.getElementDecls();
	while(clsEnu.hasMoreElements())
	{
		 ElementDecl clsElementDecl=(ElementDecl)clsEnu.nextElement();
		 buildObjectMetadata(clsElementDecl);
	}
}
private void buildObjectMetadata(ElementDecl clsDecl)
{
	ObjectMetadata objMeta=new ObjectMetadata();
	String clsType=clsDecl.getType().getName();
	objMeta.setName(clsType);
	String schemaTargetNs=clsDecl.getType().getSchema().getTargetNamespace();
	String pkName=(String)getPackageMap().get(schemaTargetNs);
	objMeta.setXPath(pkName+"."+clsDecl.getName());
	objMeta.setXmlPath(pkName+"."+clsDecl.getName());

	//set attribute mapping and association mapping
	ComplexType complexType =clsDecl.getType().getSchema().getComplexType(clsType);
	Enumeration contentEnu=complexType.enumerate();
	while(contentEnu.hasMoreElements())
	{
		 Particle particle=( Particle)contentEnu.nextElement();

		 //there is at least one group under each classMapping element
		 if (particle instanceof Group)
		 {
			 Group contentGroup=(Group)particle;
			 buildObjectContent(objMeta,contentGroup);
		 }
		 else if (particle instanceof ElementDecl)
		 {
			 ElementDecl contElm=(ElementDecl)particle;
			 System.out.println("XsdModelMetadata.buildObjectMetadata()..elemnt:"+contElm.getName());
			 if (contElm.getType().isSimpleType())
				 buildObjectElementAttribute(objMeta, contElm);
		 }
		 else
			 System.out.println("XsdModelMetadata.buildObjectMetadata()..:"+particle);
	}
	//process attributes
	Enumeration attrEnu=complexType.getAttributeDecls();
	while(attrEnu.hasMoreElements())
	{
		 AttributeDecl attrElm=( AttributeDecl)attrEnu.nextElement();
		buildObjectAttribute(objMeta, attrElm);
	}
	objectMap.put(objMeta.getXmlPath(), objMeta);
}
/**
 * XSD defines the attibute as as a XML element attribute of the parent object element
 * @param objMeta
 * @param attrElement
 */
private void buildObjectAttribute(ObjectMetadata objMeta,AttributeDecl attrElement )
{
	AttributeMetadata attr=new AttributeMetadata();
	attr.setName(attrElement.getName());
	attr.setDatatype(attrElement.getSimpleType().getName());
	attr.setXPath(objMeta.getXmlPath()+"."+attrElement.getName());
	attr.setXmlPath(objMeta.getXmlPath()+"."+attrElement.getName());
    attr.setChildTag(false);
    attributeMap.put(attr.getXPath(), attr);
	objMeta.addAttribute(attr);
}

/**
 * XSD defines the Attribute as a child element of the parent object element
 * @param objMeta
 * @param attrElement
 */
private void buildObjectElementAttribute(ObjectMetadata objMeta,ElementDecl attrElement )
{
	AttributeMetadata attr=new AttributeMetadata();
	attr.setName(attrElement.getName());
	attr.setDatatype(attrElement.getType().getName());
	attr.setXPath(objMeta.getXmlPath()+"."+attrElement.getName());
	attr.setXmlPath(objMeta.getXmlPath()+"."+attrElement.getName());
//	attr.setChildTag(true);
	attributeMap.put(attr.getXPath(), attr);
	objMeta.addAttribute(attr);
}

private void buildObjectContent(ObjectMetadata objMeta, Group complexContent)
{
	Enumeration contentEnu=complexContent.enumerate();
	System.out.println("XsdModelMetadata.buildObjectContent()..:"+objMeta.getName());
	while(contentEnu.hasMoreElements())
	{
		 Particle particle=( Particle)contentEnu.nextElement();
		 //there is at least one group under each classMapping element
		 if (particle instanceof ElementDecl)
		 {
			 ElementDecl elm=(ElementDecl)particle;
			 if (elm.getType().isSimpleType())
			 {
				 buildObjectElementAttribute(objMeta, elm);
			 }
			 else
			 {

				 ComplexType subElmType=(ComplexType)elm.getType();//.getSchema().getComplexType(elm.getType().getName());
//				 System.out.println("XsdModelMetadata.buildObjectContent()..:"+elm.getName()+"="+subElmType.getName());
				 buildAssociation(elm.getName(), objMeta,subElmType);
			 }
		 }
//		 else if (particle instanceof Group)
//		 {
//			 Group contentGroup=(Group)particle;
//		 }
	}

}

private void buildAssociation(String roleName, ObjectMetadata xsdObj, ComplexType asscType)
{
//	ComplexType
	Enumeration typeContentEnu=asscType.enumerate();
	while(typeContentEnu.hasMoreElements())
	{
		 Particle particle=( Particle)typeContentEnu.nextElement();
		 //there is at least one group under each classMapping element
		 if (particle instanceof Group)
		 {
			 Group asscGroup=(Group)particle;
			 Enumeration asscContent= asscGroup.enumerate();
			 while(asscContent.hasMoreElements())
			 {
				 Particle asscParticle=( Particle)asscContent.nextElement();
				 //there is at least one group under each classMapping element
				 if (asscParticle instanceof ElementDecl)
				 {
					 ElementDecl asscElm=(ElementDecl)asscParticle;
					 asscParticle.getMaxOccurs();

					 if(asscElm.getReference()!=null)
						 asscElm=asscElm.getReference();
					 buildAssociationWithCardinality(roleName, xsdObj, asscElm, asscParticle.getMaxOccurs(), asscParticle.getMinOccurs());
				 }
			 }
		 }
	}
}
private void buildAssociationWithCardinality(String roleName, ObjectMetadata xsdObj, ElementDecl asscElm, int maxOccurence, int minOccurence)
{
	AssociationMetadata asscMeta=new AssociationMetadata();
	asscMeta.setMultiplicity(1);
	asscMeta.setReciprocalMultiplity(1);
	asscMeta.setRoleName(roleName);
	asscMeta.setXPath(xsdObj.getXPath()+"."+roleName);
	asscMeta.setXmlPath(xsdObj.getXPath()+"."+roleName);

	String returnType=asscElm.getType().getName();
	String schemaTargetNs=asscElm.getType().getSchema().getTargetNamespace();
	String pkName=(String)getPackageMap().get(schemaTargetNs);

	asscMeta.setReturnTypeXPath(pkName+"."+returnType);

	if (maxOccurence==1&&minOccurence==1)
	{
		asscMeta.setManyToOne(true);
		asscMeta.setReciprocalMultiplity(-1);
	}
	else
		asscMeta.setMultiplicity(-1);

	associationMap.put(asscMeta.getXPath(), asscMeta);
	xsdObj.addAssociation(asscMeta);
}

private void loadSchemaSource(InputSource source)
{
    // -- get default parser from Configuration
    Parser parser = null;
    try {
        parser = _config.getParser();
    } catch (RuntimeException rte) {
        // ignore
    }

    if (parser == null) {
//        _dialog.notify("fatal error: unable to create SAX parser.");
        return;
    }

    SchemaUnmarshaller schemaUnmarshaller = null;
    try {
       schemaUnmarshaller = new SchemaUnmarshaller();
    } catch (XMLException e) {
        //--The default constructor cannot throw exception so this should never happen
        //--just log the exception
        e.printStackTrace();
        System.exit(1);
    }

    Sax2ComponentReader handler = new Sax2ComponentReader(schemaUnmarshaller);
    parser.setDocumentHandler(handler);
    parser.setErrorHandler(handler);

    try {
        parser.parse(source);
    } catch (java.io.IOException ioe) {
            ioe.printStackTrace();

    } catch (org.xml.sax.SAXException sx) {

        sx.printStackTrace();
        return;
    }

    Schema schema = schemaUnmarshaller.getSchema();

    try {
        schema.validate();
    } catch (ValidationException vx) {
         new NestedIOException(vx).printStackTrace();
    }
    gmeSchema=schema;
}

public String getProjectName() {
	if (projectName!=null&&!projectName.equals(""))
		return projectName;
	return "GME://projectName";
}

public void setProjectName(String projectName) {
	if (projectName!=null&&!projectName.equals(""))
		this.projectName=URLEncoder.encode(projectName);
//	this.projectName = projectName;
}

public HashMap getPackageMap() {
	return packageMap;
}

public TreeMap getObjectMap() {
	return objectMap;
}

public TreeMap<String, AssociationMetadata> getAssociationMap() {
	return associationMap;
}

public TreeMap<String, AttributeMetadata> getAttributeMap() {
	return attributeMap;
}

public Schema getGmeSchema() {
	return gmeSchema;
}

public String getProjectContext() {
	return projectContext;
}

public void setProjectContext(String projectContent) {
	this.projectContext = projectContent;
}

public String getProjectVersion() {
	return projectVersion;
}

public void setProjectVersion(String projectVersion) {
	this.projectVersion = projectVersion;
}

public String getProjectNamespace()
{
	String rtnSt="";
	try {
		rtnSt = "gme://"+URLEncoder.encode(getProjectName(),"UTF-8")+"."+getProjectContext()+"/"+getProjectVersion();
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return rtnSt.replace("%2B", "%20");
}
}
