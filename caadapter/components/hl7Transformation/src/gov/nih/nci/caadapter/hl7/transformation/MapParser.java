/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.hl7.transformation;

import gov.nih.nci.caadapter.castor.map.impl.C_component;
import gov.nih.nci.caadapter.castor.map.impl.C_data;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.function.FunctionConstant;
import gov.nih.nci.caadapter.common.function.FunctionManager;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.map.BaseComponent;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;
import gov.nih.nci.caadapter.hl7.map.FunctionVocabularyMapping;
import gov.nih.nci.caadapter.hl7.map.MappingException;

import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Parse csv to HL7 v3 .
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: phadkes $
 * @version $Revision: 1.7 $
 * @date $Date: 2008-06-09 19:53:50 $
 * @since caAdapter v4.0
 */

public class MapParser {
	String scsFilename ="";
	String h3sFilename ="";
	ValidatorResults theValidatorResults = new ValidatorResults();
	Hashtable <String, FunctionComponent> functions = new Hashtable<String, FunctionComponent>();
	Hashtable mappings = new Hashtable();

	public Hashtable processOpenMapFile(File file) throws Exception
	{
		/*
		 * TODO
		 * Add validation for function link
		 */
		boolean status = false;
	    SAXBuilder builder = new SAXBuilder(false);
	    Document document = builder.build(new File(file.getAbsolutePath()));
	    Element root = document.getRootElement();

	    if (!root.getName().equalsIgnoreCase("mapping")) {
            Message msg = MessageResources.getMessage("MAP10", new Object[]{"SCS and H3S"});
            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
            return null;
	    }
	    Element components = root.getChild("components");
	    if (components == null) {
            Message msg = MessageResources.getMessage("MAP10", new Object[]{"SCS and H3S"});
            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
            return null;
        }else {
	    	List<Element> componentList = components.getChildren("component");
		    if (componentList == null) {
	            Message msg = MessageResources.getMessage("MAP10", new Object[]{"SCS and H3S"});
	            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
	            return null;
		    }
		    for(int i = 0; i< componentList.size();i++)  {
		    	Element component = componentList.get(i);
                //System.out.println("  &&&& : " + component.getAttribute("type"));
                if (component.getAttribute("type")==null)
		    	{
		    		Message msg = MessageResources.getMessage("MAP10", new Object[]{"Component type is wrong"});
		            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
		            continue;
		    	}

		    	if (component.getAttribute("type").getValue().equalsIgnoreCase("source")) {
		    		scsFilename = (component.getAttribute("location")==null?"":component.getAttribute("location").getValue());
		    	}
		    	if (component.getAttribute("type").getValue().equalsIgnoreCase("target")) {
		    		h3sFilename = (component.getAttribute("location")==null?"":component.getAttribute("location").getValue());
		    	}
		    	if (component.getAttribute("type").getValue().equalsIgnoreCase("function")) {

                    if (component.getAttribute("name")!=null) {
		    			String kind = null;
		    			if (component.getAttribute("kind")!=null)
		    				kind = component.getAttribute("kind").getValue();
		    			String group =null;
		    			if (component.getAttribute("group")!=null)
		    				group = component.getAttribute("group").getValue();
		    			String name =null;
		    			if (component.getAttribute("name")!=null)
		    				name = component.getAttribute("name").getValue();
		    			String id =null;
		    			if (component.getAttribute("id")!=null)
		    				id = component.getAttribute("id").getValue();

		    			if (kind == null || group == null || name==null) {
		    	            Message msg = MessageResources.getMessage("MAP16", new Object[]{"Invalid value for function component in the .map file"});
		    	            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));
		    	            continue;
		    			}

		    			String datatype = null;
		    			String datavalue = null;
		    	       	Element data = component.getChild("data");
		    	       	if (data!=null) {
		    	       		datatype = data.getAttribute("type").getValue();
		    	       		datavalue = data.getAttribute("value").getValue();
			    			if (datatype == null || datavalue == null) {
			    	            Message msg = MessageResources.getMessage("MAP16", new Object[]{"Invalid value for function component in the .map file"});
			    	            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));
			    	            continue;
			    			}
		    	       	}
		    	       	generateFunctionComponent(kind, group, name, datatype, datavalue, id);		    		}
		    	}
		    }
	    }


	    Element links = root.getChild("links");
	    if (links == null) {
            Message msg = MessageResources.getMessage("MAP11", new Object[]{file.getAbsolutePath()});
            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
            return null;
        }

       	List<Element> linkElements = links.getChildren("link");
	    for(int i=0; i<linkElements.size();i++) {
	    	String sourceXPath="";
	    	String targetXPath="";
	        Element link = (Element) linkElements.get(i);
	        Element sourceElementP = link.getChild("source");
	        if (sourceElementP == null) {
	            Message msg = MessageResources.getMessage("MAP18", new Object[]{});
	            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));
	            continue;
	        }
	        Element sourceElement = sourceElementP.getChild("linkpointer");
	        if (sourceElement == null) {
	            Message msg = MessageResources.getMessage("MAP18", new Object[]{});
	            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));
	            continue;
	        }
	        if (sourceElement.getAttribute("kind") == null) {
	        	continue;
	        }

	        if (sourceElement.getAttribute("xmlPath") == null) {
	        	continue;
	        }
	        sourceXPath = sourceElement.getAttribute("xmlPath").getValue();
	        Element targetElementP = link.getChild("target");
	        if (targetElementP == null) {
	            Message msg = MessageResources.getMessage("MAP19", new Object[]{});
	            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));
	            continue;
	        }
	        Element targetElement = targetElementP.getChild("linkpointer");
	        if (targetElement == null) {
	            Message msg = MessageResources.getMessage("MAP19", new Object[]{});
	            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));
	            continue;
	        }
	        if (targetElement.getAttribute("xmlPath") == null) {
	        	continue;
	        }

	        targetXPath = targetElement.getAttribute("xmlPath").getValue();
	        if (targetElement.getAttribute("kind") == null) {
	        	continue;
	        }
	        String targetKind ="";
	        targetKind = targetElement.getAttribute("kind").getValue();
	        if (targetKind.equalsIgnoreCase("h3s")) {

	        }else {
	        	if (targetKind.equalsIgnoreCase("function")) {

	        	}
	        	else {

	        	}
	        }

	        mappings.put(targetXPath, sourceXPath);
	    }
		return mappings;
	}
    /**
     * A method to assist with processComponents()
     */
    private void generateFunctionComponent(String kind, String group, String name, String datatype, String datavalue, String id) throws MappingException {
        FunctionComponent functionComponent =FunctionComponent.getFunctionComponent();// new FunctionComponent();
        if (!id.equals(""))
        	functionComponent.setId(id);
        functionComponent.setType("function");
        System.out.println("MapParser.generateFunctionComponent()...functionID:"+functionComponent.getId());
		functionComponent.setKind(kind);
		try {
            FunctionManager f = FunctionManager.getInstance();
            FunctionMeta functionMeta = f.getFunctionMeta(kind, group, name);
            functionComponent.setMeta(functionMeta);

            if ("constant".equalsIgnoreCase(functionMeta.getFunctionName()))
            {
            	if (datatype == null || datavalue== null) {
    	            Message msg = MessageResources.getMessage("MAP16", new Object[]{"Invalid value for function component in the .map file"});
    	            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));
    	            return;
            	}
                FunctionConstant functionConstant = new FunctionConstant("constant", datatype, datavalue);
                functionComponent.setFunctionConstant(functionConstant);
            }
            if("vocabulary".equalsIgnoreCase(functionMeta.getGroupName()))
            {
            	if (datatype == null || datavalue== null) {
    	            Message msg = MessageResources.getMessage("MAP16", new Object[]{"Invalid value for function component in the .map file"});
    	            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));
    	            return;
            	}
                FunctionVocabularyMapping vocabularyMapping = new FunctionVocabularyMapping();
                if(vocabularyMapping.getMethodNamePossibleList()[1].equalsIgnoreCase(functionMeta.getFunctionName()))
                    vocabularyMapping = new FunctionVocabularyMapping(datatype, datavalue, true);
                else if(vocabularyMapping.getMethodNamePossibleList()[0].equalsIgnoreCase(functionMeta.getFunctionName()))
                    vocabularyMapping = new FunctionVocabularyMapping(datatype, datavalue, false);
                else throw new MappingException("Invalid method name of 'Vocabulary' function group", new Throwable());
                functionComponent.setFunctionVocabularyMapping(vocabularyMapping);
            }

            // set it in the lookup table.
//            metaLookupTable.put(cComponent.getId(), new FunctionMetaLookup(functionMeta));
//            componentLookupTable.put(cComponent.getId(), functionComponent);
            functions.put("function."+id, functionComponent);
            //System.out.println(" ***** function. (1) : "+id);
        } catch (Exception e) {
            //System.out.println(" ***** function. (2) : "+id);
            throw new MappingException(e.getMessage(), e);
        }
    }

	public String getSCSFilename() {
		return scsFilename;
	}
	public String getH3SFilename() {
		return h3sFilename;
	}
	public ValidatorResults getValidatorResults() {
		return theValidatorResults;
	}
	public Hashtable getFunctions() {
		return functions;
	}
}
