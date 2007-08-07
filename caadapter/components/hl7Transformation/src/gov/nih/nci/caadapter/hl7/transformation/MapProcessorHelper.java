/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.hl7.transformation;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.csv.data.CSVField;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.function.FunctionConstant;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.function.meta.ParameterMeta;
import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;
import gov.nih.nci.caadapter.hl7.map.MappingException;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * This class assists the MapProcessor with alot of the buisness logic.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wuye $
 * @version $Revision: 1.9 $
 * @date $Date: 2007-08-07 03:16:41 $
 * @since caAdapter v4.0
 */
public class MapProcessorHelper {
    /**
     * Used for preprocess the MIFClass
     *
     */
	private String rootCSVName="";
	
	private Hashtable<String,String> mappings;

    private Hashtable<String, FunctionComponent> functions = new Hashtable<String, FunctionComponent>();
	
	private Hashtable <String, List<CSVField>> csvFieldHash= new Hashtable <String, List<CSVField>>();

	private Hashtable <String, List<CSVSegment>> csvSegmentHash= new Hashtable <String, List<CSVSegment>>();
	
    protected void preprocessMIF(Hashtable<String,String> mappings, Hashtable<String, FunctionComponent> functions, MIFClass mifClass, boolean isChoice, String rootCSVName) {
    	this.mappings = mappings;
    	this.functions = functions;
    	this.rootCSVName = rootCSVName;
    	preprocess_mifclass(mifClass, isChoice, mifClass.getName());
    	
    }
    protected List<String> preprocess_mifclass(MIFClass mifClass, boolean isChoice, String xmlPath) {
		String commonP = "";

//		Log.logInfo(this, "Pre process mifClass:"+mifClass.getName());
    	if (isChoice && !mifClass.isChoiceSelected()) return new ArrayList<String>();

    	if (mifClass.getChoices().size()>0) { //it's a choice class,

    		HashSet<MIFClass> choices = mifClass.getChoices();

    		List<String> csvSegments = new ArrayList();
    		
    		for(MIFClass mifClassChoice : choices) {
    			if (mifClassChoice.isChoiceSelected()) {
    				combine(csvSegments,preprocess_mifclass(mifClassChoice, true, xmlPath));
    			}
        	}
    		//This does not matter
    		mifClass.setMapped(false);
    		//Something is wrong here, because no choice is selected
    		mifClass.setCsvSegments(csvSegments);
    		commonP = findCommonParent(mifClass.getCsvSegments());
    		
    		String conceptualMapping = mappings.get(xmlPath);
    		
    		if (conceptualMapping == null) {    		
    			mifClass.setCsvSegment(commonP);
    		}
    		else {
    			if (commonP.contains(conceptualMapping)) mifClass.setCsvSegment(conceptualMapping);
    			else {
        			mifClass.setCsvSegment(commonP);
        			/*
        			 * Add a warning message
        			 */
    			}
    		}
    		return csvSegments;
    	}
    	else {
        	HashSet<MIFAttribute> attributes = mifClass.getAttributes();
        	
        	List<String> csvSegments = new ArrayList<String>();
        	for(MIFAttribute mifAttribute:attributes) {
//        		Log.logInfo(this,"preprocess Attribute:"+mifAttribute.getName());
        		combine(csvSegments,preprocess_attribute(mifAttribute));
        	}
//        	if (csvSegments.size()>0)
//        	{
//        		mifClass.setMapped(true);
//        	}
//        	else 
//        	{
//        		mifClass.setMapped(false);
//        	}

        	
        	commonP = findCommonParent(csvSegments);
        	
        	HashSet<MIFAssociation> associations = mifClass.getAssociations();
        	
        	for(MIFAssociation mifAssociation : associations) {
        		List<String> csvAssocSegments = preprocess_association(mifAssociation);
        		commonP = findCommonParent(commonP, csvAssocSegments);

        	
				for(String newcsvSegment:csvAssocSegments) {
					if (csvSegments.size() == 0) {
						csvSegments.add(newcsvSegment);
					}
					else {
						boolean add = true;
						for (int i = csvSegments.size()-1; i>=0; i--) {
							String csvSegment = csvSegments.get(i);
							if (csvSegment.contains(newcsvSegment)) {
								add = false;
								break;
							}
							if (newcsvSegment.contains(csvSegment)) {
								csvSegments.remove(i);
								continue;
							}
						}
						if (add) csvSegments.add(newcsvSegment);
					}
				}        	
        	}
        	String conceptualMapping = mappings.get(xmlPath);
//        	System.out.println(mifClass.getParentXmlPath());
    		if (conceptualMapping == null) {    		
    			mifClass.setCsvSegment(commonP);
    		}
    		else {
    			if (commonP == "") 
    			{
    				mifClass.setCsvSegment(conceptualMapping);
            		mifClass.setMapped(true);
    			}
    			else {
    				for(String csvSegment:csvSegments) {
    					boolean mapFlag = true;
    					if (!(csvSegment.contains(conceptualMapping))&&conceptualMapping.contains(csvSegment)) {
    						mapFlag = false;
        					mifClass.setCsvSegment(commonP);
        					/*
        					 * Add a warning message
        					 */
    					}
        				if (mapFlag) {
            				mifClass.setCsvSegment(conceptualMapping);
                    		mifClass.setMapped(true);
        				}
    				}
    			}
    		}

        	mifClass.setCsvSegments(csvSegments);
    		
    		return mifClass.getCsvSegments();
    	}
    }
    protected List<String> preprocess_function(String scsXmlPath) {
    	List<String> strings = new ArrayList<String>();
    	int pos = scsXmlPath.lastIndexOf(".outputs.");
//    	System.out.println("--"+scsXmlPath);
    	String fXmlPath = scsXmlPath.substring(0,pos);
    	FunctionComponent functionComponent = functions.get(fXmlPath);
    	if (functionComponent == null) return strings;

    	FunctionMeta functionMeta = functionComponent.getMeta();

    	// first, is the function a constant?
    	if (functionMeta.isConstantFunction()) {
    		strings.add(rootCSVName);
    		return strings;
    	}

    	List<ParameterMeta> inputParameterMetas = functionMeta.getInputDefinitionList();

    	// for each input find it's input value.
    	for (int i = 0; i < functionMeta.getInputDefinitionList().size(); i++) {
    		ParameterMeta parameterMeta = inputParameterMetas.get(i);
    		String inputvalue = null;

    		String inputData = mappings.get("function."+functionComponent.getId()+"."+"inputs"+"."+i);
    		if (inputData.startsWith("function.")) { //function mapping to target
    			strings.addAll(preprocess_function(inputData));
    		}
    		else { //direct mapping from source to target
    			strings.add(inputData.substring(0, inputData.lastIndexOf('.')));
    		}
    	}
    	return strings;
    }
    protected List<String> preprocess_datatype(Datatype datatype, String parentXPath) {
    	
    	if (!datatype.isEnabled()) return new ArrayList<String>();
    	
    	List<String> csvSegments = new ArrayList();
    	
    	if (datatype.isSimple()) return csvSegments;
    	
    	for(String attributeName:(Set<String>)(datatype.getAttributes().keySet())) {
    		
    		Attribute attr = (Attribute)datatype.getAttributes().get(attributeName);
    		boolean isSimple = false;
    		
    		String datatypeattribute = parentXPath+"."+attr.getNodeXmlName();
//    		Log.logWarning(this, "Current datatype string: " + datatypeattribute);
//    		System.out.println("Current datatype string: " + datatypeattribute);
    		if (attr.getReferenceDatatype() == null) {
    			isSimple = true;
    		}
    		else {
    			if (attr.getReferenceDatatype().isSimple()) isSimple = true;
    		}
    		if (isSimple) {
    			String newcsvField = mappings.get(parentXPath+"."+attributeName);
    			if (newcsvField!=null) {
//    				Log.logDebug(this, "Pre process Datatyep Attribute:"+parentXPath+"."+attributeName + "--> target csv element: " + newcsvField);
    				List<String> strings = new ArrayList<String>();
    				boolean isFuncation = true;
    				if (newcsvField.startsWith("function.")) {
    					strings.addAll(preprocess_function(newcsvField));
    					
    				}
    				else {
    					strings.add(newcsvField.substring(0, newcsvField.lastIndexOf('.')));
    				}

    				for(String newcsvSegment:strings) {
    					if (csvSegments.size() == 0) {
    						csvSegments.add(newcsvSegment);
    					}
    					else {
    						boolean add = true;
    						for (int i = csvSegments.size()-1; i>=0; i--) {
    							String csvSegment = csvSegments.get(i);
    							if (csvSegment.contains(newcsvSegment)) {
    								add = false;
    								break;
    							}
    							if (newcsvSegment.contains(csvSegment)) {
    								csvSegments.remove(i);
    								continue;
    							}
    						}
    						if (add) csvSegments.add(newcsvSegment);
    					}
    				}
    			}
    		}//End is Simple
    		else {//is complex
    			List<String> strings = preprocess_datatype(attr.getReferenceDatatype(), parentXPath+"."+attributeName);
				for(String newcsvSegment:strings) {
					if (csvSegments.size() == 0) {
						csvSegments.add(newcsvSegment);
					}
					else {
						boolean add = true;
						for (int i = csvSegments.size()-1; i>=0; i--) {
							String csvSegment = csvSegments.get(i);
							if (csvSegment.contains(newcsvSegment)) {
								add = false;
								break;
							}
							if (newcsvSegment.contains(csvSegment)) {
								csvSegments.remove(i);
								continue;
							}
						}
						if (add) csvSegments.add(newcsvSegment);
					}
				}
    		}
    	}
    	datatype.setCsvSegments(csvSegments);
    	datatype.setCsvSegment(findCommonParent(csvSegments));
//    	System.out.println("--------process dt:"+datatype.getName()+ "csvSgement:"+datatype.getCsvSegment());
    	return csvSegments;
    }
    protected List<String> preprocess_attribute(MIFAttribute mifAttribute) {
    	if (mifAttribute.getDatatype() == null) return new ArrayList<String>(); //Abstract attrbiute
//    	System.out.println("------------------Attribute name" + mifAttribute.getNodeXmlName());
    	mifAttribute.setCsvSegments(preprocess_datatype(mifAttribute.getDatatype(),mifAttribute.getParentXmlPath()+"."+mifAttribute.getNodeXmlName()));
//    	System.out.println("Attribute name" + mifAttribute.getNodeXmlName() + "size" + mifAttribute.getCsvSegments().size());
    	if (mifAttribute.getCsvSegments().size() > 0) mifAttribute.setMapped(true); else mifAttribute.setMapped(false);

		String commonP = findCommonParent(mifAttribute.getCsvSegments());
    	String conceptualMapping = mappings.get(mifAttribute.getXmlPath());
		
		if (conceptualMapping == null) {    		
			mifAttribute.setCsvSegment(commonP);
		}
		else {
			if (commonP.contains(conceptualMapping)) mifAttribute.setCsvSegment(conceptualMapping);
			else {
				mifAttribute.setCsvSegment(commonP);
    			/*
    			 * Add a warning message
    			 */
			}
		}

    	
    	mifAttribute.setCsvSegment(findCommonParent(mifAttribute.getCsvSegments()));
    	return mifAttribute.getCsvSegments();
    }
    
    protected List<String> preprocess_association(MIFAssociation mifAssociation) {
    	if (mifAssociation.getMifClass()!= null) {
//    		System.out.println("mifassociation"+mifAssociation.getName());
    		mifAssociation.setCsvSegments(preprocess_mifclass(mifAssociation.getMifClass(),false, mifAssociation.getXmlPath()));
    		if (mifAssociation.getCsvSegments().size() >0) mifAssociation.setMapped(true); else mifAssociation.setMapped(false);

  		
    		String commonP = findCommonParent(mifAssociation.getCsvSegments());
        	String conceptualMapping = mappings.get(mifAssociation.getXmlPath());
    		
    		if (conceptualMapping == null) {    		
    			mifAssociation.setCsvSegment(commonP);
    		}
    		else {
    			if (commonP.contains(conceptualMapping)) mifAssociation.setCsvSegment(conceptualMapping);
    			else {
    				mifAssociation.setCsvSegment(commonP);
        			/*
        			 * Add a warning message
        			 */
    			}
    		}
    		
    		return mifAssociation.getCsvSegments();
    	}
    	return new ArrayList<String>();
    }
    
    protected void combine(List<String> csvSegments,List<String> newCsvSegments) {
    	if (newCsvSegments.size() == 0) return;
    	
    	if (csvSegments.size() == 0) {
    		for(String s:newCsvSegments) {
    			csvSegments.add(s);
    		}
    	}
    	else {
    		for(int j=newCsvSegments.size()-1; j>=0;j--) {
				boolean add = true;
				String newcsvSegment = newCsvSegments.get(j);
				for (int i = csvSegments.size()-1; i>=0; i--) {
					String csvSegment = csvSegments.get(i);
					if (csvSegment.contains(newcsvSegment)) {
						add = false;
						break;
					}
					if (newcsvSegment.contains(csvSegment)) {
						csvSegments.remove(i);
						continue;
					}
				}
				if (add) csvSegments.add(newcsvSegment);
    		}
    	}
    }

    protected String findCommonParent(List<String> csvSegments) {
    	if (csvSegments == null) return "";
    	if (csvSegments.size() == 1) return csvSegments.get(0);
    	if (csvSegments.size() == 0) return "";
    	
    	String commonP = csvSegments.get(0);
    	
    	for(int i=1;i<csvSegments.size();i++) {
    		String current = csvSegments.get(i);
    		int len;
    		if (current.length() > commonP.length()) len =commonP.length(); else len = current.length();
    		int loc = 0;
    		for (int k = 0; k<len; k++) {
    			if (current.charAt(k) != commonP.charAt(k)) {
    				if (k <1) return "";
    				commonP = commonP.substring(0, loc);
    				break;
    			}
    			if (current.charAt(k) == '.') loc = k;
    		}
    	}
    	
    	return commonP;
    }

    protected String findCommonParent(String commonP, List<String> csvSegments) {
    	if (csvSegments == null) return commonP;
    	if (csvSegments.size() == 0) return commonP;
    	int loopIndex = 0;
    	if (commonP.equals(""))
    	{
    		commonP = csvSegments.get(0);
    		loopIndex = 1;
    	}
    	for(int i=loopIndex;i<csvSegments.size();i++) {
    		String current = csvSegments.get(i);
    		int len;
    		if (current.length() > commonP.length()) len =commonP.length(); else len = current.length();
    		int loc = 0;
    		for (int k = 0; k<len; k++) {
    			if (current.charAt(k) != commonP.charAt(k)) {
    				if (k <1) return "";
    				commonP = commonP.substring(0, loc);
    				break;
    			}
    			if (current.charAt(k) == '.') loc = k;
    		}
    	}
    	
    	return commonP;
    }
    
    protected Hashtable <String, List<CSVSegment>> preprocessCSVSegments(CSVSegment csvSegment) {
    	preprocess_CSVSegments(csvSegment);
    	return csvSegmentHash;
    }

    protected void preprocess_CSVSegments(CSVSegment csvSegment) {
    	List<CSVSegment> csvList = csvSegmentHash.get(csvSegment.getXmlPath()); 
    	if (csvList == null) {
    		ArrayList<CSVSegment> newcsvList = new ArrayList<CSVSegment>();
    		newcsvList.add(csvSegment);
    	}
    	else {
    		csvList.add(csvSegment);
    	}
    	for(CSVSegment childS:csvSegment.getChildSegments()) {
    		preprocess_CSVSegments(childS);
    	}
    }

    protected Hashtable <String, List<CSVField>> preprocessCSVField(CSVSegment csvSegment) {
    	preprocess_CSVFields(csvSegment);
    	return csvFieldHash;
    }

    protected void preprocess_CSVFields(CSVSegment csvSegment) {
    	for(CSVField csvField : csvSegment.getFields()) {
    		List<CSVField> csvList = csvFieldHash.get(csvField.getXmlPath()); 
    		if (csvList == null) {
    			ArrayList<CSVField> newcsvList = new ArrayList<CSVField>();
    			newcsvList.add(csvField);
    		}
    		else {
    			csvList.add(csvField);
    		}
    	}
    	for(CSVSegment childS:csvSegment.getChildSegments()) {
    		preprocess_CSVSegments(childS);
    	}
    }
}
