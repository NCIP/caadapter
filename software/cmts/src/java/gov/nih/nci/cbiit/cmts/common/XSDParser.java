/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.common;

import java.math.BigInteger;
import java.util.*;

import org.apache.xerces.impl.xs.XSAnnotationImpl;
import org.apache.xerces.xs.*;
import org.w3c.dom.DOMException;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import gov.nih.nci.cbiit.cmts.core.*;
import gov.nih.nci.cbiit.cmts.ui.mapping.ElementMetaLoader;
import javax.swing.tree.DefaultMutableTreeNode;

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

    private static String getPrefix(int iP)
    {
        return getPrefix(iP, true);
    }
    private static String getPrefix(int iP, boolean c){
        StringBuffer sb = new StringBuffer();
        int i=iP-MetaConstants.SCHEMA_LAZY_LOADINTG_INITIAL;
        if (i<0)
            i=i*(-1);
        for(int j=0; j<i+1; j++) sb.append("  ");
        if (c) sb.append("[").append(i<10?((char)('0'+i)):((char)('a'+i-10))).append("]("+iP+")-");
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
     * @return XSNamedMap[]
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
            XSNamedMap map = null;
            XSObject xsItem = null;
            for(int i=0;i<6;i++)
            {
                short s = -1;
                String name2 = name;
                String namespace2 = namespace;
                if (i == 0 ) s = XSConstants.TYPE_DEFINITION;
                else if (i == 1 ) s = XSConstants.ELEMENT_DECLARATION;
                else if (i == 2 )
                {
                    s = XSConstants.TYPE_DEFINITION;
                    if (name.startsWith(namespace + ":")) name2 = name.substring(namespace.length()+1);
                }
                else if (i == 3 )
                {
                    s = XSConstants.ELEMENT_DECLARATION;
                    if (name.startsWith(namespace + ":")) name2 = name.substring(namespace.length()+1);
                }
                else if (i == 4 )
                {
                    s = XSConstants.TYPE_DEFINITION;
                    if ((namespace2 == null)&&(name2.indexOf(":") > 0))
                    {
                        namespace2 = name2.substring(0,name2.lastIndexOf(":"));
                    }
                    if (name2.startsWith(namespace2 + ":")) name2 = name2.substring(namespace2.length()+1);

                }
                else if (i == 5 )
                {
                    s = XSConstants.ELEMENT_DECLARATION;
                    if ((namespace2 == null)&&(name2.indexOf(":") > 0))
                    {
                        namespace2 = name2.substring(0,name2.lastIndexOf(":"));
                    }
                    if (name2.startsWith(namespace2 + ":")) name2 = name2.substring(namespace2.length()+1);
                }
                map = model.getComponents(s);

                //processMap(map, 0);
                defaultNS = namespace;
                ctStack.clear();
                elStack.clear();
                xsItem=map.itemByName(namespace2, name2);

                if (xsItem != null)
                {
                    //System.out.println("CCCX Find XS map("+i+") : ns=" + namespace2 + ", name=" + name2 + ", xsItem=" + xsItem);
                    break;
                }
                //else System.out.println("CCCX null XS map("+i+") : ns=" + namespace2 + ", name=" + name2);
            }
            if (xsItem == null) System.out.println("Null element meta from Map.itemByName, namespace= " +  namespace + ", name=" + name + ", ObjType=" + xsItem);
            else if(xsItem instanceof XSComplexTypeDefinition)
            {

                ElementMeta m = processComplexType((XSComplexTypeDefinition)xsItem, depth, false);
                if (m == null)
                {
                    System.out.println("Null element meta, namespace= " +  namespace + ", name=" + name);
                }
                //else  System.out.println("CCCX Successfully create element meta("+m+"), namespace= " +  namespace + ", name=" + name);
                return m;
            }
            else if(xsItem instanceof XSSimpleTypeDefinition)
            {
                //System.out.println("CCCX Simple type element meta, namespace= " +  namespace + ", name=" + name);
                return null;
            }
        }
        return null;
    }

    /**
     * Process a list of XSObject
     * @param itemList: XSParticle, XSAttributeUse
     * @param depth
     * @return List<BaseMeta>
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
                ret.add(processAttribute((XSAttributeUse)item, depth));
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
     * @return  ElementMeta
     */
    private ElementMeta processComplexType(XSComplexTypeDefinition item, int depth, boolean depthAdd){
        if(debug) System.out.println(getPrefix(depth)+"ComplexType{" + item.getNamespace() + "}" + item.getName()+"["+item.getClass()+"]");

        ElementMeta ret=new ElementMeta();
        ret.setNameSpace(item.getNamespace());
        ret.setName(item.getName());
        ret.setIsSimple(false);
        if (item.getAnnotations()!=null&&item.getAnnotations().getLength()>0)
        {
        	for (int i=0;i<item.getAnnotations().getLength();i++)
        	{
        		XSObject xsObj=item.getAnnotations().item(i);
        	}
        	XSAnnotationImpl annImpl=(XSAnnotationImpl)item.getAnnotations().item(0);

        	ret.setAnnotationString(annImpl.getAnnotationString());
        }

        String qname = "{" + item.getNamespace() + "}" + item.getName();
        if(item.getName()==null)
            qname = "{" + item.getNamespace() + "}" + elStack.peek();
        boolean recursive = ctStack.contains(qname);
        ctStack.push(qname);

        try {
            if (depth<0)
            {
                if (depthAdd) depth = MetaConstants.SCHEMA_LAZY_LOADINTG_INCREMENTAL;
                else
                    return ret;
            }
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
            /*
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
            */
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
     * @return  List<BaseMeta>
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
     * @return List<BaseMeta>
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
     * @return ElementMeta
     */
    private ElementMeta processElement(XSElementDeclaration item, int depth){
        if(debug) System.out.println(getPrefix(depth)+"Element{" + item.getNamespace() + "}" + item.getName()+"["+item.getClass()+"]");
        String qname = "{" + item.getNamespace() + "}" + item.getName();
        elStack.push(qname);
        ElementMeta ret = null;
        try{
            XSTypeDefinition type = item.getTypeDefinition();
            String typeStr = (type.getNamespace()==null||type.getNamespace().equals(defaultNS))?
                    type.getName()
                    :type.getNamespace()+":"+type.getName();
            if(type instanceof XSComplexTypeDefinition){
                //System.out.println("CCCX processElement() depth = " + depth);
                ret = processComplexType((XSComplexTypeDefinition)type, depth, (typeStr == null));
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
        XSAnnotation annotation=item.getAnnotation();
        if (annotation!=null)
        	ret.setAnnotationString(annotation.getAnnotationString());
        ret.setType((item.getTypeDefinition().getNamespace()==null||item.getTypeDefinition().getNamespace().equals(defaultNS))?
                    item.getTypeDefinition().getName()
                    :item.getTypeDefinition().getNamespace()+":"+item.getTypeDefinition().getName());
        return ret;
    }
    /**
     * Process XSAttributeUse object
     * The XSAttributeUse interface extends XSObject and represents the Attribute Use schema component.
     * @param item
     * @return AttributeMeta
     */
    private AttributeMeta processAttribute(XSAttributeUse item, int depth){
        if(item == null){
            if(debug) System.out.println("Attribute {null}");
            return null;
        }
        XSAttributeDeclaration 	attr = item.getAttrDeclaration();
        AttributeMeta ret = new AttributeMeta();
        ret.setNameSpace(attr.getNamespace());
        ret.setName(attr.getName());
        ret.setRequired(item.getRequired());
        if (item.getAnnotations()!=null&&item.getAnnotations().getLength()>0)
        	ret.setAnnotationString(((XSAnnotation)item.getAnnotations().item(0)).getAnnotationString());
        ret.setType((attr.getTypeDefinition().getNamespace()==null||attr.getTypeDefinition().getNamespace().equals(defaultNS))?
                attr.getTypeDefinition().getName()
                :attr.getTypeDefinition().getNamespace()+":"+attr.getTypeDefinition().getName());

        if (item.getConstraintType()==XSConstants.VC_DEFAULT) {
            ret.setDefaultValue(item.getConstraintValue());
        } else if (item.getConstraintType()==XSConstants.VC_FIXED) {
            ret.setIsFixed(true);
            ret.setFixedValue(item.getConstraintValue());
        }

        if(debug) System.out.print(" " + getPrefix(depth, false) + "AttributeUse{" + item.getNamespace() + "}" + item.getName()+"["+item.getClass()+"]"
                +(item.getRequired()?",":"Required,")
                +(item.getConstraintType()==XSConstants.VC_NONE?"":((item.getConstraintType()==XSConstants.VC_DEFAULT?"default=":"fixed=")+item.getConstraintValue())));
        if(debug) System.out.println(", Attribute{" + attr.getNamespace() + "}" + attr.getName()+"["+attr.getClass()+"]"
                +("{"+attr.getTypeDefinition().getNamespace()+"}"+attr.getTypeDefinition().getName())
                +(attr.getConstraintType()==XSConstants.VC_NONE?"":((attr.getConstraintType()==XSConstants.VC_DEFAULT?"default=":"fixed=")+attr.getConstraintValue())));
        return ret;
    }


    public DefaultMutableTreeNode expandNodeWithLazyLoad(ElementMeta meta, int newNodeType, Object rootObject)
    {
        expandElementMetaWithLazyLoad(meta);
        return (DefaultMutableTreeNode)new ElementMetaLoader(newNodeType).loadDataForRoot(meta, rootObject);
    }

    public void expandElementMetaWithLazyLoad(ElementMeta meta)
    {
        if (meta.getType() == null) return;
        ElementMeta newMeta=this.getElementMetaFromComplexType(meta.getNameSpace(), meta.getType(), MetaConstants.SCHEMA_LAZY_LOADINTG_INCREMENTAL);

        if (newMeta != null)
        {
            //System.out.println("Deep Loading XSDParser.expandElementMetaWithLazyLoad()...extended node:"+meta+ ", type=" + meta.getType() + ", nameSpace="+meta.getNameSpace() + ", name=" + meta.getName());
            while(meta.getAttrData().size() > 0) meta.getAttrData().remove(0);
            meta.getAttrData().addAll(0, newMeta.getAttrData());

            while(meta.getChildElement().size() > 0) meta.getChildElement().remove(0);
            meta.getChildElement().addAll(0, newMeta.getChildElement());
        }
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
