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
 * This class 
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-21 15:56:55 $
 *
 */
public class MappingFactory {
	public static Mapping createMappingFromXSD(String srcX, String srcRoot, String tgtX, String tgtRoot) {
		Mapping m = new Mapping();
		m.setComponents(new Mapping.Components());
		m.setLinks(new Mapping.Links());
		
		loadSourceXSD(m, srcX, srcRoot);
		loadTargetXSD(m, tgtX, tgtRoot);
		return m;		
	}

	public static void loadSourceXSD(Mapping m, String srcX, String srcRoot) {
		loadXSD(m, srcX, srcRoot, ComponentType.SOURCE);
	}

	public static void loadTargetXSD(Mapping m, String srcX, String srcRoot) {
		loadXSD(m, srcX, srcRoot, ComponentType.TARGET);
	}

	public static void loadXSD(Mapping m, String schema, String root, ComponentType type) {
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
 */

