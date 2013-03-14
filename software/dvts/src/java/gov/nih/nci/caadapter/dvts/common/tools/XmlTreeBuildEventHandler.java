/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.common.tools;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Dec 19, 2008
 * Time: 3:25:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlTreeBuildEventHandler extends DefaultHandler
{

    private DefaultMutableTreeNode headNode = null;

    private DefaultMutableTreeNode specializationChild = null;
    private DefaultMutableTreeNode specializedClass = null;

    private DefaultMutableTreeNode association = null;
    //private DefaultMutableTreeNode headNode = null;


    //String currentElement = "";
    //String currentLevel = "";
    //MetaSegment head;
    private DefaultMutableTreeNode currentParent = null;
    private DefaultMutableTreeNode curr = null;
    //private DefaultMutableTreeNode temp;
    //String mode = "hl7";
    boolean success = false;
    //int errCount = 0;
    //MetaTreeMeta tree;

    boolean justStart = false;
    DefaultMutableTreeNode toBeContinued = null;
    //boolean hasElement = false;
    //boolean errorOnThisElement = false;

    //boolean isCurrentElementField = false;

    //String TOP_LEVEL = "hl7v3meta";

    //Stack<MetaSegment> stack = new Stack<MetaSegment>();


    public XmlTreeBuildEventHandler(DefaultMutableTreeNode head)
    {
        super();
        headNode = head;
        headNode.removeAllChildren();
        justStart = false;
        //System.out.println("XXXXX 888");
    }

    public void startDocument()
    {
        //System.out.println("XXXXX 999");
    }
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
    {
        toBeContinued = null;
        XmlTreeBrowsingNode node = new XmlTreeBrowsingNode();
        node.setRole(node.getRoleKind()[0]);
        node.setName(qName);
        if (!justStart)
        {
            headNode.setUserObject(node);
            curr = headNode;
            justStart = true;
        }
        else
        {
            DefaultMutableTreeNode dNode = new DefaultMutableTreeNode(node);
            //dNode.setAllowsChildren(false);
            //curr.setAllowsChildren(true);
            //dNode.setParent(curr);
            curr.add(dNode);
            curr = dNode;

            if (qName.indexOf("specializationChild") > 0) specializationChild = curr;
            if (qName.indexOf("specializedClass") > 0) specializedClass = curr;
            if (qName.indexOf("association") > 0) association = curr;

        }

        for(int i=0;i<atts.getLength();i++)
        {
            String name = atts.getQName(i).trim();
            String val = atts.getValue(i).trim();
            //System.out.println("XXXX : name=" + name + ", value="+ val);
            XmlTreeBrowsingNode xNode = new XmlTreeBrowsingNode(node.getRoleKind()[1], name, val);
            DefaultMutableTreeNode dNode = new DefaultMutableTreeNode(xNode);
            //dNode.setAllowsChildren(false);
            //curr.setAllowsChildren(true);
            //dNode.setParent(curr);
            if (name.toLowerCase().indexOf("name") >= 0)
            {
                XmlTreeBrowsingNode nTemp1 = (XmlTreeBrowsingNode) curr.getUserObject();
                String title = nTemp1.getName();
                XmlTreeBrowsingNode nTemp2 = new XmlTreeBrowsingNode(node.getRoleKind()[0], title + ";" + val, null);
                curr.setUserObject(nTemp2);

                if ((name.equals("name"))&&
                    ((title.equals("mif:class"))||(title.equals("mif:reference")))&&
                    //(specializtionChild != null)&&
                    (specializedClass != null))
                {
                    if (specializationChild != null)
                    {
                        nTemp1 = (XmlTreeBrowsingNode) specializationChild.getUserObject();
                        String title1 = nTemp1.getName();
                        nTemp2 = new XmlTreeBrowsingNode(node.getRoleKind()[0], title1 + ";" + val, null);
                        specializationChild.setUserObject(nTemp2);
                    }
                    nTemp1 = (XmlTreeBrowsingNode) specializedClass.getUserObject();
                    String title2 = nTemp1.getName();
                    nTemp2 = new XmlTreeBrowsingNode(node.getRoleKind()[0], title2 + ";" + val, null);
                    specializedClass.setUserObject(nTemp2);

                    specializationChild = null;
                    specializedClass = null;
                }

                if ((name.equals("name"))&&
                    (title.equals("mif:targetConnection"))&&
                    (association != null))
                {
                    nTemp1 = (XmlTreeBrowsingNode) association.getUserObject();
                    String title1 = nTemp1.getName();
                    nTemp2 = new XmlTreeBrowsingNode(node.getRoleKind()[0], title1 + ";" + val, null);
                    association.setUserObject(nTemp2);

                    association = null;
                }

            }
            curr.add(dNode);
        }
    }

    public void characters(char[] cha, int start, int length)
    {
        //hasElement = true;
        char[] chars = new char[length];
        for (int i=0;i<length;i++) chars[i] = cha[(start + i)];
        String str = new String(chars);

        str = str.trim();
        if (!str.equals(""))
        {
            if (toBeContinued != null)
            {
                XmlTreeBrowsingNode oNode = (XmlTreeBrowsingNode) toBeContinued.getUserObject();
                if (oNode.getRole().equals(oNode.getRoleKind()[2]))
                {
                    XmlTreeBrowsingNode xNode = new XmlTreeBrowsingNode(oNode.getRoleKind()[2], "inlineText", oNode.getValue()+str);

                    curr.remove(toBeContinued);
                    toBeContinued = new DefaultMutableTreeNode(xNode);
                    curr.add(toBeContinued);
                }
                else System.err.println("This is a big error ::::");
            }
            else
            {
                XmlTreeBrowsingNode xNode = new XmlTreeBrowsingNode((new XmlTreeBrowsingNode()).getRoleKind()[2], "inlineText", str);
                toBeContinued = new DefaultMutableTreeNode(xNode);

                curr.add(toBeContinued);
            }
        }

    }
    public void endElement(String namespaceURI, String localName, String qName)
    {
        curr = (DefaultMutableTreeNode) curr.getParent();
        toBeContinued = null;
    }
    public void endDocument()
    {

    }

    public DefaultMutableTreeNode getHeadNode()
    {
        return headNode;
    }

}
