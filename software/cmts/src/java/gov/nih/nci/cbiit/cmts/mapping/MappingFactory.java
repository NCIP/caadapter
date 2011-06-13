/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.mapping;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

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
import gov.nih.nci.cbiit.cmts.core.TagType;
import gov.nih.nci.cbiit.cmts.core.Mapping.Components;
import gov.nih.nci.cbiit.cmts.core.Mapping.Links;

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
public class MappingFactory {

	public static void loadMetaXSD(Mapping m, XSDParser schemaParser,String rootNS, String root, ComponentType type) {

		ElementMeta e = schemaParser.getElementMeta(rootNS, root);
		if(e==null) 
			e = schemaParser.getElementMetaFromComplexType(rootNS, root);

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
					return;
				}
			}
		Component endComp = new Component();
		endComp.setKind(KindType.XML);
		endComp.setId(getNewComponentId(m));
		endComp.setLocation(schemaParser.getSchemaURI());

		endComp.setRootElement(e);
		endComp.setType(type);
		m.getComponents().getComponent().add(endComp);
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
		System.out.println("MappingFactory.loadMapping()...mappingFile:"+f.getAbsolutePath());
		JAXBContext jc=null;		
//			jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmts.core" );
		jc=com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl.newInstance("gov.nih.nci.cbiit.cmts.core");

		Unmarshaller u = jc.createUnmarshaller();
		JAXBElement<Mapping> jaxbElmt = u.unmarshal(new StreamSource(f), Mapping.class);
		Mapping mapLoaded=jaxbElmt.getValue();
		System.out.println("MappingFactory.loadMapping()...mapLoaded:"+mapLoaded);
		//re-connect the meta structure for source and target schemas
		for (Component mapComp:mapLoaded.getComponents().getComponent())
		{
            try
            {
                if (mapComp.getRootElement()!=null)
                {
                    XSDParser metaParser = new XSDParser();
                    metaParser.loadSchema(mapComp.getLocation(), f.getAbsolutePath());
                    MappingFactory.loadMetaXSD(mapLoaded, metaParser, mapComp.getRootElement().getNameSpace(),mapComp.getRootElement().getName(),mapComp.getType() );
                }
            }
            catch(Exception ee)
            {
                String msg = ee.getMessage();
                if ((msg == null)||(msg.trim().equals(""))) msg = "Possibly Failed to read or parse schema document - " + mapComp.getLocation();
                throw new JAXBException(ee.getClass().getCanonicalName()+":"+msg);
            }
        }
		Hashtable <String, BaseMeta> srcMetaHash=new Hashtable <String, BaseMeta>();
		Hashtable <String, BaseMeta> trgtMetaHash=new Hashtable <String, BaseMeta>();
		//pre-process mapping for annotation 
		for (Component mapComp:mapLoaded.getComponents().getComponent())
		{
			if (mapComp.getType().value().equals(ComponentType.SOURCE.value()))
			{
				mapComp.getRootElement().setId("/"+mapComp.getRootElement().getName());
				processMeta(srcMetaHash, mapComp.getRootElement());
			}
			else if (mapComp.getType().value().equals(ComponentType.TARGET.value()))
			{
				mapComp.getRootElement().setId("/"+mapComp.getRootElement().getName());
				processMeta(trgtMetaHash,mapComp.getRootElement());
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
				processAnnotationTag (tag,srcMetaHash);
			}
			else if (tag.getComponentType().value().equals(ComponentType.TARGET.value()))
			{
				processAnnotationTag (tag,trgtMetaHash);
			}
		} 
		return  jaxbElmt.getValue();
	}
	
	private static void processAnnotationTag(TagType tag, Hashtable <String, BaseMeta>  metaHash)
	{
		String parentKey=tag.getKey().substring(0, tag.getKey().lastIndexOf("/"));
		ElementMeta elmntMeta=(ElementMeta)metaHash.get(tag.getKey());
		ElementMeta parentMeta=(ElementMeta)metaHash.get(parentKey);
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
			processMeta(metaHash, cloneElement );
		}
		else if (tag.getKind().value().equals(KindType.CHOICE.value()))
		{
//			System.out.println("MappingFactory.processAnnotationTag()..choosen element:"+tag.getKey());
			elmntMeta.setIsChosen(true);
		}
		else if (tag.getKind().value().equals(KindType.RECURSION.value()))
		{
			
			ElementMeta recursiveMeta=searchRecursiveAncestor(metaHash, elmntMeta, elmntMeta.getType());
			ElementMeta recursiveMetaClone=(ElementMeta)recursiveMeta.clone();
			
			//add the cloned Attributes and childElement to 
			//the recursive element, then it will be referred by parent elementMeta
			elmntMeta.getAttrData().addAll(recursiveMetaClone.getAttrData());
			elmntMeta.getChildElement().addAll(recursiveMetaClone.getChildElement());
			
			processMeta(metaHash, elmntMeta);
			elmntMeta.setIsEnabled(true);
		}
 
	}
	/**
	 * Recursive search ancestor element meta to find the recursive type
	 * @param metaHash
	 * @param element
	 * @param recursionType
	 * @return
	 */
	private static ElementMeta searchRecursiveAncestor(Hashtable <String, BaseMeta>  metaHash, ElementMeta element, String recursionType)
	{
		ElementMeta parentMeta;
		String parentKey=element.getId().substring(0,element.getId().lastIndexOf("/"));
		parentMeta=(ElementMeta)metaHash.get(parentKey);
		if (parentMeta==null)
			return parentMeta;
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
	private static void processMeta(Hashtable <String, BaseMeta>  metaHash, ElementMeta element)
	{
		metaHash.put(element.getId(), element);
		if (element.isIsRecursive()&&element.isIsEnabled()) //typeStack.contains(currentType))
			return;
		//process attribute
		for (AttributeMeta attr:element.getAttrData())
		{
			String attrMetaKey=element.getId()+"/@"+attr.getName();
			attr.setId(attrMetaKey);
			metaHash.put(attrMetaKey, attr);
		}
		//process child elements
		for(ElementMeta childElement:element.getChildElement())
		{
			childElement.setId(element.getId()+"/"+childElement.getName());
			processMeta(metaHash, childElement);
		}
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
				
				List<AttributeMeta> AttrList=new ArrayList<AttributeMeta>();
				AttrList.addAll(mapComp.getRootElement().getAttrData());
				rootAttrListHash.put(mapComp.getLocation()+mapComp.getId(), AttrList);
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
			}
		}
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

