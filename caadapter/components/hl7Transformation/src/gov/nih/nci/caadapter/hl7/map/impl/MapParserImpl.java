/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/impl/MapParserImpl.java,v 1.5 2007-07-20 17:03:58 wangeug Exp $
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
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.function.FunctionConstant;
import gov.nih.nci.caadapter.common.function.FunctionManager;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.function.FunctionMetaLookup;
import gov.nih.nci.caadapter.common.map.BaseComponent;
import gov.nih.nci.caadapter.common.map.BaseMapElement;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;
import gov.nih.nci.caadapter.hl7.map.FunctionVocabularyMapping;
import gov.nih.nci.caadapter.hl7.map.Mapping;
import gov.nih.nci.caadapter.hl7.map.MappingException;
import gov.nih.nci.caadapter.hl7.map.MappingResult;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.common.map.View;
import gov.nih.nci.caadapter.common.map.ViewImpl;
//import gov.nih.nci.caadapter.hl7.validation.MapValidator;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Hashtable;

/**
 * Parser of map files.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.5 $
 * @date $Date: 2007-07-20 17:03:58 $
 * @since caAdapter v1.2
 */

public class MapParserImpl {
    private static final String LOGID = "$RCSfile: MapParserImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/impl/MapParserImpl.java,v 1.5 2007-07-20 17:03:58 wangeug Exp $";
    Mapping mapping = new MappingImpl();
    private Hashtable<String, MetaLookup> metaLookupTable = new Hashtable<String, MetaLookup>();
    private Hashtable<String, BaseComponent> componentLookupTable = new Hashtable<String, BaseComponent>();
    private String mapfiledir;

    public MappingResult parse(String mapfiledirectory, FileReader metafile)
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

                } else if (Config.HL7_V3_DEFINITION_DEFAULT_KIND.equalsIgnoreCase(kind)) {
//                	System.out.println("MapParserImpl.processComponents()...HL7v3MIF file loacation:"+cComponent.getLocation());
                    File hsmFile = FileUtil.fileLocate(mapfiledir, (String) cComponent.getLocation());
                    component = new BaseComponent();
                    component.setType(cComponent.getType());
                    component.setXmlPath(BaseMapElementImpl.getCastorComponentID(cComponent));//cComponent.getXmlPath());
                    component.setKind(kind);
                    component.setFile(hsmFile);
                    MIFClass srcMif;
                    //read mifclass
                    FileInputStream fis;
        			try {
        				fis = new FileInputStream ((File)hsmFile);
        				ObjectInputStream ois = new ObjectInputStream(fis);
        				srcMif = (MIFClass)ois.readObject();
        	    		ois.close();
        	    		fis.close();
        	    		component.setMeta(srcMif);
        	    		metaLookupTable.put(component.getKind(), new MifMetaLookup(srcMif));
        			} catch (FileNotFoundException e) {
        				// TODO Auto-generated catch block
        				Log.logException(this, e);
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				Log.logException(this, e);
        			} catch (ClassNotFoundException e) {
        				// TODO Auto-generated catch block
        				Log.logException(this, e);
        			}
                } 
                else if (Config.FUNCTION_DEFINITION_DEFAULT_KIND.equalsIgnoreCase(kind)) {
                    component = generateFunctionComponent(cComponent, kind);
                } else {
                    throw new MappingException("Component kind not understood : " + kind, null);
                }
                // set the component in the Mapping Object.
                // type = "source" OR "target"
                String type = cComponent.getType();
                if (Config.MAP_COMPONENT_SOURCE_TYPE.equalsIgnoreCase(type)) {
                    if (mapping.getSourceComponent() != null) {
                        throw new MappingException("Two Source Components Found!", null);
                    } else {
                        mapping.setSourceComponent(component);
                    }
                } else if (Config.MAP_COMPONENT_TARGET_TYPE.equalsIgnoreCase(type)) {
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
                componentLookupTable.put(BaseMapElementImpl.getCastorComponentID(cComponent), component);
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
        csvComponent.setXmlPath(BaseMapElementImpl.getCastorComponentID(cComponent));
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
            metaLookupTable.put(csvComponent.getKind(), new CSVMetaLookup(meta));
            componentLookupTable.put(csvComponent.getXmlPath(), csvComponent);
        } catch (MetaException e) {
            throw new MappingException(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            throw new MappingException(e.getMessage(), e);
        }
        return csvComponent;
    }


    /**
     * A method to assist with processComponents()
     */
    private BaseComponent generateFunctionComponent(C_component cComponent, String kind) throws MappingException {
        FunctionComponent functionComponent = FunctionComponent.getFunctionComponent();//new FunctionComponent();
        functionComponent.setXmlPath(BaseMapElementImpl.getCastorComponentID(cComponent));//cComponent.getXmlPath());
        functionComponent.setType(cComponent.getType());
		functionComponent.setKind(kind);
//		functionComponent.setId(cComponent.getId()+"");
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
            //the function ID is create from Singleton of current application
            //but the old ID is required to parse "view" and "link" 
            String lookupKey="function."+cComponent.getId();
            metaLookupTable.put(lookupKey, new FunctionMetaLookup(functionMeta));
            componentLookupTable.put(lookupKey, functionComponent);
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
            // a link must have two points.
            if (cLink.getC_source()==null||cLink.getC_target()==null)
            {
                Log.logWarning (this, "Link ignored.  Must have two points -- source: " +
                        cLink.getC_source() + " /target: " + cLink.getC_target());
            }
            // create the two map elements.
            BaseMapElement mapElement1 = null;
            BaseMapElement mapElement2 = null;
            String errMessage1 = new String();
            String errMessage2 = new String();
            try {
                mapElement1 = createBaseMapElement(cLink.getC_source().getC_linkpointer());
            } catch (MappingException e) {
                errMessage1 = e.getMessage();
            }
            try {
                mapElement2 = createBaseMapElement(cLink.getC_target().getC_linkpointer());
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
            	if (mapElement1.getMetaObject() instanceof CSVFieldMeta)
            	{
            		//csv component is always source
                    map.setSourceMapElement(mapElement1);
                    map.setTargetMapElement(mapElement2);
                }
            	else if (mapElement1.getMetaObject() instanceof FunctionMeta )
            	{
            		//function component always is target
            			map.setSourceMapElement(mapElement2);
                        map.setTargetMapElement(mapElement1);	
            	}
            	else
            	{
            		//element1 is MIF element
	                if (mapElement2.getMetaObject() instanceof FunctionMeta)
	                {
	                	//from MIF to function
	                    map.setSourceMapElement(mapElement1);
	                    map.setTargetMapElement(mapElement2);
	                }
	                else
	                {
	                	//from CSV to MIF
	                	map.setSourceMapElement(mapElement2);
	                    map.setTargetMapElement(mapElement1);
	                }
            	}
                // log a warning OR add it to the list.
                if (map.getSourceMapElement() == null || map.getTargetMapElement() == null) {
                    Log.logWarning(this, "Link ignored.  An error occured creating --source: " + cLink.getC_source().getC_linkpointer().getXmlPath()
                    			+ "/target: "+cLink.getC_target().getC_linkpointer().getXmlPath());
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
        // find the function component.
        BaseComponent baseComponent=null;
        String functionID="";
        String functionPortID="";
        if (cLinkPointer.getKind().equalsIgnoreCase(Config.MAP_COMPONENT_FUNCTION_TYPE))
        {
        	//build function ID:function.index.portname.index
        	String fullXmlPath=cLinkPointer.getXmlPath();
        	//remove port index
        	functionID=fullXmlPath.substring(0,fullXmlPath.lastIndexOf("."));
        	//remove port name
        	functionID=functionID.substring(0,functionID.lastIndexOf("."));
        	baseComponent=componentLookupTable.get(functionID);
        	//revmove "fuction.index."
        	functionPortID=fullXmlPath.substring(functionID.length()+1);
        }
        MetaLookup metaLookup = metaLookupTable.get(cLinkPointer.getKind());//.getComponentXmlPath());
        if (metaLookup==null)
        	metaLookup=metaLookupTable.get(functionID);
        if (metaLookup!=null)
        {
        	MetaObject metaObject = metaLookup.lookup(cLinkPointer.getXmlPath());//.getDataXmlPath());
	        if (metaObject==null)
	        {
	        	//look up function port
	        	for(Object metaKey:metaLookup.getAllKeys())
	        	{
	        		if (((String)metaKey).indexOf(functionPortID)>-1)
	        		{
	        			metaObject=metaLookup.lookup((String)metaKey);
	        			break;
	        		}
	        	}
	        }
	        	
        	if (metaObject == null)
	            throw new MappingException("Error processing link --meta object is not found --(linkKind/dataXmlPath): " + cLinkPointer.getKind() + "/" + cLinkPointer.getXmlPath(), null);
	        baseMapElement.setMetaObject(metaObject);
        }        	
        baseMapElement.setComponent(baseComponent);  
        baseMapElement.setXmlPath(cLinkPointer.getXmlPath());
        return baseMapElement;
    }
  
    private void processViews(C_views cViews) throws MappingException {
        // for each view..
        for (int i = 0; i < cViews.getC_viewCount(); i++) {
            C_view cView = cViews.getC_view(i);
            // create it.
            View view = generateView(cView);
            // find the component that it's associated with.
            BaseComponent baseComponent = componentLookupTable.get(cView.getComponentId());;//.getComponentXmlPath());
            // set it.
            if (baseComponent.getView() == null) {
                baseComponent.setView(view);
            } else {
                throw new MappingException("Multiple views found for component :view- " + cView.getComponentId(), null);
            }
        }
    }

    /**
     * A method to assist with processViews()
     */
    private View generateView(C_view cView) {
        ViewImpl view = ViewImpl.getViewImpl();//new ViewImpl();
        view.setColor(Color.getColor(cView.getColor()));
        view.setHeight(cView.getHeight());
        view.setVisible(true);
        view.setWidth(cView.getWidth());
        view.setX(cView.getX());
        view.setY(cView.getY());
        return view;
    }
}
