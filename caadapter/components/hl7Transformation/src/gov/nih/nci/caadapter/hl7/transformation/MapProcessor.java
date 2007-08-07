/**
 * <!-- LICENSE_TEXT_START -->
  * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.transformation;


import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.csv.data.CSVField;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.csv.data.CSVSegmentedFile;
import gov.nih.nci.caadapter.common.function.FunctionConstant;
import gov.nih.nci.caadapter.common.function.FunctionException;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.function.meta.ParameterMeta;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;
import gov.nih.nci.caadapter.hl7.map.FunctionVocabularyMapping;
import gov.nih.nci.caadapter.hl7.map.MappingException;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.transformation.data.NullXMLElement;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * The class will process the .map file an genearte HL7 v3 messages.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wuye $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.13 $
 *          date        $Date: 2007-08-07 03:19:21 $
 */

public class MapProcessor {

    // class variable from constructor
    private Hashtable<String,String> mappings = null;
    private Hashtable<String, FunctionComponent> functions = new Hashtable<String, FunctionComponent>();
    private CSVSegmentedFile csvSegmentedFile = null;
    private Hashtable <String, List<CSVField>> csvFieldHash= new Hashtable <String, List<CSVField>>();
    private Hashtable <String, List<CSVSegment>> csvSegmentHash= new Hashtable <String, List<CSVSegment>>();
    MIFClass mifClass;
    private MapProcessorHelper mapHelper = null;

    // Class variables used during processing.
    List<XMLElement> resultsArray = null;
    ValidatorResults theValidatorResults = new ValidatorResults();
    int indent = -1;

	/**
	 * This method will process the mapping file and generate a list of HL7 v3 message objects 
	 * 
	 * @param mapfilename the name of the mapping file
	 * @param csvfilename the name of the csv file
	 */
    public List<XMLElement> process(Hashtable<String,String> mappings, Hashtable<String,FunctionComponent> functions, CSVSegmentedFile csvSegmentedFile, MIFClass mifClass) throws MappingException,FunctionException{
        // init class variables
        this.mappings = mappings;
        this.csvSegmentedFile = csvSegmentedFile; 
        this.mifClass = mifClass;
        this.functions = functions;
        MapProcessorHelper mapProcessorHelper = new MapProcessorHelper();

        this.resultsArray = new ArrayList<XMLElement>();

        List<CSVSegment> logicalRecords = csvSegmentedFile.getLogicalRecords();

        if (logicalRecords.size()==0) 
        {
        	/**
        	 * TODO
        	 * Add a warning message
        	 */
        	return resultsArray;
        }
        
        mapProcessorHelper.preprocessMIF(mappings,functions, mifClass, false, logicalRecords.get(0).getName());
        

        // process one CSV source logical record at a time.
        for (int i = 0; i < logicalRecords.size(); i++) {
        	csvSegmentHash = mapProcessorHelper.preprocessCSVSegments(logicalRecords.get(i));
        	List<XMLElement> xmlElements = processRootMIFclass(mifClass, logicalRecords.get(i));
        	for(XMLElement xmlElement:xmlElements) {
        		resultsArray.add(xmlElement);
        	}
        }

        return resultsArray;
    }

	/**
	 * This method will process the root MIFClass object and generate a list of HL7 v3 message objects and
	 * populate valiation messages 
	 * 
	 * @param mifClass the MIFClass that will be processed
	 * @param pCsvSegment CSV segments that determines the root segments that dominate the cardinality
	 * 		  and data for all MIFAttributes and MIFAssociation of the MIFClass 
	 */
    
    private List<XMLElement> processRootMIFclass(MIFClass mifClass, CSVSegment pCsvSegment) throws MappingException,FunctionException {

    	List<XMLElement> xmlElements = new ArrayList<XMLElement>(); 

    	if (mifClass.getCsvSegments().size() == 0) return NullXMLElement.NULL;

    	List<CSVSegment> csvSegments = null;

    	//Step1: find all the csvSegments for attributes
    	if (mifClass.isMapped()) 
    	{
    		csvSegments = findCSVSegment(pCsvSegment, mifClass.getCsvSegment());
    	}
    	else {
    		csvSegments = new ArrayList<CSVSegment>();
    		csvSegments.add(pCsvSegment);
    	}

    	for(CSVSegment csvSegment:csvSegments) {
    		theValidatorResults.removeAll();
    	    ValidatorResults localValidatorResults = new ValidatorResults();
			List<XMLElement> xmlElementTemp = processMIFclass(mifClass,csvSegment);
    		if (theValidatorResults.getAllMessages().size() == 0) {
	            Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"HL7 v3 message is successfully generated!"});
	            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.INFO, msg));
    		}
    	    localValidatorResults.addValidatorResults(theValidatorResults);
    		//It should only return one element
    		if (xmlElementTemp.size()> 0) {
    			xmlElementTemp.get(0).setValidatorResults(localValidatorResults);
    		}
    		xmlElements.addAll(xmlElementTemp);
    	}
    	return xmlElements;
    }

    
	/**
	 * This method will process a MIFClass object and generate a list of HL7 v3 message objects 
	 * 
	 * @param mifClass the MIFClass that will be processed
	 * @param pCsvSegment CSV segments that determines the root segments that dominate the cardinality
	 * 		  and data for all MIFAttributes and MIFAssociation of the MIFClass 
	 */
    
    private List<XMLElement> processMIFclass(MIFClass mifClass, CSVSegment pCsvSegment) throws MappingException,FunctionException {

    	List<XMLElement> xmlElements = new ArrayList<XMLElement>(); 

    	if (mifClass.getCsvSegments().size() == 0) return NullXMLElement.NULL;

    	List<CSVSegment> csvSegments = null;

    	//Step1: find all the csvSegments for attributes
    	if (mifClass.isMapped()) 
    	{
    		csvSegments = findCSVSegment(pCsvSegment, mifClass.getCsvSegment());
    	}
    	else {
    		csvSegments = new ArrayList<CSVSegment>();
    		csvSegments.add(pCsvSegment);
    	}

    	for(CSVSegment csvSegment:csvSegments) {

    		XMLElement xmlElement = new XMLElement();
    		xmlElement.setName(mifClass.getName());

    		TreeSet<MIFAttribute> attributes = mifClass.getSortedAttributes();

    		//Step2: Process non-structural attributes 
    		//Non-structural attributes are child xmlelements vs structural attributes are attributes to xml elements
    		
    		for(MIFAttribute mifAttribute:attributes) {
//    			Log.logInfo(this,"Process attribute="+mifAttribute.getName()+" in mifclass "+mifClass.getName());
//    			System.out.println("Process attribute="+mifAttribute.getName()+" in mifclass "+mifClass.getName());
    			if (!mifAttribute.isStrutural()) {
    				List<XMLElement> attrXmlElements = processAttribute(mifAttribute ,csvSegment);
    				if (attrXmlElements.size() != 0)
    					xmlElement.addChildren(attrXmlElements);
    				if (mifAttribute.getMaximumMultiplicity() == 1) {
    					if (attrXmlElements.size()>1) {
    			            Message msg = MessageResources.getMessage("RIM4", new Object[]{mifAttribute.getXmlPath(),mifAttribute.getMinimumMultiplicity() + "..1", mifAttribute.getName(),attrXmlElements.size()});
    			            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
    					}
    				}
    			}
    		}

    		TreeSet<MIFAssociation> associations = mifClass.getSortedAssociations();

    		//Step 3: Process associations
    		for(MIFAssociation mifAssociation : associations) {
    			List<XMLElement> assoXmlElements = processAssociation(mifAssociation ,csvSegment);
    			if (assoXmlElements.size() != 0)
    				xmlElement.addChildren(assoXmlElements);
				if (mifAssociation.getMaximumMultiplicity() == 1) {
					if (assoXmlElements.size()>1) {
			            Message msg = MessageResources.getMessage("RIM5", new Object[]{mifAssociation.getXmlPath(),mifAssociation.getMinimumMultiplicity() + "..1", mifAssociation.getName(),assoXmlElements.size()});
			            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
					}
				}
    		}

    		//Step 4: Process structural attributes
    		/*
    		 * TODO: needs to conform with Wendy that structural attributes can not has mappings
    		 */
    		for(MIFAttribute mifAttribute:attributes) {
    			if (mifAttribute.isStrutural()) {
    				if (mifAttribute.getDefaultValue()!=null)
    					xmlElement.addAttribute(mifAttribute.getName(), mifAttribute.getDefaultValue());
    				if (mifAttribute.getFixedValue()!=null)
    					xmlElement.addAttribute(mifAttribute.getName(), mifAttribute.getFixedValue());
    			}
    		}
    		xmlElements.add(xmlElement);
    	}
    	return xmlElements;
    }

	/**
	 * This method will process a MIFAssociation object and generate a list of HL7 v3 message objects 
	 * 
	 * @param mifAssociation the MIFAssociation object that will be processed
	 * @param pCsvSegment CSV segments that determines the root segments that dominate the cardinality
	 * 		  and data for all MIFAttributes and MIFClass of the MIFAssociation 
	 */

    private List<XMLElement> processAssociation(MIFAssociation mifAssociation,  CSVSegment csvSegment) throws MappingException,FunctionException {
    	List<XMLElement> xmlElements = new ArrayList<XMLElement>();
    	MIFClass mifClass =null;
    	if (mifAssociation.getMifClass()!= null) {
    		mifClass =  mifAssociation.getMifClass();
    	}
    	//Scenario 1: process choices, and for each assoication, there can be multiple choices
    	if (mifClass.getChoices().size() > 0) { //Handle choice
    		for(MIFClass choiceMIFClass:mifClass.getChoices()) {
    	    	if (choiceMIFClass.isChoiceSelected()) {
    	    		for(XMLElement xmlElement:processMIFclass(choiceMIFClass,csvSegment)) {
    	    			xmlElement.setName(mifAssociation.getName());
    	    			xmlElements.add(xmlElement);
    	    		}
    	    	}
    		}
    		
    	}
    	//Scenario 2: process mifclass that associated with MIFAssociation
    	else {
    		// Pre-requsite one assoication must have a MIFClass object
    		// mifClass can not be null
    		if (mifClass == null) {
    			throw new MappingException("There is an error in your .h3s file, " + mifAssociation.getXmlPath() + " does not have specification", null);
    		}
    		List<XMLElement> xmlEments = processMIFclass(mifClass,csvSegment);
    		for(XMLElement xmlElement:xmlEments) {
    			xmlElement.setName(mifAssociation.getName());
    			xmlElements.add(xmlElement);
    		}
    	}
    	return xmlElements;
    }

	/**
	 * This method will process a MIFAttribute object and generate a list of HL7 v3 message objects 
	 * 
	 * @param mifAttribute the MIFAttribute object that will be processed
	 * @param pCsvSegment CSV segments that determines the root segments that dominate the cardinality
	 * 		  and data for all Datatypes of the MIFAttribute 
	 */
    
    private List<XMLElement> processAttribute(MIFAttribute mifAttribute, CSVSegment csvSegment) throws MappingException,FunctionException{
    	if (mifAttribute.getDatatype() == null) return NullXMLElement.NULL; //Abstract attrbiute
//    	System.out.println(mifAttribute.getXmlPath());
    	if (mifAttribute.getCsvSegments().size()== 0) {
    		if (mifAttribute.isMandatory()) {
    		  /*
    		   * TODO
    		   * ------generate default ----
    		   */	
    		}
    		else {
    			return NullXMLElement.NULL;
    		}
    	}
    	
    	List<XMLElement> xmlElements = 	process_datatype(mifAttribute.getDatatype(), csvSegment, mifAttribute.getParentXmlPath()+"."+mifAttribute.getNodeXmlName(),mifAttribute.getName());
    	for(XMLElement xmlElement:xmlElements)
    		xmlElement.addAttribute("xsi:type", mifAttribute.getDatatype().getName());
    	return xmlElements;
    }

	/**
	 * This method will process a datatype object and generate a list of HL7 v3 message objects 
	 * 
	 * @param datatype the Datatype that will be processed
	 * @param pCsvSegment CSV segments that determines the root segments that dominate the cardinality
	 * 		  and data for all child Datatypes
	 * @parentXPath parent xmlpath to the current datatype, will be used to determine the xmlpath for attributes of the datatype
	 * @xmlName the name that will used to determine the name of XMLElement 
	 */
    private List<XMLElement> process_datatype(Datatype datatype, CSVSegment pCsvSegment, String parentXPath, String xmlName) throws MappingException ,FunctionException{
    	
    	if (!datatype.isEnabled()) return NullXMLElement.NULL;
    	
    	if (datatype.getCsvSegments() == null)return NullXMLElement.NULL;
    	
    	if (datatype.getCsvSegments().size()==0) return NullXMLElement.NULL;
    	
    	List<XMLElement> resultList = new ArrayList<XMLElement>();


    	//Scenario 1: Inputs are from different siblings
    	if (datatype.getCsvSegments().size() > 1) {    		
    		List<List<CSVSegment>> allListsCSVSegments = new ArrayList<List<CSVSegment>>();
    		
    		
    		for (String csvS:datatype.getCsvSegments()) {
    			allListsCSVSegments.add(findCSVSegment(pCsvSegment, csvS));
    		}
    		
    		List <CSVSegment> csvSegmentList = new ArrayList<CSVSegment>();
    		List <Integer> csvSegmentIndex = new ArrayList<Integer>();
    		List <Integer> csvSegmentSum = new ArrayList<Integer>();
    		
    		int size = allListsCSVSegments.size();
    		for(int i=0;i<size;i++) {
    			csvSegmentList.add(allListsCSVSegments.get(i).get(0));
    			csvSegmentSum.add(allListsCSVSegments.get(i).size());
    			csvSegmentIndex.add(0);
    		}
    		
    		boolean hasMore = true;
    		List<XMLElement> sibXMLElements = new ArrayList<XMLElement>();
    		while (hasMore) {
    			sibXMLElements.add(process_datatype_w_sibling(datatype, csvSegmentList, parentXPath, xmlName));
    			hasMore = false;
    			for (int i=size-1;i>=0;i--) {
    				if (csvSegmentIndex.get(i) < csvSegmentSum.get(i)-1) {
    					hasMore = true;
    					int current = csvSegmentIndex.get(i);
    					csvSegmentIndex.set(i, current+1);
						csvSegmentList.set(i, allListsCSVSegments.get(i).get(current+1));
    					for (int j = i+1; j<size; j++) {
    						csvSegmentIndex.set(j, 0);
    						csvSegmentList.set(j, allListsCSVSegments.get(j).get(0));
    					}
    					break;
    				}
    			}
    		}
    		return sibXMLElements;
    	}
    	//Scenario 2: Inputs are from different the same branch
    	else {
        	return process_datatype_wo_sibling(datatype, pCsvSegment, parentXPath, xmlName);
    	}
    }    	
    

	/**
	 * This method will process a datatype object and generate a list of HL7 v3 message objects 
	 * 
	 * @param datatype the Datatype that will be processed
	 * @param csvSegments CSV segments that determines the root segments that dominate the cardinality
	 * 		  and data for all child Datatypes
	 * @parentXPath parent xmlpath to the current datatype, will be used to determine the xmlpath for attributes of the datatype
	 * @xmlName the name that will used to determine the name of XMLElement 
	 */
    private List<XMLElement> process_datatype(Datatype datatype, List<CSVSegment> csvSegments, String parentXPath, String xmlName) throws MappingException ,FunctionException {
//    	System.out.println("Process Datatype:"+datatype.getName()+" attribute name"+attrName);
    	
    	if (!datatype.isEnabled()) return NullXMLElement.NULL;
    	
    	if (datatype.getCsvSegments() == null)return NullXMLElement.NULL;
    	
    	if (datatype.getCsvSegments().size()==0) return NullXMLElement.NULL;
    	
    	List<XMLElement> resultList = new ArrayList<XMLElement>();

    	List<XMLElement> returnValue = new ArrayList<XMLElement>();
    	
    	returnValue.add(process_datatype_w_sibling(datatype, csvSegments, parentXPath, xmlName));
   		return  returnValue;
    }
    	
    
    
    private List<XMLElement> process_datatype_wo_sibling(Datatype datatype, CSVSegment pCsvSegment, String parentXPath, String xmlName) throws MappingException ,FunctionException{
    	
    	List<XMLElement> resultList = new ArrayList<XMLElement>();

    	String current = datatype.getCsvSegments().get(0);
    	List<CSVSegment> csvSegments = findCSVSegment(pCsvSegment, current);

    	for(CSVSegment csvSegment:csvSegments) {
			XMLElement xmlElement = new XMLElement();
			xmlElement.setName(xmlName);
    		List<CSVField> csvFields = csvSegment.getFields();
    		Hashtable <String, String> data = new Hashtable<String,String>();
    		for (CSVField csvField:csvFields) {
    			data.put(csvField.getXmlPath(),csvField.getValue());
    		}
    		Hashtable<String, Attribute> attrs = datatype.getAttributes();
    		for(String attributeName:(Set<String>)(attrs.keySet())) {
        		Attribute attr = attrs.get(attributeName);

//        		String test = parentXPath+"."+attr.getName();
//        		System.out.println(test);
        		
        		boolean isSimple = false;
        		
        		if (attr.getReferenceDatatype() == null) {
        			isSimple = true;
        		}
        		else {
        			if (attr.getReferenceDatatype().isSimple()) isSimple = true;
        		}
        		if (isSimple) {
    				
    				String scsXmlPath = mappings.get(parentXPath+"."+attributeName);
//    				System.out.println("---------try"+parentXPath+"."+attributeName + " actualdata:"+scsXmlPath);
    				if (scsXmlPath==null) continue;
    				if (scsXmlPath.startsWith("function.")) { //function mapping to target
    					String datavalue = getFunctionValue(csvSegment,scsXmlPath,data);
    					xmlElement.addAttribute(attributeName, datavalue);
    				}
    				else { //direct mapping from source to target
//  					System.out.println(scsXmlPath);
    					if (data.get(scsXmlPath) == null) { //inverse relationship
    						CSVField csvField = findCSVField(pCsvSegment, scsXmlPath);
    						if (csvField.getValue().equals("")) {
    							if (attr.getDefaultValue()!=null)
    								xmlElement.addAttribute(attributeName, attr.getDefaultValue());
    						}
    						else {
    							xmlElement.addAttribute(attributeName, csvField.getValue());
    						}
    					}
    					else {
    						if (data.get(scsXmlPath).equals("")) {
    							if (attr.getDefaultValue()!=null)
    								xmlElement.addAttribute(attributeName, attr.getDefaultValue());
    						}
    						else {
    							xmlElement.addAttribute(attributeName, data.get(scsXmlPath));
    						}
    					}
    				}
    			}
    			else { //complexdatatype
    				List<XMLElement> attrsXMLElement = process_datatype(attr.getReferenceDatatype(), csvSegment, parentXPath+"."+attr.getName(), attr.getName());
    				xmlElement.addChildren(attrsXMLElement);
    			}
    		}
    		resultList.add(xmlElement);
    	}   	
    	return resultList;
    }

    
    
    
    private XMLElement process_datatype_w_sibling(Datatype datatype, List<CSVSegment> csvSegments, String parentXPath, String xmlName) throws MappingException ,FunctionException{
    	
		XMLElement xmlElement = new XMLElement();
		xmlElement.setName(xmlName);

		Hashtable <String, String> data = new Hashtable<String,String>();
    	for(CSVSegment csvSegment:csvSegments) {
    		List<CSVField> csvFields = csvSegment.getFields();
    		for (CSVField csvField:csvFields) {
    			data.put(csvField.getXmlPath(),csvField.getValue());
    		}
    	}
		
		Hashtable<String, Attribute> attrs = datatype.getAttributes();
		for(String attributeName:(Set<String>)(attrs.keySet())) {
			Attribute attr = attrs.get(attributeName);

//			String test = parentXPath+"."+attr.getName();
//			System.out.println(test);

			boolean isSimple = false;

			if (attr.getReferenceDatatype() == null) {
				isSimple = true;
			}
			else {
				if (attr.getReferenceDatatype().isSimple()) isSimple = true;
			}
			if (isSimple) {

//				System.out.println("---------try"+parentXPath+"."+attributeName);
				String scsXmlPath = mappings.get(parentXPath+"."+attributeName);
				if (scsXmlPath==null) continue;
				if (scsXmlPath.startsWith("function.")) { //function mapping to target
					String datavalue = getFunctionValue(csvSegments,scsXmlPath,data);
					xmlElement.addAttribute(attributeName, datavalue);
				}
				else { //direct mapping from source to target
//					System.out.println(scsXmlPath);
					if (data.get(scsXmlPath) == null) { //inverse relationship
						CSVField csvField = findCSVField(csvSegments, scsXmlPath);
						if (csvField.getValue().equals("")) {
							if (attr.getDefaultValue()!=null)
								xmlElement.addAttribute(attributeName, attr.getDefaultValue());
						}
						else {
							xmlElement.addAttribute(attributeName, csvField.getValue());
						}
					}
					else {
						if (data.get(scsXmlPath).equals("")) {
							if (attr.getDefaultValue()!=null)
								xmlElement.addAttribute(attributeName, attr.getDefaultValue());
						}
						else {
							xmlElement.addAttribute(attributeName, data.get(scsXmlPath));
						}
					}
				}
			}
			else { //complexdatatype
				List<XMLElement> attrsXMLElement = process_datatype(attr.getReferenceDatatype(), csvSegments, parentXPath+"."+attr.getName(), attr.getName());
				xmlElement.addChildren(attrsXMLElement);
			}
		}
		return xmlElement;
    }
  
    
    
    
    private List<CSVSegment> findCSVSegment(CSVSegment csvSegment, String targetXmlPath) {
//    	System.out.println("CSVSegment "+csvSegment.getXmlPath() + "-->target"+targetXmlPath);
    	List<CSVSegment> csvSegments = new ArrayList<CSVSegment>();
    	if (csvSegment.getXmlPath().equals(targetXmlPath)) {
    		csvSegments.add(csvSegment);
    		return csvSegments;
    	}
    	if (csvSegment.getXmlPath().contains(targetXmlPath)) {
    		CSVSegment current = csvSegment.getParentSegment();
    		while (true) {
    			if (current.getXmlPath().equals(targetXmlPath)) {
    	    		csvSegments.add(current);
    	    		return csvSegments;
    			}
    			current = current.getParentSegment();
    			if (current == null) {
    				System.out.println("Error");
    				break;
    				/*
    				 * TODO throw error
    				 */
    			}
    		}
    	}
    	
    	if (targetXmlPath.contains(csvSegment.getXmlPath())) {
			CSVSegment current = csvSegment;
    		while (true) {
    			boolean canStop = false;
    			if (current.getChildSegments() == null || current.getChildSegments().size()==0) break;
    			for(CSVSegment childSegment:current.getChildSegments()) {
//    				System.out.println("ChildSegment" + childSegment.getXmlPath());
    				if (childSegment.getXmlPath().equals(targetXmlPath)) {
    					csvSegments.add(childSegment);
    					canStop = true;
    				}
    				else {
    					if (targetXmlPath.contains(childSegment.getXmlPath())) {
    						current = childSegment;
    						break;
    					}
    				}
    			}
				if (canStop) break;
    		}
    	}
    	return csvSegments;
    }

    private CSVField findCSVField(CSVSegment csvSegment, String targetXmlPath) {
    	String targetSegmentXmlPath = targetXmlPath.substring(0,targetXmlPath.lastIndexOf('.'));
    	CSVSegment current = csvSegment.getParentSegment();
    	while (true) {
    		if (current.getXmlPath().equals(targetSegmentXmlPath)) {
    			for(CSVField csvField:current.getFields()) {
    				if (csvField.getXmlPath().equals(targetXmlPath)) return csvField;
    			}
    		}
    		current = current.getParentSegment();
    		if (current == null) {
    			System.out.println("Error");
    	    	return null;
    			/*
    			 * TODO throw error
    			 */
    		}
    	}
    }

    private CSVField findCSVField(List<CSVSegment> csvSegments, String targetXmlPath) {
    	
    	String targetSegmentXmlPath = targetXmlPath.substring(0,targetXmlPath.lastIndexOf('.'));
    	
    	for (CSVSegment csvSegment: csvSegments) {
        	CSVSegment current = csvSegment.getParentSegment();
    		while (true) {
    			if (current.getXmlPath().equals(targetSegmentXmlPath)) {
    				for(CSVField csvField:current.getFields()) {
    					if (csvField.getXmlPath().equals(targetXmlPath)) return csvField;
    				}
    			}
    			current = current.getParentSegment();
    			if (current == null) {
    				break;
    			}
    		}
    	}
    	
    	return null;
    	// Error should be thrown
    }

    public String getFunctionValue(CSVSegment pCsvSegment, String scsXmlPath, Hashtable<String, String> data) throws MappingException ,FunctionException{
    	List<CSVSegment> csvSegments = new ArrayList<CSVSegment>();
    	csvSegments.add(pCsvSegment);
    	return getFunctionValue(csvSegments,scsXmlPath, data);
    }

    public String getFunctionValue(List<CSVSegment> csvSegments, String scsXmlPath, Hashtable<String, String> data) throws MappingException ,FunctionException{
    	int outputpos = 0;
    	
    	int pos = scsXmlPath.lastIndexOf(".outputs.");
    	outputpos = Integer.valueOf(scsXmlPath.substring(pos+9, scsXmlPath.length()));
    	String fXmlPath = scsXmlPath.substring(0,pos);
        List<String> inputValues = new ArrayList<String>();
//        System.out.println(fXmlPath + " -- " +  outputpos);
        FunctionComponent functionComponent = functions.get(fXmlPath);
    	if (functionComponent == null)
    		throw new MappingException("count not find function: " + scsXmlPath, null);

    	FunctionMeta functionMeta = functionComponent.getMeta();
    	
    	// first, is the function a constant?
    	if (functionMeta.isConstantFunction()) {
    		FunctionConstant fc = functionComponent.getFunctionConstant();
    		if (fc == null)
    			throw new MappingException("count not find function constant for function: " + functionComponent.getId(), null);
    		return fc.getValue();
    	}

    	List<ParameterMeta> inputParameterMetas = functionMeta.getInputDefinitionList();

    	// for each input find it's input value.
    	for (int i = 0; i < functionMeta.getInputDefinitionList().size(); i++) {
    		ParameterMeta parameterMeta = inputParameterMetas.get(i);
    		String inputvalue = null;
//    		// find maps to these inputs.
//    		List<Map> maps = findMapsAssociatedWithMetaObject(functionComponent.getUUID(), parameterMeta.getUUID(), Config.MAP_COMPONENT_TARGET_TYPE);
//    		// throw excpetions if needed.
//    		if (maps.size() > 1) throw new MappingException("Function must have ONLY one map. " + maps.size() + " found. : " + parameterMeta, null);
//    		if (maps.size() < 1) throw new MappingException("Function must have one map. Zero found. : " + parameterMeta, null);

    		String inputData = mappings.get("function."+functionComponent.getId()+"."+"inputs"+"."+i);
    		if (inputData.startsWith("function.")) { //function mapping to target
    			inputvalue = getFunctionValue(csvSegments,inputData, data);
    		}
    		else { //direct mapping from source to target
    			if (data.get(inputData) == null) { //inverse relationship
    				CSVField csvField = findCSVField(csvSegments, inputData);
    				inputvalue = csvField.getValue();
    			}
    			else {
    				inputvalue = data.get(inputData);
    			}
    		}
    		inputValues.add(inputvalue);
    	}

    	// , is the FunctionVocabularyMapping?
    	if (functionMeta.isFunctionVocabularyMapping())
    	{
    		FunctionVocabularyMapping vm = functionComponent.getFunctionVocabularyMapping();
    		if (vm == null)
    			throw new MappingException("Not found 'Vocabulary' function: " + functionComponent.getId(), null);
    		if (inputValues.size() != 1)
    			throw new MappingException("Parameter count of 'Vocabulary' fuction must be 1 but now is "+inputValues.size()+". : " + functionComponent.getId(), null);

    		String res = "";
    		try
    		{
    			if (functionMeta.getFunctionName().equalsIgnoreCase(vm.getMethodNamePossibleList()[0]))
    				res = vm.translateValue(inputValues.get(0));
    			else if (functionMeta.getFunctionName().equalsIgnoreCase(vm.getMethodNamePossibleList()[1]))
    				res = vm.translateInverseValue(inputValues.get(0));
    			else throw new MappingException("'"+ functionMeta.getFunctionName() +"' function could not be found in 'Vocabulary' function group : " + functionComponent.getId(), null);
    		}
    		catch(FunctionException fe)
    		{
    			throw new MappingException(fe.getMessage(), fe, fe.getSeverity());
    		}
    		return res;
    	}

    	// pass the input values to the function.compute() function.
    	Object[] valueArray = null;
    	String theValue = null;
    	valueArray = functionMeta.compute(inputValues.toArray());
    	theValue = valueArray[outputpos].toString();

    	return theValue;
    }
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.12  2007/08/03 23:02:48  wuye
 * HISTORY      : Fixed the choice problem.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2007/08/03 13:25:32  wuye
 * HISTORY      : Fixed the mapping scenario #1 bug according to the design document
 * HISTORY      :
 * HISTORY      : Revision 1.10  2007/08/01 14:14:46  wuye
 * HISTORY      : Added missing value handling
 * HISTORY      :
 * HISTORY      : Revision 1.9  2007/07/31 20:03:19  wuye
 * HISTORY      : Fixed validationResult error
 * HISTORY      :
 * HISTORY      : Revision 1.8  2007/07/31 15:15:25  wuye
 * HISTORY      : Added INFO message
 * HISTORY      :
 * HISTORY      : Revision 1.7  2007/07/31 14:04:31  wuye
 * HISTORY      : Add Comments
 * HISTORY      :
 */