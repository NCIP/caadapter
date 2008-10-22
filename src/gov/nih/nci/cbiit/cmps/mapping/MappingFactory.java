/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmps.mapping;

import gov.nih.nci.cbiit.cmps.common.XSDParser;
import gov.nih.nci.cbiit.cmps.core.Component;
import gov.nih.nci.cbiit.cmps.core.ComponentType;
import gov.nih.nci.cbiit.cmps.core.ElementMeta;
import gov.nih.nci.cbiit.cmps.core.KindType;
import gov.nih.nci.cbiit.cmps.core.LinkType;
import gov.nih.nci.cbiit.cmps.core.LinkpointType;
import gov.nih.nci.cbiit.cmps.core.Mapping;

/**
 * This class is used to generate CMPS Mapping
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2008-10-22 19:01:17 $
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
		Component src = new Component();
		src.setKind(KindType.XML);
		src.setId(schema);
		src.setLocation(schema);
		XSDParser p = new XSDParser();
		p.loadSchema(schema);
		ElementMeta e = p.getElementMeta(null, root);
		src.setRootElement(e);
		src.setType(type);
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
		m.getLinks().getLink().add(l);
	}
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.1  2008/10/21 15:56:55  linc
 * HISTORY: updated
 * HISTORY:
 */

