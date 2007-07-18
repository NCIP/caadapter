/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/impl/MapBuilderImpl.java,v 1.4 2007-07-18 20:41:44 wangeug Exp $
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
import gov.nih.nci.caadapter.castor.map.impl.C_source;
import gov.nih.nci.caadapter.castor.map.impl.C_target;
import gov.nih.nci.caadapter.castor.map.impl.C_view;
import gov.nih.nci.caadapter.castor.map.impl.C_views;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.function.FunctionConstant;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.map.BaseComponent;
import gov.nih.nci.caadapter.common.map.BaseMapElement;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
//import gov.nih.nci.caadapter.hl7.clone.meta.HL7V3Meta;
//import gov.nih.nci.caadapter.hl7.database.meta.DatabaseMeta;
import gov.nih.nci.caadapter.hl7.map.FunctionComponent;
import gov.nih.nci.caadapter.hl7.map.FunctionVocabularyMapping;
import gov.nih.nci.caadapter.hl7.map.Map;
import gov.nih.nci.caadapter.hl7.map.Mapping;
import gov.nih.nci.caadapter.hl7.map.MappingException;
import gov.nih.nci.caadapter.common.map.View;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;

/**
 * Builder of map files.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: wangeug $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.4 $
 */

public class MapBuilderImpl {
    private static final String LOGID = "$RCSfile: MapBuilderImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/impl/MapBuilderImpl.java,v 1.4 2007-07-18 20:41:44 wangeug Exp $";

    private static int FUNCTION = 0;
    private static int SOURCE = 1;
    private static int TARGET = 2;

    C_mapping cMapping = null;
    C_components cComponents = null;
    C_links cLinks = null;
    C_views cViews = null;

    public void build(OutputStream outputStream, Mapping mapping)
            throws MappingException {
        try {
            // setup the castor objects.
            cMapping = new C_mapping();
            cMapping.setVersion(new BigDecimal("1.2"));
            cComponents = new C_components();
            cLinks = new C_links();
            cViews = new C_views();
            cMapping.setC_components(cComponents);
            cMapping.setC_links(cLinks);
            cMapping.setC_views(cViews);

            // process the source and target.
            processComponentAndView(mapping.getSourceComponent(), SOURCE);
            processComponentAndView(mapping.getTargetComponent(), TARGET);

            // process the functions.
            List<FunctionComponent> functionComponents = mapping.getFunctionComponent();
            for (int i = 0; i < functionComponents.size(); i++) {
                FunctionComponent functionComponent = functionComponents.get(i);
                processComponentAndView(functionComponent, FUNCTION);
            }

            // process the maps.
            processMaps(mapping.getMaps());

            // set up the Source.
            StringWriter marshalString = new StringWriter();
            //cMapping.marshal(marshalString);
            Marshaller marshaller = new Marshaller(marshalString);
            marshaller.setSuppressXSIType(true);
            marshaller.marshal(cMapping);

            ByteArrayInputStream is = new ByteArrayInputStream(marshalString.toString().getBytes());

            // transform it.
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new StreamSource(is), new StreamResult(outputStream));

        } catch (IOException e) {
            throw new MappingException(e.getMessage(), e);
        }catch (MarshalException e) {
            throw new MappingException(e.getMessage(), e);
        } catch (ValidationException e) {
            throw new MappingException(e.getMessage(), e);
        } catch (TransformerException e) {
            throw new MappingException(e.getMessage(), e);
        }
    }

    private void processComponentAndView(BaseComponent baseComponent, int componentType) throws MappingException {
        // set up the component first.
        C_component cComponent = new C_component();
        MetaObject metaObject = baseComponent.getMeta();

        if (componentType == TARGET || componentType == SOURCE) {
            if(componentType == TARGET){
                cComponent.setType(Config.MAP_COMPONENT_TARGET_TYPE);
            }else if(componentType == SOURCE){
                cComponent.setType(Config.MAP_COMPONENT_SOURCE_TYPE);
            }
            String filePath = baseComponent.getFileAbsolutePath();
            if (filePath.startsWith(FileUtil.getWorkingDirPath())) filePath = filePath.replace(FileUtil.getWorkingDirPath(), Config.CAADAPTER_HOME_DIR_TAG);
            	cComponent.setLocation(filePath);

            if (metaObject ==null)
            	cComponent.setKind(Config.HL7_V3_DEFINITION_DEFAULT_KIND);
            else if (metaObject instanceof CSVMeta) {
                cComponent.setKind(Config.CSV_DEFINITION_DEFAULT_KIND);
            }
        } else if (componentType == FUNCTION) {
            FunctionMeta functionMeta = ((FunctionComponent)baseComponent).getMeta();
            cComponent.setKind(functionMeta.getKind());
            cComponent.setGroup(functionMeta.getGroupName());
            cComponent.setName(functionMeta.getFunctionName());
            String stringId=((FunctionComponent)baseComponent).getId();
            cComponent.setId(Integer.valueOf(stringId));
            
            //if("constant".equalsIgnoreCase(functionMeta.getFunctionName())){
            if(functionMeta.isConstantFunction())
            {
                FunctionConstant functionConstant = ((FunctionComponent)baseComponent).getFunctionConstant();
                if(functionConstant == null){
                    throw new MappingException("A 'constant' function MUST have a defined FunctionConstant object",null);
                }
                C_data cData = new C_data();
                cData.setType(functionConstant.getType());
                cData.setValue(functionConstant.getValue());
                cComponent.setC_data(cData);
            }
            if(functionMeta.isFunctionVocabularyMapping())
            {
                FunctionVocabularyMapping vocabularyMapping = ((FunctionComponent)baseComponent).getFunctionVocabularyMapping();

                if(vocabularyMapping == null){
                    throw new MappingException("A 'FunctionVocabularyMapping' function group MUST have a defined VocabularyMapping object",null);
                }
                C_data cData = new C_data();
                cData.setType(vocabularyMapping.getType());
                cData.setValue(vocabularyMapping.getValue());

                cComponent.setC_data(cData);
            }
            cComponent.setType(Config.MAP_COMPONENT_FUNCTION_TYPE);
        }
        cComponents.addC_component(cComponent);

        // setup the view second.
        View view = baseComponent.getView();
        if (view != null) {
            C_view cView = new C_view();
            if (view.getColor() != null) cView.setColor(view.getColor().toString());
            cView.setComponentId(view.getComponentId());//.setComponentXmlPath(baseComponent.getXmlPath());
            cView.setHeight(view.getHeight());
            cView.setWidth(view.getWidth());
            cView.setX(view.getX());
            cView.setY(view.getY());
            cViews.addC_view(cView);
        }else{
            throw new MappingException("All components must have view information", null);
        }
    }

    private void processMaps(List<Map> maps) {
        // for each of the maps.
        for (int i = 0; i < maps.size(); i++) {
            C_link cLink = new C_link();
            Map map = maps.get(i);
            //setup the source LinkPointer
            BaseMapElement sourcemap = map.getSourceMapElement();
            C_linkpointer sourcePointer = new C_linkpointer();
            sourcePointer.setXmlPath(sourcemap.getDataXmlPath());
            if (sourcemap.isComponentOfSourceType())
            	sourcePointer.setKind(Config.CSV_DEFINITION_DEFAULT_KIND);
            else if (sourcemap.isComponentOfTargetType())
            	sourcePointer.setKind(Config.HL7_V3_DEFINITION_DEFAULT_KIND);
            else if (sourcemap.isComponentOfFunctionType())
            	sourcePointer.setKind(Config.MAP_COMPONENT_FUNCTION_TYPE);
            else
            {
            	BaseComponent srcBase=sourcemap.getComponent();
            	if (srcBase instanceof FunctionComponent)
            	{
            		sourcePointer.setXmlPath(buildFunctionPortXmlPath((FunctionComponent)srcBase, sourcemap));
            		sourcePointer.setKind("function");
            	}
            	else
            		sourcePointer.setKind("default");
            }
             
            //setup the target LinkPointer
            BaseMapElement targetmap = map.getTargetMapElement();
            C_linkpointer targetPointer = new C_linkpointer();
            targetPointer.setXmlPath(targetmap.getDataXmlPath());
            if (targetmap.isComponentOfSourceType())
            	targetPointer.setKind(Config.CSV_DEFINITION_DEFAULT_KIND);
            else if (targetmap.isComponentOfTargetType())
            	targetPointer.setKind(Config.HL7_V3_DEFINITION_DEFAULT_KIND);
            else if (targetmap.isComponentOfFunctionType())
            	targetPointer.setKind(Config.MAP_COMPONENT_FUNCTION_TYPE);
            else
            {
            	BaseComponent trgtBase=targetmap.getComponent();
            	if (trgtBase instanceof FunctionComponent)
            	{
            		targetPointer.setXmlPath(buildFunctionPortXmlPath((FunctionComponent)trgtBase, targetmap));
            		targetPointer.setKind("function");
            	}
            	else
            		targetPointer.setKind("default");
            }
            //create the Link
            C_source linkSource=new C_source();
            linkSource.setC_linkpointer(sourcePointer);
            cLink.setC_source(linkSource);
            C_target linkTarget=new C_target();
            linkTarget.setC_linkpointer(targetPointer);
            cLink.setC_target(linkTarget);
            // assign it to the Links
            cLinks.addC_link(cLink);
        }
    }


    private String buildFunctionPortXmlPath(FunctionComponent fc, BaseMapElement bm )
    {
    	String dataXmlPath=bm.getDataXmlPath();
    	String portName="";
    	if (dataXmlPath.indexOf("inputs")>-1)
    		portName=dataXmlPath.substring(dataXmlPath.indexOf("inputs"));
    	else if (dataXmlPath.indexOf("outputs")>-1)
    		portName=dataXmlPath.substring(dataXmlPath.indexOf("outputs"));
    	return fc.getXmlPath()+"."+portName;
    }
}
