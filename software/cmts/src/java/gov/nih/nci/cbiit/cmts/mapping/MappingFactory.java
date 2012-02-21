/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.mapping;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.zip.ZipEntry;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

//import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;

import gov.nih.nci.cbiit.cmts.common.XSDParser;
import gov.nih.nci.cbiit.cmts.core.AttributeMeta;
import gov.nih.nci.cbiit.cmts.core.BaseMeta;
import gov.nih.nci.cbiit.cmts.core.Component;
import gov.nih.nci.cbiit.cmts.core.ComponentType;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.core.KindType;
import gov.nih.nci.cbiit.cmts.core.LinkType;
import gov.nih.nci.cbiit.cmts.core.LinkpointType;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.core.MetaConstants;
import gov.nih.nci.cbiit.cmts.core.TagType;
import gov.nih.nci.cbiit.cmts.core.Mapping.Components;
import gov.nih.nci.cbiit.cmts.core.Mapping.Links;
import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
//import gov.nih.nci.cbiit.cmts.util.ZipFileUtil;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.hl7.v2v3.tools.ZipUtil;

/**
 * This class is used to generate CMTS Mapping
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.6 $
 * @date       $Date: 2009-10-15 18:36:23 $
 *
 */
public class MappingFactory
{
    public static XSDParser sourceParser;
    public static XSDParser targetParser;
    public static ElementMeta sourceHeadMeta;
    public static ElementMeta targetHeadMeta;

    public static boolean loadMetaXSD(Mapping m, XSDParser schemaParser,String rootNS, String root, ComponentType type) {

        ElementMeta e = schemaParser.getElementMeta(rootNS, root);
        if(e==null)
        {
            e = schemaParser.getElementMetaFromComplexType(rootNS, root, MetaConstants.SCHEMA_LAZY_LOADINTG_INITIAL);
            //if(e==null)System.out.println("CCCCC HHH2 : " + schemaParser.getSchemaURI() + " : " + e);
        }
        //else System.out.println("CCCCC HHH0 : " + schemaParser.getSchemaURI() + " : " + e);
        //if (type==ComponentType.SOURCE) sourceHeadMeta = e;
        //else targetHeadMeta = e;

        if(e==null) return false;

        if (m.getComponents()!=null)
            for (Component mapComp:m.getComponents().getComponent())
            {
                if (mapComp.getRootElement()!=null
                        &mapComp.getType().equals(type))
                {
                    //clear the childElement list and attribute list for backward compatible
                    mapComp.getRootElement().getChildElement().clear();
                    mapComp.getRootElement().getChildElement().addAll(e.getChildElement());
                    mapComp.getRootElement().getAttrData().clear();
                    mapComp.getRootElement().getAttrData().addAll(e.getAttrData());
                    if (type==ComponentType.SOURCE) sourceHeadMeta = mapComp.getRootElement();
                    else targetHeadMeta = mapComp.getRootElement();
                    return true;
                }
            }
        Component endComp = new Component();
        endComp.setKind(KindType.XML);
        endComp.setId(getNewComponentId(m));
        endComp.setLocation(schemaParser.getSchemaURI());

        endComp.setRootElement(e);
        endComp.setType(type);
        m.getComponents().getComponent().add(endComp);
        return true;
    }

    private static String getNewComponentId(Mapping m){
        if(m.getComponents() == null)
            m.setComponents(new Components());
        int num = 0;
        for(Component c:m.getComponents().getComponent()){
            int tmp = -1;
            try{
                tmp = Integer.parseInt(c.getId());
            }catch(Exception ignored){}
            if(tmp>=num)
                num = tmp+1;
        }
        return String.valueOf(num);
    }
    /**
     * add link to specified Mapping
     * @param m - Mapping object to load into
     * @param srcComponentId -  source component id
     * @param srcPath - source object path
     * @param tgtComponentId - target component id
     * @param tgtPath - target object path
     */
    public static void addLink(Mapping m, String srcComponentId, String srcPath, String tgtComponentId, String tgtPath) {
        LinkType l = new LinkType();
        LinkpointType lp = new LinkpointType();
        lp.setComponentid(srcComponentId);
        lp.setId(srcPath);
        l.setSource(lp);
        lp = new LinkpointType();
        lp.setComponentid(tgtComponentId);
        lp.setId(tgtPath);
        l.setTarget(lp);
        if(m.getLinks() == null) m.setLinks(new Links());
        m.getLinks().getLink().add(l);
    }
    public static Mapping loadMapping(File f) throws JAXBException
    {
        return loadMapping(f, null, null, null);
    }
    public static Mapping loadMapping(File f, File sourceXSD, File targetXSD, Mapping mapData) throws JAXBException

    {
        Mapping mapLoaded = null;
        String mappingParentPath = null;
        if (mapData == null)
        {
            System.out.println("MappingFactory.loadMapping()...mappingFile:"+f.getAbsolutePath());
            mappingParentPath=f.getAbsoluteFile().getParentFile().getAbsolutePath();
            System.out.println("MappingFactory.loadMapping()..mapping Parent:"+mappingParentPath);
            JAXBContext jc=null;
            jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmts.core" );
    //		jc=com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl.newInstance("gov.nih.nci.cbiit.cmts.core");

            Unmarshaller u = jc.createUnmarshaller();
            JAXBElement<Mapping> jaxbElmt = null;
            try
            {
                jaxbElmt = u.unmarshal(new StreamSource(f), Mapping.class);
            }
            catch(UnmarshalException ee1)
            {
                throw new JAXBException(ActionConstants.MESSAGE_NOT_A_MAPPING_FILE  + " (MappingFactory.loadMapping()) : " + f.getName());
            }
            catch(Exception ee2)
            {
                throw new JAXBException("Mapping file opening error : " + f.getAbsolutePath() + "\n" + ee2.getMessage());
            }
            mapLoaded=jaxbElmt.getValue();
        }
        else
        {
            mapLoaded=mapData;
            if (f == null)
            {
                if (sourceXSD != null)
                    mappingParentPath=sourceXSD.getAbsoluteFile().getParentFile().getAbsolutePath();
                else if (targetXSD != null)
                    mappingParentPath=targetXSD.getAbsoluteFile().getParentFile().getAbsolutePath();
            }
            else mappingParentPath=f.getAbsoluteFile().getParentFile().getAbsolutePath();
            System.out.println("MappingFactory.loadMapping()2..mapping Parent:"+mappingParentPath);
        }
        System.out.println("MappingFactory.loadMapping()...mapLoaded:"+mapLoaded);
        //re-connect the meta structure for source and target schemas
        List<Component> listCom = null;
        try
        {
            listCom = mapLoaded.getComponents().getComponent();
        }
        catch(Exception ee)
        {
            throw new JAXBException(ActionConstants.MESSAGE_NOT_A_MAPPING_FILE + " This File is not a valid mapping file. : " + f.getName());
        }
        if ((listCom == null)||(listCom.size() == 0)) throw new JAXBException("This File is not a valid mapping file or empty : " + f.getAbsolutePath());
        for (Component mapComp:listCom)
        {
            if (mapComp.getRootElement() == null) continue;
            String xsdLocation = mapComp.getLocation();
            try
            {

                if ((mapComp.getType() != ComponentType.SOURCE)&&
                    (mapComp.getType() != ComponentType.TARGET)) continue;

                Object xsdObj = null;
                xsdLocation = xsdLocation.trim();
                if (xsdLocation.toLowerCase().startsWith("file:/"))
                {
                    String xsdLocation3 = xsdLocation.substring(6);
                    while(xsdLocation3.startsWith("/")) xsdLocation3 = xsdLocation3.substring(1);
                    File f1 = new File(xsdLocation3);
                    if ((f1.exists())||(f1.isFile())) xsdObj = f1;
                }

                String xsdLocation2 = mappingParentPath+File.separator+mapComp.getLocation();

                //File xsdFile = null;
                String sORt = "";
                while(xsdObj == null)
                {
                    File xsdFile = new File(xsdLocation2);
                    if ((xsdFile.exists())&&(xsdFile.isFile()))
                    {
                        xsdLocation = xsdLocation2;
                        xsdObj = xsdFile;
                        break;
                    }
                    xsdFile = null;
                    xsdObj = null;
                    if (xsdLocation.equals(xsdLocation2)) break;
                    else xsdLocation2 = xsdLocation;
                }

                System.out.println("MappingFactory.loadMapping()..schema:"+mapComp.getType()+"="+xsdLocation);
                XSDParser metaParser = new XSDParser();
                if (mapComp.getType()==ComponentType.SOURCE)
                {
                    sORt = "Source";
                    if ((sourceXSD != null)&&(sourceXSD.exists())&&(sourceXSD.isFile()))
                    {
                        //xsdFile = sourceXSD;
                        xsdObj = sourceXSD;
                    }
                    //if (sourceXSDObj != null) xsdObj = sourceXSDObj;
                    else sourceParser=metaParser;
                }
                else
                {
                    sORt = "Target";
                    if ((targetXSD != null)&&(targetXSD.exists())&&(targetXSD.isFile()))
                    {
                        //xsdFile = targetXSD;
                        xsdObj = targetXSD;
                    }
                    //if (targetXSDObj != null) xsdObj = targetXSDObj;
                    else targetParser=metaParser;
                }

                if (xsdObj != null)
                {
                    //System.out.println("CCCCC HHH A : " + xsdFile.getAbsolutePath());
                    if (xsdObj instanceof File) metaParser.loadSchema(((File)xsdObj).toURI().toString(), null);
                    else metaParser.loadSchema(xsdObj.toString(), null);
                }
                else  metaParser.loadSchema(xsdLocation, null);

//                    mapComp.setLocation(xsdLocation);


                String erMessage = null;
                while(true)
                {
                    boolean res = MappingFactory.loadMetaXSD(mapLoaded, metaParser, mapComp.getRootElement().getNameSpace(),mapComp.getRootElement().getName(),mapComp.getType() );
                    if (res) break;

                    if (xsdObj != null)
                        throw new JAXBException("Namespace or root element name is mismatched with "+sORt+" XSD file. : " + xsdObj.toString());
                    String c = null;
                    try
                    {
                        c = FileUtil.downloadFromURLtoTempFile(xsdLocation);
                    }
                    catch(IOException ie)
                    {
                        erMessage = ie.getMessage();
                        c = null;
                        //throw new JAXBException(ie.getMessage());
                    }
                    if ((c != null)&&(!c.trim().equals("")))
                    {
                        File fz = new File(c);
                        if ((fz.exists())&&(fz.isFile()))
                        {
                            fz.delete();
                            throw new JAXBException("Namespace or root element name is mismatched with "+sORt+" XSD location. : " + xsdLocation);
                        }
                        //else throw new JAXBException("Invalid URL as "+sORt+" XSD location. : " + xsdLocation);
                    }
//                    String xsdF1 = xsdLocation;
//                    while(true)
//                    {
//                        int idx = xsdF.indexOf(File.separator);
//                        if (idx >= 0)
//                        {
//                            xsdF = xsdF.substring(idx + File.separator.length());
//                            continue;
//                        }
//                        idx = xsdF.indexOf("/");
//                        if (idx >= 0)
//                        {
//                            xsdF = xsdF.substring(idx + 1);
//                            continue;
//                        }
//                        break;
//                    }
                    File currentDir = new File(mappingParentPath);

                    String messageInvalidXSDPath = "Not Found this " + sORt + " schema file in the map data. : " + xsdLocation;
                    if ((!currentDir.exists())||(!currentDir.isDirectory()))
                        throw new JAXBException(messageInvalidXSDPath + "  .");
                        //throw new JAXBException("Invalid "+sORt+" XSD location. (1) : " + xsdLocation);
                    File[] list = currentDir.listFiles();
                    String newLocation = null;
                    int n = 0;
                    while(true)
                    {
                        File child_sORt = null;
                        for (File file:list)
                        {
                            if (file.isDirectory())
                            {
                                if (file.getName().equalsIgnoreCase(sORt)) child_sORt = file;
                            }
                            if (!file.isFile()) continue;
                            String fn = file.getName();
                            if (fn.toLowerCase().endsWith(".zip")) {}
                            else if (fn.toLowerCase().endsWith(".jar")) {}
                            else continue;
                            //System.out.println("CCCC zip file Name:" + fn);
                            ZipUtil zipUtil = null;

                            try
                            {
                                zipUtil = new ZipUtil(file.getAbsolutePath());
                            }
                            catch(IOException ie)
                            {
                                //System.out.println("CCCC irir : " + ie.getMessage());
                                continue;
                            }
                            ZipEntry zipEntry = null;
                            zipEntry = getXSDZipEntryFromZip(zipUtil, xsdLocation);


                            /*
                            try
                            {
                                zipEntry = zipUtil.searchEntryWithWholeName(xsdF);
                            }
                            catch(IOException ie)
                            {
                                List<String> list1 = zipUtil.getEntryNames();
                                String select = null;
                                int number = -1;
                                for(String str:list1)
                                {
                                    int count = -1;

                                    if (str.equalsIgnoreCase(xsdF))
                                    {
                                        select = str;
                                        break;
                                    }

                                    if (str.toLowerCase().endsWith("/"+xsdF.toLowerCase())) {}
                                    else if (str.toLowerCase().endsWith("\\"+xsdF.toLowerCase())) {}
                                    else continue;
                                    String loc = xsdLocation.substring(0, xsdLocation.length()-xsdF.length());
                                    String ent = str.substring(0, str.length()-xsdF.length());
                                    int cnt2 = 0;
                                    for(int i=0;i<ent.length();i++)
                                    {
                                        if (i >= loc.length()) break;
                                        String acharL = loc.substring(loc.length()-(i+1), loc.length()-i);
                                        String acharE = ent.substring(ent.length()-(i+1), ent.length()-i);
                                        if (acharL.equals("\\")) acharL = "/";
                                        if (acharE.equals("\\")) acharE = "/";
                                        if (acharL.equalsIgnoreCase(acharE)) cnt2++;
                                        else break;
                                    }
                                    if (cnt2 > 0) count = cnt2;

                                    if (count > number)
                                    {
                                        number = count;
                                        select = str;
                                    }
                                }
                                if (select != null)
                                {
                                    zipEntry = zipUtil.getZipFile().getEntry(select);
                                }
                                else zipEntry = null;
                            }
                            */

                            //System.out.println("CCCC new XSD location:" + xsdF);
                            if (zipEntry == null)
                            {
                                zipUtil.getZipFile().close();
                                continue;
                            }
                            newLocation = zipUtil.getAccessURL(zipEntry);

                            zipUtil.getZipFile().close();

                            if (newLocation != null) break;
                        }
                        if (newLocation != null) break;
                        if (n > 0) break;
                        if (child_sORt == null) break;

                        list = child_sORt.listFiles();
                        n++;
                    }
                    if (newLocation == null)
                    {
                        //System.out.println("CCCC new XSD location:" + xsdF);
                        //if (erMessage == null)
                        int idxx = xsdLocation.toLowerCase().indexOf("file:/");
                        if ((xsdLocation.toLowerCase().startsWith("jar:"))||
                            (xsdLocation.toLowerCase().startsWith("zip:"))) idxx = -1;
                        if (idxx > 0)
                        {
                            newLocation = xsdLocation.substring(idxx);
                        }
                        else
                        {
                            throw new JAXBException(messageInvalidXSDPath + "  ..");
                            //throw new JAXBException("Invalid "+sORt+" XSD location. (2) : " + xsdLocation);
                        }
                    }
                    //System.out.println("CCCC new XSD location:" + newLocation);
                    metaParser.loadSchema(newLocation, null);
                    res = MappingFactory.loadMetaXSD(mapLoaded, metaParser, mapComp.getRootElement().getNameSpace(),mapComp.getRootElement().getName(),mapComp.getType() );
                    if (!res) //throw new JAXBException("Invalid "+sORt+" XSD location. (3) : " + xsdLocation);
                    {
                        String msg1 = "Namespace or root element name is mismatched with "+sORt+" XSD file. : " + xsdLocation;
                        if (newLocation.toLowerCase().startsWith("file:/"))
                        {
                            //msg1 = "Invalid "+sORt+" XSD location. (3) : " + xsdLocation;
                            msg1 = messageInvalidXSDPath + "  ...";
                        }
                        throw new JAXBException(msg1);
                    }

                    break;
                }
                mapComp.setLocation(metaParser.getSchemaURI());
                if (mapComp.getType()==ComponentType.SOURCE)
                {
                    if ((sourceXSD != null)&&(sourceXSD.exists())&&(sourceXSD.isFile())) sourceParser=metaParser;
                    //if (sourceXSDObj != null) sourceParser=metaParser;
                }
                else
                {
                    if ((targetXSD != null)&&(targetXSD.exists())&&(targetXSD.isFile())) targetParser=metaParser;
                    //if (targetXSDObj != null) targetParser=metaParser;
                }

            }
            catch(JAXBException ee)
            {
                String msg = ee.getMessage();
                if ((msg == null)||(msg.trim().equals("")))
                    msg = ee.getClass().getCanonicalName()+":"+ "Failed to read or parse schema document (1) - " + xsdLocation;
                //System.out.println("CCCC =======");
                ee.printStackTrace();
                throw new JAXBException(msg);

            }
            catch(Exception ee)
            {
                String msg = ee.getMessage();
                if ((msg == null)||(msg.trim().equals("")))
                    msg = "Failed to read or parse schema document - " + xsdLocation;
                ee.printStackTrace();
                throw new JAXBException(ee.getClass().getCanonicalName()+":"+msg);

            }
        }


        if ((mapLoaded.getTags().getTag() != null)&&(mapLoaded.getTags().getTag().size() > 0))
        {
            Hashtable <String, BaseMeta> srcMetaHash=new Hashtable <String, BaseMeta>();
            Hashtable <String, BaseMeta> trgtMetaHash=new Hashtable <String, BaseMeta>();
            //pre-process mapping for annotation
            for (Component mapComp:mapLoaded.getComponents().getComponent())
            {
                if (mapComp.getType().value().equals(ComponentType.SOURCE.value()))
                {
                    mapComp.getRootElement().setId("/"+mapComp.getRootElement().getName());
                    processMeta(srcMetaHash, mapComp.getRootElement(), null, mapLoaded.getTags().getTag());
                }
                else if (mapComp.getType().value().equals(ComponentType.TARGET.value()))
                {
                    mapComp.getRootElement().setId("/"+mapComp.getRootElement().getName());
                    processMeta(trgtMetaHash,mapComp.getRootElement(), null, mapLoaded.getTags().getTag());
                }
            }

            //sort tags with precedence from low to high
            // 0 -- componentType; enumValues: source, taret, function
            // 1 -- key; allowing value: entry's xpath
            // 2 -- componentKind; enumValues: choice, clone
            // 3 -- value; the ordered ASCII characters
            Collections.sort(mapLoaded.getTags().getTag());
            for (TagType tag:mapLoaded.getTags().getTag())
            {
                if (tag.getComponentType().value().equals(ComponentType.SOURCE.value()))
                {
                    try
                    {
                        processAnnotationTag(tag, srcMetaHash, mapLoaded.getTags().getTag());
                    }
                    catch(Exception ee)
                    {
                        throw new JAXBException("Invalid Source tag link ID:" + ee.getMessage());
                    }
                }
                else if (tag.getComponentType().value().equals(ComponentType.TARGET.value()))
                {
                    try
                    {
                        processAnnotationTag (tag, trgtMetaHash, mapLoaded.getTags().getTag());
                    }
                    catch(Exception ee)
                    {
                        throw new JAXBException("Invalid Target tag link ID:" + ee.getMessage());
                    }
                }
            }
        }




        if ((mapLoaded.getLinks().getLink() != null)&&(mapLoaded.getLinks().getLink().size() > 0))
        {

            for (LinkType link:mapLoaded.getLinks().getLink())
            {
                if ((link.getSource().getComponentid().equals("0"))||
                    (link.getSource().getComponentid().equals("1")))
                {
                    String id = link.getSource().getId();
                    //System.out.println("CCCX Source("+link.getSource().getComponentid()+") : id=" + id);
                    try
                    {
                        BaseMeta meta = searchElementMeta(id);
                    }
                    catch(Exception ee)
                    {
                        throw new JAXBException("Invalid Source link ID:" + ee.getMessage());
                    }
                    //if (meta == null) meta = searchElementMeta(id, ComponentType.TARGET);
                }

                if ((link.getTarget().getComponentid().equals("0"))||
                    (link.getTarget().getComponentid().equals("1")))
                {
                    String id = link.getTarget().getId();
                    //System.out.println("CCCX Target("+link.getTarget().getComponentid()+") : id=" + id);
                    try
                    {
                        BaseMeta meta = searchElementMeta(id);
                    }
                    catch(Exception ee)
                    {
                        throw new JAXBException("Invalid Target link ID:" + ee.getMessage());
                    }
                    //if (meta == null) meta = searchElementMeta(id, ComponentType.SOURCE);
                }

            }
        }



        return mapLoaded;
        //if (mapData == null) return  jaxbElmt.getValue();
    }
/*
    {
        Mapping mapLoaded = null;
        String mappingParentPath = null;
        if (mapData == null)
        {
            System.out.println("MappingFactory.loadMapping()...mappingFile:"+f.getAbsolutePath());
            mappingParentPath=f.getAbsoluteFile().getParentFile().getAbsolutePath();
            System.out.println("MappingFactory.loadMapping()..mapping Parent:"+mappingParentPath);
            JAXBContext jc=null;
            jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmts.core" );
    //		jc=com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl.newInstance("gov.nih.nci.cbiit.cmts.core");

            Unmarshaller u = jc.createUnmarshaller();
            JAXBElement<Mapping> jaxbElmt = u.unmarshal(new StreamSource(f), Mapping.class);
            mapLoaded=jaxbElmt.getValue();
        }
        else
        {
            mapLoaded=mapData;
            if (f == null)
            {
                if (sourceXSD != null)
                    mappingParentPath=sourceXSD.getAbsoluteFile().getParentFile().getAbsolutePath();
                else if (targetXSD != null)
                    mappingParentPath=targetXSD.getAbsoluteFile().getParentFile().getAbsolutePath();
            }
            else mappingParentPath=f.getAbsoluteFile().getParentFile().getAbsolutePath();
            System.out.println("MappingFactory.loadMapping()2..mapping Parent:"+mappingParentPath);
        }
        System.out.println("MappingFactory.loadMapping()...mapLoaded:"+mapLoaded);
		//re-connect the meta structure for source and target schemas
		for (Component mapComp:mapLoaded.getComponents().getComponent())
		{
            //String xsdLocation=mappingParentPath+File.separator+mapComp.getLocation();
            String xsdLocation = mapComp.getLocation();

            try
            {
                if (mapComp.getRootElement()!=null)
                {
                    if ((mapComp.getType() != ComponentType.SOURCE)&&
                        (mapComp.getType() != ComponentType.TARGET)) continue;

                    xsdLocation = xsdLocation.trim();
                    if (xsdLocation.toLowerCase().startsWith("file:/"))
                    {
                        xsdLocation = xsdLocation.substring(6);
                        while(xsdLocation.startsWith("/")) xsdLocation = xsdLocation.substring(1);
                    }

                    String xsdLocation2 = mappingParentPath+File.separator+mapComp.getLocation();
                    File xsdFile = null;
                    while(true)
                    {
                        xsdFile = new File(xsdLocation2);
                        if ((xsdFile.exists())&&(xsdFile.isFile()))
                        {
                            xsdLocation = xsdLocation2;
                            break;
                        }
                        if (xsdLocation.equals(xsdLocation2))
                        {
                            xsdFile = null;
                            break;
                            //throw new JAXBException("Invalid XSD file path : " + xsdLocation);
                        }
                        else xsdLocation2 = xsdLocation;
                    }

                    System.out.println("MappingFactory.loadMapping()..schema:"+mapComp.getType()+"="+xsdLocation);
                    XSDParser metaParser = new XSDParser();
                    if (mapComp.getType()==ComponentType.SOURCE)
                    {
                        //sourceHeadMeta = mapComp.getRootElement();
                        if ((sourceXSD != null)&&(sourceXSD.exists())&&(sourceXSD.isFile()))
                        {
                            xsdFile = sourceXSD;
                            //xsdLocation = sourceXSD.getAbsolutePath();
                            //mapComp.setLocation(xsdLocation);
                        }
                        else sourceParser=metaParser;
                    }
                    else
                    {
                        //targetHeadMeta = mapComp.getRootElement();
                        if ((targetXSD != null)&&(targetXSD.exists())&&(targetXSD.isFile()))
                        {
                            xsdFile = targetXSD;
                            //xsdLocation = targetXSD.getAbsolutePath();
                            //mapComp.setLocation(xsdLocation);
                        }
                        else targetParser=metaParser;
                    }

                    if (xsdFile != null)
                    {
                        metaParser.loadSchema(xsdFile.toURI().toString(), null);
                    }
                    else
                    {
                        int idx = xsdLocation.toLowerCase().indexOf("file:/");
                        if (idx > 0) xsdLocation = xsdLocation.substring(idx);

                        metaParser.loadSchema(xsdLocation, null);
                    }
//                    mapComp.setLocation(xsdLocation);
                    boolean res = MappingFactory.loadMetaXSD(mapLoaded, metaParser, mapComp.getRootElement().getNameSpace(),mapComp.getRootElement().getName(),mapComp.getType() );
                    if (!res) throw new JAXBException("Namespace or root element name is mismatched with this XSD file. : " + xsdLocation);

                    mapComp.setLocation(metaParser.getSchemaURI());
                    if (mapComp.getType()==ComponentType.SOURCE)
                    {
                        if ((sourceXSD != null)&&(sourceXSD.exists())&&(sourceXSD.isFile())) sourceParser=metaParser;
                    }
                    else
                    {
                        if ((targetXSD != null)&&(targetXSD.exists())&&(targetXSD.isFile())) targetParser=metaParser;
                    }
                }
            }
            catch(JAXBException ee)
            {
                String msg = ee.getMessage();
                ee.printStackTrace();
                if ((msg == null)||(msg.trim().equals("")))
                	msg = ee.getClass().getCanonicalName()+":"+ "Failed to read or parse schema document (1) - " + xsdLocation;
                throw new JAXBException(msg);

            }
            catch(Exception ee)
            {
                String msg = ee.getMessage();
                if ((msg == null)||(msg.trim().equals("")))
                	msg = "Failed to read or parse schema document - " + xsdLocation;
                ee.printStackTrace();
                throw new JAXBException(ee.getClass().getCanonicalName()+":"+msg);

            }
        }


        if ((mapLoaded.getTags().getTag() != null)&&(mapLoaded.getTags().getTag().size() > 0))
        {
            Hashtable <String, BaseMeta> srcMetaHash=new Hashtable <String, BaseMeta>();
            Hashtable <String, BaseMeta> trgtMetaHash=new Hashtable <String, BaseMeta>();
            //pre-process mapping for annotation
            for (Component mapComp:mapLoaded.getComponents().getComponent())
            {
                if (mapComp.getType().value().equals(ComponentType.SOURCE.value()))
                {
                    mapComp.getRootElement().setId("/"+mapComp.getRootElement().getName());
                    processMeta(srcMetaHash, mapComp.getRootElement(), null, mapLoaded.getTags().getTag());
                }
                else if (mapComp.getType().value().equals(ComponentType.TARGET.value()))
                {
                    mapComp.getRootElement().setId("/"+mapComp.getRootElement().getName());
                    processMeta(trgtMetaHash,mapComp.getRootElement(), null, mapLoaded.getTags().getTag());
                }
            }

            //sort tags with precedence from low to high
            // 0 -- componentType; enumValues: source, taret, function
            // 1 -- key; allowing value: entry's xpath
            // 2 -- componentKind; enumValues: choice, clone
            // 3 -- value; the ordered ASCII characters
            Collections.sort(mapLoaded.getTags().getTag());
            for (TagType tag:mapLoaded.getTags().getTag())
            {
                if (tag.getComponentType().value().equals(ComponentType.SOURCE.value()))
                {
                    processAnnotationTag (tag, srcMetaHash, mapLoaded.getTags().getTag());
                }
                else if (tag.getComponentType().value().equals(ComponentType.TARGET.value()))
                {
                    processAnnotationTag (tag, trgtMetaHash, mapLoaded.getTags().getTag());
                }
            }
        }




        if ((mapLoaded.getLinks().getLink() != null)&&(mapLoaded.getLinks().getLink().size() > 0))
        {

            for (LinkType link:mapLoaded.getLinks().getLink())
            {
                if ((link.getSource().getComponentid().equals("0"))||
                    (link.getSource().getComponentid().equals("1")))
                {
                    String id = link.getSource().getId();
                    //System.out.println("CCCX Source("+link.getSource().getComponentid()+") : id=" + id);
                    BaseMeta meta = searchElementMeta(id);
                    //if (meta == null) meta = searchElementMeta(id, ComponentType.TARGET);
                }

                if ((link.getTarget().getComponentid().equals("0"))||
                    (link.getTarget().getComponentid().equals("1")))
                {
                    String id = link.getTarget().getId();
                    //System.out.println("CCCX Target("+link.getTarget().getComponentid()+") : id=" + id);
                    BaseMeta meta = searchElementMeta(id);
                    //if (meta == null) meta = searchElementMeta(id, ComponentType.SOURCE);
                }

            }
        }



        return mapLoaded;
        //if (mapData == null) return  jaxbElmt.getValue();
	}
*/
    private static void processAnnotationTag(TagType tag, Hashtable <String, BaseMeta>  metaHash, List<TagType> tagList) throws Exception
    {
        //v System.out.println("CCCX processAnnotationTag: " + tag.getKey());
        if ((metaHash == null)||(metaHash.size() ==0)) return;
        String parentKey=tag.getKey().substring(0, tag.getKey().lastIndexOf("/"));
        ElementMeta elmntMeta=(ElementMeta)metaHash.get(tag.getKey());
        if (elmntMeta == null)
        {
            BaseMeta meta = searchElementMeta(tag.getKey(), tag.getComponentType());
            if (meta == null) throw new Exception("Not found element meta: " + tag.getKey());
            else if (!(meta instanceof ElementMeta)) throw new Exception("This is Not an element meta: " + tag.getKey());
            else elmntMeta = (ElementMeta)meta;
        }
        ElementMeta parentMeta=(ElementMeta)metaHash.get(parentKey);
        if (parentMeta == null)
        {
            BaseMeta meta = searchElementMeta(parentKey, tag.getComponentType());
            if (meta == null) throw new Exception("Not found parent meta: " + parentKey);
            else if (!(meta instanceof ElementMeta)) throw new Exception("This is Not an element meta: " + tag.getKey());
            else parentMeta = (ElementMeta)meta;
        }
        if (tag.getKind().value().equals(KindType.CLONE.value()))
        {
            ElementMeta cloneElement=(ElementMeta)elmntMeta.clone();
            int insertingIndx=0;

            //find the position of the element being cloned
            for (ElementMeta siblingElmnt:parentMeta.getChildElement())
            {
                insertingIndx++;
                if (siblingElmnt.getName().equals(elmntMeta.getName()))
                    break;
            }
            cloneElement.setMultiplicityIndex(BigInteger.valueOf(Integer.valueOf(tag.getValue()).intValue()));
            cloneElement.setId(parentMeta.getId()+"/"+cloneElement.getName());
            parentMeta.getChildElement().add(insertingIndx+cloneElement.getMultiplicityIndex().intValue()-1, cloneElement);
            List<ElementMeta> pList = new ArrayList<ElementMeta>();
            pList.add(parentMeta);
            processMeta(metaHash, cloneElement, pList, tagList);
        }
        else if (tag.getKind().value().equals(KindType.CHOICE.value()))
        {
            System.out.println("MappingFactory.processAnnotationTag()..choosen element:"+tag.getKey());
            elmntMeta.setIsChosen(true);
            if (elmntMeta.getChildElement().size() == 0)
            {
                if (tag.getComponentType()==ComponentType.SOURCE) sourceParser.expandElementMetaWithLazyLoad(elmntMeta);
                else targetParser.expandElementMetaWithLazyLoad(elmntMeta);
            }
        }
        else if (tag.getKind().value().equals(KindType.RECURSION.value()))
        {

            ElementMeta recursiveMeta=searchRecursiveAncestor(metaHash, elmntMeta, elmntMeta.getType());
            if (recursiveMeta != null)
            {
                ElementMeta recursiveMetaClone=(ElementMeta)recursiveMeta.clone();

                //add the cloned Attributes and childElement to
                //the recursive element, then it will be referred by parent elementMeta
                elmntMeta.getAttrData().addAll(recursiveMetaClone.getAttrData());
                elmntMeta.getChildElement().addAll(recursiveMetaClone.getChildElement());

                List<ElementMeta> pList = new ArrayList<ElementMeta>();
                pList.add(parentMeta);
                processMeta(metaHash, elmntMeta, pList, tagList);
                elmntMeta.setIsEnabled(true);
            }
            else throw new Exception("Cannot find the ancester of recursive node : name="+ elmntMeta.getName() + ", namespace=" + elmntMeta.getNameSpace() + ", type=" + elmntMeta);
        }

    }
    /**
     * Recursive search ancestor element meta to find the recursive type
     * @param metaHash
     * @param element
     * @param recursionType
     * @return ElementMeta
     */
    private static ElementMeta searchRecursiveAncestor(Hashtable <String, BaseMeta>  metaHash, ElementMeta element, String recursionType)
    {
        ElementMeta parentMeta;
        String parentKey=element.getId().substring(0,element.getId().lastIndexOf("/"));
        parentMeta=(ElementMeta)metaHash.get(parentKey);
        if (parentMeta==null)
            return parentMeta;
        else if (parentMeta.getType() == null) {}
        else if (element.getType() == null) {}
        else if (parentMeta.getType().equals(element.getType()))
            return parentMeta;
        return searchRecursiveAncestor(metaHash,  parentMeta, recursionType);
    }

    /**
     * Set unique ID for each element meta and attribute meta, these IDs are
     * referred as processing links
     * @param metaHash
     * @param element
     */
    //private static void processMeta(Hashtable <String, BaseMeta>  metaHash, ElementMeta element, ElementMeta parent)
    //{
    //    processMeta(metaHash, element, parent, null);
    //}
    private static void processMeta(Hashtable <String, BaseMeta>  metaHash, ElementMeta element, List<ElementMeta> parents, List<TagType> tagList)
    {
        if (parents == null) parents = new ArrayList<ElementMeta>();
        String addedTag = getAddedTag(element, tagList);
        boolean find = false;

        if (tagList == null) find = true;
        else if (parents == null) find = false;
        else
        {
            if (addedTag != null) find = true;
        }
        if (find)
        {
            //System.out.println("*UUU1 Add meta       : " + element.getId());
            metaHash.put(element.getId(), element);

        }
        if ((parents.size() > 1000)||(find))
        {
            //int j = 0;
            for (int i=(parents.size()-1);i>=0;i--)
            {
                ElementMeta meta = parents.get(i);


                String id = meta.getId() + "/";
                if (!element.getId().startsWith(id))
                {
                    //System.out.println("   UUU9 delete meta("+i+"): " + meta.getId());
                    parents.remove(i);
                    continue;
                }
                BaseMeta e = metaHash.get(meta.getId());
                if (e != null) continue;
                if (!find) continue;
                //j++;
                //System.out.println("*UUU2 Add parent meta("+i+", "+j+"): " + meta.getId());
                metaHash.put(meta.getId(), meta);
            }
        }





        if (element.isIsRecursive()&&element.isIsEnabled()) //typeStack.contains(currentType))
            return;
        //process attribute
        if ((find)&&(addedTag.indexOf("/@") > 0))
        {
            for (AttributeMeta attr:element.getAttrData())
            {
                String attrMetaKey=element.getId()+"/@"+attr.getName();
                attr.setId(attrMetaKey);
                //addToMetaHash(metaHash, attr, element, tagList, true);
                metaHash.put(attrMetaKey, attr);
                System.out.println("*UUU3 Add attr meta  : " + attr.getId());
            }
        }
        //process child elements

        if (element.getChildElement().size() > 0)
        {
            parents.add(element);

            for(ElementMeta childElement:element.getChildElement())
            {
                childElement.setId(element.getId()+"/"+childElement.getName());
                processMeta(metaHash, childElement, parents, tagList);
            }
        }
    }
    private static String getAddedTag(BaseMeta baseMeta, List<TagType> tagList)
    {
        String addedTag = null;

            for (TagType tag:tagList)
            {
                String tagStr = tag.getKey().trim();
                int idx = tagStr.indexOf("/@");
                if (idx > 0) tagStr = tagStr.substring(0, idx);

                if (tagStr.equals(baseMeta.getId().trim()))
                {
                    addedTag = tag.getKey().trim();
                    break;
                }
            }


        return addedTag;
    }

    public static void saveMapping(File f, Mapping m) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmts.core" );
        Marshaller u = jc.createMarshaller();

        //do not persistent the meta structure
        Hashtable<String, List<ElementMeta>> rootChildListHash=new Hashtable<String, List<ElementMeta>>();
        Hashtable<String, List<AttributeMeta>> rootAttrListHash=new Hashtable<String, List<AttributeMeta>>();
        for (Component mapComp:m.getComponents().getComponent())
        {
            if (mapComp.getRootElement()!=null)
            {
                List<ElementMeta> childList=new ArrayList<ElementMeta>();
                childList.addAll(mapComp.getRootElement().getChildElement());
                rootChildListHash.put(mapComp.getLocation()+mapComp.getId(), childList);
                mapComp.getRootElement().getChildElement().clear();

                List<AttributeMeta> attrList=new ArrayList<AttributeMeta>();
                attrList.addAll(mapComp.getRootElement().getAttrData());
                rootAttrListHash.put(mapComp.getLocation()+mapComp.getId(), attrList);
                mapComp.getRootElement().getAttrData().clear();
            }
        }
        u.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
        u.marshal(new JAXBElement<Mapping>(new QName("mapping"),Mapping.class, m), f);

        //put the unmarshalled children back
        for (Component mapComp:m.getComponents().getComponent())
        {
            if (mapComp.getRootElement()!=null)
            {
                mapComp.getRootElement().getChildElement().addAll(rootChildListHash.get(mapComp.getLocation()+mapComp.getId()));
                mapComp.getRootElement().getAttrData().addAll(rootAttrListHash.get(mapComp.getLocation()+mapComp.getId()));
                String xsdLocation=f.getParent()+File.separator+mapComp.getLocation();
                mapComp.setLocation(xsdLocation);
            }
        }
    }
    private static BaseMeta searchElementMeta(String path) throws Exception
    {
       return searchElementMeta(path, null);
    }
    private static BaseMeta searchElementMeta(String path, ComponentType type) throws Exception
    {
        while(path.startsWith("/")) path = path.substring(1);
        ElementMeta elem = null;
        if (type == null)
        {
            if (path.startsWith(sourceHeadMeta.getName())) type = ComponentType.SOURCE;
            else if (path.startsWith(targetHeadMeta.getName())) type = ComponentType.TARGET;
            else
            {
                throw new Exception("This id is not identified. => " + path);
                //return null;
            }
        }

        if (type==ComponentType.SOURCE) elem = sourceHeadMeta;
        else elem = targetHeadMeta;


        StringTokenizer st = new StringTokenizer(path, "/");
        int n = 0;
        //ElementMeta parent = null;
        String attrID = null;
        BaseMeta ret = null;
        while(st.hasMoreTokens())
        {
            String token = st.nextToken();
            if (token == null) token = "";
            else token = token.trim();
            if (token.equals("")) continue;

            if (attrID != null)
            {
                throw new Exception("Invalid attribute item(" + type.value() + ") => " + attrID);
                //return null;
            }
            if (token.startsWith("@")) attrID = token;

            if (n == 0)
            {
                if (token.equals(elem.getName()))
                {
                    n++;
                    if ((elem.getChildElement() == null)||(elem.getChildElement().size() == 0))
                    {
                        throw new Exception("Head Node doesn't have any child element(" + type.value() + ") => " + token);
                        //return null;
                    }
                    else continue;
                }
                else
                {
                    throw new Exception("Head Node not found searchElementMeta(" + type.value() + ") => " + token + " <> " + elem.getName());
                    //return null;
                }
            }

            if ((elem.getChildElement() == null)||(elem.getChildElement().size() == 0))
            {
                //XSDParser parser = null;
                if (type==ComponentType.SOURCE) sourceParser.expandElementMetaWithLazyLoad(elem);
                else targetParser.expandElementMetaWithLazyLoad(elem);

                //parser.expandElementMetaWithLazyLoad(elem);
                /*
                ElementMeta eleT = parser.expandElementMetaWithLazyLoad(parent, elem);
                if ((eleT == null)||(eleT.getChildElement() == null)||(eleT.getChildElement().size() == 0))
                {
                    System.out.println("Element expanding failure searchElementMeta(" + type.value() + ") => " + token + " at " + parent.getName());
                    return null;
                }
                else elem = eleT;
                */
            }
            BaseMeta tEle = null;
            if (token.startsWith("@"))
            {
                for (int i=0;i<elem.getAttrData().size();i++)
                {
                    AttributeMeta meta = elem.getAttrData().get(i);
                    if (meta.getName().equals(token.substring(1)))
                    {
                        tEle = meta;
                        break;
                    }
                }
            }
            else
            {
                for (int i=0;i<elem.getChildElement().size();i++)
                {
                    ElementMeta meta = elem.getChildElement().get(i);
                    if (meta.getName().equals(token))
                    {
                        tEle = meta;
                        break;
                    }
                }
            }
            if (tEle == null)
            {
                throw new Exception("Node not found node searchElementMeta(" + type.value() + ") => " + token + " at " + path);
                //return null;
            }
            //parent = elem;
            if (tEle instanceof ElementMeta)
            {
                elem = (ElementMeta)tEle;
            }
            ret = tEle;
            n++;
        }
        if (ret instanceof ElementMeta)
        {
            ElementMeta ele = (ElementMeta) ret;
            if (ele.getChildElement().size() == 0)
            {
                if (type==ComponentType.SOURCE) sourceParser.expandElementMetaWithLazyLoad(ele);
                else targetParser.expandElementMetaWithLazyLoad(ele);
                return ele;
            }
        }
        return ret;
    }
    public static ZipEntry getXSDZipEntryFromZip(ZipUtil zipUtil, String xsdLocation)
    {
        return getXSDZipEntryFromZip(zipUtil, xsdLocation, false, false);
    }
    public static ZipEntry getXSDZipEntryFromZip(ZipUtil zipUtil, String xsdLocation, boolean checkSingle, boolean caseSeparate)
    {

        String filePath = xsdLocation;
        if (filePath==null||filePath.trim().equals(""))
			   return null;
       int subIndx=0;
       if (filePath.lastIndexOf("/")>-1)
           subIndx=filePath.lastIndexOf("/");
       else if (filePath.lastIndexOf("\\")>-1)
           subIndx=filePath.lastIndexOf("\\");
       String xsdName ="";

       if (subIndx>0) xsdName = filePath.substring(subIndx+1);
       else xsdName = filePath;

        ZipEntry zipEntry = null;
        try
        {
            zipEntry = zipUtil.searchEntryWithWholeName(xsdName);
        }
        catch(IOException ie)
        {
            List<String> list1 = zipUtil.getEntryNames();
            String select = null;
            int number = -1;
            int findEntries = 0;
            for(String str:list1)
            {
                int count = -1;

                boolean cTag1 = false;
                if (caseSeparate) cTag1 = str.equals(xsdName);
                else cTag1 = str.equalsIgnoreCase(xsdName);
                if (cTag1)
                //if (str.equalsIgnoreCase(xsdName))
                {
                    select = str;
                    break;
                }

                String cTag2 = "";
                String cTag3 = "";
                if (caseSeparate)
                {
                    cTag2 = str;
                    cTag3 = xsdName;
                }
                else
                {
                    cTag2 = str.toLowerCase();
                    cTag3 = xsdName.toLowerCase();
                }

                if (cTag2.endsWith("/"+cTag3))
                {
                    findEntries++;
                }
                else if (cTag2.endsWith("\\"+cTag3))
                {
                    findEntries++;
                }
                else continue;

                if ((checkSingle)&&(findEntries > 1)) return null;

                String loc = xsdLocation.substring(0, xsdLocation.length()-xsdName.length());
                String ent = str.substring(0, str.length()-xsdName.length());
                int cnt2 = 0;
                for(int i=0;i<ent.length();i++)
                {
                    if (i >= loc.length()) break;
                    String acharL = loc.substring(loc.length()-(i+1), loc.length()-i);
                    String acharE = ent.substring(ent.length()-(i+1), ent.length()-i);
                    if (acharL.equals("\\")) acharL = "/";
                    if (acharE.equals("\\")) acharE = "/";
                    if (acharL.equalsIgnoreCase(acharE)) cnt2++;
                    else break;
                }
                if (cnt2 > 0) count = cnt2;

                if (count > number)
                {
                    number = count;
                    select = str;
                }
            }
            if (select != null)
            {
                zipEntry = zipUtil.getZipFile().getEntry(select);
            }
            else zipEntry = null;
        }

        //System.out.println("CCCC new XSD location:" + xsdF);
        return zipEntry;
    }
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.5  2008/12/10 15:43:03  linc
 * HISTORY: Fixed component id generator and delete link.
 * HISTORY:
 * HISTORY: Revision 1.4  2008/12/09 19:04:17  linc
 * HISTORY: First GUI release
 * HISTORY:
 * HISTORY: Revision 1.3  2008/12/03 20:46:14  linc
 * HISTORY: UI update.
 * HISTORY:
 * HISTORY: Revision 1.2  2008/10/22 19:01:17  linc
 * HISTORY: Add comment of public methods.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/10/21 15:56:55  linc
 * HISTORY: updated
 * HISTORY:
 */

