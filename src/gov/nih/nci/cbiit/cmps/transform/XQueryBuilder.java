/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmps.transform;

import gov.nih.nci.cbiit.cmps.core.AttributeMeta;
import gov.nih.nci.cbiit.cmps.core.Component;
import gov.nih.nci.cbiit.cmps.core.ComponentType;
import gov.nih.nci.cbiit.cmps.core.ElementMeta;
import gov.nih.nci.cbiit.cmps.core.LinkType;
import gov.nih.nci.cbiit.cmps.core.Mapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * This class generates XQuery from Mapping
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMPS v1.0
 * @version    $Revision: 1.10 $
 * @date       $Date: 2009-11-13 20:49:51 $
 *
 */
public class XQueryBuilder {
	private Mapping mapping;
	private StringBuffer sbQuery;
	private Map<String, LinkType> links;
	private Map<String, String> varMap;
	private Stack<String> xpathStack;
	private Stack<String> varStack;

	final static String sep = System.getProperty("line.separator");

	/**
	 * constructor that takes Mapping object
	 * @param m - Mapping object
	 */
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

	/**
	 * generate XQuery
	 * @return XQuery String
	 */
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
		varStack = new Stack<String>();
		sbQuery = new StringBuffer();
		varMap=new HashMap<String, String>();
		sbQuery.append("declare variable $docName as xs:string external;" + sep);
		processTargetElement(tgt.getRootElement(),null);
		return sbQuery.toString();
	}
	

	private void processTargetElement(ElementMeta tgt,String parentXPath) {
		xpathStack.push(tgt.getName());
		StringBuilder sb = new StringBuilder();
		for (String s:xpathStack) {
			sb.append("/").append(s);
		}
		String tgtMappingSrc=null;
		boolean loopRequired=false;
		LinkType link = links.get(sb.toString());
		if(link!=null) {
			link = links.get(sb.toString());
			tgtMappingSrc = link.getSource().getId();
			String var = "item_temp"+String.valueOf(varStack.size());
			//mapping source is an element,loop through it
			if (tgtMappingSrc.indexOf("@")<0)
			{
				if(xpathStack.size()>1) 
				{
					sbQuery.append("{");
					loopRequired=true;
				}
				sbQuery.append("for $"+var+" in ");
				String localpath = null;
				if (parentXPath!=null)
					localpath =QueryBuilderUtil.retrieveRelativePath(parentXPath, tgtMappingSrc);// getRelativePath(srcIdStack.peek(), srcId);
 
				if(localpath == null) {
					sbQuery.append("doc($docName)");
					sbQuery.append(tgtMappingSrc);
				}else {
					sbQuery.append("$"+varStack.peek()).append(localpath);
				}
				sbQuery.append(" return ");
				varMap.put(tgtMappingSrc, var);
			}
			varStack.push(var);
		}
		
		//start tag and attributes
		sbQuery.append("<"+tgt.getName());
		processAttributes(tgt, tgtMappingSrc);
		sbQuery.append(">");
		
		//inline text
		String srcTextId = null;
		if(link!=null && (tgt.getChildElement().size()==0)) {//map text element
			srcTextId = link.getSource().getId();
		}
		if(links.get(sb.toString()+"#inlinetext")!=null) { //map inline text for mixed node
			srcTextId = links.get(sb.toString()+"#inlinetext").getSource().getId();
		}
		if(srcTextId!=null) {
			String srcDynamicPath=null;
			if (srcTextId.indexOf("@")>-1)
			{
				String mappingSrcElementId=srcTextId.substring(0, srcTextId.lastIndexOf("/"));
				String mappingSrcElementVar=varMap.get(mappingSrcElementId);
				String srcAttrName=srcTextId.substring(srcTextId.indexOf("@"));
				if (mappingSrcElementVar!=null)
					srcDynamicPath=mappingSrcElementVar+"/"+srcAttrName;
			}
			else
			{
				String localpath =QueryBuilderUtil.retrieveRelativePath(tgtMappingSrc, tgtMappingSrc);
				if(localpath!=null)
					srcDynamicPath=varStack.peek()+localpath;
			}
			
			if (srcDynamicPath!=null)
				sbQuery.append("{data($").append(srcDynamicPath).append(")}");
			else //use the absolute path of the attribute/element in case its parent element are not mapped
				sbQuery.append("{data(doc($docName)").append(srcTextId+")}");			
		}
		 
		//child elements
		for(ElementMeta e:tgt.getChildElement()) 
			processTargetElement(e, tgtMappingSrc);
		
		//end tag
		sbQuery.append("</"+tgt.getName()+">");

		//clear temporary varible stack
		if(link!=null) 
			varStack.pop();
		
		//close loop
		if(loopRequired) 
			sbQuery.append("}").append(sep);
		xpathStack.pop();
	}

	private void processAttributes(ElementMeta tgt, String elementMapingSourceId) {
		StringBuilder sb = new StringBuilder();
		for (String s:xpathStack) {
			sb.append("/").append(s);
		}
		for(AttributeMeta a:tgt.getAttrData()) {
			if(links.get(sb.toString()+"/@"+a.getName())!=null) {
				LinkType l = links.get(sb.toString()+"/@"+a.getName());
				String attrMappingSrc = l.getSource().getId();
				String var = varStack.peek();
				String localpath = null;
				if(elementMapingSourceId!=null)
					localpath =QueryBuilderUtil.retrieveRelativePath(elementMapingSourceId, attrMappingSrc);
				//xquery data function return the value of an element or attribute
				sbQuery.append(" ").append(a.getName()).append("=\"{data(");
				sbQuery.append("$").append(var).append(localpath);
				sbQuery.append(")}\"");
			}else if(a.getFixedValue()!=null) { //use fixed value first
				sbQuery.append(" ").append(a.getName()).append("=");
				sbQuery.append("\""+a.getFixedValue()+"\"");
			} 
			else if(a.getDefaultValue()!=null) {//use default value
				sbQuery.append(" ").append(a.getName()).append("=");
				sbQuery.append("\""+a.getDefaultValue()+"\"");
			}
		}
	}
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.9  2008/12/10 15:43:02  linc
 * HISTORY: Fixed component id generator and delete link.
 * HISTORY:
 * HISTORY: Revision 1.8  2008/11/04 21:19:34  linc
 * HISTORY: core mapping and transform demo.
 * HISTORY:
 * HISTORY: Revision 1.7  2008/10/22 19:01:17  linc
 * HISTORY: Add comment of public methods.
 * HISTORY:
 * HISTORY: Revision 1.6  2008/10/21 20:49:08  linc
 * HISTORY: Tested with HL7 v3 schema
 * HISTORY:
 * HISTORY: Revision 1.5  2008/10/21 15:59:57  linc
 * HISTORY: updated.
 * HISTORY:
 * HISTORY: Revision 1.4  2008/10/21 15:56:55  linc
 * HISTORY: updated
 * HISTORY:
 * HISTORY: Revision 1.3  2008/10/20 20:46:15  linc
 * HISTORY: updated.
 * HISTORY:
 * HISTORY: Revision 1.2  2008/10/01 18:59:13  linc
 * HISTORY: updated.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/09/30 17:30:41  linc
 * HISTORY: updated.
 * HISTORY:
 */

