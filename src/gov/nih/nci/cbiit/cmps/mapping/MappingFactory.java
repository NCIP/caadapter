/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmps.mapping;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.text.html.parser.Element;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import gov.nih.nci.cbiit.cmps.common.XSDParser;
import gov.nih.nci.cbiit.cmps.core.AttributeMeta;
import gov.nih.nci.cbiit.cmps.core.BaseMeta;
import gov.nih.nci.cbiit.cmps.core.Component;
import gov.nih.nci.cbiit.cmps.core.ComponentType;
import gov.nih.nci.cbiit.cmps.core.ElementMeta;
import gov.nih.nci.cbiit.cmps.core.KindType;
import gov.nih.nci.cbiit.cmps.core.LinkType;
import gov.nih.nci.cbiit.cmps.core.LinkpointType;
import gov.nih.nci.cbiit.cmps.core.Mapping;
import gov.nih.nci.cbiit.cmps.core.Mapping.Components;
import gov.nih.nci.cbiit.cmps.core.Mapping.Links;

/**
 * This class is used to generate CMPS Mapping
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.4 $
 * @date       $Date: 2008-12-09 19:04:17 $
 *
 */
public class MappingFactory {
	/**
	 * create Mapping from a pair of XSD
	 * @param srcX - source XSD
	 * @param srcRoot - source root element
	 * @param tgtX - target XSD
	 * @param tgtRoot - target root element
	 * @return Mapping
	 */
	public static Mapping createMappingFromXSD(String srcX, String srcRoot, String tgtX, String tgtRoot) {
		Mapping m = new Mapping();
		m.setComponents(new Mapping.Components());
		m.setLinks(new Mapping.Links());
		
		loadSourceXSD(m, srcX, srcRoot);
		loadTargetXSD(m, tgtX, tgtRoot);
		return m;		
	}

	/**
	 * load additional source XSD into specified Mapping
	 * @param m - Mapping object to load into
	 * @param srcX - source XSD
	 * @param srcRoot - source root element
	 */
	public static void loadSourceXSD(Mapping m, XSDParser srcX, String srcRoot) {
		loadXSD(m, srcX, srcRoot, ComponentType.SOURCE);
	}

	/**
	 * load additional target XSD into specified Mapping
	 * @param m - Mapping object to load into
	 * @param tgtX - target XSD
	 * @param tgtRoot - target root element
	 */
	public static void loadTargetXSD(Mapping m, XSDParser tgtX, String tgtRoot) {
		loadXSD(m, tgtX, tgtRoot, ComponentType.TARGET);
	}

	/**
	 * load additional source XSD into specified Mapping
	 * @param m - Mapping object to load into
	 * @param srcX - source XSD
	 * @param srcRoot - source root element
	 */
	public static void loadSourceXSD(Mapping m, String srcX, String srcRoot) {
		loadXSD(m, srcX, srcRoot, ComponentType.SOURCE);
	}

	/**
	 * load additional target XSD into specified Mapping
	 * @param m - Mapping object to load into
	 * @param tgtX - target XSD
	 * @param tgtRoot - target root element
	 */
	public static void loadTargetXSD(Mapping m, String tgtX, String tgtRoot) {
		loadXSD(m, tgtX, tgtRoot, ComponentType.TARGET);
	}

	private static void loadXSD(Mapping m, String schema, String root, ComponentType type) {
		XSDParser p = new XSDParser();
		p.loadSchema(schema);
		loadXSD(m, p, root, type);
	}
	
	private static void loadXSD(Mapping m, XSDParser p, String root, ComponentType type) {
		Component src = new Component();
		src.setKind(KindType.XML);
		src.setId(p.getSchemaURI());
		src.setLocation(p.getSchemaURI());
		ElementMeta e = p.getElementMeta(null, root);
		if(e==null) e = p.getElementMetaFromComplexType(null, root);
		src.setRootElement(e);
		src.setType(type);
		if(m.getComponents() == null) m.setComponents(new Components());
		m.getComponents().getComponent().add(src);
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
	
	public static Mapping loadMapping(File f) throws JAXBException{
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Unmarshaller u = jc.createUnmarshaller();
		JAXBElement<Mapping> m = u.unmarshal(new StreamSource(f), Mapping.class);
		return  m.getValue();
	}
	
	public static void saveMapping(File f, Mapping m) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmps.core" );
		Marshaller u = jc.createMarshaller();
		u.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		u.marshal(new JAXBElement(new QName("mapping"),Mapping.class, m), f);
	}
	
	public static Map<String,Component> getComponentMap(Mapping m){
		HashMap<String,Component> ret = new HashMap<String,Component>();
		List<Component> l = m.getComponents().getComponent();
		for(Component c:l)
			ret.put(c.getId(), c);
		return ret;
	}
	
	public static BaseMeta findNodeById(Component c, String id){
		StringTokenizer st = new StringTokenizer(id, "/@");
		boolean foundRoot = false;
		ElementMeta e = c.getRootElement();
		while(st.hasMoreTokens()){
			String tmp = st.nextToken();
			if(!foundRoot){
				if(e.getName().equals(tmp)){
					foundRoot = true;
					continue;
				}else
					continue;
			}else{
				List<ElementMeta> childs = e.getChildElement();
				ElementMeta found = null;
				AttributeMeta foundAttr = null;
				for(ElementMeta i:childs){
					if(i.getName().equals(tmp)){
						found = i;
						break;
					}
				}
				if(found == null && st.hasMoreTokens()){
					return null;
				}else if(found == null && !st.hasMoreTokens()){
					List<AttributeMeta> attrs = e.getAttrData();
					for(AttributeMeta i:attrs){
						if(i.getName().equals(tmp)){
							foundAttr = i;
							break;
						}
					}
					return foundAttr;
				}else{
					e = found;
				}
			}
		}
		return e;
	}
	
	public static BaseMeta findNodeById(Mapping m, String componentId, String id){
		Map<String,Component> map = getComponentMap(m);
		Component c = map.get(componentId);
		return findNodeById(c, id);
	}
	
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
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

