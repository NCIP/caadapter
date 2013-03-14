/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
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
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.XmlToMIFImporter;
import gov.nih.nci.caadapter.common.map.View;
import gov.nih.nci.caadapter.common.map.ViewImpl;


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
 * @version $Revision: 1.21 $
 * @date $Date: 2009-02-02 14:55:01 $
 * @since caAdapter v1.2
 */

public class MapParserImpl {
    private static final String LOGID = "$RCSfile: MapParserImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/impl/MapParserImpl.java,v 1.21 2009-02-02 14:55:01 wangeug Exp $";
    Mapping mapping = new MappingImpl();
    private Hashtable<String, MetaLookup> metaLookupTable = new Hashtable<String, MetaLookup>();
    private Hashtable<String, BaseComponent> componentLookupTable = new Hashtable<String, BaseComponent>();
    private String mapfiledir;

    public ValidatorResults parse(String mapfiledirectory, FileReader metafile)
    {
        this.mapping = new MappingImpl();
        this.mapfiledir = mapfiledirectory;
        ValidatorResults validatorResults = new ValidatorResults();

        try {
            C_mapping cMapping = (C_mapping) C_mapping.unmarshalC_mapping(metafile);
            mapping.setMappingType(cMapping.getType());
            processComponents(cMapping.getC_components());
            processLinks(cMapping.getC_links(), validatorResults);
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

        return validatorResults;
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
                    System.out.println(mapfiledir + "  " + (String) cComponent.getLocation());
                    System.out.println(FileUtil.filenameLocate(mapfiledir, (String) cComponent.getLocation()));
                    File scmFile = new File(FileUtil.filenameLocate(mapfiledir, (String) cComponent.getLocation()));
                    component.setFile(scmFile);

                } else if (Config.HL7_V3_DEFINITION_DEFAULT_KIND.equalsIgnoreCase(kind)) {
                    File hsmFile = new File(FileUtil.filenameLocate(mapfiledir, (String) cComponent.getLocation()));
                    component = new BaseComponent();
                    component.setType(cComponent.getType());
                    component.setXmlPath(BaseMapElementImpl.getCastorComponentID(cComponent));//cComponent.getXmlPath());
                    component.setKind(kind);
                    component.setFile(hsmFile);
                    MIFClass srcMif=null;
                    //read mifclass
                    FileInputStream fis;
//        			try {
        				if (hsmFile.getName().endsWith(".h3s"))
        				{
//        					fis = new FileInputStream ((File)hsmFile);
//	        				ObjectInputStream ois = new ObjectInputStream(fis);
//	        				srcMif = (MIFClass)ois.readObject();
//	        	    		ois.close();
//	        	    		fis.close();
//        				}
//        				else
//        				{
        					XmlToMIFImporter xmlImporter=new XmlToMIFImporter();
        					srcMif=xmlImporter.importMifFromXml(hsmFile);
        				}
        	    		component.setMeta(srcMif);
        	    		metaLookupTable.put(component.getKind(), new MifMetaLookup(srcMif));
//        			} catch (FileNotFoundException e) {
//        				// TODO Auto-generated catch block
//        				Log.logException(this, e);
//        			} catch (IOException e) {
//        				// TODO Auto-generated catch block
//        				Log.logException(this, e);
//        			} catch (ClassNotFoundException e) {
//        				// TODO Auto-generated catch block
//        				Log.logException(this, e);
//        			}
                }
                else if (Config.FUNCTION_DEFINITION_DEFAULT_KIND.equalsIgnoreCase(kind)) {
                    component = generateFunctionComponent(cComponent, kind);
                } else if(kind.equalsIgnoreCase("xmi"))
                {
                	//create xmi component
                	File xmiFile = new File(FileUtil.filenameLocate(mapfiledir, (String) cComponent.getLocation()));
                    component = new BaseComponent();
                    component.setType(cComponent.getType());
                    component.setXmlPath(BaseMapElementImpl.getCastorComponentID(cComponent));//cComponent.getXmlPath());
                    component.setKind(kind);
                    component.setFile(xmiFile);

                }
                else if(kind.equalsIgnoreCase("v2"))
                {
                	//create v2Meta component
//                	File xsdFile = new File(FileUtil.filenameLocate(mapfiledir, (String) cComponent.getLocation()));
                    component = new BaseComponent();
                    component.setType(cComponent.getType());
                    component.setKind(cComponent.getLocation().toString());

//                    component.setFile(xsdFile);
                }
                else {
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

    private void processLinks(C_links cLinks,  ValidatorResults vResults) {
        // for each of the links..
        for (int i = 0; i < cLinks.getC_linkCount(); i++) {
            C_link cLink = cLinks.getC_link(i);
            MapImpl map = new MapImpl();
            // a link must have two points.
            if (cLink.getC_source()==null||cLink.getC_target()==null)
            {
            	String warningMsg="Link ignored.  Must have two points -- source: " +
                cLink.getC_source() + " /target: " + cLink.getC_target();
                Log.logWarning (this, warningMsg);
                Message msg = MessageResources.getMessage("GEN0", new Object[]{warningMsg});
                vResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));

            }
            // create the two map elements.
            BaseMapElement mapElement1 = null;
            BaseMapElement mapElement2 = null;
            String errMessage1 = new String();
            String errMessage2 = new String();

            try
            {
                mapElement1 = createBaseMapElement(cLink.getC_source().getC_linkpointer());
            }
            catch (MappingException e)
            {
                errMessage1 = e.getMessage();
            }

            try
            {
                mapElement2 = createBaseMapElement(cLink.getC_target().getC_linkpointer());
            }
            catch (MappingException e)
            {
                errMessage2 = e.getMessage();
            }

            // if there was a problem locating the links.
            if ((mapElement2 == null)||(mapElement1 == null))
            {
                StringBuffer message = new StringBuffer("**LINK ERROR**\n");
                message.append("Map Element #1 : ");
                message.append(mapElement1 == null ? errMessage1 : mapElement1.toString());
                message.append("\nMap Element #2 : ");
                message.append(mapElement2 == null ? errMessage2 : mapElement2.toString());
                Log.logWarning(this, message);
                Message msg = MessageResources.getMessage("GEN0", new Object[]{message});
                vResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));
            }
            else
            {
                map.setSourceMapElement(mapElement1);
                map.setTargetMapElement(mapElement2);

                /*
                // set the source/target respectively.
            	if ((mapElement1.getMetaObject() instanceof CSVFieldMeta)
            			||(mapElement1.getMetaObject() instanceof CSVSegmentMeta))
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
            	*/
                // log a warning OR add it to the list.
                if (map.getSourceMapElement() == null || map.getTargetMapElement() == null)
                {
                    String waringMsg="Link ignored.  An error occured creating --source: " + cLink.getC_source().getC_linkpointer().getXmlPath()
        			+ "/target: "+cLink.getC_target().getC_linkpointer().getXmlPath();

                	Log.logWarning(this, waringMsg);
                    Message msg = MessageResources.getMessage("GEN0", new Object[]{waringMsg});
                    vResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.WARNING, msg));
                }
                else
                {
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
        String xmlPathToSet=cLinkPointer.getXmlPath();
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
	        	if (!functionPortID.equals(""))
	        	{
		        	for(Object metaKey:metaLookup.getAllKeys())
		        	{
		        		if (((String)metaKey).indexOf(functionPortID)>-1)
		        		{
		        			metaObject=metaLookup.lookup((String)metaKey);
		        			break;
		        		}
		        	}
	        	}
	        }
	        if (metaObject==null&&(metaLookup instanceof MifMetaLookup))
	        {//
//	        	backward compatible since ComplexDataType
	        	String orgXmlPath=cLinkPointer.getXmlPath();
	        	System.out.println("MapParserImpl.createBaseMapElement()..original xmlPath:"+orgXmlPath);
	        	xmlPathToSet=orgXmlPath+"00";
	        	System.out.println("MapParserImpl.createBaseMapElement()..xmlPathToSet:"+xmlPathToSet);
	        	metaObject = metaLookup.lookup(xmlPathToSet);//.getDataXmlPath());
	        	if (metaObject==null)
	        	{
	        		xmlPathToSet=orgXmlPath.substring(0,orgXmlPath.lastIndexOf("."))+"00"+orgXmlPath.substring(orgXmlPath.lastIndexOf("."));
		        	System.out.println("MapParserImpl.createBaseMapElement()..xmlPathToSet:"+xmlPathToSet);
	        		metaObject = metaLookup.lookup(xmlPathToSet);//.getDataXmlPath());
	        	}
	        }

        	if (metaObject == null)
	            throw new MappingException("Error processing link --meta object is not found --(linkKind/dataXmlPath): " + cLinkPointer.getKind() + "/" + cLinkPointer.getXmlPath(), null);
	        baseMapElement.setMetaObject(metaObject);
        }
        baseMapElement.setComponent(baseComponent);
        baseMapElement.setXmlPath(xmlPathToSet);//cLinkPointer.getXmlPath());
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

	public Mapping getMapping() {
		return mapping;
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.20  2008/11/21 16:17:33  wangeug
 * HISTORY :Move back to HL7 module from common module
 * HISTORY :
 * HISTORY :Revision 1.19  2008/11/17 20:08:53  wangeug
 * HISTORY :Move FunctionComponent and VocabularyMap from HL7 module to common module
 * HISTORY :
 * HISTORY :Revision 1.18  2008/10/16 14:41:27  wangeug
 * HISTORY :parse mapping between V2Meta and H3S
 * HISTORY :
 * HISTORY :Revision 1.17  2008/09/29 15:45:55  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */