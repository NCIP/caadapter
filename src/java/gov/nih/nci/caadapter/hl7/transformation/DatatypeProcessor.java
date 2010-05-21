/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
 
package gov.nih.nci.caadapter.hl7.transformation;

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.util.HTML_SpecialCharacterTable;
import gov.nih.nci.caadapter.common.csv.data.CSVField;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.function.FunctionConstant;
import gov.nih.nci.caadapter.common.function.FunctionException;
import gov.nih.nci.caadapter.common.function.FunctionDataSpecExe;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;
import gov.nih.nci.caadapter.hl7.map.FunctionVocabularyMapping;
import gov.nih.nci.caadapter.hl7.map.MappingException;
import gov.nih.nci.caadapter.hl7.transformation.data.HL7XMLUtil;
import gov.nih.nci.caadapter.hl7.transformation.data.MutableFlag;
import gov.nih.nci.caadapter.hl7.transformation.data.NullXMLElement;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;
import gov.nih.nci.caadapter.castor.function.impl.C_dataSpec;
import gov.nih.nci.caadapter.castor.function.impl.types.OutDataType;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * This class defines functions to process a datatyp object 
 * 
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.12 $
 *          date        $Date: 2009-01-14 21:02:43 $
 */
public class DatatypeProcessor {
	/**
	 * This method will generate a default of HL7 v3 message objects 
	 * 
	 * @param datatype the Datatype that will be processed
	 * @parentXPath parent xmlpath to the current datatype, will be used to determine the xmlpath for attributes of the datatype
	 * @xmlName the name that will used to determine the name of XMLElement 
	 */
	MapProcssorCSVUtil csvUtil = new MapProcssorCSVUtil();
    private Hashtable<String, FunctionComponent> functions = new Hashtable<String, FunctionComponent>();
    private Hashtable<String,String> mappings = null;

    public void setEnv(MapProcssorCSVUtil csvUtilPassed, Hashtable<String, FunctionComponent> functions, Hashtable<String,String> mappings) {
    	this.csvUtil = csvUtilPassed;
    	this.functions = functions;
    	this.mappings = mappings;
    }
    
    public XMLElement process_default_datatype(Datatype datatype,  String parentXPath, String xmlName) throws MappingException ,FunctionException{

    	XMLElement xmlElement = new XMLElement();
    	xmlElement.setName(xmlName);
    	Hashtable<String, Attribute> attrs = datatype.getAttributes();
    	for(String attributeName:(Set<String>)(attrs.keySet())) {
    		Attribute attr = attrs.get(attributeName);
    		if (!attr.isEnabled()) continue;

    		boolean isSimple = false;

    		if (attr.getReferenceDatatype() == null) {
    			isSimple = true;
    		}
    		else {
    			if (attr.getReferenceDatatype().isSimple()) isSimple = true;
    		}
    		if (isSimple) {
    			if (attr.getDefaultValue()!= null)
    			{
    				if (!attr.getDefaultValue().equals(""))
    				{
    					xmlElement.addAttribute(attributeName, attr.getDefaultValue(), attr.getType(), null,null);
    				}
    			}
    		}
    		else { //complexdatatype
	    	    MutableFlag mutableFlag = new MutableFlag(false);
//    			XMLElement attrsXMLElement = process_default_datatype(attr.getReferenceDatatype(), parentXPath+"."+attr.getName(), attr.getName(), mutableFlag);
	    	    XMLElement attrsXMLElement = process_default_datatype(attr.getReferenceDatatype(), parentXPath+"."+attr.getNodeXmlName(), attr.getName());
	    	    if (attrsXMLElement != null)
    				xmlElement.addChild(attrsXMLElement);
    		}
    	}
    	xmlElement.addAttribute("xsi:type", datatype.getName(), null, null, null);
    	HL7XMLUtil.applyNullFlavorFunctionSecondStep(xmlElement,  datatype);
    	return xmlElement;
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
    public List<XMLElement> process_datatype(Datatype datatype, CSVSegment pCsvSegment, String parentXPath, String xmlName, boolean forceGenerate, MutableFlag hasUserdata, MutableFlag hasDefaultdata) throws MappingException ,FunctionException{
    	
    	if (!datatype.isEnabled()) return NullXMLElement.NULL;
    	if (datatype.getCsvSegments() == null)return NullXMLElement.NULL;
    	if (datatype.getCsvSegments().size()==0) return NullXMLElement.NULL;

    	//Scenario 1: Inputs are from different siblings
    	if (datatype.getCsvSegments().size() > 1) {    		
    		List<List<CSVSegment>> allListsCSVSegments = new ArrayList<List<CSVSegment>>();
    		
    		for (String csvS:datatype.getCsvSegments()) {
    			allListsCSVSegments.add(csvUtil.findCSVSegment(pCsvSegment, csvS));
    		}
    		
    		List <CSVSegment> csvSegmentList = new ArrayList<CSVSegment>();
    		List <Integer> csvSegmentIndex = new ArrayList<Integer>();
    		List <Integer> csvSegmentSum = new ArrayList<Integer>();
    		
    		int size = allListsCSVSegments.size();
    		for(int i=0;i<size;i++) {
    			if (allListsCSVSegments.get(i).isEmpty())
    			{
    				System.out.println("DatatypeProcessor.process_datatype()..missed segment:"+pCsvSegment+"."+xmlName);
//    				return null;
    				csvSegmentList.add(null);
    			}
    			else
    			csvSegmentList.add(allListsCSVSegments.get(i).get(0));
    			csvSegmentSum.add(allListsCSVSegments.get(i).size());
    			csvSegmentIndex.add(0);
    		}
    		
    		boolean hasMore = true;
    		List<XMLElement> sibXMLElements = new ArrayList<XMLElement>();
    		while (hasMore) {
	    	    MutableFlag mutableFlag = new MutableFlag(false);
	    	    MutableFlag mutableFlagDefault = new MutableFlag(true);
    			sibXMLElements.add(process_datatype_w_sibling(datatype, csvSegmentList, parentXPath, xmlName,forceGenerate, mutableFlag, mutableFlagDefault));
    			if (mutableFlag.hasUserMappedData())
    			{
    				hasUserdata.setHasUserMappedData(true);
    			}
    			if (!mutableFlagDefault.hasUserMappedData())
    			{
    				hasDefaultdata.setHasUserMappedData(false);
    			}
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
    	//Scenario 2: Inputs are from the same branch
    	else {
    	    MutableFlag mutableFlag = new MutableFlag(false);
    	    MutableFlag mutableFlagDefault = new MutableFlag(true);
        	List<XMLElement> returnXMLElements = process_datatype_wo_sibling(datatype, pCsvSegment, parentXPath, xmlName, forceGenerate, mutableFlag, mutableFlagDefault);
        	if (mutableFlag.hasUserMappedData())
        	{
        		hasUserdata.setHasUserMappedData(true);
        	}
			if (!mutableFlagDefault.hasUserMappedData())
			{
				hasDefaultdata.setHasUserMappedData(false);
			}
			return returnXMLElements;
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
    private List<XMLElement> process_datatype(Datatype datatype, List<CSVSegment> csvSegments, String parentXPath, String xmlName, boolean forceGenerate, MutableFlag hasUserdata, MutableFlag hasDefaultdata) throws MappingException ,FunctionException {
    	if (!datatype.isEnabled()) return NullXMLElement.NULL;
    	if (datatype.getCsvSegments() == null)return NullXMLElement.NULL;
    	if (datatype.getCsvSegments().size()==0) return NullXMLElement.NULL;
    	List<XMLElement> returnValue = new ArrayList<XMLElement>();
    	
	    MutableFlag mutableFlag = new MutableFlag(false);
	    MutableFlag mutableFlagDefault = new MutableFlag(true);

	    returnValue.add(process_datatype_w_sibling(datatype, csvSegments, parentXPath, xmlName, forceGenerate, mutableFlag,mutableFlagDefault));

	    if (mutableFlag.hasUserMappedData())
	    {
	    	hasUserdata.setHasUserMappedData(true);
	    }
		if (!mutableFlagDefault.hasUserMappedData())
		{
			hasDefaultdata.setHasUserMappedData(false);
		}
	    return  returnValue;
    }

    private List<XMLElement> process_datatype_wo_sibling(Datatype datatype, CSVSegment pCsvSegment, String parentXPath, String xmlName, boolean forceGenerate, MutableFlag hasUserdata, MutableFlag hasDefaultdata) throws MappingException ,FunctionException{
    	
    	List<XMLElement> resultList = new ArrayList<XMLElement>();
    	String current = datatype.getCsvSegments().get(0);
    	List<CSVSegment> csvSegments = csvUtil.findCSVSegment(pCsvSegment, current);

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
        		boolean isSimple = false;
        		if(!attr.isEnabled())
        			continue;
        		if (attr.getReferenceDatatype() == null) {
        			isSimple = true;
        		}
        		else {
        			if (attr.getReferenceDatatype().isSimple()) isSimple = true;
        		}
        		if (isSimple) {
    				String scsXmlPath = mappings.get(parentXPath+"."+attributeName);
    				if (scsXmlPath==null) 
    				{
    					//use user's default,not"forceGenerate"
    					processAttributeDefaultValue(false, attr, xmlElement,attributeName, hasDefaultdata,null,null);	
    					continue;
    				}
    				if (scsXmlPath.startsWith("function.")) { //function mapping to target
    					MutableFlag mutableFlag = new MutableFlag(false);
    					MutableFlag mutableFlagDefault = new MutableFlag(true);
    					String datavalue = getFunctionValue(csvSegment,scsXmlPath,data, mutableFlag, mutableFlagDefault);
    					if (mutableFlag.hasUserMappedData()) 
    					{
    						xmlElement.addAttribute(attributeName, datavalue, attr.getType(),null,null);
        					hasUserdata.setHasUserMappedData(true);
    					}
    					else 
    					{
   							processAttributeDefaultValue(forceGenerate, attr, xmlElement,attributeName, hasDefaultdata,null,null);
    					}
    					xmlElement.setHasUserMappedData(mutableFlag.hasUserMappedData());
    				}
    				else { //direct mapping from source to target
    					if (data.get(scsXmlPath) == null) { //inverse relationship
    						CSVField csvField = csvUtil.findCSVField(csvSegment, scsXmlPath);
    						if (csvField != null)
    						{
    							if (csvField.getValue().equals("")) 
    							{
    	   							processAttributeDefaultValue(forceGenerate, attr, xmlElement,attributeName, hasDefaultdata,null,null); 
    							}
    							else {
    								xmlElement.setHasUserMappedData(true);
    								xmlElement.addAttribute(attributeName, csvField.getValue(), attr.getType(),null,null);
    							}
    						}
    					}
    					else {
    						if (data.get(scsXmlPath).equals("")) {
	   							processAttributeDefaultValue(forceGenerate, attr, xmlElement,attributeName, hasDefaultdata,null,null); 
    						}
    						else {
    							xmlElement.setHasUserMappedData(true);
            					hasUserdata.setHasUserMappedData(true);
    							xmlElement.addAttribute(attributeName, data.get(scsXmlPath), attr.getType(),null,null);
    						}
    					}
    				}
    			}
    			else { //complexdatatype
    	    	    MutableFlag mutableFlag = new MutableFlag(false);
    	    	    MutableFlag mutableFlagDefault = new MutableFlag(true);
//    				List<XMLElement> attrsXMLElement = process_datatype(attr.getReferenceDatatype(), csvSegment, parentXPath+"."+attr.getName(), attr.getName(),forceGenerate, mutableFlag, mutableFlagDefault);
    	    	    List<XMLElement> attrsXMLElement = process_datatype(attr.getReferenceDatatype(), csvSegment, parentXPath+"."+attr.getNodeXmlName(), attr.getName(),forceGenerate, mutableFlag, mutableFlagDefault);
    	    	    if (mutableFlag.hasUserMappedData())
    				{
    					hasUserdata.setHasUserMappedData(true);
    				}
    				if (!mutableFlagDefault.hasUserMappedData())
    				{
    					hasDefaultdata.setHasUserMappedData(false);
    				}
    				xmlElement.addChildren(attrsXMLElement);
    			}
    		}
    		xmlElement.addAttribute("xsi:type", datatype.getName(), null,null, null);
    		HL7XMLUtil.applyNullFlavorFunctionSecondStep(xmlElement,  datatype);
    		resultList.add(xmlElement);
    	}   	
    	return resultList;
    }

    private XMLElement process_datatype_w_sibling(Datatype datatype, List<CSVSegment> csvSegments, String parentXPath, String xmlName, boolean forceGenerate, MutableFlag hasUserdata, MutableFlag hasDefaultdata) throws MappingException ,FunctionException{
    	
		XMLElement xmlElement = new XMLElement();
		xmlElement.setName(xmlName);
		
		Hashtable <String, String> data = new Hashtable<String,String>();
    	for(CSVSegment csvSegment:csvSegments) {
    		if (csvSegment==null)
    			continue;
    		List<CSVField> csvFields = csvSegment.getFields();
    		for (CSVField csvField:csvFields) {
    			data.put(csvField.getXmlPath(),csvField.getValue());
    		}
    	}
		
		Hashtable<String, Attribute> attrs = datatype.getAttributes();
		for(String attributeName:(Set<String>)(attrs.keySet())) {
			Attribute attr = attrs.get(attributeName);

			boolean isSimple = false;

			if (attr.getReferenceDatatype() == null) {
				isSimple = true;
			}
			else {
				if (attr.getReferenceDatatype().isSimple()) isSimple = true;
			}
			if (isSimple) {
				String scsXmlPath = mappings.get(parentXPath+"."+attributeName);
				if (scsXmlPath==null)
				{
					//use user's default,not"forceGenerate"
					processAttributeDefaultValue(false, attr, xmlElement,attributeName, hasDefaultdata,null,null);	
					continue;
				}
				
				if (scsXmlPath.startsWith("function.")) { //function mapping to target
					MutableFlag mutableFlag = new MutableFlag(false);
					MutableFlag mutableFlagDefault = new MutableFlag(true);
					String datavalue = getFunctionValue(csvSegments,scsXmlPath,data, mutableFlag, mutableFlagDefault);
					if (mutableFlag.hasUserMappedData()) 
					{
						hasUserdata.setHasUserMappedData(true);
						xmlElement.addAttribute(attributeName, datavalue, attr.getType(),null,null);
					}
					else 
					{
						processAttributeDefaultValue(forceGenerate, attr, xmlElement,attributeName, hasDefaultdata,null,null);
					}
					xmlElement.setHasUserMappedData(mutableFlag.hasUserMappedData());
				}
				else { //direct mapping from source to target
					if (data.get(scsXmlPath) == null) { //inverse relationship
						CSVField csvField = csvUtil.findCSVField(csvSegments, scsXmlPath);
						if (csvField.getValue().equals("")) {
   							processAttributeDefaultValue(forceGenerate, attr, xmlElement,attributeName, hasDefaultdata,null,null); 
						}
						else {
							xmlElement.setHasUserMappedData(true);
							hasUserdata.setHasUserMappedData(true);
							xmlElement.addAttribute(attributeName, csvField.getValue(), attr.getType(),null,null);
						}
					}
					else {
						if (data.get(scsXmlPath).equals("")) {
   							processAttributeDefaultValue(forceGenerate, attr, xmlElement,attributeName, hasDefaultdata,null,null); 
						}
						else {
							xmlElement.setHasUserMappedData(true);
							hasUserdata.setHasUserMappedData(true);
							xmlElement.addAttribute(attributeName, data.get(scsXmlPath), attr.getType(),null,null);
						}
					}
				}
			}
			else { //complexdatatype
	    	    MutableFlag mutableFlag = new MutableFlag(false);
	    	    MutableFlag mutableFlagDefault = new MutableFlag(true);
//				List<XMLElement> attrsXMLElement = process_datatype(attr.getReferenceDatatype(), csvSegments, parentXPath+"."+attr.getName(), attr.getName(),forceGenerate, mutableFlag, mutableFlagDefault);
	    	    List<XMLElement> attrsXMLElement = process_datatype(attr.getReferenceDatatype(), csvSegments, parentXPath+"."+attr.getNodeXmlName(), attr.getName(),forceGenerate, mutableFlag, mutableFlagDefault);
	    	    if (mutableFlag.hasUserMappedData())
				{
					hasUserdata.setHasUserMappedData(true);
				}
				if (!mutableFlagDefault.hasUserMappedData())
				{
					hasDefaultdata.setHasUserMappedData(false);
				}
				xmlElement.addChildren(attrsXMLElement);
			}
		}
		xmlElement.addAttribute("xsi:type", datatype.getName(), null,null, null);
		HL7XMLUtil.applyNullFlavorFunctionSecondStep(xmlElement,  datatype);
		return xmlElement;
    }
    public String getFunctionValue(CSVSegment pCsvSegment, String scsXmlPath, Hashtable<String, String> data, MutableFlag hasUserData, MutableFlag hasDefaultdata) throws MappingException ,FunctionException{
    	List<CSVSegment> csvSegments = new ArrayList<CSVSegment>();
    	csvSegments.add(pCsvSegment);
    	return getFunctionValue(csvSegments,scsXmlPath, data, hasUserData, hasDefaultdata);
    }

    private String getFunctionValue(List<CSVSegment> csvSegments, String scsXmlPath, Hashtable<String, String> data, MutableFlag hasUserData, MutableFlag hasDefaultdata) throws MappingException ,FunctionException
    {
       int outputpos = 0;

    	int pos = scsXmlPath.lastIndexOf(".outputs.");
    	outputpos = Integer.valueOf(scsXmlPath.substring(pos+9, scsXmlPath.length()));
    	String fXmlPath = scsXmlPath.substring(0,pos);
        List<String> inputValues = new ArrayList<String>();
        FunctionComponent functionComponent = functions.get(fXmlPath);
    	if (functionComponent == null)
    		throw new MappingException("count not find function: " + scsXmlPath, null);

    	FunctionMeta functionMeta = functionComponent.getMeta();

        String valueDataSpec = null;
        HTML_SpecialCharacterTable hsct = new HTML_SpecialCharacterTable();
        List<Class> parameterTypes = null;

        // first, is the function kind of dataspec?
        while ((functionMeta.isFrame())&&(functionMeta.getDataSpec()!=null))
        {
            FunctionDataSpecExe functionDataSpecExe = functionComponent.getFunctionDataSpecExe();
            if(functionDataSpecExe == null) break;
            String valueOr = functionDataSpecExe.getValue();
            //String dType = functionDataSpecExe.getDataSpec().getDatatype().toString();
            if ((valueOr == null)||(valueOr.trim().equals(""))) break;
            valueOr = valueOr.trim();

            valueDataSpec = hsct.transformTaggingToString(valueOr);
            if (functionDataSpecExe.getFunctionName().equalsIgnoreCase("constant")) return valueDataSpec;

            Class[] parameterTypesArr = functionMeta.getParameterTypes();
            parameterTypes = new ArrayList<Class>();
            for(Class classO:parameterTypesArr) parameterTypes.add(classO);
            break;
        }
        // first, is the function a constant?
    	if (functionMeta.isConstantFunction()) {
    		FunctionConstant fc = functionComponent.getFunctionConstant();
    		if (fc == null)
    			throw new MappingException("count not find function constant for function: " + functionComponent.getId(), null);
    		hasUserData.setHasUserMappedData(false);
            hasDefaultdata.setHasUserMappedData(true);
            return fc.getValue();
    	}

        // for each input find it's input value.
        for (int i = 0; i < functionMeta.getInputDefinitionList().size(); i++) {
            String inputData = mappings.get("function."+functionComponent.getId()+"."+"inputs"+"."+i);
            //ignore if no mapping for an input port
            if (inputData==null)
            {
                inputValues.add(null);
                continue;
            }
            String inputvalue = null;
            if (inputData.startsWith("function.")) { //function mapping to target
                inputvalue = getFunctionValue(csvSegments,inputData, data, hasUserData, hasDefaultdata);
            }
            else { //direct mapping from source to target
                inputvalue = data.get(inputData);
                if (inputvalue == null) { //inverse relationship
                    CSVField csvField = csvUtil.findCSVField(csvSegments, inputData);
                    if (csvField!=null)
                        inputvalue = csvField.getValue();
//    				if (inputvalue!=null&&!inputvalue.equals(""))
//    					hasUserData.setHasUserMappedData(true);
                }

                if (inputvalue!=null&&!inputvalue.equals(""))
                       hasUserData.setHasUserMappedData(true);

            }
            //if (inputvalue.startsWith("&%F:")) inputvalue = inputvalue.substring(4);
            inputValues.add(inputvalue);
        }

        // It is the FunctionVocabularyMapping?
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
            if (res!=null&&!res.equals(""))
                       hasUserData.setHasUserMappedData(true);
            return res;
        }


        if (valueDataSpec != null)
        {
            String functionName = functionComponent.getMeta().getFunctionName();
            Class classObj = null;
            try
            {
                OutDataType dType = functionComponent.getFunctionDataSpecExe().getDataSpec().getDatatype();

                if (dType.getType() == OutDataType.DOUBLE_TYPE) classObj = Double.TYPE;
                else if (dType.getType() == OutDataType.STRING_TYPE) classObj = String.class;
                else if (dType.getType() == OutDataType.INT_TYPE) classObj = Integer.TYPE;
            }
            catch(Exception fe)
            {
                classObj = null;
            }
            if (classObj == null) throw new FunctionException("classObj of dataspec == null");

            if (functionName.equalsIgnoreCase("changeFormat"))
            {
                inputValues.add(0, valueDataSpec);
                parameterTypes.add(0, classObj);
            }
            else if (functionName.equalsIgnoreCase("setprefix"))
            {
                inputValues.add(0, valueDataSpec);
                parameterTypes.add(0, classObj);
            }
            else if (functionName.equalsIgnoreCase("nullflavorwith"))
            {
                inputValues.add(valueDataSpec);
                parameterTypes.add(classObj);
                inputValues.add(null);
                parameterTypes.add(classObj);
            }
            else if (functionName.equalsIgnoreCase("substringwith"))
            {
                if ((inputValues == null)||(inputValues.size() == 0)) return "";
                String val = inputValues.get(0);
                if ((val == null)||(val.length() == 0)) return "";
                String from = "";
                String to = "";

                String delimiter = "~";
                int idx = valueDataSpec.indexOf(delimiter);
                if (idx < 0) from = valueDataSpec;
                else
                {
                    from = valueDataSpec.substring(0, idx);
                    to = valueDataSpec.substring(idx+delimiter.length());
                }
                int[] ii  = new int[2];
                String keyWord = "";
                String keyWordDef = "indexof:";
                String keyWordInc = "indexof+:";
                String keyWordExc = "indexof-:";
                String idxChar1 = null;

                for (int i=0;i<2;i++)
                {
                    String str = from;
                    if (i == 1)
                    {
                        str = to;
                        if (str.equalsIgnoreCase("end")) str = "" + val.length();
                    }

                    if (str.toLowerCase().startsWith(keyWordDef)) keyWord = keyWordDef;
                    else if (str.toLowerCase().startsWith(keyWordExc)) keyWord = keyWordExc;
                    else if (str.toLowerCase().startsWith(keyWordInc)) keyWord = keyWordInc;
                    else keyWord = null;

                    if (str.trim().equals(""))
                    {
                        if (i==1) ii[i] = val.length();
                        else ii[i] = 0;
                    }
                    else if (keyWord != null)
                    {
                        String str1 = str.substring(keyWord.length()).trim();
                        if (((str1.startsWith("\""))&&(str1.endsWith("\"")))||((str1.startsWith("'"))&&(str1.endsWith("'"))))
                            str1 = str1.substring(1, str1.length()-1);
                        String tilde = "&tilde;";
                        while(true)
                        {
                            int idxHy = str1.toLowerCase().indexOf(tilde);
                            if (idxHy < 0) break;
                            str1 = str1.substring(0, idxHy) + delimiter + str1.substring(idxHy + tilde.length());
                        }
                        if (str1.equals("")) ii[i] = -1;
                        else
                        {
                            if (i==0)
                            {
                                idxChar1 = str1;
                                ii[i] = val.indexOf(str1);
                                if ((ii[i] >= 0)&&(keyWord.equals(keyWordExc))) ii[i] = ii[i] + str1.length();
                            }
                            else
                            {
                                int startP = ii[0];
                                if ((startP >= 0)&&(idxChar1 != null)) startP = startP + idxChar1.length();
                                if (ii[0] < 0) startP = 0;
                                String subStr = val.substring(startP);
                                ii[i] = subStr.indexOf(str1);
                                if (ii[i] < 0) ii[i] = val.indexOf(str1);
                                else ii[i] = ii[i] + startP;
                                if ((ii[i] >= 0)&&(keyWord.equals(keyWordInc))) ii[i] = ii[i] + str1.length();
                            }
                        }
                    }
                    else
                    {
                        try
                        {
                            ii[i] = Integer.parseInt(str);
                        }
                        catch(NumberFormatException ne)
                        {
                            ii[i] = -1;
                        }
                    }
                }
                if (ii[0] == ii[1]) return "";
                if (ii[0] > ii[1])
                {
                    int tt = ii[0];
                    ii[0] = ii[1];
                    ii[1] = tt;
                }
                if ((ii[0] <= 0)&&(ii[1] >= val.length())) return val;
                if (ii[0] >= val.length()) return "";
                if (ii[1] <= 0) return "";
                if (ii[0] < 0) ii[0] = 0;
                if (ii[1] > val.length()) ii[1] = val.length();
                return val.substring(ii[0], ii[1]);
            }
            else if (functionName.equalsIgnoreCase("replacewith"))
            {
                if ((inputValues == null)||(inputValues.size() == 0)) return "";
                String val = inputValues.get(0);
                if ((val == null)||(val.length() == 0)) return "";
                String from = "";
                String to = "";
                int idx = valueDataSpec.indexOf("=>");
                if (idx < 0) return val;
                else
                {
                    from = valueDataSpec.substring(0, idx).trim();
                    to = valueDataSpec.substring(idx+2).trim();
                }
                if (((from.startsWith("\""))&&(from.endsWith("\"")))||((from.startsWith("'"))&&(from.endsWith("'"))))
                            from = from.substring(1, from.length()-1);
                if (((to.startsWith("\""))&&(to.endsWith("\"")))||((to.startsWith("'"))&&(to.endsWith("'"))))
                            to = to.substring(1, to.length()-1);
                if ((from.equals(""))||(to.equals(""))) return val;
                inputValues.add(from);
                inputValues.add(to);
                parameterTypes.add(classObj);
                parameterTypes.add(classObj);
            }
            else
            {
                inputValues.add(valueDataSpec);
                parameterTypes.add(classObj);
//                if (functionComponent.getMeta().getGroupName().toLowerCase().indexOf("math") >= 0)
//                {
//                    String fName = functionComponent.getMeta().getName().toLowerCase();
//                    if (fName.equals("round")) parameterTypes.add(Integer.TYPE);
//                    else if (fName.indexOf("compute") >= 0) parameterTypes.add(String.class);
//                    else parameterTypes.add(Double.TYPE);
//                }
//                else parameterTypes.add(String.class);
            }
        }


        // pass the input values to the function.compute() function.
        Object[] valueArray = null;
        String theValue = null;
        if (parameterTypes != null)
        {
            Class[] cArr = new Class[parameterTypes.size()];
            for (int i=0;i<cArr.length;i++) cArr[i] = parameterTypes.get(i);
            valueArray = functionMeta.compute(inputValues.toArray(), cArr);
        }
        else valueArray = functionMeta.compute(inputValues.toArray());

        if (valueArray[outputpos]==null)   return theValue;

        theValue = valueArray[outputpos].toString();

        return theValue;

    }

    public void processAttributeDefaultValue(boolean forceGenerate, Attribute attr, XMLElement xmlElement, String attributeName, MutableFlag hasDefaultData, String domainName, String codingStrength) 
    {
    	if (forceGenerate) {
    		if (attr == null) {
    			Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"No user data is available for the mapping to " + attributeName + ", and no default value is defined"});
    			ValidatorResults validatorResults = new ValidatorResults();
    			validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
    			xmlElement.setValidatorResults(validatorResults);
    			hasDefaultData.setHasUserMappedData(false);
    		}
    		else {
    			if (attr.getDefaultValue()!= null && !attr.getDefaultValue().equals(""))
    			{
    				Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"No user data is available for the mapping to " + attr.getXmlPath() + ", default data is used instead"});
    				ValidatorResults validatorResults = new ValidatorResults();
    				validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.INFO, msg));
    				xmlElement.addAttribute(attributeName, attr.getDefaultValue(), attr.getType(),domainName,codingStrength);
    				xmlElement.setValidatorResults(validatorResults);
    			}
    			else {
    				Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"No user data is available for the mapping to " + attr.getXmlPath() + ", and no default value is defined"});
    				ValidatorResults validatorResults = new ValidatorResults();
    				validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
    				xmlElement.setValidatorResults(validatorResults);
    				hasDefaultData.setHasUserMappedData(false);
    			}
    		}
    	}
    	else {
    		if (attr == null) 
    		{
    			hasDefaultData.setHasUserMappedData(false);
    			return;
    		}
    		if (attr.getDefaultValue()!= null && !attr.getDefaultValue().equals(""))
    		{
    			xmlElement.addAttribute(attributeName, attr.getDefaultValue(), attr.getType(),domainName,codingStrength);
    		}
    		else {
    			hasDefaultData.setHasUserMappedData(false);
    		}
    	}
    }
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.11  2009/01/09 21:57:12  wangeug
 * HISTORY :Apply NullFlavor for each XMLElement
 * HISTORY :
 * HISTORY :Revision 1.10  2008/12/04 20:42:36  wangeug
 * HISTORY :support nullFlavor
 * HISTORY :
 * HISTORY :Revision 1.9  2008/11/21 16:19:37  wangeug
 * HISTORY :Move back to HL7 module from common module
 * HISTORY :
 * HISTORY :Revision 1.8  2008/11/17 20:10:07  wangeug
 * HISTORY :Move FunctionComponent and VocabularyMap from HL7 module to common module
 * HISTORY :
 * HISTORY :Revision 1.7  2008/09/29 15:40:38  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */