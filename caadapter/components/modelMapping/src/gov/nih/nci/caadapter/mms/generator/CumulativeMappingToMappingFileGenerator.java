/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.mms.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLGeneralization;
import gov.nih.nci.ncicb.xmiinout.util.ModelUtil;
import gov.nih.nci.caadapter.mms.map.AttributeMapping;
import gov.nih.nci.caadapter.mms.map.CumulativeMapping;
import gov.nih.nci.caadapter.mms.map.DependencyMapping;
import gov.nih.nci.caadapter.mms.map.AssociationMapping;
import gov.nih.nci.caadapter.common.metadata.ModelMetadata;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
/**
 * Class for generating mapping file from cumulative mapping
 *
 * @author OWNER: Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     caAdatper v4.0
 * @version    $Revision: 1.8 $
 * @date       $Date: 2009-07-14 16:35:49 $
 * @created 11-Aug-2006 8:18:16 AM
 */
public class CumulativeMappingToMappingFileGenerator {

	/**
	 * This method constructs the xml content and write out mapping file.
	 */
	public static void writeMappingFile(File mappingFile, String xmiOutFile) throws Exception
	{
		ModelMetadata xmiMetada = CumulativeMappingGenerator.getInstance().getMetaModel();

		Element mapping = new Element("mappings");
		mapping.setAttribute("type", "sdkintegration");
		Element components = new Element("components");
		Element sourceComponent = new Element("component");
		sourceComponent.setAttribute("location", xmiMetada.getXmiFileName());
		
		Element targetComponent = new Element("component");
		targetComponent.setAttribute("location", xmiMetada.getXmiFileName());
		components.addContent(sourceComponent);
		components.addContent(targetComponent);
		mapping.addContent(components);
		
		CumulativeMapping cumulativeMapping =CumulativeMappingGenerator.getInstance().getCumulativeMapping();//CumulativeMapping.getInstance();

		Iterator i = cumulativeMapping.getDependencyMappings().iterator();
		while (i.hasNext()) {
			try {
				
				DependencyMapping m = (DependencyMapping) i.next();
				Element link = new Element("link");
				link.setAttribute("type", "dependency");
				
				if (hasParent(xmiMetada, m.getSourceDependency().getXPath())) {
					UMLClass clazz = getParent(xmiMetada, m.getSourceDependency().getXPath());
					link.setAttribute("parent",getFullPath(clazz));
				} else {
					link.setAttribute("parent", "null");
				}
				Element source = new Element("source");
				source.addContent(m.getSourceDependency().getXPath());
				link.addContent(source);
				Element target = new Element("target");
				target.addContent(m.getTargetDependency().getXPath());
				link.addContent(target);
				mapping.addContent(link);
			} catch (Exception e) {
				 e.printStackTrace();
			}
		}
		Iterator j = cumulativeMapping.getAttributeMappings().iterator();
		while (j.hasNext()) {
			try {
				AttributeMapping n = (AttributeMapping) j.next();
				Element link = new Element("link");
				link.setAttribute("type", "attribute");
				link.setAttribute("datatype", n.getAttributeMetadata().getDatatype());
				Element source = new Element("source");
				source.addContent(n.getAttributeMetadata().getXPath());
				link.addContent(source);
				Element target = new Element("target");
				target.addContent(n.getColumnMetadata().getXPath());
				link.addContent(target);
				mapping.addContent(link);
			} catch (Exception e) {
				 e.printStackTrace();
			}
		}
		Iterator k = cumulativeMapping.getAssociationMappings().iterator();
		while (k.hasNext()) {
			try {
				AssociationMapping o = (AssociationMapping) k.next();
				Element link = new Element("link");
				link.setAttribute("type", "association");
				Element source = new Element("source");
				source.addContent(o.getAssociationEndMetadata().getXPath());
				link.addContent(source);
				Element target = new Element("target");
				target.addContent(o.getColumnMetadata().getXPath());
				link.addContent(target);
				mapping.addContent(link);
			} catch (Exception e) {
				 e.printStackTrace();
			}
		}

		Document mappingDoc= new Document();
		mappingDoc.setRootElement(mapping);
	    XMLOutputter outp = new XMLOutputter();
 	    outp.setFormat(Format.getPrettyFormat());
    	FileOutputStream myStream = new FileOutputStream(mappingFile);
    	outp.output(mappingDoc, myStream);
    	myStream.close();
	}
	
	/**
	 * @param pathToObject
	 * @return hasParent
	 */
	private static boolean hasParent(ModelMetadata xmiMetadata, String pathToObject){
		boolean hasParent = false;
	    UMLClass clazz = null;
	    if (ModelUtil.findClass(xmiMetadata.getModel(),pathToObject)!= null){
	    	clazz = ModelUtil.findClass(xmiMetadata.getModel(),pathToObject);
	    	List<UMLGeneralization> generalizations = clazz.getGeneralizations();
	    	for (int i = 0; i < generalizations.size(); i++){
	    		if (generalizations.get(i).getSupertype()!= clazz) {
	    			clazz =(UMLClass) generalizations.get(i).getSupertype();
	    			hasParent = true;
	    		}
	    	}
	    }
		return hasParent;
	}
	
	/**
	 * @param pathToObject
	 * @return UMLClass
	 */
	private static UMLClass getParent(ModelMetadata xmiMetadata, String pathToObject){
	    UMLClass clazz = null;
	    UMLClass returnClass = null;
	    clazz = ModelUtil.findClass(xmiMetadata.getModel(),pathToObject);
	    UMLClass[] classes = ModelUtil.getSuperclasses(clazz);
	    if (classes.length != 0){
	    	returnClass = classes[0];
	    }
		return returnClass ;
	}
	
	/**
	 * @param clazz
	 * @return String fullPath
	 */
	private static String getFullPath(UMLClass clazz){
		StringBuffer path = new StringBuffer();
		path.append(ModelUtil.getFullPackageName(clazz));
		path.append(".");
		path.append(clazz.getName());
		return path.toString();
	}
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.7  2009/06/12 15:51:06  wangeug
 * HISTORY: clean code: caAdapter MMS 4.1.1
 * HISTORY:
 * HISTORY: Revision 1.6  2008/09/26 20:35:27  linc
 * HISTORY: Updated according to code standard.
 * HISTORY:
 */

