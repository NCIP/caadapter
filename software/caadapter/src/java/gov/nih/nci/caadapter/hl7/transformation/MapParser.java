/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.hl7.transformation;

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.function.FunctionConstant;
import gov.nih.nci.caadapter.common.function.FunctionManager;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;
import gov.nih.nci.caadapter.hl7.map.FunctionVocabularyMapping;
import gov.nih.nci.caadapter.hl7.map.MappingException;

import java.io.File;
import java.util.Hashtable;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Parse csv to HL7 v3 .
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: altturbo $
 * @version $Revision: 1.14 $
 * @date $Date: 2009-07-19 07:10:40 $
 * @since caAdapter v4.0
 */

public class MapParser {
	String sourceSpecFileName ="";
	String h3sFilename ="";
	String sourceKind="";

    File sourceMapFile = null;
    ValidatorResults theValidatorResults = new ValidatorResults();
	Hashtable <String, FunctionComponent> functions = new Hashtable<String, FunctionComponent>();
	Hashtable <String, String>mappings = new Hashtable<String, String>();

	/**
	 * @return the mappings
	 */
	public Hashtable getMappings() {
		return mappings;
	}
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

        sourceMapFile = new File(file.getAbsolutePath());

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
		    		sourceSpecFileName = (component.getAttribute("location")==null?"":component.getAttribute("location").getValue());
		    		String cmptType=component.getAttributeValue("kind");
		    		sourceKind=cmptType;
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
                boolean isInverse = false;
                if(vocabularyMapping.getMethodNamePossibleList()[1].equalsIgnoreCase(functionMeta.getFunctionName())) isInverse = true;
                else if(vocabularyMapping.getMethodNamePossibleList()[0].equalsIgnoreCase(functionMeta.getFunctionName())) isInverse = false;
                else throw new MappingException("Invalid method name of 'Vocabulary' function group", new Throwable());

                vocabularyMapping = new FunctionVocabularyMapping(datatype, datavalue, isInverse, sourceMapFile.getParentFile());

                functionComponent.setFunctionVocabularyMapping(vocabularyMapping);
            }

            // set it in the lookup table.
//            metaLookupTable.put(cComponent.getId(), new FunctionMetaLookup(functionMeta));
//            componentLookupTable.put(cComponent.getId(), functionComponent);
            functions.put("function."+id, functionComponent);
            //System.out.println(" ***** function. (1) : "+id);
        } catch (Exception e) {
            //System.out.println(" ***** function. (2) : "+id);
            e.printStackTrace();
            throw new MappingException(e.getClass().getCanonicalName()+" : " + e.getMessage(), e);
        }
    }

	public String getSourceSpecFileName() {
		return sourceSpecFileName;
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
	/**
	 * @return the v2Version
	 */
	public String getSourceKind() {
		return sourceKind;
	}

}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.13  2009/03/12 01:41:30  umkis
 * HISTORY :upgrade for flexibility of vom file location (same directory with the map file)
 * HISTORY :
 * HISTORY :Revision 1.12  2008/11/21 16:19:36  wangeug
 * HISTORY :Move back to HL7 module from common module
 * HISTORY :
 * HISTORY :Revision 1.11  2008/11/17 20:10:07  wangeug
 * HISTORY :Move FunctionComponent and VocabularyMap from HL7 module to common module
 * HISTORY :
 * HISTORY :Revision 1.10  2008/10/29 19:08:20  wangeug
 * HISTORY :make mapping hashtable accessible publicly
 * HISTORY :
 * HISTORY :Revision 1.9  2008/10/24 19:36:58  wangeug
 * HISTORY :transfer a v2 message into v3 message using SUN v2 schema
 * HISTORY :
 * HISTORY :Revision 1.8  2008/09/29 15:40:38  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */