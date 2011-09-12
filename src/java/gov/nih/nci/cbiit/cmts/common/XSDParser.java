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
import java.net.URL;

import org.apache.xerces.xs.*;
import org.w3c.dom.DOMException;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import gov.nih.nci.cbiit.cmts.core.*;
import gov.nih.nci.cbiit.cmts.util.FileUtil;

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
public class XSDParser  {
    private XSLoader schemaLoader;
    private XSModel model;
    private String schemaURI;
    private Stack<String> ctStack;
    private Stack<String> elStack;
    private String defaultNS = "";
    private static boolean debug = false;
    
    private static String getPrefix(int iP){
        StringBuffer sb = new StringBuffer();
        int i=iP-MetaConstants.SCHEMA_LAZY_LOADINTG_INITIAL;
        if (i<0)
        	i=i*(-1);
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
            System.out.println("XSDParser.XSDParser()...set vaidate");
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

    public void loadSchema(String schemaURI, String workDir)
    {
    	this.schemaURI = schemaURI;
        // parse document
        if(debug) System.out.println("Parsing " + this.schemaURI + "...");
        model = schemaLoader.loadURI(this.schemaURI);
    }

    /**
     * @return the schemaURI
     */
    public String getSchemaURI() {
        return schemaURI;
    }

    /***
     * Find mappable names:Elment, ComplexType
     * @return
     */
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
            defaultNS = namespace;
            ctStack.clear();
            elStack.clear();
            XSObject xsItem=map.itemByName(namespace, name);
            if (xsItem!=null)
            	return processElement((XSElementDeclaration)xsItem,MetaConstants.SCHEMA_LAZY_LOADINTG_INITIAL);
        }
        return null;
    }

    /**
     * get CMTS Core Model Object corresponding to the specified complex type
     * @param namespace - complex type namespace
     * @param name - complex type name
     * @return ElementMeta object
     */
    public ElementMeta getElementMetaFromComplexType(String namespace, String name,int depth){
        if (model != null) {
            // element declarations
            XSNamedMap map = model.getComponents(XSConstants.TYPE_DEFINITION);
            //processMap(map, 0);
            defaultNS = namespace;
            ctStack.clear();
            elStack.clear();
            XSObject xsItem=map.itemByName(namespace, name);
        	if(xsItem instanceof XSComplexTypeDefinition){
                return processComplexType((XSComplexTypeDefinition)xsItem, depth);
            }else if(xsItem instanceof XSSimpleTypeDefinition){
                return null;
            }
        } 
        return null;
    }

    /**
     * Process a list of XSObject
     * @param itemList: XSParticle, XSAttributeUse
     * @param depth
     * @return
     */
    private List<BaseMeta> processList(XSObjectList itemList, int depth){
    	if(debug) System.out.println(getPrefix(depth)+"XSObjectList{" + itemList.getLength() + "}" + "["+itemList.getClass()+"]");
        ArrayList<BaseMeta> ret = new ArrayList<BaseMeta>();
        for (int i = 0; i < itemList.getLength(); i++) {
            XSObject item = itemList.item(i);
//            if(item instanceof XSComplexTypeDefinition){
//                ret.add(processComplexType((XSComplexTypeDefinition)item, depth));
//            }else 
            if(item instanceof XSParticle){
                ret.addAll(processParticle((XSParticle)item, depth));
            }else if(item instanceof XSAttributeUse){
                ret.add(processAttribute((XSAttributeUse)item));
            }
        }
        return ret;
    }

    /**
     * Process XSComplexTypeDefiniton object
     * This interface extends XSTypeDefinition which extends XSObject.
     * It represents the Complex Type Definition schema component. 
     * @param item
     * @param depth
     * @return
     */
    private ElementMeta processComplexType(XSComplexTypeDefinition item, int depth){
        if(debug) System.out.println(getPrefix(depth)+"ComplexType{" + item.getNamespace() + "}" + item.getName()+"["+item.getClass()+"]");

        ElementMeta ret=new ElementMeta();
        ret.setNameSpace(item.getNamespace());
        ret.setName(item.getName());
        ret.setIsSimple(false);

        String qname = "{" + item.getNamespace() + "}" + item.getName();
        if(item.getName()==null)
            qname = "{" + item.getNamespace() + "}" + elStack.peek();        
        boolean recursive = ctStack.contains(qname);
        ctStack.push(qname);
    
        try {
            if (depth<0)
            	return ret;
            //if recursive use return here
            if(recursive){
                ret.setIsRecursive(true);
                return ret;
            } 
        	List<ElementMeta> childs = ret.getChildElement();
            List<AttributeMeta> attrs = ret.getAttrData();
            List<BaseMeta> l = processList(item.getAttributeUses(), depth);
            for (BaseMeta b:l) {
                if (b instanceof AttributeMeta) 
                    attrs.add((AttributeMeta)b);
                else
                	throw new Exception ("Invalid attributue use:"+b.getName());
            }

            l = processParticle(item.getParticle(), depth-1);
            if(l==null) return 
            	ret;
            for (BaseMeta b:l) {
            	if (b instanceof ElementMeta) 
                    childs.add((ElementMeta)b);
            	else
            		throw new Exception ("Invalid particle term:"+b.getName());
            }

            if (item.getNamespace()!=null&&
            		item.getNamespace().toLowerCase().endsWith("hl7-org:v3"))
            {
                boolean enable = true;

                if (ctStack.size() >= 3)
                {
                    String c = ctStack.get(ctStack.size()-2);
                    if (c.endsWith("}CE"))
                    {
                        String el = elStack.peek();
                        if ((qname.endsWith("}CD"))&&(el.endsWith("}translation"))) enable = false;
                        if ((qname.endsWith("}ED"))&&(el.endsWith("}originalText"))) enable = false;
                    }
                }
                if (!enable)
                {
                    ret.setAtivated(false);
                    ret.setIsRecursive(true);
                    return ret;
                }
            }
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();
        }
        finally {
            ctStack.pop();
        }
        return ret;
    }
    /**
     * Process XSParticle object
     * This interface extends XSObject and represents the Particle schema component
     * It define the usage of its associated term such as, annotations, maxOccurs, maxOccursUnbound, minOccurs
     * @param item
     * @param depth
     * @return
     */
    private List<BaseMeta> processParticle(XSParticle item, int depth){
        if(item == null){
            if(debug) System.out.println(getPrefix(depth+1)+"Particle{null}");
            return null;
        }
        if (item.getTerm()==null)
        	System.out.println("XSDParser.processParticle()...null particle:"+item.getName());
        List<BaseMeta> l = processTerm(item.getTerm(), depth);
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
            e.setRequired(minOccur>0);
            e.setMaxOccurs(BigInteger.valueOf(maxOccur));
            e.setMinOccurs(BigInteger.valueOf(minOccur));
            if(unbound) e.setMaxOccurs(BigInteger.valueOf(-1));

        }
        return l;
    }
    /**
     * Process XSTerm object
     * This interface extends XSObject. It describes a term that can be one of 
     * a model group, 
     * a wildcard, 
     * or an element declaration. 
     * Objects implementing XSElementDeclaration, XSModelGroup and XSWildcard interfaces also implement this interface.
     * @param item
     * @param depth
     * @return
     */
    private List<BaseMeta> processTerm(XSTerm item, int depth){
        ArrayList<BaseMeta> ret = new ArrayList<BaseMeta>();
        if(debug) System.out.print(getPrefix(depth)+"Term{" + item.getNamespace() + "}" + item.getName()+"["+item.getClass()+"]");
        if(item instanceof XSModelGroup){
            short comp = ((XSModelGroup)item).getCompositor();
            if(debug) System.out.println(comp==XSModelGroup.COMPOSITOR_ALL?" *ALL* ":(comp==XSModelGroup.COMPOSITOR_CHOICE?" *CHOICE* ":" *SEQ* "));
            List<BaseMeta> elmntList=processList(((XSModelGroup)item).getParticles(), depth);
            if(comp==XSModelGroup.COMPOSITOR_CHOICE) {
                //create a choice element to hold
                //the content of choice
                ElementMeta choiceMeta = new ElementMeta();
                choiceMeta.setName("<choice>");
                choiceMeta.setIsSimple(false);
                for(BaseMeta i:elmntList) {
                    ((ElementMeta)i).setIsChoice(true);
                    choiceMeta.getChildElement().add((ElementMeta)i);
                }
                ret.add(choiceMeta);
            }
            else
            	ret.addAll(elmntList);
        }else if(item instanceof XSElementDeclaration) {
            if(debug) System.out.println(" *ELEMENT*");
            ret.add(processElement((XSElementDeclaration)item, depth));
        }
        return ret;
    }
    /**
     * Process XSElementDeclaration
     * This interface extends XSTerm and represents the Element Declaration schema component
     * @param item
     * @param depth
     * @return
     */
    private ElementMeta processElement(XSElementDeclaration item, int depth){
        if(debug) System.out.println(getPrefix(depth)+"Element{" + item.getNamespace() + "}" + item.getName()+"["+item.getClass()+"]");
        String qname = "{" + item.getNamespace() + "}" + item.getName();
        elStack.push(qname);
        ElementMeta ret = null;
        try{
            XSTypeDefinition type = item.getTypeDefinition();
            if(type instanceof XSComplexTypeDefinition){
                ret = processComplexType((XSComplexTypeDefinition)type, depth);
            } 
            
            if (ret==null)
            	ret = new ElementMeta();
            //if the ElementMeta being set "name and nameSpace" by its
            //type definition, set it back
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
    /**
     * Process XSAttributeUse object
     * The XSAttributeUse interface extends XSObject and represents the Attribute Use schema component. 
     * @param item
     * @return
     */
    private AttributeMeta processAttribute(XSAttributeUse item){
        if(item == null){
            if(debug) System.out.println("Attribute {null}");
            return null;
        }
        XSAttributeDeclaration 	attr = item.getAttrDeclaration();
        AttributeMeta ret = new AttributeMeta();
        ret.setNameSpace(attr.getNamespace());
        ret.setName(attr.getName());
        ret.setRequired(item.getRequired());

        ret.setType((attr.getTypeDefinition().getNamespace()==null||attr.getTypeDefinition().getNamespace().equals(defaultNS))?
                attr.getTypeDefinition().getName()
                :attr.getTypeDefinition().getNamespace()+":"+attr.getTypeDefinition().getName());

        if (item.getConstraintType()==XSConstants.VC_DEFAULT) {
            ret.setDefaultValue(item.getConstraintValue());
        } else if (item.getConstraintType()==XSConstants.VC_FIXED) {
            ret.setIsFixed(true);
            ret.setFixedValue(item.getConstraintValue());
        }

        if(debug) System.out.print("AttributeUse{" + item.getNamespace() + "}" + item.getName()+"["+item.getClass()+"]"
                +(item.getRequired()?",":"Required,")
                +(item.getConstraintType()==XSConstants.VC_NONE?"":((item.getConstraintType()==XSConstants.VC_DEFAULT?"default=":"fixed=")+item.getConstraintValue())));
        if(debug) System.out.println(", Attribute{" + attr.getNamespace() + "}" + attr.getName()+"["+attr.getClass()+"]"
                +("{"+attr.getTypeDefinition().getNamespace()+"}"+attr.getTypeDefinition().getName())
                +(attr.getConstraintType()==XSConstants.VC_NONE?"":((attr.getConstraintType()==XSConstants.VC_DEFAULT?"default=":"fixed=")+attr.getConstraintValue())));
        return ret;
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
