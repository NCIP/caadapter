/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmps.transform;

import java.util.*;
import gov.nih.nci.cbiit.cmps.core.*;
import gov.nih.nci.cbiit.cmps.common.*;

/**
 * This class 
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2008-10-01 18:59:13 $
 *
 */
public class XQueryBuilder {
	private Mapping mapping;
	private StringBuffer sb;
	private Map<String, LinkType> links;
	
	public XQueryBuilder(Mapping m){
		this.mapping = m;
		this.sb = new StringBuffer();
	}
	
	private void loadLinks() {
		
	}
	
	public String getXQuery() {
		List<Component> l = mapping.getComponents().getComponent();
		Component tgt = null;
		for (Component c:l) {
			if (c.getType().equals(ComponentType.TARGET)) {
				tgt = c;
				break;
			}
		}
		processTargetElement(tgt.getRootElement());
		return sb.toString();
	}
	
	private void processTargetElement(ElementMeta tgt) {
		sb.append("<"+tgt.getName()+">");
		
		sb.append("</"+tgt.getName()+">");
	}
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.1  2008/09/30 17:30:41  linc
 * HISTORY: updated.
 * HISTORY:
 */

