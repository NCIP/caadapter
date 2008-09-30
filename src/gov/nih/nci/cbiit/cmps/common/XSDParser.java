/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmps.common;

import java.util.*;
import org.apache.xerces.xs.*;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMException;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import gov.nih.nci.cbiit.cmps.core.*; 

/**
 * This class 
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-09-30 17:30:41 $
 *
 */
public class XSDParser implements DOMErrorHandler {
	private XSLoader schemaLoader;
	private XSModel model;
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
	
    public void loadSchema(String schemaURI) {

            // parse document
            System.out.println("Parsing " + schemaURI + "...");
            model = schemaLoader.loadURI(schemaURI);

    }
	
    public ElementMeta getElementMeta(String namespace, String name){
        if (model != null) {
        	// element declarations
            XSNamedMap map = model.getComponents(XSConstants.ELEMENT_DECLARATION);
            processMap(map);
            return processXSObject(map.itemByName(namespace, name));
            //map = model.getComponents(XSConstants.ATTRIBUTE_DECLARATION);
            //map = model.getComponents(XSConstants.TYPE_DEFINITION);
			//map = model.getComponents(XSConstants.NOTATION_DECLARATION);
        } else {
        	return null;
        }

    }
    
    private static ElementMeta processXSObject(XSObject item) {
		if(item instanceof XSComplexTypeDefinition){
			return processComplexType((XSComplexTypeDefinition)item);
		}else if(item instanceof XSSimpleTypeDefinition){
			//processSimpleType((XSSimpleTypeDefinition)item);
			return null;
		}else if(item instanceof XSElementDeclaration){
			return processElement((XSElementDeclaration)item);
		}
		return null;
    }
    
	private static List<ElementMeta> processMap(XSNamedMap map){
		ArrayList<ElementMeta> ret = new ArrayList<ElementMeta>();
		for (int i = 0; i < map.getLength(); i++) {
			XSObject item = map.item(i);
			if(item instanceof XSComplexTypeDefinition){
				ret.add(processComplexType((XSComplexTypeDefinition)item));
			}else if(item instanceof XSSimpleTypeDefinition){
				processSimpleType((XSSimpleTypeDefinition)item);
			}else if(item instanceof XSElementDeclaration){
				ret.add(processElement((XSElementDeclaration)item));
			}
		}
		return ret;
	}
	
	private static List<BaseMeta> processList(XSObjectList map){
		ArrayList<BaseMeta> ret = new ArrayList<BaseMeta>();
		for (int i = 0; i < map.getLength(); i++) {
			XSObject item = map.item(i);
			if(item instanceof XSComplexTypeDefinition){
				ret.add(processComplexType((XSComplexTypeDefinition)item));
			}else if(item instanceof XSParticle){
				ret.addAll(processParticle((XSParticle)item));
			}else if(item instanceof XSAttributeUse){
				ret.add(processAttribute((XSAttributeUse)item));
			}
		}
		return ret;
	}
	
	private static void processSimpleType(XSSimpleTypeDefinition item){
		System.out.println("SimpleType{" + item.getNamespace() + "}" + item.getName()+"["+item.getClass()+"]");
		//processParticle(item.getParticle(), indent);
	}
	private static ElementMeta processComplexType(XSComplexTypeDefinition item){
		System.out.println("ComplexType{" + item.getNamespace() + "}" + item.getName()+"["+item.getClass()+"]");
		ElementMeta ret = new ElementMeta();
		ret.setName((item.getNamespace()==null?"":(item.getNamespace()+":"))+item.getName());
		List<ElementMeta> childs = ret.getChildElement();
		List<AttributeMeta> attrs = ret.getAttrData(); 
		List<BaseMeta> l = processList(item.getAttributeUses());
		for (BaseMeta b:l) {
			if (b instanceof AttributeMeta) {
				attrs.add((AttributeMeta)b);
			} else if (b instanceof ElementMeta) {
				childs.add((ElementMeta)b);
			}
		}
		l = processParticle(item.getParticle());
		for (BaseMeta b:l) {
			if (b instanceof AttributeMeta) {
				attrs.add((AttributeMeta)b);
			} else if (b instanceof ElementMeta) {
				childs.add((ElementMeta)b);
			}
		}
		return ret;
	}
	private static List<BaseMeta> processParticle(XSParticle item){
		if(item == null){
			System.out.println("Particle{null}");
			return null;
		}
		return processTerm(item.getTerm());
	}
	private static List<BaseMeta> processTerm(XSTerm item){
		ArrayList<BaseMeta> ret = new ArrayList<BaseMeta>();
		System.out.println("Term{" + item.getNamespace() + "}" + item.getName()+"["+item.getClass()+"]");
		if(item instanceof XSModelGroup)
			ret.addAll(processList(((XSModelGroup)item).getParticles()));
		else if(item instanceof XSElementDeclaration)
			ret.add(processElement((XSElementDeclaration)item));
		return ret;
	}
	private static ElementMeta processElement(XSElementDeclaration item){
		System.out.println("Element{" + item.getNamespace() + "}" + item.getName()+"["+item.getClass()+"]");
		ElementMeta ret = null;
//		if(indent>MAX_INDENT) {
//			System.out.println("MMMMMMMMM Reached max depth, skipping the lower levels......");
//			return;
//		}
		XSTypeDefinition type = item.getTypeDefinition();
		if(type instanceof XSComplexTypeDefinition){
				 ret = processComplexType((XSComplexTypeDefinition)type);
		}else if(type instanceof XSSimpleTypeDefinition){
				processSimpleType((XSSimpleTypeDefinition)type);
		} 
		if(ret == null) ret = new ElementMeta();
		ret.setName((item.getNamespace()==null?"":(item.getNamespace()+":"))+item.getName());
		
		//processParticle(item.getParticle(), indent+1);
		return ret;
	}
	private static AttributeMeta processAttribute(XSAttributeUse item){
		if(item == null){
			System.out.println("Attribute {null}");
			return null;
		}
		XSAttributeDeclaration 	attr = item.getAttrDeclaration();
		AttributeMeta ret = new AttributeMeta();
		ret.setName((attr.getNamespace()==null?"":(attr.getNamespace()+":"))+attr.getName());
		ret.setIsRequired(item.getRequired());
		if (attr.getConstraintType()==XSConstants.VC_DEFAULT) {
			ret.setDefaultValue(attr.getConstraintValue());
		} else if (attr.getConstraintType()!=XSConstants.VC_NONE) {
			ret.setIsFixed(true);
		}
		
		System.out.print("AttributeUse{" + item.getNamespace() + "}" + item.getName()+"["+item.getClass()+"]"
				+(item.getRequired()?",":"Required,")
				+(item.getConstraintType()==XSConstants.VC_NONE?"":((item.getConstraintType()==XSConstants.VC_DEFAULT?"default=":"fixed=")+item.getConstraintValue())));
		System.out.println(", Attribute{" + attr.getNamespace() + "}" + attr.getName()+"["+attr.getClass()+"]"
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
 */
