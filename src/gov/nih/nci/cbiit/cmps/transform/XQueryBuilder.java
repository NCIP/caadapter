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
 * @version    $Revision: 1.3 $
 * @date       $Date: 2008-10-20 20:46:15 $
 *
 */
public class XQueryBuilder {
	private Mapping mapping;
	private StringBuffer sbQuery;
	private Map<String, LinkType> links;
	private Stack<String> xpathStack;
	private Stack<String> srcIdStack;
	private Stack<String> varStack;

	final static String sep = System.getProperty("line.separator");

	public XQueryBuilder(Mapping m){
		this.mapping = m;
		loadLinks();
	}

	private void loadLinks() {
		this.links = new HashMap<String, LinkType>();
		if(mapping.getLinks()==null) {
			return;
		}
		List<LinkType> links = mapping.getLinks().getLink();
		for (LinkType l:links) {
			this.links.put(l.getTarget().getId(), l);
		}
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
		xpathStack = new Stack<String>();
		srcIdStack = new Stack<String>();
		varStack = new Stack<String>();
		sbQuery = new StringBuffer();
		sbQuery.append("declare variable $docName as xs:string external;" + sep);
		processTargetElement(tgt.getRootElement());
		return sbQuery.toString();
	}

	private void processTargetElement(ElementMeta tgt) {
		xpathStack.push(tgt.getName());
		StringBuilder sb = new StringBuilder();
		for (String s:xpathStack) {
			sb.append("/").append(s);
		}
		boolean nested = false;
		if(links.get(sb.toString())!=null) {
			nested = true;
			LinkType l = links.get(sb.toString());
			String srcId = l.getSource().getId();
			String var = "item"+String.valueOf(varStack.size());
			if(xpathStack.size()>1) {
				sbQuery.append("{").append(sep);
			}
			sbQuery.append("for $"+var+" in ");
			if(varStack.size()==0) {
				sbQuery.append("doc($docName)");
				sbQuery.append(srcId);
			}else if(srcIdStack.size()>0 && srcId.indexOf(srcIdStack.peek())==0){
				String localId = "$"+varStack.peek()+srcId.substring(srcIdStack.peek().length());
				sbQuery.append(localId);
			}else{
				sbQuery.append(srcId);
			}
			sbQuery.append(" return ");
			varStack.push(var);
			srcIdStack.push(srcId);
		}
		sbQuery.append("<"+tgt.getName());
		processAttributes(tgt);
		sbQuery.append(">");
		//inline text
		if( (nested && tgt.getAttrData().size()==0) //map text element
				|| links.get(sb.toString()+"#inlinetext")!=null //map inline text for mixed node
				) {
			String var = varStack.peek();
			sbQuery.append("{$").append(var).append("/text()").append("}");
		}
		for(ElementMeta e:tgt.getChildElement()) {
			processTargetElement(e);
		}
		sbQuery.append("</"+tgt.getName()+">");
		if(nested && xpathStack.size()>1) sbQuery.append(sep).append("}").append(sep);
		if(nested) {
			srcIdStack.pop();
			varStack.pop();
		}
		xpathStack.pop();
	}

	private void processAttributes(ElementMeta tgt) {
		StringBuilder sb = new StringBuilder();
		for (String s:xpathStack) {
			sb.append("/").append(s);
		}
		for(AttributeMeta a:tgt.getAttrData()) {
			if(links.get(sb.toString()+"@"+a.getName())!=null) {
				LinkType l = links.get(sb.toString()+"@"+a.getName());
				String srcId = l.getSource().getId();
				String var = varStack.peek();
				sbQuery.append(" ").append(a.getName()).append("={");
				sbQuery.append(var).append("@").append(srcId);
				sbQuery.append("}");
			} else if(a.getDefaultValue()!=null) {
				sbQuery.append(" ").append(a.getName()).append("=");
				sbQuery.append(a.getDefaultValue());
			}
		}
		
	}

}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.2  2008/10/01 18:59:13  linc
 * HISTORY: updated.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/09/30 17:30:41  linc
 * HISTORY: updated.
 * HISTORY:
 */

