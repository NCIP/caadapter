/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.mapping;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import gov.nih.nci.cbiit.cmts.common.XSDParser;
import gov.nih.nci.cbiit.cmts.core.Component;
import gov.nih.nci.cbiit.cmts.core.ComponentType;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.core.KindType;
import gov.nih.nci.cbiit.cmts.core.LinkType;
import gov.nih.nci.cbiit.cmts.core.LinkpointType;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.core.Mapping.Components;
import gov.nih.nci.cbiit.cmts.core.Mapping.Links;

/**
 * This class is used to generate CMPS Mapping
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMPS v1.0
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
	
	public static Mapping loadMapping(File f) throws JAXBException{
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmts.core" );
		Unmarshaller u = jc.createUnmarshaller();
		JAXBElement<Mapping> jaxbElmt = u.unmarshal(new StreamSource(f), Mapping.class);
		Mapping mapLoaded=jaxbElmt.getValue();
		//re-connect the meta structure for source and target schemas
		for (Component mapComp:mapLoaded.getComponents().getComponent())
		{
			if (mapComp.getRootElement()!=null)
			{
				XSDParser metaParser = new XSDParser();
				metaParser.loadSchema(mapComp.getLocation());
				MappingFactory.loadMetaXSD(mapLoaded, metaParser, mapComp.getRootElement().getNameSpace(),mapComp.getRootElement().getName(),mapComp.getType() );
			}
		}
		return  jaxbElmt.getValue();
	}
	
	public static void saveMapping(File f, Mapping m) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmts.core" );
		Marshaller u = jc.createMarshaller();
		//do not persistent the meta structure
		for (Component mapComp:m.getComponents().getComponent())
		{
			if (mapComp.getRootElement()!=null)
			{
				mapComp.getRootElement().getChildElement().clear();
				mapComp.getRootElement().getAttrData().clear();
			}
		}
		u.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		u.marshal(new JAXBElement<Mapping>(new QName("mapping"),Mapping.class, m), f);
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

