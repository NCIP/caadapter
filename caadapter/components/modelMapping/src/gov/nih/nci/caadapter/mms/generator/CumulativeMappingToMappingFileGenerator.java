/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.mms.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLGeneralization;
import gov.nih.nci.ncicb.xmiinout.domain.UMLModel;
import gov.nih.nci.ncicb.xmiinout.handler.HandlerEnum;
import gov.nih.nci.ncicb.xmiinout.handler.XmiException;
import gov.nih.nci.ncicb.xmiinout.handler.XmiHandlerFactory;
import gov.nih.nci.ncicb.xmiinout.handler.XmiInOutHandler;
import gov.nih.nci.ncicb.xmiinout.util.ModelUtil;
import gov.nih.nci.caadapter.mms.map.AttributeMapping;
import gov.nih.nci.caadapter.mms.map.CumulativeMapping;
import gov.nih.nci.caadapter.mms.map.DependencyMapping;
import gov.nih.nci.caadapter.mms.map.ManyToManyMapping;
import gov.nih.nci.caadapter.mms.map.SingleAssociationMapping;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.common.metadata.ColumnMetadata;
import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.common.metadata.TableMetadata;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
/**
 * @version 1.0
 * @created 11-Aug-2006 8:18:17 AM
 */
public class CumulativeMappingToMappingFileGenerator {

	private CumulativeMapping cumulativeMapping;
	private String xmiFileName;
	private Document doc = new Document();
	private UMLModel model;
	private ModelMetadata modelMetadata;
	private LinkedHashMap myMap = null;

	public CumulativeMappingToMappingFileGenerator(){
		try {
			cumulativeMapping = CumulativeMapping.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() {
	    try {
	    	modelMetadata = ModelMetadata.getInstance();
	    	if (modelMetadata == null ) {
	    		modelMetadata.setXmiFileName(xmiFileName);
	    		modelMetadata.createModel(xmiFileName);
	    	}
	    	myMap = modelMetadata.getModelMetadata();
	    	model = modelMetadata.getModel();
	    } catch (Exception e){
	      e.printStackTrace();
	    }
	  }
	/**
	 * This method constructs the xml content for the outputted mapping file.
	 */
	public void createLocalMappingFile(){
		init();
		Element mapping = new Element("mappings");
		mapping.setAttribute("type", "sdkintegration");
		Element components = new Element("components");
		Element sourceComponent = new Element("component");
		sourceComponent.setAttribute("location", this.xmiFileName);

		Element targetComponent = new Element("component");
		targetComponent.setAttribute("location", this.xmiFileName);
		components.addContent(sourceComponent);
		components.addContent(targetComponent);
		mapping.addContent(components);

		Iterator i = cumulativeMapping.getDependencyMappings().iterator();
		while (i.hasNext()) {
			try {

				DependencyMapping m = (DependencyMapping) i.next();
				Element link = new Element("link");
				link.setAttribute("type", "dependency");

				if (hasParent(m.getSourceDependency().getXPath())) {
					UMLClass clazz = getParent(m.getSourceDependency().getXPath());
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
		Iterator k = cumulativeMapping.getSingleAssociationMappings().iterator();
		while (k.hasNext()) {
			try {
				SingleAssociationMapping o = (SingleAssociationMapping) k.next();
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
		Iterator l = cumulativeMapping.getManyToManyMappings().iterator();
		while (l.hasNext()) {
			try {
				ManyToManyMapping p = (ManyToManyMapping) l.next();
				Element link = new Element("link");
				link.setAttribute("type", "manytomany");
				Element source = new Element("source");
				source.addContent(p.getAssociationEndMetadata().getXPath());
				link.addContent(source);
				Element target = new Element("target");
				target.addContent(p.getThisEndColumn().getXPath());
				link.addContent(target);
				mapping.addContent(link);

			} catch (Exception e) {
				 e.printStackTrace();
			}
			Iterator m = myMap.values().iterator();
			while (m.hasNext()) {
				try {
					Object p = m.next();
					if (p.getClass().getName().equals("TableMetadata")) {
						TableMetadata table = (TableMetadata)p;
						if (table.getType().equals("correlation")) {
							Element link = new Element("correlationTable");
							link.setAttribute("name", table.getName());
							link.setAttribute("path", table.getXPath());
							mapping.addContent(link);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	    doc.setRootElement(mapping);
	    XMLOutputter p = new XMLOutputter();
	    p.setFormat(Format.getPrettyFormat());


	}
	/**
	 * @return Document
	 */
	public Document getDocument() {
		return this.doc;

	}
	/**
	 * @return CumulativeMapping
	 */
	public CumulativeMapping getCummulativeMapping() {
		return cumulativeMapping;
	}

	/**
	 * @param cummulativeMapping
	 */
	public void setCummulativeMapping(CumulativeMapping cummulativeMapping) {
		this.cumulativeMapping = cummulativeMapping;
	}

	/**
	 * @return String xmiFileName
	 */
	public String getXmiFileName() {
		return xmiFileName;
	}

	/**
	 * @param xmiFileName
	 */
	public void setXmiFileName(String xmiFileName) {
		this.xmiFileName = xmiFileName;
	}

	/**
	 * @param pathToObject
	 * @return hasParent
	 */
	public boolean hasParent(String pathToObject){
		boolean hasParent = false;
	    UMLClass clazz = null;
	    if (ModelUtil.findClass(model,pathToObject)!= null){
	    	clazz = ModelUtil.findClass(this.model,pathToObject);
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
	public UMLClass getParent(String pathToObject){
	    UMLClass clazz = null;
	    UMLClass returnClass = null;
	    clazz = ModelUtil.findClass(this.model,pathToObject);
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
	public String getFullPath(UMLClass clazz){
		StringBuffer path = new StringBuffer();
		path.append(ModelUtil.getFullPackageName(clazz));
		path.append(".");
		path.append(clazz.getName());
		return path.toString();
	}

	public static void main(String[] args)
    {
        try
        {
        	 CumulativeMappingGenerator x = new CumulativeMappingGenerator("C:/sample.xmi");

             x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Gene","Logical View.Data Model.GENE" );
             x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Taxon","Logical View.Data Model.TAXON");
             x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Clone","Logical View.Data Model.CLONE");
             x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Chromosome","Logical View.Data Model.CHROMOSOME");
             x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Sequence","Logical View.Data Model.SEQUENCE");
         	 x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Target","Logical View.Data Model.TARGET");
         	 x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Library","Logical View.Data Model.LIBRARY");

         	 x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Taxon.id","Logical View.Data Model.TAXON.TAXON_ID");
         	 x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Clone.id","Logical View.Data Model.CLONE.CLONE_ID");
         	 x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Library.id","Logical View.Data Model.LIBRARY.LIBRARY_ID");
         	 x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Target.id","Logical View.Data Model.TARGET.TARGET_ID");
         	 x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Gene.id","Logical View.Data Model.GENE.GENE_ID");
         	 x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Sequence.id","Logical View.Data Model.SEQUENCE.SEQUENCE_ID");
         	 x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Chromosome.id","Logical View.Data Model.CHROMOSOME.CHROMOSOME_ID");

         	 x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Gene.chromosome","Logical View.Data Model.GENE.CHROMOSOME_ID");
         	 x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Chromosome.taxon","Logical View.Data Model.CHROMOSOME.TAXON_ID");
         	 x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Sequence.clone","Logical View.Data Model.SEQUENCE.CLONE_ID");
         	 x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Clone.library","Logical View.Data Model.CLONE.LIBRARY_ID");

         	 x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Gene.sequenceCollection","Logical View.Data Model.GENE_SEQUENCE.GENE_ID");
         	 x.map("Logical View.Logical Model.gov.nih.nci.cabio.domain.Sequence.geneCollection","Logical View.Data Model.GENE_SEQUENCE.SEQUENCE_ID");

         	 CumulativeMapping y = x.getCumulativeMapping();


        	 CumulativeMappingToMappingFileGenerator myGenerator = new CumulativeMappingToMappingFileGenerator();
        	 myGenerator.setXmiFileName("C:/sample.xmi");

        	//Creating mapping file

        	 myGenerator.createLocalMappingFile();
        	 XMLOutputter outp = new XMLOutputter();
     	     outp.setFormat(Format.getPrettyFormat());

     	     try {

     	    	File myFile = new File("C:/xyz.xml");
     	    	FileOutputStream myStream = new FileOutputStream(myFile);
     	    	outp.output(myGenerator.getDocument(), myStream);
     	     } catch (Exception e) {
     	    	e.printStackTrace();
     	     }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }
}
