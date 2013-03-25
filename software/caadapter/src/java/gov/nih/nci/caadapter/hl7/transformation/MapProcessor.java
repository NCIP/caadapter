/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.transformation;

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.csv.data.CSVField;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.csv.data.CSVSegmentedFile;
import gov.nih.nci.caadapter.common.function.FunctionException;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;
import gov.nih.nci.caadapter.hl7.map.MappingException;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFUtil;
import gov.nih.nci.caadapter.hl7.transformation.data.MutableFlag;
import gov.nih.nci.caadapter.hl7.transformation.data.NullXMLElement;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeSet;

/**
 * The class will process the .map file an genearte HL7 v3 messages.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.52 $
 *          date        $Date: 2009-04-17 21:57:04 $
 */

public class MapProcessor {

    // class variable from constructor
    private Hashtable<String,String> mappings = null;
    private MapProcssorCSVUtil csvUtil = null;
    private DatatypeProcessor datatypeProcessor = new DatatypeProcessor();

    // Class variables used during processing.
    ValidatorResults theValidatorResults = new ValidatorResults();

	/**
	 * This method will process the mapping file and generate a list of HL7 v3 message objects
	 *
	 * @param mapfilename the name of the mapping file
	 * @param csvfilename the name of the csv file
	 */
    public List<XMLElement> process(Hashtable<String,String> mappings, Hashtable<String,FunctionComponent> functions, CSVSegmentedFile csvSegmentedFile, MIFClass mifClass, ArrayList <TransformationObserver>transformationWatchList) throws MappingException,FunctionException{
        // init class variables
        this.mappings = mappings;
        MapProcessorHelper mapProcessorHelper = new MapProcessorHelper();
        csvUtil = new MapProcssorCSVUtil();

        datatypeProcessor.setEnv(csvUtil, functions, mappings);

        List<XMLElement> resultsArray = new ArrayList<XMLElement>();

        List<CSVSegment> logicalRecords = csvSegmentedFile.getLogicalRecords();

        if (logicalRecords.size()==0)
        {
        	return resultsArray;
        }

        mapProcessorHelper.preprocessMIF(mappings,functions, mifClass, false, logicalRecords.get(0).getName());

        // process one CSV source logical record at a time.
        if (transformationWatchList.size()!=0) {
        	for (TransformationObserver tObserver:transformationWatchList)
        	{
        		tObserver.progressUpdate(0);
        		tObserver.setMessageCount(logicalRecords.size());
        	}
        }
        for (int i = 0; i < logicalRecords.size(); i++) {
        	List<XMLElement> xmlElements = processRootMIFclass(mifClass, logicalRecords.get(i));
        	for(XMLElement xmlElement:xmlElements) {
        		resultsArray.add(xmlElement);
        	}
            if (transformationWatchList.size()!=0) {
            	for (TransformationObserver tObserver:transformationWatchList)
            	{
            		tObserver.progressUpdate(i);
            		if (tObserver.isRequestCanceled()) break;
            	}
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
    	List<CSVSegment> csvSegments = null;

    	//Step1: find all the csvSegments for attributes
    	if (mifClass.isMapped())
    	{
    		csvSegments = csvUtil.findCSVSegment(pCsvSegment, mifClass.getCsvSegment());
    	}
    	else {
    		csvSegments = new ArrayList<CSVSegment>();
    		csvSegments.add(pCsvSegment);
    	}

    	for(CSVSegment csvSegment:csvSegments) {
    		theValidatorResults.removeAll();
    	    ValidatorResults localValidatorResults = new ValidatorResults();
    	    MutableFlag mutableFlag = new MutableFlag(false);
    	    MutableFlag mutableFlagDefault = new MutableFlag(true);
    	    //process the root mifClass, do not relax cardinality
    	    boolean relaxCardinality=false;
			List<XMLElement> xmlElementTemp = processMIFclass(mifClass,null, csvSegment,true,mutableFlag,mutableFlagDefault,relaxCardinality);
    		if (theValidatorResults.getAllMessages().size() == 0) {
	            Message msg = MessageResources.getMessage("XML4", new Object[]{""});
	            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.INFO, msg));
    		}
    	    localValidatorResults.addValidatorResults(theValidatorResults);
    		//It should only return one element
    		if (xmlElementTemp.size()> 0) {
    			xmlElementTemp.get(0).setValidatorResults(localValidatorResults);
    			xmlElementTemp.get(0).setMifClass(mifClass);
    		}
    		xmlElementTemp.get(0).populateValidatorResults();
    		xmlElements.addAll(xmlElementTemp);
    	}
    	return xmlElements;
    }
	/**
	 * This method will process a MIFClass object and generate a list of HL7 v3 message objects
	 *
	 * @param mifClass the MIFClass that will be processed
	 * @param pCsvSegment CSV segments that determines the root segments that dominate the cardinality
	 * @param forceGenerate flag to tell the generator to generate XML element even without user mapping data
	 * 		  and data for all MIFAttributes and MIFAssociation of the MIFClass
	 */

    private List<XMLElement> processMIFclass(MIFClass mifClass, String holderPath, CSVSegment pCsvSegment, boolean forceGeneratePassed, MutableFlag hasUserdata, MutableFlag hasDefaultdata, boolean isCardinalityRelaxed) throws MappingException,FunctionException {
    	boolean forceGenerate = forceGeneratePassed;
    	List<XMLElement> xmlElements = new ArrayList<XMLElement>();
    	List<XMLElement> choiceXMLElements = new ArrayList<XMLElement>();

    	if (mifClass.getCsvSegments().size() == 0 && !forceGenerate) return NullXMLElement.NULL;

    	List<CSVSegment> csvSegments = null;

    	//Step1: find all the csvSegments for attributes
    	if (mifClass.isMapped())
    	{
    		csvSegments = csvUtil.findCSVSegment(pCsvSegment, mifClass.getCsvSegment());
    	}
    	else {
    		csvSegments = new ArrayList<CSVSegment>();
    		csvSegments.add(pCsvSegment);
    	}

    	for(CSVSegment csvSegment:csvSegments) {
    		XMLElement xmlElement = new XMLElement();
    		xmlElement.setName(mifClass.getName());
    		xmlElement.setMessageType(mifClass.getMessageType());
    		//Step 1.1 process Choice
        	if (mifClass.getSortedChoices().size() > 0) { //Handle choice
        		for(MIFClass choiceMIFClass:mifClass.getSortedChoices()) {
        	    	if (choiceMIFClass.isChoiceSelected()) {
        	    	    MutableFlag mutableFlag = new MutableFlag(false);
        	    	    MutableFlag mutableFlagDefault = new MutableFlag(true);
        	    		choiceXMLElements = processMIFclass(choiceMIFClass,holderPath,csvSegment, forceGenerate, mutableFlag, mutableFlagDefault,isCardinalityRelaxed);
        	    		if (mutableFlag.hasUserMappedData())
        	    		{
        	    			hasUserdata.setHasUserMappedData(true);
        	    		}
        	    		if (choiceXMLElements.size() > 0) //Should be 1
        	    		{
        	    			xmlElement = choiceXMLElements.get(0);
        	        		xmlElement.setName(mifClass.getName());
        	        		xmlElement.setMessageType(mifClass.getMessageType());
        	        		if (choiceXMLElements.size() > 1)
        	        		{
        	        			//Warning
        	        		}
        	    		}
        	    	}
        		}
        	}

        	TreeSet<MIFAttribute> attributes = mifClass.getSortedAttributes();

    		//Step2: Process non-structural attributes
    		//Non-structural attributes are child xmlelements vs structural attributes are attributes to xml elements
    		for(MIFAttribute mifAttribute:attributes) {
    			if (!mifAttribute.isStrutural()) {
    	    	    MutableFlag mutableFlag = new MutableFlag(false);
    	    	    MutableFlag mutableFlagDefault = new MutableFlag(true);
    				List<XMLElement> attrXmlElements = processAttribute(mifAttribute ,csvSegment, mutableFlag, mutableFlagDefault);
    				if (mutableFlag.hasUserMappedData())
    				{
    					hasUserdata.setHasUserMappedData(true);
    				}
    				if (mifAttribute.getMaximumMultiplicity() == 1) {
    					if (attrXmlElements.size()>1) {
    			            Message msg = MessageResources.getMessage("RIM4", new Object[]{mifAttribute.getXmlPath(),mifAttribute.getMinimumMultiplicity() + "..1", mifAttribute.getName(),attrXmlElements.size()});
    			            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
    					}
    				}
    				if (mifAttribute.getMinimumMultiplicity() == 1) {
    					if (attrXmlElements.size()== 0
    							&& !isCardinalityRelaxed) {
    			            Message msg = MessageResources.getMessage("RIM4", new Object[]{mifAttribute.getXmlPath(),mifAttribute.getMinimumMultiplicity() + "..1", mifAttribute.getName(),attrXmlElements.size()});
    			            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
    					}
    					if (!mutableFlagDefault.hasUserMappedData()) hasDefaultdata.setHasUserMappedData(false);
    				}
    				if (attrXmlElements.size() != 0)
    					xmlElement.addChildren(attrXmlElements);
    			}
    		}

    		TreeSet<MIFAssociation> associations = mifClass.getSortedAssociations();

    		//Step 3: Process associations
    		boolean startChoice=false;
    		String choiceString = "";
    		MIFAssociation choiceAssociation=null;
    		int totalChoiceHasData = 0;
    		List<List<XMLElement>> choiceHolder = new ArrayList<List<XMLElement>>();
    		List<MutableFlag> choiceFlag = new ArrayList<MutableFlag>();
    		for(MIFAssociation mifAssociation : associations) {
    			boolean toRelaxCardinality=isCardinalityRelaxed;
    			if (!toRelaxCardinality)
    			{
    			//check if to relax cardinality
    				int asscMultiple=mifClass.getMaxAssociationMultiplicityWithName(mifAssociation.getName());
    				if (asscMultiple>mifAssociation.getMaximumMultiplicity());
    					toRelaxCardinality=true;
    			}
    			boolean canAdd = true;
    			if (MIFUtil.containChoiceAssociation(mifAssociation)&& mifAssociation.getMifClass()!= null) {
    				if (mifAssociation.getNodeXmlName().substring(mifAssociation.getNodeXmlName().length()-2).equals("00"))
    				{
    					if (startChoice){ //start a new choice and need to process old choice section
    					    process_default_empty_choice(mifAssociation, xmlElement, choiceHolder, choiceFlag, choiceString);
    					}
    					choiceString = mifAssociation.getNodeXmlName().substring(0, mifAssociation.getNodeXmlName().length()-2);
    					startChoice = true;
    					totalChoiceHasData = 0;
    					choiceHolder.clear();
    					choiceFlag.clear();
    					choiceAssociation = mifAssociation;
    				}
    			}
    			else
    			{
    				if(startChoice) //need to process a set of choices
    				{
    					if (totalChoiceHasData ==0) //no choice has data
    					{
    					    process_default_empty_choice(mifAssociation, xmlElement, choiceHolder, choiceFlag, choiceString);
    					}
    				}
    			}
	    	    MutableFlag mutableFlag = new MutableFlag(false);
	    	    MutableFlag mutableFlagDefault = new MutableFlag(true);
	    	    boolean forceGenerateAssociation = (mifAssociation.isOptionForced() || mifAssociation.getMinimumMultiplicity() > 0);
	    	    List<XMLElement> choiceXmlElements = new ArrayList<XMLElement>();
 	    	    List<XMLElement> assoXmlElements = processAssociation(mifAssociation ,csvSegment, mutableFlag,mutableFlagDefault, forceGenerateAssociation, startChoice, choiceXmlElements,toRelaxCardinality);
    			/** block start **********
    			 *
    			 * The following block to avoid associations to be
    			 * included with current class if it comes from other
    			 * reference
    			 */
 	    	    boolean asscToProcess=false;
	    	    String asscParentXml=mifAssociation.getParentXmlPath();
    			if (holderPath==null||holderPath.equals(""))
    				asscToProcess=true;
    			else if (asscParentXml==null||asscParentXml.equals(""))
    				asscToProcess=true;
    			else if (asscParentXml.equalsIgnoreCase(holderPath))
    				asscToProcess=true;
    			if (!asscToProcess)
    				continue;
    			/** block end **/

	    	    if (startChoice) {
    				choiceHolder.add(choiceXmlElements);
    				choiceFlag.add(mutableFlagDefault);
    			}
    			if (mutableFlag.hasUserMappedData())
    			{
    				hasUserdata.setHasUserMappedData(true);
    				totalChoiceHasData ++;
    				if (totalChoiceHasData > 1)
    				{
    					if (mifAssociation.getMaximumMultiplicity() == 1) {
    						if (assoXmlElements.size()>1 )//0)
    						{
    							Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"The cardinality of  the choice " + mifAssociation.getXmlPath() + " is specified as "  + mifAssociation.getMinimumMultiplicity() + "..1" + ", but more than 1 choice ("+assoXmlElements.size()+")contains data, and the data is dropped"});
    							theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
    							canAdd = false;
    						}
    					}
    				}
    			}
    			if (assoXmlElements.size() != 0&&canAdd)
    				xmlElement.addChildren(assoXmlElements);
				if (mifAssociation.getMaximumMultiplicity() == 1) {
					if (assoXmlElements.size()>1) {
			            Message msg = MessageResources.getMessage("RIM5", new Object[]{mifAssociation.getXmlPath(),mifAssociation.getMinimumMultiplicity() + "..1", mifAssociation.getName(),assoXmlElements.size()});
			            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
					}
				}
				if (mifAssociation.getMinimumMultiplicity() == 1&&!startChoice) {
					if (assoXmlElements.size()== 0) {
			            Message msg = MessageResources.getMessage("RIM5", new Object[]{mifAssociation.getXmlPath(),mifAssociation.getMinimumMultiplicity() + "..1", mifAssociation.getName(),assoXmlElements.size()});
			            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
					}
					if (!mutableFlagDefault.hasUserMappedData()) hasDefaultdata.setHasUserMappedData(false);
				}
    		}
			if(startChoice) //need to process a set of choices
			{
				if (totalChoiceHasData ==0) //no choice has data
				{
				    process_default_empty_choice(choiceAssociation, xmlElement, choiceHolder, choiceFlag, choiceString);
				}
			}

    		//Step 4: Process structural attributes
    		for(MIFAttribute mifAttribute:attributes) {
    			if (mifAttribute.isStrutural()) {
		    		String h3sPath = mifAttribute.getParentXmlPath()+"."+mifAttribute.getNodeXmlName();
					if (mappings.get(h3sPath)!= null) {
						String scsPath = mappings.get(h3sPath);
    					List<CSVField> csvFields = csvSegment.getFields();
    					Hashtable <String, String> data = new Hashtable<String,String>();
    					for (CSVField csvField:csvFields) {
    						data.put(csvField.getXmlPath(),csvField.getValue());
    					}

	    				if (scsPath.startsWith("function.")) { //function mapping to target
	    					MutableFlag mutableFlag = new MutableFlag(false);
	    					MutableFlag mutableFlagDefault = new MutableFlag(true);
	    					String datavalue = datatypeProcessor.getFunctionValue(csvSegment,scsPath,data, mutableFlag, mutableFlagDefault);
	    					if (mutableFlag.hasUserMappedData())
	    					{
                                if (mifAttribute.getDatatype() == null)
                                    xmlElement.addAttribute(mifAttribute.getName(), datavalue, null, mifAttribute.getDomainName(), mifAttribute.getCodingStrength());
	        					else xmlElement.addAttribute(mifAttribute.getName(), datavalue, mifAttribute.getDatatype().getName().toLowerCase(), mifAttribute.getDomainName(), mifAttribute.getCodingStrength());

                                hasUserdata.setHasUserMappedData(true);
	    					}
	    					else
	    					{
	    						datatypeProcessor.processAttributeDefaultValue(forceGenerate, null, xmlElement,mifAttribute.getName(), hasDefaultdata,null,null);
	    					}
	    					xmlElement.setHasUserMappedData(mutableFlag.hasUserMappedData());
	    				}
	    				else  //direct mapping from source to target
	    				{

	    					if (data.get(scsPath) == null) { //inverse relationship
	    						CSVField csvField = csvUtil.findCSVField(csvSegment, scsPath);
	    						if (csvField.getValue().equals("")) {
	    							datatypeProcessor.processAttributeDefaultValue(mifAttribute.getMinimumMultiplicity()>0, null, xmlElement,mifAttribute.getName(), hasDefaultdata,mifAttribute.getDomainName(), mifAttribute.getCodingStrength());
	    							break;
	    						}
	    						else {
	    							hasUserdata.setHasUserMappedData(true);
	    							xmlElement.setHasUserMappedData(true);
	    							xmlElement.addAttribute(mifAttribute.getName(), csvField.getValue(),null,mifAttribute.getDomainName(), mifAttribute.getCodingStrength());
	    						}
	    					}
	    					else {
	    						if (data.get(scsPath).equals("")) {
	    							datatypeProcessor.processAttributeDefaultValue(mifAttribute.getMinimumMultiplicity()>0, null, xmlElement,mifAttribute.getName(), hasDefaultdata,mifAttribute.getDomainName(), mifAttribute.getCodingStrength());
	    							break;
	    						}
	    						else {
	    							xmlElement.setHasUserMappedData(true);
	    							hasUserdata.setHasUserMappedData(true);
	    							xmlElement.addAttribute(mifAttribute.getName(), data.get(scsPath), null, mifAttribute.getDomainName(), mifAttribute.getCodingStrength());
	    						}
	    					}
	    				}
					}
					else {

						if (mifAttribute.getDefaultValue()!=null&&!mifAttribute.getDefaultValue().equals(""))
						{
							//if the MIFAttribute is user editable, its value may have been set by user
							//otherwise, it should always be same with mnemonicValue from MIF spec
							xmlElement.addAttribute(mifAttribute.getName(), mifAttribute.getDefaultValue(),null,mifAttribute.getDomainName(), mifAttribute.getCodingStrength());
						}
						else if(mifAttribute.getMnemonic()!=null&&!mifAttribute.getMnemonic().equals(""))
						{
							//use HL7 default value
							xmlElement.addAttribute(mifAttribute.getName(), mifAttribute.getMnemonic(),null,mifAttribute.getDomainName(), mifAttribute.getCodingStrength());
						}
						else
						{
							if (mifAttribute.getFixedValue()!=null && !mifAttribute.getFixedValue().equals(""))
							{
								xmlElement.addAttribute(mifAttribute.getName(), mifAttribute.getFixedValue(),null,mifAttribute.getDomainName(), mifAttribute.getCodingStrength());
							}
						}
					}
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

    private List<XMLElement> processAssociation(MIFAssociation mifAssociation,  CSVSegment csvSegment, MutableFlag hasUserdata, MutableFlag hasDefaultdata, boolean forceGenerate, boolean choiceFlag, List<XMLElement> choiceXmlElements, boolean isCardinalityRelaxed) throws MappingException,FunctionException {
    	List<XMLElement> xmlElements = new ArrayList<XMLElement>();

    	MIFClass mifClass =null;
    	if (mifAssociation.getMifClass()!= null) {
    		mifClass =  mifAssociation.getMifClass();
    	}
    	if (mifClass == null) {
    		throw new MappingException("There is an error in your .h3s file, " + mifAssociation.getXmlPath() + " does not have specification", null);
    	}
    	MutableFlag mutableFlag = new MutableFlag(false);
    	MutableFlag mutableFlagDefault = new MutableFlag(true);
    	String holdXmlpath=mifAssociation.getXmlPath();
    	List<XMLElement> xmlEments = processMIFclass(mifClass,holdXmlpath,csvSegment, forceGenerate, mutableFlag, mutableFlagDefault, isCardinalityRelaxed);
    	if (mutableFlag.hasUserMappedData())
    	{
    		hasUserdata.setHasUserMappedData(true);
    	}
    	if (forceGenerate) {
    		if (!mutableFlagDefault.hasUserMappedData())
    		{
    			hasDefaultdata.setHasUserMappedData(false);
    		}
    	}
    	for(XMLElement xmlElement:xmlEments) {
    		if (mifAssociation.isChoiceSelected())
    			xmlElement.setName(mifAssociation.findChoiceSelectedMifClass().getTraversalName());
    		else
    			xmlElement.setName(mifAssociation.getName());
    		xmlElements.add(xmlElement);
    	}
    	if (hasUserdata.hasUserMappedData())
    		return xmlElements;
    	else
    	{
    		if (forceGenerate&&!choiceFlag) {
    			return xmlElements;
    		}
    		else {
    			if (choiceFlag)
    			{
    				for (XMLElement xElement:xmlElements)
    					choiceXmlElements.add(xElement);
    			}
    			return new ArrayList<XMLElement>();
    		}
    	}
    }

	/**
	 * This method will process a MIFAttribute object and generate a list of HL7 v3 message objects
	 *
	 * @param mifAttribute the MIFAttribute object that will be processed
	 * @param pCsvSegment CSV segments that determines the root segments that dominate the cardinality
	 * 		  and data for all Datatypes of the MIFAttribute
	 */
    private List<XMLElement> processAttribute(MIFAttribute mifAttribute, CSVSegment csvSegment, MutableFlag hasUserdata, MutableFlag hasDefaultdata) throws MappingException,FunctionException{

    	boolean forceGenerate = mifAttribute.getMinimumMultiplicity() > 0;
    	if (mifAttribute.getDatatype() == null) return NullXMLElement.NULL; //Abstract attrbiute

    	List<XMLElement> xmlElements = new ArrayList<XMLElement>();
    	//use the concrete Datatype if the original type is Abstract
    	Datatype mifDt=mifAttribute.getConcreteDatatype();
		if (mifDt==null)
			mifDt=mifAttribute.getDatatype();

    	//No mappings
    	if (mifAttribute.getCsvSegments().size()== 0) {
    		if (mifAttribute.isMandatory()) {
//	    	    MutableFlag mutableFlag = new MutableFlag(false);
    			XMLElement defaultXMLElement = datatypeProcessor.process_default_datatype(mifDt, mifAttribute.getParentXmlPath()+"."+mifAttribute.getNodeXmlName(),mifAttribute.getName());
    			if (defaultXMLElement != null)
    			{
    	            Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"No mapping  is available for the required attribute: " + mifAttribute.getXmlPath()});
    	            ValidatorResults validatorResults = new ValidatorResults();
    	            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
    	            defaultXMLElement.setValidatorResults(validatorResults);
    				xmlElements.add(defaultXMLElement);
    			}
    			else {
    				defaultXMLElement = new XMLElement();
    				defaultXMLElement.setName(mifAttribute.getName());

    				Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"No mapping  is available for the required attribute: " + mifAttribute.getXmlPath()});
    	            ValidatorResults validatorResults = new ValidatorResults();
    	            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
    	            defaultXMLElement.setValidatorResults(validatorResults);

    	            xmlElements.add(defaultXMLElement);
    			}
    			return xmlElements;
   			}
    		else {
    			if (forceGenerate) {
//    	    	    MutableFlag mutableFlag = new MutableFlag(false);
        			XMLElement defaultXMLElement = datatypeProcessor.process_default_datatype(mifDt, mifAttribute.getParentXmlPath()+"."+mifAttribute.getNodeXmlName(),mifAttribute.getName());
        			if (defaultXMLElement != null)
        			{
        				Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"No mapping  is available for the required attribute: " + mifAttribute.getXmlPath()});
        	            ValidatorResults validatorResults = new ValidatorResults();
        	            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
        	            defaultXMLElement.setValidatorResults(validatorResults);

        	            xmlElements.add(defaultXMLElement);
        				return xmlElements;
        			}
        			//Default datatype elements always generate at least an empty element
        			return NullXMLElement.NULL;
    			}
    			return NullXMLElement.NULL;
    		}
    	}

    	if (forceGenerate)
    	{
    	    MutableFlag mutableFlag = new MutableFlag(false);
    	    MutableFlag mutableFlagDefault = new MutableFlag(true);
    		xmlElements  = 	datatypeProcessor.process_datatype(mifDt, csvSegment, mifAttribute.getParentXmlPath()+"."+mifAttribute.getNodeXmlName(),mifAttribute.getName(), true, mutableFlag, mutableFlagDefault);
    		if (mutableFlag.hasUserMappedData())
    		{
    			hasUserdata.setHasUserMappedData(true);
    		}
    		if (!mutableFlagDefault.hasUserMappedData())
    		{
    			hasDefaultdata.setHasUserMappedData(false);
    		}
    	}
    	else
    	{
    	    MutableFlag mutableFlag = new MutableFlag(false);
    	    MutableFlag mutableFlagDefault = new MutableFlag(true);
    		xmlElements  = 	datatypeProcessor.process_datatype(mifDt, csvSegment, mifAttribute.getParentXmlPath()+"."+mifAttribute.getNodeXmlName(),mifAttribute.getName(), false, mutableFlag, mutableFlagDefault);
    		if (mutableFlag.hasUserMappedData())
    		{
    			hasUserdata.setHasUserMappedData(true);
    		}
    		else {
    			return new ArrayList<XMLElement>();
    		}
    	}

		for(XMLElement xmlElement:xmlElements)
		{
//			xmlElement.addAttribute("xsi:type", mifDt.getName(), null, null, null);
			if (mifAttribute.getDomainName()!= null && !mifAttribute.getDomainName().equals(""))
	    	{
				xmlElement.setDomainName(mifAttribute.getDomainName());
	    	}
			if (mifAttribute.getCodingStrength()!= null && !mifAttribute.getCodingStrength().equals(""))
			{
				xmlElement.setCodingStrength(mifAttribute.getCodingStrength());
			}
		}
    	return xmlElements;
    }

    private void process_default_empty_choice(MIFAssociation mifAssociation, XMLElement xmlElement, List<List<XMLElement>> choiceHolder, List<MutableFlag> choiceFlag, String choiceString)
    {
    	if (mifAssociation.getMinimumMultiplicity() == 0)
    	{
    		//No action is needed
    	}
    	else
    	{
    		boolean hasDefault = false;
    		for(MutableFlag mf:choiceFlag)
    		{
    			if (mf.hasUserMappedData()) hasDefault = true;
    		}
    		if (hasDefault)
    		{
    			if (mifAssociation.getMaximumMultiplicity() == 1) {
    				for (int i=0;i<choiceFlag.size();i++) {
    					if (choiceFlag.get(i).hasUserMappedData())
    					{
    						List<XMLElement> tempXmlElements = choiceHolder.get(i);
    						xmlElement.addChildren(tempXmlElements);
    						Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"The choice " +mifAssociation.getParentXmlPath()+"."+mifAssociation.getNodeXmlName()+"."+ choiceString + " does not have user data, default data is used instead."});
    						if (mifAssociation.getMinimumMultiplicity()==1)
    							theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));
    						else
    							theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.INFO, msg));
    						break;
    					}
    				}
    			}
    			else {
    				for (int i=0;i<choiceFlag.size();i++) {
    					if (choiceFlag.get(i).hasUserMappedData())
    					{
    						List<XMLElement> tempXmlElements = choiceHolder.get(i);
    						xmlElement.addChildren(tempXmlElements);
    					}
    				}
    				Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"The choice " +mifAssociation.getParentXmlPath()+"."+mifAssociation.getNodeXmlName()+"."+ choiceString + " does not have user data, default data is used instead."});
					if (mifAssociation.getMinimumMultiplicity()==1)
						theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));
					else
						theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.INFO, msg));
    			}
    		}
    		else // No default data
    		{
    			List<XMLElement> tempXmlElements = choiceHolder.get(0);
    			xmlElement.addChildren(tempXmlElements);
    			Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"The choice " +mifAssociation.getParentXmlPath()+"."+mifAssociation.getNodeXmlName()+"."+ choiceString + " does not have user data and default data, an empty element is generated instead."});
    			theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
    		}
		}
    }
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.50  2009/02/13 19:51:15  wangeug
 * HISTORY      : process association only if only if it come from current mifClass instance or from the association holder MIIFClass for choice item; avoid the association from other reference.
 * HISTORY      :
 * HISTORY      : Revision 1.49  2009/02/10 05:07:12  umkis
 * HISTORY      : Change message EMP_IN to XML4 and add message XML5
 * HISTORY      :
 * HISTORY      : Revision 1.48  2009/01/07 15:24:03  wangeug
 * HISTORY      : Use getSortedChoice() to include all subclass for choice item if it is an abstract class
 * HISTORY      :
 * HISTORY      : Revision 1.47  2008/12/04 20:42:36  wangeug
 * HISTORY      : support nullFlavor
 * HISTORY      :
 * HISTORY      : Revision 1.46  2008/11/21 16:19:37  wangeug
 * HISTORY      : Move back to HL7 module from common module
 * HISTORY      :
 * HISTORY      : Revision 1.45  2008/11/17 20:10:07  wangeug
 * HISTORY      : Move FunctionComponent and VocabularyMap from HL7 module to common module
 * HISTORY      :
 * HISTORY      : Revision 1.44  2008/06/09 19:53:50  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.43  2007/11/02 19:56:50  wangeug
 * HISTORY      : relax cardinality validation for choice
 * HISTORY      :
 * HISTORY      : Revision 1.42  2007/11/02 14:16:47  wangeug
 * HISTORY      : fix error:check cardinality; modify warning message
 * HISTORY      :
 * HISTORY      : Revision 1.41  2007/11/01 16:55:59  wangeug
 * HISTORY      : retrieve the concrete datatype if the datatype associated with a MIFAttribute is Abstract
 * HISTORY      :
 * HISTORY      : Revision 1.40  2007/10/31 20:37:08  wangeug
 * HISTORY      : Use user's data for structual MIFAttribute:
 * HISTORY      : 1. Use mapped source data if available
 * HISTORY      : 2. Use user's default value if available
 * HISTORY      : 3. Use HL7 default value (get from MIF spec and set with menomic value)
 * HISTORY      : 4. Use Fixed value
 * HISTORY      :
 * HISTORY      : Revision 1.39  2007/10/04 04:32:55  umkis
 * HISTORY      : Removed the problem when mifAttribute.getDatatype() is null.
 * HISTORY      :
 * HISTORY      : Revision 1.38  2007/09/11 02:16:36  wuye
 * HISTORY      : Remove extra tag
 * HISTORY      :
 * HISTORY      : Revision 1.37  2007/09/10 13:35:46  wuye
 * HISTORY      : Fixed the issue with a mifclass has both choices and other associations
 * HISTORY      :
 * HISTORY      : Revision 1.36  2007/09/06 15:08:58  wangeug
 * HISTORY      : refine codes
 * HISTORY      :
 * HISTORY      : Revision 1.35  2007/09/04 14:07:19  wuye
 * HISTORY      : Added progress bar
 * HISTORY      :
 * HISTORY      : Revision 1.34  2007/08/31 17:01:54  wuye
 * HISTORY      : Added mapping scenario when a constant is mapped to a structural attribute
 * HISTORY      :
 * HISTORY      : Revision 1.33  2007/08/30 21:56:57  wuye
 * HISTORY      : code refector
 * HISTORY      :
 * HISTORY      : Revision 1.32  2007/08/30 04:51:36  wuye
 * HISTORY      : added mapping to structural attribute capability
 * HISTORY      :
 * HISTORY      : Revision 1.31  2007/08/29 23:10:18  wuye
 * HISTORY      : Fixed an issue with mapping to structural attribute
 * HISTORY      :
 * HISTORY      : Revision 1.30  2007/08/29 17:55:10  wuye
 * HISTORY      : Code refactor
 * HISTORY      :
 * HISTORY      : Revision 1.29  2007/08/29 17:37:09  wuye
 * HISTORY      : Added comments block for packlocation info
 * HISTORY      :
 * HISTORY      : Revision 1.28  2007/08/29 15:47:52  wuye
 * HISTORY      : complete choice(0..1, 1..1) generation
 * HISTORY      :
 * HISTORY      : Revision 1.27  2007/08/29 05:50:41  wuye
 * HISTORY      : Added default value handling for choice
 * HISTORY      :
 * HISTORY      : Revision 1.26  2007/08/29 00:13:00  wuye
 * HISTORY      : Modified the default value generation strategy
 * HISTORY      :
 * HISTORY      : Revision 1.25  2007/08/27 20:49:32  umkis
 * HISTORY      : fix the Bug of infinite looping when choice included HL7 transformation
 * HISTORY      :
 * HISTORY      : Revision 1.24  2007/08/27 18:57:38  wuye
 * HISTORY      : Added validation message for error situations
 * HISTORY      :
 * HISTORY      : Revision 1.23  2007/08/24 21:14:28  wangeug
 * HISTORY      : set message type to XMLElement
 * HISTORY      :
 * HISTORY      : Revision 1.22  2007/08/23 20:31:25  wuye
 * HISTORY      : Added code to check empty csvField
 * HISTORY      :
 * HISTORY      : Revision 1.21  2007/08/20 20:40:19  wangeug
 * HISTORY      : process choice; fix bug of indefinite loop
 * HISTORY      :
 * HISTORY      : Revision 1.20  2007/08/13 20:16:02  wuye
 * HISTORY      : fixed the extra xml elements
 * HISTORY      :
 * HISTORY      : Revision 1.19  2007/08/13 19:21:05  wuye
 * HISTORY      : remove extra element when error occured
 * HISTORY      :
 * HISTORY      : Revision 1.18  2007/08/09 21:40:43  wuye
 * HISTORY      : Complete voc validation
 * HISTORY      :
 * HISTORY      : Revision 1.17  2007/08/08 22:01:39  wuye
 * HISTORY      : Added force generate capability
 * HISTORY      :
 * HISTORY      : Revision 1.16  2007/08/07 22:28:39  wuye
 * HISTORY      : Fixed choice with 0..1 problem.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2007/08/07 05:38:34  wuye
 * HISTORY      : Fixed issues with FindCSVField (null pointer exception)
 * HISTORY      :
 * HISTORY      : Revision 1.14  2007/08/07 03:45:59  wuye
 * HISTORY      : Fixed the structural attribute issue
 * HISTORY      :
 * HISTORY      : Revision 1.13  2007/08/07 03:19:21  wuye
 * HISTORY      : Fix the a bug where if there is only constant mapping
 * HISTORY      :
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
