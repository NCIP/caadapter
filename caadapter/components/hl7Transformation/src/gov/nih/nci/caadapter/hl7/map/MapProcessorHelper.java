/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/MapProcessorHelper.java,v 1.4 2007-07-18 20:37:50 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.hl7.map;

import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.csv.data.CSVField;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.function.FunctionConstant;
import gov.nih.nci.caadapter.common.function.FunctionException;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.function.meta.ParameterMeta;
import gov.nih.nci.caadapter.common.map.BaseMapElement;
import gov.nih.nci.caadapter.common.util.Config;
//import gov.nih.nci.caadapter.hl7.clone.data.CloneAttributeData;
//import gov.nih.nci.caadapter.hl7.clone.data.CloneDatatypeFieldData;
//import gov.nih.nci.caadapter.hl7.clone.data.impl.CloneDatatypeFieldDataImpl;
//import gov.nih.nci.caadapter.hl7.clone.meta.CloneAttributeMeta;
//import gov.nih.nci.caadapter.hl7.clone.meta.CloneDatatypeFieldMeta;
//import gov.nih.nci.caadapter.hl7.clone.meta.CloneMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * This class assists the MapProcessor with alot of the buisness logic.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.4 $
 * @date $Date: 2007-07-18 20:37:50 $
 * @since caAdapter v1.2
 */
public class MapProcessorHelper {
    private static final String LOGID = "$RCSfile: MapProcessorHelper.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/MapProcessorHelper.java,v 1.4 2007-07-18 20:37:50 wangeug Exp $";
    private Mapping mapping;
    private HashMap targetPathHash;

    // The constructor.
    public MapProcessorHelper(Mapping userMapping) {
        mapping = userMapping;
        
       //keep map with target xmlPath
       targetPathHash=new HashMap();  
       for (Map oneMap:mapping.getMaps())
        {
        	 BaseMapElement targetElement=oneMap.getTargetMapElement();
        	 targetPathHash.put(targetElement.getDataXmlPath(),oneMap);
       }        
    }
     
   public String findSourceDataReferenceFromTargetPath(String targetFieldPath)
   {
	   Map targetMap=(Map)targetPathHash.get(targetFieldPath);
	   if (targetMap!=null)
	   {
		  BaseMapElement srcComp=targetMap.getSourceMapElement();
		  if (srcComp.isComponentOfSourceType())
			  return srcComp.getDataXmlPath();
		  else if (srcComp.isComponentOfFunctionType())
		  {
			  String functionCompID=srcComp.getDataXmlPath();//.getComponentuuid();
			  //the serDataUUID is the target path of mapping from
			  //csv Field source to Function
			  return findSourceDataReferenceFromFunctionRef(functionCompID);	
		  }
	   }   
	   return null;
   }
   
   private String findSourceDataReferenceFromFunctionRef(String functionID)
   {
	   boolean funtionBoxFound=false;
	   //the mapping file holds the unique instance of the
	   //same functionComponent for each target field.
	   //If the same function being used by different target fields
	   //it creates multiple instances of the same functionComponent
	   for (FunctionComponent fComp:mapping.getFunctionComponent())
	   {
		   if (fComp.getXmlPath().equals(functionID))
		   {
			   funtionBoxFound=true;
			   break;
		   }
	   }

	   if (!funtionBoxFound)
		   return null;
	   
	   //search the link from sourceCompToFunctionBox
	   String srcObjectId=null;
       for (Map oneMap:mapping.getMaps())
       {
    	   String linkTargetID=oneMap.getTargetMapElement().getDataXmlPath();//.getComponentuuid();
    	   if (linkTargetID.equals(functionID))
    	   {
    		   srcObjectId=oneMap.getSourceMapElement().getDataXmlPath(); 
    		   break;
    	   }
       }  
	   return srcObjectId;
   }
   

    /**
     * Find any maps directly to this clone.
     *
     * @param cloneMeta          The Clone
     * @param cloneComponentUuid The Clone Component UUID
     * @return a list of maps
     */
//    public List<Map> findMapsToThisClone(CloneMeta cloneMeta, String cloneComponentUuid) {
//        return findMapsAssociatedWithMetaObject(cloneComponentUuid, cloneMeta.getUUID(), Config.MAP_COMPONENT_TARGET_TYPE);
//    }

    /**
     * Find any maps directly to this attribute.
     *
     * @param cloneAttributeMeta The Attribute
     * @param cloneComponentUuid The Clone Component UUID
     * @return a list of maps
     */
//    public List<Map> findMapsToThisAttribute(CloneAttributeMeta cloneAttributeMeta, String cloneComponentUuid) {
//        return findMapsAssociatedWithMetaObject(cloneComponentUuid, cloneAttributeMeta.getUUID(), Config.MAP_COMPONENT_TARGET_TYPE);
//    }

    /**
     * Find all maps that are associated with this clone's datatypefields.
     *
     * @param cloneMeta The clone in question
     * @param recursive do you want to search in child clones?
     * @return a list of maps
     */
//    public List<Map> findMapsToThisClonesDatatypefields(CloneMeta cloneMeta, String cloneComponentUuid, boolean recursive) {
//        List<Map> mapsToClone = new ArrayList<Map>();
//        // find maps to it's datatypes.
//        List<CloneAttributeMeta> cloneAttributeMetas = cloneMeta.getAttributes();
//        for (int i = 0; i < cloneAttributeMetas.size(); i++) {
//            CloneAttributeMeta cloneAttributeMeta = cloneAttributeMetas.get(i);
//            mapsToClone.addAll(findMapsToThisAttributesDatatypefields(cloneAttributeMeta, cloneComponentUuid, true));
//        }
//        if (recursive) {
//            // find maps to it's child clones.
//            List<CloneMeta> cloneMetas = cloneMeta.getChildClones();
//            for (int i = 0; i < cloneMetas.size(); i++) {
//                mapsToClone.addAll(findMapsToThisClonesDatatypefields(cloneMetas.get(i), cloneComponentUuid, true));
//            }
//        }
//        return mapsToClone;
//    }

    /**
     * Find all maps to Datatypefields below this clone (this clone is not included).
     *
     * @param cloneMeta
     * @param cloneComponentUuid
     * @return a list.
     */
//    public List<Map> findMapsBelowThisClone(CloneMeta cloneMeta, String cloneComponentUuid) {
//        List<Map> mapsBelowClone = new ArrayList<Map>();
//        List<CloneMeta> childCloneMetas = cloneMeta.getChildClones();
//        for (int i = 0; i < childCloneMetas.size(); i++) {
//            CloneMeta childCloneMeta =  childCloneMetas.get(i);
//            mapsBelowClone.addAll(findMapsToThisClonesDatatypefields(childCloneMeta,cloneComponentUuid,true));
//        }
//        return mapsBelowClone;
//    }

    /**
     * Find all maps that are associated with this attribute's datatypefields.
     *
     * @param cloneAttributeMeta The attribute in question.
     * @param recursive          do you want to search in child attributes?
     * @return a list of maps
     */
//    public List<Map> findMapsToThisAttributesDatatypefields(CloneAttributeMeta cloneAttributeMeta, String cloneComponentUuid, boolean recursive) {
//        List<Map> datatypeMaps = new ArrayList<Map>();
//
//        List<CloneDatatypeFieldMeta> datatypeFields = cloneAttributeMeta.getDatatypeFields();
//        datatypeMaps = new ArrayList<Map>();
//        for (int i = 0; i < datatypeFields.size(); i++) {
//            CloneDatatypeFieldMeta cloneDatatypeFieldMeta = datatypeFields.get(i);
//            //List<Map> fieldMaps = findMapsAssociatedWithMetaObject(cloneDatatypeFieldMeta, Config.MAP_COMPONENT_TARGET_TYPE);
//            List<Map> fieldMaps = findMapsAssociatedWithMetaObject(cloneComponentUuid, cloneDatatypeFieldMeta.getUUID(), Config.MAP_COMPONENT_TARGET_TYPE);
//            datatypeMaps.addAll(fieldMaps);
//        }
//
//        if (recursive) {
//            List<CloneAttributeMeta> childAttributes = cloneAttributeMeta.getChildAttributes();
//            for (int i = 0; i < childAttributes.size(); i++) {
//                CloneAttributeMeta attributeMeta = childAttributes.get(i);
//                datatypeMaps.addAll(findMapsToThisAttributesDatatypefields(attributeMeta, cloneComponentUuid, true));
//            }
//        }
//        return datatypeMaps;
//    }

    /**
     * Find all maps that are associated with this meta object.
     *
     * @param componentType do you want to search the "target" or "source"?
     * @return a list of maps
     * @see Config
     */
    public List<Map> findMapsAssociatedWithMetaObject(String cmpUuid, String metaUuid, String componentType) {
        List<Map> foundMaps = new ArrayList<Map>();
        List<Map> allmaps = mapping.getMaps();

        try {
            for (int i = 0; i < allmaps.size(); i++) {
                Map map = allmaps.get(i);
                BaseMapElement baseMapElement = null;

                if (componentType.equalsIgnoreCase(Config.MAP_COMPONENT_SOURCE_TYPE)) {
                    baseMapElement = map.getSourceMapElement();
                } else if (componentType.equalsIgnoreCase(Config.MAP_COMPONENT_TARGET_TYPE)) {
                    baseMapElement = map.getTargetMapElement();
                }
                //if (aMetaObject == metaObject) foundMaps.add(map);
                if (baseMapElement.getDataXmlPath().equalsIgnoreCase(cmpUuid) &&
                        baseMapElement.getDataXmlPath().equalsIgnoreCase(metaUuid)) {
                    foundMaps.add(map);
                }
            }
        } catch (Exception e) {
            System.out.println("Unexpected error in MappingImpl.findMaps() " + e.getMessage());
        }
        return foundMaps;
    }

    /**
     * Find all the segments (no duplicates) that are referenced by these maps.
     *
     * @param theMaps maps that you would like to analyze
     * @return a list of segment metas
     * @throws MappingException
     */
    public List<CSVSegmentMeta> findUniqueMappedSegments(List<Map> theMaps) throws MappingException {
        HashMap<String, CSVSegmentMeta> hashMap = new HashMap<String, CSVSegmentMeta>();
        for (int i = 0; i < theMaps.size(); i++) {
            Map map = theMaps.get(i);
            BaseMapElement sourceMapElement = map.getSourceMapElement();
            MetaObject sourceMetaObject = sourceMapElement.getMetaObject();
            String sourceComponentUuid = sourceMapElement.getDataXmlPath();//.getComponentuuid();

            if (sourceMetaObject instanceof CSVFieldMeta) {
                // if it's a CSV object, find the segment + add it to the HashMap
                hashMap.put(((CSVFieldMeta) sourceMetaObject).getSegmentName(), ((CSVFieldMeta) sourceMetaObject).getSegment());
            } else if (sourceMetaObject instanceof ParameterMeta) {
                // if it's a function object, 1) find the input parameters 2) find maps to these meta objects.
                // 3) recursively find segments to these maps 4) add them to the HashMap
                List<ParameterMeta> parameterMeta = ((ParameterMeta) sourceMetaObject).getFunctionMeta().getInputDefinitionList();
                List<Map> functionMaps = new ArrayList<Map>();
                for (int j = 0; j < parameterMeta.size(); j++) {
                    ParameterMeta meta = parameterMeta.get(j);
                    functionMaps.addAll(this.findMapsAssociatedWithMetaObject(sourceComponentUuid, meta.getXmlPath(), Config.MAP_COMPONENT_TARGET_TYPE));
                }
                List<CSVSegmentMeta> csvSegmentMetas = this.findUniqueMappedSegments(functionMaps);
                for (int j = 0; j < csvSegmentMetas.size(); j++) {
                    CSVSegmentMeta csvSegmentMeta = csvSegmentMetas.get(j);
                    hashMap.put(csvSegmentMeta.getName(), csvSegmentMeta);
                }
            }
        }
        return new ArrayList<CSVSegmentMeta>(hashMap.values());
    }

    /**
     * Find all the child segments (no duplicates) that are referenced by these maps (all
     * parents are removed).
     *
     * @param theMaps maps that you would like to analyze
     * @return a list of segment metas
     * @throws MappingException
     */
    public List<CSVSegmentMeta> findUniqueChildMappedSegments(List<Map> theMaps) throws MappingException {
        List<CSVSegmentMeta> uniqueChildMappedSegments = new ArrayList<CSVSegmentMeta>();
        // find all the unique mapped segments.
        List<CSVSegmentMeta> allmetas = findUniqueMappedSegments(theMaps);
        // now we have to remove any parents.
        for (int i = 0; i < allmetas.size(); i++) {
            boolean isparent = false;
            CSVSegmentMeta targetMeta = allmetas.get(i);
            for (int j = 0; j < allmetas.size(); j++) {
                CSVSegmentMeta potentialChildMeta = allmetas.get(j);
                if (isParent(targetMeta, potentialChildMeta)) {
                    isparent = true;
                }
            }
            if (!isparent) {
                uniqueChildMappedSegments.add(targetMeta);
            }
        }
        return uniqueChildMappedSegments;
    }

    /**
     * Is the first segment a parent (or grandparent, etc) of the second segment?
     *
     * @param potentialParentSegment The segment in question
     * @param potentialChildSegment  The potential child of potentialParentSegment
     * @return true if potentialParentSegment is a parent (or grandparent) of potentialChildSegment, otherwise false.
     */
    public boolean isParent(CSVSegmentMeta potentialParentSegment, CSVSegmentMeta potentialChildSegment) {
        boolean isparent = false;
        // iterate up the potential child and see if you find the potentialParentSegment
        CSVSegmentMeta seg = potentialChildSegment.getParent();
        while (seg != null) {
            if (seg.getXmlPath().equalsIgnoreCase(potentialParentSegment.getXmlPath())) {
                isparent = true;
            }
            seg = seg.getParent();
        }
        return isparent;
    }

    public CSVSegmentMeta findCommonSourceParentMeta(List<Map> theMaps) throws MappingException {
        CSVSegmentMeta parentMeta = null;
        // all the segments that are directly mapped to, initialization is next following the code below.
        List<CSVSegmentMeta> mappedSegmentArray = null;

        // an array of stacked metaSegments (the bottom of each stack contains the segments
        // in the above array) - this is needed to trace the parent/child relationship.
        List<Stack<CSVSegmentMeta>> segmentStackArray = new ArrayList<Stack<CSVSegmentMeta>>();

        // setup the mappedSegmentArray array.
        mappedSegmentArray = findAllMappedSegments(theMaps);

        // setup the segmentStackArray
        for (int i = 0; i < mappedSegmentArray.size(); i++) {
            // create a stack.
            Stack<CSVSegmentMeta> stack = new Stack<CSVSegmentMeta>();
            // push the first element on.
            CSVSegmentMeta csvSegmentMeta = mappedSegmentArray.get(i);
            stack.push(csvSegmentMeta);
            // iterate parents + push each one on.
            while (csvSegmentMeta.getParent() != null) {
                csvSegmentMeta = csvSegmentMeta.getParent();
                stack.push(csvSegmentMeta);
            }
            // put stack on the array.
            segmentStackArray.add(stack);
        }

        // find the common parent.
        CSVSegmentMeta tempCSVSegmentMeta = null; // used for comparison.
        boolean allStacksHaveItems = true;
        while (allStacksHaveItems) {
            for (int i = 0; i < segmentStackArray.size(); i++) {
                Stack<CSVSegmentMeta> thisStack = segmentStackArray.get(i);
				if(thisStack==null || thisStack.isEmpty())
				{//to avoid dumbly access an empty stack
//					System.out.println("is stack null? " + thisStack==null);
//					if(thisStack!=null)
//					{
//						System.out.println("is stack empty? " + thisStack.isEmpty());
//					}
//					System.out.println("segmentStackArray size is='" + segmentStackArray.size() + "' and i=" + i);
					allStacksHaveItems = false;
					continue;
				}
				CSVSegmentMeta thisCSVSegmentMeta = thisStack.pop();
                // if it's our first time through.
                if (tempCSVSegmentMeta == null) {
                    tempCSVSegmentMeta = thisCSVSegmentMeta;
                } else {
                    if (tempCSVSegmentMeta.getXmlPath().equalsIgnoreCase(thisCSVSegmentMeta.getXmlPath())) {
                        // UUIDS match up.. move on to the next one.
                    } else {
                        // UUIDS don't match up.. "tempCSVSegmentMeta" contains the common parent.
                        break;
                    }
                }
                // are we at the end of the array?
                if (i == segmentStackArray.size() - 1) {
                    // if we got here, we found a common parent.
                    // save it off and check the next level in the stack.
                    parentMeta = tempCSVSegmentMeta;
                    tempCSVSegmentMeta = null;
                }
                if (thisStack.size() == 0) allStacksHaveItems = false;
            }
        }

        if (parentMeta == null) {
            throw new MappingException("A common parent segment could not be found.", null);
        }
        return parentMeta;
    }

    /**
     * Find all the data segments contained within csvSegment that are defined by this csvSegmentMeta.  This
     * will up the object (parents, grandparents) as well as down (all children recursively).
     *
     * @param csvSegmentMeta The segment definition that you are looking for.
     * @param csvSegment     The data segment in which to look.
     * @return A list of segment datas.
     */
    public List<CSVSegment> findDataSegmentsForMeta(CSVSegmentMeta csvSegmentMeta, CSVSegment csvSegment) {
        List<CSVSegment> foundSegments = new ArrayList<CSVSegment>();

        // check this segment.
        if (isDataSegmentOfMeta(csvSegmentMeta, csvSegment)) {
            foundSegments.add(csvSegment);
        }
        // check the parent segments.
        foundSegments.addAll(findParentDataSegmentsForMeta(csvSegmentMeta, csvSegment));
        // check the child segments.
        foundSegments.addAll(findChildDataSegmentsForMeta(csvSegmentMeta, csvSegment));

        return foundSegments;
    }

    /**
     * Is this data segment based on this metadata?
     *
     * @param csvSegmentMeta the segment metadata
     * @param csvSegment     the data segment in question
     * @return true if it is based on the metadata, false if not.
     */
    public boolean isDataSegmentOfMeta(CSVSegmentMeta csvSegmentMeta, CSVSegment csvSegment) {
        if (csvSegment.getXmlPath().equalsIgnoreCase(csvSegmentMeta.getXmlPath())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Recursively find all child data segments that are based on this metadata.
     *
     * @param csvSegmentMeta the segment metadata
     * @param csvSegment     the data segment in question
     * @return a list of data segments
     */
    public List<CSVSegment> findChildDataSegmentsForMeta(CSVSegmentMeta csvSegmentMeta, CSVSegment csvSegment) {
        List<CSVSegment> foundSegments = new ArrayList<CSVSegment>();
        // iterate a list of child segments and return all that are based on this meta.
        List<CSVSegment> childSegments = csvSegment.getChildSegments();
        for (int i = 0; i < childSegments.size(); i++) {
            CSVSegment childSegment = childSegments.get(i);
            if (isDataSegmentOfMeta(csvSegmentMeta, childSegment)) {
                foundSegments.add(childSegment);
            }
            foundSegments.addAll(findChildDataSegmentsForMeta(csvSegmentMeta, childSegment));
        }
        return foundSegments;
    }

    /**
     * Find all parent data segments that are based on this metadata.
     *
     * @param csvSegmentMeta the segment metadata
     * @param csvSegment     the data segment in question
     * @return a list of data segments
     */
    public List<CSVSegment> findParentDataSegmentsForMeta(CSVSegmentMeta csvSegmentMeta, CSVSegment csvSegment) {
        List<CSVSegment> foundSegments = new ArrayList<CSVSegment>();
        // iterate up the data segment.
        CSVSegment parentDataSegment = csvSegment.getParentSegment();
        while (parentDataSegment != null) {
            if (isDataSegmentOfMeta(csvSegmentMeta, parentDataSegment)) {
                foundSegments.add(parentDataSegment);
            }
            parentDataSegment = parentDataSegment.getParentSegment();
        }
        return foundSegments;
    }

    /**
     * Find all the data segments contained within csvSegment that are defined by these csvSegmentMetas.
     *
     * @param csvSegmentMetas The segment definitions that you are looking for.
     * @param csvSegment      The data segment in which to look.
     * @return A list of segment datas.
     */
    public List<CSVSegment> findDataSegmentsForMetas(List<CSVSegmentMeta> csvSegmentMetas, CSVSegment csvSegment) {
        List<CSVSegment> foundSegments = new ArrayList<CSVSegment>();
        for (int i = 0; i < csvSegmentMetas.size(); i++) {
            CSVSegmentMeta segmentMeta = csvSegmentMetas.get(i);
            foundSegments.addAll(findDataSegmentsForMeta(segmentMeta, csvSegment));
        }
        return foundSegments;
    }

    /**
     * Searches a data segment for data defined by field meta.  This function will search
     * this data segment, it's parents (and grandparents) and it's children.
     *
     * @param csvSegment   The child segment in which you want to search.
     * @param csvFieldMeta The field whos value you want to retrieve.
     * @return The value
     * @throws MappingException If multiple segments around found.
     */
    public String getValueFromChildSegment(CSVSegment csvSegment, CSVFieldMeta csvFieldMeta) throws MappingException {
        String value = "";
        CSVSegmentMeta csvSegmentMeta = csvFieldMeta.getSegment();
        List<CSVSegment> theSegment = findDataSegmentsForMeta(csvSegmentMeta, csvSegment);

        if (theSegment.size() == 0) {
            // do nothing, return null.
        } else if (theSegment.size() == 1) {
            List<CSVField> dataFields = theSegment.get(0).getFields();
            for (int i = 0; i < dataFields.size(); i++) {
                CSVField csvField = dataFields.get(i);
                if (csvField.getXmlPath().equalsIgnoreCase(csvFieldMeta.getXmlPath())) {
                    // we found a match! get the data!
                    value = csvField.getValue();
                }
            }
        } else if (theSegment.size() > 1) {
            throw new MappingException("Found multiple segments!", null);
        }

        return value;
    }

    public String getValueFromChildSegment(CSVSegment csvSegment, FunctionComponent functionComponent, ParameterMeta outputParameterMeta) throws MappingException , FunctionException
    {
        FunctionMeta functionMeta = outputParameterMeta.getFunctionMeta();
        int outputpos = outputParameterMeta.getParameterPosition();
        List<ParameterMeta> inputParameterMetas = functionMeta.getInputDefinitionList();
        List<String> inputValues = new ArrayList<String>();


        // for each input find it's input value.
        for (int i = 0; i < inputParameterMetas.size(); i++) {
            ParameterMeta parameterMeta = inputParameterMetas.get(i);
            String inputvalue = null;
            // find maps to these inputs.
            List<Map> maps = findMapsAssociatedWithMetaObject(functionComponent.getXmlPath(), parameterMeta.getXmlPath(), Config.MAP_COMPONENT_TARGET_TYPE);
            // throw excpetions if needed.
            if (maps.size() > 1) throw new MappingException("Function must have ONLY one map. " + maps.size() + " found. : " + parameterMeta, null);
            if (maps.size() < 1) throw new MappingException("Function must have one map. Zero found. : " + parameterMeta, null);

            // System.out.println("maps = " + maps);
            // get the source metaObject.
            BaseMapElement sourceMapElement = maps.get(0).getSourceMapElement();
            MetaObject sourceMetaObject = sourceMapElement.getMetaObject();

            if (sourceMetaObject instanceof CSVFieldMeta) { // if it's a csv..
                inputvalue = getValueFromChildSegment(csvSegment, (CSVFieldMeta) sourceMetaObject);
            } else if (sourceMetaObject instanceof ParameterMeta) { // if it's a function of a function
                inputvalue = getValueFromChildSegment(csvSegment, (FunctionComponent) sourceMapElement.getComponent(), (ParameterMeta) sourceMetaObject);
            } else {
                //System.out.println("VVVVVV : " + i + "/" + inputParameterMetas.size() + " : "+parameterMeta);
                throw new MappingException("sourceMetaObject can not be understood. check whether mapping with CSVFieldMeta or CSVCloneMeta. : " + sourceMetaObject, null);
            }
            inputValues.add(inputvalue);
        }

        // first, is the function a constant?
        if (functionMeta.isConstantFunction()) {
            FunctionComponent constFunctComp = this.mapping.getFunctionComponent(functionComponent.getXmlPath());
            FunctionConstant fc = constFunctComp.getFunctionConstant();
            if (fc == null)
                throw new MappingException("count not find function constant for function: " + functionComponent.getXmlPath(), null);
            String output = "";
            if (fc.getConstantFunctionName().equals(fc.getFunctionNameArray()[0])) output = fc.getValue();
            else if (fc.getConstantFunctionName().equals(fc.getFunctionNameArray()[1]))
            {
                System.out.println("### csvSegment=" + csvSegment.getName() + ", FunctionComponent=" + functionComponent.getMeta().getFunctionName()+ ", ParameterMeta="+ outputParameterMeta.getParameterName() + ", value=" + inputValues.get(0));
                fc.saveValue(inputValues.get(0));
                if (fc.getValue().startsWith("%")) output = inputValues.get(0);
                else output = "";
            }
            else if (fc.getConstantFunctionName().equals(fc.getFunctionNameArray()[2]))
            {
                String dd = "null";
                if (inputValues.size() > 0) dd =  inputValues.get(0);
                System.out.print("##% csvSegment=" + csvSegment.getName() + ", FunctionComponent=" + functionComponent.getMeta().getFunctionName()+ ", ParameterMeta="+ outputParameterMeta.getParameterName() + ", value=" + dd);
                output = fc.readValue();
                System.out.println(", output=" + output);
            }

            return output;
        }

        // , is the FunctionVocabularyMapping?
        if (functionMeta.isFunctionVocabularyMapping())
        {
            FunctionComponent constFunctComp = this.mapping.getFunctionComponent(functionComponent.getXmlPath());
            FunctionVocabularyMapping vm = constFunctComp.getFunctionVocabularyMapping();
            if (vm == null)
                throw new MappingException("Not found 'Vocabulary' function: " + functionComponent.getXmlPath(), null);
            if (inputValues.size() != 1)
                throw new MappingException("Parameter count of 'Vocabulary' fuction must be 1 but now is "+inputValues.size()+". : " + functionComponent.getXmlPath(), null);

            String res = "";
            try
            {
                if (functionMeta.getFunctionName().equalsIgnoreCase(vm.getMethodNamePossibleList()[0]))
                    res = vm.translateValue(inputValues.get(0));
                else if (functionMeta.getFunctionName().equalsIgnoreCase(vm.getMethodNamePossibleList()[1]))
                    res = vm.translateInverseValue(inputValues.get(0));
                else throw new MappingException("'"+ functionMeta.getFunctionName() +"' function could not be found in 'Vocabulary' function group : " + functionComponent.getXmlPath(), null);
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

    public List<CSVSegmentMeta> findAllMappedSegments(List<Map> maps) throws MappingException {
        List<CSVSegmentMeta> mappedSegments = new ArrayList<CSVSegmentMeta>();

        for (int i = 0; i < maps.size(); i++) {
            Map map = maps.get(i);
            BaseMapElement sourceMapElement = map.getSourceMapElement();
            MetaObject metaObject = sourceMapElement.getMetaObject();
            String sourceComponenetUuid = sourceMapElement.getDataXmlPath();//.getComponentuuid();
            if (metaObject instanceof CSVSegmentMeta) {
                mappedSegments.add((CSVSegmentMeta) metaObject);
            } else if (metaObject instanceof CSVFieldMeta) {
                mappedSegments.add(((CSVFieldMeta) metaObject).getSegment());
            } else if (metaObject instanceof ParameterMeta) {
                mappedSegments.addAll(findAllMappedSegments(((ParameterMeta) metaObject).getFunctionMeta(), sourceComponenetUuid));
            } else {
                throw new MappingException("Map Source Element needs to be CSVFieldMeta OR ParameterMeta " + metaObject, null);
            }
        }

        return mappedSegments;
    }

    public List<CSVSegmentMeta> findAllMappedSegments(FunctionMeta functionMeta, String functionComponentUuid) throws MappingException {
        // the returned array.
        List<CSVSegmentMeta> mappedSegments = new ArrayList<CSVSegmentMeta>();
        // find all the mapped segments to these target inputs.
        List<ParameterMeta> parameterMetas = functionMeta.getInputDefinitionList();
        for (int i = 0; i < parameterMetas.size(); i++) {
            ParameterMeta parameterMeta = parameterMetas.get(i);
            List<Map> maps = findMapsAssociatedWithMetaObject(functionComponentUuid, parameterMeta.getXmlPath(), Config.MAP_COMPONENT_TARGET_TYPE);
            if (maps.size() > 1) {
                throw new MappingException("There can only be one map to for a single ParameterMeta " + parameterMeta, null);
            } else if (maps.size() == 1) {
                mappedSegments.addAll(findAllMappedSegments(maps));
            } else {
                // do nothing.
            }
        }
        return mappedSegments;
    }

//    public void applyDefaultValues(CloneAttributeData cloneAttributeData, List<CloneDatatypeFieldMeta> cloneDatatypeFieldMetas) {
//        // iterate all the possible cloneDatatypeFieldMetas
//        for (int i = 0; i < cloneDatatypeFieldMetas.size(); i++) {
//            CloneDatatypeFieldMeta cloneDatatypeFieldMeta = cloneDatatypeFieldMetas.get(i);
//            // does this meta exist in the data already?
//            if (!datatypeFieldExists(cloneAttributeData, cloneDatatypeFieldMeta)) {
//                String defaultValue = getDefaultValue(cloneDatatypeFieldMeta.getUserDefaultValue(), cloneDatatypeFieldMeta.getHL7DefaultValue());
//                // does it have a default value?
//                if (defaultValue != null) {
//                    // create it and add it!
//                    CloneDatatypeFieldData cloneDatatypeFieldData = new CloneDatatypeFieldDataImpl(cloneDatatypeFieldMeta);
//                    cloneDatatypeFieldData.setValue(defaultValue);
//                    cloneAttributeData.addDatatypeField(cloneDatatypeFieldData);
//                }
//            }
//        }
//    }

    public String getDefaultValue(String userDefaultValue, String hl7DefaultValue) {
        if (userDefaultValue != null && !"".equals(userDefaultValue)) {
            return userDefaultValue;
        }
        if (hl7DefaultValue != null && !"".equals(hl7DefaultValue)) {
            return hl7DefaultValue;
        }
        return null;
    }

//    public boolean datatypeFieldExists(CloneAttributeData cloneAttributeData, CloneDatatypeFieldMeta cloneDatatypeFieldMeta) {
//        List<CloneDatatypeFieldData> cloneDatatypeFieldData = cloneAttributeData.getDatatypeFields();
//        for (int i = 0; i < cloneDatatypeFieldData.size(); i++) {
//            CloneDatatypeFieldData datatypeFieldData = cloneDatatypeFieldData.get(i);
//            if (datatypeFieldData.getName().equalsIgnoreCase(cloneDatatypeFieldMeta.getName()))
//                return true;
//        }
//        return false;
//    }

    public List<CSVFieldMeta> findAllCsvFields(CSVSegmentMeta csvSegmentMeta, boolean recursive) {
        List<CSVFieldMeta> csvFieldMetas = new ArrayList<CSVFieldMeta>(csvSegmentMeta.getFields());
        if (recursive) {
            List<CSVSegmentMeta> csvSegmentMetas = csvSegmentMeta.getChildSegments();
            for (int i = 0; i < csvSegmentMetas.size(); i++) {
                CSVSegmentMeta segmentMeta = csvSegmentMetas.get(i);
                csvFieldMetas.addAll(findAllCsvFields(segmentMeta, true));
            }
        }
        return csvFieldMetas;
    }

//    public CSVSegmentMeta findMetaMappedToClone(CloneMeta cloneMeta, String cloneComponentUuid) throws MappingException {
//        CSVSegmentMeta csvSegmentMeta = null;
//
//        // find maps directly to THIS CLONE
//        List<Map> mapsToThisClone = this.findMapsToThisClone(cloneMeta, cloneComponentUuid);
//        if (mapsToThisClone.size() > 1) throw new MappingException("A clone can only have 1 map to it.  Maps found: " + mapsToThisClone.size(), null);
//
//        // find maps to THIS CLONE's datatype fields.
//        List<Map> mapsToThisClonesDatatypefields = this.findMapsToThisClonesDatatypefields(cloneMeta, cloneComponentUuid, false);
//
//        // find maps to THIS CLONE's datatypefields RECURSIVELY (check all clones below this clone too).
//        List<Map> mapsToThisClonesDatatypefieldsRecursive = this.findMapsToThisClonesDatatypefields(cloneMeta, cloneComponentUuid, true);
//
//        // find maps to datatypefields to clones that are BELOW THIS CLONE.
//        List<Map> mapsToDatatypefieldsBelowThisClone = new ArrayList<Map>(mapsToThisClonesDatatypefieldsRecursive);
//        mapsToDatatypefieldsBelowThisClone.removeAll(mapsToThisClonesDatatypefields);
//
//        if (mapsToThisClone.size() == 1) {
//            // STEP 1.1:  Is there a segment mapped directly to this clone?
//            List<CSVSegmentMeta> segmentsMappedToClone = this.findAllMappedSegments(mapsToThisClone);
//            if (segmentsMappedToClone.size() != 1) {
//                throw new MappingException("A clone can only have 1 segment mapped to it.  Segments found: " + segmentsMappedToClone.size(), null);
//            } else {
//                csvSegmentMeta = segmentsMappedToClone.get(0);
//            }
//        } else if (mapsToThisClonesDatatypefields.size() > 0) {
//            // STEP 1.2:  Are there maps to *this* clone's attribute fields?
//            csvSegmentMeta = this.findCommonSourceParentMeta(mapsToThisClonesDatatypefieldsRecursive);
//        }
//        return csvSegmentMeta;
//    }

//    public List<CSVSegmentMeta> findMetaMappedToAttribute(CloneAttributeMeta cloneAttributeMeta, String cloneComponentUuid) throws MappingException {
//        List<CSVSegmentMeta> csvSegmentMetas = new ArrayList<CSVSegmentMeta>();
//
//        // find maps directly to THIS ATTRIBUTE
//        List<Map> mapsToThisAttribute = this.findMapsToThisAttribute(cloneAttributeMeta, cloneComponentUuid);
//        if (mapsToThisAttribute.size() > 1) {
//            throw new MappingException("An attribute can only have 1 map to it.  Maps found: " + mapsToThisAttribute.size(), null);
//        }
//
//        // find maps to JUST THIS ATTRIBUTE.
//        List<Map> thisAttributeMaps = this.findMapsToThisAttributesDatatypefields(cloneAttributeMeta, cloneComponentUuid, false);
//
//        // find maps to THIS ATTRIBUTE RECURSIVE.
//        List<Map> allAttributeMaps = this.findMapsToThisAttributesDatatypefields(cloneAttributeMeta, cloneComponentUuid, true);
//
//        // find maps BELOW THIS ATTRIBUTE.
//        List<Map> subAttributeMaps = new ArrayList<Map>(allAttributeMaps);
//        subAttributeMaps.removeAll(thisAttributeMaps);
//
//        // if there any maps datatype fields contained within this attribute.
//        if (allAttributeMaps.size() > 0) {
//            // STEP 1: Find the segmentMeta that this attribute is mapped to.
//            if (mapsToThisAttribute.size() == 1) {
//                // STEP 1.1:  Is there a segment mapped directly to this attribute?
//                csvSegmentMetas = this.findAllMappedSegments(mapsToThisAttribute);
//            } else if (thisAttributeMaps.size() > 0) {
//                // STEP 1.2:  Are there maps to *this* attribute's datatype fields?
//                // todo: should i do this?  should i just do recursive?
//                csvSegmentMetas = this.findUniqueChildMappedSegments(allAttributeMaps);
//            } else if (subAttributeMaps.size() > 0) {
//                // STEP 1.3:  Are there maps below *this* attribute's datatype fields?
//                csvSegmentMetas = this.findUniqueChildMappedSegments(subAttributeMaps);
//            }
//        }
//
//        return csvSegmentMetas;
//    }
}
