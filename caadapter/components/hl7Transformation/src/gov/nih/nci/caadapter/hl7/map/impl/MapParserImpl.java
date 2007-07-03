/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/impl/MapParserImpl.java,v 1.1 2007-07-03 18:26:45 wangeug Exp $
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

import gov.nih.nci.caadapter.castor.map.impl.C_component;
import gov.nih.nci.caadapter.castor.map.impl.C_components;
import gov.nih.nci.caadapter.castor.map.impl.C_data;
import gov.nih.nci.caadapter.castor.map.impl.C_link;
import gov.nih.nci.caadapter.castor.map.impl.C_linkpointer;
import gov.nih.nci.caadapter.castor.map.impl.C_links;
import gov.nih.nci.caadapter.castor.map.impl.C_mapping;
import gov.nih.nci.caadapter.castor.map.impl.C_view;
import gov.nih.nci.caadapter.castor.map.impl.C_views;
import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.MetaException;
import gov.nih.nci.caadapter.common.MetaLookup;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.csv.CSVMetaLookup;
import gov.nih.nci.caadapter.common.csv.CSVMetaParserImpl;
import gov.nih.nci.caadapter.common.csv.CSVMetaResult;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.function.*;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.map.BaseComponent;
import gov.nih.nci.caadapter.common.map.BaseMapElement;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
//import gov.nih.nci.caadapter.hl7.clone.meta.CloneMetaLookup;
//import gov.nih.nci.caadapter.hl7.clone.meta.HL7V3Meta;
//import gov.nih.nci.caadapter.hl7.clone.meta.HL7V3MetaFileParser;
//import gov.nih.nci.caadapter.hl7.database.DatabaseMetaLookup;
//import gov.nih.nci.caadapter.hl7.database.DatabaseMetaParserImpl;
//import gov.nih.nci.caadapter.hl7.database.meta.DatabaseMeta;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;
import gov.nih.nci.caadapter.hl7.map.FunctionVocabularyMapping;
import gov.nih.nci.caadapter.hl7.map.Mapping;
import gov.nih.nci.caadapter.hl7.map.MappingException;
import gov.nih.nci.caadapter.hl7.map.MappingResult;
import gov.nih.nci.caadapter.common.map.View;
import gov.nih.nci.caadapter.common.map.ViewImpl;
//import gov.nih.nci.caadapter.hl7.validation.MapValidator;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Hashtable;

/**
 * Parser of map files.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.1 $
 * @date $Date: 2007-07-03 18:26:45 $
 * @since caAdapter v1.2
 */

public class MapParserImpl {
    private static final String LOGID = "$RCSfile: MapParserImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/impl/MapParserImpl.java,v 1.1 2007-07-03 18:26:45 wangeug Exp $";
    Mapping mapping = new MappingImpl();
    private Hashtable<String, MetaLookup> metaLookupTable = new Hashtable<String, MetaLookup>();
    private Hashtable<String, BaseComponent> componentLookupTable = new Hashtable<String, BaseComponent>();
    private String mapfiledir;

    public MappingResult parse(String mapfiledirectory, FileReader metafile)
//        throws MappingException {
    {
        this.mapping = new MappingImpl();
        this.mapfiledir = mapfiledirectory;

        MappingResult mappingResult = new MappingResult();
        ValidatorResults validatorResults = new ValidatorResults();
        mappingResult.setMapping(mapping);
        mappingResult.setValidatorResults(validatorResults);

        try {
            C_mapping cMapping = (C_mapping) C_mapping.unmarshalC_mapping(metafile);
            processComponents(cMapping.getC_components());
            processLinks(cMapping.getC_links());
            processViews(cMapping.getC_views());
            
        } catch(MappingException e){
            Log.logException(this, e);
            Message msg = MessageResources.getMessage("GEN0", new Object[]{e.getMessage()});
            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
        }catch (Exception e) {
            Log.logException(this, e);
            Message msg = MessageResources.getMessage("GEN0", new Object[]{"Could not parse Map file."});
            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
        }

        // validate the map - coomment out these two lines if you want to make this an *invalidating* parser.
//        MapValidator mapValidator = new MapValidator(mapping);
//        validatorResults.addValidatorResults(mapValidator.validate());

        return mappingResult;
    }

    private void processComponents(C_components cComponents) throws MappingException {
        // for each of the components...
        try {
            for (int i = 0; i < cComponents.getC_componentCount(); i++) {
                C_component cComponent = cComponents.getC_component(i);
                // create the component.
                // kind = "scs" OR "HL7v3" OR "??function"
                BaseComponent component = null;
                String kind = cComponent.getKind();
                if (Config.CSV_DEFINITION_DEFAULT_KIND.equalsIgnoreCase(kind)) {
                    component = generateCsvComponent(cComponent, kind);
                    File scmFile = FileUtil.fileLocate(mapfiledir, (String) cComponent.getLocation());
                    component.setFile(scmFile);
//                } else if (Config.HL7_V3_DEFINITION_DEFAULT_KIND.equalsIgnoreCase(kind)) {
//                    component = generateCloneComponent(cComponent, kind);
//                    File hsmFile = FileUtil.fileLocate(mapfiledir, (String) cComponent.getLocation());
//                    component.setFile(hsmFile);
                } else if ("HL7v3MIF".equalsIgnoreCase(kind)) {
//                    component = generateCloneComponent(cComponent, kind);
                	System.out.println("MapParserImpl.processComponents()...HL7v3MIF file loacation:"+cComponent.getLocation());
                    File hsmFile = FileUtil.fileLocate(mapfiledir, (String) cComponent.getLocation());
                    component = new BaseComponent();
                    component.setType(cComponent.getType());
                    component.setUUID(cComponent.getUuid());
                    component.setKind(kind);
                    component.setFile(hsmFile);
                    componentLookupTable.put(cComponent.getUuid(), component);
                } 
                else if (Config.DATABASE_DEFINITION_DEFAULT_KIND.equalsIgnoreCase(kind)) {
                    component = generateDatabaseComponent(cComponent, kind);
                    File hsmFile = FileUtil.fileLocate(mapfiledir, (String) cComponent.getLocation());
                    component.setFile(hsmFile);
                } else if (Config.FUNCTION_DEFINITION_DEFAULT_KIND.equalsIgnoreCase(kind)) {
                    component = generateFunctionComponent(cComponent, kind);
                } else {
                    throw new MappingException("Component kind not understood : " + kind, null);
                }
                // set the component in the Mapping Object.
                // type = "source" OR "target"
                String type = cComponent.getType();
                if ("source".equalsIgnoreCase(type)) {
                    if (mapping.getSourceComponent() != null) {
                        throw new MappingException("Two Source Components Found!", null);
                    } else {
                        mapping.setSourceComponent(component);
                    }
                } else if ("target".equalsIgnoreCase(type)) {
                    if (mapping.getTargetComponent() != null) {
                        throw new MappingException("Two Target Components Found!", null);
                    } else {
                        mapping.setTargetComponent(component);
                    }
                } else if ("function".equalsIgnoreCase(type)) {
                    mapping.addFunctionComponent((FunctionComponent) component);
                } else {
                    throw new MappingException("Component type not understood : " + type, null);
                }
            }
        } catch (FileNotFoundException e) {
            throw new MappingException("Error in MapParser, File not found", e);
        }

    }

    /**
     * A method to assist with processComponents()
     */
    private BaseComponent generateCsvComponent(C_component cComponent, String kind) throws MappingException {
        BaseComponent csvComponent = new BaseComponent();
        csvComponent.setType(cComponent.getType());
        csvComponent.setUUID(cComponent.getUuid());
		csvComponent.setKind(kind);
		try {
            // setup the component.
            String fullFilePath = FileUtil.filenameLocate(mapfiledir, (String) cComponent.getLocation());
            FileReader fileReader = new FileReader(new File(fullFilePath));
            CSVMetaParserImpl parser = new CSVMetaParserImpl();
            CSVMetaResult csvMetaResult = parser.parse(fileReader);
            CSVMeta meta = csvMetaResult.getCsvMeta();
            csvComponent.setMeta(meta);
            // set it in the lookup table.
            metaLookupTable.put(cComponent.getUuid(), new CSVMetaLookup(meta));
            componentLookupTable.put(cComponent.getUuid(), csvComponent);
        } catch (MetaException e) {
            throw new MappingException(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            throw new MappingException(e.getMessage(), e);
        }
        return csvComponent;
    }

//    /**
//     * A method to assist with processComponents()
//     */
//    private BaseComponent generateCloneComponent(C_component cComponent, String kind) throws MappingException {
//        BaseComponent cloneComponent = new BaseComponent();
//        cloneComponent.setType(cComponent.getType());
//        cloneComponent.setUUID(cComponent.getUuid());
//		cloneComponent.setKind(kind);
//		try {
//            // setup the component.
//            String fullFilePath = FileUtil.filenameLocate(mapfiledir, (String) cComponent.getLocation());
//            FileReader fileReader = new FileReader(new File(fullFilePath));
//            HL7V3MetaFileParser parser = HL7V3MetaFileParser.instance();
//            HL7V3Meta meta = parser.parse(fileReader).getHl7V3Meta();
//            cloneComponent.setMeta(meta);
//            // set it in the lookup table.
//            metaLookupTable.put(cComponent.getUuid(), new CloneMetaLookup(meta));
//            componentLookupTable.put(cComponent.getUuid(), cloneComponent);
//        } catch (MetaException e) {
//            throw new MappingException(e.getMessage(), e);
//        } catch (FileNotFoundException e) {
//            throw new MappingException(e.getMessage(), e);
//        }
//        return cloneComponent;
//    }

    /**
     * A method to assist with processComponents()
     */
    private BaseComponent generateDatabaseComponent(C_component cComponent, String kind) throws MappingException {
        BaseComponent dbComponent = new BaseComponent();
        dbComponent.setType(cComponent.getType());
        dbComponent.setUUID(cComponent.getUuid());
		dbComponent.setKind(kind);
		try {
            // setup the component.
            String fullFilePath = FileUtil.filenameLocate(mapfiledir, (String) cComponent.getLocation());
            FileReader fileReader = new FileReader(new File(fullFilePath));
//            DatabaseMetaParserImpl parser = new DatabaseMetaParserImpl();
//            DatabaseMeta meta = parser.parse(fileReader).getDatabaseMeta();
//            dbComponent.setMeta(meta);
            // set it in the lookup table.
//            metaLookupTable.put(cComponent.getUuid(), new DatabaseMetaLookup(meta));
            componentLookupTable.put(cComponent.getUuid(), dbComponent);
//        } catch (MetaException e) {
//            throw new MappingException(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            throw new MappingException(e.getMessage(), e);
        }
        return dbComponent;
    }

    /**
     * A method to assist with processComponents()
     */
    private BaseComponent generateFunctionComponent(C_component cComponent, String kind) throws MappingException {
        FunctionComponent functionComponent = new FunctionComponent();
        functionComponent.setUUID(cComponent.getUuid());
        functionComponent.setType(cComponent.getType());
		functionComponent.setKind(kind);
		try {
            FunctionManager f = FunctionManager.getInstance();
            FunctionMeta functionMeta = f.getFunctionMeta(cComponent.getKind(), cComponent.getGroup(), cComponent.getName());
            functionComponent.setMeta(functionMeta);

            if ("constant".equalsIgnoreCase(functionMeta.getGroupName())) {
                C_data cData = cComponent.getC_data();
                if (cData == null) throw new MappingException("A 'constant' function MUST have a defined <data> in the XML.", null);
                FunctionConstant functionConstant = new FunctionConstant(functionMeta.getFunctionName(), cData.getType(), (String) cData.getValue());
                functionComponent.setFunctionConstant(functionConstant);
            }
            if("vocabulary".equalsIgnoreCase(functionMeta.getGroupName()))
            {
                C_data cData = cComponent.getC_data();
                if (cData == null) throw new MappingException("'Vocabulary' function group MUST have a defined <data> in the core XML.", null);
                FunctionVocabularyMapping vocabularyMapping = new FunctionVocabularyMapping();
                if(vocabularyMapping.getMethodNamePossibleList()[1].equalsIgnoreCase(functionMeta.getFunctionName()))
                    vocabularyMapping = new FunctionVocabularyMapping(cData.getType(), (String) cData.getValue(), true);
                else if(vocabularyMapping.getMethodNamePossibleList()[0].equalsIgnoreCase(functionMeta.getFunctionName()))
                    vocabularyMapping = new FunctionVocabularyMapping(cData.getType(), (String) cData.getValue(), false);
                else throw new MappingException("Invalid method name of 'Vocabulary' function group", new Throwable());
                functionComponent.setFunctionVocabularyMapping(vocabularyMapping);
            }

            // set it in the lookup table.
            metaLookupTable.put(cComponent.getUuid(), new FunctionMetaLookup(functionMeta));
            componentLookupTable.put(cComponent.getUuid(), functionComponent);

        } catch (Exception e) {
            throw new MappingException(e.getMessage(), e);
        }

        return functionComponent;
    }

    private void processLinks(C_links cLinks) {
        // for each of the links..
        for (int i = 0; i < cLinks.getC_linkCount(); i++) {
            C_link cLink = cLinks.getC_link(i);
            MapImpl map = new MapImpl();
            map.setUUID(cLink.getUuid());
            // a link must have two points.
            if (cLink.getC_linkpointerCount() != 2) {
                Log.logWarning (this, "Link ignored.  Must have two points : " +
                        cLink.getC_linkpointerCount() + " / " + cLink.getUuid());
            }
            // create the two map elements.
            BaseMapElement mapElement1 = null;
            BaseMapElement mapElement2 = null;
            String errMessage1 = new String();
            String errMessage2 = new String();
            try {
                mapElement1 = createBaseMapElement(cLink.getC_linkpointer(0));
            } catch (MappingException e) {
                errMessage1 = e.getMessage();
            }
            try {
                mapElement2 = createBaseMapElement(cLink.getC_linkpointer(1));
            } catch (MappingException e) {
                errMessage2 = e.getMessage();
            }

            // if there was a problem locating the links.
            if(mapElement2==null || mapElement1 == null){
                StringBuffer message = new StringBuffer("**LINK ERROR**\n");
                message.append("Map Element #1 : ");
                message.append(mapElement1 == null ? errMessage1 : mapElement1.toString());
                message.append("\nMap Element #2 : ");
                message.append(mapElement2 == null ? errMessage2 : mapElement2.toString());
                Log.logWarning(this, message);
            }else{
            // set the source/target respectively.
                if (mapElement1.isSource()) {
                    map.setSourceMapElement(mapElement1);
                } else {
                    map.setTargetMapElement(mapElement1);
                }

                if (mapElement2.isSource()) {
                    map.setSourceMapElement(mapElement2);
                } else {
                    map.setTargetMapElement(mapElement2);
                }
                // log a warning OR add it to the list.
                if (map.getSourceMapElement() == null || map.getTargetMapElement() == null) {
                    Log.logWarning(this, "Link ignored.  An error occured creating : " + cLink.getUuid());
                } else {
                    mapping.addMap(map);
                }
            }
        }
    }

    /**
     * A method to assist with processLinks()
     */
    private BaseMapElement createBaseMapElement(C_linkpointer cLinkPointer) throws MappingException {
        BaseMapElementImpl baseMapElement = new BaseMapElementImpl();
        // find the component.
        BaseComponent baseComponent = componentLookupTable.get(cLinkPointer.getComponentUuid());
        if (baseComponent == null)
            throw new MappingException("Error processing link (cmpuuid/datauuid) - ComponentUuid found: " + cLinkPointer.getComponentUuid() + "/" + cLinkPointer.getDataUuid(), null);
        // find the metadata object.
        MetaLookup metaLookup = metaLookupTable.get(cLinkPointer.getComponentUuid());
        if (metaLookup!=null)
        {
        MetaObject metaObject = metaLookup.lookup(cLinkPointer.getDataUuid());
	        if (metaObject == null)
	            throw new MappingException("Error processing link (cmpuuid/datauuid) - DataUuid not found: " + cLinkPointer.getComponentUuid() + "/" + cLinkPointer.getDataUuid(), null);
	        baseMapElement.setMetaObject(metaObject);
        }
        else 
        {
        	//find the target treeNode from targetTree
        	String mappedObjectXmlPath= cLinkPointer.getDataUuid();
        	//mifRootNode
        	baseMapElement.setMappedObjectXmlPath(mappedObjectXmlPath);
        }
        	
        baseMapElement.setComponent(baseComponent);
        return baseMapElement;
    }
    
    private void processViews(C_views cViews) throws MappingException {
        // for each view..
        for (int i = 0; i < cViews.getC_viewCount(); i++) {
            C_view cView = cViews.getC_view(i);
            // create it.
            View view = generateView(cView);
            // find the component that it's associated with.
            BaseComponent baseComponent = componentLookupTable.get(cView.getComponentUuid());
            // set it.
            if (baseComponent.getView() == null) {
                baseComponent.setView(view);
            } else {
                throw new MappingException("Multiple views found for component : " + cView.getComponentUuid(), null);
            }
        }
    }

    /**
     * A method to assist with processViews()
     */
    private View generateView(C_view cView) {
        ViewImpl view = new ViewImpl();
        view.setColor(Color.getColor(cView.getColor()));
        view.setHeight(cView.getHeight());
        view.setVisible(true);
        view.setWidth(cView.getWidth());
        view.setX(cView.getX());
        view.setY(cView.getY());
        return view;
    }
}
