/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.common;

import java.io.File;
import java.math.BigInteger;
import java.util.*;

import org.apache.xerces.xs.*;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMException;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import gov.nih.nci.cbiit.cmts.core.*; 

/**
 * This class parses XSD into CMTS Core Model object
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.15 $
 * @date       $Date: 2009-11-24 16:00:59 $
 *
 */
public class XSDParser implements DOMErrorHandler {
	private XSLoader schemaLoader;
	private XSModel model;
	private String schemaURI;
	private Stack<String> ctStack;
	private Stack<String> elStack;
	private String defaultNS = "";
	private static boolean debug = false;
	//private static final String[] prefix={">", "  =", "    -", "      *", "        %", "          $"};

	private static String getPrefix(int i){
		//if(i<prefix.length) return prefix[i];
		StringBuffer sb = new StringBuffer();
		for(int j=0; j<i+1; j++) sb.append("  ");
		sb.append("[").append(i<10?((char)('0'+i)):((char)('a'+i-10))).append("]-");
		return sb.toString();
	}

	/**
	 * constructor
	 */
	public XSDParser() {
		try {
			// get DOM Implementation using DOM Registry
			System.setProperty(
					DOMImplementationRegistry.PROPERTY,
			"org.apache.xerces.dom.DOMXSImplementationSourceImpl");
			DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();

			XSImplementation impl = (XSImplementation) registry.getDOMImplementation("XS-Loader");

			schemaLoader = impl.createXSLoader(null);

			DOMConfiguration config = schemaLoader.getConfig();

			// create Error Handler
			DOMErrorHandler errorHandler = this;

			// set error handler
			config.setParameter("error-handler", errorHandler);

			// set validation feature
			config.setParameter("validate", Boolean.TRUE);

			ctStack = new Stack<String>();
			elStack = new Stack<String>();
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * load XSD schema
	 * @param schemaURI - XSD URI
	 */
	public void loadSchema(String schemaURI) {
		String userDirPath=System.getProperty("user.dir");
		if(debug)
		System.out.println("XSDParser.loadSchema()..user.dir:"+System.getProperty("user.dir"));
		if (schemaURI.indexOf(userDirPath)>-1)
			this.schemaURI=schemaURI.substring(userDirPath.length()+1);
		else
			this.schemaURI = schemaURI;
		// parse document
		if(debug) System.out.println("Parsing " + this.schemaURI + "...");
		model = schemaLoader.loadURI(this.schemaURI);

	}

	/**
	 * load XSD schema
	 * @param schemaURI - XSD URI
	 */
	public void loadSchema(File schemaFile) {

		loadSchema(schemaFile.getPath());
	}

	/**
	 * @return the schemaURI
	 */
	public String getSchemaURI() {
		return schemaURI;
	}

	public XSNamedMap[] getMappableNames(){
		XSNamedMap[] map = new XSNamedMap[2];
		map[0] = model.getComponents(XSConstants.ELEMENT_DECLARATION);
		map[1] = model.getComponents(XSConstants.TYPE_DEFINITION);
		
		return map;
	}
	
	/**
	 * get CMTS Core Model Object corresponding to the specified root element
	 * @param namespace - root element namespace
	 * @param name - root element name
	 * @return ElementMeta object
	 */
	public ElementMeta getElementMeta(String namespace, String name){
		if (model != null) {
			// element declarations
			XSNamedMap map = model.getComponents(XSConstants.ELEMENT_DECLARATION);
			//processMap(map, 0);
			defaultNS = namespace;
			ctStack.clear();
			elStack.clear();
			return processXSObject(map.itemByName(namespace, name), 0);
		} else {
			return null;
		}

	}

	/**
	 * get CMTS Core Model Object corresponding to the specified complex type
	 * @param namespace - complex type namespace
	 * @param name - complex type name
	 * @return ElementMeta object
	 */
	public ElementMeta getElementMetaFromComplexType(String namespace, String name){
		if (model != null) {
			// element declarations
			XSNamedMap map = model.getComponents(XSConstants.TYPE_DEFINITION);
			//processMap(map, 0);
			defaultNS = namespace;
			ctStack.clear();
			elStack.clear();
			return processXSObject(map.itemByName(namespace, name), 0);
		} else {
			return null;
		}

	}

	private ElementMeta processXSObject(XSObject item, int depth) {
		if(item instanceof XSComplexTypeDefinition){
			return processComplexType((XSComplexTypeDefinition)item, depth);
		}else if(item instanceof XSSimpleTypeDefinition){
			//processSimpleType((XSSimpleTypeDefinition)item);
			return null;
		}else if(item instanceof XSElementDeclaration){
			return processElement((XSElementDeclaration)item, depth);
		}
		return null;
	}

	private List<BaseMeta> processList(XSObjectList map, int depth){
		ArrayList<BaseMeta> ret = new ArrayList<BaseMeta>();
		for (int i = 0; i < map.getLength(); i++) {
			XSObject item = map.item(i);
			if(item instanceof XSComplexTypeDefinition){
				ret.add(processComplexType((XSComplexTypeDefinition)item, depth));
			}else if(item instanceof XSParticle){
				ret.addAll(processParticle((XSParticle)item, depth));
			}else if(item instanceof XSAttributeUse){
				ret.add(processAttribute((XSAttributeUse)item, depth));
			}
		}
		return ret;
	}

	private void processSimpleType(XSSimpleTypeDefinition item, int depth){
		if(debug) System.out.println(getPrefix(depth+1)+"SimpleType{" + item.getNamespace() + "}" + item.getName()+"["+item.getClass()+"]");
		//processParticle(item.getParticle(), indent);
	}
	private ElementMeta processComplexType(XSComplexTypeDefinition item, int depth){
		if(debug) System.out.println(getPrefix(depth)+"ComplexType{" + item.getNamespace() + "}" + item.getName()+"["+item.getClass()+"]");
		ElementMeta ret;
		String qname = "{" + item.getNamespace() + "}" + item.getName();
		if(item.getName()==null)
			qname = "{" + item.getNamespace() + "}" + elStack.peek();
		boolean recursive = ctStack.contains(qname);
		ctStack.push(qname);
		try {
			ret = new ElementMeta();
			ret.setNameSpace(item.getNamespace());
			ret.setName(item.getName());
			List<ElementMeta> childs = ret.getChildElement();
			List<AttributeMeta> attrs = ret.getAttrData(); 
			List<BaseMeta> l = processList(item.getAttributeUses(), depth);
			for (BaseMeta b:l) {
				if (b instanceof AttributeMeta) {
					attrs.add((AttributeMeta)b);
				} else if (b instanceof ElementMeta) {
					childs.add((ElementMeta)b);
				}
			}

			//if recursive use return here
			if(recursive){
				return ret;
			}
			l = processParticle(item.getParticle(), depth);
			if(l==null) return ret;
			for (BaseMeta b:l) {
				if (b instanceof AttributeMeta) {
					attrs.add((AttributeMeta)b);
				} else if (b instanceof ElementMeta) {
					childs.add((ElementMeta)b);
				}
			}
		} finally {
			ctStack.pop();
		}

		return ret;
	}
	private List<BaseMeta> processParticle(XSParticle item, int depth){
		if(item == null){
			if(debug) System.out.println(getPrefix(depth+1)+"Particle{null}");
			return null;
		}
		List<BaseMeta> l = processTerm(item.getTerm(), depth+1);
		if (l==null||l.isEmpty())
			return l;
		ElementMeta e = (ElementMeta)l.get(0);
		boolean setOccurs=false;
		if (item.getTerm() instanceof XSModelGroup)
		{
			short comp = ((XSModelGroup)item.getTerm()).getCompositor();
			if (comp==XSModelGroup.COMPOSITOR_CHOICE)
				setOccurs=true;
		}
		if (item.getTerm()!=null&&item.getTerm().getName()!=null&&
				item.getTerm().getName().equals(e.getName()))
			setOccurs=true;
		
		if (setOccurs)
		{
			int maxOccur = item.getMaxOccurs();
			int minOccur = item.getMinOccurs();
			boolean unbound = item.getMaxOccursUnbounded();
			e.setIsRequired(minOccur>0);
			e.setMaxOccurs(BigInteger.valueOf(maxOccur));
			e.setMinOccurs(BigInteger.valueOf(minOccur));
			if(unbound) e.setMaxOccurs(BigInteger.valueOf(-1));
 
		}
		return l;
	}
	private List<BaseMeta> processTerm(XSTerm item, int depth){
		ArrayList<BaseMeta> ret = new ArrayList<BaseMeta>();
		if(debug) System.out.print(getPrefix(depth)+"Term{" + item.getNamespace() + "}" + item.getName()+"["+item.getClass()+"]");
		if(item instanceof XSModelGroup){
			short comp = ((XSModelGroup)item).getCompositor();
			if(debug) System.out.println(comp==XSModelGroup.COMPOSITOR_ALL?" *ALL* ":(comp==XSModelGroup.COMPOSITOR_CHOICE?" *CHOICE* ":" *SEQ* "));
			ret.addAll(processList(((XSModelGroup)item).getParticles(), depth));
			if(comp==XSModelGroup.COMPOSITOR_CHOICE) {
				//create a choice element to hold 
				//the content of choice
				ElementMeta choiceMeta = new ElementMeta();
				choiceMeta.setName("<choice>");
				for(BaseMeta i:ret) {
					((ElementMeta)i).setIsChoice(true);
					choiceMeta.getChildElement().add((ElementMeta)i);
				}
				ret.clear();
				ret.add(choiceMeta);
			}
		}else if(item instanceof XSElementDeclaration) {
			if(debug) System.out.println(" *ELEMENT*");
			ret.add(processElement((XSElementDeclaration)item, depth));
		}
		return ret;
	}
	private ElementMeta processElement(XSElementDeclaration item, int depth){
		if(debug) System.out.println(getPrefix(depth)+"Element{" + item.getNamespace() + "}" + item.getName()+"["+item.getClass()+"]");
		String qname = "{" + item.getNamespace() + "}" + item.getName();
		elStack.push(qname);
		ElementMeta ret = null;
		try{
			XSTypeDefinition type = item.getTypeDefinition();
			if(type instanceof XSComplexTypeDefinition){
				ret = processComplexType((XSComplexTypeDefinition)type, depth);
			}else if(type instanceof XSSimpleTypeDefinition){
				processSimpleType((XSSimpleTypeDefinition)type, depth);
			}

			if(ret == null) 
				ret = new ElementMeta();
			ret.setNameSpace(item.getNamespace());
			ret.setName(item.getName());
		}finally{
			elStack.pop();
		}
		ret.setType((item.getTypeDefinition().getNamespace()==null||item.getTypeDefinition().getNamespace().equals(defaultNS))?
					item.getTypeDefinition().getName()
					:item.getTypeDefinition().getNamespace()+":"+item.getTypeDefinition().getName());
		return ret;
	}
	private AttributeMeta processAttribute(XSAttributeUse item, int depth){
		if(item == null){
			if(debug) System.out.println(getPrefix(depth+1)+"Attribute {null}");
			return null;
		}
		XSAttributeDeclaration 	attr = item.getAttrDeclaration();
		AttributeMeta ret = new AttributeMeta();
		ret.setNameSpace(attr.getNamespace());
		ret.setName(attr.getName());
		ret.setIsRequired(item.getRequired());

		ret.setType((attr.getTypeDefinition().getNamespace()==null||attr.getTypeDefinition().getNamespace().equals(defaultNS))?
				attr.getTypeDefinition().getName()
				:attr.getTypeDefinition().getNamespace()+":"+attr.getTypeDefinition().getName());
	
		if (item.getConstraintType()==XSConstants.VC_DEFAULT) {
			ret.setDefaultValue(item.getConstraintValue());
		} else if (item.getConstraintType()==XSConstants.VC_FIXED) {
			ret.setIsFixed(true);
			ret.setFixedValue(item.getConstraintValue());
		}

		if(debug) System.out.print(getPrefix(depth+1)+"AttributeUse{" + item.getNamespace() + "}" + item.getName()+"["+item.getClass()+"]"
				+(item.getRequired()?",":"Required,")
				+(item.getConstraintType()==XSConstants.VC_NONE?"":((item.getConstraintType()==XSConstants.VC_DEFAULT?"default=":"fixed=")+item.getConstraintValue())));
		if(debug) System.out.println(", Attribute{" + attr.getNamespace() + "}" + attr.getName()+"["+attr.getClass()+"]"
				+("{"+attr.getTypeDefinition().getNamespace()+"}"+attr.getTypeDefinition().getName())
				+(attr.getConstraintType()==XSConstants.VC_NONE?"":((attr.getConstraintType()==XSConstants.VC_DEFAULT?"default=":"fixed=")+attr.getConstraintValue())));
		return ret;
	}

	public boolean handleError(DOMError error){
		short severity = error.getSeverity();
		if (severity == DOMError.SEVERITY_ERROR) {
			System.out.println("[xs-error]: "+error.getMessage());
		}

		if (severity == DOMError.SEVERITY_WARNING) {
			System.out.println("[xs-warning]: "+error.getMessage());
		}
		return true;
	}
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.14  2009/11/23 20:38:16  wangeug
 * HISTORY: set element cardinality
 * HISTORY:
 * HISTORY: Revision 1.13  2009/11/02 14:47:33  wangeug
 * HISTORY: read nameSpace and data type
 * HISTORY:
 * HISTORY: Revision 1.12  2009/10/27 18:21:12  wangeug
 * HISTORY: clean codes
 * HISTORY:
 * HISTORY: Revision 1.11  2009/10/16 17:37:39  wangeug
 * HISTORY: parse default value or fixed value for an Attribute
 * HISTORY:
 * HISTORY: Revision 1.10  2008/12/29 22:18:18  linc
 * HISTORY: function UI added.
 * HISTORY:
 * HISTORY: Revision 1.9  2008/12/03 20:46:14  linc
 * HISTORY: UI update.
 * HISTORY:
 * HISTORY: Revision 1.8  2008/11/04 21:25:38  linc
 * HISTORY: updated
 * HISTORY:
 * HISTORY: Revision 1.7  2008/11/04 21:19:34  linc
 * HISTORY: core mapping and transform demo.
 * HISTORY:
 * HISTORY: Revision 1.6  2008/10/22 19:01:17  linc
 * HISTORY: Add comment of public methods.
 * HISTORY:
 * HISTORY: Revision 1.5  2008/10/21 20:49:08  linc
 * HISTORY: Tested with HL7 v3 schema
 * HISTORY:
 * HISTORY: Revision 1.4  2008/10/20 20:46:15  linc
 * HISTORY: updated.
 * HISTORY:
 * HISTORY: Revision 1.3  2008/10/08 20:05:55  linc
 * HISTORY: speed up
 * HISTORY:
 * HISTORY: Revision 1.2  2008/10/08 18:54:42  linc
 * HISTORY: updated
 * HISTORY:
 * HISTORY: Revision 1.1  2008/09/30 17:30:41  linc
 * HISTORY: updated.
 * HISTORY:
 */
