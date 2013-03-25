/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.hl7.transformation;

import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.transformation.data.MutableFlag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * This class assists the MapProcessor with alot of the buisness logic.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.22 $
 * @date $Date: 2009-01-09 21:57:32 $
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

	/*
	 * NOTE!!! setMapped(true) will only set the current MIFCLass
	 */
    protected void preprocessMIF(Hashtable<String,String> mappings, Hashtable<String, FunctionComponent> functions, MIFClass mifClass, boolean isChoice, String rootCSVName) {
    	this.mappings = mappings;
    	this.functions = functions;
    	this.rootCSVName = rootCSVName;
    	preprocess_mifclass(mifClass, isChoice, mifClass.getName());

    }
    protected List<String> preprocess_mifclass(MIFClass mifClass, boolean isChoice, String xmlPath) {
		String commonP = "";

		List<String> csvSegments = new ArrayList<String>();

    	if (isChoice && !mifClass.isChoiceSelected()) return new ArrayList<String>();

    	if (mifClass.getSortedChoices().size()>0) { //it's a choice class,
    		for(MIFClass mifClassChoice : mifClass.getSortedChoices()) {
    			if (mifClassChoice.isChoiceSelected()) {
    				combine(csvSegments,preprocess_mifclass(mifClassChoice, true, xmlPath));
    			}
        	}
    	}
    	//This does not matter
    	mifClass.setMapped(false);
    	//Something is wrong here, because no choice is selected
    	mifClass.setCsvSegments(csvSegments);
    	commonP = findCommonParent(mifClass.getCsvSegments());

    	HashSet<MIFAttribute> attributes = mifClass.getAttributes();

    	MutableFlag structuralAttributeHasMapping = new MutableFlag(false);
    	for(MIFAttribute mifAttribute:attributes) {
    		combine(csvSegments,preprocess_attribute(mifAttribute, structuralAttributeHasMapping));
    	}


    	commonP = findCommonParent(csvSegments);

    	if (structuralAttributeHasMapping.hasUserMappedData())
    		mifClass.setMapped(true);

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
    	if (conceptualMapping == null) {
    		mifClass.setCsvSegment(commonP);
    	}
    	else {
    		if (commonP.equals(""))
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
    protected List<String> preprocess_function(String scsXmlPath) {
    	List<String> strings = new ArrayList<String>();
    	int pos = scsXmlPath.lastIndexOf(".outputs.");
    	String fXmlPath = scsXmlPath.substring(0,pos);
    	FunctionComponent functionComponent = functions.get(fXmlPath);
    	if (functionComponent == null) return strings;

    	FunctionMeta functionMeta = functionComponent.getMeta();

    	// first, is the function a constant?
    	if (functionMeta.isConstantFunction()) {
    		strings.add(rootCSVName);
    		return strings;
    	}

    	// for each input find it's input value.
    	for (int i = 0; i < functionMeta.getInputDefinitionList().size(); i++) {
    		String inputData = mappings.get("function."+functionComponent.getId()+"."+"inputs"+"."+i);
    		if (inputData==null)
    			strings.add(null);
    		else if (inputData.startsWith("function.")) { //function mapping to target
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
    	List<String> csvSegments = new ArrayList<String>();
    	if (datatype.isSimple()) return csvSegments;
    	for(String attributeName:(Set<String>)(datatype.getAttributes().keySet())) {
    		Attribute attr = (Attribute)datatype.getAttributes().get(attributeName);
    		boolean isSimple = false;

    		if (attr.getReferenceDatatype() == null) {
    			isSimple = true;
    		}
    		else {
    			if (attr.getReferenceDatatype().isSimple()) isSimple = true;
    		}
    		if (isSimple) {
    			String newcsvField = mappings.get(parentXPath+"."+attributeName);
    			if (newcsvField!=null) {
    				List<String> strings = new ArrayList<String>();
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
    					else if (newcsvSegment==null)
    						csvSegments.add(newcsvSegment);
    					else {
    						boolean add = true;
    						for (int i = csvSegments.size()-1; i>=0; i--) {
    							String csvSegment = csvSegments.get(i);
    	    					if (csvSegment==null||newcsvSegment==null)
    	    					{
    								System.out
    										.println("MapProcessorHelper.preprocess_datatype()..is Simple..found null__mapping:"+newcsvField);
    								add = false;
    								continue;
    	    					}
    	    					else if (csvSegment.contains(newcsvSegment)) {
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
							if (csvSegment==null||newcsvSegment==null)
	    					{
								System.out
										.println("MapProcessorHelper.preprocess_datatype()..is complex..found null");
								add = false;
								continue;
	    					}
	    					else if (csvSegment.contains(newcsvSegment)) {
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
//    	datatype.setCsvSegment(findCommonParent(csvSegments));
//    	System.out.println("--------process dt:"+datatype.getName()+ "csvSgement:"+datatype.getCsvSegment());
    	return csvSegments;
    }
    protected List<String> preprocess_structural_datatype(String parentXPath, MutableFlag structuralAttributeHasMapping) {

    	List<String> csvSegments = new ArrayList<String>();

    	String newcsvField = mappings.get(parentXPath);
    	if (newcsvField!=null) {
    		List<String> strings = new ArrayList<String>();
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
    					if (csvSegment==null||newcsvSegment==null)
    					{
							System.out
									.println("MapProcessorHelper.preprocess_datatype()..found null");
							add = false;
							continue;
    					}
    					else if (csvSegment.contains(newcsvSegment)) {
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
    	if (csvSegments.size()>0)  structuralAttributeHasMapping.setHasUserMappedData(true);
    	return csvSegments;
    }
    protected List<String> preprocess_attribute(MIFAttribute mifAttribute, MutableFlag structuralAttributeHasMapping) {
    	if (mifAttribute.getDatatype() == null&&!mifAttribute.isStrutural()) return new ArrayList<String>(); //Abstract attrbiute
    	if (!mifAttribute.isStrutural())
    	{
    		Datatype mifDt=mifAttribute.getConcreteDatatype();
    		if (mifDt==null)
    			mifDt=mifAttribute.getDatatype();
    		mifAttribute.setCsvSegments(preprocess_datatype(mifDt,mifAttribute.getParentXmlPath()+"."+mifAttribute.getNodeXmlName()));
    	}
    	else
    	{
    		List<String> tempList = preprocess_structural_datatype(mifAttribute.getParentXmlPath()+"."+mifAttribute.getNodeXmlName(),structuralAttributeHasMapping);
    		mifAttribute.setCsvSegments(tempList);
    	}
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
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.21  2009/01/07 15:23:41  wangeug
 * HISTORY :Use getSortedChoice() to include all subclass for choice item if it is an abstract class
 * HISTORY :
 * HISTORY :Revision 1.20  2008/12/04 20:42:19  wangeug
 * HISTORY :support nullFlavor
 * HISTORY :
 * HISTORY :Revision 1.19  2008/11/21 16:19:37  wangeug
 * HISTORY :Move back to HL7 module from common module
 * HISTORY :
 * HISTORY :Revision 1.18  2008/11/17 20:10:07  wangeug
 * HISTORY :Move FunctionComponent and VocabularyMap from HL7 module to common module
 * HISTORY :
 * HISTORY :Revision 1.17  2008/09/29 15:40:38  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */