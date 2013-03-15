/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.hl7.map.impl;

import gov.nih.nci.caadapter.castor.map.impl.*;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.function.FunctionConstant;
import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.common.map.BaseComponent;
import gov.nih.nci.caadapter.common.map.BaseMapElement;
import gov.nih.nci.caadapter.common.map.View;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import gov.nih.nci.caadapter.hl7.map.*;
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
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.11 $
 */

public class MapBuilderImpl {
    private static final String LOGID = "$RCSfile: MapBuilderImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/impl/MapBuilderImpl.java,v 1.11 2008-06-09 19:53:50 phadkes Exp $";

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
            cMapping.setType(mapping.getMappingType());
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
            if ( metaObject == null )
            {
                String mType = cMapping.getType();
                if (mType == null) mType = "";
                if (mType.indexOf("CSV_TO_XMI")>-1)
                {
                    cComponent.setKind("xmi");
                } else {
                    cComponent.setKind(Config.HL7_V3_DEFINITION_DEFAULT_KIND);
                }
            }
            else if (metaObject instanceof DatatypeBaseObject) {
            	cComponent.setKind(Config.HL7_V3_DEFINITION_DEFAULT_KIND);
            }
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
            cView.setComponentId(BaseMapElementImpl.getCastorComponentID(cComponent));
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
            {
            	sourcePointer.setKind(Config.MAP_COMPONENT_FUNCTION_TYPE);
            	sourcePointer.setXmlPath(buildFunctionPortXmlPath(sourcemap));
            }
            else
            {
            	BaseComponent srcBase=sourcemap.getComponent();
            	if (srcBase instanceof FunctionComponent)
            	{
            		sourcePointer.setXmlPath(buildFunctionPortXmlPath(sourcemap));
            		sourcePointer.setKind(Config.MAP_COMPONENT_FUNCTION_TYPE);
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
            {
                targetPointer.setKind(Config.HL7_V3_DEFINITION_DEFAULT_KIND);

                if (cMapping.getType() != null)
                {
                    if (cMapping.getType().indexOf("CSV_TO_XMI")>-1)
                        targetPointer.setKind("xmi");
                }
            }
            else if (targetmap.isComponentOfFunctionType())
            {
            	targetPointer.setKind(Config.MAP_COMPONENT_FUNCTION_TYPE);
            	targetPointer.setXmlPath(buildFunctionPortXmlPath(targetmap));
            }
            else
            {
            	BaseComponent trgtBase=targetmap.getComponent();
            	if (trgtBase instanceof FunctionComponent)
            	{
            		targetPointer.setXmlPath(buildFunctionPortXmlPath(targetmap));
            		targetPointer.setKind(Config.MAP_COMPONENT_FUNCTION_TYPE);
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


    private String buildFunctionPortXmlPath(BaseMapElement bm )
    {
    	FunctionComponent fc=(FunctionComponent)bm.getComponent();
    	String dataXmlPath=bm.getDataXmlPath();
    	String portName="";
    	if (dataXmlPath.indexOf("inputs")>-1)
    		portName=dataXmlPath.substring(dataXmlPath.indexOf("inputs"));
    	else if (dataXmlPath.indexOf("outputs")>-1)
    		portName=dataXmlPath.substring(dataXmlPath.indexOf("outputs"));
    	String rtnSt= fc.getXmlPath()+"."+portName;
//    	System.out.println("MapBuilderImpl.buildFunctionPortXmlPath()..return:"+rtnSt);
    	return rtnSt;
    }
}
