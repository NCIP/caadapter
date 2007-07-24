/**
 * <!-- LICENSE_TEXT_START -->
  * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.transformation;


import gov.nih.nci.caadapter.common.csv.data.CSVField;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.csv.data.CSVSegmentedFile;
import gov.nih.nci.caadapter.common.function.FunctionConstant;
import gov.nih.nci.caadapter.common.function.FunctionException;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.function.meta.ParameterMeta;
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

/**
 * Processor of map files.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wuye $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.5 $
 *          date        $Date: 2007-07-24 14:38:05 $
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
    ValidatorResults theValidatorResults = null;
    int indent = -1;

    public List<XMLElement> process(Hashtable<String,String> mappings, Hashtable<String,FunctionComponent> functions, CSVSegmentedFile csvSegmentedFile, MIFClass mifClass) throws MappingException,FunctionException{
        // init class variables
        this.mappings = mappings;
        this.csvSegmentedFile = csvSegmentedFile; 
        this.mifClass = mifClass;
        this.functions = functions;
        MapProcessorHelper mapProcessorHelper = new MapProcessorHelper();

        mapProcessorHelper.preprocessMIF(mappings,functions, mifClass, false);
        
//        this.mapHelper = new MapProcessorHelper(mapping);
        
        this.resultsArray = new ArrayList<XMLElement>();

        // process one CSV source logical record at a time.
        List<CSVSegment> logicalRecords = csvSegmentedFile.getLogicalRecords();
        for (int i = 0; i < logicalRecords.size(); i++) {
        	csvSegmentHash = mapProcessorHelper.preprocessCSVSegments(logicalRecords.get(i));
        	List<XMLElement> xmlElements = processMIFclass(mifClass, logicalRecords.get(i));
        	for(XMLElement xmlElement:xmlElements) {
        		resultsArray.add(xmlElement);
        	}
        }

        return resultsArray;
    }


    
    private List<XMLElement> processMIFclass(MIFClass mifClass, CSVSegment pCsvSegment) throws MappingException,FunctionException {

    	List<XMLElement> xmlElements = new ArrayList<XMLElement>(); 

    	if (!mifClass.isMapped()) return NullXMLElement.NULL;

    	List<CSVSegment> csvSegments = findCSVSegment(pCsvSegment, mifClass.getCsvSegment());

    	for(CSVSegment csvSegment:csvSegments) {

    		XMLElement xmlElement = new XMLElement();
    		xmlElement.setName(mifClass.getName());

    		HashSet<MIFAttribute> attributes = mifClass.getAttributes();

    		for(MIFAttribute mifAttribute:attributes) {
    			System.out.println("attribute.name="+mifAttribute.getName());
    			if (!mifAttribute.isStrutural()) {
    				List<XMLElement> attrXmlElements = processAttribute(mifAttribute ,csvSegment);
    				if (attrXmlElements.size() != 0)
    					xmlElement.addChildren(attrXmlElements);
    			}
    		}

    		HashSet<MIFAssociation> associations = mifClass.getAssociations();

    		for(MIFAssociation mifAssociation : associations) {
    			List<XMLElement> assoXmlElements = processAssociation(mifAssociation ,csvSegment);
    			if (assoXmlElements.size() != 0)
    				xmlElement.addChildren(assoXmlElements);
    		}

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

    private List<XMLElement> processAssociation(MIFAssociation mifAssociation,  CSVSegment csvSegment) throws MappingException,FunctionException {
    	List<XMLElement> xmlElements = new ArrayList<XMLElement>();
    	MIFClass mifClass =null;
    	if (mifAssociation.getMifClass()!= null) {
    		mifClass =  mifAssociation.getMifClass();
    	}
    	
    	if (mifClass.getChoices().size() > 0) { //Handle choice
    		for(MIFClass choiceMIFClass:mifClass.getChoices()) {
    	    	if (mifClass.isChoiceSelected()) {
    	    		for(XMLElement xmlElement:processMIFclass(choiceMIFClass,csvSegment)) {
    	    			xmlElement.setName(mifAssociation.getName());
    	    			xmlElements.add(xmlElement);
    	    		}
    	    	}
    		}
    		
    	}
    	else {
    		if (mifClass ==null) {
    			System.out.println("mif is nul");
    			return xmlElements;
    		}
    		List<XMLElement> xmlEments = processMIFclass(mifClass,csvSegment);
    		for(XMLElement xmlElement:xmlEments) {
    			xmlElement.setName(mifAssociation.getName());
    			xmlElements.add(xmlElement);
    		}
    	}
    	return xmlElements;
    }

    
    private List<XMLElement> processAttribute(MIFAttribute mifAttribute, CSVSegment csvSegment) throws MappingException,FunctionException{
//    	System.out.println(" attribute name"+mifAttribute.getName()+"isMapped"+mifAttribute.isMapped());
    	if (mifAttribute.getDatatype() == null) return NullXMLElement.NULL; //Abstract attrbiute

    	if (!mifAttribute.isMapped()) {
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
    	
    	List<XMLElement> xmlElements = 	process_datatype(mifAttribute.getDatatype(), csvSegment, mifAttribute.getName(),mifAttribute.getParentXmlPath()+"."+mifAttribute.getName());
    	if (xmlElements.size() >0)
    		xmlElements.get(0).addAttribute("xsi:type", mifAttribute.getDatatype().getName());
    	return xmlElements;
    }

    private List<XMLElement> process_datatype(Datatype datatype, CSVSegment pCsvSegment, String attrName, String parentXPath) throws MappingException ,FunctionException{
//    	System.out.println("Process Datatype:"+datatype.getName()+" attribute name"+attrName);
    	
    	if (!datatype.isEnabled()) return NullXMLElement.NULL;
    	
    	if (datatype.getCsvSegments() == null)return NullXMLElement.NULL;
    	
    	if (datatype.getCsvSegments().size()==0) return NullXMLElement.NULL;
    	
    	List<XMLElement> resultList = new ArrayList<XMLElement>();


    	//If the input are from different siblings
    	if (datatype.getCsvSegments().size() > 1) {
//    		List<String> allListNames = new ArrayList<String>();
    		List<List<CSVSegment>> allListsCSVSegments = new ArrayList<List<CSVSegment>>();
    		
    		
    		for (String csvS:datatype.getCsvSegments()) {
//    			allListNames.add(csvS);
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
    		
    		//--------------
    		//--------------
    		boolean hasMore = true;
    		List<XMLElement> sibXMLElements = new ArrayList<XMLElement>();
    		while (hasMore) {
    			sibXMLElements.add(process_datatype_w_sibling(datatype, csvSegmentList, attrName, parentXPath));
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
    	return process_datatype_wo_sibling(datatype, pCsvSegment, attrName, parentXPath);
    }    	
    

    private List<XMLElement> process_datatype(Datatype datatype, List<CSVSegment> csvSegments, String attrName, String parentXPath) throws MappingException ,FunctionException {
//    	System.out.println("Process Datatype:"+datatype.getName()+" attribute name"+attrName);
    	
    	if (!datatype.isEnabled()) return NullXMLElement.NULL;
    	
    	if (datatype.getCsvSegments() == null)return NullXMLElement.NULL;
    	
    	if (datatype.getCsvSegments().size()==0) return NullXMLElement.NULL;
    	
    	List<XMLElement> resultList = new ArrayList<XMLElement>();

    	List<XMLElement> returnValue = new ArrayList<XMLElement>();
    	
    	returnValue.add(process_datatype_w_sibling(datatype, csvSegments, attrName, parentXPath));
   		return  returnValue;
    }
    	
    
    
    private List<XMLElement> process_datatype_wo_sibling(Datatype datatype, CSVSegment pCsvSegment, String attrName, String parentXPath) throws MappingException ,FunctionException{
    	
    	List<XMLElement> resultList = new ArrayList<XMLElement>();

    	String current = datatype.getCsvSegments().get(0);
    	List<CSVSegment> csvSegments = findCSVSegment(pCsvSegment, current);

    	for(CSVSegment csvSegment:csvSegments) {
			XMLElement xmlElement = new XMLElement();
			xmlElement.setName(attrName);
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
    				
//    				System.out.println("---------try"+parentXPath+"."+attributeName);
    				String scsXmlPath = mappings.get(parentXPath+"."+attributeName);
    				if (scsXmlPath==null) continue;
    				if (scsXmlPath.startsWith("function.")) { //function mapping to target
    					String datavalue = getFunctionValue(csvSegment,scsXmlPath,data);
    					xmlElement.addAttribute(attributeName, datavalue);
    				}
    				else { //direct mapping from source to target
//  					System.out.println(scsXmlPath);
    					if (data.get(scsXmlPath) == null) { //inverse relationship
    						CSVField csvField = findCSVField(pCsvSegment, scsXmlPath);
    						xmlElement.addAttribute(attributeName, csvField.getValue());
    					}
    					else {
    						xmlElement.addAttribute(attributeName, data.get(scsXmlPath));
    					}
    				}
    			}
    			else { //complexdatatype
    				List<XMLElement> attrsXMLElement = process_datatype(attr.getReferenceDatatype(), csvSegment,attr.getName(),parentXPath+"."+attr.getName());
    				xmlElement.addChildren(attrsXMLElement);
    			}
    		}
    		resultList.add(xmlElement);
    	}   	
    	return resultList;
    }

    
    
    
    private XMLElement process_datatype_w_sibling(Datatype datatype, List<CSVSegment> csvSegments, String attrName, String parentXPath) throws MappingException ,FunctionException{
    	
		XMLElement xmlElement = new XMLElement();
		xmlElement.setName(attrName);

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
						xmlElement.addAttribute(attributeName, csvField.getValue());
					}
					else {
						xmlElement.addAttribute(attributeName, data.get(scsXmlPath));
					}
				}
			}
			else { //complexdatatype
				List<XMLElement> attrsXMLElement = process_datatype(attr.getReferenceDatatype(), csvSegments,attr.getName(),parentXPath+"."+attr.getName());
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
    	
    	int pos = scsXmlPath.lastIndexOf(".output");
    	outputpos = Integer.valueOf(scsXmlPath.substring(pos+8, scsXmlPath.length()));
    	String fXmlPath = scsXmlPath.substring(0,pos);
        List<String> inputValues = new ArrayList<String>();
        System.out.println(fXmlPath + " -- " +  outputpos);
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

    		String inputData = mappings.get("function."+functionComponent.getId()+"."+"input"+"."+i);
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

/*
*TODO
*BIG to do is to determine the cardinalities, right now, we will generate everything, we could be smart if there is a vilation, we generate the first one only
*/