/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/impl/MapProcessorImpl.java,v 1.1 2007-07-03 18:26:45 wangeug Exp $
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


package gov.nih.nci.caadapter.hl7.map.impl;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.MetaException;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.csv.data.CSVSegmentedFile;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.function.FunctionException;
import gov.nih.nci.caadapter.common.function.meta.ParameterMeta;
import gov.nih.nci.caadapter.common.map.BaseMapElement;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.clone.data.CloneAttributeData;
import gov.nih.nci.caadapter.hl7.clone.data.CloneData;
import gov.nih.nci.caadapter.hl7.clone.data.CloneDatatypeFieldData;
import gov.nih.nci.caadapter.hl7.clone.data.HL7V3Data;
import gov.nih.nci.caadapter.hl7.clone.data.impl.CloneAttributeDataImpl;
import gov.nih.nci.caadapter.hl7.clone.data.impl.CloneDataImpl;
import gov.nih.nci.caadapter.hl7.clone.data.impl.CloneDatatypeFieldDataImpl;
import gov.nih.nci.caadapter.hl7.clone.data.impl.HL7V3DataImpl;
import gov.nih.nci.caadapter.hl7.clone.meta.CloneAttributeMeta;
import gov.nih.nci.caadapter.hl7.clone.meta.CloneDatatypeFieldMeta;
import gov.nih.nci.caadapter.hl7.clone.meta.CloneMeta;
import gov.nih.nci.caadapter.hl7.clone.meta.impl.HL7V3MetaImpl;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;
import gov.nih.nci.caadapter.hl7.map.HL7V3DataResult;
import gov.nih.nci.caadapter.hl7.map.Map;
import gov.nih.nci.caadapter.hl7.map.MapProcessorHelper;
import gov.nih.nci.caadapter.hl7.map.Mapping;
import gov.nih.nci.caadapter.hl7.map.MappingException;

import java.util.ArrayList;
import java.util.List;

/**
 * Processor of map files.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.1 $
 * @since caAdapter v1.2
 */

public class MapProcessorImpl {
    private static final String LOGID = "$RCSfile: MapProcessorImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/impl/MapProcessorImpl.java,v 1.1 2007-07-03 18:26:45 wangeug Exp $";

    // class variable from constructor
    private Mapping mapping = null;
    private CSVSegmentedFile csvSegmentedFile = null;
    private MapProcessorHelper mapHelper = null;
    private HL7V3MetaImpl rootv3MetaObject = null;

    // Class variables used during processing.
    List<HL7V3DataResult> resultsArray = null;
    ValidatorResults theValidatorResults = null;
    HL7V3Data currentHL7Data = null;
    int indent = -1;

    public List<HL7V3DataResult> process(Mapping mapping, CSVSegmentedFile csvSegmentedFile) throws MetaException, MappingException {
        // init class variables
    	System.out.println(this.getClass().getName()+" process start: "+new java.util.Date());
        this.mapping = mapping;
        this.csvSegmentedFile = csvSegmentedFile;
        this.mapHelper = new MapProcessorHelper(mapping);
        this.rootv3MetaObject = (HL7V3MetaImpl) mapping.getTargetComponent().getMeta();
        this.resultsArray = new ArrayList<HL7V3DataResult>();

        // process one CSV source logical record at a time.
        List<CSVSegment> logicalRecords = csvSegmentedFile.getLogicalRecords();
        for (int i = 0; i < logicalRecords.size(); i++) {
            processRootClone(logicalRecords.get(i));
        }

        log("MapProcessorImpl - done!\n");
        System.out.println(this.getClass().getName()+" process end: "+new java.util.Date());
        return resultsArray;
    }

    private void processRootClone(CSVSegment csvSegment) throws MappingException {
        // Each CSV logical record can generate more than one HL7 v3 instance. So...

        // Step 1:  find the CSV meta that the root clone is mapped to.
        CSVSegmentMeta thisClonesMappedSegmentMeta = mapHelper.findMetaMappedToClone(rootv3MetaObject.getRootCloneMeta(), mapping.getTargetComponent().getUUID());

        // Step 2:  find the CSV data that this meta refers to.
        List<CSVSegment> thisClonesMappedSegments = new ArrayList<CSVSegment>();
        if (thisClonesMappedSegmentMeta == null) {
            thisClonesMappedSegments.add(csvSegment);
        } else {
            thisClonesMappedSegments = mapHelper.findDataSegmentsForMeta(thisClonesMappedSegmentMeta, csvSegment);
        }

        // Step 3: Create a v3 instance for each one of these segments.
        for (int i = 0; i < thisClonesMappedSegments.size(); i++) {
            CSVSegment segment = thisClonesMappedSegments.get(i);
            // setup the hl7data and validation results.
            currentHL7Data = new HL7V3DataImpl(rootv3MetaObject);
            theValidatorResults = new ValidatorResults();
            // process it.
            List<CloneData> processedClones = processClone(rootv3MetaObject.getRootCloneMeta(), segment, mapping.getTargetComponent().getUUID());
            if (processedClones.size() == 1) {
                for (int j = 0; j < processedClones.size(); j++) {
                    CloneData cloneData = processedClones.get(j);
                    currentHL7Data.setRootCloneData(cloneData);
                    currentHL7Data.setMessageID(rootv3MetaObject.getMessageID());
                }
            } else {
                Message msg = MessageResources.getMessage("GEN0", new Object[]{"MapProcessor.processRootClone()"});
                theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
            }

            // add it to the list.
            resultsArray.add(new HL7V3DataResult(currentHL7Data, theValidatorResults));

        }
    }

    private List<CloneData> processClone(CloneMeta cloneMeta, CSVSegment csvSegment, String cloneComponentUuid) throws MappingException {
   
    	Boolean valid = true;
    	Boolean ignore = false;
    	
    	List<CloneData> cloneDatas = new ArrayList<CloneData>();
    	//System.out.println(this.getClass().getName() + "processClone:cloneMeta.getName:"+cloneMeta.getName());
        indent++;

        // STEP 1: Find the segment meta that is mapped to this clone.
        CSVSegmentMeta thisClonesMappedSegmentMeta = mapHelper.findMetaMappedToClone(cloneMeta, cloneComponentUuid);

        // STEP 2: Find the segment data that is represented by this metadata.
        List<CSVSegment> thisClonesMappedSegments = new ArrayList<CSVSegment>();
        if (thisClonesMappedSegmentMeta == null) {
            if (mapHelper.findMapsBelowThisClone(cloneMeta, cloneComponentUuid).size() > 0 ||
                    cloneMeta.getCardinality().getMin() > 0) {
                // STEP 2.1.1: There are no maps to this clone (or this clone's attributes)
                // *but* it's manditory OR there are maps to child clones.  Use the current segment + move on.
                thisClonesMappedSegments.add(csvSegment);
            } else {
                // STEP 2.1.2: There are no maps to this clone (or this clone's attributes)
                // and it's *not* manditory.  Just return.
                indent--;
                return cloneDatas;
            }
        } else {
            // STEP 2.2: There are maps to this clone (or this clone's attributes) So, find the data for it.
            thisClonesMappedSegments = mapHelper.findDataSegmentsForMeta(thisClonesMappedSegmentMeta, csvSegment);
        }
        Boolean presented = false;
        // STEP 3: For each segment data create child clones + process attributes.
        for (int i = 0; i < thisClonesMappedSegments.size(); i++) {
            CSVSegment segment = thisClonesMappedSegments.get(i);
            CloneDataImpl cloneData = new CloneDataImpl(cloneMeta);
            log(cloneMeta.getName() + " [" + segment.getName() + "] (" + (i + 1) + " of " + thisClonesMappedSegments.size() + ")");

            // STEP 3.1: Create Attributes.
            List<CloneAttributeMeta> cloneAttributeMetas = cloneMeta.getAttributes();
            for (int j = 0; j < cloneAttributeMetas.size(); j++) {
                CloneAttributeMeta cloneAttributeMeta = cloneAttributeMetas.get(j);
                cloneData.addAttributes(processAttribute(cloneAttributeMeta, segment, cloneComponentUuid));
                valid = valid && cloneAttributeMeta.getValid();
                presented = presented || cloneAttributeMeta.getPresented();
            }
            // STEP 3.2: Create Clones.
            List<CloneMeta> childCloneMetas = cloneMeta.getChildClones();
            for (int j = 0; j < childCloneMetas.size(); j++) {
                CloneMeta meta = childCloneMetas.get(j);
                List <CloneData> cloneData1 = processClone(meta, segment, cloneComponentUuid); 
                if (cloneData1 != null) {
                	cloneData.addClones(cloneData1);
                	presented = true;
                }
                else 
                	ignore = true;
            }
            cloneDatas.add(cloneData);
        }
        indent--;

        if (!presented) {
        	cloneDatas = null;
        }
        if (!valid && cloneMeta.getParentMeta().getCardinality().getMin()==0) {
        	cloneDatas = null;
        }
        if (!valid && cloneMeta.getCardinality().getMin()==0) {
        	cloneDatas = null;
        }
        if (ignore && cloneMeta.getCardinality().getMin() == 0) {
        	cloneDatas = null;
        }
        return cloneDatas;
    }

    private List<CloneAttributeData> processAttribute(CloneAttributeMeta cloneAttributeMeta, CSVSegment csvSegment, String cloneComponentUuid) throws MappingException {

        Boolean presented = false;
        List<CloneAttributeData> cloneAttributeDatas = new ArrayList<CloneAttributeData>();
        indent++;

        // STEP 1: Find the segment meta that is mapped to this clone.
        List<CSVSegmentMeta> thisAttributesMappedSegmentMeta = mapHelper.findMetaMappedToAttribute(cloneAttributeMeta, cloneComponentUuid);

        // STEP 2: Find the segment data that is represented by this metadata.
        List<CSVSegment> dataSegments = new ArrayList<CSVSegment>();
        if (thisAttributesMappedSegmentMeta.size() == 0) {
            boolean isOptional = cloneAttributeMeta.getCardinality() == null || cloneAttributeMeta.getCardinality().getMin() == 0;
            if (isOptional && mapHelper.findMapsToThisAttributesDatatypefields(cloneAttributeMeta, cloneComponentUuid, true).size() == 0) {
                // STEP 2.1: this attribute is *not* mandatory && nothing is mapped.
                // Just return.
                indent--;
                cloneAttributeMeta.setPresented(false);
                return cloneAttributeDatas;
            } else {
                // STEP 2.2: this attribute is mandatory.
                // Use the current segment + move on.
                dataSegments.add(csvSegment);
            }
        } else {
            // STEP 2.2: There are maps to this attribute (or this clone's attributes) So, find the data for it.
            dataSegments = mapHelper.findDataSegmentsForMetas(thisAttributesMappedSegmentMeta, csvSegment);
        }

        // STEP 3: For each segment data create child attributes + process datatype fields.
        if (dataSegments.size() == 0) {
        	cloneAttributeMeta.setPresented(false);
        }
        for (int i = 0; i < dataSegments.size(); i++) {
            CSVSegment segment = dataSegments.get(i);
            log("-" + cloneAttributeMeta.getName() + " [" + segment.getName() + "] (" + (i + 1) + " of " + dataSegments.size() + ")");

            CloneAttributeData cloneAttributeData = new CloneAttributeDataImpl(cloneAttributeMeta);

            List<CloneAttributeMeta> childAttributeMetas = cloneAttributeMeta.getChildAttributes();
            for (int j = 0; j < childAttributeMetas.size(); j++) {
                CloneAttributeMeta attributeMeta = childAttributeMetas.get(j);
                cloneAttributeData.addAttributes(processAttribute(attributeMeta, segment, cloneComponentUuid));
                if (!attributeMeta.getValid()&& cloneAttributeMeta.getCardinality().getMin() > 0)
                	cloneAttributeMeta.setValid(attributeMeta.getValid());
                presented = presented || attributeMeta.getPresented();
            }

            List<CloneDatatypeFieldMeta> cloneDatatypeFieldMetas = cloneAttributeMeta.getDatatypeFields();
            for (int j = 0; j < cloneDatatypeFieldMetas.size(); j++) {
                CloneDatatypeFieldMeta cloneDatatypeFieldMeta = cloneDatatypeFieldMetas.get(j);
                cloneAttributeData.addDatatypeFields(processDatatypeFields(cloneDatatypeFieldMeta, segment, cloneComponentUuid));
                if (!cloneDatatypeFieldMeta.getValid()&& cloneAttributeMeta.getCardinality().getMin() > 0)
                	cloneAttributeMeta.setValid(cloneDatatypeFieldMeta.getValid());
                presented = presented || cloneDatatypeFieldMeta.getPresented();
            }
            cloneAttributeDatas.add(cloneAttributeData);
        }
        cloneAttributeMeta.setPresented(presented);
        indent--;
        return cloneAttributeDatas;
    }

    private List<CloneDatatypeFieldData> processDatatypeFields(CloneDatatypeFieldMeta cloneDatatypeFieldMeta, CSVSegment segment, String cloneComponentUuid) throws MappingException {
    	
    	List<CloneDatatypeFieldData> dtFieldDatas = new ArrayList<CloneDatatypeFieldData>();
        indent++;
        //log("@"+cloneDatatypeFieldMeta.getName());

        // STEP 1: Find maps to this datatypefield.
        List<Map> maps = mapHelper.findMapsAssociatedWithMetaObject(cloneComponentUuid, cloneDatatypeFieldMeta.getUUID(), Config.MAP_COMPONENT_TARGET_TYPE);

        // STEP 2.1: If there are maps.  Fetch the data.
        if (maps.size() > 0) {
            for (int i = 0; i < maps.size(); i++) {
                Map map = maps.get(i);
                CloneDatatypeFieldData dtFieldData = new CloneDatatypeFieldDataImpl(cloneDatatypeFieldMeta);

                // get the source BaseMapElement.
                BaseMapElement sourceMapElement = map.getSourceMapElement();

                // targets must be of clone + source must be of csv OR function
                if (!(sourceMapElement.getMetaObject() instanceof CSVFieldMeta) &&
                        !(sourceMapElement.getMetaObject() instanceof ParameterMeta)) {
                    throw new MappingException("sourceMetaObject not understood " + sourceMapElement.getMetaObject(), null);
                }

                // get the MetaObjects.
                MetaObject sourceMetaObject = sourceMapElement.getMetaObject();

                // create the data object.
                String theDataValue = null;
                try {
                    // fetch the data.
                    if (sourceMapElement.isComponentOfSourceType()) {
                        theDataValue = mapHelper.getValueFromChildSegment(segment, (CSVFieldMeta) sourceMetaObject);
                    } else if (sourceMapElement.isComponentOfFunctionType()) {
                        theDataValue = mapHelper.getValueFromChildSegment(segment, (FunctionComponent) sourceMapElement.getComponent(), (ParameterMeta) sourceMetaObject);
                    }
                } catch (MappingException e) {
                    Message msg = MessageResources.getMessage("GEN0", new Object[]{cloneDatatypeFieldMeta.getLinage() + ": " + e.getMessage()});
                    theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
                } catch (FunctionException e) {
                    Message msg = MessageResources.getMessage("MAP16", new Object[]{e.getMessage()});
                    theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
                    Log.logException(this, e);
                }

                // the real value can come from the datasource, a user default OR an HL7 default.
                String theValue = getValue(theDataValue, cloneDatatypeFieldMeta.getUserDefaultValue(), cloneDatatypeFieldMeta.getHL7DefaultValue());
                if (theValue != null && !theValue.equalsIgnoreCase("")) {
                    dtFieldData.setValue(theValue);
                    // add it to the array.
                    dtFieldDatas.add(dtFieldData);
                }
            }
        } else {
            // STEP 2.2: If there are no maps.
            // Apply the default value OR an HL7 default value.
            // If neither exists, do nothing.

            String theValue = getValue("", cloneDatatypeFieldMeta.getUserDefaultValue(), cloneDatatypeFieldMeta.getHL7DefaultValue());
            if (theValue != null && !theValue.equalsIgnoreCase("")) {
                CloneDatatypeFieldData dtFieldData = new CloneDatatypeFieldDataImpl(cloneDatatypeFieldMeta);
                dtFieldData.setValue(theValue);
                // add it to the array.
                dtFieldDatas.add(dtFieldData);
            }
        }

        indent--;
        if (cloneDatatypeFieldMeta.getCardinality().getMin() > dtFieldDatas.size()) {
        	cloneDatatypeFieldMeta.setValid(false);
        }
        if (dtFieldDatas.size()==0) {
        	cloneDatatypeFieldMeta.setPresented(false);
        }
        else {
        	cloneDatatypeFieldMeta.setPresented(true);
        }
        
        return dtFieldDatas;
    }

    private String getValue(String dataValue, String userDefaultValue, String hl7DefaultValue) {
        String realValue = new String();
        if (dataValue != null && !dataValue.equals("")) {
            realValue = dataValue;
        } else if (userDefaultValue != null && !userDefaultValue.equals("")) {
            realValue = userDefaultValue;
        } else if (hl7DefaultValue != null && !hl7DefaultValue.equals("")) {
            realValue = hl7DefaultValue;
        }
        return realValue;
    }

    /**
     * Used for debugging purposes.
     *
     * @return
     */
    private String getIndent(String str) {
        String temp = new String();
        for (int i = 0; i < indent; i++) {
            temp = temp + str;
        }
        return temp;
    }

    private void log(String s) {
        //System.out.println(getIndent(" ") + s);
    }
}
